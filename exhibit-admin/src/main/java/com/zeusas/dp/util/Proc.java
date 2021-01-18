package com.zeusas.dp.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.zeusas.dp.exhibit.utils.QString;
import com.zeusas.dp.exhibit.utils.TypeConverter;

/**
 * 定义处理过程中的处理SQL描述
 * <pre>
 * 		&lt;proc id="PROC_counters">
 * 		&lt;operation>update&lt;/operation>
 *			&lt;type>4&lt;/type>
 *			&lt;name>counters&lt;/name>
 *			&lt;stmt>
 *				&lt;![CDATA[ 
 *				INSERT INTO getstringint(fieldint) values(?)
 *				]]>
 *			&lt;/stmt>
 *		&lt;/proc>
 *		</pre>
 */
public class Proc implements Comparable<Proc> {
	private static Logger log = LoggerFactory.getLogger(Proc.class);

	// the id number of a proc
	final String id;
	// the type of a PROC
	final int operation;
	// SQL statement
	final String stmt;
	// 处理顺序（多个Proc的情况下）
	final int sequence;
	// 对应字段
	String field[];
	// 对应类的属性
	String property[];
	// java.sql.Types 定义数据字段类型
	int type[];

	public Proc(String id, String stmt) {
		this.id = id;
		this.stmt = DataDDL.preprocess(stmt);
		this.operation = Meta.OP_UPDATE;
		this.sequence = 0;
	}

	Proc(Element e) {
		id = Dom4j.getAttr(e, Meta.TAG_ID);
		switch (Dom4j.getText(e.element(Meta.TAG_OPERATION))) {
		case Meta.TAG_UPDATE:
			operation = Meta.OP_UPDATE;
			break;
		case Meta.TAG_INSERT:
			operation = Meta.OP_INSERT;
			break;
		case Meta.TAG_PROC:
			operation = Meta.OP_PROC;
			break;
		case Meta.TAG_UPDATE_BYFIELD:
			operation = Meta.OP_UPDATE_BYFIELD;
			break;
		case Meta.TAG_UPDATE_BYPROPERTY:
			operation = Meta.OP_UPDATE_BYPROPERTY;
			break;
		default:
			operation = Meta.OP_QUERY;
		}
		stmt = DataDDL.preprocess(Dom4j.getText(e.element(Meta.TAG_STMT)));
		sequence = QString.toInt(Dom4j.getText(e.element(Meta.TAG_SEQUENCE)), 99);
		field = Meta.shifts(DataDDL.toArrayUpper(e.elementText(Meta.TAG_FIELD)));
		property = Meta.shifts(DataDDL.toArrayUpper(e.elementText(Meta.TAG_PROPERTY)));
		String types = Dom4j.getText(e.element(Meta.TAG_TYPE));
		// 处理TYPE
		if (types != null) {
			String tt[] = QString.split(types);
			type = new int[tt.length + 1];
			for (int i = 0; i < tt.length; i++) {
				type[i + 1] = TypeConverter.toInteger(tt[i], 0);
			}
		}
		if (id == null || Strings.isNullOrEmpty(stmt)) {
			throw new IllegalArgumentException("XXX: 处理结点必須有ID，且语句不能为空。");
		}
	}

	public String getStatement() {
		return stmt;
	}

	public int execUpdate(Connection conn, Object... args) throws SQLException {
		if (log.isDebugEnabled()) {
			log.debug("执行：{},参数：{}", stmt, args);
		}
		if (type!=null && type.length>0) {
			return QueryHelper.execUpdate(conn, stmt, type, args);
		}
		return QueryHelper.execUpdate(conn, stmt, args);
	}

	public String id() {
		return id;
	}

	public int[] type() {
		if (type.length == 0)
			return null;
		int t[] = new int[type.length];
		System.arraycopy(type, 0, t, 0, type.length);
		return t;
	}

	public void type(int[] tt) {
		if (tt != null && tt.length > 0) {
			type = new int[tt.length];
			System.arraycopy(tt, 0, type, 0, tt.length);
		}
	}
	
	public int operation() {
		return operation;
	}

	public String getStmt() {
		return stmt;
	}

	public int getSequence() {
		return sequence;
	}

	@Override
	public int compareTo(Proc pe) {
		return id.compareTo(pe.id);
	}

	@Override
	public int hashCode() {
		return id == null ? 0 : id.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || !(o instanceof Proc))
			return false;
		Proc a = (Proc) o;
		return Objects.equals(this.id, a.id)//
				&& Objects.equals(this.stmt, a.stmt)//
				&& Objects.equals(this.operation, a.operation) //
				&& Objects.equals(this.sequence, a.sequence);
	}

	@Override
	public String toString() {
		return stmt;
	}
}