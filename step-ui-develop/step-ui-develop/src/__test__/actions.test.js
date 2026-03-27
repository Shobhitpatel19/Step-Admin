import * as actions from "../redux/actions";

describe("Redux Action Creators", () => {
  it("should create a NOTIFY action", () => {
    const expectedAction = {
      type: "NOTIFY",
      payload: { notifyMessage: "Test Message", isSuccess: true },
    };
    expect(actions.notify("Test Message", true)).toEqual(expectedAction);
  });

  it("should create a CANCEL action", () => {
    const expectedAction = { type: "CANCEL" };
    expect(actions.cancelNotification()).toEqual(expectedAction);
  });

  it("should create a RESET_NOTIFY action", () => {
    const expectedAction = {
      type: "RESET_NOTIFY",
      payload: { notifyStatus: false },
    };
    expect(actions.resetNotificationStatus()).toEqual(expectedAction);
  });

  it("should create a REQUEST_EXCEL action", () => {
    const expectedAction = {
      type: "REQUEST_EXCEL",
      payload: { requiredExcel: "Excel1" },
    };
    expect(actions.RequestExcelVersion("Excel1")).toEqual(expectedAction);
  });

  it("should create a SET_CURRENT_EXCEL action", () => {
    const expectedAction = {
      type: "SET_CURRENT_EXCEL",
      payload: { currentExcel: "Excel2" },
    };
    expect(actions.setCurrentExcelVersion("Excel2")).toEqual(expectedAction);
  });

  it("should create a SET_EXCEL_VERSIONS action", () => {
    const expectedAction = {
      type: "SET_EXCEL_VERSIONS",
      payload: { excels: ["Excel1", "Excel2"] },
    };
    expect(actions.setExcelVersions(["Excel1", "Excel2"])).toEqual(
      expectedAction
    );
  });

  it("should create a SET_LATEST_VERSION_SELECTED action", () => {
    const expectedAction = {
      type: "SET_LATEST_VERSION_SELECTED",
      payload: { flag: true },
    };
    expect(actions.setLatestVersionSelected(true)).toEqual(expectedAction);
  });

  it("should create a SET_TABLE_DATA action", () => {
    const expectedAction = {
      type: "SET_TABLE_DATA",
      payload: { tableData: [{ id: 1, name: "Test" }] },
    };
    expect(actions.setTableData([{ id: 1, name: "Test" }])).toEqual(
      expectedAction
    );
  });

  it("should create a SET_SELECT_BOXES action", () => {
    const expectedAction = {
      type: "SET_SELECT_BOXES",
      payload: { selected: [1, 2, 3] },
    };
    expect(actions.setSelectedBoxes([1, 2, 3])).toEqual(expectedAction);
  });

  it("should create a SET_TOP_TALENT_DTO action", () => {
    const expectedAction = {
      type: "SET_TOP_TALENT_DTO",
      payload: { toptalent: "TopTalentData" },
    };
    expect(actions.setTopTalentDTO("TopTalentData")).toEqual(expectedAction);
  });

  it("should create a SET_FILTERED_TOP_TALENT_DTO action", () => {
    const expectedAction = {
      type: "SET_FILTERED_TOP_TALENT_DTO",
      payload: { filteredTopTalent: "FilteredData" },
    };
    expect(actions.setFilteredTopTalentDTO("FilteredData")).toEqual(
      expectedAction
    );
  });

  it("should create a SET_LIST_FOR_SAVE action", () => {
    const expectedAction = {
      type: "SET_LIST_FOR_SAVE",
      payload: { listForSave: ["Item1", "Item2"] },
    };
    expect(actions.setListForSave(["Item1", "Item2"])).toEqual(expectedAction);
  });

  it("should create a SAVE_USER_PROFILES action", () => {
    const expectedAction = {
      type: "SAVE_USER_PROFILES",
      payload: { userProfile: { name: "User1" } },
    };
    expect(actions.saveUserProfiles({ name: "User1" })).toEqual(expectedAction);
  });

  it("should create a SAVE_NO_OF_EXCELS action", () => {
    const expectedAction = {
      type: "SAVE_NO_OF_EXCELS",
      payload: { number: 5 },
    };
    expect(actions.saveNoOfExcels(5)).toEqual(expectedAction);
  });

  it("should create a RESET_STATE action", () => {
    const expectedAction = { type: "RESET_STATE" };
    expect(actions.resetState()).toEqual(expectedAction);
  });

  it("should create a SET_ROLE action", () => {
    const expectedAction = {
      type: "SET_ROLE",
      payload: { role: "Admin" },
    };
    expect(actions.setRole("Admin")).toEqual(expectedAction);
  });
});
