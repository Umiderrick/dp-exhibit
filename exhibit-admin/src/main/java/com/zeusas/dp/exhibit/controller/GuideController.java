/**
 * 
 */
package com.zeusas.dp.exhibit.controller;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zeusas.dp.bean.PagerModel;
import com.zeusas.dp.exhibit.entity.AuthUser;
import com.zeusas.dp.exhibit.entity.Guide;
import com.zeusas.dp.exhibit.service.GuideService;
import com.zeusas.dp.exhibit.utils.AppConfig;
import com.zeusas.dp.exhibit.utils.BasicController;
import com.zeusas.dp.exhibit.utils.DSResponse;
import com.zeusas.dp.exhibit.utils.DSStatus;
import com.zeusas.dp.exhibit.utils.QString;
import com.zeusas.dp.util.VfsDocClient;

/**
 * @author pengbo
 *
 */
@RestController
@RequestMapping("/guide")
public class GuideController extends BasicController{

	static Logger logger = LoggerFactory.getLogger(GuideController.class);
	
	@Autowired
	private GuideService guideService;
	
	@RequestMapping("/list.do")
	public DSResponse query(@RequestParam(required = false) String name, //
			@RequestParam(required = false) int index, //
			@RequestParam(required = false) int row) {
		DSResponse res = new DSResponse();
		PagerModel<Guide> page = new PagerModel<Guide>();
		int count;
		int startNo = (index - 1) * row;
		int endNo;
		try {
			List<Guide> findAll = guideService.findAll();
			List<Guide> returnlist =findAll;
			if (!QString.isEmpty(name)) {
				returnlist = returnlist.stream()//
						.filter(e -> e.getName().indexOf(name) >= 0)//
						.collect(Collectors.toList());
			}
			count = returnlist.size();
			endNo = Math.min(count, index * row);
			returnlist = returnlist.subList(startNo, endNo);
			page.setRows(returnlist);
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
	
	@RequestMapping("/saveGuide.do")
	public DSResponse saveGuide(Guide guide) {
		DSResponse res = new DSResponse();
		try {
			AuthUser au =super.getAuthUser();
			List<Guide> pass;
			if(QString.isEmpty(guide.getCounterCode())){
				guide.setIsCommen(true);
				pass = guideService.findByUseMonthAndisUse( guide.getUseMonth(),1);
			}else{
				guide.setIsCommen(false);
				pass = guideService.findByUseMonthAndisUse( guide.getUseMonth(),1).stream().filter(e->e.getCounterCode().equals(guide.getCounterCode())).collect(Collectors.toList());
			}
			guide.setUse(true);
			guide.setUploadTime(new Date());
			guide.setUploadUser(au.getCommonName());
			//废除上一份
			for(Guide gd :pass ){
				gd.setUse(false);
				guideService.update(gd);
			}
			guideService.save(guide);
			res.setStatus(DSStatus.SUCCESS);
			res.sendMessage("新增成功！");
		} catch (Exception e) {
			logger.error("", e);
			res.setStatus(DSStatus.FAILURE);
			res.sendMessage("新增失敗!");
		}
		return res;
	}


	@RequestMapping("/delete.do")
	public DSResponse delete(Long id) {
		DSResponse res = new DSResponse();
		try {
			AuthUser au =super.getAuthUser();
			if(au==null){
				res.setStatus(DSStatus.LOGIN_REQUIRED);
				res.sendMessage("登录信息丢失");
				return res;
			}
			Guide guide = guideService.get(id);
			String accUrl = guide.getAccUrl();
			String accReplace =accUrl.replace(AppConfig.getString("get-vfs-prefix"),AppConfig.getString("put-vfs-prefix"));
			VfsDocClient.delete(accReplace);
			guideService.delete(id);
			res.setStatus(DSStatus.SUCCESS);
			res.sendMessage("删除成功！");
		} catch (Exception e) {
			logger.error("", e);
			res.setStatus(DSStatus.FAILURE);
			res.sendMessage("删除失敗!");
		}
		return res;
	}
	
	@RequestMapping(value = "/uploadPDF.do")
	public DSResponse uploadPhoto(@RequestParam(name = "pdf", required = true) String pdf) {
		DSResponse dsResponse = new DSResponse();
		StringBuffer sb = new StringBuffer();
		try {
			String[] base64 = pdf.split(",");// 去除前面
			byte[] pdfData = Base64.decodeBase64(base64[1]);
			String uri = sb.append("exhibit/pdf/").append(System.currentTimeMillis()).append(".pdf").toString();
			sb.delete(0, sb.length());
			String putUrl = sb.append(AppConfig.getString("put-vfs-prefix")).append(uri).toString();
			sb.delete(0, sb.length());
			String getUrl = sb.append(AppConfig.getString("get-vfs-prefix")).append(uri).toString();
			VfsDocClient.put(putUrl, pdfData);
			dsResponse.setData(getUrl);
			dsResponse.sendMessage("文件上传成功");
		} catch (Exception e) {
			logger.error("文件上传失败！", e);
			dsResponse.setResponse(DSStatus.FAILURE.id, "文件上传失败");
		}

		return dsResponse;
	}
}
