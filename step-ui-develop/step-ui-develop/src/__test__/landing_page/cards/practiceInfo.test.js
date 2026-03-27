import React from "react";
import { render, screen } from "@testing-library/react";
import PracticeInfo from "../../../components/pages/landing_page/cards/practice_guide/cards";
jest.mock("../../../components/common/axios", () => ({
  default: {
    get: jest.fn(),
    post: jest.fn(),
    put: jest.fn(),
    delete: jest.fn(),
  },
}));

describe("PracticeInfo Component", () => {
  beforeEach(() => {
    render(<PracticeInfo />);
  });

  it("renders the main heading", () => {
    expect(
      screen.getByRole("heading", { name: /Practice head Actions/i })
    ).toBeInTheDocument();
  });

  it("renders the description paragraph", () => {
    expect(
      screen.getByText(
        /To ensure your practice meets the guidelines of step program/i
      )
    ).toBeInTheDocument();
  });

  it("renders all tasks with titles and descriptions", () => {
    const tasks = [
      {
        title: "Employee Performance Ratings",
        description:
          "Evaluate and rate employees based on their potential for professional growth within the practice.",
      },
      {
        title: "Delegation of Responsibilities",
        description:
          "If you're unable to manage tasks yourself, you have the option to delegate responsibilities to another team member.",
      },
    ];

    tasks.forEach((task) => {
      expect(
        screen.getByRole("heading", { name: new RegExp(task.title, "i") })
      ).toBeInTheDocument();
      expect(
        screen.getByText(new RegExp(task.description, "i"))
      ).toBeInTheDocument();
    });
  });

  it("renders all steps for each task", () => {
    const steps = [
      "Go to MyOrg -> Practice Rating",
      "Go to MyOrg -> Delegate Request",
    ];

    steps.forEach((step) => {
      expect(screen.getByText(new RegExp(step, "i"))).toBeInTheDocument();
    });
  });
});
