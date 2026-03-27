export const notify = (notifyMessage, isSuccess) => {
  return {
    type: "NOTIFY",
    payload: { notifyMessage: notifyMessage, isSuccess: isSuccess },
  };
};

export const cancelNotification = () => {
  return {
    type: "CANCEL",
  };
};

export const resetNotificationStatus = () => {
  return {
    type: "RESET_NOTIFY",
    payload: { notifyStatus : false },
  };
};

export const RequestExcelVersion = (requiredExcel) => {
  return {
    type: "REQUEST_EXCEL",
    payload: { requiredExcel: requiredExcel },
  };
};

export const setCurrentExcelVersion = (currentExcel) => {
  return {
    type: "SET_CURRENT_EXCEL",
    payload: { currentExcel: currentExcel },
  };
};

export const setExcelVersions = (excels) => {
  return {
    type: "SET_EXCEL_VERSIONS",
    payload: { excels: excels },
  };
};

export const setLatestVersionSelected = (flag) => {
  return {
    type: "SET_LATEST_VERSION_SELECTED",
    payload: { flag: flag },
  };
};

export const setTableData = (tableData) => {
  return {
    type: "SET_TABLE_DATA",
    payload: { tableData: tableData },
  };
};

export const setSelectedBoxes = (selected) => {
  return {
    type: "SET_SELECT_BOXES",
    payload: { selected: selected },
  };
};

export const setTopTalentDTO = (toptalent) => {
  return {
    type: "SET_TOP_TALENT_DTO",
    payload: { toptalent: toptalent },
  };
};

export const setFilteredTopTalentDTO = (filteredTopTalent) => {
  return {
    type: "SET_FILTERED_TOP_TALENT_DTO",
    payload: { filteredTopTalent: filteredTopTalent },
  };
};

export const setListForSave = (listForSave) => {
  return {
    type: "SET_LIST_FOR_SAVE",
    payload: { listForSave: listForSave },
  };
};

export const saveUserProfiles = (userProfile) => {
  return {
    type: "SAVE_USER_PROFILES",
    payload: { userProfile: userProfile },
  };
};

export const saveNoOfExcels = (number) => {
  return {
    type: "SAVE_NO_OF_EXCELS",
    payload: { number: number },
  };
};

export const resetState = () => {
  return {
    type: "RESET_STATE",
  };
};

export const setRole = (role) => {
  return {
    type: "SET_ROLE",
    payload: { role: role },
  };
};
