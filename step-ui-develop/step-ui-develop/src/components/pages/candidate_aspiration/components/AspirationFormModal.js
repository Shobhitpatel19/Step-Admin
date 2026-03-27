import React, { useState, useEffect, useRef } from "react";
import { Panel, TextInput, Button, Switch, ScrollBars } from "@epam/uui";
import { fetchAspirationDescription } from "../AspirationApi";
import Alert from "../../../common/Alert";
import { ReactComponent as NavigationCloseOutlineIcon } from "@epam/assets/icons/navigation-close-outline.svg";

const AspirationFormDrawer = (props) => {
  const {
    abort,
    handleSubmit,
    handleUpdate,
    aspiration,
    hasPrimaryAspiration,
  } = props;

  const [formData, setFormData] = useState({});
  const [isPrimary, setIsPrimary] = useState(false);
  const [isOpen, setIsOpen] = useState(true);
  const isFormValid = Object.values(formData).every(
    (item) => item.inputValue?.trim() !== ""
  );

  useEffect(() => {
    if (aspiration?.aspirationList?.length) {
      const initialFormData = {};
      aspiration.aspirationList.forEach((item) => {
        initialFormData[item.title] = {
          inputValue: item.inputValue || "",
          description: item.description || "",
        };
      });
      setFormData(initialFormData);
    } else {
      fetchAspirationDescription()
        .then((data) => {
          if (!Array.isArray(data?.aspirationList)) {
            console.error("Invalid aspiration data received:", data);
            return;
          }

          const initialFormData = {};
          data.aspirationList.forEach((item) => {
            initialFormData[item.title] = {
              inputValue: item.inputValue || "",
              description: item.description || "",
            };
          });
          setFormData(initialFormData);
        })
        .catch((error) => {
          abort("Error fetching aspiration fields");
          console.error("Error fetching aspiration data:", error);
        });
    }
  }, []);

  const handleInputChange = (value, title) => {
    setFormData((prev) => ({
      ...prev,
      [title]: {
        ...prev[title],
        inputValue: value,
      },
    }));
  };

  const handleBackgroundClick = () => {
    setIsOpen(false);
    abort();
  };
  const firstInputRef = useRef(null);

  // useEffect(() => {
  //   if (firstInputRef.current) {
  //     //firstInputRef.current.focus();
  //   }
  // }, [formData]);

  return (
    <>
      {isOpen && (
        <div
          style={{
            position: "fixed",
            top: 0,
            left: 0,
            height: "100vh",
            width: "100vw",
            backdropFilter: "blur(2px)",
            zIndex: 9997,
            background: "rgba(0, 0, 0, 0.3)",
          }}
          onClick={handleBackgroundClick}
        />
      )}

      {isOpen && (
        <Panel
          cx="custom-drawer"
          style={{
            position: "fixed",
            top: 0,
            right: 0,
            height: "100vh",
            width: "600px",
            background: "white",
            boxShadow: "4px 0 16px rgba(0,0,0,0.1)",
            zIndex: 9999,
            display: "flex",
            flexDirection: "column",
            pointerEvents: "auto",
          }}
        >
          <div
            style={{
              padding: "20px",
              borderBottom: "1px solid #eee",
              alignItems: "center",
            }}
          >
            <h3 style={{ margin: 0 }}>
              {aspiration ? "Edit Aspiration" : "Create Aspiration"}
            </h3>
          </div>

          <Alert />

          <ScrollBars style={{ flex: 1 }}>
            <div
              style={{
                padding: "24px",
                display: "flex",
                flexDirection: "column",
                flexWrap: "wrap",
              }}
            >
              {Object.entries(formData).map(([key, item], index) => (
                <div
                  key={key}
                  style={{
                    display: "flex",
                    fontSize: "0.75em",
                    flexDirection: "column",
                    width: "100%",
                    marginBottom: "20px", // 👈 Add spacing between form fields
                    position: "relative",
                  }}
                >
                  <label
                    style={{
                      fontSize: "0.95em",
                      color: "black",
                      overflowWrap: "break-word",
                      wordBreak: "break-word",
                      whiteSpace: "normal",
                      lineHeight: "1.4",
                      marginBottom: "8px", // 👈 Space between label and input
                    }}
                  >
                    {item.description}
                    <span style={{ color: "red", marginLeft: "4px" }}>*</span>
                  </label>

                  <TextInput
                    placeholder={`Enter ${
                      key.charAt(0).toUpperCase() + key.slice(1)
                    }`}
                    value={item.inputValue}
                    onValueChange={(value) => handleInputChange(value, key)}
                    style={{ width: "100%", paddingRight: "40px" }}
                    ref={index === 0 ? firstInputRef : null}
                  />
                  {item.inputValue && (
                    <NavigationCloseOutlineIcon
                      onClick={() => handleInputChange("", key)}
                      style={{
                        position: "absolute",
                        right: "14px",
                        top: "70%",
                        transform: "translateY(-50%)",
                        cursor: "pointer",
                        width: "20px",
                        height: "20px",
                        fill: "#555555",
                      }}
                    />
                  )}
                </div>
              ))}

              {!aspiration && (
                <div style={{ paddingTop: "17px" }}>
                  <Switch
                    size="18"
                    label="Mark this aspiration as primary"
                    value={isPrimary}
                    onValueChange={setIsPrimary}
                    isDisabled={hasPrimaryAspiration}
                  />
                </div>
              )}
            </div>
          </ScrollBars>

          <div
            style={{
              padding: "16px 24px",
              borderTop: "1px solid #eee",
              display: "flex",
              gap: "1rem",
              justifyContent: "flex-end",
            }}
          >
            <Button color="Tertiary" caption="Cancel" onClick={abort} />
            <Button
              caption={aspiration ? "Update Aspiration" : "Create Aspiration"}
              onClick={() =>
                aspiration
                  ? handleUpdate(formData, aspiration.isPrimary)
                  : handleSubmit(formData, isPrimary)
              }
              isDisabled={!isFormValid}
            />
          </div>
        </Panel>
      )}
    </>
  );
};

export default AspirationFormDrawer;
