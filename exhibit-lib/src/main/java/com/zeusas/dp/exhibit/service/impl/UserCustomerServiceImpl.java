package com.zeusas.dp.exhibit.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zeusas.dp.exhibit.dao.UserCustomerDao;
import com.zeusas.dp.exhibit.entity.UserCustomer;
import com.zeusas.dp.exhibit.service.UserCustomerService;

@Service
public class UserCustomerServiceImpl  implements UserCustomerService{

	@Autowired
	private UserCustomerDao dao;

	@Override
	@Transactional(readOnly = true)
	public List<UserCustomer> findAll() {
		return dao.findAll();
	}

	@Override
	public UserCustomer get(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(UserCustomer ucus) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(UserCustomer ucus) {
		// TODO Auto-generated method stub
		
	}
	


}
