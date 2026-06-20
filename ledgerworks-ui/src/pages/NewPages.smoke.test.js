// Smoke tests for the new ERP pages.
//
// These render four of the new pages inside <MemoryRouter> and assert each
// mounts without crashing and shows its heading / first field. No network is
// involved: ../api/api is mocked to a default-export axios-like instance whose
// get/put/post resolve to empty data.
//
// As with the existing Login/App tests, the toolchain forces two stubs:
//   * react-router-dom v7 ships an "exports" map that jest 27 (bundled with
//     react-scripts 5) cannot resolve -> virtual mock for MemoryRouter.
//   * axios 1.x ships an ESM entry jest 27 cannot transform; it is pulled in
//     transitively via ../api/authToken (getRole) -> minimal CJS mock.

import { render, screen, waitFor } from "@testing-library/react";

// ---- axios stub (transitive import via authToken) -------------------------
jest.mock("axios", () => {
  const headers = { common: {} };
  return {
    __esModule: true,
    default: {
      defaults: { headers },
      create: jest.fn(),
    },
  };
});

// ---- react-router-dom virtual mock ---------------------------------------
jest.mock(
  "react-router-dom",
  () => ({
    __esModule: true,
    MemoryRouter: ({ children }) => children,
    useNavigate: () => jest.fn(),
  }),
  { virtual: true }
);

// ---- shared axios instance (default export with get/put/post) ------------
// The factory is hoisted above imports, so the mock object is created inside it
// and retrieved afterwards via the mocked module (jest caches the instance).
jest.mock("../api/api", () => ({
  __esModule: true,
  default: {
    get: jest.fn(),
    put: jest.fn(),
    post: jest.fn(),
    delete: jest.fn(),
  },
}));

import { MemoryRouter } from "react-router-dom";
import mockApi from "../api/api";

import CompanySettingsPage from "./CompanySettingsPage";
import NumberSeriesPage from "./NumberSeriesPage";
import ReceiptVoucherPage from "./ReceiptVoucherPage";
import AuditLogPage from "./AuditLogPage";

function renderInRouter(ui) {
  return render(<MemoryRouter>{ui}</MemoryRouter>);
}

beforeEach(() => {
  jest.clearAllMocks();
  localStorage.clear();
  // Every page does an initial API.get on mount; default to empty payloads.
  mockApi.get.mockResolvedValue({ data: [] });
  mockApi.put.mockResolvedValue({ data: {} });
  mockApi.post.mockResolvedValue({ data: {} });
});

describe("CompanySettingsPage", () => {
  test("renders heading and the Company Name field", async () => {
    // Returns an object for company settings rather than a list.
    mockApi.get.mockResolvedValueOnce({ data: {} });

    renderInRouter(<CompanySettingsPage />);

    expect(
      screen.getByRole("heading", { name: /company settings/i })
    ).toBeInTheDocument();

    // The mount-time load briefly shows "Loading..."; the form (and its first
    // field) appears once /company-settings resolves.
    expect(
      await screen.findByPlaceholderText(/company name/i)
    ).toBeInTheDocument();
    expect(mockApi.get).toHaveBeenCalledWith("/company-settings");
  });
});

describe("NumberSeriesPage", () => {
  test("renders heading once the series load resolves", async () => {
    mockApi.get.mockResolvedValueOnce({ data: [] });

    renderInRouter(<NumberSeriesPage />);

    // Page shows a loading state first, then the heading after data resolves.
    expect(
      await screen.findByRole("heading", { name: /number series/i })
    ).toBeInTheDocument();
    expect(screen.getByText(/document type/i)).toBeInTheDocument();
    expect(mockApi.get).toHaveBeenCalledWith("/number-series");
  });
});

describe("ReceiptVoucherPage", () => {
  test("renders heading and the Party Name field", async () => {
    mockApi.get.mockResolvedValueOnce({ data: [] });

    renderInRouter(<ReceiptVoucherPage />);

    expect(
      screen.getByRole("heading", { name: /receipt voucher/i })
    ).toBeInTheDocument();
    expect(
      screen.getByPlaceholderText(/customer \/ party name/i)
    ).toBeInTheDocument();

    await waitFor(() =>
      expect(mockApi.get).toHaveBeenCalledWith("/receipt-vouchers")
    );
  });
});

describe("AuditLogPage", () => {
  test("renders heading and the empty-state row", async () => {
    mockApi.get.mockResolvedValueOnce({ data: [] });

    renderInRouter(<AuditLogPage />);

    expect(
      screen.getByRole("heading", { name: /audit log/i })
    ).toBeInTheDocument();

    // With no logs the table shows its empty-state message.
    expect(
      await screen.findByText(/no audit events found/i)
    ).toBeInTheDocument();
    expect(mockApi.get).toHaveBeenCalledWith("/audit-logs");
  });
});
