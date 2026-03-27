import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";
import { BrowserRouter } from "react-router-dom";
import {
  HistoryAdaptedRouter,
  useUuiServices,
  DragGhost,
  UuiContext,
} from "@epam/uui-core";
import { createBrowserHistory } from "history";
import "@epam/uui-components/styles.css";
import "@epam/uui/styles.css";
import "@epam/loveship/styles.css";
import "@epam/assets/css/theme/theme_loveship_dark.css";
import "@epam/uui-docs/styles.css";
import { Provider } from "react-redux";
import store from "../src/redux/store";
import { Modals } from "@epam/uui-components";
import { Snackbar } from "@epam/uui";

import "./css/index.css";

const history = createBrowserHistory();

const AppWrapper = () => {
  const router = new HistoryAdaptedRouter(history);
  const { services } = useUuiServices({ router });
  console.log(services);

  return (
    <UuiContext.Provider value={services}>
      <Provider store={store}>
        <BrowserRouter>
          <App />
          <Modals />
        </BrowserRouter>
      </Provider>

      <Snackbar />
      <DragGhost />
    </UuiContext.Provider>
  );
};

const rootElement = document.getElementById("root");
const root = ReactDOM.createRoot(rootElement);

root.render(
  <React.StrictMode>
    <AppWrapper />
  </React.StrictMode>
);
