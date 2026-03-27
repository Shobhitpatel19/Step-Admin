import React from "react";
import { render } from "@testing-library/react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import ProtectedRoute from "../../components/pages/landing_page/proctectedRoute";
import { decodeToken, getTokenFromCookies } from "../../components/utils/auth";
import PropTypes from "prop-types";

jest.mock("../../components/utils/auth", () => ({
  getTokenFromCookies: jest.fn(),
  decodeToken: jest.fn(),
}));

describe("ProtectedRoute Component", () => {
  afterEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
  });

  test("redirects to home when no token is present", () => {
    getTokenFromCookies.mockReturnValue(null);

    const { container } = render(
      <MemoryRouter initialEntries={["/protected"]}>
        <Routes>
          <Route
            path="/protected"
            element={<ProtectedRoute allowedRoles={["admin"]} />}
          />
          <Route path="/" element={<div>Home</div>} />
        </Routes>
      </MemoryRouter>
    );

    expect(container.innerHTML).toContain("Home");
    expect(localStorage.getItem("redirectUri")).toBe("/");
  });

  test("redirects to unauthorized when user role is not allowed", () => {
    getTokenFromCookies.mockReturnValue("fakeToken");
    decodeToken.mockReturnValue({ role: "user" });

    const { container } = render(
      <MemoryRouter initialEntries={["/protected"]}>
        <Routes>
          <Route
            path="/protected"
            element={<ProtectedRoute allowedRoles={["admin"]} />}
          />
          <Route path="/unauthorized" element={<div>Unauthorized</div>} />
        </Routes>
      </MemoryRouter>
    );

    expect(container.innerHTML).toContain("Unauthorized");
  });

  test("renders the Outlet when user has allowed role", () => {
    getTokenFromCookies.mockReturnValue("fakeToken");
    decodeToken.mockReturnValue({ role: "admin" });

    const { container } = render(
      <MemoryRouter initialEntries={["/protected"]}>
        <Routes>
          <Route
            path="/protected"
            element={<ProtectedRoute allowedRoles={["admin"]} />}
          >
            <Route path="" element={<div>Protected Content</div>} />
          </Route>
        </Routes>
      </MemoryRouter>
    );

    expect(container.innerHTML).toContain("Protected Content");
  });
});
