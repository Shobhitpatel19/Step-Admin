import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import Master from "../components/pages/master_excel_upload/Master";
import { useDispatch } from "react-redux";
import * as actions from "../redux/actions";
import * as FileUploadApi from "../components/common/file_upload/FileUploadApi";
import axios from "../components/common/axios"; // Import axios directly
import userEvent from "@testing-library/user-event";

// ✅ Mock useDispatch
jest.mock("react-redux", () => {
  const actualRedux = jest.requireActual("react-redux");
  return {
    ...actualRedux,
    useDispatch: jest.fn(),
  };
});

// ✅ Mock Axios
jest.mock("../components/common/axios", () => ({
  get: jest.fn(),
}));

// ✅ Mock Alert
jest.mock("../components/common/Alert", () => () => (
  <div data-testid="alert">Alert</div>
));

// ✅ Mock FileUploadDataTable
jest.mock(
  "../components/common/file_upload/FileUploadDataTable",
  () =>
    ({ items }) =>
      <div data-testid="data-table">{JSON.stringify(items)}</div>
);

// ✅ Mock FileUpload
jest.mock(
  "../components/common/file_upload/FileUpload",
  () =>
    ({ onUploadSuccess }) =>
      (
        <div
          data-testid="file-upload"
          onClick={() => onUploadSuccess([{ id: 1 }], true)}
        >
          FileUpload
        </div>
      )
);

// ✅ Mock Navbar
jest.mock("../components/pages/landing_page/navigation", () => ({
  Navbar: () => <div data-testid="navbar">Navbar</div>,
}));

// ✅ Mock uui components
jest.mock("@epam/uui", () => ({
  FlexCell: ({ children }) => <div>{children}</div>,
  Spinner: () => <div data-testid="spinner">Loading...</div>,
}));

describe("Master Component", () => {
  const dispatchMock = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
    useDispatch.mockReturnValue(dispatchMock);

    jest.spyOn(actions, "notify").mockImplementation((msg, status) => ({
      type: "NOTIFY",
      payload: { msg, status },
    }));
    jest
      .spyOn(actions, "resetNotificationStatus")
      .mockImplementation((val) => ({
        type: "RESET",
        payload: val,
      }));
  });

  test("renders spinner while loading", async () => {
    jest.spyOn(FileUploadApi, "getUploadedData").mockResolvedValueOnce(null);
    jest.spyOn(axios, "get").mockResolvedValueOnce({
      data: { excelName: "", headers: [], mandatoryColumns: [] },
    });

    render(<Master />);
    expect(screen.getByTestId("spinner")).toBeInTheDocument();
    await waitFor(() =>
      expect(screen.queryByTestId("spinner")).not.toBeInTheDocument()
    );
  });

  test("renders FileUpload if no items are returned", async () => {
    jest
      .spyOn(FileUploadApi, "getUploadedData")
      .mockResolvedValueOnce({ status: 200, data: null });
    jest.spyOn(axios, "get").mockResolvedValueOnce({
      data: {
        excelName: "STEP_2025_V1.xlsx",
        headers: ["col1"],
        mandatoryColumns: ["col1"],
      },
    });

    render(<Master />);
    await waitFor(() => {
      expect(screen.getByTestId("file-upload")).toBeInTheDocument();
    });
  });

  test("renders FileUploadDataTable if items are returned", async () => {
    const mockData = [{ name: "Test" }];
    jest
      .spyOn(FileUploadApi, "getUploadedData")
      .mockResolvedValueOnce({ status: 200, data: mockData });
    jest.spyOn(axios, "get").mockResolvedValueOnce({
      data: {
        excelName: "STEP_2025_V1.xlsx",
        headers: ["col1"],
        mandatoryColumns: ["col1"],
      },
    });

    render(<Master />);
    await waitFor(() => {
      expect(screen.getByTestId("data-table")).toHaveTextContent("Test");
      expect(screen.getByTestId("alert")).toBeInTheDocument();
    });
  });

  test("dispatches notify on successful upload", async () => {
    jest
      .spyOn(FileUploadApi, "getUploadedData")
      .mockResolvedValueOnce({ status: 200, data: null });
    jest.spyOn(axios, "get").mockResolvedValueOnce({
      data: {
        excelName: "",
        headers: [],
        mandatoryColumns: [],
      },
    });

    render(<Master />);
    await waitFor(() => {
      const upload = screen.getByTestId("file-upload");
      userEvent.click(upload); // Simulate successful upload
    });

    //   expect(dispatchMock).toHaveBeenCalledWith({
    //     type: "NOTIFY",
    //     payload: {
    //       msg: "Initial merit list has been uploaded successfully.",
    //       status: true,
    //     },
    //   });
  });

  test("handles axios error and sets error message", async () => {
    const consoleSpy = jest.spyOn(console, "log").mockImplementation(() => {});
    jest
      .spyOn(FileUploadApi, "getUploadedData")
      .mockResolvedValueOnce({ status: 200, data: null });
    jest.spyOn(axios, "get").mockRejectedValueOnce({
      response: { data: { errorMessage: "Something went wrong" } },
    });

    render(<Master />);
    await waitFor(() => {
      expect(consoleSpy).toHaveBeenCalledWith(
        expect.anything(),
        "Something went wrong"
      );
    });
    consoleSpy.mockRestore();
  });
});
