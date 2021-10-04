package com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs;

import com.faiveley.samng.principal.sm.parseurs.parseursATESS.ParseurParcoursAtess;

public class ParseurParcoursAtessExplorer extends ParseurParcoursAtess {

	public static ParseurParcoursAtess getInstance() {		
			return new ParseurParcoursAtessExplorer();
	}
}
