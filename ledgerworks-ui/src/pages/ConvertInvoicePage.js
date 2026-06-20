import { useEffect, useState } from "react";

import API from "../api/api";

import { useToast } from "../components/Toast";

import { useTableControls } from "../components/useTableControls";

import Pagination from "../components/Pagination";

import SortableTh from "../components/SortableTh";

function ConvertInvoicePage() {

  const toast = useToast();

  const [challans, setChallans] = useState([]);

  const [loading, setLoading] = useState(false);

  const [converting, setConverting] = useState(null);

  const [createdInvoice, setCreatedInvoice] = useState(null);

  const tc = useTableControls(challans, {
    searchKeys: ["challanNumber", "challanDate", "customerName", "status"],
    pageSize: 10,
  });

  const { query, setQuery, page, setPage, totalPages, pageItems, total } = tc;

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

        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search..."
          style={{ marginLeft: "8px" }}
        />

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
                <SortableTh field="challanNumber" controls={tc}>Challan No</SortableTh>
                <SortableTh field="challanDate" controls={tc}>Date</SortableTh>
                <SortableTh field="customerName" controls={tc}>Customer</SortableTh>
                <SortableTh field="grandTotal" controls={tc}>Grand Total</SortableTh>
                <th>Status</th>
                <th>Action</th>
              </tr>

            </thead>

            <tbody>

              {pageItems.length > 0 ? (

                pageItems.map((dc) => (

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

          <Pagination
            page={page}
            totalPages={totalPages}
            total={total}
            onPrev={() => setPage(page - 1)}
            onNext={() => setPage(page + 1)}
          />

        </div>
      )}

    </div>
  );
}

export default ConvertInvoicePage;
