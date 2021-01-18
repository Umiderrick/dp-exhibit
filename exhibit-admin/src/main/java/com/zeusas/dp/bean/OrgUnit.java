package com.zeusas.dp.bean;

/**
 * 业务组织建架构
 * <p>
 * 运营公司 - 总监（大区） - 中区 - 小区
 */
public class OrgUnit  {
	// 组织id
	private String id;
	private String orgCode;
	// 组织名称
	private String orgName;
	// 上级组织id
	private String parentOrgId;
	private String parentName;
	private int status;

	private String  nameshort;

	private Integer  level;
	
	private Character  type;

	
	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}


	public String getParentOrgId() {
		return parentOrgId;
	}

	public void setParentOrgId(String parentOrgId) {
		this.parentOrgId = parentOrgId;
	}

	public String getNameshort() {
		return nameshort;
	}

	public void setNameshort(String nameshort) {
		this.nameshort = nameshort;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Character getType() {
		return type;
	}

	public void setType(Character type) {
		this.type = type;
	}
	
	

}
