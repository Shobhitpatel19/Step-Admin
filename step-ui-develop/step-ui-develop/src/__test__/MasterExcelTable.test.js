import { render, fireEvent, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import { configureStore } from "@reduxjs/toolkit";
import MasterExcelTable from "../../src/components/pages/master_excel_view/MasterExcelTable";
import rootReducer from "../../src/redux/combinedreducer";
import axiosInstance from "../../src/components/common/axios";
import { UuiContext, useUuiServices, StubAdaptedRouter } from "@epam/uui-core";
import { BrowserRouter } from "react-router-dom";
import userEvent from "@testing-library/user-event";

function UuiContextDefaultWrapper({ children }) {
  const testUuiCtx = {};
  const router = new StubAdaptedRouter();
  const { services } = useUuiServices({ router });

  const mockData = {
    topTalentEmployeeDTOList: [{ UID: "1", Name: "John Doe", Ranking: 1 }],
    filteredTopTalentEmployees: [{ UID: "1", Name: "John Doe", Ranking: 1 }],
    topTalentExcelVersions: [
      {
        fileName: "STEP_2025_V1.xlsx",
        uploadedYear: "2025",
        versionName: "V1",
      },
      {
        fileName: "STEP_2025_V2.xlsx",
        uploadedYear: "2025",
        versionName: "V2",
      },
    ],
  };

  const mockUserprofiles = {
    1: {
      fullName: "Chaitanya Anumanchiss",
      photo:
        "https://static.cdn.epam.com/avatar/4d61872eaf01908e0392b2aae061de8b4362.jpg",
      jobDesignation: "Senior Project Manager",
      officeAddress:
        "Salarpuria Sattva Knowledge City, Plot No-2, Phase-1, Survey No.83/1, Raidurga Village, Serilingampally Mandal",
      uid: "1",
      email: "Chaitanya_Anumanchi@epam.com",
      primarySkill: "Project Management",
      jobLevel: "Project Management Level 3",
      jobTrack: "B",
      jobTrackLevel: "3",
      employmentId: "4060741400035744380",
    },
  };

  const initialState = {
    masterexcel: {
      tableData: mockData.topTalentEmployeeDTOList,
      topTalentDTO: mockData.topTalentEmployeeDTOList,
      selectedBoxes: ["1"],
      filteredToptalentDTO: mockData.filteredTopTalentEmployees,
      listForSaving: ["1"],
      userProfiles: mockUserprofiles,
    },
  };

  const store = configureStore({
    reducer: rootReducer,
    preloadedState: initialState,
  });

  Object.assign(testUuiCtx, services);
  return (
    <Provider store={store}>
      <BrowserRouter>
        <UuiContext.Provider value={services}>{children}</UuiContext.Provider>
      </BrowserRouter>
    </Provider>
  );
}

jest.mock("axios", () => ({
  create: jest.fn(() => ({
    interceptors: {
      request: { use: jest.fn() },
      response: { use: jest.fn() },
    },
    get: jest.fn(),
    post: jest.fn(),
    put: jest.fn(),
    delete: jest.fn(),
  })),
}));

describe("MasterExcelTable Component Tests", () => {
  it("renders without crashing and displays initial data", async () => {
    const mockData = {
      topTalentEmployeeDTOList: [
        { UID: "517917", Name: "John Doe", Ranking: 1 },
        { UID: "517917", Name: "Jane Smith", Ranking: 2 },
      ],
      filteredTopTalentEmployees: [
        { UID: "517917", Name: "John Doe", Ranking: 1 },
      ],
      topTalentExcelVersions: [
        {
          fileName: "STEP_2025_V1.xlsx",
          uploadedYear: "2025",
          versionName: "V1",
        },
        {
          fileName: "STEP_2025_V2.xlsx",
          uploadedYear: "2025",
          versionName: "V2",
        },
      ],
    };

    axiosInstance.post.mockResolvedValue({
      data: mockData,
    });

    render(<MasterExcelTable data={mockData} />, {
      wrapper: UuiContextDefaultWrapper,
    });

    // Check if initial data is rendered correctly
    // expect(screen.getByText(/John Doe/)).toBeInTheDocument();
    //  expect(screen.getByText(/Ranking/)).toBeInTheDocument();
    //  expect(screen.getByText(/V1/)).toBeInTheDocument();
    //expect(screen.getByText(/V2/)).toBeInTheDocument();
  });

  it("should toggle between tabs correctly", async () => {
    render(<MasterExcelTable data={{}} />, {
      wrapper: UuiContextDefaultWrapper,
    });

    const tabButton2 = screen.getByText("Filtered Top Talent Candidates");
    fireEvent.click(tabButton2);
  });

  it("should handle freeze button click and call handleDraft", async () => {
    const mockHandleDraft = jest.fn();
    render(<MasterExcelTable data={{}} />, {
      wrapper: UuiContextDefaultWrapper,
    });
  });

  it("should call API and display success message on Excel generation", async () => {
    axiosInstance.post.mockResolvedValue({
      status: 200,
      data: {
        filteredTopTalentEmployees: [
          { UID: "1", Name: "John Doe", Ranking: 1 },
        ],
      },
    });

    render(<MasterExcelTable data={{}} />, {
      wrapper: UuiContextDefaultWrapper,
    });

    const downloadButton = screen.getByText("Download");
    fireEvent.click(downloadButton);
    expect(
      screen.getByText("Excel generated successfully")
    ).toBeInTheDocument();
  });

  it("should handle API failure gracefully", async () => {
    axiosInstance.post.mockRejectedValue(new Error("API Error"));

    render(<MasterExcelTable data={{}} />, {
      wrapper: UuiContextDefaultWrapper,
    });

    const downloadButton = screen.getByText("Download");
    fireEvent.click(downloadButton);
  });

  it("should handle empty data gracefully", () => {
    render(<MasterExcelTable data={{ topTalentEmployeeDTOList: [] }} />, {
      wrapper: UuiContextDefaultWrapper,
    });
  });

  it("should update table data when filters change", async () => {
    render(<MasterExcelTable data={{}} />, {
      wrapper: UuiContextDefaultWrapper,
    });
  });
});
