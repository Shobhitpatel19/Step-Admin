import React from "react";
import { render } from "@testing-library/react";
import Layout from "../../components/pages/landing_page/footerLayout";
import Footer from "../../components/pages/landing_page/footer";
import { Outlet } from "react-router-dom"; // Mock Outlet component

// Mock the dependent files
jest.mock("../../components/pages/landing_page/footer", () => () => (
  <div data-testid="mock-footer">Mock Footer</div>
));
jest.mock("react-router-dom", () => ({
  Outlet: () => <div data-testid="mock-outlet">Mock Outlet</div>,
}));

describe("Layout Component", () => {
  test("renders the Layout component with Outlet and Footer", () => {
    // Render the Layout component
    const { getByTestId } = render(<Layout />);

    // Assert that the Outlet is rendered
    const outletElement = getByTestId("mock-outlet");
    expect(outletElement).toBeInTheDocument();

    // Assert that the Footer is rendered
    const footerElement = getByTestId("mock-footer");
    expect(footerElement).toBeInTheDocument();
  });

  test("Layout contains the correct structure", () => {
    // Render the Layout component
    const { container } = render(<Layout />);

    // Assert that the Layout component is wrapped in a <div>
    const layoutDiv = container.querySelector("div");
    expect(layoutDiv).toBeInTheDocument();

    // Assert that the <div> contains both Outlet and Footer
    expect(layoutDiv).toContainHTML(
      '<div data-testid="mock-outlet">Mock Outlet</div>'
    );
    expect(layoutDiv).toContainHTML(
      '<div data-testid="mock-footer">Mock Footer</div>'
    );
  });
});
