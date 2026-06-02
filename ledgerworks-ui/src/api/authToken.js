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

function setupFetchAuth() {
  if (fetchPatched || typeof window === "undefined" || !window.fetch) return;
  fetchPatched = true;

  const originalFetch = window.fetch.bind(window);

  window.fetch = (input, init = {}) => {
    const token = getToken();
    const url =
      typeof input === "string" ? input : (input && input.url) || "";

    // Only attach the token to our own API calls.
    if (token && url.includes("/api/")) {
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
