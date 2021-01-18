package com.zeusas.dp.exhibit.service;

import com.zeusas.dp.exhibit.entity.UserDetail;

public interface UserDetailService{

	UserDetail get(String userId);

	void update(UserDetail ud);

	void save(UserDetail ud);
}
