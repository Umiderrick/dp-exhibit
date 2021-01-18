package com.zeusas.dp.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zeusas.dp.exhibit.utils.IOUtils;

/**
 * 操作文件上传中心客户端程序。
 *
 */
public final class VfsDocClient {
	static Logger logger = LoggerFactory.getLogger(VfsDocClient.class);

	private VfsDocClient() {
	}

	final static RequestConfig config = RequestConfig.custom() //
			.setConnectTimeout(30000) //
			.setSocketTimeout(30000).build();

	/**
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static byte[] get(String url) throws IOException {
		HttpGet hg = new HttpGet(url);
		hg.setConfig(config);
		CloseableHttpClient hc = HttpClients.createDefault();
		HttpResponse response = hc.execute(hg);
		InputStream in = null;
		try {
			int errCode = response.getStatusLine().getStatusCode();
			if (errCode != 200) {
				throw new IOException("read url:" + url //
						+ " error, error code:" + errCode);
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1024 * 32);
			in = response.getEntity().getContent();
			int b;
			while ((b = in.read()) != -1) {
				bos.write(b);
			}
			in.close();
			bos.flush();
			byte[] bb = bos.toByteArray();
			bos = null;
			return bb;
		} finally {
			IOUtils.close(in);
		}
	}

	/**
	 * Delete an VFS file from document server
	 * 
	 * @param url
	 */
	public static void delete(String url) {
		CloseableHttpClient hc = HttpClients.createDefault();

		HttpDelete hd = new HttpDelete(url);
		hd.setConfig(config);
		try {
			HttpResponse response = hc.execute(hd);
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new IOException("Delete file error.");
			}
			nop(response);
		} catch (Exception e) {
			logger.error("delete url:{} failure!", url, e);
		}
	}

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

	/**
	 * Put a stream as input to Document VFS Center.
	 * 
	 * @param url
	 *            url address
	 * @param input
	 *            input stream
	 */
	public static void put(String url, InputStream input) {
		CloseableHttpClient hc = HttpClients.createDefault();
		HttpPut request = new HttpPut(url);
		InputStreamEntity inputEntity = null;
		try {
			inputEntity = new InputStreamEntity(input);
			request.setEntity(inputEntity);
			HttpResponse response = hc.execute(request);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new IOException("put file error.");
			}
			nop(response);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * Put a file to document center
	 * 
	 * @param url
	 *            url address
	 * @param file
	 *            local file object
	 */
	public static void put(String url, File file) {
		InputStream fis = null;
		try {
			fis = new FileInputStream(file);
			BufferedInputStream bs = new BufferedInputStream(fis);
			put(url, bs);
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			IOUtils.close(fis);
		}

	}

	/**
	 * Put a byte array as stream to document center.
	 * 
	 * @param url
	 * @param b
	 *            a byte array
	 */
	public static void put(String url, byte[] b) {
		ByteArrayInputStream bb = new ByteArrayInputStream(b);
		put(url, bb);
	}
}
