package com.zeusas.dp.exhibit.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class AppConfig {
	final static String SYS_PROP_PID = "pid";
	final static String APP_ID = "APP_ID";
	final static String MASTER_NODE = "MASTER";
	
	static Logger logger = LoggerFactory.getLogger(AppConfig.class);
	
	final static Properties config = new Properties();
	static {
		load("config.properties");
		load(new File("config/config.properties"));
		load(new File("etc/config.properties"));
		load("jdbc.properties");
		load(new File("etc/jdbc.properties"));
		load(new File("config/jdbc.properties"));
	}

	private static void load(String res) {
		InputStream in = null;
		try {
			in = IOUtils.getResourceAsStream(res);
			if (in != null) {
				config.load(in);
			}
		} catch (Exception e) {
			System.err.println("WRAN Resource:" + res + " not exist.");
		} finally {
			IOUtils.close(in);
		}
	}

	
	private static void load(File file) {
		InputStream in = null;
		try {
			if (file.exists()) {
				in = new FileInputStream(file);
				config.load(in);
			}
		} catch (Exception e) {
			System.err.println("WRAN: File :" + file.getName() + " not exist.");
		} finally {
			IOUtils.close(in);
		}
	}

	/**
	 * 取得配置项
	 * 
	 * @param key
	 * @return
	 */
	public static String getString(String key) {
		return (config.containsKey(key)) ? config.getProperty(key) : "";
	}

	public static boolean getBoolean(String key) {
		return QString.toBoolean(config.getProperty(key));
	}

	public static Integer getInteger(String key) {
		return QString.toInt(config.getProperty(key));
	}

	public static Long getLong(String key) {
		return QString.toLong(config.getProperty(key));
	}

	public static String getPID() {
		String pid = System.getProperty(SYS_PROP_PID);
		if (pid == null) {
			RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
			String processName = runtimeMXBean.getName();
			if (processName.indexOf('@') != -1) {
				pid = processName.substring(0, processName.indexOf('@'));
				System.setProperty(SYS_PROP_PID, pid);
			}
		}
		return pid;
	}
	
	public static boolean isOSX() {
		String osName = System.getProperty("os.name");
		return osName == null ? false : osName.toUpperCase().endsWith("X");
	}

	final static String APP_NODE_ID;
	static {
		String node = config.getProperty("NODE");
		if (System.getProperty("NODE") != null) {
			// Pass the NODE ID from command line: -DNODE=MASTER
			APP_NODE_ID = System.getProperty("NODE");
		} else if (Strings.isNullOrEmpty(node)) {
			APP_NODE_ID = UUID.randomUUID().toString();
			logger.warn("WARN: 未设置计算节点ID！随机指定 ID={}", APP_NODE_ID);
		} else {
			APP_NODE_ID = node;
		}
	}
	
	/**
	 * 取得应用ID，应用ID通过 APP_ID= 在config.properties中定义
	 * @return 取得应用ID
	 */
	public static String getAppId() {
		String appId = config.getProperty(APP_ID);
		return appId==null?"":appId;
	}
	
	/** 机器身份标识，每次启动后，标识都不一样 */
	public static String getAppNodeId() {
		return APP_NODE_ID;
	}
	
	public static boolean isMasterNode() {
		return "MASTER".equalsIgnoreCase(APP_NODE_ID);
	}
}
