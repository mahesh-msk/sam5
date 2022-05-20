package com.faiveley.samng.vuetabulaire.ihm.vues.vuefiltre.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.AbstractProviderFiltre;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.FiltreComposite;
import com.faiveley.samng.principal.sm.filtres.GestionnaireFiltresTabulaire;
import com.faiveley.samng.principal.sm.filtres.OrdonnerFiltre;
import com.faiveley.samng.principal.sm.filtres.gestionnaires.AGestionnaireFiltres;
import com.faiveley.samng.vuetabulaire.ihm.ActivatorVueTabulaire;
import com.faiveley.samng.vuetabulaire.ihm.vues.vuetabulaire.VueTabulaire;

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
		AGestionnaireFiltres filtersMng = (AGestionnaireFiltres)ActivatorVueTabulaire.getDefault().
		getFiltresProvider().getGestionnaireFiltres();
		String currentAppliedFilterName = ActivatorVueTabulaire.getDefault().getFiltresProvider().getAppliedFilter();
		
		AFiltreComposant fApplied = ActivatorVueTabulaire.getDefault().getFiltresProvider().getFiltreByNom(currentAppliedFilterName);
		if(fApplied != null){
			OrdonnerFiltre.getInstance().replaceFilterGraphique(OrdonnerFiltre.getInstance().getListeFiltreTabulaire(), fApplied);
		}
		OrdonnerFiltre.getInstance().setFiltreSelectTabulaire(fApplied);
		
		if(currentAppliedFilterName==null){
			
			FiltreComposite filtreDefaut=(FiltreComposite) filtersMng.initialiserFiltreDefaut();
			filtersMng.setFiltreDefault((FiltreComposite) filtreDefaut);
			ActivatorVueTabulaire.getDefault().getConfigurationMng().setChanged(true);
			ActivatorVueTabulaire.getDefault().getConfigurationMng().setFiltreApplique(filtreDefaut.getNom());
		}else{
		
		List<AFiltreComposant> listeFiltre = OrdonnerFiltre.getInstance().getListeFiltreTabulaire();
		AbstractProviderFiltre provider = ActivatorVueTabulaire.getDefault().getFiltresProvider();
		GestionnaireFiltresTabulaire gest = (GestionnaireFiltresTabulaire) provider.getGestionnaireFiltres();
		for(AFiltreComposant f1 : listeFiltre){
//			provider.saveFilter(f1, f1.getNom());
			if(f1 != null){
				gest.filtreTabulaireParseur.effacerFiltre(f1);
				gest.filtreTabulaireParseur.enregistrerFiltre(f1);
			}
		}
		
		ActivatorVueTabulaire.getDefault().getConfigurationMng().setFiltreApplique(currentAppliedFilterName);
		ActivatorVueTabulaire.getDefault().getConfigurationMng().setChanged(true);
		}
		
		//v�rification de la validit� des VBV
		//2 cas possibles: 
		//aucun filtre appliqu� :toutes les VBV sont v�rifi�es(else) car elles sont toutes susceptibles d'etre affich�es
		//un filtre est appliqu�: on ne v�rifie que les VBV qui sont dans le filtre
		String s = null;
		if(currentAppliedFilterName != null){
			List<String> listeVBVs = new ArrayList<String>();
		AFiltreComposant filtreApplique = ActivatorVueTabulaire.getDefault().getFiltresProvider().getFiltreByNom(currentAppliedFilterName);
		int nbVBV = filtreApplique.getEnfantCount();
		for(int i=0; i<nbVBV;i++){
			if(ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().getVBV(filtreApplique.getEnfant(i).getNom())!=null){
				listeVBVs.add(filtreApplique.getEnfant(i).getNom());
			}
		}
		if(listeVBVs.size()>0){
		//		v�rification de la validit� des VBV
		s = ActivatorData.getInstance().getProviderVBVs().verifierValiditeVBVs(listeVBVs);
		if(s!=null){
			MessageBox msgBox = new MessageBox(Display.getCurrent()
					.getActiveShell(), SWT.OK);
			msgBox.setText("v�rifications VBV");  //$NON-NLS-1$
			msgBox.setMessage(s);  //$NON-NLS-1$
			msgBox.open();
			}
		}
		}
		
		else{
			//		v�rification de la validit� des VBV
		s = ActivatorData.getInstance().getProviderVBVs().verifierValiditeVBVs(null);
		if(s!=null){
		MessageBox msgBox = new MessageBox(Display.getCurrent()
				.getActiveShell(), SWT.OK);
		msgBox.setText("v�rifications VBV");  //$NON-NLS-1$
		msgBox.setMessage(s);  //$NON-NLS-1$
		msgBox.open();}
		}
		
		ActivatorVueTabulaire.getDefault().getFiltresProvider().onDataChange() ; // Update Ctx menu
		
		VueTabulaire vueTabulaire = getVueTabulaire();
		if(vueTabulaire == null) {	//we should have the view as we just pushed the button
			return;	//nothing more to do
		}
		vueTabulaire.reloadTable();
		
		// Save the filter configuration (filter save was not called anymore in listener...).
		ActivatorVueTabulaire.getDefault().saveConfigurationVue();

	}
	
	private VueTabulaire getVueTabulaire() {
		IWorkbenchWindow window = ActivatorVueTabulaire.getDefault().getWorkbench().getActiveWorkbenchWindow();
		IViewReference[] ivr = window.getActivePage().getViewReferences();
		VueTabulaire vueTabulaire;
		for (int t = 0; t < ivr.length; t++) {
			if (ivr[t].getId().equals(VueTabulaire.ID)) {
				vueTabulaire = (VueTabulaire)ivr[t].getPart(false);
				return vueTabulaire;
			}
		}
		return null;
	}
}

