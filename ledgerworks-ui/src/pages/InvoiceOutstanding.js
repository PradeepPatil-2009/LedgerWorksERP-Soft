import { useEffect, useState } from "react";
import { getInvoiceOutstanding } from "../api/api";

function InvoiceOutstanding() {

  const [data, setData] = useState([]);

  const [loading, setLoading] = useState(false);

  const [search, setSearch] = useState("");

  useEffect(() => {
    loadData();
  }, []);

  // ================= LOAD DATA =================

  const loadData = async () => {

    setLoading(true);

    try {

      const res = await getInvoiceOutstanding();

      console.log("Outstanding Response:", res);

      setData(Array.isArray(res) ? res : []);

    } catch (err) {

      console.error(err);

      alert("Failed to load Invoice Outstanding");

    } finally {

      setLoading(false);
    }
  };

  // ================= SEARCH =================

  const filtered = data.filter((item) => {

    const keyword = search.toLowerCase();

    const invoiceNo =
      item?.invoiceNumber
        ? item.invoiceNumber.toLowerCase()
        : "";

    const customer =
      item?.customerName
        ? item.customerName.toLowerCase()
        : "";

    return (
      invoiceNo.includes(keyword)
      ||
      customer.includes(keyword)
    );
  });

  return (
    <div>

      <h2>Invoice Outstanding</h2>

      {/* SEARCH */}

      <input
        placeholder="Search invoice/customer"
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />

      <button>
        Search
      </button>

      <button onClick={() => setSearch("")}>
        Clear
      </button>

      <hr />

      {loading ? (

        <p>Loading...</p>

      ) : filtered.length > 0 ? (

        <table border="1" cellPadding="5">

          <thead>

            <tr>
              <th>ID</th>
              <th>Invoice No</th>
              <th>Customer</th>
              <th>Total</th>
              <th>Paid</th>
              <th>Outstanding</th>
            </tr>

          </thead>

          <tbody>

            {filtered.map((item, index) => (

              <tr key={item.id || index}>

                <td>{item.id}</td>

                <td>{item.invoiceNumber}</td>

                <td>{item.customerName || "-"}</td>

                <td>
                  {Number(item.totalAmount || 0).toFixed(2)}
                </td>

                <td>
                  {Number(item.paidAmount || 0).toFixed(2)}
                </td>

                <td>
                  {Number(item.outstandingAmount || 0).toFixed(2)}
                </td>

              </tr>
            ))}

          </tbody>

        </table>

      ) : (

        <p>No Data Found</p>

      )}

    </div>
  );
}

export default InvoiceOutstanding;