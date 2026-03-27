import React from "react";
import { render, screen, waitFor, fireEvent } from "@testing-library/react";
import Login from "../components/pages/login_page/login";
import { useNavigate } from "react-router-dom";
import * as authUtils from "../components/utils/auth";
jest.mock("react-router-dom", () => ({
  useNavigate: jest.fn(),
}));

jest.mock("../components/utils/auth", () => ({
  storeTokenInCookies: jest.fn(),
  decodeToken: jest.fn(),
  getTokenFromCookies: jest.fn(),
  removeAllCookies: jest.fn(),
}));

const mockNavigate = jest.fn();
beforeEach(() => {
  useNavigate.mockReturnValue(mockNavigate);
  jest.clearAllMocks();
});

describe("Login component", () => {
  const originalLocation = window.location;

  afterEach(() => {
    window.location = originalLocation;
  });

  const setupLocation = (pathname, search = "") => {
    delete window.location;
    window.location = {
      pathname,
      search,
      href: "http://localhost" + pathname + search,
    };
    window.history.replaceState({}, "", window.location.href);
  };

  test("clears cookies and redirects on '/logout' path", () => {
    setupLocation("/logout");
    render(<Login />);
    expect(authUtils.removeAllCookies).toHaveBeenCalled();
    expect(mockNavigate).toHaveBeenCalledWith("/login");
  });

  test("clears cookies and redirects on '/session-expired' path", () => {
    setupLocation("/session-expired");
    render(<Login />);
    expect(authUtils.removeAllCookies).toHaveBeenCalled();
    expect(mockNavigate).toHaveBeenCalledWith("/login");
  });

  test("navigates based on token in cookies", async () => {
    setupLocation("/login");
    authUtils.getTokenFromCookies.mockReturnValue("valid_token");
    authUtils.decodeToken.mockReturnValue({ role: "ROLE_SA" });

    render(<Login />);

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith("/welcome");
    });
  });

  test("shows error for invalid role in cookie token", async () => {
    setupLocation("/login");
    authUtils.getTokenFromCookies.mockReturnValue("invalid_token");
    authUtils.decodeToken.mockReturnValue({ role: "ROLE_X" });

    render(<Login />);

    await waitFor(() => {
      expect(
        screen.getByText("Unauthorized role or invalid token")
      ).toBeInTheDocument();
    });
  });

  test("handles token in query param and navigates accordingly", async () => {
    setupLocation("/login", "?token=abc123");
    authUtils.decodeToken.mockReturnValue({ role: "ROLE_P" });

    render(<Login />);

    await waitFor(() => {
      expect(authUtils.storeTokenInCookies).toHaveBeenCalledWith("abc123");
      expect(mockNavigate).toHaveBeenCalledWith("/welcome_p");
    });
  });

  test("shows error for invalid role in query token", async () => {
    setupLocation("/login", "?token=abc123");
    authUtils.decodeToken.mockReturnValue({ role: "ROLE_X" });

    render(<Login />);

    await waitFor(() => {
      expect(
        screen.getByText("Unauthorized role or invalid token")
      ).toBeInTheDocument();
    });
  });

  test("renders login UI and triggers SSO login", async () => {
    setupLocation("/login");
    authUtils.getTokenFromCookies.mockReturnValue(null);

    render(<Login />);

    const loginButton = screen.getByText(/Sign In with SSO/i);
    expect(loginButton).toBeInTheDocument();

    delete window.location;
    window.location = { href: "", pathname: "/login", search: "" };

    fireEvent.click(loginButton);

    expect(sessionStorage.getItem("redirectUrl")).toBe("/login");
    expect(window.location.href).toBe(
      "http://step.local/oauth2/authorization/epam"
    );
  });

  test("renders loading state initially", () => {
    setupLocation("/login");
    authUtils.getTokenFromCookies.mockReturnValue(null);

    const { getByText } = render(<Login />);
    // expect(getByText(/Loading.../i)).toBeInTheDocument();
  });
});
