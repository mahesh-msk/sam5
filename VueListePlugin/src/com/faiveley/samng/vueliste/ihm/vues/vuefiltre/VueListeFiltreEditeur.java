package com.faiveley.samng.vueliste.ihm.vues.vuefiltre;

import java.beans.PropertyChangeEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.AbstractEditeurFiltre;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.AbstractProviderFiltre;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.ListeFiltresProvider;
import com.faiveley.samng.principal.sm.data.enregistrement.Evenement;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.Util;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosFichierSamNg;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.FiltreComposite;
import com.faiveley.samng.principal.sm.filtres.LigneEvenementFiltre;
import com.faiveley.samng.principal.sm.filtres.TypeFiltre;
import com.faiveley.samng.principal.sm.filtres.gestionnaires.GestionnaireBaseFiltres;
import com.faiveley.samng.principal.sm.filtres.variables.LigneVariableFiltreComposite;
import com.faiveley.samng.vueliste.ihm.ActivatorVueListe;

/**
 * 
 * @author Cosmin Udroiu
 * 
 */
public class VueListeFiltreEditeur extends AbstractEditeurFiltre {

	private Composite filterEventsEditorComposite;

	private Button chooseVariablesCheck;

	private SashForm sashForm;

	private ListeFiltresEditorTable eventsTableViewer;

	private ListeFiltresEditorTable variablesTableViewer;

	private boolean varsEnChkInitialState;

	private Map<String, AFiltreComposant> mapEvents = new LinkedHashMap<String, AFiltreComposant>();

	private Map<String, AFiltreComposant> mapVariables = new LinkedHashMap<String, AFiltreComposant>();

	@Override
	public boolean filtreEnregistrable() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public VueListeFiltreEditeur(Composite parent, int style,
			TypeFiltre filterType) {
		super(parent, style, filterType, false);
		enableComponents(false);
		openVueFiltreHelp = Messages.getString("VueListeFiltreEditeur.5"); //$NON-NLS-1$
		selectFiltreHelp = Messages.getString("VueListeFiltreEditeur.6"); //$NON-NLS-1$
		creerFiltreHelp = Messages.getString("VueListeFiltreEditeur.8");
	}

	public void setFiltersProvider(AbstractProviderFiltre provider) {
		super.setFiltersProvider(provider);
		try{
		if(((InfosFichierSamNg) FabriqueParcours.getInstance().getParcours().getInfo()).getNomFichierParcoursBinaire()!=null)
		{this.filtersProvider.updateVariablesList(this);
		((ListeFiltresProvider) this.filtersProvider).updateEventsList(this);
		}
		}
		catch(Exception ex){
			
		}
	}
	
	@Override
	public boolean filtrevalide() {
		boolean isFiltreValide=false;
		AFiltreComposant filtrecourant=editingFilter;
		
		if (filtrecourant==null) {
			return true;
		}

		int nbEvent=filtrecourant.getEnfant(0).getEnfantCount();
		
		if (nbEvent==0) {
			return false;
		}
		
		for (int j = 0; j < nbEvent; j++) {
			String nameEvent=filtrecourant.getEnfant(0).getEnfant(j).getNom();
			List<Evenement> fpEvents = null;
			fpEvents = Util.getInstance().getAllEvents();//issue 815
			for (Evenement event : fpEvents) {
				if (event.getM_ADescripteurComposant().getM_AIdentificateurComposant().getNom().equals(nameEvent)
						||event.getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()).equals(nameEvent)) {
					isFiltreValide=true;
				}
			}
			if (!isFiltreValide) {
				return false;
			}
		}
		return isFiltreValide;
	}

	@Override
	protected void createFilterEventsEditorPanel() {
		filterEventsEditorComposite = new Composite(this, SWT.BORDER);
		GridLayout filterEventsEditorCompositeLayout = new GridLayout();
		filterEventsEditorCompositeLayout.makeColumnsEqualWidth = true;
		GridData filterEventsEditorCompositeLData = new GridData();
		filterEventsEditorCompositeLData.grabExcessHorizontalSpace = true;
		filterEventsEditorCompositeLData.horizontalAlignment = GridData.FILL;
		filterEventsEditorCompositeLData.verticalAlignment = GridData.FILL;
		filterEventsEditorCompositeLData.grabExcessVerticalSpace = true;
		filterEventsEditorComposite.setLayoutData(filterEventsEditorCompositeLData);
		filterEventsEditorComposite.setLayout(filterEventsEditorCompositeLayout);

		// create a checkbox for activating the variables table
		chooseVariablesCheck = new Button(filterEventsEditorComposite,SWT.CHECK | SWT.LEFT);
		chooseVariablesCheck.setText(Messages.getString("VueListeFiltreEditeur.0")); //$NON-NLS-1$
		GridData chooseVariablesCheckLData = new GridData();
		chooseVariablesCheckLData.horizontalAlignment = GridData.FILL;
		chooseVariablesCheckLData.heightHint = 30;
		chooseVariablesCheckLData.grabExcessHorizontalSpace = true;
		chooseVariablesCheck.setLayoutData(chooseVariablesCheckLData);
		chooseVariablesCheck.setBounds(4, -3, 60, 30);

		// Create the grid data for the split
		GridData sashFormLData = new GridData();
		sashFormLData.verticalAlignment = GridData.FILL;
		sashFormLData.horizontalAlignment = GridData.FILL;
		sashFormLData.grabExcessHorizontalSpace = true;
		sashFormLData.grabExcessVerticalSpace = true;

		// Create the split
		sashForm = new SashForm(filterEventsEditorComposite, SWT.NONE);
		GridLayout sashFormLayout = new GridLayout();
		sashFormLayout.makeColumnsEqualWidth = true;
		sashForm.setLayout(sashFormLayout);
		sashForm.setLayoutData(sashFormLData);

		// Create the left view for the split
		eventsTableViewer = new ListeFiltresEditorTable(sashForm, SWT.NONE);
		eventsTableViewer.setRemoveRowText(Messages.getString("VueListeFiltreEditeur.1")); //$NON-NLS-1$
		eventsTableViewer.setMainColumnText(Messages.getString("VueListeFiltreEditeur.2")); //$NON-NLS-1$
		eventsTableViewer.addPropertyChangeListener(this);

		GridData eventsTableViewerLData = new GridData();
		eventsTableViewer.setLayoutData(eventsTableViewerLData);
		eventsTableViewer.setBounds(5, 36, 299, 31);

		// Create the right view for the split
		variablesTableViewer = new ListeFiltresEditorTable(sashForm, SWT.NONE);
		variablesTableViewer.setRemoveRowText(Messages.getString("VueListeFiltreEditeur.3")); //$NON-NLS-1$
		variablesTableViewer.setMainColumnText(Messages.getString("VueListeFiltreEditeur.4")); //$NON-NLS-1$
		variablesTableViewer.addPropertyChangeListener(this);

		GridData variablesTableViewerLData = new GridData();
		variablesTableViewer.setLayoutData(variablesTableViewerLData);
		variablesTableViewer.getTable().setBounds(5, 36, 299, 31);
		
		
		
		if(editingFilter!=null &&editingFilter.isChoixVariable())
			sashForm.setMaximizedControl(eventsTableViewer);
		else
			sashForm.setMaximizedControl(null);
		
		chooseVariablesCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// Set the affichable state of the variables filter
				AFiltreComposant filterVariables;
				if (editingFilter.getEnfantCount() > 0)
					filterVariables = editingFilter.getEnfant(0);
				else {
					filterVariables = new FiltreComposite();
					editingFilter.ajouter(filterVariables);
				}
	
				
				//filterVariables.setFiltrable(!filterVariables.isFiltrable());
				if(!chooseVariablesCheck.getSelection()){
				variablesTableViewer.setEnabled(false);
				sashForm.setMaximizedControl(eventsTableViewer);
				editingFilter.setChoixVariable(false);
				}
				else{
					variablesTableViewer.setEnabled(true);
					sashForm.setMaximizedControl(null);
					editingFilter.setChoixVariable(true);
				}
				
				checkEditingFilterChange();
			}
		});
	}

	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if ("FILTER_SELECTED".equals(evt.getPropertyName()) ||"FILTER_CREATED".equals(evt.getPropertyName())) { 
			onFilterSelected(evt);
			return;
		}

		if ("FILTERS_UPDATE".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onFiltersUpdate(evt);
			return;
		}
		if ("EVENTS_LIST_UPDATE".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onEventsListUpdate(evt);
			return;
		}

		if ("VARIABLES_LIST_UPDATE".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onVariableListUpdate(evt);
			return;
		}
		if ("CMB_SEL_CHANGED".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onEvVarsSelectionChanged(evt, false);
			return;
		}
		if ("CMB_SEL_RESTORED".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onEvVarsSelectionChanged(evt, true);
			return;
		}
	}

	protected void onFilterSelected(PropertyChangeEvent evt) {
		
		AFiltreComposant filtre = (AFiltreComposant) evt.getNewValue();
		if (filtre == null || filtre.getFiltreType() != acceptedFilterType)
			return;

		// : if it is the same filter that is created, then we should return
		// (there is no need to refresh the view)
		if (filtre.getEnfant(0).getEnfantCount() == 0
				&& filtre.getEnfant(1).getEnfantCount() == 0) {
			enableComponents(true);
		} else {
			if (ActivatorVueListe.getDefault().getFiltresProvider().filtrevalide(null)) {
				setHelpMsgbasic();
				enableComponents(true);
			} else {
				setHelpMsgbasic(Messages.getString("VueListeFiltreEditeur.7"));
				enableComponents(false);
			}
		}

		AFiltreComposant filterEvents = filtre.getEnfant(0);
		int eventsListCount = filterEvents.getEnfantCount();
		AFiltreComposant event;
		String[] filtreEventsNames = new String[eventsListCount];
		for (int i = 0; i < eventsListCount; i++) {
			event = filterEvents.getEnfant(i);
			filtreEventsNames[i] = GestionnaireBaseFiltres.getUserNameForEventFilter(event.getNom());
		}		
		
		eventsTableViewer.initValues(filtreEventsNames);

		AFiltreComposant filterVariables = null;
		chooseVariablesCheck.setSelection(filtre.isChoixVariable());
		varsEnChkInitialState = filtre.isChoixVariable();
		if (filtre.getEnfantCount() > 1) {
			filterVariables = filtre.getEnfant(1);
			if (filterVariables != null) {
				int variablesListCount = filterVariables.getEnfantCount();
				String[] filtreVariablesNames = new String[variablesListCount];
				AFiltreComposant var;
				for (int i = 0; i < variablesListCount; i++) {
					var = filterVariables.getEnfant(i);
					filtreVariablesNames[i] = GestionnaireBaseFiltres.getUserNameForVarFilter(var.getNom());
				}
				variablesTableViewer.initValues(filtreVariablesNames);
				// Take into account the affiche flag from AFiltreComposant
				//chooseVariablesCheck.setSelection(chooseVariablesCheck.getSelection());
				//variablesTableViewer.setEnabled(filterVariables.isFiltrable());
				//varsEnChkInitialState = filterVariables.isFiltrable();

//				if (filterVariables.isFiltrable()) {
//					sashForm.setMaximizedControl(null);
//				} else {
//					// CIU - correction for issue 0_23 - 043 - Case à cocher
//					// "Choose the variable to display..."
//					// if("FILTER_CREATED".equals(evt.getPropertyName())) {
//					sashForm.setMaximizedControl(eventsTableViewer);
//					// }
//				}
				if(chooseVariablesCheck.getSelection()){
					sashForm.setMaximizedControl(null);
				}else{
					sashForm.setMaximizedControl(eventsTableViewer);
				}

			}
		}else {
			variablesTableViewer.initValues(null);
			if(chooseVariablesCheck.getSelection()){
				sashForm.setMaximizedControl(null);
			}else{
				sashForm.setMaximizedControl(eventsTableViewer);
			}
		}
	}

	protected void onFiltersUpdate(PropertyChangeEvent evt) {
		AFiltreComposant currentFiltres = (AFiltreComposant) evt.getNewValue();
		// We cannot have a null here
		if (currentFiltres == null)
			throw new IllegalArgumentException("Invalid list of filter received"); //$NON-NLS-1$
		// Check if this filters update is not intended for another filters view
		// type
		if (currentFiltres.getFiltreType() != acceptedFilterType)
			return; // it is not for us
		if (currentFiltres == null || currentFiltres.getEnfantCount() <= 0) {
			enableComponents(false);
		}
	}

	protected void onEventsListUpdate(PropertyChangeEvent evt) {
		AFiltreComposant eventsList = (AFiltreComposant) evt.getNewValue();
		if (eventsList == null)
			return;
		mapEvents.clear();
		int eventsListCount = eventsList.getEnfantCount();
		AFiltreComposant event;
		Set<String> fpNonExistingNames = new HashSet<String>();
		String userName;
		for (int i = 0; i < eventsListCount; i++) {
			event = eventsList.getEnfant(i);
			userName = GestionnaireBaseFiltres.getUserNameForEventFilter(event.getNom());
			mapEvents.put(userName, event);
			if (!event.isSelectionnable()){
				fpNonExistingNames.add(userName);
			}
		}
		eventsTableViewer.setInitialOptionValues(mapEvents.keySet().toArray(new String[0]), fpNonExistingNames);
	}

	/**
	 * Handles a notification that the list of variables changed. The list of
	 * the possible values from the combo boxes will be updated.
	 * 
	 * @param evt
	 *            Variables list update notification event
	 */
	protected void onVariableListUpdate(PropertyChangeEvent evt) {
		Map<Integer, AVariableComposant> allVars=GestionnairePool.getInstance().getAllVariables();
		
		
		
		//List<AVariableComposant> allVarList = Util.getAllVariables(true);
		Set<String> mapVariables=new HashSet<String>();
		String userName;
		AVariableComposant var;
		for (Iterator<AVariableComposant> iterator = allVars.values().iterator(); iterator.hasNext();) {
			var = iterator.next();	
//			userName=var.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());
			//gestion des variables qui ont un nom utilisateur égale à "", on doit tout de meme pouvoir utiliser la variable dans un filtre liste
//			if(userName!=null && userName.equals(""))
//				userName = "("+ var.getDescriptor().getM_AIdentificateurComposant().getNom()+")";
			userName=Util.getInstance().getNomUtilisateurSiChaineVide(var);
			mapVariables.add(userName);
		}
				
		FiltreComposite f=(FiltreComposite)evt.getNewValue();
		AFiltreComposant varList = f.getEnfant(2);
		if (varList == null)
			return;
		this.mapVariables.clear();
		Set<String> fpNonExistingNames = new HashSet<String>();
		int eventsListCount = varList.getEnfantCount();
		AFiltreComposant filtreComp;
		for (int i = 0; i < eventsListCount; i++) {
			filtreComp = varList.getEnfant(i);
			userName = GestionnaireBaseFiltres.getUserNameForVarFilter(filtreComp.getNom());
			this.mapVariables.put(userName, filtreComp);
			if (!filtreComp.isSelectionnable()){
				fpNonExistingNames.add(userName);
			}
		}
		
		variablesTableViewer.setInitialOptionValues(mapVariables.toArray(new String[0]), fpNonExistingNames);
	}

	/**
	 * Handles a notification that the list of variables changed. The list of
	 * the possible values from the combo boxes will be updated.
	 * 
	 * @param evt
	 *            Variables list update notification event
	 */
	protected void onEvVarsSelectionChanged(PropertyChangeEvent evt,boolean restored) {
		ListeFiltresEditorTable eventsList = (ListeFiltresEditorTable) evt.getNewValue();
		if (eventsList == null)
			return;
		if ((eventsList != this.variablesTableViewer)&& eventsList != eventsTableViewer)
			return; // usually, we shouldn't have this situation but never know
					// the evolution
		checkEditingFilterChange();
	}

	public boolean checkEditingFilterChange() {
		if (this.editingFilter == null)
			return false;
		boolean isEditingFilterChanged =false;
		if(!this.filterNameText.isDisposed())
		isEditingFilterChanged = super.checkEditingFilterChange();
		
		if (!isEditingFilterChanged && !this.eventsTableViewer.isDisposed()) {
			isEditingFilterChanged |= this.eventsTableViewer.isChangedStateFromInitial();
			if (!isEditingFilterChanged&& !this.variablesTableViewer.isDisposed()) {
				isEditingFilterChanged |= this.variablesTableViewer.isChangedStateFromInitial();
			
				if (!isEditingFilterChanged&&!this.chooseVariablesCheck.isDisposed()) {
					isEditingFilterChanged |= (varsEnChkInitialState != chooseVariablesCheck.getSelection());
				}
			}
		}
		return this.filtersProvider.filterContentChanged(this.editingFilter,!isEditingFilterChanged);
	}

	protected void enableComponents(boolean enabled) {
		super.enableComponents(enabled);
		chooseVariablesCheck.setEnabled(enabled);
		sashForm.setEnabled(enabled);
		eventsTableViewer.setEnabled(enabled);
		variablesTableViewer.setEnabled(enabled);
	}

	@Override
	protected boolean prepareFilterSaving() {
		AFiltreComposant filterItem;
		AFiltreComposant filterEvents = editingFilter.getEnfant(0);
		// We must have always exactly 2 childs in this kind of filter
		// remove all existing event filters
		filterEvents.removeAll();
		int nbEvent = 0;
		List<String> selEvents = eventsTableViewer.getSelectedValues();
		for (String selEvent : selEvents) {
			filterItem = mapEvents.get(selEvent);
			if (filterItem == null) {
				filterItem = new LigneEvenementFiltre();
				filterItem.setNom(selEvent);
			}
			filterEvents.ajouter(filterItem);
			nbEvent = nbEvent + 1;
		}
		
		if (editingFilter.getEnfantCount()==1) {
			editingFilter.ajouter(new FiltreComposite());
		}
		
		AFiltreComposant filterVariables=editingFilter.getEnfant(1);
		// We must have always exactly 2 childs in this kind of filter
		// remove all existing event filters
		filterVariables.removeAll();
		int nbVar = 0;
		List<String> selVariables = variablesTableViewer.getSelectedValues();
		for (String selVar : selVariables) {
			filterItem = mapVariables.get(selVar);
			if (filterItem == null) {
				filterItem = new LigneVariableFiltreComposite();
				filterItem.setNom(selVar);
			}
			filterVariables.ajouter(filterItem);
			nbVar = nbVar + 1;
		}
		if (nbVar>0) {
			filterVariables.setFiltrable(true);
		}
		
		varsEnChkInitialState = chooseVariablesCheck.getSelection();
		this.eventsTableViewer.resetChangedStateFromInitial();
		this.variablesTableViewer.resetChangedStateFromInitial();

		if (nbVar > 0 || nbEvent > 0)
			return true;
		else
			return false;
	}
	
	@Override
	protected void ajouterVariabledansFiltre() {
		// TODO Auto-generated method stub
		
	}
}
