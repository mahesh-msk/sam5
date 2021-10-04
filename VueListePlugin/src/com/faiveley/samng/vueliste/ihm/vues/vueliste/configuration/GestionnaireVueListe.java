package com.faiveley.samng.vueliste.ihm.vues.vueliste.configuration;


import java.util.List;
import java.util.Set;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.listeners.IDataChangedListener;
import com.faiveley.samng.principal.ihm.vues.VueData;
import com.faiveley.samng.principal.ihm.vues.configuration.ConfigurationColonne;
import com.faiveley.samng.principal.ihm.vues.configuration.GestionnaireVueListeBase;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.AParcoursComposant;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.gestionnaires.AGestionnaireFiltres;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.vueliste.ihm.ActivatorVueListe;

/**
 * @author olivier
 * @version 1.0
 * @created 06-déc.-2007 11:23:49
 */
public class GestionnaireVueListe extends GestionnaireVueListeBase implements IDataChangedListener {
	protected static final int 	POS_EVENT 		= 4;	
	
	public GestionnaireVueListe() {
	}
	
	protected List<String> getFixedColumns() {
		//add first the other fixed columns 
		super.getFixedColumns();
		
		//Add also the event name column
		if(!isFixedColumn(EVENT_COL_NAME_STR)) {
			ConfigurationColonne colCfg;
			colCfg = new ConfigurationColonne();
			colCfg.setNom(EVENT_COL_NAME_STR);
			colCfg.setLargeur(WIDTH_EVENT);
			colCfg.setAffiche(true);
			this.fixedColumns.add(this.fixedColumns.size(), colCfg.getNom());
			ajouterColonneConfiguration(colCfg);
		}
		return this.fixedColumns;
	}

	public void onDataChange() {
		loadFromFile(RepertoiresAdresses.configurationvueslistes_CFG, Messages.getString("GestionnaireVueListe.5")); //$NON-NLS-1$ //$NON-NLS-2$
		AGestionnaireFiltres filtersMng = (AGestionnaireFiltres)ActivatorVueListe.getDefault().
													getFiltresProvider().getGestionnaireFiltres();

		//If the applied filter does not exists in the filters manager loaded filters
		//then do not apply the filter
		AFiltreComposant appliedFilter = filtersMng.getFiltre(this.filtreApplique);
		if(this.filtreApplique != null) {
			if(appliedFilter == null)
				this.filtreApplique = null;
		}
		if(appliedFilter == null) {
			ActivatorVueListe.getDefault().getFiltresProvider().setAppliedFilterName(null);
		} else {
			ActivatorVueListe.getDefault().getFiltresProvider().setAppliedFilterName(appliedFilter.getNom());
		}
		
		filtersMng.setFiltreCourant(appliedFilter);
	}
	
	@Override
	protected List<ConfigurationColonne> getColonnes(VueData vueData, String filterName, boolean useFilter) {
		//Add first the fixed columns configuration
		List<ConfigurationColonne> colCfgsList = getFixedColumnsConfiguration();

		int maxNoVars = 0;
		Enregistrement e;
		Message msg;
		int msgsCount;
		ConfigurationColonne colCfg;
		Set<Integer> msgIdsFilter = null;
		
		if(useFilter) {
			if(filterName != null && !"".equals(filterName.trim())) { //$NON-NLS-1$
				AGestionnaireFiltres filtersMng = (AGestionnaireFiltres)ActivatorVueListe.getDefault().
													getFiltresProvider().getGestionnaireFiltres();
				AFiltreComposant currentFilter = filtersMng.getFiltre(filterName);
				if(currentFilter != null) {
					msgIdsFilter =  filtersMng.getMessagesIds(ActivatorData.getInstance().getVueData(), currentFilter);
				}
			}
		}
		
		AParcoursComposant dataTable = vueData.getDataTable();
		if(dataTable==null)
		 dataTable = FabriqueParcours.getInstance().getParcours().getData();
		
		e = dataTable.getEnregistrement();
		msgsCount = e.getGoodMessagesCount();
		for (int k = 0; k < msgsCount; k++) {
			msg = e.getEnfant(k);
			//Check for event filter				
			if(msgIdsFilter != null && !msgIdsFilter.contains(Integer.valueOf(msg.getMessageId())))
				continue;
			
			if(msg.getVariablesCount()>maxNoVars)
				maxNoVars = msg.getVariablesCountWithoutUnderVariables();
		}
		
	
		for(int i = 0; i < maxNoVars; i++) {
			colCfg = new ConfigurationColonne();
			colCfg.setNom(VAR_COL_NAME_PREFIX + i);
			colCfg.setLargeur(WIDTH);
			colCfg.setAffiche(false);	
			colCfgsList.add(colCfg);
		}
		return colCfgsList;
	}
}