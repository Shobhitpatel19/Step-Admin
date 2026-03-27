import React, { useState } from "react";
import { SuccessAlert, ErrorAlert } from "@epam/uui";
import { Text } from "@epam/uui";
import { useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import { cancelNotification } from "../../redux/actions";
// import zIndex from "@mui/material/styles/zIndex";

const Alert = () => {
  let notifystatus = useSelector((state) => state.notification.notifyStatus);
  let notifyMessage = useSelector((state) => state.notification.notifyMessage);
  let isSuccess = useSelector((state) => state.notification.isSuccess);
  const dispatch = useDispatch();

  const popupStyle = {
    width: "25%",
    maxWidth: "500px",
    zIndex: "2700",
    position: "fixed",
    top: "15%",
    right: "2.5%",
    boxShadow: "0 4px 6px rgba(0, 0, 0, 0.2)",
  };

  const handleClose = () => {
    dispatch(cancelNotification());
  };

  useEffect(() => {
    if (notifystatus) {
      const timeout = setTimeout(() => {
        dispatch(cancelNotification());
      }, 2500);

      return () => clearTimeout(timeout);
    }
  }, [notifystatus, dispatch]);

  return (
    <div style={{ zIndex: 2700 }}>
      {notifystatus && (
        <div id="div" style={popupStyle} data-testid="close-button">
          {isSuccess ? (
            <SuccessAlert rawProps={{}} onClose={handleClose}>
              <Text size="30"> {notifyMessage} </Text>
            </SuccessAlert>
          ) : (
            <ErrorAlert rawProps={{}} onClose={handleClose}>
              <Text size="30"> {notifyMessage} </Text>
            </ErrorAlert>
          )}
        </div>
      )}
    </div>
  );
};

export default Alert;
