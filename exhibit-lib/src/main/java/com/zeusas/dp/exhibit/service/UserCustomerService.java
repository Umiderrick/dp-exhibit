package com.zeusas.dp.exhibit.service;

import java.util.List;

import com.zeusas.dp.exhibit.entity.UserCustomer;

public interface UserCustomerService {

	List<UserCustomer> findAll();

	UserCustomer get(String userId);

	void update(UserCustomer ucus);

	void save(UserCustomer ucus);

}
