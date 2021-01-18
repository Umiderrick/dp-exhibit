package com.zeusas.dp.exhibit.utils;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import ch.qos.logback.core.joran.spi.ActionException;

public class DataApiClient {
	private final static Logger logger = LoggerFactory.getLogger(DataApiClient.class);

	private final static ThreadLocal<QHttpClients> LocalQHttpClients = new ThreadLocal<>();
	private final static String DATA_API = AppConfig.getString("DATA_API");
	 private static QHttpClients client() {
	    	QHttpClients qc = LocalQHttpClients.get();
	    	if (qc==null) {
	    		 qc = new QHttpClients ();
	    		 LocalQHttpClients.set(qc);
	    	}
	    	return qc;
	    }
	private  <T> RpcResponse<T> invokeDataApi(String systemId, String metaId, Class<T> entityClass)
			throws ServiceException {
		RpcRequest request = new RpcRequest(systemId, metaId);
		QHttpClients myClient = client();

		try {
			byte[] b = myClient.post(new URI(DATA_API), request);
			String json = new String(b, StandardCharsets.UTF_8);
			return JSON.parseObject(json, new TypeReference<RpcResponse<T>>(entityClass) {
			});
		} catch (Exception e) {
			logger.error("Invoke: systemId:{} metaId{} error.", systemId, metaId);
			throw new ServiceException(e);
		}
	}

	public <T> T getDataAsObject(String systemId, String metaId, String pk, Class<T> entityClass) {

		RpcRequest request = new RpcRequest(systemId, metaId);
		request.setData(pk);

		QHttpClients myClient = client();
		try {
			byte[] b = myClient.post(new URI(DATA_API), request);

			RpcResponse<?> rpc = JSON.parseObject(b, RpcResponse.class);
			if (rpc.getStatus() != 0) {
				throw new ServiceException("");
			}
			if (rpc.size() == 0) {
				return null;
			}
			return rpc.getObjectAs(entityClass);
		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

	public <T> List<T> getDataAsArray(String systemId, String metaId, Class<T> entityClass)
			throws ServiceException {

		RpcResponse<T> rpc = invokeDataApi(systemId, metaId, entityClass);
		return rpc.getArray();
	}

	/**
	 * 返回response对象流。
	 * 
	 * @param systemId
	 * @param metaId
	 * @return
	 * @throws ActionException
	 */
	public byte[] getDataAsStream(String systemId, String metaId) throws ServiceException {
		RpcRequest request = new RpcRequest(systemId, metaId);
		QHttpClients myClient = client();
		try {
			return myClient.post(new URI(DATA_API), request);
		} catch (Exception e) {
			logger.error("调用API：systemId:{}, MetaId: {} 错误。", systemId, metaId);
			throw new ServiceException(e);
		}
	}
}
