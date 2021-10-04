package com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs;

import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ParseurParcoursTomDIS;

public class ParseurParcoursTomDISExplorer extends ParseurParcoursTomDIS {
	public static ParseurParcoursTomDIS getInstance() {
		return new ParseurParcoursTomDISExplorer();
	}
}
