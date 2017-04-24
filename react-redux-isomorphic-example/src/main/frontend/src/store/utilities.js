export const doRequest = (loadedType, loadingErrorType, promise, dispatch, getState) => {
  const result = promise
    .then(response => {
      if (getState().incrementA.wait) {
        dispatch({type: loadedType, payload: response.body})
      }
    })
    .catch(error => {
      dispatch({type: loadingErrorType, payload: error.message || "Ошибка"})
      throw error
    })

  result.request = result
  return result
}
