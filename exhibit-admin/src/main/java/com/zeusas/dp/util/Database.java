package com.zeusas.dp.util;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zeusas.dp.exhibit.utils.BeanMap;
import com.zeusas.dp.exhibit.utils.ServiceException;

/**
 * 数据库对象
 * <p/>
 * 数据库对象用于数据库中的视图装入、CACHE和Meta的管理。
 * 
 * @sinces 1.0.0
 */
public final class Database {
	static final Logger log = LoggerFactory.getLogger(Database.class);

	/** 定义JDBC 链接 */
	protected final Map<String, Table> tables = new ConcurrentHashMap<>();

	/** 定义表相关的 TAG，是一个脚本文件 */
	protected final DataDDL ddl;

	protected DataSource dataSource;

	private final static ThreadLocal<Connection> myConnection = new ThreadLocal<>();

	public Database(DataDDL ddl) {
		this.ddl = ddl;
	}

	/**
	 * Database 构造函数<P>
	 * 如果使用容器外的DataSource时，系统使用jdbc.properties配置，自动创建<br>
	 * 如果不配置最大连接数，最大连接数默认为8.<br>
	 * 如果使用容器内DataSource，显示构造。Database(String ddlName, DataSource dataSource)<br>
	 * 注意：如果使用jdbc,每次会加载驱动，并创建 连接。
	 * @param ddlName
	 */
	public Database(String ddlName) {
		this(DBMS.getItem(ddlName));
	}

	/**
	 * 
	 * @param ddlName
	 * @param dataSource
	 */
	public Database(String ddlName, DataSource dataSource) {
		this(DBMS.getItem(ddlName), dataSource);
	}

	/**
	 * 
	 * @param ddl
	 * @param dataSource
	 */
	public Database(DataDDL ddl, DataSource dataSource) {
		this.ddl = ddl;
		this.dataSource = dataSource;
	}

	public DataDDL getDDL() {
		return ddl;
	}

	public void beginTransaction() throws SQLException {
		connect(false);
	}

	public void rollback() {
		try {
			this.connect().rollback();
		} catch (SQLException e) {
			throw new ServiceException(e);
		}
	}

	public void commit() {
		try {
			this.connect().commit();
		} catch (SQLException e) {
			throw new ServiceException(e);
		}
	}

	public Meta getMeta(String metaId) {
		return this.ddl.getMeta(metaId);
	}

	/**
	 * 连接数据库，在数据库对象中，有唯一的一个连接，被数据库全局使用。
	 * 
	 * @return 数据库连接
	 * @throws SQLException
	 */
	public final Connection connect() throws SQLException {
		return connect(true);
	}

	public final Connection connect(boolean autoCommit) throws SQLException {
		Connection conn = myConnection.get();
		if (conn != null && !conn.isClosed()) {
			log.debug("XXX: 复用链接:{}", conn);
			conn.clearWarnings();
		} else {
			conn = createConnection();
			log.debug("XXX: 放入连接到ThreadLocal<>: {}", conn);
			myConnection.set(conn);
			conn.setAutoCommit(autoCommit);
		}
		return conn;
	}

	public final Connection createConnection() throws SQLException {
		Connection conn = null;
		conn = dataSource.getConnection();
		log.debug("XXX:创建连接:{}/{}", ddl.dsName, conn);
		return conn;
	}



	/**
	 * 根据MetaID,打开一个表。
	 * @see #open(String, Object...)
	 * @param tableId 表的metaID
	 * @return
	 * @throws SQLException
	 */
	public Table open(String tableId) throws SQLException {
		Connection myConn = null;
		Meta meta = ddl.getMeta(tableId);
		try {
			myConn = createConnection();
			Table tb = new Table(meta, myConn);
			String stmt = meta.getStmt();
			log.debug("SQL:" + stmt);
			tb.open(stmt);
			
			if (meta.isCacheable()) {
				tables.put(meta.getId(), tb);
			}
			myConn.close();
			myConn = null;
			return tb;
		} catch (SQLException e) {
			log.error("Error, SQL=", meta.getStmt());
			throw e;
		} catch (Exception e) {
			throw new SQLException(e);
		} finally {
			QueryHelper.close(myConn);
		}
	}

	/**
	 * 根据MetaID,带条件，打开一个表。 
	 * @param name
	 * @param table
	 * @param args
	 * @return
	 * @throws SQLException
	 */
	public Table open(String table, Object... args) throws SQLException {
		Meta meta = ddl.getMeta(table);
		if (meta == null) {
			throw new ServiceException("在DDL: " + ddl + "中未定义表：" + table);
		}
		Connection connection = createConnection();
		Table tb = new Table(meta, connection);
		try {
			String stmt = meta.getStmt();
			log.debug("SQL:{}", stmt);
			tb.open(stmt, args);
		} finally {
			QueryHelper.close(connection);
		}
		if (meta.isCacheable()) {
			tables.put(meta.getId(), tb);
		}
		return tb;
	}

	public String[] getTableFields(String tableID) {
		Table t = tables.get(tableID);
		return t.COL_NAME.clone();
	}

	public Table getTable(String tid) {
		return tables.get(tid);
	}

	/**
	 * 从Database对象中，移除一个表。
	 * 
	 * @param table
	 */
	public void removeTabe(String table) {
		Table tb = tables.get(table);
		if (tb != null) {
			tb.clear();
			tables.remove(table);
		}
	}

	public int execUpdate(String procID, Object... args) throws SQLException {
		Proc proc = ddl.getProc(procID);
		Connection conn = connect();
		return proc.execUpdate(conn, args);
	}

	public int execUpdateBy(String procId, Object param) throws SQLException {
		Proc proc = ddl.getProc(procId);
		if (proc.operation == Meta.OP_UPDATE_BYPROPERTY) {
			return updateByProperty(procId, param);
		}
		return updateByField(procId, param);
	}

	public int updateByField(String procId, Object values) throws SQLException {
		Proc proc = ddl.getProc(procId);

		Map<String, Object> params = BeanMap.beanToMapA(values);

		Object args[] = new Object[proc.field.length];
		for (int i = 0; i < proc.field.length; i++) {
			args[i] = params.get(proc.field[i]);
		}
		Connection conn = connect();
		return proc.execUpdate(conn, args);
	}

	public int updateByProperty(String procId, Object param) throws SQLException {
		Proc proc = ddl.getProc(procId);

		Map<String, Object> params = BeanMap.beanToMapA(param);

		Object args[] = new Object[proc.field.length];
		for (int i = 0; i < proc.field.length; i++) {
			args[i] = params.get(proc.field[i]);
		}
		Connection conn = connect();

		return proc.execUpdate(conn, args);
	}

	public final void closeAll() {
		// 清除表中的数据，关闭表
		tables.values().forEach(e -> {
			e.close();
		});
		this.close();
	}

	public final void close() {
		Connection conn = myConnection.get();
		if (conn != null) {
			QueryHelper.close(conn);
			log.debug("VVV: 移出 ThreadLocal<>:{} / {}", ddl.dsName, conn);
			myConnection.remove();
		}
	}
}
