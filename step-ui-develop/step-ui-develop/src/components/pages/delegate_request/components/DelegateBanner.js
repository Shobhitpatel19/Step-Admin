import React from "react";
import { FlexCell, FlexRow } from "@epam/uui-components";
import css from "../module_css/PracticeDelegateBanner.module.css"
import { Text } from "@epam/uui";
import { ReactComponent as NavigationCloseOutlineIcon } from '@epam/assets/icons/navigation-close-outline.svg';
import { IconButton } from '@epam/uui';
export default function DelegateBanner({ onClose }) {

    return (
        <FlexRow cx={css.container}>
            <FlexRow cx={css.internalContainer}>
                <FlexCell grow={1}>
                    <div >

                        <div style={{ display: "flex", flexDirection: "row" }}>
                            <Text
                                fontSize="28"
                                fontWeight="600"
                                color="white"
                                cx={css.title}>
                                Delegate Access
                            </Text>
                            <IconButton icon={NavigationCloseOutlineIcon} onClick={onClose} color="white" />
                        </div>
                    </div>
                    <Text
                        fontSize="14"
                        color="white"
                        cx={css.description}>
                        You can delegate your responsibilities to anyone at or above B3 level
                    </Text>
                </FlexCell>
            </FlexRow>
        </FlexRow>
    );
}
