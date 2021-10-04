package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions;

import com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.VueGraphique;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.TypeAxe;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class ChangeAxeTypeAction extends VueGraphicAction {

	private TypeAxe typeAxe;
	public ChangeAxeTypeAction(TypeAxe axeType) {
		typeAxe = axeType;
	}
	@Override
	public void run() {
		ActivatorVueGraphique.getDefault().getConfigurationMng().setAxe(typeAxe);
		VueGraphique vueGraphique = getVueGraphique();
		if(vueGraphique == null) {	//we should have the view as we just pushed the button
			return;	//nothing more to do
		}
		vueGraphique.refresh();
	}
}
