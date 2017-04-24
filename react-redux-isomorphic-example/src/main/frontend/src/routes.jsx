import React from "react"
import PropTypes from "prop-types"
import {Router, Route, IndexRoute} from "react-router"
import {syncHistoryWithStore} from "react-router-redux"

import Layout from "./containers/layout"
import IndexPage from "./containers/index-page"
import APage from "./containers/a-page"
import BPage from "./containers/b-page"

/**
 * Настройка роутинга для web-приложения.
 * @param  {Object} props Свойства компонента.
 * @return {Object} Настройка роутинга для web-приложения.
 */
export default function routes(props = {}) {
  let {history} = props
  const {store} = props

  if (store) {
    // Синхронизация сотояния redux-rect-router.
    history = syncHistoryWithStore(history, store)
  }

  return (
    <Router history={history}>
      <Route path="/" component={Layout}>
        <IndexRoute component={IndexPage}/>
        <Route path="a" component={APage}/>
        <Route path="b" component={BPage}/>
      </Route>
    </Router>
  )
}

routes.defaultProps = {
  store: null
}
routes.propTypes = {
  store: PropTypes.shape({}),
  history: PropTypes.shape({}).isRequired
}
