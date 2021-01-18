package com.zeusas.dp.exhibit.utils;

import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;


import com.google.common.base.Splitter;
import com.google.common.base.Strings;

public final class QString {
	public final static String[] EMPTYS = new String[0];
	public final static byte EMPTY_ARRAY[] = new byte[0];
	public final static  Charset GBK = Charset.forName("GBK");
	public final static  Charset UTF8 = Charset.forName("UTF-8");

	final static Map<String, String> ids = new ConcurrentHashMap<>();
	
	private QString() {
	}

	/**
	 * 动态缓存字符串
	 * 
	 * @param s
	 * @return
	 */
	public static String intern(String s) {
		if (s == null) {
			return null;
		}
		String v = ids.get(s);
		if (v == null) {
			ids.put(s, s);
			v = s;
		}
		return v;
	}
	
	public static String nullToEmpty(Object s) {
		if (s == null) {
			return "";
		}

		if (s instanceof CharSequence) {
			return (s.toString()).trim();
		}

		if (s instanceof Date) {
			Date d = (Date) s;
			long t = d.getTime();
			// 如果精确到小时，认为是YMD
			return (t % (1000 * 60 * 60) == 0) ? QDate.format(QDate.YYYY_MM_DD, d) : //
					QDate.format(QDate.YYYY_MM_DD_HMS, d);
		}

		if (s instanceof Double //
				|| s instanceof Float) {
			DecimalFormat decimalFormat = new DecimalFormat("#0.00");
			decimalFormat.format(s);
		}

		return s.toString();
	}

	public static String trim(String s) {
		return s == null ? "" : s.trim();
	}

	public static boolean isNullOrEmpty(Object object) {
		return QBeanUtil.isEmpty(object);
	}

	public static boolean isEmpty(CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	public static boolean contains(String s, String d) {
		if (Strings.isNullOrEmpty(d))
			return true;
		if (Strings.isNullOrEmpty(s))
			return false;
		return s.contains(d);
	}

	/**
	 * 装一个串转为整数
	 * 
	 * @param s
	 *            字符串
	 * @return 整数 或 NULL
	 */
	public static Integer toInt(CharSequence s) {
		if (s == null || s.length() == 0) {
			return null;
		}

		int v = 0;

		byte b[] = preprocInt(s);
		if (b == null) {
			return null;
		}

		for (int i = 1; i < b.length; i++) {
			v = v * 10 + b[i];
		}

		return Integer.valueOf((b[0] == -1) ? -v : v);
	}

	public static int toInt(CharSequence s, int defaultV) {
		if (s == null || s.length() == 0) {
			return defaultV;
		}

		int v = 0;

		byte b[] = preprocInt(s);
		if (b == null) {
			return defaultV;
		}

		for (int i = 1; i < b.length; i++) {
			v = v * 10 + b[i];
		}
		return (b[0] == -1) ? -v : v;
	}

	public static Byte toByte(String s) {
		if (s == null || (s = s.trim()).isEmpty()) {
			return null;
		}
		byte v = 0;

		byte b[] = preprocInt(s);
		if (b == null) {
			return null;
		}

		for (int i = 1; i < b.length; i++) {
			v = (byte) (v * 10 + b[i]);
		}

		return Byte.valueOf((byte) ((b[0] == -1) ? -v : v));
	}

	public static byte toByte(String s, byte val) {
		if (s == null || (s = s.trim()).isEmpty()) {
			return val;
		}
		byte v = 0;

		byte b[] = preprocInt(s);
		if (b == null) {
			return val;
		}

		for (int i = 1; i < b.length; i++) {
			v = (byte) (v * 10 + b[i]);
		}

		return ((byte) ((b[0] == -1) ? -v : v));
	}
	
	public static short toShort(CharSequence s, short defV) {
		if (s == null || (s.length() == 0)) {
			return defV;
		}

		byte b[] = preprocInt(s);
		if (b == null) {
			return defV;
		}
		short v = 0;

		for (int i = 1; i < b.length; i++) {
			v = (short) (v * 10 + b[i]);
		}

		return (byte) (b[0] == -1 ? -v : v);
	}

	public static Short toShort(CharSequence s) {
		if (s == null || (s.length() == 0)) {
			return null;
		}

		byte b[] = preprocInt(s);
		if (b == null) {
			return null;
		}
		short v = 0;

		for (int i = 1; i < b.length; i++) {
			v = (short) (v * 10 + b[i]);
		}

		return Short.valueOf((short) ((b[0] == -1) ? -v : v));
	}

	public static Float toFloat(String s) {
		if (s == null || (s = s.trim()).length() == 0)
			return null;
		try {
			return Float.parseFloat(s);
		} catch (Exception e) {
			// NOP
		}
		return null;
	}

	public static boolean toBoolean(String s) {
		if (s == null || (s = s.trim()).isEmpty()) {
			return false;
		}
		return ("true".equalsIgnoreCase(s)) //
				|| "yes".equalsIgnoreCase(s)//
				|| "y".equalsIgnoreCase(s) //
				|| "t".equalsIgnoreCase(s) //
				|| "1".equals(s);
	}

	public static Double toDouble(String s) {
		if (s == null || (s = s.trim()).length() == 0)
			return null;
		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
			// NOP
		}
		return null;
	}

	public static double toDouble(String s, double def) {
		if (s == null || (s = s.trim()).length() == 0)
			return def;
		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
			// NOP
		}
		return def;
	}

	public static Long toLong(CharSequence s) {
		if (s == null) {
			return null;
		}

		byte b[] = preprocInt(s);
		if (b == null) {
			return null;
		}

		long v = 0;

		for (int i = 1; i < b.length; i++) {
			v = v * 10L + b[i];
		}

		return Long.valueOf((b[0] == -1) ? -v : v);
	}

	public static long toLong(CharSequence s, long def) {
		if (s == null) {
			return def;
		}

		byte b[] = preprocInt(s);
		if (b == null) {
			return def;
		}

		long v = 0;

		for (int i = 1; i < b.length; i++) {
			v = v * 10L + b[i];
		}

		return ((b[0] == -1) ? -v : v);
	}

	public static String[] split(String src, String regex) {
		if (src == null //
				|| (src = src.trim()).isEmpty()) {
			return new String[0];
		}
		return Splitter.onPattern(regex)//
				.omitEmptyStrings()//
				.trimResults()//
				.splitToList(src)//
				.toArray(EMPTYS);
	}

	/**
	 * 使用空格，:，|，;, 分隔为一个字符串数组
	 * 
	 * @param src
	 * @return
	 */
	public static String[] split(String src) {
		return split(src, "\\:|\\||\\,|\\s+");
	}

	public static boolean isNumeric(CharSequence charseq) {
		if (charseq == null || charseq.length() == 0) {
			return false;
		}

		for (int i = 0; i < charseq.length(); i++) {
			char c = charseq.charAt(i);
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}

	/***************************************************************************
	 * 判断是否是中文字符
	 * 
	 * @version: July 13, 2012 11:05:58 AM
	 * @param c
	 * @return
	 */
	public static final boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		return (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS //
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS //
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A //
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION //
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION //
		|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS);
	}

	/**
	 * 判断是否是中文
	 * 
	 * @version: July 13, 2012 11:05:58 AM
	 * @param strName
	 * @return
	 */
	public static final boolean isChinese(String strName) {
		for (int i = 0; i < strName.length(); i++) {
			if (isChinese(strName.charAt(i)))
				return true;
		}
		return false;
	}

	/**
	 * 将串分拆，组合成Regular表达式。
	 * 
	 * @param target
	 * @return 正则表达式
	 */
	public static String toRegexpr(String target) {
		StringBuilder b = new StringBuilder(target.length());
		for (int i = 0; i < target.length(); i++) {
			char c = target.charAt(i);
			if (c == '\\' || c == '/' //
					|| c == ',' //
					|| c == ';' //
					|| c == '+' //
					|| c == '*') {
				b.append(' ');
			} else if (c == '.') {
				b.append("\\.");
			} else {
				b.append(c);
			}
		}
		String[] vv = b.toString().split("\\s+");
		b.setLength(0);
		for (String v : vv) {
			if (v.length() > 0)
				b.append('|').append(v);
		}
		return b.length() > 0 ? b.substring(1) : "";
	}

	/**
	 * 匹配任何一个的判断
	 * 
	 * @param src
	 * @param expr
	 * @return
	 */
	public static boolean matchAny(String src, Pattern expr) {
		return expr.matcher(src).find();
	}

	/**
	 * 最多分3
	 * 
	 * @param src
	 * @param ww
	 * @return
	 */
	public static boolean matchAll(String src, String... ww) {
		boolean b = true;
		switch (ww.length) {
		case 0:
			break;
		case 1:
			b = src.indexOf(ww[0]) > -1;
			break;
		case 2:
			b = src.indexOf(ww[0]) > -1 && src.indexOf(ww[1]) > -1;
			break;
		default:
			b = src.indexOf(ww[0]) > -1 && src.indexOf(ww[1]) > -1 && src.indexOf(ww[2]) > -1;
		}
		return b;
	}

	static byte[] preprocInt(CharSequence s) {
		int len;
		if (s == null || (len = s.length()) == 0) {
			return null;
		}
		byte[] b = new byte[20];

		int p = 1;
		complete: for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			switch (c) {
			case '+':
				if (b[0] != 0) {
					break complete;
				}
				b[0] = 1;
				break;
			case '-':
				if (b[0] != 0) {
					break complete;
				}
				b[0] = -1;
				break;
			case ' ':
				if (p > 1) {
					break complete;
				}
				break;
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				b[p++] = (byte) (0x0F & c);
				break;
			default:
				break complete;
			}
		}
		if (p == 1) {
			return null;
		}
		return Arrays.copyOf(b, p);
	}

	/**
	 * 自由格式化。
	 * 
	 * @param temp "A{} b{} c{}"
	 * @param args arguments
	 * @return formatted string
	 * @since 2.0
	 * @deprecated
	 */
	public static String log(String temp, Object... args) {
		if (args == null || args.length < 2) {
			return temp;
		}
		int idx = 0;
		StringBuilder b = new StringBuilder(temp.length() + 16);
		int i = 0;
		do_loop:
		while (i < temp.length() - 1) {
			char c = temp.charAt(i);
			if (c == '{') {
				do {
					if (idx < args.length) {
						if (temp.charAt(i + 1) == '}') {
							Object obj = args[idx++];
							if (obj instanceof Date) {
								b.append(QDate.format((Date) obj));
							} else {
								b.append(obj);
							}
							i += 2;
							continue do_loop;
						} else if (temp.charAt(i + 1) == ' ') {
							i++;
						} else {
							break;
						}
					}
				} while (i < temp.length());
			}
			b.append(c);
			i++;
		}
		if (i < temp.length()) {
			b.append(temp.charAt(i));
		}
		for (i = idx; i < args.length; i++) {
			b.append(',').append(args[i]);
		}
		return b.toString();
	}

	public static String toString(byte[] bb) {
		if (bb == null) {
			return null;
		}
		if (bb.length == 0) {
			return "";
		}
		return new String(bb, UTF8);
	}

	public static byte[] toBytes(CharSequence cs) {
		String s = String.valueOf(cs);
		return s.getBytes(UTF8);
	}

	public static byte[] toBytes(CharSequence cs, Charset charSet) {
		String s = String.valueOf(cs);
		return s.getBytes(charSet);
	}
	
	public static Object[] buildArgs(Object... objs) {
		Object[] args;
		// 取得最后一个对象
		Object arrObj = objs[objs.length - 1];
		// 如果为空时，去掉空对象返回
		if (arrObj == null) {
			args = new Object[objs.length - 1];
			System.arraycopy(objs, 0, args, 0, objs.length - 1);
			return args;
		}
		// 如果不是数组
		if (!arrObj.getClass().isArray()) {
			return objs;
		}
		
		int len = Array.getLength(arrObj);
		args = new Object[len + objs.length - 1];
		System.arraycopy(objs, 0, args, 0, objs.length - 1);
		// 如果不为0，添加到尾部
		if (len != 0) {
			System.arraycopy(arrObj, 0, args, objs.length - 1, len);
		}
		return args;
	}
	
	public static String format(String pattern, Object ...args) {
		Object arguments[] = buildArgs(args);
		return MessageFormat.format(pattern, arguments);
	}
}
