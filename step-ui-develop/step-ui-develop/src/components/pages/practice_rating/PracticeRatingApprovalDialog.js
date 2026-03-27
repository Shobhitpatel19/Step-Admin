import React from "react";
import { IModal, useUuiContext } from "@epam/uui-core";
import { demoData } from "@epam/uui-docs";
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

export function PracticeRatingApprovalDialog(props) {
  return (
    <ModalBlocker {...props}>
      <ModalWindow>
        <Panel background="surface-main">
          <ModalHeader
            title="Want to approve?"
            rawProps={{ style: { paddingBottom: "5px" } }}
            onClose={() => props.abort()}
          />
          <ScrollBars hasTopShadow hasBottomShadow>
            <FlexRow padding="24" alignItems="center">
              <Text size="36">{"You can't modify once its approved!"}</Text>
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
              caption="Approve"
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
