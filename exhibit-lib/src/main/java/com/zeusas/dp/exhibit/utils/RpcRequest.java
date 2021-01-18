package com.zeusas.dp.exhibit.utils;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class RpcRequest implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 7598783514096238016L;

	/** 系统ID */
	private String systemId;
	/** 实体类型 */
	private String metaId;
	// token
	private String token;
	// 最后请求时间
	private Long timestamp;

	private Object data;

	public RpcRequest() {
	}

	public RpcRequest(String systemId, String metaId) {
		this.systemId = systemId;
		this.metaId = metaId;
	}

	public RpcRequest(String systemId, String metaId, Long timestamp) {
		this(systemId, metaId);
		this.timestamp = timestamp;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getMetaId() {
		return metaId;
	}

	public void setMetaId(String metaId) {
		this.metaId = metaId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T getDataAs(Class<T> type) {
		if (data == null //
				|| type.isAssignableFrom(data.getClass())) {
			return (T) data;
		}

		if (Map.class.isAssignableFrom(data.getClass())) {
			return QBeanUtil.toRpcBean((Map) data, type);
		}

		if (this.data instanceof JSONArray) {
			List<T> vv = ((JSONArray) data).toJavaList(type);
			return vv.size() > 0 ? vv.get(0) : null;
		}

		if (type.isAssignableFrom(Date.class)//
				|| type.isAssignableFrom(String.class)//
				|| Number.class.isAssignableFrom(type)) {
			return TypeConverter.toType(this.data, type);
		}
		
		if (this.data instanceof JSONObject) {
			return ((JSONObject) data).toJavaObject(type);
		}

		return null;
	}

	public <T> List<T> getArrayAs(Class<T> type) {
		if (this.data == null)
			return new ArrayList<>(0);
		if (this.data instanceof List) {
			return (List<T>) QBeanUtil.toRpcArray((List<?>) data, type);
		}
		if (this.data instanceof JSONArray) {
			return ((JSONArray) data).toJavaList(type);
		}
		// FIXME: 如果不是数组对象，返回NULL？
		return new ArrayList<>(0);
	}
}
