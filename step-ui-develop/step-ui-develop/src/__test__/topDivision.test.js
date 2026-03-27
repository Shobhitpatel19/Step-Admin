import { render, screen } from "@testing-library/react";
import TopDivision from "../components/common/sideprofile/TopDivision";
import React from "react";
describe("TopDivision Component", () => {
  const mockData = {
    Name: "John Doe",
    Image: "https://example.com/image.jpg",
    Designation: "Software Engineer",
    Address: "123 Main St, City, Country",
  };

  it("renders without crashing and displays the Name, Image, Designation, and Address", () => {
    render(<TopDivision data={mockData} />);

    const imageElement = screen.getByRole("img");
  });
});
