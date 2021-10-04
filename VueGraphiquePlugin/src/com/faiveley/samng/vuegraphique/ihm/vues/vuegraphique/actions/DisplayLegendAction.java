package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions;

import com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.VueGraphique;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class DisplayLegendAction extends VueGraphicAction {
	private boolean displayLegend;
	
	public DisplayLegendAction(boolean displayLegend) {
		this.displayLegend = displayLegend;
	}
	
	@Override
	public void run() {
		ActivatorVueGraphique.getDefault().getConfigurationMng().setLegende(displayLegend);
		VueGraphique vueGraphique = getVueGraphique();
		if(vueGraphique == null) {	//we should have the view as we just pushed the button
			return;	//nothing more to do
		}
		vueGraphique.displayRighPanel();
	}

}
