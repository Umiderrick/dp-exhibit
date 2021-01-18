package com.zeusas.dp.exhibit.entity;

import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * 授权认证用户实体类 
 *
 */
@Entity
@Table(name = "core_authuser")
public class AuthUser {
	
	public static int user_enable =1;

	/** serialVersionUID. */
	/** 用户ID */
	@Id
	@Column(name = "UID", nullable = false)
	protected String uid;
	
	/** Login Name */
	@Column(name = "LOGINNAME", nullable = false, unique = true)
	protected String loginName;
	
	@Column(name = "COMMONNAME")
	protected String commonName;
	
	@Column(name = "PASSWORD")
	protected String password;
	
	/** 所属机构ID(获取客户名抓取柜台? 通过组织架构获取子柜台) */
	@Column(name = "ORGUNIT")
	protected Integer orgUnit;
	//所属分组ID(直发未启用)
	@Column(name = "GROUPID")
	protected Integer groupId;
	
	@Column(name = "ROLES")
	@Type(type = "com.zeusas.dp.exhibit.entity.StringSetType")
	private Set<String> roles;
	
	@Column(name = "GRANTSTART")
	private Long grantStart;
	@Column(name = "GRANTEND")
	private Long grantEnd;
	@Column(name = "CREATETIME")
	private Long createTime;
	@Column(name = "EXPIRETIME")
	private Long expireTime;
	@Column(name = "MACADDR")
	private String macAddr;
	@Column(name = "STATUS")
	private Integer status;
	/**
	 * 用户类型 表示测试和正式
	 */
	@Column(name = "type")
	private Integer type;
	
	@Column(name = "LASTUPDATE")
	private Long lastUpdate;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid == null ? null : uid.intern();
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Long expireTime) {
		this.expireTime = expireTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getGrantStart() {
		return grantStart;
	}

	public void setGrantStart(Long grantStart) {
		this.grantStart = grantStart;
	}

	public Long getGrantEnd() {
		return grantEnd;
	}

	public void setGrantEnd(Long grantEnd) {
		this.grantEnd = grantEnd;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName == null ? null : commonName.intern();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getOrgUnit() {
		return orgUnit;
	}

	public void setOrgUnit(Integer orgUnit) {
		this.orgUnit = orgUnit;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	
	
	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public void addRole(String role) {
		Set<String> v = getRoles();
		v.add(role);
		this.roles = v;
	}

	
	public Long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	public String getMacAddr() {
		return macAddr;
	}

	public void setMacAddr(String macAddr) {
		this.macAddr = macAddr;
	}

	public int hashCode() {
		return uid == null ? 0 : uid.hashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof AuthUser)) {
			return false;
		}
		AuthUser u = (AuthUser) obj;
		return Objects.equals(this.uid, u.uid) //
				&& Objects.equals(this.roles, u.roles) //
				&& Objects.equals(this.status, u.status) //
				&& Objects.equals(this.password, u.password) //
				&& Objects.equals(this.orgUnit, u.orgUnit) //
				&& Objects.equals(this.loginName, u.loginName) //
				&& Objects.equals(this.groupId, u.groupId) //
				&& Objects.equals(this.commonName, u.commonName);
	}
	
}
