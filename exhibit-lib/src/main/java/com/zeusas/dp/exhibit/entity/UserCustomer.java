package com.zeusas.dp.exhibit.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * 用户关联到客户 用户可操作的柜台
 * @author shihx
 * @date 2017年1月9日 上午10:46:38
 */
@Entity
@Table(name = "base_user_customer")
public class UserCustomer{
	
	@Id
	@Column(name = "USERID", nullable = false)
	private String userId;
	/** Login Name */
	@Column(name = "LOGINNAME", nullable = false, unique = true)
	private String loginName;
	/** 客户ID */
	@Column(name = "CUSTOMERID")
	private Integer customerId;
	/** 可操作的柜台Id集合 */
	@Column(name = "COUNTERS")
	@Type(type = "com.zeusas.dp.exhibit.entity.StringSetType")
	private Set<String> counters;
	/** 客户的用户ID*/
	@Column(name = "CUSTOMERUSERID")
	private String customerUserId;
	/** 客户的用户loginName */
	@Column(name = "CUSTOMERLOGINNAME")
	private String customerLoginName;
	@Column(name = "STATUS")
	private Integer status;
	@Column(name = "LASTUPDATE")
	private Long lastUpdate;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public Set<String> getCounters() {
		return counters==null?new HashSet<>():counters;
	}

	public void setCounters(Set<String> counters) {
		this.counters = counters;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getCustomerUserId() {
		return customerUserId;
	}

	public void setCustomerUserId(String customerUserId) {
		this.customerUserId = customerUserId;
	}

	public int hashCode() {
		return userId == null ? 0 : userId.hashCode();
	}

	public String getCustomerLoginName() {
		return customerLoginName;
	}

	public void setCustomerLoginName(String customerLoginName) {
		this.customerLoginName = customerLoginName;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof UserCustomer)) {
			return false;
		}
		UserCustomer u = (UserCustomer) obj;
		return Objects.equals(this.userId, u.userId)
				&& Objects.equals(this.loginName, u.loginName)
				&& Objects.equals(this.customerId, u.customerId) ;
	}

}
