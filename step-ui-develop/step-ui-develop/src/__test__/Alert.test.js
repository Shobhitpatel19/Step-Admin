import React from "react";
import { render, screen } from "@testing-library/react";
import { Provider } from "react-redux";
import { UuiContext } from "@epam/uui-core";
import configureStore from "redux-mock-store";
import Alert from "../components/common/Alert";
import { cancelNotification } from "../redux/actions";

jest.mock("../redux/actions", () => ({
  cancelNotification: jest.fn(() => ({ type: "CANCEL" })),
}));
const mockStore = configureStore([]);

describe("Alert Component", () => {
  let store;

  beforeEach(() => {
    store = mockStore({
      notification: {
        notifyStatus: true,
        notifyMessage: "Dismiss this alert",
        isSuccess: true,
      },
    });
    store.dispatch = jest.fn();
    jest.useFakeTimers();
  });

  afterEach(() => {
    jest.clearAllMocks();
    jest.useRealTimers();
  });

  const renderWithProviders = (ui, store) => {
    const mockServices = {
      router: {
        goTo: jest.fn(),
      },
    };

    return render(
      <UuiContext.Provider value={mockServices}>
        <Provider store={store}>{ui}</Provider>
      </UuiContext.Provider>
    );
  };

  it("should not display anything when notifyStatus is false", () => {
    renderWithProviders(<Alert />, store);
    expect(screen.queryByText("Test notification")).toBeNull();
  });

  it("should display a SuccessAlert when notifyStatus is true and isSuccess is true", () => {
    store = mockStore({
      notification: {
        notifyStatus: true,
        notifyMessage: "Success message",
        isSuccess: true,
      },
    });

    renderWithProviders(<Alert />, store);
    expect(screen.getByText("Success message")).toBeInTheDocument();
  });

  it("should display an ErrorAlert when notifyStatus is true and isSuccess is false", () => {
    store = mockStore({
      notification: {
        notifyStatus: true,
        notifyMessage: "Error message",
        isSuccess: false,
      },
    });

    renderWithProviders(<Alert />, store);
    expect(screen.getByText("Error message")).toBeInTheDocument();
  });

  it("should dispatch cancelNotification after 2.5 seconds", () => {
    renderWithProviders(<Alert message="Test alert" success={true} />, store);
    expect(store.dispatch).not.toHaveBeenCalled();
    jest.advanceTimersByTime(2500);
    expect(store.dispatch).toHaveBeenCalledWith(cancelNotification());
  });
});
