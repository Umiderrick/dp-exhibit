package com.zeusas.dp.controller;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zeusas.dp.exhibit.entity.AuthUser;
import com.zeusas.dp.exhibit.service.AuthUserService;
import com.zeusas.dp.exhibit.utils.AuthcUtils;
import com.zeusas.dp.exhibit.utils.BasicController;
import com.zeusas.dp.exhibit.utils.DSResponse;
import com.zeusas.dp.exhibit.utils.DSStatus;
import com.zeusas.dp.exhibit.utils.DigestEncoder;
import com.zeusas.dp.exhibit.utils.HttpUtil;

/**
 * 后台管理的登录
 * 
 * @author pengbo
 */
@Controller
public class UILoginController extends BasicController {

	static Logger logger = LoggerFactory.getLogger(UILoginController.class);

	@Autowired
	private AuthUserService auth;

	@PostMapping(value = "/login.do")
	@ResponseBody
	public DSResponse loginAction(@RequestParam(required = true) String username,
			@RequestParam(required = true) String password) {
		boolean loginOk = authUserLogin();
		DSResponse reponse = new DSResponse();
		if (!loginOk) {
			AuthUser findByLoginName = auth.findByLoginName(username);
			if (findByLoginName == null) {
				reponse.setStatus(DSStatus.FAILURE);
			} else {
				if (DigestEncoder.encodePassword(username, password).equals(findByLoginName.getPassword())) {
					HttpSession sess = request.getSession();
					sess.setAttribute(AuthcUtils.SEC_AUTHUSER, findByLoginName);
					return reponse;
				}

			}
			return reponse;
		} else if (getAuthUser().getStatus() == 0) {
			reponse.setStatus(DSStatus.FAILURE);
			reponse.sendMessage("用户被禁用");
			return reponse;
		} else {
			try {
				// 判断角色是否允许登录
				reponse.setStatus(DSStatus.SUCCESS);
			} catch (Exception e) {
				logger.error("获取用户失败");
			}
		}
		return reponse;
	}

	private boolean authUserLogin() {
		HttpSession sess = request.getSession();
		return sess.getAttribute(AuthcUtils.SEC_AUTHUSER) != null;
	}

	@GetMapping("authuser/userinfo.do")
	@ResponseBody
	public DSResponse userinfo() {
		AuthUser user = getAuthUser();
		if (user != null) {
			return new DSResponse(user);
		}
		return DSResponse.FAILURE;
	}

	@GetMapping("index")
	public String index() {
		return "index";
	}

	@GetMapping("logout.do")
	@ResponseBody
	public DSResponse logout() {
		DSResponse ds = new DSResponse();
		HttpUtil.removeAllAttr(request.getSession());
		ds.setStatus(DSStatus.SUCCESS);
		ds.sendMessage("登出成功");
		return ds;
	}

	@GetMapping("home")
	public String home() {
		return "home";
	}
	@GetMapping("applyPage")
	public String applyPage() {
		return "exhibit/apply";
	}

	@GetMapping("guidePage")
	public String guidePage() {
		return "exhibit/guide";
	}
	
	@GetMapping("applydetail")
	public String applydetail() {
		return "exhibit/applydetail";
	}
}
