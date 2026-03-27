import React from "react";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import StagesOfTopTalent from "../../components/pages/landing_page/TopTalentSection";

jest.mock("react-slick", () => {
  return (props) => (
    <div>
      <div onClick={props.nextArrow.props.onClick} data-testid="next-arrow">
        {props.nextArrow}
      </div>
      <div onClick={props.prevArrow.props.onClick} data-testid="prev-arrow">
        {props.prevArrow}
      </div>
      <div className="mock-slider">{props.children}</div>
    </div>
  );
});

// Mock Tooltip from @epam/uui
jest.mock("@epam/uui", () => {
  return {
    Tooltip: ({ content, children }) => (
      <div data-testid="tooltip">
        {children}
        <span data-testid="tooltip-content">{content}</span>
      </div>
    ),
  };
});

describe("Stages Of Top Talent Component", () => {
  test("renders header correctly", () => {
    render(<StagesOfTopTalent />);
    expect(screen.getByText(/Stages of Top Talent/i)).toBeInTheDocument();
  });

  test("renders all cards with proper titles and descriptions", () => {
    render(<StagesOfTopTalent />);

    const expectedTitles = [
      "IDENTIFICATION",
      "ENGAGEMENTS",
      "REPORTS",
      "GOVERNANCE & SUCCESS METRICS",
    ];
    expectedTitles.forEach((title) => {
      expect(screen.getByText(title)).toBeInTheDocument();
    });

    const expectedDescriptions = [
      "Employee who permanently works for EPAM",
      "Colleagues who work closely with an employee",
      "Managers support employee growth with timely feedback",
      "People partner supports employees with feedback processes",
    ];
    expectedDescriptions.forEach((description) => {
      expect(screen.getByText(description)).toBeInTheDocument();
    });
  });

  test("renders link for cards with 'link' properties correctly", () => {
    render(<StagesOfTopTalent />);

    const links = screen.getAllByRole("link");
    const expectedLinks = [
      "/identification",
      "/engagement",
      "/governance-metrics",
    ];

    expectedLinks.forEach((expectedLink) => {
      const linkElement = links.find(
        (link) => link.getAttribute("href") === expectedLink
      );
      expect(linkElement).toBeTruthy();
      expect(linkElement).toHaveAttribute("target", "_blank");
      expect(linkElement).toHaveAttribute("rel", "noopener noreferrer");
    });
  });

  test("renders tooltip for card with tooltip property", () => {
    render(<StagesOfTopTalent />);

    const tooltipCard = screen.getByTestId("tooltip");
    const tooltipContent = screen.getByTestId("tooltip-content");

    expect(tooltipCard).toBeInTheDocument();
    expect(tooltipContent).toHaveTextContent("Content is under progress");
  });

  test("applies correct background colors to cards dynamically", () => {
    render(<StagesOfTopTalent />);
  });

  test("renders responsive settings for slider", async () => {
    render(<StagesOfTopTalent />);

    const resizeWindow = (width) => {
      global.innerWidth = width;
      global.dispatchEvent(new Event("resize"));
    };

    // Desktop view
    resizeWindow(1280);
    //expect(screen.getAllByTestId(/top-talent-card-/).length).toBe(4);

    // Tablet view
    resizeWindow(1024);
    // expect(screen.getAllByTestId(/top-talent-card-/).length).toBe(4);

    // Mobile view
    resizeWindow(768);
    //expect(screen.getAllByTestId(/top-talent-card-/).length).toBe(4);
  });

  test("renders and clicks custom next and prev arrow components", () => {
    render(<StagesOfTopTalent />);

    // Select the next and prev arrows using test IDs
    const nextArrow = screen.getByTestId("next-arrow");
    const prevArrow = screen.getByTestId("prev-arrow");

    // Ensure the arrows are rendered
    expect(nextArrow).toBeInTheDocument();
    expect(prevArrow).toBeInTheDocument();

    // Simulate clicks on both arrows
    userEvent.click(nextArrow);
    userEvent.click(prevArrow);

    // No errors should occur on clicking these elements
  });
});
