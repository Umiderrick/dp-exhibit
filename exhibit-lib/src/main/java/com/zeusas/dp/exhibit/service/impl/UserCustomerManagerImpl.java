package com.zeusas.dp.exhibit.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.zeusas.dp.exhibit.entity.UserCustomer;
import com.zeusas.dp.exhibit.service.UserCustomerManager;
import com.zeusas.dp.exhibit.service.UserCustomerService;

@Service
public class UserCustomerManagerImpl  //
		implements UserCustomerManager,InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(UserCustomerManagerImpl.class);

	private final Map<String, UserCustomer> userIdMap;
	private final Map<String, UserCustomer> loginNameMap;
	private final Map<Integer, UserCustomer> customerId;
	private final Map<Integer, List<UserCustomer>> customerId_all;

	@Autowired
	private UserCustomerService userCustomerService;

	public UserCustomerManagerImpl() {
		userIdMap = new ConcurrentHashMap<>();
		loginNameMap = new ConcurrentHashMap<>();
		customerId = new ConcurrentHashMap<>();
		customerId_all = new ConcurrentHashMap<>();
		logger.info("INIT USER_CUSTOMER READY");
	}

	private void clearAll() {
		userIdMap.clear();
		loginNameMap.clear();
		customerId.clear();
		customerId_all.clear();
	}

	@Override
	public void load() {
		clearAll();

		List<UserCustomer> userCustomers = userCustomerService.findAll();

		for (UserCustomer uc : userCustomers) {
			userIdMap.put(uc.getUserId(), uc);
			loginNameMap.put(uc.getLoginName(), uc);
			if (uc.getUserId().equals(uc.getCustomerUserId())) {
				customerId.put(uc.getCustomerId(), uc);
			}
		}

		for (UserCustomer uc : userCustomers) {
			if (!customerId_all.containsKey(uc.getCustomerId())) {
				List<UserCustomer> ucs = new ArrayList<>(userCustomers.size());
				customerId_all.put(uc.getCustomerId(), ucs);
			}
			customerId_all.get(uc.getCustomerId()).add(uc);
		}

		for (List<?> c : customerId_all.values()) {
			((ArrayList<?>) c).trimToSize();
		}
	}


	@Override
	public UserCustomer getByLoginName(String name) {
		return loginNameMap.get(name);
	}

	@Override
	public UserCustomer getCustomerById(Integer customerid) {
		return customerId.get(customerid);
	}



	@Override
	public Collection<UserCustomer> findAll() {
		return userIdMap.values();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (userIdMap.size() == 0) {
			load();
		}
	}
}
