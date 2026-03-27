import css from '../../styling/master_excel_view/MasterExcelView.module.css'
import { Panel, TabButton } from "@epam/uui";
import { FlexRow, PickerInput, FlexCell, Text } from '@epam/uui';
import { useArrayDataSource } from '@epam/uui-core';
import { useState } from "react";
import { Button } from "@epam/uui";
import { Height } from '@mui/icons-material';


const InputNav = (props) => {

  
const languageLevels = [
    {id : 1 , level : props.version},
    { id: 2, level: 'STEP_2025_V1 (Latest)' },
    { id: 3, level: 'STEP_2024_V4' },
    { id: 4, level: 'STEP_2024_V3' },
    { id: 5, level: 'STEP_2024_V2' },
];

    console.log(props, "PROPS")
    const [singlePickerValue, singleOnValueChange] = useState(1);

    const handleChange = (value) => { 
        singleOnValueChange(value);
    }

    const dataSource = useArrayDataSource(
        {
            items: languageLevels,
        },
        [],
    );

    return (

            <FlexRow justifyContent='space-between' borderBottom="true"  rawProps={{style: {alignItems  : "center", border : "2px solid green"}}} >

                <FlexRow>
                    <TabButton isLinkActive="true" caption="Unfiltered Master Excel" size="60" />
                    <TabButton isLinkActive="true" caption="Filtered Top Talent Candidates" size="60" count={12} />
                </FlexRow>

                <FlexRow rawProps={{ style: { marginRight: "1%" } }} >
                    <PickerInput
                        minBodyWidth={150}
                        dataSource={dataSource}
                        value={singlePickerValue}
                        onValueChange={handleChange}
                        getName={(item) => item.level}
                        entityName="Language level"
                        selectionMode="single"
                        valueType="id"
                        sorting={{ field: 'level', direction: 'asc' }}
                        placeholder={"STEP_YEAR_VERSION"}

                        renderFooter={() => {
                            return (
                                <FlexRow padding="12">
                                    <FlexCell width="auto">
                                        <Text color="primary">Please select a version</Text>
                                    </FlexCell>
                                </FlexRow>
                            );
                        }}
                    />
                </FlexRow>

            </FlexRow>
    );
}

export default InputNav;