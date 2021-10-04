package com.faiveley.samng.principal.sm.missions.explorersingletons.data;

import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;

public class FabriqueParcoursExplorer extends FabriqueParcours {

	private static FabriqueParcoursExplorer instance = new FabriqueParcoursExplorer();
	
	public static FabriqueParcours getInstance() {		
		return instance;
	}	
}
