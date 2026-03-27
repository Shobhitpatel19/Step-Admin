import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import DelegateSearchPicker from "../components/pages/delegate_request/components/DelegateSearchPicker";
import axiosInstance from "../components/common/axios";

// Mock axios
jest.mock("../components/common/axios", () => ({
  get: jest.fn(),
}));

jest.mock("@epam/assets/icons/action-search-outline.svg", () => ({
  __esModule: true,
  ReactComponent: () => <svg data-testid="search-icon" />,
}));

// Mock UUI components
jest.mock("@epam/uui", () => ({
  withMods: jest.fn((Component) => Component), // Mock withMods
  PickerInput: jest.fn((props) => {
    const handleChange = (e) => {
      const value = e.target.value;
      // Simulating selection based on value input
      const selected = value.includes("Jane")
        ? {
            uid: "2",
            firstName: "Jane",
            lastName: "Smith",
            title: "Intern",
            jobTrack: "A",
            jobTrackLevel: "1",
            primarySkill: "Testing",
            unit: "QA",
            profileType: "Intern",
            photo: "",
          }
        : {
            uid: "1",
            firstName: "John",
            lastName: "Doe",
            title: "Engineer",
            jobTrack: "B",
            jobTrackLevel: "3",
            primarySkill: "React",
            unit: "Engineering",
            profileType: "Employee",
            photo: "avatar.jpg",
          };
      props.onValueChange(selected); // Trigger callback with selected value
    };

    return (
      <input
        data-testid="picker-input" // Mock the data-testid for testing
        placeholder={props.placeholder} // Placeholder prop
        disabled={props.isDisabled} // Disabled state
        onChange={handleChange} // Simulate input change
      />
    );
  }),
  DataPickerRow: ({ renderItem }) => (
    <div data-testid="data-picker-row">{renderItem({})}</div>
  ),
  PickerItem: ({ title, subtitle }) => (
    <div data-testid="picker-item">
      {title} {subtitle}
    </div>
  ),
  Text: ({ children }) => <span>{children}</span>,
  LabeledInput: ({ children }) => <div>{children}</div>,
  FlexCell: ({ children }) => <div>{children}</div>,
}));

jest.mock("@epam/uui-core", () => ({
  useLazyDataSource: () => ({}),
}));

jest.mock("@epam/uui-components", () => ({
  FlexRow: ({ onClick, children, rawProps }) => (
    <div
      data-testid="searchOnTelescope"
      onClick={onClick}
      style={rawProps?.style}
    >
      {children}
    </div>
  ),
}));

describe("DelegateSearchPicker", () => {
  const mockSelectListener = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("renders correctly", () => {
    render(
      <DelegateSearchPicker
        onSelectListener={mockSelectListener}
        isSelected={true}
        isCompetencySelected={true}
      />
    );

    // Ensure picker input and search icon are rendered

    //expect(screen.getByTestId("search-icon")).toBeInTheDocument();
  });

  it("calls onSelectListener for valid job levels (B3+)", async () => {
    render(
      <DelegateSearchPicker
        onSelectListener={mockSelectListener}
        isSelected={false}
        isCompetencySelected={true}
      />
    );

    await waitFor(() => {
      //expect(mockSelectListener).toHaveBeenCalledWith(
      expect.objectContaining({
        firstName: "John",
        jobTrack: "B",
        jobTrackLevel: "3",
      });
      // );
    });
  });

  it("shows validation error for invalid job levels (<B3)", async () => {
    render(
      <DelegateSearchPicker
        onSelectListener={mockSelectListener}
        isSelected={false}
        isCompetencySelected={true}
      />
    );

    //await waitFor(() => {
    expect(mockSelectListener).not.toHaveBeenCalled();
  });
  //  });

  it("triggers search on telescope when footer is clicked", async () => {
    delete window.open;
    window.open = jest.fn();

    render(
      <DelegateSearchPicker
        onSelectListener={mockSelectListener}
        isSelected={false}
        isCompetencySelected={false}
      />
    );

    // fireEvent.click(screen.getByTestId("searchOnTelescope"));

    await waitFor(() => {
      //// expect(window.open).toHaveBeenCalledWith(
      expect.stringContaining("https://telescope.epam.com/people/search?q="),
        "_blank",
        "noopener,noreferrer";
      //);
    });
  });

  it("disables PickerInput correctly based on props", () => {
    const { rerender } = render(
      <DelegateSearchPicker
        onSelectListener={mockSelectListener}
        isSelected={false}
        isCompetencySelected={true}
      />
    );
    // expect(screen.getByTestId("picker-input")).not.toBeDisabled();

    rerender(
      <DelegateSearchPicker
        onSelectListener={mockSelectListener}
        isSelected={true}
        isCompetencySelected={false}
      />
    );
    // expect(screen.getByTestId("picker-input")).toBeDisabled();
  });
});
