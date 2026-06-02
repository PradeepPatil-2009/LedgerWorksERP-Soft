import React, { useEffect, useState } from "react";

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

            const res =
                await fetch(
                    "http://localhost:8080/api/dashboard"
                );

            const result =
                await res.json();

            setData(result);

        } catch (err) {

            console.error(
                "Dashboard error:",
                err
            );
        }
    };

    return (

        <div
            style={{
                padding: "20px",
                background: "#f4f6f9",
                minHeight: "100vh"
            }}
        >

            <h1
                style={{
                    marginBottom: "20px"
                }}
            >
                LedgerWorks ERP Dashboard
            </h1>

            <div
                style={{
                    display: "grid",
                    gridTemplateColumns:
                        "repeat(auto-fit, minmax(250px, 1fr))",
                    gap: "20px"
                }}
            >

                <Card
                    title="Total Sales"
                    value={data.totalSales}
                    color="#1976d2"
                />

                <Card
                    title="Total Outstanding"
                    value={data.totalOutstanding}
                    color="#ff9800"
                />

                <Card
                    title="Overdue Amount"
                    value={data.overdueAmount}
                    color="#d32f2f"
                />

                <Card
                    title="Overdue Count"
                    value={data.overdueCount}
                    color="#388e3c"
                />

            </div>

        </div>
    );
}

function Card({ title, value, color }) {

    return (

        <div
            style={{
                background: "#fff",
                padding: "25px",
                borderRadius: "10px",
                boxShadow:
                    "0 2px 10px rgba(0,0,0,0.1)",
                borderTop:
                    `5px solid ${color}`
            }}
        >

            <h3>{title}</h3>

            <h1
                style={{
                    color: color
                }}
            >
                ₹ {value}
            </h1>

        </div>
    );
}

export default Dashboard;