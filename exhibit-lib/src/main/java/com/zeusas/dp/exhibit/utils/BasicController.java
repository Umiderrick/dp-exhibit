package com.zeusas.dp.exhibit.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.zeusas.dp.exhibit.entity.AuthUser;
/**
 * 基本控制器
 * 
 * @date 2016年12月18日 下午1:55:58
 */
public class BasicController {
	
	@Autowired
	protected HttpServletRequest request;
	@Autowired
	protected HttpServletResponse response;
	
	public AuthUser getAuthUser(){
		HttpSession sess = request.getSession();
		return (AuthUser)sess.getAttribute(AuthcUtils.SEC_AUTHUSER);
	}
	
//	public  OrgUnit getOrgUnit(){
//		HttpSession sess = request.getSession();
//		return (OrgUnit)sess.getAttribute(SEC_ORGUNIT);
//	}
//	
//	public Set<String> getUserRoles() {
//		AuthUser users = getAuthUser(request);
//		return users == null ? new LinkedHashSet<String>(0) : users.getRoles();
//	}
//	
//	public static AuthUser getAuthUser(HttpServletRequest request){
//		HttpSession sess = request.getSession();
//		return (AuthUser)sess.getAttribute(SEC_AUTHUSER);
//	}
//
//	public static OrgUnit getOrgUnit(HttpServletRequest request){
//		HttpSession sess = request.getSession();
//		return (OrgUnit)sess.getAttribute(SEC_ORGUNIT);
//	}
//	
//	public static Set<String> getUserRoles(HttpServletRequest request) {
//		AuthUser users = getAuthUser(request);
//		return users == null ? new LinkedHashSet<String>(0) : users.getRoles();
//	}
}
