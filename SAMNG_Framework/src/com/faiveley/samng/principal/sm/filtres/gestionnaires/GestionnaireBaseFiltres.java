package com.faiveley.samng.principal.sm.filtres.gestionnaires;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.listeners.IDataChangedListener;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurEvenement;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.Evenement;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.Util;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.FiltreComposite;
import com.faiveley.samng.principal.sm.filtres.LigneEvenementFiltre;
import com.faiveley.samng.principal.sm.filtres.variables.LigneVariableFiltreComposite;
import com.faiveley.samng.principal.sm.parseurs.parseurXML.ParseurXML1;


/**
 * @author Administrateur
 * @version 1.0
 * @created 12-nov.-2007 15:58:28
 */
public class GestionnaireBaseFiltres extends AGestionnaireFiltres implements IDataChangedListener {
	static private Map<String, String> mapFiltreEventsUserName = new HashMap<String, String>(); 
	//static private Map<String, String> mapFiltreVarUserName = new HashMap<String, String>();
	static private Map<String, DescripteurVariable> mapNomVarDescr = new HashMap<String, DescripteurVariable>();

	private Set<String> messageVariablesNames =  new HashSet<String>();
	private Set<String> messageVariablesNames2 =  new HashSet<String>();
	private FiltreComposite filtreVariables;
	private FiltreComposite filtreVariables2;

	public GestionnaireBaseFiltres() {
		ActivatorData.getInstance().addDataListener(this);
	}

	public void finalize() throws Throwable {

	}

	@Override
	public AFiltreComposant initialiserFiltreDefaut() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void initBaseFilters() {
		mapFiltreEventsUserName.clear();
		mapNomVarDescr.clear();
		messageVariablesNames.clear();

		LigneEvenementFiltre evFilter;
		ParseurXML1 xmlParser = ParseurXML1.getInstance();
		if(xmlParser == null)
			return;

		List<Evenement> events = Util.getInstance().getAllEvents();
		Set<Evenement> fpEvents = Util.getInstance().getMessagesEvents();
		Set<String> messageEventsNames = new HashSet<String>();
		for(Evenement event: fpEvents) {
			if(event!=null)
				messageEventsNames.add(((DescripteurEvenement)event.getM_ADescripteurComposant()).getNom());
		}

		FiltreComposite filtreEvents = new FiltreComposite();
		this.listeFiltres.removeAll();	//cleanup
		this.listeFiltres.ajouter(filtreEvents);
		String evName;
		String evUserName;
		for(Evenement event: events) {
			evFilter = new LigneEvenementFiltre();
			evName = ((DescripteurEvenement)event.getM_ADescripteurComposant()).getNom();

			if( event.getNomUtilisateur()!=null)
				evUserName = event.getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());

			else evUserName = evName;

			if(evUserName == null)
				evUserName = evName;

			evFilter.setNom(evName);

			mapFiltreEventsUserName.put(evName, evUserName);
			filtreEvents.ajouter(evFilter);
			evFilter.m_GestionnaireFiltres = this;
			if(messageEventsNames.contains(evName)) {
				evFilter.setSelectionnable(true);
			}
		}
		filtreVariables = new FiltreComposite();
		this.listeFiltres.ajouter(filtreVariables);
		filtreVariables2 = new FiltreComposite();
		this.listeFiltres.ajouter(filtreVariables2);
		Set<AVariableComposant> fpVariables = GestionnairePool.getInstance().getVariablesRenseignees();
		if (fpVariables!=null) {
			try {
				for(AVariableComposant var: fpVariables) {
					addVariableNameToSet(var,messageVariablesNames);
				}

				for(AVariableComposant var: fpVariables) {
					addBaseFilter(var);
				}
				
				for(AVariableComposant var: fpVariables) {
					addVariableNameToSet2(var,messageVariablesNames2);
				}

				for(AVariableComposant var: fpVariables) {
					addBaseFilter2(var);
				}
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public void addVariableNameToSet2(AVariableComposant var,Set<String> messageVariablesNames) {
		TypeVariable typeVar = var.getDescriptor().getTypeVariable();
		if(typeVar == TypeVariable.VAR_ANALOGIC || typeVar == TypeVariable.VAR_DISCRETE 
				|| typeVar == TypeVariable.VAR_COMPLEXE || typeVar == TypeVariable.VAR_COMPOSEE) {
			messageVariablesNames.add(var.getDescriptor().getM_AIdentificateurComposant().getNom());
		} 
	}
	
	public void addVariableNameToSet(AVariableComposant var,Set<String> messageVariablesNames) {
		TypeVariable typeVar = var.getDescriptor().getTypeVariable();
		if(typeVar == TypeVariable.VAR_ANALOGIC || typeVar == TypeVariable.VAR_DISCRETE) {
			messageVariablesNames.add(var.getDescriptor().getM_AIdentificateurComposant().getNom());
		} else if(typeVar == TypeVariable.VAR_COMPLEXE || typeVar == TypeVariable.VAR_COMPOSEE) {
			addVariablesComposeeNamesToSet((VariableComposite)var,messageVariablesNames);
		}
	}

	private void addVariablesComposeeNamesToSet(VariableComposite var,Set<String> messageVariablesNames) {
		int size = var.getVariableCount();
		for (int i = 0; i < size; i++) {
			AVariableComposant subVar = var.getEnfant(i);
			addVariableNameToSet(subVar,messageVariablesNames);
		}	
	}

	private void addBaseFilter2(AVariableComposant var) {
		LigneVariableFiltreComposite varFilter;
		DescripteurVariable varDescr;
		String varName;
		TypeVariable typeVar = var.getDescriptor().getTypeVariable();
		if(typeVar == TypeVariable.VAR_ANALOGIC || typeVar == TypeVariable.VAR_DISCRETE || 
				typeVar == TypeVariable.VAR_VIRTUAL || typeVar == TypeVariable.VAR_COMPLEXE || typeVar == TypeVariable.VAR_COMPOSEE) {
			varFilter = new LigneVariableFiltreComposite();
			varDescr = var.getDescriptor();
			varName = varDescr.getM_AIdentificateurComposant().getNom();
			varFilter.setNom(varName);
			mapNomVarDescr.put(varName, varDescr);
			filtreVariables2.ajouter(varFilter);
			varFilter.m_GestionnaireFiltres = this;
			if(messageVariablesNames2.contains(varName)) {
				varFilter.setSelectionnable(true);
			}
		}
	}
	
	private void addBaseFilter(AVariableComposant var) {
		LigneVariableFiltreComposite varFilter;
		DescripteurVariable varDescr;
		String varName;
		TypeVariable typeVar = var.getDescriptor().getTypeVariable();
		if(typeVar == TypeVariable.VAR_ANALOGIC || typeVar == TypeVariable.VAR_DISCRETE || 
				typeVar == TypeVariable.VAR_VIRTUAL) {
			varFilter = new LigneVariableFiltreComposite();
			varDescr = var.getDescriptor();
			varName = varDescr.getM_AIdentificateurComposant().getNom();
			varFilter.setNom(varName);
			mapNomVarDescr.put(varName, varDescr);
			filtreVariables.ajouter(varFilter);
			varFilter.m_GestionnaireFiltres = this;
			if(messageVariablesNames.contains(varName)) {
				varFilter.setSelectionnable(true);
			}else{

			}
		} else if(typeVar == TypeVariable.VAR_COMPLEXE || typeVar == TypeVariable.VAR_COMPOSEE) {
			addBaseFilterForVarCompose((VariableComposite)var);
		}
	}

	private void addBaseFilterForVarCompose(VariableComposite var) {
		int size = var.getVariableCount();
		for (int i = 0; i < size; i++) {
			AVariableComposant subVar = var.getEnfant(i);
			addBaseFilter(subVar);
		}	
	}

	public static String getUserNameForEventFilter(String evFiltreNom) {
		if(evFiltreNom == null)
			return "";
		String evUserName = mapFiltreEventsUserName.get(evFiltreNom);
		if(evUserName == null)
			evUserName = evFiltreNom;
		return evUserName;
	}

	public static DescripteurVariable getVariableDescriptor(String varNom) {
		if(varNom == null)
			return null;
		return mapNomVarDescr.get(varNom);
	}

	public static String getUserNameForVarFilter(String varFiltreNom) {
		if(varFiltreNom == null)
			return "";
		String varUserName = null;
		//DescripteurVariable varDescr = mapNomVarDescr.get(varFiltreNom);
		AVariableComposant var = GestionnairePool.getInstance().getVariable(varFiltreNom);
		if (var==null) {
			return varFiltreNom;
		}
		DescripteurVariable varDescr = var.getDescriptor();
		
		if(varFiltreNom.equals(TypeRepere.vitesseCorrigee.getName()))
			varDescr = GestionnaireDescripteurs.getDescripteurVariableAnalogique(TypeRepere.vitesseCorrigee.getCode());
		else if(varFiltreNom.equals(TypeRepere.distanceCorrigee.getName()))
			varDescr = GestionnaireDescripteurs.getDescripteurVariableAnalogique(TypeRepere.distanceCorrigee.getCode());
		
		
		if(varDescr != null) {
			varUserName = varDescr.getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());

			if(varUserName ==null) {
				varUserName = varDescr.getM_AIdentificateurComposant().getNom();
			}
		}
		if(varUserName == null)
			varUserName = varFiltreNom;
		if(varUserName.equals(""))
			varUserName = "("+ var.getDescriptor().getM_AIdentificateurComposant().getNom()+")";
		
		return varUserName;
	}

	public void onDataChange() {
		initBaseFilters();
	}
}