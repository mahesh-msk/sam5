package com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects;

import com.faiveley.samng.principal.sm.missions.jaxb.TypeMission;


public class TreeMission extends TreeParent{
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((mission == null) ? 0 : mission.hashCode());
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
		TreeMission other = (TreeMission) obj;
		if (mission == null) {
			if (other.mission != null)
				return false;
		} else if (!mission.equals(other.mission))
			return false;
		return true;
	}


	private TypeMission mission;
	
	
	public TypeMission getMission() {
		return mission;
	}


	public void setMission(TypeMission mission) {
		this.mission = mission;
	}


	public TreeMission(String absoluteName) {
		super(absoluteName);
		this.name = absoluteName;	
	}
}