package com.zeusas.dp.adminservice.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zeusas.dp.adminservice.MenuService;
import com.zeusas.dp.exhibit.dao.MenuDao;
import com.zeusas.dp.exhibit.entity.MenuNode;

@Service
@Transactional
public class MenuServiceImpl  implements MenuService {

	@Autowired
	private MenuDao dao;

	@Override
	public List<MenuNode> findAll() {
		return dao.findAll();
	}



}
