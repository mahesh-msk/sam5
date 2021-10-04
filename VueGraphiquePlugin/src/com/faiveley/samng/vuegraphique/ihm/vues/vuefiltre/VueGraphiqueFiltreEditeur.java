package com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre;

import java.beans.PropertyChangeEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.listeners.IRepereChangedListener;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.AbstractEditeurFiltre;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.AbstractProviderFiltre;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.IMoveOperationsListener;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.MoveOperationsFlags;
import com.faiveley.samng.principal.ihm.vues.vuesvbv.IVbvChangeListener;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.descripteur.Type;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.Util;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.variableVirtuelle.GestionnaireVBV;
import com.faiveley.samng.principal.sm.data.variableComposant.variableVirtuelle.VariableVirtuelle;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.FiltreComposite;
import com.faiveley.samng.principal.sm.filtres.GraphiqueFiltreComposite;
import com.faiveley.samng.principal.sm.filtres.TypeFiltre;
import com.faiveley.samng.principal.sm.filtres.TypeGraphique;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.VitesseLimiteKVBService;
import com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre.actions.MoveEditorLineAction;
import com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre.graphiquefiltretable.GraphiqueFiltresEditorTable;

/**
 * 
 * @author Cosmin Udroiu
 * 
 */
public class VueGraphiqueFiltreEditeur extends AbstractEditeurFiltre implements IMoveOperationsListener, IRepereChangedListener, IVbvChangeListener {
	private GraphiqueFiltresEditorTable variablesTableViewer;

	public VueGraphiqueFiltreEditeur(Composite parent, int style, TypeFiltre filterType) {
		super(parent, style, filterType, true);

		openVueFiltreHelp = Messages.getString("VueGraphiqueFiltreEditeur.3"); //$NON-NLS-1$
		selectFiltreHelp = Messages.getString("VueGraphiqueFiltreEditeur.2"); //$NON-NLS-1$
		creerFiltreHelp = Messages.getString("VueGraphiqueFiltreEditeur.4"); //$NON-NLS-1$
		ActivatorData.getInstance().getProviderVBVs().addVbvListener(this);
	}

	public void setFiltersProvider(AbstractProviderFiltre provider) {
		super.setFiltersProvider(provider);
		this.filtersProvider.updateVariablesList(this);
		ActivatorData.getInstance().addRepereListener(this);
		setHelpMsgbasic();
	}

	@Override
	public boolean filtreEnregistrable() {
		int nbVar = 0;
		List<AFiltreComposant> selVariables = GraphiqueFiltreComposite.getSelectedValues(variablesTableViewer.permanentGraphics, true);
		for (int i = 0; i < selVariables.size(); i++) {
			nbVar = nbVar + selVariables.get(i).getEnfantCount();
		}
		if (nbVar > 0)
			return true;
		else
			return false;
	}

	@Override
	protected boolean prepareFilterSaving() {

		// We must have always exactly 2 childs in this kind of filter
		// remove all existing event filters
		int nbVar = 0;
		this.editingFilter.removeAll();
		List<AFiltreComposant> selVariables = GraphiqueFiltreComposite.getSelectedValues(variablesTableViewer.permanentGraphics, true);
		for (int i = 0; i < selVariables.size(); i++) {
			nbVar = nbVar + selVariables.get(i).getEnfantCount();
		}

		for (AFiltreComposant selVar : selVariables) {
			this.editingFilter.ajouter(selVar);
		}
		this.variablesTableViewer.resetChangedStateFromInitial();
		if (nbVar > 0)
			return true;
		else
			return false;
	}

	@Override
	public boolean filtrevalide() {
		boolean isFiltreValide = true;
		AFiltreComposant filtrecourant = editingFilter;

		if (filtrecourant == null) {
			return true;
		}

		if (filtrecourant != null && filtrecourant.getEnfantCount() > 0) {
			int nbGraphes = filtrecourant.getEnfantCount();
			for (int i = 0; i < nbGraphes; i++) {
				int nbVar = filtrecourant.getEnfant(i).getEnfantCount();
				for (int j = 0; j < nbVar; j++) {
					boolean varValide = false;
					String nameVar = filtrecourant.getEnfant(i).getEnfant(j).getNom();
					// if (GestionnaireDescripteurs
					// .getDescripteurVariableComposee(nameVar.replace("[C]",
					// "")) != null) {
					// varValide = true;
					// continue;
					// }
					if (nameVar.equals("vitesse_corrigee") || nameVar.equals("distance_corrigee") || VitesseLimiteKVBService.VITESSE_LIMITE_KVB_NAME.equals(nameVar)) {
						varValide = true;
						continue;
					}
					if (ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().getVBV(nameVar) != null) {
						if (ActivatorData.getInstance().getProviderVBVs().verifierValiditeVBV(ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().getVBV(nameVar)) == null) {
							varValide = true;
							continue;
						}
					}
					// else if
					// (GestionnairePool.getVariable(TypeRepere.vitesseCorrigee.getCode())!=null
					// && (!GestionnairePool.getVariable
					// (TypeRepere.vitesseCorrigee.getCode()).getDescriptor().getM_AIdentificateurComposant().getNom().equals(nameVar)))
					// {
					// isFiltreValide=false; return isFiltreValide;
					// }

					else {
						// Map<Integer,AVariableComposant> fpVariables = null;
						// fpVariables = GestionnairePool.getAllVariables();
						List<AVariableComposant> fpVariables = Util.getInstance().getAllVariablesIncludeSubvars();
						for (AVariableComposant var : fpVariables) {

							if ((var.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()) != null && var.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()).equals(nameVar)) || (var.getDescriptor().getM_AIdentificateurComposant().getNom() != null && var.getDescriptor().getM_AIdentificateurComposant().getNom().equals(nameVar))) {
								varValide = true;
								continue;
							}

						}
					}
					if (!varValide) {
						return false;
					}
				}
			}
		} else {
			return false;
		}
		return isFiltreValide;
	}

	@Override
	protected void createFilterEventsEditorPanel() {
		this.variablesTableViewer = new GraphiqueFiltresEditorTable(this, SWT.NONE);
		this.variablesTableViewer.setRemoveRowText(" "); //$NON-NLS-1$
		this.variablesTableViewer.setColumnText(1, Messages.getString("VueGraphiqueFiltreEditeur.1")); //$NON-NLS-1$
		this.variablesTableViewer.setColumnText(2, Messages.getString("VueGraphiqueFiltreEditeur.0")); //$NON-NLS-1$
		this.variablesTableViewer.addPropertyChangeListener(this);
		this.variablesTableViewer.addMoveOperationListener(this);

		// set the move operations buttons actions
		setMoveFirstButtonAction(new MoveEditorLineAction(this.variablesTableViewer, MoveOperationsFlags.MOVE_TOP));
		setMoveUpButtonAction(new MoveEditorLineAction(this.variablesTableViewer, MoveOperationsFlags.MOVE_UP));
		setMoveDownButtonAction(new MoveEditorLineAction(this.variablesTableViewer, MoveOperationsFlags.MOVE_DOWN));
		setMoveLastButtonAction(new MoveEditorLineAction(this.variablesTableViewer, MoveOperationsFlags.MOVE_BOTTOM));

		GridLayout filterEventsEditorCompositeLayout = new GridLayout();
		filterEventsEditorCompositeLayout.makeColumnsEqualWidth = true;
		GridData filterEventsEditorCompositeLData = new GridData();
		filterEventsEditorCompositeLData.grabExcessHorizontalSpace = true;
		filterEventsEditorCompositeLData.horizontalAlignment = GridData.FILL;
		filterEventsEditorCompositeLData.verticalAlignment = GridData.FILL;
		filterEventsEditorCompositeLData.grabExcessVerticalSpace = true;
		this.variablesTableViewer.setLayoutData(filterEventsEditorCompositeLData);
		GridData tableLData = new GridData();
		tableLData.grabExcessHorizontalSpace = true;
		tableLData.horizontalAlignment = GridData.FILL;
		tableLData.verticalAlignment = GridData.FILL;
		tableLData.grabExcessVerticalSpace = true;
		this.variablesTableViewer.setLayout(filterEventsEditorCompositeLayout);
		this.variablesTableViewer.getTable().setLayoutData(tableLData);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		super.propertyChange(evt);
		if ("FILTER_SELECTED".equals(propertyName) || //$NON-NLS-1$
				"FILTER_CREATED".equals(evt.getPropertyName())) { //$NON-NLS-1$
			updateOrderButtonsStatus(0);
			onFilterSelected(evt);
			return;
		}

		if ("FILTERS_UPDATE".equals(propertyName)) { //$NON-NLS-1$
			updateOrderButtonsStatus(0);
			onFiltersUpdate(evt);
			return;
		}

		if ("VARIABLES_LIST_UPDATE".equals(propertyName)) { //$NON-NLS-1$
			onVariableListUpdate(evt);
			return;
		}
		if ("CMB_SEL_CHANGED".equals(propertyName)) { //$NON-NLS-1$
			onVarsSelectionChanged(evt, false);
			return;
		}
		if ("CMB_SEL_RESTORED".equals(propertyName)) { //$NON-NLS-1$
			onVarsSelectionChanged(evt, true);
			return;
		}
		if ("FILTER_SAVED".equals(propertyName)) { //$NON-NLS-1$
			onVarsSelectionChanged(evt, true);
			// saveFilter((FiltreComposite)evt.getNewValue());
			return;
		}
		// if ("FILTER_DELETED".equals(propertyName)) {
		// deleteFilter((FiltreComposite)evt.getOldValue());
		// }
	}

	/**
	 * Return the type of the graphic from its name
	 * 
	 * @param graphicName
	 * @return
	 */
	public TypeGraphique getGraphicType(String graphicName) {
		if (graphicName.indexOf(Messages.getString("GraphiqueFiltresEditorTable.33")) > 0) //$NON-NLS-1$
			return TypeGraphique.analogique;
		else if (graphicName.indexOf(Messages.getString("GraphiqueFiltresEditorTable.34")) > 0) //$NON-NLS-1$
			return TypeGraphique.digital;
		else
			return null;
	}

	protected void onFilterSelected(PropertyChangeEvent evt) {
		AFiltreComposant filtre = (AFiltreComposant) evt.getNewValue();
		if (filtre == null || filtre.getFiltreType() != acceptedFilterType)
			return;

		// : if it is the same filter that is created, then we should return
		// (there is no need to refresh the view)
		if (filtre.getEnfantCount() == 0) {
			enableComponents(true);
		} else {
			if (ActivatorVueGraphique.getDefault().getFiltresProvider().verifierValiditeFiltre(filtre)) {
				setHelpMsgbasic();
				enableComponents(true);
			} else {
				setHelpMsgbasic(Messages.getString("VueGraphiqueFiltreEditeur.5"));
				enableComponents(false);
			}
		}
		// : see about this
		int variablesListCount = filtre.getEnfantCount();
		GraphiqueFiltreComposite[] namedVariables = new GraphiqueFiltreComposite[variablesListCount];
		AFiltreComposant var;
		for (int i = 0; i < variablesListCount; i++) {
			var = filtre.getEnfant(i);
			((GraphiqueFiltreComposite) var).setParent((FiltreComposite) filtre);
			namedVariables[i] = (GraphiqueFiltreComposite) var;
		}
		this.variablesTableViewer.initValues(namedVariables);

	}

	protected void onFiltersUpdate(PropertyChangeEvent evt) {
		AFiltreComposant currentFiltres = (AFiltreComposant) evt.getNewValue();
		// We cannot have a null here
		if (currentFiltres == null)
			throw new IllegalArgumentException("GraphiqueFiltresEditorTable.35"); //$NON-NLS-1$
		// Check if this filters update is not intended for another filters view
		// type
		if (currentFiltres.getFiltreType() != this.acceptedFilterType)
			return; // it is not for us
		if (currentFiltres == null || currentFiltres.getEnfantCount() <= 0) {
			enableComponents(false);
		}
	}

	/**
	 * Handles a notification that the list of variables changed. The list of
	 * the possible values from the combo boxes will be updated.
	 * 
	 * @param evt
	 *            Variables list update notification event
	 */
	protected void onVariableListUpdate(PropertyChangeEvent evt) {
		// Map<Integer, AVariableComposant>
		// allVars=GestionnairePool.getAllVariables();

		List<AVariableComposant> allVarList = Util.getInstance().getAllVariablesIncludeSubvars();
		if(VitesseLimiteKVBService.isTableKVBXMLexist()){
		allVarList.add(VitesseLimiteKVBService.getInstance().getVariable());
		}

		// FiltreComposite f=(FiltreComposite)evt.getNewValue();
		// if (f==null) return;
		// AFiltreComposant variablesList = f.getEnfant(1);
		// if(variablesList == null) return;
		Map<String, DescripteurVariable> mapUserNamesVarDescr = new LinkedHashMap<String, DescripteurVariable>();
		// int eventsListCount = variablesList.getEnfantCount();
		Set<String> fpNonExistingNames = new HashSet<String>();
		AVariableComposant var;
		String varName;
		String varUserName;
		DescripteurVariable descrVar;
		// first add the descriptions from base filters
		
		for (Iterator<AVariableComposant> iterator = allVarList.iterator(); iterator.hasNext();) {
			// var = variablesList.getEnfant(i);
			var = iterator.next();
			descrVar = var.getDescriptor();
			varName = descrVar.getM_AIdentificateurComposant().getNom();
			varUserName = descrVar.getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());

			if (!varName.equals(TypeRepere.distanceRelatif.getName()) && !varName.equals(TypeRepere.tempsRelatif.getName())) {
				if (descrVar != null && descrVar.getTypeVariable() != TypeVariable.VAR_COMPOSEE && descrVar.getType() != Type.reserved) {
					if (descrVar.getM_AIdentificateurComposant().getCode() == TypeRepere.vitesse.getCode()) {
						if (ActivatorData.getInstance().getPoolDonneesVues().get("axeDistanceCorrige") != null) { //$NON-NLS-1$
							if ((Boolean) ActivatorData.getInstance().getPoolDonneesVues().get("axeDistanceCorrige")) { //$NON-NLS-1$
								DescripteurVariable descrVitesseCorrigee = GestionnaireDescripteurs.getDescripteurVariable(TypeRepere.vitesseCorrigee.getCode());
								mapUserNamesVarDescr.put(descrVitesseCorrigee.getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()), descrVitesseCorrigee);
							} else {
								DescripteurVariable descrVitesseCorrigee = GestionnaireDescripteurs.getDescripteurVariable(TypeRepere.vitesseCorrigee.getCode());
								mapUserNamesVarDescr.remove(descrVitesseCorrigee.getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()));
							}
						}
					}
					mapUserNamesVarDescr.put(varUserName, descrVar);
					// if(!var.isSelectionnable())
					// fpNonExistingNames.add(varUserName);
				}
			}
		}

		// add the variables VBV to this list. These cannot be added in the
		// GestionnaireBaseFilters as they can change dynamically (especially
		// their names)
		// which will affect access in the maps :-(
		GestionnaireVBV vbvMng = ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs();

		List<VariableVirtuelle> vbvs = vbvMng.getListeVBV();
		for (VariableVirtuelle vbv : vbvs) {
			varUserName = vbvMng.getVbvLabel(vbv);
			descrVar = vbv.getDescriptor();
			mapUserNamesVarDescr.put(varUserName, descrVar);
			if (ActivatorData.getInstance().getProviderVBVs().verifierValiditeVBV(vbv) != null)
				fpNonExistingNames.add(varUserName);
		}

		// For the tabular view the composee variables should be added too
		Map<String, AVariableComposant> composeeVars = GestionnairePool.getInstance().getComposeeVariables();
		for (AVariableComposant compVar : composeeVars.values()) {
			descrVar = compVar.getDescriptor();
			varUserName = descrVar.getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());
			if (descrVar.getM_AIdentificateurComposant().getCode() != TypeRepere.tempsRelatif.getCode())
				mapUserNamesVarDescr.put("[C]" + " " + varUserName, descrVar); //$NON-NLS-1$ //$NON-NLS-2$
		}
		this.variablesTableViewer.setInitialOptionValues(mapUserNamesVarDescr, fpNonExistingNames);
	}

	/**
	 * Handles a notification that the list of variables changed. The list of
	 * the possible values from the combo boxes will be updated.
	 * 
	 * @param evt
	 *            Variables list update notification event
	 */
	protected void onVarsSelectionChanged(PropertyChangeEvent evt, boolean restored) {
		Object varsList = evt.getNewValue();
		if (varsList == null)
			return;
		// if(varsList != this.variablesTableViewer)
		// return; //usually, we shouldn't have this situation but never know
		// the evolution
		checkEditingFilterChange();
	}

	public boolean checkEditingFilterChange() {
		if (this.editingFilter == null)
			return false;
		boolean isEditingFilterChanged = super.checkEditingFilterChange();
		if (!isEditingFilterChanged) {
			isEditingFilterChanged |= this.variablesTableViewer.isChangedStateFromInitial();
		}
		return this.filtersProvider.filterContentChanged(this.editingFilter, !isEditingFilterChanged);
	}

	protected void enableComponents(boolean enabled) {
		super.enableComponents(enabled);
		this.variablesTableViewer.setEnabled(enabled);
	}

	public void moveFlagsChanged(int flags) {
		updateOrderButtonsStatus(flags);
	}

	/**
	 * When a repere is added/modified this method notifies the listeners
	 * 
	 * @param reper
	 *            the reperes that are added or modified
	 */
	public void onRepereAdded(TypeRepere... reper) {
		for (TypeRepere repere : reper) {
			if (repere.equals(TypeRepere.vitesseCorrigee)) {
				DescripteurVariable descrVitesseCorrigee = GestionnaireDescripteurs.getDescripteurVariable(TypeRepere.vitesseCorrigee.getCode());
				this.variablesTableViewer.initialAnalogOptionValues.put(descrVitesseCorrigee.getM_AIdentificateurComposant().getNom(), descrVitesseCorrigee);
				this.filtersProvider.updateVariablesList(this);
			}
		}
	}

	/**
	 * When a repere is removed this method notifies the listeners
	 * 
	 * @param reper
	 *            the reperes that are removed
	 */
	public void onRepereRemoved(TypeRepere... reper) {
		for (TypeRepere repere : reper) {
			if (repere.equals(TypeRepere.vitesseCorrigee)) {
				DescripteurVariable descrVitesseCorrigee = GestionnaireDescripteurs.getDescripteurVariable(TypeRepere.vitesseCorrigee.getCode());
				this.variablesTableViewer.initialAnalogOptionValues.remove(descrVitesseCorrigee.getM_AIdentificateurComposant().getNom());
				this.filtersProvider.updateVariablesList(this);
			}
		}
	}

	public void onVbvAdded(String vbvName, String oldVbvName) {
		GestionnaireVBV vbvMng = ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs();
		VariableVirtuelle vbv = vbvMng.getVBV(vbvName);
		DescripteurVariable descrVar = vbv.getDescriptor();
		this.variablesTableViewer.initialDigitalOptionValues.put("(V) " + vbvName, descrVar);
		this.variablesTableViewer.updateMissingValues();
	}

	public void onVbvRemoved(String vbvName) {
		this.variablesTableViewer.initialDigitalOptionValues.remove("(V) " + vbvName);
		this.variablesTableViewer.updateMissingValues();
	}

	@Override
	protected void ajouterVariabledansFiltre() {
		// TODO Auto-generated method stub
	}
}
