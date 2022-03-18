package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions;

import com.faiveley.samng.principal.ihm.actions.filtre.ShowFilterWindowAction;
import com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre.VueGraphiqueFiltre;


/**
 * Action that opens and shows the graphical view filters view
 *  
 * @author Cosmin Udroiu
 *
 */
public class ShowVueFiltresGraphiqueAction extends ShowFilterWindowAction {

	public ShowVueFiltresGraphiqueAction() {
		super(VueGraphiqueFiltre.ID);
	}
	
}
