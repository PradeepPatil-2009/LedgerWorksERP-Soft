import { useState, useEffect } from "react";
import axios from "axios";

function LedgerReport() {
const [accounts, setAccounts] = useState([]);
const [selectedAccount, setSelectedAccount] = useState("");
const [transactions, setTransactions] = useState([]);

// ✅ NEW STATES (DATE FILTER)
const [fromDate, setFromDate] = useState("");
const [toDate, setToDate] = useState("");

// ✅ Load accounts
useEffect(() => {
axios
.get("http://localhost:8080/api/accounts")
.then((res) => setAccounts(res.data))
.catch((err) => console.error(err));
}, []);

// ✅ Load ledger with date filter
const loadLedger = async () => {
if (!selectedAccount) {
alert("Select account ❌");
return;
}

try {
  let url = `http://localhost:8080/api/ledger-statement/${selectedAccount}`;

  // ✅ ADD QUERY PARAMS
  const params = [];
  if (fromDate) params.push(`from=${fromDate}`);
  if (toDate) params.push(`to=${toDate}`);

  if (params.length > 0) {
    url += "?" + params.join("&");
  }

  const res = await axios.get(url);

  console.log("Ledger Data:", res.data);

  setTransactions(res.data);
} catch (error) {
  console.error("Error:", error);
  alert("Failed to load ledger ❌");
}

};

return (
<div>
<h2>Ledger Report</h2>

  {/* ✅ Account Dropdown */}
  <select
    value={selectedAccount}
    onChange={(e) => setSelectedAccount(e.target.value)}
  >
    <option value="">Select Account</option>
    {accounts.map((acc) => (
      <option key={acc.id} value={acc.id}>
        {acc.accountName}
      </option>
    ))}
  </select>

  {/* ✅ DATE FILTER UI */}
  <br /><br />

  <label>From: </label>
  <input
    type="date"
    value={fromDate}
    onChange={(e) => setFromDate(e.target.value)}
  />

  <label style={{ marginLeft: "10px" }}>To: </label>
  <input
    type="date"
    value={toDate}
    onChange={(e) => setToDate(e.target.value)}
  />

  <br /><br />

  <button onClick={loadLedger}>Load</button>

  {/* ✅ TABLE */}
  <table border="1" style={{ marginTop: "20px" }}>
    <thead>
      <tr>
        <th>Date</th>
        <th>Debit</th>
        <th>Credit</th>
        <th>Balance</th>
      </tr>
    </thead>

    <tbody>
      {transactions.length > 0 ? (
        transactions.map((t, i) => (
          <tr key={i}>
            <td>{t.date}</td>

            {/* ✅ Show only value if > 0 */}
            <td>{t.debit > 0 ? t.debit : "-"}</td>
            <td>{t.credit > 0 ? t.credit : "-"}</td>

            {/* ✅ Dr / Cr format */}
            <td>
              {t.balance >= 0
                ? `${t.balance} Dr`
                : `${Math.abs(t.balance)} Cr`}
            </td>
          </tr>
        ))
      ) : (
        <tr>
          <td colSpan="4">No Data Found ❌</td>
        </tr>
      )}
    </tbody>
  </table>
</div>

);
}

export default LedgerReport;