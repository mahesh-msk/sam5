package com.faiveley.samng.principal.sm.missions.explorersingletons.vueliste;

import com.faiveley.samng.principal.ihm.vues.InstancePresentationVueDetaillee;

public class InstancePresentationVueDetailleeExplorer extends
		InstancePresentationVueDetaillee {
	
	private static InstancePresentationVueDetailleeExplorer instance 
		= new InstancePresentationVueDetailleeExplorer();
	
	public static InstancePresentationVueDetaillee getInstance() {
		return instance;
	}
}
