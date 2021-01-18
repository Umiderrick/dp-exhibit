/**
 * 
 */
package com.zeusas.dp.exhibit.service;

import java.util.List;

import com.zeusas.dp.exhibit.entity.Guide;

/**
 * @author pengbo
 *
 */
public interface GuideService{

	Guide get(Long id);

	List<Guide> findByCounterCodeAndUseMonth(String counterCode, String format);

	List<Guide> findByUseMonth(String format);

	List<Guide> findByUseMonthAndisUse(String format, int i);

	void delete(Long id);

	void update(Guide gd);

	void save(Guide guide);

	List<Guide> findAll();

}
