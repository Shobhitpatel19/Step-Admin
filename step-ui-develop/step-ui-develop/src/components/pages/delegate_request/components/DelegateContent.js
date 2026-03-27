import {
  FlexRow,
  Text,
  FlexCell,
  Avatar,
  CheckboxGroup,
  RadioGroup,
  ScrollBars,
  ModalFooter,
  Button,
  SuccessNotification,
  ErrorNotification,
  LabeledInput,
  Spinner,
} from "@epam/uui";

import css from "../module_css/PracticeDelegateContent.module.css";
import { useEffect, useRef, useState } from "react";
import { useUuiContext } from "@epam/uui-core";
import axiosInstance from "../../../common/axios";
import DelegateSearchPicker from "./DelegateSearchPicker";
import DelegateBanner from "./DelegateBanner";
import { decodeToken, getTokenFromCookies } from "../../../utils/auth";
import SuperAdminCompetencyDropdown from "./SuperAdminCompetencyDropdown";

export default function DelegateContent(props) {
  const { uuiNotifications } = useUuiContext();
  const [loading, setLoading] = useState(true);
  const [selectedDelegate, setSelectedDelegate] = useState(null);
  const [isAlreadyDelegated, setIsAlreadyDelegated] = useState(false);
  const [selectedAccessTypes, setSelectedAccessTypes] = useState([]);
  const [selectedAccessLevel, setSelectedAccessLevel] = useState();
  const [accessTypeNotSelectedError, setAccessTypeNotSelectedError] =
    useState(false);
  const [accessLevelNotSelectedError, setAccessLevelNotSelectedError] =
    useState(false);
  const accessTypesOptions = useRef([]);
  const noFeatureAvailableErrorShown = useRef(false);
  const [selectedCompetency, setSelectedCompetency] = useState(null);
  const [userRole, setUserRole] = useState(null);

  const handleCompetencyChange = (competency) => {
    onClearDelegate();
    setSelectedCompetency(competency);
  };

  //   console.log("selected competency", selectedCompetency);
  const endpoint =
    userRole === "ROLE_SA"
      ? `/step/delegate?competency=${selectedCompetency}`
      : `/step/delegate?competency=`;

  const accessLevelsOptions = [
    { id: 1, name: "Full Access (No approval required)" },
    { id: 2, name: "Limited Access (Approval required)" },
  ];

  useEffect(() => {
    const token = getTokenFromCookies();
    if (!token || typeof token !== "string") {
      console.error("Token is missing or invalid.");
      return;
    }

    try {
      const { role } = decodeToken(token);
      setUserRole(role);
      //   console.log("User role:", role);
    } catch (error) {
      console.error("Error decoding token:", error.message);
    }
  }, []);

  const showErrorNotification = (errorMessage) => {
    uuiNotifications.show((props) => (
      <ErrorNotification {...props}>
        <FlexRow alignItems="center">
          <Text>{errorMessage}</Text>
        </FlexRow>
      </ErrorNotification>
    ));
    //.catch(() => null);
  };

  const showSomethingWentWrongErrorNotification = () => {
    showErrorNotification("Something went wrong, try again!");
  };

  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      const availableAccessTypesResponse = await axiosInstance
        .get("step/get-available-practice-features")
        .catch((e) => {});

      let anyUnknownError = true;
      if (availableAccessTypesResponse) {
        if (availableAccessTypesResponse.status) {
          if (availableAccessTypesResponse.status === 200) {
            if (availableAccessTypesResponse.data.length === 0) {
              if (!noFeatureAvailableErrorShown.current) {
                noFeatureAvailableErrorShown.current = true;
                showErrorNotification("No Feature Available for delegation");
                props.abort();
              }
              return;
            } else {
              anyUnknownError = false;
              accessTypesOptions.current = [
                { id: 1, name: "Select All" },
                ...availableAccessTypesResponse.data.map(
                  (featureName, index) => {
                    return { id: index + 2, name: featureName };
                  }
                ),
              ];
            }
          }
        }
      }

      if (anyUnknownError) {
        if (!noFeatureAvailableErrorShown.current) {
          noFeatureAvailableErrorShown.current = true;
          showSomethingWentWrongErrorNotification();
          props.abort();
        }
        return;
      }

      const response = await axiosInstance.get(endpoint).catch((e) => {});

      if (response) {
        if (response.status) {
          if (response.status === 200) {
            setIsAlreadyDelegated(true);
            setSelectedDelegate(response.data.delegatedTo);

            let selectedAccess = response.data.practiceDelegationFeatures.map(
              (type) =>
                accessTypesOptions.current.findIndex(
                  (item) => item.name === type.name
                ) + 1
            );

            if (
              selectedAccess.length === availableAccessTypesResponse.data.length
            ) {
              selectedAccess = [1, ...selectedAccess];
            }

            setSelectedAccessTypes(selectedAccess);

            if (response.data.approvalRequired) {
              setSelectedAccessLevel(2);
            } else {
              setSelectedAccessLevel(1);
            }
          }
        }
      }

      setLoading(false);
    };
    loadData();
  }, [selectedCompetency]);

  const onSelectDelegate = (value) => {
    setSelectedDelegate(value);
  };

  const onAccessTypesValueSelected = (value) => {
    setAccessTypeNotSelectedError(false);

    value.sort();
    if (selectedAccessTypes[0] === 1 && value[0] !== 1) {
      setSelectedAccessTypes([]);
    } else {
      if (value[0] === 1 && selectedAccessTypes[0] !== 1) {
        setSelectedAccessTypes(
          accessTypesOptions.current.map((item) => item.id)
        );
      } else if (
        value.length === accessTypesOptions.current.length - 1 &&
        value[0] !== 1
      ) {
        setSelectedAccessTypes(
          accessTypesOptions.current.map((item) => item.id)
        );
      } else {
        if (
          value.length === accessTypesOptions.current.length - 1 &&
          value[0] === 1
        ) {
          value.splice(0, 1);
        }
        setSelectedAccessTypes(value);
      }
    }
  };

  const onClearDelegate = () => {
    setSelectedDelegate(null);
    setIsAlreadyDelegated(false);
    setSelectedAccessTypes([]);
    setSelectedAccessLevel(null);
    setAccessTypeNotSelectedError(false);
    setAccessLevelNotSelectedError(false);
  };

  const onDeleteDelegate = () => {
    axiosInstance
      .delete(endpoint)
      .then((response) => {
        setSelectedDelegate(null);
        setIsAlreadyDelegated(false);
        setSelectedAccessTypes([]);
        setSelectedAccessLevel(null);

        setAccessTypeNotSelectedError(false);
        setAccessLevelNotSelectedError(false);

        uuiNotifications
          .show((props) => (
            <ErrorNotification {...props}>
              <FlexRow alignItems="center">
                <Text>Selected Delegate Deleted Successfully!</Text>
              </FlexRow>
            </ErrorNotification>
          ))
          .catch(() => null);
      })
      .catch((error) => {
        uuiNotifications.show((props) => (
          <ErrorNotification {...props}>
            <FlexRow alignItems="center">
              <Text>Something went wrong, Try again!</Text>
            </FlexRow>
          </ErrorNotification>
        ));
        //   .catch(() => null);
      });
  };

  const onSaveDelegate = () => {
    let anyError = false;

    if (selectedAccessTypes.length === 0) {
      anyError = true;
      setAccessTypeNotSelectedError(true);
    }

    if (!selectedAccessLevel) {
      anyError = true;
      setAccessLevelNotSelectedError(true);
    }

    if (anyError) {
      return;
    }

    const delegateRawData = JSON.stringify({
      delegatedTo: selectedDelegate.email,
      practiceDelegationFeatures: selectedAccessTypes
        .filter((index) => index !== 1)
        .map((index) => {
          return { name: accessTypesOptions.current[index - 1].name };
        }),
      approvalRequired: selectedAccessLevel === 1 ? false : true,
    });

    axiosInstance
      .post(endpoint, delegateRawData)
      .then((response) => {
        {
          console.log(response);
        }
        setIsAlreadyDelegated(true);
        uuiNotifications
          .show((props) => (
            <SuccessNotification {...props}>
              <FlexRow alignItems="center">
                {isAlreadyDelegated ? (
                  <Text>Delegation Updated Successful!</Text>
                ) : (
                  <Text>Delegation Successful!</Text>
                )}
              </FlexRow>
            </SuccessNotification>
          ))
          .catch(() => null);
      })
      .catch((error) => {
        if (error.response) {
          const { status, data } = error.response;

          switch (status) {
            case 400:
              uuiNotifications.show((props) => (
                <ErrorNotification {...props}>
                  <FlexRow alignItems="center">
                    <Text>
                      {data.errorMessage ||
                        "Invalid request. Please check your input."}
                    </Text>
                  </FlexRow>
                </ErrorNotification>
              ));
              // .catch(() => null);
              break;

            case 401:
              uuiNotifications
                .show((props) => (
                  <ErrorNotification {...props}>
                    <FlexRow alignItems="center">
                      <Text>
                        {"Your session has expired. Please log in again."}
                      </Text>
                    </FlexRow>
                  </ErrorNotification>
                ))
                .catch(() => null);

              break;

            case 403:
              uuiNotifications
                .show((props) => (
                  <ErrorNotification {...props}>
                    <FlexRow alignItems="center">
                      <Text>
                        {
                          "You don't have permission to access this resource. Please check your permissions."
                        }
                      </Text>
                    </FlexRow>
                  </ErrorNotification>
                ))
                .catch(() => null);
              break;

            default:
              uuiNotifications
                .show((props) => (
                  <ErrorNotification {...props}>
                    <FlexRow alignItems="center">
                      <Text>
                        {
                          "An unexpected error occurred. Please try again later."
                        }
                      </Text>
                    </FlexRow>
                  </ErrorNotification>
                ))
                .catch(() => null);
          }
        } else {
          uuiNotifications.show((props) => (
            <ErrorNotification {...props}>
              <FlexRow alignItems="center">
                <Text>Something went wrong, Try again!</Text>
              </FlexRow>
            </ErrorNotification>
          ));
          // .catch(() => null);
        }
      });
  };

  return (
    <>
      <DelegateBanner onClose={() => props.abort()} />

      {userRole === "ROLE_SA" && (
        <FlexRow
          rawProps={{ style: { padding: "0 20px 10px 20px", gap: "20px" } }}
        >
          <SuperAdminCompetencyDropdown
            onCompetencyChange={handleCompetencyChange}
          />
        </FlexRow>
      )}

      <FlexRow
        rawProps={{ style: { padding: "0 20px 10px 20px", gap: "20px" } }}
      >
        <DelegateSearchPicker
          onSelectListener={onSelectDelegate}
          isSelected={!!selectedDelegate}
          isCompetencySelected={
            userRole === "ROLE_SA" ? selectedCompetency : true
          }
        />
      </FlexRow>

      {loading ? (
        <>
          <Spinner />
          <br />
        </>
      ) : (
        <>
          <ScrollBars hasTopShadow hasBottomShadow>
            <FlexRow cx={css.container}>
              {selectedDelegate && (
                <>
                  <Avatar alt="avatar" img={selectedDelegate.photo} size={54} />
                  <Text fontSize="18" fontWeight="600">
                    {selectedDelegate.firstName +
                      " " +
                      selectedDelegate.lastName}
                  </Text>
                  <Text fontSize="16" size="18" fontWeight="400">
                    {selectedDelegate.jobDesignation}
                  </Text>
                </>
              )}

              <FlexCell
                style={{ width: "100%", paddingLeft: "20px" }}
                alignContent="left"
              >
                <Text fontSize="18" fontWeight="800">
                  Access Types
                </Text>

                <FlexCell cx={css.accessTypesContainer}>
                  <LabeledInput
                    htmlFor="access-type-group"
                    isInvalid={accessTypeNotSelectedError}
                    validationMessage="This field is mandatory"
                  >
                    <CheckboxGroup
                      id="access-type-group"
                      items={accessTypesOptions.current}
                      value={selectedAccessTypes}
                      onValueChange={onAccessTypesValueSelected}
                      direction="vertical"
                      isDisabled={selectedDelegate ? false : true}
                    />
                  </LabeledInput>
                </FlexCell>

                <Text fontSize="18" fontWeight="800">
                  Access Level
                </Text>

                <FlexCell cx={css.accessLevelsContainer}>
                  <LabeledInput
                    htmlFor="access-level-group"
                    isInvalid={accessLevelNotSelectedError}
                    validationMessage="This field is mandatory"
                  >
                    <RadioGroup
                      id="access-level-group"
                      items={accessLevelsOptions}
                      value={selectedAccessLevel}
                      onValueChange={(value) => {
                        setAccessLevelNotSelectedError(false);
                        setSelectedAccessLevel(value);
                      }}
                      direction="vertical"
                      isDisabled={selectedDelegate ? false : true}
                    />
                  </LabeledInput>
                </FlexCell>
              </FlexCell>
            </FlexRow>
          </ScrollBars>

          <ModalFooter cx={css.footer}>
            {isAlreadyDelegated ? (
              <Button
                color="critical"
                fill="outline"
                caption="Delete"
                onClick={onDeleteDelegate}
              />
            ) : (
              <Button
                color="secondary"
                fill="outline"
                caption="Clear Selection"
                onClick={onClearDelegate}
                isDisabled={selectedDelegate ? false : true}
              />
            )}
            <Button
              color="primary"
              caption={isAlreadyDelegated ? "Update" : "Delegate"}
              onClick={onSaveDelegate}
              isDisabled={selectedDelegate ? false : true}
            />
          </ModalFooter>
        </>
      )}
    </>
  );
}
