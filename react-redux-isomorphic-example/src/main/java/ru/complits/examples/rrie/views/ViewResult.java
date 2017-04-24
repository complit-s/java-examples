package ru.complits.examples.rrie.views;

import java.util.HashMap;
import java.util.Map;

/**
 * Данные для отображения web-страницы.
 */
public class ViewResult {
	
	private final String template;
	
	private final boolean useIsomorphic;
	
	private final Map<String, Object> viewData = new HashMap<>();
	
	private final Map<String, Object> reduxInitialState = new HashMap<>();

	/**
	 * Конструктор.
	 * @param template Имя шаблона HTML страницы.
	 */
	public ViewResult(String template) {
		this.template = template;
		this.useIsomorphic = true;
	}
	
	/**
	 * Конструктор.
	 * @param template Имя шаблона HTML страницы.
	 * @param useIsomorphic true - использовать рендеринг с помощью React.
	 */
	public ViewResult(String template, boolean useIsomorphic) {
		this.template = template;
		this.useIsomorphic = useIsomorphic;
	}	
	
	/**
	 * @return Имя шаблона HTML страницы.
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * @return Данные для отображения на HTML странице.
	 */
	public Map<String, Object> getViewData() {
		return viewData;
	}

	/**
	 * @return Начальные данные для redux (помещаются на страницу в виде JSON).
	 */
	public Map<String, Object> getReduxInitialState() {
		return reduxInitialState;
	}
	
	/**
	 * @return true - использовать рендеринг с помощью React.
	 */
	public boolean getUseIsomorphic() {
		return useIsomorphic;
	}
	
	/**
	 * Добавление данных для отображения на странице.
	 * @param key Ключ данных (используется на странице как ${key}).
	 * @param value Значение, помещаемое на страницу. 
	 * @return this для методов-цепочек.
	 */
	public ViewResult add(String key, Object value) {
		viewData.put(key, value);
		
		return this;
	}

	/**
	 * Добавление начальных данных для redux.
	 * @param key Ключ данных.
	 * @param value Значение для redux.
	 * @return this для методов-цепочек.
	 */
	public ViewResult addInitialState(String key, Object value) {
		reduxInitialState.put(key, value);
		
		return this;
	}
}
