package com.zeusas.dp.exhibit.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IVfs {

	private static Logger logger = LoggerFactory.getLogger(IVfs.class);

	/**
	 * 文件上传vfs服务器
	 * @author jcm
	 */
	public static boolean put(String url, InputStream input) {
		boolean flag = false;
		CloseableHttpClient hc = HttpClients.createDefault();
		HttpPut request = new HttpPut(url);
		InputStreamEntity inputEntity;
		try {
			inputEntity = new InputStreamEntity(input);
			request.setEntity(inputEntity);
			HttpResponse response = hc.execute(request);

			if (response.getStatusLine().getStatusCode() == 200) {
				flag = true;
			} else {
				throw new IOException("put file error.");
			}
			nop(response);
		} catch (Exception e) {
			logger.error("文件上传vfs出错", e);
		}
		return flag;
	}
	
	/**
	 * byte[]数据上传到vfs
	 * @author jcm
	 * */
	public static boolean put(String url, byte[] b) {
		ByteArrayInputStream bb = new ByteArrayInputStream(b);
		return put(url, bb);
	}
	
	/**
	 * 关闭 response
	 * @author jcm
	 */
	private static void nop(HttpResponse response) {
		InputStream in = null;
		try {
			in = response.getEntity().getContent();
			while (in.read() != -1)
				;
		} catch (IOException e) {
			// NOP
		} finally {
			IOUtils.close(in);
		}
	}
}
