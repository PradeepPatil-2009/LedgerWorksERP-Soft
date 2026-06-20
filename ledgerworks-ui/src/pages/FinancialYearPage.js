import React, { useEffect, useState } from "react";
import API from "../api/api";
import { getRole } from "../api/authToken";
import { useToast } from "../components/Toast";
import { useTableControls } from "../components/useTableControls";
import Pagination from "../components/Pagination";

// =====================================================
// Financial Year Management (ADMIN only).
// Lists financial years and lets an admin create a new
// one, activate one (exclusive), and lock one.
// =====================================================

export default function FinancialYearPage() {

  const toast = useToast();

  const isAdmin = (getRole() || "USER") === "ADMIN";

  const [years, setYears] = useState([]);
  const [loading, setLoading] = useState(true);

  const {
    query,
    setQuery,
    page,
    setPage,
    totalPages,
    pageItems,
    total,
  } = useTableControls(years, {
    searchKeys: ["name", "startDate", "endDate"],
    pageSize: 10,
  });

  const [form, setForm] = useState({
    name: "",
    startDate: "",
    endDate: "",
  });

  useEffect(() => {
    loadYears();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const loadYears = async () => {
    setLoading(true);
    try {
      const res = await API.get("/financial-years");
      setYears(res.data || []);
    } catch (err) {
      console.error(err);
      toast.error("Failed to load financial years");
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const resetForm = () => {
    setForm({ name: "", startDate: "", endDate: "" });
  };

  const handleCreate = async () => {

    if (!isAdmin) {
      toast.error("Only an admin can create a financial year");
      return;
    }

    if (!form.name || !form.startDate || !form.endDate) {
      toast.error("Name, start date and end date are required");
      return;
    }

    try {
      await API.post("/financial-years", {
        name: form.name.trim(),
        startDate: form.startDate,
        endDate: form.endDate,
      });

      toast.success("Financial year created");
      resetForm();
      loadYears();
    } catch (err) {
      console.error(err);
      toast.error(
        err?.response?.data?.message || "Failed to create financial year"
      );
    }
  };

  const handleActivate = async (id) => {

    if (!isAdmin) {
      toast.error("Only an admin can activate a financial year");
      return;
    }

    try {
      await API.put(`/financial-years/${id}/activate`);
      toast.success("Financial year activated");
      loadYears();
    } catch (err) {
      console.error(err);
      toast.error("Failed to activate financial year");
    }
  };

  const handleLock = async (id) => {

    if (!isAdmin) {
      toast.error("Only an admin can lock a financial year");
      return;
    }

    if (!window.confirm("Lock this financial year? This cannot be undone.")) {
      return;
    }

    try {
      await API.put(`/financial-years/${id}/lock`);
      toast.success("Financial year locked");
      loadYears();
    } catch (err) {
      console.error(err);
      toast.error("Failed to lock financial year");
    }
  };

  if (loading) return <h3>Loading...</h3>;

  return (
    <div>
      <h2>Financial Years</h2>

      {!isAdmin && (
        <p style={{ color: "#b00" }}>
          You are viewing in read-only mode. Only an admin can manage financial
          years.
        </p>
      )}

      {isAdmin && (
        <div style={{ marginBottom: "15px" }}>
          <input
            type="text"
            name="name"
            placeholder="Name e.g. 2025-2026"
            value={form.name}
            onChange={handleChange}
          />
          <input
            type="date"
            name="startDate"
            value={form.startDate}
            onChange={handleChange}
          />
          <input
            type="date"
            name="endDate"
            value={form.endDate}
            onChange={handleChange}
          />
          <button onClick={handleCreate}>Create</button>
        </div>
      )}

      <input
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="Search..."
        style={{ marginBottom: "10px" }}
      />

      <div style={{ overflowX: "auto" }}>
        <table border="1" cellPadding="5">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Start Date</th>
              <th>End Date</th>
              <th>Active</th>
              <th>Locked</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {pageItems.length > 0 ? (
              pageItems.map((y) => (
                <tr key={y.id}>
                  <td>{y.id}</td>
                  <td>{y.name}</td>
                  <td>{y.startDate}</td>
                  <td>{y.endDate}</td>
                  <td>{y.active ? "Yes" : "No"}</td>
                  <td>{y.locked ? "Yes" : "No"}</td>
                  <td>
                    <button
                      onClick={() => handleActivate(y.id)}
                      disabled={!isAdmin || y.active}
                    >
                      Activate
                    </button>{" "}
                    <button
                      onClick={() => handleLock(y.id)}
                      disabled={!isAdmin || y.locked}
                    >
                      Lock
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="7">No financial years found</td>
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
