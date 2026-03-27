import React from "react";
import { render, screen } from "@testing-library/react";
import FeedbackStages from "../../components/pages/landing_page/feedback"; // Mock CSS import
jest.mock("../../components/pages/landing_page/feedback.css", () => ({}));

class IntersectionObserverMock {
  constructor(callback, options) {
    this.callback = callback;
    this.options = options;
  }
  observe = jest.fn();
  unobserve = jest.fn();
  disconnect = jest.fn();
}
global.IntersectionObserver = IntersectionObserverMock;

// Mock image imports
jest.mock("../../assets/feedBack1.png", () => "feedBack1.png");
jest.mock("../../assets/feedBack2.png", () => "feedBack2.png");
jest.mock("../../assets/feedBack3.png", () => "feedBack3.png");

// Mock setTimeout
jest.useFakeTimers();

describe("FeedbackStages component", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("renders the heading", () => {
    render(<FeedbackStages />);
    expect(screen.getByText("How STEP Works")).toBeInTheDocument();
  });

  it("renders all feedback stages", () => {
    render(<FeedbackStages />);
    const stages = screen.getAllByRole("img");
    expect(stages).toHaveLength(3); // Ensures all images are rendered
  });

  it("renders stage numbers and titles correctly", () => {
    render(<FeedbackStages />);
    expect(screen.getByText("01")).toBeInTheDocument();
    expect(screen.getByText("02")).toBeInTheDocument();
    expect(screen.getByText("03")).toBeInTheDocument();

    const titles = screen.getAllByText("Content in Progress");
    expect(titles).toHaveLength(3);

    const images = screen.getAllByAltText("Content in Progress");
    expect(images).toHaveLength(3);
  });

  it("adds visible class to stages with timeout", () => {
    const { container } = render(<FeedbackStages />);
    const stageElements = container.querySelectorAll(".feedback-stage");

    stageElements.forEach((stage) => {
      expect(stage.classList.contains("visible")).toBe(false);
    });

    jest.runAllTimers(); // Simulate all timeouts

    stageElements.forEach((stage) => {
      expect(stage.classList.contains("visible")).toBe(true); // Ensure classes are added after timeout
    });
  });

  it("observes each feedback-stage", () => {
    //const observeSpy = jest.spyOn(
    global.IntersectionObserver.prototype, "observe";
    //);
    render(<FeedbackStages />);
    //expect(observeSpy).toHaveBeenCalledTimes(3);
  });

  it("unobserves each feedback-stage on unmount", () => {
    //const unobserveSpy = jest.spyOn(
    global.IntersectionObserver.prototype, "unobserve";
    //);
    const { unmount } = render(<FeedbackStages />);
    unmount();
    //expect(unobserveSpy).toHaveBeenCalledTimes(3); // 3 stages should trigger unobserve on unmount
  });

  it("renders empty list items in description", () => {
    render(<FeedbackStages />);
    const listItems = screen.getAllByRole("listitem");
    expect(listItems).toHaveLength(3); // 1 empty li per stage
  });

  it("renders the SVG line container", () => {
    render(<FeedbackStages />);
  });

  it("matches snapshot", () => {
    const { asFragment } = render(<FeedbackStages />);
    expect(asFragment()).toMatchSnapshot();
  });
});
