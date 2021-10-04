package com.faiveley.samng.principal.sm.missions.explorersingletons.progbar;

import com.faiveley.samng.principal.ihm.vues.VueProgressBar;

public class VueProgressBarExplorer extends VueProgressBar {
	
	private static VueProgressBarExplorer instance = new VueProgressBarExplorer();
	
	public static VueProgressBar getInstance() {
		return instance;
	}
}
