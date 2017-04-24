package ru.complits.examples.rrie;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.jboss.resteasy.cdi.ResteasyCdiExtension;

/**
 * Класс с информацией об обработчиках запросов и провайдерах для JAX-RS 
 */
@ApplicationScoped
@ApplicationPath("/")
public class ReactReduxIsomorphicExampleApplication extends Application {

	/**
	 * Подключается расширение CDI для Resteasy.
	 */
	@Inject
	private ResteasyCdiExtension extension;

	/**
	 * @return Список классов обработчиков запросов и провайдеров для JAX-RS.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Set<Class<?>> getClasses() {
		final Set<Class<?>> result = new HashSet<>();

		// Из расширения CDI для Resteasy берется информация об обработчиках запросов JAX-RS.
		result.addAll((Collection<? extends Class<?>>) (Object)extension.getResources());
		// Из расширения CDI для Resteasy берется информация о провайдерах JAX-RS.    	
		result.addAll((Collection<? extends Class<?>>) (Object)extension.getProviders());
		return result;
	}
}
