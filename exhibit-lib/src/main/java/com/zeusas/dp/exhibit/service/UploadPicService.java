/**
 * 
 */
package com.zeusas.dp.exhibit.service;

import java.util.List;

import com.zeusas.dp.exhibit.entity.UploadPic;

/**
 * @author pengbo
 *
 */
public interface UploadPicService{

	List<UploadPic> findByIds(List<Long> longs);

}
