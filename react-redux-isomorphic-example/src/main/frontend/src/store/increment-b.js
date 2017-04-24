import request from "superagent"

/**
 * Запуск загрузки счетчика B.
 * @type {String}
 */
const B_LOADING = "example/B_LOADING"

/**
 * Счетчик B загружен.
 * @type {String}
 */
const B_LOADED = "example/B_LOADED"

/**
 * Ошибка загрузки счетчика B.
 * @type {String}
 */
const B_LOADING_ERROR = "example/B_LOADING_ERROR"

/**
 * Запуск загрузки счетчика B.
 * @return {Promise} Объект для ожидания загрузки.
 */
export const loadB = () => (dispatch, getState) => {
  if (getState().incrementB.wait) {
    // Загрузка уже идет.
    return Promise.resolve()
  }

  dispatch({type: B_LOADING})

  const promise = request.get("increment/b")
  const result = promise
    .then(response => {
      if (getState().incrementB.wait) {
        dispatch({type: B_LOADED, payload: response.body})
      }
    })
    .catch(error => {
      dispatch({type: B_LOADING_ERROR, payload: error.message || "Ошибка"})
      throw error
    })

  result.request = result
  return result
}

/**
 * Запуск инкремента счетчика B.
 * @return {Promise} Объект для ожидания результата.
 */
export const doIncrementB = () => (dispatch, getState) => {
  if (getState().incrementB.wait) {
    // Загрузка уже идет.
    return Promise.resolve()
  }

  dispatch({type: B_LOADING})

  const promise = request.put("increment/b")
  const result = promise
    .then(response => {
      if (getState().incrementB.wait) {
        dispatch({type: B_LOADED, payload: response.body})
      }
    })
    .catch(error => {
      dispatch({type: B_LOADING_ERROR, payload: error.message || "Ошибка"})
      throw error
    })

  result.request = result
  return result
}

// Redux-преобразователь для счетчика B.
export default function incrementB(state = {
  wait: false,
  value: null,
  error: null
}, action) {
  switch (action.type) {
    case B_LOADING:
      return {
        ...state,
        wait: true,
        error: null
      }
    case B_LOADED:
      return {
        ...state,
        ...action.payload,
        wait: false
      }
    case B_LOADING_ERROR:
      return {
        ...state,
        wait: false,
        error: action.payload
      }
    default:
      return state
  }
}
