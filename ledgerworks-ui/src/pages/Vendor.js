import { useEffect, useState } from "react";

import API, {
  getVendors,
  saveVendor,
  updateVendor,
  deleteVendor,
} from "../api/api";

import { useToast } from "../components/Toast";

function Vendor() {

  const toast = useToast();

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
    stateCode: "",
  });

  const [editId, setEditId] = useState(null);

  // ============ GST AUTO STATE DETECTION ============

  const autoFillStateFromGst = async (gst) => {

    if (!gst || gst.trim().length < 2) {
      return;
    }

    try {

      const res = await API.get(
        "/gst-utility/state",
        { params: { gst } }
      );

      const { code, stateName } = res.data || {};

      if (stateName) {

        setForm((prev) => ({
          ...prev,
          state: stateName,
          stateCode: code || prev.stateCode,
        }));
      }

    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    loadVendors();
    // eslint-disable-next-line react-hooks/exhaustive-deps
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

      toast.error("Error loading vendors");

    } finally {

      setLoading(false);
    }
  };

  // ================= CHANGE =================

  const handleChange = (e) => {

    const { name, value } = e.target;

    setForm({
      ...form,
      [name]: value,
    });

    if (name === "gstNumber") {
      autoFillStateFromGst(value);
    }
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
      stateCode: "",
    });

    setEditId(null);
  };

  // ================= SAVE =================

  const handleSubmit = async () => {

    if (!form.name) {

      toast.error("Vendor Name Required");

      return;
    }

    try {

      if (editId) {

        await updateVendor(editId, form);

        toast.success("Vendor Updated Successfully");

      } else {

        await saveVendor(form);

        toast.success("Vendor Saved Successfully");
      }

      resetForm();

      loadVendors();

    } catch (err) {

      console.error(err);

      toast.error("Error saving vendor");
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
      stateCode: vendor.stateCode || "",
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

      toast.success("Vendor Deleted Successfully");

      loadVendors();

    } catch (err) {

      console.error(err);

      toast.error("Error deleting vendor");
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

        <div className="table-scroll">

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

        </div>
      )}

    </div>
  );
}

export default Vendor;