package com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs;

import com.faiveley.samng.principal.sm.parseurs.parseursJRU.ParseurJRUTableAssociationEvVars;

public class ParseurJRUTableAssociationEvVarsExplorer extends
ParseurJRUTableAssociationEvVars {

	private static ParseurJRUTableAssociationEvVarsExplorer instance 
	= new ParseurJRUTableAssociationEvVarsExplorer();
	
	public static ParseurJRUTableAssociationEvVars getInstance() {
		return instance;
	}
}
