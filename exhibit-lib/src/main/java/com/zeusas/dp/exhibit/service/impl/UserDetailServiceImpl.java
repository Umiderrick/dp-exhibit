package com.zeusas.dp.exhibit.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zeusas.dp.exhibit.dao.UserDetailDao;
import com.zeusas.dp.exhibit.entity.UserDetail;
import com.zeusas.dp.exhibit.service.UserDetailService;

@Service
public class UserDetailServiceImpl  implements UserDetailService {

	private final static Logger logger = LoggerFactory.getLogger(UserDetailServiceImpl.class);
	
	@Autowired
	private UserDetailDao userDetailDao;

	@Override
	public UserDetail get(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(UserDetail ud) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(UserDetail ud) {
		// TODO Auto-generated method stub
		
	}
	


}
