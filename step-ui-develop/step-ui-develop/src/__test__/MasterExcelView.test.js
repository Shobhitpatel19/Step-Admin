import {
  render,
  fireEvent,
  screen,
  waitFor,
  act,
} from "@testing-library/react";
import { Provider } from "react-redux";
import { configureStore } from "@reduxjs/toolkit";
import MasterExcelTable from "../../src/components/pages/master_excel_view/MasterExcelTable";
import MasterExcelView from "../../src/components/pages/master_excel_view/MasterExcelView";
import rootReducer from "../../src/redux/combinedreducer";
import axiosInstance from "../../src/components/common/axios";
import { UuiContext, useUuiServices, StubAdaptedRouter } from "@epam/uui-core";
import MasterExcelError from "../components/pages/master_excel_view/MasterExcelError";

import { BrowserRouter } from "react-router-dom";
import { delay } from "@epam/uui-test-utils";

function UuiContextDefaultWrapper({ children }) {
  const testUuiCtx = {};
  const router = new StubAdaptedRouter();
  const { services } = useUuiServices({ router });

  const mockData = {
    topTalentEmployeeDTOList: [
      { UID: "1", Name: "John Doe", Ranking: 1 },
      { UID: "2", Name: "Jane Smith", Ranking: 2 },
    ],
    filteredTopTalentEmployees: [{ UID: "1", Name: "John Doe", Ranking: 1 }],
  };

  const initialState = {
    masterexcel: {
      tableData: mockData.topTalentEmployeeDTOList,
      topTalentDTO: mockData.topTalentEmployeeDTOList,
      selectedBoxes: ["1"],
      filteredToptalentDTO: mockData.filteredTopTalentEmployees,
      listForSaving: ["1"],
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

async function renderToJsDom(reactElement) {
  const result = render(reactElement, { wrapper: UuiContextDefaultWrapper });
  return result;
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

jest.mock("../components/pages/master_excel_view/FilterBox");
jest.mock("../components/pages/master_excel_view/MasterExceltable");

describe("MasterExcelTable Component Tests", () => {
  it("renders without crashing and displays initial data", async () => {
    const mockData = {
      topTalentEmployeeDTOList: [
        { UID: "1", Name: "John Doe", Ranking: 1 },
        { UID: "2", Name: "Jane Smith", Ranking: 2 },
      ],
      filteredTopTalentEmployees: [{ UID: "1", Name: "John Doe", Ranking: 1 }],
    };

    axiosInstance.get.mockResolvedValue({
      data: mockData,
    });
    await renderToJsDom(<MasterExcelView />);
    // expect(screen.getByText("Master Excel View")).toBeInTheDocument();
  });

  it("renders master excell error", async () => {
    const mockData = {
      ratingStatus: "PARTIALLY_COMPLETED",
      topTalentEmployeeDTOList: [
        { UID: "1", Name: "John Doe", Ranking: 1 },
        { UID: "2", Name: "Jane Smith", Ranking: 2 },
      ],
      filteredTopTalentEmployees: [],
      practiceHeadListDetailed: { Microsoft: { PracticeHead: ["john"] } },
      topTalentExcelVersions: [
        {
          fileName: "STEP_2025_V1.xlsx",
          uploadedYear: "2025",
          versionName: "V1",
        },
      ],
    };

    axiosInstance.get.mockResolvedValue({
      data: mockData,
    });
    await renderToJsDom(<MasterExcelError data={mockData} />);

    const unFoldButton = screen.getByTestId("unfold-state");
    expect(unFoldButton).toBeInTheDocument();

    expect(screen.getByRole("button", { name: /Home/i })).toBeInTheDocument();
  });
});
