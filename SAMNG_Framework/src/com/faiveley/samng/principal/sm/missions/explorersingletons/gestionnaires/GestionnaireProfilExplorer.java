package com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires;

import com.faiveley.samng.principal.ihm.actions.profil.GestionnaireProfil;

public class GestionnaireProfilExplorer extends GestionnaireProfil {

	private static GestionnaireProfilExplorer instance = new GestionnaireProfilExplorer();
	
	public static GestionnaireProfil getInstance() {
		return instance;
	}
}
