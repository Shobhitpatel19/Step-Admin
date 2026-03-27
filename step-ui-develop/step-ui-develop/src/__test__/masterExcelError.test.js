import React from "react";
import { render, fireEvent, screen } from "@testing-library/react";
import MasterExcelError from "../components/pages/master_excel_view/MasterExcelError";
import { useNavigate } from "react-router-dom";

const mockNavigate = jest.fn();

jest.mock("react-router-dom", () => ({
  useNavigate: () => mockNavigate,
}));

const MockAccordion = ({
  title,
  children,
  value = false,
  onValueChange = () => {},
}) => {
  const [open, setOpen] = React.useState(value);
  const handleToggle = () => {
    const newValue = !open;
    setOpen(newValue);
    onValueChange(newValue);
  };

  console.log(`Accordion ${title} open: ${open}`);
  return (
    <div>
      <h3 onClick={handleToggle} data-testid={`accordion-toggle-${title}`}>
        {title}
      </h3>
      {open && <div>{children}</div>}
    </div>
  );
};

jest.mock("@epam/uui", () => ({
  ModalBlocker: ({ children }) => <div>{children}</div>,
  ModalFooter: ({ children }) => <div>{children}</div>,
  ModalHeader: ({ onClose, borderBottom, title }) => (
    <div>
      <h1>{title}</h1>
      <button onClick={onClose}>Close</button>
    </div>
  ),
  ModalWindow: ({ children }) => <div>{children}</div>,
  FlexRow: ({ children }) => <div>{children}</div>,
  Panel: ({ children }) => <div>{children}</div>,
  ScrollBars: ({ children }) => <div>{children}</div>,
  Text: ({ children }) => <div>{children}</div>,
  Button: ({ caption, onClick }) => (
    <button onClick={onClick}>{caption}</button>
  ),
  Accordion: (props) => <MockAccordion {...props} />,
}));

describe("MasterExcelError", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("should render error message when `isError` is true", () => {
    const props = {
      isError: true,
      data: "An error occurred!",
    };

    render(<MasterExcelError {...props} />);

    expect(screen.getByText("An error occurred!")).toBeInTheDocument();
    expect(
      screen.queryByText("Cannot calculate Mean Score")
    ).toBeInTheDocument();
  });

  it("should render practice head details when there is no error", () => {
    const props = {
      isError: false,
      data: {
        practiceHeadListDetailed: {
          Practice1: { Head1: ["Candidate1", "Candidate2"] },
          Practice2: { Head2: ["Candidate3", "Candidate4"] },
        },
      },
    };

    render(<MasterExcelError {...props} />);
    expect(screen.getByText("Cannot calculate Mean Score")).toBeInTheDocument();
    expect(
      screen.getByText(
        "Below is the list of practices with respective practice heads and candidates under their respective practice, whose practice rating is yet to be finished. Mean score can be calculated once the ratings are finished."
      )
    ).toBeInTheDocument();
    expect(screen.getByText("Practice1")).toBeInTheDocument();
    expect(screen.getByText("Practice2")).toBeInTheDocument();
    fireEvent.click(screen.getByTestId("accordion-toggle-Practice1"));
    fireEvent.click(screen.getByTestId("accordion-toggle-Practice2"));
    expect(screen.getByText("Head2")).toBeInTheDocument();
    expect(screen.getByText("Candidate3")).toBeInTheDocument();
    expect(screen.getByText("Candidate4")).toBeInTheDocument();
  });

  it("should navigate to home when clicking 'Home' button", () => {
    const props = {
      isError: true,
      data: "An error occurred!",
    };

    render(<MasterExcelError {...props} />);

    fireEvent.click(screen.getByText("Home"));
    expect(mockNavigate).toHaveBeenCalledWith("/welcome");
  });

  it("should close the modal on header close button click", () => {
    const props = {
      isError: false,
      data: {
        practiceHeadListDetailed: {
          Practice1: { Head1: ["Candidate1"] },
        },
      },
    };

    render(<MasterExcelError {...props} />);

    fireEvent.click(screen.getByText("Close"));
    expect(mockNavigate).toHaveBeenCalledWith("/welcome");
  });

  it("should initialize foldState correctly", () => {
    const props = {
      isError: false,
      data: {
        practiceHeadListDetailed: {
          Practice1: { Head1: ["Candidate1"] },
          Practice2: { Head2: ["Candidate2"] },
        },
      },
    };

    render(<MasterExcelError {...props} />);
    fireEvent.click(screen.getByTestId("accordion-toggle-Practice1"));

    fireEvent.click(screen.getByTestId("accordion-toggle-Practice1"));

    fireEvent.click(screen.getByTestId("accordion-toggle-Practice2"));
  });
});
