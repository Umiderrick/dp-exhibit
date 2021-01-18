/**
 * 
 */
package com.zeusas.dp.exhibit.entity;

import java.util.List;

/**
 * @author pengbo
 *
 */
public class ApplyShowBean extends Apply{

	
	
	private Counter counter;
	
	private List<UploadPic> piclist ;
	
	public List<UploadPic> getPiclist() {
		return piclist;
	}
	public void setPiclist(List<UploadPic> piclist) {
		this.piclist = piclist;
	}
	public Counter getCounter() {
		return counter;
	}
	public void setCounter(Counter counter) {
		this.counter = counter;
	}
	
	
	 

}
