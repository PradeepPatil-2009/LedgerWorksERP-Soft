import { useCallback, useEffect, useState } from "react";
import API from "../api/api";

function BalanceSheet() {

  // ================= STATES =================

  const [fromDate, setFromDate] = useState("");
  const [toDate, setToDate] = useState("");

  const [data, setData] = useState({
    totalAssets: 0,
    totalLiabilities: 0,
    netProfit: 0,
    balanced: false,
  });

  // ================= DATE FORMAT =================

  const formatDate = (date) => {
    if (!date) return "";
    return new Date(date).toISOString().split("T")[0];
  };

  // ================= LOAD REPORT =================

  const loadBalanceSheet = useCallback(async () => {

    try {

      let url = "/reports/balance-sheet";

      if (fromDate && toDate) {

        const from = formatDate(fromDate);
        const to = formatDate(toDate);

        url += `?from=${from}&to=${to}`;
      }

      const res = await API.get(url);

      console.log("Balance Sheet Data:", res.data);

      setData({
        totalAssets: res.data.totalAssets || 0,
        totalLiabilities: res.data.totalLiabilities || 0,
        netProfit: res.data.netProfit || 0,
        balanced: res.data.balanced || false,
      });

    } catch (err) {

      console.error("Error loading Balance Sheet:", err);

      alert("Failed to load Balance Sheet ❌");
    }
  }, [fromDate, toDate]);

  // ================= AUTO LOAD =================

  useEffect(() => {

    const today = new Date();

    const firstDay = new Date(today.getFullYear(), 0, 1);

    setFromDate(formatDate(firstDay));
    setToDate(formatDate(today));

  }, []);

  // ================= AUTO FETCH =================

  useEffect(() => {

    if (fromDate && toDate) {
      loadBalanceSheet();
    }

  }, [fromDate, toDate, loadBalanceSheet]);
  // ================= UI =================

  return (
    <div>
      <h2>Balance Sheet</h2>
      <div>

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

        <button
          style={{ marginLeft: "10px" }}
          onClick={loadBalanceSheet}
        >
          Load
        </button>

      </div>

      <br />

      <table border="1" cellPadding="10">

        <thead>
          <tr>
            <th>Category</th>
            <th>Amount</th>
          </tr>
        </thead>

        <tbody>

          <tr>
            <td><b>Assets</b></td>
            <td>{Number(data.totalAssets).toFixed(2)}</td>
          </tr>

          <tr>
            <td><b>Liabilities</b></td>
            <td>{Number(data.totalLiabilities).toFixed(2)}</td>
          </tr>

          <tr>
            <td><b>Net Profit</b></td>

            <td
              style={{
                color: data.netProfit >= 0 ? "green" : "red"
              }}
            >
              {Number(data.netProfit).toFixed(2)}
            </td>
          </tr>

          <tr>
            <td><b>Status</b></td>

            <td
              style={{
                color: data.balanced ? "green" : "red",
                fontWeight: "bold"
              }}
            >
              {data.balanced
                ? "Balanced ✅"
                : "Not Balanced ❌"}
            </td>
          </tr>

        </tbody>

      </table>

    </div>
  );
}

export default BalanceSheet;