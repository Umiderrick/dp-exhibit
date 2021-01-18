/**
 * 
 */
package com.zeusas.dp;

/**
 * @author pengbo
 *
 */
public class ErTEvent {

	
	private final int message ;
	
	public ErTEvent (int message){
		this.message =message;
		System.err.println(message);
	}
	
	public int getMessage(){
		return message;
	}
}
