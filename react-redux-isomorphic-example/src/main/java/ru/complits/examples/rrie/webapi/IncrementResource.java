package ru.complits.examples.rrie.webapi;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.cache.NoCache;

import ru.complits.examples.rrie.services.IncrementService;

/**
 * Обработчик запросов к сервису инкрементирования целых.
 */
@NoCache
@RequestScoped
@Path("/increment")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class IncrementResource {
	
	@Inject
	private IncrementService incrementService;
	
	/**
	 * Ответ сервиса возвращается в виде имя/значение для преобразования в JSON. 
	 * @param value Значение целого, которое вернул сервис.
	 * @return Значение сервиса в виде имя/значение.
	 */
	public static Map<String, Integer> wrapResult(int value) {
		final Map<String, Integer> response = new HashMap<>();
		response.put("value", value);
		
		return response;
	}
	
	/**
	 * @return Текущее значения А. 
	 */
	@GET
	@Path("/a")
	public Map<String, Integer> getA() {
		final int currentA = incrementService.getA();
		
		return wrapResult(currentA);
	}
	
	/**
	 * Инкрементирование значения А.
	 * @return Новое значение А.
	 */
	@PUT
	@Path("/a")
	public Map<String, Integer> incrementA() {
		final int currentA = incrementService.incrementA();
		
		return wrapResult(currentA);
	}
	
	/**
	 * @return Текущее значения B. 
	 */	
	@GET
	@Path("/b")
	public Map<String, Integer> getB() {
		final int currentB = incrementService.getB();
		
		return wrapResult(currentB);
	}
	
	/**
	 * Инкрементирование значения B.
	 * @return Новое значение B.
	 */	
	@PUT
	@Path("/b")
	public Map<String, Integer> incrementB() {
		final int currentB = incrementService.incrementB();
		
		return wrapResult(currentB);
	}
}
