package com.zeusas.dp.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import com.zeusas.dp.exhibit.utils.AppContext;


@Component
public class OnStartupServer implements CommandLineRunner {


	@Override
	public void run(String... args) {
		ApplicationContext ctx = AppContext.getApplicationContext();
		((AbstractApplicationContext) ctx).start();
	}

}
