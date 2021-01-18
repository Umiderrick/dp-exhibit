package com.zeusas.dp.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import com.google.common.base.Strings;
import com.zeusas.dp.exhibit.utils.AppConfig;
import com.zeusas.dp.exhibit.utils.QString;

/**
 * DDL 中的数据项定义类
 * 
 * <PRE>
 * &lt;DDL id=""> &lt;datasource type="jndi|jdbc|datasource"> datasource value
 * &lt;/datasource> &lt;/DDL> &lt;/PRE>
 */
public final class DataDDL {
	public final static String DS_DATASOURCE = "datasource";

	// 数据项中的id <DDL id="ORDM_SELLER">
	private String id;

	// 定义一个DDL中的表对象，由table,field,alias(properties),key
	private final Map<String, Meta> metas = new HashMap<>();
	// 定义处理过程
	private final ArrayList<Proc> procs = new ArrayList<Proc>();

	public static final String empty[] = new String[0];

	protected String dsType = DS_DATASOURCE;
	protected String dsName = "dataSource";

	public DataDDL() {
	}

	public DataDDL(String dsType, String dsName) {
		this.dsType = ID.valueOf(dsType.toLowerCase());
		this.dsName = dsName;
	}

	DataDDL init(Element ddl) {
		id = Dom4j.getAttr(ddl, Meta.TAG_ID);
		Element ds = ddl.element(Meta.TAG_DATASOURCE);
		if (ds != null) {
			dsType = ID.valueOf(Dom4j.getAttr(ds, Meta.TAG_TYPE));
			dsName = Dom4j.getText(ds);
		}
		List<Element> emetas = ddl.elements(Meta.TAG_META);
		for (Element e : emetas) {
			Meta m = new Meta().init(e);
			metas.put(m.getId(), m);
		}
		List<Element> nodes = ddl.elements(Meta.TAG_PROC);
		for (Element e : nodes) {
			procs.add(new Proc(e));
		}
		Collections.sort(procs);
		procs.trimToSize();
		return this;
	}

	public void setDsType(String dsType) {
		this.dsType = ID.valueOf(dsType.toLowerCase());
	}

	public void setDataSource(String dsType, String dsName) {
		this.dsName = dsName;
		this.dsType = ID.valueOf(dsType.toLowerCase());
	}

	public Proc getProc(String id) {
		for (Proc p : procs) {
			if (p.id.equals(id))
				return p;
		}
		return null;
	}

	static String[] toArrayUpper(String s) {
		if (s == null || s.length() == 0) {
			return empty;
		}
		String v[] = QString.split(s);

		for (int i = 0; i < v.length; i++) {
			if (v[i] != null || v[i].length() > 0) {
				v[i] = v[i].toUpperCase();
			}
		}
		return v;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this==obj) {
			return true;
		}
		if (obj == null || !(obj instanceof DataDDL)) {
			return false;
		}
		return this.id.equals(((DataDDL) obj).id);
	}

	public String getId() {
		return id;
	}

	public Meta getMeta(String id) {
		return metas.get(id);
	}

	public Map<String, Meta> getMetas() {
		return this.metas;
	}

	public void add(Meta meta) {
		this.metas.put(meta.getId(), meta);
	}

	public ArrayList<Proc> getProcs() {
		return procs;
	}

	public String getDsType() {
		return dsType;
	}

	public String getDsName() {
		return dsName;
	}

	@Override
	public String toString() {
		return new StringBuilder("[id=").append(id) //
				.append(",type=").append(dsType) //
				.append(",name=").append(dsName) //
				.append(']') //
				.toString();
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setMetas(Map<String, Meta> metas) {
		if (metas != null) {
			this.metas.putAll(metas);
		}
	}

	public void setProcs(List<Proc> procs) {
		if (procs != null) {
			this.procs.addAll(procs);
		}
	}

	public void setDsName(String dsName) {
		this.dsName = dsName;
	}

	/**
	 * 使用变量表，去替换SQL中的变量。变量使用  ${变量名}，如：<br/>
	 * 
	 * select * from ${myTalbe} <br/>
	 * 例如：myTable=AAA 
	 * ==>select * from <b>AAA</b>
	 * 
	 * @param stmt
	 * @param vars
	 * @return
	 */
	final static String preprocess(final String sqls) {
		// variable table is empty
		if (Strings.isNullOrEmpty(sqls)) {
			return sqls;
		}
		String stmt = QueryHelper.qulifier(sqls);
		// ${ var }
		StringBuilder b = new StringBuilder();
		int pos = 0;
		int idx0 = -1;
		int idx1;
		while (pos<stmt.length()-1) {
			// satement has not match ${ from pos
			if (idx0 == -1) {
				idx0 = stmt.indexOf("${", pos);
				// not fond tag: ${ 
				if (idx0 < 0) {
					break;
				}
				// OK, idx0!=-1
				continue;
			}
			// from ${ locate '}'
			idx1 = stmt.indexOf('}', idx0 + 2);
			if (idx1 < 0) {
				break;
			}
			// relocate check ${, exclude: ${ ... ${ ... ${var}
			int t = stmt.indexOf("${", idx0 + 2);
			if (t > 0 && t<idx1) {
				idx0 = t;
				continue;
			}
			String nm = stmt.substring(idx0 + 2, idx1).trim();
			String v = AppConfig.getString(nm);
			if (v != null) {
				b.append(stmt.substring(pos, idx0));
				b.append(v);
				pos = idx1 + 1;
			} else {
				b.append(stmt.substring(pos, idx1));
				pos = idx1;
			}
			idx0 = -1;
		}

		b.append(stmt.substring(pos));

		return b.toString();
	}
}
