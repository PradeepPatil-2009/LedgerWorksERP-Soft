import { useEffect, useState } from "react";

import API from "../api/api";
import { getRole } from "../api/authToken";
import { useToast } from "../components/Toast";

export default function CompanySettingsPage() {

  const toast = useToast();

  const isAdmin = (getRole() || "").toUpperCase() === "ADMIN";

  const [loading, setLoading] = useState(false);

  const [saving, setSaving] = useState(false);

  const [form, setForm] = useState({
    companyName: "",
    gstNumber: "",
    panNumber: "",
    address: "",
    bankName: "",
    accountNumber: "",
    ifsc: "",
    branch: "",
    footerMessage: "",
    logoBase64: "",
  });

  // ================= LOAD =================

  useEffect(() => {
    loadSettings();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const loadSettings = async () => {

    setLoading(true);

    try {

      const res = await API.get("/company-settings");

      const data = res.data || {};

      setForm({
        companyName: data.companyName || "",
        gstNumber: data.gstNumber || "",
        panNumber: data.panNumber || "",
        address: data.address || "",
        bankName: data.bankName || "",
        accountNumber: data.accountNumber || "",
        ifsc: data.ifsc || "",
        branch: data.branch || "",
        footerMessage: data.footerMessage || "",
        logoBase64: data.logoBase64 || "",
      });

    } catch (err) {

      console.error(err);

      toast.error("Error loading company settings");

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

  // ================= LOGO -> BASE64 =================

  const handleLogo = (e) => {

    const file = e.target.files && e.target.files[0];

    if (!file) {

      return;
    }

    const reader = new FileReader();

    reader.onloadend = () => {

      setForm((prev) => ({
        ...prev,
        logoBase64: reader.result,
      }));
    };

    reader.readAsDataURL(file);
  };

  const clearLogo = () => {

    setForm((prev) => ({
      ...prev,
      logoBase64: "",
    }));
  };

  // ================= SAVE =================

  const handleSubmit = async () => {

    if (!isAdmin) {

      toast.error("Only ADMIN can save company settings");

      return;
    }

    if (!form.companyName) {

      toast.error("Company Name Required");

      return;
    }

    setSaving(true);

    try {

      await API.put("/company-settings", form);

      toast.success("Company Settings Saved Successfully");

      loadSettings();

    } catch (err) {

      console.error(err);

      toast.error("Error saving company settings");

    } finally {

      setSaving(false);
    }
  };

  return (

    <div>

      <h2>Company Settings</h2>

      {!isAdmin && (
        <p style={{ color: "#a00" }}>
          You have view-only access. ADMIN role is required to edit.
        </p>
      )}

      {loading ? (

        <p>Loading...</p>

      ) : (

        <>

          {/* ================= LOGO ================= */}

          <div style={{ marginBottom: "10px" }}>

            {form.logoBase64 ? (

              <div>

                <img
                  src={form.logoBase64}
                  alt="Company Logo"
                  style={{
                    maxWidth: "200px",
                    maxHeight: "120px",
                    display: "block",
                    marginBottom: "6px",
                    border: "1px solid #ccc",
                  }}
                />

                {isAdmin && (
                  <button type="button" onClick={clearLogo}>
                    Remove Logo
                  </button>
                )}

              </div>

            ) : (

              <p>No logo uploaded</p>

            )}

            {isAdmin && (
              <input
                type="file"
                accept="image/*"
                onChange={handleLogo}
              />
            )}

          </div>

          {/* ================= FORM ================= */}

          <div style={{ marginBottom: "10px" }}>

            <input
              name="companyName"
              placeholder="Company Name"
              value={form.companyName}
              onChange={handleChange}
              disabled={!isAdmin}
            />

            <input
              name="gstNumber"
              placeholder="GST Number"
              value={form.gstNumber}
              onChange={handleChange}
              disabled={!isAdmin}
            />

          </div>

          <div style={{ marginBottom: "10px" }}>

            <input
              name="panNumber"
              placeholder="PAN Number"
              value={form.panNumber}
              onChange={handleChange}
              disabled={!isAdmin}
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
              disabled={!isAdmin}
            />

          </div>

          <h3>Bank Details</h3>

          <div style={{ marginBottom: "10px" }}>

            <input
              name="bankName"
              placeholder="Bank Name"
              value={form.bankName}
              onChange={handleChange}
              disabled={!isAdmin}
            />

            <input
              name="accountNumber"
              placeholder="Account Number"
              value={form.accountNumber}
              onChange={handleChange}
              disabled={!isAdmin}
            />

          </div>

          <div style={{ marginBottom: "10px" }}>

            <input
              name="ifsc"
              placeholder="IFSC"
              value={form.ifsc}
              onChange={handleChange}
              disabled={!isAdmin}
            />

            <input
              name="branch"
              placeholder="Branch"
              value={form.branch}
              onChange={handleChange}
              disabled={!isAdmin}
            />

          </div>

          <h3>Invoice Footer</h3>

          <div style={{ marginBottom: "10px" }}>

            <textarea
              name="footerMessage"
              placeholder="Footer Message"
              value={form.footerMessage}
              onChange={handleChange}
              rows="3"
              cols="60"
              disabled={!isAdmin}
            />

          </div>

          {isAdmin && (
            <button onClick={handleSubmit} disabled={saving}>
              {saving ? "Saving..." : "Save Settings"}
            </button>
          )}

        </>
      )}

    </div>
  );
}
