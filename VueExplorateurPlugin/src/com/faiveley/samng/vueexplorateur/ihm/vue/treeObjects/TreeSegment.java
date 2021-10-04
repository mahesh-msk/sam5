package com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects;

import com.faiveley.samng.principal.sm.missions.jaxb.TypeSegment;


public class TreeSegment extends TreeObject{
	private TypeSegment segment;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((segment == null) ? 0 : segment.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TreeSegment other = (TreeSegment) obj;
		if (segment == null) {
			if (other.segment != null)
				return false;
		} else if (!segment.equals(other.segment))
			return false;
		return true;
	}

	public TypeSegment getSegment() {
		return segment;
	}

	public void setSegment(TypeSegment segment) {
		this.segment = segment;
	}

	public TreeSegment(String absoluteName) {
		super(absoluteName);
		this.name = absoluteName;	
	}
}
