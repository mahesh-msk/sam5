package com.faiveley.samng.principal.ihm.vues.vuesfiltre;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.sm.data.enregistrement.Evenement;
import com.faiveley.samng.principal.sm.data.enregistrement.Util;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.FiltreComposite;
import com.faiveley.samng.principal.sm.filtres.TypeFiltre;
import com.faiveley.samng.principal.sm.filtres.gestionnaires.AGestionnaireFiltres;
import com.faiveley.samng.principal.sm.filtres.gestionnaires.GestionnaireFiltresListe;
import com.faiveley.samng.principal.sm.filtres.variables.LigneVariableFiltreComposite;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class ListeFiltresProvider extends AbstractProviderFiltre {



	public ListeFiltresProvider() {
		super(new GestionnaireFiltresListe(), TypeFiltre.liste);
	}

	@Override
	public boolean isVitesseCorrigeeFiltrePerso(AFiltreComposant filtreApplique, 
			LigneVariableFiltreComposite ligneCourante) {
		// TODO Auto-generated method stub
		return false;
	}

	public void updateEventsList(PropertyChangeListener l) {
		//We should notify only this listener to update its filters list 
		//(if it is interested in this)
		AFiltreComposant filterList = ActivatorData.getInstance().getGestionnaireBaseFiltres().getListeFiltres();
		if(filterList != null) {
			if(l != null) {
				try {
					if(filterList.getEnfant(0)!=null){
						PropertyChangeEvent evt = new PropertyChangeEvent(this, "EVENTS_LIST_UPDATE",null, filterList.getEnfant(0));
						l.propertyChange(evt);
					}
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			} else {
				firePropertyChange("EVENTS_LIST_UPDATE", null, filterList.getEnfant(0)); //$NON-NLS-1$
			}
		}
	}

	@Override
	public boolean filtrevalide(AFiltreComposant filtre) {
		//si filtre null, on teste le filtre appliqué
		AFiltreComposant filtrecourant=filtersMng.getFiltreCourant();

		if (filtre!=null) {
			filtrecourant=filtre;
		}
		
		if (filtrecourant==null) {
			return true;
		}		
		
		int nbEvent=filtrecourant.getEnfant(0).getEnfantCount();
		if (nbEvent==0) {
			return false;
		}
		
		for (int j = 0; j < nbEvent; j++) {
			boolean isEventValide=false;
			String nameEvent=filtrecourant.getEnfant(0).getEnfant(j).getNom();
			List<Evenement> fpEvents = null;
			fpEvents = Util.getInstance().getAllEvents();//issue 815
			for (Evenement event : fpEvents) {
				if (event.getM_ADescripteurComposant().getM_AIdentificateurComposant().getNom().equals(nameEvent)
						||event.getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()).equals(nameEvent)) {
					isEventValide=true;
				}
			}
			if (!isEventValide) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void initNewFilter(AFiltreComposant newFiltre) {
		//Add on the first and second position the filters for
		//events and variables. This is useful in order to avoid 
		//instanceof calls later and to check each time the type of the 
		//objects (to see if they are events or variables)
		newFiltre.ajouter(new FiltreComposite());
		newFiltre.ajouter(new FiltreComposite());
	}

	public void onDataChange() {
		((GestionnaireFiltresListe)filtersMng).filtreListeParseur.parseRessource(RepertoiresAdresses.getFiltres_ListesXML(),false,0,-1);
		((AGestionnaireFiltres)filtersMng).listeFiltres = ((GestionnaireFiltresListe)filtersMng).filtreListeParseur.chargerFiltres();
		((AGestionnaireFiltres)filtersMng).initialiserFiltreDefaut();
		super.onDataChange();
		try {
			PropertyChangeListener[] listenersArr = this.listeners.getPropertyChangeListeners();
			for(PropertyChangeListener listener: listenersArr) {
				updateEventsList(listener);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public boolean verifierValiditeFiltre(AFiltreComposant filtre) {
		return filtrevalide(filtre);
	}
}
