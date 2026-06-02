// Tests for the centralised auth-token handling.
//
// These exercise applyAuthSession / clearAuthSession and assert that the JWT
// lands in localStorage and on axios.defaults.headers.common.Authorization.
// No network is involved.

// axios 1.x ships an ESM entry that jest 27 (bundled with react-scripts 5)
// cannot transform, so we replace it with a tiny CJS mock that exposes only
// the surface authToken.js touches: a mutable defaults.headers.common object.
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
  getToken,
  getUsername,
  getRole,
  TOKEN_KEY,
  USERNAME_KEY,
  ROLE_KEY,
} from "./authToken";

beforeEach(() => {
  localStorage.clear();
  delete axios.defaults.headers.common["Authorization"];
});

afterEach(() => {
  localStorage.clear();
  delete axios.defaults.headers.common["Authorization"];
});

describe("applyAuthSession", () => {
  test("stores token in localStorage and sets the axios Authorization header", () => {
    applyAuthSession({
      token: "jwt-token-123",
      username: "admin",
      role: "ADMIN",
    });

    // localStorage holds the persisted session.
    expect(localStorage.getItem(TOKEN_KEY)).toBe("jwt-token-123");
    expect(localStorage.getItem(USERNAME_KEY)).toBe("admin");
    expect(localStorage.getItem(ROLE_KEY)).toBe("ADMIN");

    // Helper getters read the same values back.
    expect(getToken()).toBe("jwt-token-123");
    expect(getUsername()).toBe("admin");
    expect(getRole()).toBe("ADMIN");

    // axios default header is set so every request is authenticated.
    expect(axios.defaults.headers.common["Authorization"]).toBe(
      "Bearer jwt-token-123"
    );
  });

  test("does not set the axios header when no token is provided", () => {
    applyAuthSession({ username: "guest", role: "USER" });

    expect(localStorage.getItem(USERNAME_KEY)).toBe("guest");
    expect(localStorage.getItem(ROLE_KEY)).toBe("USER");
    expect(localStorage.getItem(TOKEN_KEY)).toBeNull();
    expect(axios.defaults.headers.common["Authorization"]).toBeUndefined();
  });
});

describe("clearAuthSession", () => {
  test("removes the token/identity and clears the axios Authorization header", () => {
    applyAuthSession({
      token: "jwt-token-123",
      username: "admin",
      role: "ADMIN",
    });

    // Sanity: it was set first.
    expect(localStorage.getItem(TOKEN_KEY)).toBe("jwt-token-123");
    expect(axios.defaults.headers.common["Authorization"]).toBe(
      "Bearer jwt-token-123"
    );

    clearAuthSession();

    expect(localStorage.getItem(TOKEN_KEY)).toBeNull();
    expect(localStorage.getItem(USERNAME_KEY)).toBeNull();
    expect(localStorage.getItem(ROLE_KEY)).toBeNull();
    expect(getToken()).toBeNull();
    expect(axios.defaults.headers.common["Authorization"]).toBeUndefined();
  });
});
