import React from 'react';
import {
    ModalBlocker, ModalFooter, ModalHeader, ModalWindow, Panel,
} from '@epam/uui';
import DelegateContent from './components/DelegateContent';

export default function Delegate(props) {

    return (
        <ModalBlocker {...props}>
            <ModalWindow width="420px">
                <Panel background="surface-main" >
                    <DelegateContent {...props} />
                </Panel>
            </ModalWindow>
        </ModalBlocker>
    );
}
