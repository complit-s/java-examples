package ru.complits.examples.rrie.webresources;

import java.io.InputStream;
import java.net.URLConnection;
import java.util.Date;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import ru.complits.examples.rrie.AppConfiguration;
import ru.complits.examples.rrie.utils.ResourceUtilities;

/**
 * Обработчик запросов к статическим файлам.
 * <p>Запросом статического файла считается любой запрос вида {filename}.{ext}</p>
 */
@Path("/")
@RequestScoped
public class StaticFilesResource {
	
	private final static Date START_DATE = DateUtils.setMilliseconds(new Date(), 0);
	
	@Inject
	private Configuration configuration;

	/**
	 * Обработчик запросов к статическим файлам. Файлы отдаются из classpath.
	 * @param fileName Имя файла с путем.
	 * @param ext Расширение файла.
	 * @param uriInfo URL запроса, получается из контекста запроса.
	 * @param request Данные текущего запроса.
	 * @return Ответ с контентом запрошенного файла или ошибкой 404 - не найдено.
	 * @throws Exception Ошибка выполнения запроса.
	 */
	@GET
	@Path("{fileName:.*}.{ext}")
	public Response getAsset(
			@PathParam("fileName") String fileName, 
			@PathParam("ext") String ext,
			@Context UriInfo uriInfo,
			@Context Request request) 
					throws Exception {
		if(StringUtils.contains(fileName, "nomin") || StringUtils.contains(fileName, "server")) {
			// Не минифицированные версии не возвращаем.
			return Response.status(Response.Status.NOT_FOUND)
					.build();			
		}
		
		// Проверка ifModifiedSince запроса. Поскольку файлы отдаются из classpath, 
		// то временем изменения файла считаем запуск приложения. 
		final ResponseBuilder builder = 
				request.evaluatePreconditions(START_DATE);
		if (builder != null) {
			// Файл не изменился.
			return builder.build();
		}
		
		// Полный путь к файлу в classpath.
		final String fileFullName = 
				"webapp/static/" + fileName + "." + ext;
		// Контент файла.
		final InputStream resourceStream = 
				ResourceUtilities.getResourceStream(fileFullName);
		if(resourceStream != null) {		
			// Файл есть, получаем настройки кеширования на клиенте.
	        final String cacheControl = configuration.getString(
	        		AppConfiguration.WEBSERVER_HOST, AppConfiguration.WEBSERVER_HOST_DEFAULT);
	        // Отправляем ответ с контентом файла.
			return Response.ok(resourceStream)
					.type(URLConnection.guessContentTypeFromName(fileFullName))
					.cacheControl(CacheControl.valueOf(cacheControl))
					.lastModified(START_DATE)
					.build();
		}

		// Файл не найден.
		return Response.status(Response.Status.NOT_FOUND)
				.build();
	}
}
