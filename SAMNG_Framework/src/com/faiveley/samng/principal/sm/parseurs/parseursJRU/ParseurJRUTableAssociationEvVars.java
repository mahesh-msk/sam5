package com.faiveley.samng.principal.sm.parseurs.parseursJRU;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.apache.xmlbeans.XmlException;

import xmlbeans.jru.tableEvtVar.ConfigurationJruDocument;
import xmlbeans.jru.tableEvtVar.ConfigurationJruDocument.ConfigurationJru;
import xmlbeans.jru.tableEvtVar.EvenementVariablesDocument.EvenementVariables;
import xmlbeans.jru.tableEvtVar.ListeVariablesJruDocument.ListeVariablesJru;
import xmlbeans.jru.tableEvtVar.TableEvenementsVariablesDocument.TableEvenementsVariables;
import xmlbeans.jru.tableEvtVar.VariableJruDocument.VariableJru;

import com.faiveley.samng.principal.sm.data.descripteur.ADescripteurComposant;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurComposite;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurEvenement;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.identificateurComposant.IdentificateurVariable;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.erreurs.AExceptionSamNG;
import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurJRUTableAssociationEvVarsExplorer;
import com.faiveley.samng.principal.sm.parseurs.IParseurInterface;
import com.faiveley.samng.principal.sm.parseurs.parseurXML.ParseurUtils;

public class ParseurJRUTableAssociationEvVars implements IParseurInterface {

	private byte debData[];
	private byte finData[];
	private Integer intDebFin;
	private Integer intFinDeb;
	private File _monFile;
	private byte totalData[];
	private byte crcData[];

	private String fichierDescr;
	private int MaxCompteurTemps;
	private int MaxCompteurDistance;
	private String CRC;
	private ADescripteurComposant tableEvVars;
	private ConfigurationJruDocument configJRU;


	private static ParseurJRUTableAssociationEvVars instance = new ParseurJRUTableAssociationEvVars();

	/**
	 * @return the only instance of the parser
	 */
	public static ParseurJRUTableAssociationEvVars getInstance() {
		if (ActivationExplorer.getInstance().isActif()) {
			return ParseurJRUTableAssociationEvVarsExplorer.getInstance();
		}
		return instance;
	}

	private void initDatasDepuisFichier(String fileName) {
		intDebFin = null;
		intFinDeb = null;
		_monFile = new File(fileName);

		try {
			FileInputStream fis = new FileInputStream(_monFile);
			totalData = new byte[fis.available()];
			fis.read(totalData, 0, totalData.length);
			fis.close();
			int tailleaux = "\"".getBytes().length;
			byte aux[];
			for (int i = totalData.length - 1 - ("\"".getBytes().length - 1); i >= 0; i--) {
				aux = new byte[tailleaux];
				System.arraycopy(totalData, i, aux, 0, tailleaux);
				if (!Arrays.equals(aux, "\"".getBytes()))
					continue;
				finData = new byte[totalData.length - i];
				System.arraycopy(totalData, i, finData, 0, finData.length);
				intDebFin = new Integer(i);
				break;
			}

			aux = null;
			tailleaux = "<signature CRC=\"".getBytes().length;
			for (int i = intDebFin.intValue(); i >= 0; i--) {
				aux = new byte[tailleaux];
				System.arraycopy(totalData, i, aux, 0, tailleaux);
				if (!Arrays.equals(aux, "<signature CRC=\"".getBytes()))
					continue;
				debData = new byte[i + tailleaux];
				System.arraycopy(totalData, 0, debData, 0, debData.length);
				intFinDeb = new Integer(i + tailleaux);
				break;
			}

			if (intFinDeb != null) {
				crcData = new byte[intDebFin.intValue() - intFinDeb.intValue()];
				System.arraycopy(totalData, intFinDeb.intValue(), crcData, 0,
						crcData.length);
				return;
			}
		} catch (FileNotFoundException ef) {
			System.out.println("fichier introuvable");
		} catch (IOException e) {
			System.out.println(e + "erreur lors de la lecture du fichier");
		}
	}

	public void chargerNomFichierXML(String chemin) throws AExceptionSamNG, XmlException, IOException {	
		fichierDescr = null;
		CRC = null;

		configJRU = ConfigurationJruDocument.Factory.parse(new File(chemin));
		ConfigurationJru config=configJRU.getConfigurationJru();

		CRC = config.getSignature().getCRC();

		ParseurUtils.verifCRC(chemin, Integer.parseInt(config.getSignature().getCRC(), 16));
		
		fichierDescr=config.getFichierDescription().getXmlAssocie();
	}

	public void parseRessource(String chemin,boolean explorer,int deb,int fin) 
			throws AExceptionSamNG, XmlException, IOException {
		this.MaxCompteurDistance=-1;
		this.MaxCompteurTemps=-1;
		this.tableEvVars=null;

		ConfigurationJru config=configJRU.getConfigurationJru();

//		this.MaxCompteurDistance=Integer.valueOf(config.getListeMaximumCompteur().getCompteurDistance().getMax());
//		this.MaxCompteurTemps=Integer.valueOf(config.getListeMaximumCompteur().getCompteurTemps().getMax());
		this.tableEvVars=chargerTableEvenementsVariables(config.getTableEvenementsVariables());
	}

	public void clear() {
		this.CRC=null;
		this.fichierDescr=null;
		this.MaxCompteurDistance=-1;
		this.MaxCompteurTemps=-1;
		this.tableEvVars=null;

		debData = null;
		finData = null;
		intDebFin = null;
		intFinDeb = null;
		_monFile=null;
		totalData = null;
		crcData = null;
		fichierDescr=null;
		configJRU=null;
	}

	private ADescripteurComposant chargerTableEvenementsVariables(TableEvenementsVariables tableEV){

		int nbEve=tableEV.getEvenementVariablesList().size();
		ADescripteurComposant descripteurTable = new DescripteurComposite();

		for (int i = 0; i < nbEve; i++) {
			ADescripteurComposant descComp = new DescripteurComposite();
			EvenementVariables eveVars=tableEV.getEvenementVariablesList().get(i);
			int codeEV=eveVars.getCodeEvt();

			DescripteurEvenement descEvt = (DescripteurEvenement)GestionnairePool.getInstance().getEvent(codeEV).getM_ADescripteurComposant();

			if (descEvt != null) {
				descEvt.setCode(codeEV);
				int longueur=eveVars.getLongueur();
				descEvt.setLongueur(longueur);
				descComp.ajouter(descEvt);
				ListeVariablesJru listVarJru=eveVars.getListeVariablesJruArray(0);
				int nbVar=listVarJru.sizeOfVariableJruArray();
				for (int j = 0; j < nbVar; j++) {
					VariableJru varJru=listVarJru.getVariableJruArray(j);
					int codeVar=varJru.getCodeVar();
					if (codeVar != 0 && codeVar != -1) {
//						AVariableComposant varCourante = GestionnairePool.getVariable(codeVar);
						DescripteurVariable descVar = new DescripteurVariable();
						IdentificateurVariable identVar = new IdentificateurVariable();
						identVar.setCode(codeVar);
						identVar.setNom(""+codeVar);//ask Olivier modif
						descVar.setM_AIdentificateurComposant(identVar);
						descComp.ajouter(descVar);
					}
				}

				try {
					int codeEntreeLogique=eveVars.getEntreeLogique().getCodeEntreeLogique();
					boolean valeurEntreeLogique=eveVars.getEntreeLogique().getValeurEntreeLogique().toString().equals("true");

					if (codeEntreeLogique != 0 && codeEntreeLogique != -1) {
						AVariableComposant varCourante = GestionnairePool.getInstance().getVariable(codeEntreeLogique);

						if (varCourante != null) {
							DescripteurVariable descVar = new DescripteurVariable();
							IdentificateurVariable identVar = new IdentificateurVariable();
							identVar.setCode(codeEntreeLogique);
							identVar.setNom(""+codeEntreeLogique);//ask Olivier modif
							descVar.setM_AIdentificateurComposant(identVar);
							descComp.ajouter(descVar);
							descComp.setHasEntreeLogique(true);
							descComp.setValeurEntreeLogique(valeurEntreeLogique);
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			descripteurTable.ajouter(descComp);
		}
		return descripteurTable;
	}

	public String getCRC() {
		return CRC;
	}

	public void setCRC(String crc) {
		CRC = crc;
	}

	public String getFichierDescr() {
		return fichierDescr;
	}

	public void setFichierDescr(String fichierDescr) {
		this.fichierDescr = fichierDescr;
	}

	public int getMaxCompteurDistance() {
		return MaxCompteurDistance;
	}

	public void setMaxCompteurDistance(int maxCompteurDistance) {
		MaxCompteurDistance = maxCompteurDistance;
	}

	public int getMaxCompteurTemps() {
		return MaxCompteurTemps;
	}

	public void setMaxCompteurTemps(int maxCompteurTemps) {
		MaxCompteurTemps = maxCompteurTemps;
	}

	public ADescripteurComposant getTableEvVars() {
		return tableEvVars;
	}

	public void setTableEvVars(ADescripteurComposant tableEvVars) {
		this.tableEvVars = tableEvVars;
	}
}

