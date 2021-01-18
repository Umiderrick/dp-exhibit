package com.zeusas.dp.exhibit.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyFilter;

/**
 * 响应通用对象
 */
public final class DSResponse {
	public final static String UTF8 = "UTF-8";

	public final static String DS_DATA = "data";
	/** 返回消息TAG */
	public final static String DS_MESSAGE = "message";
	/** SmartClient RestDataSource response TAG */
	public final static String DS_RESPONSE = "response";
	/** Response "status" 状态TAG */
	public final static String DS_STATUS = "status";
	/** 状态 */
	private int status;
	/** Response 数据容器 */
	private Object data;
	/** 消息 */
	private final List<String> message = new ArrayList<>();
	/** 自定义数据 */
	private Map<String, Object> extra = new LinkedHashMap<>();;

	public static final DSResponse OK = new DSResponse(DSStatus.SUCCESS);
	public static final DSResponse FAILURE = new DSResponse(DSStatus.FAILURE);
	public static final DSResponse SUCCESS = OK;
	
	/**
	 * 按指定日期输出的构造函数。
	 * 
	 * @param fmt
	 *            JSON序列化日期格式
	 */
	public DSResponse() {
		status = 0;
	}

	public DSResponse(Object data) {
		this();
		this.data = data;
	}

	public DSResponse(int status, String message) {
		this.status = status;
		this.message.add(message);
	}

	public DSResponse(DSStatus status, String message) {
		this(status.id, message == null ? status.val : message);
	}

	public DSResponse(DSStatus status) {
		this(status.id, status.val);
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public DSResponse setResponse(DSStatus s, String mesg) {
		this.status = s.id;
		this.sendMessage(mesg);
		return this;
	}
	
	public DSResponse setResponse(int s, String mesg) {
		this.status = s;
		this.sendMessage(mesg);
		return this;
	}
	
	public void setStatus(DSStatus status) {
		this.status = status.id;
		this.message.add(status.val);
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public List<String> getMessage() {
		return message;
	}

	public void sendMessage(String... mesg) {
		this.message.addAll(Arrays.asList(mesg));
	}

	public void setMessage(List<String> message) {
		if (message != null) {
			this.message.addAll(message);
		}
	}

	public Map<String, Object> getExtra() {
		return extra;
	}

	public void setExtra(Map<String, Object> extra) {
		if (extra != null) {
			this.extra.putAll(extra);
		}
	}

	public void setParam(String attr, Object value) {
		this.extra.put(attr, value);
	}

	/**
	 * 对指字的属性输出JSON串
	 * 
	 * @param inc
	 *            包含的属性
	 * @param obj
	 *            对象
	 * @return
	 * @throws IOException
	 */
	public static byte[] toJsonStream(Set<String> inc, Object obj) throws IOException {
		PropertyFilter filter = (cls, nm, o) -> {
			return inc.contains(nm);
		};
		String json = JSON.toJSONString(obj, filter);
		return json.getBytes(UTF8);
	}

	public static byte[] toJsonExclude(Set<String> inc, Object obj) throws IOException {
		PropertyFilter filter = (cls, nm, o) -> {
			return !inc.contains(nm);
		};
		String json = JSON.toJSONString(obj, filter);
		return json.getBytes(UTF8);
	}

	public String toJSON() {
		return JSON.toJSONString(this);
	}
}
