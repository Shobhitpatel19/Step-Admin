import React from "react";
import { render, screen } from "@testing-library/react";
import Errorpage from "../components/pages/Errorpage";

test("renders error message", () => {
  render(<Errorpage />);
  const errorMessage = screen.getByText(
    /You Don't have permission to access this page/i
  );
  expect(errorMessage).toBeInTheDocument();
});
