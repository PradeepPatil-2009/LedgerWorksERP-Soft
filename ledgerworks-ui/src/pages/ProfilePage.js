import { Link } from "react-router-dom";

import { getRole, getUsername } from "../api/authToken";

// Read-only profile card showing the signed-in user's identity. The username
// and role come from the local auth session (mirrored from the JWT at login).
export default function ProfilePage() {
  const username = getUsername() || "—";
  const role = getRole() || "—";

  return (
    <div>
      <h2>Profile</h2>

      <p style={{ color: "var(--lw-muted)", marginTop: 0 }}>
        Your account details.
      </p>

      <div
        style={{
          maxWidth: "360px",
          border: "1px solid var(--lw-border)",
          borderRadius: "var(--lw-radius)",
          background: "var(--lw-surface-alt)",
          padding: "18px 20px",
        }}
      >
        <div style={{ marginBottom: "14px" }}>
          <div
            style={{
              fontSize: "13px",
              fontWeight: 600,
              color: "var(--lw-muted)",
              marginBottom: "2px",
            }}
          >
            Username
          </div>
          <div style={{ fontSize: "16px", fontWeight: 600 }}>{username}</div>
        </div>

        <div style={{ marginBottom: "18px" }}>
          <div
            style={{
              fontSize: "13px",
              fontWeight: 600,
              color: "var(--lw-muted)",
              marginBottom: "2px",
            }}
          >
            Role
          </div>
          <div>
            <span className="app-role-badge">{role}</span>
          </div>
        </div>

        <Link to="/change-password" className="btn-primary" style={{ textDecoration: "none", display: "inline-block", padding: "9px 16px", borderRadius: "var(--lw-radius)" }}>
          Change Password
        </Link>
      </div>
    </div>
  );
}
