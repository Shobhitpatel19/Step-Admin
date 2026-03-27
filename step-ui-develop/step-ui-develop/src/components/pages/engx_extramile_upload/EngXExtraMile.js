import React, { useEffect, useState } from "react";
import FileUpload from "../../common/file_upload/FileUpload";
import css from "../engx_extramile_upload/module_css/EngXExtraMile.module.css";
import { FlexCell, Spinner, Text, HintAlert } from "@epam/uui";
import axiosInstance from "../../common/axios";
import { useDispatch } from "react-redux";
import Alert from "../../common/Alert";
import { notify , resetNotificationStatus} from "../../../redux/actions";
import { getUploadedData } from "../../common/file_upload/FileUploadApi";
import FileUploadDataTable from "../../common/file_upload/FileUploadDataTable";
import Banner from "../../common/Banner/Banner";
import { Navbar } from "../landing_page/navigation";

export default function EngXExtraMile() {
  const [loading, setLoading] = useState(true);
  const [items, setItems] = useState(null);
  const [phaseEnd, setPhaseEnd] = useState(null);
  const [alertState, setAlertState] = useState();
  const dispatch = useDispatch();
  const [error, setError] = useState();
  const [fileName, setFileName] = useState("");
  const [headers, setHeaders] = useState([]);
  const [mandatoryColumns, setmandatoryColumns] = useState([]);
  

  useEffect(() => {
    dispatch(resetNotificationStatus(false));
    const loadData = async () => {
      const response = await getUploadedData("step/employees", ["Contribution EngX Culture",
        "Contribution Extra Miles"]);
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
        .get("step/file-details?excelType=HEROES")
        .then((resp) => {
          setFileName(resp.data.excelName);
          setHeaders(resp.data.headers);
          setmandatoryColumns(resp.data.mandatoryColumns);
        })
        .catch((err) => {
          setError(err.status);
        });
    };

    loadData();
    isPhaseEnded();
    getFileName();
  }, []);

  const dropShotContent = {
    excelType: "EngX Extra Mile",
    description: "Only Excel files are accepted, and the file name must strictly follow this format.",
    fileFormatEx: "STEP_HEROES_YEAR_VERSION",
    example: "STEP_HEROES_2025_V1.xlsx",
    fileName: fileName,
    headers: headers,
    mandatoryColumns: mandatoryColumns
  }

  const onUploadSuccess = (data, isSuccess = true) => {
    if (isSuccess) {
      dispatch(notify("EngX Extra Mile Excel is uploaded successfully", true));
      setItems(data);
    } else {
      dispatch(notify("EngX Extra Mile Excel is not uploaded", false));
    }
  };

  return (
    <>
      <Navbar hideContent={true} ></Navbar>
      <FlexCell cx={css.container}>

        {<Alert></Alert>}

        {loading ? <Spinner></Spinner> : <FlexCell cx={css.internalContainer} >

          {items === null && (phaseEnd === null || phaseEnd === false) && (
            <FileUpload
              onUploadSuccess={(data) => onUploadSuccess(data, true)}
              dropShotContent={dropShotContent}
              postUrl={"/step/engx-extra-mile-rating/upload"}
              requiredRegX={/^STEP_HEROES_\d{4}_V\d+\.(xlsx|xls)$/}
            />
          )}

          {items && (phaseEnd === null || phaseEnd === false) && (
            <FileUploadDataTable items={items} appendColumn={["Contribution EngX Culture",
              "Contribution Extra Miles"]} />
          )}

          {items && phaseEnd && (
            <FlexCell>
              {alertState && (
                <HintAlert onClose={() => setAlertState(false)}>
                  <Text size="30">
                    {" "}
                    Engx and Extramile should be uploaded monthly
                  </Text>
                </HintAlert>
              )}
              <FileUpload
                onUploadSuccess={(data) => onUploadSuccess(data, true)}
                dropShotContent={dropShotContent}
                postUrl={"/step/engx-extra-mile-rating/upload"}
                requiredRegX={/^STEP_HEROES_\d{4}_V\d+\.(xlsx|xls)$/}
              />
              <FileUploadDataTable items={items} appendColumn={["Contribution EngX Culture",
                "Contribution Extra Miles"]} />
            </FlexCell>
          )}

        </FlexCell>
        }

      </FlexCell>
    </>

  );
}
