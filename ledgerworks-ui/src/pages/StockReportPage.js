import React, {
    useEffect,
    useState
} from "react";

import axios from "axios";

export default function StockReportPage() {

    const [stocks, setStocks] =
        useState([]);

    const [search, setSearch] =
        useState("");

    // =====================================================
    // LOAD STOCKS
    // =====================================================

    useEffect(() => {

        loadStocks();

    }, []);

    // =====================================================
    // API
    // =====================================================

    const loadStocks = async () => {

        try {

            const res =
                await axios.get(
                    "http://localhost:8080/api/stocks"
                );

            setStocks(res.data || []);

        } catch (err) {

            console.error(err);

            alert(
                "Unable to load stock report"
            );
        }
    };

    // =====================================================
    // FILTER
    // =====================================================

    const filteredStocks =
        stocks.filter((s) => {

            const text =
                (
                    (s.itemName || "") +
                    " " +
                    (s.hsnCode || "")
                ).toLowerCase();

            return text.includes(
                search.toLowerCase()
            );
        });

    // =====================================================
    // TOTAL STOCK
    // =====================================================

    const totalStock = filteredStocks
        .reduce(
            (sum, s) =>
                sum + Number(s.availableQty || 0),
            0
        )
        .toFixed(2);

    // =====================================================
    // UI
    // =====================================================

    return (

        <div style={{ padding: "20px" }}>

            <h2>
                Stock Report
            </h2>

            {/* SEARCH */}

            <input
                type="text"
                placeholder="Search Item / HSN"
                value={search}
                onChange={(e) =>
                    setSearch(e.target.value)
                }
                style={{
                    width: "300px",
                    padding: "8px",
                    marginBottom: "20px"
                }}
            />

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
                        <th>Item</th>
                        <th>HSN</th>
                        <th>Unit</th>
                        <th>Purchase Qty</th>
                        <th>Sales Qty</th>
                        <th>Available Qty</th>
                        <th>Last Purchase Rate</th>
                        <th>Last Sales Rate</th>
                        <th>Status</th>

                    </tr>

                </thead>

                <tbody>

                    {
                        filteredStocks.length > 0 ? (

                            filteredStocks.map((s) => {

                                const lowStock =
                                    Number(
                                        s.availableQty || 0
                                    ) < 10;

                                return (

                                    <tr
                                        key={s.id}
                                        style={{
                                            background:
                                                lowStock
                                                    ? "#ffe5e5"
                                                    : "white"
                                        }}
                                    >

                                        <td>{s.id}</td>

                                        <td>
                                            {s.itemName}
                                        </td>

                                        <td>
                                            {s.hsnCode}
                                        </td>

                                        <td>
                                            {s.unit}
                                        </td>

                                        <td>
                                            {s.purchaseQty}
                                        </td>

                                        <td>
                                            {s.salesQty}
                                        </td>

                                        <td
                                            style={{
                                                fontWeight:
                                                    "bold",

                                                color:
                                                    lowStock
                                                        ? "red"
                                                        : "green"
                                            }}
                                        >
                                            {
                                                s.availableQty
                                            }
                                        </td>

                                        <td>
                                            ₹ {
                                                s.lastPurchaseRate
                                            }
                                        </td>

                                        <td>
                                            ₹ {
                                                s.lastSalesRate
                                            }
                                        </td>

                                        <td>

                                            <span
                                                style={{
                                                    color:
                                                        s.status === "ACTIVE"
                                                            ? "green"
                                                            : "red",

                                                    fontWeight:
                                                        "bold"
                                                }}
                                            >
                                                {s.status}
                                            </span>

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
                                    No Stock Found
                                </td>

                            </tr>
                        )
                    }

                </tbody>

            </table>

            {/* TOTAL */}

            <h3 style={{ marginTop: "20px" }}>
                Total Available Stock :
                <span style={{ color: "green" }}>
                    {" "} {totalStock}
                </span>
            </h3>

        </div>
    );
}
