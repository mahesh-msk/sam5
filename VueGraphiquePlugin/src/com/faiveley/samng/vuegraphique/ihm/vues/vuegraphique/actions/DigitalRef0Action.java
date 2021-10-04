package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions;

import com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.VueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.configuration.GestionnaireVueGraphique;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class DigitalRef0Action extends VueGraphicAction {
	private boolean isRefZero;
	public DigitalRef0Action(boolean isRefZero) {
		this.isRefZero = isRefZero;
	}
	
	@Override
	public void run() {
		GestionnaireVueGraphique viewMng = ActivatorVueGraphique.getDefault().getConfigurationMng();
		viewMng.setRef_zero_digit(this.isRefZero);
		VueGraphique vueGraphique = getVueGraphique();
		if(vueGraphique == null) {	//we should have the view as we just pushed the button
			return;	//nothing more to do
		}
		//keep the same courbes, just draw the lines/or not
		vueGraphique.redrawGraphes(false);
	}
}
