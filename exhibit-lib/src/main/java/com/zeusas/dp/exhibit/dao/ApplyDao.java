/**
 * 
 */
package com.zeusas.dp.exhibit.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zeusas.dp.exhibit.entity.Apply;

/**
 * @author pengbo
 *
 */
public interface ApplyDao extends JpaRepository<Apply, Long> {

	List<Apply> findByCounterCodeAndAppMonth(String counterCode, String appMonth);

	List<Apply> findByAppMonth(String appMonth);

	List<Apply> findByCounterCodeInAndAppMonth(List<String> lists, String format);

}
