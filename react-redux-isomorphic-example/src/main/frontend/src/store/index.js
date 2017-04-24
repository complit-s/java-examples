import {createStore, applyMiddleware} from "redux"
import {routerMiddleware} from "react-router-redux"
import {createLogger} from "redux-logger"
import thunk from "redux-thunk"

import reducers from "./reducers"

/**
 * Создание хранилища Redux.
 * @param  {Object} initialState Начальные данные хранилища.
 * @param  {Object} history      Обработка истории переходов для react-router.
 * @param  {Boolean} serverMode  Режим хранилища (клиент, серверный рендеринг).
 * @return {Object}              Настроеное хранилище Redux.
 */
export default function configureStore(initialState, history, serverMode) {
  const middleware =
    [
      // Обработчик для react-router.
      routerMiddleware(history),
      // Обработчик thunk (https://github.com/gaearon/redux-thunk), позволяет диспатчить функции.
      thunk
    ]
    .concat(serverMode
      ? []
      // В режиме клиента подключаем логгер redux.
      : [createLogger()])
  const store = createStore(reducers, initialState, applyMiddleware(...middleware))

  if (module.hot) {
    // Поддержка "горячей" перезагрузки Redux-преобразователей.
    module.hot.accept("./reducers", () => {
      const nextRootReducer = require("./reducers")

      store.replaceReducer(nextRootReducer)
    })
  }

  return store
}
