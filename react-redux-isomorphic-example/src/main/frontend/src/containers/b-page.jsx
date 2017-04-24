import React from "react"
import PropTypes from "prop-types"
import {connect} from "react-redux"

import {loadB, doIncrementB} from "../store/increment-b"

/**
 * Страница со счетчиком B.
 * @type {Object}
 */
class PageB extends React.Component {

  static propTypes = {
    // Флаг загрузки данных счетчика.
    loading: PropTypes.bool,
    // Ошибка при загрузке/инкременте счетчика.
    error: PropTypes.node,
    // Текущее значение счетчика.
    value: PropTypes.number,
    // Метод загрузки значения счетчика.
    loadB: PropTypes.func.isRequired,
    // Инкремент счетчика.
    doIncrementB: PropTypes.func.isRequired
  }

  static defaultProps = {
    loading: false,
    error: null,
    value: null
  }

  constructor() {
    super()

    this.handleLoad = this.handleLoad.bind(this)
    this.handleIncrement = this.handleIncrement.bind(this)
  }

  componentWillMount() {
    this.handleLoad()
  }

  shouldComponentUpdate(nextProps) {
    return this.props.loading !== nextProps.loading ||
      this.props.error !== nextProps.error ||
      this.props.value !== nextProps.value
  }

  componentWillUnmount() {
    // Страница закрывается, если есть текущий запрос - останавливаем.
    if (this.currentRequest) {
      this.currentRequest.abort()
    }
  }

  currentRequest = null

  /**
   * Выполнение загрузки текущего значения счетчика.
   * @return {undefined}
   */
  handleLoad() {
    // Загружается только если счетчик еще не загружен
    // (например с помощью установки текущего значения сервером при генерации страницы).
    if (!this.props.value) {
      if (this.currentRequest) {
        // Если есть активный запрос - останавливаем.
        this.currentRequest.abort()
      }

      // Запуск загрузки значения счетчика.
      const promise = this.props.loadB()

      // Запоминаем текущий запрос.
      this.currentRequest = promise.request
      promise
        .then(() => {
          this.currentRequest = null
        })
        .catch(() => {
          this.currentRequest = null
        })
    }
  }

  /**
   * Инкремент значения счетчика.
   * @return {undefined}
   */
  handleIncrement() {
    if (this.currentRequest) {
      // Если есть активный запрос - останавливаем.
      this.currentRequest.abort()
    }

    // Запуск выполнения инкремента.
    const incrementing = this.props.doIncrementB()

    // Запоминаем текущий запрос.
    this.currentRequest = incrementing.request
    return incrementing
      .then(() => {
        this.currentRequest = null
      })
      .catch(() => {
        this.currentRequest = null
      })
  }

  render() {
    const {loading, error, value} = this.props

    return (
      <article>
        <h3>Счетчик B</h3>
        {error && <blockquote className="error">{error}</blockquote>}
        <p>Значение - <strong>{value}</strong></p>
        <p>
          <button type="button" onClick={this.handleIncrement} disabled={loading}>
            Увеличить значение
          </button>
        </p>
      </article>
    )
  }
}

export default connect(
  state => ({
    loading: state.incrementB.wait,
    error: state.incrementB.error,
    value: state.incrementB.value
  }),
  {loadB, doIncrementB}
)(PageB)
