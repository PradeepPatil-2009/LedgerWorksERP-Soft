import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import App from "./App";
import { bootstrapAuth } from "./api/authToken";

// Patch fetch + restore the axios auth header from any stored token so a page
// refresh stays authenticated. Must run before the app makes any request.
bootstrapAuth();

// Apply the saved colour theme before first paint so there is no flash.
try {
  const savedTheme = localStorage.getItem("theme");
  document.documentElement.dataset.theme =
    savedTheme === "dark" ? "dark" : "light";
} catch (e) {
  document.documentElement.dataset.theme = "light";
}

// ❌ DO NOT use StrictMode (causes double API calls)
const root = ReactDOM.createRoot(document.getElementById("root"));

root.render(<App />);
