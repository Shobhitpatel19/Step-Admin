import { render, screen } from "@testing-library/react";
import KeyBox from "../components/common/sideprofile/KeyBox";
import React from "react";
describe("KeyBox Component", () => {
  it("renders without crashing and displays the keyName prop", () => {
    render(<KeyBox keyName="Test Key" />);
    const paragraph = screen.getByText("Test Key");

    expect(paragraph).toBeInTheDocument();
    expect(paragraph).toHaveTextContent("Test Key");
  });
});
