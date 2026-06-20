// Centralised auth-token handling.
//
// The app talks to the backend three ways: the shared `api.js` axios instance,
// bare `axios` calls, and raw `fetch` calls. To attach the JWT everywhere
// without editing every page, we (1) set a global axios default header and
// (2) patch window.fetch once to inject the bearer token on API requests.

import axios from "axios";

export const TOKEN_KEY = "token";
export const USERNAME_KEY = "username";
export const ROLE_KEY = "role";

export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function getRole() {
  return localStorage.getItem(ROLE_KEY);
}

export function getUsername() {
  return localStorage.getItem(USERNAME_KEY);
}

// Persist the session and make every subsequent request authenticated.
export function applyAuthSession({ token, username, role }) {
  if (token) {
    localStorage.setItem(TOKEN_KEY, token);
    axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
  }
  if (username) localStorage.setItem(USERNAME_KEY, username);
  if (role) localStorage.setItem(ROLE_KEY, role);
}

export function clearAuthSession() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USERNAME_KEY);
  localStorage.removeItem(ROLE_KEY);
  delete axios.defaults.headers.common["Authorization"];
}

let fetchPatched = false;

// The origin of our backend API, computed once. Anything resolving to a
// different origin is treated as third-party and never receives our JWT,
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
    const token = getToken();
    const url =
      typeof input === "string" ? input : (input && input.url) || "";

    // Resolve the request URL against the page origin so we can compare
    // origins. Only attach the token when the request targets our API
    // origin (or the page's own origin) — never a third-party URL that
    // merely contains "/api/".
    let sameTarget = false;
    if (token) {
      try {
        const requestOrigin = new URL(url, window.location.origin).origin;
        sameTarget =
          requestOrigin === apiOrigin || requestOrigin === pageOrigin;
      } catch (e) {
        sameTarget = false;
      }
    }

    if (token && sameTarget) {
      const headers = new Headers(
        init.headers ||
          (typeof input !== "string" ? input.headers : undefined) ||
          {}
      );
      if (!headers.has("Authorization")) {
        headers.set("Authorization", `Bearer ${token}`);
      }
      init = { ...init, headers };
    }

    return originalFetch(input, init);
  };
}

// Call once at app start: patch fetch and restore the axios default header
// from any previously stored token (so a page refresh stays authenticated).
export function bootstrapAuth() {
  setupFetchAuth();
  const token = getToken();
  if (token) {
    axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
  }
}
