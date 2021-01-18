package com.zeusas.dp.bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.zeusas.dp.exhibit.utils.QString;


public class PictureBean {

	// 1门头整体
	private String pic1;

	// 2高柜
	private String pic2;

	// 3集中售卖区
	private String pic3;

	// 4收银台
	private String pic4;

	// 5主推台
	private String pic5;
	
	// 6橱窗
	private String pic6;

			
	//	7其他的照片
	private String pic7;


	Map<Integer, String> typeset;

	public String getPic1() {
		return pic1;
	}

	public void setPic1(String pic1) {
		this.pic1 = pic1;
	}

	public String getPic2() {
		return pic2;
	}

	public void setPic2(String pic2) {
		this.pic2 = pic2;
	}

	public String getPic3() {
		return pic3;
	}

	public void setPic3(String pic3) {
		this.pic3 = pic3;
	}

	public String getPic4() {
		return pic4;
	}

	public void setPic4(String pic4) {
		this.pic4 = pic4;
	}

	public String getPic5() {
		return pic5;
	}

	public void setPic5(String pic5) {
		this.pic5 = pic5;
	}

	public Map<Integer, String> getTypeset() {
		return typeset;
	}

	public void setTypeset(Map<Integer, String> typeset) {
		this.typeset = typeset;
	}
	
	

	public String getPic6() {
		return pic6;
	}

	public void setPic6(String pic6) {
		this.pic6 = pic6;
	}

	public String getPic7() {
		return pic7;
	}

	public void setPic7(String pic7) {
		this.pic7 = pic7;
	}

	/**
	 * 
	 */
	public void initSet() {
		typeset=new ConcurrentHashMap<>();
		if (!QString.isEmpty(pic1)) {
			typeset.put(1, pic1);
		}
		if (!QString.isEmpty(pic2)) {
			typeset.put(2, pic2);
		}
		if (!QString.isEmpty(pic3)) {
			typeset.put(3, pic3);
		}
		if (!QString.isEmpty(pic4)) {
			typeset.put(4, pic4);
		}
		if (!QString.isEmpty(pic5)) {
			typeset.put(5, pic5);
		}
		if (!QString.isEmpty(pic6)) {
			typeset.put(6, pic6);
		}
		if (!QString.isEmpty(pic7)) {
			typeset.put(7, pic7);
		}
	}

}
