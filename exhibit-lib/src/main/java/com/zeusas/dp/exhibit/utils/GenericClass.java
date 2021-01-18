package com.zeusas.dp.exhibit.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class GenericClass<T> {

	public final Class<T> entityClass;

	public GenericClass(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	@SuppressWarnings("unchecked")
	public GenericClass() {
		Class<T> cc = null;
		Type genType = getClass().getGenericSuperclass();
		// 如果未定义类型
		if (genType != null && genType instanceof ParameterizedType) {
			Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
			if ((params != null) //
					&& (params.length > 0) //
					&& !"T".equals(params[0].getTypeName())) {
				cc = (Class<T>) params[0];
			}
		}
		this.entityClass = cc;
	}
}
