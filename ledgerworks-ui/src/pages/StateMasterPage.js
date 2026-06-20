import { useEffect, useState } from "react";

import API from "../api/api";
import { useToast } from "../components/Toast";

export default function StateMasterPage() {

  const toast = useToast();

  const [states, setStates] = useState([]);

  const [loading, setLoading] = useState(false);

  const [editId, setEditId] = useState(null);

  const [form, setForm] = useState({
    stateName: "",
    stateCode: "",
    active: true,
  });

  // ================= LOAD =================

  useEffect(() => {
    loadStates();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const loadStates = async () => {

    setLoading(true);

    try {

      const res = await API.get("/states");

      setStates(Array.isArray(res.data) ? res.data : []);

    } catch (err) {

      console.error(err);

      toast.error("Error loading states");

    } finally {

      setLoading(false);
    }
  };

  // ================= CHANGE =================

  const handleChange = (e) => {

    const { name, value, type, checked } = e.target;

    setForm({
      ...form,
      [name]: type === "checkbox" ? checked : value,
    });
  };

  // ================= RESET =================

  const resetForm = () => {

    setForm({
      stateName: "",
      stateCode: "",
      active: true,
    });

    setEditId(null);
  };

  // ================= SAVE =================

  const handleSubmit = async () => {

    if (!form.stateName) {

      toast.error("State Name Required");

      return;
    }

    if (!form.stateCode) {

      toast.error("State Code Required");

      return;
    }

    try {

      if (editId) {

        await API.put(`/states/${editId}`, form);

        toast.success("State Updated Successfully");

      } else {

        await API.post("/states", form);

        toast.success("State Saved Successfully");
      }

      resetForm();

      loadStates();

    } catch (err) {

      console.error(err);

      toast.error("Error saving state");
    }
  };

  // ================= EDIT =================

  const handleEdit = (state) => {

    setForm({
      stateName: state.stateName || "",
      stateCode: state.stateCode || "",
      active: state.active !== false,
    });

    setEditId(state.id);
  };

  // ================= DELETE =================

  const handleDelete = async (id) => {

    if (!window.confirm("Delete this state?")) {

      return;
    }

    try {

      await API.delete(`/states/${id}`);

      toast.success("State Deleted Successfully");

      loadStates();

    } catch (err) {

      console.error(err);

      toast.error("Error deleting state");
    }
  };

  return (

    <div>

      <h2>State Master</h2>

      {/* ================= FORM ================= */}

      <div style={{ marginBottom: "10px" }}>

        <input
          name="stateCode"
          placeholder="GST Code (e.g. 27)"
          value={form.stateCode}
          onChange={handleChange}
        />

        <input
          name="stateName"
          placeholder="State Name"
          value={form.stateName}
          onChange={handleChange}
        />

        <label style={{ marginLeft: "8px" }}>
          <input
            type="checkbox"
            name="active"
            checked={form.active}
            onChange={handleChange}
          />
          Active
        </label>

      </div>

      <button onClick={handleSubmit}>
        {editId ? "Update State" : "Save State"}
      </button>

      <button onClick={resetForm}>
        New
      </button>

      <hr />

      {/* ================= TABLE ================= */}

      {loading ? (

        <p>Loading...</p>

      ) : (

        <div style={{ overflowX: "auto" }}>

          <table border="1" cellPadding="5">

            <thead>

              <tr>
                <th>ID</th>
                <th>GST Code</th>
                <th>State Name</th>
                <th>Active</th>
                <th>Action</th>
              </tr>

            </thead>

            <tbody>

              {states.length > 0 ? (

                states.map((s) => (

                  <tr key={s.id}>

                    <td>{s.id}</td>

                    <td>{s.stateCode}</td>

                    <td>{s.stateName}</td>

                    <td>{s.active ? "Yes" : "No"}</td>

                    <td>

                      <button onClick={() => handleEdit(s)}>
                        Edit
                      </button>

                      <button onClick={() => handleDelete(s.id)}>
                        Delete
                      </button>

                    </td>

                  </tr>
                ))

              ) : (

                <tr>

                  <td colSpan="5">
                    No States Found
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
