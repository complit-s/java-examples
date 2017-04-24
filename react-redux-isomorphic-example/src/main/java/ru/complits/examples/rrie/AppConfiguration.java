package ru.complits.examples.rrie;

import java.nio.charset.StandardCharsets;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.ClasspathLocationStrategy;

/**
 * Настройки приложения.
 */
@Dependent
public class AppConfiguration {

	/**
	 * Адрес, на котором web-сервер будет прослушивать входящие соединения.
	 */
	public static final String WEBSERVER_HOST = "webServer.host";

	public static final String WEBSERVER_HOST_DEFAULT = "localhost";

	/**
	 * Порт, на котором web-сервер будет прослушивать входящие соединения.
	 */
	public static final String WEBSERVER_PORT = "webServer.port";
	
	public static final int WEBSERVER_PORT_DEFAULT = 8080;

	/**
	 * Настройка кеширования статических файлов (заполняется в формате HTTP заголовка Cache-Control).
	 */
	public static final String WEBSERVER_CACHECONTROL = "webServer.staticCacheControl";
	
	public static final String WEBSERVER_CACHECONTROL_DEFAULT = "max-age=3600, must-revalidate";
	
	/**
	 * Флаг использования React на сервере для рендеринга html страниц.
	 */
	public static final String WEBSERVER_ISOMORPHIC = "webServer.isomorphic";
	
	public static final boolean WEBSERVER_ISOMORPHIC_DEFAULT = true;

	/**
	 * Минимальное количество закешированных сред исполнения javascript.
	 */
	public static final String WEBSERVER_MIN_IDLE_SCRIPT_ENGINES =  "webServer.minIdleScriptEngines";

	public static final int WEBSERVER_MIN_IDLE_SCRIPT_ENGINES_DEFAULT = 3;
	
	/**
	 * Имя файла настроек.
	 */
	public static final String CLASSPATH_CONFIG_FILENAME = "config.properties";

	/**
	 * Фабричный метод получения настроек приложения, позволяет инжектить объект для доступа к настройкам.
	 * @return Настройки приложения.
	 */
	@Produces
	@ApplicationScoped
	public Configuration getConfiguration() {
		// Настройки приложения загружаются из class path, но можно добавить загрузку из файловой системы.
		final FileBasedConfigurationBuilder<FileBasedConfiguration> classPathConfigBuilder =
			new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
				.configure(
					new Parameters()
						.fileBased()
						.setFileName(CLASSPATH_CONFIG_FILENAME)
						.setThrowExceptionOnMissing(false)
						.setEncoding(StandardCharsets.UTF_8.name())
						.setLocationStrategy(new ClasspathLocationStrategy()));
		try {
			return classPathConfigBuilder.getConfiguration();
		} catch(ConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
}
