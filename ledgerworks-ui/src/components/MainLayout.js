import {
  Link,
  Outlet,
  useNavigate,
  useLocation
} from "react-router-dom";

import { useEffect } from "react";

import {
  clearAuthSession,
  getRole,
  getUsername
} from "../api/authToken";

function MainLayout() {

  const navigate = useNavigate();

  const location = useLocation();

  const username = getUsername() || "admin";

  const role = getRole() || "USER";

  const isAdmin = role === "ADMIN";

  // =====================================================
  // REDIRECT
  // =====================================================

  useEffect(() => {

    if (location.pathname === "/") {

      navigate("/dashboard");
    }

  }, [location.pathname, navigate]);

  // =====================================================
  // LOGOUT
  // =====================================================

  const handleLogout = () => {

    clearAuthSession();

    navigate("/login");
  };

  return (

    <div className="app-shell">

      {/* ===================================================== */}
      {/* HEADER */}
      {/* ===================================================== */}

      <header className="app-header">

        <div className="app-header-info">

          <h1 className="app-title">
            LedgerWorks ERP
          </h1>

          <p className="app-user">
            Welcome: <b>{username}</b>
            <span className="app-role-badge">{role}</span>
          </p>

        </div>

        <button
          onClick={handleLogout}
          className="btn btn-danger"
        >
          Logout
        </button>

      </header>

      {/* ===================================================== */}
      {/* MENU SECTION */}
      {/* ===================================================== */}

      <nav className="app-nav">

        {/* ===================================================== */}
        {/* CORE */}
        {/* ===================================================== */}

        <div className="nav-group">

          <h3 className="nav-group-title">
            Core Masters
          </h3>

          <div className="nav-links">

            <Link to="/dashboard">Dashboard</Link>

            <Link to="/customers">Customers</Link>

            <Link to="/vendors">Vendors</Link>

            {isAdmin && <Link to="/users">Users</Link>}

            <Link to="/items">Item Master</Link>

            <Link to="/stock-report">Stock Report</Link>

            <Link to="/backup-management">Backup Management</Link>

          </div>

        </div>

        {/* ===================================================== */}
        {/* TRANSACTIONS */}
        {/* ===================================================== */}

        <div className="nav-group">

          <h3 className="nav-group-title">
            Transactions
          </h3>

          <div className="nav-links">

            <Link to="/purchase">
              Purchase
            </Link>

            <Link to="/invoices">
              Sales Invoice
            </Link>

            <Link to="/delivery-challan">
              Delivery Challan
            </Link>

            <Link to="/invoice-payment">
              Invoice Payment
            </Link>

            <Link to="/invoice-outstanding">
              Invoice Outstanding
            </Link>

            <Link to="/production">
              Production Entry
            </Link>

            <Link to="/production-list">
              Production List
            </Link>

            <Link to="/material-issue">
              Material Issue
            </Link>

            <Link to="/journal">
              Journal Entry
            </Link>

          </div>

        </div>

        {/* ===================================================== */}
        {/* ACCOUNTING */}
        {/* ===================================================== */}

        <div className="nav-group">

          <h3 className="nav-group-title">
            Accounting
          </h3>

          <div className="nav-links">

            <Link to="/accounts">
              Ledger Accounts
            </Link>

            <Link to="/ledger-statement">
              Ledger Statement
            </Link>

          </div>

        </div>

        {/* ===================================================== */}
        {/* REPORTS */}
        {/* ===================================================== */}

        <div className="nav-group">

          <h3 className="nav-group-title">
            Reports
          </h3>

          <div className="nav-links">

            <Link to="/trial-balance">
              Trial Balance
            </Link>

            <Link to="/profit-loss">
              Profit &amp; Loss
            </Link>

            <Link to="/balance-sheet">
              Balance Sheet
            </Link>

            <Link to="/cash-flow">
              Cash Flow
            </Link>

            <Link to="/aging">
              Aging Report
            </Link>

            <Link to="/outstanding">
              Outstanding Report
            </Link>

            <Link to="/gst-report">
              GST Report
            </Link>

            <Link to="/gst-analytics">
              GST Analytics
            </Link>

          </div>

        </div>

      </nav>

      {/* ===================================================== */}
      {/* PAGE CONTENT */}
      {/* ===================================================== */}

      <main className="app-content">

        <Outlet />

      </main>

    </div>
  );
}

export default MainLayout;
