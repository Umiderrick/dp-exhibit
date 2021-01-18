package com.zeusas.dp.exhibit.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clone a bean.
 *
 */
public abstract class BeanClone {
	private static Logger logger = LoggerFactory.getLogger(BeanClone.class);

	/**
	 * 克隆所有字段。
	 * 
	 * @param source
	 * @param target
	 * @return 克隆对象。
	 */
	@SuppressWarnings("unchecked")
	static <T> T dupAll(T source, T target) {
		if (target == null //
				&& (source instanceof Cloneable)) {
			return (T)clone(source);
		}
		Map<String, Field> ff = BeanContextFactory.getFields(source.getClass());
		for (Field f : ff.values()) {
			try {
				Object obj = f.get(source);
				if (obj != null) {
					QBeanUtil.setQField(target, f, obj);
				}
			} catch (Exception e) {
				logger.warn("{}", e.getMessage());
			}
		}
		return target;
	}

	public static Object clone(Object src) {
		if (src instanceof Cloneable) {
			Class<?> clazz = src.getClass();
			Method m;
			try {
				m = clazz.getMethod("clone", (Class[]) null);
				return m.invoke(src, (Object[])null);
			} catch (Exception ex) {
				// NOP
			}
		}
		// XXX:是否会递归调用？
		return dup(src, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> T dup(T source, T target, String... attrs) {
		// 同类对象复制
		Class<?> cls = source.getClass();
		
		if (target == null) {
			target = (T) QBeanUtil.newInstance(cls);
		}

		if (attrs.length == 0) {
			return dupAll(source, target);
		}

		Map<String, Field> beanff = BeanContextFactory.getFiledsA(cls);

		// CachedBeans
		for (String attr : attrs) {
			Field f = beanff.get(attr);
			if (f == null) {
				continue;
			}
			Object val;
			try {
				val = f.get(source);
				QBeanUtil.setField(target, f, val);
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		return target;
	}

	/**
	 * 克隆一个类，并把指定的属性去除。
	 * 
	 * @param source
	 * @param target
	 * @param attrs
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T dupEx(T source, T target, String... attrs) {
		// 同类对象复制
		if (attrs.length == 0) {
			return dupAll(source, target);
		}

		Class<T> cls = (Class<T>) source.getClass();
		if (target == null) {
			target = QBeanUtil.newInstance(cls);
		}

		QBeanUtil.toUpperCase(attrs);
		Set<String> attrSet = new HashSet<>(Arrays.asList(attrs));

		Map<String, Field> beanFields = BeanContextFactory.getFiledsA(cls);
		for (Entry<String, Field> e : beanFields.entrySet()) {
			// 如果包含的话，SKIP
			if (attrSet.contains(e.getKey())) {
				continue;
			}
			Field f = e.getValue();
			Object val;
			try {
				val = f.get(source);
				if (val == null) {
					continue;
				}
				QBeanUtil.setField(target, f, val);
			} catch (Exception e1) {
				logger.warn("", e1);
			}
		}
		return target;
	}
}
