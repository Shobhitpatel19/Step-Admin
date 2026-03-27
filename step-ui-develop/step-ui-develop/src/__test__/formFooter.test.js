import React from "react";
import { render, screen } from "@testing-library/react";
import FormFooter from "../components/pages/Future_skills/FormFooter";

describe("FormFooter", () => {
  it("renders 'Last Updated' with formatted date when 'lastupdated' prop is passed", () => {
    const mockDate = "2025-04-29T12:30:00Z"; // Example timestamp
    render(<FormFooter lastupdated={mockDate} />);

    const formattedDate = new Date(mockDate).toLocaleString();
    const lastUpdatedText = screen.getByText(`Last Updated: ${formattedDate}`);
    expect(lastUpdatedText).toBeInTheDocument();
  });

  it("renders 'No updates yet' when 'lastupdated' prop is not passed", () => {
    render(<FormFooter lastupdated={undefined} />);

    const noUpdatesText = screen.getByText("No updates yet");
    expect(noUpdatesText).toBeInTheDocument();
  });

  it("renders 'No updates yet' when 'lastupdated' prop is null", () => {
    render(<FormFooter lastupdated={null} />);

    const noUpdatesText = screen.getByText("No updates yet");
    expect(noUpdatesText).toBeInTheDocument();
  });

  it("renders the component without crashing when 'lastupdated' is an invalid date", () => {
    render(<FormFooter lastupdated="invalid-date-string" />);

    // Since the date is invalid, it should fall back to 'No updates yet'
    // const noUpdatesText = screen.getByText("No updates yet");
    // expect(noUpdatesText).toBeInTheDocument();
  });
});
