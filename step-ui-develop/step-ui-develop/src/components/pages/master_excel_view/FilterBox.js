import { FlexCell, FlexRow } from "@epam/uui-components";
import css from "../../styling/master_excel_view/MasterExcelView.module.css"
import { Button, NumericInput, PickerInput } from "@epam/uui";
import { useArrayDataSource, useLazyDataSource } from "@epam/uui-core";
import { useEffect, useState } from "react";
import { Text } from "@epam/uui";
import { useDispatch } from "react-redux";
import { resetState, notify, setExcelVersion, setCurrentExcelVersion, setLatestVersionSelected, RequestExcelVersion, setTableData, setSelectedBoxes, setListForSave } from "../../../redux/actions";
import { useSelector } from "react-redux";
const FilterBox = (props) => {

    const dispatch = useDispatch();

    const tapTalentCandidates = useSelector((state) => state.masterexcel.topTalentDTO);
    const totalExcelVersions = useSelector((state) => state.masterexcel.excelVersions);
    const savingList = useSelector((state) => state.masterexcel.listForSaving);
    const flag = useSelector((state) => state.masterexcel.latestVersion)
    const requestedExcelVersion = useSelector((state) => state.masterexcel.requestedExcelVersion);
    const [inputPercentage, setInputPercentage] = useState();
    const [count, setCount] = useState(0);
    const num = useSelector((state) => state.masterexcel.noOFExcels);

    const excelVersions = props.data.topTalentExcelVersions.map((version, index) => ({
        id: index + 1,
        level: version.fileName,
    }));

    const versionNames = totalExcelVersions.map((version) => version.fileName)
    const x = versionNames.indexOf(requestedExcelVersion) + 1;

    const [singlePickerValue, singleOnValueChange] = useState(num);

    function getExcelWithoutType(excel) {
        const lastDotIndex = excel.lastIndexOf('.');
        const filenameWithoutType = excel.substring(0, lastDotIndex);
        return filenameWithoutType;
    }

    if (requestedExcelVersion === null) {
        dispatch(setCurrentExcelVersion(getExcelWithoutType(versionNames[num - 1])))
    }

    const dataSource = useArrayDataSource(
        {
            items: excelVersions
        },
        [],
    );


    const handleChange = (value) => {

        singleOnValueChange(value);
        const excel = dataSource.props.items.filter(obj => obj.id == value)[0].level;

        dispatch(setCurrentExcelVersion(getExcelWithoutType(excel)))
        dispatch(RequestExcelVersion(excel));
    }

    const [multiPickerValue, multiOnValueChange] = useState(0);

    const handleMultiChange = (value) => {

        multiOnValueChange(value);

        if (value !== undefined) {

            const filteredObjects = namesDataSource.props.items.filter(item => value.includes(item.id)).map((item) => item.uid);
            const matchingObjects = (props.data.topTalentEmployeeDTOList.filter(employee =>
                filteredObjects.includes(employee.UID)
            ));
            const matchingUIDS = matchingObjects.map((candidate) => (candidate.UID));
            dispatch(setTableData(matchingObjects));
        }
        else {
            dispatch(setTableData(null));
        }
    }

    const namesList = tapTalentCandidates.map((version, index) => ({
        id: index + 1,
        level: version.Name + "  (" + version.UID + ")  ",
        uid: version.UID
    }));

    const namesDataSource = useArrayDataSource(
        {
            items: namesList,
            candidates: props.data.topTalentEmployeeDTOList
        },
        [],
    );

    const applyInputPercentage = () => {

        if (inputPercentage >= 1 && inputPercentage <= 100) {

            const totalCandidates = props.data.topTalentEmployeeDTOList.length;
            const numberToSelect = Math.ceil((inputPercentage / 100) * totalCandidates);
            const sortedList = [...props.data.topTalentEmployeeDTOList].sort((a, b) => a["Ranking"] - b["Ranking"]);
            const topCandidates = sortedList.slice(0, numberToSelect).map((candidate) => (candidate.UID));

            dispatch(setTableData(sortedList));
            dispatch(setSelectedBoxes(topCandidates));
        }
        else {
            dispatch(notify("Input range should be in 1-100.", false))
        }
        setInputPercentage();
    }

    return (

        <FlexCell style={{ display: "flex", flex: "1", justifyContent: "center", flexDirection: "column" }} rawProps={{ "data-testid": "second-component" }} >
            <FlexRow rawProps={{ style: { height: "100%", "justifyContent": "space-between" } }} >

                <FlexRow rawProps={{ style: { "height": "100%", "width": "30%", "justifyContent": "space-around", "borderRight": "2px solid #E0E2EB" } }} >
                    <div style={{ width: "50%" }} data-testid="dropdown">
                        <PickerInput
                            rawProps={{ "data-testid": "dropdown" }}
                            minBodyWidth={150}
                            disableClear={true}
                            dataSource={dataSource}
                            value={singlePickerValue}
                            onValueChange={handleChange}
                            getName={(item) => item.level}
                            entityName="Excel Versions"
                            selectionMode="single"
                            valueType="id"
                            sorting={{ field: 'level', direction: 'asc' }}
                            placeholder={"STEP_YEAR_VERSION"}


                            renderFooter={() => {
                                return (
                                    <FlexRow padding="12">
                                        <FlexCell width="auto" style={{ marginLeft: "5%" }}  >
                                            <Text color="primary">Please select a version</Text>
                                        </FlexCell>
                                    </FlexRow>
                                );
                            }}
                        />
                    </div>

                    <NumericInput placeholder="Percentage" value={inputPercentage} formatOptions={ {
                        minimumFractionDigits: 2,
                        maximumFractionDigits: 2,
                    } } onValueChange={setInputPercentage}> </NumericInput>

                </FlexRow >

                <FlexRow rawProps={{ style: { "height": "100%", "width": "70%", "justifyContent": "space-between" } }} >
                    <div style={{ width: "40%", marginLeft: "2.5%" }} >
                        <PickerInput
                            minBodyWidth={150}
                            dataSource={namesDataSource}
                            value={multiPickerValue}
                            onValueChange={handleMultiChange}
                            getName={(item) => item.level}
                            searchPosition='input'
                            entityName="Language level"
                            selectionMode="multi"
                            isSingleLine="true"
                            valueType="id"
                            sorting={{ field: 'level', direction: 'asc' }}
                            placeholder={"SEARCH CANDIDATES"}

                            renderFooter={() => {
                                return (
                                    <FlexRow padding="12">
                                        <FlexCell width="auto" style={{ marginLeft: "5%" }}>
                                            <Text color="primary">Candidate (UID)</Text>
                                        </FlexCell>
                                    </FlexRow>
                                );
                            }}
                        />
                    </div>
                    <Button caption="Show" onClick={() => { applyInputPercentage() }} rawProps={{ style: { marginRight: "2.5%" } }} ></Button>
                </FlexRow>

            </FlexRow>
        </FlexCell>
    );
}

export default FilterBox;