import { useState } from "react";
import {
  getOutstanding
} from "../api/api";

function Outstanding() {

  const [data, setData] = useState([]);

  const [search, setSearch] = useState("");

  const load = async () => {

  try {

    const res =
      await getOutstanding();

    console.log(res);

    setData(

      Array.isArray(res)
        ? res
        : []

    );

  } catch (err) {

    console.error(err);

    alert(
      "Failed to load outstanding report"
    );
  }
};

  // ================= FILTER =================

  const filteredData = data.filter((r) =>
    r.customerName
      ?.toLowerCase()
      .includes(search.toLowerCase())
  );

  // ================= GRAND TOTAL =================

  const grandTotal = filteredData
    .reduce(
      (sum, r) =>
        sum + (r.outstandingAmount || 0),
      0
    )
    .toFixed(2);

  return (

    <div style={{ padding: "20px" }}>

      <h2
        style={{
          marginBottom: "20px"
        }}
      >
        Outstanding Report
      </h2>

      {/* ================= ACTIONS ================= */}

      <div
        style={{
          marginBottom: "20px",
          display: "flex",
          gap: "10px",
          alignItems: "center"
        }}
      >

        <button
          onClick={load}
          style={{
            padding: "8px 15px",
            cursor: "pointer",
            fontWeight: "bold"
          }}
        >
          Load Report
        </button>

        <input
          type="text"
          placeholder="Search Customer..."
          value={search}
          onChange={(e) =>
            setSearch(e.target.value)
          }
          style={{
            padding: "8px",
            width: "250px"
          }}
        />

      </div>

      {/* ================= TABLE ================= */}

      <table
        border="1"
        cellPadding="12"
        cellSpacing="0"
        width="900"
        style={{
          borderCollapse: "collapse"
        }}
      >

        <thead
          style={{
            background: "#f0f0f0"
          }}
        >

          <tr>

            <th width="80">
              Sr No
            </th>

            <th>
              Customer
            </th>

            <th>
              Outstanding Amount
            </th>

          </tr>

        </thead>

        <tbody>

          {filteredData.length > 0 ? (

            filteredData.map((r, i) => (

              <tr
                key={i}
                style={{
                  backgroundColor:
                    i % 2 === 0
                      ? "#ffffff"
                      : "#f9f9f9"
                }}
              >

                <td
                  style={{
                    textAlign: "center"
                  }}
                >
                  {i + 1}
                </td>

                <td>
                  {r.customerName}
                </td>

                <td
                  style={{
                    textAlign: "right",
                    fontWeight: "bold"
                  }}
                >
                  ₹ {
                    Number(
                      r.outstandingAmount || 0
                    ).toFixed(2)
                  }
                </td>

              </tr>

            ))

          ) : (

            <tr>

              <td
                colSpan="3"
                style={{
                  textAlign: "center",
                  padding: "20px"
                }}
              >
                No Outstanding Found
              </td>

            </tr>

          )}

        </tbody>

      </table>

      {/* ================= GRAND TOTAL ================= */}

      <h3
        style={{
          marginTop: "20px"
        }}
      >
        Grand Total :
        ₹ {grandTotal}
      </h3>

    </div>
  );
}

export default Outstanding;