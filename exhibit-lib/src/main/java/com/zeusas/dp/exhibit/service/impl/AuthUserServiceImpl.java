package com.zeusas.dp.exhibit.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zeusas.dp.exhibit.dao.AuthUserDao;
import com.zeusas.dp.exhibit.entity.AuthUser;
import com.zeusas.dp.exhibit.service.AuthUserService;
import com.zeusas.dp.exhibit.utils.ServiceException;

@Service
public class AuthUserServiceImpl implements AuthUserService {

    @Autowired
    AuthUserDao authUserDao;

    @Override
    public AuthUser findByLoginName(String loginName) throws ServiceException {
        List<AuthUser> users = authUserDao.findByLoginName(loginName);
        if (users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }

	@Override
	public AuthUser get(String uid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(AuthUser au) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(AuthUser au) {
		// TODO Auto-generated method stub
		
	}
}
