import React from "react";
import { render, fireEvent, screen, act } from "@testing-library/react";
import "@testing-library/jest-dom";
import { Provider } from "react-redux";
import configureStore from "redux-mock-store";
import FilterBox from "../components/pages/master_excel_view/FilterBox";
import {
  setCurrentExcelVersion,
  setTableData,
  RequestExcelVersion,
  notify,
  setSelectedBoxes,
} from "../redux/actions";

// Mock Redux actions
jest.mock("../redux/actions", () => ({
  setCurrentExcelVersion: jest.fn(),
  RequestExcelVersion: jest.fn(),
  setTableData: jest.fn(),
  notify: jest.fn(),
  setSelectedBoxes: jest.fn(),
}));

// Mocking Redux State
const mockStore = configureStore([]);
const mockedState = {
  masterexcel: {
    topTalentDTO: [
      { UID: "UID1", Name: "John Doe" },
      { UID: "UID2", Name: "Jane Smith" },
    ],
    excelVersions: [
      { fileName: "STEP_2022_Ver1.xlsx" },
      { fileName: "STEP_2022_Ver2.xlsx" },
    ],
    listForSaving: [],
    latestVersion: false,
    requestedExcelVersion: "STEP_2022_Ver2.xlsx",
    noOFExcels: 1,
  },
};

// Mocking props for FilterBox
const mockProps = {
  data: {
    topTalentExcelVersions: [
      { fileName: "STEP_2022_Ver1.xlsx" },
      { fileName: "STEP_2022_Ver2.xlsx" },
    ],
    topTalentEmployeeDTOList: [
      { UID: "UID1", Ranking: 1, Name: "John Doe" },
      { UID: "UID2", Ranking: 2, Name: "Jane Smith" },
    ],
  },
};

// Rendering Helper Function
const renderComponent = (props = {}) => {
  const store = mockStore(mockedState);
  render(
    <Provider store={store}>
      <FilterBox {...props} />
    </Provider>
  );
};

describe("FilterBox Component", () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  it("renders FilterBox component correctly", () => {
    renderComponent(mockProps);
    expect(screen.getByTestId("second-component")).toBeInTheDocument();
    expect(
      screen.getByPlaceholderText("STEP_YEAR_VERSION")
    ).toBeInTheDocument();
    expect(screen.getByPlaceholderText("Percentage")).toBeInTheDocument();
    expect(
      screen.getByPlaceholderText("SEARCH CANDIDATES")
    ).toBeInTheDocument();
    expect(screen.getByText("Show")).toBeInTheDocument();
  });

  it("handles dropdown selection for Excel Versions correctly", () => {
    renderComponent(mockProps);

    act(() => {
      fireEvent.mouseDown(screen.getByTestId("dropdown"));
      fireEvent.change(screen.getByTestId("dropdown"), {
        target: { value: "1" },
      });
    });

    expect(setCurrentExcelVersion).toHaveBeenCalledWith("STEP_2022_Ver1");
    expect(RequestExcelVersion).toHaveBeenCalledWith("STEP_2022_Ver1.xlsx");
  });

  it("handles percentage input and Apply button functionality correctly", () => {
    renderComponent(mockProps);

    const percentageInput = screen.getByPlaceholderText("Percentage");
    act(() => {
      fireEvent.change(percentageInput, { target: { value: "50" } });
    });

    expect(percentageInput.value).toBe("50");

    const applyButton = screen.getByText("Show");
    act(() => {
      fireEvent.click(applyButton);
    });

    expect(setTableData).toHaveBeenCalled();
    expect(setSelectedBoxes).toHaveBeenCalledWith(["UID1"]);
  });

  it("shows notification for invalid percentage input", () => {
    renderComponent(mockProps);

    const percentageInput = screen.getByPlaceholderText("Percentage");
    act(() => {
      fireEvent.change(percentageInput, { target: { value: "-10" } });
    });

    const applyButton = screen.getByText("Show");
    act(() => {
      fireEvent.click(applyButton);
    });

    expect(notify).toHaveBeenCalledWith(
      "Input range should be in 1-100.",
      false
    );
  });

  it("handles multi-select correctly", () => {
    renderComponent(mockProps);

    const multiSelect = screen.getByPlaceholderText("SEARCH CANDIDATES");
    act(() => {
      fireEvent.change(multiSelect, { target: { value: ["1", "2"] } });
    });

    expect(setTableData).toHaveBeenCalled();
  });

  it("dispatches fallback logic when requestedExcelVersion is null", () => {
    const store = mockStore({
      ...mockedState,
      masterexcel: { ...mockedState.masterexcel, requestedExcelVersion: null },
    });

    render(
      <Provider store={store}>
        <FilterBox {...mockProps} />
      </Provider>
    );

    expect(setCurrentExcelVersion).toHaveBeenCalledWith("STEP_2022_Ver1");
  });
});
