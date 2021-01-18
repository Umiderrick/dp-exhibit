package com.zeusas.dp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zeusas.dp.exhibit.utils.IOUtils;


/**
 * DBMS Meta management class
 */
public final class DBMS {
	static final Logger log = LoggerFactory.getLogger(DBMS.class);

	final static Map<String, DataDDL> ddls = new HashMap<String, DataDDL>();
	final static Map<String, Long> lastUpdate = new HashMap<String, Long>();
	final static Map<String, String> jdbc = new HashMap<>();

	public static Connection getConnection(DataSource ds) //
			throws SQLException {
		return ds.getConnection();
	}

	/**
	 * 使用源生的JDBC参数，建立连接。
	 * 
	 * @param jdbcName
	 * @return
	 * @throws SQLException
	 */
	public static Connection getJdbcConnection(String url,String username ,String password) throws SQLException {
		// 如果已经定义了连接池，从连接池取得
		Connection conn = DriverManager.getConnection(url, username, password);
		return conn;
	}

	public static DataSource getJndiDataSource(String jndiName) //
			throws SQLException {
		Context initContext;
		try {
			initContext = new InitialContext();
			Context ctx = (Context) initContext.lookup("java:/comp/env");
			return (DataSource) ctx.lookup(jndiName);
		} catch (Exception e) {
			log.error("JNDI数据源:{}取得连接错误.", jndiName);
			throw new SQLException(e);
		}
	}

	/**
	 * Load DDL xml file
	 * @param fxml DDL xml file
	 * @return load success or NOT
	 */
	public static boolean load(File fxml) {
		String fullName = fxml.getAbsolutePath();
		Long t = lastUpdate.get(fullName);
		if (t != null && t == fxml.lastModified()) {
			return false;
		}
		boolean success = false;
		lastUpdate.put(fullName, fxml.lastModified());
		InputStream input = null;
		try {
			input = new FileInputStream(fxml);
			load(input);
			success = true;
		} catch (IOException e) {
			log.error("Load file:{} error.", fxml.getName(), e);
		} finally {
			IOUtils.close(input);
		}
		return success;
	}

	static void load(InputStream input) throws IOException {
		Document doc = Dom4j.getXmlDoc(input);
		Element root = doc.getRootElement();
		List<Element> nodes = root.elements(Meta.TAG_DDL);
		for (Element e : nodes) {
			DataDDL ddl = new DataDDL().init(e);
			// 检查是否存在旧的项，如果不存在，加入
			DataDDL origDdl = ddls.get(ddl.getId());
			if (origDdl == null) {
				ddls.put(ddl.getId(), ddl);
			} else if (origDdl.dsName.equals(ddl.dsName) //
					&& origDdl.dsType.equals(ddl.dsType)) {
				origDdl.setMetas(ddl.getMetas());
				origDdl.setProcs(ddl.getProcs());
			} else {
				log.error("DDL ITEM 定义重复，并且不可合并！,DDL ID={}", ddl.getId());
			}
		}
	}

	public static boolean load(String xmlResource) {
		log.info("DDL 装入资源文件：{}", xmlResource);
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream input = null;
		boolean success = false;
		try {
			input = loader.getResourceAsStream(xmlResource);
			load(input);
			success = true;
		} catch (Exception e) {
			log.error("装入DDL资源错误{}", xmlResource, e);
		} finally {
			IOUtils.close(input);
		}
		return success;
	}

	public static DataDDL getItem(String id) {
		return ddls.get(id);
	}

	public static Collection<DataDDL> values() {
		return ddls.values();
	}
}
