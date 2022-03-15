package com.faiveley.samng.vueliste.ihm.actions.vue;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import com.faiveley.samng.vueliste.ihm.ActivatorVueListe;
import com.faiveley.samng.vueliste.ihm.vues.vuefiltre.VueListeFiltre;


/**
 * Action that opens and shows the list view filters view
 *  
 * @author Cosmin Udroiu
 *
 */
@Deprecated
public class ShowVueFiltresListeAction extends Action {

	public ShowVueFiltresListeAction() {
	}
	
	@Override
	public void run() {
		IWorkbenchWindow window = ActivatorVueListe.getDefault().getWorkbench().getActiveWorkbenchWindow();
		try {
			window.getActivePage().showView(VueListeFiltre.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
}
