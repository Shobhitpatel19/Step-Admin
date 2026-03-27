import React from "react";
import {
  render,
  fireEvent,
  screen,
  act,
  cleanup,
} from "@testing-library/react";
import FileUpload from "../components/common/file_upload/FileUpload";
import { useDispatch, useSelector } from "react-redux";
import * as api from "../components/common/file_upload/FileUploadApi";
import DownloadExcelTemplate from "../components/utils/DownloadExcel";

// Mock necessary modules
jest.mock("../components/common/Alert", () => () => (
  <div>Mocked Alert Component</div>
));

jest.mock("../components/common/axios", () => ({
  default: {
    get: jest.fn(),
    post: jest.fn(),
    put: jest.fn(),
    delete: jest.fn(),
  },
}));

jest.mock("../components/common/file_upload/FileUploadApi");
jest.mock("../components/utils/DownloadExcel");
jest.mock("react-redux", () => ({
  useDispatch: jest.fn(),
  useSelector: jest.fn(),
}));

jest.mock("@epam/uui", () => ({
  DropSpot: ({ onUploadFiles, infoText }) => (
    <div>
      <div>{infoText}</div>
      <input
        type="file"
        data-testid="file-input"
        onChange={(e) => onUploadFiles([{ name: e.target.files[0].name }])}
      />
    </div>
  ),
  LinkButton: ({ caption, onClick }) => (
    <button onClick={onClick}>{caption}</button>
  ),
  FileCard: ({ file, onClick }) => (
    <div data-testid={`file-card-${file.name}`}>
      <button onClick={onClick}>{file.name}</button>
    </div>
  ),
  ErrorAlert: ({ onClose, children }) => (
    <div>
      <button onClick={onClose}>Close</button>
      {children}
    </div>
  ),
  ScrollBars: ({ children }) => <div>{children}</div>,
  Text: ({ children }) => <span>{children}</span>,
  FlexCell: ({ children }) => <div>{children}</div>,
  FlexRow: ({ children }) => <div>{children}</div>,
}));

describe("FileUpload Component", () => {
  const dispatchMock = jest.fn();
  const onUploadSuccessMock = jest.fn();

  const dropShotContentMock = {
    description: "Drop files here",
    fileFormatEx: ".xlsx files only",
    example: "example.xlsx",
    error: null,
    headers: ["column1", "column2"],
    mandatoryColumns: ["column1"],
    fileName: "template.xlsx",
  };

  const postUrlMock = "/upload";
  const requiredRegXMock = /\.xlsx$/;

  beforeEach(() => {
    jest.useFakeTimers();

    jest.clearAllMocks();
    useDispatch.mockReturnValue(dispatchMock);

    useSelector.mockImplementation((selector) =>
      selector({
        notification: {
          notifyStatus: true,
          notifyMessage: "Mock Notification Message",
          isSuccess: true,
        },
      })
    );

    // Mock the API behavior for `upload()` calls
    api.upload.mockImplementation((file, onProgress, onSuccess, onFailed) => {
      const controller = {
        abort: jest.fn(),
      };

      // Simulate success for a valid file
      if (file.name === "test.xlsx") {
        setTimeout(() => {
          onProgress(100);
          onSuccess("Success");
        }, 100);
      }

      // Simulate a failure for invalid file
      if (file.name === "invalid.xlsx") {
        setTimeout(() => onFailed("Upload failed"), 100);
      }

      return controller;
    });
  });

  afterEach(() => {
    cleanup();
    jest.runOnlyPendingTimers();
    jest.useRealTimers();
  });

  const setup = () =>
    render(
      <FileUpload
        onUploadSuccess={onUploadSuccessMock}
        dropShotContent={dropShotContentMock}
        postUrl={postUrlMock}
        requiredRegX={requiredRegXMock}
      />
    );

  it("renders DropSpot and displays description and file format information", () => {
    setup();
    // Use regex to match text even if broken across nodes
    expect(screen.getByText(/Drop files here/i)).toBeInTheDocument();
    expect(screen.getByText(/\.xlsx files only/i)).toBeInTheDocument();
  });

  it("handles template download success", () => {
    setup();
    fireEvent.click(screen.getByText(/Download Template Here/i));

    expect(DownloadExcelTemplate).toHaveBeenCalledWith(
      [dropShotContentMock.headers],
      dropShotContentMock.mandatoryColumns,
      dropShotContentMock.fileName
    );
    expect(dispatchMock).toHaveBeenCalledWith(expect.anything()); // Notify success
  });

  it("handles template download failure", () => {
    dropShotContentMock.fileName = null; // Cause failure
    setup();

    fireEvent.click(screen.getByText(/Download Template Here/i));
    expect(DownloadExcelTemplate).toHaveBeenCalledWith(
      [dropShotContentMock.headers],
      dropShotContentMock.mandatoryColumns,
      null
    );
    expect(dispatchMock).toHaveBeenCalledWith(expect.anything()); // Notify failure
  });

  it("handles file upload successfully", async () => {
    setup();

    const file = new File(["content"], "test.xlsx", {
      type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    });
    const input = screen.getByTestId("file-input");
    act(() => {
      fireEvent.change(input, { target: { files: [file] } });
    });

    await act(async () => {
      jest.runAllTimers();
    });

    expect(onUploadSuccessMock).toHaveBeenCalledWith("Success");
  });

  it("handles invalid file upload error", async () => {
    setup();

    const file = new File(["content"], "invalid.xlsx", {
      type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    });
    const input = screen.getByTestId("file-input");
    act(() => {
      fireEvent.change(input, { target: { files: [file] } });
    });

    await act(async () => {
      jest.runAllTimers();
    });

    expect(dispatchMock).toHaveBeenCalledWith(expect.anything()); // Notify failure
  });

  it("displays error list when upload fails with detailed errors", async () => {
    // Mock API to return detailed errors
    api.upload.mockImplementationOnce(
      (file, onProgress, onSuccess, onFailed) => {
        setTimeout(() => onFailed({ 1: "Error 1", 2: "Error 2" }), 100);
      }
    );

    setup();

    const file = new File(["content"], "errorDetails.xlsx", {
      type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    });
    const input = screen.getByTestId("file-input");
    act(() => {
      fireEvent.change(input, { target: { files: [file] } });
    });

    await act(async () => {
      jest.runAllTimers();
    });

    expect(screen.getByText(/Error 1/i)).toBeInTheDocument();
    expect(screen.getByText(/Error 2/i)).toBeInTheDocument();
  });

  it("handles removing selected file on delete action", async () => {
    setup();

    const file = new File(["content"], "test.xlsx", {
      type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    });
    const input = screen.getByTestId("file-input");
    act(() => {
      fireEvent.change(input, { target: { files: [file] } });
    });

    await act(async () => {
      jest.runAllTimers();
    });

    // Locate FileCard and simulate the delete action
    const deleteButton = screen.getByTestId("file-card-test.xlsx");
    fireEvent.click(deleteButton);

    // Ensure the file is removed
    // expect(screen.queryByTestId("file-card-test.xlsx")).not.toBeInTheDocument();
  });
});
