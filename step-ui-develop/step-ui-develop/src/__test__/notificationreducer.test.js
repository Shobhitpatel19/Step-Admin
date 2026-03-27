import notificationreducer from "../redux/notificationreducer";

describe("Notification Reducer", () => {
  const initialState = {
    notifyStatus: null,
    notifyMessage: null,
    isSuccess: null,
  };

  it("should return the initial state", () => {
    expect(notificationreducer(undefined, {})).toEqual(initialState);
  });

  it("should handle NOTIFY action", () => {
    const action = {
      type: "NOTIFY",
      payload: { notifyMessage: "Test Notification", isSuccess: true },
    };
    expect(notificationreducer(initialState, action)).toEqual({
      notifyStatus: true,
      notifyMessage: "Test Notification",
      isSuccess: true,
    });
  });

  it("should handle CANCEL action", () => {
    const currentState = {
      notifyStatus: true,
      notifyMessage: "Some message",
      isSuccess: false,
    };
    const action = { type: "CANCEL" };
    expect(notificationreducer(currentState, action)).toEqual({
      notifyStatus: false,
      notifyMessage: "Some message",
      isSuccess: false,
    });
  });

  it("should handle RESET_NOTIFY action", () => {
    const currentState = {
      notifyStatus: true,
      notifyMessage: "Some message",
      isSuccess: true,
    };
    const action = { type: "RESET_NOTIFY" };
    expect(notificationreducer(currentState, action)).toEqual({
      notifyStatus: false,
      notifyMessage: "Some message",
      isSuccess: true,
    });
  });

  it("should return the current state for an unknown action", () => {
    const unknownAction = { type: "UNKNOWN_ACTION" };
    expect(notificationreducer(initialState, unknownAction)).toEqual(
      initialState
    );
  });
});
