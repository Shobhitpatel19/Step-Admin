import * as React from "react";
import {
  LinkButton,
  FileCard,
  DropSpot,
  Text,
  FlexCell,
  FlexRow,
  ErrorAlert,
  ScrollBars,
} from "@epam/uui";
import css from "../../styling/file_upload/FileUpload.module.css";
import { useRef } from "react";
import { upload } from "./FileUploadApi";
import { useDispatch } from "react-redux";
import { notify } from "../../../redux/actions";
import DownloadExcelTemplate from "../../utils/DownloadExcel";
import Alert from "../../common/Alert";

export default function FileUpload({
  onUploadSuccess,
  dropShotContent,
  postUrl,
  requiredRegX,
}) {
  const [selectedFile, setSelectedFile] = React.useState(null);
  const dispatch = useDispatch();
  const [errorList, setErrorList] = React.useState({});
  const [displayList, setDisplayList] = React.useState(false);

  const controllerRef = useRef(null);
  const errorRef = useRef("");

  const onProgressChange = (progress) => {
    setSelectedFile((file) => {
      return { ...file, progress };
    });
  };

  const onUploadFailed = (error) => {
    setSelectedFile(null);
    errorRef.current = error;

    if (typeof error === "string") {
      setDisplayList(false);
      dispatch(notify(errorRef.current, false));
    } else {
      setErrorList(errorRef.current);
      setDisplayList(true);
    }
  };

  const deleteFile = () => {
    controllerRef.current.abort();
    setSelectedFile(null);
  };

  const uploadFile = (file) => {
    if (selectedFile) {
      controllerRef.current.abort();
    }

    controllerRef.current = upload(
      file,
      onProgressChange,
      onUploadSuccess,
      onUploadFailed,
      postUrl,
      requiredRegX
    );

    if (controllerRef.current) {
      setSelectedFile({ name: file.name, progress: 0, size: file.size });
    }
  };

  const DropShotInfoText = (
    <FlexRow>
      <FlexCell
        grow={1}
        style={{
          justifyContent: "center",
          alignItems: "center",
          display: "flex",
          flexDirection: "column",
        }}
      >
        <Text color="tertiary">
          {dropShotContent.description}
          <br />
          {dropShotContent.fileFormatEx}( Example: {dropShotContent.example})
        </Text>

        <LinkButton
          caption="Download Template Here"
          onClick={() => {
            if (dropShotContent.error === "Invalid Excel Type") {
              dispatch(notify("Error in fetching templates", false));
            } else {
              const bool = DownloadExcelTemplate(
                [dropShotContent.headers],
                dropShotContent.mandatoryColumns,
                dropShotContent.fileName
              );
              if (bool) {
                dispatch(notify("Template Downloaded Successfully", true));
              } else {
                dispatch(notify("Template Download Failed", false));
              }
            }
          }}
          size="40"
          rawProps={{
            style: { justifyContent: "center", alignItems: "center" },
          }}
        />
      </FlexCell>
    </FlexRow>
  );

  return (
    <FlexCell cx={css.container}>
      {<Alert></Alert>}

      <div className={css.divCenter}>
        <DropSpot
          cx={css.dropShot}
          accept=".xls,.xlsx"
          single={true}
          onUploadFiles={(files) => uploadFile(files[0])}
          infoText={DropShotInfoText}
        />

        {selectedFile && (
          <div className={css.attachmentBlock}>
            <FileCard
              width={1200}
              file={selectedFile}
              onClick={() => deleteFile()}
            />
          </div>
        )}
      </div>

      {displayList && (
        <div className={css.divCenter}>
          {Object.values(errorRef.current).length > 0 && (
            <ErrorAlert
              rawProps={{
                style: {
                  minHeight: "30px",
                  maxHeight: "200px",
                  display: "flex",
                  justifyContent: "normal",
                  alignItems: "normal",
                },
              }}
              onClose={() => setDisplayList(false)}
            >
              <div style={{ width: "100%", height: "100%", margin: "0px" }}>
                <ScrollBars>
                  <ol style={{ paddingLeft: "2%" }}>
                    {Object.values(errorRef.current).map((msg, index) => (
                      <li style={{ marginBottom: "5px" }} key={index}>
                        {msg}
                      </li>
                    ))}
                  </ol>
                </ScrollBars>
              </div>
            </ErrorAlert>
          )}
        </div>
      )}
    </FlexCell>
  );
}
