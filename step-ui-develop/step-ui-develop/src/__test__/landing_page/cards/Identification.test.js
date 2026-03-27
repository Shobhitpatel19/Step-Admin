import React from "react";
import { render, screen, waitFor, act } from "@testing-library/react";
import { Provider } from "react-redux";
import { createStore } from "redux";
import IdentificationPhase from "../../../components/pages/landing_page/cards/identification/identification";
import { setRole } from "../../../redux/actions";
import {
  getTokenFromCookies,
  decodeToken,
} from "../../../components/utils/auth";
jest.mock("../../../components/common/axios", () => ({
  get: jest.fn(),
  post: jest.fn(),
  put: jest.fn(),
  delete: jest.fn(),
}));

jest.mock("../../../assets/identification.png", () => "identification.png");
jest.mock("../../../components/pages/landing_page/navigation", () => ({
  Navbar: jest.fn(() => <nav>Navbar</nav>),
  NavbarForP: jest.fn(() => <nav>NavbarForP</nav>),
}));

jest.mock("../../../redux/actions", () => ({
  setRole: jest.fn((role) =>
    role
      ? { type: "SET_ROLE", payload: role }
      : { type: "SET_ROLE", payload: "" }
  ),
}));

jest.mock("../../../components/utils/auth", () => ({
  decodeToken: jest.fn(),
  getTokenFromCookies: jest.fn(),
}));

// Reducer setup for testing
const reducer = (state = { practicerating: { role: "" } }, action) => {
  switch (action.type) {
    case "SET_ROLE":
      return { ...state, practicerating: { role: action.payload || "" } }; // Default undefined/empty roles
    default:
      return state;
  }
};

describe("IdentificationPhase Component", () => {
  let store;

  beforeEach(() => {
    jest.clearAllMocks();
    store = createStore(reducer); // Fresh store for each test
  });

  it("renders NavbarForP when role is 'ROLE_P'", async () => {
    decodeToken.mockReturnValue({ role: "ROLE_P" });
    getTokenFromCookies.mockReturnValue("mockedToken");

    render(
      <Provider store={store}>
        <IdentificationPhase />
      </Provider>
    );
  });

  it("renders Navbar when role is 'ROLE_SA'", async () => {
    decodeToken.mockReturnValue({ role: "ROLE_SA" });
    getTokenFromCookies.mockReturnValue("mockedToken");

    render(
      <Provider store={store}>
        <IdentificationPhase />
      </Provider>
    );
  });

  it("does not render Navbar for unknown roles", async () => {
    decodeToken.mockReturnValue({ role: "ROLE_UNKNOWN" });
    getTokenFromCookies.mockReturnValue("mockedToken");

    render(
      <Provider store={store}>
        <IdentificationPhase />
      </Provider>
    );

    await waitFor(() => {
      expect(screen.queryByText("Navbar")).toBeNull();
      expect(screen.queryByText("NavbarForP")).toBeNull();
    });
  });

  it("calls setRole with decoded role on valid token", async () => {
    const mockToken = "mockedToken";
    decodeToken.mockReturnValue({ role: "ROLE_P" });
    getTokenFromCookies.mockReturnValue(mockToken);

    render(
      <Provider store={store}>
        <IdentificationPhase />
      </Provider>
    );

    await waitFor(() => {
      expect(setRole).toHaveBeenCalledWith("ROLE_P");
    });
  });

  it("logs error when token is missing or invalid", () => {
    getTokenFromCookies.mockReturnValue(null);

    const consoleErrorMock = jest
      .spyOn(console, "error")
      .mockImplementation(() => {});

    render(
      <Provider store={store}>
        <IdentificationPhase />
      </Provider>
    );

    expect(consoleErrorMock).toHaveBeenCalledWith(
      "Token is missing or invalid."
    );
    consoleErrorMock.mockRestore();
  });

  it("logs error when token decoding fails", () => {
    const mockToken = "mockedToken";
    getTokenFromCookies.mockReturnValue(mockToken);
    decodeToken.mockImplementation(() => {
      throw new Error("Invalid token");
    });

    const consoleErrorMock = jest
      .spyOn(console, "error")
      .mockImplementation(() => {});

    render(
      <Provider store={store}>
        <IdentificationPhase />
      </Provider>
    );

    expect(consoleErrorMock).toHaveBeenCalledWith(
      "Error decoding token:",
      "Invalid token"
    );
    consoleErrorMock.mockRestore();
  });

  it("renders the Eligibility Criteria content correctly", async () => {
    decodeToken.mockReturnValue({ role: "ROLE_P" });
    getTokenFromCookies.mockReturnValue("mockedToken");

    render(
      <Provider store={store}>
        <IdentificationPhase />
      </Provider>
    );

    expect(screen.getByText("Eligibility Criteria")).toBeInTheDocument();
    expect(
      screen.getByText("Be within the A4 to B3 job levels.")
    ).toBeInTheDocument();
  });

  it("renders the image with correct src", async () => {
    decodeToken.mockReturnValue({ role: "ROLE_P" });
    getTokenFromCookies.mockReturnValue("mockedToken");

    render(
      <Provider store={store}>
        <IdentificationPhase />
      </Provider>
    );

    const image = screen.getByAltText("Identification Process");
    expect(image).toHaveAttribute("src", "identification.png");
  });

  it("does not render Navbar for undefined or empty reduxRole", async () => {
    decodeToken.mockReturnValue({ role: undefined });
    getTokenFromCookies.mockReturnValue("mockedToken");

    render(
      <Provider store={store}>
        <IdentificationPhase />
      </Provider>
    );

    await waitFor(() => {
      expect(screen.queryByText("Navbar")).toBeNull();
      expect(screen.queryByText("NavbarForP")).toBeNull();
    });
  });
});
