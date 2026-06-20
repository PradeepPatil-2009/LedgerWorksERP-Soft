import { useState } from "react";

import API from "../api/api";
import { useToast } from "../components/Toast";

// Change password — available to ANY authenticated user. The username is taken
// from the JWT server-side, so we only send {oldPassword, newPassword}.
export default function ChangePasswordPage() {

  const toast = useToast();

  const [saving, setSaving] = useState(false);

  const [form, setForm] = useState({
    oldPassword: "",
    newPassword: "",
    confirmPassword: "",
  });

  const handleChange = (e) => {

    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const resetForm = () => {

    setForm({
      oldPassword: "",
      newPassword: "",
      confirmPassword: "",
    });
  };

  const handleSubmit = async () => {

    if (!form.oldPassword || !form.newPassword) {

      toast.error("Current and new password are required");

      return;
    }

    if (form.newPassword !== form.confirmPassword) {

      toast.error("New password and confirmation do not match");

      return;
    }

    setSaving(true);

    try {

      await API.put("/account/password", {
        oldPassword: form.oldPassword,
        newPassword: form.newPassword,
      });

      toast.success("Password changed successfully");

      resetForm();

    } catch (err) {

      console.error(err);

      const status = err?.response?.status;

      if (status === 400) {

        toast.error("Current password is incorrect");

      } else {

        toast.error("Error changing password");
      }

    } finally {

      setSaving(false);
    }
  };

  return (

    <div>

      <h2>Change Password</h2>

      <p style={{ color: "var(--lw-muted)", marginTop: 0 }}>
        Update the password for your account.
      </p>

      <div style={{ maxWidth: "360px" }}>

        <div style={{ marginBottom: "10px" }}>

          <label
            style={{
              display: "block",
              fontSize: "13px",
              fontWeight: 600,
              color: "var(--lw-muted)",
              marginBottom: "4px",
            }}
          >
            Current Password
          </label>

          <input
            type="password"
            name="oldPassword"
            placeholder="Current Password"
            value={form.oldPassword}
            onChange={handleChange}
            autoComplete="current-password"
            style={{ width: "100%" }}
          />

        </div>

        <div style={{ marginBottom: "10px" }}>

          <label
            style={{
              display: "block",
              fontSize: "13px",
              fontWeight: 600,
              color: "var(--lw-muted)",
              marginBottom: "4px",
            }}
          >
            New Password
          </label>

          <input
            type="password"
            name="newPassword"
            placeholder="New Password"
            value={form.newPassword}
            onChange={handleChange}
            autoComplete="new-password"
            style={{ width: "100%" }}
          />

        </div>

        <div style={{ marginBottom: "14px" }}>

          <label
            style={{
              display: "block",
              fontSize: "13px",
              fontWeight: 600,
              color: "var(--lw-muted)",
              marginBottom: "4px",
            }}
          >
            Confirm New Password
          </label>

          <input
            type="password"
            name="confirmPassword"
            placeholder="Confirm New Password"
            value={form.confirmPassword}
            onChange={handleChange}
            autoComplete="new-password"
            style={{ width: "100%" }}
          />

        </div>

        <button
          className="btn-primary"
          onClick={handleSubmit}
          disabled={saving}
        >
          {saving ? "Saving..." : "Change Password"}
        </button>

      </div>

    </div>
  );
}
