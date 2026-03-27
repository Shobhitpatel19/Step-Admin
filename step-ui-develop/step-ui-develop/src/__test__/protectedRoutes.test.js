import React from "react";
import { render, screen } from "@testing-library/react";
import { MemoryRouter, Routes, Route } from "react-router-dom";
import ProtectedRoutes from "../components/pages/ProtectedRoutes"
import { AuthContext } from "../components/pages/AuthContext";

const DummyComponent = () => <div>Protected Content</div>;

describe("ProtectedRoutes", () => {
  it('should render the child route if role is "SA"', () => {
    render(
      <AuthContext.Provider value="SA">
        <MemoryRouter initialEntries={["/protected"]}>
          <Routes>
            <Route element={<ProtectedRoutes />}>
              <Route path="/protected" element={<DummyComponent />} />
            </Route>
          </Routes>
        </MemoryRouter>
      </AuthContext.Provider>
    );

    expect(screen.getByText("Protected Content")).toBeInTheDocument();
  });

  it('should redirect to /error if role is not "SA"', () => {
    render(
      <AuthContext.Provider value="USER">
        <MemoryRouter initialEntries={["/protected"]}>
          <Routes>
            <Route path="/error" element={<div>Error Page</div>} />
            <Route element={<ProtectedRoutes />}>
              <Route path="/protected" element={<DummyComponent />} />
            </Route>
          </Routes>
        </MemoryRouter>
      </AuthContext.Provider>
    );

    expect(screen.getByText("Error Page")).toBeInTheDocument();
  });
});
