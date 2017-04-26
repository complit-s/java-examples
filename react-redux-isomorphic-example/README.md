#КОМПЛИТ-С

https://complit-s.ru/

## Java React Redux Isomorphic

Приложение, демонстрирующее серверный рендеринг React с бэкендом на Java.

### Ключевые моменты:

* Backend на Java в стиле микросервиса на основе Netty и JAX-RS (в реализации Resteasy) для обработки web-запросов, с возможностью запуска в docker.
* Dependency Injection с использованием библиотеки CDI (в реализации Weld SE).
* Сборка javascript бандла с помощью webpack 2.
* Настройка редеринга HTML на сервере с помощью React.
* Запуск отладки  с поддержкой "горячей" перезагрузки страниц и стилей с использованием  webpack dev server.

### Команды примера, которые используются для разработки и сборки приложения:

Разработка клиентской части приложения с "горячей" перезагрузкой (приложение доступно по адресу http://localhost:8081/ это порт web pack dev server): запустить backend приложения либо с помощью команды запуска приложения либо в IDE (если приложение запускается в IDE, желательно включить maven профиль frontendDevelopment), перейти в каталог src/main/frontend и запустить web pack dev server командой:

	npm run debug

Построение production версии приложения (минифицированный клиенский бандл скриптов и стилей, настрока кеширования статики на клиенте, привязка к адресу 0.0.0.0):
	
	mvn clean package -P production

Запуск приложения на исполнение (приложение доступно по адресу http://0.0.0.0:8080/):

	java -jar target/java-react-redux-isomorphic-example-0.0.1-SNAPSHOT.jar

Построение docker image (используется плагин к maven: docker-maven-plugin)
	
	mvn clean package docker:build -P production

Запуск docker container (приложение доступно по адресу http://localhost:8080/):
	
	docker run -d -p 8080:8080 java-react-redux-isomorphic-example
