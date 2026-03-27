import { render, screen } from "@testing-library/react";
import BottomDivision from "../components/common/sideprofile/BottomDivision";
import React from "react";

describe("BottomDivision Component", () => {
  const mockData = {
    "Is Active?": "Yes",
    "TP-2021": "Completed",
    "TP-2022": "Ongoing",
    "Niche Skill": "N.A",
    "Primary Skill": "JavaScript",
    "Job Level": "Senior Developer",
    "Time In Track": "5 years",
    "Last Assessment Date": "2024-10-15",
    "Last Assessment Result": "Passed",
    "Project Account": "TechCorp Inc.",
    "Bench Status": "N.A.",
    "Joining Date": "2019-07-22",
    "AGS/EPAM": "EPAM",
  };

  it("renders without crashing and displays DetailBox for each entry in data", () => {
    render(<BottomDivision data={mockData} />);

    const detailBoxes = screen.getAllByTestId("detail-box-main");
    expect(detailBoxes.length).toBe(Object.entries(mockData).length);

    Object.entries(mockData).forEach(([key, value]) => {
      const keyElement = screen.getByText(key);
      const valueElement = screen.getByText(value.toString());

      expect(keyElement).toBeInTheDocument();
      expect(valueElement).toBeInTheDocument();
    });
  });
});
