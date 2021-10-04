package com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs;

import com.faiveley.samng.principal.sm.parseurs.ParseurCorrections;

public class ParseurCorrectionsExplorer extends ParseurCorrections {
	
	private static ParseurCorrectionsExplorer instance = new ParseurCorrectionsExplorer();
	
	public static ParseurCorrections getInstance() {
		return instance;
	}
}
