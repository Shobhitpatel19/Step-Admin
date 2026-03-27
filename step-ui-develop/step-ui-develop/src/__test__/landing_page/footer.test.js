import React from "react";
import { render, screen } from "@testing-library/react";
import Footer from "../../components/pages/landing_page/footer";
import "../../components/pages/landing_page/footer.css"; // Mock this dependency

// Mock the CSS file to avoid issues during testing
jest.mock("../../components/pages/landing_page/footer.css", () => ({}));

describe("Footer Component", () => {
  test("renders the footer with the correct text", () => {
    // Render the Footer component
    render(<Footer />);

    // Assert that the footer text is rendered correctly
    const footerText = screen.getByText(/1993-2025 EPAM Systems. All Rights Reserved./i);
    expect(footerText).toBeInTheDocument();
  });

  test("footer contains the correct class names", () => {
    // Render the Footer component
    render(<Footer />);

    // Assert that the footer contains the correct class names
    const footerElement = screen.getByText(/1993-2025 EPAM Systems. All Rights Reserved./i);
    expect(footerElement).toHaveClass(
      "Text_text__NL5ab",
      "Text_font-sans__U7NcT",
      "loveship-color-vars_color-carbon__OUTbg",
      "text-layout_line-height-18__9P-h9",
      "text-layout_font-size-14__Fd52N",
      "text-layout_v-padding-9__sE-NS",
      "Text_container__yIUnd"
    );
  });

  test("footer is wrapped in a <footer> tag", () => {
    // Render the Footer component
    render(<Footer />);

    // Assert that the footer is wrapped in a <footer> tag
    const footerTag = screen.getByRole("contentinfo"); // <footer> is considered a landmark role with "contentinfo"
    expect(footerTag).toBeInTheDocument();
  });
});