import {
    useEffect,
    useState,
    useCallback,
    useMemo
} from "react";

import API from "../api/api";

export default function GstReportPage() {

    // ================= STATE =================

    const [fromDate, setFromDate] =
        useState("2026-05-01");

    const [toDate, setToDate] =
        useState("2026-05-31");

    const [summary, setSummary] =
        useState(null);

    const [details, setDetails] =
        useState([]);

    const [search, setSearch] =
        useState("");

    const [loading, setLoading] =
        useState(false);

    const [currentPage, setCurrentPage] =
        useState(1);

    const recordsPerPage = 10;

    // ================= LOAD REPORT =================

    const loadReport = useCallback(async () => {

        try {

            setLoading(true);

            // ================= SUMMARY =================

            const summaryRes =
                await API.get(
                    "/gst/summary",
                    {
                        params: {
                            fromDate,
                            toDate
                        }
                    }
                );

            setSummary(summaryRes.data);

            // ================= DETAILS =================

            const detailRes =
                await API.get(
                    "/gst/details",
                    {
                        params: {
                            fromDate,
                            toDate
                        }
                    }
                );

            setDetails(detailRes.data);

        } catch (err) {

            console.error(
                "GST Report Error",
                err
            );

        } finally {

            setLoading(false);
        }

    }, [fromDate, toDate]);

    // ================= INITIAL LOAD =================

    useEffect(() => {

        loadReport();

    }, [loadReport]);

    // ================= SEARCH FILTER =================

    const filteredDetails = useMemo(() => {

        return [...details]

            .reverse()

            .filter((row) => {

                const invoice =
                    row.invoiceNumber
                        ?.toLowerCase()
                        .includes(
                            search.toLowerCase()
                        );

                const customer =
                    row.customerName
                        ?.toLowerCase()
                        .includes(
                            search.toLowerCase()
                        );

                const state =
                    row.customerState
                        ?.toLowerCase()
                        .includes(
                            search.toLowerCase()
                        );

                return (
                    invoice ||
                    customer ||
                    state
                );
            });

    }, [details, search]);

    // ================= PAGINATION =================

    const totalPages =
        Math.ceil(
            filteredDetails.length /
            recordsPerPage
        );

    const startIndex =
        (currentPage - 1)
        * recordsPerPage;

    const paginatedDetails =
        filteredDetails.slice(
            startIndex,
            startIndex +
            recordsPerPage
        );


    const exportExcel = () => {

        window.open(

            `http://localhost:8080/api/gst/export/excel?fromDate=${fromDate}&toDate=${toDate}`,

            "_blank"
        );
    };
    // ================= UI =================

    return (

        <div
            style={{
                padding: "20px",
                backgroundColor:
                    "#f5f5f5",
                minHeight: "100vh"
            }}
        >

            {/* ================= TITLE ================= */}

            <h2
                style={{
                    marginBottom: "20px"
                }}
            >
                GST Summary Report
            </h2>

            {/* ================= FILTER SECTION ================= */}

            <div
                style={{
                    display: "flex",
                    gap: "15px",
                    alignItems: "end",
                    flexWrap: "wrap",
                    backgroundColor: "#fff",
                    padding: "15px",
                    borderRadius: "8px",
                    marginBottom: "20px",
                    boxShadow:
                        "0 2px 5px rgba(0,0,0,0.1)"
                }}
            >

                <div>

                    <label>
                        From Date
                    </label>

                    <br />

                    <input
                        type="date"
                        value={fromDate}
                        onChange={(e) =>
                            setFromDate(
                                e.target.value
                            )
                        }
                    />

                </div>

                <div>

                    <label>
                        To Date
                    </label>

                    <br />

                    <input
                        type="date"
                        value={toDate}
                        onChange={(e) =>
                            setToDate(
                                e.target.value
                            )
                        }
                    />

                </div>

                <div>

                    <label>
                        Search
                    </label>

                    <br />

                    <input
                        type="text"
                        placeholder="Invoice / Customer / State"
                        value={search}
                        onChange={(e) => {

                            setSearch(
                                e.target.value
                            );

                            setCurrentPage(1);
                        }}
                        style={{
                            width: "250px"
                        }}
                    />

                </div>


                <div
                    style={{
                        display: "flex",
                        gap: "10px"
                    }}
                >
                    <button
                        onClick={loadReport}
                        style={{
                            height: "35px",
                            padding:
                                "0 20px",
                            backgroundColor:
                                "#1976d2",
                            color: "#fff",
                            border: "none",
                            cursor: "pointer",
                            borderRadius: "5px"
                        }}
                    >
                        Generate
                    </button>

                    <button onClick={exportExcel}>
                        Export Excel
                    </button>

                </div>

            </div>

            {/* ================= SUMMARY CARDS ================= */}

            {
                summary && (

                    <div
                        style={{
                            display: "grid",
                            gridTemplateColumns:
                                "repeat(auto-fit, minmax(180px, 1fr))",
                            gap: "15px",
                            marginBottom: "25px"
                        }}
                    >

                        <div style={cardStyle}>
                            <h4>
                                Taxable
                            </h4>
                            <p>
                                ₹ {summary.taxable}
                            </p>
                        </div>

                        <div style={cardStyle}>
                            <h4>
                                CGST
                            </h4>
                            <p>
                                ₹ {summary.cgst}
                            </p>
                        </div>

                        <div style={cardStyle}>
                            <h4>
                                SGST
                            </h4>
                            <p>
                                ₹ {summary.sgst}
                            </p>
                        </div>

                        <div style={cardStyle}>
                            <h4>
                                IGST
                            </h4>
                            <p>
                                ₹ {summary.igst}
                            </p>
                        </div>

                        <div style={cardStyle}>
                            <h4>
                                Grand Total
                            </h4>
                            <p>
                                ₹ {summary.grandTotal}
                            </p>
                        </div>

                    </div>
                )
            }

            {/* ================= DETAILS TABLE ================= */}

            <div
                style={{
                    backgroundColor: "#fff",
                    padding: "15px",
                    borderRadius: "8px",
                    boxShadow:
                        "0 2px 5px rgba(0,0,0,0.1)"
                }}
            >

                <div
                    style={{
                        display: "flex",
                        justifyContent:
                            "space-between",
                        marginBottom: "10px"
                    }}
                >

                    <h3>
                        GST Invoice Details
                    </h3>

                    <strong>
                        Total Records :
                        {" "}
                        {
                            filteredDetails.length
                        }
                    </strong>

                </div>

                {
                    loading ? (

                        <p>
                            Loading GST Report...
                        </p>

                    ) : filteredDetails.length === 0 ? (

                        <p>
                            No GST records found
                        </p>

                    ) : (

                        <div
                            style={{
                                overflowX:
                                    "auto"
                            }}
                        >

                            <table
                                style={{
                                    width: "100%",
                                    borderCollapse:
                                        "collapse"
                                }}
                            >

                                <thead
                                    style={{
                                        backgroundColor:
                                            "#1976d2",
                                        color: "#fff",
                                        position:
                                            "sticky",
                                        top: 0
                                    }}
                                >

                                    <tr>

                                        <th style={thStyle}>
                                            Invoice No
                                        </th>

                                        <th style={thStyle}>
                                            Date
                                        </th>

                                        <th style={thStyle}>
                                            Customer
                                        </th>

                                        <th style={thStyle}>
                                            State
                                        </th>

                                        <th style={thStyle}>
                                            Taxable
                                        </th>

                                        <th style={thStyle}>
                                            CGST
                                        </th>

                                        <th style={thStyle}>
                                            SGST
                                        </th>

                                        <th style={thStyle}>
                                            IGST
                                        </th>

                                        <th style={thStyle}>
                                            Total
                                        </th>

                                    </tr>

                                </thead>

                                <tbody>

                                    {
                                        paginatedDetails.map(
                                            (
                                                row,
                                                index
                                            ) => (

                                                <tr
                                                    key={index}
                                                    style={{
                                                        backgroundColor:
                                                            index % 2 === 0
                                                                ? "#f9f9f9"
                                                                : "#fff"
                                                    }}
                                                >

                                                    <td style={tdStyle}>
                                                        {
                                                            row.invoiceNumber
                                                        }
                                                    </td>

                                                    <td style={tdStyle}>
                                                        {
                                                            row.invoiceDate
                                                        }
                                                    </td>

                                                    <td style={tdStyle}>
                                                        {
                                                            row.customerName
                                                        }
                                                    </td>

                                                    <td style={tdStyle}>
                                                        {
                                                            row.customerState
                                                        }
                                                    </td>

                                                    <td style={tdStyle}>
                                                        {
                                                            row.taxable
                                                        }
                                                    </td>

                                                    <td style={tdStyle}>
                                                        {
                                                            row.cgst
                                                        }
                                                    </td>

                                                    <td style={tdStyle}>
                                                        {
                                                            row.sgst
                                                        }
                                                    </td>

                                                    <td style={tdStyle}>
                                                        {
                                                            row.igst
                                                        }
                                                    </td>

                                                    <td style={tdStyle}>
                                                        {
                                                            row.total
                                                        }
                                                    </td>

                                                </tr>
                                            )
                                        )
                                    }

                                </tbody>

                            </table>

                        </div>
                    )
                }

                {/* ================= PAGINATION ================= */}

                <div
                    style={{
                        marginTop: "20px",
                        display: "flex",
                        justifyContent:
                            "center",
                        alignItems: "center",
                        gap: "10px"
                    }}
                >

                    <button
                        disabled={
                            currentPage === 1
                        }
                        onClick={() =>
                            setCurrentPage(
                                currentPage - 1
                            )
                        }
                    >
                        Previous
                    </button>

                    <span>
                        Page
                        {" "}
                        {currentPage}
                        {" "}
                        of
                        {" "}
                        {totalPages || 1}
                    </span>

                    <button
                        disabled={
                            currentPage ===
                            totalPages
                        }
                        onClick={() =>
                            setCurrentPage(
                                currentPage + 1
                            )
                        }
                    >
                        Next
                    </button>

                </div>

            </div>

        </div>
    );
}

// ================= COMMON STYLES =================

const cardStyle = {

    backgroundColor: "#fff",

    padding: "15px",

    borderRadius: "8px",

    boxShadow:
        "0 2px 5px rgba(0,0,0,0.1)",

    textAlign: "center"
};

const thStyle = {

    padding: "10px",

    border: "1px solid #ccc"
};

const tdStyle = {

    padding: "8px",

    border: "1px solid #ddd"
};