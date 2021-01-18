package com.zeusas.dp.exhibit.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.zeusas.dp.exhibit.entity.Counter;

/**
 * 
 * @author shihx
 * @date 2016年12月19日 下午6:13:12
 */
public interface CounterManager {

	public Counter get(Integer id);

	public Counter getCounterById(Integer Id);

	public Counter getCounterByCode(String counterCode);

	public List<Counter> getCounterByCustomerId(Integer CustomerId);

	public Collection<Counter> findAll();

	public List<Counter> findByName(String Name);
	
	public List<Counter> findByCounterId(Set<String> counterId);

	void load();

}
