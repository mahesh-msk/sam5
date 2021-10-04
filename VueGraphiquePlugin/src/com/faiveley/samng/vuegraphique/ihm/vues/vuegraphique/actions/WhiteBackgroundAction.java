package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IWorkbenchPart;

import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.vues.MessageSelection;
import com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.VueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.curseur.Curseur;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class WhiteBackgroundAction extends VueGraphicAction {
	private boolean whiteBackground;
	public WhiteBackgroundAction(boolean whiteBackground) {
		this.whiteBackground = whiteBackground;
	}
	
	@Override
	public void run() {
		ActivatorVueGraphique.getDefault().getConfigurationMng().setFond_blanc(whiteBackground);
		Curseur.getInstance().setAddCursorAfterRedraw(true);
		VueGraphique vueGraphique = getVueGraphique();
		if(vueGraphique == null) {	//we should have the view as we just pushed the button
			return;	//nothing more to do
		}
		vueGraphique.refresh();
		
		final IWorkbenchPart activePart = Activator.getDefault().getWorkbench()
		.getActiveWorkbenchWindow().getActivePage().getActivePart();
		
		if (activePart instanceof ISelectionProvider) {
				ISelectionProvider selProvider = (ISelectionProvider) activePart;
				ISelection sel = selProvider.getSelection();
					if (!(sel instanceof MessageSelection))
						return;
					selectionChanged(activePart,sel);
		}		
	}
	
}
