package com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs;

import com.faiveley.samng.principal.sm.parseurs.ParseurFlags;

public class ParseurFlagsExplorer extends ParseurFlags {
	
	private static ParseurFlagsExplorer instance = new ParseurFlagsExplorer();
	
	public static ParseurFlags getInstance() {
		return instance;
	}
}
