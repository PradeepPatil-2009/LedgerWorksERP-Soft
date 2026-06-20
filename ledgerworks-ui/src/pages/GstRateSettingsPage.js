import { useEffect, useState } from "react";

import API from "../api/api";
import { getRole } from "../api/authToken";
import { useToast } from "../components/Toast";

// GST rate master — ADMIN can add/edit/delete the configured slabs. Other roles
// may view (the backend exposes GET to any authenticated user).
export default function GstRateSettingsPage() {

  const toast = useToast();

  const isAdmin = (getRole() || "").toUpperCase() === "ADMIN";

  const [rates, setRates] = useState([]);

  const [loading, setLoading] = useState(false);

  const [saving, setSaving] = useState(false);

  const [form, setForm] = useState({
    id: null,
    rate: "",
    label: "",
    active: true,
  });

  // ================= LOAD =================

  useEffect(() => {
    loadRates();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const loadRates = async () => {

    setLoading(true);

    try {

      const res = await API.get("/gst-rates");

      setRates(res.data || []);

    } catch (err) {

      console.error(err);

      toast.error("Error loading GST rates");

    } finally {

      setLoading(false);
    }
  };

  // ================= FORM =================

  const handleChange = (e) => {

    const { name, value, type, checked } = e.target;

    setForm({
      ...form,
      [name]: type === "checkbox" ? checked : value,
    });
  };

  const resetForm = () => {

    setForm({
      id: null,
      rate: "",
      label: "",
      active: true,
    });
  };

  const editRate = (r) => {

    setForm({
      id: r.id,
      rate: r.rate != null ? String(r.rate) : "",
      label: r.label || "",
      active: r.active !== false,
    });
  };

  // ================= SAVE =================

  const handleSubmit = async () => {

    if (!isAdmin) {

      toast.error("Only ADMIN can manage GST rates");

      return;
    }

    if (form.rate === "" || isNaN(Number(form.rate))) {

      toast.error("A valid rate is required");

      return;
    }

    if (!form.label) {

      toast.error("Label is required");

      return;
    }

    const payload = {
      rate: Number(form.rate),
      label: form.label,
      active: form.active,
    };

    setSaving(true);

    try {

      if (form.id) {

        await API.put(`/gst-rates/${form.id}`, payload);

        toast.success("GST rate updated");

      } else {

        await API.post("/gst-rates", payload);

        toast.success("GST rate added");
      }

      resetForm();

      loadRates();

    } catch (err) {

      console.error(err);

      toast.error("Error saving GST rate");

    } finally {

      setSaving(false);
    }
  };

  // ================= DELETE =================

  const handleDelete = async (id) => {

    if (!isAdmin) {

      toast.error("Only ADMIN can manage GST rates");

      return;
    }

    if (!window.confirm("Delete this GST rate?")) {

      return;
    }

    try {

      await API.delete(`/gst-rates/${id}`);

      toast.success("GST rate deleted");

      if (form.id === id) {
        resetForm();
      }

      loadRates();

    } catch (err) {

      console.error(err);

      toast.error("Error deleting GST rate");
    }
  };

  return (

    <div>

      <h2>GST Rate Settings</h2>

      {!isAdmin && (
        <p style={{ color: "#a00" }}>
          You have view-only access. ADMIN role is required to edit.
        </p>
      )}

      {/* ================= FORM ================= */}

      {isAdmin && (

        <div style={{ marginBottom: "12px" }}>

          <input
            type="number"
            name="rate"
            placeholder="Rate %"
            value={form.rate}
            onChange={handleChange}
          />

          <input
            type="text"
            name="label"
            placeholder="Label (e.g. GST 18%)"
            value={form.label}
            onChange={handleChange}
          />

          <label style={{ marginRight: "10px" }}>
            <input
              type="checkbox"
              name="active"
              checked={form.active}
              onChange={handleChange}
            />{" "}
            Active
          </label>

          <button
            className="btn-primary"
            onClick={handleSubmit}
            disabled={saving}
          >
            {saving ? "Saving..." : form.id ? "Update Rate" : "Add Rate"}
          </button>

          {form.id && (
            <button onClick={resetForm}>
              Cancel
            </button>
          )}

        </div>
      )}

      <hr />

      {/* ================= TABLE ================= */}

      {loading ? (

        <p>Loading...</p>

      ) : (

        <div className="table-scroll">

          <table>

            <thead>

              <tr>
                <th>ID</th>
                <th>Rate %</th>
                <th>Label</th>
                <th>Active</th>
                {isAdmin && <th>Actions</th>}
              </tr>

            </thead>

            <tbody>

              {rates.length > 0 ? (

                rates.map((r) => (

                  <tr key={r.id}>

                    <td>{r.id}</td>
                    <td>{r.rate}</td>
                    <td>{r.label}</td>
                    <td>{r.active !== false ? "Yes" : "No"}</td>

                    {isAdmin && (
                      <td>
                        <button
                          className="btn-sm"
                          onClick={() => editRate(r)}
                        >
                          Edit
                        </button>
                        <button
                          className="btn-danger btn-sm"
                          onClick={() => handleDelete(r.id)}
                        >
                          Delete
                        </button>
                      </td>
                    )}

                  </tr>
                ))

              ) : (

                <tr>
                  <td colSpan={isAdmin ? "5" : "4"}>
                    No GST rates configured
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
