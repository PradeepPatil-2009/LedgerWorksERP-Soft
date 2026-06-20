import React, { useEffect, useState } from "react";
import {
    BarChart,
    Bar,
    XAxis,
    YAxis,
    Tooltip,
    CartesianGrid,
    PieChart,
    Pie,
    Cell,
    Legend,
    ResponsiveContainer,
} from "recharts";

import API from "../api/api";

function Dashboard() {

    const [data, setData] = useState({
        totalSales: 0,
        totalOutstanding: 0,
        overdueAmount: 0,
        overdueCount: 0
    });

    useEffect(() => {
        loadDashboard();
    }, []);

    const loadDashboard = async () => {

        try {

            const res = await API.get("/dashboard");

            setData(res.data || {});

        } catch (err) {

            console.error(
                "Dashboard error:",
                err
            );
        }
    };

    const totalSales = Number(data.totalSales) || 0;
    const totalOutstanding = Number(data.totalOutstanding) || 0;
    const overdueAmount = Number(data.overdueAmount) || 0;

    // Has any monetary figure to chart?
    const hasData =
        totalSales > 0 || totalOutstanding > 0 || overdueAmount > 0;

    const barData = [
        { name: "Total Sales", value: totalSales },
        { name: "Outstanding", value: totalOutstanding },
        { name: "Overdue", value: overdueAmount },
    ];

    const pieData = [
        { name: "Outstanding", value: totalOutstanding },
        { name: "Overdue", value: overdueAmount },
    ];

    const BAR_COLORS = ["#1976d2", "#ff9800", "#d32f2f"];
    const PIE_COLORS = ["#ff9800", "#d32f2f"];

    return (

        <div>

            <h2 style={{ marginTop: 0 }}>
                Dashboard
            </h2>

            {/* ================= METRIC CARDS ================= */}

            <div
                style={{
                    display: "grid",
                    gridTemplateColumns:
                        "repeat(auto-fit, minmax(220px, 1fr))",
                    gap: "16px",
                    marginBottom: "24px"
                }}
            >

                <Card
                    title="Total Sales"
                    value={totalSales}
                    color="#1976d2"
                />

                <Card
                    title="Total Outstanding"
                    value={totalOutstanding}
                    color="#ff9800"
                />

                <Card
                    title="Overdue Amount"
                    value={overdueAmount}
                    color="#d32f2f"
                />

                <Card
                    title="Overdue Count"
                    value={Number(data.overdueCount) || 0}
                    color="#388e3c"
                    currency={false}
                />

            </div>

            {/* ================= CHARTS ================= */}

            {hasData ? (

                <div
                    style={{
                        display: "grid",
                        gridTemplateColumns:
                            "repeat(auto-fit, minmax(320px, 1fr))",
                        gap: "16px"
                    }}
                >

                    <ChartCard title="Sales vs Outstanding vs Overdue">

                        <ResponsiveContainer width="100%" height={280}>

                            <BarChart data={barData}>

                                <CartesianGrid strokeDasharray="3 3" />
                                <XAxis dataKey="name" />
                                <YAxis />
                                <Tooltip
                                    formatter={(v) => `₹ ${v}`}
                                />
                                <Bar dataKey="value">
                                    {barData.map((entry, index) => (
                                        <Cell
                                            key={`bar-${index}`}
                                            fill={BAR_COLORS[index % BAR_COLORS.length]}
                                        />
                                    ))}
                                </Bar>

                            </BarChart>

                        </ResponsiveContainer>

                    </ChartCard>

                    <ChartCard title="Outstanding breakdown">

                        <ResponsiveContainer width="100%" height={280}>

                            <PieChart>

                                <Pie
                                    data={pieData}
                                    dataKey="value"
                                    nameKey="name"
                                    cx="50%"
                                    cy="50%"
                                    outerRadius={90}
                                    label
                                >
                                    {pieData.map((entry, index) => (
                                        <Cell
                                            key={`pie-${index}`}
                                            fill={PIE_COLORS[index % PIE_COLORS.length]}
                                        />
                                    ))}
                                </Pie>
                                <Tooltip
                                    formatter={(v) => `₹ ${v}`}
                                />
                                <Legend />

                            </PieChart>

                        </ResponsiveContainer>

                    </ChartCard>

                </div>

            ) : (

                <div
                    style={{
                        border: "1px dashed var(--lw-border)",
                        borderRadius: "var(--lw-radius)",
                        padding: "32px",
                        textAlign: "center",
                        color: "var(--lw-muted)"
                    }}
                >
                    No data yet — charts will appear once you have sales and
                    outstanding figures.
                </div>

            )}

        </div>
    );
}

function Card({ title, value, color, currency = true }) {

    return (

        <div
            style={{
                background: "var(--lw-surface)",
                padding: "22px",
                borderRadius: "var(--lw-radius)",
                boxShadow: "var(--lw-shadow)",
                border: "1px solid var(--lw-border)",
                borderTop: `4px solid ${color}`
            }}
        >

            <h3 style={{ margin: "0 0 8px", fontSize: "14px", color: "var(--lw-muted)" }}>
                {title}
            </h3>

            <div
                style={{
                    color: color,
                    fontSize: "28px",
                    fontWeight: 700
                }}
            >
                {currency ? `₹ ${value}` : value}
            </div>

        </div>
    );
}

function ChartCard({ title, children }) {

    return (

        <div
            style={{
                background: "var(--lw-surface)",
                border: "1px solid var(--lw-border)",
                borderRadius: "var(--lw-radius)",
                boxShadow: "var(--lw-shadow)",
                padding: "18px"
            }}
        >

            <h3 style={{ margin: "0 0 12px", fontSize: "15px" }}>
                {title}
            </h3>

            {children}

        </div>
    );
}

export default Dashboard;
