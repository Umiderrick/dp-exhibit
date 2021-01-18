package com.zeusas.dp.util;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.UUID;

import com.zeusas.dp.exhibit.utils.QString;

/**
 * ID 静态池处理
 */
public final class ID implements Serializable {
	/** serialVersionUID */
	private static final long serialVersionUID = 6508519386760294599L;

	final static Charset UTF8C = Charset.forName("UTF-8");
	// ID 关联的值
	private Object value[];
	// ID
	private transient String id;

	public ID() {
		id = "";
		value = new Object[0];
	}

	private ID(String id, Object... o) {
		this.id = id;
		this.value = o.clone();
	}

	public ID(String v) {
		this(v, new Object[] { v });
	}

	public ID(Number v) {
		this(String.valueOf(v), new Object[] { v });
	}

	public ID(Class<?> v) {
		this(valueOf(v), new Object[] { v });
	}

	public ID(Object... ids) {
		this(valueOf(ids), ids.clone());
	}

	public static String valueOf(Class<?> type) {
		return type.getSimpleName() + '@';
	}

	public static String valueOf(String typeID) {
		return typeID;
	}

	public static String valueOf(Object... s) {
		StringBuffer bb = new StringBuffer();
		if (s.length == 0)
			return "";
		if (s[0] instanceof Class) {
			bb.append(((Class<?>) s[0]).getSimpleName());
			if (s.length == 1) {
				return bb.append('@').toString();
			}
		} else {
			bb.append(String.valueOf(s[0]));
		}
		if (s.length == 1) {
			return bb.toString();
		}
		bb.append('@');
		for (int i = 1; i < s.length; i++) {
			bb.append(String.valueOf(s[i])).append('-');
		}
		return bb.substring(0, bb.length() - 1);
	}

	public static String asUUID() {
		UUID id = UUID.randomUUID();
		return id.toString();
	}

	public Object[] getValue() {
		return value.clone();
	}

	public void setValue(Object[] value) {
		this.value = value.clone();
		this.id = null;
	}

	@Override
	public String toString() {
		if (this.id == null) {
			id = valueOf(value);
		}
		return this.id;
	}

	public byte[] toBytes() {
		return QString.toBytes(toString());
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || !(obj instanceof ID)) {
			return false;
		}
		return toString().equals(obj.toString());
	}

	public static byte[] toBytes(Object... ids) {
		return valueOf(ids).getBytes(UTF8C);
	}
}
