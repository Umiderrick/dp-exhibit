package com.zeusas.dp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

	@RequestMapping("index")
	public String index() {
		return "index";
	}
	@RequestMapping("monthdata")
	public String monthdata() {
		return "monthdata";
	}
	@RequestMapping("viewPDF")
	public String viewPDF() {
		return "viewPDF";
	}
	@RequestMapping("wxme")
	public String wxme() {
		return "wxme";
	}
	@RequestMapping("wxlogin")
	public String wxlogin() {
		return "wxlogin";
	}
	@RequestMapping("wxapply")
	public String wxapply() {
		return "wxapply";
	}
	
	@RequestMapping("lamp")
	public String lamp() {
		return "lamp";
	}
	
	@RequestMapping("lampdetail")
	public String lampdetail() {
		return "lampdetail";
	}
	
	
	
}
