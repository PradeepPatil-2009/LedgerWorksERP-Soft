import React, { useEffect, useState } from "react";
import API from "../api/api";
import { getRole } from "../api/authToken";
import { useToast } from "../components/Toast";
import { useTableControls } from "../components/useTableControls";
import Pagination from "../components/Pagination";

// =====================================================
// Number Series Management (ADMIN only).
// Lists each document type with an editable prefix and
// current number, and lets an admin save changes.
// =====================================================

export default function NumberSeriesPage() {

  const toast = useToast();

  const isAdmin = (getRole() || "USER") === "ADMIN";

  const [series, setSeries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [savingType, setSavingType] = useState(null);

  const {
    query,
    setQuery,
    page,
    setPage,
    totalPages,
    pageItems,
    total,
  } = useTableControls(series, {
    searchKeys: ["documentType", "prefix"],
    pageSize: 10,
  });

  useEffect(() => {
    loadSeries();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const loadSeries = async () => {
    setLoading(true);
    try {
      const res = await API.get("/number-series");
      setSeries(res.data || []);
    } catch (err) {
      console.error(err);
      toast.error("Failed to load number series");
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (documentType, field, value) => {
    setSeries((prev) =>
      prev.map((row) =>
        row.documentType === documentType
          ? { ...row, [field]: value }
          : row
      )
    );
  };

  const handleSave = async (row) => {

    if (!isAdmin) {
      toast.error("Only an admin can update number series");
      return;
    }

    setSavingType(row.documentType);

    try {
      const payload = {
        documentType: row.documentType,
        prefix: row.prefix || "",
        currentNumber: Number(row.currentNumber) || 0,
        padding: Number(row.padding) > 0 ? Number(row.padding) : 4,
      };

      await API.put("/number-series", payload);

      toast.success(`Saved series for ${row.documentType}`);
      loadSeries();
    } catch (err) {
      console.error(err);
      toast.error("Failed to save number series");
    } finally {
      setSavingType(null);
    }
  };

  const preview = (row) => {
    const padding = Number(row.padding) > 0 ? Number(row.padding) : 4;
    const next = (Number(row.currentNumber) || 0) + 1;
    return `${row.prefix || ""}${String(next).padStart(padding, "0")}`;
  };

  if (loading) return <h3>Loading...</h3>;

  return (
    <div>
      <h2>Number Series</h2>

      {!isAdmin && (
        <p style={{ color: "#b00" }}>
          You are viewing in read-only mode. Only an admin can edit number
          series.
        </p>
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
              <th>Document Type</th>
              <th>Prefix</th>
              <th>Current Number</th>
              <th>Padding</th>
              <th>Next (preview)</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {pageItems.length > 0 ? (
              pageItems.map((row) => (
                <tr key={row.documentType}>
                  <td>{row.documentType}</td>
                  <td>
                    <input
                      type="text"
                      value={row.prefix || ""}
                      disabled={!isAdmin}
                      onChange={(e) =>
                        handleChange(row.documentType, "prefix", e.target.value)
                      }
                    />
                  </td>
                  <td>
                    <input
                      type="number"
                      value={row.currentNumber}
                      disabled={!isAdmin}
                      onChange={(e) =>
                        handleChange(
                          row.documentType,
                          "currentNumber",
                          e.target.value
                        )
                      }
                    />
                  </td>
                  <td>
                    <input
                      type="number"
                      style={{ width: "60px" }}
                      value={row.padding}
                      disabled={!isAdmin}
                      onChange={(e) =>
                        handleChange(
                          row.documentType,
                          "padding",
                          e.target.value
                        )
                      }
                    />
                  </td>
                  <td>{preview(row)}</td>
                  <td>
                    <button
                      onClick={() => handleSave(row)}
                      disabled={!isAdmin || savingType === row.documentType}
                    >
                      {savingType === row.documentType ? "Saving..." : "Save"}
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="6">No number series found</td>
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
