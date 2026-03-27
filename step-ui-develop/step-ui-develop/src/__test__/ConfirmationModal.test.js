import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import { ConfirmationModal } from "../components/common/ConfirmationModal";

// Mock external UI components and hooks from '@epam/uui' and '@epam/uui-core'
jest.mock("@epam/uui", () => ({
  ModalBlocker: ({ children, ...rest }) => (
    <div data-testid="modal-blocker" {...rest}>
      {children}
    </div>
  ),
  ModalWindow: ({ children }) => (
    <div data-testid="modal-window">{children}</div>
  ),
  Panel: ({ children }) => <div data-testid="panel">{children}</div>,
  ModalHeader: ({ title, onClose }) => (
    <div data-testid="modal-header">
      <span>{title}</span>
      <button data-testid="close-button" onClick={onClose}>
        X
      </button>
    </div>
  ),
  ScrollBars: ({ children }) => <div data-testid="scrollbars">{children}</div>,
  ModalFooter: ({ children }) => (
    <div data-testid="modal-footer">{children}</div>
  ),
  FlexRow: ({ children }) => <div data-testid="flex-row">{children}</div>,
  Text: ({ children }) => <div data-testid="text">{children}</div>,
  Button: ({ caption, onClick, ...rest }) => (
    <button
      {...rest}
      data-testid={`button-${caption.toLowerCase()}`}
      onClick={onClick}
    >
      {caption}
    </button>
  ),
  SuccessNotification: jest.fn(),
  WarningNotification: jest.fn(),
}));

jest.mock("@epam/uui-core", () => ({
  useUuiContext: jest.fn(),
  IModal: jest.fn(),
}));

describe("ConfirmationModal", () => {
  const mockAbort = jest.fn();
  const mockSuccess = jest.fn();

  const defaultProps = {
    title: "Confirm Action",
    description: "Are you sure you want to proceed?",
    abort: mockAbort,
    success: mockSuccess,
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("renders modal with title and description", () => {
    render(<ConfirmationModal {...defaultProps} />);
    expect(screen.getByTestId("modal-header")).toHaveTextContent(
      "Confirm Action"
    );
    expect(screen.getByTestId("text")).toHaveTextContent(
      "Are you sure you want to proceed?"
    );
  });

  test("calls abort when clicking cancel button", () => {
    render(<ConfirmationModal {...defaultProps} />);
    fireEvent.click(screen.getByTestId("button-cancel"));
    expect(mockAbort).toHaveBeenCalledTimes(1);
  });

  test("calls success and abort when clicking confirm button", () => {
    render(<ConfirmationModal {...defaultProps} />);
    fireEvent.click(screen.getByTestId("button-confirm"));
    expect(mockSuccess).toHaveBeenCalledWith("Success action");
    expect(mockAbort).toHaveBeenCalledTimes(1);
  });

  test("calls abort when clicking close button", () => {
    render(<ConfirmationModal {...defaultProps} />);
    fireEvent.click(screen.getByTestId("close-button"));
    expect(mockAbort).toHaveBeenCalledTimes(1);
  });
});
