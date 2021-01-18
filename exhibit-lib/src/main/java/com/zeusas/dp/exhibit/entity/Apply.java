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
@Table(name = "bus_apply")
public class Apply {


	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * 柜台号 柜台名称 间
	 */
	// 柜台号
	@Column(name = "countercode")
	private String counterCode;
	// 收到物料时间
	@Column(name = "receivetime")
	private Date receiveTime;
	// 满意度
	@Column(name = "satisfy")
	private int satisfy;
	// 提交日期
	@Column(name = "createtime")
	private Date createTime;
	// 最终修改时间
	@Column(name = "lastupdate")
	private Long lastupdate;
	@Column(name = "createuser")
	private String createUser;
	// 审核人
	@Column(name = "verifyuser")
	private String verifyUser;
	// 是否审核
	@Column(name = "pass")
	private Boolean isPass;
	// 审核次数
	@Column(name = "count")
	private int count;
	//店面对意见
	@Column(name = "suggest")
	private String suggest;
	//店面对意见
	@Column(name = "auditsuggest")
	private String auditsuggest;
	// 审核月份
	@Column(name = "appmonth")
	private String appMonth;
	
	@Column(name = "active")
	private Boolean active;
	
	//newIn
	@Column(name = "mark")
	private int mark;

	@Column(name ="showcase_qty")
	private int showcaseQty;

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCounterCode() {
		return counterCode;
	}

	public void setCounterCode(String counterCode) {
		this.counterCode = counterCode;
	}

	public Date getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}

	public int getSatisfy() {
		return satisfy;
	}

	public void setSatisfy(int satisfy) {
		this.satisfy = satisfy;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getVerifyUser() {
		return verifyUser;
	}

	public void setVerifyUser(String verifyUser) {
		this.verifyUser = verifyUser;
	}

	public Boolean getIsPass() {
		return isPass;
	}

	public void setIsPass(Boolean isPass) {
		this.isPass = isPass;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getSuggest() {
		return suggest;
	}

	public void setSuggest(String suggest) {
		this.suggest = suggest;
	}

	public String getAppMonth() {
		return appMonth;
	}

	public void setAppMonth(String appMonth) {
		this.appMonth = appMonth;
	}

	public Long getLastupdate() {
		return lastupdate;
	}

	public void setLastupdate(Long lastupdate) {
		this.lastupdate = lastupdate;
	}

	public String getAuditsuggest() {
		return auditsuggest;
	}

	public void setAuditsuggest(String auditsuggest) {
		this.auditsuggest = auditsuggest;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public int getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
	}

	public int getShowcaseQty() {
		return showcaseQty;
	}

	public void setShowcaseQty(int showcaseQty) {
		this.showcaseQty = showcaseQty;
	}
}
