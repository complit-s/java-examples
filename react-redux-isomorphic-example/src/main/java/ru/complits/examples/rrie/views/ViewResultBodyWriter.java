package ru.complits.examples.rrie.views;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.script.AbstractScriptEngine;
import javax.script.Invocable;
import javax.script.ScriptEngineManager;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.complits.examples.rrie.AppConfiguration;
import ru.complits.examples.rrie.utils.ResourceUtilities;

/**
 * Обработка данных страницы, заполненных в {@link ViewResult} и отправка HTML. 
 * <p>
 * 	Если в конфигурации включено использование React в качестве движка для рендеринга HTML (React Isomorphic),
 *  то в шаблон страницы включается контент, сформированный с помощью движка javascript и React. 
 * </p>
 */
@Provider
@ApplicationScoped
public class ViewResultBodyWriter implements MessageBodyWriter<ViewResult> {
	
	/**
	 * Каталог в classpath для скриптов рендеринга данных страницы.
	 */
	public static final String WEBAPP_ROOT = "webapp/";

	/**
	 * Ключ в шаблоне для помещения на его места контента, полученного от React.
	 */
	public static final String HTML_CONTENT_KEY = "htmlContent";
	
	/**
	 * Ключ в шаблоне для помещения на его место JSON с начальными данными для redux.
	 */
	public static final String INITIAL_STATE_KEY = "initialState";

	@Inject
	private Configuration configuration;
	
	@Inject
	private ObjectMapper jsonMapper;	
	
	@Context
	private UriInfo uriInfo;
	
	private ObjectPool<AbstractScriptEngine> enginePool = null;
	
	private final static Logger LOG = LoggerFactory.getLogger(ViewResultBodyWriter.class);
		
	@PostConstruct
	public void initialize() {
		// Получение настроек рендеринга с помощью React.
		final boolean useIsomorphicRender = configuration.getBoolean(
        		AppConfiguration.WEBSERVER_ISOMORPHIC, AppConfiguration.WEBSERVER_ISOMORPHIC_DEFAULT);
        final int minIdleScriptEngines = configuration.getInt(
        		AppConfiguration.WEBSERVER_MIN_IDLE_SCRIPT_ENGINES, AppConfiguration.WEBSERVER_MIN_IDLE_SCRIPT_ENGINES_DEFAULT);			
		
        LOG.info("Isomorphic render: {}", useIsomorphicRender);
        
		if(useIsomorphicRender) {
			// Если будет использоваться рендеринг React на сервере, то создается пул 
			// javascript движков. Javascript однопоточный, 
			// поэтому для каждого запроса используется свой экземпляр настроенного движка javascript. 
			final GenericObjectPoolConfig config = new GenericObjectPoolConfig();
			config.setMinIdle(minIdleScriptEngines);		
			enginePool = new GenericObjectPool<AbstractScriptEngine>(new ScriptEngineFactory(), config);
		}
	}
	
	@PreDestroy
	public void destroy() {
		if(enginePool != null) {
			enginePool.close();
		}		
	}	
	
	@Override
	public boolean isWriteable(
			Class<?> type,
			Type genericType,
			Annotation[] annotations,
			MediaType mediaType) {
		 return ViewResult.class.isAssignableFrom(type);
	}

	@Override
	public long getSize(
			ViewResult t,
			Class<?> type,
			Type genericType,
			Annotation[] annotations,
			MediaType mediaType) {
		return -1;
	}

	@Override
	public void writeTo(
			ViewResult t,
			Class<?> type,
			Type genericType,
			Annotation[] annotations,
			MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream)
					throws IOException, WebApplicationException {
		final Map<String, Object> viewData = 
				new HashMap<>(t.getViewData());
		final String initialStateJson = 
				jsonMapper.writeValueAsString(t.getReduxInitialState());
		// Загрузка шаблона из classpath.
		final String templateContent = 
				ResourceUtilities.getResourceAsString(WEBAPP_ROOT + t.getTemplate());
		
		// Начальное значение для redux.
		viewData.put(INITIAL_STATE_KEY, initialStateJson);
		
		if(enginePool != null && t.getUseIsomorphic()) {
			// Используется React на сервере.
			try {
				// Из пула достается свободный движок javascript.
				final AbstractScriptEngine scriptEngine = enginePool.borrowObject();
				try {
					// URL текущего запроса, нужен react-router для определения какую страницу рендерить.
					final String uri = uriInfo.getPath() + 
							(uriInfo.getRequestUri().getQuery() != null 
								? (String) ("?" + uriInfo.getRequestUri().getQuery()) 
								: StringUtils.EMPTY);
					// Выполнение серверного рендеринга React.
					final String htmlContent = 
							(String)((Invocable)scriptEngine).invokeFunction(
									"renderHtml", uri, initialStateJson);
				
					// Возврат освободившегося движка в пул.
					enginePool.returnObject(scriptEngine);
					
					viewData.put(HTML_CONTENT_KEY, htmlContent);
				} catch (Throwable e) {
					enginePool.invalidateObject(scriptEngine);
					
					throw e;
				}
			} catch (Exception e) {
				throw new WebApplicationException(e);
			}
		} else {
			viewData.put(HTML_CONTENT_KEY, StringUtils.EMPTY);
		}		
		
		// Наполнение шаблона данными.
		final String pageContent = 
				StrSubstitutor.replace(templateContent, viewData);
		entityStream.write(pageContent.getBytes(StandardCharsets.UTF_8));
	}
	
	/**
	 * Фабрика для создания и настройки движка javascript.
	 */
	private static class ScriptEngineFactory extends BasePooledObjectFactory<AbstractScriptEngine> {

		@Override
		public AbstractScriptEngine create() 
				throws Exception {
			LOG.info("Create new script engine");
			
			// Используем nashorn в качестве javascript движка.
			final AbstractScriptEngine scriptEngine = 
					(AbstractScriptEngine) new ScriptEngineManager().getEngineByName("nashorn");
			try(final InputStreamReader polyfillReader = 
					ResourceUtilities.getResourceTextReader(WEBAPP_ROOT + "server-polyfill.js");
				final InputStreamReader serverReader = 
					ResourceUtilities.getResourceTextReader(WEBAPP_ROOT + "static/assets/server.js")) {
				// Исполнение скрипта с некоторыми функциями, которых нет в nashorn, потому что он не исполняется в браузере.
				scriptEngine.eval(polyfillReader);
				// Регистрация функции, которая будет рендерить HTML на сервере с помощью React.
				scriptEngine.eval(serverReader);
			}
			
			// Запуск функции инициализации.
			((Invocable)scriptEngine).invokeFunction(
					"initializeEngine", ResourceUtilities.class.getName());

			return scriptEngine;
		}

		@Override
		public PooledObject<AbstractScriptEngine> wrap(AbstractScriptEngine obj) {
			return new DefaultPooledObject<AbstractScriptEngine>(obj);
		}
	}	
}
