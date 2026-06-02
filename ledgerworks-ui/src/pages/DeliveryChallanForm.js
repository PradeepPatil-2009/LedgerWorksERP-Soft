import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate, useParams } from "react-router-dom";

export default function DeliveryChallanForm() {

    const navigate = useNavigate();

    const { id: editingId } = useParams();

    const [customers, setCustomers] = useState([]);

    const [items, setItems] = useState([]);

    const [form, setForm] = useState({

        customerId: "",

        customerName: "",

        customerGST: "",

        customerAddress: "",

        customerEmail: "",

        customerPhone: "",

        placeOfSupply: "MH",

        transportName: "",

        vehicleNumber: "",

        descriptions: "",

        items: [
            {
                description: "",
                hsnCode: "",
                unit: "",
                quantity: 1,
                rate: 0,

                taxableAmount: 0,

                cgstPercent: 9,
                sgstPercent: 9,
                igstPercent: 0,

                cgstAmount: 0,
                sgstAmount: 0,
                igstAmount: 0,

                totalAmount: 0
            }
        ]
    });

    // ================= LOAD EDIT DATA =================

    useEffect(() => {

        if (editingId) {

            axios.get(
                `http://localhost:8080/api/delivery-challan/${editingId}`
            )
                .then((res) => {

                    setForm(res.data);

                })
                .catch((err) => {

                    console.log(err);

                });
        }

    }, [editingId]);

    // ================= LOAD CUSTOMERS =================

    const loadCustomers = async () => {

        try {

            const res = await axios.get(
                "http://localhost:8080/api/customers"
            );

            setCustomers(res.data);

        } catch (error) {

            console.log(error);
        }
    };

    // ================= LOAD ITEMS =================

    const loadItems = async () => {

        try {

            const res = await axios.get(
                "http://localhost:8080/api/items"
            );

            setItems(res.data);

        } catch (error) {

            console.log(error);
        }
    };

    useEffect(() => {

        loadCustomers();

        loadItems();

    }, []);

    // ================= CUSTOMER SELECT =================

    const handleCustomerSelect = (e) => {

        const customerId = e.target.value;

        const selected = customers.find(
            (c) => String(c.id) === String(customerId)
        );

        if (!selected) return;

        setForm(prev => ({

            ...prev,

            customerId: selected.id,

            customerName: selected.name || "",

            customerGST: selected.gstNumber || "",

            customerAddress: selected.address || "",

            customerEmail: selected.email || "",

            customerPhone: selected.phone || "",

            placeOfSupply: selected.state || "MH"

        }));
    };

    // ================= COMMON CHANGE =================

    const handleChange = (e) => {

    const { name, value } = e.target;

    // ================= PLACE OF SUPPLY =================

    if (name === "placeOfSupply") {

        const updatedItems = [...form.items];

        updatedItems.forEach((item, index) => {

            // ================= MAHARASHTRA =================

            if (value === "Maharashtra") {

                item.cgstPercent = 9;
                item.sgstPercent = 9;
                item.igstPercent = 0;

            }

            // ================= OTHER STATES =================

            else {

                item.cgstPercent = 0;
                item.sgstPercent = 0;
                item.igstPercent = 18;
            }

            // RECALCULATE ITEM

            calculateItem(updatedItems, index);
        });

        setForm(prev => ({

            ...prev,

            placeOfSupply: value,

            items: updatedItems

        }));

        return;
    }

    // ================= NORMAL CHANGE =================

    setForm(prev => ({

        ...prev,

        [name]: value

    }));
};

    // ================= ITEM SELECT =================

    const handleItemSelect = (index, itemId) => {

        const selectedItem = items.find(
            (i) => String(i.id) === String(itemId)
        );

        if (!selectedItem) return;

        const updatedItems = [...form.items];

        updatedItems[index] = {

            ...updatedItems[index],

            description: selectedItem.itemName || "",

            hsnCode: selectedItem.hsnCode || "",

            unit: selectedItem.unit || "",

            rate: Number(selectedItem.saleRate || 0)

        };

        calculateItem(updatedItems, index);

        setForm({
            ...form,
            items: updatedItems
        });
    };

    // ================= ITEM CHANGE =================

    const handleItemChange = (index, field, value) => {

        const updatedItems = [...form.items];

        updatedItems[index][field] = value;

        calculateItem(updatedItems, index);

        setForm({
            ...form,
            items: updatedItems
        });
    };

    // ================= CALCULATE =================

    const calculateItem = (updatedItems, index) => {

        const item = updatedItems[index];

        const qty =
            Number(item.quantity || 0);

        const rate =
            Number(item.rate || 0);

        // ================= TAXABLE =================

        const taxable = qty * rate;

        item.taxableAmount =
            Number(taxable.toFixed(2));

        // ================= GST RATES =================

        const cgstPercent =
            Number(item.cgstPercent || 0);

        const sgstPercent =
            Number(item.sgstPercent || 0);

        const igstPercent =
            Number(item.igstPercent || 0);

        // ================= GST AMOUNTS =================

        const cgst =
            taxable * cgstPercent / 100;

        const sgst =
            taxable * sgstPercent / 100;

        const igst =
            taxable * igstPercent / 100;
        item.cgstAmount =
            Number(cgst.toFixed(2));

        item.sgstAmount =
            Number(sgst.toFixed(2));

        item.igstAmount =
            Number(igst.toFixed(2));

        // ================= TOTAL =================

        item.totalAmount = Number(
            (
                taxable +
                cgst +
                sgst +
                igst
            ).toFixed(2)
        );
    };

    // ================= ADD ITEM =================

    const addItem = () => {

        setForm({

            ...form,

            items: [

                ...form.items,

                {
                    description: "",
                    hsnCode: "",
                    unit: "",
                    quantity: 1,
                    rate: 0,

                    taxableAmount: 0,

                    cgstPercent: 9,
                    sgstPercent: 9,
                    igstPercent: 0,

                    cgstAmount: 0,
                    sgstAmount: 0,
                    igstAmount: 0,

                    totalAmount: 0
                }
            ]
        });
    };

    // ================= REMOVE ITEM =================

    const removeItem = (index) => {

        const updatedItems =
            form.items.filter((_, i) => i !== index);

        setForm({
            ...form,
            items: updatedItems
        });
    };

    // ================= SAVE =================

    const saveChallan = async () => {

        try {

            await axios.post(
                "http://localhost:8080/api/delivery-challan",
                form
            );

            alert("Delivery Challan Created");

            navigate("/delivery-challan");

        } catch (error) {

            console.log(error);

            alert("Save failed");
        }
    };

    // ================= UI =================
const states = [

    "Maharashtra",
    "Gujarat",
    "Karnataka",
    "Tamil Nadu",
    "Delhi",
    "Rajasthan",
    "Uttar Pradesh",
    "Madhya Pradesh",
    "Bihar",
    "Punjab",
    "Haryana",
    "West Bengal",
    "Odisha",
    "Kerala",
    "Andhra Pradesh",
    "Telangana",
    "Chhattisgarh",
    "Jharkhand",
    "Assam",
    "Goa"

];
    return (

        <div style={{ padding: "20px" }}>

            <h2>Create Delivery Challan</h2>

            <h3>Customer Details</h3>

            <select
                value={form.customerId || ""}
                onChange={handleCustomerSelect}
                style={{ width: "300px" }}
            >

                <option value="">
                    Select Existing Customer
                </option>

                {
                    customers.map((customer) => (

                        <option
                            key={customer.id}
                            value={customer.id}
                        >
                            {customer.name}
                        </option>
                    ))
                }

            </select>

            <br /><br />

            <input
                type="text"
                name="customerName"
                placeholder="Customer Name"
                value={form.customerName}
                onChange={handleChange}
            />

            <input
                type="text"
                name="customerGST"
                placeholder="GST Number"
                value={form.customerGST}
                onChange={handleChange}
            />

            <br /><br />

            <textarea
                name="customerAddress"
                placeholder="Customer Address"
                value={form.customerAddress}
                onChange={handleChange}
                rows={4}
                cols={80}
            />

            <br /><br />

            <input
                type="text"
                name="customerEmail"
                placeholder="Email"
                value={form.customerEmail}
                onChange={handleChange}
            />

            <input
                type="text"
                name="customerPhone"
                placeholder="Phone"
                value={form.customerPhone}
                onChange={handleChange}
            />


            <br /><br />


<select
    name="placeOfSupply"
    value={form.placeOfSupply}
    onChange={handleChange}
>

    {
        states.map((state) => (

            <option
                key={state}
                value={state}
            >
                {state}
            </option>

        ))
    }

</select>

<br></br><br></br>
            <input
                type="text"
                name="transportName"
                placeholder="Transport Name"
                value={form.transportName}
                onChange={handleChange}
            />

            <input
                type="text"
                name="vehicleNumber"
                placeholder="Vehicle Number"
                value={form.vehicleNumber}
                onChange={handleChange}
            />

            <h3>Items</h3>

            <table border="1" cellPadding="5">

                <thead>

                    <tr>

                        <th>Item</th>

                        <th>HSN</th>

                        <th>Unit</th>

                        <th>Qty</th>

                        <th>Rate</th>

                        <th>Taxable</th>

                        <th>CGST %</th>
                        <th>CGST Amt</th>

                        <th>SGST %</th>
                        <th>SGST Amt</th>

                        <th>IGST %</th>
                        <th>IGST Amt</th>

                        <th>Total</th>

                        <th>Action</th>

                    </tr>

                </thead>

                <tbody>

                    {
                        form.items.map((item, index) => (

                            <tr key={index}>

                                <td>

                                    <select
                                        onChange={(e) =>
                                            handleItemSelect(
                                                index,
                                                e.target.value
                                            )
                                        }
                                    >

                                        <option value="">
                                            Select Item
                                        </option>

                                        {
                                            items.map((i) => (

                                                <option
                                                    key={i.id}
                                                    value={i.id}
                                                >
                                                    {i.itemName}
                                                </option>
                                            ))
                                        }

                                    </select>

                                </td>

                                <td>
                                    <input
                                        value={item.hsnCode}
                                        readOnly
                                    />
                                </td>

                                <td>
                                    <input
                                        value={item.unit}
                                        readOnly
                                    />
                                </td>

                                <td>
                                    <input
                                        type="number"
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
                                    {Number(item.taxableAmount).toFixed(2)}
                                </td>

                                {/* CGST */}

                                <td>
                                    <select
                                        value={item.cgstPercent}
                                        onChange={(e) =>
                                            handleItemChange(
                                                index,
                                                "cgstPercent",
                                                e.target.value
                                            )
                                        }
                                    >
                                        <option value={0}>0%</option>
                                        <option value={2}>2%</option>
                                        <option value={5}>5%</option>
                                        <option value={7}>7%</option>
                                        <option value={9}>9%</option>
                                        <option value={15}>15%</option>
                                        <option value={18}>18%</option>
                                    </select>
                                </td>

                                <td>
                                    {Number(item.cgstAmount).toFixed(2)}
                                </td>

                                {/* SGST */}

                                <td>
                                    <select
                                        value={item.sgstPercent}
                                        onChange={(e) =>
                                            handleItemChange(
                                                index,
                                                "sgstPercent",
                                                e.target.value
                                            )
                                        }
                                    >
                                        <option value={0}>0%</option>
                                        <option value={2}>2%</option>
                                        <option value={5}>5%</option>
                                        <option value={7}>7%</option>
                                        <option value={9}>9%</option>
                                        <option value={15}>15%</option>
                                        <option value={18}>18%</option>
                                    </select>
                                </td>

                                <td>
                                    {Number(item.sgstAmount).toFixed(2)}
                                </td>

                                {/* IGST */}

                                <td>
                                    <select
                                        value={item.igstPercent}
                                        onChange={(e) =>
                                            handleItemChange(
                                                index,
                                                "igstPercent",
                                                e.target.value
                                            )
                                        }
                                    >
                                        <option value={0}>0%</option>
                                        <option value={2}>2%</option>
                                        <option value={5}>5%</option>
                                        <option value={7}>7%</option>
                                        <option value={9}>9%</option>
                                        <option value={15}>15%</option>
                                        <option value={18}>18%</option>
                                    </select>
                                </td>

                                <td>
                                    {Number(item.igstAmount).toFixed(2)}
                                </td>

                                {/* TOTAL */}

                                <td>
                                    {Number(item.totalAmount).toFixed(2)}
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
                        ))
                    }

                </tbody>

            </table>

            <br />

            <button onClick={addItem}>
                Add Item
            </button>

            <br /><br />

            <textarea
                name="descriptions"
                placeholder="Comments / Notes"
                value={form.descriptions}
                onChange={handleChange}
                rows={4}
                cols={80}
            />

            <br /><br />

            <button onClick={saveChallan}>
                Save Challan
            </button>

        </div>
    );
}