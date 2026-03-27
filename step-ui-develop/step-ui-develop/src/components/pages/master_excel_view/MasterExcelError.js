import React, { useState } from 'react';
import {
    ModalBlocker, ModalFooter, ModalHeader, ModalWindow, FlexRow, Panel, ScrollBars, Text, Button, Accordion
} from '@epam/uui';
import { useNavigate } from "react-router-dom";

const MasterExcelError = (props) => {
    const navigate = useNavigate();
    const [practiceHeadListDetailed, setPracticeHeadListDetailed] = useState(props.isError ? {} : props.data.practiceHeadListDetailed);

    const firstKey = Object.keys(practiceHeadListDetailed)[0];

    const [foldState, setFoldState] = useState(() => {
        const initialState = { [firstKey]: true };
        Object.keys(practiceHeadListDetailed).forEach((key) => {
            if (key !== firstKey) initialState[key] = false;
        });
        return initialState;
    });

    const handleValueChange = (key, isOpen) => {
        setFoldState((prevState) => ({
            ...prevState,
            [key]: isOpen,
        }));
    };

    const handleNavigation = () => {
        navigate("/welcome");
    };

    return (
        <div>
            <ModalBlocker disableCloseByEsc={true} disallowClickOutside={true} {...props} rawProps={{ style: { zIndex: "10" } }}>
                <ModalWindow>
                    <Panel background="surface-main" rawProps={{ style: { width: "100%" } }}>
                        <ModalHeader onClose={handleNavigation} borderBottom title="Cannot calculate Mean Score" />
                        <FlexRow rawProps={{ style: { margin: "5%" } }}>

                            {props.isError ?
                                <Text>
                                    {props.data}
                                </Text>
                                :
                                <Text>
                                    Below is the list of practices with respective practice heads and candidates under their
                                    respective practice, whose practice rating is yet to be finished. Mean score can be calculated
                                    once the ratings are finished.
                                </Text>
                            }

                        </FlexRow>
                        {!props.isError && <ScrollBars>
                            <div style={{ marginBottom: "2.5%" }}>
                                {Object.entries(practiceHeadListDetailed).map(([key, value]) => (
                                    <div style={{ marginLeft: "2.5%", marginRight: "2.5%" }} key={key}>
                                        <Accordion
                                            title={key}
                                            mode="inline"
                                            value={foldState[key]}
                                            onValueChange={(isOpen) => handleValueChange(key, isOpen)}
                                            rawProps={{ "data-testid": "unfold-state" }}
                                        >
                                            <div>
                                                <Text fontSize="16">
                                                    {Object.entries(value).map(([subKey, names]) => (
                                                        <div key={subKey} style={{ marginLeft: '20px' }}>
                                                            <p style={{ margin: "0%" }}>
                                                                <strong>{subKey}</strong>
                                                            </p>
                                                            <ul>
                                                                {names.map((name, index) => (
                                                                    <li key={index}>{name}</li>
                                                                ))}
                                                            </ul>
                                                        </div>
                                                    ))}
                                                </Text>
                                            </div>
                                        </Accordion>
                                    </div>
                                ))}
                            </div>
                        </ScrollBars>
                        }
                        <ModalFooter borderTop={true} rawProps={{ style: { justifyContent: "end" } }}>
                            <Button color="accent" fill="outline" caption="Home" onClick={handleNavigation} />
                        </ModalFooter>
                    </Panel>
                </ModalWindow>
            </ModalBlocker>
        </div>
    );
};

export default MasterExcelError;