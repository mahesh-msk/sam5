package com.faiveley.samng.principal.sm.data.enregistrement;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurEvenement;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariableDiscrete;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.ATableAssociationComposant;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.Data;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TableValeurLabel;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.LabelValeur;
import com.faiveley.samng.principal.sm.data.variableComposant.Langage;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.data.UtilExplorer;
import com.faiveley.samng.principal.sm.parseurs.parseurXML.ParseurXML1;

public class Util {

	private static Util instance = new Util();
	
	protected Util() {
	}
	
	public static Util getInstance(){
		if (ActivationExplorer.getInstance().isActif()) {
			return UtilExplorer.getInstance();
		}
		return instance;
	}

	private Set<Evenement> events = new LinkedHashSet<Evenement>();
	private List<AVariableComposant> allVars = new ArrayList<AVariableComposant>(0);
	private List<AVariableComposant> allVarsIncludeSubvars = new ArrayList<AVariableComposant>(0);
	private List<Evenement> allEvents = new ArrayList<Evenement>(0);

	public void clear() {
		if (events!=null) {
			events.clear();
		}
		if (allVars!=null) {
			allVars.clear();
		}
		if (allVarsIncludeSubvars!=null) {
			allVarsIncludeSubvars.clear();
		}
		if (allEvents!=null) {
			allEvents.clear();
		}		
	}
	
	public String getNomUtilisateurSiChaineVide(AVariableComposant var){
		String nom=var.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());
		if (nom!=null && nom.equals("")) {
			nom="("+ var.getDescriptor().getM_AIdentificateurComposant().getNom() +")";
		}
		return nom;
	}
	
	public Set<Evenement> getMessagesEvents() {
		if (events.size() == 0) {
			Data data = null;
			try {
				data = ((Data) FabriqueParcours.getInstance().getParcours()
						.getData());
			} catch (Exception e) {

			}
			if (data != null) {
				Enregistrement enrg = data.getEnregistrement();
				int msgLength = enrg.getGoodMessagesCount();
				for (int i = 0; i < msgLength; i++) {
					Message msg = enrg.getEnfant(i);
					events.add(msg.getEvenement());
				}
			}
		}
		return events;
	}

	/**
	 * retourne si l'evenement est renseigne dans le parcours
	 */
	public boolean isEventDansParcours(String evNom,boolean nomUtilisateur) {
		Set<Evenement> events = getMessagesEvents();
		for (Evenement ev : events) {
			if (ev == null) {
				continue;
			}
			try {
				String nom = nomUtilisateur ? getNomUtilisateurEvent(ev)
						: getNomUniqueEvent(ev);
				if (nom != null && nom.equals(evNom)) {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private String getNomUniqueEvent(Evenement ev) {
		return ev.getM_ADescripteurComposant().getM_AIdentificateurComposant()
				.getNom();
	}

	private String getNomUtilisateurEvent(Evenement ev) {
		return ev.getNomUtilisateur().getNomUtilisateur(
				Activator.getDefault().getCurrentLanguage());
	}

	/**
	 * Test si une varaible est dans le fichier xml
	 * 
	 * @param varNom
	 * @param nomUtilisateur
	 * @return
	 */
	public boolean isVariableInXml(String varNom, boolean nomUtilisateur) {
		boolean isVariableDansXml = false;

		List<AVariableComposant> vars = Util.getInstance().getAllVariablesIncludeSubvars();

		for (AVariableComposant var : vars) {
			try {
				String nom = var
						.getDescriptor()
						.getNomUtilisateur()
						.getNomUtilisateur(
								Activator.getDefault().getCurrentLanguage());
				if(nom == null){
					nom = var
							.getDescriptor().getM_AIdentificateurComposant().getNom();
				}
				if (nom.equals(varNom)){
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
		if (isVariableDansXml == false) {
			vars = Util.getInstance().getAllVariables();
			for (AVariableComposant var : vars) {
				try {
					String nom = var
							.getDescriptor()
							.getNomUtilisateur()
							.getNomUtilisateur(
									Activator.getDefault().getCurrentLanguage());
					if(nom == null){
						nom = var
								.getDescriptor().getM_AIdentificateurComposant().getNom();
					}
					if (nom.equals(varNom)){
						return true;
					}
				} catch (Exception e) {
					e.printStackTrace();

				}
			}
		}

		return isVariableDansXml;
	}
	
	/**
	 * Test si une variable est dans le fichier xml par son nom unique
	 * 
	 * @param varNom
	 * @param nomUtilisateur
	 * @return
	 */
	public boolean isVariableComplexeInXmlByNomUnique(String varNom) {
		boolean isVariableDansXml = false;

		List<AVariableComposant> vars = Util.getInstance().getAllVariablesIncludeSubvars();

		
			vars = Util.getInstance().getAllVariables();
			for (AVariableComposant var : vars) {
				try {
					String nom =var
							.getDescriptor()
							.getM_AIdentificateurComposant().getNom();
					
					if (nom.equals(varNom)){
						return true;
					}else if(("("+nom+")").equals(varNom)){
						return true;
					}
				} catch (Exception e) {
					e.printStackTrace();

				}
			}
		return isVariableDansXml;
	}

	public boolean isEventInXml(String evNom, boolean nomUtilisateur) {
		boolean isEventDansParcours = false;
		List<Evenement> events = getAllEvents();
		for (Evenement ev : events) {
			try {
				String nom = nomUtilisateur ? getNomUtilisateurEvent(ev)
						: getNomUniqueEvent(ev);
				if (nom != null && nom.equals(evNom)) {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return isEventDansParcours;
	}

	/**
	 * Fonction utilisée les  variables avec un nom uilisateur eégal à "" est dans le xml
	 * @param varNom
	 * @return
	 */
	public boolean isVariableComplexeDansParcoursByNomUnique(String varNom) {
		boolean isVariableDansParcours = true;

		Set<AVariableComposant> varsNonRenseignees = GestionnairePool.getInstance().getVariablesNonRenseignees();

		for (AVariableComposant varNonRenseignee : varsNonRenseignees) {
			try {
				String nom = varNonRenseignee.getDescriptor().getM_AIdentificateurComposant().getNom();
				if (nom.equals(varNom)) {
					if(varNonRenseignee.getDescriptor().isVolatil()){
						if (!(varNonRenseignee instanceof VariableAnalogique)){
							return false;							
						}
					}else{
						return false;
					}
				} else if (("(" + nom + ")").equals(varNom)) {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
		return isVariableDansParcours;
	}
	
	
	/**
	 * retourne si la variable est renseignee dans le parcours si nomUtilisateur
	 * à true : test sur le nom utilisateur si nomUtilisateur à false : test sur
	 * le nom unique
	 */
	public boolean isVariableDansParcours(String varNom, boolean nomUtilisateur) {

		Set<AVariableComposant> varsNonRenseignees = GestionnairePool.getInstance().getVariablesNonRenseignees();

		for (AVariableComposant varNonRenseignee : varsNonRenseignees) {
			try {
				String nom = nomUtilisateur ? getNomUtilisateurVariable(varNonRenseignee) : getNomUniqueVariable(varNonRenseignee);

				if (nom != null && nom.equals(varNom)) {
					if(varNonRenseignee.getDescriptor().isVolatil()){
						if (!(varNonRenseignee instanceof VariableAnalogique)){
							return false;							
						}
					}else{
						return false;
					}
				}
				if (varNonRenseignee instanceof VariableComplexe) {
					String nomUniqueVarComplexe = varNonRenseignee.getDescriptor().getM_AIdentificateurComposant().getNom();
					VariableComplexe varc = (VariableComplexe) varNonRenseignee;
					for (int i = 0; i < varc.getVariableCount(); i++) {
						nom = nomUtilisateur ? getNomUtilisateurVariable(varc.getEnfant(i)) : getNomUniqueVariable(varc.getEnfant(i));
						if (nom != null && nom.equals(varNom)) {
							return false;
						} else if (("(" + nomUniqueVarComplexe + ") " + nom).equals(varNom)) {
							return false;
						} else if (nom.equals("(" + nomUniqueVarComplexe + ") " + varNom)) {
							return false;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
		// if(isVariableDansParcours){
		// Set<AVariableComposant> varsRenseignees =
		// GestionnairePool.getVariablesRenseignees();
		// for (AVariableComposant var : varsRenseignees) {
		// try {
		// String nom=nomUtilisateur ? getNomUtilisateurVariable(var) :
		// getNomUniqueVariable(var);
		// if (nom.equals(varNom)) {
		// return true;
		// }
		// if (var instanceof VariableComplexe) {
		// VariableComplexe varc=(VariableComplexe) var;
		// for (int i = 0; i < varc.getVariableCount(); i++) {
		// nom=nomUtilisateur ? getNomUtilisateurVariable(varc.getEnfant(i)) :
		// getNomUniqueVariable(varc.getEnfant(i));
		// if (nom.equals(varNom)) {
		// return true;
		// }
		// }
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// }

		return true;
	}

	private String getNomUniqueVariable(AVariableComposant var) {
		return var.getDescriptor().getM_AIdentificateurComposant().getNom();
	}

	private String getNomUtilisateurVariable(AVariableComposant var) {
		return var.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());
	}

	public boolean isVariableDansParcours(AVariableComposant v) {
		boolean isVariableDansParcours = false;
		Set<AVariableComposant> vars = GestionnairePool.getInstance()
				.getVariablesRenseignees();
		for (AVariableComposant var : vars) {
			try {
				if (var.getDescriptor()
						.getM_AIdentificateurComposant()
						.getNom()
						.equals(v.getDescriptor()
								.getM_AIdentificateurComposant().getNom())) {
					return true;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return isVariableDansParcours;
	}

	public Set<AVariableComposant> getMessagesVariables() {
		Set<AVariableComposant> vars = new LinkedHashSet<AVariableComposant>();
		Data data = null;
		try {
			data = ((Data) FabriqueParcours.getInstance().getParcours()
					.getData());
		} catch (Exception e) {

		}
		
		Enregistrement enrg = data.getEnregistrement();
		int msgLength = enrg.getGoodMessagesCount();
		for (int i = 0; i < msgLength; i++) {
			Message msg = enrg.getEnfant(i);
			if (msg.getVariablesAnalogique() != null)
				vars.addAll(msg.getVariablesAnalogique());
			if (msg.getVariablesDiscrete() != null)
				vars.addAll(msg.getVariablesDiscrete());
			if (msg.getVariablesComplexe() != null)
				vars.addAll(msg.getVariablesComplexe());
			if (msg.getVariablesComposee() != null)
				vars.addAll(msg.getVariablesComposee());
			if (msg.getVariablesVirtuelle() != null)
				vars.addAll(msg.getVariablesVirtuelle());
		}
		
		return vars;
	}

	public List<Evenement> getAllEvents() {
		
		if (allEvents.size() == 0) {
			if (ParseurXML1.getInstance() != null) {
				List<Evenement> events = ParseurXML1.getInstance().loadAllEvents();
				allEvents = events;
				return allEvents;
			} else {
				return null;
			}
		}
		return allEvents;
	}

	/**
	 * Returns all variables from an XML file.
	 * 
	 * @param includeSubvars
	 * @return
	 */
	public List<AVariableComposant> getAllVariables() {
		if (allVars.size() == 0) {
			if (ParseurXML1.getInstance() != null) {
				List<AVariableComposant> allVars2 = ParseurXML1.getInstance()
						.loadAllVariables();
				allVars = allVars2;
				return allVars;
			} else {
				return null;
			}
		}
		return allVars;
	}

	/**
	 * Returns all variables from an XML file, the complex variables are ignored
	 * but their subvariables will be added
	 * 
	 * @param includeSubvars
	 * @return
	 */
	public List<AVariableComposant> getAllVariablesIncludeSubvars() {
		if (allVarsIncludeSubvars.size() == 0) {
			if (ParseurXML1.getInstance() != null) {
				List<AVariableComposant> allVars = ParseurXML1.getInstance()
						.loadAllVariables();
				ArrayList<AVariableComposant> retList = new ArrayList<AVariableComposant>();
				for (AVariableComposant var : allVars) {
					addVariable(var, retList);
				}
				retList.trimToSize();
				allVarsIncludeSubvars = retList;
				return retList;
			} else {
				return null;
			}
		
		}
		return allVarsIncludeSubvars;
	}

	/**
	 * Méthode qui retourne le nom utilisateur d'une variable (y compris
	 * sous-variable), à partir du nom unique
	 * 
	 * @param nom
	 * @return
	 */
	public String getUserNameForVarFilter(String nom) {
		List<AVariableComposant> allVariables = getAllVariablesIncludeSubvars();
		String nomVar = null;
		for (AVariableComposant aVariableComposant : allVariables) {
			if (aVariableComposant.getDescriptor()
					.getM_AIdentificateurComposant().getNom().equals(nom)) {
				nomVar = aVariableComposant
						.getDescriptor()
						.getNomUtilisateur()
						.getNomUtilisateur(
								Activator.getDefault().getCurrentLanguage());
				break;
			}
		}
		return nomVar;
	}
	
	public AVariableComposant getVariableFromNom(String nom) {
		List<AVariableComposant> allVariables = getAllVariablesIncludeSubvars();
		for (AVariableComposant aVariableComposant : allVariables) {
			if (aVariableComposant.getDescriptor()
					.getM_AIdentificateurComposant().getNom().equals(nom)) {
				return aVariableComposant;
			}
		}
		return null;
	}
	
	public String getNomCourtFromNomUtilisateur(String nomUtilisateur) {
		int lastPointIdx = nomUtilisateur.lastIndexOf('.');
		return computeNomCourtFromNomUtilisateur(nomUtilisateur, lastPointIdx);
	}
	
	private String computeNomCourtFromNomUtilisateur(String nomUtilisateur, int pointIdx) {
		String substring = nomUtilisateur.substring(pointIdx + 1);
		Pattern p = Pattern.compile("^[0-9].*");
		Matcher m = p.matcher(substring);
		boolean matches = m.matches();
		if (pointIdx != -1 && (substring.startsWith(" ") || substring.startsWith("(") || matches)) {
			String tmp = nomUtilisateur.substring(0, pointIdx);
			int newPointIdx = tmp.lastIndexOf('.');
			return computeNomCourtFromNomUtilisateur(nomUtilisateur, newPointIdx);
		} else {
			return substring;
		}
	}

	/**
	 * Adds a variable in the target list. If the variable is a composite
	 * variable, only its subvariables will be added
	 * 
	 * @param var
	 * @param targetList
	 */
	private void addVariable(AVariableComposant var,
			List<AVariableComposant> targetList) {
		TypeVariable typeVar = var.getDescriptor().getTypeVariable();
		if (typeVar == TypeVariable.VAR_COMPLEXE
				|| typeVar == TypeVariable.VAR_COMPOSEE) {
			addVarCompose((VariableComposite) var, targetList);
		} else {
			targetList.add(var);
		}
	}

	/**
	 * Adds all subvariables of a variable composite in a target collection
	 * 
	 * @param var
	 *            the composite variable
	 * @param targetList
	 */
	private void addVarCompose(VariableComposite var,
			List<AVariableComposant> targetList) {
		int size = var.getVariableCount();
		for (int i = 0; i < size; i++) {
			AVariableComposant subVar = var.getEnfant(i);
			addVariable(subVar, targetList);
		}
	}

	public String[] chargerNomsEvenements() {
		List<Evenement> events = getAllEvents();
		if (events != null) {
			String[] eventNames = new String[events.size()];
			int i = 0;
			for (Evenement ev : events) {
				eventNames[i] = ((DescripteurEvenement) ev
						.getM_ADescripteurComposant()).getNom();
				i++;
			}
			return eventNames;
		}
		return new String[0]; // return an empty array to avoid further checks
	}

	public String[] chargerNomsVariables() {
		List<AVariableComposant> variables = getAllVariables();
		if (variables != null) {
			String[] varNames = new String[variables.size()];
			int i = 0;
			for (AVariableComposant var : variables) {
				varNames[i] = var.getDescriptor()
						.getM_AIdentificateurComposant().getNom();
				i++;
			}
			return varNames;
		}
		return new String[0]; // return an empty array to avoid further checks
	}

	/**
	 * Returns the label for a discrete variable
	 * 
	 * @param var
	 * @return
	 */
	public String getLabelForDiscreteVariable(AVariableComposant var) {
		List<LabelValeur> labelValues = getLabelsForVariable(var
				.getDescriptor());
		String cmpVal;
		String label;
		if (labelValues != null) {
			for (LabelValeur valLabel : labelValues) {
				cmpVal = (String) valLabel.getValeurs();
				label = valLabel.getLabel();
				if ("".equals(cmpVal) || "".equals(label)) {
					System.err
							.println("Ignoring empty label or value found for variable discrete "
									+ var.getDescriptor()
											.getM_AIdentificateurComposant()
											.getNom());
					continue;
				}
				if (var.compareValueWithStringValue(cmpVal) == 0) {
					String retour = valLabel.getLabel();
					if (retour.equals("FALSE"))
						retour = new String(retour).toLowerCase();
					return retour;
				}
			}
		}
		return var.toString();
	}
	
	/**
	 * Returns the labels for the current language associated to a variable
	 * descriptor
	 * 
	 * @param descr
	 * @return
	 */
	public List<LabelValeur> getLabelsForVariable(
			DescripteurVariable descr) {
		int count = descr.getTableComposantCount();
		ATableAssociationComposant tblAssoc;
		Langage curLang = Activator.getDefault().getCurrentLanguage();
		List<LabelValeur> labelValues = null;
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				tblAssoc = descr.getTableComposant(i);
				// get the next table of associations
				if (tblAssoc instanceof TableValeurLabel) {
					labelValues = ((TableValeurLabel) tblAssoc).get(curLang);
					if (labelValues != null) {
						break;
					}
				}
			}
		} else {
			AVariableComposant var = GestionnairePool.getInstance().getVariable(descr
					.getM_AIdentificateurComposant().getCode());
			if (var != null) {
				if (var.getDescriptor().getTypeVariable() == TypeVariable.VAR_DISCRETE) {
					TableValeurLabel valeurLabel = ((DescripteurVariableDiscrete) descr)
							.getLabels();
					if (valeurLabel != null) {
						labelValues = valeurLabel.get(curLang);

					}
				}
			}
		}
		return labelValues;
	}

	/**
	 * Get the label for a variable composee for a given message. If all
	 * variables that are in that variable are present in the file then the
	 * values are taken and see what label is matching them
	 * 
	 * @param var
	 * @param message
	 * @return
	 */
	public static LabelValeur getLabelForVariableComposee(
			VariableComposite var, Message message) {

		StringBuilder str = new StringBuilder();
		AVariableComposant subvar;
		AVariableComposant msgSubVar;
		int count = var.getVariableCount();
		for (int i = 0; i < count; i++) {
			subvar = var.getEnfant(i);
			msgSubVar = message.getVariable(subvar.getDescriptor());
			if (msgSubVar == null) {
				List<Message> messages = ActivatorData.getInstance().getVueData()
						.getDataTable().getEnregistrement().getMessages();
				for (int j = messages.size() - 1; j > 0; j--) {
					if (messages.get(j).getMessageId() >= message
							.getMessageId()) {
						continue;
					}

					if (messages.get(j).getVariable(subvar.getDescriptor()) != null) {
						msgSubVar = messages.get(j).getVariable(
								subvar.getDescriptor());
						break;
					}
				}
			}
			if (msgSubVar == null)
				return null;
			str.append(msgSubVar.getCastedValeur());
			if (i != count - 1)
				str.append(","); //$NON-NLS-1$
		}
		String subVarsValue = str.toString();

		List<LabelValeur> labelValues = Util.getInstance().getLabelsForVariable(var.getDescriptor());
		String cmpVal;
		if (labelValues != null) {
			for (LabelValeur valLabel : labelValues) {
				cmpVal = (String) valLabel.getValeurs();
				if (cmpVal.equals(subVarsValue))
					return valLabel;
			}
		}
		return null;
	}

	public Set<Evenement> getEvents() {
		return events;
	}

	public void setEvents(Set<Evenement> events) {
		this.events = events;
	}

	public List<AVariableComposant> getAllVars() {
		return allVars;
	}

	public void setAllVars(List<AVariableComposant> allVars) {
		this.allVars = allVars;
	}

	public List<AVariableComposant> getAllVarsIncludeSubvars() {
		return allVarsIncludeSubvars;
	}

	public void setAllVarsIncludeSubvars(
			List<AVariableComposant> allVarsIncludeSubvars) {
		this.allVarsIncludeSubvars = allVarsIncludeSubvars;
	}

	public void setAllEvents(List<Evenement> allEvents) {
		this.allEvents = allEvents;
	}

}
