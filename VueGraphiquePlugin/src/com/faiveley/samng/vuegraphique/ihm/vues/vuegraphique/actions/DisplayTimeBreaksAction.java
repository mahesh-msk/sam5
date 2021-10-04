package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions;

import com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.VueGraphique;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class DisplayTimeBreaksAction extends VueGraphicAction {
	private boolean displayTimeBreaks;
	public DisplayTimeBreaksAction(boolean display) {
		this.displayTimeBreaks = display;
	}
	
	@Override
	public void run() {
//		AfficherRupturesTemps.getInstance().setDeClic(!this.displayTimeBreaks);
		ActivatorVueGraphique.getDefault().getConfigurationMng().setRupture_temps(displayTimeBreaks);
		VueGraphique vueGraphique = getVueGraphique();
		if(vueGraphique == null) {	//we should have the view as we just pushed the button
			return;	//nothing more to do
		}
		vueGraphique.redrawGraphes(false);
	}
}
