package com.ledger.ledgerworks.service;

import com.ledger.ledgerworks.entity.Invoice;
import com.ledger.ledgerworks.entity.LedgerAccount;
import com.ledger.ledgerworks.entity.Purchase;
import com.ledger.ledgerworks.enums.AccountType;
import com.ledger.ledgerworks.repository.LedgerAccountRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Single double-entry poster used by every document flow (sales, purchase,
 * receipt, payment, contra, returns).
 *
 * <h2>Party-account approach (DOCUMENTED CHOICE)</h2>
 * The existing customer / vendor ledger reporting drives off a
 * <b>per-party {@link LedgerAccount}</b>: {@code CustomerService.create()}
 * creates a ledger account named after the customer ({@code ASSET}), the
 * existing {@code LedgerPostingService.postInvoice} resolves the debtor via
 * {@code findByAccountName(customerName)}, the receivables / outstanding queries
 * sum per-ASSET-account balances, and {@code LedgerStatementService} renders a
 * statement <i>per account id</i>. To keep those existing reports correct we
 * therefore post to a <b>per-party ledger account</b> (get-or-create
 * {@code "<CustomerName>"} as {@code ASSET} for debtors and
 * {@code "<VendorName>"} as {@code LIABILITY} for creditors) rather than to the
 * Sundry Debtors / Sundry Creditors control accounts. The control accounts are
 * still seeded so a global trial balance has the grouping rows available, but
 * party movements land on the per-party account so each customer / vendor
 * statement reconciles.
 *
 * <p>Every method emits one balanced {@link com.ledger.ledgerworks.entity.LedgerTransaction}
 * row per non-zero leg sharing a single counter-account (the party), stamps the
 * DOCUMENT's own date, skips zero-amount legs, and reconciles any rupee rounding
 * difference (grandTotal vs taxable + taxes) to a "Round Off" leg so every
 * document's rows net to zero. All posting is best-effort: it never throws out
 * of document creation.</p>
 */
@Service
public class AccountingPostingService {

    private static final Logger log =
            LoggerFactory.getLogger(AccountingPostingService.class);

    // Standard control / nominal account names (seeded by ChartOfAccountsInitializer).
    public static final String SALES = "Sales";
    public static final String PURCHASES = "Purchases";
    public static final String CASH = "Cash";
    public static final String BANK = "Bank";
    public static final String ROUND_OFF = "Round Off";
    public static final String OUTPUT_CGST = "Output CGST";
    public static final String OUTPUT_SGST = "Output SGST";
    public static final String OUTPUT_IGST = "Output IGST";
    public static final String INPUT_CGST = "Input CGST";
    public static final String INPUT_SGST = "Input SGST";
    public static final String INPUT_IGST = "Input IGST";

    private final LedgerAccountRepository accountRepository;
    private final LedgerTransactionService ledgerTransactionService;

    public AccountingPostingService(
            LedgerAccountRepository accountRepository,
            LedgerTransactionService ledgerTransactionService) {
        this.accountRepository = accountRepository;
        this.ledgerTransactionService = ledgerTransactionService;
    }

    // =====================================================
    // GET-OR-CREATE AN ACCOUNT BY NAME + TYPE
    // =====================================================
    public LedgerAccount getOrCreateAccount(String name, AccountType type) {

        if (name == null || name.isBlank()) {
            return null;
        }

        final String accountName = name.trim();

        return accountRepository.findByAccountName(accountName)
                .orElseGet(() -> {
                    LedgerAccount account = new LedgerAccount();
                    account.setAccountName(accountName);
                    account.setAccountType(type);
                    account.setBalance(BigDecimal.ZERO);
                    return accountRepository.save(account);
                });
    }

    // =====================================================
    // PARTY-ACCOUNT RESOLVERS (per-party ledger account)
    // =====================================================

    /** Debtor (customer receivable) ledger account: get-or-create "<name>" as ASSET. */
    public LedgerAccount resolveDebtorAccount(String customerName) {
        return getOrCreateAccount(customerName, AccountType.ASSET);
    }

    /** Creditor (vendor payable) ledger account: get-or-create "<name>" as LIABILITY. */
    public LedgerAccount resolveCreditorAccount(String vendorName) {
        return getOrCreateAccount(vendorName, AccountType.LIABILITY);
    }

    /** Cash / Bank resolver. mode treated as BANK unless it clearly says CASH. */
    public LedgerAccount resolveCashOrBank(String mode) {
        boolean isCash = mode != null && mode.trim().equalsIgnoreCase(CASH);
        return getOrCreateAccount(isCash ? CASH : BANK, AccountType.ASSET);
    }

    // =====================================================
    // SALES INVOICE
    // Dr party-debtor (grandTotal)
    //   Cr Sales (totalTaxable)
    //   Cr Output CGST / SGST / IGST
    //   + Round Off leg to absorb rounding
    // =====================================================
    public void postSalesInvoice(Invoice inv) {

        if (inv == null) {
            return;
        }

        try {
            LedgerAccount debtor = resolveDebtorAccount(inv.getCustomerName());

            if (debtor == null) {
                log.warn("Sales invoice {} has no resolvable customer account; "
                        + "skipping ledger posting", inv.getInvoiceNumber());
                return;
            }

            LocalDate date = inv.getInvoiceDate();
            String ref = "Sales Invoice " + safe(inv.getInvoiceNumber());

            BigDecimal taxable = nz(inv.getTotalTaxable());
            BigDecimal cgst = nz(inv.getTotalCGST());
            BigDecimal sgst = nz(inv.getTotalSGST());
            BigDecimal igst = nz(inv.getTotalIGST());
            BigDecimal grandTotal = nz(inv.getGrandTotal());

            // Dr debtor against each credit leg: Cr Sales (taxable) + Cr Output GST.
            // (postLeg(debit, credit, amount) -> debtor is the debit side of a sale.)
            postLeg(debtor, getOrCreateAccount(SALES, AccountType.INCOME), taxable, date, ref);
            postLeg(debtor, getOrCreateAccount(OUTPUT_CGST, AccountType.LIABILITY), cgst, date, ref);
            postLeg(debtor, getOrCreateAccount(OUTPUT_SGST, AccountType.LIABILITY), sgst, date, ref);
            postLeg(debtor, getOrCreateAccount(OUTPUT_IGST, AccountType.LIABILITY), igst, date, ref);

            // Round off: grandTotal - (taxable + cgst + sgst + igst)
            BigDecimal diff = grandTotal.subtract(taxable.add(cgst).add(sgst).add(igst));
            postRoundOff(debtor, diff, date, ref, true);

        } catch (Exception ex) {
            log.warn("Sales invoice ledger posting failed (invoice saved anyway): {}",
                    ex.getMessage());
        }
    }

    // =====================================================
    // PURCHASE
    // Dr Purchases (taxable)
    // Dr Input CGST / SGST / IGST
    //   Cr party-creditor (grandTotal)
    //   + Round Off leg to absorb rounding
    // =====================================================
    public void postPurchase(Purchase p) {

        if (p == null) {
            return;
        }

        try {
            LedgerAccount creditor = resolveCreditorAccount(p.getVendorName());

            if (creditor == null) {
                log.warn("Purchase {} has no resolvable vendor account; "
                        + "skipping ledger posting", p.getPurchaseNumber());
                return;
            }

            LocalDate date = p.getPurchaseDate();
            String ref = "Purchase " + safe(p.getPurchaseNumber());

            BigDecimal taxable = nz(p.getTotalTaxable());
            BigDecimal cgst = nz(p.getTotalCGST());
            BigDecimal sgst = nz(p.getTotalSGST());
            BigDecimal igst = nz(p.getTotalIGST());
            BigDecimal grandTotal = nz(p.getGrandTotal());

            // Dr Purchases / Input GST, all credited to the creditor.
            postLeg(getOrCreateAccount(PURCHASES, AccountType.EXPENSE), creditor, taxable, date, ref);
            postLeg(getOrCreateAccount(INPUT_CGST, AccountType.ASSET), creditor, cgst, date, ref);
            postLeg(getOrCreateAccount(INPUT_SGST, AccountType.ASSET), creditor, sgst, date, ref);
            postLeg(getOrCreateAccount(INPUT_IGST, AccountType.ASSET), creditor, igst, date, ref);

            // Round off: grandTotal - (taxable + cgst + sgst + igst)
            BigDecimal diff = grandTotal.subtract(taxable.add(cgst).add(sgst).add(igst));
            // For a purchase the creditor is on the CREDIT side, so a positive
            // diff (grandTotal larger) must be DEBITED to balance -> debit=false.
            postRoundOff(creditor, diff, date, ref, false);

        } catch (Exception ex) {
            log.warn("Purchase ledger posting failed (purchase saved anyway): {}",
                    ex.getMessage());
        }
    }

    // =====================================================
    // RECEIPT - money in from a customer
    // Dr Cash / Bank ; Cr party-debtor
    // =====================================================
    public void postReceipt(String partyName, BigDecimal amount,
                            String mode, LocalDate date) {
        try {
            LedgerAccount debtor = resolveDebtorAccount(partyName);
            LedgerAccount cashBank = resolveCashOrBank(mode);

            if (debtor == null || cashBank == null) {
                log.warn("Receipt has no resolvable accounts; skipping ledger posting");
                return;
            }

            postLeg(cashBank, debtor, nz(amount), date,
                    "Receipt from " + safe(partyName));
        } catch (Exception ex) {
            log.warn("Receipt ledger posting failed (saved anyway): {}", ex.getMessage());
        }
    }

    // =====================================================
    // PAYMENT - money out to a vendor
    // Dr party-creditor ; Cr Cash / Bank
    // =====================================================
    public void postPayment(String partyName, BigDecimal amount,
                            String mode, LocalDate date) {
        try {
            LedgerAccount creditor = resolveCreditorAccount(partyName);
            LedgerAccount cashBank = resolveCashOrBank(mode);

            if (creditor == null || cashBank == null) {
                log.warn("Payment has no resolvable accounts; skipping ledger posting");
                return;
            }

            postLeg(creditor, cashBank, nz(amount), date,
                    "Payment to " + safe(partyName));
        } catch (Exception ex) {
            log.warn("Payment ledger posting failed (saved anyway): {}", ex.getMessage());
        }
    }

    // =====================================================
    // CONTRA - movement between two own accounts (cash <-> bank, etc.)
    // Dr toAccount ; Cr fromAccount
    // =====================================================
    public void postContra(String fromAccount, String toAccount,
                           BigDecimal amount, LocalDate date) {
        try {
            LedgerAccount from = getOrCreateAccount(fromAccount, AccountType.ASSET);
            LedgerAccount to = getOrCreateAccount(toAccount, AccountType.ASSET);

            if (from == null || to == null) {
                log.warn("Contra has no resolvable accounts; skipping ledger posting");
                return;
            }

            postLeg(to, from, nz(amount), date,
                    "Contra " + safe(fromAccount) + " -> " + safe(toAccount));
        } catch (Exception ex) {
            log.warn("Contra ledger posting failed (saved anyway): {}", ex.getMessage());
        }
    }

    // =====================================================
    // SALES RETURN (Credit Note) - reverse a sale
    // Dr Sales (taxable) + Dr Output CGST/SGST/IGST ; Cr party-debtor (grandTotal)
    //   + Round Off leg
    // =====================================================
    public void postSalesReturn(String customerName, BigDecimal taxable,
                                BigDecimal cgst, BigDecimal sgst, BigDecimal igst,
                                BigDecimal grandTotal, LocalDate date, String docRef) {
        try {
            LedgerAccount debtor = resolveDebtorAccount(customerName);

            if (debtor == null) {
                log.warn("Sales return has no resolvable customer account; "
                        + "skipping ledger posting");
                return;
            }

            String ref = "Sales Return " + safe(docRef);

            BigDecimal tx = nz(taxable);
            BigDecimal c = nz(cgst);
            BigDecimal s = nz(sgst);
            BigDecimal i = nz(igst);
            BigDecimal gt = nz(grandTotal);

            // Reverse of a sale: each nominal/GST account is DEBITED, debtor CREDITED.
            // (postLeg(debit, credit, amount) -> nominal on debit, debtor on credit.)
            postLeg(getOrCreateAccount(SALES, AccountType.INCOME), debtor, tx, date, ref);
            postLeg(getOrCreateAccount(OUTPUT_CGST, AccountType.LIABILITY), debtor, c, date, ref);
            postLeg(getOrCreateAccount(OUTPUT_SGST, AccountType.LIABILITY), debtor, s, date, ref);
            postLeg(getOrCreateAccount(OUTPUT_IGST, AccountType.LIABILITY), debtor, i, date, ref);

            BigDecimal diff = gt.subtract(tx.add(c).add(s).add(i));
            // Debtor is on the CREDIT side here, so a positive diff must be DEBITED.
            postRoundOff(debtor, diff, date, ref, false);

        } catch (Exception ex) {
            log.warn("Sales return ledger posting failed (saved anyway): {}", ex.getMessage());
        }
    }

    // =====================================================
    // PURCHASE RETURN (Debit Note) - reverse a purchase
    // Dr party-creditor (grandTotal) ; Cr Purchases (taxable) + Cr Input CGST/SGST/IGST
    //   + Round Off leg
    // =====================================================
    public void postPurchaseReturn(String vendorName, BigDecimal taxable,
                                   BigDecimal cgst, BigDecimal sgst, BigDecimal igst,
                                   BigDecimal grandTotal, LocalDate date, String docRef) {
        try {
            LedgerAccount creditor = resolveCreditorAccount(vendorName);

            if (creditor == null) {
                log.warn("Purchase return has no resolvable vendor account; "
                        + "skipping ledger posting");
                return;
            }

            String ref = "Purchase Return " + safe(docRef);

            BigDecimal tx = nz(taxable);
            BigDecimal c = nz(cgst);
            BigDecimal s = nz(sgst);
            BigDecimal i = nz(igst);
            BigDecimal gt = nz(grandTotal);

            // Reverse of a purchase: creditor DEBITED, each nominal/GST account CREDITED.
            postLeg(creditor, getOrCreateAccount(PURCHASES, AccountType.EXPENSE), tx, date, ref);
            postLeg(creditor, getOrCreateAccount(INPUT_CGST, AccountType.ASSET), c, date, ref);
            postLeg(creditor, getOrCreateAccount(INPUT_SGST, AccountType.ASSET), s, date, ref);
            postLeg(creditor, getOrCreateAccount(INPUT_IGST, AccountType.ASSET), i, date, ref);

            BigDecimal diff = gt.subtract(tx.add(c).add(s).add(i));
            // Creditor is on the DEBIT side here, so a positive diff must be CREDITED.
            postRoundOff(creditor, diff, date, ref, true);

        } catch (Exception ex) {
            log.warn("Purchase return ledger posting failed (saved anyway): {}", ex.getMessage());
        }
    }

    // =====================================================
    // INTERNAL HELPERS
    // =====================================================

    /**
     * Emit one balanced ledger row for a non-zero leg.
     * Skips zero / null / negative-or-zero legs.
     */
    private void postLeg(LedgerAccount debit, LedgerAccount credit,
                         BigDecimal amount, LocalDate date, String narration) {

        if (debit == null || credit == null) {
            return;
        }
        if (amount == null || amount.signum() == 0) {
            return;
        }

        // Negative amount: flip the sides and use the magnitude so we always
        // store a positive amount on the correct side.
        LedgerAccount dr = debit;
        LedgerAccount cr = credit;
        BigDecimal amt = amount;
        if (amt.signum() < 0) {
            dr = credit;
            cr = debit;
            amt = amt.abs();
        }

        ledgerTransactionService.createTransaction(
                dr.getId(), cr.getId(), amt, date, narration);
    }

    /**
     * Reconcile a rounding difference to the Round Off account so the document's
     * rows net to zero.
     *
     * @param party        the document's party (single shared counter-account).
     * @param diff         grandTotal - (taxable + taxes); may be negative or zero.
     * @param partyOnDebit true when the party sits on the DEBIT side of the
     *                     document (e.g. a sales invoice debits the debtor). When
     *                     true, a positive diff means the party was under-debited
     *                     relative to the legs, so we Dr party / Cr Round Off.
     */
    private void postRoundOff(LedgerAccount party, BigDecimal diff,
                              LocalDate date, String narration, boolean partyOnDebit) {

        if (diff == null || diff.signum() == 0) {
            return;
        }

        LedgerAccount roundOff = getOrCreateAccount(ROUND_OFF, AccountType.EXPENSE);
        if (roundOff == null) {
            return;
        }

        // If the party is on the debit side, the extra grandTotal owed by the
        // party (positive diff) is balanced by a CREDIT to Round Off.
        // postLeg handles a negative magnitude by flipping sides.
        if (partyOnDebit) {
            // Dr party / Cr Round Off for positive diff.
            postLeg(party, roundOff, diff, date, narration + " (round off)");
        } else {
            // Party on credit side: Dr Round Off / Cr party for positive diff.
            postLeg(roundOff, party, diff, date, narration + " (round off)");
        }
    }

    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    private static String safe(String v) {
        return v != null ? v : "";
    }
}
