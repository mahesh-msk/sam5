package com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects;

import org.eclipse.core.runtime.IAdaptable;

public class TreeObject implements IAdaptable {
	protected String name;
	private String absoluteName;
	private TreeParent parent;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((absoluteName == null) ? 0 : absoluteName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TreeObject other = (TreeObject) obj;
		if (absoluteName == null) {
			if (other.absoluteName != null)
				return false;
		} else if (!absoluteName.equals(other.absoluteName))
			return false;
		return true;
	}

	public TreeObject() {
	}
	
	public TreeObject(String absoluteName) {
		this.absoluteName = absoluteName;
	}
	
	public String getName() {
		return name;
	}	
	public String getAbsoluteName() {
		return absoluteName;
	}
	public void setParent(TreeParent parent) {
		this.parent = parent;
	}
	public TreeParent getParent() {
		return parent;
	}
	public String toString() {
		return getName();
	}
	public Object getAdapter(@SuppressWarnings("rawtypes") Class key) {
		return null;
	}
}