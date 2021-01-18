/**
 * 
 */
package com.zeusas.dp.exhibit.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zeusas.dp.exhibit.entity.Guide;

/**
 * @author pengbo
 *
 */
public interface GuideDao extends JpaRepository<Guide, Long> {

	List<Guide> findByCounterCodeAndUseMonth(String counterCode, String format);

	List<Guide> findByUseMonthAndIsUse(String format, int i);

	List<Guide> findByUseMonth(String format);

}
