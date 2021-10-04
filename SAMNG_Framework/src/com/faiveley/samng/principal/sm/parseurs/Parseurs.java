package com.faiveley.samng.principal.sm.parseurs;

public enum Parseurs {
	
	ParseurParcoursSamng(1),
	ParseurParcoursTomDIS(2),
	ParseurParcoursTomHSBC(2),
	ParseurParcoursTomUk(2),
	ParseurParcoursAtess(4);
	
	public final int ID;
	
	Parseurs(int ID){
		this.ID=ID;
	}

	public final int getID() {
		return ID;
	}
}
