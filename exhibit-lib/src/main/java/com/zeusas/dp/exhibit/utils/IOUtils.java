package com.zeusas.dp.exhibit.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.MappedByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IOUtils {
	static Logger logger = LoggerFactory.getLogger(IOUtils.class);
	public final static byte EOF = -1;

	private IOUtils() {
	}

	public static URL getURL(final String url, final int timeout) throws IOException {
		return new URL(null, url, new URLStreamHandler() {
			@Override
			protected URLConnection openConnection(URL url) throws IOException {
				URL clone_url = new URL(url.toString());
				HttpURLConnection clone_urlconnection = (HttpURLConnection) clone_url.openConnection();

				clone_urlconnection.setConnectTimeout(timeout);
				clone_urlconnection.setReadTimeout(timeout);
				return (clone_urlconnection);
			}
		});
	}

	public static boolean delete(String fname) {
		File f = new File(fname);
		if (!f.exists()) {
			return false;
		}
		return f.delete();
	}

	public static void close(InputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				// NOP
			}
		}
	}

	public static void close(OutputStream os) {
		if (os != null) {
			try {
				os.close();
			} catch (IOException e) {
				// NOP
			}
		}

	}

	public static void close(Reader r) {
		if (r != null) {
			try {
				r.close();
			} catch (IOException e) {
				// NOP
			}
		}
	}

	public static void close(Writer r) {
		if (r != null) {
			try {
				r.close();
			} catch (IOException e) {
				// NOP
			}
		}
	}

	/**
	 * Return an Input stream for reading a resource from a class loader context.
	 * 
	 * @param res
	 * @return
	 */
	public static InputStream getResourceAsStream(String res) {
		ClassLoader loader;
		loader = Thread.currentThread().getContextClassLoader();
		res = res.replace('.', '/');
		int idx = res.lastIndexOf('/');
		String s = idx >= 0 ? res.substring(0, idx) + '.' + res.substring(idx + 1) : res;
		return loader.getResourceAsStream(s);
	}

	public static long fileCopy(String src, String dest) throws IOException {
		long size = 0;
		FileInputStream fsrc = null;
		FileOutputStream fdst = null;
		if (src == null || dest == null) {
			throw new IOException("复制文件名不能为空，请检查文件名称。");
		}
		// 8K缓存
		byte[] bb = new byte[32 * 1024];
		try {
			fsrc = new FileInputStream(src);
			fdst = new FileOutputStream(dest);

			int len;
			while ((len = fsrc.read(bb)) != -1) {
				size += len; // 字节数 文件大小
				fdst.write(bb, 0, len);
			}
			fsrc.close();
			fsrc = null;
			fdst.close();
			fdst = null;
		} finally {
			close(fsrc);
			close(fdst);
		}

		return size;
	}

	/**
	 * 回调私有方法， 清除MappedByteBuffer 占用内存。
	 * 
	 * @param buff
	 */
	public static void clean(MappedByteBuffer buff) {
		AccessController.doPrivileged(new PrivilegedAction<Object>() {
			@SuppressWarnings("restriction")
			public Object run() {
				try {
					Method getCleanerMethod = buff.getClass().getMethod("cleaner", new Class[0]);
					getCleanerMethod.setAccessible(true);
					sun.misc.Cleaner cleaner = (sun.misc.Cleaner) getCleanerMethod.invoke(buff, new Object[0]);
					cleaner.clean();
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
				return null;
			}
		});
	}

	@SuppressWarnings("restriction")
	private static sun.misc.Unsafe unsafe;

	@SuppressWarnings("restriction")
	public static sun.misc.Unsafe getUnsafe() {
		if (unsafe != null) {
			return unsafe;
		}
		sun.misc.Unsafe val = null;
		Field theUnsafe;
		try {
			theUnsafe = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
			val = (sun.misc.Unsafe) theUnsafe.get(null);
		} catch (Exception e) {
			// NOP
		}
		unsafe = val;
		return val;
	}
}
