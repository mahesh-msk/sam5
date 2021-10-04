package com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires;

import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;

public class GestionnairePoolExplorer extends GestionnairePool {

	private static GestionnairePoolExplorer instance = new GestionnairePoolExplorer();
	
	public static GestionnairePool getInstance(){
		return instance;
	}
}
