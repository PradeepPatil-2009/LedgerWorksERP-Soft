// Tests for the centralised auth-session handling.
//
// The JWT now lives in an HttpOnly cookie, so applyAuthSession only persists
// the identity (username + role) and makes axios send cookies by default
// (withCredentials). clearAuthSession drops the identity. No network is
// involved.

// axios 1.x ships an ESM entry that jest 27 (bundled with react-scripts 5)
// cannot transform, so we replace it with a tiny CJS mock that exposes only
// the surface authToken.js touches: a mutable defaults object.
jest.mock("axios", () => {
  const headers = { common: {} };
  return {
    __esModule: true,
    default: {
      defaults: { headers },
      create: jest.fn(),
    },
  };
});

import axios from "axios";
import {
  applyAuthSession,
  clearAuthSession,
  getUsername,
  getRole,
  USERNAME_KEY,
  ROLE_KEY,
} from "./authToken";

beforeEach(() => {
  localStorage.clear();
  delete axios.defaults.headers.common["Authorization"];
  axios.defaults.withCredentials = false;
});

afterEach(() => {
  localStorage.clear();
  delete axios.defaults.headers.common["Authorization"];
  axios.defaults.withCredentials = false;
});

describe("applyAuthSession", () => {
  test("stores username/role and makes axios send cookies, without an Authorization header", () => {
    applyAuthSession({ username: "admin", role: "ADMIN" });

    // localStorage holds only the identity — never the token.
    expect(localStorage.getItem(USERNAME_KEY)).toBe("admin");
    expect(localStorage.getItem(ROLE_KEY)).toBe("ADMIN");
    expect(localStorage.getItem("token")).toBeNull();

    // Helper getters read the same values back.
    expect(getUsername()).toBe("admin");
    expect(getRole()).toBe("ADMIN");

    // Cookies ride along on every axios request; no bearer header is set.
    expect(axios.defaults.withCredentials).toBe(true);
    expect(axios.defaults.headers.common["Authorization"]).toBeUndefined();
  });

  test("persists identity even when called repeatedly", () => {
    applyAuthSession({ username: "guest", role: "USER" });

    expect(localStorage.getItem(USERNAME_KEY)).toBe("guest");
    expect(localStorage.getItem(ROLE_KEY)).toBe("USER");
    expect(axios.defaults.withCredentials).toBe(true);
    expect(axios.defaults.headers.common["Authorization"]).toBeUndefined();
  });
});

describe("clearAuthSession", () => {
  test("removes the stored identity", () => {
    applyAuthSession({ username: "admin", role: "ADMIN" });

    // Sanity: it was set first.
    expect(localStorage.getItem(USERNAME_KEY)).toBe("admin");
    expect(localStorage.getItem(ROLE_KEY)).toBe("ADMIN");

    clearAuthSession();

    expect(localStorage.getItem(USERNAME_KEY)).toBeNull();
    expect(localStorage.getItem(ROLE_KEY)).toBeNull();
    expect(getUsername()).toBeNull();
    expect(getRole()).toBeNull();
  });
});
