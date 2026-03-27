import { legacy_createStore as createStore } from "redux";
import rootReducer from "../../redux/combinedreducer";

export function createTestStore() {
  const store = createStore(rootReducer);
  return store;
}
