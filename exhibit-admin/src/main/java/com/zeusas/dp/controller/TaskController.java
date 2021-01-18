/**
 * 
 */
package com.zeusas.dp.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zeusas.dp.adminservice.TaskPointManager;
import com.zeusas.dp.exhibit.entity.Counter;
import com.zeusas.dp.exhibit.service.CounterManager;
import com.zeusas.dp.exhibit.utils.DSResponse;

/**
 * @author pengbo
 *
 */
@RestController
public class TaskController {

	
	@Autowired
	private TaskPointManager tpm;
	
	@Autowired
	private CounterManager ctm;
	
	@RequestMapping("doreload")
	public String taskdashboard(int i){
		tpm.doReload(i);
		return "success";
	}
	
	@RequestMapping("counter/getallcounters.do")
	public DSResponse getallcounters(){
		Collection<Counter> findAll = ctm.findAll();
		return new DSResponse(findAll);
	}
}
