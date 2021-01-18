package com.zeusas.dp.exhibit.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public final class AppContext implements ApplicationContextAware {

	private static ApplicationContext ctx;

	@Override
	public void setApplicationContext(ApplicationContext aware) throws BeansException {
		if (ctx == null) {
			synchronized (this) {
				ctx = aware;
			}
		}
	}

	public static <T> T getBean(Class<T> cls) {
		return ctx.getBean(cls);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String beanName, Class<T> cls) {
		return (T) ctx.getBean(beanName);
	}

	public static Object getBean(String beanName) {
		return ctx.getBean(beanName);
	}

	public static ApplicationContext getApplicationContext() {
		return ctx;
	}
}
