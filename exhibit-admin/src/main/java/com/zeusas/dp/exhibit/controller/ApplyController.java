/**
 * 
 */
package com.zeusas.dp.exhibit.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.zeusas.dp.bean.OrgUnit;
import com.zeusas.dp.bean.PagerModel;
import com.zeusas.dp.exhibit.bean.ApplyBean;
import com.zeusas.dp.exhibit.entity.Apply;
import com.zeusas.dp.exhibit.entity.ApplyShowBean;
import com.zeusas.dp.exhibit.entity.AuthUser;
import com.zeusas.dp.exhibit.entity.Counter;
import com.zeusas.dp.exhibit.entity.UploadPic;
import com.zeusas.dp.exhibit.service.ApplyService;
import com.zeusas.dp.exhibit.service.CounterManager;
import com.zeusas.dp.exhibit.utils.AppConfig;
import com.zeusas.dp.exhibit.utils.AppContext;
import com.zeusas.dp.exhibit.utils.BasicController;
import com.zeusas.dp.exhibit.utils.BeanClone;
import com.zeusas.dp.exhibit.utils.DSResponse;
import com.zeusas.dp.exhibit.utils.DSStatus;
import com.zeusas.dp.exhibit.utils.DateUtil;
import com.zeusas.dp.exhibit.utils.QDate;
import com.zeusas.dp.exhibit.utils.QString;
import com.zeusas.dp.util.PoiDownloadUtil;
import com.zeusas.dp.util.Record;
import com.zeusas.dp.util.VfsDocClient;

/**
 * @author pengbo
 *
 */
@Controller
@RequestMapping("/apply")
public class ApplyController extends BasicController {

	static Logger logger = LoggerFactory.getLogger(ApplyController.class);
	//SMS_126363596
	public static final String templateId = "SMS_158547134";
	
	final Comparator<ApplyBean> cmp = (a1, a2) -> {
		
		boolean s1 = a1.getActive();
		boolean s2 = a2.getActive();
		long u1 = a1.getLastupdate();
		long u2 = a2.getLastupdate();
		if(s1==s2){
			return Long.compare(u2, u1);
		}
		if (s1) {
			return -1;
		}
		return 1;

	};
	@Autowired
	private ApplyService applyService;
	@Autowired
	private CounterManager counterManager;


	private Map<String,OrgUnit> orgUnitsByCode=new HashMap<>();


	@RequestMapping("/list.do")
	@ResponseBody
	public DSResponse query(@RequestParam(required = false) String name, //
			@RequestParam(required = false) String code,
			@RequestParam(required = false) int index, //
			@RequestParam(required = false) int row,
			@RequestParam(required = false) String appmonth) {
		DSResponse res = new DSResponse();
		PagerModel<ApplyBean> page = new PagerModel<>();
		// 总记录数
		int count;
		int startNo = (index - 1) * row;
		int endNo;
		String iname =name==null?"":name;
		String icode =code==null?"":code;
		try {
			if(QString.isEmpty(appmonth)){
				appmonth=DateUtil.getFiscalMonth();
			}
			List<Apply> findAll = applyService.findByAppMonth(appmonth);
			List<ApplyBean> returnCounter = new ArrayList<>();
			for (Apply a : findAll) {
				if(a.getCounterCode().contains(icode)) {
					ApplyBean applyBean = new ApplyBean();
					BeanClone.dup(a, applyBean);
					Counter counterByCode = counterManager.getCounterByCode(a.getCounterCode());
					if (counterByCode != null) {
						String counterName = counterByCode.getCounterName();
						if(counterName.indexOf(iname)>=0){
							applyBean.setCounterName(counterName);
							returnCounter.add(applyBean);
						}
					}
					applyBean.setUpTime(QDate.format(applyBean.getCreateTime()));
				}
			}
			returnCounter = returnCounter.stream()//
					.sorted(cmp)//
					.collect(Collectors.toList());

			count = returnCounter.size();
			endNo = Math.min(count, index * row);
			returnCounter = returnCounter.subList(startNo, endNo);
			page.setRows(returnCounter);
			page.setTotal(count);
			res.setData(page);
			res.setStatus(DSStatus.SUCCESS);
		} catch (Exception e) {
			logger.error("", e);
			res.setStatus(DSStatus.FAILURE);
			res.sendMessage("查询失败!");
		}
		return res;
	}

	@RequestMapping("/detail.do")
	@ResponseBody
	public DSResponse detail(@RequestParam(required = false) Long id) {

		DSResponse res = new DSResponse();
		try {
			Apply apply = applyService.get(id);
			List<UploadPic> list = applyService.getpicById(id);
			ApplyShowBean asb = new ApplyShowBean();
			BeanClone.dup(apply, asb);
			asb.setPiclist(list);
			if (counterManager.getCounterByCode(asb.getCounterCode()) != null) {
				asb.setCounter(counterManager.getCounterByCode(asb.getCounterCode()));
			}
			res.setData(asb);
			res.setStatus(DSStatus.SUCCESS);
		} catch (Exception e) {
			logger.error("", e);
			res.setStatus(DSStatus.FAILURE);
			res.sendMessage("查询失败!");
		}
		return res;
	}

	@RequestMapping(value = "pass.do", method = RequestMethod.POST)
	@ResponseBody
	public DSResponse pass(Long id, String suggest,Integer mark) {
		DSResponse res = new DSResponse();
		try {
			AuthUser au = super.getAuthUser();
			if(au==null){
				res.setStatus(DSStatus.FAILURE);
				res.sendMessage("请重新登陆");
			}
			if (applyService.passApply(id, au, suggest,mark)) {
				res.sendMessage("审核通过");
				res.setStatus(DSStatus.SUCCESS);
			} else {
				res.sendMessage("审核次数达到2次无法审核");
				res.setStatus(DSStatus.SUCCESS);
			}
		} catch (Exception e) {
			logger.error("", e);
			res.setStatus(DSStatus.FAILURE);
			res.sendMessage("审核出错!");
		}
		return res;
	}
	
	
	@RequestMapping(value = "suggestImg.do", method = RequestMethod.POST)
	@ResponseBody
	public DSResponse uploadsuggestImg(MultipartFile file) throws IOException {
		DSResponse res = new DSResponse();
		StringBuffer sb = new StringBuffer();
		String originalFilename = file.getOriginalFilename();
		String substring = originalFilename.substring(originalFilename.indexOf("."), originalFilename.length());
		String uri = sb.append("exhibit/suggestImg/").append(System.currentTimeMillis()).append(substring).toString();
		sb.delete(0, sb.length());
		String putUrl = sb.append(AppConfig.getString("put-vfs-prefix")).append(uri).toString();
		sb.delete(0, sb.length());
		String getUrl = sb.append(AppConfig.getString("get-vfs-prefix")).append(uri).toString();
		VfsDocClient.put(putUrl, file.getInputStream());
		res.setData(getUrl);
		file.getSize();
		return res;
	}

	@RequestMapping(value = "refuse.do", method = RequestMethod.POST)
	@ResponseBody
	public DSResponse refuse(Long id, String suggest,Integer mark) {
		DSResponse res = new DSResponse();
		try {
			AuthUser au = super.getAuthUser();
			if (applyService.refuseApply(id, au, suggest,mark)) {
				Apply apply = applyService.get(id);
				Calendar cal = Calendar.getInstance();
				 int i = cal.get(Calendar.DATE);
				 boolean x =i<5||i>26;
				 if(cal.get(Calendar.DATE)>25){
				 	cal.add (Calendar.MONTH,1);
				 }
				 if (apply.getCount() == 1&&x) {
					String counterCode = apply.getCounterCode();
					CounterManager cm = AppContext.getBean(CounterManager.class);
					Counter counterByCode = cm.getCounterByCode(counterCode);
					Map<String, String> ctx = new HashMap<>();
					ctx.put("appMonth", apply.getAppMonth());
					ctx.put("counterName", counterByCode.getCounterName());
					cal.set (Calendar.DATE,5);
					ctx.put("dealline",QDate.format (QDate.YYYY_MM_DD,cal.getTime ()));
//					if (!smsService.send(templateId, counterByCode.getMobile (), ctx)) {
//						logger.error(counterByCode.getMobile() + "短信发送失败");
//					}
				}
				res.sendMessage("审核不通过成功");
				res.setStatus(DSStatus.SUCCESS);
			} else {
				res.sendMessage("审核次数达到2次无法审核 ");
				res.setStatus(DSStatus.SUCCESS);
			}
		} catch (Exception e) {
			logger.error("", e);
			res.setStatus(DSStatus.FAILURE);
			res.sendMessage("审核出错!");
		}
		return res;
	}


	// type 0未通过 1通过
	@RequestMapping(value = "getExcel/{type}/{appMonth}.do")
	public void getExcel(@PathVariable("type") Boolean type,@PathVariable("appMonth") String appMonth) {
		XSSFWorkbook excel = new XSSFWorkbook();
		String[] mergeHeader = { "运营","大区","小区", "主管", "柜台号", "店面地址", "电话", "联系人", "物料满意度", "审核状态"};
		String lastMonth = appMonth==null?DateUtil.getLastMonth():appMonth;
		List<Apply> alist = applyService.findByAppMonth(lastMonth)
				.stream()
				.filter(e->e.getIsPass().equals(type))
				.collect(Collectors.toList());
		List<Record> rlist = decro(alist);
		PoiDownloadUtil.addSheet(excel, "陈列审核列表", mergeHeader, rlist, 0, 0);
		String path = request.getSession().getServletContext().getRealPath("") + "/excel/";
		String fileName = lastMonth + ".xlsx";
		String filePath = path + fileName;
		File file1 = new File(filePath);
		if (!file1.getParentFile().exists()) {
			file1.getParentFile().mkdirs();
		}
		String userAgent = request.getHeader("User-Agent");
		try {
			FileOutputStream fout = new FileOutputStream(filePath);
			excel.write(fout);
			fout.close();
		} catch (FileNotFoundException e2) {
			logger.error("未知文件错误", e2);
		} catch (IOException e1) {
			logger.error("输出错误", e1);
		}
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");
		try {
			// name.getBytes("UTF-8")处理safari的乱码问题
			byte[] bytes = userAgent.contains("MSIE") ? fileName.getBytes() : fileName.getBytes("UTF-8");
			fileName = new String(bytes, "ISO-8859-1"); // 各浏览器基本都支持ISO编码
			response.reset();

			// 文件名外的双引号处理firefox的空格截断问题
			response.setHeader("Content-disposition", String.format("attachment; filename=\"%s\"", fileName));
			response.setContentType("application/octet-stream; charset=utf-8");

			OutputStream out = response.getOutputStream();
			FileInputStream fs = new FileInputStream(file1);
			int streamLength = (int) file1.length();
			byte[] execlfile = new byte[streamLength];
			fs.read(execlfile, 0, streamLength);
			fs.close();
			out.write(execlfile);
			out.flush();
			out.close();
		} catch (IOException e1) {
			logger.error("输出错误", e1);
		}
	}

	public OrgUnit getOrgUnitByCode(String cd) {
		return (OrgUnit)this.orgUnitsByCode.get(cd);
	}

	/**
	 * @param alist
	 * @return
	 */
	private List<Record> decro(List<Apply> alist) {
		List<Record> li = new ArrayList<>(alist.size());
		for (Apply ap : alist) {
			Counter counterByCode = counterManager.getCounterByCode(ap.getCounterCode());
			Record rec = new Record(12);
			if(counterByCode==null){
				continue;
			}
			try {
			OrgUnit self = getOrgUnitByCode(counterByCode.getCounterCode());
			OrgUnit fa1 =self==null?null: getOrgUnitByCode(self.getParentOrgId());
			OrgUnit fa2 =fa1==null?null: getOrgUnitByCode(fa1.getParentOrgId());
			OrgUnit fa3 =fa2==null?null: getOrgUnitByCode(fa2.getParentOrgId());
			OrgUnit fa4 =fa3==null?null: getOrgUnitByCode(fa3.getParentOrgId());
			String run = fa4==null?"":fa4.getOrgName();
			String big = fa3==null?"":fa3.getOrgName();
			String small = fa2==null?"":fa2.getOrgName();
			String charge = fa1==null?"":fa1.getOrgName();
			rec.set(0, run);
			rec.set(1, big);
			rec.set(2, small);
			rec.set(3, charge);
			rec.set(4, counterByCode.getCounterCode());
			rec.set(5, counterByCode.getAddress());
			rec.set(6, counterByCode.getMobile());
			rec.set(7, counterByCode.getContact());
			rec.set(8, ap.getSatisfy());
			String p ;
			if(ap.getIsPass()){
				p="通过";
			}else{
				p="不通过";
			}
			rec.set(9, p);
			}catch(Exception e) {
				logger.error("counterByCode: {}",counterByCode.getCounterCode());
			}
			li.add(rec);
		}
		return li;
	}


	@RequestMapping("cancel/{id}.do")
	@ResponseBody
	public DSResponse cancel(@PathVariable("id") Long id) {
		DSResponse res = new DSResponse();
		try {
			AuthUser au = super.getAuthUser();
			if(au!=null){
				boolean cancel = applyService.cancel(id);
				if(cancel){
					res.sendMessage("撤销成功");
				}else{
					res.sendMessage("撤销异常");
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			res.setStatus(DSStatus.FAILURE);
		}
		return res;
	}
}
