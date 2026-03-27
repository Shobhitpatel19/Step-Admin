import React, { useMemo, useState, useEffect } from "react";
import { useArrayDataSource } from "@epam/uui-core";
import {
  DataTable,
  Panel,
  Text,
  Button,
  Paginator,
  FlexCell,
  SearchInput,
  RichTextView,
} from "@epam/uui";
import css from "../module_css/AspirationTable.module.css";
import { deleteAspiration, fetchAspirationByPriority } from "../AspirationApi";
import { ReactComponent as ContentEditOutlineIcon } from "@epam/assets/icons/content-edit-outline.svg";
import { ReactComponent as ActionDeleteOutlineIcon } from "@epam/assets/icons/action-delete-outline.svg";
import AspirationFormModal from "./AspirationFormModal";
import Alert from "../../../common/Alert";
import { useDispatch } from "react-redux";
import { notify } from "../../../../redux/actions";
import { BorderAll } from "@mui/icons-material";

export default function AspirationTable({
  aspirations,
  handleEdit,
  handleDelete,
  isSubmissionDisabled,
}) {
  const [dataSourceState, setDataSourceState] = useState({});

  const flatAspirations = useMemo(() => {
    return aspirations.map(({ priority, aspirationList }) => ({
      priority: priority === "P1" ? "Primary" : "Secondary",
      ...aspirationList.reduce((acc, item) => {
        acc[item.title] = item.inputValue;
        return acc;
      }, {}),
    }));
  }, [aspirations]);

  const dataSource = useArrayDataSource(
    {
      items: flatAspirations,
      getId: ({ priority }) => priority,
    },
    [flatAspirations]
  );

  const columns = useMemo(
    () => [
      {
        key: "priorityNo",
        caption: "Priority No.",
        textAlign: "left",
        render: (item) => <Text>{item.priority}</Text>,
        width: 150,
      },
      {
        key: "aspiration",
        caption: "Aspiration",
        textAlign: "left",
        render: (item) => <Text>{item.Aspiration}</Text>,
        width: 200,
      },
      {
        key: "requirements",
        caption: "Requirements",
        textAlign: "left",
        render: (item) => <Text>{item.Requirements}</Text>,
        width: 200,
      },
      {
        key: "goals",
        caption: "Goals",
        textAlign: "left",
        render: (item) => <Text>{item.Goals}</Text>,
        width: 200,
      },
      {
        key: "alignments",
        caption: "Alignments",
        textAlign: "left",
        render: (item) => <Text>{item.Alignments}</Text>,
        width: 150,
      },
      {
        key: "realignments",
        caption: "Realignments",
        textAlign: "left",
        render: (item) => <Text>{item.Realignments}</Text>,
        width: 200,
      },
      {
        key: "actions",
        caption: "Actions",
        width: 100,
        textAlign: "left",
        render: (item) => (
          <div
            style={{
              display: "flex",
              justifyContent: "left",
              paddingTop: "10%",
              paddingBottom: "10%",
            }}
          >
            <div style={{ paddingRight: "5px" }}>
              <Button
                icon={ContentEditOutlineIcon}
                fill="none"
                onClick={() => handleEdit(item.priority)}
                isDisabled={isSubmissionDisabled}
              />
            </div>
            <div style={{ paddingRight: "5px" }}>
              <Button
                icon={ActionDeleteOutlineIcon}
                color="critical"
                fill="none"
                onClick={() => handleDelete(item.priority)}
                isDisabled={isSubmissionDisabled}
              />
            </div>
          </div>
        ),
      },
    ],
    [handleDelete, handleEdit]
  );

  const view = dataSource.useView(dataSourceState, setDataSourceState, {});

  return (
    <div>
      <Panel background="surface-main" shadow cx={css.container}>
        <DataTable
          {...view.getListProps()}
          getRows={view.getVisibleRows}
          value={dataSourceState}
          onValueChange={setDataSourceState}
          columns={columns}
          headerTextCase="upper"
        />
      </Panel>
    </div>
  );
}
