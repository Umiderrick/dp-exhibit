package com.zeusas.dp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zeusas.dp.exhibit.service.CounterManager;
import com.zeusas.dp.exhibit.service.CustomerManager;
import com.zeusas.dp.exhibit.service.UserCustomerManager;
import com.zeusas.dp.exhibit.utils.DSResponse;
import com.zeusas.dp.exhibit.utils.DSStatus;
import com.zeusas.dp.exhibit.utils.DateUtil;

@RestController
public class CacheReloadController {

	@Autowired
	private CounterManager counterm;
	@Autowired
	private UserCustomerManager ucm;
	@Autowired
	private CustomerManager cm;

	private static Logger logger = LoggerFactory.getLogger(CacheReloadController.class);

	@RequestMapping("reload_ca")
	public DSResponse reload() {
		counterm.load();
		ucm.load();
		cm.load();
		logger.info("内存刷新成功");
		return new DSResponse(DSStatus.SUCCESS, "内存刷新成功");
	}

	@RequestMapping("getFiscaleDate")
	public DSResponse getFiscaleDate() {
		DSResponse dsResponse = new DSResponse();
		String fiscalMonth = DateUtil.getFiscalMonth();
		dsResponse.setData(fiscalMonth);
		return dsResponse;
	}


}
