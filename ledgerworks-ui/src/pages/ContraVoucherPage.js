import { useEffect, useState } from "react";

import API from "../api/api";
import { useToast } from "../components/Toast";

// =====================================================
// Contra Voucher - cash <-> bank transfer.
// Debit the "to" account, credit the "from" account (backend).
// =====================================================

const EMPTY_FORM = {
  date: new Date().toISOString().slice(0, 10),
  fromAccount: "Cash",
  toAccount: "Bank",
  amount: "",
  narration: "",
};

function ContraVoucherPage() {
  const toast = useToast();

  const [vouchers, setVouchers] = useState([]);
  const [form, setForm] = useState(EMPTY_FORM);
  const [saving, setSaving] = useState(false);

  // ================= LOAD =================
  const load = async () => {
    try {
      const res = await API.get("/contra-vouchers");
      setVouchers(res.data || []);
    } catch (err) {
      console.error(err);
      toast.error("Failed to load contra vouchers");
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
    if (!form.fromAccount || !form.toAccount) {
      toast.error("From and To accounts are required");
      return;
    }

    if (form.fromAccount.trim().toLowerCase() === form.toAccount.trim().toLowerCase()) {
      toast.error("From and To accounts must differ");
      return;
    }

    const amountNum = parseFloat(form.amount);
    if (!amountNum || amountNum <= 0) {
      toast.error("Enter a valid amount");
      return;
    }

    try {
      setSaving(true);
      await API.post("/contra-vouchers", {
        date: form.date,
        fromAccount: form.fromAccount,
        toAccount: form.toAccount,
        amount: amountNum,
        narration: form.narration,
      });
      toast.success("Contra voucher saved");
      resetForm();
      load();
    } catch (err) {
      console.error(err);
      toast.error("Error saving contra voucher");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div style={{ padding: "20px" }}>
      <h2>Contra Voucher</h2>
      <p style={{ color: "#666", marginTop: 0 }}>
        Transfer between cash and bank accounts.
      </p>

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
          name="fromAccount"
          placeholder="From Account (e.g. Cash)"
          value={form.fromAccount}
          onChange={handleChange}
        />

        <input
          name="toAccount"
          placeholder="To Account (e.g. Bank)"
          value={form.toAccount}
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

      <div style={{ overflowX: "auto" }}>
        <table border="1" cellPadding="10" width="100%">
          <thead>
            <tr>
              <th>Voucher No</th>
              <th>Date</th>
              <th>From</th>
              <th>To</th>
              <th>Amount</th>
              <th>Narration</th>
            </tr>
          </thead>
          <tbody>
            {vouchers.length > 0 ? (
              vouchers.map((v) => (
                <tr key={v.id}>
                  <td>{v.voucherNumber}</td>
                  <td>{v.date}</td>
                  <td>{v.fromAccount}</td>
                  <td>{v.toAccount}</td>
                  <td>{v.amount}</td>
                  <td>{v.narration}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="6">No contra vouchers found</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default ContraVoucherPage;
