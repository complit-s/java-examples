import React from "react"
import PropTypes from "prop-types"
import {Provider} from "react-redux"
import {AppContainer} from "react-hot-loader"

import Routes from "../routes"

/**
 * Стартовый компонент приложения (клиентская часть).
 * @param {Object} store   Redux хранилище.
 * @param {Object} history Объект - история переходов для react-router.
 * @returns {Object} Стартовый компонент приложения.
 */
export default function App({store, history}) {
  return (
    <AppContainer>
      <Provider store={store}>
        <Routes store={store} history={history}/>
      </Provider>
    </AppContainer>
  )
}

App.propTypes = {
  store: PropTypes.shape({}).isRequired,
  history: PropTypes.shape({}).isRequired
}
