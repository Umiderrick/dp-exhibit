package com.zeusas.dp.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.zeusas.dp.exhibit.entity.AuthUser;
import com.zeusas.dp.exhibit.entity.UserDetail;

public class UserBean {


	private String uid;
	private String loginName;
	private String password;
	private String commonName;
	private String orgUnit;
	private String orgUnitName;
	private Set<String> roles;
	private String mobile;
	private String customerType;
	private String status;
	private String lastUpdate;
	private Long createTime;
	
	public UserBean() {
	}

	public UserBean(AuthUser authUser) {
		this.uid = authUser.getUid() == null ? "" : authUser.getUid();
		this.loginName = authUser.getLoginName() == null ? "" : authUser.getLoginName();
		this.password = authUser.getPassword() == null ? "" : authUser.getPassword();
		this.commonName = authUser.getCommonName() == null ? "" : authUser.getCommonName();
		this.orgUnit = authUser.getOrgUnit() == null ? "" : authUser.getOrgUnit().toString();
		this.mobile = "";
		this.customerType = "";
		this.roles = authUser.getRoles();
		this.status = AuthUser.user_enable==authUser.getStatus().intValue() ? "启用" : "禁用";
		if (authUser.getLastUpdate() != null) {
			Date dt = new Date(authUser.getLastUpdate());
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			this.lastUpdate = dateFormat.format(dt);
		} else {
			this.lastUpdate = "";
		}
		this.createTime = authUser.getCreateTime();
	}

	
	public UserBean(AuthUser authUser, UserDetail userDetail) {
		this.uid = authUser.getUid() == null ? "" : authUser.getUid();
		this.loginName = authUser.getLoginName() == null ? "" : authUser.getLoginName();
		this.password = authUser.getPassword() == null ? "" : authUser.getPassword();
		this.commonName = authUser.getCommonName() == null ? "" : authUser.getCommonName();
		this.orgUnit = authUser.getOrgUnit() == null ? "" : authUser.getOrgUnit().toString();
		this.mobile = userDetail.getMobile() == null ? "" : userDetail.getMobile();
		this.customerType = userDetail.getCustomerType() == null ? "" : userDetail.getCustomerType();
		this.roles = authUser.getRoles();
		this.status = AuthUser.user_enable==authUser.getStatus().intValue() ? "启用" : "禁用";
		if (authUser.getLastUpdate() != null) {
			Date dt = new Date(authUser.getLastUpdate());
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			this.lastUpdate = dateFormat.format(dt);
		} else {
			this.lastUpdate = "";
		}
		this.createTime = authUser.getCreateTime();
	}

	public String getUid() {
		return uid == null ? "" : uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getLoginName() {
		return loginName == null ? "" : loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getCommonName() {
		return commonName == null ? "" : commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String getOrgUnit() {
		return orgUnit == null ? "" : orgUnit;
	}

	public void setOrgUnit(String orgUnit) {
		this.orgUnit = orgUnit;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public String getMobile() {
		return mobile == null ? "" : mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getStatus() {
		return status == null ? "" : status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLastUpdate() {
		return lastUpdate == null ? "" : lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public String getOrgUnitName() {
		return orgUnitName;
	}

	public void setOrgUnitName(String orgUnitName) {
		this.orgUnitName = orgUnitName;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}
