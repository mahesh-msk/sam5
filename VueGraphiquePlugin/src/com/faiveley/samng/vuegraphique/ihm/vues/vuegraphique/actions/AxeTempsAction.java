package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions;

import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.VueGraphique;


public class AxeTempsAction extends VueGraphicAction {

	public AxeTempsAction() {

	}
	@Override
	public void run() {
		VueGraphique vueGraphique = getVueGraphique();
		if(vueGraphique == null) {	//we should have the view as we just pushed the button
			return;	//nothing more to do
		}
		vueGraphique.afficherAxeTemps();
	}
}
