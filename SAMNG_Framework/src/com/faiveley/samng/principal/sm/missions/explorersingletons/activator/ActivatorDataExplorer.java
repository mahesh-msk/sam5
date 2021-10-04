package com.faiveley.samng.principal.sm.missions.explorersingletons.activator;

import com.faiveley.samng.principal.data.ActivatorData;

public class ActivatorDataExplorer extends ActivatorData {

	private static ActivatorData instance = new ActivatorDataExplorer();
		
	public static ActivatorData getInstance() {		
		return instance;
	}	
	
	public static void setInstance(ActivatorData activatorData) {
		ActivatorDataExplorer.instance = activatorData;
	}
}
