package com.zeusas.dp.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zeusas.dp.exhibit.entity.MenuNode;
import com.zeusas.dp.exhibit.entity.MenuTree;

@SuppressWarnings("serial")
public class JsTree implements Serializable {

	@JsonProperty("id")
	private String id;

	@JsonProperty("text")
	private String name;

	 
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}
	@JsonProperty("parent")
	private String parent;

	@JsonProperty("children")
	private final List<JsTree> children = new ArrayList<JsTree>(); // 子节点

	public JsTree(MenuTree tree) {
		MenuNode node = tree.getNode();
		this.id = node.getCode();
		this.name = "【"+node.getCode()+"】"+node.getName();
		this.parent=node.getParentCode();

		for (MenuTree t : tree.getChildern()) {
			this.children.add(new JsTree(t));
		}
	}
	
	public List<JsTree> getChildren() {
		return children;
	}

	public void addChild(JsTree jsTree) {
		this.children.add(jsTree);
	}

	public void setChildren(List<JsTree> childrenList) {
		if (childrenList != null) {
			this.children.addAll(childrenList);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	} 

	public JsTree() {
	}
}