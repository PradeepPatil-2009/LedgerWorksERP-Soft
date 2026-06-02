import React, { useEffect, useState } from "react";

import {
    BarChart,
    Bar,
    XAxis,
    YAxis,
    Tooltip,
    CartesianGrid,
    ResponsiveContainer
} from "recharts";

function GstAnalyticsPage() {

    const [salesData, setSalesData] = useState([]);

    useEffect(() => {
        loadMonthlySales();
    }, []);

    const loadMonthlySales = async () => {

        try {

            const res = await fetch(
                "http://localhost:8080/api/gst/monthly-sales"
            );

            const data = await res.json();

            setSalesData(data);

        } catch (err) {

            console.error(
                "Analytics Error:",
                err
            );
        }
    };

    return (

        <div
            style={{
                padding: "20px"
            }}
        >

            <h2>
                GST Analytics Dashboard
            </h2>

            <div
                style={{
                    background: "#fff",
                    padding: "20px",
                    borderRadius: "10px",
                    boxShadow: "0 2px 10px rgba(0,0,0,0.1)",
                    marginTop: "20px"
                }}
            >

                <h3>
                    Monthly Sales
                </h3>

                <ResponsiveContainer
                    width="100%"
                    height={400}
                >

                    <BarChart data={salesData}>

                        <CartesianGrid strokeDasharray="3 3" />

                        <XAxis dataKey="month" />

                        <YAxis />

                        <Tooltip />

                        <Bar
                            dataKey="sales"
                            fill="#1976d2"
                        />

                    </BarChart>

                </ResponsiveContainer>

            </div>

        </div>
    );
}

export default GstAnalyticsPage;