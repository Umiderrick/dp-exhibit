package com.zeusas.dp.exhibit.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class BeanMap {

	private final static Logger logger = LoggerFactory.getLogger(BeanMap.class);

	private BeanMap() {
	}

	/**
	 * 将一Map封装的对象，转为一个Bean对象。
	 * 
	 * @param values
	 * @param type
	 * @return
	 */
	public static <T> T mapToBean(Map<String, Object> values, Class<T> type) {
		Map<String, PropertyDescriptor> descs = BeanContextFactory.getDescriptorsA(type);
		T retValue = null;
		try {
			retValue = (T) type.newInstance();
			for (Entry<String, Object> e : values.entrySet()) {
				Object obj = e.getValue();

				if (obj == null) {
					continue;
				}
				PropertyDescriptor d = descs.get(e.getKey());
				if (d == null) {
					continue;
				}
				Method setter = d.getWriteMethod();
				if (setter == null) {
					continue;
				}
				QBeanUtil.setProperty(setter, retValue, obj);
			}
		} catch (Exception e) {
			logger.error("{}", e.getMessage());
		}
		return retValue;
	}

	/**
	 * 将一个RequestParameters对象对应的参数转为Bean.
	 * 
	 * @param params
	 * @param type
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T pojoBean(Map<String, String[]> params, Class<T> type) {
		T target = null;
		Map<String, PropertyDescriptor> descs = BeanContextFactory.getDescriptors(type);

		try {
			target = (T) type.newInstance();
			for (Map.Entry<String, String[]> e : params.entrySet()) {
				PropertyDescriptor pd = descs.get(e.getKey());
				Method mset;
				if (pd == null || (mset = pd.getWriteMethod()) == null) {
					continue;
				}
				String[] vv = e.getValue();
				if (vv == null || vv.length == 0) {
					continue;
				}
				Class<?> pClass = mset.getParameterTypes()[0];
				if (Collection.class.isAssignableFrom(pClass)) {
					Type mtype = mset.getGenericParameterTypes()[0];
					Type vType = null;
					if (mtype instanceof ParameterizedType) {
						vType = ((ParameterizedType) mtype).getActualTypeArguments()[0];
					}

					if (vType == null || vType.equals(Object.class)) {
						QBeanUtil.setProperty(mset, target, Arrays.asList(vv));
						continue;
					}

					Collection args;
					if (Set.class.isAssignableFrom(pClass)) {
						args = new HashSet();
					} else {
						args = new ArrayList();
					}
					for (int i = 0; i < vv.length; i++) {
						args.add(TypeConverter.toType(vv[i], (Class<Object>) vType));
					}
					QBeanUtil.setProperty(mset, target, args);
				} else {
					QBeanUtil.setProperty(mset, target, vv[0]);
				}
			}
		} catch (Exception e) {
			logger.error("{}", e.getMessage());
		}
		return target;
	}

	public static Map<String, Object> beanToMapEx(Object obj, String... attrs) {
		Map<String, Object> val = new HashMap<>();
		if (obj == null) {
			return val;
		}
		if (attrs == null || attrs.length == 0) {
			return beanToMapAll(obj);
		}
		QBeanUtil.toUpperCase(attrs);
		Set<String> exclAttrs = new HashSet<>(Arrays.asList(attrs));

		Map<String, PropertyDescriptor> descs = BeanContextFactory.getDescriptorsA(obj.getClass());
		for (Map.Entry<String, PropertyDescriptor> e : descs.entrySet()) {
			PropertyDescriptor desc = e.getValue();
			Method mget = desc.getReadMethod();
			if (mget == null || exclAttrs.contains(e.getKey())) {
				continue;
			}
			try {
				Object value = mget.invoke(obj);
				if (value != null) {
					val.put(desc.getName(), value);
				}
			} catch (Exception ex) {
				logger.error("{}", ex.getMessage());
			}
		}
		return val;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static Map<String, Object> beanToMapAll(Object obj) {
		Map<String, Object> val = new TreeMap<>();
		if (obj == null) {
			return val;
		}

		if (Map.class.isAssignableFrom(obj.getClass())) {
			val.putAll((Map) obj);
			return val;
		}

		Map<String, PropertyDescriptor> descs = BeanContextFactory.getDescriptors(obj.getClass());
		for (PropertyDescriptor d : descs.values()) {
			Method mget = d.getReadMethod();
			if (mget == null) {
				continue;
			}
			try {
				Object value = mget.invoke(obj);
				if (value != null) {
					val.put(d.getName(), value);
				}
			} catch (Exception e) {
				logger.error("{}", e.getMessage());
			}
		}
		return val;
	}

	@SuppressWarnings("rawtypes")
	public static Map<String, Object> beanToMap(Object obj, String... attrs) {
		Map<String, Object> val = new HashMap<>();
		if (obj == null) {
			return val;
		}
		if (attrs == null || attrs.length == 0) {
			return beanToMapAll(obj);
		}

		if (Map.class.isAssignableFrom(obj.getClass())) {
			for (String k : attrs) {
				val.put(k, ((Map) obj).get(k));
			}
			return val;
		}
		
		Map<String, PropertyDescriptor> descs = BeanContextFactory.getDescriptorsA(obj.getClass());

		for (String attr : attrs) {
			PropertyDescriptor desc = descs.get(attr);
			if (desc == null) {
				logger.warn("property:{} not exist!", attr);
				continue;
			}
			Method mget = desc.getReadMethod();
			if (mget == null) {
				logger.warn("property:{} not exist!", attr);
				continue;
			}
			try {
				Object value = mget.invoke(obj);
				if (value != null) {
					val.put(desc.getName(), value);
				}
			} catch (Exception e) {
				logger.error("{}", e.getMessage());
			}
		}
		return val;
	}

	public static Map<String, Object> beanToMapA(Object obj) {
		Map<String, Object> v0 = beanToMap(obj);
		Map<String, Object> vA = new HashMap<>();
		for (Map.Entry<String, Object> e : v0.entrySet()) {
			vA.put(e.getKey().toUpperCase(), e.getValue());
		}
		return vA;
	}
}
