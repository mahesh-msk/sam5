package com.faiveley.samng.vuetabulaire.ihm.actions;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.actions.filtre.ShowFilterWindowAction;


/**
 * Action that opens and shows the list view filters view
 *  
 * @author Cosmin Udroiu
 *
 */
public class ShowVueFiltresTabularAction extends ShowFilterWindowAction {

	public ShowVueFiltresTabularAction() {
		super(ActivatorData.TABULAR_VUE_FILTRE_ID);
	}
	
}
