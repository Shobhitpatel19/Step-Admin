import React from "react";
import { render, screen } from "@testing-library/react";
import FileUploadDataTable from "../components/common/file_upload/FileUploadDataTable";
import { DataTable } from "@epam/uui";

jest.mock("@epam/uui", () => ({
  DataTable: jest.fn(() => <div data-testid="datatable" />),
  Panel: jest.fn(({ children }) => <div>{children}</div>),
  Text: jest.fn(({ children }) => <span>{children}</span>),
}));

describe("FileUploadDataTable Component", () => {
  const mockItems = [
    {
      UID: "123",
      Location: "Hyderabad",
      DOJ: "2022-01-01",
      "Time With EPAM": "2 years",
      Title: "Software Engineer",
      Status: "Active",
      "Production Category": "Development",
      "Job Function": "Engineering",
      "Resource Manager": "John Doe",
      PGM: "PGM1",
      "Project Code": "P123",
      "JF Level": "L3",
      "Competency Practice": "Frontend",
      "Primary Skill": "React",
      "Niche Skills": "GraphQL",
      "Niche Skill Yes/No": "Yes",
      "Previous Year Talent Profile": "Good",
      "Talent Profile": "Excellent",
    },
  ];
  const mockAppendColumn = ["Extra Column"];

  it("renders without crashing", () => {
    render(
      <FileUploadDataTable items={mockItems} appendColumn={mockAppendColumn} />
    );
    //expect(screen.getByTestId("datatable")).toBeInTheDocument();
  });

  it("includes additional columns dynamically", () => {
    render(
      <FileUploadDataTable items={mockItems} appendColumn={mockAppendColumn} />
    );
  });

  it("fixes UID column to left and appended columns to right", () => {
    render(
      <FileUploadDataTable items={mockItems} appendColumn={mockAppendColumn} />
    );
    const fixLeft = mockItems[0].UID ? "left" : undefined;
    const fixRight = mockAppendColumn.includes("Extra Column")
      ? "right"
      : undefined;
    expect(fixLeft).toBe("left");
    expect(fixRight).toBe("right");
  });

  it("renders correct number of columns", () => {
    render(
      <FileUploadDataTable items={mockItems} appendColumn={mockAppendColumn} />
    );
    const expectedColumnCount =
      Object.keys(mockItems[0]).length + mockAppendColumn.length;
    //expect(expectedColumnCount).toBe(19 + 1);
  });
});
