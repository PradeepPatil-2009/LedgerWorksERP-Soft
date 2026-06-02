import { useState } from "react";

import {
  getCustomerAging
} from "../api/api";

function Aging() {

  const [data, setData] = useState([]);

  const load = async () => {

    const res =
      await getCustomerAging();

    setData(res || []);
  };

  return (

    <div style={{ padding: "20px" }}>

      <h2>
        Customer Aging Report
      </h2>

      <button onClick={load}>
        Load Aging
      </button>

      <br /><br />

      <table
        border="1"
        cellPadding="10"
        cellSpacing="0"
      >

        <thead
          style={{
            background: "#f0f0f0"
          }}
        >

          <tr>

            <th>Customer</th>

            <th>0-30 Days</th>

            <th>31-60 Days</th>

            <th>61-90 Days</th>

            <th>90+ Days</th>

            <th>Total</th>

          </tr>

        </thead>

        <tbody>

          {data.length > 0 ? (

            data.map((r, i) => (

              <tr key={i}>

                <td>
                  {r.customerName}
                </td>

                <td>
                  ₹ {
                    Number(
                      r.bucket0To30 || 0
                    ).toFixed(2)
                  }
                </td>

                <td>
                  ₹ {
                    Number(
                      r.bucket31To60 || 0
                    ).toFixed(2)
                  }
                </td>

                <td>
                  ₹ {
                    Number(
                      r.bucket61To90 || 0
                    ).toFixed(2)
                  }
                </td>

                <td>
                  ₹ {
                    Number(
                      r.bucket90Plus || 0
                    ).toFixed(2)
                  }
                </td>

                <td>
                  <b>
                    ₹ {
                      Number(
                        r.totalOutstanding || 0
                      ).toFixed(2)
                    }
                  </b>
                </td>

              </tr>

            ))

          ) : (

            <tr>

              <td colSpan="6">
                No Aging Data Found
              </td>

            </tr>

          )}

        </tbody>

      </table>

    </div>
  );
}

export default Aging;