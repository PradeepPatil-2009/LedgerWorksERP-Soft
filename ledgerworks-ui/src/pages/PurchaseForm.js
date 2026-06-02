import React, {
    useEffect,
    useState
} from "react";

import axios from "axios";

import {
    useNavigate,
    useParams
} from "react-router-dom";

export default function PurchaseForm() {

    const navigate = useNavigate();

    const { id } = useParams();

    const editingId = id;

    // ================= VENDORS =================

    const [vendors, setVendors] =
        useState([]);

    // ================= ITEM MASTER =================

    const [itemsMaster, setItemsMaster] =
        useState([]);

    // ================= FORM =================

    const [form, setForm] = useState({

        vendorId: "",

        vendorName: "",

        vendorAddress: "",

        vendorGST: "",

        vendorPhone: "",

        vendorEmail: "",

        placeOfSupply: "MH",

        remarks: "",

        items: [

            {
                itemName: "",
                hsnCode: "",
                unit: "",
                quantity: 1,
                rate: 0,
                cgstPercent: 9,
                sgstPercent: 9,
                igstPercent: 18
            }
        ]
    });

    // ================= LOAD =================

    useEffect(() => {

        loadVendors();

        loadItems();

        if (editingId) {

            loadPurchase();
        }

        // eslint-disable-next-line
    }, [editingId]);

    // ================= LOAD VENDORS =================

    const loadVendors = async () => {

        try {

            const res = await axios.get(
                "http://localhost:8080/api/vendors"
            );

            setVendors(res.data || []);

        } catch (err) {

            console.error(err);
        }
    };

    // ================= LOAD ITEMS =================

    const loadItems = async () => {

        try {

            const res = await axios.get(
                "http://localhost:8080/api/items"
            );

            setItemsMaster(res.data || []);

        } catch (err) {

            console.error(err);
        }
    };

    // ================= LOAD PURCHASE =================

    const loadPurchase = async () => {

        try {

            const res = await axios.get(
                `http://localhost:8080/api/purchases/${editingId}`
            );

            setForm(res.data);

        } catch (err) {

            console.error(err);

            alert("Unable to load purchase");
        }
    };

    // ================= VENDOR SELECT =================

    const handleVendorSelect = (e) => {

        const vendorId = e.target.value;

        const selectedVendor =
            vendors.find(
                (v) =>
                    String(v.id) === vendorId
            );

        if (!selectedVendor) return;

        setForm({

            ...form,

            vendorId:
                selectedVendor.id,

            vendorName:
                selectedVendor.name || "",

            vendorEmail:
                selectedVendor.email || "",

            vendorPhone:
                selectedVendor.phone || "",

            vendorAddress:
                selectedVendor.address || "",

            vendorGST:
                selectedVendor.gstNumber || ""
        });
    };

    // ================= CHANGE =================

    const handleChange = (e) => {

        setForm({

            ...form,

            [e.target.name]:
                e.target.value
        });
    };

    // ================= ITEM CHANGE =================

    const handleItemChange = (
        index,
        field,
        value
    ) => {

        const updated =
            [...form.items];

        updated[index][field] = value;

        setForm({
            ...form,
            items: updated
        });
    };

    // ================= ADD ITEM =================

    const addItem = () => {

        setForm({

            ...form,

            items: [

                ...form.items,

                {
                    itemName: "",
                    hsnCode: "",
                    unit: "",
                    quantity: 1,
                    rate: 0,
                    cgstPercent: 9,
                    sgstPercent: 9,
                    igstPercent: 18
                }
            ]
        });
    };

    // ================= REMOVE ITEM =================

    const removeItem = (index) => {

        const updated =
            form.items.filter(
                (_, i) => i !== index
            );

        setForm({
            ...form,
            items: updated
        });
    };

    // ================= SAVE =================

    // ================= SAVE =================

    const savePurchase = async () => {

        try {

            // ================= CLEAN ITEMS =================

            const cleanedItems =
                form.items
                    .filter(
                        (item) =>
                            item.itemName &&
                            item.itemName.trim() !== ""
                    )
                    .map((item) => ({

                        id: item.id || null,

                        itemName:
                            item.itemName || "",

                        hsnCode:
                            item.hsnCode || "",

                        unit:
                            item.unit || "",

                        quantity:
                            Number(item.quantity || 0),

                        rate:
                            Number(item.rate || 0),

                        cgstPercent:
                            Number(item.cgstPercent || 0),

                        sgstPercent:
                            Number(item.sgstPercent || 0),

                        igstPercent:
                            Number(item.igstPercent || 0)
                    }));

            if (cleanedItems.length === 0) {

                alert("Please add at least one item");

                return;
            }

            // ================= FINAL PAYLOAD =================

            const payload = {

                vendorName:
                    form.vendorName || "",

                vendorAddress:
                    form.vendorAddress || "",

                vendorGST:
                    form.vendorGST || "",

                vendorPhone:
                    form.vendorPhone || "",

                vendorEmail:
                    form.vendorEmail || "",

                placeOfSupply:
                    form.placeOfSupply || "MH",

                remarks:
                    form.remarks || "",

                items: cleanedItems
            };

            console.log(
                "FINAL PAYLOAD = ",
                payload
            );

            // ================= CREATE VENDOR =================

            if (
                !editingId
                &&
                !form.vendorId
            ) {

                await axios.post(
                    "http://localhost:8080/api/vendors",
                    {
                        name:
                            form.vendorName,

                        email:
                            form.vendorEmail,

                        phone:
                            form.vendorPhone,

                        address:
                            form.vendorAddress,

                        gstNumber:
                            form.vendorGST
                    }
                );
            }

            // ================= UPDATE =================

            if (editingId) {

                await axios.put(
                    `http://localhost:8080/api/purchases/${editingId}`,
                    payload
                );

                alert("Purchase Updated");

            } else {

                // ================= CREATE =================

                await axios.post(
                    "http://localhost:8080/api/purchases",
                    payload
                );

                alert("Purchase Created");
            }

            navigate("/purchase");

        } catch (err) {

            console.error(err);

            console.log(
                "BACKEND ERROR = ",
                err.response?.data
            );

            alert("Save failed");
        }
    };

    // ================= TOTAL =================

    const calculateGrandTotal = () => {

        let total = 0;

        form.items.forEach((i) => {

            const qty =
                Number(i.quantity || 0);

            const rate =
                Number(i.rate || 0);

            const taxable =
                qty * rate;

            let gst = 0;

            if (
                form.placeOfSupply === "MH"
            ) {

                gst =
                    taxable
                    *
                    (
                        Number(i.cgstPercent || 0)

                        +

                        Number(i.sgstPercent || 0)
                    )
                    / 100;

            } else {

                gst =
                    taxable
                    *
                    Number(i.igstPercent || 0)
                    / 100;
            }

            total += taxable + gst;
        });

        return total.toFixed(2);
    };

    // ================= UI =================

    return (

        <div style={{ padding: "20px" }}>

            <h2>

                {
                    editingId
                        ? "Edit Purchase"
                        : "Create Purchase"
                }

            </h2>

            {/* ================= VENDOR ================= */}

            <h3>
                Vendor Details
            </h3>

            <div
                style={{
                    border: "1px solid #ccc",
                    padding: "15px",
                    marginBottom: "20px"
                }}
            >

                <select
                    onChange={
                        handleVendorSelect
                    }
                    style={{
                        width: "100%",
                        padding: "8px",
                        marginBottom: "10px"
                    }}
                >

                    <option value="">
                        Select Existing Vendor
                    </option>

                    {vendors.map((v) => (

                        <option
                            key={v.id}
                            value={v.id}
                        >
                            {v.name}
                        </option>
                    ))}

                </select>

                <br />

                <input
                    type="text"
                    name="vendorName"
                    placeholder="Vendor Name"
                    value={form.vendorName}
                    onChange={handleChange}
                    style={{
                        width: "48%",
                        marginRight: "2%",
                        padding: "8px"
                    }}
                />

                <input
                    type="text"
                    name="vendorGST"
                    placeholder="GST Number"
                    value={form.vendorGST}
                    onChange={handleChange}
                    style={{
                        width: "48%",
                        padding: "8px"
                    }}
                />

                <br />
                <br />

                <textarea
                    name="vendorAddress"
                    placeholder="Vendor Address"
                    value={form.vendorAddress}
                    onChange={handleChange}
                    rows="3"
                    style={{
                        width: "100%",
                        padding: "8px"
                    }}
                />

                <br />
                <br />

                <input
                    type="text"
                    name="vendorPhone"
                    placeholder="Phone"
                    value={form.vendorPhone}
                    onChange={handleChange}
                    style={{
                        width: "48%",
                        marginRight: "2%",
                        padding: "8px"
                    }}
                />

                <input
                    type="text"
                    name="vendorEmail"
                    placeholder="Email"
                    value={form.vendorEmail}
                    onChange={handleChange}
                    style={{
                        width: "48%",
                        padding: "8px"
                    }}
                />

                <br />
                <br />

                <select
                    name="placeOfSupply"
                    value={form.placeOfSupply}
                    onChange={handleChange}
                    style={{
                        width: "200px",
                        padding: "8px"
                    }}
                >

                    <option value="MH">
                        Maharashtra
                    </option>

                    <option value="OTHER">
                        Other State
                    </option>

                </select>

            </div>

            {/* ================= ITEMS ================= */}

            <h3>
                Purchase Items
            </h3>

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

                        <th>Item</th>

                        <th>HSN</th>

                        <th>Unit</th>

                        <th>Qty</th>

                        <th>Rate</th>

                        <th>CGST%</th>

                        <th>SGST%</th>

                        <th>IGST%</th>

                        <th>Total</th>

                        <th>Action</th>

                    </tr>

                </thead>

                <tbody>

                    {
                        form.items.map(
                            (item, index) => {

                                const qty =
                                    Number(
                                        item.quantity || 0
                                    );

                                const rate =
                                    Number(
                                        item.rate || 0
                                    );

                                const taxable =
                                    qty * rate;

                                let gst = 0;

                                if (
                                    form.placeOfSupply
                                    === "MH"
                                ) {

                                    gst =
                                        taxable
                                        *
                                        (
                                            Number(
                                                item.cgstPercent || 0
                                            )

                                            +

                                            Number(
                                                item.sgstPercent || 0
                                            )
                                        )
                                        / 100;

                                } else {

                                    gst =
                                        taxable
                                        *
                                        Number(
                                            item.igstPercent || 0
                                        )
                                        / 100;
                                }

                                const total =
                                    taxable + gst;

                                return (

                                    <tr key={index}>

                                        <td>

                                            <select
                                                value={item.itemName}
                                                onChange={(e) => {

                                                    const selectedItem =
                                                        itemsMaster.find(
                                                            x =>
                                                                x.itemName ===
                                                                e.target.value
                                                        );

                                                    const updated =
                                                        [...form.items];

                                                    updated[index].itemName =
                                                        selectedItem?.itemName || "";

                                                    updated[index].hsnCode =
                                                        selectedItem?.hsnCode || "";

                                                    updated[index].unit =
                                                        selectedItem?.unit || "";

                                                    setForm({
                                                        ...form,
                                                        items: updated
                                                    });
                                                }}
                                            >

                                                <option value="">
                                                    Select Item
                                                </option>

                                                {
                                                    itemsMaster.map((m) => (

                                                        <option
                                                            key={m.id}
                                                            value={m.itemName}
                                                        >
                                                            {m.itemName}
                                                        </option>
                                                    ))
                                                }

                                            </select>

                                        </td>

                                        <td>

                                            <input
                                                value={item.hsnCode || ""}
                                                readOnly
                                            />

                                        </td>

                                        <td>

                                            <input
                                                value={item.unit || ""}
                                                readOnly
                                                style={{
                                                    width: "70px"
                                                }}
                                            />

                                        </td>

                                        <td>

                                            <input
                                                type="number"
                                                style={{
                                                    width: "60px"
                                                }}
                                                value={item.quantity}
                                                onChange={(e) =>
                                                    handleItemChange(
                                                        index,
                                                        "quantity",
                                                        e.target.value
                                                    )
                                                }
                                            />

                                        </td>

                                        <td>

                                            <input
                                                type="number"
                                                style={{
                                                    width: "90px"
                                                }}
                                                value={item.rate}
                                                onChange={(e) =>
                                                    handleItemChange(
                                                        index,
                                                        "rate",
                                                        e.target.value
                                                    )
                                                }
                                            />

                                        </td>

                                        <td>

                                            <input
                                                type="number"
                                                style={{
                                                    width: "60px"
                                                }}
                                                value={item.cgstPercent}
                                                onChange={(e) =>
                                                    handleItemChange(
                                                        index,
                                                        "cgstPercent",
                                                        e.target.value
                                                    )
                                                }
                                            />

                                        </td>

                                        <td>

                                            <input
                                                type="number"
                                                style={{
                                                    width: "60px"
                                                }}
                                                value={item.sgstPercent}
                                                onChange={(e) =>
                                                    handleItemChange(
                                                        index,
                                                        "sgstPercent",
                                                        e.target.value
                                                    )
                                                }
                                            />

                                        </td>

                                        <td>

                                            <input
                                                type="number"
                                                style={{
                                                    width: "60px"
                                                }}
                                                value={item.igstPercent}
                                                onChange={(e) =>
                                                    handleItemChange(
                                                        index,
                                                        "igstPercent",
                                                        e.target.value
                                                    )
                                                }
                                            />

                                        </td>

                                        <td>

                                            {
                                                total.toFixed(2)
                                            }

                                        </td>

                                        <td>

                                            <button
                                                onClick={() =>
                                                    removeItem(index)
                                                }
                                            >
                                                Remove
                                            </button>

                                        </td>

                                    </tr>
                                );
                            }
                        )
                    }

                </tbody>

            </table>

            <br />

            <button onClick={addItem}>
                + Add Item
            </button>

            <br />
            <br />

            <textarea
                name="remarks"
                placeholder="Remarks"
                value={form.remarks}
                onChange={handleChange}
                rows="4"
                style={{
                    width: "100%",
                    padding: "8px"
                }}
            />

            <br />
            <br />

            <h3>

                Grand Total :
                {" "}
                ₹ {calculateGrandTotal()}

            </h3>

            <button
                onClick={savePurchase}
                style={{
                    padding: "10px 20px"
                }}
            >

                {
                    editingId
                        ? "Update Purchase"
                        : "Save Purchase"
                }

            </button>

        </div>
    );
}