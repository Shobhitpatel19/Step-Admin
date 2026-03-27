import React from "react";
import css from "../../styling/PracticeRatingDialog.module.css";
import axiosInstance from "../../common/axios";
import PracticeRatingTable from "./PracticeRatingTable";

import { useState, useEffect, useRef } from "react";
import { CountIndicator, Text } from "@epam/uui";
import { IconButton, Button, Panel } from "@epam/uui";
import { demoData } from "@epam/uui-docs";

import { Avatar } from "@epam/uui";
import { useDispatch } from "react-redux";
import { notify } from "../../../redux/actions";
import { useNavigate } from "react-router-dom";

import { ReactComponent as ContentCoreSkillFillOptIcon } from "@epam/assets/icons/content-core_skill-fill-opt.2.svg";
import { ReactComponent as NavigationCloseOutlineIcon } from "@epam/assets/icons/navigation-close-outline.svg";
import { ReactComponent as HeroesFillIcon } from "@epam/assets/icons/internal_logo/heroes-fill.svg";
import { ReactComponent as TelescopeFillIcon } from "@epam/assets/icons/internal_logo/telescope-fill.svg";
import { ReactComponent as PeopleOutlineIcon } from "@epam/assets/icons/internal_logo/people-outline.svg";
import { ReactComponent as MicrosoftTeamsFillIcon } from "@epam/assets/icons/external_logo/microsoft_teams-fill.svg";

const PracticeRatingDialog = ({
  onClose,
  user,
  dialogOpen,
  canApprove,
  isDelegate,
  canApproveAll,
}) => {
  const [data, setData] = useState({ message: "", categories: [] });
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [isFormComplete, setIsFormComplete] = useState(false);
  const [isFormEmpty, setIsFormEmpty] = useState(true);
  const ratingsRef = useRef({});
  const [mean, setMean] = useState();
  const navigate = useNavigate();
  const [confirmationDialogue, setConfirmationDialogue] = useState();
  const [confirmation, setConfirmation] = useState(false);
  const [type, setType] = useState("D");
  const [isApproved, setIsApproved] = useState(false);

  const dispatch = useDispatch();

  useEffect(() => {

    axiosInstance
      .get(`step/practice-rating/employees/get-rating/${user.uid}`)
      .then((response) => {
        const fetchedData = response.data;

        setData(fetchedData);
        if (fetchedData.status === "S" || fetchedData.status === "A" || fetchedData.status === "D" ) {
          setMean(fetchedData.mean);
        }

        const initialRatings = fetchedData.categories.reduce(
          (acc, category) => {
            acc[category.categoryName.toLowerCase()] =
              category.subCategory.reduce((subAcc, subCategory) => {
                subAcc[subCategory.subCategoryName] = {
                  rating: subCategory.employeeRating===null?null:subCategory.employeeRating+1,
                  description: subCategory.description,
                };
                return subAcc;
              }, {});
            return acc;
          },
          {}
        );

        ratingsRef.current = initialRatings;

        const formComplete = Object.keys(initialRatings).every((category) =>
          Object.values(initialRatings[category]).every((sub) => {
            sub.rating !== null && isFormEmpty && setIsFormEmpty(false);
            return sub.rating !==null;
          })
        );

        setIsFormComplete(formComplete);
        setIsSubmitted(fetchedData.status === "S");
        if (fetchedData.status === "A") {
          setIsSubmitted(true);
          setIsApproved(true);
        }
      })
      .catch((error) => {
        dialogOpen(false);
        dispatch(notify("Candidate is not a part of your practice.", false));
      });
  }, []);

  function generatePayload() {
    const payload = {
      categories: Object.keys(ratingsRef.current).map((category) => ({
        categoryName: category,
        subCategory: Object.keys(ratingsRef.current[category]).map(
          (subCategoryName) => ({
            subCategoryName: subCategoryName,
            employeeRating: ratingsRef.current[category][subCategoryName].rating===null?null:ratingsRef.current[category][subCategoryName].rating-1
             
          })
        ),
      })),
    };

    return payload;
  }

  const valueCallback = async (category, key, newRating) => {

    var sum = 0;
    var mean = 0;

    if (!ratingsRef.current[category]) {
      ratingsRef.current[category] = {};
    }
    ratingsRef.current[category][key] = {
      ...ratingsRef.current[category][key],
      rating: newRating,
    };

    Object.keys(ratingsRef.current).map((category) => {
      Object.keys(ratingsRef.current[category]).map(
        (subCategory) =>
          (sum = sum + (ratingsRef.current[category][subCategory].rating===null
            ? 0 : (ratingsRef.current[category][subCategory].rating-1)))
      );
    });
    

    mean = sum / 15;
    setMean(mean.toFixed(1));

    const formComplete = Object.keys(ratingsRef.current).every((category) =>
      Object.values(ratingsRef.current[category]).every((sub) => sub.rating !== null)
    );
    const formEmpty = Object.keys(ratingsRef.current).every((category) =>
      Object.values(ratingsRef.current[category]).every(
        (sub) => sub.rating === null
      )
    );
    setIsFormEmpty(formEmpty);
    setIsFormComplete(formComplete);
    return "";
  };

  const handleSave = async (type) => {
    const payload = generatePayload();

    try {
      const response = await axiosInstance.post(
        `step/practice-rating/employees/save-rating/${user.uid}?submissionStatus=${type}`,
        payload,
        { headers: { "Content-Type": "application/json" } }
      );


      if (type === "A") {
        
        setMean(response.data.mean);
        setIsApproved(true);
        dispatch(notify("Rating Approved Successfully!", true));
        onClose(true,response.data.mean, true);
    
      }

      if (type === "S") {
        
        setMean(response.data.mean);
        setIsSubmitted(true);
        dispatch(notify("Rating Submitted Successfully!", true));
        onClose(true,response.data.mean, false);
        
      }

      if (type === "D") {
        
        setMean(response.data.mean);
        isSubmitted ? dispatch(notify("Discarded successfully!", true)): dispatch(notify("Rating Draft Successfully!", true));
        setIsApproved(false);
        setIsSubmitted(false);
        onClose(false,"NA", false);
      
      }
    } catch (error) {
      console.error("Error saving data:", error);
      dispatch(notify("Error on saving data", false));
    }
  };

  return (
    <div className={css["dialog-box"]}>
      <div className={css["dialog-header"]}>
        <div className={css["header-content"]}>
          <div className={css["provide-rating"]}>
            <div className={css["text"]}>Provide Rating</div>
            <div className={css["flash-icon"]}>
              <ContentCoreSkillFillOptIcon></ContentCoreSkillFillOptIcon>
            </div>
          </div>

          <div className={css["cancel"]}>
            <div className={css["cancel-icon"]}>
              <IconButton
                icon={NavigationCloseOutlineIcon}
                onClick={() => {
                  navigate(`/practice`);
                  dialogOpen(false);

                }}
                data-testid="close-button"
              />
            </div>
          </div>
        </div>
        <div className={css["rating-profile"]}>
          <div className={css["profile-details"]}>
            <div className={css["avatar-div"]}>
              <Avatar alt="avatar" img={user.photo} size={72} fon />
            </div>
            <div className={css["avatar-details"]}>
              <div className={css["practice-username"]}>{user.fullName}</div>
              <div className={css["title"]}>{user.jobDesignation} </div>
              <div className={css["skill"]}>
                {" "}
                Primary Skill :{user.primarySkill}
              </div>
              <div className={css["icons-container"]}>
                <IconButton
                  color="info"
                  icon={HeroesFillIcon}
                  onClick={() => {
                    window.open(
                      `https://heroes.epam.com/app/user/${user.employmentId}`
                    );
                  }}
                />
                <IconButton
                  color="info"
                  icon={TelescopeFillIcon}
                  onClick={() => {
                    window.open(
                      `https://telescope.epam.com/who/key/upsaId/${user.employmentId}?tab=profile`
                    );
                  }}
                />
                <IconButton
                  color="info"
                  icon={PeopleOutlineIcon}
                  onClick={() => {
                    window.open(
                      `https://people.epam.com/profile/${user.employmentId}`
                    );
                  }}
                />
                <IconButton
                  color="info"
                  icon={MicrosoftTeamsFillIcon}
                  onClick={() => {
                    window.open(
                      `https://teams.microsoft.com/l/chat/0/0?users=${user.email}`
                    );
                  }}
                />
              </div>
            </div>
          </div>
          <div className={css["mean-score"]}>
            <div>
              <Text fontSize="18" fontWeight="600">Mean Score</Text>
            </div>
            <div>
              <CountIndicator caption={mean==null?0:mean} color="neutral" size={24} />
            </div>
          </div>
        </div>
      </div>
      <div className={css["dialog-body"]}>
        <div className="practice-rating-form">
          <PracticeRatingTable
            categories={data.categories}
            ratings={ratingsRef.current}
            valueCallback={valueCallback}
            onSave={handleSave}
            isSubmitted={isSubmitted}
            isFormComplete={isFormComplete}
            isFormEmpty={isFormEmpty}
            message={data.message}
            isDelegate={isDelegate}
            canApprove={canApprove}
            isApproved={isApproved}
            canApproveAll={canApproveAll}
          />
        </div>
      </div>
    </div>
  );
};

export default PracticeRatingDialog;
