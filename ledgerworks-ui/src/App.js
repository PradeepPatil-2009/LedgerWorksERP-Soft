import {
  BrowserRouter,
  Routes,
  Route,
  Navigate
} from "react-router-dom";

// ================= LAYOUT =================

import MainLayout from "./components/MainLayout";
import ProtectedRoute from "./components/ProtectedRoute";
import { ToastProvider } from "./components/Toast";

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
import GstReturnsPage from "./pages/GstReturnsPage";
import BackupManagementPage from "./pages/BackupManagementPage";

import CompanySettingsPage from "./pages/CompanySettingsPage";
import StateMasterPage from "./pages/StateMasterPage";
import NumberSeriesPage from "./pages/NumberSeriesPage";
import FinancialYearPage from "./pages/FinancialYearPage";
import ReceiptVoucherPage from "./pages/ReceiptVoucherPage";
import PaymentVoucherPage from "./pages/PaymentVoucherPage";
import ContraVoucherPage from "./pages/ContraVoucherPage";
import CreditNotePage from "./pages/CreditNotePage";
import DebitNotePage from "./pages/DebitNotePage";
import ImportPage from "./pages/ImportPage";
import OpeningStockPage from "./pages/OpeningStockPage";
import AuditLogPage from "./pages/AuditLogPage";
import ConvertInvoicePage from "./pages/ConvertInvoicePage";

import ChangePasswordPage from "./pages/ChangePasswordPage";
import ProfilePage from "./pages/ProfilePage";
import SettingsPage from "./pages/SettingsPage";
import GstRateSettingsPage from "./pages/GstRateSettingsPage";

// ================= APP =================

function App() {

  return (

    <ToastProvider>

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

          <Route
            path="gst-returns"
            element={<GstReturnsPage />}
          />

          {/* NEW MODULES */}

          <Route
            path="company-settings"
            element={<CompanySettingsPage />}
          />

          <Route
            path="states"
            element={<StateMasterPage />}
          />

          <Route
            path="number-series"
            element={<NumberSeriesPage />}
          />

          <Route
            path="financial-years"
            element={<FinancialYearPage />}
          />

          <Route
            path="receipt-vouchers"
            element={<ReceiptVoucherPage />}
          />

          <Route
            path="payment-vouchers"
            element={<PaymentVoucherPage />}
          />

          <Route
            path="contra-vouchers"
            element={<ContraVoucherPage />}
          />

          <Route
            path="credit-notes"
            element={<CreditNotePage />}
          />

          <Route
            path="debit-notes"
            element={<DebitNotePage />}
          />

          <Route
            path="import"
            element={<ImportPage />}
          />

          <Route
            path="opening-stock"
            element={<OpeningStockPage />}
          />

          <Route
            path="audit-logs"
            element={<AuditLogPage />}
          />

          <Route
            path="convert-invoice"
            element={<ConvertInvoicePage />}
          />

          {/* ACCOUNT / SETTINGS */}

          <Route
            path="profile"
            element={<ProfilePage />}
          />

          <Route
            path="change-password"
            element={<ChangePasswordPage />}
          />

          <Route
            path="settings"
            element={<SettingsPage />}
          />

          <Route
            path="gst-rates-settings"
            element={<GstRateSettingsPage />}
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

    </ToastProvider>
  );
}

export default App;