import React from "react";
import { render, screen } from "@testing-library/react";
import Engagement from "../../../components/pages/landing_page/cards/engagement/engagement";
import * as reactRedux from "react-redux";
import * as authUtils from "../../../components/utils/auth";
import * as actions from "../../../redux/actions";
import { useDispatch, useSelector } from "react-redux";
jest.mock(
  "../../../components/pages/landing_page/cards/identification/identificationPhase.css",
  () => ({})
);

jest.mock("../../../components/pages/landing_page/navigation", () => ({
  Navbar: ({ hideContent }) => (
    <div data-testid="navbar">Navbar {hideContent && "(hidden)"}</div>
  ),
}));

jest.mock("../../../components/pages/landing_page/navigation_p", () => ({
  NavbarForP: ({ hideContent }) => (
    <div data-testid="navbar-p">NavbarForP {hideContent && "(hidden)"}</div>
  ),
}));
jest.mock("react-redux", () => ({
  useDispatch: jest.fn(),
  useSelector: jest.fn(),
}));

const useSelectorMock = jest.spyOn(reactRedux, "useSelector");
const useDispatchMock = jest.spyOn(reactRedux, "useDispatch");

describe("Engagement Component", () => {
  const mockDispatch = jest.fn();

  beforeEach(() => {
    // dispatchMock = jest.fn();
    useDispatchMock.mockReturnValue(mockDispatch);

    jest.spyOn(authUtils, "getTokenFromCookies");
    jest.spyOn(authUtils, "decodeToken");
    jest.spyOn(actions, "setRole");
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it("should dispatch role if token is valid", () => {
    authUtils.getTokenFromCookies.mockReturnValue("validToken");
    authUtils.decodeToken.mockReturnValue({ role: "ROLE_P" });
    useSelectorMock.mockReturnValue("ROLE_P");

    render(<Engagement />);

    expect(authUtils.getTokenFromCookies).toHaveBeenCalled();
    expect(authUtils.decodeToken).toHaveBeenCalledWith("validToken");
    expect(mockDispatch).toHaveBeenCalledWith(actions.setRole("ROLE_P"));
    expect(screen.getByTestId("navbar-p")).toBeInTheDocument();
  });

  it("should render Navbar if role is ROLE_SA", () => {
    authUtils.getTokenFromCookies.mockReturnValue("validToken");
    authUtils.decodeToken.mockReturnValue({ role: "ROLE_SA" });
    useSelectorMock.mockReturnValue("ROLE_SA");

    render(<Engagement />);

    expect(screen.getByTestId("navbar")).toBeInTheDocument();
  });

  it("should handle missing token gracefully", () => {
    const consoleErrorMock = jest
      .spyOn(console, "error")
      .mockImplementation(() => {});
    authUtils.getTokenFromCookies.mockReturnValue(null);
    useSelectorMock.mockReturnValue(null);

    render(<Engagement />);

    expect(consoleErrorMock).toHaveBeenCalledWith(
      "Token is missing or invalid."
    );
    consoleErrorMock.mockRestore();
  });

  it("should handle decoding errors gracefully", () => {
    const consoleErrorMock = jest
      .spyOn(console, "error")
      .mockImplementation(() => {});
    authUtils.getTokenFromCookies.mockReturnValue("badToken");
    authUtils.decodeToken.mockImplementation(() => {
      throw new Error("Decoding failed");
    });
    useSelectorMock.mockReturnValue(null);

    render(<Engagement />);

    expect(consoleErrorMock).toHaveBeenCalledWith(
      "Error decoding token:",
      "Decoding failed"
    );
    consoleErrorMock.mockRestore();
  });

  it("should render all cards and content", () => {
    authUtils.getTokenFromCookies.mockReturnValue("validToken");
    authUtils.decodeToken.mockReturnValue({ role: "ROLE_P" });
    useSelectorMock.mockReturnValue("ROLE_P");

    render(<Engagement />);

    expect(screen.getByText("Key Aspects of Engagement")).toBeInTheDocument();
    expect(screen.getByText("Future Skills Mapping")).toBeInTheDocument();
    expect(screen.getByText("Aspirations Alignment")).toBeInTheDocument();
    expect(screen.getByText("Readiness Evaluation")).toBeInTheDocument();
    expect(screen.getByText("Development Planning")).toBeInTheDocument();
    expect(screen.getByText("Cohort-Based Learning")).toBeInTheDocument();
    expect(screen.getByText("Tracking & Accountability")).toBeInTheDocument();
    expect(screen.getByText(/structured engagement model/)).toBeInTheDocument();
  });

  it("renders nothing for undefined role", () => {
    authUtils.getTokenFromCookies.mockReturnValue("validToken");
    authUtils.decodeToken.mockReturnValue({ role: "UNKNOWN_ROLE" });
    useSelectorMock.mockReturnValue("UNKNOWN_ROLE");

    render(<Engagement />);

    expect(screen.queryByTestId("navbar")).not.toBeInTheDocument();
    expect(screen.queryByTestId("navbar-p")).not.toBeInTheDocument();
  });
});
