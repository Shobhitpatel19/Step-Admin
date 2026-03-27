import { combineReducers } from "redux";
import masterexcelviewreducer from "./masterexcelviewreducer";
import notificationreducer from "./notificationreducer";
import practiceratingreducer from "./practiceratingreducer";

const rootReducer = combineReducers({
  masterexcel: masterexcelviewreducer,
  notification: notificationreducer,
  practicerating: practiceratingreducer,
});

export default rootReducer;
