package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions;

import com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.VueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.configuration.GestionnaireVueGraphique;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class SteppedGraphAction extends VueGraphicAction {
	private boolean steppedGraph;
	public SteppedGraphAction(boolean steppedGraphe) {
		this.steppedGraph = steppedGraphe;
	}
	
	@Override
	public void run() {
		GestionnaireVueGraphique viewMng = ActivatorVueGraphique.getDefault().getConfigurationMng();
		viewMng.setMarches_escalier(this.steppedGraph);
		VueGraphique vueGraphique = getVueGraphique();
		if(vueGraphique == null) {	//we should have the view as we just pushed the button
			return;	//nothing more to do
		}
		vueGraphique.redrawGraphes(true);
	}
}
