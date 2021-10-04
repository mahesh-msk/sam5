package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions;

import com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.VueGraphique;

/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class DisplayDistanceBreaksAction extends VueGraphicAction {
	private boolean displayDistanceBreaks;
	public DisplayDistanceBreaksAction(boolean display) {
		this.displayDistanceBreaks = display;
	}
	
	@Override
	public void run() {
		//AfficherRupturesDistance.getInstance().setDeClic(!this.displayDistanceBreaks);
		ActivatorVueGraphique.getDefault().getConfigurationMng().setRuptures_distances(displayDistanceBreaks);
		VueGraphique vueGraphique = getVueGraphique();
		if(vueGraphique == null) {	//we should have the view as we just pushed the button
			return;	//nothing more to do
		}
		vueGraphique.redrawGraphes(false);
	}

}
