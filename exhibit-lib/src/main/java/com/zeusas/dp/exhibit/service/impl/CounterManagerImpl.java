package com.zeusas.dp.exhibit.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.zeusas.dp.exhibit.entity.Counter;
import com.zeusas.dp.exhibit.service.CounterManager;
import com.zeusas.dp.exhibit.utils.DataApiClient;
import com.zeusas.dp.exhibit.utils.QDate;

@Service
public class CounterManagerImpl implements CounterManager,InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(CounterManagerImpl.class);
	// 全部门店
	private final Map<Integer, Counter> allCounter;
	private final Map<String, Counter> codeMap;
	private final Map<Integer, List<Counter>> customerIdMap;
	private final Map<String, Counter> nameMap;
	private long lastUpdate = 0;

	public CounterManagerImpl() {
		allCounter = new LinkedHashMap<>();
		codeMap = new HashMap<>();
		customerIdMap = new HashMap<>();
		nameMap = new HashMap<>();
	}

	@Override
	public void load() {
		allCounter.clear();
		codeMap.clear();
		customerIdMap.clear();
		nameMap.clear();
		List<Counter> all = this.listMidplatData();

		for (Counter c : all) {
			allCounter.put(c.getCounterId(), c);
			if(!c.getStatus()){
				continue;
			}
			codeMap.put(c.getCounterCode(), c);
			nameMap.put(c.getCounterName(), c);
		}

		for (Counter c : allCounter.values()) {
			if (!customerIdMap.containsKey(c.getCustomerId())) {
				List<Counter> cltemp1 = new ArrayList<Counter>(allCounter.size());
				customerIdMap.put(c.getCustomerId(), cltemp1);
			}
			customerIdMap.get(c.getCustomerId()).add(c);
		}

		for (List<?> c : customerIdMap.values()) {
			((ArrayList<?>) c).trimToSize();
		}
		lastUpdate=System.currentTimeMillis();
		logger.info("柜台reload成功");
	}

	public Counter getCounterById(Integer Id) {
		if (!QDate.checkPeriod(Calendar.HOUR, lastUpdate)) {
			load();
		}
		return allCounter.get(Id);
	}

	public Counter getCounterByCode(String counterCode) {
		return codeMap.get(counterCode);
	}

	public List<Counter> getCounterByCustomerId(Integer CustomerId) {
		List<Counter> cl = customerIdMap.get(CustomerId);
		return cl == null ? new ArrayList<>(0) : cl;
	}

	public Collection<Counter> findAll() {
		if (!QDate.checkPeriod(Calendar.DATE, lastUpdate)) {
			load();
		}
		return allCounter.values();
	}

	public List<Counter> pagination(int page, int num) {
		final List<Counter> pageList = new ArrayList<Counter>(page);
		int size = allCounter.size();
		int start = (page - 1) * num;
		int end = num * page;
		if (start >= size) {
			return pageList;
		}
		end = end < size ? end : size;

		Collection<Counter> all = allCounter.values();
		Iterator<Counter> itr = all.iterator();

		for (int i = 0; i < end && itr.hasNext(); i++) {
			Counter c = itr.next();
			if (i < start) {
				continue;
			}
			pageList.add(c);
		}
		return pageList;
	}

	public List<Counter> findByName(String Name) {
		List<Counter> cl = new ArrayList<Counter>();
		Set<String> keys = nameMap.keySet();
		for (String key : keys) {
			if (key.contains(Name)) {
				cl.add(nameMap.get(key));
			}
		}
		return cl;
	}

	@Override
	public Counter get(Integer id) {
		return this.allCounter.get(id);
	}
	
	@Override
	public List<Counter> findByCounterId(Set<String> counterId) {
		List<Counter> counters = findAll().stream()//
				.filter(e -> counterId.contains(e.getCounterId().toString()))//
				.collect(Collectors.toList());
		return counters.isEmpty() ? new ArrayList<>(0) : counters;
	}

	
	private List<Counter> listMidplatData() {
		DataApiClient client=new DataApiClient();
		return client.getDataAsArray("NC","COUNTER",Counter.class);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (allCounter.size() == 0) {
			load();
		}
	}
}
