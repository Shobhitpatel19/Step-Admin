import axiosInstance from "../../common/axios";

export function upload(
  excelFile,
  onProgressChange,
  onUploadSuccess,
  onUploadFailed,
  postUrl,
  requiredRegX
) {
  if (!isValidExcel(excelFile)) {
    onUploadFailed("You can't upload a invalid excel file!");
    return null;
  }

  if (!isValidFilename(excelFile.name, requiredRegX)) {
    onUploadFailed("File name should be in required format only!");
    return null;
  }

  const controller = new AbortController();

  function getErrorMessage(data) {
    if (!data) return "Something went wrong, try again!";

    if (data.errors === null) {
      if (typeof data.errorMessage === "string") return data.errorMessage;
    } else if (data.errors !== null) {
      return data.errors;
    }
    if (typeof data === "string") return data;
    return "Something went wrong, try again!";
  }

  isValidExcelContent(excelFile)
    .then((isValidExcel) => {
      if (isValidExcel) {
        const formData = new FormData();
        formData.append("file", excelFile);
        axiosInstance
          .post(postUrl, formData, {
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
            if (!error) {
              return onUploadFailed("Something went wrong, try again!");
            }
            if (error.status === 413) {
              return onUploadFailed("Uploaded file size is too large!");
            }

            if (error.response?.data) {
              return onUploadFailed(getErrorMessage(error.response.data));
            }
            onUploadFailed("Server down or Session expired, try login again!");
          });
      } else {
        onUploadFailed("You can't upload a invalid or corrupted excel file!");
      }
    })
    .catch((error) => {
      onUploadFailed(error.message);
    });

  return controller;
}

export async function getUploadedData(getUrl, columnNames) {
  try {
    const response = await axiosInstance.get(getUrl);

    if (
      response?.status === 200 &&
      response.data.length > 0 &&
      columnNames.length > 0
    ) {
      const isInvalid = columnNames.every(
        (column) => response.data[0][column] === null
      );

      if (isInvalid) {
        return { ...response, status: 400 };
      }
    }
    return response;
  } catch (error) {
    return null;
  }
}
function isValidFilename(filename, requiredRegX) {
  const filenamePattern = requiredRegX;
  return filenamePattern.test(filename);
}

function isValidExcel(file) {
  const excelMimeTypes = [
    "application/vnd.ms-excel",
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
  ];

  if (!excelMimeTypes.includes(file.type)) {
    return false;
  }

  const allowedExtensions = [".xls", ".xlsx"];
  const fileExtension = file.name.split(".").pop();

  return allowedExtensions.includes(`.${fileExtension.toLowerCase()}`);
}

function isValidExcelContent(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();

    reader.onload = (event) => {
      const data = new Uint8Array(event.target.result);
      resolve(
        (data[0] === 0x50 && data[1] === 0x4b) ||
          (data[0] === 0xd0 &&
            data[1] === 0xcf &&
            data[2] === 0x11 &&
            data[3] === 0xe0)
      );
    };

    reader.onerror = () =>
      reject(new Error("Some error came while checking the file!"));
    reader.readAsArrayBuffer(file);
  });
}
