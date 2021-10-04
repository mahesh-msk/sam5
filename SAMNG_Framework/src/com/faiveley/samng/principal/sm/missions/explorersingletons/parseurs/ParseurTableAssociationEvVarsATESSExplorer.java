package com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs;

import com.faiveley.samng.principal.sm.parseurs.parseursATESS.ParseurTableAssociationEvVars;

public class ParseurTableAssociationEvVarsATESSExplorer extends
		ParseurTableAssociationEvVars {
	
	private static ParseurTableAssociationEvVarsATESSExplorer instance = new ParseurTableAssociationEvVarsATESSExplorer();
	
	public static ParseurTableAssociationEvVars getInstance() {
		return instance;
	}
}
