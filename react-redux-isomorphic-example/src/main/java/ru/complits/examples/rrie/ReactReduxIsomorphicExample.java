package ru.complits.examples.rrie;

import java.util.concurrent.CountDownLatch;

import org.apache.commons.configuration2.Configuration;
import org.jboss.resteasy.cdi.CdiInjectorFactory;
import org.jboss.resteasy.plugins.server.netty.cdi.CdiNettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Стартовый класс приложения.
 */
public class ReactReduxIsomorphicExample {
	
	private final static Logger LOG = LoggerFactory.getLogger(ReactReduxIsomorphicExample.class);
	
	public static void main(String[] args) {
		// Лог JUL переводится на логирование в SLF4J.
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
		
		LOG.info("Start application");
		
		// Создание CDI контейнера http://weld.cdi-spec.org/
		final Weld weld = new Weld();
		weld.property(Weld.SHUTDOWN_HOOK_SYSTEM_PROPERTY, false);	// Завершаем сами.
		final WeldContainer container = weld.initialize();
		
		// Получение настроек приложения.
		final Configuration configuration = 
				container.select(Configuration.class).get();
		
		// Создание Netty сервера для обслуживания запросов через JAX-RS, который работает с CDI контейнером.
		// Для JAX-RS используется библиотека Resteasy http://resteasy.jboss.org/
		final CdiNettyJaxrsServer nettyServer = new CdiNettyJaxrsServer();
		
		// Настройка Netty (адрес и порт).
		final String host = configuration.getString(
				AppConfiguration.WEBSERVER_HOST, AppConfiguration.WEBSERVER_HOST_DEFAULT);
		nettyServer.setHostname(host);
		final int port = configuration.getInt(
				AppConfiguration.WEBSERVER_PORT, AppConfiguration.WEBSERVER_PORT_DEFAULT);
		nettyServer.setPort(port);
		
		// Настройка JAX-RS.
		
		final ResteasyDeployment deployment = nettyServer.getDeployment();
		// Регистрации фабрики классов для JAX-RS (обработчики запросов и провайдеры).
		deployment.setInjectorFactoryClass(CdiInjectorFactory.class.getName());
		// Регистрация класса, который нужен JAX-RS для получения информации об обработчиках запросов и провайдеров.
	    deployment.setApplicationClass(ReactReduxIsomorphicExampleApplication.class.getName());
		
	    // Запуск web сервера.
		nettyServer.start();
		
	    LOG.info("Server on http://{}:{}", host, port);
		
		// Ожидание сигнала TERM для корректного завершения.
		try {
			final CountDownLatch shutdownSignal = new CountDownLatch(1);
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				shutdownSignal.countDown();
			}));
	
			try {
				shutdownSignal.await();
			} catch (InterruptedException e) {
			}	
		} finally {
			// Останов сервера и CDI контейнера.
			nettyServer.stop();
			container.shutdown();
			
			LOG.info("Application shutdown");
			
			SLF4JBridgeHandler.uninstall();
		}
	}
}
