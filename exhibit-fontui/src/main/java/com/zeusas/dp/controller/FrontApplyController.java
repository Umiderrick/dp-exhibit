/**
 * 
 */
package com.zeusas.dp.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.Base64;
import com.google.common.base.Strings;
import com.zeusas.dp.bean.PictureBean;
import com.zeusas.dp.exhibit.entity.Apply;
import com.zeusas.dp.exhibit.entity.ApplyShowBean;
import com.zeusas.dp.exhibit.entity.UploadPic;
import com.zeusas.dp.exhibit.service.ApplyService;
import com.zeusas.dp.exhibit.service.CounterManager;
import com.zeusas.dp.exhibit.utils.AppConfig;
import com.zeusas.dp.exhibit.utils.BeanClone;
import com.zeusas.dp.exhibit.utils.DSResponse;
import com.zeusas.dp.exhibit.utils.DSStatus;
import com.zeusas.dp.exhibit.utils.DateUtil;
import com.zeusas.dp.exhibit.utils.IVfs;
import com.zeusas.dp.exhibit.utils.QDate;

/**
 * @author pengbo
 *
 */
@RestController
@RequestMapping("/apply")
public class FrontApplyController {
	static Logger logger = LoggerFactory.getLogger(FrontApplyController.class);
	
	@Autowired
	private ApplyService applyService;
	@Autowired
	private CounterManager counterManager;

	@RequestMapping(value = "/fapply", method = RequestMethod.POST)
	public DSResponse firstApply(Apply apply, PictureBean picBean, HttpServletRequest req) {
		DSResponse dsResponse = new DSResponse();
		DSResponse ds = judgeDate (dsResponse);
		if(ds.getStatus ()==-1){
			return dsResponse;
		}
		HttpSession session = req.getSession();
		String counterCode;
		Object tmp = session.getAttribute("counterCode");
		if (tmp != null) {
			counterCode = tmp.toString();
		} else {
			return dsResponse.setResponse(DSStatus.FAILURE.id, "超时重新登录。");
		}
		List<Apply> find = applyService.findByCounterCodeAndAppMonth(counterCode,DateUtil.getFiscalMonth());
		if (find.size() > 0) {
			return dsResponse.setResponse(DSStatus.FAILURE.id, "其他柜台提交过");
		}
		apply.setAppMonth(DateUtil.getFiscalMonth());
		apply.setIsPass(false);
		apply.setCreateTime(new Date());
		apply.setLastupdate(System.currentTimeMillis());
		apply.setCreateUser(counterCode);
		apply.setActive(true);

		// 构建7图片list(增加效率)
		List<UploadPic> list = new ArrayList<>();
		UploadPic pic1 = new UploadPic();
		setPic(pic1, picBean.getPic1(), 1, counterCode, apply.getId());
		list.add(pic1);
		UploadPic pic2 = new UploadPic();
		setPic(pic2, picBean.getPic2(), 2, counterCode, apply.getId());
		list.add(pic2);

		UploadPic pic3_1 = new UploadPic();
		setPic(pic3_1, picBean.getPic3(), 3, counterCode, apply.getId());
		list.add(pic3_1);
		UploadPic pic3_2 = new UploadPic();
		setPic(pic3_2, picBean.getPic3(), 8, counterCode, apply.getId());
		list.add(pic3_2);
		UploadPic pic3_3 = new UploadPic();
		setPic(pic3_3, picBean.getPic3(), 9, counterCode, apply.getId());
		list.add(pic3_3);

		UploadPic pic4 = new UploadPic();
		setPic(pic4, picBean.getPic4(), 4, counterCode, apply.getId());
		list.add(pic4);
		UploadPic pic5 = new UploadPic();
		setPic(pic5, picBean.getPic5(), 5, counterCode, apply.getId());
		list.add(pic5);
		UploadPic pic6 = new UploadPic();
		setPic(pic6, picBean.getPic6(), 6, counterCode, apply.getId());
		list.add(pic6);
		UploadPic pic7 = new UploadPic();
		setPic(pic7, picBean.getPic7(), 7, counterCode, apply.getId());
		list.add(pic7);
		try {
			boolean initapply = applyService.initapply(apply, list);
			if (!initapply) {
				dsResponse.setStatus(DSStatus.DB_ERROR);
				dsResponse.sendMessage("请勿使用表情");
			}
		} catch (Exception e) {
			logger.error("保存申请出错", e);
			dsResponse.setStatus(DSStatus.FAILURE);
			dsResponse.sendMessage("系统错误请重新提交");

		}
		return dsResponse;
	}

	private void setPic(UploadPic pic, String url, int areaType, String upUser, Long applyId) {
		pic.setUrl(url);
		pic.setUploadTime(new Date());
		pic.setAreatype(areaType);
		pic.setUploaduser(upUser);
		pic.setStatus(1);
		pic.setApplyId(applyId);
	}

	@InitBinder
	public void InitBinder(ServletRequestDataBinder binder) {
		binder.registerCustomEditor(java.util.Date.class,
				new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
	}

	private int end = AppConfig.getInteger("END_DATE");
	
	private int lamp_s = AppConfig.getInteger("LAMP_START");
	
	private int lamp_e = AppConfig.getInteger("LAMP_END");

	@RequestMapping("/end")
	public DSResponse end() {
		return new DSResponse("正常结束:"+end+"灯片："+lamp_s+"->"+lamp_e);
	}
	
	
	private DSResponse judgeDate(DSResponse dsResponse){
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DATE);
		if (day > end) {
			dsResponse.setStatus(DSStatus.FAILURE);
			dsResponse.sendMessage("不在提交期");
		}
		return  dsResponse;
	}
	
	// 上传文件返回服务器url
	@RequestMapping("/uploadpic")
	public DSResponse uploadPic(String img, String picType, HttpServletRequest req) {
		DSResponse ds =new DSResponse ();
		DSResponse dsResponse = judgeDate (ds);
		if(dsResponse.getStatus ()==-1){
			return dsResponse;
		}
		// 项目/柜台号/年月/1-5图片
		StringBuilder sb = new StringBuilder();
		HttpSession session = req.getSession();
		try {
			String counterCode = null;
			Object tmp = session.getAttribute("counterCode");
			if (tmp != null) {
				counterCode = tmp.toString();
			}

			String[] base64 = img.split(",");
			String[] sp = base64[0].split("/");
			String[] type = sp[1].split(";");// 得到图片格式
			byte[] picture = Base64.base64ToByteArray(base64[1]);
			String uri = sb.append("exhibit/").append(counterCode).append("/")
					.append(QDate.format("yyyyMM", new Date())).append("/")
					.append(picType)
					.append("_")
					.append(System.currentTimeMillis())
					.append(".").append(type[0])
					.toString();
			sb.delete(0, sb.length());
			String putUrl = sb.append(AppConfig.getString("put-vfs-prefix")).append(uri).toString();
			sb.delete(0, sb.length());
			String getUrl = sb.append(AppConfig.getString("get-vfs-prefix")).append(uri).toString();
			if (IVfs.put(putUrl, picture)) {
				dsResponse.setData(getUrl);
			} else {
				dsResponse.setResponse(DSStatus.FAILURE.id, "图片上传出错!");
			}

		} catch (Exception e) {
			logger.error("", e);
		}
		return dsResponse;
	}

	@RequestMapping("/curmonthresult")
	public DSResponse curMonthResult(HttpServletRequest req) {
		DSResponse dsResponse = new DSResponse();
		String time = req.getParameter("time");
		HttpSession session = req.getSession();
		String counterCode = null;
		try {

			Object tmp = session.getAttribute("counterCode");
			if (tmp != null) {
				counterCode = tmp.toString();
			}
			if (Strings.isNullOrEmpty(time) || "0".equals(time)) {
				time = DateUtil.getFiscalMonth();
			}
			List<Apply> apps = applyService.findByCounterCodeAndAppMonth(counterCode, time);
			if (apps.size() != 1) {
				logger.info(time + "月未提交数据");
				dsResponse.sendMessage(time + "月未提交数据");
				return dsResponse;
			}

			Apply apply = apps.get(0);
			List<UploadPic> list = applyService.getpicById(apply.getId());
			ApplyShowBean asb = new ApplyShowBean();
			BeanClone.dup(apply, asb);
			asb.setPiclist(list);
			if (counterManager.getCounterByCode(asb.getCounterCode()) != null) {
				asb.setCounter(counterManager.getCounterByCode(asb.getCounterCode()));
			}
			dsResponse.setData(asb);
			dsResponse.setStatus(DSStatus.SUCCESS);
		} catch (Exception e) {
			dsResponse.sendMessage("查询出错");
			logger.error("", e);
		}
		return dsResponse;
	}

	@RequestMapping("/passmonthresult")
	public DSResponse passMonthResult(HttpServletRequest req, String appmonth) {
		DSResponse dsResponse = new DSResponse();
		HttpSession session = req.getSession();
		String counterCode = null;
		try {
			Object tmp = session.getAttribute("counterCode");
			if (tmp != null) {
				counterCode = tmp.toString();
			}
			List<Apply> apps = applyService.findByCounterCodeAndAppMonth(counterCode, appmonth);
			dsResponse.setData(apps);
			dsResponse.setStatus(DSStatus.SUCCESS);
		} catch (Exception e) {
			logger.error("", e);
		}
		return dsResponse;
	}

	@RequestMapping("/cancelAudit")
	public DSResponse cancelAudit(HttpServletRequest req, String appmonth) {
		DSResponse ds =new DSResponse ();
		DSResponse dsResponse = judgeDate (ds);
		if(dsResponse.getStatus ()==-1){
			dsResponse.sendMessage ("仅在提交期内可以撤回");
			return dsResponse;
		}
		HttpSession session = req.getSession();
		String counterCode;
		try {
			Object tmp = session.getAttribute("counterCode");
			if (tmp != null) {
				counterCode = tmp.toString();
			} else {
				dsResponse.setStatus(DSStatus.FAILURE);
				return dsResponse;
			}
			boolean result = applyService.cancelAudit(counterCode, appmonth);
			dsResponse.setData(result);
			dsResponse.setStatus(DSStatus.SUCCESS);
		} catch (Exception e) {
			logger.error("", e);
		}
		return dsResponse;
	}

}
