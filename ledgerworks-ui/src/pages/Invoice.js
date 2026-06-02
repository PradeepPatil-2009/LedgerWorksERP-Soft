import { useEffect, useState } from "react";
import axios from "axios";
import { getInvoices } from "../api/api";
import { useToast } from "../components/Toast";

function Invoice() {

  const toast = useToast();

  const [invoices, setInvoices] = useState([]);
  const [search, setSearch] = useState("");

  useEffect(() => {
    loadData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const loadData = async () => {

    const data = await getInvoices();

    if (data) {
      setInvoices(data);
    }
  };

  // DOWNLOAD PDF

  const getPdf = async (invoice) => {

    try {

      const response = await axios.get(
        `http://localhost:8080/api/invoices/${invoice.id}/pdf?copyType=ORIGINAL`,
        {
          responseType: "blob"
        }
      );

      // CREATE PDF FILE

      const file =
        new Blob(
          [response.data],
          {
            type: "application/pdf"
          }
        );

      // CREATE DOWNLOAD LINK

      const fileURL =
        window.URL.createObjectURL(file);

      const link =
        document.createElement("a");

      link.href = fileURL;

      // FINAL FILE NAME

      link.setAttribute(
        "download",
        `${invoice.invoiceNumber.replace(/\//g, "-")}.pdf`
      );

      document.body.appendChild(link);

      // AUTO DOWNLOAD

      link.click();

      // CLEANUP

      link.remove();

      window.URL.revokeObjectURL(fileURL);

    } catch (error) {

      console.error(error);

      toast.error("PDF download failed");
    }
  };

  // FILTER

  const filtered = invoices.filter(
    (inv) =>
      inv.customerName
        ?.toLowerCase()
        .includes(search.toLowerCase())
  );

  return (
    <div>

      <h2>Invoices</h2>

      {/* SEARCH */}

      <input
        placeholder="Search Customer"
        value={search}
        onChange={(e) =>
          setSearch(e.target.value)
        }
      />

      {/* REFRESH */}

      <button onClick={loadData}>
        Refresh
      </button>

      <br />
      <br />

      {/* TABLE */}

      <div className="table-scroll">

      <table border="1" width="100%">

        <thead>
          <tr>

            <th>ID</th>

            <th>Invoice No</th>

            <th>Customer</th>

            <th>Date</th>

            <th>Total</th>

            <th>Status</th>

            <th>PDF</th>

          </tr>
        </thead>

        <tbody>

          {filtered.map((i) => (

            <tr key={i.id}>

              <td>{i.id}</td>

              <td>{i.invoiceNumber}</td>

              <td>{i.customerName}</td>

              <td>{i.invoiceDate}</td>

              <td>₹{i.grandTotal}</td>

              <td>
                <span
                  style={{
                    color:
                      i.paymentStatus === "PAID"
                        ? "green"
                        : i.paymentStatus === "PARTIAL"
                          ? "orange"
                          : "red",
                    fontWeight: "bold"
                  }}
                >
                  {i.paymentStatus || "UNPAID"}
                </span>
              </td>

              <td>

                <button
                  onClick={() => getPdf(i)}
                >
                  Download PDF
                </button>

              </td>

            </tr>

          ))}

        </tbody>

      </table>

      </div>

    </div>
  );
}

export default Invoice;