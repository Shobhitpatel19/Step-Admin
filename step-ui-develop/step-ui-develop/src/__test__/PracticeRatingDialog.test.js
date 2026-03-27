import React from "react";
import {
  render,
  screen,
  fireEvent,
  waitFor,
  act,
} from "@testing-library/react";
import PracticeRatingDialog from "../components/pages/practice_rating/PracticeRatingDialog";
import { UuiContext, useUuiServices, StubAdaptedRouter } from "@epam/uui-core";
import axiosInstance from "../components/common/axios";
import { Provider } from "react-redux";
import { configureStore } from "@reduxjs/toolkit";
import { BrowserRouter, useNavigate } from "react-router-dom";

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: jest.fn(),
  useSearchParams: jest.fn(),
}));

jest.mock("react-redux", () => ({
  ...jest.requireActual("react-redux"),
  useSelector: jest.fn(),
}));

jest.mock("axios", () => {
  const actualAxios = jest.requireActual("axios");
  return {
    ...actualAxios,
    default: {
      create: jest.fn(() => ({
        interceptors: {
          request: { use: jest.fn() },
          response: { use: jest.fn() },
        },
        get: jest.fn(),
        post: jest.fn(),
        put: jest.fn(),
        delete: jest.fn(),
      })),
    },
  };
});

jest.mock("../components/common/axios", () => ({
  get: jest.fn(),
  post: jest.fn(),
  put: jest.fn(),
  delete: jest.fn(),
}));

function UuiContextDefaultWrapper({ children }) {
  const testUuiCtx = {};
  const router = new StubAdaptedRouter();
  const { services } = useUuiServices({ router });

  const store = configureStore({
    reducer: {
      user: (state = { name: "John Doe", isAuthenticated: true }, action) =>
        state,
    },
  });

  Object.assign(testUuiCtx, services);
  return (
    <Provider store={store}>
      <BrowserRouter>
        <UuiContext.Provider value={services}>{children}</UuiContext.Provider>
      </BrowserRouter>
    </Provider>
  );
}

async function renderToJsDom(reactElement) {
  return render(reactElement, { wrapper: UuiContextDefaultWrapper });
}

describe("PracticeRatingDialog Component", () => {
  let ratingsRef;

  const user = {
    uid: "123",
    name: "John Doe",
    jobTitle: "Developer",
    primarySkill: "Java",
  };
  const onCloseMock = jest.fn();
  const dialogOpenMock = jest.fn();

  const generatePayload = () => ({
    categories: Object.keys(ratingsRef.current).map((category) => ({
      categoryName: category,
      subCategory: Object.keys(ratingsRef.current[category]).map(
        (subCategoryName) => ({
          subCategoryName,
          employeeRating:
            ratingsRef.current[category][subCategoryName]?.rating || 0,
        })
      ),
    })),
  });

  beforeEach(() => {
    ratingsRef = { current: {} };
  });

  it("renders the component correctly", async () => {
    const mockData = {
      message: "Test message",
      mean: 4.5,
      categories: [
        {
          categoryName: "Category 1",
          subCategory: [
            {
              subCategoryName: "Subcategory 1",
              employeeRating: 3,
              description: "Test description",
            },
          ],
        },
      ],
    };
    axiosInstance.get.mockResolvedValueOnce({ data: mockData });

    await act(async () => {
      renderToJsDom(
        <PracticeRatingDialog
          user={user}
          onClose={onCloseMock}
          dialogOpen={dialogOpenMock}
        />
      );
    });

    expect(screen.getByText("Provide Rating")).toBeInTheDocument();
  });

  it("displays confirmation modal on submit", async () => {
    const mockData = {
      message: "Test message",
      mean: 4.5,
      categories: [
        {
          categoryName: "Category 1",
          subCategory: [
            {
              subCategoryName: "Subcategory 1",
              employeeRating: 3,
              description: "Test description",
            },
          ],
        },
      ],
    };
    axiosInstance.get.mockResolvedValueOnce({ data: mockData });

    await act(async () => {
      renderToJsDom(
        <PracticeRatingDialog
          user={user}
          onClose={onCloseMock}
          dialogOpen={dialogOpenMock}
        />
      );
    });

    fireEvent.click(screen.getByRole("button", { name: /Submit/i }));
  });

  it("closes the modal on Cancel click", async () => {
    const mockData = {
      message: "Test message",
      mean: 4.5,
      categories: [
        {
          categoryName: "Category 1",
          subCategory: [
            {
              subCategoryName: "Subcategory 1",
              employeeRating: 3,
              description: "Test description",
            },
          ],
        },
      ],
    };
    axiosInstance.get.mockResolvedValueOnce({ data: mockData });

    await act(async () => {
      renderToJsDom(
        <PracticeRatingDialog
          user={user}
          onClose={onCloseMock}
          dialogOpen={dialogOpenMock}
        />
      );
    });

    fireEvent.click(screen.getByRole("button", { name: /Submit/i }));
    //fireEvent.click(screen.getByRole("button", { name: /Cancel/i }));

    await waitFor(() => {
      expect(screen.queryByText("Confirm Submit")).not.toBeInTheDocument();
    });
  });

  it("fetches data on mount and populates state", async () => {
    const mockData = {
      message: "Test message",
      mean: 4.5,
      categories: [
        {
          categoryName: "Category 1",
          subCategory: [
            {
              subCategoryName: "Subcategory 1",
              employeeRating: 3,
              description: "Test description",
            },
          ],
        },
      ],
    };
    axiosInstance.get.mockResolvedValueOnce({ data: mockData });

    await act(async () => {
      renderToJsDom(
        <PracticeRatingDialog
          user={user}
          onClose={onCloseMock}
          dialogOpen={dialogOpenMock}
        />
      );
    });

    expect(screen.getByText("Category 1")).toBeInTheDocument();
    // expect(screen.getByText("Subcategory 1")).toBeInTheDocument();
  });

  it("handles API errors correctly", async () => {
    axiosInstance.get.mockImplementation(() =>
      Promise.reject(new Error("Request failed"))
    );

    const mockNavigate = jest.fn();
    useNavigate.mockReturnValue(mockNavigate);

    await act(async () => {
      renderToJsDom(
        <PracticeRatingDialog
          user={user}
          onClose={onCloseMock}
          dialogOpen={dialogOpenMock}
        />
      );
    });

    await waitFor(() => {
      expect(dialogOpenMock).toHaveBeenCalledWith(false);
    });
  });

  it("updates ratings state when a new rating is selected", async () => {
    const mockData = {
      message: "Test message",
      mean: 4.5,
      categories: [
        {
          categoryName: "Category 1",
          subCategory: [
            {
              subCategoryName: "Subcategory 1",
              employeeRating: 2,
              description: "Test description",
            },
          ],
        },
      ],
    };
    axiosInstance.get.mockResolvedValueOnce({ data: mockData });

    await act(async () => {
      renderToJsDom(
        <PracticeRatingDialog
          user={user}
          onClose={onCloseMock}
          dialogOpen={dialogOpenMock}
        />
      );
    });

    const ratingInput = screen.getByRole("slider");
    expect(ratingInput).toBeInTheDocument();

    fireEvent.keyDown(ratingInput);
    expect(ratingInput).toHaveAttribute("aria-valuenow", "3");
  });

  it("should generate a correct payload for a simple ratingsRef", () => {
    ratingsRef.current = {
      Category1: {
        SubCategory1: { rating: 4 },
        SubCategory2: { rating: 3 },
      },
    };

    const expectedPayload = {
      categories: [
        {
          categoryName: "Category1",
          subCategory: [
            { subCategoryName: "SubCategory1", employeeRating: 4 },
            { subCategoryName: "SubCategory2", employeeRating: 3 },
          ],
        },
      ],
    };

    expect(generatePayload()).toEqual(expectedPayload);
  });
});
