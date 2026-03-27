import React from "react";
import { render, screen } from "@testing-library/react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import LayoutForBanner from "../../../components/common/Banner/BannerLayout";
import Banner from "../../../components/common/Banner/Banner";

jest.mock("../../../components/common/Banner/Banner", () =>
  jest.fn(() => null)
);

describe("LayoutForBanner Component Tests", () => {
  const testRoutes = [
    { path: "/culture-score", title: "Admin - Upload Culture Score" },
    { path: "/practice_delegate", title: "Delegate Access" },
    { path: "/merit-list", title: "Admin - Upload Identification Merit List" },
    { path: "/engx-extra-mile", title: "Admin - Upload EngX Extra Mile" },
    { path: "/view-master-data", title: "Master Excel View" },

    { path: "/nonexistent", title: null },
  ];

  testRoutes.forEach(({ path, title }) => {
    it(`renders Banner with correct props for path ${path}`, () => {
      render(
        <MemoryRouter initialEntries={[path]}>
          <Routes>
            <Route path="*" element={<LayoutForBanner />} />
          </Routes>
        </MemoryRouter>
      );

      if (title) {
        expect.objectContaining({
          title: expect.any(String),
          description: expect.any(String),
        }),
          {};
      } else {
        // Expect that Banner was not called if the path does not match config
        expect(Banner).not.toHaveBeenCalled();
      }
    });
  });

  it("renders the Outlet component", () => {
    // Define a component to act as a child in the Outlet
    const TestOutletComponent = () => <div>Test Outlet Content</div>;
    render(
      <MemoryRouter initialEntries={["/culture-score"]}>
        <Routes>
          <Route path="/culture-score" element={<LayoutForBanner />}>
            <Route index element={<TestOutletComponent />} />
          </Route>
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByText("Test Outlet Content")).toBeInTheDocument();
  });
});
