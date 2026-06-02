// Smoke test.
//
// The full <App /> tree pulls in react-router-dom (whose v7 "exports" map
// jest 27 cannot resolve) plus ~30 page components, so instead we smoke-render
// the Login route in isolation behind a virtual router mock. This guarantees
// the primary entry screen mounts without crashing.

import { render, screen } from "@testing-library/react";

// Login -> authToken.js -> axios (1.x, ESM). jest 27 cannot transform it, so
// stub axios out with a minimal CJS mock.
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
    useNavigate: () => jest.fn(),
  }),
  { virtual: true }
);

jest.mock("./api/api", () => ({
  __esModule: true,
  loginUser: jest.fn(),
}));

import { MemoryRouter } from "react-router-dom";
import Login from "./pages/Login";

test("renders the Login route without crashing", () => {
  render(
    <MemoryRouter>
      <Login />
    </MemoryRouter>
  );

  expect(screen.getByText(/LedgerWorks ERP/i)).toBeInTheDocument();
  expect(
    screen.getByRole("button", { name: /login/i })
  ).toBeInTheDocument();
});
