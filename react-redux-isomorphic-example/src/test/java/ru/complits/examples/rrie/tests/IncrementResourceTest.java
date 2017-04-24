package ru.complits.examples.rrie.tests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.complits.examples.rrie.services.IncrementService;
import ru.complits.examples.rrie.webapi.IncrementResource;

/**
 * Тест для {@link IncrementResource}.
 * <p>
 * Тест проводится с помощью http://arquillian.org/ . Этот движок для тестов
 * поддерживает CDI.
 * </p>
 */
@RunWith(Arquillian.class)
public class IncrementResourceTest {

	@Inject
	private IncrementResource incrementResource;

	/**
	 * @return Настроенный бандл, который будет использоваться для разрешения зависимостей CDI.
	 */
	@Deployment
	public static JavaArchive createDeployment() {
		return ShrinkWrap.create(JavaArchive.class).addClass(IncrementResource.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Test
	public void getATest() {
		final Map<String, Integer> response = incrementResource.getA();

		assertNotNull(response.get("value"));
		assertEquals(Integer.valueOf(1), response.get("value"));
	}

	@Test
	public void incrementATest() {
		final Map<String, Integer> response = incrementResource.incrementA();

		assertNotNull(response.get("value"));
		assertEquals(Integer.valueOf(2), response.get("value"));
	}

	@Test
	public void getBTest() {
		final Map<String, Integer> response = incrementResource.getB();

		assertNotNull(response.get("value"));
		assertEquals(Integer.valueOf(2), response.get("value"));
	}

	@Test
	public void incrementBTest() {
		final Map<String, Integer> response = incrementResource.incrementB();

		assertNotNull(response.get("value"));
		assertEquals(Integer.valueOf(3), response.get("value"));
	}

	/**
	 * Возвращает мок для {@link IncrementService}, используется RequestScoped,
	 * Arquillian использует ее для создание отдельного объекта для каждого теста.
	 * @return Мок для {@link IncrementService}.
	 */
	@Produces
	@RequestScoped
	public IncrementService getIncrementService() {
		final IncrementService service = mock(IncrementService.class);
		when(service.getA()).thenReturn(1);
		when(service.incrementA()).thenReturn(2);
		when(service.getB()).thenReturn(2);
		when(service.incrementB()).thenReturn(3);
		return service;
	}
}
