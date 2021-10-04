package com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires;

import com.faiveley.samng.principal.sm.data.flag.GestionnaireFlags;

public class GestionnaireFlagsExplorer extends GestionnaireFlags {
	
	private static GestionnaireFlagsExplorer instance = new GestionnaireFlagsExplorer();
	
	public static GestionnaireFlags getInstance(){	
		return instance;
	}
}
