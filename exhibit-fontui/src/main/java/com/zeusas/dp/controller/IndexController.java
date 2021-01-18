/**
 * 
 */
package com.zeusas.dp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zeusas.dp.exhibit.entity.Apply;
import com.zeusas.dp.exhibit.entity.Counter;
import com.zeusas.dp.exhibit.entity.Guide;
import com.zeusas.dp.exhibit.service.ApplyService;
import com.zeusas.dp.exhibit.service.CounterManager;
import com.zeusas.dp.exhibit.service.GuideService;
import com.zeusas.dp.exhibit.service.ReadGuideManager;
import com.zeusas.dp.exhibit.utils.DSResponse;
import com.zeusas.dp.exhibit.utils.DSStatus;
import com.zeusas.dp.exhibit.utils.DateUtil;

/**
 * @author pengbo
 *
 */
@RestController
public class IndexController {

	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

	private static final Integer IS_READ = 1;

	@Autowired
	private ApplyService applyService;

	@Autowired
	private GuideService guideService;

	@Autowired
	private CounterManager counterManager;

	@Autowired
	private ReadGuideManager readGuideManager;

	@RequestMapping("/counterInfo")
	public DSResponse getUInfo(HttpServletRequest req) {

		DSResponse ds = new DSResponse();
		try {
			HttpSession session = req.getSession();
			String counterCode = null;
			Object tmp = session.getAttribute("counterCode");
			if (tmp != null) {
				counterCode = tmp.toString();
			}
			ds.setStatus(DSStatus.SC_NOT_FOUND);
			if (counterCode != null) {
				Counter counter = counterManager.getCounterByCode(counterCode);
				List<Counter> list = new ArrayList<>();
				list.add(counter);
				ds.setData(list);
				ds.setStatus(DSStatus.SUCCESS);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		ds.sendMessage("获取柜台信息成功");
		return ds;
	}

	// 店长查看柜台号
	@RequestMapping("/getCounterCode")
	public DSResponse getCounter(HttpServletRequest req) {
		DSResponse ds = new DSResponse();
		try {
			ds.setStatus(DSStatus.SC_NOT_FOUND);
			HttpSession session = req.getSession();
			String counterCode = null;
			Object tmp = session.getAttribute("counterCode");
			if (tmp != null) {
				counterCode = tmp.toString();
			}
			if (counterCode != null) {
				ds.setData(counterCode);
				ds.setStatus(DSStatus.SUCCESS);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return ds;
	}

	// 区域经理
	@SuppressWarnings("unchecked")
	@RequestMapping("/getCounters")
	public DSResponse getCounters(HttpServletRequest req) {
		DSResponse ds = new DSResponse();
		try {
			HttpSession session = req.getSession();
			Set<String> roles = (Set<String>) session.getAttribute("roles");
			List<Counter> counters = null;
			if (roles == null) {
				logger.error("登录状态丢失");
				ds.setStatus(DSStatus.LOGIN_ERROR);
				return ds;
			}
			if (roles.size() > 0) {
				counters = (List<Counter>) session.getAttribute("counters");
			}
			ds.setData(counters);
			ds.setStatus(DSStatus.SUCCESS);
		} catch (Exception e) {
			logger.error("", e);
		}
		return ds;
	}

	@RequestMapping("/readGuide")
	public DSResponse readGuide(HttpServletRequest req) {
		DSResponse ds = new DSResponse();
		try {
			HttpSession session = req.getSession();
			String counterCode = null;
			Object tmp = session.getAttribute("counterCode");
			if (tmp != null) {
				counterCode = tmp.toString();
			}
			if (counterCode != null) {
				readGuideManager.read(counterCode);
				ds.setData(IS_READ);
			} else {
				ds.setStatus(DSStatus.LOGIN_REQUIRED);
				ds.sendMessage("会话丢失");
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return ds;
	}

	// 用于首页获取本月是否提交过申请
	@RequestMapping("/issub")
	public DSResponse issub(HttpServletRequest req) {
		DSResponse ds = new DSResponse();
		HttpSession session = req.getSession();
		try {
			String counterCode = null;
			Object tmp = session.getAttribute("counterCode");
			if (tmp != null) {
				counterCode = tmp.toString();
			}
			String format = DateUtil.getFiscalMonth();
			if (counterCode != null) {
				List<Apply> find = applyService.findByCounterCodeAndAppMonth(counterCode, format);
				if (find.size() == 0) {
					ds.setData(true);
				} else {
					ds.setData(false);
				}
			} else {
				ds.setStatus(DSStatus.LOGIN_REQUIRED);
				ds.sendMessage("会话丢失");
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return ds;
	}

	// 用于首页拿到指导
	@RequestMapping("/getguide")
	public DSResponse getguide(HttpServletRequest req) {
		DSResponse ds = new DSResponse();
		HttpSession session = req.getSession();
		try {
			String counterCode = null;
			Object tmp = session.getAttribute("counterCode");
			if (tmp != null) {
				counterCode = tmp.toString();
			}
			String format = DateUtil.getFiscalMonth2();
			if (counterCode != null) {
				Guide gd = null;
				List<Guide> find = guideService.findByCounterCodeAndUseMonth(counterCode,format);
				if (find.size() == 0) {
					List<Guide> avoidNUll = guideService.findByUseMonth(format);
					if(!avoidNUll.isEmpty()){
						gd = guideService.findByUseMonth(format).get(0);
					}
					
				} else {
					gd = find.get(0);
				}
				if(gd!=null) {
					ds.setData(gd);
				}else {
					ds.setStatus(DSStatus.FAILURE);
				}
				
			} else {
				ds.setStatus(DSStatus.LOGIN_REQUIRED);
				ds.sendMessage("会话丢失");
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return ds;
	}

	@RequestMapping("/getcommonguide")
	public DSResponse getcommonguide() {
		DSResponse ds = new DSResponse();
		try {
			String format = DateUtil.getFiscalMonth();
			List<Guide> find = guideService.findByUseMonthAndisUse(format,1);
			if (find.size() > 0) {
				ds.setData(find.get(0));
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return ds;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/changecounter")
	public DSResponse getguide(HttpServletRequest req, String counterCode) {
		DSResponse ds = new DSResponse();
		if (counterCode.equals("0")) {
			req.getSession().removeAttribute("counterCode");
			ds.setStatus(DSStatus.SUCCESS);
			return ds;
		}
		try {
			Object tmp = req.getSession().getAttribute("counters");
			List<Counter> cts = null;
			if (tmp != null) {
				cts = (List<Counter>) tmp;
			}
			if (cts != null
					&& cts.stream().map(Counter::getCounterCode).collect(Collectors.toList()).contains(counterCode)) {
				req.getSession().setAttribute("counterCode", counterCode);
			} else {
				ds.setStatus(DSStatus.LOGIN_REQUIRED);
				ds.sendMessage("会话错误");
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return ds;
	}

	@RequestMapping("/getunpass")
	public DSResponse getunpass(HttpServletRequest req) {
		DSResponse ds = new DSResponse();
		try {
			Object tmp = req.getSession().getAttribute("counterCode");
			String counterCode;
			List<String> lists = new ArrayList<>();
			if (tmp != null) {
				counterCode = tmp.toString();
				lists.add(counterCode);
			} else {
				@SuppressWarnings("unchecked")
				List<Counter> counters = (List<Counter>) req.getSession().getAttribute("counters");
				if (counters != null && counters.size()>0) {
					lists = counters.stream().map(Counter::getCounterCode).collect(Collectors.toList());
				}else{
					ds.setStatus(DSStatus.LOGIN_ERROR);
					ds.setData(0);
					return ds;
				}
			}
			String format =DateUtil.getFiscalMonth();
			List<Apply> find = applyService.findByCounterCodeInAndAppmonth(lists,format);
			ds.setStatus(DSStatus.SUCCESS);
			int upass = 0;
			if (find.size() > 0) {
				for (Apply ap : find) {
					if (ap.getCount() == 1 && !ap.getIsPass()) {
						upass++;
					}
				}
			}
			ds.setData(upass);
		} catch (Exception e) {
			logger.error("", e);
		}
		return ds;
	}
}
