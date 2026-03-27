import React from "react";
import { render, screen } from "@testing-library/react";
import { AuthContext } from "../components/pages/AuthContext";

test("provides auth context", () => {
  const user = { email: "user@example.com", role: "SA", status: "Active" };

  render(
    <AuthContext.Provider value={{ user }}>
      <AuthContext.Consumer>
        {({ user }) => <div>{user.email}</div>}
      </AuthContext.Consumer>
    </AuthContext.Provider>
  );

  const emailElement = screen.getByText(/user@example.com/i);
  expect(emailElement).toBeInTheDocument();
});
