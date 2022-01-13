package com.faiveley.samng.principal.ihm.vues.vuesfiltre;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.listeners.IDataChangedListener;
import com.faiveley.samng.principal.sm.controles.util.XMLName;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.FiltreComposite;
import com.faiveley.samng.principal.sm.filtres.OrdonnerFiltre;
import com.faiveley.samng.principal.sm.filtres.TypeFiltre;
import com.faiveley.samng.principal.sm.filtres.gestionnaires.AGestionnaireFiltres;
import com.faiveley.samng.principal.sm.filtres.variables.LigneVariableFiltreComposite;
/**
 * 
 * @author Cosmin Udroiu
 * 
 */
public abstract class AbstractProviderFiltre implements IDataChangedListener {

	private static final int VARIABLES_BASE_FILTERS_INDEX = 1;

	protected int newFilterCounter = 1;

	protected AFiltreComposant lastCreatedFilter = null;

	protected AGestionnaireFiltres filtersMng;

	protected TypeFiltre handledFilterType;

	protected String appliedFilterName = null;

	public static final String errDeleteAppliedFilterMsg = Messages
	.getString("AbstractProviderFiltre.0"); //$NON-NLS-1$

	public static final String questionDeleteFilterMsg = Messages
	.getString("AbstractProviderFiltre.1"); //$NON-NLS-1$

	public static final String errEmptyNameMsg = Messages
	.getString("AbstractProviderFiltre.2"); //$NON-NLS-1$

	public static final String errDuplicateFiltreNameMsg = Messages
	.getString("AbstractProviderFiltre.3"); //$NON-NLS-1$

	protected String currentXmlFileName = ""; //$NON-NLS-1$

	private String filtersManagementName = Messages
	.getString("AbstractProviderFiltre.5"); //$NON-NLS-1$

	protected transient PropertyChangeSupport listeners = new PropertyChangeSupport(
			this);

	public AbstractProviderFiltre(AGestionnaireFiltres filtersMng,
			TypeFiltre filtreType) {
		ActivatorData.getInstance().addDataListener(this);
		this.filtersMng = filtersMng;
		this.handledFilterType = filtreType;
//		currentXmlFileName=XMLName.updateCurrentXmlName();
	}

	public AGestionnaireFiltres getGestionnaireFiltres() {
		return this.filtersMng;
	}

	public AFiltreComposant getFiltreCourant() {
		return this.filtersMng.getFiltreCourant();
	}

	/**
	 * Adds a property-change listener.
	 * 
	 * @param l
	 *            the listener
	 */
	public final void addPropertyChangeListener(final PropertyChangeListener l) {
		if (l == null) {
			throw new IllegalArgumentException();
		}
		this.listeners.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(final PropertyChangeListener l) {
		this.listeners.removePropertyChangeListener(l);
	}

	/**
	 * Notificates all listeners to a model-change
	 * 
	 * @param prop
	 *            the property-id
	 * @param old
	 *            the old-value
	 * @param newValue
	 *            the new value
	 */
	protected final void firePropertyChange(final String prop,
			final Object old, final Object newValue) {
		try {
			if (this.listeners.hasListeners(prop)) {
				this.listeners.firePropertyChange(prop, old, newValue);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void createNewFilter() {
		this.currentXmlFileName = XMLName.updateCurrentXmlName();
		AFiltreComposant filter = new FiltreComposite();
		filter.setFiltreType(handledFilterType);

		// String xmlFileName = Acti
//		filter.setNom(creerUniqueFiltreNom(currentXmlFileName,
//				"_New Filter", newFilterCounter, false)); //$NON-NLS-1$
		filter.setNom(creerUniqueFiltreNom("", Messages.getString("AbstractProviderFiltre.16"), newFilterCounter, false));
		initNewFilter(filter);
		AFiltreComposant filterList = filtersMng.getListeFiltres();
		filterList.ajouter(filter);
		filtersMng.setFiltreCourant(filter);
		lastCreatedFilter = filter;
		firePropertyChange("FILTER_CREATED", null, filter); //$NON-NLS-1$
	}

	/**
	 * Méthode qui ajoute n filtres à la liste des filtres
	 * 
	 * @param listeFiltres
	 */
	public void createNewFiltersDuplicated(java.util.List<AFiltreComposant> listeFiltres) {
		AFiltreComposant filterList = filtersMng.getListeFiltres();


		boolean filtreTrouve = false;
		for (AFiltreComposant composant : listeFiltres) {

			if (filterList.getEnfantCount() > 0) {
				for (int i = 0; i < filterList.getEnfantCount(); i++) {
					if (filterList.getEnfant(i).getNom().equals(composant.getNom())) {
						filtreTrouve = true;
						showDuplicatedFilterAlreadyExistsMsgBox();
						break;
					}
				}
				if (!filtreTrouve) {
					addDuplicatedFilterToFilterList(filterList, composant);
				}

			}
			else{
				addDuplicatedFilterToFilterList(filterList, composant);
			}
			
			firePropertyChange("FILTER_DUPLICATED", null, composant);
		}

	}
	
	private void showDuplicatedFilterAlreadyExistsMsgBox() {
		MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK);
		msgBox.setText(Messages.getString("AbstractProviderFiltre.17")); //$NON-NLS-1$
		msgBox.setMessage(Messages.getString("AbstractProviderFiltre.19")); //$NON-NLS-1$
		msgBox.open();
	}

	private void addDuplicatedFilterToFilterList(AFiltreComposant filterList, AFiltreComposant composant) {
		filterList.ajouter(composant);
		filtersMng.setFiltreCourant(composant);
		lastCreatedFilter = composant;
		showDuplicatedFilterAddedMsgBox();
	}
	
	private void showDuplicatedFilterAddedMsgBox() {
		MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_INFORMATION | SWT.OK);
		msgBox.setText(Messages.getString("AbstractProviderFiltre.17")); //$NON-NLS-1$
		msgBox.setMessage(Messages.getString("AbstractProviderFiltre.18")); //$NON-NLS-1$
		msgBox.open();
	}

	protected abstract void initNewFilter(AFiltreComposant newFiltre);

	public void updateFiltersList(PropertyChangeListener l) {
		// We should notify only this listener to update its filters list
		// (if it is interested in this)
		AFiltreComposant filterList = filtersMng.getListeFiltres();
		if (filterList != null) {
			if (l != null) {
				try {
					PropertyChangeEvent evt = new PropertyChangeEvent(this,
							"FILTERS_UPDATE", //$NON-NLS-1$
							null, filterList);
					l.propertyChange(evt);
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			} else {
				firePropertyChange("FILTERS_UPDATE", null, filterList); //$NON-NLS-1$
			}
		}
	}

	public void updateVariablesList(PropertyChangeListener l) {
		// We should notify only this listener to update its filters list
		// (if it is interested in this)
		AFiltreComposant filterList = ActivatorData.getInstance().getGestionnaireBaseFiltres().getListeFiltres();
		if (filterList.getEnfantCount()>0) {
			if (l != null) {
				try {
					PropertyChangeEvent evt = new PropertyChangeEvent(this,
							"VARIABLES_LIST_UPDATE", //$NON-NLS-1$
							null, filterList);
					l.propertyChange(evt);
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			} else {
				firePropertyChange("VARIABLES_LIST_UPDATE", null, //$NON-NLS-1$
						filterList.getEnfant(VARIABLES_BASE_FILTERS_INDEX)); //$NON-NLS-1$
			}
		}
	}

	public void deleteFilter(String filterName) {
		// Check to see if the deleted filter is the currently applied one
		if (filterName.equals(appliedFilterName)) {
			MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_ERROR
					| SWT.OK);
			msgBox.setText(filtersManagementName);
			msgBox.setMessage(filterName + errDeleteAppliedFilterMsg);
			msgBox.open();
			return;
		}
		// Ask the user if he really wants to delete the selected filter
		MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_QUESTION
				| SWT.YES | SWT.NO);
		msgBox.setText(filtersManagementName);
		msgBox.setMessage(questionDeleteFilterMsg + " " +filterName + "?"); //$NON-NLS-1$
		if (msgBox.open() == SWT.NO)
			return;

		AFiltreComposant removedFilter = filtersMng.supprimerFiltre(filterName);
		OrdonnerFiltre.getInstance().getListeFiltreGraphique().remove(removedFilter);
		OrdonnerFiltre.getInstance().getListeFiltreListe().remove(removedFilter);
		if (removedFilter != null)
			firePropertyChange("FILTER_DELETED", removedFilter, null); //$NON-NLS-1$
		AFiltreComposant filterList = filtersMng.getListeFiltres();
		if (filterList.getEnfantCount() == 0) {
			AFiltreComposant filter = new FiltreComposite();
			filter.setFiltreType(handledFilterType);
			filter.setNom(""); //$NON-NLS-1$
			initNewFilter(filter);

			filterList.ajouter(filter);
			filtersMng.setFiltreCourant(filter);
			lastCreatedFilter = filter;

			firePropertyChange("FILTER_CREATED", null, filter); //$NON-NLS-1$
			firePropertyChange("FILTER_DELETED", filter, true); //$NON-NLS-1$
			filterList.supprimer(filter);

		}

	}

	public void duplicateFilter(String filterName) {
		AFiltreComposant filter = filtersMng.getFiltre(filterName);
		if (filter == null)
			throw new IllegalArgumentException(Messages
					.getString("AbstractProviderFiltre.11")); //$NON-NLS-1$

		AFiltreComposant duplicateFilter = filtersMng.dupliquerFiltre(filter,
				creerUniqueFiltreNom(filterName, Messages
						.getString("AbstractProviderFiltre.12"), 1, false)); //$NON-NLS-1$

		filtersMng.ajouterFiltre(duplicateFilter);
		filtersMng.setFiltreCourant(duplicateFilter);
		lastCreatedFilter = duplicateFilter;
		firePropertyChange("FILTER_CREATED", "true", duplicateFilter); //$NON-NLS-1$


	}

	/**
	 * Receives a clone of an existing filter in the Filters List to save it
	 * 
	 * @param filter
	 * @param newFilterName
	 */
	public void saveFilter(AFiltreComposant filter, String newFilterName) {
		// : notify filters manager about this
		if (filter != null) {
			String oldFilterName = filter.getNom();
			if (newFilterName == null || "".equals(newFilterName)) { //$NON-NLS-1$
				MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_ERROR
						| SWT.OK);
				msgBox.setText(filtersManagementName);
				msgBox.setMessage(errEmptyNameMsg);
				msgBox.open();
				return;
			}

			AFiltreComposant existingFilter1 = filtersMng.getFiltre(newFilterName);
			if (existingFilter1 != null && !filter.getNom().equals(newFilterName)) {
				MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_ERROR
						| SWT.OK);
				msgBox.setText(filtersManagementName);
				msgBox.setMessage(newFilterName + errDuplicateFiltreNameMsg);
				msgBox.open();
				return;
			}

			this.filtersMng.supprimerFiltre(oldFilterName);

			// if everything is ok, set the name and fire the save event
			filter.setNom(newFilterName);
			AFiltreComposant currentFilter = filter.clone();

			for (int i = currentFilter.getEnfantCount()-1; i >=0; i--) {
				if (currentFilter.getEnfant(i).getEnfantCount()==0) {
					currentFilter.supprimer(i);
				}
			}
			
//			for (int i =0; i<currentFilter.getEnfantCount();i++) {
//				GraphiqueFiltreComposite filtreCourant = (GraphiqueFiltreComposite)currentFilter.getEnfant(i);
//				if(filtreCourant.getEnfantCount()>0){
//					TypeVariable typeVariablePremiereLigneGraphique = GestionnairePool.getVariable(filtreCourant.getM_ALigneVariableFiltreComposant().get(0).getNom()).getDescriptor().getTypeVariable();
//					
//						if(typeVariablePremiereLigneGraphique==TypeVariable.VAR_ANALOGIC)
//							filtreCourant.setTypeGraphique(TypeGraphique.analogique);
//						else if(typeVariablePremiereLigneGraphique==TypeVariable.VAR_VIRTUAL||typeVariablePremiereLigneGraphique==TypeVariable.VAR_DISCRETE)
//							filtreCourant.setTypeGraphique(TypeGraphique.digital);		
//				}
//				
//			}
			this.filtersMng.ajouterFiltre(currentFilter);
			this.filtersMng.setFiltreCourant(currentFilter);

			firePropertyChange("FILTER_SAVED", oldFilterName, filter); //$NON-NLS-1$
			AFiltreComposant filterList = filtersMng.getListeFiltres();
			firePropertyChange("FILTERS_UPDATE", filter.getNom(), filterList); //$NON-NLS-1$
			lastCreatedFilter = null;
			
			if(oldFilterName.equals(this.appliedFilterName)) {
				this.appliedFilterName = filtersMng.getFiltreCourant().getNom() ; // Get the new name: filter renaming
				applyCurrentFilter(true);
			}

		}
	}

	/**
	 * Fires a notification that the current filter editing was canceled. If the
	 * current editing filter was just created (due to a New or Duplicate
	 * action) this filter will be deleted also from the list of filters
	 * 
	 * @param filter
	 */
	public void filterEditingCanceled(AFiltreComposant filter) {
		if (lastCreatedFilter != null) {
			filtersMng.supprimerFiltre(filter.getNom());
			// firePropertyChange("FILTER_EDIT_CANCEL", filter, null);
			// //$NON-NLS-1$
			filter = new FiltreComposite();
			filter.setFiltreType(handledFilterType);
			filter.setNom(""); //$NON-NLS-1$
			initNewFilter(filter);
			AFiltreComposant filterList = filtersMng.getListeFiltres();
			filterList.ajouter(filter);
			filtersMng.setFiltreCourant(filter);
			lastCreatedFilter = filter;
			firePropertyChange("FILTER_EDIT_CANCEL", filter, null); //$NON-NLS-1$
			firePropertyChange("FILTER_CREATED", null, filter); //$NON-NLS-1$
			firePropertyChange("FILTER_EDIT_CANCEL", filter, null); //$NON-NLS-1$
			firePropertyChange("FILTER_DELETED", filter, null); //$NON-NLS-1$
			filterList.supprimer(filter);
		} else {
			// Normally we should put the same filter here but unfortunatelly
			// the
			// firePropertyChange is checking if is the same object as old and
			// new
			firePropertyChange("FILTER_EDIT_CANCEL", filter, new Object()); //$NON-NLS-1$

		}

		lastCreatedFilter = null;
	}

	public AFiltreComposant getFiltreByNom(String nomFiltre) {
		if (nomFiltre==null) {
			return null;
		}
		if (nomFiltre.equals("defaut")) {
			return this.getGestionnaireFiltres().getFiltreDefault();
		}else{
			return filtersMng.getFiltre(nomFiltre);
		}
	}

	/**
	 * Sends a notification that the filter selection changed. The selected
	 * filter is cloned such that the editing is made on a copy of the selected
	 * filter
	 * 
	 * @param filterName
	 */
	public void filterSelected(String filterName, boolean notifyWithClone) {
		AFiltreComposant filtre = filtersMng.getFiltre(filterName);
		if (filtre != null) {
			AFiltreComposant cloneFiltre = notifyWithClone ? filtre.clone()
					: filtre;
			// when we select a filter, in fact we use a clone for editing
			filtersMng.setFiltreCourant(cloneFiltre);
			firePropertyChange("FILTER_SELECTED", null, cloneFiltre); //$NON-NLS-1$
		}
	}

	/**
	 * Returns the computed change state of the filter. If is a new created
	 * filter then it will return always true (as it is always changed until the
	 * save)
	 * 
	 * @param filter
	 *            the verified filter
	 * @param restored
	 *            the restored state computed by the caller (usually a view)
	 * @return the new change state
	 */
	public boolean filterContentChanged(AFiltreComposant filter,
			boolean restored) {
		// send notification only if we are not editing a new created filter.
		// If we are editing a new created filter this is always changed
		//if (lastCreatedFilter == null) {
		//tendu
		firePropertyChange("FILTER_CONTENT_CHANGED", restored, filter); //$NON-NLS-1$
		return !restored; // return the change state
		//}
		//return true;
	}

	public abstract boolean filtrevalide(AFiltreComposant var);

	public void applyFilterdefault(){
		try {
			firePropertyChange("FILTER_APPLIED", this.appliedFilterName, null);
		} catch (Exception e) {
			
		}		
	}
	
	public void applyCurrentFilter(boolean isSave) {
		//si filtre valide ou invalide mais doit être déselectionné : issue 815
				if (filtrevalide(null)  ||  
						(!filtrevalide(null) 
								)){

			boolean applyFilter = true;

			//test de validité des filtres para rapport à la vitesse corrigée
			if(filtersMng.getFiltreCourant().getFiltreType() != TypeFiltre.liste){

				boolean trouve = isVitesseCorrigeeFiltre();
				if(trouve 
					&& this.getAppliedFilter()!=filtersMng.getFiltreCourant().getNom()
					&& ((ActivatorData.getInstance().getPoolDonneesVues().get("axeDistanceCorrige")==null)
					|| ActivatorData.getInstance().getPoolDonneesVues().get("axeDistanceCorrige").equals(Boolean.valueOf(false)))
				){
					applyFilter = false;
				}
			}

			if(applyFilter){
				if (this.appliedFilterName == null) {
					// if we do not have in this moment a filter applied
					this.appliedFilterName = filtersMng.getFiltreCourant().getNom();
				} else {
					// if is a save operation of the currently applied filter
					// we should keep the same filter
					if (!isSave) {
						// we have already a filter applied and is not a save operation
						if (this.appliedFilterName.equals(filtersMng.getFiltreCourant()
								.getNom())) {
							// if the applied filter is the same as the current filter,
							// it means
							// the user deselected it
							this.appliedFilterName = null;
						} else {
							// the filter changed
							this.appliedFilterName = filtersMng.getFiltreCourant()
							.getNom();
						}
					}
				}
			}
			// if is not a save and the new filter name is the same as the current
			// one
			// then we don't have to apply again the filter
			// if(toggleState && (this.appliedFilterName != null)) {
			// this.appliedFilterName = null;
			// } else {
			// this.appliedFilterName = filtersMng.getFiltreCourant().getNom();
			// }

			if(!applyFilter){
				firePropertyChange("FILTER_NOT_APPLIED", null, appliedFilterName);
			}
			else
				firePropertyChange("FILTER_APPLIED", null, appliedFilterName); //$NON-NLS-1$

		}else{
			MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_WARNING| SWT.OK);
			msgBox.setText(Messages.getString("AbstractProviderFiltre.14"));
			msgBox.setMessage(Messages.getString("AbstractProviderFiltre.15"));
			msgBox.open();
		}
	}

	public String getAppliedFilter() {
		return this.appliedFilterName;
	}

	public void setAppliedFilterName(String filterName) {
		this.appliedFilterName = filterName;
		firePropertyChange(
				"APPLIED_FILTER_NAME_CHANGED", null, appliedFilterName); //$NON-NLS-1$
	}

	protected String creerUniqueFiltreNom(String namePrefix, String nameSuffix,
			int baseIdx, boolean idxAfterPrefix) {
		String name;
		// avoid adding filters with the same name
		while (true) {
			// try finding a name that do not exists yet
			name = idxAfterPrefix ? namePrefix
					+ " (" + (baseIdx++) + ")" + nameSuffix : //$NON-NLS-1$ //$NON-NLS-2$
						namePrefix + nameSuffix + " (" + (baseIdx++) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
					AFiltreComposant existingFilter = filtersMng.getFiltre(name);
					if (existingFilter == null) {
						return name;
					}
		}
	}

	public void onDataChange() {
		try {
			PropertyChangeListener[] listenersArr = this.listeners.getPropertyChangeListeners();
			for (PropertyChangeListener listener : listenersArr) {
				updateVariablesList(listener);
			}
			currentXmlFileName=XMLName.updateCurrentXmlName();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}



	public void filterMultiSelected(List filtersList) {
		if (filtersList != null) {
			firePropertyChange("FILTER_MULTI_SELECTED", null, null); //$NON-NLS-1$
		}
	}

	public AFiltreComposant getLastCreatedFilter() {
		return lastCreatedFilter;
	}

	public void setLastCreatedFilter(AFiltreComposant lastCreatedFilter) {
		this.lastCreatedFilter = lastCreatedFilter;
	}

	public abstract boolean isVitesseCorrigeeFiltrePerso(
			AFiltreComposant filtreApplique,
			LigneVariableFiltreComposite ligneCourante);
	
	/**
	 * Méthode de vérification du filtre: vérifie si le filtre contient la vitesse corrigee
	 * @return
	 */
	public boolean isVitesseCorrigeeFiltre(){
		boolean trouve =false;
		AFiltreComposant filtreApplique = this.getFiltreCourant();
		this.getFiltreCourant().getNom();
		
		LigneVariableFiltreComposite ligneCourante=null;
		trouve=isVitesseCorrigeeFiltrePerso(filtreApplique,ligneCourante);
		
		return trouve;
	}


	public abstract  boolean verifierValiditeFiltre(AFiltreComposant var);

	/**
	 * Méthode de vérification des filtres
	 * @return la liste des filtres non valides ou null si aucun filtre non valide
	 */
	public java.util.List<AFiltreComposant> verifierValiditeFiltres(){
		java.util.List<AFiltreComposant> listeFiltresNonValides = new ArrayList<AFiltreComposant>(0);
		AFiltreComposant listeFiltre = this.filtersMng.getListeFiltres();

		for(int i=0; i<listeFiltre.getEnfantCount();i++){
			boolean isValide = verifierValiditeFiltre(listeFiltre.getEnfant(i));
			if(!isValide)
				listeFiltresNonValides.add(listeFiltre.getEnfant(i));
		}

		if(listeFiltresNonValides.size()>0)
			return listeFiltresNonValides;
		else return null;
	}

}
