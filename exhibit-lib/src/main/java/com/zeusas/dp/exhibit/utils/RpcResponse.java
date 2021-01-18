package com.zeusas.dp.exhibit.utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;

public class RpcResponse<T> extends GenericClass<T> {
	private int status;
	private String message;

	private Long timestamp;

	private final List<T> array = new ArrayList<>();
	
	private Object extra;
	
	public RpcResponse() {
		super();
	}

	public RpcResponse(Class<T> entityClass) {
		super(entityClass);
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public RpcResponse<T> setResponse(int status, String message, Object... args) {
		this.status = status;
		this.setMessage(message, args);
		return this;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setMessage(String message, Object... args) {
		try {
			this.message = MessageFormat.format(message, args);
		} catch (Exception e) {
			this.message = message
					+ ((args == null || args.length == 0) ? "" : JSON.toJSONString(args));
		}
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public List<T> getArray() {
		return array;
	}

	public <E> List<E> getArrayAs(Class<E> type) {
		return QBeanUtil.toRpcArray(this.array, type);
	}

	public void setArray(List<T> array) {
		if (array != null && array.size() > 0) {
			Class<?> srcClass = array.get(0).getClass();
			if (entityClass == null //
					|| entityClass.isAssignableFrom(srcClass)) {
				this.array.addAll(array);
			} else {
				this.array.addAll(QBeanUtil.toRpcArray(array, entityClass));
			}
		}
	}

	public void add(T data) {
		this.array.add(data);
	}

	/**
	 * XXX: WARN 可能会出现类型转换错误。
	 * @return
	 * @deprecated
	 */
	public T asObject() {
		return (array.isEmpty()) ? null : array.get(0);
	}

	public <E> E getObjectAs(Class<E> type) {
		return (array.isEmpty()) ? null : QBeanUtil.toRpcBean(array.get(0), type);
	}

	public void setData(T t) {
		if (this.array.size() > 0) {
			this.array.set(0, t);
		} else {
			this.array.add(t);
		}
	}

	public Object getExtra() {
		return extra;
	}

	public void setExtra(Object extra) {
		this.extra = extra;
	}

	public int size() {
		return this.array.size();
	}
}
