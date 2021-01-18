package com.zeusas.dp.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Strings;
import com.zeusas.dp.adminservice.MenuManager;
import com.zeusas.dp.bean.JsTree;
import com.zeusas.dp.bean.PagerModel;
import com.zeusas.dp.exhibit.entity.AuthUser;
import com.zeusas.dp.exhibit.entity.MenuNode;
import com.zeusas.dp.exhibit.entity.MenuTree;
import com.zeusas.dp.exhibit.utils.BasicController;
import com.zeusas.dp.exhibit.utils.DSResponse;
import com.zeusas.dp.exhibit.utils.DSStatus;

@Controller
public class MenuController extends BasicController {

	static final Logger logger = LoggerFactory.getLogger(MenuController.class);

	@Autowired
	MenuManager menuManager;

	/**
	 * 初始化当前用户的菜单
	 * 
	 * @return
	 */
	@RequestMapping(value = "/menuinit.do", method = { POST, GET })
	public @ResponseBody DSResponse menuInit() {
		try {
			AuthUser user = getAuthUser();
			List<MenuTree> trees = menuManager.buildMenu(user);
			return new DSResponse(trees);
		} catch (Exception e) {
			return DSResponse.FAILURE;
		}

	}

	/**
	 * 获取当前用户的所有功能父节点
	 * 
	 * @return
	 */
	@RequestMapping(value = "/get/pmenu.do", method = { POST, GET })
	public @ResponseBody DSResponse parentMenu() {
		AuthUser user = getAuthUser();
		if (user == null) {
			return new DSResponse(DSStatus.LOGIN_REQUIRED);
		}
		List<MenuTree> trees=menuManager.buildMenu(user);
		JsTree js;
		List<JsTree> jsTree=new ArrayList<>();
		for(MenuTree mt:trees)
		{
			js=new JsTree(mt);
			jsTree.add(js);
		}
		 
		DSResponse reponse = new DSResponse();
		reponse.setData(jsTree);
		return reponse;
	}
	
	
	

	/**
	 * 根据父级Code获取他的子级
	 * 
	 * @param parentCode
	 * @return
	 */
	@RequestMapping(value = "/get/cmenu", method = { POST, GET })
	public @ResponseBody DSResponse getChirdenMenu(@RequestParam(required = true) String parentCode) {

		AuthUser user = getAuthUser();
		if (user == null) {
			return new DSResponse(DSStatus.LOGIN_REQUIRED);
		}

		if (Strings.isNullOrEmpty(parentCode)) {
			return new DSResponse(new ArrayList<>(0));
		}

		List<MenuNode> menuNode = menuManager.getSubmenu(user, parentCode);
		PagerModel<MenuNode> page = new PagerModel<MenuNode>();
		page.setRows(menuNode);
		page.setTotal(menuNode.size());

		return new DSResponse(page);
	}

	
}
