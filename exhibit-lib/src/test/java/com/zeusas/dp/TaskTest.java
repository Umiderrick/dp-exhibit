package com.zeusas.dp;

import org.junit.Before;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class TaskTest {
	FileSystemXmlApplicationContext ctx ;
	@Before
	public void setUp(){
	 ctx = new FileSystemXmlApplicationContext("classpath:unit-test.xml");
	ctx.start();
	}


}
