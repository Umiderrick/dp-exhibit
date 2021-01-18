package com.zeusas.dp.exhibit.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 本类{@code BeanContext} 用于保持 Bean的信息
 * <p>
 * {@code BeanContext} 将一个类分解为{@code PropertyDescriptor}， Field，用于快速反射
 * 的低级处理。
 * 
 * @author ITC of BGT
 *
 */
@SuppressWarnings("restriction")
public class BeanContext implements Cloneable {
	private final static Logger logger = LoggerFactory.getLogger(BeanContext.class);
	/** 属性描述，通过属性名，取得 {@code PropertyDescriptor} */
	private final Map<String, PropertyDescriptor> descriptors = new TreeMap<>();
	/** 属性描述，通过不区分大小写属性名，取得 {@code PropertyDescriptor} */
	private final Map<String, PropertyDescriptor> descriptorsA = createMap(PropertyDescriptor.class);
	/** Field 名*/
	private final Map<String, Field> fields = new TreeMap<>();
	private final Map<String, Field> fieldsA = createMap(Field.class);
	
	// 字段对应的偏移地址
	private final Map<String, Long> fieldOffsetN = new TreeMap<>();

	// 由于字段FIELD无法实现比较，使用函数传入，实现FIELD按名字比较功能
	private final Map<Field, Long> fieldOffset = new TreeMap<Field, Long>(
			(v1, v2) -> v1.getName().compareTo(v2.getName()));
	private final Class<?> type;

	final static sun.misc.Unsafe unsafe = IOUtils.getUnsafe();

	private BeanContext(Class<?> cls, boolean cloneOps) {
		this.type = cls;
		if (!cloneOps) {
			doRegister();
		}
	}

	BeanContext(Class<?> cls) {
		this(cls, false);
	}

	public Map<String, PropertyDescriptor> getDescriptors() {
		return this.descriptors;
	}

	public Map<String, Long> getFieldOffsetN() {
		return fieldOffsetN;
	}

	public Map<Field, Long> getFieldOffset() {
		return fieldOffset;
	}

	public Map<Field, Long> getNameOffset() {
		return fieldOffset;
	}

	public PropertyDescriptor getDescriptor(String key) {
		return this.descriptors.get(key);
	}

	public Map<String, PropertyDescriptor> getDescriptorsA() {
		return this.descriptorsA;
	}

	public PropertyDescriptor getDescriptorA(String key) {
		return this.descriptorsA.get(key);
	}

	public Map<String, Field> getFields() {
		return this.fields;
	}

	public Field getField(String key) {
		return this.fields.get(key);
	}

	public Map<String, Field> getFieldsA() {
		return this.fieldsA;
	}

	public Field getFieldA(String key) {
		return this.fieldsA.get(key);
	}

	public Long getFieldOffset(String f) {
		return this.fieldOffsetN.get(f);
	}
	
	public Long getFieldOffset(Field f) {
		return this.getFieldOffset(f.getName());
	}
	
	public Collection<Field> fields() {
		return fields.values();
	}

	public Collection<PropertyDescriptor> propertyDescriptors() {
		return this.descriptors.values();
	}

	public Class<?> type() {
		return type;
	}

	private static <T> Map<String, T> createMap(Class<T> cls) {
		final Map<String, T> myMap = new TreeMap<String, T>() {
			private static final long serialVersionUID = 1L;

			@Override
			public T get(Object key) {
				return super.get(((String) key).toUpperCase());
			}

			@Override
			public T put(String key, T desc) {
				if (key == null || desc == null) {
					return null;
				}
				return super.put(key.toUpperCase(), desc);
			}

			@Override
			public boolean containsKey(Object key) {
				String k = ((String) key).toUpperCase();
				return super.containsKey(k);
			}
		};
		return myMap;
	}

	private void doRegister() {
		BeanInfo beanInfo;
		try {
			Class<?> t = type;
			while (t != Object.class) {
				Field[] ff = t.getDeclaredFields();
				for (Field f : ff) {
					// 如果是静态，或TRANSISENT类型
					if (Modifier.isStatic(f.getModifiers())//
							|| Modifier.isTransient(f.getModifiers())) {
						continue;
					}
					f.setAccessible(true);
					String name = f.getName();
					Class<?> type = f.getType();
					if (Modifier.isFinal(f.getModifiers())) {
						if (Collection.class.isAssignableFrom(type) //
								|| Map.class.isAssignableFrom(type)) {
							this.fields.put(name, f);
							this.fieldsA.put(name, f);
						}
					} else {
						fields.put(name, f);
						fieldsA.put(name, f);
					}
					// 仅原生类型可以使用直接操作
					Long offset = Long.valueOf(unsafe.objectFieldOffset(f));
					this.fieldOffset.put(f, offset);
					this.fieldOffsetN.put(f.getName(), offset);
				}
				t = t.getSuperclass();
			}

			beanInfo = Introspector.getBeanInfo(type);
			PropertyDescriptor[] descs = beanInfo.getPropertyDescriptors();

			for (PropertyDescriptor desc : descs) {
				if (desc.getName().equals("class")//
						&& desc.getWriteMethod() == null) {

					continue;
				}
				descriptors.put(desc.getName(), desc);
				descriptorsA.put(desc.getName(), desc);
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			logger.error("注册类：{}错误！", type.getName(), e);
		}
	}

	@Override
	public int hashCode() {
		return type.getName().hashCode();
	}

	@Override
	public boolean equals(Object b) {
		if (this == b) {
			return true;
		}
		if (b == null || !(b instanceof BeanContext)) {
			return false;
		}
		return this.type.equals(((BeanContext) b).type);
	}

	public Object clone() {
		try {
			BeanContext ctx =(BeanContext) super.clone();
			return ctx;
		} catch (Exception e) {
			// NOP
		}
		BeanContext ctx = new BeanContext(this.type, true);
		ctx.descriptors.putAll(this.descriptors);
		ctx.descriptorsA.putAll(this.descriptorsA);
		ctx.fieldOffsetN.putAll(this.fieldOffsetN);
		ctx.fieldOffset.putAll(this.fieldOffset);
		ctx.fields.putAll(this.fields);
		ctx.fieldsA.putAll(this.fieldsA);
		return ctx;
	}
}
