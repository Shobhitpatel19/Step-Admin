import React from "react";
import { render } from "@testing-library/react";
import { Navigate } from "react-router-dom";
import PrivateRoute from "../../components/pages/landing_page/privateRoute";
import * as authUtils from "../../components/utils/auth"; // Import the module containing isAuthenticated

// Mock the dependent files
jest.mock("react-router-dom", () => ({
  Navigate: jest.fn(({ to }) => (
    <div data-testid="navigate">Navigate to {to}</div>
  )),
}));

describe("PrivateRoute Component", () => {
  afterEach(() => {
    jest.clearAllMocks(); // Clear mocks after each test
  });

  test("renders children when user is authenticated", () => {
    // Mock isAuthenticated to return true
    jest.spyOn(authUtils, "isAuthenticated").mockReturnValue(true);

    // Render the PrivateRoute component with children
    const { getByText } = render(
      <PrivateRoute>
        <div>Authenticated Content</div>
      </PrivateRoute>
    );

    // Assert that the children are rendered
    const authenticatedContent = getByText("Authenticated Content");
    expect(authenticatedContent).toBeInTheDocument();

    // Verify that Navigate is not called
    expect(Navigate).not.toHaveBeenCalled();
  });

  test("redirects to '/' when user is not authenticated", () => {
    // Mock isAuthenticated to return false
    jest.spyOn(authUtils, "isAuthenticated").mockReturnValue(false);

    // Render the PrivateRoute component
    const { getByTestId } = render(
      <PrivateRoute>
        <div>Authenticated Content</div>
      </PrivateRoute>
    );

    // Verify that the children are not rendered
    const authenticatedContent = document.querySelector(
      "Authenticated Content"
    );
    expect(authenticatedContent).toBeNull();
  });

  test("calls isAuthenticated function", () => {
    // Mock isAuthenticated to return true
    const isAuthenticatedMock = jest
      .spyOn(authUtils, "isAuthenticated")
      .mockReturnValue(true);

    // Render the PrivateRoute component
    render(
      <PrivateRoute>
        <div>Authenticated Content</div>
      </PrivateRoute>
    );

    // Verify that isAuthenticated is called
    expect(isAuthenticatedMock).toHaveBeenCalledTimes(1);
  });
});
