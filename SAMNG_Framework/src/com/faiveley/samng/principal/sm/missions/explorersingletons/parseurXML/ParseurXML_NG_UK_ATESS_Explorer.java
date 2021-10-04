package com.faiveley.samng.principal.sm.missions.explorersingletons.parseurXML;

import com.faiveley.samng.principal.sm.parseurs.parseurXML.ParseurXML1;
import com.faiveley.samng.principal.sm.parseurs.parseurXML.ParseurXML_NG_UK_ATESS;

public class ParseurXML_NG_UK_ATESS_Explorer extends ParseurXML_NG_UK_ATESS {

	private static ParseurXML_NG_UK_ATESS_Explorer instance 
		= new ParseurXML_NG_UK_ATESS_Explorer();
	
	public static ParseurXML1 getInstance() {
		return instance;
	}
}

