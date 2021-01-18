package com.zeusas.dp.exhibit.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zeusas.dp.exhibit.entity.UserDetail;
/**
 * 
 * @author shihx
 * @date 2016年12月6日 下午3:04:11
 */
public interface UserDetailDao extends JpaRepository<UserDetail, String> {

}
