import React, { useEffect, useState } from "react";
import axios from "axios";

export default function ItemMasterPage() {

  const [items, setItems] = useState([]);

  const [form, setForm] = useState({
    itemCode: "",
    itemName: "",
    hsnCode: "",
    unit: "",
    purchaseRate: "",
    saleRate: "",
    gstPercent: "",
    currentStock: "",
    minimumStock: "",
    category: "",
    status: "ACTIVE",
  });

  // ================= LOAD ITEMS =================

  useEffect(() => {
    loadItems();
  }, []);

  const loadItems = async () => {

    try {

      const res = await axios.get(
        "http://localhost:8080/api/items"
      );

      setItems(res.data || []);

    } catch (err) {

      console.error(err);

      alert("Error loading items");
    }
  };

  // ================= HANDLE CHANGE =================

  const handleChange = (e) => {

    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  // ================= RESET =================

  const resetForm = () => {

    setForm({
      itemCode: "",
      itemName: "",
      hsnCode: "",
      unit: "",
      purchaseRate: "",
      saleRate: "",
      gstPercent: "",
      currentStock: "",
      minimumStock: "",
      category: "",
      status: "ACTIVE",
    });
  };

  // ================= SAVE ITEM =================

  const handleSubmit = async () => {

    if (!form.itemName) {

      alert("Item Name Required");

      return;
    }

    try {

      await axios.post(
        "http://localhost:8080/api/items",
        form
      );

      alert("Item Saved Successfully");

      resetForm();

      loadItems();

    } catch (err) {

      console.error(err);

      alert("Error saving item");
    }
  };

  return (

    <div>

      <h2>Item Master</h2>

      {/* ================= FORM ================= */}

      <div style={{ marginBottom: "10px" }}>

        <input
          type="text"
          name="itemCode"
          placeholder="Item Code"
          value={form.itemCode}
          onChange={handleChange}
        />

        <input
          type="text"
          name="itemName"
          placeholder="Item Name"
          value={form.itemName}
          onChange={handleChange}
        />

      </div>

      <div style={{ marginBottom: "10px" }}>

        <input
          type="text"
          name="hsnCode"
          placeholder="HSN Code"
          value={form.hsnCode}
          onChange={handleChange}
        />

        <input
          type="text"
          name="unit"
          placeholder="Unit"
          value={form.unit}
          onChange={handleChange}
        />

      </div>

      <div style={{ marginBottom: "10px" }}>

        <input
          type="number"
          name="purchaseRate"
          placeholder="Purchase Rate"
          value={form.purchaseRate}
          onChange={handleChange}
        />

        <input
          type="number"
          name="saleRate"
          placeholder="Sale Rate"
          value={form.saleRate}
          onChange={handleChange}
        />

      </div>

      <div style={{ marginBottom: "10px" }}>

        <input
          type="number"
          name="gstPercent"
          placeholder="GST %"
          value={form.gstPercent}
          onChange={handleChange}
        />

        <input
          type="number"
          name="currentStock"
          placeholder="Current Stock"
          value={form.currentStock}
          onChange={handleChange}
        />

      </div>

      <div style={{ marginBottom: "10px" }}>

        <input
          type="number"
          name="minimumStock"
          placeholder="Minimum Stock"
          value={form.minimumStock}
          onChange={handleChange}
        />

        <input
          type="text"
          name="category"
          placeholder="Category"
          value={form.category}
          onChange={handleChange}
        />

      </div>

      <button onClick={handleSubmit}>
        Save Item
      </button>

      <hr />

      {/* ================= TABLE ================= */}

      <table border="1" cellPadding="5">

        <thead>

          <tr>
            <th>ID</th>
            <th>Item Code</th>
            <th>Item Name</th>
            <th>HSN</th>
            <th>Unit</th>
            <th>Purchase Rate</th>
            <th>Sale Rate</th>
            <th>GST%</th>
            <th>Stock</th>
            <th>Min Stock</th>
            <th>Category</th>
            <th>Status</th>
          </tr>

        </thead>

        <tbody>

          {items.length > 0 ? (

            items.map((i) => (

              <tr key={i.id}>

                <td>{i.id}</td>
                <td>{i.itemCode}</td>
                <td>{i.itemName}</td>
                <td>{i.hsnCode}</td>
                <td>{i.unit}</td>
                <td>{i.purchaseRate}</td>
                <td>{i.saleRate}</td>
                <td>{i.gstPercent}</td>
                <td>{i.currentStock}</td>
                <td>{i.minimumStock}</td>
                <td>{i.category}</td>
                <td>{i.status}</td>

              </tr>
            ))

          ) : (

            <tr>

              <td colSpan="12">
                No Items Found
              </td>

            </tr>

          )}

        </tbody>

      </table>

    </div>
  );
}