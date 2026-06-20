import { useEffect, useState } from "react";

import API from "../api/api";
import { useToast } from "../components/Toast";
import { useTableControls } from "../components/useTableControls";
import Pagination from "../components/Pagination";
import SortableTh from "../components/SortableTh";

// =====================================================
// Payment Voucher - money paid TO a vendor.
// Debit the vendor, credit Cash/Bank (handled by backend).
// =====================================================

const EMPTY_FORM = {
  date: new Date().toISOString().slice(0, 10),
  partyName: "",
  amount: "",
  paymentMode: "CASH",
  reference: "",
  narration: "",
};

function PaymentVoucherPage() {
  const toast = useToast();

  const [vouchers, setVouchers] = useState([]);
  const [form, setForm] = useState(EMPTY_FORM);
  const [saving, setSaving] = useState(false);

  const tc = useTableControls(vouchers, {
    searchKeys: ["voucherNumber", "date", "partyName", "paymentMode", "reference", "narration"],
    pageSize: 10,
  });
  const { query, setQuery, page, setPage, totalPages, pageItems, total } = tc;

  // ================= LOAD =================
  const load = async () => {
    try {
      const res = await API.get("/payment-vouchers");
      setVouchers(res.data || []);
    } catch (err) {
      console.error(err);
      toast.error("Failed to load payment vouchers");
    }
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // ================= CHANGE =================
  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const resetForm = () => setForm(EMPTY_FORM);

  // ================= SAVE =================
  const save = async () => {
    if (!form.partyName) {
      toast.error("Vendor / Party name required");
      return;
    }

    const amountNum = parseFloat(form.amount);
    if (!amountNum || amountNum <= 0) {
      toast.error("Enter a valid amount");
      return;
    }

    try {
      setSaving(true);
      await API.post("/payment-vouchers", {
        date: form.date,
        partyName: form.partyName,
        amount: amountNum,
        paymentMode: form.paymentMode,
        reference: form.reference,
        narration: form.narration,
      });
      toast.success("Payment voucher saved");
      resetForm();
      load();
    } catch (err) {
      console.error(err);
      toast.error("Error saving payment voucher");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div style={{ padding: "20px" }}>
      <h2>Payment Voucher</h2>
      <p style={{ color: "#666", marginTop: 0 }}>Money paid to a vendor.</p>

      <div
        style={{
          display: "grid",
          gap: "10px",
          maxWidth: "420px",
        }}
      >
        <label>
          Date
          <input
            type="date"
            name="date"
            value={form.date}
            onChange={handleChange}
            style={{ width: "100%" }}
          />
        </label>

        <input
          name="partyName"
          placeholder="Vendor / Party Name"
          value={form.partyName}
          onChange={handleChange}
        />

        <input
          name="amount"
          type="number"
          min="0"
          step="0.01"
          placeholder="Amount"
          value={form.amount}
          onChange={handleChange}
        />

        <label>
          Payment Mode
          <select
            name="paymentMode"
            value={form.paymentMode}
            onChange={handleChange}
            style={{ width: "100%" }}
          >
            <option value="CASH">Cash</option>
            <option value="BANK">Bank</option>
            <option value="UPI">UPI</option>
          </select>
        </label>

        <input
          name="reference"
          placeholder="Reference (cheque / txn no.)"
          value={form.reference}
          onChange={handleChange}
        />

        <textarea
          name="narration"
          placeholder="Narration"
          value={form.narration}
          onChange={handleChange}
        />

        <div>
          <button onClick={save} disabled={saving}>
            {saving ? "Saving..." : "Save"}
          </button>
          <button onClick={resetForm} style={{ marginLeft: "10px" }}>
            New
          </button>
        </div>
      </div>

      <br />

      <input
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="Search..."
        style={{ marginBottom: "10px" }}
      />

      <div style={{ overflowX: "auto" }}>
        <table border="1" cellPadding="10" width="100%">
          <thead>
            <tr>
              <SortableTh field="voucherNumber" controls={tc}>Voucher No</SortableTh>
              <SortableTh field="date" controls={tc}>Date</SortableTh>
              <SortableTh field="partyName" controls={tc}>Party</SortableTh>
              <SortableTh field="paymentMode" controls={tc}>Mode</SortableTh>
              <SortableTh field="amount" controls={tc}>Amount</SortableTh>
              <SortableTh field="reference" controls={tc}>Reference</SortableTh>
              <SortableTh field="narration" controls={tc}>Narration</SortableTh>
            </tr>
          </thead>
          <tbody>
            {pageItems.length > 0 ? (
              pageItems.map((v) => (
                <tr key={v.id}>
                  <td>{v.voucherNumber}</td>
                  <td>{v.date}</td>
                  <td>{v.partyName}</td>
                  <td>{v.paymentMode}</td>
                  <td>{v.amount}</td>
                  <td>{v.reference}</td>
                  <td>{v.narration}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="7">No payment vouchers found</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      <Pagination
        page={page}
        totalPages={totalPages}
        total={total}
        onPrev={() => setPage(page - 1)}
        onNext={() => setPage(page + 1)}
      />
    </div>
  );
}

export default PaymentVoucherPage;
