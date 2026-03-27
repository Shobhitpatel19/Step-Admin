import React, { useEffect, useState } from "react";
import FileUpload from "../../common/file_upload/FileUpload";
import css from "../culture_upload/module_css/CultureScore.module.css";
import { Spinner, FlexCell, Text, HintAlert } from "@epam/uui";
import axiosInstance from "../../common/axios";
import { useDispatch } from "react-redux";
import Alert from "../../common/Alert";
import { notify, resetNotificationStatus } from "../../../redux/actions";
import { getUploadedData } from "../../common/file_upload/FileUploadApi";
import FileUploadDataTable from "../../common/file_upload/FileUploadDataTable";
import { Navbar } from "../landing_page/navigation";


export default function CultureScore() {
  const [loading, setLoading] = useState(true);
  const [items, setItems] = useState(null);
  const [phaseEnd, setPhaseEnd] = useState(null)
  const [alertState, setAlertState] = useState();
  const [error, setError] = useState();
  const dispatch = useDispatch();
  const [fileName, setFileName] = useState("");
  const [headers, setHeaders] = useState([]);
  const [mandatoryColumns, setmandatoryColumns] = useState([]);
  useEffect(() => {
    dispatch(resetNotificationStatus(false));
    const loadData = async () => {
      const response = await getUploadedData("step/employees", ["Culture Score from Feedback"]);
      if (response) {
        if (response.status) {
          if (response.status === 200) {
            setItems(response.data);
            console.log(response.data);
          }
        }
      }
      setLoading(false);
    };
    const isPhaseEnded = async () => {
      await axiosInstance
        .get("step/identification/isended")
        .then((resp) => {
          setPhaseEnd(resp.data["status"]);
        })
        .catch((err) => {
          setError(err.status);
        });
    };
    const getFileName = async () => {
      await axiosInstance
        .get("step/file-details?excelType=CULTURAL_SCORE")
        .then((resp) => {
          setFileName(resp.data.excelName);
          setHeaders(resp.data.headers);
          setmandatoryColumns(resp.data.mandatoryColumns);
          if(resp.data.excelName === "Invalid Excel Type"){
            dispatch(notify("Error in fetching templates", false));
          }
        })
        .catch((err) => {
          setError(err.status);
          dispatch(notify("Error in fetching templates", false));
        });
    };
    loadData();
    isPhaseEnded();
    getFileName();
  }, []);

  const onUploadSuccess = (data, isSuccess = true) => {
    if (isSuccess) {
      dispatch(notify("Culture score excel is uploaded successfully", true));
      setItems(data);
    } else {
      dispatch(notify("Culture score excel is not uploaded", false));
    }
  };
 
  const dropShotContent = {
    excelType: "CULTURE SCORE", 
    description: "Only Excel files are accepted, and the file name must strictly follow this format.",
    fileFormatEx: "STEP_CULTURAL_SCORE_YEAR_VERSION",
    example: "STEP_CULTURAL_SCORE_2025_V1.xlsx",
    fileName: fileName,
    headers: headers,
    mandatoryColumns: mandatoryColumns
  }

  return (

    <>

      <Navbar hideContent={true} ></Navbar>

      <FlexCell cx={css.container}>
        {<Alert></Alert>}
        
        {loading ? <Spinner></Spinner> :

          <FlexCell cx={css.internalContainer}>

            {items === null && (phaseEnd === null || phaseEnd === false) && (
              <FileUpload
                onUploadSuccess={(data) => onUploadSuccess(data, true)}
                dropShotContent={dropShotContent}
                postUrl={"/step/cultural-score/upload"}
                requiredRegX={/^STEP_CULTURAL_SCORE_\d{4}_V\d+\.(xlsx|xls)$/}
              />
            )}

            {items && (phaseEnd === null || phaseEnd === false) && (
              <FileUploadDataTable items={items} appendColumn={["Culture Score from Feedback"]} />
            )}

            {items && phaseEnd && (
              <FlexCell>
                {alertState && (
                  <HintAlert onClose={() => setAlertState(false)}>
                    <Text size="30">
                      {" "}
                      Culture Score should be uploaded monthly
                    </Text>
                  </HintAlert>
                )}
                <FileUpload
                  onUploadSuccess={(data) => onUploadSuccess(data, true)}
                  dropShotContent={dropShotContent}
                  postUrl={"/step/cultural-score/upload"}
                  requiredRegX={/^STEP_CULTURAL_SCORE_\d{4}_V\d+\.(xlsx|xls)$/}
                />
                <FileUploadDataTable items={items} appendColumn={["Culture Score from Feedback"]} />
              </FlexCell>
            )}
          </FlexCell>
        }

      </FlexCell>

    </>


  );
}
