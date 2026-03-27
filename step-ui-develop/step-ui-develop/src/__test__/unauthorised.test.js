import React from "react";
import { render, screen } from "@testing-library/react";
import Unauthorized from "../components/pages/Unauthorised";
jest.mock("../components/pages/login_page/login.css", () => ({}));

describe("Unauthorized Component", () => {
  test("renders the unauthorized access message", () => {
    render(<Unauthorized />);
    const message = screen.getByText(/unauthorized access to step portal/i);
    expect(message).toBeInTheDocument();
    expect(message).toHaveClass("error-message");
  });
});
