import { legacy_createStore as createStore } from "redux";
import rootReducer from "../redux/combinedreducer";
import store from "../redux/store";

describe("Redux Store", () => {
  it("should create a store with the rootReducer", () => {
    const testStore = createStore(rootReducer);
    expect(testStore.getState()).toEqual(store.getState());
  });

  it("should use Redux DevTools if available", () => {
    const devToolsMock = jest.fn();
    window.__REDUX_DEVTOOLS_EXTENSION__ = devToolsMock;

    createStore(rootReducer, window.__REDUX_DEVTOOLS_EXTENSION__());

    expect(devToolsMock).toHaveBeenCalled();
  });

  it("should contain the expected reducers", () => {
    expect(store.getState()).toEqual(rootReducer(undefined, {}));
  });
});
