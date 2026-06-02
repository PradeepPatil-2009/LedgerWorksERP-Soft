import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

export default function PurchasePage() {

    const navigate = useNavigate();

    // ================= STATES =================

    const [purchases, setPurchases] =
        useState([]);

    const [search, setSearch] =
        useState("");

    const [statusFilter, setStatusFilter] =
        useState("ALL");

    const [currentPage, setCurrentPage] =
        useState(1);

    const itemsPerPage = 10;

    // ================= LOAD =================

    useEffect(() => {

        loadPurchases();

    }, []);

        const loadPurchases = async () => {

    try {

        const res = await axios.get(
            "http://localhost:8080/api/purchases"
        );

        console.log("API RESPONSE =", res.data);

        let purchaseData = [];

        // ARRAY RESPONSE
        if (Array.isArray(res.data)) {

            purchaseData = res.data;
        }

        // PAGE RESPONSE
        else if (
            res.data &&
            Array.isArray(res.data.content)
        ) {

            purchaseData = res.data.content;
        }

        // CUSTOM WRAPPER
        else if (
            res.data &&
            Array.isArray(res.data.data)
        ) {

            purchaseData = res.data.data;
        }

        console.log(
            "FINAL PURCHASE DATA =",
            purchaseData
        );

        setPurchases(purchaseData);

    } catch (err) {

        console.error(err);

        setPurchases([]);
    }
};

    // ================= CANCEL =================

    const handleCancel = async (id) => {

        try {

            await axios.post(
                `http://localhost:8080/api/purchases/cancel/${id}`
            );

            alert("Purchase Cancelled");

            loadPurchases();

        } catch (err) {

            console.error(err);

            alert("Cancel failed");
        }
    };

    // ================= FILTER =================

    const filteredPurchases =
        purchases.filter((p) => {

            const searchText =
                search.toLowerCase();

            const purchaseNo =
                String(
                    p.purchaseNumber || ""
                ).toLowerCase();

            const vendor =
                String(
                    p.vendorName || ""
                ).toLowerCase();

            const purchaseId =
                String(
                    p.id || ""
                ).toLowerCase();

            const matchesSearch = (

                purchaseNo.includes(searchText)

                ||

                vendor.includes(searchText)

                ||

                purchaseId.includes(searchText)
            );

            const matchesStatus =

                statusFilter === "ALL"

                ||

                p.status === statusFilter;

            return (
                matchesSearch
                &&
                matchesStatus
            );
        });

    // ================= SORT =================

    const sortedPurchases =
        [...filteredPurchases]
            .sort((a, b) => b.id - a.id);

    // ================= PAGINATION =================

    const totalPages = Math.ceil(
        sortedPurchases.length
        / itemsPerPage
    );

    const paginatedPurchases =
        sortedPurchases.slice(

            (currentPage - 1)
            * itemsPerPage,

            currentPage
            * itemsPerPage
        );

    // ================= DEBUG =================

    

    // ================= UI =================

    return (

        <div style={{ padding: "20px" }}>

            <h2>
                Purchase Entries
            </h2>

            {/* ================= TOP BAR ================= */}

            <div
                style={{
                    display: "flex",
                    gap: "10px",
                    marginBottom: "15px",
                    alignItems: "center"
                }}
            >

                <input
                    type="text"
                    placeholder="Search Purchase / Vendor"
                    value={search}
                    onChange={(e) => {

                        setSearch(
                            e.target.value
                        );

                        setCurrentPage(1);
                    }}
                    style={{
                        width: "300px",
                        padding: "8px"
                    }}
                />

                <select
                    value={statusFilter}
                    onChange={(e) => {

                        setStatusFilter(
                            e.target.value
                        );

                        setCurrentPage(1);
                    }}
                    style={{
                        padding: "8px"
                    }}
                >

                    <option value="ALL">
                        All Status
                    </option>

                    <option value="ACTIVE">
                        ACTIVE
                    </option>

                    <option value="CANCELLED">
                        CANCELLED
                    </option>

                </select>

                <button
                    onClick={() =>
                        navigate("/purchase/new")
                    }
                >
                    + Create Purchase
                </button>

            </div>

            {/* ================= TABLE ================= */}

            <table
                border="1"
                width="100%"
                cellPadding="6"
                style={{
                    borderCollapse: "collapse"
                }}
            >

                <thead>

                    <tr>

                        <th>ID</th>

                        <th>Purchase No</th>

                        <th>Date</th>

                        <th>Vendor</th>

                        <th>Taxable</th>

                        <th>GST</th>

                        <th>Grand Total</th>

                        <th>Status</th>

                        <th>Actions</th>

                    </tr>

                </thead>

                <tbody
                    style={{
                        backgroundColor: "white"
                    }}
                >

                    {
                        Array.isArray(
                            paginatedPurchases
                        )
                        &&
                        paginatedPurchases.map(
                            (p, index) => (

                                <tr
                                    key={
                                        p.id || index
                                    }
                                    style={{
                                        border:
                                            "1px solid black",

                                        backgroundColor:
                                            "#f5f5f5"
                                    }}
                                >

                                    <td>
                                        {p.id || ""}
                                    </td>

                                    <td>
                                        {
                                            p.purchaseNumber
                                            || ""
                                        }
                                    </td>

                                    <td>
                                        {
                                            p.purchaseDate
                                            || ""
                                        }
                                    </td>

                                    <td>
                                        {
                                            p.vendorName
                                            || ""
                                        }
                                    </td>

                                    <td>
                                        {
                                            p.taxableAmount
                                            ||
                                            p.totalTaxable
                                            ||
                                            0
                                        }
                                    </td>

                                    <td>

                                        {

                                            Number(
                                                p.totalCGST || 0
                                            )

                                            +

                                            Number(
                                                p.totalSGST || 0
                                            )

                                            +

                                            Number(
                                                p.totalIGST || 0
                                            )
                                        }

                                    </td>

                                    <td>
                                        {
                                            p.grandTotal
                                            || 0
                                        }
                                    </td>

                                    <td>

                                        <span
                                            style={{

                                                color:

                                                    p.status === "ACTIVE"
                                                        ? "blue"
                                                        : "red",

                                                fontWeight:
                                                    "bold"
                                            }}
                                        >

                                            {
                                                p.status || ""
                                            }

                                        </span>

                                    </td>

                                    <td>

                                        <button
                                            onClick={() =>
                                                navigate(
                                                    `/purchase/edit/${p.id}`
                                                )
                                            }
                                        >
                                            Edit
                                        </button>

                                        {" "}

                                        <button
                                            onClick={() =>
                                                handleCancel(
                                                    p.id
                                                )
                                            }
                                        >
                                            Cancel
                                        </button>

                                    </td>

                                </tr>
                            ))
                    }

                    {
                        (
                            !paginatedPurchases
                            ||
                            paginatedPurchases.length === 0
                        )

                        &&

                        (
                            <tr>

                                <td
                                    colSpan="9"
                                    style={{
                                        textAlign:
                                            "center",

                                        padding:
                                            "20px"
                                    }}
                                >

                                    No Purchase Found

                                </td>

                            </tr>
                        )
                    }

                </tbody>

            </table>

            {/* ================= PAGINATION ================= */}

            <div
                style={{
                    marginTop: "20px",
                    display: "flex",
                    gap: "10px",
                    alignItems: "center"
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

                    Page {currentPage}
                    {" "}of{" "}
                    {totalPages || 1}

                </span>

                <button
                    disabled={
                        currentPage === totalPages
                        ||
                        totalPages === 0
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
    );
}