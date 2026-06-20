// Centralised auth-session handling.
//
// The JWT now lives in an HttpOnly `access_token` cookie that JavaScript cannot
// read. We therefore no longer store or attach the token anywhere. Instead we:
//   (1) keep only the user's identity (username + role) in localStorage so the
//       UI can render it and gate routes, and
//   (2) make sure every request carries cookies — axios via
//       `withCredentials: true`, and raw `fetch` via `credentials: "include"`.

import axios from "axios";

export const USERNAME_KEY = "username";
export const ROLE_KEY = "role";

// The JWT is an HttpOnly cookie now; nothing in JS can read it. Kept as a
// no-op so any stray import does not break, but nothing relies on it for auth.
export function getToken() {
  return null;
}

export function getRole() {
  return localStorage.getItem(ROLE_KEY);
}

export function getUsername() {
  return localStorage.getItem(USERNAME_KEY);
}

// Persist the identity and make sure axios always sends cookies.
export function applyAuthSession({ username, role }) {
  if (username) localStorage.setItem(USERNAME_KEY, username);
  if (role) localStorage.setItem(ROLE_KEY, role);
  axios.defaults.withCredentials = true;
}

export function clearAuthSession() {
  localStorage.removeItem(USERNAME_KEY);
  localStorage.removeItem(ROLE_KEY);
}

let fetchPatched = false;

// The origin of our backend API, computed once. Anything resolving to a
// different origin is treated as third-party and never receives our cookies,
// even if its URL happens to contain the substring "/api/".
function getApiOrigin() {
  if (typeof window === "undefined") return null;
  const apiUrl = process.env.REACT_APP_API_URL || "http://localhost:8080/api";
  try {
    return new URL(apiUrl, window.location.origin).origin;
  } catch (e) {
    return null;
  }
}

function setupFetchAuth() {
  if (fetchPatched || typeof window === "undefined" || !window.fetch) return;
  fetchPatched = true;

  const originalFetch = window.fetch.bind(window);
  const apiOrigin = getApiOrigin();
  const pageOrigin = window.location.origin;

  window.fetch = (input, init = {}) => {
    const url =
      typeof input === "string" ? input : (input && input.url) || "";

    // Resolve the request URL against the page origin so we can compare
    // origins. Only send cookies when the request targets our API origin (or
    // the page's own origin) — never a third-party URL that merely contains
    // "/api/".
    let sameTarget = false;
    try {
      const requestOrigin = new URL(url, window.location.origin).origin;
      sameTarget =
        requestOrigin === apiOrigin || requestOrigin === pageOrigin;
    } catch (e) {
      sameTarget = false;
    }

    if (sameTarget) {
      init = { ...init, credentials: "include" };
    }

    return originalFetch(input, init);
  };
}

// Call once at app start: patch fetch to send cookies on same-origin/API
// requests and make axios send cookies by default.
export function bootstrapAuth() {
  axios.defaults.withCredentials = true;
  setupFetchAuth();
}
