import { useState } from "react";

import API from "../api/api";
import { useToast } from "../components/Toast";

// GST Returns — generate the GSTR-1 invoice listing and the GSTR-3B summary
// for a chosen date range. Visible to any authenticated user.
export default function GstReturnsPage() {

  const toast = useToast();

  const [fromDate, setFromDate] = useState("");

  const [toDate, setToDate] = useState("");

  const [loading, setLoading] = useState(false);

  // Which report has been generated: "gstr1", "gstr3b" or null.
  const [view, setView] = useState(null);

  const [rows, setRows] = useState([]);

  const [summary, setSummary] = useState(null);

  // Format a numeric/BigDecimal value for display, tolerating null/strings.
  const money = (v) => {
    if (v == null || v === "") return "0.00";
    const n = Number(v);
    return Number.isNaN(n) ? String(v) : n.toFixed(2);
  };

  const ensureRange = () => {
    if (!fromDate || !toDate) {
      toast.error("Please select both From and To dates");
      return false;
    }
    return true;
  };

  // ================= GSTR-1 =================

  const loadGstr1 = async () => {

    if (!ensureRange()) return;

    setLoading(true);

    try {

      const res = await API.get("/gst/gstr1", {
        params: { fromDate, toDate },
      });

      setRows(Array.isArray(res.data) ? res.data : []);

      setSummary(null);

      setView("gstr1");

    } catch (err) {

      console.error("GSTR-1 Error", err);

      toast.error("Error loading GSTR-1");

    } finally {

      setLoading(false);
    }
  };

  // ================= GSTR-3B =================

  const loadGstr3b = async () => {

    if (!ensureRange()) return;

    setLoading(true);

    try {

      const res = await API.get("/gst/gstr3b", {
        params: { fromDate, toDate },
      });

      setSummary(res.data || null);

      setRows([]);

      setView("gstr3b");

    } catch (err) {

      console.error("GSTR-3B Error", err);

      toast.error("Error loading GSTR-3B");

    } finally {

      setLoading(false);
    }
  };

  return (

    <div>

      <h2>GST Returns</h2>

      {/* ================= FILTERS ================= */}

      <div style={{ marginBottom: "12px" }}>

        <label style={{ marginRight: "10px" }}>
          From{" "}
          <input
            type="date"
            value={fromDate}
            onChange={(e) => setFromDate(e.target.value)}
          />
        </label>

        <label style={{ marginRight: "10px" }}>
          To{" "}
          <input
            type="date"
            value={toDate}
            onChange={(e) => setToDate(e.target.value)}
          />
        </label>

        <button
          className="btn-primary"
          onClick={loadGstr1}
          disabled={loading}
        >
          GSTR-1
        </button>

        <button
          className="btn-primary"
          onClick={loadGstr3b}
          disabled={loading}
        >
          GSTR-3B
        </button>

      </div>

      <hr />

      {loading && <p>Loading...</p>}

      {/* ================= GSTR-1 TABLE ================= */}

      {!loading && view === "gstr1" && (

        <div className="table-scroll">

          <table>

            <thead>

              <tr>
                <th>Invoice No</th>
                <th>Date</th>
                <th>Customer</th>
                <th>GSTIN</th>
                <th>Place of Supply</th>
                <th>Taxable</th>
                <th>CGST</th>
                <th>SGST</th>
                <th>IGST</th>
                <th>Total</th>
              </tr>

            </thead>

            <tbody>

              {rows.length > 0 ? (

                rows.map((r, idx) => (

                  <tr key={r.invoiceNumber || idx}>

                    <td>{r.invoiceNumber}</td>
                    <td>{r.invoiceDate}</td>
                    <td>{r.customerName}</td>
                    <td>{r.customerGstin}</td>
                    <td>{r.placeOfSupply}</td>
                    <td>{money(r.taxableValue)}</td>
                    <td>{money(r.cgst)}</td>
                    <td>{money(r.sgst)}</td>
                    <td>{money(r.igst)}</td>
                    <td>{money(r.total)}</td>

                  </tr>
                ))

              ) : (

                <tr>
                  <td colSpan="10">
                    No GSTR-1 records found for this period
                  </td>
                </tr>

              )}

            </tbody>

          </table>

        </div>
      )}

      {/* ================= GSTR-3B SUMMARY ================= */}

      {!loading && view === "gstr3b" && (

        summary ? (

          <div className="card" style={{ maxWidth: "360px" }}>

            <h3>GSTR-3B Summary</h3>

            <p>
              <strong>Total Taxable:</strong> {money(summary.totalTaxableValue)}
            </p>
            <p>
              <strong>CGST:</strong> {money(summary.totalCgst)}
            </p>
            <p>
              <strong>SGST:</strong> {money(summary.totalSgst)}
            </p>
            <p>
              <strong>IGST:</strong> {money(summary.totalIgst)}
            </p>
            <p>
              <strong>Total Tax:</strong> {money(summary.totalTax)}
            </p>
            <p>
              <strong>Invoice Count:</strong> {summary.invoiceCount ?? 0}
            </p>

          </div>

        ) : (

          <p>No GSTR-3B summary found for this period</p>
        )
      )}

    </div>
  );
}
