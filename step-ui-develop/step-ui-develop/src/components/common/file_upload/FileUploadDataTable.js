import React, { useMemo, useState } from "react";
import { useArrayDataSource } from "@epam/uui-core";
import { DataTable, Panel, Text } from "@epam/uui";
import css from "../../styling/file_upload/FileUpload.module.css"
// import { TextOverflow } from "@epam/loveship";

export default function FileUploadDataTable({ items, appendColumn }) {
  const [value, onValueChange] = useState({});

  const dataSource = useArrayDataSource(
    {
      items,
      getId: ({ UID }) => UID,
    },
    []
  );

  const keysArray = [
    "UID",
    "Name",
    "Location",
    "DOJ",
    "Time With EPAM",
    "Title",
    "Status",
    "Production Category",
    "Job Function",
    "Resource Manager",
    "PGM",
    "Project Code",
    "JF Level",
    "Competency Practice",
    "Primary Skill",
    "Niche Skills",
    "Niche Skill Yes/No",
    "Previous Year Talent Profile",
    "Talent Profile",
    "Delivery Feedback TT Score",
  ];

  keysArray.push(...appendColumn);
  console.log(keysArray, "keysArray");

  function getFix(key) {
    if (appendColumn.includes(key)) {
      return "right";
    } else if (key === "UID") {
      return "left";
    }
  }
  const columns = useMemo(
    () =>
      Object.keys(items[0])
        .filter((key) => keysArray.includes(key))
        .map((key) => ({
          key,
          caption: key,
          render: (item) => <Text color="primary">{item[key]}</Text>,
          // width: 200,
          width: 140,
          textAlign: "center",
          fix: getFix(key),
        })),
    []
  );

  const view = dataSource.useView(value, onValueChange, {});
  return (
    <Panel background="surface-main" shadow cx={css.container}>
      <DataTable
        {...view.getListProps()}
        getRows={view.getVisibleRows}
        value={value}
        onValueChange={onValueChange}
        columns={columns}
        headerTextCase="upper"
      />
    </Panel>
  );
}
