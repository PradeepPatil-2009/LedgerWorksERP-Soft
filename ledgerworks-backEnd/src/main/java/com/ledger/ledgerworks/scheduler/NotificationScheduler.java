package com.ledger.ledgerworks.scheduler;

import com.ledger.ledgerworks.entity.Invoice;
import com.ledger.ledgerworks.entity.Stock;
import com.ledger.ledgerworks.repository.InvoiceRepository;
import com.ledger.ledgerworks.repository.StockRepository;
import com.ledger.ledgerworks.service.NotificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Daily best-effort notification jobs. The cron schedules keep these from
 * firing during tests. Each job is wrapped in try/catch and never throws.
 */
@Component
public class NotificationScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(NotificationScheduler.class);

    private final StockRepository stockRepository;
    private final InvoiceRepository invoiceRepository;
    private final NotificationService notificationService;

    // No per-item minimum exists in the schema, so treat anything below this
    // configurable level as "low stock" (override via app.notifications.*).
    @Value("${app.notifications.low-stock-threshold:10}")
    private BigDecimal lowStockThreshold;

    public NotificationScheduler(
            StockRepository stockRepository,
            InvoiceRepository invoiceRepository,
            NotificationService notificationService
    ) {
        this.stockRepository = stockRepository;
        this.invoiceRepository = invoiceRepository;
        this.notificationService = notificationService;
    }

    // =========================================
    // LOW STOCK CHECK (daily 08:00)
    // =========================================

    @Scheduled(cron = "0 0 8 * * ?")
    public void lowStockCheck() {

        try {

            List<Stock> lowStock =
                    stockRepository.findByAvailableQtyLessThan(
                            lowStockThreshold
                    );

            if (lowStock == null || lowStock.isEmpty()) {
                return;
            }

            StringBuilder body = new StringBuilder();
            body.append(lowStock.size())
                    .append(" item(s) at or below the minimum stock level of ")
                    .append(lowStockThreshold)
                    .append(":\n");

            for (Stock stock : lowStock) {

                if (stock == null) {
                    continue;
                }

                body.append("- ")
                        .append(stock.getItemName())
                        .append(": ")
                        .append(stock.getAvailableQty() == null
                                ? BigDecimal.ZERO
                                : stock.getAvailableQty())
                        .append(stock.getUnit() == null
                                ? ""
                                : " " + stock.getUnit())
                        .append("\n");
            }

            notificationService.notify(
                    "Low stock alert: " + lowStock.size() + " item(s)",
                    body.toString()
            );

        } catch (Exception e) {

            log.error(
                    "lowStockCheck failed: {}",
                    e.getMessage(),
                    e
            );
        }
    }

    // =========================================
    // OVERDUE INVOICE REMINDER (daily 09:00)
    // =========================================

    @Scheduled(cron = "0 0 9 * * ?")
    public void overdueInvoiceReminder() {

        try {

            List<Invoice> unpaid =
                    invoiceRepository.findByPaymentStatusNot("PAID");

            if (unpaid == null || unpaid.isEmpty()) {
                return;
            }

            LocalDate today = LocalDate.now();

            StringBuilder body = new StringBuilder();
            int count = 0;
            BigDecimal outstanding = BigDecimal.ZERO;

            for (Invoice invoice : unpaid) {

                if (invoice == null) {
                    continue;
                }

                LocalDate dueDate = invoice.getDueDate();

                if (dueDate == null || !dueDate.isBefore(today)) {
                    continue;
                }

                count++;

                BigDecimal due =
                        invoice.getOutstandingAmount() == null
                                ? BigDecimal.ZERO
                                : invoice.getOutstandingAmount();

                outstanding = outstanding.add(due);

                body.append("- ")
                        .append(invoice.getInvoiceNumber())
                        .append(" (")
                        .append(invoice.getCustomerName())
                        .append("), due ")
                        .append(dueDate)
                        .append(", outstanding ")
                        .append(due)
                        .append("\n");
            }

            if (count == 0) {
                return;
            }

            String header =
                    count + " overdue invoice(s), total outstanding "
                            + outstanding + ":\n";

            notificationService.notify(
                    "Overdue invoice reminder: " + count + " invoice(s)",
                    header + body
            );

        } catch (Exception e) {

            log.error(
                    "overdueInvoiceReminder failed: {}",
                    e.getMessage(),
                    e
            );
        }
    }
}
