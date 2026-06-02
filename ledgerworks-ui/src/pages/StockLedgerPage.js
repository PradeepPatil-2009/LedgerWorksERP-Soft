import React, {
    useEffect,
    useState
} from "react";

import axios from "axios";

export default function StockLedgerPage() {

    const [ledger, setLedger] =
        useState([]);

    const [search, setSearch] =
        useState("");

    const [loading, setLoading] =
        useState(false);

    // =====================================================
    // LOAD LEDGER
    // =====================================================

    useEffect(() => {

        loadLedger();

    }, []);

    // =====================================================
    // API
    // =====================================================

    const loadLedger = async () => {

        try {

            setLoading(true);

            const res =
                await axios.get(
                    "http://localhost:8080/api/stock-ledger"
                );

            setLedger(res.data || []);

        } catch (err) {

            console.error(err);

            alert(
                "Unable to load stock ledger"
            );

        } finally {

            setLoading(false);
        }
    };

    // =====================================================
    // FILTER
    // =====================================================

    const filteredLedger =
        ledger.filter((l) => {

            const text =
                (
                    (l.itemName || "") +
                    " " +
                    (l.referenceNo || "") +
                    " " +
                    (l.transactionType || "")
                ).toLowerCase();

            return text.includes(
                search.toLowerCase()
            );
        });

    // =====================================================
    // TOTALS
    // =====================================================

    const totalIn = filteredLedger
        .reduce(
            (sum, l) =>
                sum + Number(l.qtyIn || 0),
            0
        )
        .toFixed(2);

    const totalOut = filteredLedger
        .reduce(
            (sum, l) =>
                sum + Number(l.qtyOut || 0),
            0
        )
        .toFixed(2);

    // =====================================================
    // UI
    // =====================================================

    return (

        <div style={{ padding: "20px" }}>

            <h2>
                Stock Ledger Report
            </h2>

            {/* SEARCH */}

            <input
                type="text"
                placeholder="Search Item / Ref No / Type"
                value={search}
                onChange={(e) =>
                    setSearch(e.target.value)
                }
                style={{
                    width: "350px",
                    padding: "8px",
                    marginBottom: "20px"
                }}
            />

            {/* LOADING */}

            {
                loading && (
                    <p>
                        Loading stock ledger...
                    </p>
                )
            }

            {/* TABLE */}

            <table
                border="1"
                width="100%"
                cellPadding="6"
                style={{
                    borderCollapse: "collapse"
                }}
            >

                <thead>

                    <tr
                        style={{
                            background: "#f2f2f2"
                        }}
                    >

                        <th>ID</th>
                        <th>Date</th>
                        <th>Item</th>
                        <th>Type</th>
                        <th>Reference</th>
                        <th>Qty IN</th>
                        <th>Qty OUT</th>
                        <th>Balance</th>
                        <th>Rate</th>
                        <th>Remarks</th>

                    </tr>

                </thead>

                <tbody>

                    {
                        filteredLedger.length > 0 ? (

                            filteredLedger.map((l) => {

                                const stockOut =
                                    Number(l.qtyOut || 0) > 0;

                                return (

                                    <tr
                                        key={l.id}
                                        style={{
                                            background:
                                                stockOut
                                                    ? "#fff5f5"
                                                    : "#f5fff5"
                                        }}
                                    >

                                        <td>{l.id}</td>

                                        <td>
                                            {l.entryDate}
                                        </td>

                                        <td>
                                            {l.itemName}
                                        </td>

                                        <td
                                            style={{
                                                color:
                                                    stockOut
                                                        ? "red"
                                                        : "green",

                                                fontWeight:
                                                    "bold"
                                            }}
                                        >
                                            {
                                                l.transactionType
                                            }
                                        </td>

                                        <td>
                                            {l.referenceNo}
                                        </td>

                                        <td
                                            style={{
                                                color: "green"
                                            }}
                                        >
                                            {l.qtyIn}
                                        </td>

                                        <td
                                            style={{
                                                color: "red"
                                            }}
                                        >
                                            {l.qtyOut}
                                        </td>

                                        <td>
                                            {
                                                l.balanceQty
                                            }
                                        </td>

                                        <td>
                                            ₹ {l.rate}
                                        </td>

                                        <td>
                                            {l.remarks}
                                        </td>

                                    </tr>
                                );
                            })

                        ) : (

                            <tr>

                                <td
                                    colSpan="10"
                                    style={{
                                        textAlign: "center"
                                    }}
                                >
                                    No Ledger Found
                                </td>

                            </tr>
                        )
                    }

                </tbody>

            </table>

            {/* TOTALS */}

            <div style={{ marginTop: "20px" }}>

                <h3>
                    Total IN :
                    <span style={{ color: "green" }}>
                        ₹ {totalIn}
                    </span>
                </h3>

                <h3>
                    Total OUT :
                    <span style={{ color: "red" }}>
                        ₹ {totalOut}
                    </span>
                </h3>

            </div>

        </div>
    );
}
