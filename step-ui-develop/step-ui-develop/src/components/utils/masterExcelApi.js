import axiosInstance from "./axiosInstance";

export function uploadCultureScore(
  excelFile,
  onProgressChange,
  onUploadSuccess,
  onUploadFailed
) {
  if (!isValidExcel(excelFile)) {
    onUploadFailed("You can't upload a invalid excel file!");
    return null;
  }

  if (!isValidFilename(excelFile.name)) {
    onUploadFailed("File name should be in required format only!");
    return null;
  }

  const formData = new FormData();
  formData.append("file", excelFile);

  const controller = new AbortController();

  axiosInstance
    .post("/step/upload", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
      signal: controller.signal,
      onUploadProgress: (progressEvent) => {
        const progress = Math.round(
          (progressEvent.loaded * 100) / progressEvent.total
        );
        onProgressChange(progress);
      },
    })
    .then((response) => {
      onUploadSuccess(response.data);
    })
    .catch((error) => {
      if (error.response) {
        onUploadFailed(error.response.data);
      } else {
        onUploadFailed("Server down!");
      }
    });

  return controller;
}

export async function getUploadedCultureScore() {
  const response = await axiosInstance.get("/step/cultural-score/employees");
  return response;
}

function isValidFilename(filename) {
  const filenamePattern = /^STEP_\d{4}_CULTURAL_SCORE_V\d+\.(xlsx|xls)$/;
  return filenamePattern.test(filename);
}

function isValidExcel(file) {
  const excelMimeTypes = [
    "application/vnd.ms-excel", // .xls
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
  ];

  // Check file MIME type
  if (!excelMimeTypes.includes(file.type)) {
    return false;
  }

  // Optionally, check the file extension
  const allowedExtensions = [".xls", ".xlsx"];
  const fileExtension = file.name.split(".").pop();
  if (!allowedExtensions.includes(`.${fileExtension.toLowerCase()}`)) {
    return false;
  }

  return true;
}
