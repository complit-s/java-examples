package ru.complits.examples.rrie.webresources;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.cache.NoCache;

import ru.complits.examples.rrie.services.IncrementService;
import ru.complits.examples.rrie.views.ViewResult;
import ru.complits.examples.rrie.webapi.IncrementResource;

/**
 * Обработчик запросов к web страницам.
 */
@NoCache
@Path("/")
@RequestScoped
@Produces(MediaType.TEXT_HTML + ";charset=utf-8")
public class RootResource {

	/**
	 * Подключение зависимости {@link IncrementService}.
	 */
	@Inject
	private IncrementService incrementService;
	
	/**
	 * Получение стартовой страницы.
	 * @return Данные для отображения стартовой страницы.
	 */
	@GET
	@Path("/")
	public ViewResult getIndex() {
		return new ViewResult("index.html");
	}	

	/**
	 * @return Данные для отображения страницы со счетчиком А.
	 */	
	@GET
	@Path("/a")
	public ViewResult getAPage() 
			throws Throwable {
		final int currentA = incrementService.getA();
		
		return new ViewResult("index.html")
				// Установка начальных данных страницы для redux.
				.addInitialState("incrementA", IncrementResource.wrapResult(currentA));
	}	

	/**
	 * @return Данные для отображения страницы со счетчиком B.
	 */	
	@GET
	@Path("/b")
	public ViewResult getBPage() 
			throws Throwable {
		final int currentB = incrementService.getB();
		
		return new ViewResult("index.html")
				// Установка начальных данных страницы для redux.
				.addInitialState("incrementB", IncrementResource.wrapResult(currentB));
	}

	/**
	 * @return Ошибка.
	 */	
	@GET
	@Path("/error")
	public void error() 
			throws Throwable {
		throw new ClientErrorException(Response.Status.BAD_REQUEST);
	}
}
