import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import App from "./App";
import { bootstrapAuth } from "./api/authToken";

// Patch fetch + restore the axios auth header from any stored token so a page
// refresh stays authenticated. Must run before the app makes any request.
bootstrapAuth();

// ❌ DO NOT use StrictMode (causes double API calls)
const root = ReactDOM.createRoot(document.getElementById("root"));

root.render(<App />);
