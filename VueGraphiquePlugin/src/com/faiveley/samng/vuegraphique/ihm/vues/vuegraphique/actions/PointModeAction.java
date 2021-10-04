package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions;

import com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.VueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.configuration.GestionnaireVueGraphique;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.TypeMode;

/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class PointModeAction extends VueGraphicAction {
	private boolean pointMode;
	
	public PointModeAction(boolean pointMode) {
		this.pointMode = pointMode;
	}
	
	@Override
	public void run() {
		GestionnaireVueGraphique viewMng = ActivatorVueGraphique.getDefault().getConfigurationMng();
		viewMng.setMode(this.pointMode ? TypeMode.POINT : TypeMode.LINE);
		VueGraphique vueGraphique = getVueGraphique();
		if(vueGraphique == null) {	//we should have the view as we just pushed the button
			return;	//nothing more to do
		}
		vueGraphique.redrawGraphes(true);
	}

}
