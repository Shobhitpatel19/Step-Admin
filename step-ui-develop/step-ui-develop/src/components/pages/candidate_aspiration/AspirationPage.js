import React, { useState, useEffect, useRef } from "react";
import AspirationTable from "./components/AspirationTable";
import styles from "./module_css/AspirationPage.module.css";
import { Button, Text, RichTextView, Accordion } from "@epam/uui";
import AspirationFormModal from "./components/AspirationFormModal";
import { NavbarForU } from "../landing_page/navigation_U";
import {
  fetchAspirationsList,
  deleteAspiration,
  submitAspiration,
  updateAspiration,
  fetchAspirationByPriority,
} from "./AspirationApi";
import { useDispatch } from "react-redux";
import { notify } from "../../../redux/actions";
import { useUuiContext } from "@epam/uui-core";
import Alert from "../../common/Alert";
import { PickerInput } from "@epam/uui";
import { useArrayDataSource } from "@epam/uui-core";
import { FlexCell, FlexRow } from "@epam/uui-components";
import axiosInstance from "../../common/axios";
import { ConfirmationModal } from "../../common/ConfirmationModal";

const AspirationPage = () => {
  const [explanations, setExplanations] = useState([]);
  const [aspirations, setAspirations] = useState([]);
  const hasPrimaryAspiration = useRef(false);
  const [previousYearAspirations, setPreviousYearAspirations] = useState([]);
  const [displayedAspirations, setDisplayedAspirations] = useState([]);
  const dispatch = useDispatch();
  const { uuiModals } = useUuiContext();
  const [isSubmissionDisabled, setIsSubmissionDisabled] = useState(false);

  useEffect(() => {
    fetchAndSetAspirations();
  }, []);
  const createButtonRef = useRef(null);
  useEffect(() => {
    if (createButtonRef.current) {
      createButtonRef.current.focus();
    }
  }, []);

  useEffect(() => {
    const savedYear = localStorage.getItem("selectedYear");
    handleMultiChange(savedYear ? Number(savedYear) : 2);
  }, [aspirations, previousYearAspirations]);

  const openModal = (aspiration) => {
    uuiModals
      .show((props) => (
        <AspirationFormModal
          {...props}
          handleSubmit={handleSubmit}
          handleUpdate={handleUpdate}
          aspiration={aspiration}
          hasPrimaryAspiration={hasPrimaryAspiration.current}
        />
      ))
      .then((_) => {})
      .catch((error) => {
        if (typeof error === "string") {
          dispatch(notify(error, false));
        }
      });
  };

  const fetchAndSetAspirations = () => {
    fetchAspirationsList()
      .then((response) => {
        setExplanations(response.data.aspirationExplanation || []);
        setAspirations(response.data.aspirations);
        setPreviousYearAspirations(response.data.previousYearAspirations || []);
        console.log("aspirations:", response.data.aspirations);
        console.log(
          "previousYearAspirations:",
          response.data.previousYearAspirations
        );

        response.data.aspirations.map((value) => {
          if (value.priority === "P1") {
            hasPrimaryAspiration.current = true;
          }
        });

        const hasSubmitted = response.data.aspirations.some(
          (aspiration) => aspiration.submissionStatus === "S"
        );
        setIsSubmissionDisabled(hasSubmitted);
      })
      .catch((error) => {
        console.error("Error fetching data:", error);
      });
  };

  const handleCreateClick = () => {
    openModal(null);
  };
  // const handleSubmitClick = () => {
  //   axiosInstance.post('/step/aspirations/submit', { aspirations })
  //     .then(response => {
  //       dispatch(notify("Aspirations submitted successfully!", true));
  //       fetchAndSetAspirations();

  //     })
  //     .catch(error => {
  //       dispatch(notify("Error submitting aspirations", false));
  //       console.error("Submission error:", error);
  //     });
  // };

  const handleSubmitClick = () => {
    uuiModals
      .show((props) => (
        <ConfirmationModal
          {...props}
          title="Confirm Submission"
          description="Are you sure you want to submit your aspirations?"
          success={() => {
            axiosInstance
              .post("/step/aspirations/submit", { aspirations })
              .then((response) => {
                dispatch(notify("Aspirations submitted successfully!", true));
                fetchAndSetAspirations();
              })
              .catch((error) => {
                dispatch(notify("Error submitting aspirations", false));
                console.error("Submission error:", error);
              });
          }}
        />
      ))
      .catch((error) => {
        if (typeof error === "string") {
          dispatch(notify(error, false));
        }
      });
  };

  const handleEditClick = (aspirationPriority) => {
    const priorityCode = aspirationPriority === "Primary" ? "P1" : "P2";
    fetchAspirationByPriority(priorityCode)
      .then((response) => {
        if (response && response.aspirationList) {
          openModal({
            isPrimary: aspirationPriority === "Primary",
            aspirationList: response.aspirationList,
          });
        } else {
          console.error("API response is empty or incorrect format");
        }
      })
      .catch((error) => {
        dispatch(notify("Something Went Wrong!", false));
        console.error("Failed to fetch aspiration:", error);
      });
  };

  const handleSubmit = (formData, isPrimary) => {
    const formattedData = Object.keys(formData).map((key) => ({
      title: key.charAt(0).toUpperCase() + key.slice(1),
      inputValue: formData[key]?.inputValue || "",
    }));

    const aspirationData = {
      primary: isPrimary,
      aspirationList: formattedData,
    };

    submitAspiration(aspirationData)
      .then((data) => {
        uuiModals.closeAll();
        fetchAndSetAspirations();
        console.log("data", data);
        dispatch(notify("Aspiration Submitted Successfully!", true));
      })
      .catch((error) => {
        dispatch(notify(error.response.data.errorMessage, false));
        console.error("Error submitting aspiration:", error);
      });
  };

  const handleUpdate = (formData, isPrimary) => {
    const formattedData = Object.keys(formData).map((key) => ({
      title: key.charAt(0).toUpperCase() + key.slice(1),
      inputValue: formData[key]?.inputValue || "", // Fix: Extract correct value
    }));

    const aspirationData = {
      primary: isPrimary,
      aspirationList: formattedData,
    };

    updateAspiration(isPrimary ? "Primary" : "Secondary", aspirationData)
      .then((data) => {
        fetchAndSetAspirations();
        uuiModals.closeAll();
        dispatch(notify("Aspiration Updated Successfully!", true));
      })
      .catch((error) => {
        uuiModals.closeAll();
        dispatch(notify("Something Went Wrong!", false));
        console.error("Error submitting aspiration:", error);
      });
  };

  const handleDelete = async (priority) => {
    try {
      const priorityCode = priority === "Primary" ? "P1" : "P2";

      await deleteAspiration(priority);
      setAspirations((prev) =>
        prev.filter((item) => item.priority !== priorityCode)
      );
      console.log(priority);
      hasPrimaryAspiration.current =
        priority === "Primary" ? false : aspirations.length == 2 ? true : false;
      dispatch(notify("Aspiration Deleted Successfully!", true));
    } catch (error) {
      console.error(
        `Failed to delete aspiration with Sr. No. ${priority}:`,
        error
      );
    }
  };

  const competencyList = ["Previous Year", "Current Year"].map(
    (item, index) => ({
      id: index + 1,
      level: item,
    })
  );

  const competencyDataSource = useArrayDataSource(
    { items: competencyList },
    []
  );

  const [multiPickerValue, setMultiPickerValue] = useState(1);

  const handleMultiChange = (value) => {
    setMultiPickerValue(value);
    localStorage.setItem("selectedYear", value);
    const selectedYear = competencyList.find(
      (item) => item.id === value
    )?.level;
    setDisplayedAspirations(
      selectedYear === "Previous Year" ? previousYearAspirations : aspirations
    );
  };

  return (
    <div>
      <Alert />
      <NavbarForU hideContent={true}></NavbarForU>

      <div className={styles.contentArea}>
        <Accordion
          rawProps={{ style: { marginTop: "10px" } }}
          title="Future Skills Summary"
          mode="block"
        >
          <Text>Content in Progress</Text>
        </Accordion>
        <div style={{ display: "flex", margin: "15px 0" }}>
          <div style={{ width: "20%" }}>
            <PickerInput
              minBodyWidth={150}
              dataSource={competencyDataSource}
              value={multiPickerValue}
              onValueChange={handleMultiChange}
              getName={(item) => item.level}
              searchPosition="input"
              entityName="Language level"
              selectionMode="single"
              isSingleLine={true}
              valueType="id"
              sorting={{ field: "level", direction: "asc" }}
              placeholder="Select Year"
              disableClear={true}
              renderFooter={() => (
                <FlexRow padding="12">
                  <FlexCell
                    width="auto"
                    style={{ marginLeft: "5%" }}
                  ></FlexCell>
                </FlexRow>
              )}
            />
          </div>

          <Button
            {...(aspirations.length !== 2
              ? {
                  rawProps: {
                    ref: createButtonRef,
                    style: { marginLeft: "auto" },
                  },
                }
              : {
                  rawProps: {
                    style: { marginLeft: "auto" },
                  },
                })}
            onClick={handleCreateClick}
            caption="Create Aspiration"
            color="accent"
            fill="outline"
            isDisabled={aspirations.length === 2}
          />
        </div>

        <AspirationTable
          aspirations={displayedAspirations}
          handleDelete={handleDelete}
          handleEdit={handleEditClick}
          isSubmissionDisabled={isSubmissionDisabled}
        />

        <Button
          caption="Submit Aspirations"
          color="accent"
          rawProps={{
            style: {
              display: "flex",
              justifySelf: "flex-end",
              marginTop: "15px",
            },
          }}
          onClick={handleSubmitClick}
          isDisabled={aspirations.length !== 2 || isSubmissionDisabled}
        />
      </div>
    </div>
  );
};

export default AspirationPage;
