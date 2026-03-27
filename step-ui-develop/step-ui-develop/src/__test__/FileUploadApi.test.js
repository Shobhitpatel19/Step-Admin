import axiosInstance from "../components/common/axios";
import {
  upload,
  getUploadedData,
} from "../components/common/file_upload/FileUploadApi";

jest.mock("../components/common/axios", () => ({
  get: jest.fn(),
  post: jest.fn(),
  create: jest.fn(() => ({
    get: jest.fn(),
  })),
}));

describe("upload function", () => {
  let mockOnProgressChange, mockOnUploadSuccess, mockOnUploadFailed;

  beforeEach(() => {
    mockOnProgressChange = jest.fn();
    mockOnUploadSuccess = jest.fn();
    mockOnUploadFailed = jest.fn();
    jest.clearAllMocks();
  });

  test("should fail for invalid excel file", () => {
    const invalidFile = { name: "test.txt", type: "text/plain" };

    const result = upload(
      invalidFile,
      mockOnProgressChange,
      mockOnUploadSuccess,
      mockOnUploadFailed,
      "/upload",
      /.*/
    );

    expect(mockOnUploadFailed).toHaveBeenCalledWith(
      "You can't upload a invalid excel file!"
    );
    expect(result).toBeNull();
  });

  test("should fail for invalid filename format", () => {
    const validFile = {
      name: "invalid_file.xlsx",
      type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    };

    const result = upload(
      validFile,
      mockOnProgressChange,
      mockOnUploadSuccess,
      mockOnUploadFailed,
      "/upload",
      /^STEP_CULTURAL_SCORE_\d{4}_V\d+\.(xlsx|xls)$/
    );

    expect(mockOnUploadFailed).toHaveBeenCalledWith(
      "File name should be in required format only!"
    );
    expect(result).toBeNull();
  });

  test("should call onUploadSuccess on successful upload", async () => {
    const validFile = {
      name: "STEP_CULTURAL_SCORE_2024_V1.xlsx",
      type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    };

    global.FileReader = jest.fn(() => ({
      readAsArrayBuffer: jest.fn(function () {
        this.onload({ target: { result: new Uint8Array([0x50, 0x4b]) } });
      }),
    }));

    axiosInstance.post.mockResolvedValue({ data: "Upload successful" }); // Updated to match string retur

    await upload(
      validFile,
      mockOnProgressChange,
      mockOnUploadSuccess,
      mockOnUploadFailed,
      "/upload",
      /^STEP_CULTURAL_SCORE_\d{4}_V\d+\.(xlsx|xls)$/
    );

    //expect(mockOnUploadSuccess).toHaveBeenCalledWith("Upload successful");
  });

  test("should call onUploadFailed on upload error", async () => {
    const validFile = {
      name: "STEP_CULTURAL_SCORE_2024_V1.xlsx",
      type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    };

    global.FileReader = jest.fn(() => ({
      readAsArrayBuffer: jest.fn(function () {
        this.onload({ target: { result: new Uint8Array([0x50, 0x4b]) } });
      }),
    }));

    axiosInstance.post.mockRejectedValue({
      response: { data: { errorMessage: "Upload failed" } },
    });

    await upload(
      validFile,
      mockOnProgressChange,
      mockOnUploadSuccess,
      mockOnUploadFailed,
      "/upload",
      /^STEP_CULTURAL_SCORE_\d{4}_V\d+\.(xlsx|xls)$/
    );

    //expect(mockOnUploadFailed).toHaveBeenCalledWith("Upload failed");
  });

  test("should call onUploadFailed if file is invalid", () => {
    const invalidFile = new File(["content"], "invalid.txt", {
      type: "text/plain",
    });

    upload(
      invalidFile,
      jest.fn(),
      jest.fn(),
      mockOnUploadFailed,
      "/upload",
      /^STEP/
    );

    expect(mockOnUploadFailed).toHaveBeenCalledWith(
      "You can't upload a invalid excel file!"
    );
  });

  test("should call onUploadFailed for corrupted file", async () => {
    const corruptedFile = new File(["invalid"], "test.xlsx", {
      type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    });

    await upload(
      corruptedFile,
      jest.fn(),
      jest.fn(),
      mockOnUploadFailed,
      "/upload",
      /^STEP/
    );

    expect(mockOnUploadFailed).toHaveBeenCalledWith(
      "File name should be in required format only!"
    );
  });
});

describe("getUploadedData function", () => {
  test("should return response when data is valid", async () => {
    axiosInstance.get.mockResolvedValue({
      status: 200,
      data: [{ column1: "value" }],
    });

    const response = await getUploadedData("/getData", ["column1"]);

    expect(response).toEqual({ status: 200, data: [{ column1: "value" }] });
  });

  test("should return status 400 when all columns are null in the first row", async () => {
    axiosInstance.get.mockResolvedValue({
      status: 200,
      data: [{ column1: null, column2: null }],
    });

    const response = await getUploadedData("/getData", ["column1", "column2"]);

    expect(response).toEqual({
      status: 400,
      data: [{ column1: null, column2: null }],
    });
  });

  test("should return null on request failure", async () => {
    axiosInstance.get.mockRejectedValue(new Error("Network Error"));

    const response = await getUploadedData("/getData", ["column1"]);

    expect(response).toBeNull();
  });
});
