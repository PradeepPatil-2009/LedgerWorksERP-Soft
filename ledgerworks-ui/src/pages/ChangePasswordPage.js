import { useState } from "react";

import API from "../api/api";
import { useToast } from "../components/Toast";

// Client-side mirror of the server password policy. The server is the source
// of truth (returns 400 on violation); this is just an early, friendly guard.
const PASSWORD_HINT = "At least 8 characters, including a letter and a number.";

function isValidPassword(pw) {
  return /^(?=.*[A-Za-z])(?=.*\d).{8,}$/.test(pw || "");
}

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

    if (!isValidPassword(form.newPassword)) {

      toast.error(
        "Password must be at least 8 characters and include a letter and a number."
      );

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
      const serverMessage =
        typeof err?.response?.data === "string"
          ? err.response.data
          : err?.response?.data?.message;

      if (status === 400) {

        // The server is the source of truth: it returns either the
        // wrong-current-password message or the password-policy message.
        toast.error(serverMessage || "Current password is incorrect");

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

          <div
            style={{
              fontSize: "12px",
              color: "var(--lw-muted)",
              marginTop: "4px",
            }}
          >
            {PASSWORD_HINT}
          </div>

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
