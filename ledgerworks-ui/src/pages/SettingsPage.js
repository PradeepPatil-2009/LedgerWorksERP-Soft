import { Link } from "react-router-dom";

import { getRole } from "../api/authToken";

// Admin landing page — a hub of links to the various configuration screens.
const SETTINGS_CARDS = [
  {
    to: "/company-settings",
    title: "Company Settings",
    desc: "Company profile, GST/PAN, bank details and invoice footer.",
  },
  {
    to: "/states",
    title: "State Master",
    desc: "Manage states and their GST state codes.",
  },
  {
    to: "/number-series",
    title: "Number Series",
    desc: "Document numbering for invoices, vouchers and more.",
  },
  {
    to: "/financial-years",
    title: "Financial Years",
    desc: "Define and activate accounting periods.",
  },
  {
    to: "/gst-rates-settings",
    title: "GST Rates",
    desc: "Maintain the configured GST rate slabs.",
  },
  {
    to: "/audit-logs",
    title: "Audit Log",
    desc: "Review user activity across the system.",
  },
];

export default function SettingsPage() {

  const isAdmin = (getRole() || "").toUpperCase() === "ADMIN";

  if (!isAdmin) {

    return (

      <div>

        <h2>Settings</h2>

        <p style={{ color: "#a00" }}>
          ADMIN role is required to access settings.
        </p>

      </div>
    );
  }

  return (

    <div>

      <h2>Settings</h2>

      <p style={{ color: "var(--lw-muted)", marginTop: 0 }}>
        Configure your organisation and system preferences.
      </p>

      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(auto-fill, minmax(240px, 1fr))",
          gap: "16px",
          marginTop: "16px",
        }}
      >

        {SETTINGS_CARDS.map((c) => (

          <Link
            key={c.to}
            to={c.to}
            style={{
              textDecoration: "none",
              color: "inherit",
              border: "1px solid var(--lw-border)",
              borderRadius: "var(--lw-radius)",
              background: "var(--lw-surface)",
              padding: "18px",
              display: "block",
              boxShadow: "var(--lw-shadow)",
            }}
          >

            <h3 style={{ margin: "0 0 6px" }}>{c.title}</h3>

            <p
              style={{
                margin: 0,
                fontSize: "14px",
                color: "var(--lw-muted)",
              }}
            >
              {c.desc}
            </p>

          </Link>
        ))}

      </div>

    </div>
  );
}
