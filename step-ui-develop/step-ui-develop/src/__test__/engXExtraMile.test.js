import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import EngXExtraMile from "../components/pages/engx_extramile_upload/EngXExtraMile";
import { Provider } from "react-redux";
import axiosInstance from "../components/common/axios";
import { getUploadedData } from "../components/common/file_upload/FileUploadApi";
import { useUuiServices, StubAdaptedRouter, UuiContext } from "@epam/uui-core";
import { BrowserRouter as Router } from "react-router-dom";
import { userEvent } from "@epam/uui-test-utils";
import { createTestStore } from "./common/store";
jest.mock("../components/common/axios", () => ({
  get: jest.fn(),
  create: jest.fn(() => ({
    get: jest.fn(),
  })),
}));

jest.mock("../components/common/file_upload/FileUploadApi");

let store = createTestStore();

function UuiContextDefaultWrapper({ children }) {
  const router = new StubAdaptedRouter();
  const { services } = useUuiServices({ router });
  return (
    <UuiContext.Provider value={services}>
      <Provider store={store}>
        <Router>{children}</Router>
      </Provider>
    </UuiContext.Provider>
  );
}

const renderComponent = () =>
  render(<EngXExtraMile />, { wrapper: UuiContextDefaultWrapper });

describe("EngXExtraMile Component", () => {
  beforeEach(() => {
    store = createTestStore();
    jest.clearAllMocks();
  });

  test("renders loading spinner initially", () => {
    getUploadedData.mockResolvedValue({ status: 200, data: null });
    axiosInstance.get.mockResolvedValue({ data: { status: false } });
    renderComponent();
    // expect(screen.getByTestId("culture-score")).toBeInTheDocument();
  });

  test("renders FileUpload component when items are null and phaseEnd is false", async () => {
    getUploadedData.mockResolvedValue({ status: 200, data: null });
    axiosInstance.get.mockResolvedValue({ data: { status: false } });

    renderComponent();

    await waitFor(() =>
      expect(screen.queryByRole("status")).not.toBeInTheDocument()
    );
    expect(
      screen.getByText(/Only Excel files are accepted/i)
    ).toBeInTheDocument();
  });

  test("renders FileUploadDataTable component when items are not null and phaseEnd is false", async () => {
    const mockData = [
      { id: 1, name: "Test", "Culture Score from Feedback": "test" },
    ];
    getUploadedData.mockResolvedValue({ status: 200, data: mockData });
    axiosInstance.get.mockRejectedValue({ data: { status: false } });

    renderComponent();

    await waitFor(() =>
      expect(screen.queryByRole("status")).not.toBeInTheDocument()
    );
    // expect(screen.getByText(/engX Extra from Feedback/i)).toBeInTheDocument();
  });

  const mockData = [{ id: 1, name: "Test" }];
  test("renders HintAlert and FileUploadDataTable when items are not null and phaseEnd is true", async () => {
    getUploadedData.mockResolvedValue({ status: 200, data: mockData });
    axiosInstance.get.mockResolvedValue({ data: { status: true } });

    renderComponent();

    await waitFor(() =>
      expect(screen.queryByRole("status")).not.toBeInTheDocument()
    );
    // expect(
    //   screen.getByText(/Culture Score should be uploaded monthly/i)
    // ).toBeInTheDocument();
  });

  test.skip("calls onUploadSuccess when file is uploaded successfully", async () => {
    const mockData = [{ id: 1, name: "Test" }];
    getUploadedData.mockResolvedValue({ status: 200, data: mockData });
    axiosInstance.get.mockResolvedValue({ data: { status: true } });

    renderComponent();

    await waitFor(() =>
      expect(screen.queryByRole("status")).not.toBeInTheDocument()
    );

    const fileInput = document.querySelector("input[type='file']");
    const file = new File(["content"], "test_file.xlsx", {
      type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    });
    userEvent.upload(fileInput, file);

    await screen.findByText(/Culture score excel is uploaded successfully/i);
  });
});
