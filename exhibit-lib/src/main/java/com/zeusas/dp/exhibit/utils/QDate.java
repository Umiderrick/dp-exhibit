package com.zeusas.dp.exhibit.utils;

import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.HOUR;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.WEEK_OF_MONTH;
import static java.util.Calendar.WEEK_OF_YEAR;
import static java.util.Calendar.YEAR;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
/**
 * ISO 8601 Year: <br>
 * YYYY (eg 1997)<br>
 * Year and month:<br>
 * YYYY-MM (eg 1997-07)<br>
 * Complete date:<br>
 * YYYY-MM-DD (eg 1997-07-16)<br>
 * Complete date plus hours and minutes:<br>
 * YYYY-MM-DDThh:mmTZD (eg 1997-07-16T19:20+01:00)<br>
 * Complete date plus hours, minutes and seconds:<br>
 * YYYY-MM-DDThh:mm:ssTZD (eg 1997-07-16T19:20:30+01:00)<br>
 * YYYY-MM-DDThh:mm:ss.sTZD (eg 1997-07-16T19:20:30.45+01:00) *
 *
 */
public final class QDate {
	/** 年年月月格式 */
	public static final String YYYYMMDD = "yyyyMMdd";
	public static final String YYMMDD = "yyMMdd";
	/** 生日：月日 */
	public static final String MMDD = "MMdd";
	public static final String YYYYMM = "yyyyMM";
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	/** 生日：月_日 */
	public static final String MM_DD = "MM-dd";
	public static final String YYYY_MM_DD_HMS = "yyyy-MM-dd HH:mm:ss";
	public static final String ISO_DATETIME = "yyyy-MM-dd'T'HH:mm:ss";
	/** 中文格式年月日表示 */
	public static final String YYYY_MM_DD_CN = "yyyy'年'MM'月'dd'日'";
	public static final DateTimeFormatter DTF_YYYYMMDD = DateTimeFormatter.ofPattern(YYYYMMDD);
	public static final DateTimeFormatter DTF_YYMMDD = DateTimeFormatter.ofPattern(YYMMDD);
	public static final DateTimeFormatter DTF_YYYYMM = DateTimeFormatter.ofPattern(YYYYMM);
	public static final DateTimeFormatter DTF_YYYY_MM_DD = DateTimeFormatter.ofPattern(YYYY_MM_DD);
	public static final DateTimeFormatter DTF_YYYY_MM_DD_HMS = DateTimeFormatter.ofPattern(YYYY_MM_DD_HMS);
	public static final DateTimeFormatter DTF_MMDD = DateTimeFormatter.ofPattern(MMDD);
	public static final DateTimeFormatter DTF_MM_DD = DateTimeFormatter.ofPattern(MM_DD);
	
	final static TimeZone LOCAL_TIME_ZONE = TimeZone.getDefault();
	final static int TIME_ZONE_OFFSET = LOCAL_TIME_ZONE.getOffset(0);
	/** 系统时区 */
	final static ZoneId SYSTEM_ZONE_ID = ZoneId.systemDefault();
	final static ZoneOffset ZONE_OFFSET = ZoneOffset.ofHours(TIME_ZONE_OFFSET/1000/60/60);
	
	private QDate() {
	}

	/**
	 * 按指定的格式化串格式日期，并输出日期串。
	 * 
	 * @param format
	 *            格式
	 * @param date
	 *            日期
	 * @return 格式化后的串
	 */
	public static String format(String format, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
		return sdf.format(date);
	}

	public static String format(String format, Long date) {
		return (date == null) ? "" : format(format, new Date(date));
	}

	/**
	 * 格式化日期，如果日期是以小时的整数倍，格式化为yyyy-MM-dd,如果不是小时的整数倍，格式化为带 时间的格式 XXX: 不推荐使用
	 * 
	 * @param date
	 *            日期
	 * @return 格式化后的串
	 */
	public static String format(Date date) {
		if (date == null)
			return null;
		return (date.getTime() % (1000 * 60 * 60) == 0) ? //
				format(YYYY_MM_DD, date) : format(YYYY_MM_DD_HMS, date);
	}

	public static Date asDate(int y, int m, int d, int h, int mm, int ss) {
		LocalDateTime dd = LocalDateTime.of(y, m, d, h, mm, ss);
		return Date.from(dd.atZone(LOCAL_TIME_ZONE.toZoneId()).toInstant());
	}

	public static Date asDate(int y, int m, int d) {
		LocalDate dd = LocalDate.of(y, m, d);
		return Date.from(dd.atStartOfDay().atZone(LOCAL_TIME_ZONE.toZoneId()).toInstant());
	}

	/**
	 * 取得日期对应凌晨的日期，
	 * <p>
	 * 如 2018-01-01 12:02:03 -> 2018-01-01 00:00:00
	 * 
	 * @param date
	 * @return 凌晨的日期
	 */
	public static Date asMorning(Date date) {
		assert date != null;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date.getTime());
		return asDate(cal.get(Calendar.YEAR), //
				cal.get(Calendar.MONTH) + 1, //
				cal.get(Calendar.DATE));
	}

	/**
	 * 取得日期对应午夜的日期，
	 * <p>
	 * 如 2018-01-01 12:02:03 -> 2018-01-01 00:00:00
	 * 
	 * @param date
	 * @return 午夜的日期
	 */
	public static Date asNight(Date date) {
		assert date != null;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date.getTime());
		return asDate(cal.get(Calendar.YEAR), //
				cal.get(Calendar.MONTH) + 1, //
				cal.get(Calendar.DATE), 23, 59, 59);
	}

	/**
	 * 解析一个串按指定的格式，返回时间值。如果失败，返回当前时间.
	 * 
	 * @param fmt
	 *            格式
	 * @param date
	 *            日期时间串 如 2001-01-01或"2001-01-01 00:05"
	 * @return
	 */
	public static Date toDate(String fmt, String date) {
		return toDate(fmt, date, null);
	}

	private final static int QD_LEN[] = { 4, 2, 2, 2, 2, 2, 3 };

	private static void parseDD(String s, int[] dd) {
		int len = s.length();
		int stack = 0;
		int v = 0;
		int vlen = 0;
		for (int i = 0; i < len && stack < dd.length; i++) {
			char ch = s.charAt(i);
			if (ch > '9' || ch < '0') {
				if (vlen > 0) {
					dd[stack] = v;
					stack++;
					v = 0;
					vlen = 0;
				}
			} else {
				if (vlen < QD_LEN[stack]) {
					v = 10 * v + (ch & 0x0f);
					vlen++;
				} else {
					dd[stack] = v;
					stack++;
					vlen = 1;
					v = (ch & 0x0f);
				}
			}
		}

		if (v > 0) {
			dd[stack] = v;
		}
		if (dd[2] == 0)
			dd[2] = 1;
	}

	public static Date toQDate(String s) {
		int dd[] = { 0, 0, 0 };
		parseDD(s, dd);
		return dd[0] == 0 ? null : asDate(dd[0], dd[1], dd[2]);
	}

	public static Date toQDatetime(String s) {
		int dd[] = { 0, 0, 0, 0, 0, 0, 0 };
		parseDD(s, dd);
		return dd[0] == 0 ? null : asDate(dd[0], dd[1], dd[2], dd[3], dd[4], dd[5]);
	}

	/**
	 * <b>按指定格式解析时间</b>
	 * <p>
	 * 
	 * 解析一个串按指定的格式，返回时间值。如果失败，返回为职.
	 * 
	 * @param fmt
	 *            格式
	 * @param date
	 *            日期时间串 如 2001-01-01或"2001-01-01 00:05"
	 * @return
	 */
	public static Date toDate(String fmt, String date, Date defDate) {
		// 为空的情况下，返回默认值
		if (date == null || date.length() < 0) {
			return defDate;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(fmt, Locale.ENGLISH);
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			if (date.indexOf('年') > 0) {
				return toDate(YYYY_MM_DD_CN, date, defDate);
			}
		}
		return defDate;
	}

	/**
	 * 判断该年份是不是闰年
	 * 
	 * @param year
	 * @return
	 */
	public static boolean isLeapYear(int year) {
		return ((year % 100 == 0) //
				&& (year % 400 == 0) //
				|| (year % 100 != 0) && (year % 4 == 0));
	}

	/**
	 * 取得两个时间相差的天数
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public static int getDateDiff(Date from, Date to) {
		long t1 = from.getTime();
		long t2 = to.getTime();
		long d1 = (t1 + TIME_ZONE_OFFSET) / (1000L * 60 * 60 * 24);
		long d2 = (t2 + TIME_ZONE_OFFSET) / (1000L * 60 * 60 * 24);
		return (int) (d2 - d1);
	}

	public static int getMonthDiff(Date from, Date to) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(from);
		int yy1 = cal1.get(YEAR);
		int mm1 = cal1.get(MONTH);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(to);
		int yy2 = cal2.get(YEAR);
		int mm2 = cal2.get(MONTH);

		return 12 * (yy2 - yy1) + mm2 - mm1;
	}

	/**
	 * 计算指定日期增加几年或减少几年对应的日期。
	 * 
	 * @param date
	 *            日期函数
	 * @param n
	 *            年数
	 * @return 计算结果日期
	 */
	public static Date addYear(Date date, int n) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date.getTime());

		int year = cal.get(YEAR);
		int day = cal.get(DATE);

		cal.set(DATE, 1);
		cal.set(YEAR, year + n);

		int lastDate = cal.getActualMaximum(DATE);
		cal.set(DATE, (lastDate < day) ? lastDate : day);
		return cal.getTime();
	}

	public static Date addMonth(Date date, int n) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date.getTime());

		int month = cal.get(MONTH);
		int day = cal.get(DATE);

		cal.set(DATE, 1);
		cal.set(MONTH, month + n);
		int lastDate = cal.getActualMaximum(DATE);
		cal.set(DATE, (lastDate < day) ? lastDate : day);
		return cal.getTime();
	}

	/**
	 * 对当前日期增加或减少几天的日期。参数正为向后的天数，负数为前多少天。
	 * 
	 * @param date
	 *            操作日期
	 * @param n
	 *            加减操作天数，可以为负数。
	 * @return 加/减后的日期
	 */
	public static Date addDay(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}

	/**
	 * 将一个对象转为日期形式。
	 * 
	 * @param obj
	 * @param defltV
	 *            默认值
	 * @return 转换结果日期，如果不能转，返回默认值
	 */
	public static Date toDate(Object obj, Date defltV) {
		Date v = toDate(obj);
		return v == null ? defltV : v;
	}

	/**
	 * 将一个未知的对象转化为日期格式。 本方法仅限于Action中Bean对应的日期处理。
	 * 
	 * @param obj
	 *            需转为日期的对象，对象可能是：串、日期、长整形数组等
	 * @param defltV
	 *            默认日期
	 * @return 解析后的日期，如果失败，反回默认日期
	 */
	public static Date toDate(Object obj) {
		// 1. 如果是空的情况
		if (obj == null) {
			return null;
		}
		String param = null;
		if ((obj instanceof String[]) //
				&& Array.getLength(obj) >= 1) {
			// 如果是数组，取第一个
			param = (String) Array.get(obj, 0);
		} else if (obj instanceof CharSequence) {
			// 如果是串？
			param = obj.toString();
		} else if (obj instanceof java.util.Date) {
			// 如果是日期，返回
			return (Date) obj;
		} else if (obj instanceof java.sql.Date) {
			long tm = ((java.sql.Date) obj).getTime();
			return new Date(tm);
		} else if (obj instanceof java.sql.Timestamp) {
			long tm = ((java.sql.Timestamp) obj).getTime();
			return new Date(tm);
		} else if (obj instanceof Long) {
			// 如果是长型:YMDHHMMSS?
			return new Date(((Long) obj).longValue());
		} else if (obj instanceof Object[]) {
			Object dataObj = Array.get(obj, 0);
			if (dataObj == null)
				return null;
			return toDate(dataObj, null);
		} else {
			param = obj.toString();
		}
		// 如果长度小于8，不做处理
		if (param == null || param.length() < 8) {
			return null;
		}
		// 2012-01-01 00:00:00.12
		Date val = null;
		if (param.charAt(4) == '-' //
				|| param.startsWith("20")//
				|| param.startsWith("19")) {
			val = param.length()<=YYYY_MM_DD.length()?toQDate(param):toQDatetime(param);
		}
		if (val != null) {
			return val;
		}
		if (param.charAt(4) == '年') {
			return toDate(YYYY_MM_DD_CN, param, null);
		}

		if (QString.isNumeric(param)) {
			if (param.length() == 8) {
				return toDate(YYYYMMDD, param, null);
			}
			long tm = QString.toLong(param, 0);
			return tm == 0 ? null : new Date(tm);
		}
		return null;
	}

	public static void waitMillis(long tm) {
		try {
			Thread.sleep(tm);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 根据输入数据，检查是否在当前周期。
	 * 
	 * @param period
	 *            Calendar.Date...
	 * @param millis
	 *            时间
	 * @return
	 */
	public static boolean checkPeriod(int period, long millis) {
		Calendar cal0 = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		cal1.setTimeInMillis(millis);

		long v0 = cal0.getTimeInMillis() / 1000;
		long v1 = millis / 1000;
		switch (period) {
		case YEAR:
			v0 = cal0.get(YEAR);
			v1 = cal1.get(YEAR);
			break;
		case MONTH:
			v0 = cal0.get(YEAR) * 100 + cal0.get(MONTH);
			v1 = cal1.get(YEAR) * 100 + cal1.get(MONTH);
			break;
		case WEEK_OF_MONTH:
			v0 = cal0.get(YEAR) * 10000 + cal0.get(MONTH) * 100 + cal0.get(WEEK_OF_MONTH);
			v1 = cal1.get(YEAR) * 10000 + cal1.get(MONTH) * 100 + cal1.get(WEEK_OF_MONTH);
			break;
		case WEEK_OF_YEAR:
			v0 = cal0.get(YEAR) * 100 + cal0.get(WEEK_OF_YEAR);
			v1 = cal1.get(YEAR) * 100 + cal1.get(WEEK_OF_YEAR);
			break;
		case DATE:
			v0 = cal0.get(YEAR) * 10000 + cal0.get(MONTH) * 100 + cal0.get(DATE);
			v1 = cal1.get(YEAR) * 10000 + cal1.get(MONTH) * 100 + cal1.get(DATE);
			break;
		case DAY_OF_WEEK:
			v0 = cal0.get(YEAR) * 10000 + cal0.get(WEEK_OF_YEAR) * 100
					+ cal0.get(DAY_OF_WEEK);
			v1 = cal1.get(YEAR) * 10000 + cal1.get(WEEK_OF_YEAR) * 100
					+ cal1.get(DAY_OF_WEEK);
			break;
		case HOUR_OF_DAY:
		case HOUR:
			v0 = cal0.getTimeInMillis() / 1000 / 60 / 60;
			v1 = cal1.getTimeInMillis() / 1000 / 60 / 60;
			break;
		case MINUTE:
			v0 = cal0.getTimeInMillis() / 1000 / 60;
			v1 = cal1.getTimeInMillis() / 1000 / 60;
			break;
		default:
			; // NOP
		}
		return v0 == v1;
	}

	/**
	 * 取得现有时间离批定时间的差值。计算当前时间点在期间中的时刻
	 * 
	 * @param period
	 *            计算周期（Calendar.DATE,...)
	 * @return
	 */
	public static long getNextPeriod(int period) {
		long now = System.currentTimeMillis();

		Calendar nex = Calendar.getInstance();
		nex.add(period, 1);
		nex.set(Calendar.SECOND, 0);
		nex.set(Calendar.MILLISECOND, 0);
		switch (period) {
		case Calendar.HOUR:
			nex.set(Calendar.MINUTE, 0);
			break;
		case Calendar.DATE:
			nex.set(Calendar.MINUTE, 0);
			nex.set(Calendar.HOUR_OF_DAY, 0);
			break;
		case Calendar.WEEK_OF_YEAR:
			nex.set(Calendar.MINUTE, 0);
			nex.set(Calendar.HOUR, 0);
			nex.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			break;
		case Calendar.MONTH:
			nex.set(Calendar.MINUTE, 0);
			nex.set(Calendar.HOUR, 0);
			nex.set(Calendar.DAY_OF_MONTH, 1);
			break;
		case Calendar.YEAR:
			nex.set(Calendar.MINUTE, 0);
			nex.set(Calendar.HOUR, 0);
			nex.set(Calendar.DAY_OF_MONTH, 1);
			nex.set(Calendar.MONTH, 0);
			break;
		default:
			;
		}
		return nex.getTimeInMillis() - now;
	}

	public static Date toDate(LocalDateTime time) {
		ZonedDateTime tm = time.atZone(SYSTEM_ZONE_ID);
		return Date.from(tm.toInstant());
	}
	
	public static Date toDate(LocalDate time) {
		ZonedDateTime tm = time.atStartOfDay(SYSTEM_ZONE_ID);
		return Date.from(tm.toInstant());
	}
	
	public static LocalDateTime toLocalDateTime(Date time) {
		return LocalDateTime.ofInstant(time.toInstant(), SYSTEM_ZONE_ID);
	}
	
	public static LocalDateTime toLocalDateTime(long time) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), SYSTEM_ZONE_ID);
	}
	
	public static LocalDate toLocalDate(Date time) {
		return LocalDateTime.ofInstant(time.toInstant(), SYSTEM_ZONE_ID).toLocalDate();
	}
	
	public static LocalDate toLocalDate(long time) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), SYSTEM_ZONE_ID).toLocalDate();
	}
	
	public static long toTimeInMillis(LocalDateTime time) {
		return time.toInstant(ZONE_OFFSET).toEpochMilli();
	}
	
	public static long toTimeInMillis(LocalDate time) {
		return toTimeInMillis(time.atStartOfDay());
	}

	/**
	 * 将一个整数转换为日期。
	 * 
	 * @param k
	 * @return
	 */
	public static Date toDate(final Integer ymd) {
		if (ymd == null) {
			return null;
		}
		int t = ymd.intValue();
		int dd = t % 100;
		t /= 100;
		int mm = t % 100;
		int yy = t / 100;
		// 检查是否是合法日期
		if (yy < 1900 || yy > 3000 //
				|| mm > 12 || mm == 0 //
				|| dd == 0 || dd > 31) {
			return new Date(ymd * 1000L);
		}
		return asDate(yy, mm, dd);
	}

	/**
	 * 转换成YMD的整型
	 * 
	 * @param date
	 * @return
	 */
	public static Integer toYMD(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return Integer.valueOf(cal.get(Calendar.YEAR) * 10000 //
				+ (cal.get(Calendar.MONTH) + 1) * 100//
				+ cal.get(Calendar.DATE));
	}

	public static Integer toYMD(LocalDate d) {
		return (d.getYear() * 100 + d.getMonthValue()) * 100 + d.getDayOfMonth();
	}

	public static Integer toYMD(LocalDateTime d) {
		return (d.getYear() * 100 + d.getMonthValue()) * 100 + d.getDayOfMonth();
	}
	
	public static boolean contains(Date now, Date from, Date to) {
		int d0 = toYMD(now);
		int d1 = toYMD(from);
		int d2 = toYMD(to);

		return d0 >= d1 && d0 <= d2;
	}
}
