import React, { useMemo, useState, useEffect } from "react";
import { useArrayDataSource, useUuiContext } from "@epam/uui-core";
import {
  DataTable,
  Panel,
  Text,
  Button,
  Spinner,
  SearchInput,
  PickerInput,
  Avatar,
  CountIndicator,
} from "@epam/uui";
import { NavbarForP } from "../landing_page/navigation_p";
import { FlexCell, FlexRow } from "@epam/uui-components";
import { useNavigate, useSearchParams } from "react-router-dom";
import axiosInstance from "../../common/axios";
import PracticeRatingDialog from "./PracticeRatingDialog";
import { PracticeRatingApprovalDialog } from "./PracticeRatingApprovalDialog";
import css from "./TablesExamples.module.css";
import css2 from "../../styling/PracticeHeadPage.module.css";
import css3 from "../../styling/PracticeRatingDialog.module.css";
import { useDispatch, useSelector } from "react-redux";
import Alert from "../../common/Alert";
import { decodeToken, getTokenFromCookies } from "../../utils/auth";
import { Navbar } from "../landing_page/navigation";
import { setRole, notify } from "../../../redux/actions";
import Banner from "../../common/Banner/Banner";
import DownloadExcelTemplate from "../../utils/DownloadExcel";
import { ReactComponent as FileFileExcelFillIcon } from "@epam/assets/icons/file-file_excel-fill.svg";
import SideProfile from "../../common/sideprofile/SideProfile";

const PracticeHeadPage = () => {
  const [dataSourceState, setDataSourceState] = useState({});
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isDialogOpen, setDialogOpen] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [competency, setCompetency] = useState();
  const [mean, setMean] = useState("NA");

  const [uid, setUid] = useState(true);
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [tableData, setTableData] = useState([]);
  const dispatch = useDispatch();
  const [canApprove, setCanApprove] = useState(false);
  const [canApproveAll, setCanApproveAll] = useState(false);
  const { uuiModals } = useUuiContext();
  const [isDelegate, setIsDelegate] = useState(false);
  const [competencies, setCompetencies] = useState([]);
  const [searchValue, setSearchValue] = useState("");
  const [originalUsers, setOriginalUsers] = useState([]);
  const reduxRole = useSelector((state) => state.practicerating.role);
  const [flag, setFlag] = useState(0);

  const [excelUsers, setExcelUsers] = useState([]);

  const desiredKeys = [
    "uid",
    "email",
    "fullName",
    "jobDesignation",
    "practice",
    "practiceRating",
    "submissionStatus",
  ];

  function transformDataWithDesiredKeys(data, desiredKeys) {
    if (
      !Array.isArray(data) ||
      data.length === 0 ||
      !Array.isArray(desiredKeys) ||
      desiredKeys.length === 0
    ) {
      return [];
    }
    for (const item of data) {
      if (item.submissionStatus === "D") {
        item.submissionStatus = "Not provided";
        item.practiceRating = 0;
      } else if (item.submissionStatus === "NA") {
        item.submissionStatus = "Not provided";
      } else if (item.submissionStatus === "S") {
        item.submissionStatus = "Approval pending";
      } else if (item.submissionStatus === "A") {
        item.submissionStatus = "Approved";
      }
    }
    const result = [desiredKeys];

    for (const item of data) {
      const values = desiredKeys.map((key) =>
        item.hasOwnProperty(key) ? item[key] : null
      );
      result.push(values);
    }
    return result;
  }

  useEffect(() => {
    const { isDelegate } = decodeToken(getTokenFromCookies());
    setIsDelegate(isDelegate);
  }, []);

  useEffect(() => {
    const uuid = searchParams.get("uid");
    const fetchData = async () => {
      try {
        const sessionKey = "userList";
        const storedList = sessionStorage.getItem(sessionKey);

        let fetchedUsers;

        if (storedList) {
          fetchedUsers = JSON.parse(storedList);
        }

        const response = await axiosInstance
          .get("/step/practice-rating/employees")
          .catch((err) => console.log(err));
        console.log(response.data, "reponse from praftic rating employees");
        fetchedUsers = response.data.users.map((user) => ({
          ...user,
          isSubmitted: user.submissionStatus === "S",
          isApproved: user.submissionStatus === "A",
        }));
        sessionStorage.setItem(sessionKey, JSON.stringify(fetchedUsers));
        setUsers(fetchedUsers);
        setOriginalUsers(fetchedUsers);
        setExcelUsers(fetchedUsers);

        {
          flag === 0 && setTableData(fetchedUsers);
        }

        setCompetency(response.data.competency);
        setLoading(false);

        if (uuid != null && flag === 0) {
          navigate(`/practice?uid=${uuid}`);

          const getUserByUID = (users, uuid) => {
            return users.filter((user) => user.uid == uuid);
          };

          const filteredUser = getUserByUID(fetchedUsers, uuid);

          if (filteredUser.length !== 0) {
            setDialogOpen(true);
            setSelectedUser(filteredUser[0]);
            setUid(uuid);
          } else {
            dispatch(
              notify("Candidate is not a part of your practice.", false)
            );
          }
        }

        const anySubmitted = fetchedUsers.some((user) => user.isSubmitted);
        setCanApproveAll(anySubmitted);
      } catch (error) {
        console.error("Error fetching data:", error);
        setLoading(false);
      }
    };

    fetchData();
  }, [uid, canApproveAll, flag]);

  useEffect(() => {
    const fetchData = () => {
      axiosInstance
        .get("/step/is-approval-required")
        .then((response) => {
          if (response.status === 200) {
            setCanApprove(!response.data);
          }
        })
        .catch((error) => {
          console.error("Error fetching data:", error);
        });
    };

    fetchData();
  }, []);

  const handleApproveAll = async () => {
    uuiModals
      .show((props) => <PracticeRatingApprovalDialog {...props} />)
      .then((r) => {
        axiosInstance
          .post("step/practice-rating/employees/approve-all")
          .then((response) => {
            if (response) {
              if (response.status) {
                if (response.status === 200) {
                  setCanApproveAll(false);
                }
              }
            }
          })
          .catch((error) => {
            console.log(error);
          });
      });
  };

  const handleDialogClose = (selectedUser, isSubmitted, mean, isApproved) => {
    if (isSubmitted) {
      setFlag(flag + 1);
      setUsers((prevUsers) =>
        prevUsers.map((user) =>
          user.uid === selectedUser.uid
            ? {
                ...user,
                isSubmitted: true,
                submissionStatus: "S",
                practiceRating: mean,
              }
            : user
        )
      );
    }
    if (isApproved) {
      setUsers((prevUsers) =>
        prevUsers.map((user) =>
          user.uid === selectedUser.uid
            ? {
                ...user,
                isApproved: true,
                submissionStatus: "A",
                practiceRating: mean,
              }
            : user
        )
      );
    }
  };

  useEffect(() => {
    setExcelUsers(users);
  }, users);

  useEffect(() => {
    const token = getTokenFromCookies();
    if (!token || typeof token !== "string") {
      console.error("Token is missing or invalid.");
      return;
    }

    try {
      const { role } = decodeToken(token);
      dispatch(setRole(role));
    } catch (error) {
      console.error("Error decoding token:", error.message);
    }
  }, []);

  useEffect(() => {
    const fetchCompetencies = async () => {
      try {
        const response = await axiosInstance.get(
          "step/practice-rating/competencies"
        );
        setCompetencies(response.data);
      } catch (error) {
        console.error("Error fetching competencies:", error);
      }
    };

    fetchCompetencies();
  }, []);

  const onValueChange = (value) => {
    var initialData;
    if (value.length === 0) {
      initialData = tableData;
    }
    setSearchValue(value);
    if (value.trim() === "") {
      if (
        competencyDataSource.props.items[multiPickerValue - 1] !== undefined
      ) {
        setTableData(users);
        setExcelUsers(users);
      } else {
        setTableData(originalUsers);
        setExcelUsers(originalUsers);
      }
    } else {
      const filtered = tableData.filter((row) =>
        row.fullName.toLowerCase().includes(value.toLowerCase())
      );
      setTableData(filtered);
      setExcelUsers(filtered);
    }
  };

  const competencyList = Array.isArray(competencies)
    ? competencies.map((competency, index) => ({
        id: index + 1,
        level: competency,
      }))
    : [];

  const competencyDataSource = useArrayDataSource(
    {
      items: competencyList,
      candidates: users,
    },
    []
  );

  const [multiPickerValue, multiOnValueChange] = useState(undefined);
  const handleMultiChange = (value) => {
    multiOnValueChange(value);
  };

  useEffect(() => {
    let fetchedUsers;
    const fetchPractice = async () => {
      if (
        competencyDataSource.props.items[multiPickerValue - 1] !== undefined
      ) {
        var selectedCompetency =
          competencyDataSource.props.items[multiPickerValue - 1].level;

        try {
          const response = await axiosInstance.get(
            `step/practice-rating/employees?competency=${selectedCompetency}`
          );
          fetchedUsers = response.data.users.map((user) => ({
            ...user,
            isSubmitted: user.submissionStatus === "S",
            isApproved: user.submissionStatus === "A",
          }));
          setUsers(fetchedUsers);
          setTableData(fetchedUsers);
          setExcelUsers(fetchedUsers);
        } catch (error) {
          console.error("Error fetching data for selected competency:", error);
        }
      } else {
        setTableData(originalUsers);
        setExcelUsers(originalUsers);
      }
    };
    fetchPractice();
  }, [multiPickerValue, originalUsers]);

  const dataSource = useArrayDataSource(
    {
      items: tableData,
      getId: (item) => item.uid,
    },
    [users]
  );

  const view = dataSource.useView(dataSourceState, setDataSourceState, {});
  const handleProfileDrawerOpen = (user) => {
    console.log("Opening drawer for:", user.email);
    setSelectedUser(user);
    setDrawerOpen(true);
  };

  const productColumns = useMemo(
    () => [
      {
        key: "name",
        caption: "Name",
        render: (item) => (
          <div
            onClick={() => handleProfileDrawerOpen(item)}
            style={{
              display: "flex",
              alignItems: "center",
              cursor: "pointer",
              width: "100%",
              padding: "5px 0",
            }}
          >
            <div
              className={css2["user-profile"]}
              style={{
                display: "flex",
                alignItems: "center",
                width: "100%",
              }}
            >
              <Avatar alt="avatar" img={item.photo} size="30" />

              <div
                style={{
                  display: "flex",
                  flexDirection: "column",
                  paddingLeft: "10px",
                  overflow: "hidden",
                  textAlign: "left",
                }}
              >
                <span className={css2["name-link"]}>
                  {item.firstName + " " + item.lastName}
                </span>
                <Text
                  color="primary"
                  fontSize="14"
                  fontWeight="400"
                  rawProps={{ style: { margin: "0px", padding: "0px" } }}
                >
                  {item.jobDesignation}
                </Text>
              </div>
            </div>
          </div>
        ),

        isAlwaysVisible: true,
        isSortable: true,
        width: 300,
      },
      {
        key: "talentProfilePreviousYear",
        caption: "Talent Profile (Previous Year)",
        render: (item) => <Text>{item.talentProfilePreviousYear}</Text>,
        width: 210,
        textAlign: "center",
        isAlwaysVisible: true,
        alignSelf: "center",
      },
      {
        key: "talentProfile",
        caption: "Talent Profile (Current Year)",
        render: (item) => <Text>{item.talentProfile}</Text>,
        width: 210,
        textAlign: "center",
        isAlwaysVisible: true,
        alignSelf: "center",
      },
      {
        key: "primarySkill",
        caption: "Primary Skill",
        textAlign: "center",
        render: (item) => <Text>{item.primarySkill}</Text>,
        width: 130,
        isAlwaysVisible: true,

        alignSelf: "center",
      },

      {
        key: "action",
        caption: "Action",
        textAlign: "center",
        isAlwaysVisible: true,
        render: (item) => {
          return item.isApproved ? (
            <Button
              color="accent"
              fill={"solid"}
              size="24"
              caption={"Approved"}
              onClick={() => {
                setDialogOpen(true);
                setSelectedUser(item);
                navigate(`/practice?uid=${item.uid}`);
              }}
              data-testid="practice-rating-button"
            />
          ) : item.isSubmitted ? (
            <Button
              fill="outline"
              color="critical"
              size="24"
              caption="Approval pending"
              onClick={() => {
                setDialogOpen(true);
                setSelectedUser(item);
                navigate(`/practice?uid=${item.uid}`);
              }}
              data-testid="practice-rating-button"
            />
          ) : (
            <Button
              fill="outline"
              color="primary"
              size="24"
              caption="Provide rating"
              onClick={() => {
                navigate(`/practice?uid=${item.uid}`);
                setDialogOpen(true);
                setSelectedUser(item);
              }}
              data-testid="practice-rating-button"
            />
          );
        },
        alignSelf: "center",
        width: 150,
      },

      {
        key: "meanScore",
        caption: "Mean Score",
        textAlign: "center",
        render: (item) => (
          <div>
            <CountIndicator
              caption={
                item.submissionStatus === "D" || item.submissionStatus === "NA"
                  ? "NA"
                  : item.practiceRating
              }
              color={
                item.submissionStatus === "S" || item.submissionStatus === "A"
                  ? "success"
                  : "primary"
              }
              size={24}
            />
          </div>
        ),
        width: 120,
        isAlwaysVisible: true,
        alignSelf: "center",
      },
    ],
    []
  );

  return (
    <div className={css2["practice-headpage"]}>
      {reduxRole === "ROLE_SA" ? (
        <Navbar hideContent={true} />
      ) : reduxRole === "ROLE_P" ? (
        <NavbarForP hideContent={true} />
      ) : null}
      <Alert></Alert>

      <Banner
        backlink={reduxRole === "ROLE_SA" ? "/welcome" : "/welcome_p"}
        pageTitle={
          reduxRole === "ROLE_SA"
            ? "Practice Rating"
            : `Practice Rating-${competency}`
        }
        pageDescription="Gain valuable insights, identify areas for improvement, and foster personal and professional growth."
        isBackLinkVisible={true}
      />

      {loading === true ? (
        <Panel background="surface-main" shadow>
          <Spinner color="blue" rawProps={{ "data-testid": "spinner" }} />
        </Panel>
      ) : (
        <div className={css2["practice-data"]}>
          <div
            style={{
              width: "80%",
              display: "flex",
              flexDirection: "row",
              justifyContent: "space-between",
              alignItems: "center",
              paddingTop: "10px",
              paddingBottom:  "10px",
            }}
          >
            <div>
              {reduxRole === "ROLE_SA" && (
                <PickerInput
                  minBodyWidth={150}
                  dataSource={competencyDataSource}
                  value={multiPickerValue}
                  onValueChange={handleMultiChange}
                  getName={(item) => item.level}
                  searchPosition="input"
                  entityName="Language level"
                  selectionMode="single"
                  isSingleLine="true"
                  valueType="id"
                  sorting={{ field: "level", direction: "asc" }}
                  placeholder={"SELECT A PRACTICE"}
                  renderFooter={() => {
                    return (
                      <FlexRow padding="12">
                        <FlexCell width="auto" style={{ marginLeft: "5%" }}>
                          <Text color="primary">Select a Practice</Text>
                        </FlexCell>
                        <FlexCell>
                          <Text color="primary">Select a Practice</Text>
                        </FlexCell>
                      </FlexRow>
                    );
                  }}
                />
              )}
            </div>

            <div style={{ width: "30%" }}>
              {reduxRole === "ROLE_SA" && (

                <div style={{ "display": "flex", "justifyContent": "space-around" }} >
                  <SearchInput
                    value={searchValue}
                    onValueChange={onValueChange}
                    placeholder="Type for search"
                    debounceDelay={1000}
                    rawProps={{ style: { width: "50%" } }}
                  />
                  <Button
                    icon={FileFileExcelFillIcon}
                    color="primary"
                    caption="Export to Excel"
                    fill="outline"
                    onClick={() => {
                      if (excelUsers.length > 0) {
                        DownloadExcelTemplate(
                          transformDataWithDesiredKeys(excelUsers, desiredKeys),
                          [], 'practice_rating');
                      }
                      else {
                        console.log("Cannot download")
                        dispatch(notify("No data available to download.", false))
                      }
                    }}
                  />
                </div>
              )}
            </div>
          </div>

          <div
            className={css3["data-table"]}
            style={{ width: "80%", height: "100%" }}
          >
            <Panel
              background="surface-main"
              shadow
              cx={css.container}
              rawProps={{ "data-testid": "data-panel" }}
            >
              <DataTable
                {...view.getListProps()}
                getRows={view.getVisibleRows}
                value={dataSourceState}
                onValueChange={setDataSourceState}
                columns={productColumns}
                headerTextCase="upper"
                rawProps={{ "data-testid": "data-table" }}
              />

              <div
                className="approve-button"
                style={{
                  display: "flex",
                  justifyContent: "flex-end",
                  margin: "10px",
                }}
              >
                {!isDelegate && reduxRole !== "ROLE_SA" ? (
                  <Button
                    rawProps={{ size: "48" }}
                    color="primary"
                    caption="Approve All"
                    size="30"
                    onClick={handleApproveAll}
                    isDisabled={!canApproveAll}
                  />
                ) : (
                  canApprove && (
                    <Button
                      rawProps={{ size: "48" }}
                      color="primary"
                      caption="Approve All"
                      size="30"
                      onClick={handleApproveAll}
                      isDisabled={!canApproveAll}
                    />
                  )
                )}
              </div>
            </Panel>
            {drawerOpen && selectedUser && (
              <SideProfile
                isOpen={drawerOpen}
                onClose={() => setDrawerOpen(false)}
                emailAdd={selectedUser.email}
              />
            )}

            {isDialogOpen && (
              <div className={css3["dialog-overlay"]}>
                <div
                  className={css3["dialog-container"]}
                  onClick={() => {
                    navigate("/practice");
                    setDialogOpen(false);
                  }}
                />

                <div
                  className={css3["rating-dialog-box"]}
                  data-testid="practice-rating-dialog"
                >
                  <PracticeRatingDialog
                    user={selectedUser}
                    isDelegate={isDelegate}
                    canApprove={canApprove}
                    onClose={(isSubmitted, mean, isApproved) => {
                      handleDialogClose(
                        selectedUser,
                        isSubmitted,
                        mean,
                        isApproved
                      );
                    }}
                    dialogOpen={(bool) => setDialogOpen(bool)}
                  />
                </div>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default PracticeHeadPage;
