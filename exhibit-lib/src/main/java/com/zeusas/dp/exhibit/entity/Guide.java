/**
 * 
 */
package com.zeusas.dp.exhibit.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * @author pengbo
 *
 */
@Entity
@Table(name = "bus_guide")
public class Guide{

	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	//指导名称
	@Column(name = "name")
	private String name;
	
	@Column(name = "relate_path")
	private String relatePath;
	//图片缩略
	@Column(name = "acc_url")
	private String accUrl;
	//指导月份
	@Column(name = "use_month")
	private String useMonth;
	
	@Column(name = "is_commen")
	private Boolean isCommen;
	//指定柜台
	@Column(name = "counter_code")
	private String counterCode;
	@Column(name = "upload_time")
	private Date uploadTime;
	//是否使用
	@Column(name = "is_use")
	private boolean isUse;
	
	@Column(name = "upload_user")
	private String uploadUser;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRelatePath() {
		return relatePath;
	}
	public void setRelatePath(String relatePath) {
		this.relatePath = relatePath;
	}
	public String getAccUrl() {
		return accUrl;
	}
	public void setAccUrl(String accUrl) {
		this.accUrl = accUrl;
	}
	public String getUseMonth() {
		return useMonth;
	}
	public void setUseMonth(String useMonth) {
		this.useMonth = useMonth;
	}
	public Boolean getIsCommen() {
		return isCommen;
	}
	public void setIsCommen(Boolean isCommen) {
		this.isCommen = isCommen;
	}
	public String getCounterCode() {
		return counterCode;
	}
	public void setCounterCode(String counterCode) {
		this.counterCode = counterCode;
	}
	public Date getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}
	
	public boolean isUse() {
		return isUse;
	}
	public void setUse(boolean isUse) {
		this.isUse = isUse;
	}
	public String getUploadUser() {
		return uploadUser;
	}
	public void setUploadUser(String uploadUser) {
		this.uploadUser = uploadUser;
	}
	
	
}
