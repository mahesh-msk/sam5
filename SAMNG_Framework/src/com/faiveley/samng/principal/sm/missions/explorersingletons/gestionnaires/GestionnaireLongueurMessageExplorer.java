package com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires;

import com.faiveley.samng.principal.sm.parseurs.parseursJRU.GestionnaireLongueurMessage;

public class GestionnaireLongueurMessageExplorer extends GestionnaireLongueurMessage {
	
	private static GestionnaireLongueurMessageExplorer instance = new GestionnaireLongueurMessageExplorer();
	
	public static GestionnaireLongueurMessage getInstance(){	
		return instance;
	}
}
