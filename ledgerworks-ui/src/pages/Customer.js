import { useEffect, useState } from "react";

import {
  getCustomers,
  saveCustomer,
  updateCustomer,
  deleteCustomer
} from "../api/api";

function Customer() {

  const [customers, setCustomers] =
    useState([]);

  const [form, setForm] = useState({

    name: "",

    gstNumber: "",

    state: "",

    phone: "",

    email: "",

    address: ""
  });

  const [editId, setEditId] =
    useState(null);

  // ================= LOAD =================

  const loadCustomers = async () => {

    try {

      const res =
        await getCustomers();

      setCustomers(res || []);

    } catch (err) {

      console.error(err);

      alert(
        "Failed to load customers"
      );
    }
  };

  useEffect(() => {
    loadCustomers();
  }, []);

  // ================= CHANGE =================

  const handleChange = (e) => {

    setForm({

      ...form,

      [e.target.name]:
        e.target.value
    });
  };

  // ================= RESET =================

  const resetForm = () => {

    setForm({

      name: "",

      gstNumber: "",

      state: "",

      phone: "",

      email: "",

      address: ""
    });

    setEditId(null);
  };

  // ================= SAVE =================

  const save = async () => {

    try {

      if (!form.name) {

        alert(
          "Customer Name required"
        );

        return;
      }

      if (editId) {

        await updateCustomer(
          editId,
          form
        );

        alert(
          "Customer Updated"
        );

      } else {

        await saveCustomer(form);

        alert(
          "Customer Saved"
        );
      }

      resetForm();

      loadCustomers();

    } catch (err) {

      console.error(err);

      alert(
        "Error saving customer"
      );
    }
  };

  // ================= EDIT =================

  const edit = (c) => {

    setEditId(c.id);

    setForm({

      name:
        c.name || "",

      gstNumber:
        c.gstNumber || "",

      state:
        c.state || "",

      phone:
        c.phone || "",

      email:
        c.email || "",

      address:
        c.address || ""
    });
  };

  // ================= DELETE =================

  const remove = async (id) => {

    if (
      !window.confirm(
        "Delete customer?"
      )
    ) return;

    try {

      await deleteCustomer(id);

      loadCustomers();

    } catch (err) {

      console.error(err);

      alert(
        "Delete failed"
      );
    }
  };

  return (

    <div style={{ padding: "20px" }}>

      <h2>Customer Master</h2>

      <div
        style={{
          display: "grid",
          gap: "10px",
          width: "400px"
        }}
      >

        <input
          name="name"
          placeholder="Customer Name"
          value={form.name}
          onChange={handleChange}
        />

        <input
          name="gstNumber"
          placeholder="GST Number"
          value={form.gstNumber}
          onChange={handleChange}
        />

        <input
          name="state"
          placeholder="State"
          value={form.state}
          onChange={handleChange}
        />

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

        <textarea
          name="address"
          placeholder="Address"
          value={form.address}
          onChange={handleChange}
        />

        <div>

          <button onClick={save}>
            {editId
              ? "Update"
              : "Save"}
          </button>

          <button
            onClick={resetForm}
            style={{
              marginLeft: "10px"
            }}
          >
            New
          </button>

        </div>

      </div>

      <br />

      <table
        border="1"
        cellPadding="10"
        width="100%"
      >

        <thead>

          <tr>
            <th>Name</th>
            <th>GST</th>
            <th>State</th>
            <th>Phone</th>
            <th>Action</th>
          </tr>

        </thead>

        <tbody>

          {customers.length > 0 ? (

            customers.map((c) => (

              <tr key={c.id}>

                <td>{c.name}</td>

                <td>{c.gstNumber}</td>

                <td>{c.state}</td>

                <td>{c.phone}</td>

                <td>

                  <button
                    onClick={() =>
                      edit(c)
                    }
                  >
                    Edit
                  </button>

                  <button
                    onClick={() =>
                      remove(c.id)
                    }
                  >
                    Delete
                  </button>

                </td>

              </tr>

            ))

          ) : (

            <tr>
              <td colSpan="5">
                No Customers Found
              </td>
            </tr>

          )}

        </tbody>

      </table>

    </div>
  );
}

export default Customer;