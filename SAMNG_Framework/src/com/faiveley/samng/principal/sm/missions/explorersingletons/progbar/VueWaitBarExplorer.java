package com.faiveley.samng.principal.sm.missions.explorersingletons.progbar;

import com.faiveley.samng.principal.ihm.vues.VueWaitBar;

public class VueWaitBarExplorer extends VueWaitBar {
	
	private static VueWaitBarExplorer instance = new VueWaitBarExplorer();
	
	public static VueWaitBar getInstance() {
		return instance;
	}
}
