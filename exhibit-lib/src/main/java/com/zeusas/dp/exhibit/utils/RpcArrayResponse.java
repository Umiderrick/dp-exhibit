package com.zeusas.dp.exhibit.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RpcArrayResponse<T> implements Serializable {
	/** 状态 */
	private int status;
	/** rpc message */
	private List<String> message;
	/** Response 数据容器 */
	private List<T> data;

	
	public RpcArrayResponse() {
		message = new ArrayList<>();
		data = new ArrayList<>();
	}
	
	public RpcArrayResponse(int status, String ...message) {
		this();
		this.status = status;
		this.message.addAll(Arrays.asList(message));
	}
	
	public RpcArrayResponse(int status, List<T>values, String ...message) {
		this(status,message);
		this.data = values;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<String> getMessage() {
		return message;
	}

	public void setMessage(List<String> message) {
		this.message = message;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}
}
