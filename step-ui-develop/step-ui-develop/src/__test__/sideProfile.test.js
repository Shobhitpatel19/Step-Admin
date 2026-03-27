import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import axiosInstance from "../components/common/axios";
import SideProfile from "../components/common/sideprofile/SideProfile";
import TopDivision from "../components/common/sideprofile/TopDivision";
import BottomDivision from "../components/common/sideprofile/BottomDivision";

// ✅ Mock axios
jest.mock("../components/common/axios", () => ({
  __esModule: true,
  default: {
    get: jest.fn(),
    post: jest.fn(),
    put: jest.fn(),
    delete: jest.fn(),
  },
}));

// ✅ Mock CSS file
jest.mock("../components/styling/side-profile.css", () => ({}));

// ✅ Mock child components with test IDs
jest.mock("../components/common/sideprofile/TopDivision", () =>
  jest.fn(() => <div data-testid="top-division" />)
);
jest.mock("../components/common/sideprofile/BottomDivision", () =>
  jest.fn(() => <div data-testid="bottom-division" />)
);

describe("SideProfile Component", () => {
  const emailAdd = "test@example.com";

  const mockData = {
    fullName: "John Doe",
    jobDesignation: "Software Engineer",
    officeAddress: "123 Main St",
    photo: "photo.jpg",
    employmentId: "EMP123",
    firstName: "John",
    lastName: "Doe",
    profileType: "Full-time",
    jobTrack: "Engineering",
    jobTrackLevel: "2",
    unit: "Tech",
    email: "test@example.com",
    department: "Development",
    location: "NYC",
  };

  afterEach(() => {
    jest.clearAllMocks();
  });

  it("renders loading state initially", () => {
    axiosInstance.get.mockReturnValue(new Promise(() => {})); // Never resolves
    render(<SideProfile emailAdd={emailAdd} />);
    expect(screen.getByText("Loading...")).toBeInTheDocument();
  });

  it("renders error message on API failure", async () => {
    axiosInstance.get.mockRejectedValueOnce(new Error("Network Error"));
    render(<SideProfile emailAdd={emailAdd} />);
    await waitFor(() =>
      expect(screen.getByText("Error: Network Error")).toBeInTheDocument()
    );
  });

  it("renders TopDivision and BottomDivision on successful API call", async () => {
    axiosInstance.get.mockResolvedValueOnce({ data: mockData });
    render(<SideProfile emailAdd={emailAdd} />);

    await waitFor(() => {
      expect(screen.getByTestId("top-division")).toBeInTheDocument();
      expect(screen.getByTestId("bottom-division")).toBeInTheDocument();
    });

    const expectedFirstJson = {
      fullName: "John Doe",
      jobDesignation: "Software Engineer",
      officeAddress: "123 Main St",
      photo: "photo.jpg",
      employmentId: "EMP123",
    };

    const expectedSecondJson = {
      department: "Development",
      location: "NYC",
      jobLevel: "Engineering Level 2",
    };

    expect(TopDivision).toHaveBeenCalledWith({ data: expectedFirstJson }, {});
    expect(BottomDivision).toHaveBeenCalledWith(
      { data: expectedSecondJson },
      {}
    );
  });
});
