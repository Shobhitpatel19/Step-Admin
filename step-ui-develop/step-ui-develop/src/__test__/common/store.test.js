import { createTestStore } from "./store";
import rootReducer from "../../redux/combinedreducer";
import { legacy_createStore as createStore } from "redux";

jest.mock("redux", () => ({
  legacy_createStore: jest.fn(),
}));

jest.mock("../../redux/combinedreducer", () => jest.fn());

describe("createTestStore", () => {
  let mockStore;

  beforeEach(() => {
    mockStore = {
      getState: jest.fn(),
      dispatch: jest.fn(),
      subscribe: jest.fn(),
    };
    createStore.mockReturnValue(mockStore);
  });

  test("should create a Redux store using rootReducer", () => {
    const store = createTestStore();

    expect(createStore).toHaveBeenCalledWith(rootReducer);
    expect(store).toBe(mockStore);
  });
});
