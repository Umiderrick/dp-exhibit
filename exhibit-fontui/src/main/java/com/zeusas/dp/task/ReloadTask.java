/**
 * 
 */
package com.zeusas.dp.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.zeusas.dp.exhibit.service.CounterManager;
import com.zeusas.dp.exhibit.service.CustomerManager;
import com.zeusas.dp.exhibit.service.UserCustomerManager;

@Component
public class ReloadTask{

	private static final Logger logger = LoggerFactory.getLogger(ReloadTask.class);

	@Autowired
	private CounterManager counterManager;
	@Autowired
	private CustomerManager customerManager;
	@Autowired
	private UserCustomerManager userCustomerManager;

	/**
	 * 读取日志
	 */
	@Scheduled(cron="0 0 3 * * ?")
	public void readlogging() {
		counterManager.load();
		customerManager.load();
		userCustomerManager.load();
		logger.info("manager刷新完毕");
	}
}
