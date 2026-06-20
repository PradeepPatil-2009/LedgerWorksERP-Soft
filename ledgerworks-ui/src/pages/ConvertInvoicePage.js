import { useEffect, useState } from "react";

import API from "../api/api";

import { useToast } from "../components/Toast";

function ConvertInvoicePage() {

  const toast = useToast();

  const [challans, setChallans] = useState([]);

  const [loading, setLoading] = useState(false);

  const [converting, setConverting] = useState(null);

  const [createdInvoice, setCreatedInvoice] = useState(null);

  // ================= LOAD =================

  const loadChallans = async () => {

    setLoading(true);

    try {

      const res = await API.get("/delivery-challan");

      setChallans(Array.isArray(res.data) ? res.data : []);

    } catch (err) {

      console.error(err);

      toast.error("Failed to load delivery challans");

    } finally {

      setLoading(false);
    }
  };

  useEffect(() => {
    loadChallans();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // ================= CONVERT =================

  const generateInvoice = async (id) => {

    setConverting(id);

    try {

      const res = await API.post("/invoices/convert/" + id);

      setCreatedInvoice(res.data);

      toast.success(
        "Invoice " +
          (res.data?.invoiceNumber || "") +
          " generated"
      );

      loadChallans();

    } catch (err) {

      console.error(err);

      toast.error(
        err?.response?.data?.message ||
          "Failed to generate invoice"
      );

    } finally {

      setConverting(null);
    }
  };

  // ================= FORMAT =================

  const isConverted = (dc) =>
    dc.invoiceCreated === true || dc.invoice != null;

  return (

    <div style={{ padding: "20px" }}>

      <h2>Convert Delivery Challan to Invoice</h2>

      <div style={{ marginBottom: "10px" }}>

        <button onClick={loadChallans}>
          Refresh
        </button>

      </div>

      {createdInvoice && (

        <div
          style={{
            border: "1px solid #4caf50",
            background: "#f1f8e9",
            padding: "10px",
            marginBottom: "15px",
          }}
        >

          <strong>Invoice Created</strong>

          <div>
            Invoice Number:{" "}
            {createdInvoice.invoiceNumber}
          </div>

          <div>
            Grand Total:{" "}
            {createdInvoice.grandTotal}
          </div>

        </div>
      )}

      {loading ? (

        <p>Loading...</p>

      ) : (

        <div style={{ overflowX: "auto" }}>

          <table
            border="1"
            cellPadding="8"
            width="100%"
          >

            <thead>

              <tr>
                <th>Challan No</th>
                <th>Date</th>
                <th>Customer</th>
                <th>Grand Total</th>
                <th>Status</th>
                <th>Action</th>
              </tr>

            </thead>

            <tbody>

              {challans.length > 0 ? (

                challans.map((dc) => (

                  <tr key={dc.id}>

                    <td>{dc.challanNumber}</td>

                    <td>{dc.challanDate}</td>

                    <td>{dc.customerName}</td>

                    <td>{dc.grandTotal}</td>

                    <td>
                      {isConverted(dc)
                        ? "Invoiced"
                        : dc.status}
                    </td>

                    <td>

                      {isConverted(dc) ? (

                        <span>Invoiced</span>

                      ) : (

                        <button
                          disabled={converting === dc.id}
                          onClick={() =>
                            generateInvoice(dc.id)
                          }
                        >
                          {converting === dc.id
                            ? "Generating..."
                            : "Generate Invoice"}
                        </button>
                      )}

                    </td>

                  </tr>
                ))

              ) : (

                <tr>
                  <td colSpan="6">
                    No Delivery Challans Found
                  </td>
                </tr>

              )}

            </tbody>

          </table>

        </div>
      )}

    </div>
  );
}

export default ConvertInvoicePage;
