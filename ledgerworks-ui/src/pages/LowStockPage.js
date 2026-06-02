import React, {
    useEffect,
    useState
} from "react";

import axios from "axios";

export default function LowStockPage() {

    const [stocks, setStocks] =
        useState([]);

    const [loading, setLoading] =
        useState(false);

    // ================= LOAD =================

    useEffect(() => {

        loadLowStock();

    }, []);

    // ================= API =================

    const loadLowStock = async () => {

        try {

            setLoading(true);

            const res =
                await axios.get(
                    "http://localhost:8080/api/stocks/low-stock"
                );

            setStocks(res.data || []);

        } catch (err) {

            console.error(err);

            alert(
                "Unable to load low stock items"
            );

        } finally {

            setLoading(false);
        }
    };

    // ================= UI =================

    return (

        <div style={{
            padding: "20px",
            overflowX: "auto"
        }}>

            <h2>
                ⚠ Low Stock Alert
            </h2>

            {
                loading &&
                (
                    <p>
                        Loading low stock items...
                    </p>
                )
            }

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
                            background: "#ffe5e5"
                        }}
                    >

                        <th>ID</th>

                        <th>Item</th>

                        <th>HSN</th>

                        <th>Available Qty</th>

                        <th>Unit</th>

                        <th>Status</th>

                    </tr>

                </thead>

                <tbody>

                    {
                        stocks.map((s) => (

                            <tr
                                key={s.id}
                                style={{
                                    background: "#fff5f5"
                                }}
                            >

                                <td>
                                    {s.id}
                                </td>

                                <td>
                                    {s.itemName}
                                </td>

                                <td>
                                    {s.hsnCode}
                                </td>

                                <td
                                    style={{
                                        color: "red",
                                        fontWeight: "bold"
                                    }}
                                >

                                    {s.availableQty}

                                </td>

                                <td>
                                    {s.unit}
                                </td>

                                <td>

                                    <span
                                        style={{
                                            color: "red",
                                            fontWeight: "bold"
                                        }}
                                    >

                                        LOW STOCK

                                    </span>

                                </td>

                            </tr>
                        ))
                    }

                    {
                        stocks.length === 0
                        &&
                        (
                            <tr>

                                <td
                                    colSpan="6"
                                    align="center"
                                >

                                    No Low Stock Items

                                </td>

                            </tr>
                        )
                    }

                </tbody>

            </table>

        </div>
    );
}