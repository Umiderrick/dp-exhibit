package com.zeusas.dp.exhibit.utils;

import static java.sql.Types.BIGINT;
import static java.sql.Types.BIT;
import static java.sql.Types.BOOLEAN;
import static java.sql.Types.CHAR;
import static java.sql.Types.DATE;
import static java.sql.Types.DECIMAL;
import static java.sql.Types.DOUBLE;
import static java.sql.Types.FLOAT;
import static java.sql.Types.INTEGER;
import static java.sql.Types.LONGNVARCHAR;
import static java.sql.Types.LONGVARCHAR;
import static java.sql.Types.NCHAR;
import static java.sql.Types.NUMERIC;
import static java.sql.Types.NVARCHAR;
import static java.sql.Types.REAL;
import static java.sql.Types.SMALLINT;
import static java.sql.Types.TIME;
import static java.sql.Types.TIMESTAMP;
import static java.sql.Types.TINYINT;
import static java.sql.Types.VARCHAR;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 类型转换器
 * 
 * 转换从数据库中取得的类型。
 */
public interface TypeConverter {
	static Logger logger = LoggerFactory.getLogger(TypeConverter.class);

	public static int toInteger(Object obj, int val) {
		Integer i = toInteger(obj);
		return i == null ? val : i.intValue();
	}

	public static Integer toInteger(Object obj) {
		// 如果为空或整型
		if (obj == null || (obj instanceof Integer)) {
			return (Integer) obj;
		}
		if (obj instanceof String) {
			return QString.toInt((String) obj);
		}
		if (obj instanceof Number) {
			return ((Number) obj).intValue();
		}
		if (obj instanceof Date) {
			return (int) (((Date) obj).getTime() / 1000);
		}
		return QString.toInt(obj.toString());
	}

	public static Byte toByte(Object obj) {
		// 如果为空或整型
		if (obj == null || (obj instanceof Byte)) {
			return (Byte) obj;
		}
		if (obj instanceof String) {
			return QString.toByte((String) obj);
		}
		if (obj instanceof Number) {
			return Byte.valueOf(((Number) obj).byteValue());
		}
		return null;
	}

	public static byte toByte(Object obj, byte val) {
		// 如果为空或整型
		if (obj == null) {
			return val;
		}
		if (obj instanceof Number) {
			return Byte.valueOf(((Number) obj).byteValue());
		}

		if (obj instanceof String) {
			Byte b = QString.toByte((String) obj);
			return b == null ? val : b.byteValue();
		}

		return val;
	}
	public static short toShort(Object obj, short defV) {
		// 如果为空或整型
		if (obj == null || (obj instanceof Short)) {
			return (Short) obj;
		}
		if (obj instanceof String) {
			return QString.toShort((String) obj, defV);
		}
		if (obj instanceof Number) {
			return Short.valueOf(((Number) obj).shortValue());
		}
		
		return defV;
	}

	public static Short toShort(Object obj) {
		// 如果为空或整型
		if (obj == null || (obj instanceof Short)) {
			return (Short) obj;
		}
		if (obj instanceof String) {
			return QString.toShort((String) obj);
		}
		if (obj instanceof Number) {
			return Short.valueOf(((Number) obj).shortValue());
		}
		return null;
	}

	public static double toDouble(Object obj, double val) {
		Double d = toDouble(obj);
		return d == null ? val : d.doubleValue();
	}

	public static Double toDouble(Object obj) {
		if (obj == null || obj instanceof Double) {
			return (Double) obj;
		}
		if (obj instanceof Number) {
			return ((Number) obj).doubleValue();
		}
		if (obj instanceof CharSequence) {
			return QString.toDouble(obj.toString());
		}
		if (obj instanceof Date) {
			return Double.valueOf(((Date) obj).getTime());
		}
		return QString.toDouble(obj.toString());
	}

	public static long toLong(Object obj, long val) {
		if (obj == null)
			return val;

		if (obj instanceof Number) {
			return ((Number) obj).longValue();
		}

		if (obj instanceof CharSequence) {
			return QString.toLong(obj.toString(), val);
		}
		if (obj instanceof Date) {
			return (((Date) obj).getTime());
		}
		return QString.toLong(obj.toString(), val);
	}

	public static Long toLong(Object obj) {
		if (obj == null || obj instanceof Long) {
			return (Long) obj;
		}
		if (obj instanceof Number) {
			return Long.valueOf(((Number) obj).longValue());
		}
		if (obj instanceof CharSequence) {
			return QString.toLong((CharSequence) obj, 0L);
		}
		if (obj instanceof Date) {
			return ((Date) obj).getTime();
		}
		if (obj instanceof LocalDate) {
			return QDate.toTimeInMillis((LocalDate) obj);
		}
		if (obj instanceof LocalDateTime) {
			return QDate.toTimeInMillis((LocalDateTime) obj);
		}
		return QString.toLong(obj.toString());
	}

	public static java.sql.Date toSQLDate(Object obj) {
		if (obj == null || obj instanceof java.sql.Date) {
			return (java.sql.Date) obj;
		}
		Date dd = toDate(obj);
		return dd == null ? null : new java.sql.Date(dd.getTime());
	}

	public static java.sql.Timestamp toTimestamp(Object obj) {
		if (obj == null || obj instanceof java.sql.Timestamp) {
			return (java.sql.Timestamp) obj;
		}
		Date dd = toDate(obj);
		return dd == null ? null : new java.sql.Timestamp(dd.getTime());
	}

	public static Byte toBit(Object obj) {
		Byte v = toByte(obj);
		if (v == null)
			return null;
		return v.intValue() == 0 ? Byte.valueOf((byte) 0) : Byte.valueOf((byte) 1);
	}

	public static Date toDate(Object obj) {
		if (obj == null || obj instanceof Date) {
			return (Date) obj;
		}
		if (obj instanceof Long) {
			return new Date((Long) obj);
		}
		// 21000000
		if (obj instanceof Integer) {
			int val = ((Integer) obj).intValue();
			if (val / 10000 > 1900 && val / 10000 < 3000) {
				return QDate.asDate(val / 10000, //
						val % 10000 % 100, //
						val % 100);
			}
			return new Date(1000L * val);
		}

		if (obj instanceof LocalDate) {
			return toDate((LocalDate) obj);
		}
		if (obj instanceof LocalDateTime) {
			return toDate((LocalDateTime) obj);
		}
		String s = obj.toString();

		return QDate.toQDatetime(s);
	}

	public static boolean toBoolean(Object obj, boolean v) {
		Boolean b = toBoolean(obj);
		return b == null ? v : b.booleanValue();
	}

	public static <T> List<T> toList(Object v, Class<T> type) {
		List<T> retVal = new ArrayList<>();
		if (v == null)
			return null;
		if (!(v instanceof Collection)) {
			throw new IllegalArgumentException("变换为List,必须为集合对象。");
		}
		for (Object obj : ((Collection<?>) v)) {
			retVal.add((T) toType(obj, type));
		}
		return retVal;
	}

	public static <T> Set<T> toSet(Object v, Class<T> type) {
		Set<T> retVal = new HashSet<>();
		if (v == null)
			return null;
		if (!(v instanceof Collection)) {
			throw new IllegalArgumentException("变换为List,必须为集合对象。");
		}
		for (Object obj : ((Collection<?>) v)) {
			retVal.add((T) toType(obj, type));
		}
		return retVal;
	}

	public static BigDecimal toBigDecimal(Object obj, BigDecimal defValue) {
		BigDecimal v = toBigDecimal(obj);
		return v == null ? defValue : v;
	}

	public static BigDecimal toBigDecimal(Object obj) {
		if (obj == null || (obj instanceof BigDecimal)) {
			return (BigDecimal) obj;
		}
		if (obj instanceof Double) {
			return new BigDecimal((Double) obj);
		}
		if (obj instanceof Float) {
			return new BigDecimal((Float) obj);
		}
		if (obj instanceof Integer) {
			return new BigDecimal((Integer) obj);
		}
		if (obj instanceof Long) {
			return new BigDecimal((Long) obj);
		}
		if (obj instanceof BigInteger) {
			return new BigDecimal((BigInteger) obj);
		}
		if (obj instanceof Date) {
			return new BigDecimal(((Date) obj).getTime());
		}
		try {
			if (obj instanceof String) {
				String b = (String) obj;
				// 兼容用友的空和非0的情况
				if (QString.isNullOrEmpty(b)) {
					return null;
				}
				// xxx: 用友兼容性
				if ("~".equals(b)) {
					return BigDecimal.ZERO;
				}
				return new BigDecimal((String) obj);
			}

			return new BigDecimal(obj.toString());
		} catch (Exception e) {
			// NOP
		}
		return null;
	}

	public static Boolean toBoolean(Object obj) {
		// 如果是空或布尔类弄，直接返回
		if (obj == null || obj instanceof Boolean) {
			return (Boolean) obj;
		}
		// 如果是数字，仅当为0时，返因TRUE;
		if (obj instanceof Number) {
			return Boolean.valueOf(((Number) obj).intValue() != 0);
		}
		// 如果是串的情况，当为真时，返回TRUE
		if (obj instanceof String) {
			String s = ((String) obj).trim();
			if (s.isEmpty()) {
				return Boolean.FALSE;
			}
			char c = Character.toUpperCase(s.charAt(0));
			if (c == 'T' || c == 'Y' || c == '1' || c == '是') {
				return Boolean.TRUE;
			}
			if (c == 'f' || c == 'N' || c == '0' || c == '否') {
				return Boolean.FALSE;
			}
		}
		// XXX: 可能从已经状态不能确定是否为TRUE/FALSE。
		return  Boolean.FALSE;
	}

	public static BigInteger toBigInteger(Object bg) {
		if (bg == null || bg instanceof BigInteger) {
			return (BigInteger) bg;
		}

		if (bg instanceof Number) {
			return BigInteger.valueOf(((Number) bg).longValue());
		}

		if (bg instanceof CharSequence) {
			long val = QString.toLong((CharSequence) bg, 0);
			return BigInteger.valueOf(val);
		}
		if (bg instanceof Date) {
			return BigInteger.valueOf(((Date) bg).getTime());
		}
		if (bg instanceof LocalDate) {
			return BigInteger.valueOf(QDate.toTimeInMillis((LocalDate) bg));
		}
		if (bg instanceof LocalDateTime) {
			return BigInteger.valueOf(QDate.toTimeInMillis((LocalDateTime) bg));
		}
		return null;
	}

	public static String toString(Object obj) {
		if (obj == null || obj instanceof String) {
			return (String) obj;
		}
		if (obj instanceof Date) {
			return QDate.format(QDate.YYYY_MM_DD, (Date) obj);
		}
		if (obj instanceof LocalDate) {
			return ((LocalDate) obj).format(QDate.DTF_YYYY_MM_DD);
		}
		if (obj instanceof LocalDateTime) {
			return ((LocalDateTime) obj).format(QDate.DTF_YYYY_MM_DD);
		}
		return obj.toString();
	}

	public static float toFloat(Object obj, float val) {
		Float f = toFloat(obj);
		return f == null ? val : f.floatValue();
	}

	public static Float toFloat(Object obj) {
		if (obj == null || obj instanceof Float) {
			return (Float) obj;
		}
		if (obj instanceof Number) {
			return ((Number) obj).floatValue();
		}
		if (obj instanceof String) {
			QString.toFloat((String) obj);
		}
		if (obj instanceof Date) {
			return new Float(((Date) obj).getTime());
		}
		return QString.toFloat(obj.toString());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T toType(Object obj, Class<T> type) {
		if (obj == null || type.isAssignableFrom(obj.getClass())) {
			return (T) obj;
		}
		if (type.equals(String.class)) {
			if (obj instanceof Date) {
				return (T) QDate.format(QDate.YYYY_MM_DD, (Date) obj);
			}
			return (T) obj.toString();
		}
		if (type.equals(int.class) || type.equals(Integer.class)) {
			return (T) toInteger(obj);
		}
		if (type.equals(long.class) || type.equals(Long.class)) {
			return (T) toLong(obj);
		}
		if (type.equals(double.class) || type.equals(Double.class)) {
			return (T) toDouble(obj);
		}
		if (type.equals(float.class) || type.equals(Float.class)) {
			return (T) toFloat(obj);
		}
		if (type.equals(byte.class) || type.equals(Byte.class)) {
			return (T) toByte(obj);
		}
		if (type.equals(short.class) || type.equals(Short.class)) {
			return (T) toShort(obj);
		}
		if (type.equals(Date.class)) {
			return (T) toDate(obj);
		}
		if (type.equals(boolean.class) || type.equals(Boolean.class)) {
			return (T) toBoolean(obj);
		}
		if (type.equals(BigInteger.class)) {
			return (T) toBigInteger(obj);
		}
		if (type.equals(BigDecimal.class)) {
			return (T) toBigDecimal(obj);
		}
		// XXX: 如果参数为一Map,需要使用Map对象转为Bean
		if (obj instanceof Map) {
			return (T) BeanMap.mapToBean((Map) obj, type);
		}

		if (type.equals(Short.class)) {
			return (T) toShort(obj);
		}
		if (type.equals(Byte.class)) {
			return (T) toByte(obj);
		}
		return null;
	}

	public static Time toTime(Object obj) {
		if (obj == null || obj instanceof Time) {
			return (Time) obj;
		}
		if (obj instanceof Date) {
			return new Time(((Date) obj).getTime());
		}
		if (obj instanceof Long) {
			return new Time((Long) obj);
		}
		if (obj instanceof Integer) {
			// 00-235959
			int t = ((Integer) obj).intValue();
			if (t <= 235959) {
				int s = t % 100;
				t = t / 100;
				int m = t % 100;
				int h = t / 100;
				LocalDateTime ldt = LocalDateTime.of(0, 0, 0, h, m, s);
				return Time.valueOf(ldt.toLocalTime());
			}

			return new Time(new Date((long)t * 1000L).getTime());
		}
		if (obj instanceof String) {
			return Time.valueOf((String) obj);
		}
		return null;
	}

	public static String toSQLBit(Object b) {
		if (b == null) {
			return null;
		}
		if (b instanceof Number) {
			return ((Number) b).intValue() == 0 ? "0" : "1";
		}
		if (b instanceof Boolean) {
			return ((Boolean) b).booleanValue() ? "0" : "1";
		}
		String v = b.toString();
		if (v.length() == 0)
			return null;
		char c = v.charAt(0);
		if (c == '0' || c == 'f' || c == 'F' || c == 'n' || c == 'N') {
			return "0";
		}

		if (c == '1' || c == 'T' || c == 't' || c == 'Y' || c == 'y') {
			return "1";
		}
		return null;
	}

	/**
	 * 将一个对象转为sql对应的类型对象
	 * 
	 * @param type
	 *            java.sql.Types.* 定义的类型
	 * @param b
	 *            源对象
	 * @return 返回结果，如果不能转换，返回为NULL
	 */
	public static Object toSQLType(int type, Object b) {
		if (b == null) {
			return null;
		}

		switch (type) {
		case BIGINT:
			if (b instanceof Long || b instanceof BigInteger) {
				return b;
			}
			return toBigInteger(b);
		case BIT:
			if (b instanceof Byte)
				return b;
			return toBit(b);
		case CHAR:
		case VARCHAR:
		case LONGNVARCHAR:
		case LONGVARCHAR:
		case NCHAR:
		case NVARCHAR:
			return toString(b);
		case DATE:
			return toSQLDate(b);
		case TIMESTAMP:
			return toTimestamp(b);
		case DECIMAL:
		case NUMERIC:
			return toBigDecimal(b);
		case DOUBLE:
			return toDouble(b);
		case FLOAT:
			return toFloat(b);
		case TIME:
			return toTime(b);
		case REAL:
		case BOOLEAN:
			return toBoolean(b);
		case TINYINT:
			return toByte(b);
		case INTEGER:
			return toInteger(b);
		case SMALLINT:
			return toShort(b);
		}

		return null;
	}
}
