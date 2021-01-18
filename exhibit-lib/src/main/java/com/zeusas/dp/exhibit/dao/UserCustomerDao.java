package com.zeusas.dp.exhibit.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zeusas.dp.exhibit.entity.UserCustomer;

/**
 * 
 * @author shihx
 * @date 2017年1月9日 上午10:46:24
 */
public interface UserCustomerDao extends JpaRepository<UserCustomer, String>{

}
