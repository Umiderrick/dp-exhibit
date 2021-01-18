/**
 * 
 */
package com.zeusas.dp.exhibit.utils;

import java.util.Calendar;

/**
 * @author pengbo
 *
 */
public class DateUtil {


	/**
	 * 
	 */
	public static String getLastMonth() {
		Calendar today =Calendar.getInstance();
		int m;
		int y;
		int d = today.get(Calendar.DAY_OF_MONTH);
		if(d<=25){
			 m =today.get(Calendar.MONTH);
		}else{
			 m =today.get(Calendar.MONTH)+1;
		}
		y=today.get(Calendar.YEAR);
		StringBuilder fm =new StringBuilder();
		if(m<10){
			fm.append("0").append(m);
		}else{
			fm.append(m);
		}
		return y+"-"+fm.toString();
		
	}
	/**
	 * 获取财政月
	 *
	 */
	public static String getFiscalMonth() {
		Calendar cal = Calendar.getInstance();
		return getFormatString(cal);
	}

	private static String getFormatString(Calendar cal) {
		StringBuilder fm =new StringBuilder();
		int i = cal.get(Calendar.MONTH);
		if(i<9){
			fm.append("0").append(cal.get(Calendar.MONTH)+1);
		}else{
			fm.append(cal.get(Calendar.MONTH)+1);
		}
		return cal.get(Calendar.YEAR) +"-"+fm.toString();
	}

	/**
	 * 获取财政月
	 *
	 */
	public static String getFiscalMonth2() {
		Calendar cal = Calendar.getInstance();
		int dayOfMonth = cal.get(Calendar.DATE);
		if (dayOfMonth > 18) {
			cal.add(Calendar.MONTH, +1);
		}
		return getFormatString(cal);
	}
}
