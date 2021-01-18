package com.zeusas.dp.exhibit.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.zeusas.dp.exhibit.entity.Counter;
import com.zeusas.dp.exhibit.entity.Customer;
import com.zeusas.dp.exhibit.service.CounterManager;
import com.zeusas.dp.exhibit.service.CustomerManager;
import com.zeusas.dp.exhibit.utils.AppContext;
import com.zeusas.dp.exhibit.utils.DataApiClient;
import com.zeusas.dp.exhibit.utils.QDate;
import com.zeusas.dp.exhibit.utils.TypeConverter;

/**
 * 
 */
@Service
public class CustomerManagerImpl implements CustomerManager,InitializingBean {

	static Logger logger = LoggerFactory.getLogger(CustomerManagerImpl.class);
	final Map<Integer, Customer> customers;
	private long lastUpdate = 0;

	public CustomerManagerImpl() {
		customers = new LinkedHashMap<>();
	}

	@Override
	public synchronized void load() {
		List<Customer> all=new ArrayList<>();
		for (Customer customer : listMidplatData()) {
			customer.setLevel(Customer.LEVEL_NORMAL_CUSTOMER);
			all.add(customer);
		}
		for (Customer customer : listOrgCustomer()) {
			String customerCode = customer.getCustomerCode();
			if(customerCode==null){
				continue;
			}
			//为了给客户分类创建帐号 NC该字段没有值
			if(customerCode.startsWith("01")){
				customer.setCustomerType("直营");
				customer.setCustomerTypeID(TypeConverter.toInteger(Customer.customerType_Direct));
			}else{
				customer.setCustomerType("运营商");
				customer.setCustomerTypeID(TypeConverter.toInteger(Customer.customerType_Operator));
			}
			customer.setLevel(Customer.LEVEL_ORG_CUSTOMER);
			all.add(customer);
		}

		customers.clear();
		for (Customer c : all) {
			customers.put(c.getCustomerID(), c);
		}
		fixCounters();
		for (Customer c : all) {
			if (!c.getStatus()) {
				continue;
			}
			Integer pid = c.getParentId();
			Customer pCustomer = customers.get(pid);
			if (pCustomer == null) {
				continue;
			}
			pCustomer.addChildren(c);
		}
		lastUpdate = System.currentTimeMillis();
		logger.info("重载Customer成功");
	}

	@Override
	public List<Customer> findAll() {
		List<Customer> all = new ArrayList<Customer>(customers.size());
		all.addAll(customers.values());
		return all;
	}

	@Override
	public Customer get(Integer id) {
		if (!QDate.checkPeriod(Calendar.HOUR, lastUpdate)) {
			load();
		}
		return id == null ? null : customers.get(id);
	}

	public List<Customer> pagination(int page, int num) {
		int size = customers.size();
		int startNo = (page - 1) * num;
		int endNo = page * num < size ? page * num : size;
		if (startNo >= size) {
			return new ArrayList<>(0);
		}

		final List<Customer> cl = new ArrayList<Customer>(num);
		int i = 0;
		for (Customer c : customers.values()) {
			if (i >= endNo) {
				break;
			} else if (i >= startNo) {
				cl.add(c);
			}
			i++;
		}
		return cl;
	}

	public List<Customer> findByName(String name) {
		if (Strings.isNullOrEmpty(name)) {
			return findAll();
		}
		return customers.values().stream()//
				.filter(e -> e.getCustomerName().contains(name))//
				.collect(Collectors.toList());
	}

	private void findAll(Customer customer, List<Customer> customers) {
		List<Customer> child = customer.getChildren();
		if (child == null || child.isEmpty()) {
			return;
		}
		for (Customer c : child) {
			customers.add(c);
			findAll(c, customers);
		}
	}

	public List<Customer> findAllChildren(Customer customer) {
		List<Customer> customers = new ArrayList<Customer>();
		findAll(customer, customers);
		Collections.sort(customers);
		return customers;
	}

	public List<Integer> findAllChildrenCounterId(Customer customer) {
		Set<String> counterIds = findAllChildrenCounters(customer);
		List<Integer> counterIds2 = new ArrayList<>(counterIds.size());
		counterIds.forEach(e -> counterIds2.add(Integer.parseInt(e)));
		return counterIds2;
	}

	@Override
	public Set<String> findAllChildrenCounters(Customer customer) {
		Set<String> counterIds = new HashSet<>();
		List<Customer> children = findAllChildren(customer);
		children.stream().filter(e -> e.getCounters() != null)//
				.forEach(e -> counterIds.addAll(e.getCounters()));
		return counterIds;
	}

	@Autowired
	private CounterManager cm;
	
	private void fixCounters() {
		List<Customer> customers = this.findAll();
		if (customers.isEmpty()) {
			customers = listMidplatData();
		}
		if (cm.findAll().isEmpty()) {
			cm.load();
		}
		for (Customer customer : customers) {
			List<Counter> counters = cm.getCounterByCustomerId(customer.getCustomerID());
			Set<String> counterIds = new HashSet<>();
			if(Customer.LEVEL_ORG_CUSTOMER==customer.getLevel()){
				continue;
			}
			if (counters != null) {
				for (Counter counter : counters) {
					counterIds.add(counter.getCounterId().toString());
				}
			}
			customer.setCounters(counterIds);
			if (counterIds.isEmpty()) {
				customer.setStatus(Customer.STATUS_DISABLE);
			}
		}
	}

	private List<Customer> listMidplatData() {
		return getCustomers("CUSTOMER");
	}

	private List<Customer> getCustomers(String customer) {
		List<Customer> customers=new ArrayList<>();
		DataApiClient client = new DataApiClient();
		List<Customer> nc_customers = client.getDataAsArray("NC", customer, Customer.class);
		if(nc_customers!=null){
			customers=nc_customers;
		}
		return customers;
	}

	private List<Customer> listOrgCustomer() {
		return getCustomers("CUSTCLASS");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (customers.isEmpty()) {
			load();
		}
	}
}
