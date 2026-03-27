import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import DelegateBanner from "../components/pages/delegate_request/components/DelegateBanner";

// Mock CSS module
jest.mock(
  "../components/pages/delegate_request/components/module_css/PracticeDelegateBanner.module.css",
  () => ({
    container: "container",
    internalContainer: "internalContainer",
    title: "title",
    description: "description",
  })
);

// Mock Icon and IconButton
jest.mock("@epam/assets/icons/navigation-close-outline.svg", () => ({
  ReactComponent: () => <svg data-testid="close-icon" />,
}));

jest.mock("@epam/uui", () => ({
  IconButton: ({ icon: Icon, onClick, color }) => (
    <button data-testid="icon-button" onClick={onClick} data-color={color}>
      <Icon />
    </button>
  ),
  Text: ({ children, fontSize, fontWeight, color, cx }) => (
    <div
      data-testid="text"
      data-font-size={fontSize}
      data-font-weight={fontWeight}
      data-color={color}
      className={cx}
    >
      {children}
    </div>
  ),
}));

jest.mock("@epam/uui-components", () => ({
  FlexCell: ({ children, grow }) => (
    <div data-testid="flex-cell" data-grow={grow}>
      {children}
    </div>
  ),
  FlexRow: ({ children, cx }) => (
    <div data-testid="flex-row" className={cx}>
      {children}
    </div>
  ),
}));

describe("DelegateBanner", () => {
  it("renders title, description and close icon", () => {
    const onCloseMock = jest.fn();

    render(<DelegateBanner onClose={onCloseMock} />);

    // Title text
    expect(screen.getByText("Delegate Access")).toBeInTheDocument();

    // Description text
    expect(
      screen.getByText(
        "You can delegate your responsibilities to anyone at or above B3 level"
      )
    ).toBeInTheDocument();

    // Icon Button rendered
    expect(screen.getByTestId("icon-button")).toBeInTheDocument();

    // Close icon rendered
    expect(screen.getByTestId("close-icon")).toBeInTheDocument();
  });

  it("calls onClose when close icon is clicked", () => {
    const onCloseMock = jest.fn();

    render(<DelegateBanner onClose={onCloseMock} />);

    fireEvent.click(screen.getByTestId("icon-button"));
    expect(onCloseMock).toHaveBeenCalledTimes(1);
  });
});
