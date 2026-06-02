import {
  Link,
  Outlet,
  useNavigate,
  useLocation
} from "react-router-dom";

import { useEffect } from "react";

function MainLayout() {

  const navigate = useNavigate();

  const location = useLocation();

  const username =
    localStorage.getItem("username")
    || "admin";

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

    localStorage.removeItem("token");

    localStorage.removeItem("username");

    navigate("/login");
  };

  return (

    <div
      style={{
        padding: "20px",
        fontFamily: "Arial",
        background: "#f5f5f5",
        minHeight: "100vh"
      }}
    >

      {/* ===================================================== */}
      {/* HEADER */}
      {/* ===================================================== */}

      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          marginBottom: "20px",
          background: "#fff",
          padding: "20px",
          border: "1px solid #ddd"
        }}
      >

        <div>

          <h1
            style={{
              margin: 0,
              color: "#1e293b"
            }}
          >
            LedgerWorks ERP
          </h1>

          <p
            style={{
              marginTop: "5px"
            }}
          >
            Welcome: <b>{username}</b>
          </p>

        </div>

        <button
          onClick={handleLogout}
          style={{
            padding: "10px 18px",
            cursor: "pointer",
            border: "none",
            background: "#dc2626",
            color: "#fff",
            borderRadius: "5px"
          }}
        >
          Logout
        </button>

      </div>

      {/* ===================================================== */}
      {/* MENU SECTION */}
      {/* ===================================================== */}

      <div
        style={{
          background: "#ffffff",
          border: "1px solid #ddd",
          padding: "20px",
          marginBottom: "20px"
        }}
      >

        {/* ===================================================== */}
        {/* CORE */}
        {/* ===================================================== */}

        <div style={{ marginBottom: "20px" }}>

          <h3 style={{ marginBottom: "10px" }}>
            Core Masters
          </h3>

          <div
            style={{
              display: "flex",
              flexWrap: "wrap",
              gap: "15px"
            }}
          >

            <Link to="/dashboard">Dashboard</Link>

            <Link to="/customers">Customers</Link>

            <Link to="/vendors">Vendors</Link>

            <Link to="/users">Users</Link>

            <Link to="/items">Item Master</Link>

            <Link to="/stock-report">Stock Report</Link>
            <Link to="/backup-management">Backup Management </Link>

          </div>

        </div>

        {/* ===================================================== */}
        {/* TRANSACTIONS */}
        {/* ===================================================== */}

        <div style={{ marginBottom: "20px" }}>

          <h3 style={{ marginBottom: "10px" }}>
            Transactions
          </h3>

          <div
            style={{
              display: "flex",
              flexWrap: "wrap",
              gap: "15px"
            }}
          >

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

        <div style={{ marginBottom: "20px" }}>

          <h3 style={{ marginBottom: "10px" }}>
            Accounting
          </h3>

          <div
            style={{
              display: "flex",
              flexWrap: "wrap",
              gap: "15px"
            }}
          >

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


        <div>

          <h3 style={{ marginBottom: "10px" }}>
            Reports
          </h3>

          <div
            style={{
              display: "flex",
              flexWrap: "wrap",
              gap: "15px"
            }}
          >

            <Link to="/trial-balance">
              Trial Balance
            </Link>

            <Link to="/profit-loss">
              Profit & Loss
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

      </div>

      {/* ===================================================== */}
      {/* PAGE CONTENT */}
      {/* ===================================================== */}

      <div
        style={{
          background: "#fff",
          padding: "20px",
          border: "1px solid #ddd"
        }}
      >

        <Outlet />

      </div>

    </div>
  );
}

export default MainLayout;