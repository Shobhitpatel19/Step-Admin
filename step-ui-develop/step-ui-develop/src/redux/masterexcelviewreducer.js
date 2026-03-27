const initialState = {
    excelVersions: [],
    currentExcelVersion: null,
    requestedExcelVersion: null,
    tableData: null,
    selectedBoxes: [],
    listForSaving: [],
    topTalentDTO: [],
    filteredToptalentDTO: [],
    userProfiles: {},
    noOFExcels: 0,
    latestVersion: 0

};

const masterexcelviewreducer = (state = initialState, action) => {
    switch (action.type) {
        case "SET_EXCEL":
            return {
                ...state,
                currentExcelVersion: action.payload.currentExcel
            };
        case "SET_LATEST_VERSION_SELECTED":
            return {
                ...state,
                latestVersion: action.payload.flag
            };
        case "SET_EXCEL_VERSIONS":
            return {
                ...state,
                excelVersions: action.payload.excels
            };
        case "REQUEST_EXCEL":
            return {
                ...state,
                requestedExcelVersion: action.payload.requiredExcel,
            };
        case "SET_TABLE_DATA":
            return {
                ...state,
                tableData: action.payload.tableData,
            };
        case "SET_SELECT_BOXES":
            return {
                ...state,
                selectedBoxes: action.payload.selected,
            };
        case "SET_TOP_TALENT_DTO":
            return {
                ...state,
                topTalentDTO: action.payload.toptalent,
            };
        case "SET_FILTERED_TOP_TALENT_DTO":
            return {
                ...state,
                filteredToptalentDTO: action.payload.filteredTopTalent,
            };
        case "SET_LIST_FOR_SAVE":
            return {
                ...state,
                listForSaving: action.payload.listForSave,
            };
        case "SAVE_USER_PROFILES":
            return {
                ...state,
                userProfiles: action.payload.userProfile
            };
        case "SAVE_NO_OF_EXCELS":
            return {
                ...state,
                noOFExcels: action.payload.number
            };
        case "SET_CURRENT_EXCEL":
            return {
                ...state,
                currentExcelVersion: action.payload.currentExcel
            };

        case "RESET_STATE":
            return {
                ...initialState
            };
        default:
            return state;
    }
}

export default masterexcelviewreducer;