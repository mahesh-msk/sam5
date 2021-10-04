package com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs;

import com.faiveley.samng.principal.sm.parseurs.ParseurCouleurs;

public class ParseurCouleursExplorer extends ParseurCouleurs {
	
	private static ParseurCouleursExplorer instance = new ParseurCouleursExplorer();
	
	public static ParseurCouleurs getInstance() {
		return instance;
	}
}
