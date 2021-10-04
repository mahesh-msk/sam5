package com.faiveley.samng.vuetabulaire.ihm.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.vuetabulaire.ihm.ActivatorVueTabulaire;


/**
 * Action that opens and shows the list view filters view
 *  
 * @author Cosmin Udroiu
 *
 */
public class ShowVueFiltresTabularAction extends Action {

	public ShowVueFiltresTabularAction() {
	}
	
	@Override
	public void run() {
		IWorkbenchWindow window = ActivatorVueTabulaire.getDefault().getWorkbench().getActiveWorkbenchWindow();
		try {
			window.getActivePage().showView(ActivatorData.TABULAR_VUE_FILTRE_ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
}
