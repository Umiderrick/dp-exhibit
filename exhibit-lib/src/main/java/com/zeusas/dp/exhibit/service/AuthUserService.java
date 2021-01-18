package com.zeusas.dp.exhibit.service;

import com.zeusas.dp.exhibit.entity.AuthUser;
import com.zeusas.dp.exhibit.utils.ServiceException;

public interface AuthUserService {


    AuthUser findByLoginName(String loginName)  throws ServiceException;

	AuthUser get(String uid);

	void update(AuthUser au);

	void save(AuthUser au);
}
