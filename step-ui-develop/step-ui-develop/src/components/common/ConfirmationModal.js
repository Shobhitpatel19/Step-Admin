import React from "react";
import { IModal, useUuiContext } from "@epam/uui-core";

import {
  ModalBlocker,
  ModalFooter,
  ModalHeader,
  ModalWindow,
  FlexRow,
  Panel,
  ScrollBars,
  Text,
  Button,
  SuccessNotification,
  WarningNotification,
} from "@epam/uui";

export function ConfirmationModal(props) {
  return (
    <ModalBlocker {...props}>
      <ModalWindow>
        <Panel background="surface-main">
          <ModalHeader
            title={props.title}
            rawProps={{ style: { paddingBottom: "5px" } }}
            onClose={() => props.abort()}
          />
          <ScrollBars hasTopShadow hasBottomShadow>
            <FlexRow padding="24" alignItems="center">
              <Text size="36">{props.description}</Text>
            </FlexRow>
          </ScrollBars>
          <ModalFooter rawProps={{ style: { justifyContent: "end" } }}>
            <Button
              color="secondary"
              fill="outline"
              caption="Cancel"
              onClick={() => props.abort()}
            />
            <Button
              color="accent"
              caption="Confirm"
              onClick={() => {
                props.success("Success action");
                props.abort();
              }}
            />
          </ModalFooter>
        </Panel>
      </ModalWindow>
    </ModalBlocker>
  );
}
