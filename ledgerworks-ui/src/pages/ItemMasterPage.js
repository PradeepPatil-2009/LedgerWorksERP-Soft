import React, { useEffect, useState } from "react";
import axios from "axios";
import API from "../api/api";
import { useToast } from "../components/Toast";
import { useTableControls } from "../components/useTableControls";
import Pagination from "../components/Pagination";
import SortableTh from "../components/SortableTh";

export default function ItemMasterPage() {

  const toast = useToast();

  const [items, setItems] = useState([]);

  // Configured GST slabs offered as quick-pick options for the GST % field.
  const [gstRates, setGstRates] = useState([]);

  const tc = useTableControls(items, {
    searchKeys: ["itemCode", "itemName", "hsnCode", "unit", "category", "status"],
    pageSize: 10,
  });

  const {
    query,
    setQuery,
    page,
    setPage,
    totalPages,
    pageItems,
    total,
  } = tc;

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
    loadGstRates();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const loadItems = async () => {

    try {

      const res = await axios.get(
        "http://localhost:8080/api/items"
      );

      setItems(res.data || []);

    } catch (err) {

      console.error(err);

      toast.error("Error loading items");
    }
  };

  const loadGstRates = async () => {

    try {

      const res = await API.get("/gst-rates");

      setGstRates(res.data || []);

    } catch (err) {

      // Non-fatal: the GST % field still works as a plain number input.
      console.error(err);
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

      toast.error("Item Name Required");

      return;
    }

    try {

      await axios.post(
        "http://localhost:8080/api/items",
        form
      );

      toast.success("Item Saved Successfully");

      resetForm();

      loadItems();

    } catch (err) {

      console.error(err);

      toast.error("Error saving item");
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
          list="gst-rate-options"
        />

        <datalist id="gst-rate-options">
          {gstRates.map((r) => (
            <option key={r.id} value={r.rate}>
              {r.label}
            </option>
          ))}
        </datalist>

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

      <input
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="Search..."
        style={{ marginBottom: "10px" }}
      />

      <div className="table-scroll">

      <table border="1" cellPadding="5">

        <thead>

          <tr>
            <SortableTh field="id" controls={tc}>ID</SortableTh>
            <SortableTh field="itemCode" controls={tc}>Item Code</SortableTh>
            <SortableTh field="itemName" controls={tc}>Item Name</SortableTh>
            <SortableTh field="hsnCode" controls={tc}>HSN</SortableTh>
            <SortableTh field="unit" controls={tc}>Unit</SortableTh>
            <SortableTh field="purchaseRate" controls={tc}>Purchase Rate</SortableTh>
            <SortableTh field="saleRate" controls={tc}>Sale Rate</SortableTh>
            <SortableTh field="gstPercent" controls={tc}>GST%</SortableTh>
            <SortableTh field="currentStock" controls={tc}>Stock</SortableTh>
            <SortableTh field="minimumStock" controls={tc}>Min Stock</SortableTh>
            <SortableTh field="category" controls={tc}>Category</SortableTh>
            <SortableTh field="status" controls={tc}>Status</SortableTh>
          </tr>

        </thead>

        <tbody>

          {pageItems.length > 0 ? (

            pageItems.map((i) => (

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

      <Pagination
        page={page}
        totalPages={totalPages}
        total={total}
        onPrev={() => setPage(page - 1)}
        onNext={() => setPage(page + 1)}
      />

    </div>
  );
}