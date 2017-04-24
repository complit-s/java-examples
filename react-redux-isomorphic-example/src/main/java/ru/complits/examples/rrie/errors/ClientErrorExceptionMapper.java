package ru.complits.examples.rrie.errors;

import javax.enterprise.context.Dependent;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import ru.complits.examples.rrie.views.ViewResult;

/**
 * Ошибка 4xx.
 */
@Provider
@Dependent
public class ClientErrorExceptionMapper implements ExceptionMapper<ClientErrorException> {
	
	@Override
	public Response toResponse(ClientErrorException exception) {
		return Response.fromResponse(exception.getResponse())
				.type(MediaType.TEXT_HTML + ";charset=utf-8")
				.entity(new ViewResult("error.html", false))
				.build();
	}
}
