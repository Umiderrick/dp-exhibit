/**
 * 
 */
package com.zeusas.dp.exhibit.utils;

import java.util.ResourceBundle;

/**
 * @author pengbo
 *
 */
public final class RoleResources {
	/** 前台禁止登陆角色 */
	public final static String FOREGROUND = "foreground";
	/** 后台禁止登陆角色 */
	public final static String BACKGROUND = "background";

	private static ResourceBundle role_resource = ResourceBundle.getBundle("RoleResources");

	public static String get(String key) {
		return role_resource.getString(key);
	}
}
