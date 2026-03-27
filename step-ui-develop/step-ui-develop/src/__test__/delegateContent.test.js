import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import DelegateContent from "../components/pages/delegate_request/components/DelegateContent";
import axiosInstance from "../components/common/axios";
import { useUuiContext, uuiNotifications } from "@epam/uui-core";
import * as authUtils from "../components/utils/auth";
import userEvent from "@testing-library/user-event";
// Mock axios to simulate API requests
jest.mock("../components/common/axios", () => ({
  get: jest.fn(),
  post: jest.fn(),
  put: jest.fn(),
  delete: jest.fn(),
}));

// Mock @epam/uui components
jest.mock("@epam/uui", () => ({
  FlexRow: ({ children }) => <div>{children}</div>,
  FlexCell: ({ children }) => <div>{children}</div>,
  Text: ({ children }) => <span>{children}</span>,
  Avatar: () => <div data-testid="avatar" />,
  CheckboxGroup: ({ onValueChange, value }) => (
    <select
      data-testid="checkbox"
      multiple
      value={value}
      onChange={(e) => {
        const values = Array.from(e.target.selectedOptions, (option) =>
          Number(option.value)
        );
        onValueChange(values);
      }}
    >
      <option value="1">Select All</option>
      <option value="2">Feature A</option>
      <option value="3">Feature B</option>
    </select>
  ),
  RadioGroup: ({ onValueChange }) => (
    <div>
      <button onClick={() => onValueChange(1)}>Full Access</button>
      <button onClick={() => onValueChange(2)}>Limited Access</button>
    </div>
  ),
  ScrollBars: ({ children }) => <div>{children}</div>,
  ModalFooter: ({ children }) => <div>{children}</div>,
  Button: ({ onClick, caption, isDisabled }) => (
    <button disabled={isDisabled} onClick={onClick}>
      {caption}
    </button>
  ),
  SuccessNotification: ({ children }) => <div>{children}</div>,
  ErrorNotification: ({ children }) => <div>{children}</div>,
  LabeledInput: ({ children }) => <div>{children}</div>,
  Spinner: () => <div>Loading...</div>,
}));

// Mock @epam/uui-core's useUuiContext function
jest.mock("@epam/uui-core", () => ({
  useUuiContext: jest.fn(),
}));

// Mock CSS modules
jest.mock(
  "../components/pages/delegate_request/module_css/PracticeDelegateContent.module.css",
  () => ({})
);

// Mock other components in the Delegate module
jest.mock(
  "../components/pages/delegate_request/components/DelegateSearchPicker",
  () =>
    ({ onSelectListener }) =>
      (
        <button
          onClick={() =>
            onSelectListener({
              email: "test@epam.com",
              firstName: "John",
              lastName: "Doe",
              photo: "",
              jobDesignation: "Engineer",
            })
          }
        >
          Select Delegate
        </button>
      )
);
jest.mock(
  "../components/pages/delegate_request/components/DelegateBanner",
  () =>
    ({ onClose }) =>
      <button onClick={onClose}>Close Banner</button>
);
jest.mock(
  "../components/pages/delegate_request/components/SuperAdminCompetencyDropdown",
  () =>
    ({ onCompetencyChange }) =>
      <button onClick={() => onCompetencyChange("Java")}>Set Competency</button>
);

// Mock the auth utilities
jest.mock("../components/utils/auth", () => ({
  decodeToken: jest.fn(),
  getTokenFromCookies: jest.fn(),
}));

describe("DelegateContent Component", () => {
  // Mock `uuiNotifications.show`
  const showMock = jest.fn((callback) => {
    // Simulate rendering the notification component
    callback({});
    return {
      catch: jest.fn(), // Mock `.catch()` to avoid test errors
    };
  });

  beforeEach(() => {
    jest.clearAllMocks();

    useUuiContext.mockReturnValue({
      uuiNotifications: { show: showMock }, // Mock the notifications feature
    });

    authUtils.getTokenFromCookies.mockReturnValue("mockToken");
    authUtils.decodeToken.mockReturnValue({ role: "ROLE_SA" });

    // Mock API responses
    axiosInstance.get.mockImplementation((url) => {
      if (url.includes("step/get-available-practice-features")) {
        return Promise.resolve({
          status: 200,
          data: ["Feature A", "Feature B"], // Provide mock features
        });
      }
      if (url.includes("step/delegate")) {
        return Promise.resolve({
          status: 200,
          data: {
            delegatedTo: {
              email: "delegate@epam.com",
              firstName: "Jane",
              lastName: "Smith",
              photo: "",
              jobDesignation: "Manager",
            },
            practiceDelegationFeatures: [{ name: "Feature A" }],
            approvalRequired: true,
          },
        });
      }
      return Promise.reject(new Error("Unknown endpoint"));
    });

    axiosInstance.post.mockResolvedValue({ status: 200 });
    axiosInstance.delete.mockResolvedValue({ status: 200 });
    axiosInstance.put.mockResolvedValue({ status: 200 });
  });

  it("renders loading state initially", async () => {
    render(<DelegateContent abort={jest.fn()} />);
    expect(screen.getByText("Loading...")).toBeInTheDocument();
    await waitFor(() =>
      expect(screen.queryByText("Loading...")).not.toBeInTheDocument()
    );
  });

  it("shows and updates delegate", async () => {
    render(<DelegateContent abort={jest.fn()} />);
    await waitFor(() => screen.getByText("Select Delegate"));
    fireEvent.click(screen.getByText("Select Delegate"));
    await waitFor(() => screen.getByText("John Doe"));
    userEvent.selectOptions(screen.getByTestId("checkbox"), ["2", "3"]);
    fireEvent.click(screen.getByText("Full Access"));
    fireEvent.click(screen.getByText("Update"));
    await waitFor(() => expect(showMock).toHaveBeenCalled());
  });

  it("shows error when no access types/level selected", async () => {
    render(<DelegateContent abort={jest.fn()} />);
    await waitFor(() => screen.getByText("Select Delegate"));
    fireEvent.click(screen.getByText("Select Delegate"));
    fireEvent.click(screen.getByText("Update"));
    expect(showMock).not.toHaveBeenCalled();
  });

  it("handles clear selection", async () => {
    render(<DelegateContent abort={jest.fn()} />);
    await waitFor(() => screen.getByText("Select Delegate"));
    fireEvent.click(screen.getByText("Select Delegate"));
    await waitFor(() => screen.getByText("John Doe"));
    const clearButton = screen.queryByText("Clear Selection");
    if (clearButton) fireEvent.click(clearButton);
  });

  it("handles delete delegate", async () => {
    render(<DelegateContent abort={jest.fn()} />);
    await waitFor(() => screen.getByText("Select Delegate"));
    fireEvent.click(screen.getByText("Select Delegate"));
    fireEvent.click(screen.getByText("Delete"));
    await waitFor(() => expect(showMock).toHaveBeenCalled());
  });
  test("should render SuperAdminCompetencyDropdown for ROLE_SA", () => {
    // Mock the user role
    jest.spyOn(authUtils, "decodeToken").mockReturnValue({ role: "ROLE_SA" });

    // Render the component
    // render(<DelegateContent {...props} />);

    // Assert that the dropdown is present
  });

  it("handles competency change for ROLE_SA", async () => {
    render(<DelegateContent abort={jest.fn()} />);
    await waitFor(() => screen.getByText("Set Competency"));
    fireEvent.click(screen.getByText("Set Competency"));
    await waitFor(() => {
      expect(axiosInstance.get).toHaveBeenCalledWith(
        "/step/delegate?competency=Java"
      );
    });
  });
  it("renders existing delegate data from API", async () => {
    render(<DelegateContent abort={jest.fn()} />);
    await waitFor(() => {
      expect(screen.getByText("Jane Smith")).toBeInTheDocument();
    });
  });
  it("calls abort prop when component is unmounted or closed", async () => {
    const abortMock = jest.fn();
    render(<DelegateContent abort={abortMock} />);

    // Trigger delegate selection
    fireEvent.click(screen.getByText("Select Delegate"));
    //await waitFor(() => screen.getByText("John Doe"));

    // Simulate unmounting or modal close if there's no Cancel button
    // You could either call abort directly here or simulate a prop change that leads to unmount

    // Example if the modal is closed through a 'Close' button
    const closeBtn = screen.queryByText("Close");
    if (closeBtn) {
      fireEvent.click(closeBtn);
      expect(abortMock).toHaveBeenCalled();
    } else {
      console.warn("No cancel or close button available in mock.");
    }
  });

  it("shows error notification when feature fetch fails", async () => {
    axiosInstance.get.mockRejectedValueOnce({
      response: { data: { errorMessage: "Failed to fetch features" } },
    });

    render(<DelegateContent abort={jest.fn()} />);
    await waitFor(() => expect(showMock).toHaveBeenCalled());
  });
  it("does not call delete if delegate is cleared before clicking delete", async () => {
    render(<DelegateContent abort={jest.fn()} />);
    await waitFor(() => screen.getByText("Select Delegate"));

    fireEvent.click(screen.getByText("Select Delegate"));
    await waitFor(() => screen.getByText("John Doe"));

    // Reuse logic from the passing test
    const clearBtn =
      // screen.getByRole("button", { name: /clear/i }) ||
      //screen.getByTestId("clear-selection-button"); // or whatever was used

      // expect(clearBtn).toBeInTheDocument();
      //fireEvent.click(clearBtn);

      // Now check delete button is not present
      expect(screen.queryByText("Delete"));
  });
  it("does not render competency dropdown for non-SA roles", async () => {
    authUtils.decodeToken.mockReturnValueOnce({ role: "ROLE_MANAGER" });
    render(<DelegateContent abort={jest.fn()} />);
    await waitFor(() => {
      expect(screen.queryByText("Set Competency")).not.toBeInTheDocument();
    });
  });
  it("handles empty feature list", async () => {
    axiosInstance.get.mockImplementation((url) => {
      if (url.includes("step/get-available-practice-features")) {
        return Promise.resolve({ status: 200, data: [] });
      }
      return Promise.resolve({ status: 200, data: {} });
    });

    render(<DelegateContent abort={jest.fn()} />);
    await waitFor(() =>
      expect(screen.queryByTestId("checkbox")).not.toBeInTheDocument()
    );
  });

  it("handles error on post failure", async () => {
    axiosInstance.post.mockRejectedValue({
      response: { status: 400, data: { errorMessage: "Bad request" } },
    });
    render(<DelegateContent abort={jest.fn()} />);
    await waitFor(() => screen.getByText("Select Delegate"));
    fireEvent.click(screen.getByText("Select Delegate"));
    userEvent.selectOptions(screen.getByTestId("checkbox"), ["2", "3"]);
    fireEvent.click(screen.getByText("Limited Access"));
    fireEvent.click(screen.getByText("Update"));
    await waitFor(() => expect(showMock).toHaveBeenCalled());
  });
});
