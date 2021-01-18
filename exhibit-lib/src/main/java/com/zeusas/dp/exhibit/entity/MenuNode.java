package com.zeusas.dp.exhibit.entity;


import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;


@Entity
@Table(name = "core_menu")
public class MenuNode{	 
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "MID")
	private int id; 
	
	@Column(name = "MCODE")
	private String code; 
	
	@Column(name = "MNAME")
    private String name; 
    
	@Column(name = "PARENTCODE")
    private String parentCode;  
    
	@Column(name = "MICON")
    private String icon;
    
	@Column(name = "URL")
    private String hrefUrl;
	
	@Column(name = "LASTUPDATE")
	private Long lastUpdate;
	
	@Column(name = "ROLES")
	@Type(type = "com.zeusas.dp.exhibit.entity.StringSetType")
	private Set<String> roles;
	
	@Column(name = "SEQCODE")
	private String seqCode;
	
	public MenuNode() {
	}

	public String getSeqCode() {
		return seqCode;
	}

	public void setSeqCode(String seqCode) {
		this.seqCode = seqCode;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getHrefUrl() {
		return hrefUrl;
	}

	public void setHrefUrl(String hrefUrl) {
		this.hrefUrl = hrefUrl;
	}

	public Long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	
	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public boolean containsRoles(Set<String> myRoles) {
		if (myRoles == null || myRoles.isEmpty()) {
			return false;
		}

		for (String role : myRoles) {
			if (this.roles.contains(role)) {
				return true;
			}
		}

		return false;
	}
}
