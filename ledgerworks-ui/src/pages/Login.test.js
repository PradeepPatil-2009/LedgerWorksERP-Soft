// Tests for the Login page.
//
// react-router-dom v7 ships an "exports" map that jest 27 (bundled with
// react-scripts 5) cannot resolve, so we provide a lightweight virtual mock
// for the only two router APIs the page touches: MemoryRouter + useNavigate.
// The api module is mocked so loginUser never hits the network.

import { render, screen, fireEvent, waitFor } from "@testing-library/react";

const mockNavigate = jest.fn();

// authToken.js (imported transitively by Login) pulls in axios 1.x, whose ESM
// entry jest 27 cannot transform. Stub it to the small surface it needs.
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

jest.mock(
  "react-router-dom",
  () => ({
    __esModule: true,
    MemoryRouter: ({ children }) => children,
    useNavigate: () => mockNavigate,
  }),
  { virtual: true }
);

jest.mock("../api/api", () => ({
  __esModule: true,
  loginUser: jest.fn(),
}));

import { MemoryRouter } from "react-router-dom";
import { loginUser } from "../api/api";
import Login from "./Login";

function renderLogin() {
  return render(
    <MemoryRouter>
      <Login />
    </MemoryRouter>
  );
}

beforeEach(() => {
  jest.clearAllMocks();
  localStorage.clear();
});

test("renders username, password inputs and a submit button", () => {
  renderLogin();

  expect(screen.getByLabelText(/username/i)).toBeInTheDocument();
  expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
  expect(
    screen.getByRole("button", { name: /login/i })
  ).toBeInTheDocument();
});

test("submitting valid credentials calls loginUser and navigates without crashing", async () => {
  loginUser.mockResolvedValueOnce({
    id: 1,
    username: "admin",
    role: "ADMIN",
    token: "jwt-token-123",
  });

  renderLogin();

  fireEvent.change(screen.getByLabelText(/username/i), {
    target: { value: "admin" },
  });
  fireEvent.change(screen.getByLabelText(/password/i), {
    target: { value: "admin123" },
  });
  fireEvent.click(screen.getByRole("button", { name: /login/i }));

  await waitFor(() => {
    expect(loginUser).toHaveBeenCalledWith("admin", "admin123");
  });

  await waitFor(() => {
    expect(mockNavigate).toHaveBeenCalledWith("/dashboard");
  });

  // Identity (not the token) was persisted by applyAuthSession; the JWT lives
  // in an HttpOnly cookie and is never written to localStorage.
  expect(localStorage.getItem("username")).toBe("admin");
  expect(localStorage.getItem("role")).toBe("ADMIN");
  expect(localStorage.getItem("token")).toBeNull();
});

test("shows an error message when loginUser rejects", async () => {
  // Login logs the rejected error via console.error by design; silence it so
  // the expected failure path does not pollute the test output.
  const errorSpy = jest.spyOn(console, "error").mockImplementation(() => {});
  loginUser.mockRejectedValueOnce(new Error("bad creds"));

  renderLogin();

  fireEvent.change(screen.getByLabelText(/username/i), {
    target: { value: "admin" },
  });
  fireEvent.change(screen.getByLabelText(/password/i), {
    target: { value: "wrong" },
  });
  fireEvent.click(screen.getByRole("button", { name: /login/i }));

  // Error surfaces in the role="alert" region, not via window.alert.
  const alert = await screen.findByRole("alert");
  expect(alert).toHaveTextContent(/invalid username or password/i);
  expect(mockNavigate).not.toHaveBeenCalled();

  errorSpy.mockRestore();
});

test("shows a validation message when fields are empty", async () => {
  renderLogin();

  fireEvent.click(screen.getByRole("button", { name: /login/i }));

  const alert = await screen.findByRole("alert");
  expect(alert).toHaveTextContent(/enter both username and password/i);
  expect(loginUser).not.toHaveBeenCalled();
});
