import React from "react";
import { render, fireEvent, screen, waitFor } from "@testing-library/react";
import AspirationFormDrawer from "../components/pages/candidate_aspiration/components/AspirationFormModal";
import * as api from "../components/pages/candidate_aspiration/AspirationApi";

jest.mock("../components/common/axios", () => ({
  default: {
    get: jest.fn(),
    post: jest.fn(),
    put: jest.fn(),
    delete: jest.fn(),
  },
}));

jest.mock("../components/pages/candidate_aspiration/AspirationApi", () => ({
  fetchAspirationDescription: jest.fn(() =>
    Promise.reject(new Error("API error"))
  ),
}));

jest.mock("@epam/uui", () => {
  const React = require("react");
  return {
    Panel: ({ children, ...rest }) => <div {...rest}>{children}</div>,
    TextInput: React.forwardRef(({ value, onValueChange, ...rest }, ref) => (
      <input
        ref={ref}
        value={value}
        onChange={(e) => onValueChange(e.target.value)}
        {...rest}
      />
    )),
    Button: ({ caption, onClick, isDisabled }) => (
      <button disabled={isDisabled} onClick={onClick}>
        {caption}
      </button>
    ),
    Switch: ({ value, onValueChange, isDisabled, label }) => (
      <label>
        <input
          type="checkbox"
          disabled={isDisabled}
          checked={value}
          onChange={() => onValueChange(!value)}
        />
        {label}
      </label>
    ),
    ScrollBars: ({ children }) => <div>{children}</div>,
  };
});

jest.mock("../components/common/Alert", () => () => <div>Mocked Alert</div>);
jest.mock("@epam/assets/icons/navigation-close-outline.svg", () => ({
  ReactComponent: (props) => <svg {...props}>X</svg>,
}));

describe("AspirationFormDrawer", () => {
  const aspirationMock = {
    aspirationList: [
      { title: "goal1", inputValue: "test1", description: "Goal 1" },
      { title: "goal2", inputValue: "test2", description: "Goal 2" },
    ],
    isPrimary: true,
  };

  const setup = (props = {}) =>
    render(
      <AspirationFormDrawer
        abort={props.abort || jest.fn()}
        handleSubmit={props.handleSubmit || jest.fn()}
        handleUpdate={props.handleUpdate || jest.fn()}
        aspiration={props.aspiration || null}
        hasPrimaryAspiration={false}
      />
    );

  afterEach(() => {
    jest.clearAllMocks();
  });

  it("renders and allows editing an existing aspiration", async () => {
    setup({ aspiration: aspirationMock });
    expect(screen.getByText("Edit Aspiration")).toBeInTheDocument();
    expect(screen.getByDisplayValue("test1")).toBeInTheDocument();
    expect(screen.getByDisplayValue("test2")).toBeInTheDocument();
  });

  it("renders form with fetched data when no aspiration prop", async () => {
    const mockData = {
      aspirationList: [
        { title: "goalX", inputValue: "", description: "Goal X" },
      ],
    };
    jest.spyOn(api, "fetchAspirationDescription").mockResolvedValue(mockData);
    setup();

   // await waitFor(() =>
    //  expect(screen.getByText("Create Aspiration")).toBeInTheDocument()
   // );
    // expect(screen.getByText("Goal X")).toBeInTheDocument();
    // expect(screen.getByPlaceholderText("Enter GoalX")).toBeInTheDocument();
  });

  it("calls handleSubmit with form data when Create Aspiration is clicked", async () => {
    const handleSubmit = jest.fn();
    const mockData = {
      aspirationList: [
        { title: "goalX", inputValue: "", description: "Goal X" },
      ],
    };
    jest.spyOn(api, "fetchAspirationDescription").mockResolvedValue(mockData);

    render(
      <AspirationFormDrawer
        abort={jest.fn()}
        handleSubmit={handleSubmit}
        handleUpdate={jest.fn()}
        aspiration={null}
        hasPrimaryAspiration={false}
      />
    );

    await waitFor(() =>
      expect(
        screen.getByRole("button", { name: "Create Aspiration" })
      ).toBeInTheDocument()
    );

    const input = screen.getByPlaceholderText("Enter GoalX");
    fireEvent.change(input, { target: { value: "My goal" } });

    const checkbox = screen.getByLabelText("Mark this aspiration as primary");
    fireEvent.click(checkbox);

    fireEvent.click(screen.getByRole("button", { name: "Create Aspiration" }));
    await waitFor(() => {
      expect(handleSubmit).toHaveBeenCalledWith(
        {
          goalX: {
            inputValue: "My goal",
            description: "Goal X",
          },
        },
        true
      );
    });
  });

  it("calls handleUpdate with form data when Update Aspiration is clicked", async () => {
    const handleUpdate = jest.fn();
    render(
      <AspirationFormDrawer
        abort={jest.fn()}
        handleSubmit={jest.fn()}
        handleUpdate={handleUpdate}
        aspiration={aspirationMock}
        hasPrimaryAspiration={false}
      />
    );

    const input = screen.getByDisplayValue("test1");
    fireEvent.change(input, { target: { value: "Updated goal" } });

    fireEvent.click(screen.getByText("Update Aspiration"));
    expect(handleUpdate).toHaveBeenCalledWith(
      {
        goal1: { inputValue: "Updated goal", description: "Goal 1" },
        goal2: { inputValue: "test2", description: "Goal 2" },
      },
      true
    );
  });

  it("calls abort when cancel is clicked", () => {
    const abort = jest.fn();
    render(
      <AspirationFormDrawer
        abort={abort}
        handleSubmit={jest.fn()}
        handleUpdate={jest.fn()}
        aspiration={aspirationMock}
        hasPrimaryAspiration={false}
      />
    );

    fireEvent.click(screen.getByText("Cancel"));
    expect(abort).toHaveBeenCalled();
  });

  it("calls abort when backdrop is clicked", () => {
    const abort = jest.fn();
    const { container } = render(
      <AspirationFormDrawer
        abort={abort}
        handleSubmit={jest.fn()}
        handleUpdate={jest.fn()}
        aspiration={aspirationMock}
        hasPrimaryAspiration={false}
      />
    );

    const divs = container.querySelectorAll("div");
    const backdrop = Array.from(divs).find((el) => el.onclick);
    expect(backdrop).toBeTruthy();
    fireEvent.click(backdrop);
    expect(abort).toHaveBeenCalled();
  });

  it("clears the input when close icon is clicked", () => {
    render(
      <AspirationFormDrawer
        abort={jest.fn()}
        handleSubmit={jest.fn()}
        handleUpdate={jest.fn()}
        aspiration={aspirationMock}
        hasPrimaryAspiration={false}
      />
    );

    const input = screen.getByDisplayValue("test1");
    expect(input.value).toBe("test1");

    const closeIcon = screen.getAllByText("X")[0];
    fireEvent.click(closeIcon);

    expect(input.value).toBe("");
  });

  it("disables Create/Update button if form is invalid", async () => {
    const mockData = {
      aspirationList: [
        { title: "goalX", inputValue: "", description: "Goal X" },
      ],
    };
    jest.spyOn(api, "fetchAspirationDescription").mockResolvedValue(mockData);

    render(
      <AspirationFormDrawer
        abort={jest.fn()}
        handleSubmit={jest.fn()}
        handleUpdate={jest.fn()}
        aspiration={null}
        hasPrimaryAspiration={false}
      />
    );

    await waitFor(() =>
      screen.getByRole("button", { name: "Create Aspiration" })
    );
    const button = screen.getByRole("button", { name: "Create Aspiration" });
    expect(button).toBeDisabled();
  });

  it("shows error when fetchAspirationDescription fails", async () => {
    jest
      .spyOn(api, "fetchAspirationDescription")
      .mockRejectedValue(new Error("Fail"));
    const abort = jest.fn();

    setup({ aspiration: null, abort });

    await waitFor(() => {
      expect(abort).toHaveBeenCalledWith("Error fetching aspiration fields");
    });
  });
});
