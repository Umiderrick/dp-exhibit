/**
 * 
 */
package com.zeusas.dp.adminservice;

import java.util.List;

/**
 * @author pengbo
 *
 */
public interface TaskPointManager {
	public <T> int diffupdate(List<T> list);
	
	public void doReload(int i);
}
