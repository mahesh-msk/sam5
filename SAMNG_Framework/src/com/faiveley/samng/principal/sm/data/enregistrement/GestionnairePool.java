package com.faiveley.samng.principal.sm.data.enregistrement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.actions.profil.GestionnaireProfil;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariableAnalogique;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariableDiscrete;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.descripteur.Type;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.identificateurComposant.AIdentificateurComposant;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.Data;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosParcours;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.Reperes;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TableLangueNomUtilisateur;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TableValeurLabel;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.LabelValeur;
import com.faiveley.samng.principal.sm.data.variableComposant.Langage;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.VariableDynamique;
import com.faiveley.samng.principal.sm.erreurs.ParseurXMLException;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires.GestionnairePoolExplorer;
import com.faiveley.samng.principal.sm.parseurs.TypeParseur;
import com.faiveley.samng.principal.sm.parseurs.parseurXML.ParseurXML1;
import com.faiveley.samng.principal.sm.parseurs.parseurXML.ParseurXMLJRU;
import com.faiveley.samng.principal.sm.parseurs.parseursJRU.ParseurParcoursJRU;

public class GestionnairePool {

	private static GestionnairePool instance = new GestionnairePool();
	
	protected GestionnairePool(){

	}
	
	public static GestionnairePool getInstance(){
		if (ActivationExplorer.getInstance().isActif()) {
			return GestionnairePoolExplorer.getInstance();
		}
		return instance;
	}
	
	private Map<Integer, Evenement> events = new LinkedHashMap<Integer, Evenement>();
	private Map<Integer, AVariableComposant> variables = new LinkedHashMap<Integer, AVariableComposant>();
	private Set<Integer> variablesFailedLoading = new HashSet<Integer>();
	private Map<String, AVariableComposant> variablesNamesMap = new LinkedHashMap<String, AVariableComposant>();
	private Map<String, AVariableComposant> composeeVariablesMap = new LinkedHashMap<String, AVariableComposant>();
	private Map<String, AVariableComposant> complexeVariablesMap = new LinkedHashMap<String, AVariableComposant>();
	private Map<Integer, AVariableComposant> variablesDynamiquesMap = new LinkedHashMap<Integer, AVariableComposant>();
	private Map<String,String> mapNomVariableValeurZeroVariable = new HashMap<String, String>();
	private List<Evenement> listeEvenements=new ArrayList<Evenement>(0);
	private InfosParcours infosParcours;
	private ParseurXML1 xmlParser;
	private Reperes reperes;
	private boolean flagDisplayMsg=false;
	private Map<String, String> valeursVar = new  HashMap<String, String>();
	private Map<Object, Object> valeursVarObject = new  HashMap<Object, Object>();
	private Map<Byte,String> mapByteHexa = new HashMap<Byte, String>();

	private Set<AVariableComposant> variablesRenseignees;
	private Set<AVariableComposant> variablesNonRenseignees;
	
	private HashMap<String, Object> mapTempsCumuleEtDistanceCumule = null;
	//private static GestionnairePool instance;

	/** Suppression de l'instance */
	public void clear(){
		events.clear();
		variables.clear();
		variablesFailedLoading.clear();
		variablesNamesMap.clear();
		composeeVariablesMap.clear();
		complexeVariablesMap.clear();
		variablesDynamiquesMap.clear();
		//TAG BBB 		mapNomVariableValeurZeroVariable.clear();
		listeEvenements.clear();
		if (xmlParser!=null) {
			xmlParser.clear();
		}

		if(reperes!=null)
			reperes.clear();
		if (infosParcours!=null) {
			infosParcours.clear();	
		}
		if (variablesRenseignees!=null){
			variablesRenseignees.clear();
			variablesRenseignees=null;
		}
		
		if (variablesNonRenseignees!=null){
			variablesNonRenseignees.clear();
			variablesNonRenseignees=null;
		}
		
		if(valeursVar!=null)
			valeursVar.clear();
		if(valeursVarObject!=null)
			valeursVarObject.clear();
		//		TAG BBB			if(mapByteHexa!=null)
		//TAG BBB 			mapByteHexa.clear();
		valeursVar = null;
		valeursVarObject =null;

		valeursVar = new  HashMap<String, String>();
		valeursVarObject = new  HashMap<Object, Object>();
		//		TAG BBB		if (mapTempsCumuleEtDistanceCumule!=null)
		//TAG BBB 			mapTempsCumuleEtDistanceCumule.clear();
		//CHECK01
	}
	/**
	 * @return
	 */
	/*public static GestionarePool getInstance() {
		if (instance == null) {
			instance = new GestionarePool();
		}

		return instance;
	}
	 */
	public void chargerFichierXml(String fileName, String RunFileName) throws ParseurXMLException {
		GestionnaireProfil.getInstance().gererOuvertureFichierXML(fileName);
		
		emptyPool();
		GestionnaireDescripteurs.emptyPool();
		
		if(RunFileName!= null && !RunFileName.equals(""))
			xmlParser = ParseurXML1.getInstance(RunFileName);
		else{
			xmlParser = ParseurXML1.getInstance();
		}
		
		xmlParser.parseRessource(fileName, false, 0, -1);

		List<Evenement> eventsList = xmlParser.loadAllEvents();
		listeEvenements = eventsList;
		
		// Load all events
		for (Evenement ev : eventsList) {
			events.put(Integer.valueOf(ev.getM_ADescripteurComposant().getM_AIdentificateurComposant().getCode()),	ev);
		}
		
		//load all variables
		List<AVariableComposant> varsList = xmlParser.loadAllVariables();
		AIdentificateurComposant identif;
		DescripteurVariable descr;
		for (AVariableComposant var : varsList) {
			descr = var.getDescriptor();
			identif = descr.getM_AIdentificateurComposant();

			variables.put(Integer.valueOf(identif.getCode()), var);
			variablesNamesMap.put(identif.getNom(), var);
			if(descr.getTypeVariable() == TypeVariable.VAR_COMPOSEE) {
				composeeVariablesMap.put(identif.getNom(), var);
			}
			if(descr.getTypeVariable() == TypeVariable.VAR_COMPLEXE) {
				complexeVariablesMap.put(identif.getNom(), var);
			}
			if(descr.getTypeVariable()==TypeVariable.STRUCTURE_DYNAMIQUE || descr.getTypeVariable()==TypeVariable.TABLEAU_DYNAMIQUE||descr.getTypeVariable()==TypeVariable.CHAINE_DYNAMIQUE){
				variablesDynamiquesMap.put(identif.getCode(),var);
			}
			//ajout des valeurs correspondantes à la valeur "0" cf point 4 du Lot A septembre 2010
			if(descr.getTypeVariable()==TypeVariable.VAR_DISCRETE){
				TableValeurLabel valeurLabel = ((DescripteurVariableDiscrete)var.getDescriptor()).getLabels();

				if(valeurLabel!=null){
					List<LabelValeur> listeLabelvaleur = valeurLabel.get(Activator.getDefault().getCurrentLanguage());
					if(listeLabelvaleur!=null){
						int size = listeLabelvaleur.size();
						boolean trouve = false;
						int f =0;
						Object valeur=null;
						String label=null;
						while(!trouve && f<size){
							valeur = listeLabelvaleur.get(f).getValeurs();
							label = listeLabelvaleur.get(f).getLabel();
							if(valeur.toString().equals("0")){
								trouve = true;
								label =listeLabelvaleur.get(f).getLabel();
							}
							f++;
						}
						if(trouve)
							mapNomVariableValeurZeroVariable.put(identif.getNom(), label);	
						else mapNomVariableValeurZeroVariable.put(identif.getNom(), "0");
					}
				}
			}
		}
		//load all reperes
		reperes = (Reperes)xmlParser.chargerReperes();
		infosParcours = (InfosParcours)xmlParser.chargerInfosParcours();
		createCorrectionsAndRelativeReperes();
	}

	public Evenement getEvent(int code) {
		Evenement e = null;
		Integer c = Integer.valueOf(code);
		
		if (events.containsKey(c)) {
			e = events.get(c);
		} else {
			e = xmlParser.chargerEvenement(code);
			events.put(c, e);
		}
		
		return e;
	}


	public AVariableComposant getVariable(int code) {
		AVariableComposant v = null;
		Integer c = Integer.valueOf(code);
		if (variables.containsKey(c)) {
			v = variables.get(c);
			if (v != null) {
				v = v.copy();
			}
		} else if (!variablesFailedLoading.contains(c)) {
			v = xmlParser.chargerVariable(code);
			if (v!= null) {
				variables.put(c, v);
				variablesNamesMap.put(v.getDescriptor().getM_AIdentificateurComposant().getNom(), v);
				v = v.copy();
			} else {
				variablesFailedLoading.add(c);
			}
		}
		return v;
	}

	public AVariableComposant getVariableByUseName(String name) {
		Iterator<AVariableComposant> i=variablesRenseignees.iterator();
		while(i.hasNext())
		{
			AVariableComposant var=(AVariableComposant)i.next();
			String nameVar=var.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());
			if (name.equals(nameVar))
				return var;			
		}
		return null;
	}

	public AVariableComposant getVariable(String name) {
		AVariableComposant var = variablesNamesMap.get(name);
		if(var == null)
			var = getSubvariable(name);
		return var;
	}

	public AVariableComposant getSubvariable(String name) {
		if(name == null)
			return null;
		AVariableComposant retVar = null;
		TypeVariable typeVar;
		AVariableComposant subVar;
		for(AVariableComposant var: variables.values()) {
			typeVar = var.getDescriptor().getTypeVariable();
			if(typeVar == TypeVariable.VAR_COMPLEXE || typeVar == TypeVariable.VAR_COMPOSEE) {
				VariableComposite variableComposite = (VariableComposite)var;
				int size = variableComposite.getVariableCount();
				for (int i = 0; i < size; i++) {
					subVar = variableComposite.getEnfant(i);
					if(name.equals(subVar.getDescriptor().getM_AIdentificateurComposant().getNom())
							||name.equals(subVar.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()))) {
						retVar = subVar;
						// FIXME On set le parent ici mais normalement il devrait être déjà présent
						retVar.setParent(variableComposite);
						break;
					}
				}	

			}
		}
		return retVar;
	}

	public Map<Integer, AVariableComposant> getAllVariables() {
		return new LinkedHashMap<Integer, AVariableComposant>(variables);
	}

	public ParseurXML1 getXMLParser() {
		return xmlParser;
	}

	private void createCorrectionsAndRelativeReperes() {
		//temp corrigee
		createRepereBasedOnOtherRepere(
				TypeRepere.temps, TypeRepere.tempsCorrigee);
		//temp relatif
		createRepereBasedOnOtherRepere(
				TypeRepere.temps, TypeRepere.tempsRelatif);
		if((ParseurXML1.getInstance()!=null && !(ParseurXML1.getInstance() instanceof ParseurXMLJRU)) 
				||(TypeParseur.getInstance().getParser()!=null && !(TypeParseur.getInstance().getParser()instanceof ParseurParcoursJRU))){
			//distance corrigee 
			createRepereBasedOnOtherRepere(
					TypeRepere.distance, TypeRepere.distanceCorrigee);
			//distance relatif

			createRepereBasedOnOtherRepere(
					TypeRepere.distance, TypeRepere.distanceRelatif);
		}
		if(TypeRepere.vitesse.getCode()!=-1){
			//vitesse corrigee 
			try {
				createRepereBasedOnOtherRepere(
						TypeRepere.vitesse, TypeRepere.vitesseCorrigee);
			} catch (Exception e) {
				System.out.println("probleme repere vitesse corrigee");
			}
		}
	}

	private String getNomVariable(TypeRepere baseRepere, TypeRepere newRepere){

		if (baseRepere==TypeRepere.temps && newRepere==TypeRepere.tempsCorrigee) 
			return Messages.getString("NomRepere.0");
		else if	(baseRepere==TypeRepere.temps && newRepere==TypeRepere.tempsRelatif) 
			return Messages.getString("NomRepere.1");
		else if (baseRepere==TypeRepere.distance && newRepere==TypeRepere.distanceCorrigee) 
			return Messages.getString("NomRepere.2");
		else if (baseRepere==TypeRepere.distance && newRepere==TypeRepere.distanceRelatif)
			return Messages.getString("NomRepere.3");
		else if (baseRepere==TypeRepere.vitesse && newRepere==TypeRepere.vitesseCorrigee);
		return Messages.getString("NomRepere.4");
	}


	private void createRepereBasedOnOtherRepere(TypeRepere baseRepere, TypeRepere newRepere) {

		//create repere for temp corrigee
		newRepere.setCode(GestionnaireDescripteurs.generateCode());


		if (baseRepere==TypeRepere.vitesse && newRepere==TypeRepere.vitesseCorrigee)
			newRepere.setName("vitesse_corrigee");
		else if (baseRepere==TypeRepere.distance && newRepere==TypeRepere.distanceCorrigee)
			newRepere.setName("distance_corrigee");
		else 
			newRepere.setName(getNomVariable(baseRepere,newRepere));





		//newRepere.setName(baseRepere.getName() + " " + labelNewRepere);

		//get base descriptor
		DescripteurVariable descr = GestionnaireDescripteurs.getDescripteurVariable(baseRepere.getCode());

		//generate a new descriptor 
		DescripteurVariable d = GestionnaireDescripteurs.generateDescriptorVariable(descr);

		//set the values that should modify
		d.getM_AIdentificateurComposant().setNom(newRepere.getName());
		d.getM_AIdentificateurComposant().setCode(newRepere.getCode());

		//set the unite
		if (d instanceof DescripteurVariableAnalogique) {
			String unite = ((DescripteurVariableAnalogique) descr).getUnite();
			((DescripteurVariableAnalogique) d).setUnite(unite);

		}

		//set the nom utilisateur
		TableLangueNomUtilisateur nomUtilisateur= new TableLangueNomUtilisateur();
		TableLangueNomUtilisateur baseNomUtilisateur = descr.getNomUtilisateur();
		for (Langage lang : baseNomUtilisateur.getLanguages()) {
			nomUtilisateur.setNomUtilisateur(lang, getNomVariable(baseRepere,newRepere));	
		}

		d.setNomUtilisateur(nomUtilisateur);

		//create a copy of variable assciated to the base repere and change the descriptor and type
		AVariableComposant newRepereVar = this.getVariable(baseRepere.getCode());
		d.setType(Type.BCD8);

		newRepereVar.setDescripteur(d);
		newRepereVar.setTypeValeur(Type.BCD8);


		System.err.println("Add new repere " + newRepere.getName() + ": " + newRepere.getCode());
		//add new Repere
		variables.put(newRepere.getCode(), newRepereVar);
		variablesNamesMap.put(newRepere.getName(), newRepereVar);
		reperes.ajouterReper(newRepere, newRepereVar);
		GestionnaireDescripteurs.ajouterDescriptorVariable(d);
	}

	public Reperes getReperes() {
		return reperes;
	}

	public Map<String, AVariableComposant> getComposeeVariables() {
		return composeeVariablesMap;
	}
	public Map<String, AVariableComposant> getComplexeVariables() {
		return complexeVariablesMap;
	}	
	public AVariableComposant getVariableDynamique(int code) {
		return variablesDynamiquesMap.get(code);

	}
	public void ajouterEvenement(Evenement ev){
		events.put(ev.getM_ADescripteurComposant().getM_AIdentificateurComposant().getCode(), ev);
	}

	public void emptyPool() {
		events.clear();
		variables.clear();
		variablesFailedLoading.clear();
		variablesNamesMap.clear();
		composeeVariablesMap.clear();
		complexeVariablesMap.clear();
		if(listeEvenements!=null)
			listeEvenements = new ArrayList<Evenement>(0);
		if(reperes != null) {
			reperes.clear();
		}
		if (xmlParser != null) {
			xmlParser.clear();
			xmlParser = null;	
		}
		if(valeursVarObject!=null)
			valeursVarObject.clear();
		if(valeursVar!=null)
			valeursVar.clear();

		valeursVar = new  HashMap<String, String>();
		valeursVarObject = new  HashMap<Object, Object>();
	}

	public InfosParcours getInfosParcours() {
		return infosParcours;
	}

	public List<Evenement> getListeEvenements() {
		return listeEvenements;
	}

	private Set<AVariableComposant> getMessagesVariables() {
		Set<AVariableComposant> vars = new LinkedHashSet<AVariableComposant>();
		Data data = (Data) FabriqueParcours.getInstance().getParcours()
				.getData();
		Enregistrement enrg = data.getEnregistrement();

		int msgLength = enrg.getGoodMessagesCount();
		for (int i = 0; i < msgLength; i++) {
			Message msg = enrg.getEnfant(i);
			vars = addMsgVarToVars(msg.getVariablesAnalogique(), vars);
			vars = addMsgVarToVars(msg.getVariablesDiscrete(), vars);
			vars = addMsgVarToVars(msg.getVariablesComplexe(), vars);
			vars = addMsgVarToVars(msg.getVariablesComposee(), vars);
			vars = addMsgVarToVars(msg.getVariablesVirtuelle(), vars);
			vars = addMsgVarToVars(msg.getStructuresDynamique(), vars);
			vars = addMsgVarToVars(msg.getChainesDynamique(), vars);
			vars = addMsgVarToVars(msg.getTableauxDynamique(), vars);
		}

		return vars;
	}

	/**
	 * Méthode permettatn de tester si le repere vitesse est présent dans le parcours
	 * @return un booléen
	 */
	public boolean isRepereVitesseRenseignee(){
		boolean vitFind=false;
		String vitInst=TypeRepere.vitesse.getName();
		if(vitInst!=null){


			Iterator<?> i=getMessagesVariables().iterator(); // on crée un Iterator pour parcourir notre HashSet
			while(i.hasNext() && !vitFind) // tant qu'on a un suivant
			{
				AVariableComposant var=(AVariableComposant)i.next(); // on affiche le suivant
				String nameVar=var.getDescriptor().getM_AIdentificateurComposant().getNom();
				if(vitInst.equals(nameVar))
					vitFind=true;
			}
		}
		else{
			vitFind = false;
		}
		return vitFind;
	}

	public void displayMsgBoxVitesseNonPresente(){
		if (!flagDisplayMsg) {
			flagDisplayMsg=true;
			MessageBox messageBox = new MessageBox(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_INFORMATION);
			messageBox.setMessage(com.faiveley.samng.principal.sm.data.enregistrement.Messages.getString("GestionnairePool.0")+" "+ Messages.getString("GestionnairePool.1"));
			messageBox.open();
		}
	}

	public Set<AVariableComposant> getVariablesRenseignees() {
		try {
			if (variablesRenseignees==null) 
				variablesRenseignees=getMessagesVariables();

			return variablesRenseignees;
		} catch (Exception e) {
			return null;
		}

	}
	
	
	public Set<AVariableComposant> getVariablesNonRenseignees() {
		try {
			
			if (variablesNonRenseignees==null) {
				
				Collection<AVariableComposant> collectionAllVariables =  getAllVariables().values();
				
				Set<AVariableComposant> setVariableRenseignees = getVariablesRenseignees();
				
				//récupération des codes des variables renseignées
				List<Integer> listeCodeVarRenseigne = new ArrayList<Integer>();
				for (AVariableComposant aVariableComposant : setVariableRenseignees) {
					Integer code;
					if(aVariableComposant instanceof VariableDynamique){
						code = findCode(aVariableComposant.getDescriptor(), collectionAllVariables, aVariableComposant);
					}else{
						code = aVariableComposant.getDescriptor().getM_AIdentificateurComposant().getCode();
					}
					listeCodeVarRenseigne.add(code);
					if(aVariableComposant instanceof VariableComposite) {
						addSousVariableToListeCodeVarRenseigne(
								collectionAllVariables, listeCodeVarRenseigne,
								aVariableComposant);
					}
				}
				
				//récupération des code de toutes les variables du xml
				List<Integer> listeCodeVarTotal = new ArrayList<Integer>();
				for (AVariableComposant aVariableComposant : collectionAllVariables) {
					listeCodeVarTotal.add(aVariableComposant.getDescriptor().getM_AIdentificateurComposant().getCode());
				}
				
				//suppression des codes var des variables renseignées pour obenir les code de variables non renseignees
				listeCodeVarTotal.removeAll(listeCodeVarRenseigne);
				
				variablesNonRenseignees = new HashSet<AVariableComposant>();
				for (Integer codeVar : listeCodeVarTotal) {
					AVariableComposant var = getAllVariables().get(codeVar);
					variablesNonRenseignees.add(var);
				}
				//System.out.println( variablesNonRenseignees);
			}
			
			return variablesNonRenseignees;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * @param collectionAllVariables
	 * @param listeCodeVarRenseigne
	 * @param aVariableComposant
	 */
	public void addSousVariableToListeCodeVarRenseigne(
			Collection<AVariableComposant> collectionAllVariables,
			List<Integer> listeCodeVarRenseigne,
			AVariableComposant aVariableComposant) {
		AVariableComposant[] sousVars = ((VariableComposite) aVariableComposant).getEnfants();
		if (sousVars != null) {
			for (AVariableComposant sousVar : sousVars) {
				Integer sousVarCode = findCode(sousVar.getDescriptor(),
						collectionAllVariables,sousVar);
				if (sousVarCode != null) {
					listeCodeVarRenseigne.add(sousVarCode);
				}
				// recursivité
				if (sousVar instanceof VariableComposite) {
					addSousVariableToListeCodeVarRenseigne(collectionAllVariables,
							listeCodeVarRenseigne, sousVar);
				}
			}
		}
	}

	private Integer findCode(DescripteurVariable descriptor,
			Collection<AVariableComposant> collectionAllVariables,
			AVariableComposant sousVar) {

		if (descriptor == null
				|| descriptor.getM_AIdentificateurComposant() == null
				|| descriptor.getM_AIdentificateurComposant().getNom() == null) {
			if (sousVar instanceof VariableDynamique) {
				AVariableComposant var = ((VariableDynamique) sousVar)
						.getVariableEntete();
				return findCode(var.getDescriptor(), collectionAllVariables,
						var);
			} else {
				return null;
			}
		} else if (sousVar instanceof VariableDynamique) {
			// une variable StructureDynamique a les infos dans les variables d'entete!
			AVariableComposant var = ((VariableDynamique) sousVar)
					.getVariableEntete();
			return findCode(var.getDescriptor(), collectionAllVariables, var);
		} else {
			String nom = descriptor.getM_AIdentificateurComposant().getNom();

			for (AVariableComposant var : collectionAllVariables) {
				if (nom.endsWith(var.getDescriptor()
						.getM_AIdentificateurComposant().getNom())) {
					return Integer.valueOf(var.getDescriptor()
							.getM_AIdentificateurComposant().getCode());
				}
			}
			return null;
		}
	}

	private Set<AVariableComposant> addMsgVarToVars(List<? extends AVariableComposant> msgVars,Set<AVariableComposant> vars){
		if (msgVars != null){
			int nbVars=msgVars.size();

			Set<AVariableComposant> updatedVars = vars;

			for (int j = 0; j < nbVars; j++) {
				AVariableComposant currentMsgVar = msgVars.get(j);
				String name = currentMsgVar.getDescriptor().getM_AIdentificateurComposant().getNom();
				if (name != null) {
					boolean find = isVarInSet(name, vars);
					if (!find) {
						updatedVars.add(currentMsgVar);
					}
					if (currentMsgVar instanceof VariableComplexe) {
						VariableComplexe varComplexe = (VariableComplexe) currentMsgVar;
						AVariableComposant[] enfants = varComplexe.getEnfants();
						if (enfants != null && enfants.length > 0) {
							updatedVars = addMsgVarToVars(Arrays.asList(enfants), updatedVars);
						}
					} else if (currentMsgVar instanceof VariableDynamique) {
						VariableDynamique varDynamique = (VariableDynamique) currentMsgVar;
						AVariableComposant varEntete = varDynamique.getVariableEntete();
						updatedVars = addMsgVarToVars(Arrays.asList(new AVariableComposant[] { varEntete }), updatedVars);
						AVariableComposant[] enfants = varDynamique.getEnfants();
						if (enfants != null && enfants.length > 0) {
							updatedVars = addMsgVarToVars(Arrays.asList(enfants), updatedVars);
						}
					}
				}
			}
			
			return updatedVars;

		} else {
			return vars;
		}
	}

	private boolean isVarInSet(String name, Set<AVariableComposant> vars) {
		boolean find = false;
		Iterator<AVariableComposant> i = vars.iterator();
		while (i.hasNext()) {
			AVariableComposant var = (AVariableComposant) i.next();
			String nameVar = var.getDescriptor().getM_AIdentificateurComposant().getNom();
			if (name.equals(nameVar)) {
				find = true;
			}
		}
		return find;
	}

	public Map<String, String> getValeursVar() {
		return valeursVar;
	}


	public void setValeursVar(Map<String, String> valeursVar) {
		this.valeursVar = valeursVar;
	}


	public Map<Object, Object> getValeursVarObject() {
		return valeursVarObject;
	}


	public void setValeursVarObject(Map<Object, Object> valeursVarObject) {
		this.valeursVarObject = valeursVarObject;
	}


	public Map<Byte, String> getMapByteHexa() {
		return mapByteHexa;
	}


	public void setMapByteHexa(Map<Byte, String> mapByteHexa) {
		this.mapByteHexa = mapByteHexa;
	}


	public Map<String, String> getMapNomVariableValeurzeroVariable() {
		return mapNomVariableValeurZeroVariable;
	}


	public void setMapNomVariableValeurzeroVariable(
			Map<String, String> nomVariableValeurzeroVariable) {
		this.mapNomVariableValeurZeroVariable = nomVariableValeurzeroVariable;
	}


	public HashMap<String, Object> getMapTempsCumuleEtDistanceCumule() {
		return mapTempsCumuleEtDistanceCumule;
	}


	public void setMapTempsCumuleEtDistanceCumule(
			HashMap<String, Object> mapTempsCumuleEtDistanceCumule) {
		this.mapTempsCumuleEtDistanceCumule = mapTempsCumuleEtDistanceCumule;
	}

	public Map<Integer, AVariableComposant> getVariablesDynamiquesMap() {
		return variablesDynamiquesMap;
	}

	//	vars.addAll(msgVars.values());			

	//	vars=supprimerDoublons(vars);

	//	Map map = new HashMap();
	//	map.putAll(tab);
	//	map = new TreeMap(map);


	//	for (Iterator <AVariableComposant> i = msgVars.keySet().iterator() ; i.hasNext() ; ){
	//	if (!vars.contains(i.next())) {
	//	vars.add(i.next());
	//	}	
	//	}



	//	for (Iterator <AVariableComposant> i = msgVars.values().iterator() ; i.hasNext();){
	//	if (!vars.contains(i.next())) {
	//	vars.add(i.next());
	//	}	
	//	}

	//	for (int j = 0; j < nbVal; j++) {
	//	if (!vars.contains(msgVars.values())) {
	//	vars.addall(msgVars.values());
	//	}					
	//	}				
}
