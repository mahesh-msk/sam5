package com.faiveley.samng.vueliste.ihm.actions.vue.e4;

import com.faiveley.samng.principal.ihm.actions.filtre.ShowFilterWindowAction;
import com.faiveley.samng.vueliste.ihm.vues.vuefiltre.e4.VueListeFiltre;

/**
 * Action that opens and shows the list view filters view
 *  
 * @author Cosmin Udroiu
 *
 */
public class ShowVueFiltresListeAction extends ShowFilterWindowAction {

	
	public ShowVueFiltresListeAction()
	{
		super(VueListeFiltre.ID);
	}
	
	
}
