import React from "react"
import {createMemoryHistory, match, RouterContext} from "react-router"
import ReactDOMServer from "react-dom/server"
import {Provider} from "react-redux"
import {AppContainer} from "react-hot-loader"

import routes from "./routes"
import configureStore from "./store"

// Этот модуль используется для рендеринга React на стороне сервера.

/* eslint no-unused-vars: ["error", { "vars": "local" }] */

/* global initializeEngine */
/* globals initializeEngine:true */

/**
 * Инициализация серверного рендеринга.
 * @return {undefined}
 */
initializeEngine = function initializeEngine() {
  // Пока ничего инициализировать не нужно.
}

/* global renderHtml */
/* globals renderHtml:true */

/**
 * Выполнение рендеринга HTML с помощью React.
 * @param  {String} url              URL ткущего запроса.
 * @param  {String} initialStateJson Начальное состояние для Redux в сиде строки с JSON.
 * @return {String}                  HTML, сформированный React.
 */
renderHtml = function renderHtml(url, initialStateJson) {
  // Парсинг JSON начального состояния для Redux.
  const initialState = JSON.parse(initialStateJson)
  // Обработка истории переходов для react-router (обработка проиходит в памяти).
  const history = createMemoryHistory()
  // Создание хранилища Redux на основе текущего состояния, переданного в функцию.
  const store = configureStore(initialState, history, true)
  // Объект для записи в него результат рендеринга.
  const htmlContent = {}

  global.INITIAL_STATE = initialState

  // Эмуляция перехода на страницу с заданным URL с помощью react-router.
  match({
    routes: routes({history}),
    location: url
  }, (error, redirectLocation, renderProps) => {
    if (error) {
      throw error
    }

    // Рендеринг HTML текущей страницы с помощью React.
    htmlContent.result = ReactDOMServer.renderToString(
      <AppContainer>
        <Provider store={store}>
          <RouterContext {...renderProps}/>
        </Provider>
      </AppContainer>
    )
  })

  return htmlContent.result
}
