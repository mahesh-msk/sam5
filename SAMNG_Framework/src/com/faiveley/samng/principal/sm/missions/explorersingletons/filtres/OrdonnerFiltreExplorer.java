package com.faiveley.samng.principal.sm.missions.explorersingletons.filtres;

import com.faiveley.samng.principal.sm.filtres.OrdonnerFiltre;

public class OrdonnerFiltreExplorer extends OrdonnerFiltre {

	private static final OrdonnerFiltreExplorer INSTANCE = new OrdonnerFiltreExplorer();

	public static OrdonnerFiltre getInstance() {
		return INSTANCE;
	}
}
