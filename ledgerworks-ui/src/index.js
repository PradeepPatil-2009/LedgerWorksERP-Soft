import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";

// ❌ DO NOT use StrictMode (causes double API calls)
const root = ReactDOM.createRoot(document.getElementById("root"));

root.render(
  <App />
);