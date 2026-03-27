import React, { useState, useEffect } from "react";
import Slider from "../../common/Slider";
import { SuccessNotification, Text, Button } from "@epam/uui";

import { ReactComponent as NotificationInfoOutlineIcon } from "@epam/assets/icons/notification-info-outline.svg";
import { Padding } from "@mui/icons-material";
import { red } from "@mui/material/colors";
import { IModal, useUuiContext } from "@epam/uui-core";
import { PracticeRatingApprovalDialog } from "./PracticeRatingApprovalDialog";
import css from "../../styling/PracticeRatingPage.module.css";
import Alert from "../../common/Alert";
import { useSelector } from "react-redux";

const PracticeRatingTable = ({
  categories = [],
  ratings,
  valueCallback,
  onSave,
  isSubmitted,
  isFormComplete,
  message,
  isFormEmpty,
  canApprove,
  isDelegate,
  canApproveAll,
  isApproved,
}) => {
  const { uuiModals } = useUuiContext();
  const reduxRole = useSelector((state) => state.practicerating.role);

  const draftAndSubmitButton = (isDisabled) => {
    return (
      <>
        <div className="draft-button">
          <Button
            fill="outline"
            color="primary"
            caption="Save as Draft"
            onClick={() => {
              onSave("D");
            }}
            size="30"
            isDisabled={isDisabled || isFormEmpty || isSubmitted || isApproved}
          />
        </div>
        <div className="submit-button">
          <Button
            color="accent"
            caption="Submit"
            size="30"
            onClick={() => {
              if (reduxRole === "ROLE_SA") {
                onSave("S");
              } else if (reduxRole === "ROLE_P" && isDelegate && !canApprove) {
                onSave("S");
              } else {
                uuiModals
                  .show((props) => <PracticeRatingApprovalDialog {...props} />)
                  .then((result) => {
                    onSave("A");
                  })
                  .catch(() => {});
              }
            }}
            isDisabled={
              isDisabled ||
              (reduxRole === "ROLE_P" && isDelegate && !canApprove) ||
              reduxRole === "ROLE_SA"
                ? isSubmitted || !isFormComplete
                : isApproved || isSubmitted || !isFormComplete
            }
          />
        </div>
      </>
    );
  };

  const discardAndApproveButton = (isDisabled) => {
    return (
      <>
        <div className="discard-button">
          <Button
            color="critical"
            fill="outline"
            caption="Discard"
            onClick={() => {
              onSave("D");
            }}
            size="30"
            isDisabled={isApproved}
          />
        </div>
        <div className="approve-button">
          <Button
            color="primary"
            caption="Approve"
            onClick={() =>
              uuiModals
                .show((props) => <PracticeRatingApprovalDialog {...props} />)
                .then((result) => {
                  onSave("A");
                })
                .catch(() => {})
            }
            size="30"
            isDisabled={isApproved}
          />
        </div>
      </>
    );
  };

  return (
    <div className={css["table-container"]}>
      <Alert />
      <div className={css["scrollable"]}>
        {categories.map((category) => (
          <div
            key={category.categoryName}
            className={css["category-container"]}
          >
            <div className={css["category-header"]}>
              <Text
                fontSize="16"
                lineHeight="12"
                color="primary"
                fontWeight="600"
              >
                {category.categoryName}
              </Text>
            </div>
            <div className={css["subcategories"]}>
              {category.subCategory.map((subCategory) => (
                <div
                  key={subCategory.subCategoryName}
                  className={css["subcategory-row"]}
                >
                  <div className={css["subcategory-name"]}>
                    <div>
                      <Text
                        fontSize="12"
                        lineHeight="12"
                        color="primary"
                        fontWeight="600"
                      >
                        {/* {subCategory.subCategoryName} */}
                        {subCategory.description}
                      </Text>
                    </div>
                    {/* <div className={css["view-description"]}>
                      <NotificationInfoOutlineIcon
                        rawProps={{ "data-testid": "info-btn" }}
                        title={subCategory.description}
                        width={10}
                        height={20}
                        data-testid="notification-icon"
                      />
                    </div> */}
                  </div>

                  <div className={css["subcategory-slider"]}>
                    <Slider
                      value={
                        ratings[category.categoryName.toLowerCase()]?.[
                          subCategory.subCategoryName
                        ]?.rating || 0
                      }
                      category={category.categoryName.toLowerCase()}
                      fieldKey={subCategory.subCategoryName}
                      valueCallback={valueCallback}
                      isSubmitted={isSubmitted || isApproved}
                    />
                  </div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>

      <div
        className={css["dialog-footer"]}
        style={{ display: "flex", gap: "5px" }}
      >
        {isApproved
          ? draftAndSubmitButton(false)
          : reduxRole === "ROLE_SA"
          ? isSubmitted
            ? draftAndSubmitButton(true)
            : draftAndSubmitButton(false)
          : isDelegate
          ? isSubmitted
            ? canApprove
              ? discardAndApproveButton()
              : draftAndSubmitButton(true)
            : draftAndSubmitButton(false)
          : isSubmitted
          ? discardAndApproveButton()
          : draftAndSubmitButton(false)}
      </div>
    </div>
  );
};

export default PracticeRatingTable;
