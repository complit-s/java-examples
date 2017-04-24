/* eslint no-process-env: "off" */

const Path = require("path")
const Webpack = require("webpack")
const ExtractTextPlugin = require("extract-text-webpack-plugin")
const UnminifiedWebpackPlugin = require("unminified-webpack-plugin")
const autoprefixer = require("autoprefixer")

// Номера портов по-умолчанию.
const APP_PORT_DEFAULT = 8080
const PROXY_PORT_DEFAULT = 8081

// Флаг использования "горячей" перезагрузки UI.
const isHot = process.env.DEBUG === "true"
// Флаг включения минификации скриптов.
const isProduction = process.argv.indexOf("-p") !== -1 ||
  process.argv.indexOf("--optimize-minimize") !== -1

// Порт приложения.
const appPort = process.env.APP_PORT || APP_PORT_DEFAULT

// Порт прокси сервера webpack для разработки.
const proxyPort = process.env.PROXY_PORT || PROXY_PORT_DEFAULT

// Общая настройка сборки стилей (autoprefixer, source maps).
const cssStyles = [
  "css-loader?sourceMap&sourceComments&?-autoprefixer&&importLoaders=1", {
    loader: "postcss-loader",
    options: {
      // Подключение формирования префиксов для разных браузеров.
      plugins: [autoprefixer({browserslist: ["> 1%", "last 2 versions"]})]
    }
  }
]

// Сборка LESS стилей.
const lessStyles = cssStyles.concat(["less-loader?sourceMap&sourceComments"])

// Сборка SASS стилей.
const sassStyles = cssStyles.concat(["sass-loader?sourceMap&sourceComments"])

module.exports = {

  devtool: isHot
    // Инструменты отладки при "горячей" перезагрузке.
    ? "module-source-map" // "module-eval-source-map"
    // Инструменты отладки в production.
    : "source-map",

  // Настройки сервера бандлов для разработки.
  devServer: {
    // Горячая перезагрузка.
    hot: true,
    // Порт сервера.
    port: proxyPort,
    // Сервер бандлов работает как прокси к основному приложения.
    proxy: {
      "*": `http://localhost:${appPort}`
    }
  },

  entry: {
    // Бандл для клиентского скрипта.
    main: ["es6-promise", "babel-polyfill"]
      .concat(isHot
        // Если используется "горячая" перезагрузка - требуется медиатор.
        ? ["react-hot-loader/patch"]
        // Стартовый скрипт клиентского скрипта.
        : [])
      .concat(["./src/main.jsx"]),
    // Бандл для рендеринга на стороне сервера.
    [isProduction ? "server.min" : "server"]:
      ["es6-promise", "babel-polyfill", "./src/server.jsx"]
  },

  output: {
    // Путь для бандлов.
    path: Path.join(__dirname, "../resources/webapp/static/assets/"),
    publicPath: isHot
      // Сервер разработчика с "горячей" перезагрузкой (требуется задавать полный путь).
      ? `http://localhost:${proxyPort}/assets/`
      : "/assets/",
    filename: "[name].js",
    chunkFilename: "[name].js"
  },

  resolve: {
    // Расширения файлов скриптов.
    extensions: [
      ".js", ".jsx"
    ],
    // Каталоги с исходным кодом, откуда требуется подключать модули.
    modules: ["src", "node_modules"]
  },

  module: {
    rules: [
      {
        // Загрузчик JavaScript (Babel).
        test: /\.(js|jsx)?$/,
        exclude: /(node_modules)/,
        use: [
          {
            loader: isHot
              // Для "гарячей" перезагрузки требуется настроить babel.
              ? "babel-loader?plugins[]=syntax-dynamic-import,plugins[]=react-hot-loader/babel"
              : "babel-loader"
          }
        ]
      }, {
        // Загрузчик стилей CSS.
        test: /\.css$/,
        use: isHot
        // При использовании "горячей" перезагрузки стили помещаются в бандл с JavaScript кодом.
          ? ["style-loader"].concat(cssStyles)
          // В production - стили это отдельный бандл.
          : ExtractTextPlugin.extract({use: cssStyles, publicPath: "../assets/"})
      }, {
        // Загрузчик стилей LESS.
        test: /\.less$/,
        use: isHot
        // При использовании "горячей" перезагрузки стили помещаются в бандл с JavaScript кодом.
          ? ["style-loader"].concat(lessStyles)
          // В production - стили это отдельный бандл.
          : ExtractTextPlugin.extract({use: lessStyles, publicPath: "../assets/"})
      }, {
        // Загрузчик стилей SASS.
        test: /\.(sass|scss)?$/,
        use: isHot
        // При использовании "горячей" перезагрузки стили помещаются в бандл с JavaScript кодом.
          ? ["style-loader"].concat(sassStyles)
          // В production - стили это отдельный бандл.
          : ExtractTextPlugin.extract({use: sassStyles, publicPath: "../assets/"})
      }, {
        // Загрузчик изображений.
        test: /\.(png|jpg|gif)$/,
        loader: "file-loader",
        options: {
          name: "img/[name].[ext]"
        }
      }, {
        // Загрузчик шрифтов.
        test: /\.(ttf|eot|woff|woff2|svg)$/,
        loader: "file-loader",
        options: {
          name: "fonts/[name].[ext]"
        }
      }
    ]
  },

  plugins: [
    // В production - стили это отдельный бандл.
    new ExtractTextPlugin("main.css")
    // Выделение общих частей между серверным и клиентским бандлом в отдельный бандл.
  ].concat(isHot
    // При использовании "горячей" перезагрузки требуется подключение плагина включающего именованные модули.
    ? [new Webpack.NamedModulesPlugin()]
    : []
  ).concat(isProduction
    // В продакшене включена минификация, но нужна неминифицированная версия серверного скрипта,
    // минифицированный серверный скрипт почему-то не переваривается Java.
    ? [new UnminifiedWebpackPlugin()]
    : []
  )
}
