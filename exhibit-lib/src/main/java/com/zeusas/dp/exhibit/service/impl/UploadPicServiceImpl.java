/**
 * 
 */
package com.zeusas.dp.exhibit.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zeusas.dp.exhibit.dao.UploadPicDao;
import com.zeusas.dp.exhibit.entity.UploadPic;
import com.zeusas.dp.exhibit.service.UploadPicService;

/**
 * @author pengbo
 *
 */
@Service
public class UploadPicServiceImpl  implements UploadPicService {

	
	@Autowired
	private UploadPicDao dao;

	@Override
	public List<UploadPic> findByIds(List<Long> longs) {
		return dao.findAllById(longs);
	}
	

}
