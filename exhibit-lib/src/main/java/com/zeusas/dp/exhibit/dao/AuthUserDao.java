package com.zeusas.dp.exhibit.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zeusas.dp.exhibit.entity.AuthUser;


public interface AuthUserDao extends JpaRepository<AuthUser, String> {

	List<AuthUser> findByLoginName(String loginName);
}
