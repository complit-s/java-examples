import "normalize.css"
import "milligram/src/milligram.sass"
import "../styles/index.less"

import React from "react"
import ReactDOM from "react-dom"
import {browserHistory} from "react-router"

import configureStore from "./store"
import App from "./containers/app"

// Этот модуль используется для рендеринга React на стороне клиента.

/* eslint no-empty-function: ["error", { "allow": ["arrowFunctions"] }] */

// Начальные данные для Redux.
const initialState = global.INITIAL_STATE
// Обработка истории переходов для react-router.
const history = {
  ...browserHistory,
  isActive: () => {},
  setRouteLeaveHook: () => {}
}
// Создание хранилища Redux.
const store = configureStore(initialState, history, false)
// Элемент в который нужно вставлять HTML, сформированный React.
const contentElement = document.getElementById("content")

// Выполнение рендеринга HTML с помощью React.
ReactDOM.render(<App store={store} history={history}/>, contentElement)

if (module.hot) {
  // Поддержка "горячей" перезагрузки компонентов.
  module.hot.accept("./containers/app", () => {
    const app = require("./containers/app").default

    ReactDOM.render(app({store, history}), contentElement)
  })
}
