import request from "superagent"

/**
 * Запуск загрузки счетчика A.
 * @type {String}
 */
const A_LOADING = "example/A_LOADING"

/**
 * Счетчик A загружен.
 * @type {String}
 */
const A_LOADED = "example/A_LOADED"

/**
 * Ошибка загрузки счетчика A.
 * @type {String}
 */
const A_LOADING_ERROR = "example/A_LOADING_ERROR"

/**
 * Запуск загрузки счетчика A.
 * @return {Promise} Объект для ожидания загрузки.
 */
export const loadA = () => (dispatch, getState) => {
  if (getState().incrementA.wait) {
    // Загрузка уже идет.
    return Promise.resolve()
  }

  dispatch({type: A_LOADING})

  const promise = request.get("increment/a")
  const result = promise
    .then(response => {
      if (getState().incrementA.wait) {
        dispatch({type: A_LOADED, payload: response.body})
      }
    })
    .catch(error => {
      dispatch({type: A_LOADING_ERROR, payload: error.message || "Ошибка"})
      throw error
    })

  result.request = result
  return result
}

/**
 * Запуск инкремента счетчика A.
 * @return {Promise} Объект для ожидания результата.
 */
export const doIncrementA = () => (dispatch, getState) => {
  if (getState().incrementA.wait) {
    // Загрузка уже идет.
    return Promise.resolve()
  }

  dispatch({type: A_LOADING})

  const promise = request.put("increment/a")
  const result = promise
    .then(response => {
      if (getState().incrementA.wait) {
        dispatch({type: A_LOADED, payload: response.body})
      }
    })
    .catch(error => {
      dispatch({type: A_LOADING_ERROR, payload: error.message || "Ошибка"})
      throw error
    })


  result.request = result
  return result
}

// Redux-преобразователь для счетчика A.
export default function incrementA(state = {
  wait: false,
  value: null,
  error: null
}, action) {
  switch (action.type) {
    case A_LOADING:
      return {
        ...state,
        wait: true,
        error: null
      }
    case A_LOADED:
      return {
        ...state,
        ...action.payload,
        wait: false
      }
    case A_LOADING_ERROR:
      return {
        ...state,
        wait: false,
        error: action.payload
      }
    default:
      return state
  }
}
