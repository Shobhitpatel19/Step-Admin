import rootReducer from "../redux/combinedreducer";
import masterexcelviewreducer from "../redux/masterexcelviewreducer";
import notificationreducer from "../redux/notificationreducer";
import practiceratingreducer from "../redux/practiceratingreducer";

describe("rootReducer", () => {
  it("should return the initial state", () => {
    const initialState = rootReducer(undefined, {});
    expect(initialState).toEqual({
      masterexcel: masterexcelviewreducer(undefined, {}),
      notification: notificationreducer(undefined, {}),
      practicerating: practiceratingreducer(undefined, {}),
    });
  });

  it("should handle actions and update state correctly", () => {
    const action = { type: "TEST_ACTION", payload: "test" };

    const newState = rootReducer(undefined, action); // Pass `undefined`, not `{}`

    expect(newState).toEqual({
      masterexcel: masterexcelviewreducer(undefined, action),
      notification: notificationreducer(undefined, action),
      practicerating: practiceratingreducer(undefined, action),
    });
  });
});
