package com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects;

import java.util.ArrayList;

public class TreeParent extends TreeObject {
	private ArrayList<TreeObject> children;

	public TreeParent() {
	}
	
	public TreeParent(String name) {
		super(name);
		children = new ArrayList<TreeObject>(0);
	}
	
	public void addChild(TreeObject child) {
		children.add(child);
		child.setParent(this);
	}
	
	public void addChildren(TreeObject [] children) {
		for(TreeObject child : children)
		{
			this.addChild(child);
		}
	}
	
	public void removeChild(TreeObject child) {
		children.remove(child);
		child.setParent(null);
	}
	
	public TreeObject [] getChildren() {
		return children.toArray(new TreeObject[children.size()]);
	}
	
	public boolean hasChildren() {
		if (children!=null) {
			return children.size()>0;
		}else{
			return false;
		}	
	}	
}