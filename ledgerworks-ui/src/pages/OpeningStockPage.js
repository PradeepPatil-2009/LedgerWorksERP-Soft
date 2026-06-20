import React, { useEffect, useState } from "react";
import API from "../api/api";
import { useToast } from "../components/Toast";

// =====================================================
// Opening Stock entry.
// Enter an item with its opening quantity + value; the
// entry is posted into the stock ledger. Existing entries
// are listed below.
// =====================================================

const EMPTY_FORM = {
  itemName: "",
  hsnCode: "",
  unit: "",
  quantity: "",
  rate: "",
  value: "",
  asOfDate: "",
};

export default function OpeningStockPage() {

  const toast = useToast();

  const [entries, setEntries] = useState([]);

  const [form, setForm] = useState({ ...EMPTY_FORM });

  const [saving, setSaving] = useState(false);

  // ================= LOAD =================

  useEffect(() => {
    loadEntries();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const loadEntries = async () => {
    try {
      const res = await API.get("/opening-stock");
      setEntries(res.data || []);
    } catch (err) {
      console.error(err);
      toast.error("Unable to load opening stock");
    }
  };

  // ================= HANDLERS =================

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const resetForm = () => setForm({ ...EMPTY_FORM });

  const handleSubmit = async () => {

    if (!form.itemName.trim()) {
      toast.error("Item name is required");
      return;
    }

    const payload = {
      itemName: form.itemName.trim(),
      hsnCode: form.hsnCode || null,
      unit: form.unit || null,
      quantity: form.quantity ? Number(form.quantity) : 0,
      rate: form.rate ? Number(form.rate) : null,
      value: form.value ? Number(form.value) : null,
      asOfDate: form.asOfDate || null,
    };

    setSaving(true);

    try {
      await API.post("/opening-stock", payload);
      toast.success("Opening stock recorded");
      resetForm();
      loadEntries();
    } catch (err) {
      console.error(err);
      toast.error("Failed to save opening stock");
    } finally {
      setSaving(false);
    }
  };

  // ================= UI =================

  return (

    <div style={{ padding: "20px" }}>

      <h2>Opening Stock</h2>

      {/* ================= FORM ================= */}

      <div style={{ marginBottom: "10px" }}>

        <input
          type="text"
          name="itemName"
          placeholder="Item Name"
          value={form.itemName}
          onChange={handleChange}
          style={{ marginRight: "8px" }}
        />

        <input
          type="text"
          name="hsnCode"
          placeholder="HSN Code"
          value={form.hsnCode}
          onChange={handleChange}
          style={{ marginRight: "8px" }}
        />

        <input
          type="text"
          name="unit"
          placeholder="Unit"
          value={form.unit}
          onChange={handleChange}
        />

      </div>

      <div style={{ marginBottom: "10px" }}>

        <input
          type="number"
          name="quantity"
          placeholder="Opening Qty"
          value={form.quantity}
          onChange={handleChange}
          style={{ marginRight: "8px" }}
        />

        <input
          type="number"
          name="rate"
          placeholder="Rate"
          value={form.rate}
          onChange={handleChange}
          style={{ marginRight: "8px" }}
        />

        <input
          type="number"
          name="value"
          placeholder="Total Value"
          value={form.value}
          onChange={handleChange}
          style={{ marginRight: "8px" }}
        />

        <input
          type="date"
          name="asOfDate"
          value={form.asOfDate}
          onChange={handleChange}
        />

      </div>

      <button
        onClick={handleSubmit}
        disabled={saving}
        style={{ marginRight: "8px" }}
      >
        {saving ? "Saving..." : "Save Opening Stock"}
      </button>

      <button onClick={resetForm} disabled={saving}>
        Clear
      </button>

      <hr />

      {/* ================= TABLE ================= */}

      <div style={{ overflowX: "auto" }}>

        <table
          border="1"
          cellPadding="6"
          style={{ borderCollapse: "collapse", width: "100%" }}
        >

          <thead>
            <tr style={{ background: "#f2f2f2" }}>
              <th>ID</th>
              <th>Item</th>
              <th>HSN</th>
              <th>Unit</th>
              <th>Qty</th>
              <th>Rate</th>
              <th>Value</th>
              <th>As Of Date</th>
            </tr>
          </thead>

          <tbody>

            {entries.length > 0 ? (

              entries.map((e) => (

                <tr key={e.id}>
                  <td>{e.id}</td>
                  <td>{e.itemName}</td>
                  <td>{e.hsnCode}</td>
                  <td>{e.unit}</td>
                  <td>{e.quantity}</td>
                  <td>{e.rate}</td>
                  <td>{e.value}</td>
                  <td>{e.asOfDate}</td>
                </tr>
              ))

            ) : (

              <tr>
                <td colSpan="8" style={{ textAlign: "center" }}>
                  No Opening Stock Entries
                </td>
              </tr>
            )}

          </tbody>

        </table>

      </div>

    </div>
  );
}
