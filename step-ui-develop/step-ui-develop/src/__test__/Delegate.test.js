import React from "react";
import { render } from "@testing-library/react";
import Delegate from "../components/pages/delegate_request/Delegate";
import { ModalBlocker, ModalWindow, Panel } from "@epam/uui";

jest.mock("@epam/uui", () => ({
  ModalBlocker: jest.fn(({ children }) => (
    <div data-testid="modal-blocker">{children}</div>
  )),
  ModalWindow: jest.fn(({ children }) => (
    <div data-testid="modal-window">{children}</div>
  )),
  Panel: jest.fn(({ children }) => <div data-testid="panel">{children}</div>),
}));

jest.mock(
  "../components/pages/delegate_request/components/DelegateContent",
  () => jest.fn(() => <div data-testid="delegate-content" />)
);

describe("Delegate Component", () => {
  it("should render Delegate component with all child components", () => {
    const props = { onClose: jest.fn() }; // Mock any necessary props
    const { getByTestId } = render(<Delegate {...props} />);
  });

  it("should pass props correctly to ModalBlocker and DelegateContent", () => {
    const props = { testProp: "testValue" };
    render(<Delegate {...props} />);
    expect(ModalBlocker).toHaveBeenCalledWith(
      expect.objectContaining(props),
      {}
    );
  });
});
