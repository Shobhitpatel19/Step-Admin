import css from "../../styling/master_excel_view/MasterExcelView.module.css";
import css2 from "../../styling/PracticeHeadPage.module.css";
import { Avatar, DataTable, TabButton, Button, FlexRow, Text } from "@epam/uui";
import { useArrayDataSource, useUuiContext } from "@epam/uui-core";
import { useState, useMemo, useEffect } from "react";
import { ReactComponent as FileDownloadOutlineOptIcon } from "@epam/assets/icons/file-download-outline-opt.2.svg";
import { useDispatch, useSelector } from "react-redux";
import axiosInstance from "../../common/axios";
import {
  notify,
  setFilteredTopTalentDTO,
  setListForSave,
} from "../../../redux/actions";
import Alert from "../../common/Alert";
import { ConfirmationModal } from "../../common/ConfirmationModal";
import DownloadExcelTemplate from '../../utils/DownloadExcel';

const MasterExcelTable = ({ data }) => {
  const [activeTab, setActiveTab] = useState("Top Talent Candidates");
  const [freeze, setFreeze] = useState();
  const dispatch = useDispatch();
  const dataToDisplay = useSelector((state) => state.masterexcel.tableData);
  const staticTopTalent = useSelector(
    (state) => state.masterexcel.topTalentDTO
  );
  const [displayData, setDisplayData] = useState(staticTopTalent);
  const selected = useSelector((state) => state.masterexcel.selectedBoxes);
  const filteredTopTalent = useSelector(
    (state) => state.masterexcel.filteredToptalentDTO
  );
  const savingList = useSelector((state) => state.masterexcel.listForSaving);
  const userProfiles = useSelector((state) => state.masterexcel.userProfiles);
  const currentExcel = useSelector((state) => state.masterexcel.currentExcelVersion);

  const [displayFilteredData, setDisplayFilteredData] =
    useState(filteredTopTalent);

  useEffect(() => {
    if (dataToDisplay === null) {
      setDisplayData(staticTopTalent);
    } else {
      setDisplayData(dataToDisplay);
    }
  }, [staticTopTalent, dataToDisplay]);

  useEffect(() => {
    setDisplayFilteredData(filteredTopTalent);
  }, [filteredTopTalent]);

  const desiredKeys = [
    "UID",
    "Name",
    "Competency Practice",
    "Contribution EngX Culture",
    "Contribution Extra Miles",
    "Culture Score from Feedback",
    "Practice Rating",
    "Delivery Feedback TT Score",
    "Overall Weighted Score for Merit",
    "Ranking",
  ];

  function transformDataWithDesiredKeys(data, desiredKeys) {
    if (!Array.isArray(data) || data.length === 0 || !Array.isArray(desiredKeys) || desiredKeys.length === 0) {
      return [];
    }

    const result = [desiredKeys];

    for (const item of data) {
      const values = desiredKeys.map(key => item.hasOwnProperty(key) ? item[key] : null);
      result.push(values);
    }

    return result;
  }

  const handleToggle = (btn) => {
    if (activeTab !== btn) {
      setActiveTab(btn);
    }
  };

  const [filteredDataSourceState, setFilteredDataSource] = useState({});
  const filteredDataSource = useArrayDataSource(
    {
      items: displayFilteredData,
      getId: (item) => item.UID,
    },
    []
  );
  const filteredview = filteredDataSource.useView(
    filteredDataSourceState,
    setFilteredDataSource,
    {}
  );
  const filteredColumns = useMemo(
    () => {
      return Object.keys(staticTopTalent[0]).filter((key) => desiredKeys.includes(key)).map((key) => ({
        key,
        caption: (
          <div style={{
            whiteSpace: "normal",
            wordWrap: "break-word",
            overflow: "hidden",
            textAlign: "center",
            lineHeight: "1.2",
            margin: "0",
            padding: "0",
          }}>
            {({
              "Delivery Feedback TT Score": "Delivery Feedback Score",
              "Contribution Extra Miles": "Extra Miles Contribution",
              "Contribution EngX Culture": "EngX Contribution",
              "Overall Weighted Score for Merit": "Merit Score",
              "Culture Score from Feedback" : "Culture Score"
            }[key] || key)}
          </div>
        ),
        render: (item) =>
          key === "Name" ? (

            <div style={{ display: "flex", alignItems: "center", justifyContent: "center", width: "100%" }}>
              <div className={css2["user-profile"]} style={{ width: "100%", justifyContent: "space-around", alignItems: "center" }} >
                <div style={{ flex: "0.1", alignSelf: "center" }} >
                  <Avatar alt="avatar" img={userProfiles[(item.UID)].photo} size="36" />
                </div>
                <div style={{ display: "flex", flexDirection: "column", paddingLeft: "7.5%", flex: "0.9", justifyContent: "left", alignItems: "flex-start", overflow: "hidden" }}>
                  <Text color="info" fontSize="18" fontWeight="600">
                    {userProfiles[(item.UID)].firstName + " " + userProfiles[(item.UID)].lastName}
                  </Text>
                  <Text color="primary" fontSize="14" fontWeight="400" rawProps={{ style: { margin: "0px", padding: "0px" } }}>
                    {userProfiles[(item.UID)].jobDesignation}
                  </Text>
                </div>
              </div>
            </div>

          ) : (
            <Text color="primary">{item[key]}</Text>
          ),
          width: key === "Name" ? 280 : 150,
          textAlign: key === "Name" ? "left" : "center",
          allowResizing: true,
          fix: key === "Ranking" || key === "Overall Weighted Score for Merit"
            ? "right"
            : key === "Name"
              ? "left"
              : "",
          isSortable: key === "Ranking" ? true : false,

      }))
    },
    [filteredTopTalent],
  );

  const [unFilteredDataSourceState, setUnFilteredDataSource] = useState({
    topIndex: 0,
    visibleCount: displayData.length,
    checked: selected,
  });
  const unFilteredDataSource = useArrayDataSource(
    {
      items: displayData,
      getId: (item) => item.UID,
    },
    []
  );


  const unFilteredview = unFilteredDataSource.useView(
    unFilteredDataSourceState,
    setUnFilteredDataSource,
    {
      getRowOptions: (row) => ({
        checkbox: {
          isVisible:
            data.submissionStatus === "D" || data.submissionStatus === "NA"
              ? true
              : false,
        },
      }),
    }
  );

  useEffect(() => {
    setUnFilteredDataSource((prevState) => ({
      ...prevState,
      checked: selected,
    }));
  }, [selected]);

  useEffect(() => {
    if (freeze !== "S") {
      dispatch(setListForSave(unFilteredDataSourceState.checked));
    }
  }, [unFilteredDataSourceState.checked]);

  useEffect(() => {
    if (data.submissionStatus !== "S") {
      handleDraft("D");
    }
  }, [savingList]);

  const unFilteredColumns = useMemo(
    () =>
      Object.keys(staticTopTalent[0]).filter((key) => desiredKeys.includes(key)).map((key) => ({
        key,
        caption: (
          <div style={{
            whiteSpace: "normal",
            wordWrap: "break-word",
            overflow: "hidden",
            textAlign: "center",
            lineHeight: "1.2",
            margin: "0",
            padding: "0",
          }}>
            {({
              "Delivery Feedback TT Score": "Delivery Feedback Score",
              "Contribution Extra Miles": "Extra Miles Contribution",
              "Contribution EngX Culture": "EngX Contribution",
              "Overall Weighted Score for Merit": "Merit Score",
              "Culture Score from Feedback" : "Culture Score"
            }[key] || key)}
          </div>
        ),
        render: (item) =>
          key === "Name" ? (
            <div style={{ display: "flex", alignItems: "center", justifyContent: "center", width: "100%" }}>
              <div className={css2["user-profile"]} style={{ width: "100%", justifyContent: "space-around", alignItems: "center" }} >
                <div style={{ flex: "0.1", alignSelf: "center" }} >
                  <Avatar alt="avatar" img={userProfiles[(item.UID)].photo} size="36" />
                </div>
                <div style={{ display: "flex", flexDirection: "column", paddingLeft: "7.5%", flex: "0.9", justifyContent: "left", alignItems: "flex-start", overflow: "hidden" }}>
                  <Text color="info" fontSize="18" fontWeight="600">
                    {userProfiles[(item.UID)].firstName + " " + userProfiles[(item.UID)].lastName}
                  </Text>
                  <Text color="primary" fontSize="14" fontWeight="400" rawProps={{ style: { margin: "0px", padding: "0px" } }}>
                    {userProfiles[(item.UID)].jobDesignation}
                  </Text>
                </div>
              </div>
            </div>
          ) : (
            <Text color="primary">{item[key]}</Text>
          ),
        width: key === "Name" ? 280 : 150,
        textAlign: key === "Name" ? "left" : "center",
        allowResizing: true,
        fix: key === "Ranking" || key === "Overall Weighted Score for Merit"
          ? "right"
          : key === "Name"
            ? "left"
            : "",
        isSortable: key === "Ranking" ? true : false,
      })),

    [staticTopTalent],
  );


  const handleDraft = async (type) => {
    if (data.ratingStatus === "COMPLETED") {
      var payload;
      if (savingList === undefined || savingList === null) {
        payload = [];
      } else {
        payload = savingList;
      }
      const response = await axiosInstance.post(
        `${process.env.REACT_APP_STEP_MASTER_EXCEL_POST_URL}?submissionStatus=${type}`,
        payload,
        { headers: { "Content-Type": "application/json" } }
      );
      if (response.status === 200) {
        if (type === "S") {
          setFreeze("S");
          dispatch(notify("Candidates Shortlisted Successfully.", true));
        }
        dispatch(
          setFilteredTopTalentDTO(response.data.filteredTopTalentEmployees)
        );
      }
      else {
        dispatch(notify("some error", false));
      }
    }
  };

  const { uuiModals } = useUuiContext();
  return (
    <div>
      <div className={css["buttons-container"]}>
        <Alert></Alert>
        <FlexRow
          justifyContent="space-between"
          borderBottom="true"
          rawProps={{ style: { width: "100%" } }}
        >
          <FlexRow>
            <TabButton
              onClick={() => {
                handleToggle("Top Talent Candidates");
              }}
              rawProps={
                activeTab === "Top Talent Candidates"
                  ? { style: { backgroundColor: "#FAFAFC" } }
                  : ""
              }
              isLinkActive={
                activeTab === "Top Talent Candidates" ? true : false
              }
              caption="Top Talent Candidates"
              size="60"
            />
            <TabButton
              onClick={() => {
                handleToggle("Filtered Top Talent Candidates");
              }}
              rawProps={
                activeTab === "Filtered Top Talent Candidates"
                  ? { style: { backgroundColor: "#FAFAFC" } }
                  : ""
              }
              isLinkActive={
                activeTab === "Filtered Top Talent Candidates" ? true : false
              }
              caption="Filtered Top Talent Candidates"
              size="60"
              count={filteredTopTalent.length}
            />
          </FlexRow>

          <FlexRow
            rawProps={{ style: { width: "15%", justifyContent: "flex-end" } }}
          >
            <Button fill="outline" color="secondary" icon={FileDownloadOutlineOptIcon} caption="Download" rawProps={{ style: { marginRight: "10%", background: "rgba(245, 246, 250, 1)" } }}
              onClick={() => {
                const templateName = activeTab === "Top Talent Candidates"
                  ? "Top Talent Candidates"
                  : "Filtered Top Talent Candidates";

                const dataToTransform = activeTab === "Top Talent Candidates"
                  ? staticTopTalent
                  : filteredTopTalent;
                DownloadExcelTemplate(transformDataWithDesiredKeys(dataToTransform, desiredKeys), [], `${templateName} (${currentExcel})`);

                dispatch(notify("Excel generated successfully", true));
              }}
            ></Button>
            {savingList &&
              activeTab === "Filtered Top Talent Candidates" &&
              savingList.length > 0 &&
              (data.submissionStatus === "D" || data.submissionStatus == "NA"
                ? true
                : false) ? (
              <Button
                caption="Freeze"
                isDisabled={freeze === "S" ? true : false}
                onClick={() => {
                  uuiModals
                    .show((props) => (
                      <ConfirmationModal
                        title={"Would to like to freeze the list?"}
                        description={"You cannot edit once you freeze the list"}
                        {...props}
                      />
                    ))
                    .then((result) => {
                      handleDraft("S");
                    })
                    .catch(() => { });
                }}
                rawProps={{ style: { marginRight: "10%" } }}
              ></Button>
            ) : null}
          </FlexRow>
        </FlexRow>
      </div>

      <div
        className={css["table"]}
        style={{
          boxShadow: "#0000000D",
          height: "100%",
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
        }}
      >
        <div style={{ height: "55vh", width: "100%" }}>
          {activeTab === "Filtered Top Talent Candidates" ? (
            <DataTable
              {...filteredview.getListProps()}
              getRows={filteredview.getVisibleRows}
              value={filteredDataSourceState}
              onValueChange={setFilteredDataSource}
              columns={filteredColumns}
              headerTextCase="upper"
            />
          ) : (
            <DataTable
              {...unFilteredview.getListProps()}
              getRows={unFilteredview.getVisibleRows}
              value={unFilteredDataSourceState}
              onValueChange={setUnFilteredDataSource}
              columns={unFilteredColumns}
              headerTextCase="upper"
            />
          )}
        </div>
      </div>
    </div>
  );
};

export default MasterExcelTable;