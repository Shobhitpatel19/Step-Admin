import css from "../../styling/master_excel_view/MasterExcelView.module.css";
import MasterExcelTable from "./MasterExcelTable";
import FilterBox from "./FilterBox";
import { Panel, Spinner } from "@epam/uui";
import { useEffect, useState } from "react";
import { Navbar } from "../landing_page/navigation";

import {
  setExcelVersions,
  setListForSave,
  saveUserProfiles,
  setSelectedBoxes,
  setTopTalentDTO,
  setFilteredTopTalentDTO,
  saveNoOfExcels,
} from "../../../redux/actions";
import { useDispatch, useSelector } from "react-redux";
import axiosInstance from "../../common/axios";
import MasterExcelError from "./MasterExcelError";

const MasterExcelView = () => {
  const dispatch = useDispatch();
  const [data, setData] = useState();
  const [practiceList, setPracticeList] = useState();
  const [error, setError] = useState(false);

  const requestedExcelVersion = useSelector(
    (state) => state.masterexcel.requestedExcelVersion
  );
  const userProfiles = useSelector((state) => state.masterexcel.userProfiles);
  const [loading, setLoading] = useState();

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);

      try {
        const url =
          requestedExcelVersion === null
            ? `${process.env.REACT_APP_STEP_MASTER_EXCEL_BASE_URL}`
            : `${process.env.REACT_APP_STEP_MASTER_EXCEL_BASE_URL}?fileName=${requestedExcelVersion}`;
        const response = await axiosInstance.get(url);
        const result = response.data;
        setData(result);

        if (result.ratingStatus === "COMPLETED") {
          setPracticeList("COMPLETED");
        } else {
          setPracticeList("PARTIALLY_COMPLETED");
        }

        let boxes = result.filteredTopTalentEmployees.map(
          (candidate) => candidate.UID
        );
        dispatch(setTopTalentDTO(result.topTalentEmployeeDTOList));
        dispatch(setFilteredTopTalentDTO(result.filteredTopTalentEmployees));
        dispatch(setSelectedBoxes(boxes));
        dispatch(saveNoOfExcels(result.noOfExcelVersion));
        dispatch(setExcelVersions(result.topTalentExcelVersions));
        dispatch(setListForSave(boxes));
      } catch (err) {
        if (
          err.response.data.errorMessage ===
          "Cannot find eligibility list of this year OR version"
        ) {
          setError(true);
          setLoading(false);
        }
      }
    };

    const fetchData2 = async () => {
      try {
        const url = `${process.env.REACT_APP_STEP_MASTER_EXCEL_USER_PROFILE_GET_URL}`;
        const response = await axiosInstance.get(url);
        const result = response.data;

        dispatch(saveUserProfiles(result));
        setLoading(false);
      } catch (err) {
        setError(err);
      }
    };

    fetchData();
    fetchData2();
  }, [requestedExcelVersion]);

  const renderContent = (data, isError) => {
    if (error) {
      const data =
        "Make sure that Initial Merit list, Culture score and EngX Extramile contribution files are uploaded before viewing this page.";
      return (
        <MasterExcelError data={data} isError={isError}></MasterExcelError>
      );
    } else {
      if (practiceList === "PARTIALLY_COMPLETED") {
        return (
          <MasterExcelError data={data} isError={isError}></MasterExcelError>
        );
      }
      if (practiceList === "COMPLETED") {
        return <MasterExcelTable data={data}></MasterExcelTable>;
      }
    }
  };

  return (
    <div className={css["full-container"]}>
      
      <Navbar hideContent={true} />

      <div className={css["filter-container"]}>
        {data && userProfiles && practiceList === "COMPLETED" ? (
          <Panel
            style={{
              backgroundColor: "white",
              width: "100%",
              justifyContent: "center",
            }}
          >
            <FilterBox data={data}></FilterBox>
          </Panel>
        ) : (
          ""
        )}
      </div>

      {loading === true ? (
        <Spinner rawProps={{style:{marginTop:"2%"}}}></Spinner>
      ) : (
        <div className={css["main-container"]}>
          <div className={css["data-container"]}>
            {true ? (
              <div className={css["table-container"]}>
                {renderContent(data, error)}
              </div>
            ) : (
              <div></div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default MasterExcelView;
