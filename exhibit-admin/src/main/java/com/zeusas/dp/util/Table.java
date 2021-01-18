package com.zeusas.dp.util;

/*
 * Copyright 2017, Bright Grand Technologies Co., Ltd.
 * 
 * 版权所有，明弘科技(北京)有限公司，2017
 * 
 */
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.zeusas.dp.exhibit.utils.ServiceException;
import com.zeusas.dp.exhibit.utils.TypeConverter;

/**
 * 数据库中表{@code Table}或视图获取存储对象。
 * 
 * <p>
 * 表在操作中，使用Meta中定义的结构、SQL定义，从数据 库中取得。如果定义了索引，在建立表示，自动建立<K,V>的 索引。
 * 
 * @author BGT R&D Center
 * @see com.zeusas.data.Database
 * @see com.zeusas.data.Record
 * @see com.zeusas.data.Meta
 * @since 1.0
 */
public class Table implements Serializable {
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(Table.class);

	public final Meta meta;
	/** 字段名 */
	protected String[] COL_NAME = null;
	/** 列中的类名 */
	protected String[] col_class = null;
	/** 列字段别名 */
	protected String[] col_alias = null;
	// 字段类型
	protected int[] col_type = null;

	/** 字段对应在行中的位置 */
	protected final Map<String, Integer> COL_INDEX = new LinkedHashMap<>();
	protected final Map<String, Integer> PROP_INDEX = new LinkedHashMap<>();
	/** 默认无主键 */
	protected final int pkNumber;
	// 主键对应的位置
	protected final int[] pkIndex = new int[3];
	// 对应字段名
	protected final String[] pkIndexName = new String[3];

	/** 列数 */
	protected int fieldLen;

	protected transient Connection conn;

	protected final List<Record> data = new ArrayList<>();

	protected Map<PK, Record> index = null;

	protected long lastUpdate;

	public Table(Connection conn) {
		this(new Meta(), conn);
	}

	public Table(Meta meta, Connection conn) {
		this.conn = conn;
		this.meta = meta;
		this.pkNumber = meta == null ? 0 : meta.getPkName().length;
		if (meta != null) {
			pkIndex[0] = pkIndex[1] = pkIndex[2] = -1;
			if (pkNumber > 0) {
				System.arraycopy(meta.getPkName(), 0, pkIndexName, 0, pkNumber);
			}
			for (int i = 1; i < meta.getProperty().length; i++) {
				PROP_INDEX.put(meta.getProperty()[i], i);
			}
		}
		this.lastUpdate = System.currentTimeMillis();
	}

	public Record getByIndex(int idx) {
		return data.get(idx);
	}

	public Record get(PK pk) {
		return index.get(pk);
	}

	public Record get(Object a) {
		return index.get(new PK(a));
	}

	public Record get(Object a, Object b) {
		return index.get(new PK(a, b));
	}

	public Record get(Object a, Object b, Object c) {
		return index.get(new PK(a, b, c));
	}

	public Record get(Object[] a) {
		switch (a.length) {
		case 1:
			return index.get(new PK(a[0]));
		case 2:
			return index.get(new PK(a[0], a[1]));
		case 3:
			return index.get(new PK(a[0], a[1], a[2]));
		default:
			;
		}
		throw new UnsupportedOperationException();
	}

	private void buildMeta(int len) {
		COL_NAME = new String[len];
		col_class = new String[len];
		col_type = new int[len];
		col_alias = new String[len];

		meta.setField(COL_NAME);
		meta.setAlias(COL_NAME);
	}

	/**
	 * 根据一行，取得记录的集合表示。
	 * 
	 * @param rowData
	 * @return
	 */
	public final Map<String, Object> getRowMap(Record rowData) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		for (int i = 1; i < COL_NAME.length; i++) {
			map.put(COL_NAME[i], rowData.get(i));
		}
		return map;
	}

	public final int getPropertyIndexByName(String prop) throws ServiceException {
		Integer idx = this.PROP_INDEX.get(prop);
		if (idx == null) {
			logger.error("META:{}属性{}不存在！", meta.getId(), prop);
			throw new ServiceException("属性不存在。");
		}
		return idx.intValue();
	}

	public final int getColumnIndexByName(String fldName) throws ServiceException {
		String t = fldName.toUpperCase();
		Integer idx = COL_INDEX.get(t);
		if (idx == null) {
			throw new ServiceException("数据库表：【" + meta.getId() + "】不存在字段：" + fldName);
		}
		return idx.intValue();
	}

	public final PK getPK(Record row) {
		switch (this.pkNumber) {
		case 1:
			return new PK(row.get(pkIndex[0]));
		case 2:
			return new PK(row.get(pkIndex[0]), row.get(pkIndex[1]));
		case 3:
			return new PK(row.get(pkIndex[0]), row.get(pkIndex[1]), row.get(pkIndex[2]));
		default:
			return null;
		}
	}

	protected Connection getConnection() {
		return this.conn;
	}

	public void open() throws SQLException {
		open(meta.getStmt());
	}

	public void open(String sql) throws SQLException {
		// 如果没有写出，直接使用表名构造一条SQL语句。
		Statement pstmt = conn.createStatement();

		ResultSet rs = null;
		// 初始化媒体
		try {
			rs = pstmt.executeQuery(sql);
			// 初始化Meta
			initMetaData(rs);
			data.clear();
			while (rs.next()) {
				Record row = toRecord(rs);
				data.add(row);
			}
			if (this.pkNumber != 0) {
				createIndex();
			}
		} finally {
			// 关闭 ResultSet
			QueryHelper.close(rs);
			QueryHelper.close(pstmt);
		}
		lastUpdate = System.currentTimeMillis();
	}

	public Map<PK, List<Record>> createClusterIndex(String... flds) {
		Integer[] idx = new Integer[flds.length];
		for (int i = 0; i < flds.length; i++) {
			idx[i] = this.COL_INDEX.get(flds[i].toUpperCase());
		}
		return data.parallelStream()//
				.collect(Collectors.groupingBy(e -> toPK(e, idx)));
	}

	private static PK toPK(Record r, Integer[] idx) {
		if (idx.length == 1)
			return new PK(r.get(idx[0]));
		if (idx.length == 2)
			return new PK(r.get(idx[0]), r.get(idx[1]));

		return new PK(r.get(idx[0]), r.get(idx[1]), r.get(idx[1]));
	}

	private void createIndex() {
		index = new ConcurrentHashMap<>(data.size() * 4 / 3);
		data.parallelStream().forEach(e -> {
			index.put(e.getPK(), e);
		});
	}

	/**
	 * 根据SQL语句，打开一张表。
	 * 
	 * @param conn JDBC 链接
	 * @param sql  SQL语句
	 * @param args 条件参数表
	 * @throws SQLException
	 */
	public void open(String sql, Object... args) throws SQLException {
		// 如果没有写出，直接使用表名构造一条SQL语句。
		PreparedStatement pstmt = QueryHelper.preparedStatement(conn, sql, args);

		ResultSet rs = null;
		// 初始化媒体
		try {
			rs = pstmt.executeQuery();
			// 初始化Meta
			initMetaData(rs);
			data.clear();
			while (rs.next()) {
				Record row = toRecord(rs);
				data.add(row);
			}
			if (this.pkNumber != 0) {
				createIndex();
			}
			this.lastUpdate = System.currentTimeMillis();
		} finally {
			// 关闭 ResultSet
			QueryHelper.close(rs);
			QueryHelper.close(pstmt);
		}
	}

	public void reopen(Connection conn, String sql, Object... args) throws SQLException {
		this.conn = conn;
		this.open(sql, args);
	}

	/**
	 * 生成新的数据行
	 * 
	 * @param key
	 * @return RowData
	 */
	public final Record toRecord(Object... args) {
		Record row = new Record(COL_NAME.length);
		for (int i = 1; i < args.length && i < COL_NAME.length; i++) {
			row.set(i, args[i]);
		}
		return row;
	}

	final void initMetaData(ResultSet rs) throws SQLException {
		logger.debug("初始化 META...");
		// 取得数据库记录的MetaData
		ResultSetMetaData resultMeta = rs.getMetaData();
		// 获得字段数
		fieldLen = resultMeta.getColumnCount();
		// JDBC 字段从1开始，第一个为空占位
		this.buildMeta(fieldLen + 1);

		for (int i = 1; i < meta.getField().length; i++) {
			COL_INDEX.put(meta.getField()[i], i);
		}
		for (int i = 1; i <= fieldLen; i++) {
			col_class[i] = resultMeta.getColumnClassName(i);
			col_type[i] = resultMeta.getColumnType(i);

			String fname;

			fname = resultMeta.getColumnName(i).toUpperCase();
			int idx = fname.lastIndexOf('.');
			if (idx >= 0) {
				fname = fname.substring(idx + 1);
			}

			COL_NAME[i] = fname;
			COL_INDEX.put(fname, i);

			if (fname.equals(pkIndexName[0])) {
				pkIndex[0] = i;
			} else if (fname.equals(pkIndexName[1])) {
				pkIndex[1] = i;
			} else if (fname.equals(pkIndexName[2])) {
				pkIndex[2] = i;
			}
		}
		col_alias[0] = "PK";
		// 默认表明天表中定义的名字
		int len = col_alias.length;
		if (meta.getAlias().length != len) {
			System.arraycopy(COL_NAME, 0, col_alias, 0, len);
		} else {
			System.arraycopy(meta.getAlias(), 0, col_alias, 0, len);
		}
		for (int i = 0; i < meta.getPkName().length; i++) {
			if (pkIndex[i] < 0) {
				logger.error("Meta: {} 索引{}定义错误！", meta.getId(), meta.getPkName()[i]);
			}
		}
	}

	protected final Record toRecord(ResultSet rs) throws SQLException {
		Record row = new Record(fieldLen);
		for (int i = 1; i <= fieldLen; i++) {
			row.set(i, rs.getObject(i));
		}
		PK pk = (pkNumber == 0) ? null : getPK(row);
		row.setPK(pk);
		return row;
	}

	/**
	 * 查找一列中，查找所有存在串arg的数据，返回行集。
	 * 
	 * @param fld 字段名
	 * @param arg 参数
	 * @return 行集表
	 */
	public final List<Record> findLike(String fld, String arg) {
		int idx = COL_INDEX.get(fld.toUpperCase());
		return values().parallelStream().filter(e -> e.get(idx).toString().indexOf(arg) >= 0)
				.collect(Collectors.toList());
	}

	public final List<Record> findFullTextLike(String arg) {
		return values().parallelStream().filter(e -> e.toString().indexOf(arg) >= 0).collect(Collectors.toList());
	}

	/**
	 * 取出行集中指字段的数据
	 * 
	 * @param row 数据库表在的一行
	 * @param fld 字段名
	 * @return 字段对应的数据
	 * @throws SQLException
	 */
	public final Object getFieldData(Record row, String fld) {
		Integer idx = COL_INDEX.get(fld.toUpperCase());
		if (idx == null) {
			return null;
		}
		return row.get(idx);
	}

	public final String getFieldString(Record row, String fld) {
		Integer idx = COL_INDEX.get(fld.toUpperCase());
		if (idx == null) {
			return null;
		}
		Object obj = row.get(idx);
		if (obj == null || (obj instanceof String)) {
			return (String) obj;
		}
		return obj.toString();
	}

	public final Integer getFieldInteger(Record row, String fld) {
		Integer idx = COL_INDEX.get(fld.toUpperCase());
		if (idx == null) {
			return null;
		}
		Object obj = row.get(idx);
		if (obj == null || (obj instanceof Integer)) {
			return (Integer) obj;
		}
		return TypeConverter.toInteger(row.get(idx));
	}

	protected static PK getRecordValue(Record rec, int ndx) {
		return new PK(rec.get(ndx));
	}

	/**
	 * 创建辅助索引,索引由键值和对应的一对多的关系。
	 * <p>
	 * XXX: 本方法会产生大量的计算，消耗CPU计算资源。
	 * 
	 * @param idxKey 键值名
	 * @throws SQLException
	 */
	public Map<PK, List<Record>> createIndex(String idxKey) throws SQLException {
		final Integer ndxNo = COL_INDEX.get(idxKey.toUpperCase());
		// 如果列不存在，抛出异常，结束处理
		if (ndxNo == null) {
			throw new SQLException("不能创建表缩引，索引字段不存在。");
		}

		return data.parallelStream() //
				.collect(Collectors.groupingBy( //
						rec -> getRecordValue(rec, ndxNo)));
	}

	public List<Record> values() {
		return data;
	}

	public int size() {
		return data.size();
	}

	public void clear() {
		data.clear();
	}

	public final void close() {
		if (conn != null) {
			QueryHelper.close(conn);
		}
		conn = null;
	}

	public long lastUpdate() {
		return this.lastUpdate;
	}

	public <T> List<T> asArray(Class<T> voClass) {
		List<T> array = new LinkedList<T>();
		for (Record rec : this.data) {
			array.add(rec.toBean(this.meta, voClass));
		}
		return array;
	}

	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append('[');
		for (Record r : values()) {
			b.append(r.toJSONObject(meta)).append(',');
		}
		if (b.length() > 1) {
			b.setLength(b.length() - 1);
		}
		b.append(']');
		return b.toString();
	}

	public String toJSON() {
		StringBuilder b = new StringBuilder();
		b.append('[');

		for (Object r : this.data) {
			b.append(JSON.toJSONString(r)).append(',');
		}
		if (data.size() > 0) {
			b.setLength(b.length() - 1);
		}
		b.append(']');
		return b.toString();
	}

	/**
	 * 向表中插入数据
	 * 
	 * @param pk  主键
	 * @param rec 数据库记录 [0:PK][1]...[N]
	 */
	public void put(PK pk, Record rec) {
		rec.set(0, pk);
		Record rec0 = this.index.get(pk);
		if (rec0 != null) {
			this.data.remove(rec0);
		}
		this.index.put(pk, rec);
		this.data.add(rec);
	}

	public String[] getColName() {
		return Arrays.copyOf(COL_NAME, COL_NAME.length);
	}

	public String[] getColClass() {
		return Arrays.copyOf(col_class, col_class.length);
	}

	public int[] getColType() {
		return Arrays.copyOf(col_type, col_type.length);
	}
}
