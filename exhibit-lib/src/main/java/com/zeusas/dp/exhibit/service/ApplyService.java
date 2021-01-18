/**
 * 
 */
package com.zeusas.dp.exhibit.service;

import java.util.List;

import com.zeusas.dp.exhibit.entity.Apply;
import com.zeusas.dp.exhibit.entity.AuthUser;
import com.zeusas.dp.exhibit.entity.UploadPic;

/**
 * @author pengbo
 *
 */
public interface ApplyService  {
	
	boolean passApply(Long id,AuthUser au,String suggestions, Integer mark);
	
	boolean refuseApply(Long id,AuthUser au,String suggestions, Integer mark);

	/**
	 * @param id
	 * @return
	 */
	List<UploadPic> getpicById(Long id);

	/**
	 * @param apply
	 * @param list
	 * 增加事务处理逻辑
	 */
	boolean initapply(Apply apply, List<UploadPic> list);
	
	boolean cancel(Long id);

	boolean cancelAudit(String counterCode, String appmonth);

	List<Apply> findByAppMonth(String appMonth);

	List<Apply> findByCounterCodeAndAppMonth(String counterCode, String format);

	List<Apply> findByCounterCodeInAndAppmonth(List<String> lists, String format);

	Apply get(Long id);

}
