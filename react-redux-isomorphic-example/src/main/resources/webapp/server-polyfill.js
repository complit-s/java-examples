// Инициализация объекта global для javascript библиотек.
var global = this;

// Инициализация объекта window для javascript библиотек, которые написаны не совсем правильно,
// они думают что всегда исполняются в браузере.
var window = this;

// Инициализация объекта ведения логов, в Nashorn нет console.
var console = {
	error: print,
	debug: print,
	warn: print,
	log: print
};

// В Nashorn нет setTimeout, выполняем callback - на сервере сразу требуется ответ.
function setTimeout(func, delay) {
	func();
	return 0;
};
function clearTimeout() {
};

// В Nashorn нет setInterval, выполняем callback - на сервере сразу требуется ответ.
function setInterval(func, delay) {
	func();
	return 0;
};
function clearInterval() {
};

// Объявление функции инициализации.
var initializeEngine = null;
// Объявление функции серверного рендеринга React.
var renderHtml = null;