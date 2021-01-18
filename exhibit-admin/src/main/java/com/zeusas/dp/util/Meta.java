package com.zeusas.dp.util;

/*
 * Copyright 2017, Bright Grand Technologies Co., Ltd.
 * 
 * 版权所有，明弘科技(北京)有限公司，2017
 * 
 */
import java.io.Serializable;
import java.util.Arrays;

import org.dom4j.Element;

import com.google.common.base.Objects;
import com.zeusas.dp.exhibit.utils.QString;

public class Meta implements Serializable {
	/** serialVersionUID */
	private static final long serialVersionUID = -1157069247381702240L;

	final static String TAG_ID = "id";
	final static String TAG_FIELD = "field";
	final static String TAG_KEY = "key";
	final static String TAG_ALIAS = "alias";
	final static String TAG_PROPERTY = "property";
	final static String TAG_STMT = "stmt";
	final static String TAG_META = "meta";
	final static String TAG_DDL = "DDL";

	static final String TAG_PROC = "proc";
	static final String TAG_TYPE = "type";
	static final String TAG_SEQUENCE = "sequence";
	static final String TAG_WHEN = "when";
	static final String TAG_CACHEABLE = "cacheable";
	static final String TAG_OPERATION = "operation";
	static final String TAG_QUERY = "query";
	static final String TAG_UPDATE = "update";

	static final String TAG_UPDATE_BYFIELD = "updateByField";
	static final String TAG_UPDATE_BYPROPERTY = "updateByProperty";

	static final String TAG_INSERT = "insert";

	static final String TAG_SCOPE = "scope";
	static final String TAG_EXPIRED = "expired";

	public final static int OP_QUERY = 0;
	public final static int OP_INSERT = 1;
	public final static int OP_UPDATE = 2;
	public final static int OP_PROC = 4;

	public final static int OP_UPDATE_BYFIELD = 21;
	public final static int OP_UPDATE_BYPROPERTY = 22;

	public static final String TAG_DATASOURCE = "datasource";

	// id：对应table id(name)
	private String id;
	/** 数据库字段定义，如果无定义，从ResultMeta中取得 */
	private String[] field;
	/** 别名，用于显示用 */
	private String[] alias;
	/** 主键字段名，最多 XXX:3个 */
	private String[] pkName;
	/** 字段对应属性名 */
	private String[] property;
	/** 定义类型 */
	private int[] type;
	/** SQL 语句 */
	private String stmt;
	// 定义cache访问
	private Integer scope;
	// 定义cache的时间
	private Integer expired;
	// 是否需要Cache
	private boolean cacheable;

	public Meta() {
		cacheable = false;
	}

	public Meta(String id, String[] pk, String[] property, String stmt) {
		this();
		this.id = id;
		this.setPkName(pk);
		this.setProperty(property);
		this.stmt = DataDDL.preprocess(stmt);
	}

	Meta init(Element ddl) {
		id = ID.valueOf(Dom4j.getAttr(ddl, TAG_ID));
		pkName = DataDDL.toArrayUpper(ddl.elementText(TAG_KEY));

		field = shifts(DataDDL.toArrayUpper(ddl.elementText(TAG_FIELD)));
		alias = shifts(QString.split(ddl.elementText(TAG_ALIAS)));
		property = shifts(QString.split(ddl.elementText(TAG_PROPERTY)));

		scope = QString.toInt(ddl.elementText(TAG_SCOPE));
		expired = QString.toInt(ddl.elementText(TAG_EXPIRED));

		cacheable = QString.toBoolean(ddl.elementText(TAG_CACHEABLE));

		String vstmt = ddl.elementText(TAG_STMT);
		if (vstmt == null //
				|| vstmt.isEmpty()) {
			throw new IllegalArgumentException("处理结点必須有statement，且语句不能为空。");
		}
		stmt = DataDDL.preprocess(vstmt);
		return this;
	}

	static String[] shifts(String arr[]) {
		if (arr == null || arr.length == 0) {
			return arr;
		}
		String[] vv = new String[arr.length + 1];
		System.arraycopy(arr, 0, vv, 1, arr.length);
		return vv;
	}

	public boolean isCacheable() {
		return cacheable;
	}

	public boolean allCache() {
		return this.expired == null && this.scope == null;
	}

	@Override
	public String toString() {
		return stmt;
	}

	public String getId() {
		return id;
	}

	public String[] getField() {
		return field == null ? null : Arrays.copyOf(field, field.length);
	}

	public String[] getAlias() {
		return alias == null ? null : Arrays.copyOf(alias, alias.length);
	}

	public String[] getPkName() {
		return pkName == null ? QString.EMPTYS : Arrays.copyOf(pkName, pkName.length);
	}

	public String[] getProperty() {
		return property == null ? QString.EMPTYS : property.clone();
	}

	public Integer getScope() {
		return scope;
	}

	public Integer getExpired() {
		return expired;
	}

	public String getStmt() {
		return stmt;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setField(String[] field) {
		this.field = Arrays.copyOf(field, field.length);
	}

	public void setAlias(String[] alias) {
		this.alias = Arrays.copyOf(alias, alias.length);
	}

	public void setPkName(String[] pkName) {
		this.pkName = Arrays.copyOf(pkName, pkName.length);
	}

	public void setProperty(String[] property) {
		this.property = Arrays.copyOf(property, property.length);
	}

	public void setStmt(String stmt) {
		this.stmt = DataDDL.preprocess(stmt);
	}

	public void setType(int type[]) {
		this.type = Arrays.copyOf(type, type.length);
	}

	public int[] getType() {
		return type == null ? null : Arrays.copyOf(type, type.length);
	}

	@Override
	public int hashCode() {
		return this.id == null ? 0 : this.id.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || !(o instanceof Meta))
			return false;
		Meta b = (Meta) o;
		return Objects.equal(this.id, b.id) //
				&& Objects.equal(this.stmt, b.stmt);
	}
}
