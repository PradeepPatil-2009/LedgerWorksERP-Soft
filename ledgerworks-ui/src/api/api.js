import axios from "axios";
import { clearAuthSession } from "./authToken";

// Base URL is overridable for deployment; defaults to the local backend.
// `withCredentials: true` makes the browser send the auth cookies (access_token
// / refresh_token) on every request — the JWT is never attached as a header.
const API = axios.create({
  baseURL: process.env.REACT_APP_API_URL || "http://localhost:8080/api",
  withCredentials: true,
});

// ================= 401 HANDLING =================
// The access_token cookie can expire. On the first 401/403 for a non-/auth
// request, try to silently refresh the access cookie ONCE and replay the
// original request. If the refresh fails, clear the local session and send the
// user back to login.
API.interceptors.response.use(
  (response) => response,
  async (error) => {
    const status = error?.response?.status;
    const original = error?.config || {};
    const url = original.url || "";

    const isAuthCall = url.includes("/auth/");
    const recoverable = status === 401 || status === 403;

    if (recoverable && !isAuthCall && !original._retried) {
      original._retried = true;
      try {
        await refreshSession();
        return API(original);
      } catch (refreshErr) {
        clearAuthSession();
        if (window.location.pathname !== "/login") {
          window.location.href = "/login";
        }
        return Promise.reject(refreshErr);
      }
    }

    return Promise.reject(error);
  }
);

// ================= COMMON ERROR =================
const handleError = (error, msg = "Something went wrong") => {
  console.error(error);
  alert(error?.response?.data?.message || msg);
  return null;
};

// ================= AUTH =================
// Real login: POST credentials, receive { id, username, role, token } in the
// body (kept for backward compatibility) plus HttpOnly access/refresh cookies.
export const loginUser = async (username, password) => {
  const res = await API.post("/auth/login", { username, password });
  return res.data;
};

// Silently mint a fresh access_token cookie from the refresh_token cookie.
// Resolves (200) on success, rejects (401) when the refresh token is invalid.
// Declared as a hoisted function so the response interceptor above can use it.
export function refreshSession() {
  return API.post("/auth/refresh");
}

// Revoke the refresh token server-side and clear both auth cookies.
export const logoutUser = async () => {
  try {
    await API.post("/auth/logout");
  } catch (e) {
    // Best effort — clear the local session regardless of server response.
  }
};

// ================= CUSTOMER =================
export const getCustomers = async () => {
  try {
    const res = await API.get("/customers");
    return res.data || [];
  } catch (e) {
    return handleError(e, "Failed to load customers");
  }
};

export const saveCustomer = async (data) => {
  try {
    const res = await API.post("/customers", data);
    return res.data;
  } catch (e) {
    return handleError(e);
  }
};

export const updateCustomer = async (id, data) => {
  try {
    const res = await API.put(`/customers/${id}`, data);
    return res.data;
  } catch (e) {
    return handleError(e);
  }
};

export const deleteCustomer = async (id) => {
  try {
    const res = await API.delete(`/customers/${id}`);
    return res.data;
  } catch (e) {
    return handleError(e);
  }
};

export const searchCustomers = async (keyword) => {
  return await API.get(`/customers/search?keyword=${keyword}`);
};

// ================= VENDOR =================
export const getVendors = async () => {
  try {
    const res = await API.get("/vendors");
    return res.data || [];
  } catch (e) {
    return handleError(e);
  }
};

export const saveVendor = async (data) => {
  try {
    const res = await API.post("/vendors", data);
    return res.data;
  } catch (e) {
    return handleError(e);
  }
};

export const updateVendor = async (id, data) => {
  try {
    const res = await API.put(`/vendors/${id}`, data);
    return res.data;
  } catch (e) {
    return handleError(e);
  }
};

export const deleteVendor = async (id) => {
  try {
    const res = await API.delete(`/vendors/${id}`);
    return res.data;
  } catch (e) {
    return handleError(e);
  }
};

// ================= DELIVERY CHALLAN =================
export const getDeliveryChallans = async () => {
  try {
    const res = await API.get("/delivery-challan");
    return res.data || [];
  } catch (e) {
    console.error(e);
    return [];
  }
};

export const createDeliveryChallan = async (data) => {
  try {
    const res = await API.post("/delivery-challan", data);
    return res.data;
  } catch (e) {
    return handleError(e);
  }
};

export const updateDeliveryChallan = async (id, data) => {
  try {
    const res = await API.put(`/delivery-challan/${id}`, data);
    return res.data;
  } catch (e) {
    return handleError(e);
  }
};

export const cancelDeliveryChallan = async (id) => {
  try {
    const res = await API.post(`/delivery-challan/cancel/${id}`);
    return res.data;
  } catch (e) {
    return handleError(e);
  }
};

export const getDeliveryChallanPdf = (id) =>
  `${API.defaults.baseURL}/delivery-challan/${id}/pdf`;

// ================= INVOICE =================
export const getInvoices = async () => {
  try {
    const res = await API.get("/invoices");
    return res.data || [];
  } catch (e) {
    return handleError(e, "Failed to load invoices");
  }
};

export const getInvoiceByChallan = async (challanId) => {
  try {
    const res = await API.get(`/invoices/by-challan/${challanId}`);
    return res.data;
  } catch (e) {
    return handleError(e);
  }
};

export const createInvoice = async (data) => {
  try {
    const res = await API.post("/invoices", data);
    return res.data;
  } catch (e) {
    return handleError(e);
  }
};

export const convertToInvoice = async (challanId) => {
  try {
    const res = await API.post(`/invoices/convert/${challanId}`);
    return res.data;
  } catch (e) {
    return handleError(e, "Conversion failed");
  }
};

export const getInvoicePdf = (id) =>
  `${API.defaults.baseURL}/invoices/${id}/pdf`;

// ================= REPORTS =================
export const getInvoiceOutstanding = async () => {
  try {
    const res = await API.get("/reports/outstanding");
    return res.data || [];
  } catch (e) {
    return handleError(e);
  }
};

export const getOutstanding = async () => {
  try {
    const res = await API.get("/reports/outstanding");
    return res.data || [];
  } catch (e) {
    return handleError(e, "Failed to load outstanding");
  }
};

export const getCashFlow = async (fromDate, toDate) => {
  try {
    let url = "/reports/cash-flow";
    if (fromDate && toDate) {
      url += `?from=${fromDate}&to=${toDate}`;
    }
    const res = await API.get(url);
    return res.data;
  } catch (e) {
    return handleError(e, "Failed to load cash flow");
  }
};

export const getCustomerAging = async () => {
  try {
    const res = await API.get("/reports/customer-aging");
    return res.data || [];
  } catch (e) {
    return handleError(e, "Failed to load aging report");
  }
};

// ================= PRODUCTION =================
export const getProductions = async () => {
  try {
    const res = await API.get("/production");
    return res.data || [];
  } catch (e) {
    return handleError(e, "Failed to load productions");
  }
};

export const saveProduction = async (data) => {
  try {
    const res = await API.post("/production", data);
    return res.data;
  } catch (e) {
    return handleError(e, "Failed to save production");
  }
};

export const deleteProduction = async (id) => {
  try {
    const res = await API.delete(`/production/${id}`);
    return res.data;
  } catch (e) {
    return handleError(e, "Failed to delete production");
  }
};

// ================= PAYMENT =================
export const saveInvoicePayment = async (data) => {
  try {
    const payload = {
      invoiceNumber: data.invoiceNumber,
      amount: Number(data.amount),
      paymentMode: data.paymentMode || "CASH",
      reference: data.reference || "",
    };
    const res = await API.post("/invoice-payments", payload);
    return res.data;
  } catch (e) {
    return handleError(e, "Failed to save payment");
  }
};

// ================= GST REPORT =================
export const getGstSummary = async (fromDate, toDate) => {
  try {
    const res = await API.get("/gst/summary", {
      params: { fromDate, toDate },
    });
    return res.data;
  } catch (e) {
    return handleError(e, "Failed to load GST report");
  }
};

export default API;
