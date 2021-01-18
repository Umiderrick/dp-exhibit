package com.zeusas.dp.exhibit.entity;


import java.util.ArrayList;
import java.util.List;

public class MenuTree {
	final MenuNode node;
	final List<MenuTree> childern;

	public MenuTree(MenuNode node) {
		childern = new ArrayList<>();
		this.node = node;
	}

	public String key() {
		return node == null ? null : node.getCode();
	}

	public void addChild(MenuTree child) {
		if (child != null) {
			this.childern.add(child);
		}
	}

	public MenuNode getNode() {
		return node;
	}

	public List<MenuTree> getChildern() {
		return childern;
	}
	
	public String seqCode(){
		return node.getSeqCode();
	}
}
