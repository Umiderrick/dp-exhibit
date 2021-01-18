package com.zeusas.dp.exhibit.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@SuppressWarnings("restriction")
public final class QBeanUtil {
	static Logger logger = LoggerFactory.getLogger(QBeanUtil.class);

	final static sun.misc.Unsafe unsafe = IOUtils.getUnsafe();

	private QBeanUtil() {
	}

	public static Object getField(Object src, String fieldName) {
		if (src == null) {
			throw new ServiceException(String.format("获取字段:%s的源对象为空。", fieldName));
		}
		try {
			return src.getClass().getField(fieldName).get(src);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	public static void setField(Object target, Field field, String value) {
		Map<Field, Long> offsets = BeanContextFactory.getBeanContext(target.getClass()).getFieldOffset();
		Long offset = offsets.get(field);
		if (offset == null) {
			throw new RuntimeException(String.format("Class: %s has not a field: %s", //
					target.getClass().getName(), field.getName()));
		}
		Class<?> type = field.getType();
		if (type.equals(String.class)) {
			unsafe.putObject(target, offset, value);
		} else if (type.equals(int.class)) {
			unsafe.putInt(target, offset, TypeConverter.toInteger(value, 0));
		} else if (type.equals(short.class)) {
			unsafe.putShort(target, offset, (short)TypeConverter.toInteger(value, 0));
		} else if (type.equals(byte.class)) {
			unsafe.putByte(target, offset,TypeConverter.toByte(value,(byte) 0));
		} else if (type.equals(float.class)) {
			unsafe.putFloat(target, offset, TypeConverter.toFloat(value,(float) 0));
		} else if (type.equals(long.class)) {
			unsafe.putLong(target, offset, TypeConverter.toLong(value, 0));
		} else if (type.equals(boolean.class)) {
			unsafe.putBoolean(target, offset, TypeConverter.toBoolean(value, false));
		} else {
			setField(target, field, (Object) Double.valueOf(value));
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void setQField(Object target, Field field, Object value) {
		try {
			// 空值也处理？
			if (value == null) {
				if (field.getType().isPrimitive() //
						|| Modifier.isFinal(field.getModifiers())) {
					return;
				}
				field.set(target, null);
				return;
			}

			// 如果是final
			if (Modifier.isFinal(field.getModifiers())) {
				Object targetVal = field.get(target);
				if (Collection.class.isAssignableFrom(field.getType())) {
					((Collection) targetVal).addAll((Collection) value);
				} else if (Map.class.isAssignableFrom(field.getType())) {
					((Map) targetVal).putAll((Map) value);
				} else {
					logger.warn("无法复制的类型。{}", field.getType());
				}
			} else {
				field.set(target, value);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void setField(Object target, //
			Field field, //
			Object value) {
		try {
			if (value == null) {
				if (field.getType().isPrimitive() //
						|| Modifier.isFinal(field.getModifiers())) {
					return;
				}
				field.set(target, null);
			} else if (Modifier.isFinal(field.getModifiers())) {
				Object targetVal = field.get(target);
				if (Collection.class.isAssignableFrom(field.getType())) {
					((Collection) targetVal).addAll((Collection) value);
				} else if (Map.class.isAssignableFrom(field.getType())) {
					((Map) targetVal).putAll((Map) value);
				}
			} else {
				Class<?> type = field.getType();
				if (type.isAssignableFrom(value.getClass())) {
					field.set(target, value);
				} else {
					field.set(target, TypeConverter.toType(value, type));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 设用setter，设置属性的方法
	 * 
	 * @param m      method
	 * @param target 目标类
	 * @param value  值
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static void setProperty(Method m, Object target, Object value) {
		assert m != null;
		Class<?> type = m.getParameterTypes()[0];
		try {
			if (value == null) {
				if (!type.isPrimitive()) {
					m.invoke(target, new Object[] { null });
				}
				return;
			}
			if (!type.isAssignableFrom(value.getClass())) {
				value = TypeConverter.toType(value, type);
			}
			if (value != null) {
				m.invoke(target, value);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Class<?> forName(String className) {
		try {
			return Class.forName(className);
		} catch (Exception e) {
			logger.warn("不存在Java类：{}", className);
		}
		return null;
	}

	public static <T> T newInstance(Class<T> type) {
		try {
			return type.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	static void toUpperCase(String s[]) {
		for (int i = 0; i < s.length; i++)
			s[i] = s[i].toUpperCase();
	}

	public static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		}
		if (obj instanceof Optional) {
			return ((Optional<?>) obj).isPresent();
		}
		if (obj instanceof CharSequence) {
			return ((CharSequence) obj).length() == 0;
		}
		if (obj instanceof Collection) {
			return ((Collection<?>) obj).isEmpty();
		}
		if (obj instanceof Map) {
			return ((Map<?, ?>) obj).isEmpty();
		}
		if (obj.getClass().isArray()) {
			return Array.getLength(obj) == 0;
		}
		return true;
	}

	/**
	 * 将数据转化为数组对象。
	 * 
	 * @param data
	 * @param type
	 * @return
	 */
	public static <T> List<T> toRpcArray(Object data, Class<T> type) {
		List<T> array = new ArrayList<>();
		if (data instanceof JSONArray) {
			return ((JSONArray) data).toJavaList(type);
		}
		if (List.class.isAssignableFrom(data.getClass())) {
			for (Object v : (List<?>) data) {
				array.add(toRpcBean(v, type));
			}
		} else if (data.getClass().isArray()) {
			for (int i = 0; i < Array.getLength(data); i++) {
				array.add(toRpcBean(Array.get(data, i), type));
			}
		}
		return array;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T toRpcBean(Object v, //
			Class<T> type) {
		if (v == null || type.isAssignableFrom(v.getClass())) {
			return (T) v;
		} else if (v instanceof Map || v instanceof JSONObject) {
			return ((T) BeanMap.mapToBean((Map) v, type));
		}
		return (TypeConverter.toType(v, type));
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> getGenericClass(Class<?> clazz, int index) {
		Type genType = clazz.getGenericSuperclass();
		// 如果未定义类型
		if (genType != null && genType instanceof ParameterizedType) {
			Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
			if ((params != null) && (params.length >= (index - 1))) {
				return (Class<T>) params[index];
			}
		}
		return null;
	}
	
	public static sun.misc.Unsafe getUnsafe() {
		return unsafe;
	}
}
