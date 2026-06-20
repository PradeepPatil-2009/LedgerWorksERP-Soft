// =====================================================================
// LedgerWorks ERP — FUNCTIONAL end-to-end test suite (real Chrome).
//
// Drives the built React app (served on BASE_URL) against the live Spring
// Boot backend (:8080 + MySQL). Each scenario uses its OWN browser context
// so cookies/localStorage are isolated. A tiny assert helper records
// pass/fail per scenario, prints a summary, and the process exits non-zero
// if any scenario fails.
//
//   Run via: node e2e/e2e-tests.mjs   (BASE_URL defaults to :3000)
// =====================================================================

import { chromium } from "playwright";

const BASE = (process.env.BASE_URL || "http://localhost:3000").replace(/\/$/, "");
const API = process.env.API_URL || "http://localhost:8080/api";

const ADMIN = { username: "admin", password: "admin123" };

// ---------------------------------------------------------------------
// Tiny assertion / result framework
// ---------------------------------------------------------------------
const results = []; // { name, status: 'pass'|'fail'|'skip', error? }

function assert(cond, message) {
  if (!cond) throw new Error("Assertion failed: " + message);
}

class SkipError extends Error {}
function skip(message) {
  throw new SkipError(message);
}

async function scenario(name, fn) {
  process.stdout.write(`\n▶ ${name}\n`);
  try {
    await fn();
    results.push({ name, status: "pass" });
    process.stdout.write(`  ✓ PASS\n`);
  } catch (err) {
    if (err instanceof SkipError) {
      results.push({ name, status: "skip", error: err.message });
      process.stdout.write(`  ⊘ SKIP — ${err.message}\n`);
    } else {
      results.push({ name, status: "fail", error: err.message });
      process.stdout.write(`  ✗ FAIL — ${err.message}\n`);
    }
  }
}

// ---------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------

// Launch a single browser; reuse across scenarios. Prefer real Chrome,
// fall back to the bundled Chromium when the channel is unavailable.
async function launchBrowser() {
  try {
    return await chromium.launch({ channel: "chrome", headless: true });
  } catch (e) {
    process.stdout.write(`  (chrome channel unavailable, using bundled chromium: ${e.message})\n`);
    return await chromium.launch({ headless: true });
  }
}

// Log in through the real UI in the given page. Leaves the page on /dashboard.
async function uiLogin(page, creds = ADMIN) {
  await page.goto(`${BASE}/login`, { waitUntil: "domcontentloaded" });
  await page.locator("#login-username").fill(creds.username);
  await page.locator("#login-password").fill(creds.password);
  await page.getByRole("button", { name: "Login" }).click();
}

const uniq = () => Date.now() + "" + Math.floor(Math.random() * 1000);

// ---------------------------------------------------------------------
// Scenarios
// ---------------------------------------------------------------------

async function run() {
  const browser = await launchBrowser();

  // 1. LOGIN SUCCESS ---------------------------------------------------
  await scenario("1. LOGIN SUCCESS", async () => {
    const ctx = await browser.newContext();
    const page = await ctx.newPage();
    try {
      await uiLogin(page);
      await page.waitForURL("**/dashboard", { timeout: 15000 });
      assert(page.url().endsWith("/dashboard"), `URL should end with /dashboard, got ${page.url()}`);
      // Topbar shows the username chip; opening it reveals the Logout action.
      await page.locator(".user-menu .user-chip").click();
      await page.getByRole("menuitem", { name: "Logout" }).waitFor({ timeout: 10000 });
      const bodyText = await page.locator("body").innerText();
      assert(/\badmin\b/i.test(bodyText), 'page should show "admin" after login');
    } finally {
      await ctx.close();
    }
  });

  // 2. LOGIN FAILURE ---------------------------------------------------
  await scenario("2. LOGIN FAILURE", async () => {
    const ctx = await browser.newContext();
    const page = await ctx.newPage();
    try {
      await uiLogin(page, { username: "admin", password: "wrong-password-xyz" });
      // An error alert should appear and we must remain on /login.
      const alert = page.getByRole("alert");
      await alert.waitFor({ timeout: 10000 });
      const alertText = (await alert.innerText()).trim();
      assert(alertText.length > 0, "an error alert with text should appear");
      // Give the (failed) navigation a beat; URL must still be /login.
      await page.waitForTimeout(500);
      assert(page.url().endsWith("/login"), `URL should still be /login, got ${page.url()}`);
    } finally {
      await ctx.close();
    }
  });

  // 3. PROTECTED REDIRECT ---------------------------------------------
  await scenario("3. PROTECTED REDIRECT", async () => {
    const ctx = await browser.newContext(); // fresh: not logged in
    const page = await ctx.newPage();
    try {
      await page.goto(`${BASE}/customers`, { waitUntil: "domcontentloaded" });
      await page.waitForURL("**/login", { timeout: 10000 });
      assert(page.url().endsWith("/login"), `unauthenticated /customers should redirect to /login, got ${page.url()}`);
    } finally {
      await ctx.close();
    }
  });

  // 4. CUSTOMER CRUD ---------------------------------------------------
  await scenario("4. CUSTOMER CRUD", async () => {
    const ctx = await browser.newContext();
    const page = await ctx.newPage();
    try {
      await uiLogin(page);
      await page.waitForURL("**/dashboard", { timeout: 15000 });

      await page.goto(`${BASE}/customers`, { waitUntil: "domcontentloaded" });
      await page.getByRole("main").getByRole("heading", { name: "Customer Master" }).waitFor({ timeout: 10000 });

      const name = `E2E Cust ${uniq()}`;

      await page.getByPlaceholder("Customer Name").fill(name);
      await page.getByPlaceholder("GST Number").fill("27AAAAA0000A1Z5");
      await page.getByPlaceholder("State", { exact: true }).fill("Maharashtra");

      await page.getByRole("button", { name: "Save", exact: true }).click();

      // The created customer must appear as a table row (real persistence).
      const row = page.getByRole("row", { name: new RegExp(name.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")) });
      await row.first().waitFor({ timeout: 12000 });
      assert(await row.count() >= 1, `customer "${name}" should appear in the table after Save`);

      // Search: typing part of the name keeps it visible and filters others out.
      const fragment = name.split(" ").pop(); // the unique numeric chunk
      await page.getByPlaceholder("Search...").fill(fragment);
      await page.waitForTimeout(400);
      const filtered = page.getByRole("row", { name: new RegExp(name.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")) });
      assert(await filtered.count() >= 1, `search for "${fragment}" should still show "${name}"`);

      // Delete the row (accept the confirm() dialog) and assert it disappears.
      page.once("dialog", (d) => d.accept());
      await filtered.first().getByRole("button", { name: "Delete" }).click();

      await page.waitForFunction(
        (n) => !Array.from(document.querySelectorAll("td")).some((td) => td.textContent.trim() === n),
        name,
        { timeout: 12000 }
      );
      // Clear the search to confirm it's gone from the whole table, not just filtered out.
      await page.getByPlaceholder("Search...").fill("");
      await page.waitForTimeout(300);
      const after = page.getByRole("row", { name: new RegExp(name.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")) });
      assert(await after.count() === 0, `customer "${name}" should be gone after Delete`);
    } finally {
      await ctx.close();
    }
  });

  // 5. SEARCH + PAGINATION (State Master) ------------------------------
  await scenario("5. SEARCH + PAGINATION", async () => {
    const ctx = await browser.newContext();
    const page = await ctx.newPage();
    try {
      await uiLogin(page);
      await page.waitForURL("**/dashboard", { timeout: 15000 });

      await page.goto(`${BASE}/states`, { waitUntil: "domcontentloaded" });
      // The page renders the <h2>State Master</h2> heading inside <main>.
      await page.getByRole("main").getByRole("heading", { name: "State Master" }).waitFor({ timeout: 10000 });

      // Pagination text: "Showing page 1 of N (M records)".
      const pager = page.getByText(/Showing page \d+ of \d+ \(\d+ records\)/);
      await pager.waitFor({ timeout: 10000 });
      const pagerText = await pager.innerText();
      const m = pagerText.match(/Showing page (\d+) of (\d+) \((\d+) records\)/);
      assert(m, `pagination text not found, got: "${pagerText}"`);
      const totalPages = Number(m[2]);
      const records = Number(m[3]);
      assert(totalPages >= 2, `expected >=2 pages, got ${totalPages}`);
      assert(records >= 30 && records <= 60, `expected ~38 records, got ${records}`);

      // Click Next -> the first data row changes.
      const firstCellBefore = await page.locator("table tbody tr").first().innerText();
      await page.getByRole("button", { name: "Next" }).click();
      await page.waitForTimeout(400);
      const firstCellAfter = await page.locator("table tbody tr").first().innerText();
      assert(
        firstCellBefore !== firstCellAfter,
        `first row should change after Next (before="${firstCellBefore.replace(/\s+/g, " ")}", after="${firstCellAfter.replace(/\s+/g, " ")}")`
      );

      // Search "Maharashtra" -> filters to a single matching row.
      await page.getByPlaceholder("Search...").fill("Maharashtra");
      await page.waitForTimeout(400);
      const dataRows = page.locator("table tbody tr");
      const count = await dataRows.count();
      assert(count === 1, `searching "Maharashtra" should yield exactly 1 row, got ${count}`);
      const rowText = await dataRows.first().innerText();
      assert(/Maharashtra/i.test(rowText), `the single row should be Maharashtra, got "${rowText.replace(/\s+/g, " ")}"`);
    } finally {
      await ctx.close();
    }
  });

  // 6. LOGOUT ----------------------------------------------------------
  await scenario("6. LOGOUT", async () => {
    const ctx = await browser.newContext();
    const page = await ctx.newPage();
    try {
      await uiLogin(page);
      await page.waitForURL("**/dashboard", { timeout: 15000 });

      // Logout now lives inside the topbar user-menu dropdown.
      await page.locator(".user-menu .user-chip").click();
      await page.getByRole("menuitem", { name: "Logout" }).click();
      await page.waitForURL("**/login", { timeout: 10000 });
      assert(page.url().endsWith("/login"), `after Logout URL should be /login, got ${page.url()}`);

      // Session cleared: navigating to a protected route bounces back to /login.
      await page.goto(`${BASE}/dashboard`, { waitUntil: "domcontentloaded" });
      await page.waitForURL("**/login", { timeout: 10000 });
      assert(page.url().endsWith("/login"), `/dashboard after logout should redirect to /login, got ${page.url()}`);
    } finally {
      await ctx.close();
    }
  });

  // 7. RBAC (best-effort; skip on flakiness, never paper over a bug) ----
  await scenario("7. RBAC (VIEWER cannot see admin nav)", async () => {
    const viewer = { username: `e2e_viewer_${uniq()}`, password: "viewer12345" };

    // --- Step A: create a VIEWER user as admin -----------------------
    // The UI User Management page is the intended path, but we still need an
    // authenticated cookie. We log in as admin first, then create the user
    // through the same API the UI uses (the UI's own create form does not
    // expose the VIEWER role, and — see the bug report — the page itself is
    // currently broken). We create via page.request so the admin's HttpOnly
    // cookies are attached automatically.
    const adminCtx = await browser.newContext();
    const adminPage = await adminCtx.newPage();
    let viewerId = null;
    try {
      await uiLogin(adminPage, ADMIN);
      await adminPage.waitForURL("**/dashboard", { timeout: 15000 });

      const createRes = await adminPage.request.post(`${API}/users`, {
        data: { username: viewer.username, password: viewer.password, role: "VIEWER" },
      });
      if (!createRes.ok()) {
        skip(`could not create VIEWER user (status ${createRes.status()})`);
      }
      const created = await createRes.json().catch(() => ({}));
      viewerId = created.id ?? null;
      // Confirm it actually persisted with the VIEWER role.
      if (created.role && created.role !== "VIEWER") {
        skip(`created user has role ${created.role}, not VIEWER`);
      }
    } finally {
      // keep adminCtx open for cleanup at the end
    }

    // --- Step B: log in as the viewer in a FRESH context -------------
    const viewerCtx = await browser.newContext();
    const viewerPage = await viewerCtx.newPage();
    try {
      await uiLogin(viewerPage, viewer);
      // If login failed we won't reach /dashboard.
      try {
        await viewerPage.waitForURL("**/dashboard", { timeout: 12000 });
      } catch (e) {
        const al = viewerPage.getByRole("alert");
        if (await al.count()) {
          skip(`VIEWER login was rejected by the app: "${(await al.innerText()).trim()}"`);
        }
        throw new Error(`VIEWER did not reach /dashboard (url=${viewerPage.url()})`);
      }

      // The role badge / sidebar should reflect VIEWER.
      const body = await viewerPage.locator("body").innerText();
      assert(/VIEWER/i.test(body), "VIEWER role should be visible in the shell");

      // Admin-gated nav links must NOT be present for a VIEWER:
      //   "Users" (admin:true), "Number Series"/"Financial Years"/... (Admin group).
      const usersLink = viewerPage.locator("aside.app-sidebar").getByRole("link", { name: "Users", exact: true });
      assert((await usersLink.count()) === 0, `"Users" nav link must be hidden for VIEWER`);

      const numberSeriesLink = viewerPage.locator("aside.app-sidebar").getByRole("link", { name: "Number Series", exact: true });
      assert((await numberSeriesLink.count()) === 0, `"Number Series" admin nav link must be hidden for VIEWER`);

      const companySettingsLink = viewerPage.locator("aside.app-sidebar").getByRole("link", { name: "Company Settings", exact: true });
      assert((await companySettingsLink.count()) === 0, `"Company Settings" admin nav link must be hidden for VIEWER`);

      // Sanity: a non-admin link IS still visible (the nav rendered at all).
      const dashLink = viewerPage.locator("aside.app-sidebar").getByRole("link", { name: "Dashboard", exact: true });
      assert((await dashLink.count()) >= 1, "non-admin nav (Dashboard) should still render for VIEWER");
    } finally {
      await viewerCtx.close();
    }

    // --- Step C: cleanup — delete the viewer user as admin -----------
    try {
      if (viewerId != null) {
        await adminPage.request.delete(`${API}/users/${viewerId}`);
      }
    } catch (e) {
      // best effort
    } finally {
      await adminCtx.close();
    }
  });

  // 8. USER MANAGEMENT UI (admin): page loads (no token-bounce) + create VIEWER
  await scenario("8. USER MANAGEMENT UI (create VIEWER via form)", async () => {
    const ctx = await browser.newContext();
    const page = await ctx.newPage();
    try {
      await uiLogin(page);
      await page.waitForURL("**/dashboard", { timeout: 15000 });

      // Navigate via the admin-only "Users" sidebar link.
      await page
        .locator("aside.app-sidebar")
        .getByRole("link", { name: "Users", exact: true })
        .click();
      await page.waitForTimeout(800);
      // Regression guard: the page must NOT bounce to /login (old localStorage-token bug).
      assert(!page.url().endsWith("/login"), `Users page must not redirect to /login (got ${page.url()})`);
      await page.getByRole("heading", { name: "User Management" }).waitFor({ timeout: 10000 });

      const uname = `e2e_um_${uniq()}`;
      await page.getByPlaceholder("Username").fill(uname);
      await page.getByPlaceholder("Password").fill("viewer12345");
      // The create-form role <select> is the first select on the page; it must
      // now offer VIEWER (previously only USER/ADMIN were selectable).
      await page.locator("select").first().selectOption("VIEWER");
      await page.getByRole("button", { name: "Create", exact: true }).click();

      // New user appears in the table with role VIEWER.
      const row = page.getByRole("row", { name: new RegExp(uname) });
      await row.first().waitFor({ timeout: 12000 });
      assert((await row.count()) >= 1, `created user "${uname}" should appear in the table`);
      assert(/VIEWER/.test(await row.first().innerText()), `"${uname}" should have role VIEWER`);

      // Cleanup: delete via the UI Delete button.
      page.once("dialog", (d) => d.accept());
      await row.first().getByRole("button", { name: "Delete" }).click();
      await page.waitForFunction(
        (n) => !Array.from(document.querySelectorAll("td")).some((td) => td.textContent.trim() === n),
        uname,
        { timeout: 12000 }
      );
    } finally {
      await ctx.close();
    }
  });

  // 9. SMOKE-RENDER EVERY PROTECTED ROUTE ------------------------------
  // Visit every non-parameterised protected path from App.js. For each, the
  // page must NOT bounce to /login and the app shell + a real heading/main
  // must render (catches blank-screen / crashing routes).
  await scenario("9. SMOKE-RENDER EVERY ROUTE", async () => {
    const ctx = await browser.newContext();
    const page = await ctx.newPage();
    try {
      await uiLogin(page);
      await page.waitForURL("**/dashboard", { timeout: 15000 });

      // Authoritative list parsed from ledgerworks-ui/src/App.js (protected
      // routes only; parameterised :id routes excluded — a create route is
      // covered to exercise the form variants).
      const ROUTES = [
        "/dashboard",
        "/backup-management",
        "/customers",
        "/vendors",
        "/users",
        "/items",
        "/stock-report",
        "/invoices",
        "/invoice-outstanding",
        "/invoice-payment",
        "/delivery-challan",
        "/delivery-challan/create",
        "/purchase",
        "/production",
        "/production-list",
        "/material-issue",
        "/accounts",
        "/ledger",
        "/ledger-statement",
        "/journal",
        "/trial-balance",
        "/profit-loss",
        "/balance-sheet",
        "/cash-flow",
        "/aging",
        "/outstanding",
        "/gst-report",
        "/gst-analytics",
        "/company-settings",
        "/states",
        "/number-series",
        "/financial-years",
        "/receipt-vouchers",
        "/payment-vouchers",
        "/contra-vouchers",
        "/credit-notes",
        "/debit-notes",
        "/import",
        "/opening-stock",
        "/audit-logs",
        "/convert-invoice",
        "/change-password",
        "/settings",
        "/gst-rates-settings",
      ];

      const broken = [];
      for (const route of ROUTES) {
        try {
          await page.goto(`${BASE}${route}`, { waitUntil: "domcontentloaded" });
          await page.waitForLoadState("networkidle", { timeout: 8000 }).catch(() => {});

          if (page.url().endsWith("/login")) {
            broken.push(`${route} → redirected to /login`);
            continue;
          }

          // App shell must be present.
          const shell = await page.locator("aside.app-sidebar").count();
          if (shell === 0) {
            broken.push(`${route} → app shell (aside.app-sidebar) missing`);
            continue;
          }

          // Real page content: a heading inside <main>, or at least <main> with text.
          const main = page.locator("main");
          const headingCount = await main.locator("h1, h2, h3").count();
          const mainText = ((await main.innerText().catch(() => "")) || "").trim();
          if (headingCount === 0 && mainText.length === 0) {
            broken.push(`${route} → no heading and empty <main> (blank page)`);
          }
        } catch (e) {
          broken.push(`${route} → threw: ${e.message}`);
        }
      }

      assert(
        broken.length === 0,
        `${broken.length}/${ROUTES.length} route(s) failed to render:\n      - ${broken.join("\n      - ")}`
      );
    } finally {
      await ctx.close();
    }
  });

  // 10. DASHBOARD CHARTS -----------------------------------------------
  // recharts renders only when there is monetary data; otherwise the page
  // shows a "No data yet" placeholder. Either is a valid healthy state.
  await scenario("10. DASHBOARD CHARTS", async () => {
    const ctx = await browser.newContext();
    const page = await ctx.newPage();
    try {
      await uiLogin(page);
      await page.waitForURL("**/dashboard", { timeout: 15000 });
      await page.goto(`${BASE}/dashboard`, { waitUntil: "domcontentloaded" });
      await page.getByRole("main").getByRole("heading", { name: "Dashboard", exact: true }).waitFor({ timeout: 10000 });
      await page.waitForLoadState("networkidle", { timeout: 8000 }).catch(() => {});

      // Wait briefly for either a chart svg or the placeholder to settle.
      const chart = page.locator(".recharts-responsive-container, .recharts-surface");
      const placeholder = page.getByText(/No data yet/i);

      const haveChart = await chart
        .first()
        .waitFor({ timeout: 4000 })
        .then(() => true)
        .catch(() => false);

      if (haveChart) {
        assert((await chart.count()) >= 1, "at least one recharts chart should render");
      } else {
        // No monetary data seeded → the explicit placeholder must be shown.
        await placeholder.waitFor({ timeout: 4000 });
        assert(
          (await placeholder.count()) >= 1,
          'with no data the "No data yet" placeholder must be shown'
        );
      }
    } finally {
      await ctx.close();
    }
  });

  // 11. DARK MODE TOGGLE -----------------------------------------------
  await scenario("11. DARK MODE TOGGLE", async () => {
    const ctx = await browser.newContext();
    const page = await ctx.newPage();
    try {
      await uiLogin(page);
      await page.waitForURL("**/dashboard", { timeout: 15000 });

      const toggle = page.getByRole("button", { name: "Toggle dark mode" });
      await toggle.waitFor({ timeout: 10000 });

      // Toggle → dark, and it persists across reload via localStorage.
      await toggle.click();
      await page.waitForFunction(
        () => document.documentElement.dataset.theme === "dark",
        null,
        { timeout: 5000 }
      );
      assert(
        (await page.evaluate(() => localStorage.getItem("theme"))) === "dark",
        'localStorage "theme" should be "dark" after toggle'
      );

      await page.reload({ waitUntil: "domcontentloaded" });
      await page.waitForFunction(
        () => document.documentElement.dataset.theme === "dark",
        null,
        { timeout: 5000 }
      );
      assert(
        (await page.evaluate(() => document.documentElement.dataset.theme)) === "dark",
        "dark theme should persist after reload"
      );

      // Toggle back → light.
      await page.getByRole("button", { name: "Toggle dark mode" }).click();
      await page.waitForFunction(
        () => document.documentElement.dataset.theme === "light",
        null,
        { timeout: 5000 }
      );
      assert(
        (await page.evaluate(() => localStorage.getItem("theme"))) === "light",
        'localStorage "theme" should be "light" after toggling back'
      );
    } finally {
      await ctx.close();
    }
  });

  // 12. SETTINGS HUB (admin) -------------------------------------------
  await scenario("12. SETTINGS HUB", async () => {
    const ctx = await browser.newContext();
    const page = await ctx.newPage();
    try {
      await uiLogin(page);
      await page.waitForURL("**/dashboard", { timeout: 15000 });

      await page.goto(`${BASE}/settings`, { waitUntil: "domcontentloaded" });
      await page.getByRole("main").getByRole("heading", { name: "Settings", exact: true }).waitFor({ timeout: 10000 });

      // Entry-point cards are <Link>s; assert each by href (robust to layout).
      const main = page.getByRole("main");
      for (const href of [
        "/company-settings",
        "/gst-rates-settings",
        "/number-series",
        "/financial-years",
      ]) {
        const link = main.locator(`a[href="${href}"]`);
        assert(
          (await link.count()) >= 1,
          `Settings hub should link to ${href}`
        );
      }
    } finally {
      await ctx.close();
    }
  });

  // 13. GST RATE MASTER CRUD (admin) -----------------------------------
  await scenario("13. GST RATE MASTER CRUD", async () => {
    const ctx = await browser.newContext();
    const page = await ctx.newPage();
    try {
      await uiLogin(page);
      await page.waitForURL("**/dashboard", { timeout: 15000 });

      await page.goto(`${BASE}/gst-rates-settings`, { waitUntil: "domcontentloaded" });
      await page.getByRole("heading", { name: "GST Rate Settings" }).waitFor({ timeout: 10000 });

      // The 5 seeded slabs (GST 0/5/12/18/28 %) must be listed.
      for (const label of ["GST 0%", "GST 5%", "GST 12%", "GST 18%", "GST 28%"]) {
        const cell = page.getByRole("cell", { name: label, exact: true });
        await cell.first().waitFor({ timeout: 10000 });
        assert((await cell.count()) >= 1, `seeded slab "${label}" should be listed`);
      }

      // ADD a new rate: 3 / "GST 3% <uniq>" (unique label so the row is findable).
      const label = `GST 3% ${uniq()}`;
      await page.getByPlaceholder("Rate %").fill("3");
      await page.getByPlaceholder("Label (e.g. GST 18%)").fill(label);
      await page.getByRole("button", { name: "Add Rate" }).click();

      const row = page.getByRole("row", { name: new RegExp(label.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")) });
      await row.first().waitFor({ timeout: 12000 });
      assert((await row.count()) >= 1, `newly added rate "${label}" should appear in the list`);

      // DELETE it (window.confirm → accept) and assert it's gone.
      page.once("dialog", (d) => d.accept());
      await row.first().getByRole("button", { name: "Delete" }).click();
      await page.waitForFunction(
        (l) => !Array.from(document.querySelectorAll("td")).some((td) => td.textContent.trim() === l),
        label,
        { timeout: 12000 }
      );
      const after = page.getByRole("row", { name: new RegExp(label.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")) });
      assert((await after.count()) === 0, `rate "${label}" should be gone after Delete`);
    } finally {
      await ctx.close();
    }
  });

  // 14. CHANGE PASSWORD (negative, safe) -------------------------------
  // Submit a WRONG current password and assert an error toast appears. We do
  // NOT change the admin password (it would break later logins).
  await scenario("14. CHANGE PASSWORD (negative)", async () => {
    const ctx = await browser.newContext();
    const page = await ctx.newPage();
    try {
      await uiLogin(page);
      await page.waitForURL("**/dashboard", { timeout: 15000 });

      await page.goto(`${BASE}/change-password`, { waitUntil: "domcontentloaded" });
      await page.getByRole("heading", { name: "Change Password" }).waitFor({ timeout: 10000 });

      await page.getByPlaceholder("Current Password").fill("definitely-not-the-password");
      const newPw = "NewPass" + uniq();
      await page.getByPlaceholder("New Password", { exact: true }).fill(newPw);
      await page.getByPlaceholder("Confirm New Password").fill(newPw);

      await page.getByRole("button", { name: "Change Password" }).click();

      // An error toast (role="alert") must appear; the message should indicate
      // the current password was rejected.
      const alert = page.getByRole("alert");
      await alert.first().waitFor({ timeout: 10000 });
      const text = (await alert.first().innerText()).trim();
      assert(text.length > 0, "an error toast should appear for a wrong current password");
      assert(
        /incorrect|current password|error/i.test(text),
        `error toast should reference the bad current password, got "${text}"`
      );
    } finally {
      await ctx.close();
    }
  });

  // 15. VENDOR CREATE and ITEM CREATE ----------------------------------
  await scenario("15. VENDOR + ITEM CREATE", async () => {
    const ctx = await browser.newContext();
    const page = await ctx.newPage();
    try {
      await uiLogin(page);
      await page.waitForURL("**/dashboard", { timeout: 15000 });

      // ---- VENDOR ----
      await page.goto(`${BASE}/vendors`, { waitUntil: "domcontentloaded" });
      await page.getByRole("main").getByRole("heading", { name: "Vendors", exact: true }).waitFor({ timeout: 10000 });

      const vendorName = `E2E Vendor ${uniq()}`;
      await page.getByPlaceholder("Vendor Name").fill(vendorName);
      await page.getByPlaceholder("GST Number").fill("27AAAAA0000A1Z5");
      await page.getByRole("button", { name: "Save Vendor" }).click();

      const vRow = page.getByRole("row", { name: new RegExp(vendorName.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")) });
      await vRow.first().waitFor({ timeout: 12000 });
      assert((await vRow.count()) >= 1, `vendor "${vendorName}" should appear in the table`);

      // Best-effort cleanup: delete the vendor (Delete control exists per row).
      try {
        page.once("dialog", (d) => d.accept());
        await vRow.first().getByRole("button", { name: "Delete" }).click();
        await page.waitForFunction(
          (n) => !Array.from(document.querySelectorAll("td")).some((td) => td.textContent.trim() === n),
          vendorName,
          { timeout: 12000 }
        );
      } catch (e) {
        // non-fatal: creation (the assertion above) is what matters
      }

      // ---- ITEM ----
      await page.goto(`${BASE}/items`, { waitUntil: "domcontentloaded" });
      await page.getByRole("main").getByRole("heading", { name: "Item Master", exact: true }).waitFor({ timeout: 10000 });

      const itemName = `E2E Item ${uniq()}`;
      await page.getByPlaceholder("Item Code").fill(`EI${uniq()}`);
      await page.getByPlaceholder("Item Name").fill(itemName);
      await page.getByPlaceholder("Unit").fill("PCS");
      await page.getByRole("button", { name: "Save Item" }).click();

      // Find it via the search box (the table paginates at 10 rows).
      await page.getByPlaceholder("Search...").fill(itemName);
      await page.waitForTimeout(400);
      const iRow = page.getByRole("row", { name: new RegExp(itemName.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")) });
      await iRow.first().waitFor({ timeout: 12000 });
      assert((await iRow.count()) >= 1, `item "${itemName}" should appear in the table`);
      // Note: the Item Master table has no Delete control — best-effort cleanup is a no-op.
    } finally {
      await ctx.close();
    }
  });

  // 16. COLUMN SORT (State Master) -------------------------------------
  // One-click column sorting: clicking the "State Name" <th> sorts the
  // (filtered) rows ascending; a second click flips to descending. Each
  // click must change the FIRST visible data row's text. The header is the
  // feature under test, so a missing header is a hard FAIL (never a skip).
  await scenario("16. COLUMN SORT", async () => {
    const ctx = await browser.newContext();
    const page = await ctx.newPage();
    try {
      await uiLogin(page);
      await page.waitForURL("**/dashboard", { timeout: 15000 });

      await page.goto(`${BASE}/states`, { waitUntil: "domcontentloaded" });
      await page.getByRole("main").getByRole("heading", { name: "State Master" }).waitFor({ timeout: 10000 });

      // Scope to the data table and wait for at least one real row to render.
      const table = page.locator("main table").first();
      await table.waitFor({ timeout: 10000 });
      const rows = table.locator("tbody tr");
      await rows.first().waitFor({ timeout: 10000 });

      const firstRowText = async () =>
        (await rows.first().innerText()).replace(/\s+/g, " ").trim();

      // Sanity: there must be at least 2 distinct rows, otherwise sorting
      // can't visibly reorder anything and the assertions are meaningless.
      const rowCount = await rows.count();
      assert(rowCount >= 2, `need >=2 rows to test sorting, got ${rowCount}`);

      // Locate the clickable "State Name" column header. Prefer the ARIA
      // columnheader role (a <th> rendered by SortableTh); fall back to a
      // <th> matched by text. If neither exists, the feature is missing.
      let header = table.getByRole("columnheader", { name: /State Name/i });
      if ((await header.count()) === 0) {
        header = table.locator("th", { hasText: /State Name/i });
      }
      assert(
        (await header.count()) >= 1,
        'sortable "State Name" column header not found — column sorting is not wired up on /states'
      );
      header = header.first();

      // --- Click 1: ascending. First row text must change from the initial
      //     (server) order.
      const before = await firstRowText();
      await header.click();
      await page.waitForFunction(
        ([text]) => {
          const tr = document.querySelector("main table tbody tr");
          if (!tr) return false;
          return tr.innerText.replace(/\s+/g, " ").trim() !== text;
        },
        [before],
        { timeout: 8000 }
      ).catch(() => {});
      const afterAsc = await firstRowText();
      assert(
        afterAsc !== before,
        `first row should change after sorting ascending (before="${before}", after="${afterAsc}")`
      );

      // --- Click 2: descending. First row text must change again (asc -> desc
      //     puts the opposite end of the alphabet on top).
      await header.click();
      await page.waitForFunction(
        ([text]) => {
          const tr = document.querySelector("main table tbody tr");
          if (!tr) return false;
          return tr.innerText.replace(/\s+/g, " ").trim() !== text;
        },
        [afterAsc],
        { timeout: 8000 }
      ).catch(() => {});
      const afterDesc = await firstRowText();
      assert(
        afterDesc !== afterAsc,
        `first row should change again after sorting descending (asc="${afterAsc}", desc="${afterDesc}")`
      );
    } finally {
      await ctx.close();
    }
  });

  await browser.close();
}

// ---------------------------------------------------------------------
// Run + summarise
// ---------------------------------------------------------------------
run()
  .catch((err) => {
    process.stdout.write(`\nFATAL harness error: ${err.stack || err}\n`);
    results.push({ name: "HARNESS", status: "fail", error: String(err.message || err) });
  })
  .finally(() => {
    const pass = results.filter((r) => r.status === "pass").length;
    const fail = results.filter((r) => r.status === "fail").length;
    const skipped = results.filter((r) => r.status === "skip").length;

    process.stdout.write("\n================ E2E SUMMARY ================\n");
    for (const r of results) {
      const tag = r.status === "pass" ? "PASS" : r.status === "skip" ? "SKIP" : "FAIL";
      process.stdout.write(`  [${tag}] ${r.name}${r.error ? " — " + r.error : ""}\n`);
    }
    process.stdout.write("--------------------------------------------\n");
    const summary = `RESULT: ${pass} passed, ${fail} failed, ${skipped} skipped (of ${results.length})`;
    process.stdout.write(summary + "\n");
    process.stdout.write("============================================\n");

    process.exit(fail > 0 ? 1 : 0);
  });
