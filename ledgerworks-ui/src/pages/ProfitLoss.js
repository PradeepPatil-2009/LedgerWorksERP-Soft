import { useEffect, useState } from "react";
import API from "../api/api";

function ProfitLoss() {

  const [fromDate, setFromDate] = useState("");

  const [toDate, setToDate] = useState("");

  const [data, setData] = useState({
    totalIncome: 0,
    totalExpense: 0,
    netProfit: 0,
  });

  // ================= DATE FORMAT =================

  const formatDate = (date) => {

    if (!date) return "";

    return new Date(date)
      .toISOString()
      .split("T")[0];
  };

  // ================= LOAD REPORT =================

  const loadProfitLoss = async () => {

    try {

      let url = "/reports/profit-loss";

      // ================= FILTER =================

      if (fromDate && toDate) {

        const from = formatDate(fromDate);

        const to = formatDate(toDate);

        url += `?from=${from}&to=${to}`;
      }

      const res = await API.get(url);

      console.log("ProfitLoss Data:", res.data);

      setData({
        totalIncome: res.data.totalIncome || 0,
        totalExpense: res.data.totalExpense || 0,
        netProfit: res.data.netProfit || 0,
      });

    } catch (err) {

      console.error("Error loading Profit & Loss:", err);

      alert("Failed to load Profit & Loss");
    }
  };

  // ================= AUTO LOAD =================
// eslint-disable-next-line react-hooks/exhaustive-deps
  useEffect(() => {
    const load = async () => {
      await loadProfitLoss();
    };

    load();

  }, []); 

  // ================= UI =================

  return (

    <div>

      <h2>Profit & Loss</h2>

      {/* ================= FILTER ================= */}

      <div>

        <label>From: </label>

        <input
          type="date"
          value={fromDate}
          onChange={(e) => setFromDate(e.target.value)}
        />

        <label style={{ marginLeft: "10px" }}>
          To:
        </label>

        <input
          type="date"
          value={toDate}
          onChange={(e) => setToDate(e.target.value)}
        />

        <button
          style={{ marginLeft: "10px" }}
          onClick={loadProfitLoss}
        >
          Load
        </button>

      </div>

      <br />

      {/* ================= REPORT TABLE ================= */}

      <table border="1" cellPadding="10">

        <thead>

          <tr>
            <th>Type</th>
            <th>Amount</th>
          </tr>

        </thead>

        <tbody>

          <tr>

            <td>Income</td>

            <td align="right">
              ₹ {Number(data.totalIncome).toFixed(2)}
            </td>

          </tr>

          <tr>

            <td>Expense</td>

            <td align="right">
              ₹ {Number(data.totalExpense).toFixed(2)}
            </td>

          </tr>

          <tr>

            <td>
              <b>Net Profit</b>
            </td>

            <td align="right">

              <b
                style={{
                  color:
                    data.netProfit >= 0
                      ? "green"
                      : "red"
                }}
              >
                ₹ {Number(data.netProfit).toFixed(2)}
              </b>

            </td>

          </tr>

        </tbody>

      </table>

    </div>
  );
}

export default ProfitLoss;