/* eslint-disable react/jsx-no-undef */
import { ModalBlocker } from "@epam/uui";
import React from "react";
export const FutureSkillApprovalDialog = (props) => {
  return (
    <ModalBlocker disableCloseByEsc={true} disallowClickOutside={true} >
      <ModalWindow>
        <ModalHeader title="Confirm Approval" />
        <FlexRow padding="24">
          <Text fontSize="18">
            Are you sure you want to approve these Future Skills submissions?
          </Text>
        </FlexRow>
        <ModalFooter>
          <Button
            color="secondary"
            fill="outline"
            caption="Cancel"
            onClick={() => props.success(false)}
          />
          <Button
            color="accent"
            caption="Approve"
            onClick={() => props.success(true)}
          />
        </ModalFooter>
      </ModalWindow>
    </ModalBlocker>
  );
};
