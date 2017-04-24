package ru.complits.examples.rrie.errors;

import javax.enterprise.context.Dependent;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.complits.examples.rrie.views.ViewResult;

/**
 * Преобразование исключения в html страницу (+ логирование).
 */
@Provider
@Dependent
public class ThrowableExceptionMapper implements ExceptionMapper<Throwable> {

	private static final Logger LOG = LoggerFactory.getLogger(ThrowableExceptionMapper.class);
	
	@Override
	public Response toResponse(Throwable exception) {
		LOG.error("Web error", exception);
		
		return Response
				.status(Status.INTERNAL_SERVER_ERROR)
				.type(MediaType.TEXT_HTML + ";charset=utf-8")
				.entity(new ViewResult("error.html", false))
				.build();
	}
}
