/**
 * 
 */
package com.zeusas.dp.exhibit.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import org.springframework.stereotype.Service;

import com.zeusas.dp.exhibit.service.ReadGuideManager;

/**
 * @author pengbo
 *
 */
@Service
public class ReadGuideManagerImpl implements ReadGuideManager{

	
	private Map<String,Integer> readMap =new ConcurrentSkipListMap<>();
	
	@Override
	public void read(String code) {
		Integer res = readMap.get(code);
		if(res==null){
			res=0;
		}else{
			res++;
		}
		readMap.put(code,res);
	}

	@Override
	public void refresh() {
		readMap.clear();
	}

}
