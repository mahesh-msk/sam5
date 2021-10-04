package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre.VueGraphiqueFiltre;


/**
 * Action that opens and shows the graphical view filters view
 *  
 * @author Cosmin Udroiu
 *
 */
public class ShowVueFiltresGraphiqueAction extends Action {

	public ShowVueFiltresGraphiqueAction() {
	}
	
	@Override
	public void run() {
		IWorkbenchWindow window = ActivatorVueGraphique.getDefault().getWorkbench().getActiveWorkbenchWindow();
		try {
			window.getActivePage().showView(VueGraphiqueFiltre.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
}
