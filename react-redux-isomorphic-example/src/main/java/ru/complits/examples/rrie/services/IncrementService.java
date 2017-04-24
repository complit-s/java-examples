package ru.complits.examples.rrie.services;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.complits.examples.rrie.ReactReduxIsomorphicExample;

/**
 * Сервис инкрементирования целых, один на приложение.
 */
@ApplicationScoped
public class IncrementService {
		
	private int a = 1;
	
	private int b = 1;
	
	private final static Logger LOG = LoggerFactory.getLogger(ReactReduxIsomorphicExample.class);
	
	/**
	 * @return Текущее значения А. 
	 */	
	public int getA() {
		return a;
	}

	/**
	 * Инкрементирование значения А.
	 * @return Новое значение А.
	 */	
	public int incrementA() {
		LOG.info("Increment A - {}", a);
		
		return ++a;
	}

	/**
	 * @return Текущее значения B. 
	 */		
	public int getB() {
		return b;
	}
	
	/**
	 * Инкрементирование значения B.
	 * @return Новое значение B.
	 */		
	public int incrementB() {
		LOG.info("Increment B - {}", b);
		
		return ++b;
	}
}
