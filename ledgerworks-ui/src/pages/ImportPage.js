import React, { useRef, useState } from "react";
import API from "../api/api";
import { useToast } from "../components/Toast";

// =====================================================
// Data Import utility.
// Choose an entity type, upload an .xlsx / .csv file and
// view a summary of imported / skipped rows.
// =====================================================

const ENTITY_TYPES = [
  { value: "customers", label: "Customers", endpoint: "/import/customers" },
  { value: "vendors", label: "Vendors", endpoint: "/import/vendors" },
  { value: "items", label: "Items", endpoint: "/import/items" },
];

const COLUMN_HINTS = {
  customers: "name, email, phone, address, state, stateCode, gstType, gstNumber",
  vendors: "name, email, phone, address, state, stateCode, gstType, gstNumber",
  items:
    "itemName, itemCode, hsnCode, unit, category, status, purchaseRate, saleRate, currentStock",
};

export default function ImportPage() {

  const toast = useToast();

  const fileInputRef = useRef(null);

  const [entityType, setEntityType] = useState("customers");

  const [file, setFile] = useState(null);

  const [result, setResult] = useState(null);

  const [loading, setLoading] = useState(false);

  // ================= HANDLERS =================

  const handleFileChange = (e) => {
    setFile(e.target.files && e.target.files[0] ? e.target.files[0] : null);
    setResult(null);
  };

  const handleUpload = async () => {

    if (!file) {
      toast.error("Please choose a file to import");
      return;
    }

    const entity = ENTITY_TYPES.find((t) => t.value === entityType);

    const formData = new FormData();
    formData.append("file", file);

    setLoading(true);
    setResult(null);

    try {

      const res = await API.post(entity.endpoint, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      setResult(res.data);

      toast.success(
        `Imported ${res.data?.imported ?? 0}, skipped ${
          res.data?.skipped ?? 0
        }`
      );

    } catch (err) {
      console.error(err);
      toast.error("Import failed");
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setFile(null);
    setResult(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  };

  // ================= UI =================

  return (

    <div style={{ padding: "20px" }}>

      <h2>Data Import</h2>

      <p style={{ color: "#555" }}>
        Upload an Excel (.xlsx) or CSV (.csv) file to bulk import records.
      </p>

      {/* ================= FORM ================= */}

      <div style={{ marginBottom: "12px" }}>

        <label style={{ marginRight: "8px", fontWeight: "bold" }}>
          Entity Type:
        </label>

        <select
          value={entityType}
          onChange={(e) => {
            setEntityType(e.target.value);
            setResult(null);
          }}
          style={{ padding: "6px" }}
        >
          {ENTITY_TYPES.map((t) => (
            <option key={t.value} value={t.value}>
              {t.label}
            </option>
          ))}
        </select>

      </div>

      <div style={{ marginBottom: "12px", color: "#777", fontSize: "13px" }}>
        Expected columns: {COLUMN_HINTS[entityType]}
      </div>

      <div style={{ marginBottom: "12px" }}>

        <input
          ref={fileInputRef}
          type="file"
          accept=".xlsx,.xls,.csv"
          onChange={handleFileChange}
        />

      </div>

      <div style={{ marginBottom: "20px" }}>

        <button
          onClick={handleUpload}
          disabled={loading}
          style={{ marginRight: "8px" }}
        >
          {loading ? "Importing..." : "Import"}
        </button>

        <button onClick={handleReset} disabled={loading}>
          Reset
        </button>

      </div>

      {/* ================= RESULT ================= */}

      {result && (

        <div style={{ marginTop: "10px" }}>

          <h3>Import Summary</h3>

          <p>
            <span style={{ color: "green", fontWeight: "bold" }}>
              Imported: {result.imported}
            </span>
            {"  |  "}
            <span style={{ color: "#b36b00", fontWeight: "bold" }}>
              Skipped: {result.skipped}
            </span>
          </p>

          {result.errors && result.errors.length > 0 && (

            <div style={{ overflowX: "auto" }}>

              <table
                border="1"
                cellPadding="6"
                style={{ borderCollapse: "collapse", marginTop: "8px" }}
              >

                <thead>
                  <tr style={{ background: "#f2f2f2" }}>
                    <th>#</th>
                    <th>Skipped / Error Detail</th>
                  </tr>
                </thead>

                <tbody>
                  {result.errors.map((msg, idx) => (
                    <tr key={idx}>
                      <td>{idx + 1}</td>
                      <td>{msg}</td>
                    </tr>
                  ))}
                </tbody>

              </table>

            </div>
          )}

        </div>
      )}

    </div>
  );
}
