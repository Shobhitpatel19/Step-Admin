import React from "react";
import { render, screen } from "@testing-library/react";
import DetailBox from "../components/common/sideprofile/DetailBox";

jest.mock("../components/common/sideprofile/KeyBox", () => ({ keyName }) => (
  <div data-testid="key-box">{keyName}</div>
));
jest.mock("../components/common/sideprofile/ValueBox", () => ({ value }) => (
  <div data-testid="value-box">{value}</div>
));

describe("DetailBox Component", () => {
  it("renders without crashing", () => {
    render(<DetailBox keyName="Test Key" value="Test Value" />);
    const mainContainer = screen.getByTestId("detail-box-main");
    expect(mainContainer).toBeInTheDocument();
  });

  it("renders the KeyBox component with the correct props", () => {
    render(<DetailBox keyName="Sample Key" value="Sample Value" />);
    const keyBoxElement = screen.getByTestId("key-box");
    expect(keyBoxElement).toHaveTextContent("Sample Key");
  });

  it("renders the ValueBox component with the correct props", () => {
    render(<DetailBox keyName="Sample Key" value="Sample Value" />);
    const valueBoxElement = screen.getByTestId("value-box");
    expect(valueBoxElement).toHaveTextContent("Sample Value");
  });

  it("renders a dividing line", () => {
    render(<DetailBox keyName="Test Key" value="Test Value" />);
    const lineElement = screen.getByTestId("separator");
    expect(lineElement).toBeInTheDocument();
  });

  it("renders child components in the correct order", () => {
    render(<DetailBox keyName="Ordered Key" value="Ordered Value" />);
    const keyBoxElement = screen.getByTestId("key-box");
    const valueBoxElement = screen.getByTestId("value-box");

    expect(keyBoxElement).toBeInTheDocument();
    expect(valueBoxElement).toBeInTheDocument();
    expect(keyBoxElement).toBeVisible();
    expect(valueBoxElement).toBeVisible();
  });
});
