package ru.complits.examples.rrie.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

/**
 * Вспомогательные утилиты для работу с ресурсами в class path.
 */
public final class ResourceUtilities {
	
	/**
	 * @param fileName Имя файла в class path.
	 * @return Данные файла в class path в виде {@link InputStream}.
	 */
	public static InputStream getResourceStream(String fileName) {
		final ClassLoader contextClassLoader = 
				Thread.currentThread().getContextClassLoader();
		if(contextClassLoader != null) {
			return contextClassLoader.getResourceAsStream(fileName);
		} else {
			return ClassLoader.getSystemResourceAsStream(fileName);
		}
	}

	/**
	 * @param fileName Имя файла в class path.
	 * @return Данные файла в class path в виде строки (UTF-8).
	 */
	public static String getResourceAsString(String fileName) 
			throws IOException {
		try(InputStream stream = getResourceStream(fileName)) {
			final String result = new String(
					IOUtils.toByteArray(stream), StandardCharsets.UTF_8);
			return result;
		}
	}	
	
	/**
	 * @param fileName Имя файла в class path.
	 * @return Данные файла в class path в виде {@link InputStreamReader} (UTF-8).
	 */
	public static InputStreamReader getResourceTextReader(String fileName) 
			throws IOException {
		final InputStream stream = getResourceStream(fileName);
		return new InputStreamReader(stream, StandardCharsets.UTF_8);
	}
}
