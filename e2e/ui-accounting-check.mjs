// Real-Chrome end-to-end ACCOUNTING proof, driven entirely from the UI.
//
// Logs in through the browser, CREATES a sales document via the Delivery
// Challan form, DELIVERS it (which creates + posts the invoice through
// AccountingPostingService), then opens the Trial Balance PAGE and asserts the
// posting reconciles in the rendered report:
//   - the customer (debtor) row shows a DEBIT of the grand total (1180),
//   - the Sales row shows a CREDIT (not a debit) of the taxable value (1000),
//   - Output CGST + Output SGST show credits (90 each),
//   - the page's own totals balance (total debit == total credit).
//
// Intra-state (CGST + SGST) is forced by a Maharashtra place-of-supply against
// the Maharashtra (27) company GSTIN.
//
// Usage: BASE_URL=http://localhost:3000 node e2e/ui-accounting-check.mjs
import { chromium } from "playwright";

const BASE = process.env.BASE_URL || "http://localhost:3000";
// LOCAL date (the server stamps ledger rows with LocalDate.now()); using
// toISOString() here would give the UTC date and miss the window near midnight.
const _now = new Date();
const TODAY = `${_now.getFullYear()}-${String(_now.getMonth() + 1).padStart(2, "0")}-${String(_now.getDate()).padStart(2, "0")}`;
const CUST = "UI Acct Buyer " + Date.now();

let failures = 0;
function check(cond, msg) {
  if (cond) {
    console.log("  ✓", msg);
  } else {
    failures += 1;
    console.log("  ✗ FAIL:", msg);
  }
}

let browser;
try {
  browser = await chromium.launch({ headless: true, channel: "chrome" });
} catch {
  browser = await chromium.launch({ headless: true });
}
const ctx = await browser.newContext({ viewport: { width: 1440, height: 900 } });
const page = await ctx.newPage();
page.setDefaultTimeout(20000);
// Auto-accept every window.confirm / window.alert (save + deliver dialogs).
page.on("dialog", (d) => d.accept().catch(() => {}));

try {
  // ---- 1. LOGIN through the UI ----
  console.log("▶ login");
  await page.goto(`${BASE}/login`, { waitUntil: "domcontentloaded" });
  await page.fill("#login-username", "admin");
  await page.fill("#login-password", "admin123");
  await Promise.all([
    page.waitForURL("**/dashboard", { timeout: 20000 }),
    page.click("button[type=submit]"),
  ]);
  check(true, "logged in (reached /dashboard)");

  // ---- 2. CREATE a Delivery Challan via the form ----
  console.log("▶ create delivery challan via UI form");
  await page.goto(`${BASE}/delivery-challan/create`, { waitUntil: "domcontentloaded" });
  await page.fill('input[name="customerName"]', CUST);
  await page.fill('input[name="customerGST"]', "27TESTU1234U1Z5"); // 27 -> intra-state
  await page.selectOption('select[name="placeOfSupply"]', "Maharashtra");

  // Item row: the two editable number inputs are qty then rate.
  const numInputs = page.locator("table tbody tr").first().locator('input[type="number"]');
  await numInputs.nth(0).fill("10");  // qty
  await numInputs.nth(1).fill("100"); // rate
  await page.waitForTimeout(300); // let calculateItem run

  await page.click('button:has-text("Save Challan")');
  // saveChallan() navigates back to /delivery-challan on success.
  await page.waitForURL("**/delivery-challan", { timeout: 20000 });
  check(true, "challan saved (returned to list)");

  // ---- 3. DELIVER it (creates + posts the invoice) ----
  console.log("▶ deliver challan (creates + posts invoice)");
  // Narrow the list to our customer so we click the right Deliver button.
  const search = page.locator('input[placeholder*="Search"]').first();
  if (await search.count()) {
    await search.fill(CUST);
    await page.waitForTimeout(500);
  }
  const row = page.locator("tr", { hasText: CUST }).first();
  await row.waitFor({ timeout: 20000 });
  await row.locator('button:has-text("Deliver")').first().click();
  // Toast "Delivered + Invoice Created"; give the POST time to commit.
  await page.waitForTimeout(2500);
  check(true, "deliver action invoked");

  // ---- 4. Verify the Trial Balance PAGE reflects the posting ----
  console.log("▶ read Trial Balance in the browser");
  await page.goto(`${BASE}/trial-balance`, { waitUntil: "domcontentloaded" });
  const dates = page.locator('input[type="date"]');
  await dates.nth(0).fill(TODAY);
  await dates.nth(1).fill(TODAY);
  // Click Load and WAIT for the report response + our customer's row to render
  // (avoids scraping the empty pre-load table).
  await Promise.all([
    page.waitForResponse(
      (r) => r.url().includes("/reports/trial-balance") && r.status() === 200,
      { timeout: 20000 }
    ).catch(() => {}),
    page.click('button:has-text("Load")'),
  ]);
  // Wait until the table is actually PAINTED with values (the row can match on
  // the account name a tick before the debit/credit cells render).
  await page.waitForFunction(() => {
    const rows = [...document.querySelectorAll("table tbody tr")];
    const sales = rows.find((r) => r.children[0]?.textContent?.trim() === "Sales");
    return sales && parseFloat(sales.children[2]?.textContent || "0") > 0;
  }, { timeout: 15000 }).catch(() => {});

  await page.waitForTimeout(1200); // let the value cells settle before scraping

  // Scrape the rendered table into {account: {debit, credit}}.
  const rows = await page.locator("table tbody tr").all();
  const data = {};
  for (const r of rows) {
    const tds = await r.locator("td").allInnerTexts();
    if (tds.length >= 3) {
      const name = (tds[0] || "").trim();
      const debit = parseFloat((tds[1] || "0").replace(/[^0-9.\-]/g, "")) || 0;
      const credit = parseFloat((tds[2] || "0").replace(/[^0-9.\-]/g, "")) || 0;
      if (name) data[name] = { debit, credit };
    }
  }
  console.log("    Trial Balance rows seen:", Object.keys(data).join(", ") || "(none)");

  const near = (a, b) => Math.abs(a - b) < 0.5;

  const debtor = data[CUST];
  check(!!debtor, `customer "${CUST}" appears as a ledger account`);
  if (debtor) {
    check(near(debtor.debit, 1180) && debtor.credit === 0,
      `debtor debited grand total 1180 (got debit=${debtor.debit}, credit=${debtor.credit})`);
  }

  const sales = data["Sales"];
  check(!!sales, "Sales account appears");
  if (sales) {
    check(sales.credit >= 1000 - 0.5 && sales.debit === 0,
      `Sales is CREDITED (>=1000), not debited (got debit=${sales.debit}, credit=${sales.credit})`);
  }

  const cgst = data["Output CGST"];
  const sgst = data["Output SGST"];
  check(cgst && cgst.credit >= 90 - 0.5, `Output CGST credited >=90 (got ${cgst ? cgst.credit : "n/a"})`);
  check(sgst && sgst.credit >= 90 - 0.5, `Output SGST credited >=90 (got ${sgst ? sgst.credit : "n/a"})`);

  // Page-level totals must balance (the page renders these).
  const totalsText = (await page.locator("body").innerText()) || "";
  const m = totalsText.match(/Total[^0-9]*([0-9.,]+)[^0-9]*([0-9.,]+)/i);
  if (m) {
    const td = parseFloat(m[1].replace(/,/g, "")) || 0;
    const tc = parseFloat(m[2].replace(/,/g, "")) || 0;
    check(near(td, tc), `page totals balance (debit=${td}, credit=${tc})`);
  }
} catch (e) {
  failures += 1;
  console.log("  ✗ EXCEPTION:", e.message);
} finally {
  await browser.close();
}

console.log(`\nUI ACCOUNTING RESULT: ${failures === 0 ? "PASS" : failures + " check(s) FAILED"}`);
process.exit(failures === 0 ? 0 : 1);
