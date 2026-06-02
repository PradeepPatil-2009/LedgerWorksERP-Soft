import { useEffect, useState } from "react";

import {
  getCashFlow
} from "../api/api";

function CashFlow() {

  // ================= STATES =================

  const [fromDate, setFromDate] =
    useState("");

  const [toDate, setToDate] =
    useState("");

  const [data, setData] = useState({
    rows: [],
    totalInflow: 0,
    totalOutflow: 0,
    netCashFlow: 0,
  });

  const [loading, setLoading] =
    useState(false);

  // ================= DATE FORMAT =================

  const formatDate = (date) => {

    if (!date) return "";

    return new Date(date)
      .toISOString()
      .split("T")[0];
  };

  // ================= LOAD =================

  const load = async () => {

    setLoading(true);

    try {

      const res = await getCashFlow(
        fromDate,
        toDate
      );

      console.log(
        "Cash Flow:",
        res
      );

      setData({
        rows: res?.rows || [],
        totalInflow:
          res?.totalInflow || 0,
        totalOutflow:
          res?.totalOutflow || 0,
        netCashFlow:
          res?.netCashFlow || 0,
      });

    } catch (err) {

      console.error(err);

      alert(
        "Error loading cash flow"
      );

    } finally {

      setLoading(false);
    }
  };

  // ================= DEFAULT DATE =================

  useEffect(() => {

    const today = new Date();

    const firstDay = new Date(
      today.getFullYear(),
      0,
      1
    );

    setFromDate(
      formatDate(firstDay)
    );

    setToDate(
      formatDate(today)
    );

  }, []);

  // ================= AUTO LOAD =================

  // eslint-disable-next-line react-hooks/exhaustive-deps
  useEffect(() => {

    if (fromDate && toDate) {
      load();
    }

  }, [fromDate, toDate]);

  // ================= UI =================

  return (
    <div>

      <h2>Cash Flow</h2>

      <div>

        <label>From: </label>

        <input
          type="date"
          value={fromDate}
          onChange={(e) =>
            setFromDate(
              e.target.value
            )
          }
        />

        <label
          style={{
            marginLeft: "10px"
          }}
        >
          To:
        </label>

        <input
          type="date"
          value={toDate}
          onChange={(e) =>
            setToDate(
              e.target.value
            )
          }
        />

        <button
          style={{
            marginLeft: "10px"
          }}
          onClick={load}
        >
          Load
        </button>

      </div>

      <br />

      {loading ? (

        <p>Loading...</p>

      ) : (

        <table
          border="1"
          cellPadding="10"
        >

          <thead>

            <tr>
              <th>Category</th>
              <th>Inflow</th>
              <th>Outflow</th>
            </tr>

          </thead>

          <tbody>

            {data.rows.length > 0 ? (

              <>
                {data.rows.map(
                  (r, i) => (

                    <tr key={i}>
                      <td>
                        {r.category}
                      </td>

                      <td>
                        {Number(
                          r.inflow
                        ).toFixed(2)}
                      </td>

                      <td>
                        {Number(
                          r.outflow
                        ).toFixed(2)}
                      </td>
                    </tr>
                  )
                )}

                <tr>
                  <td>
                    <b>
                      Total Inflow
                    </b>
                  </td>

                  <td>
                    <b>
                      {Number(
                        data.totalInflow
                      ).toFixed(2)}
                    </b>
                  </td>

                  <td>-</td>
                </tr>

                <tr>
                  <td>
                    <b>
                      Total Outflow
                    </b>
                  </td>

                  <td>-</td>

                  <td>
                    <b>
                      {Number(
                        data.totalOutflow
                      ).toFixed(2)}
                    </b>
                  </td>
                </tr>

                <tr>

                  <td>
                    <b>
                      Net Cash Flow
                    </b>
                  </td>

                  <td
                    colSpan="2"
                    style={{
                      color:
                        data.netCashFlow >= 0
                          ? "green"
                          : "red",

                      fontWeight: "bold"
                    }}
                  >
                    {Number(
                      data.netCashFlow
                    ).toFixed(2)}
                  </td>

                </tr>
              </>

            ) : (

              <tr>
                <td colSpan="3">
                  No Data Found
                </td>
              </tr>

            )}

          </tbody>

        </table>
      )}

    </div>
  );
}

export default CashFlow;