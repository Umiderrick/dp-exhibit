/**
 * 
 */
package com.zeusas.dp;

import com.google.common.eventbus.Subscribe;

/**
 * @author pengbo
 *
 */
public class ErtEventListener {

	public int lastMessage =0;
	
	@Subscribe 
	public void list(ErTEvent e) {
		lastMessage =e.getMessage();
		System.out.println("Message "+ lastMessage);
	}
	
	public int getlastMessage(){
		return lastMessage;
	}
}
