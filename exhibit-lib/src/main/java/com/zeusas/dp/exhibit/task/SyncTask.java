package com.zeusas.dp.exhibit.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.zeusas.dp.exhibit.service.CounterManager;
import com.zeusas.dp.exhibit.service.CustomerManager;
import com.zeusas.dp.exhibit.service.UserCustomerManager;

public class SyncTask  {
	
	
	private static Logger logger = LoggerFactory.getLogger(SyncTask.class);

	@Autowired
	private CounterManager counterManager;
	@Autowired
	private CustomerManager customerManager;
	@Autowired
	private UserCustomerManager userCustomerManager;
	

	/**
	 * 任务执点
	 */
	public void exec(){
		counterManager.load();
		customerManager.load();
		userCustomerManager.load();
		logger.info("manager刷新完毕");
	
	}
	


	/**
	 * 是否准备就緒
	 */
	protected boolean ready() {
		return true;
	}
}
