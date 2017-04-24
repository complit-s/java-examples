import React from "react"
import {connect} from "react-redux"
import {Link} from "react-router"

/**
 * Страница макета.
 * @type {Object}
 */
class Layout extends React.Component {

  static propTypes = {
    // React-router location.
    location: React.PropTypes.shape({
      pathname: React.PropTypes.string.isRequired
    }).isRequired
  }

  shouldComponentUpdate(nextProps) {
    return this.props.location !== nextProps.location
  }

  render() {
    const {location: {pathname}, children} = this.props

    return (
      <main className="wrapper">
        <nav className="navigation">
          <section className="container">
            <Link className="navigation-title" to="/">Java Isomorphic React Redux</Link>
            <ul className="navigation-list float-right">
              <li className={`navigation-item${pathname.startsWith("/a") ? " active" : ""}`}>
                <Link className="navigation-link" to="/a">Счетчик А</Link>
              </li>
              <li className={`navigation-item${pathname.startsWith("/b") ? " active" : ""}`}>
                <Link className="navigation-link" to="/b">Счетчик Б</Link>
              </li>
            </ul>
          </section>
        </nav>
        <section className="container main-container">
          {children}
        </section>
      </main>
    )
  }
}

export default connect()(Layout)
