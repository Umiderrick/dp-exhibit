package com.zeusas.dp.exhibit.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class HttpUtil {
	
	final static String UNKNOW = "unknown";

	private HttpUtil() {
	}

	public static boolean isEmpty(String s) {
		return s == null //
				|| (s = s.trim()).length() == 0 //
				|| "null".equals(s)//
				|| "NaN".equalsIgnoreCase(s) //
				|| "undefined".equalsIgnoreCase(s);
	}

	/**
	 * 取得IP
	 * 
	 * @param request
	 * @return
	 */
	public static String getRemoteAddr(HttpServletRequest request) {
		// X-Forwarded-For 和 X-Real-IP 的区别？
		String ip = request.getHeader("X-Real-IP");
		if (QString.isEmpty(ip) || UNKNOW.equals(ip)) {
			ip = request.getHeader("X-Forwarded-For");
			if (QString.isEmpty(ip) || UNKNOW.equals(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
				if (QString.isEmpty(ip) || UNKNOW.equals(ip)) {
					ip = request.getHeader("Proxy-Client-IP");
				}
			}
		}
		// 如果没有在头中找到IP，可以直接取得remote address
		if (QString.isEmpty(ip) || UNKNOW.equals(ip)) {
			return request.getRemoteAddr();
		}
		
		int index = ip.indexOf(",");
		// 多级代理的情况
		if (index != -1) {
			return ip.substring(0, index);
		}

		return ip;
	}

	/**
	 * Remove all attributes from Session. WARN: DON'T invoke this function to
	 * remove attributes.
	 * 
	 * @param sess
	 */
	public static void removeAllAttr(HttpSession sess) {
		List<String> keys = new ArrayList<String>();
		Enumeration<String> enu = sess.getAttributeNames();
		while (enu.hasMoreElements()) {
			String nm = enu.nextElement();
			keys.add(nm);
		}
		for (String key : keys) {
			sess.removeAttribute(key);
		}
	}

	public static String getString(HttpServletRequest req, String param) {
		String s = req.getParameter(param);
		return isEmpty(s) ? "" : s;
	}

	public static Integer getInteger(HttpServletRequest req, String param) {
		String s = req.getParameter(param);
		return QString.toInt(s);
	}

	public static Long getLong(HttpServletRequest req, String param) {
		String s = req.getParameter(param);
		return QString.toLong(s);
	}

	public static long getLong(HttpServletRequest req, String param,
			long defaultValue) {
		Long v = getLong(req, param);
		return v == null ? defaultValue : v.longValue();
	}

	public static Double getDouble(HttpServletRequest req, String param) {
		String s = req.getParameter(param);
		return QString.toDouble(s);
	}

	public static double getDouble(HttpServletRequest req, String param,
			double defaultValue) {
		Double v = getDouble(req, param);
		return v == null ? defaultValue : v.doubleValue();
	}

	public static Date getDate(HttpServletRequest req, String param) {
		String s = req.getParameter(param);
		if (isEmpty(s)) {
			return null;
		}
		// s: yyyyMMdd yyyy-MM-dd timeInMillis
		if (QString.isNumeric(s)) {
			if (s.length() == 8) {
				return QDate.toQDate(s);
			}
			Long tm = QString.toLong(s);
			return tm == null ? null : new Date(tm.longValue());
		}

		return QDate.toQDate(s);
	}

	public static Date getDate(HttpServletRequest req, String param,
			Date defaultVal) {
		Date v = getDate(req, param);
		return v == null ? defaultVal : v;
	}
}
