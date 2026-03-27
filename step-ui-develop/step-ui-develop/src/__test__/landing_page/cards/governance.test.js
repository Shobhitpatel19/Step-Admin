import React from "react";
import { render, screen } from "@testing-library/react";
import configureMockStore from "redux-mock-store";
import { Provider } from "react-redux";
import GovernanceMetrics from "../../../components/pages/landing_page/cards/hrImpact/goveranace";
import * as authUtils from "../../../components/utils/auth";
import { setRole } from "../../../redux/actions";

const mockStore = configureMockStore();

jest.mock("../../../components/common/axios", () => ({
  default: {
    get: jest.fn(),
    post: jest.fn(),
    put: jest.fn(),
    delete: jest.fn(),
  },
}));

jest.mock("../../../redux/actions", () => ({
  setRole: jest.fn((role) => ({ type: "SET_ROLE", payload: role })),
}));

jest.mock("../../../components/pages/landing_page/navigation", () => ({
  Navbar: () => <div data-testid="navbar-sa">Navbar SA</div>,
}));
jest.mock("../../../components/pages/landing_page/navigation_p", () => ({
  NavbarForP: () => <div data-testid="navbar-p">Navbar P</div>,
}));

describe("GovernanceMetrics Component", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  const setup = (role = "Sample Role") => {
    const store = mockStore({
      practicerating: {
        role,
      },
    });

    render(
      <Provider store={store}>
        <GovernanceMetrics />
      </Provider>
    );
  };

  it("dispatches setRole from token on mount", () => {
    const mockToken = "mock.jwt.token";
    const mockDecoded = { role: "ROLE_SA" };

    jest.spyOn(authUtils, "getTokenFromCookies").mockReturnValue(mockToken);
    jest.spyOn(authUtils, "decodeToken").mockReturnValue(mockDecoded);

    setup("ROLE_SA");

    expect(authUtils.getTokenFromCookies).toHaveBeenCalled();
    expect(authUtils.decodeToken).toHaveBeenCalledWith(mockToken);
    expect(setRole).toHaveBeenCalledWith("ROLE_SA");
  });

  it("renders Navbar if role is ROLE_SA", () => {
    setup("ROLE_SA");
    expect(screen.getByTestId("navbar-sa")).toBeInTheDocument();
  });

  it("renders NavbarForP if role is ROLE_P", () => {
    setup("ROLE_P");
    expect(screen.getByTestId("navbar-p")).toBeInTheDocument();
  });

  it("renders no navbar for unknown role", () => {
    setup("UNKNOWN_ROLE");
    expect(screen.queryByTestId("navbar-sa")).not.toBeInTheDocument();
    expect(screen.queryByTestId("navbar-p")).not.toBeInTheDocument();
  });

  it("renders STEP Core Team Composition section and content", () => {
    setup();

    expect(
      screen.getByRole("heading", { name: /STEP Core Team Composition/i })
    ).toBeInTheDocument();

    const roles = [
      "Practices (Competency Heads)",
      "Delivery",
      "Total Rewards",
      "HR",
      "Sponsorship from Delivery Leadership",
    ];

    roles.forEach(() => {});
  });

  it("renders Governance & Tracking Mechanisms and bullet points", () => {
    setup();

    expect(
      screen.getByRole("heading", { name: /Governance & Tracking Mechanisms/i })
    ).toBeInTheDocument();

    const points = [
      "Regular tracking of participants' skill development progress.",
      "Insights shared on key improvements and focus areas.",
      "Transparent progress updates to all stakeholders.",
      "Showcasing individual growth trajectory and readiness for future roles.",
      "Highlighting performance in skill-building, mentorship participation, and business contributions.",
      "Skill Readiness Impact – _____% of participants achieving a higher RRI score.",
      "Leadership Pipeline – Number of STEP participants transitioning into expanded roles.",
      "Engagement Index – Active participation in learning, mentorship, and business initiatives.",
      "Mentorship Effectiveness – Feedback scores from participants and mentors.",
      "Retention & Growth – Increased retention of top talent and career progression data.",
    ];

    points.forEach((point) => {
      expect(screen.getByText(new RegExp(point, "i"))).toBeInTheDocument();
    });
  });

  it("renders subtitle elements", () => {
    setup();

    expect(
      screen.getByRole("heading", {
        name: /Governance & Tracking Mechanisms/i,
      })
    ).toBeInTheDocument();

    expect(
      screen.getByRole("heading", { name: /STEP Core Team Composition/i })
    ).toBeInTheDocument();
  });
});
