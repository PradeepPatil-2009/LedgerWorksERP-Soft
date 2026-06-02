/*import axios from "axios";

const API = axios.create({
  baseURL: "http://localhost:8080/api",
});

// ================= AUTH HEADER =================
API.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// ================= COMMON ERROR =================
const handleError = (error, msg = "Something went wrong") => {
  console.error(error);
  alert(error?.response?.data?.message || msg);
  return null;
};

// ================= AUTH =================
export const loginUser = async (username, password) => {
  if (username === "admin" && password === "admin123") {
    return {
      token: "dummy-token",
      user: { username: "admin", role: "ADMIN" },
    };
  }
  throw new Error("Invalid credentials");
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

// ✅ FIXED HERE (IMPORTANT)
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
    const res = await API.get("/reports/invoice-outstanding");
    return res.data || [];
  } catch (e) {
    return handleError(e);
  }
};
// ================= OUTSTANDING REPORT =================
export const getOutstanding = async () => {
  try {
    const res = await API.get("/reports/outstanding");
    return res.data || [];
  } catch (e) {
    return handleError(e, "Failed to load outstanding");
  }
};
// ================= PAYMENT =================
export const saveInvoicePayment = async (data) => {
  try {
    const res = await API.post("/invoice-payments", data);
    return res.data;
  } catch (e) {
    return handleError(e);
  }
};

export default API;


*/
import axios from "axios";

const API = axios.create({
  baseURL: "http://localhost:8080/api",
});

// ================= AUTH HEADER =================
API.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

// ================= COMMON ERROR =================
const handleError = (
  error,
  msg = "Something went wrong"
) => {
  console.error(error);

  alert(
    error?.response?.data?.message || msg
  );

  return null;
};

// ================= AUTH =================
export const loginUser = async (
  username,
  password
) => {
  if (
    username === "admin" &&
    password === "admin123"
  ) {
    return {
      token: "dummy-token",
      user: {
        username: "admin",
        role: "ADMIN",
      },
    };
  }

  throw new Error("Invalid credentials");
};

// ================= CUSTOMER =================
export const getCustomers = async () => {
  try {
    const res = await API.get("/customers");
    return res.data || [];
  } catch (e) {
    return handleError(
      e,
      "Failed to load customers"
    );
  }
};

export const saveCustomer = async (data) => {
  try {
    const res = await API.post(
      "/customers",
      data
    );

    return res.data;
  } catch (e) {
    return handleError(e);
  }
};

export const updateCustomer = async (
  id,
  data
) => {
  try {
    const res = await API.put(
      `/customers/${id}`,
      data
    );

    return res.data;
  } catch (e) {
    return handleError(e);
  }
};

export const deleteCustomer = async (id) => {
  try {
    const res = await API.delete(
      `/customers/${id}`
    );

    return res.data;
  } catch (e) {
    return handleError(e);
  }
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
    const res = await API.post(
      "/vendors",
      data
    );

    return res.data;
  } catch (e) {
    return handleError(e);
  }
};

export const updateVendor = async (
  id,
  data
) => {
  try {
    const res = await API.put(
      `/vendors/${id}`,
      data
    );

    return res.data;
  } catch (e) {
    return handleError(e);
  }
};

export const deleteVendor = async (id) => {
  try {
    const res = await API.delete(
      `/vendors/${id}`
    );

    return res.data;
  } catch (e) {
    return handleError(e);
  }
};

// ================= DELIVERY CHALLAN =================
export const getDeliveryChallans =
  async () => {
    try {
      const res = await API.get(
        "/delivery-challan"
      );

      return res.data || [];
    } catch (e) {
      console.error(e);
      return [];
    }
  };

export const createDeliveryChallan =
  async (data) => {
    try {
      const res = await API.post(
        "/delivery-challan",
        data
      );

      return res.data;
    } catch (e) {
      return handleError(e);
    }
  };

export const updateDeliveryChallan =
  async (id, data) => {
    try {
      const res = await API.put(
        `/delivery-challan/${id}`,
        data
      );

      return res.data;
    } catch (e) {
      return handleError(e);
    }
  };

export const cancelDeliveryChallan =
  async (id) => {
    try {
      const res = await API.post(
        `/delivery-challan/cancel/${id}`
      );

      return res.data;
    } catch (e) {
      return handleError(e);
    }
  };

export const getDeliveryChallanPdf = (
  id
) =>
  `${API.defaults.baseURL}/delivery-challan/${id}/pdf`;

// ================= INVOICE =================
export const getInvoices = async () => {
  try {
    const res = await API.get("/invoices");

    return res.data || [];
  } catch (e) {
    return handleError(
      e,
      "Failed to load invoices"
    );
  }
};

export const getInvoiceByChallan =
  async (challanId) => {
    try {
      const res = await API.get(
        `/invoices/by-challan/${challanId}`
      );

      return res.data;
    } catch (e) {
      return handleError(e);
    }
  };

export const createInvoice = async (
  data
) => {
  try {
    const res = await API.post(
      "/invoices",
      data
    );

    return res.data;
  } catch (e) {
    return handleError(e);
  }
};

export const convertToInvoice =
  async (challanId) => {
    try {
      const res = await API.post(
        `/invoices/convert/${challanId}`
      );

      return res.data;
    } catch (e) {
      return handleError(
        e,
        "Conversion failed"
      );
    }
  };

export const getInvoicePdf = (id) =>
  `${API.defaults.baseURL}/invoices/${id}/pdf`;

// ================= REPORTS =================
export const getInvoiceOutstanding =
  async () => {
    try {
      const res = await API.get(
        "/reports/outstanding"
      );

      return res.data || [];
    } catch (e) {
      return handleError(e);
    }
  };

// ================= OUTSTANDING REPORT =================
export const getOutstanding = async () => {
  try {
    const res = await API.get(
      "/reports/outstanding"
    );

    return res.data || [];
  } catch (e) {
    return handleError(
      e,
      "Failed to load outstanding"
    );
  }
};

// ================= CASH FLOW =================

export const getCashFlow = async (
  fromDate,
  toDate
) => {

  try {

    let url = "/reports/cash-flow";

    if (fromDate && toDate) {

      url += `?from=${fromDate}&to=${toDate}`;
    }

    const res = await API.get(url);

    return res.data;

  } catch (e) {

    return handleError(
      e,
      "Failed to load cash flow"
    );
  }
};

// ================= Aging =================
export const getCustomerAging =
  async () => {

    try {

      const res = await API.get(
        "/reports/customer-aging"
      );

      return res.data || [];

    } catch (e) {

      return handleError(
        e,
        "Failed to load aging report"
      );
    }
  };
export const searchCustomers = async (keyword) => {
  return await API.get(
    `/customers/search?keyword=${keyword}`
  );
};

// ================= PRODUCTION =================

export const getProductions =
  async () => {

    try {

      const res = await API.get(
        "/production"
      );

      return res.data || [];

    } catch (e) {

      return handleError(
        e,
        "Failed to load productions"
      );
    }
  };

export const saveProduction =
  async (data) => {

    try {

      const res = await API.post(
        "/production",
        data
      );

      return res.data;

    } catch (e) {

      return handleError(
        e,
        "Failed to save production"
      );
    }
  };

export const deleteProduction =
  async (id) => {

    try {

      const res = await API.delete(
        `/production/${id}`
      );

      return res.data;

    } catch (e) {

      return handleError(
        e,
        "Failed to delete production"
      );
    }
  };

// ================= PAYMENT =================

export const saveInvoicePayment =
  async (data) => {

    try {

      const payload = {

        invoiceNumber:
          data.invoiceNumber,

        amount:
          Number(data.amount),

        paymentMode:
          data.paymentMode || "CASH",

        reference:
          data.reference || ""
      };

      const res = await API.post(
        "/invoice-payments",
        payload
      );

      return res.data;

    } catch (e) {

      return handleError(
        e,
        "Failed to save payment"
      );
    }
  };


  // ================= GST REPORT =================

export const getGstSummary =
  async (fromDate, toDate) => {

    try {

      const res = await API.get(
        "/gst/summary",
        {
          params: {
            fromDate,
            toDate
          }
        }
      );

      return res.data;

    } catch (e) {

      return handleError(
        e,
        "Failed to load GST report"
      );
    }
  };
export default API;