import { useEffect, useState } from "react";
import axios from "axios";
import { getInvoices } from "../api/api";
import { useToast } from "../components/Toast";
import { useTableControls } from "../components/useTableControls";
import Pagination from "../components/Pagination";
import SortableTh from "../components/SortableTh";

function Invoice() {

  const toast = useToast();

  const [invoices, setInvoices] = useState([]);

  const tc = useTableControls(invoices, {
    searchKeys: [
      "invoiceNumber",
      "customerName",
      "invoiceDate",
      "paymentStatus",
    ],
    pageSize: 10,
  });

  const { query, setQuery, page, setPage, totalPages, pageItems, total } = tc;

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

  return (
    <div>

      <h2>Invoices</h2>

      {/* SEARCH */}

      <input
        placeholder="Search..."
        value={query}
        onChange={(e) =>
          setQuery(e.target.value)
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

            <SortableTh field="id" controls={tc}>ID</SortableTh>

            <SortableTh field="invoiceNumber" controls={tc}>Invoice No</SortableTh>

            <SortableTh field="customerName" controls={tc}>Customer</SortableTh>

            <SortableTh field="invoiceDate" controls={tc}>Date</SortableTh>

            <SortableTh field="grandTotal" controls={tc}>Total</SortableTh>

            <SortableTh field="paymentStatus" controls={tc}>Status</SortableTh>

            <th>PDF</th>

          </tr>
        </thead>

        <tbody>

          {pageItems.map((i) => (

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

      <Pagination
        page={page}
        totalPages={totalPages}
        total={total}
        onPrev={() => setPage(page - 1)}
        onNext={() => setPage(page + 1)}
      />

    </div>
  );
}

export default Invoice;