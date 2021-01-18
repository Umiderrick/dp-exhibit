package com.zeusas.dp.exhibit.utils;

public class AuthcUtils {

	public static final String SEC_AUTHUSER = "_AUTH_";
	public static final String SEC_ORGUNIT  = "_ORGU_";

//	static void setSession(Object key, Object value) {
//		Subject subject = SecurityUtils.getSubject();
//		if (subject.isAuthenticated()) {
//			Session session = subject.getSession();
//			session.setAttribute(key, value);
//		}
//	}
//
//	public static AuthUser getCurrentAuthUser() {
//		Subject subject = SecurityUtils.getSubject();
//		if (subject.isAuthenticated()) {
//			Session session = subject.getSession();
//			return (AuthUser) session.getAttribute(SEC_AUTHUSER);
//		}
//		return null;
//	}
//
//	public static OrgUnit getCurrentOrgUnit() {
//		Subject subject = SecurityUtils.getSubject();
//		if (subject.isAuthenticated()) {
//			Session session = subject.getSession();
//			return (OrgUnit) session.getAttribute(SEC_ORGUNIT);
//		}
//		return null;
//	}
}
