package com.zeusas.dp.util;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zeusas.dp.exhibit.utils.QString;
import com.zeusas.dp.exhibit.utils.TypeConverter;
import com.zeusas.dp.util.SearchInfo.Op;


/**
 * This class is 查询辅助类
 */
public class QueryHelper {
	private static Logger logger = LoggerFactory.getLogger(QueryHelper.class);
	// 参数表
	private final List<Object> params = new ArrayList<Object>();
	// 命名参数表
	private final Map<String, Object> namedParams = new LinkedHashMap<String, Object>();
	// SQL 脚本
	private final StringBuilder script = new StringBuilder(256);

	public QueryHelper() {
	}

	/**
	 * 带脚本的构造器
	 * 
	 * @param SQL脚本
	 */
	public QueryHelper(CharSequence sc) {
		script.append(sc);
	}

	public int length() {
		return script.length();
	}

	/**
	 * 设定长度。如果是负数，从当前的长度中减去其值，如果是非负数，设定为期长度。
	 * 
	 * @param len
	 */
	public void setLength(int len) {
		if (len < 0) {
			int newLen = script.length() + len;
			script.setLength(newLen < 0 ? 0 : newLen);
		} else if (len < script.length()) {
			script.setLength(len);
		}
	}

	/**
	 * 追加一个HQL的类名
	 * 
	 * @param cls 类名
	 * @return
	 */
	public QueryHelper append(Class<?> cls) {
		script.append(cls.getName());
		return this;
	}

	/**
	 * 追加脚本（语句或条件）
	 * 
	 * @param sc SQL脚本
	 * @return
	 */
	public QueryHelper append(CharSequence sc) {
		script.append(sc);
		return this;
	}

	/**
	 * 追加条件和参数
	 * 
	 * @param sc     条件部分脚本
	 * @param values 参数表
	 * @return
	 */
	public QueryHelper addParameters(CharSequence sc, Object... values) {
		script.append(sc);
		addParameter(values);
		return this;
	}

	public void addSearchInfos(List<SearchInfo> infos) {
		// 从参数表中，获得有效的项
		SearchInfo[] valid = infos.stream()//
				.filter(e -> e.valid())//
				.collect(Collectors.toList()).toArray(new SearchInfo[0]);
		// 如果为空，不处理
		if (valid.length == 0) {
			return;
		}
		// 取得已经存在的脚本，变为大写
		String s = script.toString().toUpperCase();
		// 检查是否存在WHERE？
		boolean hasWhere = s.indexOf(" WHERE ") > 0 || s.endsWith(" WHERE");
		script.append(hasWhere ? " " : " WHERE ");
		addSearchInfo(valid[0]);
		// 追加SearchInfo项
		for (int i = 1; i < valid.length; i++) {
			script.append(" ");
			if (valid[i].getLink() != null) {
				script.append(valid[i].getLink());
			} else {
				script.append("AND");
			}
			script.append(" ");
			addSearchInfo(valid[i]);
		}
	}

	protected void addSearchInfo(SearchInfo info) {
		Op opr = Op.of(info.getOp());
		StringBuilder b = new StringBuilder(64);
		b.append(info.getField());
		switch (opr) {
		case equal:
		case greater:
		case less:
		case lessorequal:
		case endwith:
		case startwith:
		case greaterorequal:
			b.append(opr.operator()).append('?');
			addParameters(b, info.getObjectValue());
			break;
		case like:
			b.append(opr.operator()).append('?');
			addParameters(b, "%" + info.getValue() + "%");
			break;
		case in:
		case notin:
			// FIXME: 需要测试确认
			String[] v = QString.split(info.getValue());
			switch (v.length) {
			case 0:
				// 当参数表为空时，不作操作
				break;
			case 1:
				// 当仅一个时，使用=,不使用IN
				b.append((opr == Op.in ? Op.equal : Op.notequal).operator()).append('?');
				addParameters(b, v[0]);
				break;
			default:
				b.append(opr.operator()).append(" (?");
				addParameter(v[0]);
				for (int i = 1; i < v.length; i++) {
					b.append(" ,?");
					addParameter(v[i]);
				}
				b.append(')');
				script.append(b);
			}
			break;
		default:
			;// 出错处理？
		}
	}

	/**
	 * 追加参数表, 参数表中参数，替换SQL中的？
	 * 
	 * @param values 参数表
	 */
	public QueryHelper addParameter(Object... values) {
		for (Object val : values) {
			this.params.add(val);
		}
		return this;
	}

	/**
	 * 设置以命名参数的参数表，如 select ... from ... where aaa=:aaa
	 * 
	 * @param name  参数名称
	 * @param value 参数对应的值
	 */
	public void setNamedParameter(String name, Object value) {
		this.namedParams.put(name, value);
	}

	/**
	 * 设置以命名参数的参数表，如 select ... from ... where aaa=:aaa
	 * 
	 * @param sc    SQL或HQL脚本
	 * @param name  参数名称
	 * @param value 参数对应的值
	 */
	public void setNamedParameter(String sc, String name, Object value) {
		this.script.append(' ').append(sc);
		this.namedParams.put(name, value);
	}

	public String getScript() {
		return qulifier(script);
	}

	public List<Object> getParameters() {
		return this.params;
	}

	public Map<String, Object> getNamedParaters() {
		return this.namedParams;
	}

	public String toString() {
		return script.toString();
	}

	static boolean isSpace(char c) {
		return c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == '　';
	}

	public static String qulifier(CharSequence psql) {
		assert (psql != null);

		int len = psql.length();
		int idx = 0;

		if (len <= 5) {
			return psql.toString().trim();
		}

		final StringBuilder bb = new StringBuilder(len);
		boolean comment = false;
		boolean lineComment = false;
		bb.append(psql.charAt(idx++));
		for (int i = idx; i < len; i++) {
			char c = psql.charAt(i);
			switch (c) {
			case '\n':
				if (lineComment) {
					lineComment = false;
				}
			case '\r':
			case '\t':
			case '\b':
			case ' ':
			case '　':// 全角空格也要滤掉
				// 如果是行注释，或块注释
				if (lineComment || comment) {
					break;
				}
				// 如果长度为0，不在前面加空格
				if (bb.length() == 0) {
					break;
				}
				// 前面一个不是空格的性况
				if (bb.charAt(bb.length() - 1) != ' ') {
					bb.append(' ');
				}
				break;
			case ',':
				if (!comment && !lineComment) {
					// 如果前面有空，
					if (bb.charAt(bb.length() - 1) == ' ') {
						bb.setLength(bb.length() - 1);
					}
					bb.append(c).append(' ');
				}
				break;
			case '*':
				if (psql.charAt(i - 1) == '/') {
					bb.setLength(bb.length() - 1);
					comment = true;
				} else if (!comment) {
					bb.append(c);
				}
				break;
			case '-':
				// 已经是注释的情况
				if (comment || lineComment) {
					break;
				}
				// 前一个是-,显然是注释
				if (psql.charAt(i - 1) == '-') {
					lineComment = true;
					bb.setLength(bb.length() - 1);
				} else {
					bb.append(c);
				}
				break;
			case '/':
				if (psql.charAt(i - 1) == '*') {
					comment = false;
					if (bb.charAt(bb.length() - 1) != ' ') {
						bb.append(' ');
					}
				} else if (psql.charAt(i - 1) == '/') {
					if (lineComment) {
						break;
					}
					// 当前一个为一时，且当前不是块注释，设为行注释
					if (!comment) {
						lineComment = true;
						bb.setLength(bb.length() - 1);
					}
				} else if (!comment) {
					bb.append(c);
				}
				break;
			default:
				if (!lineComment && !comment) {
					bb.append(c);
				}
			}
		}
		return bb.toString().trim();
	}

	public final static void close(Connection c) {
		if (c != null) {
			try {
				if (!c.isClosed()) {
					logger.debug("VVV: 关闭连接:{}", c);
					c.close();
				}
			} catch (Exception e) {
				// NOP
			}
		}
	}

	public final static void close(Statement c) {
		if (c != null) {
			try {
				c.close();
			} catch (Exception e) {
				// NOP
			}
		}
	}

	public final static void close(ResultSet c) {
		if (c != null) {
			try {
				c.close();
			} catch (Exception e) {
				// NOP
			}
		}
	}

	public final static void cancel(Statement stmt) {
		try {
			stmt.cancel();
		} catch (SQLException e) {
			// NOP
		}
	}

	/**
	 * 根据参数列表，取回结果。
	 * 
	 * @param conn JDBC链接
	 * @param sql  SQL文本
	 * @param args 参数表
	 * @return ResultSet 结果集
	 * @throws SQLException
	 */
	public static PreparedStatement preparedStatement(Connection conn, //
			String SQL, Object[] args) throws SQLException {
		assert SQL != null;
		/*
		 * XXX:SQL Server 未实现自定义TYPE_FORWARD_ONLY，CONCUR_READ_ONLY
		 * 的特性，但MySQL在实际读取数据中，自动全部取成，需要更大的内存@2019.3.30
		 */
		PreparedStatement pstmt;
		if (isSQLServer(conn)) {
			pstmt = conn.prepareStatement(SQL);
		} else {
			pstmt = conn.prepareStatement(SQL, //
					ResultSet.TYPE_FORWARD_ONLY, //
					ResultSet.CONCUR_READ_ONLY);
		}
		try {
			int idx = 1;
			for (Object arg : args) {
				// FIXME: 如果是NULL，可能查不出记录
				if (arg == null) {
					pstmt.setNull(idx++, Types.JAVA_OBJECT);
				} else if (arg instanceof String) {
					pstmt.setString(idx++, (String) arg);
				} else if (arg instanceof Integer) {
					pstmt.setInt(idx++, (Integer) arg);
				} else if (arg instanceof Long) {
					pstmt.setLong(idx++, (Long) arg);
				} else if (arg instanceof Float) {
					pstmt.setFloat(idx++, (Float) arg);
				} else if (arg instanceof Double) {
					pstmt.setDouble(idx++, (Double) arg);
				} else if (arg instanceof Date) {
					pstmt.setTimestamp(idx++, new Timestamp(((Date) arg).getTime()));
				} else {
					pstmt.setObject(idx++, arg);
				}
			}
		} catch (Exception e) {
			close(pstmt);
			throw new SQLException(e);
		}
		return pstmt;
	}

	public static int execUpdate(Connection conn, String SQL) throws SQLException {
		Statement stmt = conn.createStatement();
		try {
			return stmt.executeUpdate(SQL);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	static Predicate<String> FMTARGS = Pattern.compile("[%]s|[%]\\d+").asPredicate();

	/**
	 * Execute SQL update statement
	 * 
	 * @param conn JDBC connection
	 * @param SQL  SQL statement
	 * @param type SQL Types [0,1,1,...] 数组的第一个元素必须为0,
	 * @param args updated parameters args[NULL,1,...] 参数第一个保留
	 * @return update items number
	 * @throws SQLException
	 */
	public static int execUpdate(Connection conn, String SQL, int[] type, Object... args) throws SQLException {
		// 使用带%号的格式，处理参数，XXX:不推荐！
		if (type == null || type.length == 0) {
			throw new SQLException("类弄不匹配，未定义类型！");
		}

		if (type[0] != 0) {
			int vt[] = new int[type.length + 1];
			System.arraycopy(type, 0, vt, 1, type.length);
			type = vt;
		}

		if (type.length != args.length) {
			if (args.length + 1 == type.length) {
				Object[] vo = new Object[type.length];
				System.arraycopy(args, 0, vo, 1, args.length);
				args = vo;
			} else {
				throw new SQLException("Type , args 长度不匹配！");
			}
		}

		int idx;
		try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
			for (idx = 1; idx < type.length; idx++) {
				Object var = args[idx];
				// if the value is NULL, set to NULL
				if (var == null) {
					pstmt.setNull(idx, Types.NULL);
					continue;
				}
				switch (type[idx]) {
				case Types.BIT:
					pstmt.setString(idx, TypeConverter.toSQLBit(var));
					break;
				case Types.BOOLEAN:
					pstmt.setBoolean(idx, TypeConverter.toBoolean(var));
					break;
				case Types.BINARY:
				case Types.VARBINARY:
					byte[] x = null;
					if (var instanceof byte[]) {
						x = (byte[]) var;
					} else if (var instanceof String) {
						x = ((String) var).getBytes(QString.UTF8);
					}
					pstmt.setBytes(idx, x);
					break;
				case Types.CHAR:
				case Types.NCHAR:
				case Types.VARCHAR:
				case Types.NVARCHAR:
				case Types.LONGVARCHAR:
				case Types.LONGNVARCHAR:
					String v = TypeConverter.toString(var);
					pstmt.setString(idx, v);
					break;
				case Types.DATE:
					java.sql.Date vd = TypeConverter.toSQLDate(var);
					if (vd == null)
						pstmt.setNull(idx, Types.DATE);
					else
						pstmt.setDate(idx, vd);
					break;
				case Types.TIMESTAMP:
					Timestamp vts = TypeConverter.toTimestamp(var);
					if (vts == null)
						pstmt.setNull(idx, Types.TIMESTAMP);
					else
						pstmt.setTimestamp(idx, vts);
					break;
				case Types.BIGINT:
					Long vl = TypeConverter.toLong(var);
					if (vl == null)
						pstmt.setNull(idx, Types.BIGINT);
					else
						pstmt.setLong(idx, vl.longValue());
					break;
				case Types.INTEGER:
					Integer vi = TypeConverter.toInteger(var);
					if (vi == null)
						pstmt.setNull(idx, Types.INTEGER);
					else
						pstmt.setInt(idx, vi.intValue());
					break;
				case Types.TINYINT:
					Byte vb = TypeConverter.toByte(var);
					if (vb == null)
						pstmt.setNull(idx, Types.TINYINT);
					else
						pstmt.setByte(idx, vb.byteValue());
					break;
				case Types.SMALLINT:
					Short vs = TypeConverter.toShort(var);
					if (vs == null)
						pstmt.setNull(idx, Types.SMALLINT);
					else
						pstmt.setShort(idx, vs.shortValue());
					break;
				case Types.FLOAT:
					Float vf = TypeConverter.toFloat(var);
					if (vf == null)
						pstmt.setNull(idx, Types.FLOAT);
					else
						pstmt.setFloat(idx, vf.floatValue());
					break;
				case Types.NUMERIC:
				case Types.DECIMAL:
					BigDecimal vbd = TypeConverter.toBigDecimal(var);
					if (vbd == null)
						pstmt.setNull(idx, Types.DECIMAL);
					else
						pstmt.setBigDecimal(idx, vbd);
					break;
				case Types.DOUBLE:
					Double vdb = TypeConverter.toDouble(var);
					if (vdb == null)
						pstmt.setNull(idx, Types.DOUBLE);
					else
						pstmt.setDouble(idx, vdb.doubleValue());
					break;
				default:
					logger.error("type:{} not support!", type[idx]);
				}
			}
			return pstmt.executeUpdate();
		}
	}

	public static int execUpdate(Connection conn, String SQL, Object... args) throws SQLException {
		// 使用带%号的格式，处理参数，XXX:不推荐！
		if (FMTARGS.test(SQL)) {
			return execUpdate(conn, String.format(SQL, args));
		}
		int idx = 1;
		try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
			for (Object arg : args) {
				if (arg == null) {
					// FIXME: SQL Server NOT SUPPORT this feature
					pstmt.setNull(idx++, Types.VARCHAR);
				} else if (arg instanceof String) {
					pstmt.setString(idx++, (String) arg);
				} else if (arg instanceof Integer) {
					pstmt.setInt(idx++, (Integer) arg);
				} else if (arg instanceof Long) {
					pstmt.setLong(idx++, (Long) arg);
				} else if (arg instanceof Float) {
					pstmt.setFloat(idx++, (Float) arg);
				} else if (arg instanceof Double) {
					pstmt.setDouble(idx++, (Double) arg);
				} else if (arg instanceof Date) {
					Timestamp d = new Timestamp(((Date) arg).getTime());
					pstmt.setTimestamp(idx++, d);
				} else {
					pstmt.setObject(idx++, arg);
				}
			}
			return pstmt.executeUpdate();
		}
	}

	static boolean isSQLServer(Connection conn) {
		return conn.toString().contains("sqlserver");
	}
}
