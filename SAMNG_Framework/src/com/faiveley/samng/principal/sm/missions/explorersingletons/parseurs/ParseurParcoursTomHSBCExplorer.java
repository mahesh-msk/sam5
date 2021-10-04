package com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs;

import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ParseurParcoursTomHSBC;

public class ParseurParcoursTomHSBCExplorer extends ParseurParcoursTomHSBC {
	public static ParseurParcoursTomHSBC getInstance() {
		return new ParseurParcoursTomHSBCExplorer();
	}
}
