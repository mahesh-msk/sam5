package com.faiveley.samng.principal.ihm.vues;

import java.util.ArrayList;

import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.vueliste.InstancePresentationVueDetailleeExplorer;

public class InstancePresentationVueDetaillee {
	private VueDetaillee presentation;
	
	private static InstancePresentationVueDetaillee instance = new InstancePresentationVueDetaillee();
	
	protected InstancePresentationVueDetaillee() {}
	
	public static InstancePresentationVueDetaillee getInstance() {
		if (ActivationExplorer.getInstance().isActif()) {
			return InstancePresentationVueDetailleeExplorer.getInstance();
		}
		
		return instance;
	}
	
	public void clear() {}
	
	public void enregistrerPresentation(int codeEv, int posVScroll, ArrayList<ArrayList<Integer>> expandedElements) {
		this.presentation = new VueDetaillee(codeEv, posVScroll, expandedElements);
	}

	public VueDetaillee getPresentation() {
		return presentation;
	}

	public void setPresentation(VueDetaillee presentation) {
		this.presentation = presentation;
	}
}
