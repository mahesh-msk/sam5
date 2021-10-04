package com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs;

import com.faiveley.samng.principal.sm.parseurs.parseursJRU.ParseurParcoursJRU;

public class ParseurParcoursJRUExplorer extends ParseurParcoursJRU {
	
	public static ParseurParcoursJRU getInstance() {
		return new ParseurParcoursJRUExplorer();
	}
}
