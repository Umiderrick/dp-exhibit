package com.zeusas.dp.util;

import java.beans.PropertyDescriptor;
import java.beans.Transient;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.zeusas.dp.exhibit.utils.BeanContextFactory;
import com.zeusas.dp.exhibit.utils.QBeanUtil;
import com.zeusas.dp.exhibit.utils.QDate;
import com.zeusas.dp.exhibit.utils.QString;
import com.zeusas.dp.exhibit.utils.TypeConverter;

/**
 * 处理数据库中表的一行数据
 *
 */
public class Record implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7970408853461943587L;
	private static Logger logger = LoggerFactory.getLogger(Record.class);

	/** 存储数据库中的一行，放入一个数组 */
	private Object[] value;

	public Record() {
	}

	public Record(Object[] value) {
		this.value = new Object[value.length + 1];
		for (int i = 0; i < value.length; i++) {
			this.set(i + 1, value[i]);
		}
	}

	/**
	 * 构造函数，len为一行的列数。
	 * 
	 * @param len
	 */
	public Record(int len) {
		// 初始化行，行的第0位表示本行在操作中的状态
		value = new Object[len + 1];
	}

	@Transient
	public void setPK(PK pk) {
		value[0] = pk;
	}

	@JSONField(deserialize = false, serialize = false)
	@Transient
	public PK getPK() {
		return (PK) value[0];
	}

	/**
	 * 设定列数据
	 * 
	 * @param stat 数据行的状态
	 * @param args 列数据
	 */
	public void setColumnData(int index, Object... args) {
		value[0] = new PK(args[index]);
		for (int i = 1; i < value.length; i++) {
			this.set(i, args[i]);
		}
	}

	/**
	 * 取得行数据的总列数。
	 * 
	 * @return
	 */
	public int size() {
		return value.length - 1;
	}

	public void set(int index, Object obj) {
		if (obj != null && (obj instanceof String)) {
			String s = ((String) obj).trim();
			obj = "~".equals(s) ? null : s;
		}
		value[index] = obj;
	}

	@Transient
	public Object get(int index) {
		return value[index];
	}

	public String getString(int idx) {
		Object obj = value[idx];
		if (obj == null || obj instanceof String) {
			return (String) obj;
		}
		if (obj instanceof Date) {
			return QDate.format(QDate.YYYY_MM_DD, (Date) obj);
		}
		return obj.toString();
	}

	public Integer getInteger(int idx) {
		Object obj = value[idx];
		return TypeConverter.toInteger(obj, 0);
	}

	public Long getLong(int idx) {
		Object obj = value[idx];
		return TypeConverter.toLong(obj, 0L);
	}

	public Double getDouble(int idx) {
		return TypeConverter.toDouble(value[idx], 0.0);
	}

	public Float getFloat(int idx) {
		return TypeConverter.toFloat(value[idx], 0.0F);
	}

	public Boolean getBoolean(int idx) {
		Object obj = value[idx];
		return TypeConverter.toBoolean(obj);
	}

	public void update(Record r) {
		if (r.value.length != value.length) {
			throw new ArrayIndexOutOfBoundsException("Cannot update!");
		}
		for (int i = 1; i < value.length; i++) {
			value[i] = r.get(i);
		}
	}

	@Override
	public int hashCode() {
		return value[0] == null ? 0 : value[0].hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || (!(obj instanceof Record))) {
			return false;
		}
		Record a = (Record) obj;
		for (int i = 1; i < value.length; i++) {
			if (!Objects.equals(value[i], a.value[i])) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder bb = new StringBuilder(128);
		bb.append(value[0]);
		for (int i = 1; i < this.value.length; i++) {
			bb.append(',');
			if (value[i] != null) {
				bb.append(value[i]);
			}
		}
		return bb.toString();
	}

	public <T> T toBean(Meta meta, Class<T> cls) {
		String[] props = meta.getProperty();
		T bean = QBeanUtil.newInstance(cls);

		Map<String, PropertyDescriptor> pdescs = BeanContextFactory.getDescriptorsA(cls);
		PropertyDescriptor pdesc;
		Method mw;

		for (int idx = 1; idx < props.length; idx++) {
			String prop = props[idx];
			if (prop == null //
					|| (pdesc = pdescs.get(prop)) == null) {
				continue;
			}

			if ((mw = pdesc.getWriteMethod()) == null) {
				continue;
			}

			try {
				QBeanUtil.setProperty(mw, bean, value[idx]);
			} catch (Exception e) {
				// NOP
			}
		}

		return bean;
	}

	public JSONObject toJSONObject(Meta meta) {
		JSONObject jo = new JSONObject();
		String[] property = meta.getProperty();
		if (property == null || property.length == 0) {
			logger.warn("在DDL中没有定义属性名称！");
			return jo;
		}
		int len = (value.length < property.length) ? value.length : property.length;
		for (int i = 1; i < len; i++) {
			jo.put(property[i], value[i]);
		}
		return jo;
	}

	public Object[] getValue() {
		return value;
	}

	public void setValue(Object[] val) {
		if (val != null) {
			this.value = Arrays.copyOf(val, val.length);
		}
	}

	public void extern() {
		for (int i = 1; i < value.length; i++) {
			Object obj = value[i];
			if (obj != null) {
				if (obj instanceof String) {
					value[i] = QString.intern((String) obj);
				}
			}
		}
	}
}
