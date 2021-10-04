package com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires;

import com.faiveley.samng.principal.ihm.vues.vuemarqueurs.configuration.GestionnaireVueMarqueurs;

public class GestionnaireVueMarqueursExplorer extends GestionnaireVueMarqueurs {

	private static GestionnaireVueMarqueursExplorer instance = new GestionnaireVueMarqueursExplorer();
	
	public static GestionnaireVueMarqueurs getInstance(){
		return instance;
	}
}
