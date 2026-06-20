// Real-browser (Chromium) walkthrough of LedgerWorks ERP.
// Logs in as admin, then visits every feature route in sequence and saves a
// numbered, full-page screenshot. Output: docs/screenshots/NN-<feature>.png
//
// Usage: BASE_URL=http://localhost:3000 node e2e/screenshots.mjs
import { chromium } from "playwright";
import fs from "fs";
import path from "path";

const BASE = process.env.BASE_URL || "http://localhost:3000";
const OUT = process.env.OUT_DIR || "docs/screenshots";
fs.mkdirSync(OUT, { recursive: true });

// Feature routes grouped the way the nav presents them.
const ROUTES = [
  // Core Masters
  ["customers", "Customers"],
  ["vendors", "Vendors"],
  ["items", "Item-Master"],
  ["stock-report", "Stock-Report"],
  ["company-settings", "Company-Settings"],
  ["states", "State-Master"],
  ["opening-stock", "Opening-Stock"],
  ["users", "Users"],
  // Transactions
  ["purchase", "Purchase"],
  ["invoices", "Sales-Invoice"],
  ["delivery-challan", "Delivery-Challan"],
  ["convert-invoice", "Convert-To-Invoice"],
  ["invoice-payment", "Invoice-Payment"],
  ["invoice-outstanding", "Invoice-Outstanding"],
  ["production", "Production-Entry"],
  ["production-list", "Production-List"],
  ["material-issue", "Material-Issue"],
  ["journal", "Journal-Entry"],
  // Accounting
  ["accounts", "Ledger-Accounts"],
  ["ledger-statement", "Ledger-Statement"],
  ["receipt-vouchers", "Receipt-Voucher"],
  ["payment-vouchers", "Payment-Voucher"],
  ["contra-vouchers", "Contra-Voucher"],
  ["credit-notes", "Credit-Note"],
  ["debit-notes", "Debit-Note"],
  // Reports
  ["trial-balance", "Trial-Balance"],
  ["profit-loss", "Profit-And-Loss"],
  ["balance-sheet", "Balance-Sheet"],
  ["cash-flow", "Cash-Flow"],
  ["aging", "Aging-Report"],
  ["outstanding", "Outstanding-Report"],
  ["gst-report", "GST-Report"],
  ["gst-analytics", "GST-Analytics"],
  // Admin
  ["number-series", "Number-Series"],
  ["financial-years", "Financial-Years"],
  ["import", "Data-Import"],
  ["audit-logs", "Audit-Log"],
  ["settings", "Settings"],
  ["gst-rates-settings", "GST-Rates"],
  ["change-password", "Change-Password"],
  ["backup-management", "Backup-Management"],
];

let n = 0;
async function shot(page, name) {
  n += 1;
  const file = path.join(OUT, `${String(n).padStart(2, "0")}-${name}.png`);
  try {
    await page.screenshot({ path: file, fullPage: true });
    console.log("OK   ", file);
  } catch (e) {
    console.log("FAIL ", file, e.message);
  }
}

let browser;
try {
  // Prefer the system Google Chrome (already installed) — no browser download needed.
  browser = await chromium.launch({ headless: true, channel: "chrome" });
} catch (e) {
  console.log("chrome channel unavailable, using bundled chromium:", e.message);
  browser = await chromium.launch({ headless: true });
}
const ctx = await browser.newContext({ viewport: { width: 1440, height: 900 } });
const page = await ctx.newPage();
page.setDefaultTimeout(15000);

// 01 — Login page
await page.goto(`${BASE}/login`, { waitUntil: "domcontentloaded" }).catch(() => {});
await page.waitForTimeout(800);
await shot(page, "Login");

// Authenticate as the seeded admin.
await page.fill("#login-username", "admin");
await page.fill("#login-password", "admin123");
await Promise.all([
  page.waitForURL("**/dashboard", { timeout: 15000 }).catch(() => {}),
  page.click("button[type=submit]"),
]);
await page.waitForTimeout(1500);
await shot(page, "Dashboard");

// Walk every feature route.
for (const [route, label] of ROUTES) {
  await page.goto(`${BASE}/${route}`, { waitUntil: "domcontentloaded" }).catch(() => {});
  // let data load / render
  await page.waitForLoadState("networkidle", { timeout: 8000 }).catch(() => {});
  await page.waitForTimeout(900);
  await shot(page, label);
}

await browser.close();
console.log(`\nDone. ${n} screenshots in ${OUT}`);
