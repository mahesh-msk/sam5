package com.faiveley.samng.principal.sm.linecommands;

import java.util.List;
import java.util.Set;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.AbstractActivatorVue;
import com.faiveley.samng.principal.ihm.vues.VueData;
import com.faiveley.samng.principal.ihm.vues.configuration.ConfigurationColonne;
import com.faiveley.samng.principal.ihm.vues.configuration.GestionnaireVueListeBase;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.AParcoursComposant;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.gestionnaires.AGestionnaireFiltres;

public class DummyGestionnaireVueListe extends GestionnaireVueListeBase {

	private AbstractActivatorVue activator;

	public AbstractActivatorVue getActivator() {
		return activator;
	}

	public void setActivator(AbstractActivatorVue activator) {
		this.activator = activator;
	}
	
	@Override
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

	@Override
	protected List<ConfigurationColonne> getColonnes(VueData vueData,
			String filterName, boolean useFilter) {
		// Add first the fixed columns configuration
		List<ConfigurationColonne> colCfgsList = getFixedColumnsConfiguration();

		int maxNoVars = 0;
		Message msg;
		int msgsCount;
		ConfigurationColonne colCfg;
		Set<Integer> msgIdsFilter = null;

		if (useFilter) {
			if (filterName != null && !"".equals(filterName.trim())) { //$NON-NLS-1$
				AGestionnaireFiltres filtersMng = activator
						.getFiltresProvider().getGestionnaireFiltres();
				AFiltreComposant currentFilter = filtersMng
						.getFiltre(filterName);
				if (currentFilter != null) {
					msgIdsFilter = filtersMng.getMessagesIds(ActivatorData
							.getInstance().getVueData(), currentFilter);
				}
			}
		}

		AParcoursComposant dataTable = vueData.getDataTable();
		if (dataTable == null)
			dataTable = FabriqueParcours.getInstance().getParcours().getData();


		Enregistrement e = dataTable.getEnregistrement();
		msgsCount = e.getGoodMessagesCount();
		for (int k = 0; k < msgsCount; k++) {
			msg = e.getEnfant(k);
			// Check for event filter
			if (msgIdsFilter != null
					&& !msgIdsFilter.contains(Integer.valueOf(msg
							.getMessageId())))
				continue;

			if (msg.getVariablesCount() > maxNoVars)
				maxNoVars = msg.getVariablesCountWithoutUnderVariables();
		}
		

		for (int i = 0; i < maxNoVars; i++) {
			colCfg = new ConfigurationColonne();
			colCfg.setNom(VAR_COL_NAME_PREFIX + i);
			colCfg.setLargeur(WIDTH);
			colCfg.setAffiche(true);
			colCfgsList.add(colCfg);
		}
		return colCfgsList;
	}
}
