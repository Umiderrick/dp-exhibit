package com.zeusas.dp.exhibit.dao;


import org.springframework.data.jpa.repository.JpaRepository;

import com.zeusas.dp.exhibit.entity.MenuNode; 

public interface MenuDao extends JpaRepository<MenuNode, Integer> {
	
}
