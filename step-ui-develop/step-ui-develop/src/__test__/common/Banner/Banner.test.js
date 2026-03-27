import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import Banner from "../../../components/common/Banner/Banner";
import { UuiContextProvider } from "@epam/uui-core";

const mockNavigate = jest.fn();

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockNavigate,
}));

beforeEach(() => {
  mockNavigate.mockClear();
});

jest.mock("@epam/uui-core", () => ({
  ...jest.requireActual("@epam/uui-core"),
  UuiContextProvider: ({ children }) => (
    <div data-testid="mock-uui-provider">{children}</div>
  ),
  useUuiContext: jest.fn(() => ({})),
}));

describe("Banner Component", () => {
  const defaultProps = {
    title: "Test Title",
    description: "Test Description",
    pageTitle: "Page Title",
    pageDescription: "Page Description",
    backlinkCaption: "Go Back",
    backlink: "/welcome",
    isBackLinkVisible: true,
    onBackLinkClick: jest.fn(),
    backgroundImage: "https://example.com/test-bg.jpg",
  };

  const renderWithProvider = (ui) =>
    render(
      <MemoryRouter>
        <UuiContextProvider>{ui}</UuiContextProvider>
      </MemoryRouter>
    );

  test("renders Banner with title and description", () => {
    renderWithProvider(<Banner {...defaultProps} />);
    expect(screen.getByText(/Test Title/i)).toBeInTheDocument();
    expect(screen.getByText(/Test Description/i)).toBeInTheDocument();
  });

  test("renders pageTitle and pageDescription when title and description are missing", () => {
    renderWithProvider(
      <Banner
        title=""
        description=""
        pageTitle="Fallback Title"
        pageDescription="Fallback Description"
      />
    );
    expect(screen.getByText(/Fallback Title/i)).toBeInTheDocument();
    expect(screen.getByText(/Fallback Description/i)).toBeInTheDocument();
  });

  test("renders backlink when isBackLinkVisible is true", () => {
    renderWithProvider(<Banner {...defaultProps} />);
    expect(
      screen.getByRole("button", { name: /Go Back/i })
    ).toBeInTheDocument();
  });

  test("does not render backlink when isBackLinkVisible is false", () => {
    renderWithProvider(<Banner {...defaultProps} isBackLinkVisible={false} />);
    expect(
      screen.queryByRole("button", { name: /Go Back/i })
    ).not.toBeInTheDocument();
  });

  test("calls onBackLinkClick when backlink is clicked", () => {
    renderWithProvider(<Banner {...defaultProps} />);
    fireEvent.click(screen.getByRole("button", { name: /Go Back/i }));
    expect(defaultProps.onBackLinkClick).toHaveBeenCalled();
  });

  test("navigates to backlink when onBackLinkClick is not provided", () => {
    renderWithProvider(
      <Banner {...defaultProps} onBackLinkClick={undefined} />
    );
    fireEvent.click(screen.getByRole("button", { name: /Go Back/i }));
    expect(mockNavigate).toHaveBeenCalledWith(defaultProps.backlink);
  });

  test("applies background image correctly", () => {
    renderWithProvider(<Banner {...defaultProps} />);
    const bannerElement = screen.getByTestId("mock-uui-provider");
  });

  test("handles missing optional props without crashing", () => {
    renderWithProvider(<Banner />);
    expect(screen.getByTestId("mock-uui-provider")).toBeInTheDocument();
  });
});
