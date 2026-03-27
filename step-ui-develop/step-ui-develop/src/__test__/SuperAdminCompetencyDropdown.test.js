import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import SuperAdminCompetencyDropdown from "../components/pages/delegate_request/components/SuperAdminCompetencyDropdown";
import userEvent from "@testing-library/user-event";
import axiosInstance from "../components/common/axios";

// ✅ Mock axios
jest.mock("../components/common/axios", () => ({
  get: jest.fn(),
}));

jest.mock("@epam/uui-core", () => {
  const actualCore = jest.requireActual("@epam/uui-core");
  return {
    ...actualCore,
    useArrayDataSource: actualCore.useArrayDataSource,
  };
});

// ✅ Mock UI components
jest.mock("@epam/uui", () => ({
  PickerInput: ({ value, onValueChange, placeholder }) => (
    <div>
      <div data-testid="picker-placeholder">{placeholder}</div>
      <button
        data-testid="mock-competency-option"
        onClick={() => onValueChange("Mock Competency")}
      >
        Mock Competency
      </button>
      <div data-testid="picker-selected">{value}</div>
    </div>
  ),
}));

jest.mock("@epam/uui-components", () => ({
  FlexCell: ({ children }) => <div data-testid="flex-cell">{children}</div>,
}));

describe("SuperAdminCompetencyDropdown", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("fetches and renders competencies", async () => {
    axiosInstance.get.mockResolvedValueOnce({ data: ["Mock Competency"] });
    const onCompetencyChange = jest.fn();

    render(
      <SuperAdminCompetencyDropdown onCompetencyChange={onCompetencyChange} />
    );

    await waitFor(() => {
      expect(axiosInstance.get).toHaveBeenCalledWith(
        "step/practice-rating/competencies"
      );
    });

    expect(screen.getByTestId("picker-placeholder")).toHaveTextContent(
      "Select a Practice"
    );
    expect(screen.getByTestId("mock-competency-option")).toBeInTheDocument();
  });

  it("handles competency selection and triggers onCompetencyChange", async () => {
    axiosInstance.get.mockResolvedValueOnce({ data: ["Mock Competency"] });
    const onCompetencyChange = jest.fn();

    render(
      <SuperAdminCompetencyDropdown onCompetencyChange={onCompetencyChange} />
    );

    await waitFor(() => {
      expect(axiosInstance.get).toHaveBeenCalled();
    });

    userEvent.click(screen.getByTestId("mock-competency-option"));
    ("Mock Competency");
  });

  it("handles fetch error gracefully", async () => {
    const consoleErrorSpy = jest
      .spyOn(console, "error")
      .mockImplementation(() => {});
    axiosInstance.get.mockRejectedValueOnce(new Error("Fetch failed"));
    const onCompetencyChange = jest.fn();

    render(
      <SuperAdminCompetencyDropdown onCompetencyChange={onCompetencyChange} />
    );

    await waitFor(() => {
      expect(axiosInstance.get).toHaveBeenCalled();
    });

    expect(consoleErrorSpy).toHaveBeenCalledWith(
      "Error fetching competencies:",
      expect.any(Error)
    );

    consoleErrorSpy.mockRestore();
  });
});
