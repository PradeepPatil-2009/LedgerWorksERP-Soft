import { Link, Outlet, useNavigate, useLocation } from "react-router-dom";
import { useEffect, useState } from "react";

import { clearAuthSession, getRole, getUsername } from "../api/authToken";
import { logoutUser } from "../api/api";

// Navigation model — grouped exactly as before. `admin: true` on a link or
// `adminOnly: true` on a group hides it for non-ADMIN roles.
const NAV = [
  {
    title: "Core Masters",
    links: [
      { to: "/dashboard", label: "Dashboard" },
      { to: "/customers", label: "Customers" },
      { to: "/vendors", label: "Vendors" },
      { to: "/users", label: "Users", admin: true },
      { to: "/items", label: "Item Master" },
      { to: "/stock-report", label: "Stock Report" },
      { to: "/backup-management", label: "Backup Management" },
      { to: "/company-settings", label: "Company Settings", admin: true },
      { to: "/states", label: "State Master" },
      { to: "/opening-stock", label: "Opening Stock" },
    ],
  },
  {
    title: "Transactions",
    links: [
      { to: "/purchase", label: "Purchase" },
      { to: "/invoices", label: "Sales Invoice" },
      { to: "/delivery-challan", label: "Delivery Challan" },
      { to: "/invoice-payment", label: "Invoice Payment" },
      { to: "/invoice-outstanding", label: "Invoice Outstanding" },
      { to: "/production", label: "Production Entry" },
      { to: "/production-list", label: "Production List" },
      { to: "/material-issue", label: "Material Issue" },
      { to: "/journal", label: "Journal Entry" },
      { to: "/convert-invoice", label: "Convert to Invoice" },
    ],
  },
  {
    title: "Accounting",
    links: [
      { to: "/accounts", label: "Ledger Accounts" },
      { to: "/ledger-statement", label: "Ledger Statement" },
      { to: "/receipt-vouchers", label: "Receipt Voucher" },
      { to: "/payment-vouchers", label: "Payment Voucher" },
      { to: "/contra-vouchers", label: "Contra Voucher" },
      { to: "/credit-notes", label: "Credit Notes" },
      { to: "/debit-notes", label: "Debit Notes" },
    ],
  },
  {
    title: "Reports",
    links: [
      { to: "/trial-balance", label: "Trial Balance" },
      { to: "/profit-loss", label: "Profit & Loss" },
      { to: "/balance-sheet", label: "Balance Sheet" },
      { to: "/cash-flow", label: "Cash Flow" },
      { to: "/aging", label: "Aging Report" },
      { to: "/outstanding", label: "Outstanding Report" },
      { to: "/gst-report", label: "GST Report" },
      { to: "/gst-analytics", label: "GST Analytics" },
    ],
  },
  {
    title: "Admin",
    adminOnly: true,
    links: [
      { to: "/settings", label: "Settings" },
      { to: "/number-series", label: "Number Series" },
      { to: "/financial-years", label: "Financial Years" },
      { to: "/gst-rates-settings", label: "GST Rates" },
      { to: "/import", label: "Data Import" },
      { to: "/audit-logs", label: "Audit Log" },
    ],
  },
];

// Apply and persist the colour theme. Returns the theme that is now active.
function applyTheme(theme) {
  const next = theme === "dark" ? "dark" : "light";
  document.documentElement.dataset.theme = next;
  try {
    localStorage.setItem("theme", next);
  } catch (e) {
    // Storage may be unavailable (private mode); the toggle still works.
  }
  return next;
}

function MainLayout() {
  const navigate = useNavigate();
  const location = useLocation();

  const username = getUsername() || "admin";
  const role = getRole() || "USER";
  const isAdmin = role === "ADMIN";

  const [sidebarOpen, setSidebarOpen] = useState(false);

  const [theme, setTheme] = useState(
    () => document.documentElement.dataset.theme || "light"
  );

  const toggleTheme = () => {
    setTheme((prev) => applyTheme(prev === "dark" ? "light" : "dark"));
  };

  // Default landing
  useEffect(() => {
    if (location.pathname === "/") {
      navigate("/dashboard");
    }
  }, [location.pathname, navigate]);

  // Close the mobile drawer whenever the route changes.
  useEffect(() => {
    setSidebarOpen(false);
  }, [location.pathname]);

  const handleLogout = async () => {
    // Revoke the refresh token and clear the server cookies first, then drop
    // the local identity and return to login.
    await logoutUser();
    clearAuthSession();
    navigate("/login");
  };

  const groups = NAV.filter((g) => !g.adminOnly || isAdmin);
  const activeLink = groups
    .flatMap((g) => g.links)
    .find((l) => l.to === location.pathname);
  const currentTitle = activeLink ? activeLink.label : "LedgerWorks ERP";

  return (
    <div className="app-shell">
      {/* ===================== SIDEBAR ===================== */}
      <aside className={`app-sidebar ${sidebarOpen ? "open" : ""}`}>
        <div className="sidebar-brand">
          <span className="brand-mark">LW</span>
          <span className="brand-text">LedgerWorks ERP</span>
        </div>

        <nav className="sidebar-nav">
          {groups.map((group) => (
            <div className="nav-group" key={group.title}>
              <h3 className="nav-group-title">{group.title}</h3>
              <div className="nav-links">
                {group.links
                  .filter((l) => !l.admin || isAdmin)
                  .map((l) => (
                    <Link
                      key={l.to}
                      to={l.to}
                      className={location.pathname === l.to ? "active" : ""}
                    >
                      {l.label}
                    </Link>
                  ))}
              </div>
            </div>
          ))}
        </nav>

        <div className="sidebar-footer">
          <div className="avatar">{(username[0] || "U").toUpperCase()}</div>
          <div className="sidebar-user-meta">
            <div className="sidebar-username">{username}</div>
            <div className="sidebar-role">{role}</div>
          </div>
        </div>
      </aside>

      {sidebarOpen && (
        <div
          className="sidebar-overlay"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* ===================== MAIN ===================== */}
      <div className="app-main">
        <header className="app-topbar">
          <button
            className="sidebar-toggle"
            onClick={() => setSidebarOpen((o) => !o)}
            aria-label="Toggle menu"
          >
            ☰
          </button>

          <h2 className="topbar-title">{currentTitle}</h2>

          <div className="topbar-user">
            <button
              className="theme-toggle"
              onClick={toggleTheme}
              aria-label="Toggle dark mode"
              title={theme === "dark" ? "Switch to light mode" : "Switch to dark mode"}
            >
              {theme === "dark" ? "☀" : "☾"}
            </button>
            <span className="app-role-badge">{role}</span>
            <span className="topbar-username">{username}</span>
            <Link to="/change-password" className="topbar-link">
              Change Password
            </Link>
            <button className="btn btn-danger btn-sm" onClick={handleLogout}>
              Logout
            </button>
          </div>
        </header>

        <main className="app-content">
          <div className="page-card">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
}

export default MainLayout;
