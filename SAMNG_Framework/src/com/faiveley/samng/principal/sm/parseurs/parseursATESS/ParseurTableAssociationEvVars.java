package com.faiveley.samng.principal.sm.parseurs.parseursATESS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xmlbeans.XmlException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.sm.data.compression.DecompressedFile;
import com.faiveley.samng.principal.sm.data.descripteur.ADescripteurComposant;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurComposite;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurEvenement;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.identificateurComposant.IdentificateurVariable;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.erreurs.AExceptionSamNG;
import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurTableAssociationEvVarsATESSExplorer;
import com.faiveley.samng.principal.sm.parseconfigatess.ConfigurationAtess;
import com.faiveley.samng.principal.sm.parseconfigatess.EvenementVariables;
import com.faiveley.samng.principal.sm.parseconfigatess.Identifiant;
import com.faiveley.samng.principal.sm.parseconfigatess.ListeVariablesAtess;
import com.faiveley.samng.principal.sm.parseconfigatess.TableEvenementsVariables;
import com.faiveley.samng.principal.sm.parseconfigatess.VariableAtess;
import com.faiveley.samng.principal.sm.parseurs.IParseurInterface;
import com.faiveley.samng.principal.sm.parseurs.parseurXML.ParseurUtils;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.engins.DonneesFichierXMLEngin;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.engins.TypeEngin;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

public class ParseurTableAssociationEvVars implements IParseurInterface {
	private byte debData[];
	private byte finData[];
	private Integer intDebFin;
	private Integer intFinDeb;
	private File _monFile;
	private byte totalData[];
	private byte crcData[];

	public String fichierDescr;
	public List <Identifiant> idExtended;
	public int MaxCompteurTemps;
	public int MaxCompteurDistance;
	public String CRC;
	public ADescripteurComposant tableEvVars;
	public static ConfigurationAtess config;

	public static String fileName;
	public static DonneesFichierXMLEngin ficXML=new DonneesFichierXMLEngin();
	public static String fichierXML;

	private static ParseurTableAssociationEvVars instance = new ParseurTableAssociationEvVars();

	/**
	 * @return the only instance of the parser
	 */
	public static ParseurTableAssociationEvVars getInstance() {
		if (ActivationExplorer.getInstance().isActif()) {
			return ParseurTableAssociationEvVarsATESSExplorer.getInstance();
		}
		
		return instance;
	}

	public void initDatasDepuisFichier(String fileName) {
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
				
				if (!Arrays.equals(aux, "\"".getBytes())) {
					continue;
				}
				
				finData = new byte[totalData.length - i];
				System.arraycopy(totalData, i, finData, 0, finData.length);
				intDebFin = Integer.valueOf(i);
				break;
			}

			aux = null;
			tailleaux = "<signature CRC=\"".getBytes().length;
			
			for (int i = intDebFin.intValue(); i >= 0; i--) {
				aux = new byte[tailleaux];
				System.arraycopy(totalData, i, aux, 0, tailleaux);
				
				if (!Arrays.equals(aux, "<signature CRC=\"".getBytes())) {
					continue;
				}
				
				debData = new byte[i + tailleaux];
				System.arraycopy(totalData, 0, debData, 0, debData.length);
				intFinDeb = Integer.valueOf(i + tailleaux);
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

	public String chargerNomFichierXML(String file) throws AExceptionSamNG, XmlException, IOException {	
		String ConfigurationATESSxml = "ConfigurationATESS.xml";
		fichierDescr = "ATESS.xml";
		CRC = null;

		ConfigurationATESSxml = gererEngins(file);
		
		if (ConfigurationATESSxml == null) {
			ConfigurationATESSxml = "ConfigurationATESS.xml";
		}
		
		config = null;
		
		try {
			JAXBContext jc = JAXBContext.newInstance("com.faiveley.samng.principal.sm.parseconfigatess", getClass().getClassLoader());
			Unmarshaller unmarshaller = jc.createUnmarshaller();		
			config = (ConfigurationAtess) unmarshaller.unmarshal(new File(RepertoiresAdresses.xml + File.separator + ConfigurationATESSxml));
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		fichierDescr = config.getFichierDescription().getXmlAssocie();
		ParseurUtils.verifCRC(RepertoiresAdresses.xml + File.separator + ConfigurationATESSxml, Integer.parseInt(config.getSignature().getCRC(), 16));
		
		return ConfigurationATESSxml;
	}

	public String gererEngins(String file){
		lireFichierXMLFicParamEngin();
		
		if (ficXML == null) {
			System.out.println("La lecture du fichier FICPARAM_TYP_ENGIN.xml a �chou�");
			return null;
		}
		
		int selection_type_engin = ficXML.getSelection_type_engin();
		
		if (selection_type_engin == 0) {
			return null;
		} else if (selection_type_engin == 1){
			String ret = gererFichierAccompagnement(file);
			
			if (ret == null) {
				System.out.println("Erreur de lecture du fichier d'accompagnement");
				return null;
			} else {
				return ret;
			}
		} else {
			System.out.println("La valeur de selection_type_engin devrait �tre 0 ou 1");
			return null;
		}
	}

	public DonneesFichierXMLEngin lireFichierXMLFicParamEngin(){
		try {
			// Cr�ation d'une fabrique de documents
			DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();

			// Cr�ation d'un constructeur de documents
			DocumentBuilder constructeur = fabrique.newDocumentBuilder();

			// Lecture du contenu d'un fichier XML avec DOM
			File xml = new File(RepertoiresAdresses.FICPARAM_TYP_ENGIN_XML);
			Document document = constructeur.parse(xml);

			// Traitement du document
			Element racine = document.getDocumentElement();
			NodeList nodes = racine.getChildNodes();
			ficXML = new DonneesFichierXMLEngin();
			
			for (int i = 0; i < nodes.getLength(); i++){
				Node n = nodes.item(i);
				
				if(n.getNodeName().equals("selection_type_engin")) {
					int sel=0;
					try {
						sel=Integer.valueOf(((Element)n).getAttribute("valeur"));
					} catch (Exception e) {
						System.out.println("La valeur de selection_type_engin n'est pas un nombre");
					}
					
					ficXML.setSelection_type_engin(sel);
				} else if(n.getNodeName().equals("selection_engins")) {
					NodeList nodes_list = n.getChildNodes();
					TypeEngin typeEngin;
					
					for(int j = 0; j < nodes_list.getLength(); j++){
						typeEngin=new TypeEngin();
						
						try {
							Node node = nodes_list.item(j);
							typeEngin.setNom(((Element)node).getAttribute("nom"));
							typeEngin.setLibelle(((Element)node).getAttribute("libelle"));
							typeEngin.setNomFichierXML(((Element)node).getAttribute("nomFichierXML"));
							ficXML.getListeTypeEngins().add(typeEngin);
						} catch (RuntimeException e) {
							continue;
						}
					}
				}
			}
		} catch(ParserConfigurationException pce) {
			System.out.println("Erreur de configuration du parseur DOM");
			System.out.println("lors de l'appel � fabrique.newDocumentBuilder();");
		} catch(SAXException se) {
			System.out.println("Erreur lors du parsing du document");
			System.out.println("lors de l'appel � construteur.parse(xml)");
		} catch(IOException ioe) {
			System.out.println("Erreur d'entr�e/sortie");
			System.out.println("lors de l'appel � construteur.parse(xml)");
		}
		
		return ficXML;
	}

	public String gererFichierAccompagnement(String fileName){
		int cesure = fileName.lastIndexOf(File.separator);
		String chemin = fileName.substring(0, cesure);
		File repertoire = new File(chemin);

		if (repertoire.isDirectory()) {
			File[] list = repertoire.listFiles();
			
			if (list != null){
				for ( int i = 0; i < list.length; i++) {
					if(list[i].getAbsolutePath().toUpperCase().contains(fileName.toUpperCase())){
						String[] listS = list[i].getAbsolutePath().toUpperCase().split(File.separator + ".");
						
						if (listS.length>1 && listS[listS.length-2].equals("TYP")) {
							String typeEngin=listS[listS.length-1];
							String fichierXML = ficXML.getFichierXMLByTypeEngin(typeEngin);
							return fichierXML;
						}
					}
				} 
			} else {
				System.err.println(repertoire + " : Erreur de lecture.");
				return null;
			}
		}
		
		ParseurTableAssociationEvVars.fileName=fileName;

		Display.getDefault().syncExec(new Runnable(){
			public void run() {
				int cesure=ParseurTableAssociationEvVars.fileName.lastIndexOf(File.separator);
				String file=ParseurTableAssociationEvVars.fileName;

				ficXML.getListLibelles();

				MessageDialog mdi=new MessageDialog(Display.getCurrent().getActiveShell(), com.faiveley.samng.principal.sm.parseurs.parseursATESS.Messages.getString("questionEngin"), null, ParseurTableAssociationEvVars.fileName.substring(cesure+1,ParseurTableAssociationEvVars.fileName.length()), MessageDialog.QUESTION, ficXML.getListLibelles(), 0);
				int res = -1;
				res = mdi.open();

				String versionEngin = ficXML.getTypeEnginByLibelle(ficXML.getListLibelles()[res]);
				fichierXML = ficXML.getFichierXMLByTypeEngin(versionEngin);
				createTYP(file, versionEngin);
			}
		});
		
		return fichierXML;
	}

	public static void createTYP(String filename, String versionEngin){
		String chemin = filename;
		if (ActivatorData.getInstance().isCompressedFile()) {
			DecompressedFile df = ActivatorData.getInstance().getDecompressedFile();
			File f = new File(df.getCompressedFileName());
			chemin = new File(f.getParent(), df.getInnerFileName()).getAbsolutePath();
		}
		chemin += ".TYP." + versionEngin;
		FileWriter fw = null;
		
		try {
			fw = new FileWriter(chemin);					
			fw.close();
		} catch (Exception e) {
			System.out.println("erreur lors de la cr�ation du fichier d'accompagnement");
		}
	}

	public void parseRessource(String chemin,boolean explorer,int deb,int fin) throws AExceptionSamNG, XmlException, IOException {
		this.idExtended=new ArrayList<Identifiant>(0);
		this.MaxCompteurDistance=-1;
		this.MaxCompteurTemps=-1;
		this.tableEvVars=null;

		this.idExtended=config.getListeIdentifiantsEtendus().getIdentifiant();
		
		this.MaxCompteurDistance=Integer.valueOf(config.getListeMaximumCompteur().getCompteurDistance().getMax());
		this.MaxCompteurTemps=Integer.valueOf(config.getListeMaximumCompteur().getCompteurTemps().getMax());
		this.tableEvVars=chargerTableEvenementsVariables(config.getTableEvenementsVariables());
	}

	public void clear() {
		this.CRC = null;
		this.fichierDescr = null;
		this.idExtended = null;
		this.MaxCompteurDistance = -1;
		this.MaxCompteurTemps = -1;
		this.tableEvVars = null;
		fileName =null;
		debData = null;
		finData = null;
		intDebFin = null;
		intFinDeb = null;
		_monFile=null;
		totalData = null;
		crcData = null;
		ficXML = null;
		fichierXML = null;
	}

	private ADescripteurComposant chargerTableEvenementsVariables(TableEvenementsVariables tableEV){
		int nbEve=tableEV.getEvenementVariables().size();
		ADescripteurComposant descripteurTable = new DescripteurComposite();

		for (int i = 0; i < nbEve; i++) {
			ADescripteurComposant descComp = new DescripteurComposite();
			EvenementVariables eveVars=tableEV.getEvenementVariables().get(i);
			int codeEV = eveVars.getCodeEvt();

			DescripteurEvenement descEvt = (DescripteurEvenement)GestionnairePool.getInstance().getEvent(codeEV).getM_ADescripteurComposant();

			if (descEvt != null) {
				descEvt.setCode(codeEV);
				int longueur = eveVars.getLongueur();
				descEvt.setLongueur(longueur);
				descComp.ajouter(descEvt);
				ListeVariablesAtess listVarAtess=eveVars.getListeVariablesAtess().get(0);
				int nbVar = listVarAtess.getVariableAtess().size();
				
				for (int j = 0; j < nbVar; j++) {
					VariableAtess varATESS=listVarAtess.getVariableAtess().get(j);
					int codeVar = varATESS.getCodeVar();
					
					if (codeVar != 0 && codeVar != -1) {
						DescripteurVariable descVar = new DescripteurVariable();
						IdentificateurVariable identVar = new IdentificateurVariable();
						identVar.setCode(codeVar);
						identVar.setNom("" + codeVar);
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
							identVar.setNom("" + codeEntreeLogique);
							descVar.setM_AIdentificateurComposant(identVar);
							descComp.ajouter(descVar);
							descComp.setHasEntreeLogique(true);
							descComp.setValeurEntreeLogique(valeurEntreeLogique);
						}
					}
				} catch (Exception e) {}
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

	public List<Identifiant> getIdExtended() {
		return idExtended;
	}

	public void setIdExtended(List<Identifiant> idExtended) {
		this.idExtended = idExtended;
	}
}
