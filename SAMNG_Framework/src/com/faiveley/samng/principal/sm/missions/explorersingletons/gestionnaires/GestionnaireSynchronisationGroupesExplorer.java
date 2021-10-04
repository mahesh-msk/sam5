package com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires;

import com.faiveley.samng.principal.sm.data.enregistrement.tom4.GestionnaireSynchronisationGroupes;

public class GestionnaireSynchronisationGroupesExplorer extends
		GestionnaireSynchronisationGroupes {

	private static GestionnaireSynchronisationGroupesExplorer instance = new GestionnaireSynchronisationGroupesExplorer();
	
	public static GestionnaireSynchronisationGroupes getInstance(){
		return instance;
	}
}
