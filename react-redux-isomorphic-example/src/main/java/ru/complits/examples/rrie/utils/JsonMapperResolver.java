package ru.complits.examples.rrie.utils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Позволяет использовать {@link ObjectMapper}
 * настроенный в {@link JsonMapperProducer} в JAX-RS для получения JSON из объектов.
 */
@Provider
@ApplicationScoped
public class JsonMapperResolver implements ContextResolver<ObjectMapper> {

	@Inject
	private ObjectMapper jsonMapper;
	
	@Override
	public ObjectMapper getContext(Class<?> type) {
		return jsonMapper;
	}
}	