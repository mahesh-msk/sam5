package com.faiveley.samng.principal.sm.parseurs.parseurXML;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import noNamespace.ChaineDynamiqueDocument.ChaineDynamique;
import noNamespace.EnregistreurDefDocument;
import noNamespace.EnregistreurDocument.Enregistreur;
import noNamespace.EnregistreurDocument.Enregistreur.ResolutionTemps;
import noNamespace.EvenementDocument.Evenement;
import noNamespace.EvenementDocument.Evenement.RazCompteurDistance;
import noNamespace.EvenementDocument.Evenement.RazCompteurTemps;
import noNamespace.EvenementDocument.Evenement.SynchroTempsDistance;
import noNamespace.InformationDocument.Information;
import noNamespace.InfosParcoursDocument;
import noNamespace.ListeSousvarDocument.ListeSousvar;
import noNamespace.ListeTablelabelsDocument.ListeTablelabels;
import noNamespace.ListeVariablesDocument.ListeVariables;
import noNamespace.NomUtilisateurDocument.NomUtilisateur;
import noNamespace.ReperesDocument.Reperes;
import noNamespace.SousVarDocument.SousVar;
import noNamespace.SousVariableDocument.SousVariable;
import noNamespace.SousVariableType2Document.SousVariableType2;
import noNamespace.StructureDynamiqueDocument.StructureDynamique;
import noNamespace.TableLabelsDocument.TableLabels;
import noNamespace.TableSousVariablesDocument.TableSousVariables;
import noNamespace.TableauDynamiqueDocument.TableauDynamique;
import noNamespace.VariableAnalogiqueDocument.VariableAnalogique;
import noNamespace.VariableComplexeDocument.VariableComplexe;
import noNamespace.VariableComposeeDocument.VariableComposee;
import noNamespace.VariableDiscreteDocument.VariableDiscrete;
import noNamespace.impl.EvenementDocumentImpl.EvenementImpl.ChangementHeureImpl;
import noNamespace.impl.EvenementDocumentImpl.EvenementImpl.RuptureAcquisitionImpl;
import noNamespace.impl.VariableDiscreteDocumentImpl.VariableDiscreteImpl.TypeImpl;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;

import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.logging.SamngLogger;
import com.faiveley.samng.principal.sm.controles.util.XMLName;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurChaineDynamique;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurEvenement;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurStructureDynamique;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurTableauDynamique;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariableAnalogique;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariableDiscrete;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.descripteur.OffsetComposant;
import com.faiveley.samng.principal.sm.data.descripteur.Poids;
import com.faiveley.samng.principal.sm.data.descripteur.Temporelle;
import com.faiveley.samng.principal.sm.data.descripteur.Type;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.identificateurComposant.IdentificateurEvenement;
import com.faiveley.samng.principal.sm.data.identificateurComposant.IdentificateurVariable;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.AParcoursComposant;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosFichierSamNg;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosParcours;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TableLangueNomUtilisateur;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TableValeurLabel;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TempResolutionEnum;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.LabelValeur;
import com.faiveley.samng.principal.sm.data.variableComposant.Langage;
import com.faiveley.samng.principal.sm.data.variableComposant.TableSousVariable;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableRepere;
import com.faiveley.samng.principal.sm.erreurs.AExceptionSamNG;
import com.faiveley.samng.principal.sm.erreurs.BadVariableCodeException;
import com.faiveley.samng.principal.sm.erreurs.DuplicateEventCodeException;
import com.faiveley.samng.principal.sm.erreurs.DuplicateVariableCodeException;
import com.faiveley.samng.principal.sm.erreurs.ParseurXMLException;
import com.faiveley.samng.principal.sm.formats.FormatSAM;
import com.faiveley.samng.principal.sm.parseurs.BridageFormats;
import com.faiveley.samng.principal.sm.parseurs.IParseurInterface;
import com.faiveley.samng.principal.sm.parseurs.Messages;

public class ParseurXML1 implements IParseurInterface {
	private static final Logger LOGGER = SamngLogger.getLogger();

	private static ParseurXML1 instance;

	private Enregistreur enr = null;

	private HashMap<String, Integer> varsNameId = null;

	private InfosFichierSamNg infoData;

	private InfosParcoursDocument.InfosParcours infoParcours;

	private String xmlFileName;

	/**
	 * Parses the xml file
	 * 
	 * @param fileName, the name of the xml file to loadg
	 */
	public void parseRessource(String fileName, boolean explorer, int deb, int fin) throws ParseurXMLException {
		// CIU - before parsing a new file the members must be cleared otherwise if an exception is thrown in the parser, we migth use later invalid informations
		// (Pay attention that we are using a singleton)
		// The most relevant case is when we have a valid binary file loaded with a valid XML and after that we load another binary file but the XML for it does not exists
		this.enr = null;
		this.infoData = null;
		this.varsNameId = null;
		
		noNamespace.EnregistreurDefDocument enrDoc = null;

		try {
			enrDoc = noNamespace.EnregistreurDefDocument.Factory.parse(new File(fileName));
		} catch (XmlException e1) {
			e1.printStackTrace();
			throw new ParseurXMLException(Messages.getString("errors.blocking.errorLoadingXMLFile"), true);
		} catch (IOException e1) {
			throw new ParseurXMLException(Messages.getString("errors.blocking.errorLoadingXMLFile"), true);
		}

		this.enr = enrDoc.getEnregistreurDef().getEnregistreur();
		
		// Gets the info about the file
		this.infoData = new InfosFichierSamNg();
		this.infoData.setNomFichierXml(XMLName.updateCurrentXmlName());
		this.infoData.setNumplan(enrDoc.getEnregistreurDef().getInfosFichier().getNumplan());
		this.infoData.setTitreProjet(enrDoc.getEnregistreurDef().getInfosFichier().getTitre());
		this.infoData.setVersionXML(enrDoc.getEnregistreurDef().getInfosFichier().getVersionXML());
		this.infoData.setCRCFichierXML(enrDoc.getEnregistreurDef().getSignature().getCRC());
		ParseurUtils.verifCRC(fileName,	Integer.parseInt(enrDoc.getEnregistreurDef().getSignature().getCRC(), 16));

		this.infoParcours = enrDoc.getEnregistreurDef().getInfosParcours();

		try {
			switch (enrDoc.getEnregistreurDef().getEnregistreur().getResolutionTemps().intValue()) {
				case ResolutionTemps.INT_X_1:
					this.infoData.setTempResolution(TempResolutionEnum.RESOLUTION_1);
					break;
				case ResolutionTemps.INT_X_0_1:
					this.infoData.setTempResolution(TempResolutionEnum.RESOLUTION_0_1);
					break;
				case ResolutionTemps.INT_X_0_01:
					this.infoData.setTempResolution(TempResolutionEnum.RESOLUTION_0_01);
					break;
				case ResolutionTemps.INT_X_0_001:
					this.infoData.setTempResolution(TempResolutionEnum.RESOLUTION_0_001);
					break;
				case ResolutionTemps.INT_X_0_0001:
					this.infoData.setTempResolution(TempResolutionEnum.RESOLUTION_0_0001);
					break;
				case ResolutionTemps.INT_X_0_00001:
					this.infoData.setTempResolution(TempResolutionEnum.RESOLUTION_0_00001);
					break;
			}
		} catch (Exception e) {
			System.err.println("resolution-temp not set");
		}
		
		// Creates the lists of variables and events
		this.varsNameId = new HashMap<String, Integer>();
		this.xmlFileName = fileName;
	}

	/**
	 * Clears the data loaded in the parser
	 * 
	 */
	public void clear() {
		this.enr = null;
		if (this.varsNameId != null) {
			this.varsNameId.clear();
			this.varsNameId = null;
		}
		this.infoData = null;
		this.xmlFileName = null;
		infoParcours = null;
		xmlFileName = null;
	}

	/**
	 * @return true if the xml file was parsed
	 */
	public boolean isRessourceLoaded() {
		return this.enr != null;
	}

	/**
	 * @return the only instance of the parser
	 */
	public static ParseurXML1 getInstance() {
		if (instance == null) {
			if (BridageFormats.getInstance().getFormatFichierOuvert("") != null) {
				FormatSAM fS = BridageFormats.getInstance()
						.getFormatFichierOuvert("");
				if (fS == FormatSAM.TOMNG || fS == FormatSAM.ATESS
						|| fS == FormatSAM.TOM4) {
					instance = new ParseurXML_NG_UK_ATESS();
					// if (ActivationExplorer.getInstance().isActif()) {
					// instance=new ParseurXML_NG_UK_ATESS_Explorer();
					// }
					return instance;
				} else if (fS == FormatSAM.JRU) {
					instance = new ParseurXMLJRU();
					// if (ActivationExplorer.getInstance().isActif()) {
					// instance=new ParseurXML_JRUExplorer();
					// }
					return instance;
				} else
					return null;
			} else
				return null;
		} else {
			return instance;
		}
	}

	/**
	 * @return the only instance of the parser
	 */
	public static ParseurXML1 getInstance(String filename) {
		if (BridageFormats.getInstance().getFormatFichierOuvert(filename) != null) {
			FormatSAM fS = BridageFormats.getInstance().getFormatFichierOuvert(
					filename);
			if (fS == FormatSAM.TOMNG || fS == FormatSAM.ATESS
					|| fS == FormatSAM.TOM4) {
				instance = new ParseurXML_NG_UK_ATESS();
				return instance;
			} else if (fS == FormatSAM.JRU) {
				instance = new ParseurXMLJRU();
				return instance;
			} else
				return null;
		} else
			return null;
	}

	public static void setParseurXML() {
		if (BridageFormats.getInstance().getFormatFichierOuvert("") != null) {
			FormatSAM fS = BridageFormats.getInstance().getFormatFichierOuvert(
					"");
			if (fS == FormatSAM.TOMNG || fS == FormatSAM.ATESS
					|| fS == FormatSAM.TOM4) {
				instance = new ParseurXML_NG_UK_ATESS();
			} else if (fS == FormatSAM.JRU) {
				instance = new ParseurXMLJRU();
			}
		}
	}

	public static void setParseurXML(ParseurXML1 parseur) {
		instance = parseur;
	}

	/**
	 * Loads the reperes
	 * 
	 * @return the reperes in a parcourComposant
	 */
	public AParcoursComposant chargerReperes() {
		AVariableComposant reper;
		com.faiveley.samng.principal.sm.data.tableAssociationComposant.Reperes reperes = new com.faiveley.samng.principal.sm.data.tableAssociationComposant.Reperes();

		// gets all the reperes
		for (TypeRepere reperType : EnumSet.range(TypeRepere.date,
				TypeRepere.tempsAvantChangement)) {
			switch (reperType) {
			case date:
				reperType.setOptionnel(false);
				break;

			case distance:
				reperType.setOptionnel(false);
				break;

			case temps:
				reperType.setOptionnel(false);
				break;

			case vitesse:
				reperType.setOptionnel(true);
				break;

			case diametreRoue:
				reperType.setOptionnel(true);
				break;

			case dateAvantChangement:
				reperType.setOptionnel(true);
				break;

			case tempsAvantChangement:
				reperType.setOptionnel(true);
				break;

			default:
				break;
			}

			// load reper
			reper = chargerRepere(reperType);
			if (reper != null) {
				// add reper
				reperes.ajouterReper(reperType, reper);
			}
		}
		return reperes;
	}

	/**
	 * Loads the element <xs:element name="reperes"> from xml
	 * 
	 * @return the variable
	 */
	private AVariableComposant chargerRepere(TypeRepere typeReper) {
		if (!isRessourceLoaded())
			return null;

		// GET REPERE FROM XML
		Reperes reper = this.enr.getReperes();
		VariableRepere rep = new VariableRepere(typeReper);
		DescripteurVariable descr = null;
		String varName = null;
		// load the variable that corresponds to the ID from repere
		switch (typeReper) {
		case date:
			varName = reper.getDate();
			break;

		case distance:
			varName = reper.getDistance();
			break;

		case temps:
			varName = reper.getTemps();
			break;

		case vitesse:
			varName = reper.getVitesse();
			break;

		case diametreRoue:
			varName = reper.getDiametreRoue();
			break;

		case dateAvantChangement:
			varName = reper.getDateAvantChangement();
			break;

		case tempsAvantChangement:
			varName = reper.getTempsAvantChangement();
			break;

		default:
			break;
		}

		if (varName == null) {
			if (!typeReper.isOptionnel()
					&& (typeReper != TypeRepere.distanceCorrigee)
					&& (typeReper != TypeRepere.tempsCorrigee)
					&& (typeReper != TypeRepere.vitesseCorrigee)
					&& (typeReper != TypeRepere.distanceRelatif)
					&& (typeReper != TypeRepere.tempsRelatif)
					&& (typeReper != TypeRepere.distanceCumulee)) {
				LOGGER.warn(Messages
						.getString("errors.nonblocking.unsupportedRepereRequested"));
			}
			return null;
		}

		// loads the descriptor for this repere
		try {
			Integer varNameId = this.varsNameId.get(varName);
			if (varNameId == null) {
				descr = loadDescriptorForRepereName(varName);
			} else {
				descr = GestionnaireDescripteurs
						.getDescripteurVariable(varNameId);
			}
		} catch (Exception e) {
			descr = null;
		}

		if (descr == null) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.repereVariableNotFoundInXML")
					+ " " + varName);
			return null;
		}

		// set the members data for the reper
		typeReper.setName(varName);
		typeReper.setCode(descr.getM_AIdentificateurComposant().getCode());
		rep.setNomRepere(varName);
		rep.setDescripteur(descr);
		return rep;
	}

	/**
	 * loads the event with the given code from the xml file
	 * 
	 * @param code
	 *            the event code
	 * @return the event
	 */
	public com.faiveley.samng.principal.sm.data.enregistrement.Evenement chargerEvenement(
			int code) {

		// if the xml was loaded
		if (!isRessourceLoaded())
			return null;

		com.faiveley.samng.principal.sm.data.enregistrement.Evenement e = new com.faiveley.samng.principal.sm.data.enregistrement.Evenement();

		// gets the descriptor from pool
		DescripteurEvenement descr = GestionnaireDescripteurs
				.getDescripteurEvenement(code);
		e.setM_ADescripteurComposant(descr);

		List<Evenement> evList = this.enr.getListeEvenements()
				.getEvenementList();
		for (Evenement ev : evList) {
			// loads the descriptor
			if (code == getEvenementCode(ev)) {
				fillDescripteurEvenement(ev, descr);
				List<NomUtilisateur> usersList = null;
				try {
					usersList = ev.getNomUtilisateurList();
				} catch (Exception ex) {
				}
				e.setNomUtilisateur(setListOfUsers(usersList));

				switch (ev.getChangementHeure().intValue()) {
				case ChangementHeureImpl.INT_TRUE:
					e.setChangementHeure(true);
					break;

				case ChangementHeureImpl.INT_FALSE:
					e.setChangementHeure(false);
					break;
				}

				switch (ev.getRuptureAcquisition().intValue()) {
				case RuptureAcquisitionImpl.INT_TRUE:
					e.setRuptureAcquisition(true);
					break;

				case RuptureAcquisitionImpl.INT_FALSE:
					e.setRuptureAcquisition(false);
					break;
				}

				switch (ev.getRazCompteurTemps().intValue()) {
				case RazCompteurTemps.INT_TRUE:
					e.setRazCompteurTemps(true);
					break;

				case RazCompteurTemps.INT_FALSE:
					e.setRazCompteurTemps(false);
					break;
				}

				switch (ev.getRazCompteurDistance().intValue()) {
				case RazCompteurDistance.INT_TRUE:
					e.setRazCompteurDistance(true);
					break;

				case RazCompteurDistance.INT_FALSE:
					e.setRazCompteurDistance(false);
					break;
				}

				switch (ev.getSynchroTempsDistance().intValue()) {
				case SynchroTempsDistance.INT_YES:
					e.setASychroniser(true);
					e.setReferenceSynchro(false);
					break;
				case SynchroTempsDistance.INT_NO:
					e.setASychroniser(false);
					e.setReferenceSynchro(false);
					break;
				case SynchroTempsDistance.INT_REF:
					e.setReferenceSynchro(true);
					e.setASychroniser(false);
					break;
				default:
					e.setASychroniser(false);
					e.setReferenceSynchro(false);
					break;
				}
			}
		}
		return e;
	}

	/**
	 * loads the variable with the given code from the xml file
	 * 
	 * @param codeVar
	 *            the variable code
	 * @return the variable
	 */
	public AVariableComposant chargerVariable(int codeVar) {
		if (!isRessourceLoaded())
			return null;

		AVariableComposant var = null;
		DescripteurVariable descr = null;

		// look for the variable if it's an analogic one
		List<VariableAnalogique> analogicVars = getVariablesAnalogiques(enr);
		for (VariableAnalogique varA : analogicVars) {
			if (getAnalogicVariableCode(varA) == codeVar) {
				com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique v = new com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique();

				// get descriptor from pool
				descr = GestionnaireDescripteurs
						.getDescripteurVariableAnalogique(codeVar);
				v.setDescripteur(descr);

				if (!descr.isRenseigne()) {
					descr.setTypeVariable(TypeVariable.VAR_ANALOGIC);
					// set the members
					fillDescriptorVariableAnalogique(varA,
							(DescripteurVariableAnalogique) descr);
				}
				v.setTypeValeur(v.getDescriptor().getType());
				var = v;
				break;
			}
		}

		if (var == null) {
			// if complexe variable
			List<VariableComplexe> complexeVars = getVariablesComplexes(enr);
			for (VariableComplexe varC : complexeVars) {
				// look for the variable
				if (getComplexVariableCode(varC) == codeVar) {
					com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe v = new com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe();

					// get the descriptor from pool
					descr = GestionnaireDescripteurs
							.getDescripteurVariableComplexe(codeVar);
					v.setDescripteur(descr);
					if (!descr.isRenseigne()) {
						// fill descriptor
						descr.setTypeVariable(TypeVariable.VAR_COMPLEXE);
						fillDescriptorVariableComplexe(varC, descr);
					}
					transformVariableComplexe(varC, v);
					var = v;
					break;
				}
			}
		}

		if (var == null) {
			// look for a discrete variable
			List<VariableDiscrete> discreteVars = getVariablesDiscretes(enr);
			for (VariableDiscrete varD : discreteVars) {
				if (getDiscreteVariableCode(varD) == codeVar) {

					// create the variable
					com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete v = createDiscreteVarOfType(varD);

					// get descriptor from pool
					descr = GestionnaireDescripteurs
							.getDescripteurVariableDiscrete(codeVar);
					v.setDescripteur(descr);
					if (!descr.isRenseigne()) {
						descr.setTypeVariable(TypeVariable.VAR_DISCRETE);
						// set the members
						fillDescriptorVariableDiscrete(varD,
								(DescripteurVariableDiscrete) descr);
					}
					v.setTypeValeur(v.getDescriptor().getType());
					var = v;

					break;
				}
			}
		}

		// CIU - we might not find the code given and thus descr can be null
		if (descr != null) {
			// throw exception if nom is null
			if (descr.getM_AIdentificateurComposant().getNom() == null
					|| "".equals(descr.getM_AIdentificateurComposant().getNom()
							.trim())) {
				LOGGER.warn(Messages
						.getString("errors.nonblocking.invalidXmlVariableName")
						+ descr.getM_AIdentificateurComposant().getCode());
				return null;
			}
			Integer existingId = this.varsNameId.get(descr
					.getM_AIdentificateurComposant().getNom());
			// Check if the variable name already exists defined
			if (existingId != null
					&& descr.getM_AIdentificateurComposant().getCode() != existingId) {
				LOGGER.warn(Messages
						.getString("errors.nonblocking.duplicateXmlVariableName")
						+ " Code: "
						+ descr.getM_AIdentificateurComposant().getCode()
						+ " Existing code: "
						+ existingId
						+ " Name: "
						+ descr.getM_AIdentificateurComposant().getNom());
			}

			this.varsNameId.put(descr.getM_AIdentificateurComposant().getNom(),
					descr.getM_AIdentificateurComposant().getCode());
		}
		return var;
	}

	/**
	 * Returns the type of the variable with the given code
	 * 
	 * @param codeVar
	 *            variable code
	 * @return the type of the variable
	 */
	public TypeVariable getVariableType(int codeVar) {

		// if xml is not loaded then return
		if (!isRessourceLoaded())
			return null;

		DescripteurVariable descr = GestionnaireDescripteurs
				.containsDescriptorVariable(codeVar);
		if (descr != null && descr.getTypeVariable() != null) {
			return descr.getTypeVariable();
		}

		// look for the variable if it's an analogic one
		List<VariableAnalogique> variablesAnalogiques = getVariablesAnalogiques(enr);
		for (VariableAnalogique varA : variablesAnalogiques) {
			if (getAnalogicVariableCode(varA) == codeVar) {
				return TypeVariable.VAR_ANALOGIC;
			}
		}

		// look if it's complexe
		List<VariableComplexe> variablesComplexes = getVariablesComplexes(enr);
		for (VariableComplexe varC : variablesComplexes) {
			if (getComplexVariableCode(varC) == codeVar) {
				return TypeVariable.VAR_COMPLEXE;
			}
		}

		// look if it's discrete
		List<VariableDiscrete> variablesDiscretes = getVariablesDiscretes(enr);
		for (VariableDiscrete varD : variablesDiscretes) {
			if (getDiscreteVariableCode(varD) == codeVar) {
				return TypeVariable.VAR_DISCRETE;
			}
		}

		// look if it's a structure dynamique
		List<StructureDynamique> structureDynamiques = getStructureDynamiques(enr);
		for (StructureDynamique structDyn : structureDynamiques) {
			if (getStructureDynamiqueVariableCode(structDyn) == codeVar) {
				return TypeVariable.STRUCTURE_DYNAMIQUE;
			}
		}

		// look if it's tableau dynamique
		List<TableauDynamique> tableauDynamiques = getTableauDynamiques(enr);
		for (TableauDynamique tableauDyn : tableauDynamiques) {
			if (getTableauDynamiqueVariableCode(tableauDyn) == codeVar) {
				return TypeVariable.TABLEAU_DYNAMIQUE;
			}
		}

		// if not found show an error
		LOGGER.warn(Messages
				.getString("errors.nonblocking.codeVarNotFoundInXML") + codeVar);

		return null;
	}

	/**
	 * Returns the info file
	 * 
	 * @return info as a ParcourComposant
	 */
	public AParcoursComposant getInfosFichier() {
		return this.infoData;
	}

	/**
	 * Fills a descriptor for an analogic variable
	 * 
	 * @param varA
	 *            the variable
	 * @param descr
	 *            the descriptor
	 * @return the descriptor filled
	 */
	private DescripteurVariableAnalogique fillDescriptorVariableAnalogique(
			VariableAnalogique varA, DescripteurVariableAnalogique descr) {
		if (descr.isRenseigne()) {
			return descr;
		}

		// get identificator
		IdentificateurVariable ident = (IdentificateurVariable) descr
				.getM_AIdentificateurComposant();

		// set identificator members
		ident.setNom(varA.getNom());
		int varCode = getAnalogicVariableCode(varA);
		ident.setCode(varCode);

		descr.setEscalier(varA.isSetEscalier());
		
		// check coef directeur
		try {
			descr.setCoefDirecteur(varA.getCoefDirecteur());
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlAnaCoefDir"));
			descr.setCoefDirecteur(1.0);
		}

		// check offset
		try {
			descr.setOrdonneeOrigine(varA.getOffset());
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlAnaOffset"));
			descr.setOrdonneeOrigine(0.0);
		}

		// check nb decimales
		try {

			descr.setNbDecimales(varA.getNbDecimales());
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlAnaNbDecimales"));
			descr.setNbDecimales(-1);
		}

		// check codage chaine
		try {
			descr.setCodageChaine(varA.getCodageChaine());
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlAnaCodageChaine"));
			descr.setCodageChaine("UTF-8");
		}

		// unnite
		descr.setUnite(varA.getUnite());

		// taille
		try {
			descr.setTailleBits(varA.getTaille());
		} catch (Exception e) {
			descr.setTailleBits(4);
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlAnaTaille"));
		}

		// PoidsPremierBit
		try {
			descr.setPoidsPremierBit(varA.getPoids1ErBit().intValue() == 1 ? Poids.MSB
					: Poids.LSB);
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlAnaPoidsPremierBit"));
			descr.setPoidsPremierBit(Poids.MSB);
		}

		// PoidsPremierOctet
		try {
			descr.setPoidsPremierOctet(varA.getPoids1ErOctet().intValue() == 1 ? Poids.MSB
					: Poids.LSB);
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlAnaPoidsPremierOcted"));
			descr.setPoidsPremierOctet(Poids.MSB);
		}

		// NomUtilisateur
		List<NomUtilisateur> usersList = null;
		try {
			usersList = Arrays.asList(varA.getNomUtilisateurArray());
		} catch (Exception ex) {
		}
		descr.setNomUtilisateur(setListOfUsers(usersList));

		// set value type
		fillDescripteurValueTypeVariableAnalogique(varA, descr);

		descr.setRenseigne(true);
		return descr;
	}

	private static void fillDescripteurValueTypeVariableAnalogique(
			VariableAnalogique varA, DescripteurVariableAnalogique descr) {
		try {
			switch (varA.getType().intValue()) {
			case VariableAnalogique.Type.INT_UINT_8: // INT_UINT_8 = 1;
				descr.setTailleBits(8);
				descr.setType(Type.uint8);
				break;
			case VariableAnalogique.Type.INT_X_INT_8: // INT_X_INT_8 = 2;
				descr.setTailleBits(8);
				descr.setType(Type.int8);
				break;
			case VariableAnalogique.Type.INT_UINT_16: // INT_UNIT_16 = 3;
				descr.setTailleBits(16);
				descr.setType(Type.uint16);
				break;
			case VariableAnalogique.Type.INT_X_INT_16: // INT_X_INT_16 = 4;
				descr.setTailleBits(16);
				descr.setType(Type.int16);
				break;
			case VariableAnalogique.Type.INT_UINT_24: // INT_UINT_24 = 5;
				descr.setTailleBits(24);
				descr.setType(Type.uint24);
				break;
			case VariableAnalogique.Type.INT_X_INT_24: // INT_X_INT_24 = 6;
				descr.setTailleBits(24);
				descr.setType(Type.int24);
				break;
			case VariableAnalogique.Type.INT_X_INT_32: // INT_UINT_32 = 7;
				descr.setTailleBits(32);
				descr.setType(Type.int32);
				break;
			case VariableAnalogique.Type.INT_UINT_32: // INT_UINT_32 = 7;
				descr.setTailleBits(32);
				descr.setType(Type.uint32);
				break;
			case VariableAnalogique.Type.INT_UINT_64: // INT_UINT_64 = 8;
				descr.setTailleBits(64);
				descr.setType(Type.uint64);
				break;
			case VariableAnalogique.Type.INT_X_INT_64: // INT_X_INT_64 = 9;
				descr.setTailleBits(64);
				descr.setType(Type.int64);
				break;
			case VariableAnalogique.Type.INT_REAL_32: // INT_REAL_32 = 10;
				descr.setTailleBits(32);
				descr.setType(Type.real32);
				break;
			case VariableAnalogique.Type.INT_REAL_64: // INT_REAL_64 = 11;
				descr.setTailleBits(64);
				descr.setType(Type.real64);
				break;
			case VariableAnalogique.Type.INT_UINT_XBITS: // INT_UINT_XBITS = 12;
				descr.setTailleBits(varA.getTaille());
				descr.setType(Type.uintXbits);
				break;
			case TypeImpl.INT_RESERVED:
				descr.setTailleBits(varA.getTaille());
				descr.setType(Type.reserved);
				break;
			default:
				// reserved
				descr.setTailleBits(varA.getTaille());
				descr.setType(Type.reserved);
				break;
			}
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlAnaType"));
			descr.setTailleBits(32);
			descr.setType(Type.real32);
		}
	}

	/**
	 * Fills a descriptor of a discrete variable
	 * 
	 * @param varD
	 *            the variable
	 * @param descr
	 *            the descriptor
	 * @return the descriptor filled
	 */
	private DescripteurVariableDiscrete fillDescriptorVariableDiscrete(
			VariableDiscrete varD, DescripteurVariableDiscrete descr) {
		if (descr.isRenseigne()) {
			return descr;
		}

		// get and fill the identificator
		IdentificateurVariable ident = (IdentificateurVariable) descr
				.getM_AIdentificateurComposant();
		ident.setNom(varD.getNom());
		int varCode = getDiscreteVariableCode(varD);
		ident.setCode(varCode);

		// set the members for the descriptor
		try {
			descr.setPoidsPremierBit(varD.getPoids1ErBit().intValue() == 1 ? Poids.MSB
					: Poids.LSB);
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlDiscrPoidsPremierBit"));
			descr.setPoidsPremierBit(Poids.MSB);
		}

		try {
			descr.setPoidsPremierOctet(varD.getPoids1ErOctet().intValue() == 1 ? Poids.MSB
					: Poids.LSB);
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlDiscrPoidsPremierOcted"));
			descr.setPoidsPremierOctet(Poids.MSB);
		}

		try {
			descr.setTailleBits(varD.getTaille());
		} catch (Exception e) {
			descr.setTailleBits(1);
		}

		// check nb decimales
		try {
			descr.setNbDecimales(varD.getNbDecimales());
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlDiscrNbDecimales"));
			descr.setNbDecimales(-1);
		}

		// check codage chaine
		try {
			descr.setCodageChaine(varD.getCodageChaine());
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlDiscrCodageChaine"));
			descr.setCodageChaine("UTF-8");
		}
		// set the list of users
		List<NomUtilisateur> usersList = null;
		try {
			usersList = varD.getNomUtilisateurList();
		} catch (Exception ex) {
		}
		descr.setNomUtilisateur(setListOfUsers(usersList));

		fillDescripteurValueTypeVariableDiscrete(varD, descr);

		// set the list of labels
		setLanguageLabels(varD.getListeTablelabelsList(), descr);

		descr.setRenseigne(true);

		return descr;
	}

	/**
	 * Méthode de renseignement du descripeur variable analogique utilisée pour
	 * les sous varaibles d'un tableau ou structure dynamique qui n'ont pas de
	 * code
	 * 
	 * @param varA
	 * @param descr
	 * @return une instance de DescripteurVariableAnalogique
	 */
	private DescripteurVariableDiscrete renseignerDescripteurVariableDiscreteVide(
			VariableDiscrete varD, DescripteurVariableDiscrete descr) {
		if (descr.isRenseigne()) {
			return descr;
		}

		// get and fill the identificator
		IdentificateurVariable ident = new IdentificateurVariable();
		if (varD.getNom() != null && !varD.getNom().equals("")) {
			ident.setNom(varD.getNom());
			// System.out.println("nom variable :" + ident.getNom() );
		}

		if (varD.getCode() != 0) {
			ident.setCode(varD.getCode());
			// System.out.println("code variable :" + ident.getCode());
		}
		// set the members for the descriptor
		try {
			descr.setPoidsPremierBit(varD.getPoids1ErBit().intValue() == 1 ? Poids.MSB
					: Poids.LSB);
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlDiscrPoidsPremierBit"));
			descr.setPoidsPremierBit(Poids.MSB);
		}

		try {
			descr.setPoidsPremierOctet(varD.getPoids1ErOctet().intValue() == 1 ? Poids.MSB
					: Poids.LSB);
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlDiscrPoidsPremierOcted"));
			descr.setPoidsPremierOctet(Poids.MSB);
		}

		try {
			descr.setTailleBits(varD.getTaille());
		} catch (Exception e) {
			descr.setTailleBits(1);
		}

		// check nb decimales
		try {
			descr.setNbDecimales(varD.getNbDecimales());
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlDiscrNbDecimales"));
			descr.setNbDecimales(-1);
		}

		// check codage chaine
		try {
			descr.setCodageChaine(varD.getCodageChaine());
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlDiscrCodageChaine"));
			descr.setCodageChaine("UTF-8");
		}
		// set the list of users
		List<NomUtilisateur> usersList = null;
		try {
			usersList = varD.getNomUtilisateurList();
		} catch (Exception ex) {
		}
		descr.setNomUtilisateur(setListOfUsers(usersList));

		// set the value type
		fillDescripteurValueTypeVariableDiscrete(varD, descr);

		// set the list of labels
		setLanguageLabels(varD.getListeTablelabelsList(), descr);

		descr.setRenseigne(true);

		return descr;
	}

	private static void fillDescripteurValueTypeVariableDiscrete(
			VariableDiscrete varD, DescripteurVariableDiscrete descr) {
		try {
			switch (varD.getType().intValue()) {
			case TypeImpl.INT_UINT_8: // INT_UINT_8 = 1;
				descr.setTailleBits(8);				
				descr.setType(Type.uint8);
				break;
			case TypeImpl.INT_X_INT_8: // INT_X_INT_8 = 2;
				descr.setTailleBits(8);
				descr.setType(Type.int8);
				break;
			case TypeImpl.INT_UINT_16: // INT_UNIT_16 = 3;
				descr.setTailleBits(16);
				descr.setType(Type.uint16);
				break;
			case TypeImpl.INT_X_INT_16: // INT_X_INT_16 = 4;
				descr.setTailleBits(16);
				descr.setType(Type.int16);
				break;
			case TypeImpl.INT_UINT_24: // INT_UINT_24 = 5;
				descr.setTailleBits(24);
				descr.setType(Type.uint24);
				break;
			case TypeImpl.INT_X_INT_24: // INT_X_INT_24 = 6;
				descr.setTailleBits(24);
				descr.setType(Type.int24);
				break;
			case TypeImpl.INT_UINT_32: // INT_UINT_32 = 7;
				descr.setTailleBits(32);
				descr.setType(Type.uint32);
				break;
			case TypeImpl.INT_X_INT_32: // INT_X_INT_32 = 8;
				descr.setTailleBits(32);
				descr.setType(Type.int32);
				break;
			case TypeImpl.INT_UINT_64: // INT_UINT_64 = 9;
				descr.setTailleBits(64);
				descr.setType(Type.uint64);
				break;
			case TypeImpl.INT_X_INT_64: // INT_X_INT_64 = 10;
				descr.setTailleBits(64);
				descr.setType(Type.int64);
				break;
			case TypeImpl.INT_BOOLEAN_8: // INT_BOOLEAN_8 = 11;
				descr.setTailleBits(8);
				descr.setType(Type.boolean8);
				break;
			case TypeImpl.INT_BOOLEAN_1: // INT_BOOLEAN_1 = 12;
				if (descr.getTailleBits() == 0)
					descr.setTailleBits(1);
				descr.setType(Type.boolean1);
				break;
			case TypeImpl.INT_STRING: // INT_STRING = 13;
				descr.setTailleBits(varD.getTaille() * 8);
				descr.setType(Type.string);
				break;
			case TypeImpl.INT_UNIX_TIMESTAMP: // INT_UNIX_TIMESTAMP = 14;
				descr.setTailleBits(varD.getTaille() * 8);
				descr.setType(Type.unixTimestamp);
				break;
			case TypeImpl.INT_BCD_4: // INT_BCD_4 = 15;
				descr.setTailleBits(4); // override the taille that is written in
				// XML as it can be missing
				// descr.setTaille(descr.getTaille() * 4);
				descr.setType(Type.BCD4);
				break;
			case TypeImpl.INT_BCD_8: // INT_BCD_8 = 16;
				// descr.setTaille(descr.getTaille() * 8);
				descr.setTailleBits(8); // override the taille that is written in
				// XML as it can be missing
				descr.setType(Type.BCD8);
				break;
			case TypeImpl.INT_ARRAY: // INT_ARRAY = 17;
				descr.setTailleBits(varD.getTaille() * 8);
				descr.setType(Type.array);
				break;
			case TypeImpl.INT_UINT_XBITS: // INT_UINT_XBITS = 18;
				descr.setType(Type.uintXbits);
				break;
			case TypeImpl.INT_RESERVED:
				descr.setTailleBits(varD.getTaille());
				descr.setType(Type.reserved);
				break;
			case TypeImpl.INT_DATE_HEURE_BCD:
				descr.setTailleBits(48);
				descr.setType(Type.dateHeureBCD);
				break;
			default:
				// reserved
				descr.setTailleBits(varD.getTaille());
				descr.setType(Type.reserved);
				break;
			}
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlDiscrType"));
			descr.setTailleBits(8);
			descr.setType(Type.uint8);
		}
	}

	/**
	 * Méthode de renseignement du descripeur variable analogique utilisée pour
	 * les sous varaibles d'un tableau ou structure dynamique qui n'ont pas de
	 * code
	 * 
	 * @param varA
	 * @param descr
	 * @return une instance de DescripteurVariableAnalogique
	 */
	private DescripteurVariableAnalogique renseignerDescripteurVariableAnalogiqueVide(
			VariableAnalogique varA, DescripteurVariableAnalogique descr) {
		if (descr.isRenseigne()) {
			return descr;
		}

		// get identificator
		IdentificateurVariable ident = new IdentificateurVariable();

		// set identificator members
		if (varA.getNom() != null && !varA.getNom().equals(""))
			ident.setNom(varA.getNom());

		if (varA.getCode() != 0)
			ident.setCode(varA.getCode());

		// check coef directeur
		try {
			descr.setCoefDirecteur(varA.getCoefDirecteur());
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlAnaCoefDir"));
			descr.setCoefDirecteur(1.0);
		}

		// check offset
		try {
			descr.setOrdonneeOrigine(varA.getOffset());
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlAnaOffset"));
			descr.setOrdonneeOrigine(0.0);
		}

		// check nb decimales
		try {

			descr.setNbDecimales(varA.getNbDecimales());
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlAnaNbDecimales"));
			descr.setNbDecimales(-1);
		}

		// check codage chaine
		try {
			descr.setCodageChaine(varA.getCodageChaine());
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlAnaCodageChaine"));
			descr.setCodageChaine("UTF-8");
		}

		// unnite
		descr.setUnite(varA.getUnite());

		// taille
		try {
			descr.setTailleBits(varA.getTaille());
		} catch (Exception e) {
			descr.setTailleBits(4);
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlAnaTaille"));
		}

		// PoidsPremierBit
		try {
			descr.setPoidsPremierBit(varA.getPoids1ErBit().intValue() == 1 ? Poids.MSB
					: Poids.LSB);
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlAnaPoidsPremierBit"));
			descr.setPoidsPremierBit(Poids.MSB);
		}

		// PoidsPremierOctet
		try {
			descr.setPoidsPremierOctet(varA.getPoids1ErOctet().intValue() == 1 ? Poids.MSB
					: Poids.LSB);
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlAnaPoidsPremierOcted"));
			descr.setPoidsPremierOctet(Poids.MSB);
		}

		// NomUtilisateur
		List<NomUtilisateur> usersList = null;
		try {
			usersList = Arrays.asList(varA.getNomUtilisateurArray());
		} catch (Exception ex) {
		}
		descr.setNomUtilisateur(setListOfUsers(usersList));

		fillDescripteurValueTypeVariableAnalogique(varA, descr);

		descr.setRenseigne(true);
		return descr;
	}

	/**
	 * Fills the descriptor of a complexe variable
	 * 
	 * @param var
	 *            the variable
	 * @param descr
	 *            the descriptor
	 * @return the descriptor filled
	 */
	private DescripteurVariable fillDescriptorVariableComplexe(
			VariableComplexe var, DescripteurVariable descr) {
		if (descr.isRenseigne()) {
			return descr;
		}

		// get and fill the identificator
		IdentificateurVariable ident = (IdentificateurVariable) descr
				.getM_AIdentificateurComposant();
		ident.setNom(var.getNom());
		ident.setCode(getComplexVariableCode(var));

		// set the members of the descriptor
		int taille = 0;
		try {
			taille = var.getTaille();
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlComplexTaille"));
		}

		// La taille d'une variable complexe est donnée en octet
		descr.setTailleBits(taille * 8);

		try {
			descr.setPoidsPremierBit(var.getPoids1ErBit().intValue() == 1 ? Poids.MSB
					: Poids.LSB);
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlComplexPoidsPremierBit"));
			descr.setPoidsPremierBit(Poids.MSB);
		}

		try {
			descr.setPoidsPremierOctet(var.getPoids1ErOctet().intValue() == 1 ? Poids.MSB
					: Poids.LSB);
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlComplexPoidsPremierOctet"));
			descr.setPoidsPremierOctet(Poids.MSB);
		}

		// set the nom utilisateur
		List<NomUtilisateur> usersList = null;
		try {
			usersList = var.getNomUtilisateurList();
		} catch (Exception ex) {
		}
		descr.setNomUtilisateur(setListOfUsers(usersList));
		descr.setRenseigne(true);
		return descr;
	}

	/**
	 * Fills the descriptor of a dynamic structure
	 * 
	 * @param structure
	 *            the variable
	 * @param descr
	 *            the descriptor
	 * @return the descriptor filled
	 */
	private DescripteurStructureDynamique fillDescriptorStructureDynamique(
			StructureDynamique structure, DescripteurStructureDynamique descr) {
		if (descr.isRenseigne()) {
			return descr;
		}

		// get and fill the identificator
		IdentificateurVariable ident = (IdentificateurVariable) descr
				.getM_AIdentificateurComposant();
		ident.setNom(structure.getNom());
		ident.setCode(structure.getCode());

		// set the nom utilisateur
		List<NomUtilisateur> usersList = null;
		try {
			usersList = structure.getNomUtilisateurList();
		} catch (Exception ex) {
		}
		descr.setNomUtilisateur(setListOfUsers(usersList));
		descr.setRenseigne(true);
		return descr;
	}

	/**
	 * Fills the descriptor of a dynamic table
	 * 
	 * @param tableau
	 *            the variable
	 * @param descr
	 *            the descriptor
	 * @return the descriptor filled
	 */
	private DescripteurTableauDynamique fillDescriptorTableauDynamique(
			TableauDynamique tableau, DescripteurTableauDynamique descr) {
		if (descr.isRenseigne()) {
			return descr;
		}

		// get and fill the identificator
		IdentificateurVariable ident = (IdentificateurVariable) descr
				.getM_AIdentificateurComposant();
		if (tableau.getNom() != null && !tableau.getNom().equals(""))
			ident.setNom(tableau.getNom());

		if (tableau.getCode() != 0)
			ident.setCode(tableau.getCode());

		// set the nom utilisateur
		List<NomUtilisateur> usersList = null;
		try {
			usersList = tableau.getNomUtilisateurList();
		} catch (Exception ex) {
		}
		descr.setNomUtilisateur(setListOfUsers(usersList));
		descr.setRenseigne(true);
		return descr;
	}

	/**
	 * Fills the descriptor of a dynamic string
	 * 
	 * @param chaine
	 *            the variable
	 * @param descr
	 *            the descriptor
	 * @return the descriptor filled
	 */
	private DescripteurChaineDynamique fillDescriptorChaineDynamique(
			ChaineDynamique tableau, DescripteurChaineDynamique descr) {
		if (descr.isRenseigne()) {
			return descr;
		}

		// get and fill the identificator
		IdentificateurVariable ident = (IdentificateurVariable) descr
				.getM_AIdentificateurComposant();
		if (tableau.getNom() != null && !tableau.getNom().equals(""))
			ident.setNom(tableau.getNom());

		if (tableau.getCode() != 0)
			ident.setCode(tableau.getCode());

		// set the nom utilisateur
		List<NomUtilisateur> usersList = null;
		try {
			usersList = tableau.getNomUtilisateurList();
		} catch (Exception ex) {
		}
		descr.setNomUtilisateur(setListOfUsers(usersList));
		descr.setRenseigne(true);
		return descr;
	}

	/**
	 * Méthode d'ajout d'une variable simple(pas de structure dynamique ni
	 * tableau dynamique) à une table de sous variable
	 * 
	 * @param structureDynamiqueXml
	 * @param structureDynamique
	 * @param nomVariable
	 * @param variableVolatile
	 */
	private void ajouterVariableSimpleTableSousVariable(
			com.faiveley.samng.principal.sm.data.variableComposant.TableSousVariable tableSousVar,
			String nomVariable, boolean variableVolatile) {
		Integer code = this.varsNameId.get(nomVariable);
		if (code == null) {
			LOGGER.error(Messages.getString("errors.nonblocking.variableNotFound") + " : " + nomVariable);
			return;
		}
		
		int codeVarEntete = code.intValue();
		DescripteurVariable descVar = GestionnaireDescripteurs
				.getDescripteurVariable(codeVarEntete);
		AVariableComposant var = null;
		if (descVar.getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
			var = new com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique();
			DescripteurVariableAnalogique d = GestionnaireDescripteurs
					.getDescripteurVariableAnalogique(codeVarEntete);
			var.setDescripteur(d);
			var.setTypeValeur(d.getType());
			tableSousVar.ajouter(var);

		} else if (descVar.getTypeVariable() == TypeVariable.VAR_DISCRETE) {
			var = new com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete();
			DescripteurVariableDiscrete d = GestionnaireDescripteurs
					.getDescripteurVariableDiscrete(codeVarEntete);
			var.setDescripteur(d);
			var.setTypeValeur(d.getType());
			tableSousVar.ajouter(var);
		} else if (descVar.getTypeVariable() == TypeVariable.STRUCTURE_DYNAMIQUE) {
			var = new com.faiveley.samng.principal.sm.data.variableComposant.JRU.StructureDynamique();
			DescripteurStructureDynamique d = GestionnaireDescripteurs
					.getDescripteurStructureDynamique(codeVarEntete);
			var.setDescripteur(d);
			var.setTypeValeur(d.getType());
			tableSousVar.ajouter(var);
		} else if (descVar.getTypeVariable() == TypeVariable.TABLEAU_DYNAMIQUE) {
			var = new com.faiveley.samng.principal.sm.data.variableComposant.JRU.TableauDynamique();
			DescripteurTableauDynamique d = GestionnaireDescripteurs
					.getDescripteurTableauDynamique(codeVarEntete);
			var.setDescripteur(d);
			var.setTypeValeur(d.getType());
			tableSousVar.ajouter(var);
		} else if (descVar.getTypeVariable() == TypeVariable.CHAINE_DYNAMIQUE) {
			var = new com.faiveley.samng.principal.sm.data.variableComposant.JRU.ChaineDynamique();
			DescripteurChaineDynamique d = GestionnaireDescripteurs
					.getDescripteurChaineDynamique(codeVarEntete);
			var.setDescripteur(d);
			var.setTypeValeur(d.getType());
			tableSousVar.ajouter(var);
		} else if (descVar.getTypeVariable() == TypeVariable.VAR_COMPLEXE) {
			// Get the descriptor of the complexe variable		
			com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe v = new com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe();
 			v = (com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe)GestionnairePool.getInstance().getVariable(codeVarEntete).copy();
 			
 			int nbChildren = v.getVariableCount();
 			
 			for (int i = 0; i < nbChildren; i++) {
 				v.getEnfant(i).getDescriptor().setVolatil(true);
 			}
 			
			tableSousVar.ajouter(v);
		}
		
		if (variableVolatile && var != null) {
			descVar.setVolatil(true);
		}
	}

	/**
	 * Méthode d'ajout de la variable d'entete à la structure dynamique
	 * 
	 * @param structureDynamiqueXml
	 * @param structureDynamique
	 */
	private void ajouterVariableTeteStructureDynamique(
			StructureDynamique structureDynamiqueXml,
			com.faiveley.samng.principal.sm.data.variableComposant.JRU.StructureDynamique structureDynamique) {
		String nomVariable = structureDynamiqueXml.getVariableVolatile()
				.selectAttribute(new QName("nom")).getDomNode().getFirstChild()
				.getNodeValue();
		int codeVarEntete = this.varsNameId.get(nomVariable);
		ParseurUtils.ajouterVariableTeteVariableDynamique(codeVarEntete,
				structureDynamique);
	}

	/**
	 * Méthode d'ajout de la variable d'entete au tableau dynamique
	 * 
	 * @param tableauDynamiqueXml
	 * @param tableauDynamique
	 */
	private void ajouterVariableTeteTableauDynamique(
			TableauDynamique tableauDynamiqueXml,
			com.faiveley.samng.principal.sm.data.variableComposant.JRU.TableauDynamique tableauDynamique) {
		String nomVariable = tableauDynamiqueXml.getVariableVolatile()
				.selectAttribute(new QName("nom")).getDomNode().getFirstChild()
				.getNodeValue();
		int codeVarEntete = this.varsNameId.get(nomVariable);
		ParseurUtils.ajouterVariableTeteVariableDynamique(codeVarEntete,
				tableauDynamique);
	}

	/**
	 * Méthode d'ajout de la variable d'entete à la chaine dynamique
	 * 
	 * @param chaineDynamiqueXml
	 * @param chaineDynamique
	 */
	private void ajouterVariableTeteChaineDynamique(
			ChaineDynamique chaineDynamiqueXml,
			com.faiveley.samng.principal.sm.data.variableComposant.JRU.ChaineDynamique chaineDynamique) {
		String nomVariable = chaineDynamiqueXml.getVariableVolatile()
				.selectAttribute(new QName("nom")).getDomNode().getFirstChild()
				.getNodeValue();
		int codeVarEntete = this.varsNameId.get(nomVariable);
		ParseurUtils.ajouterVariableTeteVariableDynamique(codeVarEntete,
				chaineDynamique);
	}

	/**
	 * 
	 * Méthode de création d'une table sous-variable
	 * 
	 * @param tableSousVarXml
	 * @return une instance de TableSousVariable remplie
	 */
	private TableSousVariable creerTableSousVariableVariableDynamique(
			TableSousVariables tableSousVarXml) {

		TableSousVariable tableSousVar = new TableSousVariable();
		if (tableSousVarXml.getValeur() != null
				&& !tableSousVarXml.getValeur().toString().equals(""))
			tableSousVar.setValeur(tableSousVarXml.getValeur());

		// System.out.println("valeur table sous variable:" +
		// tableSousVar.getValeur());
		List<SousVariable> listeSousVarXml = tableSousVarXml
				.getSousVariableList();
		for (SousVariable variable : listeSousVarXml) {
			// System.out.println("sous-variable");
			if (variable.isSetStructureDynamique()) {
				// cas où la structure dynamique n'est pas référencée plus haut
				// dans le fichier xml:
				// tableau dynamique définie entièrement dans l'élément
				// sous-variable
				// System.out.println("structure dynamique");
				StructureDynamique structureDynamiqueXml = variable
						.getStructureDynamique();
				com.faiveley.samng.principal.sm.data.variableComposant.JRU.StructureDynamique structureDynamique = creerStructureDynamique(structureDynamiqueXml);
				tableSousVar.ajouter(structureDynamique);
			} else if (variable.isSetTableauDynamique()) {
				// cas où le tableau dynamique n'est pas référencée plus haut
				// dans le fichier xml:
				// tableau dynamique définie entièrement dans l'élément
				// sous-variable
				TableauDynamique tableauDynamiqueXml = variable
						.getTableauDynamique();
				com.faiveley.samng.principal.sm.data.variableComposant.JRU.TableauDynamique tableauDynamique = creerTableauDynamique(tableauDynamiqueXml);
				tableSousVar.ajouter(tableauDynamique);
				// System.out.println("tableau dynamique");
			} else if (variable.isSetChaineDynamique()) {
				// cas où la chaine dynamique n'est pas référencée plus haut
				// dans le fichier xml:
				// tableau dynamique définie entièrement dans l'élément
				// sous-variable
				ChaineDynamique chaineDynamiqueXml = variable
						.getChaineDynamique();
				com.faiveley.samng.principal.sm.data.variableComposant.JRU.ChaineDynamique tableauDynamique = creerChaineDynamique(chaineDynamiqueXml);
				tableSousVar.ajouter(tableauDynamique);
				// System.out.println("tableau dynamique");
			} else if (variable.isSetVariableComplexe()) {
				System.out.println("variable complexe");
				//a faire
			} else if (variable.isSetVariableDiscrete()) {
				// System.out.println("variable discrete");
				String nomVariable = variable.getVariableDiscrete().getNom();
				// System.out.println("nomVariable : " + nomVariable);
				// cas où la varaible est référencée plus haut dans le fichier
				// xml: attribut nom de type idref
				if (this.varsNameId.get(nomVariable) != null) {
					// System.out.println("variable referencée");
					ajouterVariableSimpleTableSousVariable(tableSousVar,
							nomVariable, false);
				}
				// cas où la variable n'est pas référencée plus haut dans le
				// fichier xml:
				// variable définie entièrement dans l'élément sous-variable
				else {
					// System.out.println("variable non référencée");
					DescripteurVariableDiscrete descr = new DescripteurVariableDiscrete();
					com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete varDiscrete = new com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete();
					renseignerDescripteurVariableDiscreteVide(
							variable.getVariableDiscrete(), descr);
					tableSousVar.ajouter(varDiscrete);
				}
			} else if (variable.isSetVariableAnalogique()) {
				// System.out.println("variable analogique");
				String nomVariable = variable.getVariableAnalogique().getNom();
				// System.out.println("nomVariable : " + nomVariable);
				// cas où la varaible est référencée plus haut dans le fichier
				// xml: attribut nom de type idref
				if (this.varsNameId.get(nomVariable) != null) {
					// System.out.println("variable referencée");
					ajouterVariableSimpleTableSousVariable(tableSousVar,
							nomVariable, false);
				}
				// cas où la variable n'est pas référencée plus haut dans le
				// fichier xml:
				// variable définie entièrement dans l'élément sous-variable
				else {
					// System.out.println("variable non référencée");
					DescripteurVariableAnalogique descr = new DescripteurVariableAnalogique();
					com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique varAnalogique = new com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique();
					renseignerDescripteurVariableAnalogiqueVide(
							variable.getVariableAnalogique(), descr);
					tableSousVar.ajouter(varAnalogique);
				}
			} else if (variable.isSetVariableVolatile()) {
				// dans tous les cas: la variable est référencée plus haut dans
				// le fichier xml: attribut nom de type idref
				// System.out.println("variable volatile");
				String nomVariable = variable.getVariableVolatile().getNom();
				// System.out.println("nomVariable : " + nomVariable);
				ajouterVariableSimpleTableSousVariable(tableSousVar,
						nomVariable, true);
			} else if (variable.isSetPaquets()) {
				// dans tous les cas: la structure dynamique est référencée plus
				// haut dans le fichier xml: attribut nom de type idref
				// System.out.println("paquet");
				String nomVariable = variable.getPaquets().getNom();
				int codePaquet = this.varsNameId.get(nomVariable);

				com.faiveley.samng.principal.sm.data.variableComposant.Paquets paquet = new com.faiveley.samng.principal.sm.data.variableComposant.Paquets();
				DescripteurStructureDynamique descr = GestionnaireDescripteurs
						.getDescripteurPaquets(codePaquet);
				paquet.setDescripteur(descr);
				// System.out.println("structure dynamique : " +
				// descr.getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()));
				tableSousVar.ajouter(paquet);
			}
		}
		return tableSousVar;
	}

	/**
	 * Fill the variable complexe from the the variable returned by the parser
	 * 
	 * @param var
	 *            the variable returned by parser
	 * @param v
	 *            the varible complexe in Sam format
	 */
	private void transformVariableComplexe(
			VariableComplexe var,
			com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe v) {
		// loads the analogic variables
		int totalVariablesSize = 0;
		Langage langue = Activator.getDefault().getCurrentLanguage();

		String complexVarBeginName = "";
		String nomUniqueVariableComplexe = "";
		nomUniqueVariableComplexe = v.getDescriptor()
				.getM_AIdentificateurComposant().getNom();
		if (v.getDescriptor().getNomUtilisateur().getNomUtilisateur(langue) != null)
			complexVarBeginName = v.getDescriptor().getNomUtilisateur()
					.getNomUtilisateur(langue);
		else if (v.getDescriptor().getM_AIdentificateurComposant().getNom() != null)
			complexVarBeginName = v.getDescriptor()
					.getM_AIdentificateurComposant().getNom();
		else
			complexVarBeginName = var.getNom();

		complexVarBeginName += ".";

		List<SousVariable> sousVars = var.getSousVariableList();
		for (SousVariable sousVar : sousVars) {
			if (sousVar.isSetVariableAnalogique()) {
				if (!sousVar.getVariableAnalogique().getType()
						.equals(Type.reserved)) {
					VariableAnalogique varAna = sousVar.getVariableAnalogique();
					com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique vA = new com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique();
					int code = this.varsNameId.containsKey(varAna.getNom()) ? this.varsNameId
							.get(varAna.getNom()) : 0;

					DescripteurVariableAnalogique d = GestionnaireDescripteurs
							.getDescripteurVariableAnalogique(code);
					vA.setDescripteur(d);
					// set the members
					fillDescriptorVariableAnalogique(varAna, d);
					ParseurUtils.updateLanguage(d, complexVarBeginName);
					vA.setTypeValeur(vA.getDescriptor().getType());
					vA.setEscalier(d.isEscalier());

					v.ajouter(vA);
					totalVariablesSize += ParseurUtils.getVariableBitSize(vA);

					if (!vA.getDescriptor().getM_AIdentificateurComposant()
							.getNom()
							.startsWith(nomUniqueVariableComplexe + "."))
						LOGGER.warn(Messages
								.getString("errors.nonblocking.invalidXmlComplexSubVarName")
								+ " "
								+ vA.getDescriptor()
										.getM_AIdentificateurComposant()
										.getNom());
				}
			} else if (sousVar.isSetVariableDiscrete()) {
				if (!sousVar.getVariableDiscrete().getType()
						.equals(Type.reserved)) {
					VariableDiscrete varDiscr = sousVar.getVariableDiscrete();
					com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete vD = null;
					// create the variable
					vD = createDiscreteVarOfType(varDiscr);
					int code = this.varsNameId.containsKey(varDiscr.getNom()) ? this.varsNameId
							.get(varDiscr.getNom()) : 0;
					DescripteurVariableDiscrete d = GestionnaireDescripteurs
							.getDescripteurVariableDiscrete(code);
					vD.setDescripteur(d);

					// set the members
					fillDescriptorVariableDiscrete(varDiscr, d);
					ParseurUtils.updateLanguage(d, complexVarBeginName);
					vD.setTypeValeur(vD.getDescriptor().getType());

					v.ajouter(vD);
					totalVariablesSize += ParseurUtils.getVariableBitSize(vD);

					// totalVariablesSize +=
					// vD.getDescriptor().getTailleOctets();
					if (!vD.getDescriptor().getM_AIdentificateurComposant()
							.getNom()
							.startsWith(nomUniqueVariableComplexe + "."))
						LOGGER.warn(Messages
								.getString("errors.nonblocking.invalidXmlComplexSubVarName")
								+ " "
								+ vD.getDescriptor()
										.getM_AIdentificateurComposant()
										.getNom());
					;
				}
			} else if (sousVar.isSetVariableVolatile()) {
				String nomVariable = sousVar.getVariableVolatile().getNom();
				int codeVar = this.varsNameId.get(nomVariable);
				DescripteurVariable descVar = GestionnaireDescripteurs
						.getDescripteurVariable(codeVar);

				if (descVar.getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
					com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique variableAnalogique = new com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique();
					DescripteurVariableAnalogique d = GestionnaireDescripteurs
							.getDescripteurVariableAnalogique(codeVar);
					variableAnalogique.setDescripteur(d);
					v.ajouter(variableAnalogique);
				} else if (descVar.getTypeVariable() == TypeVariable.VAR_DISCRETE) {
					com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete variableDiscrete = new com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete();
					DescripteurVariableDiscrete d = GestionnaireDescripteurs
							.getDescripteurVariableDiscrete(codeVar);
					variableDiscrete.setDescripteur(d);
					v.ajouter(variableDiscrete);
				}
			}
		}

		List<SousVariableType2> sousVarsType2 = var.getSousVariableType2List();
		boolean contientSousVarType2 = false;
		for (SousVariableType2 sousVarType2 : sousVarsType2) {
			contientSousVarType2 = true;
			if (sousVarType2.isSetVariableAnalogique()) {
				VariableAnalogique varAna = sousVarType2
						.getVariableAnalogique();
				if (!varAna.getType().equals(Type.reserved)) {
					com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique vA = new com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique();
					int code = this.varsNameId.containsKey(varAna.getNom()) ? this.varsNameId
							.get(varAna.getNom()) : 0;

					DescripteurVariableAnalogique d = GestionnaireDescripteurs
							.getDescripteurVariableAnalogique(code);
					int bitOffset = Integer.parseInt(sousVarType2
							.getBitOffset().getStringValue());
					int byteOffset = Integer.parseInt(sousVarType2
							.getByteOffset().getStringValue());
					d.setOffsetComposant(new OffsetComposant(bitOffset,
							byteOffset));

					vA.setDescripteur(d);
					// set the members
					fillDescriptorVariableAnalogique(varAna, d);
					ParseurUtils.updateLanguage(d, complexVarBeginName);
					vA.setTypeValeur(vA.getDescriptor().getType());

					v.ajouter(vA);
					totalVariablesSize += ParseurUtils.getVariableBitSize(vA);

					if (!vA.getDescriptor().getM_AIdentificateurComposant()
							.getNom()
							.startsWith(nomUniqueVariableComplexe + "."))
						LOGGER.warn(Messages
								.getString("errors.nonblocking.invalidXmlComplexSubVarName")
								+ " "
								+ vA.getDescriptor()
										.getM_AIdentificateurComposant()
										.getNom());

					testerCoherenceSousVarType2(v, vA);
				}

			} else if (sousVarType2.isSetVariableDiscrete()) {
				if (!sousVarType2.getVariableDiscrete().getType()
						.equals(Type.reserved)) {
					VariableDiscrete varDiscr = sousVarType2
							.getVariableDiscrete();
					com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete vD = null;
					// create the variable
					vD = createDiscreteVarOfType(varDiscr);
					int code = this.varsNameId.containsKey(varDiscr.getNom()) ? this.varsNameId
							.get(varDiscr.getNom()) : 0;
					DescripteurVariableDiscrete d = GestionnaireDescripteurs
							.getDescripteurVariableDiscrete(code);

					int bitOffset = Integer.parseInt(sousVarType2
							.getBitOffset().getStringValue());
					int byteOffset = Integer.parseInt(sousVarType2
							.getByteOffset().getStringValue());
					OffsetComposant offsetComposant = new OffsetComposant(
							bitOffset, byteOffset);
					d.setOffsetComposant(offsetComposant);

					vD.setDescripteur(d);

					// set the members
					fillDescriptorVariableDiscrete(varDiscr, d);
					ParseurUtils.updateLanguage(d, complexVarBeginName);
					vD.setTypeValeur(vD.getDescriptor().getType());

					v.ajouter(vD);
					totalVariablesSize += ParseurUtils.getVariableBitSize(vD);

					// totalVariablesSize +=
					// vD.getDescriptor().getTailleOctets();
					if (!vD.getDescriptor().getM_AIdentificateurComposant()
							.getNom()
							.startsWith(nomUniqueVariableComplexe + "."))
						LOGGER.warn(Messages
								.getString("errors.nonblocking.invalidXmlComplexSubVarName")
								+ " "
								+ vD.getDescriptor()
										.getM_AIdentificateurComposant()
										.getNom());

					testerCoherenceSousVarType2(v, vD);
				}

			}

		}

		// cas des sous-variables de type 1
		// on enregistre une erreur non blocante dans les défauts
		// si total taille des sous-variables != taille varaiblecomplexe en bits
		// pour les sous-varaibles de type 2, le total taille des sous-variables
		// peut etre différent de la taille de la variable complexe en bits
		// (CL_J_6 )
		// Note, la taille d'une variable complexe est donnée en octet
		// Si la structure n'a pas une taille multiple de 8, la taille
		// ne peut être renseignée dans le XML (ex : var JRU), et donc ce test  
		// ne peut se faire. De manière générale, si la taille de la variable
		// complexe n'est pas renseignée, il ne faut pas faire ce test
		if (v.getDescriptor().getTailleBits() != 0)	{
			if (!contientSousVarType2) {
				if (totalVariablesSize != v.getDescriptor().getTailleBits()) {
					LOGGER.warn(Messages
						.getString("errors.nonblocking.invalidXmlComplexTaille1")
						+ " "
						+ Messages.getString("errors.nonblocking.variableId1")
						+ " "
						+ v.getDescriptor().getM_AIdentificateurComposant()
								.getCode());
				}
			}  
		} else { 
			// La taille en bit d'une variable complexe ne peut être
			// fixée dans le XML, mais ici on peut la positionner
			v.getDescriptor().setTailleBits(totalVariablesSize);
		}
	}

	/**
	 * Methode implémentant la clause CL_J_7 : la combinaison des champs
	 * "byte-offset", "bit-offset" et "taille" ne peut pas aboutir à ce qune
	 * sous-variable de type 2 soit en dehors de la taille totale de la variable
	 * complexe il sagit d'une erreur non blocante
	 * 
	 * @param variableComplexe
	 * @param sousVariable
	 */
	private static void testerCoherenceSousVarType2(
			com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe variableComplexe,
			com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant sousVariable) {

		int tailleVC = variableComplexe.getDescriptor().getTailleOctets();
		DescripteurVariable d = sousVariable.getDescriptor();
		int byteOffset = d.getOffsetComposant().getByteOffset();
		int bitOffset = d.getOffsetComposant().getBitOffset();
		String nomSousVariable = d.getM_AIdentificateurComposant().getNom();

		Logger logger = LOGGER;
		if (byteOffset > tailleVC - 1)
			logger.warn(Messages
					.getString("errors.nonblocking.invalidByteOffsetSubVar")
					+ " : "
					+ byteOffset
					+ " for "
					+ nomSousVariable
					+ ", "
					+ Messages
							.getString("errors.nonblocking.invalidOffsetComplexVar")
					+ " : " + tailleVC);

		if (bitOffset > 7)
			logger.warn(Messages
					.getString("errors.nonblocking.invalidBitOffsetSubVar")
					+ " : " + bitOffset + " for " + nomSousVariable);

	}

	/**
	 * Fills the descriptor of a composed variable
	 * 
	 * @param var
	 *            the variable
	 * @param descr
	 *            the descriptor
	 * @return the descriptor filled
	 */
	private DescripteurVariable fillDescriptorVariableComposee(
			VariableComposee var, DescripteurVariable descr) {
		if (descr.isRenseigne()) {
			return descr;
		}

		// get and fill the identificator
		IdentificateurVariable ident = (IdentificateurVariable) descr
				.getM_AIdentificateurComposant();
		ident.setNom(var.getNom());

		// set the nom utilisateur
		List<NomUtilisateur> usersList = null;
		try {
			usersList = var.getNomUtilisateurList();
		} catch (Exception ex) {
		}
		descr.setNomUtilisateur(setListOfUsers(usersList));
		// loads the tabel label-valeur
		setLanguageLabels(var.getListeTablelabelsList(), descr);

		descr.setRenseigne(true);
		return descr;
	}

	/**
	 * Fill the variable composed from the the variable returned by the parser
	 * 
	 * @param var
	 *            the variable returned by parser
	 * @param v
	 *            the varible composed in Sam format
	 */
	private void transformVariableComposee(VariableComposee var,
			VariableComposite v) {
		// loads the variables
		String sousVarNom;
		Integer varId;
		List<SousVar> listeSousVarsListe;

		// get the list of subvariables
		try {
			ListeSousvar listeSousVar = var.getListeSousvar();
			if (listeSousVar == null)
				return;
			listeSousVarsListe = listeSousVar.getSousVarList();
			if (listeSousVarsListe == null)
				return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		for (SousVar varAna : listeSousVarsListe) {
			if (varAna == null) {
				LOGGER.warn(Messages
						.getString("errors.nonblocking.nullSousVarDansCompose"));
				continue;
			}
			try {
				sousVarNom = varAna.getNom();
				if (sousVarNom == null)
					continue;
			} catch (Exception e) {
				LOGGER.warn(Messages
						.getString("errors.nonblocking.invalidVarComposeSousVarNom"));
				continue;
			}
			varId = this.varsNameId.get(sousVarNom);
			if (varId == null) {
				LOGGER.warn(Messages
						.getString("errors.nonblocking.invalidVarComposeSousVarNom"));
				continue;
			}
			ParseurUtils.updateSubVarVarComposite(varId, v);
		}
	}

	/**
	 * Fill the event from the the event returned by the parser
	 * 
	 * @param ev
	 *            the event returned by parser
	 * @param e
	 *            the event in Sam format
	 */
	private DescripteurEvenement fillDescripteurEvenement(Evenement ev,
			DescripteurEvenement descr) {
		if (descr.isRenseigne()) {
			return descr;
		}

		// get and fill the identificator
		IdentificateurEvenement identif = (IdentificateurEvenement) descr
				.getM_AIdentificateurComposant();
		int evCode = getEvenementCode(ev);

		identif.setCode(evCode);
		// if(evCode!=1)
		identif.setNom(ev.getNom());

		// set the descripotor members
		descr.setCode(evCode);
		descr.setNom(ev.getNom());

		try {
			descr.setCaractTemporelle(Temporelle.COPY_DOWN);
		} catch (Exception ex) {
			descr.setCaractTemporelle(null);
		}
		return descr;
	}

	/**
	 * Loads and sets the nom utilisator
	 * 
	 * @param list
	 * @return
	 */
	private static TableLangueNomUtilisateur setListOfUsers(
			List<NomUtilisateur> list) {
		TableLangueNomUtilisateur util = new TableLangueNomUtilisateur();
		if (list == null || list.size() == 0) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlUsersList"));
			return util;
		}
		// set for each language
		for (NomUtilisateur nm : list) {
			Langage lang = null;
			try {
				lang = ParseurUtils.chargerLangage(nm.getLang().intValue());
			} catch (Exception ex) {
				lang = Langage.DEF;
			}
			util.setNomUtilisateur(lang, nm.getLibelle());
		}
		return util;
	}

	/**
	 * Creates a Sam discrete variable from the discrete variable returned by
	 * parser
	 * 
	 * @param varDiscr
	 *            discrete variable return by paser
	 * @return Sam discrete variable
	 */
	private com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete createDiscreteVarOfType(
			VariableDiscrete varDiscr) {
		com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete v = null;
		int typeVal;
		if (varDiscr == null)
			return new com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete();
		try {
			typeVal = varDiscr.getType().intValue();
		} catch (Exception ex) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlDiscrType"));
			typeVal = 1; // make it a variable Numerique
		}

		// create a variable of the right type
		switch (typeVal) {
		case TypeImpl.INT_UINT_8: // INT_UINT_8 = 1;
		case TypeImpl.INT_X_INT_8: // INT_X_INT_8 = 2;
		case TypeImpl.INT_UINT_16: // INT_UNIT_16 = 3;
		case TypeImpl.INT_X_INT_16: // INT_X_INT_16 = 4;
		case TypeImpl.INT_UINT_24: // INT_UINT_24 = 5;
		case TypeImpl.INT_X_INT_24: // INT_X_INT_24 = 6;
		case TypeImpl.INT_UINT_32: // INT_UINT_32 = 7;
		case TypeImpl.INT_X_INT_32: // INT_X_INT_32 = 8;
		case TypeImpl.INT_UINT_64: // INT_UINT_64 = 9;
		case TypeImpl.INT_X_INT_64: // INT_X_INT_64 = 10;
			v = new com.faiveley.samng.principal.sm.data.variableComposant.VariableNumerique();
			break;
		case TypeImpl.INT_BOOLEAN_8: // INT_BOOLEAN_8 = 11;
		case TypeImpl.INT_BOOLEAN_1: // INT_BOOLEAN_1 = 12;
			v = new com.faiveley.samng.principal.sm.data.variableComposant.VariableBooleenne();
			break;
		case TypeImpl.INT_STRING: // INT_STRING = 13;
		case TypeImpl.INT_UNIX_TIMESTAMP: // INT_UNIX_TIMESTAMP = 14;
		case TypeImpl.INT_BCD_4: // INT_BCD_4 = 15;
		case TypeImpl.INT_BCD_8: // INT_BCD_8 = 16;
		case TypeImpl.INT_ARRAY: // INT_ARRAY = 17;
		case TypeImpl.INT_UINT_XBITS: // INT_UINT_XBITS = 18;
		default:
			v = new com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete();
			break;
		}

		return v;
	}

	/**
	 * Loads all the variables that are found in the xml file
	 * 
	 * @return a list with all variables
	 */
	public List<AVariableComposant> loadAllVariables() {
		List<AVariableComposant> variables = new ArrayList<AVariableComposant>();
		int varCode;
		// return if xml not loaded
		if (!isRessourceLoaded()) {
			return variables;
		}
		// set containing the codes of the already loaded variables
		// if a code already exists in this set, an exception will be thrown
		Set<Integer> variableCodes = new HashSet<Integer>();

		// convert discrete variables
		List<VariableDiscrete> varsDiscrete = getVariablesDiscretes(enr);
		for (VariableDiscrete varD : varsDiscrete) {
			varCode = getDiscreteVariableCode(varD);
			if (variableCodes.contains(varCode)) {
				throw new DuplicateVariableCodeException(
						"Blocking error. Duplicate variable code " + varCode);
			}
			variableCodes.add(varCode);
			com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete v = new com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete();
			DescripteurVariableDiscrete descr = GestionnaireDescripteurs
					.getDescripteurVariableDiscrete(varCode);
			v.setDescripteur(descr);

			descr = fillDescriptorVariableDiscrete(varD, descr);
			((DescripteurVariableDiscrete) v.getDescriptor()).getLabels();
			v.setTypeValeur(v.getDescriptor().getType());
			this.varsNameId.put(varD.getNom(), varCode);
			variables.add(v);
		}

		// convert analogic variables
		List<VariableAnalogique> varsAnalogiques = getVariablesAnalogiques(enr);
		for (VariableAnalogique varA : varsAnalogiques) {
			varCode = getAnalogicVariableCode(varA);
			if (variableCodes.contains(varCode)) {
				throw new DuplicateVariableCodeException(
						"Blocking error. Duplicate variable code " + varCode);
			}
			variableCodes.add(varCode);
			com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique v = new com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique();
			DescripteurVariableAnalogique descr = GestionnaireDescripteurs
					.getDescripteurVariableAnalogique(varCode);
			v.setDescripteur(descr);

			descr = fillDescriptorVariableAnalogique(varA, descr);
			v.setTypeValeur(v.getDescriptor().getType());
			this.varsNameId.put(varA.getNom(), varCode);
			variables.add(v);
		}

		// convert complexe variables
		List<VariableComplexe> varsComplexes = getVariablesComplexes(enr);
		for (VariableComplexe varC : varsComplexes) {
			try {
				varCode = getComplexVariableCode(varC); // a little optimization
				// and some checks
				if ((varCode & 0x8000) != 0)
					throw new BadVariableCodeException(
							Messages.getString("errors.nonblocking.invalidVariableId1") + ": " + varC.getNom());

				// check that the current variable code does not exists already
				// in the
				// variables set
				if (variableCodes.contains(varCode)) {
					throw new DuplicateVariableCodeException(
							"Blocking error. Duplicate variable code "
									+ varCode);
				}
				variableCodes.add(varCode);

				com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe v = new com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe();
				DescripteurVariable descr = GestionnaireDescripteurs
						.getDescripteurVariableComplexe(varCode);
				v.setDescripteur(descr);

				descr = fillDescriptorVariableComplexe(varC, descr);
				transformVariableComplexe(varC, v);
				this.varsNameId.put(varC.getNom(), varCode);
				variables.add(v);
			} catch (AExceptionSamNG ex) {
				ex.printStackTrace();
			}
		}

		List<TableauDynamique> varsTableauDynamiques = getTableauDynamiques(enr);
		for (TableauDynamique tableauDynXml : varsTableauDynamiques) {
			com.faiveley.samng.principal.sm.data.variableComposant.JRU.TableauDynamique tableauDyn = creerTableauDynamique(tableauDynXml);
			this.varsNameId.put(tableauDyn.getDescriptor()
					.getM_AIdentificateurComposant().getNom(), tableauDyn
					.getDescriptor().getM_AIdentificateurComposant().getCode());
			variables.add(tableauDyn);
		}

		List<ChaineDynamique> varsChainesDynamiques = getChaineDynamiques(enr);
		for (ChaineDynamique chaineDynXml : varsChainesDynamiques) {
			com.faiveley.samng.principal.sm.data.variableComposant.JRU.ChaineDynamique chaineDyn = creerChaineDynamique(chaineDynXml);
			this.varsNameId.put(chaineDyn.getDescriptor()
					.getM_AIdentificateurComposant().getNom(), chaineDyn
					.getDescriptor().getM_AIdentificateurComposant().getCode());
			variables.add(chaineDyn);
		}

		// convert composed variables
		List<VariableComposee> varsComposees = getVariablesComposee(enr);
		for (VariableComposee varC : varsComposees) {
			com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite v = new com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite();
			DescripteurVariable descr = GestionnaireDescripteurs
					.getDescripteurVariableComposee(varC.getNom());
			v.setDescripteur(descr);

			descr = fillDescriptorVariableComposee(varC, descr);
			transformVariableComposee(varC, v);
			variables.add(v);
		}

		List<StructureDynamique> varsStructuresDynamiques = getStructureDynamiques(enr);
		for (StructureDynamique structDynXml : varsStructuresDynamiques) {
			com.faiveley.samng.principal.sm.data.variableComposant.JRU.StructureDynamique structureDyn = creerStructureDynamique(structDynXml);
			this.varsNameId.put(structureDyn.getDescriptor()
					.getM_AIdentificateurComposant().getNom(), structureDyn
					.getDescriptor().getM_AIdentificateurComposant().getCode());
			variables.add(structureDyn);
		}

		List<AVariableComposant> cpVar = variables;
		Langage lng = Activator.getDefault().getCurrentLanguage();
		String nomUtilisateur;
		boolean isIdentique = false;
		for (AVariableComposant var : variables) {
			nomUtilisateur = var.getDescriptor().getNomUtilisateur()
					.getNomUtilisateur(lng);
			isIdentique = false;
			if (nomUtilisateur != null && (!nomUtilisateur.equals(""))) {
				for (AVariableComposant var2 : cpVar) {
					if (var2.getDescriptor().getNomUtilisateur()
							.getNomUtilisateur(lng) != null) {
						if (nomUtilisateur.equals(var2.getDescriptor()
								.getNomUtilisateur().getNomUtilisateur(lng))
								&& !var2.equals(var)) {
							var2.getDescriptor()
									.getNomUtilisateur()
									.setNomUtilisateur(
											lng,
											"("
													+ var2.getDescriptor()
															.getM_AIdentificateurComposant()
															.getNom()
													+ ") "
													+ var2.getDescriptor()
															.getNomUtilisateur()
															.getNomUtilisateur(
																	lng));
							isIdentique = true;
						}
					}
				}
				if (isIdentique) {
					var.getDescriptor()
							.getNomUtilisateur()
							.setNomUtilisateur(
									lng,
									"("
											+ var.getDescriptor()
													.getM_AIdentificateurComposant()
													.getNom()
											+ ") "
											+ var.getDescriptor()
													.getNomUtilisateur()
													.getNomUtilisateur(lng));
				}
			}
		}

		return variables;
	}

	/**
	 * Méthode de création d'une structure dynamique à partir de sa définition
	 * xml
	 * 
	 * @param structureDynamiqueXml
	 * @return une instance de StructureDynamique
	 */
	private com.faiveley.samng.principal.sm.data.variableComposant.JRU.StructureDynamique creerStructureDynamique(
			StructureDynamique structureDynamiqueXml) {
		int varCode = getStructureDynamiqueVariableCode(structureDynamiqueXml);
		com.faiveley.samng.principal.sm.data.variableComposant.JRU.StructureDynamique structureDyn = new com.faiveley.samng.principal.sm.data.variableComposant.JRU.StructureDynamique();

		DescripteurStructureDynamique descr = new DescripteurStructureDynamique();
		IdentificateurVariable identif = null;
		// cas où la structure dynamique est définie directement sous l'élément
		// xml liste-varaibles
		// dans ce cas il possède nom, code
		if (structureDynamiqueXml.getCode() != 0) {
			descr = GestionnaireDescripteurs
					.getDescripteurStructureDynamique(varCode);
			identif = (IdentificateurVariable) descr
					.getM_AIdentificateurComposant();
			if (structureDynamiqueXml.getNom() != null
					&& !structureDynamiqueXml.getNom().equals(""))
				identif.setNom(structureDynamiqueXml.getNom());
		}
		// cas où la structure dynamique est définie en tant que sous-variable
		// dans un élément table-sous-variables
		// dans ce cas elle ne possède pas de code mais peux possèder une nom
		else {
			identif = new IdentificateurVariable();
			if (structureDynamiqueXml.getNom() != null
					&& !structureDynamiqueXml.getNom().equals(""))
				identif.setNom(structureDynamiqueXml.getNom());
			descr.setM_AIdentificateurComposant(identif);
			descr.setTypeVariable(TypeVariable.STRUCTURE_DYNAMIQUE);
		}

		structureDyn.setDescripteur(descr);
		descr = fillDescriptorStructureDynamique(structureDynamiqueXml, descr);
		ajouterVariableTeteStructureDynamique(structureDynamiqueXml,
				structureDyn);
		// System.out.println("varaible entete: " +
		// structureDyn.getVariableEntete().getDescriptor().getM_AIdentificateurComposant().getNom());
		List<TableSousVariables> listeTableSousVar = structureDynamiqueXml
				.getTableSousVariablesList();
		// System.out.println("nom structure dynamique: " +
		// structureDyn.getDescriptor().getM_AIdentificateurComposant().getNom());
		for (TableSousVariables tableSousVarXml : listeTableSousVar) {
			com.faiveley.samng.principal.sm.data.variableComposant.TableSousVariable tableSousVariable = creerTableSousVariableVariableDynamique(tableSousVarXml);
			structureDyn.ajouterTableSousVariable(tableSousVariable);			
		}

		try {
			structureDyn.getVariableEntete().getDescriptor().setVolatil(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return structureDyn;
	}

	/**
	 * Méthode de création d'un tableau dynamique dynamique à partir de sa
	 * définition xml
	 * 
	 * @param tableauDynamiqueXml
	 * @return une instance de StructureDynamique
	 */
	private com.faiveley.samng.principal.sm.data.variableComposant.JRU.TableauDynamique creerTableauDynamique(
			TableauDynamique tableauDynamiqueXml) {
		com.faiveley.samng.principal.sm.data.variableComposant.JRU.TableauDynamique tableauDyn = new com.faiveley.samng.principal.sm.data.variableComposant.JRU.TableauDynamique();
		DescripteurTableauDynamique descr = new DescripteurTableauDynamique();

		// cas où le tableau dynamique est défini directement sous l'élément xml
		// liste-varaibles
		// dans ce cas il possède nom, code et isString
		if (tableauDynamiqueXml.getCode() != 0) {
			int varCode = tableauDynamiqueXml.getCode();
			descr = GestionnaireDescripteurs
					.getDescripteurTableauDynamique(varCode);
		}
		// cas où le tableau dynamique est défini en tant que sous-variable dans
		// un élément table-sous-variables
		// dans ce cas il ne possède pas de code mais peux possèder une nom
		else {

			IdentificateurVariable identif = new IdentificateurVariable();
			if (tableauDynamiqueXml.getNom() != null
					&& !tableauDynamiqueXml.getNom().equals(""))
				identif.setNom(tableauDynamiqueXml.getNom());
			descr.setM_AIdentificateurComposant(identif);
			descr.setTypeVariable(TypeVariable.TABLEAU_DYNAMIQUE);
		}

		tableauDyn.setDescripteur(descr);
		descr = fillDescriptorTableauDynamique(tableauDynamiqueXml, descr);
		// ajout de la variable d'entete
		ajouterVariableTeteTableauDynamique(tableauDynamiqueXml, tableauDyn);
		// System.out.println("varaible entete: " +
		// tableauDyn.getVariableEntete().getDescriptor().getM_AIdentificateurComposant().getNom());

		// valorisation de l'attribut chaine qui spécifie si le tableau
		// dynamique permet de lire une chaine de type X_TEXT
		// if(tableauDynamiqueXml.isSetIsString())
		// tableauDyn.setChaine(Boolean.parseBoolean(tableauDynamiqueXml.getIsString().toString()));
		List<TableSousVariables> listeTableSousVar = tableauDynamiqueXml
				.getTableSousVariablesList();
		for (TableSousVariables tableSousVarXml : listeTableSousVar) {
			com.faiveley.samng.principal.sm.data.variableComposant.TableSousVariable tableSousVariable = creerTableSousVariableVariableDynamique(tableSousVarXml);
			tableauDyn.ajouterTableSousVariable(tableSousVariable);
		}

		try {
			tableauDyn.getVariableEntete().getDescriptor().setVolatil(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tableauDyn;
	}

	/**
	 * Méthode de création d'une chaine dynamique dynamique à partir de sa
	 * définition xml
	 * 
	 * @param chaineDynamiqueXml
	 * @return une instance de ChaineDynamique
	 */
	private com.faiveley.samng.principal.sm.data.variableComposant.JRU.ChaineDynamique creerChaineDynamique(
			ChaineDynamique chaineDynamiqueXml) {
		com.faiveley.samng.principal.sm.data.variableComposant.JRU.ChaineDynamique tableauDyn = new com.faiveley.samng.principal.sm.data.variableComposant.JRU.ChaineDynamique();
		DescripteurChaineDynamique descr = new DescripteurChaineDynamique();
		// cas où le tableau dynamique est défini directement sous l'élément xml
		// liste-varaibles
		// dans ce cas il possède nom, code et isString
		if (chaineDynamiqueXml.getCode() != 0) {
			int varCode = chaineDynamiqueXml.getCode();
			descr = GestionnaireDescripteurs
					.getDescripteurChaineDynamique(varCode);
		}
		// cas où le tableau dynamique est défini en tant que sous-variable dans
		// un élément table-sous-variables
		// dans ce cas il ne possède pas de code mais peux possèder une nom
		else {
			IdentificateurVariable identif = new IdentificateurVariable();
			if (chaineDynamiqueXml.getNom() != null
					&& !chaineDynamiqueXml.getNom().equals(""))
				identif.setNom(chaineDynamiqueXml.getNom());
			descr.setM_AIdentificateurComposant(identif);
			descr.setTypeVariable(TypeVariable.CHAINE_DYNAMIQUE);
		}

		tableauDyn.setDescripteur(descr);
		descr = fillDescriptorChaineDynamique(chaineDynamiqueXml, descr);
		// ajout de la variable d'entete
		ajouterVariableTeteChaineDynamique(chaineDynamiqueXml, tableauDyn);
		// System.out.println("varaible entete: " +
		// tableauDyn.getVariableEntete().getDescriptor().getM_AIdentificateurComposant().getNom());

		// valorisation de l'attribut chaine qui spécifie si le tableau
		// dynamique permet de lire une chaine de type X_TEXT
		// if(tableauDynamiqueXml.isSetIsString())
		// tableauDyn.setChaine(Boolean.parseBoolean(tableauDynamiqueXml.getIsString().toString()));
		List<TableSousVariables> listeTableSousVar = chaineDynamiqueXml
				.getTableSousVariablesList();
		for (TableSousVariables tableSousVarXml : listeTableSousVar) {
			com.faiveley.samng.principal.sm.data.variableComposant.TableSousVariable tableSousVariable = creerTableSousVariableVariableDynamique(tableSousVarXml);
			tableauDyn.ajouterTableSousVariable(tableSousVariable);
		}

		try {
			tableauDyn.getVariableEntete().getDescriptor().setVolatil(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tableauDyn;
	}

	/**
	 * Méthode de cahrgement
	 * 
	 * @return
	 */
	public InfosParcours chargerInfosParcours() {

		InfosParcours informations = null;

		List<String> infos = new ArrayList<String>();
		try {
			for (Information info : this.infoParcours.getInformationList()) {
				infos.add(info.getNom());
			}
			if (infos.size() > 0)
				informations = new InfosParcours(infos);
		} catch (Exception ex) {
			informations = null;
		}

		return informations;
	}

	/**
	 * Méthode de chargement du modèle(défini le type de parseur à utiliser)
	 * 
	 * @return
	 */
	public String chargerType() {

		String type = null;

		try {
			type = this.enr.getType();
		} catch (Exception ex) {

		}

		return type;
	}

	/**
	 * Loads all the events from the xml
	 * 
	 * @return the list with all events
	 */
	public List<com.faiveley.samng.principal.sm.data.enregistrement.Evenement> loadAllEvents() {
		List<com.faiveley.samng.principal.sm.data.enregistrement.Evenement> events = new ArrayList<com.faiveley.samng.principal.sm.data.enregistrement.Evenement>();

		// return if xml not loaded
		if (!isRessourceLoaded())
			return events;
		// set containing the codes of the already loaded events
		// if a code already exists in this set, an exception will be thrown
		Set<Integer> eventsCodes = new HashSet<Integer>();
		int evCode;

		// convert all events from the format returned by parser in the Sam
		// format
		for (Evenement ev : this.enr.getListeEvenements().getEvenementList()) {
			com.faiveley.samng.principal.sm.data.enregistrement.Evenement e = new com.faiveley.samng.principal.sm.data.enregistrement.Evenement();
			evCode = getEvenementCode(ev);
			if (eventsCodes.contains(evCode)) {
				throw new DuplicateEventCodeException(
						"Blocking error. Duplicate event code found in XML. Event code = "
								+ evCode);
			}
			eventsCodes.add(evCode); // add the event code in the set of
			// codes

			DescripteurEvenement descr = GestionnaireDescripteurs
					.getDescripteurEvenement(getEvenementCode(ev));
			e.setM_ADescripteurComposant(descr);

			fillDescripteurEvenement(ev, descr);

			List<NomUtilisateur> usersList = null;
			try {
				usersList = ev.getNomUtilisateurList();
			} catch (Exception ex) {
			}
			e.setNomUtilisateur(setListOfUsers(usersList));

			try {

				switch (ev.getChangementHeure().intValue()) {
				case ChangementHeureImpl.INT_TRUE:
					e.setChangementHeure(true);
					break;

				case ChangementHeureImpl.INT_FALSE:
					e.setChangementHeure(false);
					break;
				}
			} catch (Exception ex) {
				e.setChangementHeure(false);
			}

			try {

				switch (ev.getRuptureAcquisition().intValue()) {
				case RuptureAcquisitionImpl.INT_TRUE:
					e.setRuptureAcquisition(true);
					break;

				case RuptureAcquisitionImpl.INT_FALSE:
					e.setRuptureAcquisition(false);
					break;
				}
			} catch (Exception ex) {
				e.setRuptureAcquisition(false);
			}

			try {
				switch (ev.getRazCompteurTemps().intValue()) {
				case RazCompteurTemps.INT_TRUE:
					e.setRazCompteurTemps(true);
					break;

				case RazCompteurTemps.INT_FALSE:
					e.setRazCompteurTemps(false);
					break;
				}

			} catch (Exception ex) {
				e.setRuptureAcquisition(false);
			}
			try {
				switch (ev.getRazCompteurDistance().intValue()) {
				case RazCompteurDistance.INT_TRUE:
					e.setRazCompteurDistance(true);
					break;

				case RazCompteurDistance.INT_FALSE:
					e.setRazCompteurDistance(false);
					break;
				}
			} catch (Exception ex) {
				e.setRuptureAcquisition(false);
			}
			try {
				switch (ev.getSynchroTempsDistance().intValue()) {
				case SynchroTempsDistance.INT_YES:
					e.setASychroniser(true);
					e.setReferenceSynchro(false);
					break;
				case SynchroTempsDistance.INT_NO:
					e.setASychroniser(false);
					e.setReferenceSynchro(false);
					break;
				case SynchroTempsDistance.INT_REF:
					e.setReferenceSynchro(true);
					e.setASychroniser(false);
					break;
				default:
					e.setASychroniser(false);
					e.setReferenceSynchro(false);
					break;
				}

			} catch (Exception ex) {
				e.setASychroniser(false);
				e.setReferenceSynchro(false);
			}
			events.add(e);

		}

		List<com.faiveley.samng.principal.sm.data.enregistrement.Evenement> cpEvt = events;
		Langage lng = Activator.getDefault().getCurrentLanguage();
		String nomUtilisateur;
		boolean isIdentique = false;
		for (com.faiveley.samng.principal.sm.data.enregistrement.Evenement evt : events) {
			nomUtilisateur = evt.getNomUtilisateur().getNomUtilisateur(lng);
			if (nomUtilisateur != null) {
				isIdentique = false;
				for (com.faiveley.samng.principal.sm.data.enregistrement.Evenement evt2 : cpEvt) {
					if (nomUtilisateur.equals(evt2.getNomUtilisateur()
							.getNomUtilisateur(lng)) && !evt2.equals(evt)) {
						evt2.getNomUtilisateur()
								.setNomUtilisateur(
										lng,
										"("
												+ evt2.getM_ADescripteurComposant()
														.getM_AIdentificateurComposant()
														.getNom()
												+ ") "
												+ evt2.getNomUtilisateur()
														.getNomUtilisateur(lng));
						isIdentique = true;
					}
				}

				if (isIdentique) {
					evt.getNomUtilisateur().setNomUtilisateur(
							lng,
							"("
									+ evt.getM_ADescripteurComposant()
											.getM_AIdentificateurComposant()
											.getNom()
									+ ") "
									+ evt.getNomUtilisateur()
											.getNomUtilisateur(lng));
				}
			}
		}
		for (com.faiveley.samng.principal.sm.data.enregistrement.Evenement evt : events) {

			nomUtilisateur = evt.getNomUtilisateur().getNomUtilisateur(lng);
			if (nomUtilisateur != null
					&& nomUtilisateur.equals("")
					&& evt.getM_ADescripteurComposant()
							.getM_AIdentificateurComposant().getNom() != null
					&& !evt.getM_ADescripteurComposant()
							.getM_AIdentificateurComposant().getNom()
							.equals("")) {

				evt.getNomUtilisateur().setNomUtilisateur(
						lng,
						"("
								+ evt.getM_ADescripteurComposant()
										.getM_AIdentificateurComposant()
										.getNom() + ") ");
			} else if (nomUtilisateur == null
					&& evt.getM_ADescripteurComposant()
							.getM_AIdentificateurComposant().getNom() != null
					&& !evt.getM_ADescripteurComposant()
							.getM_AIdentificateurComposant().getNom()
							.equals("")) {
				evt.getNomUtilisateur().setNomUtilisateur(
						lng,
						"("
								+ evt.getM_ADescripteurComposant()
										.getM_AIdentificateurComposant()
										.getNom() + ") ");
			}

		}

		return events;
	}

	/**
	 * Sets the list of labels for a descriptor
	 * 
	 * @param listTableLabels
	 *            list of labels
	 * @param descr
	 *            descriptor
	 */
	private void setLanguageLabels(List<ListeTablelabels> listTableLabels,
			DescripteurVariable descr) {
		TableValeurLabel langLabels = new TableValeurLabel();
		if (listTableLabels != null && listTableLabels.size() > 0) {
			for (ListeTablelabels langTabelLabel : listTableLabels) {
				// add the language and the list of labels for that langugae

				Langage lang = ParseurUtils.chargerLangage(langTabelLabel
						.getLang().intValue());

				ArrayList<LabelValeur> labels = new ArrayList<LabelValeur>();
				// get the labels
				for (TableLabels tabelLabels : langTabelLabel
						.getTableLabelsList()) {

					// add label for a language
					LabelValeur label = new LabelValeur();
					label.setLabel(tabelLabels.getLabel());
					label.setValeurs(tabelLabels.getValeur().trim());
					if (label.getLabel() == null) {
						LOGGER.warn(Messages
								.getString("errors.nonblocking.invalidLabelName"));
						continue;
					}
					if (label.getValeurs() == null) {
						LOGGER.warn(Messages
								.getString("errors.nonblocking.invalidLabelValues"));
						continue;
					}

					labels.add(label);
				}
				langLabels.put(lang, labels);
				// langLabels.setLanguage(lang);

			}

			if (descr.getTypeVariable() == TypeVariable.VAR_DISCRETE)
				((DescripteurVariableDiscrete) descr).setLabels(langLabels);
			else
				descr.add(langLabels);
		} else {
			// for a discrete variable
			if ((descr.getTypeVariable() == TypeVariable.VAR_DISCRETE)
					&& ((descr.getType() == Type.boolean1) || (descr.getType() == Type.boolean8))) {
				langLabels = new TableValeurLabel();
				Langage lang = Langage.DEF;
				ArrayList<LabelValeur> labels = new ArrayList<LabelValeur>(2);
				LabelValeur label = new LabelValeur();
				label.setLabel(Messages.getString("ParseurXML1.1"));
				label.setValeurs("0");
				labels.add(label);
				label = new LabelValeur();
				label.setLabel(Messages.getString("ParseurXML1.0"));
				label.setValeurs("1");
				labels.add(label);
				langLabels.put(lang, labels);
				((DescripteurVariableDiscrete) descr).setLabels(langLabels);

			}
		}
	}

	/**
	 * Loads the descriptor for a repere
	 * 
	 * @param repere
	 *            the repere
	 * @return thr descriptor
	 */
	private DescripteurVariable loadDescriptorForRepereName(String repere) {
		List<VariableDiscrete> variablesDiscretes = getVariablesDiscretes(enr);
		// search for repere which is a discrete variable
		for (VariableDiscrete varD : variablesDiscretes) {
			try {
				int varCode = getDiscreteVariableCode(varD);
				if ((varCode & 0x8000) != 0)
					throw new BadVariableCodeException(
							Messages.getString("errors.nonblocking.invalidVariableId1") + ": " + varD.getNom());
				if (repere.equals(varD.getNom())) {
					// found the repere then load it
					com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete v = new com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete();
					DescripteurVariableDiscrete descr = GestionnaireDescripteurs
							.getDescripteurVariableDiscrete(varCode);
					v.setDescripteur(descr);

					descr = fillDescriptorVariableDiscrete(varD, descr);
					v.setTypeValeur(v.getDescriptor().getType());
					this.varsNameId.put(varD.getNom(), varCode);
					return descr;
				}
			} catch (AExceptionSamNG ex) {
				ex.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * Gets the code of a discrete variable. If not found return 0
	 * 
	 * @param var
	 *            the variable
	 * @return the code
	 */
	private int getDiscreteVariableCode(VariableDiscrete var) {
		int varCode = 0;
		try {
			varCode = var.getCode();
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlVarCode"));
			varCode = 0;
		}
		if (varCode < 0 || varCode > 2147483647) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlVarCode1"));
			varCode = 0; // code unusable
		}
		return varCode;
	}

	/**
	 * Gets the cde for an analogic variable.If not found return 0
	 * 
	 * @param var
	 *            the variable
	 * @return the code
	 */
	private int getAnalogicVariableCode(VariableAnalogique var) {
		int varCode = 0;
		try {
			varCode = var.getCode();
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlVarCode"));
			varCode = 0;
		}
		if (varCode < 0 || varCode > 2147483647) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlVarCode1"));
			varCode = 0; // code unusable
		}
		return varCode;
	}

	/**
	 * Gets the code for a structure dynamique.If not found return 0
	 * 
	 * @param var
	 *            the variable
	 * @return the code
	 */
	private int getStructureDynamiqueVariableCode(StructureDynamique var) {
		int varCode = 0;
		try {
			varCode = var.getCode();
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlVarCode"));
			varCode = 0;
		}
		if (varCode < 0 || varCode > 2147483647) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlVarCode1"));
			varCode = 0; // code unusable
		}
		return varCode;
	}

	/**
	 * Gets the code for a tableau dynamique.If not found return 0
	 * 
	 * @param var
	 *            the variable
	 * @return the code
	 */
	private int getTableauDynamiqueVariableCode(TableauDynamique var) {
		int varCode = 0;
		try {
			varCode = var.getCode();
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlVarCode"));
			varCode = 0;
		}
		if (varCode < 0 || varCode > 2147483647) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlVarCode1"));
			varCode = 0; // code unusable
		}
		return varCode;
	}

	/**
	 * Gets the code for a complexe variable.If not found return 0
	 * 
	 * @param var
	 *            the variable
	 * @return the code
	 */
	private int getComplexVariableCode(VariableComplexe var) {
		int varCode = 0;
		try {
			varCode = var.getCode();
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlVarCode"));
			varCode = 0;
		}
		if (varCode < 0 || varCode > 2147483647) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlVarCode1"));
			varCode = 0; // code unusable
		}
		return varCode;
	}

	/**
	 * Gets the code for an event.If not found return 0
	 * 
	 * @param var
	 *            the variable
	 * @return the code
	 */
	private int getEvenementCode(Evenement ev) {
		int evCode = 0;
		try {
			evCode = ev.getCode();
		} catch (Exception e) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlVarCode"));
			evCode = 0;
		}
		if (evCode < 0 || evCode > 2147483647) {
			LOGGER.warn(Messages
					.getString("errors.nonblocking.invalidXmlVarCode1"));
			evCode = 0; // code unusable
		}
		return evCode;
	}

	/**
	 * Returns the name of the current loaded xml file
	 * 
	 * @return the name
	 */
	public String getXmlFileName() {
		return this.xmlFileName;
	}

	public static boolean fichierXmlAssocieValide(String fileName) {
		try {
			EnregistreurDefDocument.Factory.parse(new File(fileName));
			return true;
		} catch (XmlException e) {
			return false;

		} catch (IOException e) {
			return false;
		}

	}

	public InfosFichierSamNg getInfoData() {
		return infoData;
	}

	private static List<VariableAnalogique> getVariablesAnalogiques(
			Enregistreur enr) {
		List<VariableAnalogique> result = new ArrayList<VariableAnalogique>(0);
		for (ListeVariables lv : enr.getListeVariablesList()) {
			result.addAll(lv.getVariableAnalogiqueList());
		}
		return result;
	}

	private static List<VariableComplexe> getVariablesComplexes(Enregistreur enr) {
		List<VariableComplexe> result = new ArrayList<VariableComplexe>(0);
		for (ListeVariables lv : enr.getListeVariablesList()) {
			result.addAll(lv.getVariableComplexeList());
		}
		return result;
	}

	private static List<VariableDiscrete> getVariablesDiscretes(Enregistreur enr) {
		List<VariableDiscrete> result = new ArrayList<VariableDiscrete>(0);
		for (ListeVariables lv : enr.getListeVariablesList()) {
			result.addAll(lv.getVariableDiscreteList());
		}
		return result;
	}

	private static List<VariableComposee> getVariablesComposee(Enregistreur enr) {
		List<VariableComposee> result = new ArrayList<VariableComposee>(0);
		for (ListeVariables lv : enr.getListeVariablesList()) {
			result.addAll(lv.getVariableComposeeList());
		}
		return result;
	}

	private static List<StructureDynamique> getStructureDynamiques(
			Enregistreur enr) {
		List<StructureDynamique> result = new ArrayList<StructureDynamique>(0);
		for (ListeVariables lv : enr.getListeVariablesList()) {
			result.addAll(lv.getStructureDynamiqueList());
		}
		return result;
	}

	private static List<ChaineDynamique> getChaineDynamiques(Enregistreur enr) {
		List<ChaineDynamique> result = new ArrayList<ChaineDynamique>(0);
		for (ListeVariables lv : enr.getListeVariablesList()) {
			result.addAll(lv.getChaineDynamiqueList());
		}
		return result;
	}

	private static List<TableauDynamique> getTableauDynamiques(Enregistreur enr) {
		List<TableauDynamique> result = new ArrayList<TableauDynamique>(0);
		for (ListeVariables lv : enr.getListeVariablesList()) {
			result.addAll(lv.getTableauDynamiqueList());
		}
		return result;
	}

}
