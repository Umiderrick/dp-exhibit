package com.zeusas.dp.adminservice;

import java.util.List;

import com.zeusas.dp.exhibit.entity.AuthUser;
import com.zeusas.dp.exhibit.entity.MenuNode;
import com.zeusas.dp.exhibit.entity.MenuTree;

public interface MenuManager {
	public List<MenuTree> buildMenu(AuthUser user);

	public List<MenuNode> getRootMenu(AuthUser user);

	public List<MenuNode> getSubmenu(AuthUser user, String parentCode);

	void reload();

	public MenuNode getMenuDetail(String id);

}
