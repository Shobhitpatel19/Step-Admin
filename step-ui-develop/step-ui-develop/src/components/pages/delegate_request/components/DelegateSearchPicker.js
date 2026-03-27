import React, { useCallback, useRef, useState } from "react";
import { useLazyDataSource } from "@epam/uui-core";
import {
  PickerInput,
  DataPickerRow,
  PickerItem,
  FlexCell,
  Text,
  LabeledInput,
} from "@epam/uui";
import axiosInstance from "../../../common/axios";
import { FlexRow } from "@epam/uui-components";
import { ReactComponent as SearchOutlineIcon } from "@epam/assets/icons/action-search-outline.svg";

export default function DelegateSearchPicker({
  onSelectListener,
  isSelected,
  isCompetencySelected
}) {
  const [showIsJobLevelBelowB3Error, setShowIsJobLevelBelowB3Error] =
    useState(false);
  const searchValue = useRef();

  const loadData = useCallback(async (request) => {
    searchValue.current = request.search;
    return axiosInstance
      .get("step/full-user-profile-above-b3?name=" + request.search)
      .then(async (res) => {
        return {
          items: res.data,
        };
      }).catch(e => {
        return {
          items: [],
        }
      });

  }, []);

  const dataSource = useLazyDataSource(
    {
      api: loadData,
      getId: (item) => item.uid,
    },
    []
  );

  const onSearchValueChange = (value) => {
    setShowIsJobLevelBelowB3Error(false);
    const trackAndLevel = value.jobTrack + value.jobTrackLevel;
    if (
      trackAndLevel === "B3" ||
      trackAndLevel === "B4" ||
      trackAndLevel === "B5" ||
      trackAndLevel === "C"  
    ) {
      onSelectListener(value);
    } else {
      setShowIsJobLevelBelowB3Error(true);
    }
  };

  const pickerItemTitle = (item, search) => {
    const fullname = item.firstName + " " + item.lastName;
    const fullnameParts = fullname.split(new RegExp(`(${search})`, "gi"));

    return (
      <Text color="info" rawProps={{ style: { padding: "0px" } }}>
        {fullnameParts.map((namePart, index) =>
          namePart.toLowerCase() === search.toLowerCase() ? (
            <span key={index} className="uui-highlight">
              {namePart}
            </span>
          ) : (
            namePart
          )
        )}
        <Text
          fontWeight="600"
          rawProps={{ style: { display: "inline", marginLeft: "10px" } }}
        >
          {item.title}
        </Text>
      </Text>
    );
  };

  const pickerItemSubtitle = (item) => {
    return (
      <>
        <Text rawProps={{ style: { padding: "0px", marginBottom: "3px" } }}>
          {item.primarySkill}
          <Text rawProps={{ style: { display: "inline" } }}>

            <Text rawProps={{ style: { display: "inline" } }}>
              {" | "}
              <Text rawProps={{ style: { display: "inline" } }}>
                {item.unit}
              </Text>
            </Text>
          </Text>
        </Text>

        <Text color="disabled" rawProps={{ style: { padding: 0 } }}>
          {item.profileType}
        </Text>
      </>
    );
  };

  const renderCustomRow = (props, dataSourceState) => {
    return (
      <DataPickerRow
        {...props}
        key={props.key}
        alignActions="center"
        padding="12"
        renderItem={(item, rowProps) => (
          <PickerItem
            {...rowProps}
            dataSourceState={dataSourceState}
            highlightSearchMatches={false}
            avatarUrl={item.photo}
            title={pickerItemTitle(item, dataSourceState.search)}
            subtitle={pickerItemSubtitle(item)}
          />
        )}
      />
    );
  };

  const customFooter = () => {
    return (
      <FlexCell grow={1} rawProps={{ style: { padding: "5px" } }}>
        <FlexRow
          onClick={() => {
            window.open(
              "https://telescope.epam.com/people/search?q=" +
              searchValue.current,
              "_blank",
              "noopener,noreferrer"
            );
          }}
          rawProps={{
            "data-testid": "searchOnTelescope",
            style: {
              background: "#009ecc",
              justifyContent: "center",
              borderRadius: "3px",
              cursor: "pointer",
            },
          }}
        >
          <Text
            color="white"
            fontSize="14"
            rawProps={{ style: { cursor: "pointer" } }}
          >
            SEARCH ON TELESCOPE
          </Text>
        </FlexRow>
      </FlexCell>
    );
  };

  return (
    <FlexCell grow={1}>
      <LabeledInput
        htmlFor="delegate-search-input"
        isInvalid={showIsJobLevelBelowB3Error}
        validationMessage="Level should be B3 or above!"
      >
        <PickerInput
          dataSource={dataSource}
          icon={SearchOutlineIcon}
          placeholder={"Search to delegate"}
          onValueChange={onSearchValueChange}
          renderRow={renderCustomRow}
          getName={(item) => item.firstName + " " + item.lastName}
          dropdownHeight={220}
          selectionMode="single"
          valueType="entity"
          maxItems={3}
          minCharsToSearch={3}
          renderFooter={customFooter}
          isInvalid={showIsJobLevelBelowB3Error}
          isDisabled={isCompetencySelected ? isSelected : !isCompetencySelected}
        />
      </LabeledInput>
    </FlexCell>
  );
}