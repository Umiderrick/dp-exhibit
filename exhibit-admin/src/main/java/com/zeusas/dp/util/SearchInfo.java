package com.zeusas.dp.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.zeusas.dp.exhibit.utils.HttpUtil;
import com.zeusas.dp.exhibit.utils.QDate;
import com.zeusas.dp.exhibit.utils.TypeConverter;

@SuppressWarnings("serial")
public final class SearchInfo implements Serializable {
	private static final Map<String, Link> links = new HashMap<>();
	private static final Map<String, Op> ops = new HashMap<>();
	private static final Map<String, Type> types = new HashMap<>();

	public static enum Link {
		and( "AND","and"), //
		or("OR","or");

		private final String v;
		private final String operator;

		private Link(String val, String operator) {
			this.v = val;
			this.operator = operator;
			links.put(val, this);
		}

		public String value() {
			return this.v;
		}

		public String operator() {
			return operator;
		}

		public static Link of(String s) {
			Link v = links.get(s);
			return (v == null) ?  and: v;
		}
	};
	
	public static enum Op {
		equal("equal", "="), //
		notequal("notequal", "<>"), //
		startwith("startwith", ">"), //
		endwith("endwith", "<"), //
		like("like", " LIKE "), //
		greater("greater", ">"), //
		greaterorequal("greaterorequal", ">="), //
		less("less", "<"), //
		lessorequal("lessorequal", "<="), //
		in("in", " IN "), //
		notin("notin", " NOT IN ");

		private final String v;
		private final String operator;

		private Op(String val, String operator) {
			this.v = val;
			this.operator = operator;
			ops.put(val, this);
		}

		public String value() {
			return this.v;
		}

		public String operator() {
			return operator;
		}

		public static Op of(String s) {
			Op v = ops.get(s);
			return (v == null) ? equal : v;
		}
	};

	public static enum Type {
		t_str("str"), //
		t_date("date"), //
		t_int("int"), //
		t_long("long"), //
		t_double("double"), //
		t_float("float"), //
		t_time("time"), //
		t_datetime("datetime");

		private final String type;

		private Type(String val) {
			this.type = val;
			types.put(val, this);
		}

		public String value() {
			return this.type;
		}

		public static Type of(String s) {
			Type v = types.get(s);
			return (v == null) ? t_str : v;
		}
	}

	/**
	 * 算符
	 */
	private String op;
	/**
	 * 字符
	 */
	private String field;
	/**
	 * 值
	 */
	private String value;
	/**
	 * 类型
	 */
	private String type;
	
	/**
	 * 连接类型
	 */
	private String link;

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getOp() {
		return this.op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public String getField() {
		return this.field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getObjectValue() {
		Object object;
		switch (Type.of(type)) {
		case t_date:
			object = TypeConverter.toDate(value);
			break;
		case t_long:
			object = TypeConverter.toLong(value);
			break;
		case t_int:
			object = TypeConverter.toInteger(value);
			break;
		case t_double:
			object = TypeConverter.toDouble(value);
			break;
		case t_float:
			object = TypeConverter.toFloat(value);
			break;
		case t_datetime:
			object = QDate.toQDatetime(value);
			break;
		default:
			object = value;
		}
		return object;
	}
	
	public boolean valid() {
		return !((getObjectValue() == null //
		|| HttpUtil.isEmpty(field)));
	}
}
