import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import { PracticeRatingApprovalDialog } from "../components/pages/practice_rating/PracticeRatingApprovalDialog";
import { IModal } from "@epam/uui-core";

// Mock dependencies
jest.mock("@epam/uui-core", () => ({
  useUuiContext: jest.fn(),
  IModal: jest.fn(),
}));

jest.mock("@epam/uui-docs", () => ({
  demoData: {},
}));

jest.mock("@epam/uui", () => ({
  ModalBlocker: ({ children, ...props }) => (
    <div data-testid="modal-blocker" {...props}>
      {children}
    </div>
  ),
  ModalWindow: ({ children }) => (
    <div data-testid="modal-window">{children}</div>
  ),
  ModalHeader: ({ title, onClose }) => (
    <div data-testid="modal-header">
      <span>{title}</span>
      <button onClick={onClose}>Close</button>
    </div>
  ),
  ModalFooter: ({ children }) => (
    <div data-testid="modal-footer">{children}</div>
  ),
  FlexRow: ({ children }) => <div data-testid="flex-row">{children}</div>,
  Panel: ({ children }) => <div data-testid="panel">{children}</div>,
  ScrollBars: ({ children }) => <div data-testid="scroll-bars">{children}</div>,
  Text: ({ children }) => <div data-testid="text">{children}</div>,
  Button: ({ caption, onClick }) => (
    <button onClick={onClick}>{caption}</button>
  ),
  SuccessNotification: jest.fn(),
  WarningNotification: jest.fn(),
}));

describe("PracticeRatingApprovalDialog", () => {
  let mockProps;

  beforeEach(() => {
    mockProps = {
      abort: jest.fn(),
      success: jest.fn(),
    };
  });

  it("renders the modal with all elements", () => {
    render(<PracticeRatingApprovalDialog {...mockProps} />);

    // Check if all elements are rendered
    expect(screen.getByTestId("modal-blocker")).toBeInTheDocument();
    expect(screen.getByTestId("modal-window")).toBeInTheDocument();
    expect(screen.getByTestId("panel")).toBeInTheDocument();
    expect(screen.getByTestId("modal-header")).toBeInTheDocument();
    expect(screen.getByTestId("scroll-bars")).toBeInTheDocument();
    expect(screen.getByTestId("flex-row")).toBeInTheDocument();
    expect(screen.getByTestId("modal-footer")).toBeInTheDocument();
    expect(screen.getByTestId("text")).toHaveTextContent(
      "You can't modify once its approved!"
    );
  });

  it("calls abort when the close button is clicked", () => {
    render(<PracticeRatingApprovalDialog {...mockProps} />);

    // Simulate clicking the close button
    fireEvent.click(screen.getByText("Close"));

    // Verify that abort is called
    expect(mockProps.abort).toHaveBeenCalledTimes(1);
  });

  it("calls abort when the Cancel button is clicked", () => {
    render(<PracticeRatingApprovalDialog {...mockProps} />);

    // Simulate clicking the Cancel button
    fireEvent.click(screen.getByText("Cancel"));

    // Verify that abort is called
    expect(mockProps.abort).toHaveBeenCalledTimes(1);
  });

  it("calls success and abort when the Approve button is clicked", () => {
    render(<PracticeRatingApprovalDialog {...mockProps} />);

    // Simulate clicking the Approve button
    fireEvent.click(screen.getByText("Approve"));

    // Verify that success is called with the correct argument
    expect(mockProps.success).toHaveBeenCalledWith("Success action");

    // Verify that abort is called
    expect(mockProps.abort).toHaveBeenCalledTimes(1);
  });
});
