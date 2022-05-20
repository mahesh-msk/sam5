package com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;

import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.OrdonnerFiltre;
import com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.VueGraphique;
import com.faiveley.samng.vuegraphique.sm.filtres.GestionnaireFiltresGraphique;

/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class ApplyFiltreAction extends Action {
	public ApplyFiltreAction() {
	}
	
	public void run() {
		System.gc();

		String currentAppliedFilterName = ActivatorVueGraphique.getDefault().getFiltresProvider().getAppliedFilter();
		ActivatorVueGraphique.getDefault().getConfigurationMng().setFiltreApplique(currentAppliedFilterName);
		
		AFiltreComposant fApplied = ActivatorVueGraphique.getDefault().getFiltresProvider().getFiltreByNom(currentAppliedFilterName);
		if(fApplied != null){
			OrdonnerFiltre.getInstance().replaceFilterGraphique(OrdonnerFiltre.getInstance().getListeFiltreGraphique(),fApplied);
		}
		OrdonnerFiltre.getInstance().setFiltreSelectGraphique(fApplied);
		
		List<AFiltreComposant> listeFiltre = OrdonnerFiltre.getInstance().getListeFiltreGraphique();
		GestionnaireFiltresGraphique gest = (GestionnaireFiltresGraphique) ActivatorVueGraphique.getDefault().getFiltresProvider().getGestionnaireFiltres();

		for(AFiltreComposant f1 : listeFiltre){
			if(f1 != null){
				gest.filtreGraphiqueParseur.effacerFiltre(f1);
				gest.filtreGraphiqueParseur.enregistrerFiltre(f1);
			}
		}
		
		ActivatorVueGraphique.getDefault().getFiltresProvider().onDataChange() ; // Update Ctx menu
		
		VueGraphique vueGraphique = getVueGraphique();
		if(vueGraphique == null) {	//we should have the view as we just pushed the button
			return;	//nothing more to do
		}
		vueGraphique.applyFilter();
		
		// Save the filter configuration (filter save was not called anymore in listener...).
		ActivatorVueGraphique.getDefault().saveConfigurationVue();

		//GestionnaireZoom.ajouterZoom(null);
	}
	
	private VueGraphique getVueGraphique() {
		IWorkbenchWindow window = ActivatorVueGraphique.getDefault().getWorkbench().getActiveWorkbenchWindow();
		IViewReference[] ivr = window.getActivePage().getViewReferences();
		VueGraphique vueGraphique;
		for (int t = 0; t < ivr.length; t++) {
			if (ivr[t].getId().equals(VueGraphique.ID)) {
				vueGraphique = (VueGraphique)ivr[t].getPart(false);
				return vueGraphique;
			}
		}
		return null;
	}
}

