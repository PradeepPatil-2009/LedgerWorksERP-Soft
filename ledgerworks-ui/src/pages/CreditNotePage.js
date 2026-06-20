import { useEffect, useState } from "react";

import API from "../api/api";

import { useToast } from "../components/Toast";
import { useTableControls } from "../components/useTableControls";
import Pagination from "../components/Pagination";
import SortableTh from "../components/SortableTh";

function CreditNotePage() {

  const toast = useToast();

  const [notes, setNotes] = useState([]);

  const [loading, setLoading] = useState(false);

  const [form, setForm] = useState({
    noteNumber: "",
    date: "",
    customerId: "",
    partyName: "",
    invoiceReference: "",
    amount: "",
    gstAmount: "",
    reason: "",
    narration: "",
  });

  const tc = useTableControls(notes, {
    searchKeys: ["noteNumber", "date", "partyName", "invoiceReference", "reason"],
    pageSize: 10,
  });
  const { query, setQuery, page, setPage, totalPages, pageItems, total } = tc;

  useEffect(() => {
    loadNotes();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // ================= LOAD =================

  const loadNotes = async () => {

    setLoading(true);

    try {

      const res = await API.get("/credit-notes");

      const data = Array.isArray(res.data) ? res.data : [];

      setNotes(data);

    } catch (err) {

      console.error(err);

      toast.error("Error loading credit notes");

    } finally {

      setLoading(false);
    }
  };

  // ================= CHANGE =================

  const handleChange = (e) => {

    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  // ================= RESET =================

  const resetForm = () => {

    setForm({
      noteNumber: "",
      date: "",
      customerId: "",
      partyName: "",
      invoiceReference: "",
      amount: "",
      gstAmount: "",
      reason: "",
      narration: "",
    });
  };

  // ================= SAVE =================

  const handleSubmit = async () => {

    if (!form.partyName) {

      toast.error("Customer / Party Name Required");

      return;
    }

    if (!form.amount) {

      toast.error("Amount Required");

      return;
    }

    const payload = {
      noteNumber: form.noteNumber || null,
      date: form.date || null,
      customerId: form.customerId ? Number(form.customerId) : null,
      partyName: form.partyName,
      invoiceReference: form.invoiceReference,
      amount: Number(form.amount),
      gstAmount: form.gstAmount ? Number(form.gstAmount) : 0,
      reason: form.reason,
      narration: form.narration,
    };

    try {

      await API.post("/credit-notes", payload);

      toast.success("Credit Note Saved Successfully");

      resetForm();

      loadNotes();

    } catch (err) {

      console.error(err);

      toast.error("Error saving credit note");
    }
  };

  return (

    <div>

      <h2>Credit Notes</h2>

      <p style={{ color: "#666" }}>
        Sales return / discount issued to a customer.
      </p>

      {/* ================= FORM ================= */}

      <div style={{ marginBottom: "10px" }}>

        <input
          name="noteNumber"
          placeholder="Note Number (auto)"
          value={form.noteNumber}
          onChange={handleChange}
        />

        <input
          name="date"
          type="date"
          value={form.date}
          onChange={handleChange}
        />

      </div>

      <div style={{ marginBottom: "10px" }}>

        <input
          name="partyName"
          placeholder="Customer / Party Name"
          value={form.partyName}
          onChange={handleChange}
        />

        <input
          name="customerId"
          placeholder="Customer ID"
          value={form.customerId}
          onChange={handleChange}
        />

      </div>

      <div style={{ marginBottom: "10px" }}>

        <input
          name="invoiceReference"
          placeholder="Invoice Reference"
          value={form.invoiceReference}
          onChange={handleChange}
        />

      </div>

      <div style={{ marginBottom: "10px" }}>

        <input
          name="amount"
          type="number"
          placeholder="Amount"
          value={form.amount}
          onChange={handleChange}
        />

        <input
          name="gstAmount"
          type="number"
          placeholder="GST Amount"
          value={form.gstAmount}
          onChange={handleChange}
        />

      </div>

      <div style={{ marginBottom: "10px" }}>

        <input
          name="reason"
          placeholder="Reason"
          value={form.reason}
          onChange={handleChange}
        />

      </div>

      <div style={{ marginBottom: "10px" }}>

        <textarea
          name="narration"
          placeholder="Narration"
          value={form.narration}
          onChange={handleChange}
          rows="3"
          cols="60"
        />

      </div>

      <button onClick={handleSubmit}>
        Save Credit Note
      </button>

      <button onClick={resetForm}>
        New
      </button>

      <hr />

      {/* ================= TABLE ================= */}

      {loading ? (

        <p>Loading...</p>

      ) : (

        <div style={{ overflowX: "auto" }}>

          <input
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search..."
            style={{ marginBottom: "10px" }}
          />

          <table border="1" cellPadding="5">

            <thead>

              <tr>
                <SortableTh field="id" controls={tc}>ID</SortableTh>
                <SortableTh field="noteNumber" controls={tc}>Note No</SortableTh>
                <SortableTh field="date" controls={tc}>Date</SortableTh>
                <SortableTh field="partyName" controls={tc}>Party</SortableTh>
                <SortableTh field="invoiceReference" controls={tc}>Invoice Ref</SortableTh>
                <SortableTh field="amount" controls={tc}>Amount</SortableTh>
                <SortableTh field="gstAmount" controls={tc}>GST</SortableTh>
                <SortableTh field="reason" controls={tc}>Reason</SortableTh>
              </tr>

            </thead>

            <tbody>

              {pageItems.length > 0 ? (

                pageItems.map((n) => (

                  <tr key={n.id}>

                    <td>{n.id}</td>

                    <td>{n.noteNumber}</td>

                    <td>{n.date}</td>

                    <td>{n.partyName}</td>

                    <td>{n.invoiceReference}</td>

                    <td>{n.amount}</td>

                    <td>{n.gstAmount}</td>

                    <td>{n.reason}</td>

                  </tr>
                ))

              ) : (

                <tr>

                  <td colSpan="8">
                    No Data Found
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

export default CreditNotePage;
