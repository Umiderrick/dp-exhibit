/**
 * 
 */
package com.zeusas.dp.exhibit.bean;

import com.zeusas.dp.exhibit.entity.Apply;

/**
 * @author pengbo
 *
 */
public class ApplyBean extends Apply {

	/**
	 * serialVersionUID
	 */
	private String counterName;

	private String upTime;

	public String getCounterName() {
		return counterName;
	}

	public void setCounterName(String counterName) {
		this.counterName = counterName;
	}

	public String getUpTime() {
		return upTime;
	}

	public void setUpTime(String upTime) {
		this.upTime = upTime;
	}
	
	
	
}
