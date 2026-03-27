import practiceratingreducer from "../redux/practiceratingreducer";

describe("Practice Rating Reducer", () => {
  const initialState = { role: "" };

  it("should return the initial state", () => {
    expect(practiceratingreducer(undefined, {})).toEqual(initialState);
  });

  it("should handle SET_ROLE action", () => {
    const action = {
      type: "SET_ROLE",
      payload: { role: "Admin" },
    };
    expect(practiceratingreducer(initialState, action)).toEqual({
      role: "Admin",
    });
  });

  it("should return the current state for an unknown action", () => {
    const unknownAction = { type: "UNKNOWN_ACTION" };
    expect(practiceratingreducer(initialState, unknownAction)).toEqual(
      initialState
    );
  });
});
