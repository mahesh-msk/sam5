package com.faiveley.samng.principal.sm.missions.explorersingletons.parseurXML;

import com.faiveley.samng.principal.sm.parseurs.TypeParseur;

public class TypeParseurExplorer extends TypeParseur {
	
	private static TypeParseurExplorer instance = new TypeParseurExplorer();
	
	public static TypeParseur getInstance() {
		return instance;
	}
}
