package com.zeusas.dp.exhibit.service;

import java.util.List;
import java.util.Set;

import com.zeusas.dp.exhibit.entity.Customer;

/**
 * 
 * @author shihx
 * @date 2016年12月14日 上午10:07:42
 */
public interface CustomerManager {
	
	public List<Customer> findAll();
	
	public Customer get(Integer id);
	
	public List<Customer> findByName(String Name);
	
	public List<Customer> pagination(int page,int num);
	
	public List<Customer> findAllChildren(Customer customer);
	
	public List<Integer> findAllChildrenCounterId(Customer customer);
	
	public Set<String> findAllChildrenCounters(Customer customer);
	
	void load();
}
