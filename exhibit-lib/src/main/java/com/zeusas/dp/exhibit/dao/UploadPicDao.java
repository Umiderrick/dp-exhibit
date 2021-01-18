/**
 * 
 */
package com.zeusas.dp.exhibit.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zeusas.dp.exhibit.entity.UploadPic;

/**
 * @author pengbo
 *
 */
public interface UploadPicDao extends JpaRepository<UploadPic, Long> {

	List<UploadPic> findByApplyId(Long id);

}
