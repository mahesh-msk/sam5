package com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs;

import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ParseurParcoursTomUk;

public class ParseurParcoursTomUkExplorer extends ParseurParcoursTomUk {
	public static ParseurParcoursTomUk getInstance() {
		return new ParseurParcoursTomUkExplorer();
	}
}
