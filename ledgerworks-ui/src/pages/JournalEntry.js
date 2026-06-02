import { useState, useEffect } from "react";
import axios from "axios";

function JournalEntry() {
  const [accounts, setAccounts] = useState([]);
  const [description, setDescription] = useState("");

  const [entries, setEntries] = useState([
    { accountId: "", type: "DEBIT", amount: "" },
    { accountId: "", type: "CREDIT", amount: "" }
  ]);

  // ✅ Load accounts from API
  useEffect(() => {
    axios
      .get("http://localhost:8080/api/accounts")
      .then((res) => {
        console.log("Accounts:", res.data);
        setAccounts(res.data);
      })
      .catch((err) => console.error("Error loading accounts", err));
  }, []);

  // ✅ Handle input change
  const handleChange = (index, field, value) => {
    const updated = [...entries];
    updated[index][field] = value;
    setEntries(updated);
  };

  // ✅ Add new row
  const addRow = () => {
    setEntries([...entries, { accountId: "", type: "DEBIT", amount: "" }]);
  };

  // ✅ Save journal
  const saveJournal = async () => {
    try {
      // 🔴 Validation: account required
      for (let e of entries) {
        if (!e.accountId) {
          alert("Please select account ❌");
          return;
        }
      }

      const debitRows = entries.filter(e => e.type === "DEBIT");
      const creditRows = entries.filter(e => e.type === "CREDIT");

      if (debitRows.length === 0 || creditRows.length === 0) {
        alert("Both DEBIT and CREDIT required ❌");
        return;
      }

      const totalDebit = debitRows.reduce(
        (sum, e) => sum + Number(e.amount || 0),
        0
      );

      const totalCredit = creditRows.reduce(
        (sum, e) => sum + Number(e.amount || 0),
        0
      );

      if (totalDebit !== totalCredit) {
        alert("Debit and Credit must match ❌");
        return;
      }

      // ✅ FINAL CORRECT PAYLOAD
      const payload = entries.map(e => ({
        accountId: Number(e.accountId),   // 🔥 IMPORTANT FIX
        type: e.type,
        amount: Number(e.amount),
        description: description || ""
      }));

      console.log("Sending payload:", payload);

      await axios.post("http://localhost:8080/api/journal", payload);

      alert("Saved Successfully ✅");

      // 🔄 Reset form
      setEntries([
        { accountId: "", type: "DEBIT", amount: "" },
        { accountId: "", type: "CREDIT", amount: "" }
      ]);
      setDescription("");

    } catch (error) {
      console.error("FULL ERROR:", error.response?.data);
      alert("Error saving ❌");
    }
  };

  return (
    <div>
      <h2>Journal Entry</h2>

      {/* ✅ Description */}
      <input
        placeholder="Description (optional)"
        value={description}
        onChange={(e) => setDescription(e.target.value)}
      />

      <br /><br />

      {/* ✅ Table */}
      <table border="1">
        <thead>
          <tr>
            <th>Account</th>
            <th>Type</th>
            <th>Amount</th>
          </tr>
        </thead>

        <tbody>
          {entries.map((row, index) => (
            <tr key={index}>
              
              {/* ✅ Account Dropdown */}
              <td>
                <select
                  value={row.accountId}
                  onChange={(e) =>
                    handleChange(index, "accountId", e.target.value)
                  }
                >
                  <option value="">Select Account</option>

                  {accounts.map((acc) => (
                    <option key={acc.id} value={acc.id}>
                      {acc.accountName}
                    </option>
                  ))}
                </select>
              </td>

              {/* ✅ Type */}
              <td>
                <select
                  value={row.type}
                  onChange={(e) =>
                    handleChange(index, "type", e.target.value)
                  }
                >
                  <option value="DEBIT">DEBIT</option>
                  <option value="CREDIT">CREDIT</option>
                </select>
              </td>

              {/* ✅ Amount */}
              <td>
                <input
                  type="number"
                  value={row.amount}
                  onChange={(e) =>
                    handleChange(index, "amount", e.target.value)
                  }
                />
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <br />

      {/* ✅ Buttons */}
      <button onClick={addRow}>Add Row ➕</button>
      <button onClick={saveJournal}>Save 💾</button>
    </div>
  );
}

export default JournalEntry;