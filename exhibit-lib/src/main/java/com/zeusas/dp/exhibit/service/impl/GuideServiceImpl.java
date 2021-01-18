/**
 * 
 */
package com.zeusas.dp.exhibit.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zeusas.dp.exhibit.dao.GuideDao;
import com.zeusas.dp.exhibit.entity.Guide;
import com.zeusas.dp.exhibit.service.GuideService;

/**
 * @author pengbo
 *
 */
@Service
public class GuideServiceImpl implements GuideService {

	@Autowired
	private GuideDao dao;

	@Override
	public Guide get(Long id) {
		return dao.findById(id).get();
	}

	@Override
	public List<Guide> findByCounterCodeAndUseMonth(String counterCode, String format) {
		return dao.findByCounterCodeAndUseMonth(counterCode,format);
	}

	@Override
	public List<Guide> findByUseMonth(String format) {
		return dao.findByUseMonth(format);
	}

	@Override
	public List<Guide> findByUseMonthAndisUse(String format, int i) {
		return dao.findByUseMonthAndIsUse(format,i);
	}

	@Override
	public void delete(Long id) {
		dao.deleteById(id);
	}

	@Override
	public void update(Guide guide) {
		dao.save(guide);
	}

	@Override
	public void save(Guide guide) {
		dao.save(guide);
	}

	@Override
	public List<Guide> findAll() {
		return dao.findAll();
	}
	

}
