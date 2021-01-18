package com.zeusas.dp.exhibit.service;

import java.util.Collection;

import com.zeusas.dp.exhibit.entity.UserCustomer;

public interface UserCustomerManager {

	Collection<UserCustomer> findAll();

	UserCustomer getByLoginName(String name);

	UserCustomer getCustomerById(Integer customerd);

	void load();

}
