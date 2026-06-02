import {
  BrowserRouter,
  Routes,
  Route,
  Navigate
} from "react-router-dom";

// ================= LAYOUT =================

import MainLayout from "./components/MainLayout";
import ProtectedRoute from "./components/ProtectedRoute";

// ================= PAGES =================

import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";

import Customer from "./pages/Customer";
import Vendor from "./pages/Vendor";
import UserManagement from "./pages/UserManagement";

import ItemMasterPage from "./pages/ItemMasterPage";
import StockReportPage from "./pages/StockReportPage";

import Invoice from "./pages/Invoice";
import InvoiceOutstanding from "./pages/InvoiceOutstanding";
import InvoicePayment from "./pages/InvoicePayment";

import DeliveryChallan from "./pages/DeliveryChallan";
import DeliveryChallanForm from "./pages/DeliveryChallanForm";

import PurchasePage from "./pages/PurchasePage";

import ProductionForm from "./pages/ProductionForm";
import ProductionList from "./pages/ProductionList";

import MaterialIssuePage from "./pages/MaterialIssuePage";

import JournalEntry from "./pages/JournalEntry";

import Accounting from "./pages/Accounting";
import LedgerReport from "./pages/LedgerReport";

import TrialBalance from "./pages/TrialBalance";
import ProfitLoss from "./pages/ProfitLoss";
import BalanceSheet from "./pages/BalanceSheet";
import CashFlow from "./pages/CashFlow";
import Aging from "./pages/Aging";
import Outstanding from "./pages/Outstanding";

import GstReportPage from "./pages/GstReportPage";
import GstAnalyticsPage from "./pages/GstAnalyticsPage";
import BackupManagementPage from "./pages/BackupManagementPage";

// ================= APP =================

function App() {

  return (

    <BrowserRouter>

      <Routes>

        {/* ================= LOGIN ================= */}

        <Route
          path="/login"
          element={<Login />}
        />

        {/* ================= DEFAULT REDIRECT ================= */}

        <Route
          path="/"
          element={
            <Navigate
              to="/login"
              replace
            />
          }
        />

        {/* ================= PROTECTED ROUTES ================= */}

        <Route
          path="/"
          element={
            <ProtectedRoute>
              <MainLayout />
            </ProtectedRoute>
          }
        >

          {/* DASHBOARD */}
          


          <Route
            path="dashboard"
            element={<Dashboard />}
          />
          <Route
    path="/backup-management"
    element={<BackupManagementPage />}
/>
          {/* CORE */}

          <Route
            path="customers"
            element={<Customer />}
          />

          <Route
            path="vendors"
            element={<Vendor />}
          />

          <Route
            path="users"
            element={<UserManagement />}
          />

          <Route
            path="items"
            element={<ItemMasterPage />}
          />

          <Route
            path="stock-report"
            element={<StockReportPage />}
          />

          {/* SALES */}

          <Route
            path="invoices"
            element={<Invoice />}
          />

          <Route
            path="invoice-outstanding"
            element={<InvoiceOutstanding />}
          />

          <Route
            path="invoice-payment"
            element={<InvoicePayment />}
          />

          {/* DELIVERY CHALLAN */}

          <Route
            path="delivery-challan"
            element={<DeliveryChallan />}
          />

          <Route
            path="delivery-challan/create"
            element={<DeliveryChallanForm />}
          />

          <Route
            path="delivery-challan/edit/:id"
            element={<DeliveryChallanForm />}
          />

          {/* PURCHASE */}

          <Route
            path="purchase"
            element={<PurchasePage />}
          />

          {/* PRODUCTION */}

          <Route
            path="production"
            element={<ProductionForm />}
          />

          <Route
            path="production-list"
            element={<ProductionList />}
          />

          {/* MATERIAL ISSUE */}

          <Route
            path="material-issue"
            element={<MaterialIssuePage />}
          />

          {/* ACCOUNTING */}

          <Route
            path="accounts"
            element={<Accounting />}
          />

          <Route
            path="ledger"
            element={<LedgerReport />}
          />

          <Route
            path="ledger-statement"
            element={<LedgerReport />}
          />

          <Route
            path="journal"
            element={<JournalEntry />}
          />

          {/* REPORTS */}

          <Route
            path="trial-balance"
            element={<TrialBalance />}
          />

          <Route
            path="profit-loss"
            element={<ProfitLoss />}
          />

          <Route
            path="balance-sheet"
            element={<BalanceSheet />}
          />

          <Route
            path="cash-flow"
            element={<CashFlow />}
          />

          <Route
            path="aging"
            element={<Aging />}
          />

          <Route
            path="outstanding"
            element={<Outstanding />}
          />

          {/* GST */}

          <Route
            path="gst-report"
            element={<GstReportPage />}
          />

          <Route
            path="gst-analytics"
            element={<GstAnalyticsPage />}
          />

        </Route>

        {/* ================= UNKNOWN URL ================= */}

        <Route
          path="*"
          element={
            <Navigate
              to="/login"
              replace
            />
          }
        />

      </Routes>

    </BrowserRouter>
  );
}

export default App;