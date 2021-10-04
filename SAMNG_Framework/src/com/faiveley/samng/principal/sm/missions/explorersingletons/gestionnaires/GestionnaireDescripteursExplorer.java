package com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires;

import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;

public class GestionnaireDescripteursExplorer extends GestionnaireDescripteurs {

	private static GestionnaireDescripteursExplorer instance = new GestionnaireDescripteursExplorer();
	
	public static GestionnaireDescripteurs getInstance(){		
		return instance;
	}
}
