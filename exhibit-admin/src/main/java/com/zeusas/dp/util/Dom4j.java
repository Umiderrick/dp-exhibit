package com.zeusas.dp.util;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Collection;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zeusas.dp.exhibit.utils.QString;

public final class Dom4j {
	static Logger logger = LoggerFactory.getLogger(Dom4j.class);

	private Dom4j() {
	}

	/**
	 * Get an attribute string from an element.
	 * 
	 * @param e
	 *            element
	 * @param attrId
	 *            attribute ID
	 * @return The value of attribute of an element.
	 */
	public static String getAttr(Element e, String attrId) {
		Attribute attr = e.attribute(attrId);
		return attr == null ? null : attr.getValue();
	}

	public static Integer getAttrInteger(Element e, String attrId) {
		Attribute attr = e.attribute(attrId);
		return attr == null ? null : //
				QString.toInt(attr.getValue());
	}

	/**
	 * Get the Document Object from a specified file.
	 * 
	 * @param file
	 *            the File object
	 * @return the Document object of Dom4J
	 */
	public static Document getXmlDoc(File file) {
		SAXReader reader = new SAXReader();
		try {
			reader.setIgnoreComments(true);
			reader.setMergeAdjacentText(true);
			return reader.read(file);
		} catch (DocumentException e) {
			logger.error("XML文档解析错误，文件:{}", file.getAbsolutePath());
		}
		return null;
	}

	public static Document getXmlDoc(InputStream in) {
		SAXReader reader = new SAXReader();
		try {
			reader.setIgnoreComments(true);
			reader.setMergeAdjacentText(true);
			return reader.read(in);
		} catch (DocumentException e) {
			logger.error("{}", e);
		}
		return null;
	}

	/**
	 * Check whether the Object is EMPTY or NULL or NOT
	 * 
	 * @param obj
	 * @return
	 */
	static boolean isNnullOrEmpty(Object obj) {
		if (obj == null) {
			return true;
		}
		if (obj instanceof CharSequence) {
			return ((CharSequence) obj).length() == 0;
		}
		if (obj.getClass().isArray()) {
			return Array.getLength(obj) == 0;
		}
		if (obj instanceof Collection<?>) {
			return ((Collection<?>) obj).size() == 0;
		}
		return false;
	}

	/**
	 * XML 输出转码。在XML中到少把&和<转换成对应的转义符，才是合法的XML。
	 * 
	 * @param cs
	 * @return
	 */
	public static CharSequence xmlEscape(CharSequence cs) {
		StringBuilder bb = new StringBuilder(cs.length() + 256);
		for (int i = 0; i < cs.length(); i++) {
			char ch = cs.charAt(i);
			switch (ch) {
			case '<':
				bb.append("&lt;");
				break;
			case '>':
				bb.append("&gt;");
				break;
			case '&':
				bb.append("&amp;");
				break;
			default:
				bb.append(ch);
			}
		}
		return bb;
	}

	public static String getText(Element e) {
		return getText(e, null);
	}

	/**
	 * 从节点e中取得串，如果未定义，返回默认值。
	 * 
	 * @param e
	 * @param v0 默认值
	 * @return 如果节点定义，但无值，返回值为空串
	 */
	public static String getText(Element e, String v0) {
		if (e == null) {
			return v0;
		}
		return e.getTextTrim();
	}
}
