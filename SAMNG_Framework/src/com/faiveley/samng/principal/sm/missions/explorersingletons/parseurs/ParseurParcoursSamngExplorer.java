package com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs;

import com.faiveley.samng.principal.sm.parseurs.parseursTomNg.ParseurParcoursSamng;

public class ParseurParcoursSamngExplorer extends ParseurParcoursSamng {
	public static ParseurParcoursSamng getInstance() {
		return new ParseurParcoursSamngExplorer();
	}
}
