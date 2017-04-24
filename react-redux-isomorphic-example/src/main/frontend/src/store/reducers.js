import {combineReducers} from "redux"
import {routerReducer} from "react-router-redux"

import incrementA from "./increment-a"
import incrementB from "./increment-b"

export default combineReducers({
  // Redux-преобразователь для счетчика A.
  incrementA,
  // Redux-преобразователь для счетчика B.
  incrementB,
  // Redux-преобразователь для react-router.
  routing: routerReducer
})
