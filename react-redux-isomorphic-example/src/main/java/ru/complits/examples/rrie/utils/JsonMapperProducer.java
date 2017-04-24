package ru.complits.examples.rrie.utils;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Фабрика {@link ObjectMapper} для преобразования объектов в JSON и обратно.
 * <p>Создает настроенный объект для преобразования в JSON. 
 * Его можно инжектить в разные сервисы, которым нужно преобразование объектов в JSON И обратно.</p> 
 */
@ApplicationScoped
public class JsonMapperProducer {
	
	private final ObjectMapper jsonMapper;
	
	public JsonMapperProducer() {
		jsonMapper = new ObjectMapper();
		jsonMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		jsonMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
	}
	
	@Produces
	@Dependent
	public ObjectMapper getJsonMapper() {
		return jsonMapper;	
	}
}
