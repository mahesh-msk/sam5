package com.faiveley.samng.vuetabulaire.ihm.vues.vuefiltre;

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.listeners.IRepereChangedListener;
import com.faiveley.samng.principal.ihm.vues.search.dialogs.RechercheDialog;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.AbstractEditeurFiltre;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.AbstractProviderFiltre;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.IMoveOperationsListener;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.MoveOperationsFlags;
import com.faiveley.samng.principal.ihm.vues.vuesvbv.IVbvChangeListener;
import com.faiveley.samng.principal.ihm.vues.vuesvbv.VbvsProvider;
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
import com.faiveley.samng.principal.sm.filtres.TypeFiltre;
import com.faiveley.samng.principal.sm.filtres.gestionnaires.GestionnaireBaseFiltres;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.VitesseLimiteKVBService;
import com.faiveley.samng.vuetabulaire.ihm.ActivatorVueTabulaire;
import com.faiveley.samng.vuetabulaire.ihm.vues.vuefiltre.actions.MoveEditorLineAction;
import com.faiveley.samng.vuetabulaire.sm.filtres.NommeeFiltreComposant;

/**
 * 
 * @author Cosmin Udroiu
 * 
 */
public class VueTabulaireFiltreEditeur extends AbstractEditeurFiltre implements IMoveOperationsListener, IRepereChangedListener, IVbvChangeListener {
	private TabularFiltresEditorTable variablesTableViewer;

	public String oldValue = "";

	public VueTabulaireFiltreEditeur(Composite parent, int style, TypeFiltre filterType) {
		super(parent, style, filterType, true);
		enableComponents(false);

		openVueFiltreHelp = Messages.getString("VueTabulaireFiltreEditeur.19" + ""); //$NON-NLS-1$
		selectFiltreHelp = Messages.getString("VueTabulaireFiltreEditeur.20");
		creerFiltreHelp = Messages.getString("VueTabulaireFiltreEditeur.4");

		ActivatorData.getInstance().getProviderVBVs().addVbvListener(this);
	}

	@Override
	public boolean filtreEnregistrable() {
		// TODO Auto-generated method stub
		return true;
	}

	public void setFiltersProvider(AbstractProviderFiltre provider) {
		super.setFiltersProvider(provider);
		this.filtersProvider.updateVariablesList(this);
		ActivatorData.getInstance().addRepereListener(this);

	}

	@Override
	public boolean filtrevalide() {
		AFiltreComposant filtrecourant = editingFilter;

		if (filtrecourant == null) {
			return true;
		}

		if (filtrecourant.getEnfantCount() == 0) {
			return false;
		}

		boolean isFiltreValide = true;
		// Map<Integer, AVariableComposant> fpVariables = null;
		// fpVariables = GestionnairePool.getAllVariables();
		VbvsProvider providerVbv = ActivatorData.getInstance().getProviderVBVs();

		List<AVariableComposant> fpVariables = Util.getInstance().getAllVariablesIncludeSubvars();

		if (filtrecourant != null && fpVariables != null) {
			int nbVar = filtrecourant.getEnfantCount();
			for (int j = 0; j < nbVar; j++) {
				boolean varValide = false;
				String nameVar = filtrecourant.getEnfant(j).getNom();

				// if (GestionnaireDescripteurs
				// .getDescripteurVariableComposee(nameVar
				// .replace("[C]", "")) != null) {
				// varValide = true;
				// continue;
				// }
				if (nameVar.equals("vitesse_corrigee") || nameVar.equals("distance_corrigee")|| VitesseLimiteKVBService.VITESSE_LIMITE_KVB_NAME.equals(nameVar)) {
					varValide = true;
					continue;
				}
				// if
				// (GestionnairePool.getVariable(TypeRepere.vitesseCorrigee.getCode())!=null
				// &&
				// (!GestionnairePool.getVariable(TypeRepere.vitesseCorrigee.getCode()).getDescriptor().getM_AIdentificateurComposant().getNom().equals(nameVar)))
				// {
				// isFiltreValide=false;
				// return isFiltreValide;
				// }
				if (ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().getVBV(nameVar) != null) {
					VariableVirtuelle vbv = providerVbv.getGestionnaireVbvs().getVBV(nameVar);
					if (providerVbv.verifierValiditeVBV(vbv) == null) {
						varValide = true;
						continue;
					}
				} else {
					for (AVariableComposant var : fpVariables) {

						String nomUtilisateur = var.getDescriptor().getNomUtilisateur().getNomUtilisateur(
								Activator.getDefault().getCurrentLanguage());
						String code = var.getDescriptor().getM_AIdentificateurComposant().getNom();
						if (ActivatorVueTabulaire.getDefault().isUsesShortNames()) {
							nomUtilisateur = Util.getInstance().getNomCourtFromNomUtilisateur(nomUtilisateur);
						}
						if ((nomUtilisateur != null && nomUtilisateur.equals(nameVar))
								|| (code !=null && code.equals(nameVar))) {
							varValide = true;
							continue;
						}
					}

					if (!varValide && Util.getInstance().isVariableInXml(nameVar, true)) {
						varValide = true;
					}
				}

				if (!varValide) {
					return false;
				}
				// if (!isFiltreValide) {
				// AVariableComposant varComp = GestionnairePool
				// .getComposeeVariables().get(nameVar);
				// if (varComp != null) {
				// boolean varComposeeValide = GestionnaireVariablesComposee
				// .isVariableComposeeRenseignee(varComp);
				// isFiltreValide = varComposeeValide;
				// return isFiltreValide;
				// }
				// }

			}
		}
		return isFiltreValide;
	}

	@Override
	protected void createFilterEventsEditorPanel() {
		this.variablesTableViewer = new TabularFiltresEditorTable(this, SWT.NONE);
		this.variablesTableViewer.setRemoveRowText(" "); //$NON-NLS-1$
		this.variablesTableViewer.setColumnText(1, Messages.getString("VueTabulaireFiltreEditeur.1")); //$NON-NLS-1$
		this.variablesTableViewer.setColumnText(2, Messages.getString("VueTabulaireFiltreEditeur.2")); //$NON-NLS-1$
		this.variablesTableViewer.setColumnText(3, Messages.getString("VueTabulaireFiltreEditeur.3")); //$NON-NLS-1$
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
		super.propertyChange(evt);
		if ("FILTER_SELECTED".equals(evt.getPropertyName()) || //$NON-NLS-1$
				"FILTER_CREATED".equals(evt.getPropertyName())) { //$NON-NLS-1$

			updateOrderButtonsStatus(0);
			onFilterSelected(evt);
			return;
		}

		if ("FILTER_DUPLICATED".equals(evt.getPropertyName())) { //$NON-NLS-1$
			enableComponents(true);
		}

		if ("FILTERS_UPDATE".equals(evt.getPropertyName())) { //$NON-NLS-1$
			updateOrderButtonsStatus(0);
			onFiltersUpdate(evt);
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

		if ("VBV_DELETED".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onVariableListUpdate(evt);
			return;
		}
	}

	/**
	 * Handles the change of the current edited filter
	 * 
	 * @param evt
	 */
	protected void onFilterSelected(PropertyChangeEvent evt) {
		AFiltreComposant filtre = (AFiltreComposant) evt.getNewValue();
		if (filtre == null || filtre.getFiltreType() != acceptedFilterType)
			return;

		// : if it is the same filter that is created, then we should return
		// (there is no need to refresh the view)
		if (filtre.getEnfantCount() == 0) {
			enableComponents(true);

		} else {
			if (ActivatorVueTabulaire.getDefault().getFiltresProvider().filtrevalide(null)) {
				setHelpMsgbasic();
				enableComponents(true);

			} else {
				// externaliser
				setHelpMsgbasic(Messages.getString("VueTabulaireFiltreEditeur.21"));
				enableComponents(false);
			}
		}
		int variablesListCount = filtre.getEnfantCount();
		NommeeFiltreComposant[] namedVariables = new NommeeFiltreComposant[variablesListCount];
		AFiltreComposant var;
		String varName;
		// get the VBVs manager
		GestionnaireVBV vbvMng = ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs();
		VariableVirtuelle vbv = null;
		AVariableComposant varComposee = null;
		for (int i = 0; i < variablesListCount; i++) {
			var = filtre.getEnfant(i);
			// first check if it is a VBV
			vbv = vbvMng.getVBV(var.getNom());
			if (vbv != null) { // if the name is a VBV name
				varName = vbvMng.getVbvLabel(vbv); // get its label
			} else {
				// if is not a VBV bu ta real variable
				// check if is a composee variable
				varComposee = GestionnairePool.getInstance().getComposeeVariables().get(var.getNom());
				if (varComposee != null) {
					// if is a compose variable, get the current label
					varName = varComposee.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());
				} else {
					// get the name of the variable
					varName = Util.getInstance().getUserNameForVarFilter(var.getNom());
					if (varName == null)
						varName = GestionnaireBaseFiltres.getUserNameForVarFilter(var.getNom());
				}
			}

			if (varComposee != null) {
				if (varComposee.getDescriptor().getM_AIdentificateurComposant().getCode() != TypeRepere.tempsRelatif.getCode())

					// check first if is a VBV variable
					if (varName != null && varName.length() > 4 && vbvMng.getVBV(varName.substring(4, varName.length())) == null)
						namedVariables[i] = new NommeeFiltreComposant("[C]" + " " + varName, var); //$NON-NLS-1$ //$NON-NLS-2$
					else {
						namedVariables[i] = new NommeeFiltreComposant(varName, var); //$NON-NLS-1$ //$NON-NLS-2$
					}
			} else {
				namedVariables[i] = new NommeeFiltreComposant(varName, var);
			}
			
			AVariableComposant currVar = Util.getInstance().getVariableFromNom(namedVariables[i].getFiltre().getNom());
			if (currVar != null) {
				AVariableComposant parent = currVar.getParent();
				namedVariables[i].setParent(parent);
			}
		}

		this.variablesTableViewer.initValues(namedVariables);
	}

	protected void onFiltersUpdate(PropertyChangeEvent evt) {
		AFiltreComposant currentFiltres = (AFiltreComposant) evt.getNewValue();
		// We cannot have a null here
		if (currentFiltres == null)
			throw new IllegalArgumentException("Invalid list of filter received"); //$NON-NLS-1$
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
		// AFiltreComposant variablesList = f.getEnfant(1);
		// if(variablesList == null)
		// return;
		// this.mapVariables.clear();
		Map<String, DescripteurVariable> mapUserNamesVarDescr = new LinkedHashMap<String, DescripteurVariable>();
		// int varsListCount = variablesList.getEnfantCount();
		Set<String> fpNonExistingNames = new HashSet<String>();
		AVariableComposant var;
		String varName;
		String varUserName;
		DescripteurVariable descr2Var;

		// for(int i = 0; i < varsListCount; i++) {
		// Rempli la fen�tre de s�lection d'une variable pour le filtre
		for (Iterator<AVariableComposant> iterator = allVarList.iterator(); iterator.hasNext();) {
			// var = variablesList.getEnfant(i);
			var = iterator.next();
			descr2Var = var.getDescriptor();
			varName = descr2Var.getM_AIdentificateurComposant().getNom();
			varUserName = descr2Var.getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());

			if (!varName.equals(TypeRepere.distanceRelatif.getName()) && !varName.equals(TypeRepere.tempsRelatif.getName())) {
//				varUserName = var.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());
				// GestionnaireBaseFiltres.getUserNameForVarFilter(var.getNom());
				if (descr2Var != null && descr2Var.getTypeVariable() != TypeVariable.VAR_COMPOSEE && descr2Var.getType() != Type.reserved) {
					if (descr2Var.getM_AIdentificateurComposant().getCode() == TypeRepere.vitesse.getCode()) {
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
					mapUserNamesVarDescr.put(varUserName, descr2Var);
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
			descr2Var = vbv.getDescriptor();
			mapUserNamesVarDescr.put(varUserName, descr2Var);
			if (ActivatorData.getInstance().getProviderVBVs().verifierValiditeVBV(vbv) != null)
				fpNonExistingNames.add(varUserName);
		}

		// For the tabular view the composee variables should be added too
		Map<String, AVariableComposant> composeeVars = GestionnairePool.getInstance().getComposeeVariables();
		for (AVariableComposant compVar : composeeVars.values()) {
			descr2Var = compVar.getDescriptor();
			varUserName = descr2Var.getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());
			if (descr2Var.getM_AIdentificateurComposant().getCode() != TypeRepere.tempsRelatif.getCode())
				mapUserNamesVarDescr.put("[C]" + " " + varUserName, descr2Var); //$NON-NLS-1$ //$NON-NLS-2$
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
	protected void onEvVarsSelectionChanged(PropertyChangeEvent evt, boolean restored) {
		Object varsList = evt.getNewValue();
		if (varsList == null)
			return;
		if (varsList != this.variablesTableViewer)
			return; // usually, we shouldn't have this situation but never know
					// the evolution
		if (checkEditingFilterChange())
			enableComponents(true);
	}

	/**
	 * Check if the editing filter structure changed
	 */
	public boolean checkEditingFilterChange() {
		if (this.editingFilter == null)
			return false;
		boolean isEditingFilterChanged = super.checkEditingFilterChange();
		if (!isEditingFilterChanged) {
			isEditingFilterChanged |= this.variablesTableViewer.isChangedStateFromInitial();
		}
		boolean b = this.filtersProvider.filterContentChanged(this.editingFilter, !isEditingFilterChanged);
		return b;
	}

	protected void enableComponents(boolean enabled) {
		super.enableComponents(enabled);
		this.variablesTableViewer.setEnabled(enabled);
	}

	/**
	 * Handler for preparing the saving of the filter
	 */
	@Override
	protected boolean prepareFilterSaving() {
		// We must have always exactly 2 childs in this kind of filter
		// remove all existing event filters
		this.editingFilter.removeAll();
		List<NommeeFiltreComposant> selVariables = this.variablesTableViewer.getSelectedValues();
		int nbVar = 0;
		checkFiltersToBeSaved(selVariables);
		for (NommeeFiltreComposant selVar : selVariables) {
			this.editingFilter.ajouter(selVar.getFiltre());
			nbVar = nbVar + 1;
		}
		this.variablesTableViewer.resetChangedStateFromInitial();
		if (nbVar > 0)
			return true;
		else
			return false;
	}

	/**
	 * Checks that the filters that are about to be saved are valid An
	 * IllegalArgumentException is thrown if a filter is inconsistent
	 * 
	 * @param selVariables
	 * @return
	 */
	private boolean checkFiltersToBeSaved(List<NommeeFiltreComposant> selVariables) {
		AFiltreComposant filter;
		AFiltreComposant operatorFilter;
		AFiltreComposant valueFilter;
		String opFilterName;
		String valFilterName;
		String message;
		boolean isInvalidOperator;
		boolean isInvalidValue;

		for (NommeeFiltreComposant namedFilter : selVariables) {
			filter = namedFilter.getFiltre();
			operatorFilter = filter.getEnfant(0);
			valueFilter = filter.getEnfant(1);
			opFilterName = operatorFilter.getNom().trim();
			valFilterName = valueFilter.getNom().trim();
			isInvalidOperator = "".equals(opFilterName); //$NON-NLS-1$
			isInvalidValue = "".equals(valFilterName); //$NON-NLS-1$
			message = null;
			if (isInvalidOperator && !isInvalidValue) {
				message = valFilterName + " " + Messages.getString("VueTabulaireFiltreEditeur.14"); //$NON-NLS-1$
			} else if (isInvalidValue && !isInvalidOperator) {
				message = Messages.getString("VueTabulaireFiltreEditeur.15") + namedFilter.getNomUtilisateur() + " variable"; //$NON-NLS-1$ //$NON-NLS-2$
			}

			if ((isInvalidOperator || isInvalidValue) && message != null) {
				MessageBox msgBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_ERROR | SWT.OK);
				msgBox.setText(""); //$NON-NLS-1$
				msgBox.setMessage(message); //$NON-NLS-1$
				msgBox.open();

				throw new IllegalArgumentException("Invalid operator or variable value"); //$NON-NLS-1$
			}
		}
		return true;
	}

	/**
	 * Handler for notifications that the move flags changed and the buttons
	 * should be enabled/disabled accordingly
	 */
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
				this.variablesTableViewer.initialOptionValues.put(descrVitesseCorrigee.getM_AIdentificateurComposant().getNom(), descrVitesseCorrigee);
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
				this.variablesTableViewer.initialOptionValues.remove(descrVitesseCorrigee.getM_AIdentificateurComposant().getNom());
				this.filtersProvider.updateVariablesList(this);
			}
		}
	}

	public void onVbvAdded(String vbvName, String oldVbvName) {
		GestionnaireVBV vbvMng = ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs();
		VariableVirtuelle vbv = vbvMng.getVBV(vbvName);
		DescripteurVariable descrVar = vbv.getDescriptor();
		this.variablesTableViewer.initialOptionValues.put("(V) " + vbvName, descrVar);
		this.variablesTableViewer.raffraichirListeVariables();
		this.filtersProvider.updateVariablesList(this);
	}

	public void onVbvRemoved(String vbvName) {
		this.variablesTableViewer.initialOptionValues.remove("(V) " + vbvName);
		this.variablesTableViewer.raffraichirListeVariables();
		this.filtersProvider.updateVariablesList(this);
	}

	@Override
	protected void ajouterVariabledansFiltre() {
		RechercheDialog searchDlg = new RechercheDialog(getDisplay().getActiveShell(), true);
		searchDlg.setHideVolatilVariables(true);
		searchDlg.setInputLabelText(Messages.getString("TabularFiltresEditorTable.2"));
		searchDlg.setFilterText(this.variablesTableViewer.searchFilter);
		searchDlg.setAppelant(this.getClass().getName());
		searchDlg.setTypeRecherche("Variable");

		List<String> valuesPresent = this.variablesTableViewer.getCurrentVariablesNames();
		// The oldValue should be in this case searchString
		List<String> possibleValues = this.variablesTableViewer.listDifference(this.variablesTableViewer.initialOptionValues.keySet(), valuesPresent, " ");
		possibleValues.remove(TabularFiltresEditorTable.removeRowUid); // Remove
																		// the
		// removeRowString value
		searchDlg.setSelectableValues(possibleValues.toArray(new String[this.variablesTableViewer.initialOptionValues.size()]));
		String selValue = searchDlg.open();
		this.variablesTableViewer.searchFilter = searchDlg.getFilterText(); // save
																			// the
																			// filter
		// for further
		// searches

		if (!selValue.equals(oldValue)) {
			if ("".equals(oldValue)) { // we had the last empty row
				// //$NON-NLS-1$
				// this.variablesTableViewer.itemInfo.item.setText(0,
				// Integer.toString(this.variablesTableViewer.itemsInfo
				// .size()));
				this.variablesTableViewer.addRowItem(this.variablesTableViewer.removeRow);
			}
		}
		this.variablesTableViewer.onFilterChanged();

		// this.variablesTableViewer.checkVarNameChanged(this.variablesTableViewer.itemInfo);
	}
}
