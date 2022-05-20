package com.faiveley.samng.vueliste.ihm.actions.vue.e4;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;

import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.OrdonnerFiltre;
import com.faiveley.samng.principal.sm.filtres.gestionnaires.GestionnaireFiltresListe;
import com.faiveley.samng.vueliste.ihm.ActivatorVueListe;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.e4.VueListe;

/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class ApplyFiltreAction extends Action {
	public ApplyFiltreAction() {
	}
	
	public void run() {
		runWithEvent(null);
	}
	
	public void runWithEvent(Event ev) {
		String currentAppliedFilterName = ActivatorVueListe.getDefault().getFiltresProvider().getAppliedFilter();
		ActivatorVueListe.getDefault().getConfigurationMng().setFiltreApplique(currentAppliedFilterName);
		
		AFiltreComposant fApplied = ActivatorVueListe.getDefault().getFiltresProvider().getFiltreByNom(currentAppliedFilterName);
		if(fApplied != null){
			OrdonnerFiltre.getInstance().replaceFilterGraphique(OrdonnerFiltre.getInstance().getListeFiltreListe(), fApplied);
		}
		OrdonnerFiltre.getInstance().setFiltreSelectListe(fApplied);
		
		List<AFiltreComposant> listeFiltre = OrdonnerFiltre.getInstance().getListeFiltreListe();
		GestionnaireFiltresListe gest = (GestionnaireFiltresListe) ActivatorVueListe.getDefault().getFiltresProvider().getGestionnaireFiltres();

		for(AFiltreComposant f1 : listeFiltre){
			if(f1 != null){
				gest.filtreListeParseur.effacerFiltre(f1);
				gest.filtreListeParseur.enregistrerFiltre(f1);
			}
		}
		
		ActivatorVueListe.getDefault().getFiltresProvider().onDataChange() ; // Update Ctx menu
		
		VueListe vueListe = getVueList();
		if(vueListe == null) {	//we should have the view as we just pushed the button
			return;	//nothing more to do
		}
		
		vueListe.reloadTable();
		
		// Save the filter configuration (filter save was not called anymore in listener...).
		ActivatorVueListe.getDefault().saveConfigurationVue();

	}
	
	private VueListe getVueList() {
		IWorkbenchWindow window = ActivatorVueListe.getDefault().getWorkbench().getActiveWorkbenchWindow();
		IViewReference[] ivr = window.getActivePage().getViewReferences();
		VueListe vueListe;
		for (int t = 0; t < ivr.length; t++) {
			if (ivr[t].getId().equals(VueListe.ID)) {
				vueListe = (VueListe)ivr[t].getPart(false);
				return vueListe;
			}
		}
		return null;
	}
}

