import reducer from "../redux/masterexcelviewreducer";

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

describe('masterexcelviewreducer', () => {
    it('should return the initial state when action is undefined', () => {
        expect(reducer(undefined, {})).toEqual(initialState);
    });

    it('should handle SET_EXCEL', () => {
        const action = {
            type: 'SET_EXCEL',
            payload: { currentExcel: 'Excel_1' }
        };
        const state = reducer(initialState, action);
        expect(state.currentExcelVersion).toBe('Excel_1');
    });

    it('should handle SET_LATEST_VERSION_SELECTED', () => {
        const action = {
            type: 'SET_LATEST_VERSION_SELECTED',
            payload: { flag: 5 }
        };
        const state = reducer(initialState, action);
        expect(state.latestVersion).toBe(5);
    });

    it('should handle SET_EXCEL_VERSIONS', () => {
        const excels = ['Excel_1', 'Excel_2'];
        const action = {
            type: 'SET_EXCEL_VERSIONS',
            payload: { excels }
        };
        const state = reducer(initialState, action);
        expect(state.excelVersions).toEqual(excels);
    });

    it('should handle REQUEST_EXCEL', () => {
        const action = {
            type: 'REQUEST_EXCEL',
            payload: { requiredExcel: 'Excel_3' }
        };
        const state = reducer(initialState, action);
        expect(state.requestedExcelVersion).toBe('Excel_3');
    });

    it('should handle SET_TABLE_DATA', () => {
        const action = {
            type: 'SET_TABLE_DATA',
            payload: { tableData: [{ id: 1 }] }
        };
        const state = reducer(initialState, action);
        expect(state.tableData).toEqual([{ id: 1 }]);
    });

    it('should handle SET_SELECT_BOXES', () => {
        const action = {
            type: 'SET_SELECT_BOXES',
            payload: { selected: ['A', 'B'] }
        };
        const state = reducer(initialState, action);
        expect(state.selectedBoxes).toEqual(['A', 'B']);
    });

    it('should handle SET_TOP_TALENT_DTO', () => {
        const action = {
            type: 'SET_TOP_TALENT_DTO',
            payload: { toptalent: [{ name: 'Alice' }] }
        };
        const state = reducer(initialState, action);
        expect(state.topTalentDTO).toEqual([{ name: 'Alice' }]);
    });

    it('should handle SET_FILTERED_TOP_TALENT_DTO', () => {
        const action = {
            type: 'SET_FILTERED_TOP_TALENT_DTO',
            payload: { filteredTopTalent: [{ name: 'Bob' }] }
        };
        const state = reducer(initialState, action);
        expect(state.filteredToptalentDTO).toEqual([{ name: 'Bob' }]);
    });

    it('should handle SET_LIST_FOR_SAVE', () => {
        const action = {
            type: 'SET_LIST_FOR_SAVE',
            payload: { listForSave: ['Item1'] }
        };
        const state = reducer(initialState, action);
        expect(state.listForSaving).toEqual(['Item1']);
    });

    it('should handle SAVE_USER_PROFILES', () => {
        const userProfile = { user1: { name: 'Tom' } };
        const action = {
            type: 'SAVE_USER_PROFILES',
            payload: { userProfile }
        };
        const state = reducer(initialState, action);
        expect(state.userProfiles).toEqual(userProfile);
    });

    it('should handle SAVE_NO_OF_EXCELS', () => {
        const action = {
            type: 'SAVE_NO_OF_EXCELS',
            payload: { number: 10 }
        };
        const state = reducer(initialState, action);
        expect(state.noOFExcels).toBe(10);
    });

    it('should handle SET_CURRENT_EXCEL', () => {
        const action = {
            type: 'SET_CURRENT_EXCEL',
            payload: { currentExcel: 'Excel_4' }
        };
        const state = reducer(initialState, action);
        expect(state.currentExcelVersion).toBe('Excel_4');
    });

    it('should handle RESET_STATE', () => {
        const modifiedState = {
            ...initialState,
            currentExcelVersion: 'Some Version'
        };
        const action = { type: 'RESET_STATE' };
        const state = reducer(modifiedState, action);
        expect(state).toEqual(initialState);
    });

    it('should return state for unknown action type', () => {
        const action = {
            type: 'UNKNOWN_ACTION',
            payload: { data: 'should not matter' }
        };
        const currentState = { ...initialState, currentExcelVersion: 'Excel_5' };
        const state = reducer(currentState, action);
        expect(state).toEqual(currentState);
    });
});
