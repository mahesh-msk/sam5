package com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires;

import com.faiveley.samng.principal.sm.corrections.GestionnaireCorrection;

public class GestionnaireCorrectionExplorer extends GestionnaireCorrection {
	
	private static GestionnaireCorrectionExplorer instance = new GestionnaireCorrectionExplorer();
	
	public static GestionnaireCorrection getInstance() {
		return instance;
	}
}
