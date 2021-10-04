package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions;

import com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.VueGraphique;

/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class DisplayMarkersAction extends VueGraphicAction {
	
	private boolean displayMarkers;
	
	public DisplayMarkersAction(boolean displayMarkers) {
		this.displayMarkers = displayMarkers;
	}
	
	@Override
	public void run() {
		ActivatorVueGraphique.getDefault().getConfigurationMng().setMarqueurs(displayMarkers);
		VueGraphique vueGraphique = getVueGraphique();
		if(vueGraphique == null) {	//we should have the view as we just pushed the button
			return;	//nothing more to do
		}
		vueGraphique.redrawGraphes(false);
	}
}
