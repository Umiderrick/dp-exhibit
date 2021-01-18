package com.zeusas.dp.exhibit.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
/**
 * 转拼音
 */
public class Hanyu {
	private HanyuPinyinOutputFormat format;
	private String[] pinyin;

	public Hanyu() {
		format = new HanyuPinyinOutputFormat();
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		pinyin = null;
	}

	/**
	 * 转换单个字符 
	 * @param c
	 * @return
	 */
	private String getCharacterPinYin(char c) {
		try {
			pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		// 如果c不是汉字，toHanyuPinyinStringArray会返回null
		if (pinyin == null)
			return null;
		// 只取一个发音，如果是多音字，仅取第一个发音
		return pinyin[0];
	}

	/**
	 * 转换一个字符串
	 * @param str
	 * @return
	 */
	public String getStringPinYin(String str) {
		StringBuilder sb = new StringBuilder();
		String tempPinyin = null;
		for (int i = 0; i < str.length(); ++i) {
			tempPinyin = getCharacterPinYin(str.charAt(i));
			if (tempPinyin == null) {
				// 如果str.charAt(i)非汉字，则保持原样
				sb.append(str.charAt(i));
			} else {
				sb.append(tempPinyin);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 从自订位置开始转换为拼音
	 * 起始为零
	 * @param str
	 * @param start
	 * @return
	 */
	public String getStringPinYin(String str,int start) {
		StringBuilder sb = new StringBuilder();
		String tempPinyin = null;
		String returnPinyin = null;
		if(start>=0&&start<str.length()){
			String prefix=str.substring(0,start);
			for (int i = start; i <str.length(); ++i) {
				tempPinyin = getCharacterPinYin(str.charAt(i));
				if (tempPinyin == null) {
					// 如果str.charAt(i)非汉字，则保持原样
					sb.append(str.charAt(i));
				} else {
					sb.append(tempPinyin);
				}
			}
			returnPinyin=prefix+sb.toString();
		}
		return returnPinyin;
	}
	
	/**
	 * 指定位置转换为拼音
	 * 起始为零
	 * @param str
	 * @param start
	 * @param end
	 * @return
	 */
	public String getStringPinYin(String str,int start,int end) {
		StringBuilder sb = new StringBuilder();
		String tempPinyin = null;
		String returnPinyin = null;
		if(start<end&&start>=0&&end<str.length()){
			String prefix=str.substring(0,start);
			String suffix=str.substring(end+1);
			for (int i = start; i <=end; ++i) {
				tempPinyin = getCharacterPinYin(str.charAt(i));
				if (tempPinyin == null) {
					// 如果str.charAt(i)非汉字，则保持原样
					sb.append(str.charAt(i));
				} else {
					sb.append(tempPinyin);
				}
			}
			returnPinyin=prefix+sb.toString()+suffix;
		}
		return returnPinyin;
	}
	
	/**
	 * 指定位置转换为拼音首字母
	 * 起始为零
	 * @param str
	 * @param start
	 * @return
	 */
	public String getFirstSpell(String str,int start) {
		StringBuilder sb = new StringBuilder();
		String tempPinyin;
		String returnPinyin = null;
		if(start>=0&&start<str.length()){
			String prefix=str.substring(0,start);
			for (int i = start; i <str.length(); ++i) {
				tempPinyin = getCharacterPinYin(str.charAt(i));
				if (tempPinyin == null) {
					// 如果str.charAt(i)非汉字，则保持原样
					sb.append(str.charAt(i));
				} else {
					sb.append(tempPinyin.charAt(0));
				}
			}
			returnPinyin=prefix+sb.toString();
		}
		return returnPinyin;
	} 
}
