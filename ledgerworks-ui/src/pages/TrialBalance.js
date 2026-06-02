import { useState } from "react";
import axios from "axios";

function TrialBalance() {

const [rows, setRows] = useState([]);
const [totalDebit, setTotalDebit] = useState(0);
const [totalCredit, setTotalCredit] = useState(0);
const [balanced, setBalanced] = useState(false);

const [fromDate, setFromDate] = useState("");
const [toDate, setToDate] = useState("");

const loadTrialBalance = async () => {
try {

  let url = "http://localhost:8080/reports/trial-balance";

  if (fromDate && toDate) {
    url += `?from=${fromDate}&to=${toDate}`;
  }

  const res = await axios.get(url);

  setRows(res.data.rows);
  setTotalDebit(res.data.totalDebit);
  setTotalCredit(res.data.totalCredit);
  setBalanced(res.data.balanced);

} catch (error) {
  console.error("Error loading trial balance:", error);
  alert("Failed to load trial balance ❌");
}

};

return (
<div>
<h2>Trial Balance</h2>

  {/* Date Filters */}
  <div style={{ marginBottom: "10px" }}>
    From:{" "}
    <input
      type="date"
      value={fromDate}
      onChange={(e) => setFromDate(e.target.value)}
    />

    {" "}To:{" "}
    <input
      type="date"
      value={toDate}
      onChange={(e) => setToDate(e.target.value)}
    />

    {" "}
    <button onClick={loadTrialBalance}>Load</button>
  </div>

  {/* Table */}
  <table border="1" cellPadding="5">
    <thead>
      <tr>
        <th>Account</th>
        <th>Debit</th>
        <th>Credit</th>
      </tr>
    </thead>

    <tbody>
      {rows.map((row, index) => (
        <tr key={index}>
          <td>{row.accountName}</td>
          <td>{row.debit}</td>
          <td>{row.credit}</td>
        </tr>
      ))}
    </tbody>

    {/* Totals */}
    <tfoot>
      <tr>
        <th>Total</th>
        <th>{totalDebit}</th>
        <th>{totalCredit}</th>
      </tr>
    </tfoot>
  </table>

  {/* Balanced Status */}
  <h3 style={{ color: balanced ? "green" : "red" }}>
    {balanced ? "Balanced ✅" : "Not Balanced ❌"}
  </h3>
</div>

);
}

export default TrialBalance;