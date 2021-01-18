/**
 * 
 */
package com.zeusas.dp.exhibit.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zeusas.dp.exhibit.dao.ApplyDao;
import com.zeusas.dp.exhibit.dao.UploadPicDao;
import com.zeusas.dp.exhibit.entity.Apply;
import com.zeusas.dp.exhibit.entity.AuthUser;
import com.zeusas.dp.exhibit.entity.UploadPic;
import com.zeusas.dp.exhibit.service.ApplyService;
import com.zeusas.dp.exhibit.utils.EmojiFilter;
import com.zeusas.dp.exhibit.utils.ServiceException;

/**
 * 
 * @author pengbo
 *
 */
@Service
@Transactional
public class ApplyServiceImpl  implements ApplyService {

	private static final Logger logger = LoggerFactory.getLogger(ApplyServiceImpl.class);
	@Autowired
	private ApplyDao dao;

	@Autowired
	private UploadPicDao uploadPicDao;

	/*
	 * 通过申请
	 */
	@Override
	public boolean passApply(Long id, AuthUser au, String suggest, Integer mark) {
		Apply apply = dao.findById(id).get();
		int count = apply.getCount();
		// 如果次数小于2可以通过
		if (count < 2) {
			count++;
			apply.setVerifyUser(au.getCommonName());
			apply.setMark(100);
			apply.setIsPass(true);
			apply.setAuditsuggest(suggest);
			apply.setCount(count);
			apply.setLastupdate(System.currentTimeMillis());
			apply.setActive(false);
			dao.save(apply);
			return true;
		}

		return false;
	}

	/*
	 * 驳回申请 第一次提交 0，驳回1次 1 ..两次之后无法审核
	 */
	@Override
	public boolean refuseApply(Long id, AuthUser au, String suggest, Integer mark) {
		Apply apply = dao.findById(id).get();
		int count = apply.getCount();
		// 如果次数小于2可以通过
		if (count < 2) {
			apply.setVerifyUser(au.getCommonName());
			apply.setMark(100);
			apply.setLastupdate(System.currentTimeMillis());
			apply.setAuditsuggest(suggest);
			count++;
			apply.setCount(count);
			apply.setActive(false);
			dao.save(apply);
			return true;
		}
		return false;
	}

	/*
	 * 根据appid查询用到的图片
	 */
	@Override
	public List<UploadPic> getpicById(Long id) {
		return uploadPicDao.findByApplyId(id);
	}

	/* 
	 * 
	 */
	@Override
	@org.springframework.transaction.annotation.Transactional
	public boolean initapply(Apply apply, List<UploadPic> list) {
		boolean flag = false;
		try {
			String sug = EmojiFilter.filterEmoji(apply.getSuggest());
			apply.setSuggest(sug);
			dao.save(apply);
			Long id = apply.getId();
			for (UploadPic t : list) {
				t.setApplyId(id);
				uploadPicDao.save(t);
			}
			flag = true;
		} catch (Exception e) {
			logger.error("保存申请异常", e);
		}
		return flag;

	}

	// 可退回的count>0
	@Override
	public boolean cancel(Long id) {
		boolean flag = false;
		try {
			Apply toCancel = dao.findById(id).get();
			// 先转换成可审核状态
			if (!toCancel.getActive()) {
				toCancel.setActive(true);
				if (toCancel.getCount() == 1) {
					toCancel.setCount(0);
					toCancel.setIsPass(false);
				} else {
					// 两次通过或者不过
					toCancel.setCount(1);
					toCancel.setIsPass(false);
				}
				toCancel.setLastupdate(System.currentTimeMillis());
				dao.save(toCancel);
				flag = true;
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		return flag;
	}

	@Override
	public boolean cancelAudit(String counterCode, String appMonth) {
		boolean flag = false;
		List<Apply> find = dao.findByCounterCodeAndAppMonth(counterCode, appMonth);
		// 大于一单属于数据异常
		if (find.size() > 1) {
			logger.error("取消订单查询结果大于1参数：柜台号-{},{}", counterCode, appMonth);
		} else if (find.isEmpty()) {
			logger.error("重复取消：柜台号-{},{}", counterCode, appMonth);
		} else {
			// 只有数量为1为正常撤回
			Apply apply = find.get(0);
			// 激活状态可以手动撤回
			if (apply.getActive()) {
				if (apply.getCount() == 0) {
					// 第一次提交直接删除该单
					dao.deleteById(apply.getId());
					flag = true;
				} else if (apply.getCount() == 1) {
					apply.setActive(false);
					dao.save(apply);
					flag = true;
				}
			}
		}

		return flag;
	}

	@Override
	public List<Apply> findByAppMonth(String appMonth) {
		return dao.findByAppMonth(appMonth);
	}

	@Override
	public List<Apply> findByCounterCodeAndAppMonth(String counterCode, String format) {
		return dao.findByCounterCodeAndAppMonth(counterCode,format);
	}

	@Override
	public List<Apply> findByCounterCodeInAndAppmonth(List<String> lists, String format) {
		return dao.findByCounterCodeInAndAppMonth(lists, format);
	}

	@Override
	public Apply get(Long id) {
		return dao.findById(id).get();
	}

}
