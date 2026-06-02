import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

export default function DeliveryChallanPage() {

    const navigate = useNavigate();

    // ================= STATES =================

    const [challans, setChallans] = useState([]);

    const [search, setSearch] = useState("");

    const [statusFilter, setStatusFilter] =
        useState("ALL");

    const [currentPage, setCurrentPage] =
        useState(1);

    const itemsPerPage = 10;

    // ================= LOAD =================

    useEffect(() => {

        loadChallans();

    }, []);

    const loadChallans = async () => {

        try {

            const res = await axios.get(
                "http://localhost:8080/api/delivery-challan"
            );

            setChallans(res.data || []);

        } catch (err) {

            console.error(err);
        }
    };

    // ================= FILTER =================

    const filteredChallans = challans.filter((c) => {

        const searchText =
            search.toLowerCase();

        const challanNo =
            String(c.challanNumber || "")
                .toLowerCase();

        const customer =
            String(c.customerName || "")
                .toLowerCase();

        const invoiceNo =
            String(
                c.invoice?.invoiceNumber || ""
            ).toLowerCase();

        const dcId =
            String(c.id || "")
                .toLowerCase();

        const matchesSearch = (

            challanNo.includes(searchText)

            ||

            customer.includes(searchText)

            ||

            invoiceNo.includes(searchText)

            ||

            dcId.includes(searchText)
        );

        const matchesStatus =

            statusFilter === "ALL"

            ||

            c.status === statusFilter;

        return (
            matchesSearch
            &&
            matchesStatus
        );

    });

    // ================= SORT =================

    const sortedChallans = [...filteredChallans]
        .sort((a, b) => b.id - a.id);

    // ================= PAGINATION =================

    const totalPages = Math.ceil(
        sortedChallans.length / itemsPerPage
    );

    const paginatedChallans =
        sortedChallans.slice(

            (currentPage - 1)
            * itemsPerPage,

            currentPage
            * itemsPerPage
        );

    // ================= DOWNLOAD DC PDF =================

    const handlePrint = async (challan) => {

        try {

            const response = await axios.get(
                `http://localhost:8080/api/delivery-challan/${challan.id}/pdf`,
                {
                    responseType: "blob"
                }
            );

            const blob = new Blob(
                [response.data],
                {
                    type: "application/pdf"
                }
            );

            const url =
                window.URL.createObjectURL(blob);

            const link =
                document.createElement("a");

            link.href = url;

            // FILE NAME

            link.download =
                `${challan.challanNumber.replaceAll("/", "-")}.pdf`;

            document.body.appendChild(link);

            link.click();

            document.body.removeChild(link);

            window.URL.revokeObjectURL(url);

        } catch (err) {

            console.error(err);

            alert("Unable to download DC PDF");
        }
    };

    // ================= DOWNLOAD INVOICE =================

    const handleViewInvoice = async (
        challan,
        type
    ) => {

        try {

            if (!challan.invoice?.id) {

                alert("Invoice not created yet");

                return;
            }

            const response = await axios.get(
                `http://localhost:8080/api/invoices/${challan.invoice.id}/pdf?copyType=${type}`,
                {
                    responseType: "blob"
                }
            );

            const blob = new Blob(
                [response.data],
                {
                    type: "application/pdf"
                }
            );

            const url =
                window.URL.createObjectURL(blob);

            const link =
                document.createElement("a");

            link.href = url;

            // FILE NAME

            link.download =
                `${challan.invoice.invoiceNumber.replaceAll("/", "-")}-${type}.pdf`;

            document.body.appendChild(link);

            link.click();

            document.body.removeChild(link);

            window.URL.revokeObjectURL(url);

        } catch (err) {

            console.error(err);

            alert("Unable to download invoice");
        }
    };

    // ================= DELIVER =================

    const handleDeliver = async (id) => {

        const confirmDeliver =
            window.confirm(
                "Mark this Delivery Challan as DELIVERED and create Invoice?"
            );

        if (!confirmDeliver) {

            return;
        }

        try {

            await axios.post(
                `http://localhost:8080/api/delivery-challan/deliver/${id}`
            );

            alert(
                "Delivered + Invoice Created"
            );

            loadChallans();

        } catch (err) {

            console.error(err);

            alert("Delivery failed");
        }
    };

    // ================= CANCEL =================

    const handleCancel = async (id) => {

        const confirmCancel =
            window.confirm(
                "Are you sure you want to cancel this Delivery Challan?"
            );

        if (!confirmCancel) {

            return;
        }

        try {

            await axios.post(
                `http://localhost:8080/api/delivery-challan/cancel/${id}`
            );

            alert(
                "Delivery Challan Cancelled"
            );

            loadChallans();

        } catch (err) {

            console.error(err);

            alert("Cancel failed");
        }
    };

    // ================= UI =================

    return (

        <div style={{ padding: "20px" }}>

            <h2>
                Delivery Challans
            </h2>

            {/* SEARCH + FILTER */}

            <div
                style={{
                    marginBottom: "15px",
                    display: "flex",
                    gap: "10px",
                    alignItems: "center"
                }}
            >

                <input
                    type="text"
                    placeholder="Search DC / Customer / Invoice"
                    value={search}
                    onChange={(e) => {

                        setSearch(
                            e.target.value
                        );

                        setCurrentPage(1);
                    }}
                    style={{
                        width: "350px",
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

                    <option value="DELIVERED">
                        DELIVERED
                    </option>

                    <option value="CANCELLED">
                        CANCELLED
                    </option>

                </select>

                <button
                    onClick={() =>
                        navigate("/delivery-challan/create")
                    }
                >
                    + Create Challan
                </button>

            </div>

            {/* TABLE */}

            <table
                border="1"
                width="100%"
                cellPadding="8"
            >

                <thead>

                    <tr>

                        <th>ID</th>

                        <th>Challan No</th>

                        <th>Date</th>

                        <th>Customer</th>

                        <th>Status</th>

                        <th>Invoice</th>

                        <th>Actions</th>

                    </tr>

                </thead>

                <tbody>

                    {paginatedChallans.map((c) => (

                        <tr key={c.id}>

                            <td>{c.id}</td>

                            <td>{c.challanNumber}</td>

                            <td>{c.challanDate}</td>

                            <td>{c.customerName}</td>

                            <td>

                                <span
                                    style={{
                                        fontWeight: "bold",

                                        color:

                                            c.status === "DELIVERED"
                                                ? "green"

                                                :

                                                c.status === "CANCELLED"
                                                    ? "red"

                                                    :

                                                    "blue"
                                    }}
                                >
                                    {c.status}
                                </span>

                            </td>

                            <td>

                                {
                                    c.invoice
                                        ?.invoiceNumber

                                    ||

                                    "NOT CREATED"
                                }

                            </td>

                            <td>

                                {/* EDIT */}

                                <button
                                    disabled={
                                        c.status === "DELIVERED"
                                        ||
                                        c.status === "CANCELLED"
                                    }
                                    onClick={() =>
                                        navigate(
                                            `/delivery-challan/edit/${c.id}`
                                        )
                                    }
                                >
                                    Edit
                                </button>

                                {" "}

                                {/* DC PDF */}

                                <button
                                    onClick={() =>
                                        handlePrint(c)
                                    }
                                >
                                    DC
                                </button>

                                {" "}

                                {/* DELIVER */}

                                <button
                                    disabled={
                                        c.status === "DELIVERED"
                                        ||
                                        c.status === "CANCELLED"
                                    }
                                    onClick={() =>
                                        handleDeliver(c.id)
                                    }
                                >
                                    Deliver
                                </button>

                                {" "}

                                {/* CANCEL */}

                                <button
                                    disabled={
                                        c.status === "CANCELLED"
                                        ||
                                        c.status === "DELIVERED"
                                    }
                                    onClick={() =>
                                        handleCancel(c.id)
                                    }
                                >
                                    Cancel
                                </button>

                                {" "}

                                {/* ORIGINAL */}

                                <button
                                    disabled={
                                        !c.invoice?.id
                                    }
                                    onClick={() =>
                                        handleViewInvoice(
                                            c,
                                            "ORIGINAL"
                                        )
                                    }
                                >
                                    Original
                                </button>

                                {" "}

                                {/* DUPLICATE */}

                                <button
                                    disabled={
                                        !c.invoice?.id
                                    }
                                    onClick={() =>
                                        handleViewInvoice(
                                            c,
                                            "DUPLICATE"
                                        )
                                    }
                                >
                                    Duplicate
                                </button>

                                {" "}

                                {/* TRANSPORT */}

                                <button
                                    disabled={
                                        !c.invoice?.id
                                    }
                                    onClick={() =>
                                        handleViewInvoice(
                                            c,
                                            "TRANSPORT"
                                        )
                                    }
                                >
                                    Transport
                                </button>

                            </td>

                        </tr>
                    ))}

                    {
                        paginatedChallans.length === 0
                        &&
                        (
                            <tr>

                                <td
                                    colSpan="7"
                                    style={{
                                        textAlign: "center",
                                        padding: "20px"
                                    }}
                                >
                                    No Delivery Challans Found
                                </td>

                            </tr>
                        )
                    }

                </tbody>

            </table>

            {/* PAGINATION */}

            <div
                style={{
                    marginTop: "20px",
                    display: "flex",
                    gap: "10px",
                    alignItems: "center"
                }}
            >

                <button
                    disabled={currentPage === 1}
                    onClick={() =>
                        setCurrentPage(
                            currentPage - 1
                        )
                    }
                >
                    Previous
                </button>

                <span>

                    Page {currentPage} of{" "}

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