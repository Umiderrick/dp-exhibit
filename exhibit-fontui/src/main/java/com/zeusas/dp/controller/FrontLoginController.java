/**
 *
 */
package com.zeusas.dp.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.zeusas.dp.exhibit.entity.AuthUser;
import com.zeusas.dp.exhibit.entity.Counter;
import com.zeusas.dp.exhibit.entity.Customer;
import com.zeusas.dp.exhibit.entity.UserCustomer;
import com.zeusas.dp.exhibit.service.AuthUserService;
import com.zeusas.dp.exhibit.service.CounterManager;
import com.zeusas.dp.exhibit.service.CustomerManager;
import com.zeusas.dp.exhibit.service.UserCustomerManager;
import com.zeusas.dp.exhibit.utils.DSResponse;
import com.zeusas.dp.exhibit.utils.DSStatus;
import com.zeusas.dp.exhibit.utils.DigestEncoder;
import com.zeusas.dp.exhibit.utils.HttpUtil;
import com.zeusas.dp.exhibit.utils.RoleResources;

/**
 * @author pengbo
 *
 */
@RestController
public class FrontLoginController {

    private static Logger logger = LoggerFactory.getLogger(FrontLoginController.class);

    @Autowired
    private AuthUserService authUserService;
    @Autowired
    private UserCustomerManager ucm;
    @Autowired
    private CustomerManager cusm;
    @Autowired
    private CounterManager cm;
    
    

    @RequestMapping(value = "/login", method = POST)
    public DSResponse loginAction(String username, String password, HttpServletRequest request) {
        DSResponse reponse = new DSResponse();
        String allowRoleJSON = RoleResources.get(RoleResources.FOREGROUND);
        String encodePassword = DigestEncoder.encodePassword(username, password);
        List<String> allowRoles = JSON.parseArray(allowRoleJSON, String.class);
        try {
            AuthUser authUser = authUserService.findByLoginName(username);
            if (authUser == null) {
                reponse.setStatus(DSStatus.LOGIN_ERROR);
                reponse.sendMessage("未找到该用户");
                return reponse;
            }
            String dbPassWord = authUser.getPassword();
            if (!Objects.equals(dbPassWord, encodePassword)) {
                reponse.setStatus(DSStatus.LOGIN_ERROR);
                reponse.sendMessage("密码错误");
                return reponse;
            }
            UserCustomer userCustomer = ucm.getByLoginName(authUser.getLoginName());
            Customer customer = cusm.get(userCustomer.getCustomerId());
            Set<String> counterSet = userCustomer.getCounters();
            if (counterSet.isEmpty() //
                    && customer.getLevel() == Customer.LEVEL_ORG_CUSTOMER) {
                counterSet = cusm.findAllChildrenCounters(customer);
            }
            List<Counter> counters = new ArrayList<>(cm.findByCounterId(counterSet));
            Set<String> roles = authUser.getRoles();
            if (checkroles(allowRoles, roles)) {
                request.getSession().setAttribute("counters", counters);
                if (counters.size() == 1) {
                    request.getSession().setAttribute("counterCode", counters.get(0).getCounterCode());
                }
                request.getSession().setAttribute("roles", roles);
                reponse.setData(roles);
                reponse.setStatus(DSStatus.SUCCESS);
            } else {
                reponse.setStatus(DSStatus.LOGIN_ERROR);
                reponse.sendMessage("无权限");
            }
        } catch (Exception e) {
            logger.error("获取用户失败");
        }
        return reponse;

    }

    //List(前台允许登录用户)Set用户包含权限String 前台传入权限
    private boolean checkroles(List<String> allowRoles, Set<String> roles) {
        for (String role : roles) {
            if (allowRoles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    @RequestMapping(value = "/loginout", method = {POST, GET})
    public DSResponse loginout(HttpServletRequest request) {
        DSResponse ds = new DSResponse();
        // Remove Session
        HttpUtil.removeAllAttr(request.getSession());
        ds.setStatus(DSStatus.SUCCESS);
        ds.sendMessage("登出成功");
        return ds;
    }
}