package com.faiveley.samng.principal.sm.data.descripteur;

import java.util.ArrayList;
import java.util.HashMap;
import org.eclipse.equinox.log.Logger;
import com.faiveley.samng.principal.logging.SamngLogger;
import com.faiveley.samng.principal.sm.data.identificateurComposant.IdentificateurEvenement;
import com.faiveley.samng.principal.sm.data.identificateurComposant.IdentificateurVariable;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.ATableAssociationComposant;
import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires.GestionnaireDescripteursExplorer;

public class GestionnaireDescripteurs {

	private static HashMap<Integer, DescripteurVariable> variablesDescriptors = new HashMap<Integer, DescripteurVariable>();

	private static HashMap<Integer, DescripteurEvenement> eventsDescriptors = new HashMap<Integer, DescripteurEvenement>();
	
	private static HashMap<String, DescripteurVariable> variablesComposeeDescriptors = new HashMap<String, DescripteurVariable>();
	
	private static GestionnaireDescripteurs instance = new GestionnaireDescripteurs();
	private static HashMap<Integer, DescripteurComposite> mapEvenementVariables= new HashMap<Integer, DescripteurComposite>();
	
	private static int maxUsedVarCode;
	
	//private static int maxUsedEventCode;
	
	protected GestionnaireDescripteurs() {
	
	}
	
	public static GestionnaireDescripteurs getInstance(){
		if (ActivationExplorer.getInstance().isActif()) {
			return GestionnaireDescripteursExplorer.getInstance();
		}
		return instance;
	}
	
	/** Suppression de l'instance */
	public void clear(){
		variablesDescriptors.clear();
		variablesComposeeDescriptors.clear();
		eventsDescriptors.clear();
		mapEvenementVariables.clear();
	}
	
	public static DescripteurVariableAnalogique getDescripteurVariableAnalogique(int code) {
		DescripteurVariableAnalogique descr = null;
		if (code != 0) {
			try {
				descr = (DescripteurVariableAnalogique)variablesDescriptors.get(code);
			} catch (ClassCastException e) {
				SamngLogger.getLogger().info(code + " : " + "dataDescripteur.1" +" : " 
						+variablesDescriptors.get(code).getClass() + "  " 
						+descr.getClass()+ ". " + "dataDescripteur.4");
				//e.printStackTrace();
			}
		} 
		if (descr == null) {
			descr = new DescripteurVariableAnalogique();
			IdentificateurVariable identif = new IdentificateurVariable();
			identif.setCode(code);
			descr.setM_AIdentificateurComposant(identif);
			
			descr.setTypeVariable(TypeVariable.VAR_ANALOGIC);			
			variablesDescriptors.put(code, descr);
			maxUsedVarCode = maxUsedVarCode > code ? maxUsedVarCode : code;
		}
		return descr;
	}
	
	public static DescripteurVariableDiscrete getDescripteurVariableDiscrete(int code) {
		DescripteurVariableDiscrete descr = null;
		if (code != 0) {
			try {
				//descr = getDescriptor((DescripteurVariableDiscrete)descr, code);
				descr = (DescripteurVariableDiscrete)variablesDescriptors.get(code);
			} catch (ClassCastException e) {
				SamngLogger.getLogger().info("dataDescripteur.1" + " " + code + " " + "dataDescripteur.2" + " " + variablesDescriptors.get(code).getClass() + " " + "dataDescripteur.3" + " " + descr.getClass() + "dataDescripteur.4");
				//e.printStackTrace();
			}
		} 
		if (descr == null) {
			descr = new DescripteurVariableDiscrete();
			IdentificateurVariable identif = new IdentificateurVariable();
			identif.setCode(code);
			
			descr.setM_AIdentificateurComposant(identif);
			
			descr.setTypeVariable(TypeVariable.VAR_DISCRETE);
			variablesDescriptors.put(code, descr);
			maxUsedVarCode = maxUsedVarCode > code ? maxUsedVarCode : code;
		}
		return descr;
	}
	
	public static DescripteurVariable getDescripteurVariableComplexe(int code) {
		DescripteurVariable descr = null;
		if (code != 0 ) {
			descr = variablesDescriptors.get(code);
		}
		if (descr == null) {
			descr = new DescripteurVariable();
			IdentificateurVariable identif = new IdentificateurVariable();
			identif.setCode(code);
			descr.setM_AIdentificateurComposant(identif);
			
			descr.setTypeVariable(TypeVariable.VAR_COMPLEXE);
			variablesDescriptors.put(code, descr);
			maxUsedVarCode = maxUsedVarCode > code ? maxUsedVarCode : code;
		}
		return descr;
	}
	
	public static DescripteurVariable getDescripteurVariableComposee(String nom) {
		DescripteurVariable descr = null;
		if (nom != null) {
			descr = variablesComposeeDescriptors.get(nom);
		}
		if (descr == null) {
			descr = new DescripteurVariable();
			IdentificateurVariable identif = new IdentificateurVariable();
			identif.setNom(nom);
			descr.setM_AIdentificateurComposant(identif);
			
			descr.setTypeVariable(TypeVariable.VAR_COMPOSEE);
			variablesComposeeDescriptors.put(nom, descr);
		}
		return descr;
	}
	
	public static DescripteurStructureDynamique getDescripteurStructureDynamique(int code) {
		DescripteurStructureDynamique descr = null;
		if (code != 0) {
			try {
				
				descr = (DescripteurStructureDynamique)variablesDescriptors.get(code);
			} catch (ClassCastException e) {
				SamngLogger.getLogger().info("dataDescripteur.1" + " " + code + " " + "dataDescripteur.2" + " " + variablesDescriptors.get(code).getClass() + " " + "dataDescripteur.3" + " " + descr.getClass() + "dataDescripteur.4");
			
			}
		} 
		if (descr == null) {
			descr = new DescripteurStructureDynamique();
			IdentificateurVariable identif = new IdentificateurVariable();
			identif.setCode(code);
			
			descr.setM_AIdentificateurComposant(identif);
			
			descr.setTypeVariable(TypeVariable.STRUCTURE_DYNAMIQUE);
			variablesDescriptors.put(code, descr);
			maxUsedVarCode = maxUsedVarCode > code ? maxUsedVarCode : code;
		}
		return descr;
	}
	public static DescripteurStructureDynamique getDescripteurPaquets(int code) {
		DescripteurStructureDynamique descr = null;
		if (code != 0) {
			try {
				
				descr = (DescripteurStructureDynamique)variablesDescriptors.get(code);
			} catch (ClassCastException e) {
				SamngLogger.getLogger().info("dataDescripteur.1" + " " + code + " " + "dataDescripteur.2" + " " + variablesDescriptors.get(code).getClass() + " " + "dataDescripteur.3" + " " + descr.getClass() + "dataDescripteur.4");
			
			}
		} 
		if (descr == null) {
			descr = new DescripteurStructureDynamique();
			IdentificateurVariable identif = new IdentificateurVariable();
			identif.setCode(code);
			
			descr.setM_AIdentificateurComposant(identif);
			
			descr.setTypeVariable(TypeVariable.PAQUETS);
			variablesDescriptors.put(code, descr);
			maxUsedVarCode = maxUsedVarCode > code ? maxUsedVarCode : code;
		}
		return descr;
	}
	
	public static DescripteurTableauDynamique getDescripteurTableauDynamique(int code) {
		DescripteurTableauDynamique descr = null;
		if (code != 0) {
			try {
				
				descr = (DescripteurTableauDynamique)variablesDescriptors.get(code);
			} catch (ClassCastException e) {
				SamngLogger.getLogger().info("dataDescripteur.1" + " " + code + " " + "dataDescripteur.2" + " " + variablesDescriptors.get(code).getClass() + " " + "dataDescripteur.3" + " " + descr.getClass() + "dataDescripteur.4");			
			}
		} 
		if (descr == null) {
			descr = new DescripteurTableauDynamique();
			IdentificateurVariable identif = new IdentificateurVariable();
			identif.setCode(code);
			
			descr.setM_AIdentificateurComposant(identif);
			
			descr.setTypeVariable(TypeVariable.TABLEAU_DYNAMIQUE);
			variablesDescriptors.put(code, descr);
			maxUsedVarCode = maxUsedVarCode > code ? maxUsedVarCode : code;
		}
		return descr;
	}
	
	
	public static DescripteurChaineDynamique getDescripteurChaineDynamique(int code) {
		DescripteurChaineDynamique descr = null;
		if (code != 0) {
			try {
				
				descr = (DescripteurChaineDynamique)variablesDescriptors.get(code);
			} catch (ClassCastException e) {
				SamngLogger.getLogger().info("dataDescripteur.1" + " " + code + " " + "dataDescripteur.2" + " " + variablesDescriptors.get(code).getClass() + " " + "dataDescripteur.3" + " " + descr.getClass() + "dataDescripteur.4");
			
			}
		} 
		if (descr == null) {
			descr = new DescripteurChaineDynamique();
			IdentificateurVariable identif = new IdentificateurVariable();
			identif.setCode(code);
			
			descr.setM_AIdentificateurComposant(identif);
			
			descr.setTypeVariable(TypeVariable.CHAINE_DYNAMIQUE);
			variablesDescriptors.put(code, descr);
			maxUsedVarCode = maxUsedVarCode > code ? maxUsedVarCode : code;
		}
		return descr;
	}
	
	
	
	public static DescripteurEvenement getDescripteurEvenement(int code) {
		DescripteurEvenement descr = null;
		if (code != 0) {
			descr = eventsDescriptors.get(code);
		}
		if (descr == null) {
			descr = new DescripteurEvenement();
			IdentificateurEvenement identif = new IdentificateurEvenement();
			identif.setCode(code);
			descr.setM_AIdentificateurComposant(identif);
			eventsDescriptors.put(code, descr);
		}
		return descr;
	}
	
	
	
	/**
	 * Returns the descriptor variable if the descriptor was created, null otherwise
	 * @param code
	 * @return
	 */
	public static DescripteurVariable containsDescriptorVariable(int code) {
		return variablesDescriptors.get(code);
	}
	
	public static DescripteurVariable containsDescriptorVariableComposee(String nom) {
		return variablesComposeeDescriptors.get(nom);
	}
	

	/**
	 * Returns the descriptor variable if the descriptor was created, null otherwise
	 * @param code
	 * @return
	 */
	public static DescripteurEvenement containsDescriptorEvenement(int code) {
		return eventsDescriptors.get(code);
	}
	
	public static void emptyPool() {
		eventsDescriptors.clear();
		variablesDescriptors.clear();
		variablesComposeeDescriptors.clear();
		maxUsedVarCode = 0;
	}
	
	
	public static DescripteurVariable getDescripteurVariable(int c) {
		DescripteurVariable descr = null;
		Integer code = Integer.valueOf(c);
		if (c != 0) {
			try {
				DescripteurVariable descrTmp = variablesDescriptors.get(code);
				if (descrTmp != null && descrTmp.getTypeVariable()!=null) {
					switch (descrTmp.getTypeVariable()) {
						case VAR_ANALOGIC:
							if (descrTmp instanceof DescripteurVariableAnalogique) {
								descr = descrTmp;
							} else {
								descr = new DescripteurVariableAnalogique();
								variablesDescriptors.remove(descrTmp);
								variablesDescriptors.put(code, descr);
							}
							break;
						case VAR_DISCRETE:
							if (descrTmp instanceof DescripteurVariableDiscrete) {
								descr = descrTmp;
							} else {
								descr = new DescripteurVariableDiscrete();
								variablesDescriptors.remove(descrTmp);
								variablesDescriptors.put(code, descr);
							}
							break;
						default:
							descr = descrTmp;
							break;
					}
					descr.setNomUtilisateur(descrTmp.getNomUtilisateur());
					descr.setM_AIdentificateurComposant(descrTmp.getM_AIdentificateurComposant());
					descr.setPoidsPremierBit(descrTmp.getPoidsPremierBit());
					descr.setPoidsPremierOctet(descrTmp.getPoidsPremierOctet());
					descr.setTailleBits(descrTmp.getTailleBits());
					descr.setType(descrTmp.getType());
					descr.setTypeVariable(descrTmp.getTypeVariable());
					maxUsedVarCode = maxUsedVarCode > c ? maxUsedVarCode : c;
					
				}
			} catch (ClassCastException e) {
				SamngLogger.getLogger().info("dataDescripteur.1" + " " + code + " " + "dataDescripteur.2" + " " + variablesDescriptors.get(code).getClass() + " " + "dataDescripteur.3" + " " + descr.getClass() + "dataDescripteur.4");
				e.printStackTrace();
			}
		} 
		if (descr == null) {
			descr = new DescripteurVariable();
			IdentificateurVariable identif = new IdentificateurVariable();
			identif.setCode(c);
			
			descr.setM_AIdentificateurComposant(identif);
			
			//descr.setTypeVariable(TypeVariable.VAR_DISCRETE);
			variablesDescriptors.put(code, descr);
			maxUsedVarCode = maxUsedVarCode > c ? maxUsedVarCode : c;
			
		}
		return descr;
	}
	
	public static void ajouterDescriptorVariable(DescripteurVariable descr) {
		if (descr != null) {
			variablesDescriptors.put(descr.getM_AIdentificateurComposant().getCode(), descr);
		}
	}
 	
	public static DescripteurVariable generateDescriptorVariable(DescripteurVariable d) {
		DescripteurVariable descr = null;
		
		maxUsedVarCode++;
		
		if(d != null && d.typeVariable!=null) {
			switch(d.typeVariable) {
				case VAR_ANALOGIC:
					descr = new DescripteurVariableAnalogique();
					((DescripteurVariableAnalogique) descr).setUnite(
							((DescripteurVariableAnalogique)d).getUnite());
					break;
				case VAR_DISCRETE:
					descr = new DescripteurVariableDiscrete();
					break;
				case VAR_VIRTUAL:
				case VAR_COMPOSEE:
				case VAR_COMPLEXE:
				case UNKNOWN:
					descr = new DescripteurVariable();
					break;					
			}
			if (descr != null) {
				descr.nomUtilisateur = d.nomUtilisateur;
				descr.poidsPremierBit = d.poidsPremierBit;
				descr.poidsPremierOctet = d.poidsPremierOctet;
				descr.tailleBits = d.tailleBits;
				descr.type = d.type;
				descr.typeVariable = d.typeVariable;
				descr.setRenseigne(d.isRenseigne());
				if (d.tableComposant != null) {
					descr.tableComposant = new ArrayList<ATableAssociationComposant>(d.tableComposant); 
				}			
			} else {
				throw new RuntimeException(Messages.getString("dataDescripteur.5"));
			}
		} else {
			d = new DescripteurVariable();
			d.typeVariable = TypeVariable.UNKNOWN;
		}
		if (descr != null) {
			descr.m_AIdentificateurComposant = new IdentificateurVariable();
			descr.m_AIdentificateurComposant.setCode( maxUsedVarCode);
		} else {
//			throw new RuntimeException(Messages.getString("dataDescripteur.6"));
		}
		variablesDescriptors.put(Integer.valueOf(maxUsedVarCode), descr);
		
		return descr;
		
	}
	
	public static int generateCode() {
		return ++maxUsedVarCode;
	}

	public static HashMap<Integer, DescripteurComposite> getMapEvenementVariables() {
		return mapEvenementVariables;
	}

	public static void setMapEvenementVariables(
			HashMap<Integer, DescripteurComposite> mapEvenementVariables) {
		GestionnaireDescripteurs.mapEvenementVariables = mapEvenementVariables;
	}
	
}
