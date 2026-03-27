// import React, { useEffect, useState } from "react";
// import { useDispatch } from "react-redux";
// import { notify, resetNotificationStatus } from "../../../redux/actions";
// import Alert from "../../common/Alert";
// import css from "./Master.module.css";
// import { FlexCell, Spinner } from "@epam/uui";
// import { getUploadedData } from "../../common/file_upload/FileUploadApi";
// import FileUploadDataTable from "../../common/file_upload/FileUploadDataTable";
// import FileUpload from "../../common/file_upload/FileUpload";
// import { Navbar } from "../landing_page/navigation";
// import axiosInstance from "../../common/axios";

// export default function Master() {
//   const [loading, setLoading] = useState(true);
//   const [items, setItems] = useState(null);
//   const dispatch = useDispatch();
//   const [fileName, setFileName] = useState("");
//   const [headers, setHeaders] = useState([]);
//   const [error, setError] = useState("");
//   const [mandatoryColumns, setmandatoryColumns] = useState([]);

//   useEffect(() => {
//     dispatch(resetNotificationStatus(false));
//     const loadData = async () => {
//       const response = await getUploadedData("step/employees", []);
//       if (response) {
//         if (response.status) {
//           if (response.status === 200) {
//             setItems(response.data);
//           }
//         }
//       }
//       setLoading(false);
//     };
//     const getFileName = async () => {
//       await axiosInstance
//         .get("step/file-details?excelType=STEP")
//         .then((resp) => {
//           setFileName(resp.data.excelName);
//           setHeaders(resp.data.headers);
//           setmandatoryColumns(resp.data.mandatoryColumns);
//         })
//         .catch((err) => {
//           setError(err.response.data.errorMessage);
//         });
//     };
//     loadData();
//     getFileName();
//   }, []);

//   const onUploadSuccess = (data, isSuccess = true) => {
//     if (isSuccess) {
//       dispatch(
//         notify("Initial merit list has been uploaded successfully.", true)
//       );
//       setItems(data);
//     } else {
//       dispatch(notify("Initial merit list has not been uploaded.", false));
//     }
//   };
//   const dropShotContent = {
//     excelType: "STEP",
//     description:
//       "Only Excel files are accepted, and the file name must strictly follow this format.",
//     fileFormatEx: "STEP_YEAR_VERSION ",
//     example: "STEP_2025_V1.xlsx",
//     fileName: fileName,
//     headers: headers,
//     mandatoryColumns: mandatoryColumns,
//     error: error,
//   };

//   return (
//     <>
//       <Navbar hideContent={true} />
//       <FlexCell cx={css.container}>
//         {loading ? (
//           <Spinner />
//         ) : (
//           <FlexCell cx={css.internalContainer}>
//             {items ? (
//               <>
//                 <Alert></Alert>

//                 <FileUploadDataTable items={items} appendColumn={[]} />
//               </>
//             ) : (
//               <FileUpload
//                 onUploadSuccess={(data) => onUploadSuccess(data, true)}
//                 dropShotContent={dropShotContent}
//                 postUrl={"/step/upload"}
//                 requiredRegX={/^STEP_\d{4}_V\d+\.(xlsx|xls)$/}
//               />
//             )}
//           </FlexCell>
//         )}
//       </FlexCell>
//     </>
//   );
// }

import React, { useEffect, useState } from "react";
import { useDispatch } from "react-redux";
import { notify, resetNotificationStatus } from "../../../redux/actions";
import Alert from "../../common/Alert";
import css from "./Master.module.css";
import { FlexCell, Spinner } from "@epam/uui";
import { getUploadedData } from "../../common/file_upload/FileUploadApi";
import FileUploadDataTable from "../../common/file_upload/FileUploadDataTable";
import FileUpload from "../../common/file_upload/FileUpload";
import { Navbar } from "../landing_page/navigation";
import axiosInstance from "../../common/axios";

export default function Master() {
  const [loading, setLoading] = useState(true);
  const [items, setItems] = useState(null);
  const dispatch = useDispatch();
  const [fileName, setFileName] = useState("");
  const [headers, setHeaders] = useState([]);
  const [error, setError] = useState("");
  const [mandatoryColumns, setMandatoryColumns] = useState([]);

  useEffect(() => {
    dispatch(resetNotificationStatus(false));

    const loadData = async () => {
      try {
        // Added try-catch for error handling
        const response = await getUploadedData("step/employees", []);
        if (response && response.status === 200) {
          setItems(response.data); // Safely accessing response.data
        }
      } catch (err) {
        // Added error logging
        console.error("Error loading data:", err);
      } finally {
        setLoading(false); // Ensures loading is turned off
      }
    };

    const getFileName = async () => {
      try {
        // Added try-catch for error handling
        const resp = await axiosInstance.get(
          "step/file-details?excelType=STEP"
        );
        if (resp && resp.data) {
          setFileName(resp.data.excelName); // Safely accessing resp.data
          setHeaders(resp.data.headers);
          setMandatoryColumns(resp.data.mandatoryColumns);
        } else {
          setError("Invalid response structure."); // Added error message if structure is incorrect
        }
      } catch (err) {
        // Added fallback error handling for missing or malformed response
        setError(err?.response?.data?.errorMessage || "An error occurred.");
      }
    };

    loadData();
    getFileName();
  }, [dispatch]);

  const onUploadSuccess = (data, isSuccess = true) => {
    if (isSuccess) {
      dispatch(
        notify("Initial merit list has been uploaded successfully.", true)
      );
      setItems(data);
    } else {
      dispatch(notify("Initial merit list has not been uploaded.", false));
    }
  };

  const dropShotContent = {
    excelType: "STEP",
    description:
      "Only Excel files are accepted, and the file name must strictly follow this format.",
    fileFormatEx: "STEP_YEAR_VERSION ",
    example: "STEP_2025_V1.xlsx",
    fileName: fileName,
    headers: headers,
    mandatoryColumns: mandatoryColumns,
    error: error,
  };

  return (
    <>
      <Navbar hideContent={true} />
      <FlexCell cx={css.container}>
        {loading ? (
          <Spinner />
        ) : (
          <FlexCell cx={css.internalContainer}>
            {items ? (
              <>
                <Alert />
                <FileUploadDataTable items={items} appendColumn={[]} />
              </>
            ) : (
              <FileUpload
                onUploadSuccess={(data) => onUploadSuccess(data, true)}
                dropShotContent={dropShotContent}
                postUrl={"/step/upload"}
                requiredRegX={/^STEP_\d{4}_V\d+\.(xlsx|xls)$/}
              />
            )}
          </FlexCell>
        )}
      </FlexCell>
    </>
  );
}
