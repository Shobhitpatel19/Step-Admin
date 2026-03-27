import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import AspirationTable from "../components/pages/candidate_aspiration/components/AspirationTable";
jest.mock(
  "../components/pages/candidate_aspiration/module_css/AspirationTable.module.css",
  () => ({
    container: "mocked-container",
  })
);
jest.mock("@epam/assets/icons/content-edit-outline.svg", () => ({
  ReactComponent: () => <svg data-testid="edit-icon" />,
}));
jest.mock("@epam/assets/icons/action-delete-outline.svg", () => ({
  ReactComponent: () => <svg data-testid="delete-icon" />,
}));

jest.mock("../components/pages/candidate_aspiration/AspirationApi", () => ({
  deleteAspiration: jest.fn(),
  fetchAspirationByPriority: jest.fn(),
}));
jest.mock("react-redux", () => ({
  useDispatch: () => jest.fn(),
}));
jest.mock("../redux/actions", () => ({
  notify: jest.fn(),
}));

// Mock uui-core
const mockUseArrayDataSource = jest.fn();
jest.mock("@epam/uui-core", () => ({
  useArrayDataSource: (config) => {
    const items = config.items || [];
    return {
      useView: () => ({
        getListProps: () => ({}),
        getVisibleRows: () => items,
      }),
    };
  },
}));

// Mock uui components
jest.mock("@epam/uui", () => ({
  DataTable: (props) => (
    <table data-testid="datatable">
      <tbody>
        {props.getRows().map((row, index) => (
          <tr key={index}>
            {props.columns.map((col) => (
              <td key={col.key}>{col.render(row)}</td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  ),
  Panel: ({ children }) => <div data-testid="panel">{children}</div>,
  Text: ({ children }) => <span>{children}</span>,
  Button: ({ icon: Icon, onClick, isDisabled }) => (
    <button onClick={onClick} disabled={isDisabled}>
      <Icon />
    </button>
  ),
  Paginator: () => <div data-testid="paginator" />,
  FlexCell: ({ children }) => <div>{children}</div>,
  SearchInput: () => <input type="text" />,
  RichTextView: () => <div>RichTextView</div>,
}));

describe("AspirationTable", () => {
  const mockHandleEdit = jest.fn();
  const mockHandleDelete = jest.fn();

  const sampleData = [
    {
      priority: "P1",
      aspirationList: [
        {
          title: "Aspiration",
          inputValue: "Become a team lead",
        },
        {
          title: "Requirements",
          inputValue: "Improve soft skills",
        },
        {
          title: "Goals",
          inputValue: "Lead 3 projects",
        },
        {
          title: "Alignments",
          inputValue: "With company objectives",
        },
        {
          title: "Realignments",
          inputValue: "Adjust to team needs",
        },
      ],
    },
    {
      priority: "P2",
      aspirationList: [
        {
          title: "Aspiration",
          inputValue: "Learn DevOps",
        },
        {
          title: "Requirements",
          inputValue: "Understand CI/CD",
        },
        {
          title: "Goals",
          inputValue: "Automate deployments",
        },
        {
          title: "Alignments",
          inputValue: "Cross-functional goals",
        },
        {
          title: "Realignments",
          inputValue: "Adapt to cloud",
        },
      ],
    },
  ];

  const setup = (isSubmissionDisabled = false) =>
    render(
      <AspirationTable
        aspirations={sampleData}
        handleEdit={mockHandleEdit}
        handleDelete={mockHandleDelete}
        isSubmissionDisabled={isSubmissionDisabled}
      />
    );

  it("renders table and data correctly", () => {
    setup();

    expect(screen.getByTestId("panel")).toBeInTheDocument();
    expect(screen.getByTestId("datatable")).toBeInTheDocument();

    expect(screen.getAllByText("Primary")[0]).toBeInTheDocument();
    expect(screen.getAllByText("Secondary")[0]).toBeInTheDocument();
    expect(screen.getByText("Become a team lead")).toBeInTheDocument();
    expect(screen.getByText("Understand CI/CD")).toBeInTheDocument();
  });

  it("calls handleEdit when edit button is clicked", () => {
    setup();
    const editButtons = screen.getAllByTestId("edit-icon");
    fireEvent.click(editButtons[0].closest("button"));
    expect(mockHandleEdit).toHaveBeenCalledWith("Primary");
  });

  it("calls handleDelete when delete button is clicked", () => {
    setup();
    const deleteButtons = screen.getAllByTestId("delete-icon");
    fireEvent.click(deleteButtons[1].closest("button"));
    expect(mockHandleDelete).toHaveBeenCalledWith("Secondary");
  });

  it("disables buttons when isSubmissionDisabled is true", () => {
    setup(true);
    const buttons = screen.getAllByRole("button");
    buttons.forEach((btn) => {
      expect(btn).toBeDisabled();
    });
  });
});
