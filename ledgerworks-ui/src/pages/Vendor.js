import { useEffect, useState } from "react";

import {
  getVendors,
  saveVendor,
  updateVendor,
  deleteVendor,
} from "../api/api";

function Vendor() {

  const [vendors, setVendors] = useState([]);

  const [loading, setLoading] = useState(false);

  const [search, setSearch] = useState("");

  const [form, setForm] = useState({
    name: "",
    gstNumber: "",
    phone: "",
    email: "",
    address: "",
    state: "",
  });

  const [editId, setEditId] = useState(null);

  useEffect(() => {
    loadVendors();
  }, []);

  // ================= LOAD =================

  const loadVendors = async () => {

    setLoading(true);

    try {

      const res = await getVendors();

      console.log("Vendor Response:", res);

      const vendorData =
        Array.isArray(res)
          ? res
          : Array.isArray(res.data)
          ? res.data
          : [];

      setVendors(vendorData);

    } catch (err) {

      console.error(err);

      alert("Error loading vendors");

    } finally {

      setLoading(false);
    }
  };

  // ================= CHANGE =================

  const handleChange = (e) => {

    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  // ================= RESET =================

  const resetForm = () => {

    setForm({
      name: "",
      gstNumber: "",
      phone: "",
      email: "",
      address: "",
      state: "",
    });

    setEditId(null);
  };

  // ================= SAVE =================

  const handleSubmit = async () => {

    if (!form.name) {

      alert("Vendor Name Required");

      return;
    }

    try {

      if (editId) {

        await updateVendor(editId, form);

        alert("Vendor Updated Successfully");

      } else {

        await saveVendor(form);

        alert("Vendor Saved Successfully");
      }

      resetForm();

      loadVendors();

    } catch (err) {

      console.error(err);

      alert("Error saving vendor");
    }
  };

  // ================= EDIT =================

  const handleEdit = (vendor) => {

    setForm({
      name: vendor.name || "",
      gstNumber: vendor.gstNumber || "",
      phone: vendor.phone || "",
      email: vendor.email || "",
      address: vendor.address || "",
      state: vendor.state || "",
    });

    setEditId(vendor.id);
  };

  // ================= DELETE =================

  const handleDelete = async (id) => {

    if (!window.confirm("Delete this vendor?")) {

      return;
    }

    try {

      await deleteVendor(id);

      alert("Vendor Deleted Successfully");

      loadVendors();

    } catch (err) {

      console.error(err);

      alert("Error deleting vendor");
    }
  };

  // ================= FILTER =================

  const filtered = vendors.filter((v) => {

    const keyword = search.toLowerCase();

    return (
      v?.name?.toLowerCase().includes(keyword) ||
      v?.email?.toLowerCase().includes(keyword) ||
      v?.phone?.toLowerCase().includes(keyword) ||
      v?.gstNumber?.toLowerCase().includes(keyword)
    );
  });

  return (

    <div>

      <h2>Vendors</h2>

      {/* ================= FORM ================= */}

      <div style={{ marginBottom: "10px" }}>

        <input
          name="name"
          placeholder="Vendor Name"
          value={form.name}
          onChange={handleChange}
        />

        <input
          name="gstNumber"
          placeholder="GST Number"
          value={form.gstNumber}
          onChange={handleChange}
        />

      </div>

      <div style={{ marginBottom: "10px" }}>

        <input
          name="phone"
          placeholder="Phone"
          value={form.phone}
          onChange={handleChange}
        />

        <input
          name="email"
          placeholder="Email"
          value={form.email}
          onChange={handleChange}
        />

      </div>

      <div style={{ marginBottom: "10px" }}>

        <input
          name="state"
          placeholder="State"
          value={form.state}
          onChange={handleChange}
        />

      </div>

      <div style={{ marginBottom: "10px" }}>

        <textarea
          name="address"
          placeholder="Address"
          value={form.address}
          onChange={handleChange}
          rows="3"
          cols="60"
        />

      </div>

      <button onClick={handleSubmit}>
        {editId ? "Update Vendor" : "Save Vendor"}
      </button>

      <button onClick={resetForm}>
        New
      </button>

      <hr />

      {/* ================= SEARCH ================= */}

      <h3>Search</h3>

      <input
        placeholder="Search name/email/gst"
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />

      <button onClick={loadVendors}>
        Search
      </button>

      <button
        onClick={() => {
          setSearch("");
          loadVendors();
        }}
      >
        Clear
      </button>

      <hr />

      {/* ================= TABLE ================= */}

      {loading ? (

        <p>Loading...</p>

      ) : (

        <table border="1" cellPadding="5">

          <thead>

            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>GST</th>
              <th>Phone</th>
              <th>Email</th>
              <th>State</th>
              <th>Address</th>
              <th>Action</th>
            </tr>

          </thead>

          <tbody>

            {filtered.length > 0 ? (

              filtered.map((v) => (

                <tr key={v.id}>

                  <td>{v.id}</td>

                  <td>{v.name}</td>

                  <td>{v.gstNumber}</td>

                  <td>{v.phone}</td>

                  <td>{v.email}</td>

                  <td>{v.state}</td>

                  <td>{v.address}</td>

                  <td>

                    <button
                      onClick={() => handleEdit(v)}
                    >
                      Edit
                    </button>

                    <button
                      onClick={() => handleDelete(v.id)}
                    >
                      Delete
                    </button>

                  </td>

                </tr>
              ))

            ) : (

              <tr>

                <td colSpan="8">
                  No Data Found
                </td>

              </tr>

            )}

          </tbody>

        </table>
      )}

    </div>
  );
}

export default Vendor;