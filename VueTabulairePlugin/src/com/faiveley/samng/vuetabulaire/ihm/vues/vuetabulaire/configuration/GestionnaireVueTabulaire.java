package com.faiveley.samng.vuetabulaire.ihm.vues.vuetabulaire.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.listeners.IDataChangedListener;
import com.faiveley.samng.principal.ihm.vues.VueData;
import com.faiveley.samng.principal.ihm.vues.configuration.ConfigurationColonne;
import com.faiveley.samng.principal.ihm.vues.configuration.GestionnaireVueListeBase;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.descripteur.Type;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnaireVariablesComposee;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.AParcoursComposant;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.Reperes;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.VariableDynamique;
import com.faiveley.samng.principal.sm.data.variableComposant.variableVirtuelle.VariableVirtuelle;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.FiltreComposite;
import com.faiveley.samng.principal.sm.filtres.gestionnaires.AGestionnaireFiltres;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.vuetabulaire.ihm.ActivatorVueTabulaire;

/**
 * @author olivier
 * @version 1.0
 * @created 06-déc.-2007 11:23:49
 */
public class GestionnaireVueTabulaire extends GestionnaireVueListeBase implements IDataChangedListener {
	public GestionnaireVueTabulaire() {
		setVbvListener(true);
	}
	
	private LinkedHashMap<String, Type> varNames;

	/**
	 * Adds the variables names for variables composee that can be associated with a message
	 * @param msg
	 * @param targetCollection
	 * @param varNamesFilters
	 */
	private void addVariablesComposeeNames(Message msg, HashMap<String, Type> targetCollection, List<String> varVolatiles, 
			Map<String, AFiltreComposant> varNamesFilters) {
		if(targetCollection == null || msg == null)
			return;
		//get the list of variables composee that can be associated to a message
		List<AVariableComposant> varComposee = GestionnaireVariablesComposee.checkForVariablesComposee(msg);
		for(AVariableComposant var: varComposee) {
			addVariableName(var, targetCollection, varVolatiles, varNamesFilters);
		}
	}

	/**
	 * Adds the variable names that pass the filter to the target collection
	 *  
	 * @param msg message that contains the variables
	 * @param reperes
	 * @param listVariables
	 * @param targetCollection
	 * @param varNamesFilters
	 */
//	SUPPR_HASHMAP_VALEURVARIABLE
	private void addVariablesNames(Message msg, Reperes reperes, 
			List<? extends AVariableComposant> listVariables, 
			HashMap<String, Type> targetCollection, List<String> varVolatiles, Map<String, AFiltreComposant> varNamesFilters) {
		if(listVariables == null || targetCollection == null || reperes == null)
			return;

		for (AVariableComposant v : listVariables) {
			if (v instanceof VariableComposite) {
				//: in this moment the type VAR_COMPOSEE does not appears in message
				//		if later this is added this should be changed accordingly
				//add subvariable for complexe or composee variables
				VariableComposite variableComposite = (VariableComposite) v;
				int size = variableComposite.getVariableCount();
				for (int i = 0; i < size; i++) {
					AVariableComposant enfant = variableComposite.getEnfant(i);
					if(enfant.getDescriptor().getType()!= Type.reserved) {
						addVariableName(enfant, targetCollection, varVolatiles, varNamesFilters);
					}
				}
			} else {
				String nom = v.getDescriptor().getM_AIdentificateurComposant().getNom();
				
				if (isFixedColumn(nom) || isNotDisplayableVar(nom))  {
					continue;
				}
				//add the variable
				if(v.getDescriptor().getType()!= Type.reserved)
					addVariableName(v, targetCollection, varVolatiles, varNamesFilters);
			}
		}	
	}

	/**
	 * Adds a variable in the target collection. The variable is added only
	 * if it passed the filter and if it was not already added in the target collection
	 * @param var
	 * @param targetCollection
	 * @param varNamesFilters
	 */
	private void addVariableName(AVariableComposant var, HashMap<String, Type> targetCollection, List<String> varVolatiles,
			Map<String, AFiltreComposant> varNamesFilters) {
		String nom = var.getDescriptor().getM_AIdentificateurComposant().getNom();
		AFiltreComposant filtreComposant = null;
		boolean isVolatile = false;
		if(varNamesFilters!=null){
			filtreComposant = varNamesFilters.get(nom);
		
		if(filtreComposant == null && nom.equals(TypeRepere.vitesseCorrigee.getName())){
			filtreComposant  = varNamesFilters.get(com.faiveley.samng.principal.sm.data.enregistrement.Messages.getString("NomRepere.4"));
			
		}else{
			filtreComposant = varNamesFilters.get(nom);
		}
		}
		if (var.getDescriptor().isVolatil()) {
			isVolatile = true;
		}
		if(varNamesFilters == null || filtreComposant != null) {
			if(targetCollection.get(nom)==null) {
				targetCollection.put(nom,var.getTypeValeur());
				if (isVolatile) {
					varVolatiles.add(nom);
				}
			}
		}
	}

	/**
	 * Handler for notifications that the binary data changed (a new file was loaded)
	 */
	public void onDataChange() {
		loadFromFile(RepertoiresAdresses.configurationvuestabulaires_CFG, "Vue Tabulaire");

		AGestionnaireFiltres filtersMng = (AGestionnaireFiltres)ActivatorVueTabulaire.getDefault().
		getFiltresProvider().getGestionnaireFiltres();

		if(this.filtreApplique==null)
			this.filtreApplique = ActivatorVueTabulaire.getDefault().getFiltresProvider().getAppliedFilter();

		AFiltreComposant filter = null;
		if(ActivatorVueTabulaire.getDefault().getFiltresProvider().verifierValiditeFiltre(filtersMng.getFiltre(this.filtreApplique)))
			//If the applied filter does not exists in the filters manager loaded filters
			//then do not apply the filter
			//or filter is not valid
			filter = filtersMng.getFiltre(this.filtreApplique);
		
		if(filter!=null && filtersMng.getFiltreNomsVars(filter).size()>0 && GestionnairePool.getInstance().getVariable(TypeRepere.vitesseCorrigee.getCode())!=null && 
				filtersMng.getFiltreNomsVars(filter).containsKey(GestionnairePool.getInstance().getVariable(TypeRepere.vitesseCorrigee.getCode()).getDescriptor().getM_AIdentificateurComposant().getNom())){

			if((ActivatorData.getInstance().getPoolDonneesVues().get("axeDistanceCorrige") ==null)){
				this.filtreApplique = null;
				filter=null;

			}}
		if(this.filtreApplique != null) {
			if(filter == null)
				this.filtreApplique = null;
		}
		
		if (FabriqueParcours.getInstance().getParcours()!=null) {
			if(filter == null) {
				if(this.filtreApplique==null){
				filter=filtersMng.initialiserFiltreDefaut();
				filtersMng.setFiltreDefault((FiltreComposite) filter);
				}
				ActivatorVueTabulaire.getDefault().getFiltresProvider().setAppliedFilterName("defaut");
			} else {
				ActivatorVueTabulaire.getDefault().getFiltresProvider().setAppliedFilterName(filter.getNom());
			}
			filtersMng.setFiltreCourant(filter);
		}
	}

	/**
	 * Overrides the method from GestionnaireVueListeBase method
	 */
	@Override
	protected List<ConfigurationColonne> getColonnes(VueData vueData, 
			String filterName, boolean useFilter) {
		//Add first the fixed columns configuration
		List<ConfigurationColonne> colCfgsList = getFixedColumnsConfiguration();
		boolean isCorrections = true;
		if(!fileHasDistanceCorrections() && !fileHasTimeCorrections())
			isCorrections = false;
		List<ConfigurationColonne> colToRemove = new ArrayList<ConfigurationColonne>();
		if(!isCorrections){		
			for (ConfigurationColonne colonne : colCfgsList) {
				if(colonne.getNom().equals(TypeRepere.distanceCorrigee.getName())&&(!fileHasDistanceCorrections()))
					colToRemove.add(colonne);
				if(colonne.getNom().equals(TypeRepere.tempsCorrigee.getName())&& (!fileHasTimeCorrections())){
					colToRemove.add(colonne);
				}
			}
		}
		colCfgsList.removeAll(colToRemove);

		Enregistrement e;
		Message msg;
		int msgsCount;
		ConfigurationColonne colCfg;
		Map<String, AFiltreComposant> varNamesFilters = null;

		if(useFilter) {
			//check if we have a configured filter
			if(filterName != null && !"".equals(filterName.trim())) {
				AGestionnaireFiltres filtersMng = (AGestionnaireFiltres)ActivatorVueTabulaire.getDefault().
				getFiltresProvider().getGestionnaireFiltres();
				AFiltreComposant filter = filtersMng.getFiltre(filterName);
				if(filter != null)
					varNamesFilters = filtersMng.getFiltreNomsVars(filter);
			}
		}

		varNames = new LinkedHashMap<String, Type>();
		List<String> varVolatiles = new ArrayList<String>();
		Reperes reperes = (Reperes)vueData.getReperes();
		AParcoursComposant dataTable = vueData.getDataTable();
		if(dataTable==null)
			dataTable = FabriqueParcours.getInstance().getParcours().getData();

		e = dataTable.getEnregistrement();
		msgsCount = e.getGoodMessagesCount();
		for (int k = 0; k < msgsCount; k++) {
			msg = e.getEnfant(k);
			addVariablesNames(msg, reperes, msg.getVariablesAnalogique(), varNames, varVolatiles, varNamesFilters);
			addVariablesNamesDynamiques(msg, reperes, msg.getVariablesComplexe(), varNames, varVolatiles, varNamesFilters);
			//: the following line should be removed as it will never add (in this moment)
			//		anything in the list, as in this moment variables composee do not exist in message
			//addVariablesNames(msg, reperes, msg.getVariablesComposee(), varNames, varNamesFilters);
			addVariablesNames(msg, reperes, msg.getVariablesDiscrete(), varNames, varVolatiles, varNamesFilters);
			addVariablesNames(msg, reperes, msg.getVariablesVirtuelle(), varNames, varVolatiles, varNamesFilters);
			addVariablesNamesDynamiques(msg, reperes, msg.getChainesDynamique(), varNames, varVolatiles, varNamesFilters);
			addVariablesNamesDynamiques(msg, reperes, msg.getStructuresDynamique(), varNames, varVolatiles, varNamesFilters);
			addVariablesNamesDynamiques(msg, reperes, msg.getTableauxDynamique(), varNames, varVolatiles, varNamesFilters);
			//check if there are any variables composee that can be associated with this message
			//(variables composee that contain a variable contained in this message)
			addVariablesComposeeNames(msg, varNames, varVolatiles, varNamesFilters);
		}
		
		int speedPos = -1;
		int correctedSpeedPos = -1;
		
		for (String strVar : varNames.keySet()) {
			if(!this.listReperesRemoved.contains(strVar)){
				colCfg = new ConfigurationColonne();
				colCfg.setNom(strVar);
				colCfg.setTypeVar(varNames.get(strVar));
				colCfg.setLargeur(WIDTH);
				colCfg.setAffiche(true);
				// On indique qu'elle est volatile si elle est dans la liste des variables volatiles
				colCfg.setVolatile(varVolatiles.contains(strVar));
				colCfgsList.add(colCfg);
			}
			//set the speed position
			if (strVar.equals(TypeRepere.vitesse.getName())) {
				speedPos = colCfgsList.size() - 1;
			}
			
			//set the corrected speed position
			if (strVar.equals(TypeRepere.vitesseCorrigee.getName())||strVar.equals(com.faiveley.samng.principal.sm.data.enregistrement.Messages.getString("NomRepere.4"))) {
				correctedSpeedPos = colCfgsList.size() - 1;
			}

		}
		List<VariableVirtuelle> listvbv = ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().getListeVBV();

		//ActivatorData.getInstance().getPoolDonneesVues().put("isVBVListChanged",Boolean.valueOf(true));
		ConfigurationColonne existingColCfg = null;
		//SUPPR_ITERATOR
//		for (Iterator iter = listvbv.iterator(); iter.hasNext();) {
//			VariableVirtuelle vbv = (VariableVirtuelle) iter.next();
//
//			if(this.mapConfigurationColonne.get(vbv.getDescriptor().getM_AIdentificateurComposant().getNom()) == null) {
//				//if the column does not exists, add the column to the configuration
//				existingColCfg = new ConfigurationColonne();
//
//				existingColCfg.setAffiche(true);
//				existingColCfg.setNom(vbv.getDescriptor().getM_AIdentificateurComposant().getNom());
//				existingColCfg.setLargeur(0);
//				this.mapConfigurationColonne.put(vbv.getDescriptor().getM_AIdentificateurComposant().getNom(), existingColCfg);
//			}
//		}
		
		for (VariableVirtuelle vbv : listvbv) {
			//VariableVirtuelle vbv = (VariableVirtuelle) iter.next();

			if(this.mapConfigurationColonne.get(vbv.getDescriptor().getM_AIdentificateurComposant().getNom()) == null) {
				//if the column does not exists, add the column to the configuration
				existingColCfg = new ConfigurationColonne();

				existingColCfg.setAffiche(true);
				existingColCfg.setNom(vbv.getDescriptor().getM_AIdentificateurComposant().getNom());
				existingColCfg.setLargeur(WIDTH);
				this.mapConfigurationColonne.put(vbv.getDescriptor().getM_AIdentificateurComposant().getNom(), existingColCfg);
			}
		}

		//the corrected speed must be inserted right after the speed column
		if (speedPos != -1 && correctedSpeedPos != -1) {
			//if corrected speed exists in list, remove it and insert it after the speed
			colCfg = colCfgsList.remove(correctedSpeedPos);
			if(!this.listReperesRemoved.contains(GestionnaireDescripteurs.getDescripteurVariable(TypeRepere.vitesseCorrigee.getCode()).getM_AIdentificateurComposant().getNom()))
				colCfgsList.add(speedPos + 1, colCfg);
		} 

		return colCfgsList;
	}

	private void addVariablesNamesDynamiques(Message msg, Reperes reperes, 
			List<? extends AVariableComposant> listVariables, 
			HashMap<String, Type> targetCollection, List<String> varVolatiles, Map<String, AFiltreComposant> varNamesFilters) {
		if(listVariables == null || targetCollection == null || reperes == null)
			return;
		
		for (AVariableComposant var : listVariables) {
			addVariableNameDynamique(msg, reperes, var, targetCollection, varVolatiles, varNamesFilters);
		}			
	}
	
	private void addVariableNameDynamique(Message msg, Reperes reperes, 
			AVariableComposant var, HashMap<String, Type> targetCollection, 
			List<String> varVolatiles, Map<String, AFiltreComposant> varNamesFilters) {
		if (var instanceof VariableComplexe) {
			VariableComplexe varComplexe = (VariableComplexe) var;
			if (varComplexe.getEnfants() != null) {
				addVariablesNamesDynamiques(msg, reperes, Arrays.asList(varComplexe.getEnfants()), 
						targetCollection, varVolatiles, varNamesFilters);
			}
		} else if (var instanceof VariableDynamique) {
			VariableDynamique varDyn = (VariableDynamique) var;
			addVariableName(varDyn.getVariableEntete(), targetCollection, varVolatiles, varNamesFilters);
			if (varDyn.getEnfants() != null) {
				addVariablesNamesDynamiques(msg, reperes, Arrays.asList(varDyn.getEnfants()), 
						targetCollection, varVolatiles, varNamesFilters);
			}
		} else if (var instanceof VariableDiscrete || var instanceof VariableAnalogique) {
			addVariableName(var, targetCollection, varVolatiles, varNamesFilters);
		}
	}

	public LinkedHashMap<String, Type> getVarNames() {
		return varNames;
	}

	public void setVarNames(LinkedHashMap<String, Type> varNames) {
		this.varNames = varNames;
	}


}