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
@Table(name = "bus_up_pic")
public class UploadPic {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	
	@Column(name = "filename")
	private String filename;
	@Column(name = "filetype")
	private String filetype;
	
	//每张图片
	@Column(name ="applyid")
	private Long applyId;
	
	/**
	 * 1门头整体2高柜3集中售卖区4收银台5主推台6橱窗7其他的照片
	 */
	@Column(name = "areatype")
	private int areatype;
	@Column(name = "file_path")
	private String filePath;
	@Column(name = "url")
	private String url;
	@Column(name = "status")
	private int status;
	@Column(name = "is_use")
	private String isUse;
	@Column(name = "uploaduser")
	private String uploaduser;
	@Column(name = "upload_time")
	private Date uploadTime;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFiletype() {
		return filetype;
	}
	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}
	public int getAreatype() {
		return areatype;
	}
	public void setAreatype(int areatype) {
		this.areatype = areatype;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getIsUse() {
		return isUse;
	}
	public void setIsUse(String isUse) {
		this.isUse = isUse;
	}
	public String getUploaduser() {
		return uploaduser;
	}
	public void setUploaduser(String uploaduser) {
		this.uploaduser = uploaduser;
	}
	public Date getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}
	public Long getApplyId() {
		return applyId;
	}
	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}

}
