import React, { useState, useEffect } from "react";
import { useArrayDataSource } from "@epam/uui-core";
import { FlexCell } from "@epam/uui-components";
import axiosInstance from "../../../common/axios";
import { PickerInput } from "@epam/uui";

export default function SuperAdminCompetencyDropdown({ onCompetencyChange }) {
  const [competencies, setCompetencies] = useState([]);
  const [competencyValue, onCompetencyValueChange] = useState(null);

  useEffect(() => {
    const fetchCompetencies = async () => {
      try {
        const response = await axiosInstance.get(
          "step/practice-rating/competencies"
        );
        console.log(response.data)
        setCompetencies(response.data);
      } catch (error) {
        console.error("Error fetching competencies:", error);
      }
    };

    fetchCompetencies();
  }, []);

  const competencyDataSource = useArrayDataSource({
    items: competencies,
    getId: (c) => c
  }, [competencies]);

  const handleOnCompetencyValueChange = (value) => {
    onCompetencyValueChange(value);
    onCompetencyChange(value);
  };

  return (
    <FlexCell grow={1}>
      <PickerInput
        minBodyWidth={120}
        dataSource={competencyDataSource}
        value={competencyValue}
        onValueChange={handleOnCompetencyValueChange}
        getName={(item) => item}
        searchPosition="input"
        selectionMode="single"
        isSingleLine="true"
        valueType="entity"
        sorting={{ direction: "asc" }}
        placeholder="Select a Practice"
      />
    </FlexCell>
  )
}
