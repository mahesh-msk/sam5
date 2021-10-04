package com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects;

import com.faiveley.samng.principal.sm.missions.jaxb.TypeRegroupementTemps;


public class TreeMensuel extends TreeParent{
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((regroupementTemps == null) ? 0 : regroupementTemps
						.hashCode());
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
		TreeMensuel other = (TreeMensuel) obj;
		if (regroupementTemps == null) {
			if (other.regroupementTemps != null)
				return false;
		} else if (!regroupementTemps.equals(other.regroupementTemps))
			return false;
		return true;
	}

	private TypeRegroupementTemps regroupementTemps;
	
	
	public TypeRegroupementTemps getRegroupementTemps() {
		return regroupementTemps;
	}


	public void setRegroupementTemps(TypeRegroupementTemps regroupementTemps) {
		this.regroupementTemps = regroupementTemps;
	}

	public TreeMensuel(String absoluteName) {
		super(absoluteName);
		this.name = absoluteName;	
	}
}