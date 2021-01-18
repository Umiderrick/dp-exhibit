package com.zeusas.dp.exhibit.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *UserDetail entity. 
 *@author fx
 *用户细节表，用户的地址，姓名等
 */
@Entity
@Table(name = "base_user_detail")
public class UserDetail {

	//用户ID
	@Id
	@Column(name = "USERID", unique = true, nullable = false)
	private String userId;
	//姓名
	@Column(name = "NAME")
	private String name;
	//手机
	@Column(name = "MOBILE")
	private String mobile;
	//电话
	@Column(name = "PHONE")
	private String phone;
	//客户类型
	@Column(name = "CUSTOMERTYPE")
	private String customerType;
	//省
	@Column(name = "PROVINCE")
	private String province;
	//市
	@Column(name = "CITY")
	private String city;
	//区(县)
	@Column(name = "AREACOUNTY")
	private String areaCounty;
	//地址
	@Column(name = "ADDRESS")
	private String address;
	//邮编
	@Column(name = "POSTCODE")
	private String postCode;
	@Column(name = "STATUS")
	private Boolean status;
	@Column(name = "LASTUPDATE")
	private long lastUpdate;
	
	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCustomerType() {
		return this.customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getProvince() {
		return this.province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAreaCounty() {
		return this.areaCounty;
	}

	public void setAreaCounty(String areaCounty) {
		this.areaCounty = areaCounty;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostCode() {
		return this.postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public Boolean getStatus() {
		return this.status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	
	public long getLastUpdate() {
		return this.lastUpdate;
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public int hashCode() {
		return userId == null ? 0 : userId.hashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!obj.getClass().isInstance(this)){
			return false;
		}
		UserDetail other = (UserDetail) obj;
		
		return Objects.equals(this.userId, other.userId)//
				&& Objects.equals(this.name, other.name) //
				&& Objects.equals(this.mobile, other.mobile)//
				&& Objects.equals(this.phone, other.phone)//
				&& Objects.equals(this.province, other.province)//
				&& Objects.equals(this.areaCounty, other.areaCounty)//
				&& Objects.equals(this.status, other.status)//
				&& Objects.equals(this.postCode, other.postCode)//
				&& Objects.equals(this.userId, other.userId)//
				;
	}
}