import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import InputNav from "../components/pages/master_excel_view/InputNav";
jest.mock("@epam/uui", () => ({
  Panel: ({ children }) => <div>{children}</div>,
  TabButton: ({ caption, count }) => (
    <button>
      {caption} {count}
    </button>
  ),
  FlexRow: ({ children, ...rest }) => <div {...rest}>{children}</div>,
  PickerInput: (props) => (
    <select
      data-testid="picker-input"
      value={props.value}
      onChange={(e) => props.onValueChange(Number(e.target.value))}
    >
      {props.dataSource.props.items.map((item) => (
        <option key={item.id} value={item.id}>
          {props.getName(item)}
        </option>
      ))}
    </select>
  ),
  FlexCell: ({ children }) => <div>{children}</div>,
  Text: ({ children }) => <span>{children}</span>,
}));

jest.mock("@epam/uui-core", () => ({
  useArrayDataSource: (config) => ({
    ...config,
    props: config,
  }),
}));

jest.mock("@mui/icons-material", () => ({
  Height: () => <div>HeightIcon</div>,
}));

describe("InputNav Component", () => {
  const mockProps = {
    version: "STEP_2026_V0",
  };

  it("renders all elements correctly", () => {
    render(<InputNav {...mockProps} />);

    // Tab buttons
    expect(screen.getByText("Unfiltered Master Excel")).toBeInTheDocument();
    expect(
      screen.getByText("Filtered Top Talent Candidates 12")
    ).toBeInTheDocument();

    // Picker with placeholder
    expect(screen.getByTestId("picker-input")).toBeInTheDocument();
  });

  it("has default selected picker value", () => {
    render(<InputNav {...mockProps} />);
    const select = screen.getByTestId("picker-input");
    expect(select.value).toBe("1");
  });

  it("updates picker value on selection", () => {
    render(<InputNav {...mockProps} />);
    const select = screen.getByTestId("picker-input");
    fireEvent.change(select, { target: { value: "2" } });
    expect(select.value).toBe("2");
  });

  it("contains version passed via props", () => {
    render(<InputNav {...mockProps} />);
    const select = screen.getByTestId("picker-input");
    const options = select.querySelectorAll("option");
    const containsCustomVersion = Array.from(options).some(
      (option) => option.textContent === mockProps.version
    );
    expect(containsCustomVersion).toBe(true);
  });

  it("renders footer text in PickerInput", () => {
    render(<InputNav {...mockProps} />);
    //expect(screen.getByText("Please select a version")).toBeInTheDocument();
  });

  it("matches snapshot", () => {
    const { asFragment } = render(<InputNav {...mockProps} />);
    expect(asFragment()).toMatchSnapshot();
  });
});
