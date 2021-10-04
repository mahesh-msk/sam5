package com.faiveley.samng.principal.sm.missions.explorersingletons.parseurXML;

import com.faiveley.samng.principal.sm.parseurs.parseurXML.ParseurXMLJRU;

public class ParseurXML_JRUExplorer extends ParseurXMLJRU {

	private static ParseurXML_JRUExplorer instance = new ParseurXML_JRUExplorer();
	
	public static ParseurXMLJRU getInstance() {
		return instance;
	}
}
