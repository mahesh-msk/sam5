package com.faiveley.samng.principal.sm.parseurs.parseursATESS;

import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.codeFF;
import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.tailleBlocData;
import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.tailleEntete;
import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.tailleTableEvenement;
import static com.faiveley.samng.principal.sm.parseurs.parseursATESS.ConstantesParcoursATESS.maxCptDistance;
import static com.faiveley.samng.principal.sm.parseurs.parseursATESS.ConstantesParcoursATESS.maxCptTps;
import static com.faiveley.samng.principal.sm.parseurs.parseursATESS.ConstantesParcoursATESS.pasCptDistance;
import static com.faiveley.samng.principal.sm.parseurs.parseursATESS.ConstantesParcoursATESS.pasCptTps;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.faiveley.kvbdecoder.decoder.KVBDecoder;
import com.faiveley.kvbdecoder.decoder.KVBDecoderResult;
import com.faiveley.kvbdecoder.exception.xml.XMLException;
import com.faiveley.kvbdecoder.exception.xml.XMLFileMissingException;
import com.faiveley.kvbdecoder.model.kvb.ip.InformationPoint;
import com.faiveley.kvbdecoder.model.kvb.marker.Marker;
import com.faiveley.kvbdecoder.model.kvb.marker.Marker.MarkerValue;
import com.faiveley.kvbdecoder.model.kvb.marker.MarkerX3X6X9;
import com.faiveley.kvbdecoder.model.kvb.train.TrainDirectionEnum;
import com.faiveley.kvbdecoder.services.decoder.DecoderService;
import com.faiveley.kvbdecoder.services.json.JSONService;
import com.faiveley.kvbdecoder.services.loader.KVBLoaderService;
import com.faiveley.kvbdecoder.services.message.MessageService;
import com.faiveley.kvbdecoder.services.xml.sam.CRC16Xml.CrcValue;
import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.preferences.PreferenceConstants;
import com.faiveley.samng.principal.logging.SamngLogger;
import com.faiveley.samng.principal.sm.calculs.conversionTemps.ConversionTempsAtess;
import com.faiveley.samng.principal.sm.controles.Checksum;
import com.faiveley.samng.principal.sm.controles.IStrategieControle;
import com.faiveley.samng.principal.sm.controles.util.CRC16CCITTHash;
import com.faiveley.samng.principal.sm.conversionCodage.ConversionBase;
import com.faiveley.samng.principal.sm.data.compression.DecompressedFile;
import com.faiveley.samng.principal.sm.data.descripteur.ADescripteurComposant;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurCodeBloc;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurComposite;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurEvenement;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.descripteur.Poids;
import com.faiveley.samng.principal.sm.data.descripteur.Temporelle;
import com.faiveley.samng.principal.sm.data.descripteur.Type;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.donneeBlocComposant.ADonneeBlocComposant;
import com.faiveley.samng.principal.sm.data.enregistrement.ErrorType;
import com.faiveley.samng.principal.sm.data.enregistrement.Evenement;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.enregistrement.atess.AtessMessage;
import com.faiveley.samng.principal.sm.data.enregistrement.tom4.GestionnaireSynchronisationGroupes;
import com.faiveley.samng.principal.sm.data.flag.Flag;
import com.faiveley.samng.principal.sm.data.flag.GestionnaireFlags;
import com.faiveley.samng.principal.sm.data.identificateurComposant.IdentificateurEvenement;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.AParcoursComposant;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosFichierSamNg;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TableLangueNomUtilisateur;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.Langage;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete;
import com.faiveley.samng.principal.sm.erreurs.AExceptionSamNG;
import com.faiveley.samng.principal.sm.erreurs.BadArgumentInFileException;
import com.faiveley.samng.principal.sm.erreurs.BadEventCodeException;
import com.faiveley.samng.principal.sm.erreurs.BadFileLengthException;
import com.faiveley.samng.principal.sm.erreurs.BadHeaderInfoException;
import com.faiveley.samng.principal.sm.erreurs.BadStructureFileException;
import com.faiveley.samng.principal.sm.erreurs.InconsistentFileException;
import com.faiveley.samng.principal.sm.erreurs.ParseurBinaireException;
import com.faiveley.samng.principal.sm.erreurs.ParseurXMLException;
import com.faiveley.samng.principal.sm.fabriques.AFabriqueParcoursAbstraite;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.formats.DistinctionExtension;
import com.faiveley.samng.principal.sm.formats.FormatSAM;
import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurParcoursAtessExplorer;
import com.faiveley.samng.principal.sm.parseconfigatess.Identifiant;
import com.faiveley.samng.principal.sm.parseurs.AParseurParcours;
import com.faiveley.samng.principal.sm.parseurs.Messages;
import com.faiveley.samng.principal.sm.parseurs.ParseurFlags;
import com.faiveley.samng.principal.sm.parseurs.adapteur.adapteur.ParseurAdapteur;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.principal.sm.segments.SegmentTemps;
import com.faiveley.samng.principal.sm.segments.TableSegments;
import com.faiveley.samng.principal.sm.segments.ruptures.TableRuptures;
import com.faiveley.samng.principal.sm.segments.ruptures.TypeRupture;
import com.faiveley.samng.principal.sm.util.ComputingUtils;
import com.faiveley.samng.principal.sm.util.ComputingUtilsAccumulatedDistanceInitParams;

public class ParseurParcoursAtess extends AParseurParcours {
	/**
	 * Private constructor. Just in the class is created the only instance of the parser
	 */
	
	private final static Short[][] SENS_MARCHE_SEQUENCES = {{1, 14}, {1, 77}, {1, 2}, {1, 3, 4}, {1, 3, 14}, {1, 3, 77}};
	private final static Short[][] CONTRE_SENS_SEQUENCES = {{14, 1}, {77, 1}, {2, 1}, {4, 3, 1}, {14, 3, 1}, {77, 3, 1}};
	
	int cpt = 0;
	double incRazDist;
	double lastRaz = 0;

    private ComputingUtilsAccumulatedDistanceInitParams params = null;
    private LinkedList<DelayVitesseLimiteKVB> delayVitesseLimiteKVBFifo = new LinkedList<DelayVitesseLimiteKVB>(){

		private static final long serialVersionUID = 1138431204115054347L;

		public boolean add(DelayVitesseLimiteKVB dVLKVB) {
            super.add(dVLKVB);
            Collections.sort(delayVitesseLimiteKVBFifo, new Comparator<DelayVitesseLimiteKVB>(){
            @Override 
            public int compare(DelayVitesseLimiteKVB dVLKVB1, DelayVitesseLimiteKVB dVLKVB2) {
            	int ret = 0;
            	
            	if (dVLKVB1.getDistanceApplicationVitesseLimiteKVB() > dVLKVB2.getDistanceApplicationVitesseLimiteKVB()) {
            		ret = 1;
            	} else if (dVLKVB1.getDistanceApplicationVitesseLimiteKVB() < dVLKVB2.getDistanceApplicationVitesseLimiteKVB()) {
            		ret = -1;
            	}
            	
            	return ret;
            } } );            	
            
            return true;
       }
   };

	double date;
	double dateBeforeChange;

	long time;
	long timeBeforeChange;
	long timeCountBeforeChange;

	double timeCount;

	double newDistance;
	double diametreRoue;

	boolean dateTimeChanged;
	boolean timeCountChanged;
	boolean distanceChanged;

	boolean messageSansTemps = false;
	int firstMsgSansTemps = 0;
	
	boolean kvbLoaded = false;
	
	private List<Short> sequenceBalisesBS1;
	private Long odometrieCourante = -1L;
    private List<AtessMessage> balisesVitesseLimiteKVBQueue;
	
	protected ParseurParcoursAtess() {}

	public static ParseurParcoursAtess getInstance() {
		try {
			if(instance == null) {
				instance = new ParseurParcoursAtess();
			}
		
			if (ActivationExplorer.getInstance().isActif()) {
				return ParseurParcoursAtessExplorer.getInstance();
			}
			
			return (ParseurParcoursAtess) instance;
		} catch (ClassCastException e) {
			if (ActivationExplorer.getInstance().isActif()) {
				return new ParseurParcoursAtessExplorer();
			}
			return new ParseurParcoursAtess();
		}
	}

	@Override
	public void parseRessource(String fileName, boolean explorer, int deb, int fin) throws AExceptionSamNG {		
		try {
			kvbLoaded = true;
			
			// Set Language of KVB Decoder service
			MessageService.getServiceInstance().setServiceLanguage(Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.LANG_CHOICE));
			
			String xmlLoaderPath = String.format("{'%s':'%s'}", JSONService.JSON_XMLLOADER_PATH_LABEL, RepertoiresAdresses.xml.replace("\\", "/"));
			CrcValue[] crcValues = KVBDecoder.getDecoderInstance().loadXml(xmlLoaderPath, false);
		
			if (crcValues != null) {
				for (int i = 0; i < crcValues.length; i++) {
					CrcValue crcValue = crcValues[i];
					
					if (crcValue != null && !crcValue.isCheckedCRC()) {
						String errorMsg = String.format(Messages.getString("errors.blocking.badXmlCrc"), crcValue.getFileName());
						SamngLogger.getLogger().warn(errorMsg);
					}
				}
			}
		} catch (XMLException e) {
			kvbLoaded = false;
			
			if (!(e instanceof XMLFileMissingException)) {
				SamngLogger.getLogger().error(e.getMsg());
			}
		}
		
		resetCumulsTemps();

		this.derniereValeurCptTemps = 0;
		this.derniereValeurHeure = 0;
		this.derniereValeurDate = 0;
		this.lastCptTempsTimeChange = 0;
		this.derniereValeurCptDistance = 0;
		this.cumulDistance = 0;
		this.cumulDistanceMax = 0;
		this.lastMessage = null;
		this.lastSegDistance = null;
		this.lastSegTemp = null;
		this.premiereDateRencontree = false;

		TableSegments.getInstance().empty();
		TableRuptures.getInstance().clear();
		
		AFabriqueParcoursAbstraite factory = FabriqueParcours.getInstance();
		
		fixerOctetsVides(fileName.substring(fileName.length() - 3, fileName.length()));
		this.nomFichierBinaire = fileName;
		traitementFichier(factory, fileName,explorer);
	
		if (explorer) {
			chargerDataExplore();
		} else {
			Message[][] messages;
			messages = chargerData(deb,fin);
			
			factory.creerData(messages);
			factory.creerReperes(GestionnairePool.getInstance().getReperes());
		}
	}

	private static String getNomTableEvenementVariable() {
		return (RepertoiresAdresses.xml + File.separator + "ConfigurationATESS.xml");
	}

	protected void chargerConstantes() {
		ConstantesParcoursATESS.tailleBlocData = 32;
		ConstantesParcoursATESS.resolutionTemps = 1;
		ConstantesParcoursATESS.pasCptTps = 1;
		pasCptTps = 1;
		pasCptDistance = 1;
	}

	@Override
	protected void traitementFichier(AFabriqueParcoursAbstraite factory, String fileName, boolean explorer) throws AExceptionSamNG {
		resetParser();
		
		this.tc = new ConversionTempsAtess("01/01/1970 00:00:00.000");

		// Check if the file is ok
		if (fileName == null || fileName.length() == 0 || !new File(fileName).exists()) {
			throw new IllegalArgumentException("No resource to parse");
		}
		
		//  Check the extension file
		List <String> listeExtensionsATESS=new ArrayList <String> (FormatSAM.ATESS.getExtensions());
		String extensionFichier=fileName.substring(fileName.length()-4,fileName.length());
		
		if (!DistinctionExtension.isExtensionOF(listeExtensionsATESS, extensionFichier)) {
			throw new IllegalArgumentException(Messages.getString("errors.blocking.invalidFileExtension"));
		}
		
		File binaryFile = new File(fileName);

		this.message = loadBinaryFile(binaryFile, 0, -1);
		
		if (this.message == null) {
			throw new IllegalArgumentException("No resource to parse");
		}
		
		if (this.message.length < 4) {
			throw new BadFileLengthException(Messages.getString("errors.blocking.invalidHeaderLength1"));
		}

		// Create the parcours
		factory.creerParcours();
		factory.creerEntete(null, null);

		chargerConstantes();

		// ************* PARSE FLAGS file **********/

		ParseurFlags.getInstance().parseRessource(RepertoiresAdresses.FLAGS_FILE_DIR + "flags.xml", false, 0, -1);
		this.loadedFlags = ParseurFlags.getInstance().chargerFlags();

		String nomTableAssocciationEvtVars=getNomTableEvenementVariable();
		
		try {
			String xmlFile = fileName;
			if (ActivatorData.getInstance().isCompressedFile()) {
				DecompressedFile df = ActivatorData.getInstance().getDecompressedFile();
				File f = new File(df.getCompressedFileName());
				xmlFile = new File(f.getParent(), df.getInnerFileName()).getAbsolutePath();
			}
			nomTableAssocciationEvtVars=ParseurTableAssociationEvVars.getInstance().chargerNomFichierXML(xmlFile);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ParseurXMLException(Messages.getString("ChargeurParcours.9"), true);
		}

		String nomFichierXML = ParseurTableAssociationEvVars.getInstance().getFichierDescr();
		GestionnairePool.getInstance().chargerFichierXml(RepertoiresAdresses.xml + File.separator + nomFichierXML, fileName);


		// Chargement de la table evt/var
		try {
			ParseurTableAssociationEvVars.getInstance().parseRessource(nomTableAssocciationEvtVars, false, 0, -1);
		} catch (Exception e) {
			System.out.println("ParseurTableAssociationEvVars a échoué");
		}
		
		ADescripteurComposant descripteursEvt = ParseurTableAssociationEvVars.getInstance().getTableEvVars();

		// Create the table
		factory.creerTableEvtVar(descripteursEvt);

		// Affichage ordonné des variables
		DescripteurComposite descrComposite = (DescripteurComposite)descripteursEvt;
		HashMap<Integer, DescripteurComposite> hashMapCodeDescComp = new HashMap<Integer, DescripteurComposite>();
		
		for (int o = 0; o < descrComposite.getLength(); o++) {
			hashMapCodeDescComp.put((Integer)descrComposite.getEnfant(o).getEnfant(0).getM_AIdentificateurComposant().getCode(), (DescripteurComposite) descrComposite.getEnfant(o));
		}
		
		GestionnaireDescripteurs.setMapEvenementVariables(hashMapCodeDescComp);
		
		// ********* LOAD Info ******************
		InfosFichierSamNg info = (InfosFichierSamNg) GestionnairePool.getInstance().getXMLParser().getInfosFichier();
		
		if (info == null) {
			throw new ParseurXMLException(Messages.getString("errors.blocking.errorLoadingXMLFile"), true);
		}
		info.setNomFichierParcoursBinaire(binaryFile.getAbsolutePath());
		info.setNomFichierXml(nomFichierXML);
		factory.creerInfoFichier(info);

		maxCptTps = (short) ParseurTableAssociationEvVars.getInstance().getMaxCompteurTemps();
		maxCptDistance = (short) ParseurTableAssociationEvVars.getInstance().getMaxCompteurDistance();
	}

	@Override
	protected ADonneeBlocComposant chargerCRCConfiguration() {
		return null;
	}

	protected boolean octetNonVide(int pos,byte[] messages,int cpt){
		if (this.message[pos] != FIN_CODE) {
			return true;
		}else{
			int octetsRestants=tailleBlocData-2-cpt+1;
			for (int i = 0; i < octetsRestants; i++) {
				if (this.message[pos+i]!= FIN_CODE) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	protected Message[][] chargerData(int deb, int fin) throws ParseurBinaireException {
		crcEnregistrements = new HashMap<Integer, Boolean>();
		this.crc = new CRC16CCITTHash();
		List<Message> goodMsgs = new ArrayList<Message>();
		List<Message> badMsgs = new ArrayList<Message>();
		lastMessage = null;
		
		boolean vLKAllowed = VitesseLimiteKVBService.isTableKVBXMLexist();

		sequenceBalisesBS1 = new ArrayList<Short>();
		balisesVitesseLimiteKVBQueue = new ArrayList<AtessMessage>();

		vitesseLimiteKVB.getInstance().clear();
		delayVitesseLimiteKVBFifo.clear();
		
		descrTable = (DescripteurComposite) ParseurTableAssociationEvVars.getInstance().getTableEvVars();

		// on défini la position de lecture de l'octet à l'octet de début du premier enregistrement
		int pos = 0;
		
		int tailleTableauMessage = this.message.length;
		
		if (fin != -1 && fin != 0) {
			tailleTableauMessage = fin;
		}
		
		if (deb != 0) {
			pos = deb;
		}

		// Lorsque l'on atteint la fin du fichier, peu importe ce qui a été lu on doit s'arreter
		if (pos >= tailleTableauMessage) {
			SamngLogger.getLogger().error(Messages.getString("errors.blocking.invalidFileStructure1"));
			ActivatorData.getInstance().getPoolDonneesVues().put("fichierVide", new String("true"));
			throw new BadFileLengthException(Messages.getString("errors.blocking.invalidFileStructure1"));
		}

		// Booléen arretant la lecture du fichier
		boolean finLecture = false;
		
		// Booléen indiquant si la variable H Date/Heure a encore été rencontrée dans le parcours
		boolean dateAlreadyMet = false;
		
		// La longueur de chaque enregistrement doit etre lu dans un champs de l'entete:ConstantesParcoursBinaire.tailleBlocDataFp
		// Compteur permettant de s'arreter lorsque l'on a atteint la fin de l'enregistrement
		// Le compteur va de 1 à ConstantesParcoursBinaire.tailleBlocData - 2
		int cpt = 1;
		int cptBlocs = 1;
		int posDebutEnreg = pos;

		// Calcul CRC premier enregistrement
		int crc = 0;
		byte[] crcEnreg = new byte[2];
		boolean crcEnregValide = false;
		
		// On récupère la valeur du crc
		crcEnreg = Arrays.copyOfRange(this.message, posDebutEnreg+ tailleBlocData - 2, posDebutEnreg + tailleBlocData);
		crc = new BigInteger(crcEnreg).intValue();
		crcEnregValide = verifierCRCBloc(crc, Arrays.copyOfRange(this.message,posDebutEnreg, posDebutEnreg + tailleBlocData - 2));
		crcEnregistrements.put(cptBlocs, crcEnregValide);

		AtessMessage trainKVBMessage = null;

		while (!finLecture && !Thread.interrupted()){
			ActivatorData.getInstance().getVp().setValeurProgressBar(pos*100/(tailleTableauMessage));
			ActivatorData.getInstance().getVpExportExplorer().setValeurProgressBar(pos,tailleTableauMessage);

			// Si on arrive au niveau du crc de l'enregistrement on doit aller à l'enregistrement suivant
			if (cpt > tailleBlocData - 2) {
				if((tailleBlocData - cpt + 1 + pos) >= tailleTableauMessage - 1) {
					finLecture = true;
				} else {
					cptBlocs++;
					posDebutEnreg += tailleBlocData;
					pos += (tailleBlocData - cpt + 1);
					cpt = 1;

					// À chaque nouvel enregistrement on calcul le CRC on récupère la valeur du CRC
					crcEnreg = Arrays.copyOfRange(this.message,posDebutEnreg+tailleBlocData - 2, posDebutEnreg+tailleBlocData);
					crc = new BigInteger(crcEnreg).intValue();

					crcEnregValide = verifierCRCBloc(crc, Arrays.copyOfRange(this.message, posDebutEnreg, posDebutEnreg + tailleBlocData - 2));
					crcEnregistrements.put(cptBlocs, crcEnregValide);
				}
			}

			while (cpt <= tailleBlocData - 2 && !finLecture) {				
				ActivatorData.getInstance().getVp().setValeurProgressBar(pos * 100 / (tailleTableauMessage));
				ActivatorData.getInstance().getVpExportExplorer().setValeurProgressBar(pos, tailleTableauMessage);

				// If the blocks structure is ok load the message
				AtessMessage msg = new AtessMessage();
				
				// On ne commence à lire un message que si l'octet a une valeur différente de -1
				if (pos < tailleTableauMessage) {
					if (octetNonVide(pos, this.message, cpt))	{
						// Chargement du message
						try {
							msg = chargerMessage(pos, false);
							msg.setOffsetDebut(pos);
							msg.setOffsetFin(pos + msg.getLongueur());
							
							msg.setMessageData(Arrays.copyOfRange(this.message, pos, pos + msg.getLongueur()));
							
							this.tc.getFormatedTime();
							// Changement de la position de lecture
							pos += msg.getLongueur();
							
							// Incrémentation du compteur
							cpt += msg.getLongueur();
							
							if (!crcEnregValide) {
								msg.setError(ErrorType.CRC);
								String messageCRC = "" + Checksum.getCRC(Arrays.copyOfRange(this.message, posDebutEnreg, posDebutEnreg + tailleBlocData - 2));
								String calculCRC = (crc >> 8 & 0xFF) + "";
								SamngLogger.getLogger().warn(Messages.getString("errors.nonblocking.badMsgCrc") + " : " + calculCRC+ "; " + Messages.getString("errors.nonblocking.blockStart1") + " "  + (goodMsgs.size() + 1)  + "; " + "CRC enregistré:" + " " + messageCRC);
								badMsgs.add(msg);
							}
						} catch (ParseurBinaireException ex) {
							pos += tailleBlocData - cpt + 1;
							cpt = tailleBlocData;
						}						
					} else {
						// Lorsque l'on rencontre un octet égal à -1 on incrémente la position et le compteur de 1
						pos++;
						cpt++;
					}

					if (msg.getEvenement() != null) {						
						goodMsgs.add(msg);
						long refCpt = 0;
						long refTemps = 0;
						long cumulCpt = 0;
						long currentCpt = 0;
						long cumulRazCpt = 0;
						long time = 0;
						long cptTime = 0;
						long coefTemps = ConstantesParcoursATESS.pasCptTps * (long) ConstantesParcoursATESS.resolutionTemps * 1000;
						int codeT = TypeRepere.temps.getCode();
						int codeD = TypeRepere.date.getCode();
						long lastAbsoluteTime = 0;
						long decalageRAZ = 0;
						
						// SI Absence Date/Heure (H)
						if (msg.getVariable(codeD) == null 
								&& msg.getAbsoluteTime() == 0) {
							
							// Message à datation incertaine (pas de Date/Heure)
							msg.setMessageIncertitude(true);
							
							// SI Présence variable Temps (t)
							if (msg.getVariable(codeT) != null) {
								// Récupérer valeur compteur temps (MM:SS en sec * coefTemps)
								cptTime = Long.valueOf(msg.getVariable(codeT).toString()) * coefTemps;
								// 01/01/1970 00:MM:SS
								msg.setAbsoluteTime(cptTime);
							} else {
								// Message à datation absente
								msg.setUndatedMessage(true);
							}
						}
						
						// SI Message non daté ET n'est pas le premier de la liste
						if (msg.isUndatedMessage() && lastMessage != null) {
							// ALORS on admet de le dater avec le dernier temps
							long previousTime = lastMessage.getAbsoluteTime();
							msg.setAbsoluteTime(previousTime);
							
							// SI le message précédent était à datation incertaine
							if (lastMessage.isMessageIncertitude()) {
								// ALORS on propage l'incertitude
								msg.setMessageIncertitude(true);
							}
						} 
						// SINON le premier message restera non daté
						// lastAbsoluteTime = 0 --> provoquera l'affichage complet de ##
							
						// SI Le message courrant est le PREMIER message contenant la variable Date/Heure (H)
						if (msg.getVariable(codeD) != null && dateAlreadyMet == false) {
							
							dateAlreadyMet = true;
							
							// SI Au moins un message a été rencontré sans variable Date/Heure (H)
							if (lastMessage != null) {
								
								// SI ce message courrant n'est pas une rupture d'acquisition
								if (msg.getEvenement().isRuptureAcquisition() == false) {
									// ALORS Calcul rétroactif Date/Heure pour les précédents messages
								
									// Récupérer la valeur de l'heure (HH en sec * coefTemps)
									time = Long.valueOf(msg.getVariable(codeD).toString().substring(8, 10)) * 3600 * coefTemps;
									
									// SI le message est [09] RAZ Compteur horaire
									if (msg.getEvenement().isRazCompteurTemps()) {
										// ALORS On reculera d'une heure pour le/les messages précédents
										decalageRAZ = 3600 * coefTemps;
									}
									
									// Récupérer la valeur du temps complet (HH:MM:SS en sec * coefTemps)
									refCpt = time + cptTime;
									
									// Epoch GMT transposé en locale du message obtenu grâce à H et t
									// (H seulement pour [09] RAZ Compteur horaire et [28] Test journalier)
									refTemps = msg.getAbsoluteTime();
		
									// Nombre de messages à dater
									int i = goodMsgs.size() - 2;
									
									// TANT QU'il reste des messages à dater
									while (i >= 0) {		
										
										// SI l'événement à dater est [09] RAZ Compteur horaire ???
										if (goodMsgs.get(i).getEvenement().isRazCompteurTemps()) {
											long cptAvantRaz = 0;
											int k = i;
											
											while(goodMsgs.get(k).getVariable(codeT) == null && k > 0) {
												k--;
											}
											
											currentCpt = ConstantesParcoursATESS.maxCptTps * coefTemps;
											cumulRazCpt += ConstantesParcoursATESS.maxCptTps * coefTemps;
											cptAvantRaz = Long.valueOf(goodMsgs.get(k).getVariable(codeT).toString()) * coefTemps;
											cumulCpt = refCpt - (currentCpt- cptAvantRaz);
											goodMsgs.get(i).setAbsoluteTime(refTemps - cumulCpt);
										} else {
											
											// SI Présence variable Temps (t)
											if (goodMsgs.get(i).getVariable(codeT) != null) {
												long heure = 0;
												
												// Le premier message qui a apporté la date/heure n'est pas une rupture d'acquisition
												// donc le/les messages précédents ont une Date/heure contigue
												// on lève l'incertitude
												goodMsgs.get(i).setMessageIncertitude(false);
											
												// SI un des messages précédent contient Date/Heure (h) ???
												if (goodMsgs.get(i).getVariable(codeD) != null) {
													// ALORS Récupérer la valeur de l'heure (HH en sec * coefTemps)
													heure = Long.valueOf(goodMsgs.get(i).getVariable(codeD).toString().substring(8, 10)) * 3600 * 1000;
												} else {
													// SINON garder celle du premier message l'ayant apporté
													heure = Long.valueOf(msg.getVariable(codeD).toString().substring(8, 10)) * 3600 * 1000;
												}
												
												
												// Récupérer valeur compteur temps du précédent message à dater (MM:SS en sec * coefTemps)
												currentCpt = ((Long) Long.valueOf(goodMsgs.get(i).getVariable(codeT).toString())) * coefTemps;						
												
												// Calcul du cumul en arrière
												// HH:MM:SS(ref) - 00:MM:SS - HH:00:00 ( + 01:00:00 si RAZ compteur horaire)
												cumulCpt = refCpt - currentCpt - heure + cumulRazCpt + decalageRAZ;
												
												// dater le message
												// = la première date rencontrée - cumul en arrière calculé pour ce message
												goodMsgs.get(i).setAbsoluteTime(refTemps - cumulCpt);
												
												// Conserver cette nouvelle date
												lastAbsoluteTime = refTemps - cumulCpt ;
												
												// SI Au moins un message sans temps a été rencontré depuis le recul
												if (messageSansTemps) {
													// ALORS on admet de les dater avec le dernier temps connu
													// (celui du message courrant vu qu'on recule)
													remplirMessageSansTemps(goodMsgs, i, lastAbsoluteTime, true);
												}
											} else {
												// SINON ( Absence variable Temps (t) )
												
												// Le premier message qui a apporté la date/heure n'est pas une rupture d'acquisition
												// donc le/les messages précédents ont une Date/heure contigue
												// on lève l'incertitude
												goodMsgs.get(i).setMessageIncertitude(false);
												
												// SI Il s'agit du premier message sans temps (non redaté) lors du recul
												// messageSansTemps pas encore flaggé
												if (!messageSansTemps) {
													// ALORS mémoriser ce message pour définir le point d'arret
													// de la recopie de datage
													firstMsgSansTemps=goodMsgs.get(i).getMessageId();
													messageSansTemps=true;	
												}
											}
										}
										
										// Reculer d'un message
										i--;
									}
								}
										                   
							}
							// SINON
							// Le premier message valorise H, donc pas de calcul rétroactif
						}
						
						// Paramètres initiaux pour le calcul de la distance
	                    // cumulée à mettre dans le message courant
	                    if (params == null) {
	                        params = new ComputingUtilsAccumulatedDistanceInitParams(msg.getAbsoluteDistance());
	                    }

	                    ComputingUtils.computeAccumulatedDistance(msg, params);
					
	                    if (vLKAllowed)
	                    {
	                    	int msgCode = msg.getEvenement().getM_ADescripteurComposant().getM_AIdentificateurComposant().getCode();
						
	                    	// Si le code du message est 0x74 : Données train KVB
	                    	if (msgCode == 116) {
	                    		trainKVBMessage = msg;
	                    		updateVitesseLimiteKVBFromDonneesTrainKVB(msg);
	                    	} else if (trainKVBMessage != null) {
	                    		msg.setTrainKvb(trainKVBMessage);
	                    	}
						
	                    	// Si le code du message est 0x77 : Balises N1
	                    	if (msgCode == 119) {
	                    		updateTrainDirection(msg);
	                    	}
						
	                    	// Si le code du message est 0x75 : Balises 123 ou 0x76
	                    	// : Balises 45 ou 0x78 : Balises N2
	                    	if (msgCode == 117 || msgCode == 118 || msgCode == 120) {
	                    		TrainDirectionEnum trainDirection = getTrainDirection();
							
	                    		if (trainDirection != TrainDirectionEnum.INCONNU) {
	                    			msg.setTrainDirection(trainDirection);
	                    		} else {
	                    			balisesVitesseLimiteKVBQueue.add(msg);
	                    		}
	                        
	                    		// Si le code du message est 0x75 : Balises 123 ou
	                    		// 0x76 : Balises 45
	                    		if (msgCode == 117 || msgCode == 118) {
	                    			updateVitesseLimiteKVBFromBalisesVitesseLimiteKVB(msg);
	                    		}
	                    	}

	                    	// On regarde pour le message courant si une vitesse
	                    	// limite KVB différée est à appliquer
	                    	boolean valorisation = false;			
	
	                    	while (!delayVitesseLimiteKVBFifo.isEmpty() && msg.getAccumulatedDistance() >= delayVitesseLimiteKVBFifo.getFirst().getDistanceApplicationVitesseLimiteKVB()) {
		
	                    		vitesseLimiteKVB.getInstance().set(delayVitesseLimiteKVBFifo.getFirst().getApplicationVitesseLimiteKVB());
	                    		delayVitesseLimiteKVBFifo.remove();
	                    		valorisation = true;
	                    	}
	                    
	                    	// Si une vitesse limite KVB différée est à appliquer
	                    	if (valorisation) {			    
	                    		updateVitesseLimiteKVBRightNow(msg);
	                    	}
	                    }
						
						lastMessage = msg;
					}						
				} else {
					finLecture = true;
				}
			}
		}
		
		lastMessage = null;
		lastSegTemp = null;
		lastSegDistance = null;

		for (int j = 0; j < goodMsgs.size(); j++) {
			try {
				Evenement ev = goodMsgs.get(j).getEvenement();
				
				if(!ev.isASychroniser() && !ev.isReferenceSynchro()){
					if(GestionnaireSynchronisationGroupes.getInstance().isSynchroEnCours()) {
						GestionnaireSynchronisationGroupes.getInstance().synchronisationMessages();
					}
				} else{
					GestionnaireSynchronisationGroupes.getInstance().setSynchroEnCours(true);
					
					if(ev.isASychroniser()) {
						GestionnaireSynchronisationGroupes.getInstance().ajouterMessageASynchroniser(goodMsgs.get(j));
					} else{
						GestionnaireSynchronisationGroupes.getInstance().setMsgReferenceSynchro(goodMsgs.get(j));
					}
				}
				
				setFlags(goodMsgs.get(j));
			} catch (Exception e) {
				SamngLogger.getLogger().error(this, e);
			}
			
			lastMessage = goodMsgs.get(j);
		}
		
		if(lastSegDistance != null){
			enregistrerSegmentDistance(lastMessage);
		}
		
		if (lastSegTemp != null) {
			enregistrerSegmentTemps(lastMessage);
		}
		
		// Creates the array with bad and good messages
		Message[][] msgs = new Message[2][];
		msgs[0] = goodMsgs.toArray(new Message[goodMsgs.size()]);
		msgs[1] = badMsgs.toArray(new Message[badMsgs.size()]);
		
		return msgs;
	}

    private void updateVitesseLimiteKVBFromDonneesTrainKVB(AtessMessage msg) {
    	// La reprogrammation du KVB annule toutes les vitesses limites en cours
    	vitesseLimiteKVB.getInstance().clear();    		
    	updateVitesseLimiteKVBRightNow(msg);
    	vitesseLimiteKVB.getInstance().setValeurMax(getVitesseLimiteTrainFromDonneesTrainKVB(msg));
    	delayVitesseLimiteKVBFifo.clear();
    }

    private void updateVitesseLimiteKVBRightNow(AtessMessage msg) {
    	
	VariableAnalogique vitesseLimiteKVBVariable = VitesseLimiteKVBService.getInstance().getNewVariable(vitesseLimiteKVB.getInstance().getValeur());
    
	msg.ajouterVariable(vitesseLimiteKVBVariable);
    }

	private void updateTrainDirection(AtessMessage msg) {
		// Récupération de l'odométrie pour savoir si le message doit être ajouté à la séquence ou si une nouvelle séquence doit être créée
		VariableComplexe varBS1 = (VariableComplexe) msg.getVariable("BS1");
		AVariableComposant[] enfants = varBS1.getEnfants();
		VariableAnalogique odometrie = null;
		VariableDiscrete indicateurBS1 = null;
		
		for (AVariableComposant var : enfants) {
			if ("BS1.odometrie".equals(var.getDescriptor().getM_AIdentificateurComposant().getNom())) {
				odometrie = (VariableAnalogique) var;
			}
			if ("BS1.Indicateur_BS1".equals(var.getDescriptor().getM_AIdentificateurComposant().getNom())) {
				indicateurBS1 = (VariableDiscrete) var;
			}
		}
		
		Long odometrieValeur = (Long) odometrie.getCastedValeur();
		if (this.odometrieCourante.longValue() != odometrieValeur.longValue()) {
			if (!this.sequenceBalisesBS1.isEmpty()) {
				this.sequenceBalisesBS1.clear();
			}
	        if (!this.balisesVitesseLimiteKVBQueue.isEmpty()) {
		    for (AtessMessage queuedMsg : this.balisesVitesseLimiteKVBQueue) {
				queuedMsg.setTrainDirection(TrainDirectionEnum.INCONNU);
			}
    		this.balisesVitesseLimiteKVBQueue.clear();
			}
			this.odometrieCourante = odometrieValeur;
		}
		this.sequenceBalisesBS1.add((Short) indicateurBS1.getCastedValeur());
		
    	if (!this.balisesVitesseLimiteKVBQueue.isEmpty()) {
	       updateBalisesVitesseLimiteKVBQueue();
		}
	}
	
    private void updateBalisesVitesseLimiteKVBQueue() {
		TrainDirectionEnum trainDirection = getTrainDirection();
		if (trainDirection != TrainDirectionEnum.INCONNU) {
	    for (AtessMessage msg : this.balisesVitesseLimiteKVBQueue) {
				msg.setTrainDirection(trainDirection);
		updateVitesseLimiteKVBFromBalisesVitesseLimiteKVB(msg);
	    }
	    this.balisesVitesseLimiteKVBQueue.clear();
	}
    }

    /**
     * 
     * @param msg
     *            : MEssage Atess pour extraire les point d'information dans la
     *            bonne direction et pour la bonne catégorie de train
     */
    private void updateVitesseLimiteKVBFromBalisesVitesseLimiteKVB(AtessMessage msg) {
	msg.decodeKVBMessage();
	KVBDecoderResult kvbDecoderResult = msg.getDecodedEvent();
	if(kvbDecoderResult != null){
		List<InformationPoint> infoPointsAConsiderer = getPointsInformationAConsiderer(kvbDecoderResult);
		for (InformationPoint infoPoint : infoPointsAConsiderer) {
		    handleInformationPoint(msg, infoPoint);
		}
	}
    }

    /**
     * Application de l'algorithme de calcul de la vitesse limite KVB.
     * 
     * @param msg
     *            : Message Atess
     * @param infoPoint
     *            : Point d'information qui doit être dans la bonne direction et
     *            pour la bonne catégoriee de train
     */
    private void handleInformationPoint(AtessMessage msg, InformationPoint infoPoint) {

	// Dans le cas où le développeur ne respecte pas la règle
	// "Point d'information qui doit être dans la bonne direction
	if (!infoPoint.isRightDirection()){
	    return;
	}

	// Objet pour l'application d'une vitesse limite KVB à une distance
	// donnée
	DelayVitesseLimiteKVB delayVitesseLimiteKVB = new DelayVitesseLimiteKVB();

	// Booléen pour déterminer si une vitesse a pu être récupérée d'une
	// séquence dédiée.
	boolean foundX75VitesseLimiteKVB = false;
	boolean foundX6VitesseLimiteKVB = false;
	
	vitesseLimiteKVB vLKTmp = null;
	
	// Debug
	/*if (msg.getAccumulatedDistance() > 641){
		System.out.println("stop");
	}*/

	// Pour chaque marqueur du point d'information
	for (Marker m : infoPoint.getMarkers()) {
				
		// Premier filtre : les autres marqueurs ne sont pas utiles
		if (!KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X3.equals(m.getCode()) &&
			!KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X9.equals(m.getCode()) &&
			!KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X14TIVD.equals(m.getCode()) &&
			!KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X6.equals(m.getCode()) &&
			!KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X1X4.equals(m.getCode()) &&
			!KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X7.equals(m.getCode()) &&
			!KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X5.equals(m.getCode())) {
			continue;
		}
				
	    // Si le code marqueur est X3
		// La vitesse ici a déjà été corrigée par les balises X8 éventuelles
	    if (KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X3.equals(m.getCode())) {
	    	// Récupération de la vitesse
	    	for (MarkerValue value : m.getValues()) {
	    		if (value.isNumeric() && "km/h".equals(m.getUnit())) {	    				    	
	    			// Application immédiate de la vitesse limite KVB
	    			if (vitesseLimiteKVB.getInstance().isRightNowApplication(value)) {
	    				vitesseLimiteKVB.getInstance().set(value, m, false);
	    				updateVitesseLimiteKVBRightNow(msg);
	    				
	    				// Rechercher si un TIVD correspondant a ce TIVE n'est pas en attente d'application
	    				// le supprimer
	    				// Tous les non TIVD en cours doivent être supprimés
	    				for (int i = 0 ; i < delayVitesseLimiteKVBFifo.size() ; i++) {
	    					if (delayVitesseLimiteKVBFifo.get(i).getApplicationVitesseLimiteKVB().isTivd()) {
	    						if (delayVitesseLimiteKVBFifo.get(i).getApplicationVitesseLimiteKVB().getValeur() == value.getNumericalValue().longValue()) {	    							
	    							delayVitesseLimiteKVBFifo.remove(i--);
	    						}
	    					} else {
	    						delayVitesseLimiteKVBFifo.remove(i--);
	    					}
	    				}
	    				
	    			} else {
	    				// Application de la vitesse quand l'arrière du train arrive au point d'information
	    				Double applicationVitesseLimiteKVB = getLongueurTrainFromDonneesTrainKVB(msg.getTrainKvb()).doubleValue() / 1000D + msg.getAccumulatedDistance();			    
				    
	    				// Rechercher dans les vitesses limites KVB à venir (max laongueur du train), si des vitesses sont moins contraignantes
	    				// Si c'est le cas, cette nouvelle limite doit prendre leur place
	    				for (int i = 0 ; i < delayVitesseLimiteKVBFifo.size() ; i++) {
	    					if (applicationVitesseLimiteKVB >= delayVitesseLimiteKVBFifo.get(i).getDistanceApplicationVitesseLimiteKVB()) {
	    						if ((delayVitesseLimiteKVBFifo.get(i).getApplicationVitesseLimiteKVB().isRightNowApplication(value))) {	    							
	    								applicationVitesseLimiteKVB = delayVitesseLimiteKVBFifo.get(i).getDistanceApplicationVitesseLimiteKVB();
	    								break;
	    						}
	    					}
	    				}
	    				
	    				// Rechercher si un TIVD correspondant a ce TIVE n'est pas en attente d'application
	    				// le supprimer
	    				for (int i = 0 ; i < delayVitesseLimiteKVBFifo.size() ; i++) {
	    					if (delayVitesseLimiteKVBFifo.get(i).getApplicationVitesseLimiteKVB().isTivd()) {
	    						if (delayVitesseLimiteKVBFifo.get(i).getApplicationVitesseLimiteKVB().getValeur() == value.getNumericalValue().longValue()) {	    							
	    							delayVitesseLimiteKVBFifo.remove(i--);
	    						}
	    					} 
	    				}
	    				
	    				// Si un TIVD va être appliqué avant
	    				// et qu'il est plus restrictif
	    				// Il ne faut pas tenir compte de ce TIVE
	    				boolean ignore = false;
	    				
	    				for (int i = 0 ; i < delayVitesseLimiteKVBFifo.size() ; i++) {
	    					if (applicationVitesseLimiteKVB >= delayVitesseLimiteKVBFifo.get(i).getDistanceApplicationVitesseLimiteKVB()) {
	    						if (delayVitesseLimiteKVBFifo.get(i).getApplicationVitesseLimiteKVB().isTivd()) {
	    							if (value.getNumericalValue().longValue() > delayVitesseLimiteKVBFifo.get(i).getApplicationVitesseLimiteKVB().getValeur()) {
	    								ignore = true;
	    							}
	    						}
	    					}
	    				}
			    
	    				if (!ignore) {
	    					delayVitesseLimiteKVB.setDistanceApplicationVitesseLimiteKVB(applicationVitesseLimiteKVB);
	    					delayVitesseLimiteKVB.setApplicationVitesseLimiteKVB(new vitesseLimiteKVB(value, m, false));
	    					delayVitesseLimiteKVBFifo.add(delayVitesseLimiteKVB);
	    				}
	    			}
	    		} 
	    		
	    		break; // Plus besoin d'information
	    	}
	    // SI Cas où le code marqueur est un X9 ou X14_TIVD et qu'un X6 est présent dans la séquence (couples X6-X9 ou X6-X14)
	    } else if (foundX6VitesseLimiteKVB && (KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X9.equals(m.getCode()) 
	    		   || KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X14TIVD.equals(m.getCode()))) {
	    		
	    	// Récupération de la distance
	    	for (MarkerValue value : m.getValues()) {
	    		// Extraction de la distance en mètre du délai d'applciation de la vitesse limite KVB
	    		if (value.isNumeric() && "m".equals(m.getUnit())) {
	    			Double applicationVitesseLimiteKVB = value.getNumericalValue().doubleValue() / 1000D + msg.getAccumulatedDistance();
	    			long vLKVB = delayVitesseLimiteKVB.getApplicationVitesseLimiteKVB().getValeur();
	    			
	    			// Suppression de tous les non TIVD qui arrive après l'application du TIVD
	    			// Si c'est une mise à jour d'un TIVD, il faut supprimer les anciens TIVD correspondant à la même vitesse limite
	    			for (int i = 0 ; i < delayVitesseLimiteKVBFifo.size() ; i++) {
	    				if (!delayVitesseLimiteKVBFifo.get(i).getApplicationVitesseLimiteKVB().isTivd()) {
	    					if (applicationVitesseLimiteKVB <= delayVitesseLimiteKVBFifo.get(i).getDistanceApplicationVitesseLimiteKVB()) {	    							
	    						delayVitesseLimiteKVBFifo.remove(i--);
	    					}
	    				} else {
	    					if (delayVitesseLimiteKVBFifo.get(i).getApplicationVitesseLimiteKVB().getValeur() == delayVitesseLimiteKVB.getApplicationVitesseLimiteKVB().getValeur()) {
	    						delayVitesseLimiteKVBFifo.remove(i--);
	    					}
	    				}
	    			}
	    			
	    			// Si la distance est égale à 0m le TIVD
	    			if (value.getNumericalValue().doubleValue() == 0.0) {
	    				
	    					vitesseLimiteKVB.getInstance().set(vLKVB, m, false);
		    				updateVitesseLimiteKVBRightNow(msg);
	    			} else {
	    				delayVitesseLimiteKVB.setDistanceApplicationVitesseLimiteKVB(applicationVitesseLimiteKVB);
			    		// Ajout de la vitesse limite KVB retardée à la file d'attente
			    		delayVitesseLimiteKVBFifo.add(delayVitesseLimiteKVB);
	    			}

	    			break; // La vitesse a été trouvée pas besoin d'autre information
	    		}
	    	}
	    }
	    // CAS d'une séquence en X6 
	    else if (KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X6.equals(m.getCode())) {
	    	
	    	// Si c'est une anulation d'execution, il faut supprimer le TIVD correspondant
	    	// Seuls les marqueurs permettent sont identification
	    	// Le premier marqueur contient la vitesse limite ou l'annulation, donc il ne faut pas le comparer
	    	// La distance n'est pas forcément égale	    	
	    	if ((((MarkerX3X6X9) m).getDistanceOrSpeed() != null) &&
	    		(((MarkerX3X6X9) m).getDistanceOrSpeed().contentEquals("all.AE") ||
	    		((MarkerX3X6X9) m).getDistanceOrSpeed().contentEquals("X6.AA"))) {
	    			    		
	    		for (int i = 0 ; i < delayVitesseLimiteKVBFifo.size() ; i++) {
					if (delayVitesseLimiteKVBFifo.get(i).getApplicationVitesseLimiteKVB().isTivd()) {						
						if (delayVitesseLimiteKVBFifo.get(i).getApplicationVitesseLimiteKVB().getValues() != null) {

							boolean same = true;
						
							for (int j = 1 ; j < m.getValues().size() ; j++) {
						
								if (!delayVitesseLimiteKVBFifo.get(i).getApplicationVitesseLimiteKVB().getValues().get(j).getValue().contentEquals(m.getValues().get(j).getValue())) {
									same = false;
									break;
								}
							}
						
							if (same) {
								delayVitesseLimiteKVBFifo.remove(i--);
							}
						}
					}	    					
				}
	    	} else {
	    	
	    		// Récupération de la vitesse
	    		for (MarkerValue value : m.getValues()) {
	    			if (value.isNumeric() && "km/h".equals(m.getUnit())) {
	    				// Extraction de la vitesse limite KVB de la séquence avec un X9 ou X14
	    				delayVitesseLimiteKVB.setApplicationVitesseLimiteKVB(new vitesseLimiteKVB(value, m, true));
			
	    				foundX6VitesseLimiteKVB = true;
			
	    				break; // La vitesse a été trouvée pas besoin d'autre information
	    			}
	    		}
	    	}
	    }
	    // CAS d'une séquence en X4
	    else if (KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X1X4.equals(m.getCode())) {
	    	long vL = -1;
	    	
	    	for (MarkerValue value : m.getValues()) {
	    		if (value.getValue().contentEquals("X1X4.VE.30LPV")) {
	    			vL = 30;
	    		} else if (value.getValue().contentEquals("X1X4.VE.40LPV")) {
	    			vL = 40;
	    		} else if (value.getValue().contentEquals("X1X4.VE.60LPV")) {
	    			vL = 60;
	    		}
	    		
	    		if (vL != -1) {
	    			if (vitesseLimiteKVB.getInstance().isRightNowApplication(vL)) {
	    				vitesseLimiteKVB.getInstance().set(vL, "GSFC", false);
	    				updateVitesseLimiteKVBRightNow(msg);
	    			} else {
	    				// Application de la vitesse quand l'arrière du train arrive au point d'information
	    				Double applicationVitesseLimiteKVB = getLongueurTrainFromDonneesTrainKVB(msg.getTrainKvb()).doubleValue() / 1000D + msg.getAccumulatedDistance();			    
			    
	    				// Rechercher dans les vitesses limites KVB à venir (max laongueur du train), si des vitesses sont moins contraignantes
	    				// Si c'est le cas, cette nouvelle limite doit prendre leur place
	    				for (int i = 0 ; i < delayVitesseLimiteKVBFifo.size() ; i++) {
	    					if (applicationVitesseLimiteKVB >= delayVitesseLimiteKVBFifo.get(i).getDistanceApplicationVitesseLimiteKVB()) {
	    						if ((delayVitesseLimiteKVBFifo.get(i).getApplicationVitesseLimiteKVB().isRightNowApplication(vL))) {	    							
	    							applicationVitesseLimiteKVB = delayVitesseLimiteKVBFifo.get(i).getDistanceApplicationVitesseLimiteKVB();
	    							// Ne pas supprimer car le TIVE s'il est permanent doit être sauvegarder : c'est la vitesse limite permanente à restituer au cas ou
    								break;
	    						}
	    					}
	    				}
		    
	    				delayVitesseLimiteKVB.setDistanceApplicationVitesseLimiteKVB(applicationVitesseLimiteKVB);
	    				delayVitesseLimiteKVB.setApplicationVitesseLimiteKVB(new vitesseLimiteKVB(vL, "GSFC", false));
		    			    
	    				delayVitesseLimiteKVBFifo.add(delayVitesseLimiteKVB);
	    			}
	    			
	    			break;
    			}
	    	}
	    }
	    // CAS d'une séquence en X7
	    else if (KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X7.equals(m.getCode())) {
	    	
	    	// Récupération de la vitesse
	    	for (MarkerValue value : m.getValues()) {
	    		if (value.isNumeric() && "km/h".equals(m.getUnit())) {	    				    		
	    			
	    			if ("7,7".equals(infoPoint.getXSequence())) {
	    				if (vitesseLimiteKVB.getInstance().isRightNowApplication(value)) {
	    					
	    					vitesseLimiteKVB.getInstance().set(value, m, false);
	    					// Application immédiate de la vitesse limite KVB	    				
	    					updateVitesseLimiteKVBRightNow(msg);
	    					
	    				} else {
	    					
	    					delayVitesseLimiteKVB.setApplicationVitesseLimiteKVB(new vitesseLimiteKVB(value, m, false));
							delayVitesseLimiteKVB.setDistanceApplicationVitesseLimiteKVB(getLongueurTrainFromDonneesTrainKVB(msg.getTrainKvb()).doubleValue() / 1000D + msg.getAccumulatedDistance());
							delayVitesseLimiteKVBFifo.add(delayVitesseLimiteKVB);
	    				}
	    			} 
	    			// Dans le cas d'un 7,5, plusieurs cas sont possibles...
	    			else if ("7,5".equals(infoPoint.getXSequence())) {	    		
	    				
	    				vLKTmp = new vitesseLimiteKVB(value, m, false);
	    				foundX75VitesseLimiteKVB = true;
	    			}
	    			// Fin de limite temporaire de vitesse, il faut restituer la vitesse permanente
			    	// Attention, la fin de vitesse doit correspondre à la vitesse limite en court d'application
					else if("5,7".equals(infoPoint.getXSequence())) {
						if (value.getNumericalValue().longValue() == vitesseLimiteKVB.getInstance().getValeur()) {
							
							// Dans le cas d'une GSFC, aucune vitesse limite ne doit être appliquée ensuite
							if (vitesseLimiteKVB.getInstance().isGSFC()) {														
								delayVitesseLimiteKVB.setApplicationVitesseLimiteKVB(new vitesseLimiteKVB());
							} else {
								delayVitesseLimiteKVB.setApplicationVitesseLimiteKVB(vitesseLimiteKVB.getInstance().getValeurPrec());
							}	
							
							delayVitesseLimiteKVB.setDistanceApplicationVitesseLimiteKVB(getLongueurTrainFromDonneesTrainKVB(msg.getTrainKvb()).doubleValue() / 1000D + msg.getAccumulatedDistance());	
							delayVitesseLimiteKVBFifo.add(delayVitesseLimiteKVB);							
						}
					}
	    			
	    			break; // La vitesse a été trouvée pas besoin d'autre information
			}
	    	}	    
	    }
	    // CAS d'une séquence en X5
	    // Note : le cas des X5 X7 est traité dans le X7
	    else if (KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X5.equals(m.getCode()) && !"5,7".equals(infoPoint.getXSequence())) {
	    	
	    Double applicationVitesseLimiteKVB = getLongueurTrainFromDonneesTrainKVB(msg.getTrainKvb()).doubleValue() / 1000D + msg.getAccumulatedDistance();
	    	
		for (MarkerValue value : m.getValues()) {
	    	
			if (foundX75VitesseLimiteKVB) {
				// Application immédiate de la vitesse limite KVB
    			if (vitesseLimiteKVB.getInstance().isRightNowApplication(vLKTmp.getValeur())) {
    				vitesseLimiteKVB.getInstance().set(vLKTmp);
	    			updateVitesseLimiteKVBRightNow(msg);
	    		// Sinon la vitesse limite sera a appliquer que lorsque l'intégralité du train aura franchi le point d'information (Dt.longueur)
	    		} else {
	    			delayVitesseLimiteKVB.setApplicationVitesseLimiteKVB(new vitesseLimiteKVB(vLKTmp));
	    			delayVitesseLimiteKVB.setDistanceApplicationVitesseLimiteKVB(applicationVitesseLimiteKVB);
	    			delayVitesseLimiteKVBFifo.add(delayVitesseLimiteKVB);
	    		}
				
			} else if ("X5.DGV".equals(value.getValue()) || "X5.FNE".equals(value.getValue()) || "X5.FVL".equals(value.getValue())) {
				
				// Toutes les vitesses limites qui interviennent après cette limite doivent être supprimées
				for (int i = 0 ; i < delayVitesseLimiteKVBFifo.size() ; i++) {
					if (delayVitesseLimiteKVBFifo.get(i).getDistanceApplicationVitesseLimiteKVB() >= applicationVitesseLimiteKVB) {
	    							
						delayVitesseLimiteKVBFifo.remove(i--);						
					} 
				}
								
			    delayVitesseLimiteKVB.setApplicationVitesseLimiteKVB(new vitesseLimiteKVB());
			    delayVitesseLimiteKVB.setDistanceApplicationVitesseLimiteKVB(applicationVitesseLimiteKVB);
			    delayVitesseLimiteKVBFifo.add(delayVitesseLimiteKVB);	
			} else if ("X5.FLTV".equals(value.getValue())) {
				delayVitesseLimiteKVB.setApplicationVitesseLimiteKVB(vitesseLimiteKVB.getInstance().getValeurPrec());
				delayVitesseLimiteKVB.setDistanceApplicationVitesseLimiteKVB(applicationVitesseLimiteKVB);	
				delayVitesseLimiteKVBFifo.add(delayVitesseLimiteKVB);
			}
			
			break;

		}
	}
	
    	// Si point bidirectionnel, seule la première balise doit être prise en compte
    	if ("5,5".equals(infoPoint.getXSequence()) || "7,7".equals(infoPoint.getXSequence())) {
    		break;
    	}
    }
    }

    private List<InformationPoint> getPointsInformationAConsiderer(KVBDecoderResult kvbDecoderResult) {
	List<InformationPoint> infoPointsAConsiderer = new ArrayList<InformationPoint>();
	List<InformationPoint> infoPoints = kvbDecoderResult.getEvent().getKVBVariable().getInformationPoints();
	for (InformationPoint ip : infoPoints) {
	    // Si le point d'information est pour la bonne catégorie de train et
	    // dans la bonne direction
	    if (ip.isRightDirection() && !DecoderService.LABEL_ALERT_TEXT_INVALID_FOR_TRAIN_CATEGORY.equals(ip.getAlertText())) {
		infoPointsAConsiderer.add(ip);
	    }
	}
	return infoPointsAConsiderer;
    }

	private TrainDirectionEnum getTrainDirection() {
		for (Short[] sequence : SENS_MARCHE_SEQUENCES) {
			if (matchesSequenceBS1(sequence)) {
				return TrainDirectionEnum.SENS_DE_MARCHE;
			}
		}
		
		for (Short[] sequence : CONTRE_SENS_SEQUENCES) {
			if (matchesSequenceBS1(sequence)) {
				return TrainDirectionEnum.CONTRE_SENS;
			}
		}
		
		return TrainDirectionEnum.INCONNU;
	}
	
	private boolean matchesSequenceBS1(Short[] sequence) {
		if (this.sequenceBalisesBS1.size() == sequence.length) {
			int idx = 0;
			for (Short code : this.sequenceBalisesBS1) {
				if (code != sequence[idx]) {
					return false;
				}
				idx++;
			}
			return true;
		} else {
			return false;
		}
	}

	protected void chargerDataExplore() throws ParseurBinaireException {			
		// Tampon des messages représentants un boud de segment de temps, mais qui n'ont pas encore d'absolute time
		List<Message> segBounds = new ArrayList<Message>(); 
		
		lastMessage = null; // Dernier message traité
		
		int pos = 0; // Offset de l'octet en court de traitement par rapport au début du fichier
		int cpt = 1; // Compteur d'octets traité dans un enregistrement : varie de 1 à 31 
				
		// tailleTableauMessage contient le nombre d'octets du fichier à explorer
		int tailleTableauMessage = this.message.length;
		
		// Table de décodage des messages : utilisée par chargerMessage()  		
		descrTable = (DescripteurComposite)ParseurTableAssociationEvVars.getInstance().getTableEvVars();

		// Flag de fin de lecture
		boolean finLecture = false;		
		

		//while (!finLecture && !Thread.interrupted())
		while (!finLecture && !monitor.isCanceled())
		{
			ActivatorData.getInstance().getVp().setValeurProgressBar(pos*100/(tailleTableauMessage));
			ActivatorData.getInstance().getVpExportExplorer().setValeurProgressBar(pos,tailleTableauMessage);

			// Si on arrive au niveau du crc de l'enregistrement...
			if (cpt > tailleBlocData - 2) 
			{
				// Si on est arrivé au bout du fichier : on vient de traiter le dernier enregistrement
				if((tailleBlocData - cpt + 1 + pos) >= tailleTableauMessage - 1)
				{
					finLecture = true;
				}
				else
				{					
					// Passage à l'enregistrement suivant
					pos += (tailleBlocData - cpt + 1);
					cpt = 1;
				}
			}

			AtessMessage trainKVBMessage = null;
			
			// Tant qu'on n'est pas arrivé au CRC, on dépile les évènements de l'enregistrement
			while (cpt <= tailleBlocData - 2 && !finLecture && !monitor.isCanceled()) 
			{
				ActivatorData.getInstance().getVp().setValeurProgressBar(pos*100/(tailleTableauMessage));
				ActivatorData.getInstance().getVpExportExplorer().setValeurProgressBar(pos,tailleTableauMessage);

				AtessMessage msg = new AtessMessage();

				// Si on n'a pas dépassé la taille du fichier.
				// A quoi sert ce test ? : peut-être pour se prémunir d'un fichier tronqué (message tronqué)
				if (pos < tailleTableauMessage) 
				{
					// Si l'octet en cours est renseigné : != 0xFF ou != 0xEE
					// Pour que la fonction octetNonVide() retourne false, il faut que tous le reste des octets 
					// de l'enregsitrement soit non renseignés : sinon, il ne serait pas possible de traiter les 
					// messages 0xFF et 0xEE qui sont des ID de message valides
					if (octetNonVide(pos, this.message, cpt))	
					{
						try 
						{
							// Chargement du message pointer par "pos", avec ses ressources
							msg = chargerMessage(pos, true);
							msg.setOffsetDebut(((int)pos / tailleBlocData) * tailleBlocData);
							msg.setOffsetFin(pos + msg.getLongueur());
							
							// ??
							this.tc.getFormatedTime();
							
							// changement de la position de lecture
							pos += msg.getLongueur();
							// incrémentation du compteur
							cpt += msg.getLongueur();							
						} 
						catch (ParseurBinaireException ex) 
						{
							// System.out
							// .println("evt non trouvé dans table evt/var: id =
							// "
							// + this.message[pos]);
							pos += tailleBlocData - cpt + 1;
							cpt = tailleBlocData;
						}											
					} 
					else 
					{
						// On saute les octets non renseignés
						pos += tailleBlocData - 2 - cpt + 1 ;
						cpt += tailleBlocData - 2 - cpt + 1 ;
					}

					if (msg.getEvenement() != null) 
					{
						// Evènement datant = premier évènement possédant un absolute time
						long refCpt = 0; // Secondes écoulées depuis le début du jour lorsque l'évènement datant a été enregistré
						long refTemps = 0; // Absolute time de l'évènement datant : epoch time
						long cumulCpt = 0;
						long currentCpt = 0; // Temps écoulé, en seconde, depuis la dernière heure, lorsque l'évènement en cours de traitement a été enregistré 
						long cumulRazCpt = 0;
						long time=0; // Conversion, en secondes, de la ressource heure contenue dans la variable H de l'évènement en cours  
						long cptTime=0; // Valeur de la ressource T, en secondes, contenue dans l'évènement en cours
						long coefTemps=ConstantesParcoursATESS.pasCptTps * (long)ConstantesParcoursATESS.resolutionTemps * 1000;
						int codeT=TypeRepere.temps.getCode();
						int codeD=TypeRepere.date.getCode();
						long lastAbsoluteTime=0;
						long decalageRAZ=0;
						
						// Si le msg est celui d'un début de segment de temps
						if (isItSegmentTempBeginning(msg, lastMessage))
						{							
							// Fermer le segment précédent et ouvrir un nouveau
							// Dans le cas du premier msg, lastMessage est null, mais il faut l'enregistrer
							// pour rendre l'algo homogene
							segBounds.add(lastMessage);							
							segBounds.add(msg);
						}
																												
						// Si les messages précédents n'avaient pas d'AbsoluteTime et que celui que l'on vient de récupérer en a un...
						// segBounds.get(1) : on teste le "1", car dans le cas particulier où le msg est le premier du fichier,
						// segBounds.get(1) est null : pas de prédécesseur. Il y aura toujours un segBounds.get(1), car l'ouverture d'un 
						// segment est liée à la fermeture du précédent
						if (!segBounds.isEmpty() && msg.getAbsoluteTime() > 0 && segBounds.get(1).getAbsoluteTime() == 0)
						{
							// ??
							msg.setEvNonDate(true);
							
							// Les évènements RAZ cpt temps et Test journalier n'ont pas de ressource Temps
							if (msg.getVariable(codeT) != null)
							{
								// substring(8,10)) : pour récupérer uniquement l'information d'heure
								time=Long.valueOf(msg.getVariable(codeD).toString().substring(8,10))*3600*coefTemps;
								cptTime = Long.valueOf(msg.getVariable(codeT).toString()) * coefTemps;
							}
							else
							{
								// Pour un RAZ cpt time, t vaut 0s : cptTime = 0
								if (msg.getEvenement().isRazCompteurTemps()) 
								{
									// substring(8,10)) : pour récupérer uniquement l'information d'heure
									time=Long.valueOf(msg.getVariable(codeD).toString().substring(8,10))*3600*coefTemps;
								}
							}
							
							if (msg.getEvenement().isRazCompteurTemps()) 
							{
								// Le RAZ est dans l'heure suivante au minimum
								decalageRAZ=3600000;
							}
							
							refCpt = time + cptTime;
							refTemps = msg.getAbsoluteTime();

							// -1 car indice commence à 0
							int i = segBounds.size() - 1;
							
							// Si le dernier msg de segBounds est l'évènement datant, il est déjà daté ! 
							if (segBounds.get(i) == msg) i--;
							
							// Tant qu'il y a des évènements à dater
							// Dans le cas particulier du début de fichier, segBounds.get(0) = null et i = 0
							while (i >= 0 && segBounds.get(i) != null && segBounds.get(i).getAbsoluteTime() == 0) 
							{		
								// Comment l'évènement pourait être un RAZ sans absolutetime
								// Sachant qu'un RAZ fournit les informations nécessaires 
								// pour renseigner l'absolute time ??
								if ( segBounds.get(i).getEvenement().isRazCompteurTemps())
								{
									long cptAvantRaz = 0;
									int k = i;
									
									while(segBounds.get(k).getVariable(codeT) == null && k>0)
									{
										k--;
									}
									
									currentCpt = ConstantesParcoursATESS.maxCptTps * coefTemps;
									cumulRazCpt += ConstantesParcoursATESS.maxCptTps * coefTemps;
									cptAvantRaz = Long.valueOf(segBounds.get(k).getVariable(codeT).toString()) * coefTemps;
									cumulCpt = refCpt - (currentCpt- cptAvantRaz);
									segBounds.get(i).setAbsoluteTime(refTemps	- cumulCpt);
								}
								else
								{
									// Es-ce qu'un début ou un fin de segment de temps peut ne pas avoir de ressource temps ?
									// Réponse oui, du moins, le message de fin de segment peut être n'importe quel message !
									if (segBounds.get(i).getVariable(codeT) != null) 
									{
										long heure=0;
										// Comment l'évènement pourrait avoir les ressources t et H et ne pas avoir d'absolute time ??? 
										if (segBounds.get(i).getVariable(codeD)!=null) 
										{
											heure=Long.valueOf(segBounds.get(i).getVariable(codeD).toString().substring(8,10))*3600*1000;
										}
										else
										{
											heure=Long.valueOf(msg.getVariable(codeD).toString().substring(8,10))*3600*1000;
										}
										
										currentCpt=((Long)Long.valueOf(segBounds.get(i).getVariable(codeT).toString()))* coefTemps;						
										cumulCpt = refCpt - currentCpt - heure + cumulRazCpt + decalageRAZ;
										segBounds.get(i).setAbsoluteTime(refTemps - cumulCpt);
										lastAbsoluteTime=refTemps - cumulCpt ;
										
										if (messageSansTemps) 
										{
											remplirMessageSansTemps(segBounds, i, lastAbsoluteTime,true);
										}
									}
									else
									{
										if (!messageSansTemps) 
										{
											firstMsgSansTemps=segBounds.get(i).getMessageId();
											messageSansTemps=true;	
										}
										else
										{
											if (i==0) 
											{
												remplirMessageSansTemps(segBounds, i, lastAbsoluteTime,false);
											}
										}
									}
								}
								
								i--;
							} // While
						}
						
			
						// Si le dernier msg est daté
						// Tous les précédents le sont forcéments
						if (!segBounds.isEmpty() && (segBounds.get(segBounds.size() - 1).getAbsoluteTime() != 0))
						{
							for (int i = 0 ; i < segBounds.size() ; i = i + 2)
							{
								setSegmentsTemp(segBounds.get(i + 1), segBounds.get(i));
							}
									
							segBounds.clear();
						}						
						
						int msgCode = msg.getEvenement().getM_ADescripteurComposant().getM_AIdentificateurComposant().getCode();
						
						if (msgCode == 116) {
							trainKVBMessage = msg;
						} else if (trainKVBMessage != null) {
							msg.setTrainKvb(trainKVBMessage);
						}
												
						lastMessage = msg;
					} 
				}
				else
				{
					finLecture=true;
				}
			} // While bloc enregistrement 30 octets
		} // While Fin de lecture
								
		// Pour le dernier segment de temps, c'est le dernier message du parcours qui le ferme
		if (lastSegTemp != null) 
			enregistrerSegmentTemps(lastMessage);
	}	

	protected void remplirMessageSansTemps(List<Message> goodMsgs,int i, long lastAbsoluteTime,boolean tempsRenseigne){
		Message msgToFill=goodMsgs.get(i);
		int j=tempsRenseigne ? i+1 : i;
		while (msgToFill.getMessageId()!=firstMsgSansTemps) {
			msgToFill=goodMsgs.get(j);
			msgToFill.setAbsoluteTime(lastAbsoluteTime);
			j=j+1;
		}
		messageSansTemps=false;
	}

	@Override
	protected ADescripteurComposant chargerDescripteursCodeBloc() throws BadHeaderInfoException {
		DescripteurCodeBloc descr = new DescripteurCodeBloc();

		return descr;
	}

	@Override
	protected AParcoursComposant chargerEntete() throws AExceptionSamNG {
		// TODO Auto-generated method stub
		return null;
	}

	protected int tailleIdentifiant(int ID){
		List<Identifiant> listIDsHexa=ParseurTableAssociationEvVars.getInstance().idExtended;
		for (int i = 0; i<listIDsHexa.size(); i++) {
			String valeurHexa=listIDsHexa.get(i).getCode();
			int intVal = Integer.parseInt(valeurHexa, 16);
			if (intVal==ID) {
				return listIDsHexa.get(i).getNbOctets();


			}
		}
		return 1;	
	}
	
	protected int tailleIdentifiantRealID(int ID){
		if (ID>65535) {
			return 4;
		}else if (ID>255) {
			return 2;
		}
		return 1;	
	}

	protected int gestionID(int start){
		byte[] tabIdEvt = new byte[2];
		tabIdEvt[0] = 0;
		tabIdEvt[1] = this.message[start];
		BigInteger bInt = new BigInteger(tabIdEvt);
		int id = bInt.intValue();

		switch (tailleIdentifiant(id)) {
		case 2:

			byte[] tabIdExtEvt = new byte[2];
			tabIdExtEvt[0] = 0;
			tabIdExtEvt[1] = this.message[start+1];
			BigInteger bIntExt = new BigInteger(tabIdExtEvt);
			int idExt = bIntExt.intValue();
			int tampon=id*256+idExt;	
			id=tampon;
			break;
		case 4:
			byte[] tabIdExtEvt2 = new byte[2];
			tabIdExtEvt2[0] = 0;
			tabIdExtEvt2[1] = this.message[start+1];
			
			byte[] tabIdExtEvt3 = new byte[2];
			tabIdExtEvt3[0] = 0;
			tabIdExtEvt3[1] = this.message[start+2];
			
			byte[] tabIdExtEvt4 = new byte[2];
			tabIdExtEvt4[0] = 0;
			tabIdExtEvt4[1] = this.message[start+3];

			BigInteger bIntExt2 = new BigInteger(tabIdExtEvt2);
			BigInteger bIntExt3 = new BigInteger(tabIdExtEvt3);
			BigInteger bIntExt4 = new BigInteger(tabIdExtEvt4);
			
			int idExt2 = bIntExt2.intValue();
			int idExt3 = bIntExt3.intValue();
			int idExt4 = bIntExt4.intValue();
			
			int tampon2=id*16777216+idExt2*65536+idExt3*256+idExt4;
			id=tampon2;
			break;
		default:
			break;
		}
		return id;
	}

	protected void gererIDNonTrouve(Message msg,int id)throws ParseurBinaireException{
		msg.setError(ErrorType.EventId);
		throw new BadEventCodeException(Messages.getString("errors.nonblocking.invalidEventId") //$NON-NLS-1$
				+ "; "  + Messages.getString("errors.nonblocking.eventId1") + id + "; ");
	}

	protected void gererNomEvtNonTrouve(int id, Evenement ev,Message msg){
		DescripteurEvenement descrEvt = new DescripteurEvenement();

		IdentificateurEvenement identifEvt = new IdentificateurEvenement();

		identifEvt.setCode(id);
		identifEvt.setNom("Event code: " + id);

		descrEvt.setCode(id);
		descrEvt.setNom("Event code: " + id);
		descrEvt.setCaractTemporelle(Temporelle.COPY_DOWN);

		descrEvt.setM_AIdentificateurComposant(identifEvt);

		ev.setM_ADescripteurComposant(descrEvt);
		ev.setChangementHeure(false);

		TableLangueNomUtilisateur tblLangues = new TableLangueNomUtilisateur();
		tblLangues.setNomUtilisateur(Langage.FR, "Code de l'événement: "+ id);
		tblLangues.setNomUtilisateur(Langage.EN, "Event code: " + id);

		ev.setNomUtilisateur(tblLangues);
		GestionnairePool.getInstance().ajouterEvenement(ev);

		msg.setError(ErrorType.EventId);
		SamngLogger.getLogger().warn(Messages.getString("errors.nonblocking.notXmlFoundEvent") //$NON-NLS-1$
				+ "; " + Messages.getString("errors.nonblocking.eventId1") + id + " " + //$NON-NLS-1$ //$NON-NLS-2$
				Messages.getString("errors.nonblocking.blockStart1") + " " + ((((msg.getMessageId() - (tailleEntete + tailleTableEvenement)) / tailleBlocData)) + 1)); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected void gererNomEvtTrouve(int id, DescripteurEvenement descEvt, Evenement ev,Message msg) throws ParseurBinaireException{
		//		 check if is set the nom utilisateur
		if (ev.getNomUtilisateur() == null
				|| ev.getNomUtilisateur().size() == 0) {
			msg.setError(ErrorType.XMLRelated);
			throw new BadArgumentInFileException(Messages.getString("errors.nonblocking.invalidXmlUsersList")
					+ "; " + Messages.getString("errors.nonblocking.eventId1") + id + "; " +
					Messages.getString("errors.nonblocking.blockStart1") + " " 
					+ (((msg.getMessageId() - (tailleEntete + tailleTableEvenement)) / tailleBlocData) + 1));
		}

		// check if is set the caractereTemporelle
		if (descEvt.getCaractTemporelle() == null) {
			msg.setError(ErrorType.XMLRelated);
			throw new BadArgumentInFileException(Messages.getString("errors.nonblocking.invalidXmlEvCaractTemp")
					+ "; " + Messages.getString("errors.nonblocking.eventId1") + id + "; " +
					Messages.getString("errors.nonblocking.blockStart1") + " " + (((msg.getMessageId() - (tailleEntete + tailleTableEvenement)) / tailleBlocData) + 1));
		}
	}

	protected byte[]remplirValue(AVariableComposant var,int start,int pos){
		byte[]value=null;
		if (var.getDescriptor().getTailleOctets() == 1) {
			value = new byte[1];
			value[0] = this.message[pos + start];
		} else {
			try {
				value = Arrays.copyOfRange(this.message, pos + start, var.getDescriptor().getTailleOctets()
						+ pos + start);
			} catch (Exception e) {
				System.out.println("pb lecture value");
			}
		}

		try {
			value = setVariableValue(var, value);
		} catch (Exception e) {
			System.out.println("problème de lecture de variable");
		}
		return value;
	}


	protected void gereVariablesTypeRepere(DescripteurComposite descrComp,int id,Message msg,int start,DescripteurEvenement descrEvent,Evenement ev){
		int variablesLength = 0;
		int longueurEvt=descrEvent.getLongueur();
		int size = descrComp.getLength();
		int pos = tailleIdentifiantRealID(id);
		DescripteurVariable descVar = null;
		int codeVar = 0;

		for (int i = 1; i < size; i++) {
			descVar = (DescripteurVariable) descrComp.getEnfant(i);
			codeVar = descVar.getM_AIdentificateurComposant().getCode();
			AVariableComposant var = GestionnairePool.getInstance().getVariable(codeVar);

			if (var == null) {
				System.out.println("code variable not found : "+codeVar);
			} else {
				msg.ajouterVariable(var);				
				if (var.getTypeValeur()!=null) {
					if (var.getTypeValeur().name().equals(Type.dateHeureBCD.name())) {
						var.getDescriptor().setTailleBits(32);
					}
				}			

				if (descrComp.isHasEntreeLogique()&&i==size-1) {
					int val=descrComp.isValeurEntreeLogique() ? 1 : 0;
					byte[]tab={(byte)val};
					var.setValeur(tab);
					continue;
				}

				byte[] value = remplirValue(var, start, pos);			
				pos += var.getDescriptor().getTailleOctets();

				if (codeVar == TypeRepere.date.getCode()) {
					try {
						setDate(ConversionTempsAtess.getDateFromHeureBCD(value));
						setTime(ConversionBase.HexaBCDToDecimal(value[3])*3600*1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (codeVar == TypeRepere.temps.getCode()) {
					int sec=value[1]<0 ? 256+value[1] : value[1];
					setTimeCount((value[0]*256+sec)*2* ConstantesParcoursATESS.pasCptTps
							* (long) (ConstantesParcoursATESS.resolutionTemps * 1000));

				} else if (codeVar == TypeRepere.distance.getCode()) {
					setNewDistance(Double.parseDouble(var.toString())* ConstantesParcoursATESS.pasCptDistance);
				} else if (codeVar == TypeRepere.diametreRoue.getCode()) {
					setDiametreRoue(Double.parseDouble(var.toString()));
					msg.setDiametreRoue(diametreRoue);
				}
				variablesLength += var.getDescriptor().getTailleOctets();
			}
		}
		int valeurCalculee=variablesLength+tailleIdentifiantRealID(id);
		if (longueurEvt!=valeurCalculee) {
			System.out.println("longueur de message erronee, code EV : "+descrEvent.getCode()+" "+valeurCalculee+" ->"+longueurEvt);
		}
	}

	protected void evIsRuptureAcquisition(Message msg){
		//		on remet tous les compteurs de temps  et les variable globale utilisée à 0
		resetCumulsTemps();
		setLastCptTempsTimeChange(0);
		nomFichierBinaire = "";
		setNbBloc(0);
		setCumulDistance(0);
		setDerniereValeurCptDistance(0);
		setCumulDistanceMax(0);

		setPremiereDateRencontree(true);
		// on défini le temps absolu à l'aide :
		// du nombre de jour depuis la date pivot  + nombre de millisecondes

		if (isDateTimeChanged()) {
			this.tc.addDate(getDate());
			this.tc.addTime(getTime()+getTimeCount());
			//			on stocke la date et l'heure dans 2 variables
			setDerniereValeurDate(getDate());
			setDerniereValeurHeure(getTime());
		}else{
			this.tc.addDate(getDerniereValeurDate());
			this.tc.addTime(getTime()+getTimeCount());
			setDerniereValeurHeure(getTime());
		}
		setDerniereValeurCptTemps(getTimeCount());

		//		int nbVarAna = 0;
		//		int nbVarDis = 0;
		//
		//		if (msg.getVariablesAnalogique() != null){
		//			List <VariableAnalogique> varsAna=msg.getVariablesAnalogique();
		//			nbVarAna = varsAna.size();
		//			for (int i = 0; i < nbVarAna; i++) {
		//
		//				if (varsAna.get(i).getDescriptor().getM_AIdentificateurComposant().getCode()!=com.faiveley.samng.principal.sm.data.descripteur.TypeRepere.temps.getCode()
		//						&& varsAna.get(i).getDescriptor().getM_AIdentificateurComposant().getCode()!=com.faiveley.samng.principal.sm.data.descripteur.TypeRepere.date.getCode()) {					
		//					//varsAna.get(i).setValeurChaine("0");
		//					if(varsAna.get(i).getDescriptor().getType()==Type.string)
		//						varsAna.get(i).setValeur("".getBytes());
		//					else{
		//						byte [] valeurTmp = (byte[])varsAna.get(i).getValeur();
		//						for (int a=0;a<valeurTmp.length;a++) {
		//							valeurTmp[a] = 0;
		//						}
		//						varsAna.get(i).setValeur(valeurTmp);
		//					}
		//				}
		//			}
		//		}
		//
		//		if (msg.getVariablesDiscrete() != null){
		//			List <VariableDiscrete> varsDis=msg.getVariablesDiscrete();
		//			nbVarDis = varsDis.size();
		//			for (int i = 0; i < nbVarDis; i++) {
		//				//varsDis.get(i).setValeurChaine("false");
		//				if (varsDis.get(i).getDescriptor().getM_AIdentificateurComposant().getCode()!=com.faiveley.samng.principal.sm.data.descripteur.TypeRepere.temps.getCode()
		//						&& varsDis.get(i).getDescriptor().getM_AIdentificateurComposant().getCode()!=com.faiveley.samng.principal.sm.data.descripteur.TypeRepere.date.getCode()) {
		//					if(varsDis.get(i).getDescriptor().getType()==Type.string)
		//						varsDis.get(i).setValeur("".getBytes());
		//					else{
		//						byte [] valeurTmp = (byte[])varsDis.get(i).getValeur();
		//						for (int a=0;a<valeurTmp.length;a++) {
		//							valeurTmp[a] = 0;
		//						}
		//						varsDis.get(i).setValeur(valeurTmp);
		//					}
		//				}
		//			}
		//		}
		//
		//		if (msg.getVariablesComplexe() != null) {
		//			// nbVar+=this.getVariablesComplexe().size();
		//			Collection<VariableComplexe> listeVarComp = msg.getVariablesComplexe();
		//			int nbSousVarMax = 0;
		//			for (VariableComplexe complexe : listeVarComp) {
		//				nbSousVarMax = complexe.getVariableCount();
		//				for (int i = 0; i < nbSousVarMax; i++) {
		//					TypeVariable typeVar=complexe.getEnfant(i).getDescriptor().getTypeVariable();
		//					if (typeVar==TypeVariable.VAR_DISCRETE) {
		//						//complexe.getEnfant(i).setValeurChaine("false");
		//						if(complexe.getEnfant(i).getDescriptor().getType()==Type.string)
		//							complexe.getEnfant(i).setValeur("".getBytes());
		//						else{
		//							byte [] valeurTmp = (byte[])complexe.getEnfant(i).getValeur();
		//							for (int a=0;a<valeurTmp.length;a++) {
		//								valeurTmp[a] = 0;
		//							}
		//							complexe.getEnfant(i).setValeur(valeurTmp);
		//						}
		//
		//					}else if (typeVar==TypeVariable.VAR_ANALOGIC) {
		//						//complexe.getEnfant(i).setValeurChaine("0");
		//						if(complexe.getEnfant(i).getDescriptor().getType()==Type.string)
		//							complexe.getEnfant(i).setValeur("".getBytes());
		//						else{
		//							byte [] valeurTmp = (byte[])complexe.getEnfant(i).getValeur();
		//							for (int a=0;a<valeurTmp.length;a++) {
		//								valeurTmp[a] = 0;
		//							}
		//							complexe.getEnfant(i).setValeur(valeurTmp);
		//						}
		//
		//					}
		//				}
		//			}
		//		}
	}

	protected void initialiserVariablesTempsDistance(){
		//		setDate(0);		
		//		setTime(0);
		//		setTimeCount(0);
		//		setNewDistance(0);
		//		setDiametreRoue(0);
		//		setTimeBeforeChange(0);
		setDateTimeChanged(false);
		setTimeCountChanged(false);
		setDistanceChanged(false);
	}

	protected void evIsRazCompteurTemps(){

		//		le temps voulu est en milliseconde donc on multiplie par 1000
		incrementerCumulTempsMax(ConstantesParcoursATESS.pasCptTps * (long) (ConstantesParcoursATESS.resolutionTemps * 1000)
				* ConstantesParcoursATESS.maxCptTps);
		setTimeCount(0);
		// la valeur du cumul de temps est égale à :
		//cumul temps +  valeur courante du compteur temps - dernière valeur du compteur temps


		if (getDerniereValeurCptTemps()!=0) {
			incrementerCumulTemps(3600  - getDerniereValeurCptTemps());
		}

		this.tc.addDate(getDate());
		this.tc.addTime(getTime());

		/////////////////////////////////////////////////////////		
		setPremiereDateRencontree(true);

		// on remet à zéro la dernière valeur du compteur temps
		setDerniereValeurCptTemps(0);

		if(isDistanceChanged()){
			incrementerCumulDistance(getNewDistance() - getDerniereValeurCptDistance());
			setDerniereValeurCptDistance(getNewDistance());
		}
	}

	protected void evIsRazCompteurDistance(){

		double rapport=getCumulDistance()/getIncRazDist();
		double ecartEntier=Math.abs(Math.round(rapport)-rapport);
		if (ecartEntier<0.00001 && rapport!=Math.round(rapport)) {
			rapport=Math.round(rapport);
		}
		setLastRaz(((int)(rapport))*getIncRazDist());
		double newRaz=getLastRaz()+getIncRazDist();		
		if (getNewDistance()==getIncRazDist()&&getDerniereValeurCptDistance()!=0) {
			setCumulDistance(getLastRaz());
		}else{
			setCumulDistance(newRaz);
		}

		if(isTimeCountChanged()){
			//la valeur du cumul detemps est égale à :
			//cumul temps +  valeur courante du compteur temps - dernière valeur du compteur temps
			incrementerCumulTemps(getTimeCount()  - getDerniereValeurCptTemps()); 
			this.tc.addTime(getDerniereValeurHeure() + getCumulTemps() + getCumulTempsMax());
			setDerniereValeurCptTemps(getTimeCount());
		}
		setDerniereValeurCptDistance(0);
	}

	protected void evIsChangementHeure(){
		//cas des événements de changement d'heure 
		//on ne doit ici pas prendre en compte le cumul du compteur temps 
		//mais on ajoute la valeur de la variable LatchTemps(tempsAvantChgt) pour les événements suivants
		
		if (!isPremiereDateRencontree()) {
			setPremiereDateRencontree(true);

			this.tc.addTime(getTime()+getTimeCount());
		}

		this.tc.addDate(getDate());

		setDateBeforeChange(getDerniereValeurDate());
		setTimeCountBeforeChange((long)getDerniereValeurCptTemps());
		setTimeBeforeChange((long)getDerniereValeurHeure());

		//on remet les cumul de temps  à 0
		resetCumulsTemps();
		//on stocke les valeurs des date, heure et compteur temps
		setDerniereValeurCptTemps(getTimeCount());
		setDerniereValeurDate(getDate());
		setDerniereValeurHeure(getTime());

		this.tc.addTime(getTime()+getTimeCount());

		if(isDistanceChanged()){
			incrementerCumulDistance(getNewDistance() - getDerniereValeurCptDistance());
			setDerniereValeurCptDistance(getNewDistance());
		}
	}

	protected void newDateHeure(int id){
		if (!isPremiereDateRencontree()) {
			setPremiereDateRencontree(true);
			setCumulTemps(0);
			this.tc.addDate(getDate());
			this.tc.addTime(getTime()+getTimeCount());

			if (isTimeCountChanged()) {
				setDerniereValeurCptTemps(getTimeCount());
			}
			setDerniereValeurDate(getDate());
			setDerniereValeurHeure(getTime());
		}else if(isTimeCountChanged()){
			this.tc.addDate(getDate());
			if (getTimeCount()>getDerniereValeurCptTemps()) {
				incrementerCumulTemps(getTimeCount() - getDerniereValeurCptTemps());
			}
			this.tc.addTime(getTime()+getTimeCount());
			setDerniereValeurCptTemps(getTimeCount());
			setDerniereValeurDate(getDate());
			setDerniereValeurHeure(getTime());
		}
	}

	protected void noNewDateHeure(int id){
		if (isPremiereDateRencontree() && isTimeCountChanged()) {
			if (id!=15&&(getTimeCount()>getDerniereValeurCptTemps())) {
				incrementerCumulTemps(getTimeCount() - getDerniereValeurCptTemps());
			}
			this.tc.addTime(getTimeCount()+getTime());
			setDerniereValeurCptTemps(getTimeCount());
		}
	}

	protected void recupererIncrementRazDistance(){
		double coefDir=0;
		try {
			coefDir=GestionnaireDescripteurs.getDescripteurVariableAnalogique(TypeRepere.distance.getCode()).getCoefDirecteur();
		} catch (Exception e) {
			coefDir=1;
		}
		setIncRazDist(ConstantesParcoursATESS.maxCptDistance*coefDir*0.01*10);
	}

	protected AtessMessage gererTempsMsg(int start,AtessMessage msg,Evenement ev,DescripteurComposite descrComp,int id,DescripteurEvenement descrEvent){

		recupererIncrementRazDistance();

		msg.setEvenement(ev);	
		int longueurEvt=descrEvent.getLongueur();
		
		gereVariablesTypeRepere(descrComp, id, msg, start, descrEvent, ev);	

		if (ev.getM_ADescripteurComposant().getM_AIdentificateurComposant().getNom().equals("ApparitionTensionBT")) {
			cpt++;
		}

		//Algorithme de gestion du temps
		if (ev.isRuptureAcquisition()&& isLastMsgNotRuptAcq()) {
			evIsRuptureAcquisition(msg);
		}
		else if (ev.isRazCompteurTemps()) {
			evIsRazCompteurTemps();
		}
		//cas des événements marqués de razCompteurDistance dans le fichier xml
		else if(ev.isRazCompteurDistance()){
			evIsRazCompteurDistance();
		}
		else if (ev.isChangementHeure()) {
			evIsChangementHeure();
		} 
		//tous les autres cas
		else {
			//si la variable dateHeure est valorisée
			if (isDateTimeChanged()) {
				newDateHeure(id);
			} else {
				noNewDateHeure(id);
			}
			//			si la distance est valorisée on incrémente le compteur distance
			//la valeur du cumul detemps est égale à :
			//cumul distance +  valeur courante du compteur distance - dernière valeur du compteur distance
			if(isDistanceChanged()){
				incrementerCumulDistance(getNewDistance() - getDerniereValeurCptDistance());
				setDerniereValeurCptDistance(getNewDistance());
			}
		}

		ParseurAdapteur padapt=new ParseurAdapteur();
		padapt.gererEvNonDate(msg);

		if (isPremiereDateRencontree())
			msg.setAbsoluteTime(this.tc.getCurrentDateAsMillis());
		msg.setAbsoluteDistance(getCumulDistance());
		msg.setLongueur(longueurEvt);

		return msg;
	}

	protected boolean isLastMsgNotRuptAcq(){
		try {
			return (!lastMessage.getEvenement().isRuptureAcquisition());
		} catch (Exception e) {
			return true;
		}

	}

	@Override
	protected AtessMessage chargerMessage(int start, boolean isExploration) throws ParseurBinaireException {
		initialiserVariablesTempsDistance();
		
		AtessMessage msg = new AtessMessage();
		msg.setMessageId(start);
		int id = gestionID(start);

		DescripteurComposite descrComp = null;
		DescripteurEvenement descrEvent = null;

		// Get the event id in the table
		int length = descrTable.getLength();
		
		for (int i = 0; i < length; i++) {
			descrComp = (DescripteurComposite) descrTable.getEnfant(i);
			descrEvent = (DescripteurEvenement) descrComp.getEnfant(0);
			
			if (descrEvent != null && descrEvent.getM_AIdentificateurComposant().getCode() == id) {
				break;
			}
			
			descrComp = null;
		}

		// The code read in the message is not found in the table
		if (descrComp == null) {
			gererIDNonTrouve(msg, id);
		}

		Evenement ev = GestionnairePool.getInstance().getEvent(id);
		DescripteurEvenement descEvt = (DescripteurEvenement) ev.getM_ADescripteurComposant();

		// Cas où l'évévement est dans la table evt/var et dans le fichier xml
		if (descEvt.getNom() == null) {
			gererNomEvtNonTrouve(id, ev, msg);
		} else {
			gererNomEvtTrouve(id, descEvt, ev, msg);
		}

		msg = gererTempsMsg(start, msg, ev, descrComp, id, descrEvent);
		msg.deepTrimToSize();
		
		return msg;
	}

    private Long getVitesseLimiteTrainFromDonneesTrainKVB(AtessMessage msg) {
	VariableComplexe variableDt = (VariableComplexe) msg.getVariable("Dt");
	for (AVariableComposant enfant : variableDt.getEnfants()) {
	    if ("Dt.vitesseLimiteTrain".equals(enfant.getDescriptor().getM_AIdentificateurComposant().getNom())) {
		VariableAnalogique vitesseLimiteTrain = (VariableAnalogique) enfant;
		return (Long) vitesseLimiteTrain.getCastedValeur();
	    }
	}
	return null;
    }
		
    private Long getLongueurTrainFromDonneesTrainKVB(AtessMessage msg) {
	VariableComplexe variableDt = (VariableComplexe) msg.getVariable("Dt");
	for (AVariableComposant enfant : variableDt.getEnfants()) {
	    if ("Dt.longueur".equals(enfant.getDescriptor().getM_AIdentificateurComposant().getNom())) {
		VariableAnalogique longueurTrain = (VariableAnalogique) enfant;
		return (Long) longueurTrain.getCastedValeur();
	    }
	}
	return null;
	}

	@Override
	protected AVariableComposant chargerReperes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ADescripteurComposant chargerTableEvtVariable() throws ParseurBinaireException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean checkOctetsFF(int start, int noFF) {
		boolean valid = true;
		int crt = 0;
		while (crt < noFF) {
			if ((this.message[start + crt] & 0xff) != codeFF) {
				valid = false;
				break;
			}
			crt++;
		}
		return valid;
	}

	public void resetCumulsTemps(){
		setCumulTempsMax(0);
		setCumulTemps(0);
	}

	@Override
	public void clear() {
		setDerniereValeurHeure(0);
		setDerniereValeurDate(0);
		setDerniereValeurCptTemps(0);
		resetCumulsTemps();
		setLastCptTempsTimeChange(0);
		nomFichierBinaire = "";
		setPremiereDateRencontree(false);
		setNbBloc(0);
	}


	@Override
	protected byte getBits(byte crtByte, int offset, int length) {
		byte bits = 0;
		// get the usefull bits from the current byte
		int mask = 0x0000;
		for (int i = offset; i < length + offset; i++) {
			// if is a used bit then set 1 on that position of the mask
			// offset = 3 => 1110 0000
			mask += Math.pow(2, i);
		}
		bits = (byte) (crtByte & mask); // offset = 3 => bits = xxx0 0000
		return bits;
	}

	@Override
	public HashMap<Integer, Boolean> getCrcEnregistrements() {
		return crcEnregistrements;
	}
	@Override
	public String getNomFichierXml(String nomFichier) throws Exception {
		String nomTableAssocciationEvtVars=getNomTableEvenementVariable();
		try {
			ParseurTableAssociationEvVars.getInstance().chargerNomFichierXML(nomFichier);
		} catch (Exception e) {
			throw new ParseurXMLException(Messages.getString("ChargeurParcours.9") + nomTableAssocciationEvtVars,true);
		}

		String nomFichierXML=ParseurTableAssociationEvVars.getInstance().getFichierDescr();
		return nomFichierXML;
	}

	@Override
	public void setCrcEnregistrements(HashMap<Integer, Boolean> crcEnregistrements) {
		this.crcEnregistrements = crcEnregistrements;

	}
	@Override
	protected void setFlags(Message crtMsg) throws ParseurBinaireException {

		// set the flag for the current event
		Flag flag = this.loadedFlags.get(crtMsg.getEvenement()
				.getM_ADescripteurComposant().getM_AIdentificateurComposant()
				.getNom());

		crtMsg.setFlag(flag);

		if (crtMsg.getFlag() != null) {
			if (lastMessage != null && crtMsg.getFlag().getLabel().contains("{")) { //$NON-NLS-1$

				Flag flagLastMsg = lastMessage.getFlag();

				if(flagLastMsg==null ||!flagLastMsg.getLabel().contains("{")){
					Flag fl = new Flag(0, "}", lastMessage.getEvenement() //$NON-NLS-1$
							.getM_ADescripteurComposant()
							.getM_AIdentificateurComposant().getNom());
					if (flagLastMsg != null) {
						flagLastMsg.appendFlag(fl);
					} else {
						lastMessage.setFlag(fl);
					}
				}else{
					if(lastMessage.getEvenement().getM_ADescripteurComposant().getM_AIdentificateurComposant().getCode()!=crtMsg.getEvenement().getM_ADescripteurComposant().getM_AIdentificateurComposant().getCode()){
						Flag fl = new Flag(0, crtMsg.getFlag().getLabel().replace("{",""), lastMessage.getEvenement() //$NON-NLS-1$
								.getM_ADescripteurComposant()
								.getM_AIdentificateurComposant().getNom());


						crtMsg.setFlag(fl);
					}
				}
			}
		}
		setSegmentsTemp(crtMsg, this.lastMessage);
		setSegmentsDistance(crtMsg, this.lastMessage);

		// if time rupture for this message, set flag for this message
		TypeRupture rupture = TableRuptures.getInstance().getRuptureTime(
				crtMsg.getMessageId());
		if (rupture == TypeRupture.RUPTURE_TEMP_CALCULEE && dateValide) {
			// if (rupture == TypeRupture.RUPTURE_TEMP_CALCULEE && dateValide &&
			// !lastMessage.getEvenement().isChangementHeure()) {

			//zzzFlagRupt

			Flag newFlag = GestionnaireFlags.getInstance().getFlag(GestionnaireFlags.getInstance().ID_TC);

			if (flag != null) {
				// if exists flag, then append to it
				flag.appendFlag(newFlag);
			} else {
				// if not exist flag create one
				crtMsg.setFlag(newFlag);

			}
		}

		// if distance rupture for this message, set flag for this message
		rupture = TableRuptures.getInstance().getRuptureDistance(
				crtMsg.getMessageId());
		if (rupture == TypeRupture.RUPTURE_DIST_CALCULEE) {
			// if (rupture == TypeRupture.RUPTURE_DIST_CALCULEE &&
			// !lastMessage.getEvenement().isChangementHeure()) {
			//			zzzFlagRupt
			flag = crtMsg.getFlag();
			Flag newFlag = GestionnaireFlags.getInstance().getFlag(GestionnaireFlags.getInstance().ID_DC);

			if (flag != null) {
				// if exists flag, then append to it
				if (!crtMsg.getEvenement().isChangementHeure())
					flag.appendFlag(newFlag);
			} else {
				// if not exist flag create one
				crtMsg.setFlag(newFlag);
			}

			//			byte[] val=new byte[1];
			//			val[0]=0;

			//			List<VariableAnalogique> listva=crtMsg.getVariablesAnalogique();
			//			if (listva!=null) {
			//			int nbVarAna=listva.size();
			//			for (int i = 0; i < nbVarAna; i++) {
			//			AVariableComposant v=listva.get(i);
			//			v.setValeurChaine("0");
			//			v.setValeurObjet("0");
			//			v.setValeur(val);
			//			}
			//			}

			//			List<VariableComplexe> listvc=crtMsg.getVariablesComplexe();
			//			if (listvc!=null) {
			//			int nbVarComp=listvc.size();
			//			for (int i = 0; i < nbVarComp; i++) {
			//			AVariableComposant v=listvc.get(i);
			//			v.setValeurChaine("0");
			//			v.setValeurObjet("0");
			//			v.setValeur(val);
			//			}
			//			}

			//			List<VariableComposite> listvcompo=crtMsg.getVariablesComposee();
			//			if (listvcompo!=null) {
			//			int nbVarCompo=listvcompo.size();
			//			for (int i = 0; i < nbVarCompo; i++) {
			//			AVariableComposant v=listvcompo.get(i);
			//			v.setValeurChaine("0");
			//			v.setValeurObjet("0");
			//			v.setValeur(val);
			//			}
			//			}

			//			List<VariableDiscrete> listvd=crtMsg.getVariablesDiscrete();
			//			if (listvd!=null) {
			//			int nbVarDis=listvd.size();
			//			for (int i = 0; i < nbVarDis; i++) {
			//			AVariableComposant v=listvd.get(i);
			//			v.setValeurChaine("0");
			//			v.setValeurObjet("0");
			//			v.setValeur(val);
			//			}
			//			}
		}
	}
	
	protected boolean isItSegmentTempBeginning(Message crtMsg, Message prevMsg) 
	{
		// si l'événement est un événement de rupture d'acquisition ou bien le premier message du parcours
		if (prevMsg == null
				|| crtMsg.getEvenement().isRuptureAcquisition()
				&& !prevMsg.getEvenement().isRuptureAcquisition()
				&& prevMsg.getEvenement().getM_ADescripteurComposant()
				.getM_AIdentificateurComposant().getCode() != crtMsg
				.getEvenement().getM_ADescripteurComposant()
				.getM_AIdentificateurComposant().getCode()) 
		{
			return (true);
		}
		else if (prevMsg != null) 
		{
			// on vérifie si il y a un changement d'heure
			boolean isTimeEvtChgt = crtMsg.getEvenement().isChangementHeure();
				
			if (isTimeEvtChgt) 
			{
				return (true);

			}
			else if (crtMsg.getAbsoluteTime() < prevMsg.getAbsoluteTime()
						&& !prevMsg.getEvenement().isChangementHeure()) 
			{
				return (true);
			} 
			else if (prevMsg.isMessageIncertitude()
						&& !crtMsg.isMessageIncertitude()) 
			{
				return (true);
			} 
			else if (crtMsg.getEvenement().isRuptureAcquisition()
						&& prevMsg.getEvenement().getM_ADescripteurComposant()
						.getM_AIdentificateurComposant().getCode() == crtMsg
						.getEvenement().getM_ADescripteurComposant()
						.getM_AIdentificateurComposant().getCode()) 
			{
				return (true);
			}
		}
		
		return (false);
	}	
	@Override
	protected void setSegmentsTemp(Message crtMsg, Message prevMsg) 
	{
		if (crtMsg != null) 
		{
			// si l'événement est un événement de rupture d'acquisition ou bien le premier message du parcours
			if (prevMsg == null
					|| crtMsg.getEvenement().isRuptureAcquisition()
					&& !prevMsg.getEvenement().isRuptureAcquisition()
					&& prevMsg.getEvenement().getM_ADescripteurComposant()
					.getM_AIdentificateurComposant().getCode() != crtMsg
					.getEvenement().getM_ADescripteurComposant()
					.getM_AIdentificateurComposant().getCode()) {
				// si on est sur les messages de 2 à n
				if (prevMsg != null) {

					// si un segment a déjà été créé on teste si l'événement
					// précédent n'est pas le début du segment
					// si c'est le cas on enregistre le segment et on en créé un
					// autre
					if (this.lastSegTemp != null) {
						TableRuptures.getInstance().ajouterRuptureTemps(
								crtMsg.getMessageId(),
								TypeRupture.RUPTURE_TOTALE_TEMPS_ABSOLU);
						TableRuptures.getInstance().ajouterRuptureDistance(
								crtMsg.getMessageId(),
								TypeRupture.RUPTURE_TOTALE_DISTANCE_ABSOLU);
						enregistrerSegmentTemps(prevMsg);
						SegmentTemps segment = new SegmentTemps();
						segment.setStartMsgId(crtMsg.getMessageId());
						segment.setTempInitial(crtMsg.getAbsoluteTime());
						segment.setOffsetDebut(crtMsg.getOffsetDebut());
						
						segment.setTempCorrige(segment.getTempInitial());
						this.lastSegTemp = segment;
					}
				}
				// on est sur le message 1: on créé un segment
				else {
					SegmentTemps segment = new SegmentTemps();
					segment.setStartMsgId(crtMsg.getMessageId());
					segment.setTempInitial(crtMsg.getAbsoluteTime());
					segment.setOffsetDebut(crtMsg.getOffsetDebut());
					
					segment.setTempCorrige(segment.getTempInitial());
					this.lastSegTemp = segment;
				}
			}

			else if (prevMsg != null) {
				// on vérifie si il y a un changement d'heure
				boolean isTimeEvtChgt = crtMsg.getEvenement().isChangementHeure();
				if (isTimeEvtChgt) {
					// si il y a un segment précédent
					if (this.lastSegTemp != null) {
						enregistrerSegmentTemps(prevMsg);
					}

					TableRuptures.getInstance().ajouterRuptureTemps(
							crtMsg.getMessageId(),
							TypeRupture.CHANGEMENT_HEURE_RUPTURE);

					SegmentTemps segment = new SegmentTemps();
					segment.setStartMsgId(crtMsg.getMessageId());
					segment.setTempInitial(crtMsg.getAbsoluteTime());
					segment.setOffsetDebut(crtMsg.getOffsetDebut());
					
					segment.setTempCorrige(segment.getTempInitial());
					this.lastSegTemp = segment;
				}

				else if (crtMsg.getAbsoluteTime() < prevMsg.getAbsoluteTime()
						&& !prevMsg.getEvenement().isChangementHeure()) {
					TableRuptures.getInstance().ajouterRuptureTemps(
							crtMsg.getMessageId(),
							TypeRupture.RUPTURE_TEMP_CALCULEE);
					enregistrerSegmentTemps(prevMsg);

					SegmentTemps segment = new SegmentTemps();
					segment.setStartMsgId(crtMsg.getMessageId());
					segment.setTempInitial(crtMsg.getAbsoluteTime());
					segment.setOffsetDebut(crtMsg.getOffsetDebut());
					
					segment.setTempCorrige(segment.getTempInitial());
					this.lastSegTemp = segment;
				} else if (prevMsg.isMessageIncertitude()
						&& !crtMsg.isMessageIncertitude()) {
					enregistrerSegmentTemps(prevMsg);
					// a new segment starts
					SegmentTemps segment = new SegmentTemps();
					segment.setStartMsgId(crtMsg.getMessageId());
					segment.setTempInitial(crtMsg.getAbsoluteTime());
					segment.setOffsetDebut(crtMsg.getOffsetDebut());
					
					segment.setTempCorrige(segment.getTempInitial());
					this.lastSegTemp = segment;
				} else if (crtMsg.getEvenement().isRuptureAcquisition()
						&& prevMsg.getEvenement().getM_ADescripteurComposant()
						.getM_AIdentificateurComposant().getCode() == crtMsg
						.getEvenement().getM_ADescripteurComposant()
						.getM_AIdentificateurComposant().getCode()) {
					TableRuptures.getInstance().ajouterRuptureTemps(
							crtMsg.getMessageId(),
							TypeRupture.RUPTURE_TOTALE_TEMPS_ABSOLU);
					TableRuptures.getInstance().ajouterRuptureDistance(
							crtMsg.getMessageId(),
							TypeRupture.RUPTURE_TOTALE_DISTANCE_ABSOLU);
					enregistrerSegmentTemps(prevMsg);
					// a new segment starts
					SegmentTemps segment = new SegmentTemps();
					segment.setStartMsgId(crtMsg.getMessageId());
					segment.setTempInitial(crtMsg.getAbsoluteTime());
					segment.setOffsetDebut(crtMsg.getOffsetDebut());
					
					segment.setTempCorrige(segment.getTempInitial());
					this.lastSegTemp = segment;
				}
			}
		} else {
			// close the segment
			if (this.lastSegTemp != null) {
				enregistrerSegmentTemps(prevMsg);
			}
		}
	}
	@Override
	protected byte[] setVariableValue(AVariableComposant var, byte[] valueOrig) throws BadStructureFileException {
		Poids poidsPremierOctetVar = null;
		Poids poidsPremierBitVar = null;
		AVariableComposant v = null;
		long retVal = 0;
		Type typeVar;
		byte value[] = null;
		int posVar = 0;
		int vo_size = valueOrig.length;

		if (var instanceof VariableComposite) {

			// it is a composed variable
			VariableComposite varComp = ((VariableComposite) var);

			int remainedBits = 0;
			int size = varComp.getVariableCount();
			for (int k = 0; k < size; k++) {
				// extract the value for each variable
				v = varComp.getEnfant(k);
				v.setParent(var);
				poidsPremierOctetVar = v.getDescriptor().getPoidsPremierOctet();
				poidsPremierBitVar = v.getDescriptor().getPoidsPremierBit();
				value = new byte[vo_size];
				value = Arrays.copyOfRange(valueOrig,0,valueOrig.length);
				//				for (int ii = 0; ii < vo_size; ii++) {
				//				value[ii] = valueOrig[ii];
				//				}

				// inversion des octets et des bits de la variable complexe
				if (varComp.getDescriptor().getPoidsPremierBit() == Poids.LSB) {
					value = reverseBits(value);
				}

				if (varComp.getDescriptor().getPoidsPremierOctet() == Poids.LSB) {
					value = reverseOctets(value);
				}
				
				typeVar = v.getDescriptor().getType();
				
				if(v.getDescriptor().getOffsetComposant() != null){
					remainedBits = 7 - v.getDescriptor().getOffsetComposant().getBitOffset();
					//if(varComp.getDescriptor().getPoidsPremierOctet() == Poids.LSB){
						//calcul de la taille de la variable en octet pour déterminer la position de départ de la variable (posVar)
						//if(tailleOctets == -1){
						//	if(v.getDescriptor().getTaille() < 8 )
							//	tailleOctets = 1;
						//	else {
							//	if( (v.getDescriptor().getTaille()%8) > 0){
							//		tailleOctets = v.getDescriptor().getTaille()/8 +1;
							//	}else{
								//	tailleOctets = v.getDescriptor().getTaille()/8;
								//}
							//}
								
						//}
							
						//posVar = value.length  -  v.getDescriptor().getOffsetComposant().getByteOffset() - tailleOctets;
//						posVar = value.length  -  1 - v.getDescriptor().getOffsetComposant().getByteOffset();
					//}
					//else
						posVar =  v.getDescriptor().getOffsetComposant().getByteOffset();
				}
				
//				if(varComp.getDescriptor().getM_AIdentificateurComposant().getCode()==16383){
//					if(v.getDescriptor().getOffsetComposant() != null){
//					System.out.println("offset present");
//					}
//					System.out.println("variable : " + v.getDescriptor().getM_AIdentificateurComposant().getNom());
//					System.out.println("posVar : " + posVar);
//					System.out.println("remainedBits : " + remainedBits);
//				}
				
				// if it's a value given in bits not octets, then gets the bits
				if (typeVar == Type.uintXbits || typeVar == Type.BCD4
						|| typeVar == Type.BCD8 || typeVar == Type.intXbits
						|| typeVar == Type.boolean1) {

					retVal = setXbitsValue2(v, value, posVar, remainedBits);
					remainedBits = (int) (retVal & 0xFFFFFFFF);
					posVar = (int) ((retVal >> 32) & 0xFFFFFFFF);
					//v.setValeur(null);

				}
				// cas d'une variable de type reserved
				else if (typeVar == Type.reserved) {

					// on ne récupère aucune valeur pour ce type de variable

					// on teste si le nombre de bits actuels à lire est
					// inférieur ou supérieur à la taille de la variable
					// reserved
					int diffBits = remainedBits + v.getDescriptor().getTailleBits();
					// s'il est necessaire d'avancer d'un ou plusieurs octets(si
					// diffBits>8)
					int nbOctets = Math.abs(diffBits) / 8;
					if (nbOctets > 0) {
						// on calcul le nombre de bits restants
						remainedBits = Math.abs(diffBits) % 8;
						// on avance d'autant d'octets que nécessaire
						posVar = posVar + nbOctets;
					} else {

						remainedBits = Math.abs(remainedBits
								+ v.getDescriptor().getTailleBits());

					}

				} else {
					// if the previous var was a bitwise variable, and the
					// remained bits are different
					// than 0 we should increment the posVar position as this
					// was not made before

					// cas o`u la variable commence au début d'un octet
					if (remainedBits == 0) {

						byte valTemp[] = Arrays.copyOfRange(value, posVar,
								posVar + v.getDescriptor().getTailleOctets());

						if (poidsPremierOctetVar == Poids.LSB)
							valTemp = reverseOctets(valTemp);

						if (poidsPremierBitVar == Poids.LSB)
							valTemp = reverseBits(valTemp);

						v.setValeur(valTemp);

					}
					// cas où la varaible commence à un nb bit donné dans
					// l'octet
					else {

						byte valTemp[] = Arrays.copyOfRange(value, posVar,
								posVar + v.getDescriptor().getTailleOctets()
								+ 1);
						int mask = (int) Math.pow(2, 8 - remainedBits) - 1;
						// on met à 0 tous les bits inutiles sur le premier
						// octet
						valTemp[0] = (byte) (valTemp[0] & mask);
						// on place le tableau de byte dans un BigInteger
						BigInteger bigInt = new BigInteger(valTemp);
						// on fait un décalage d'autant de bits que necessaire
						bigInt = bigInt.shiftRight(8 - remainedBits);

						byte valVar[] = bigInt.toByteArray();
						if (poidsPremierOctetVar == Poids.LSB)
							valVar = reverseOctets(valVar);

						if (poidsPremierBitVar == Poids.LSB)
							valVar = reverseBits(valVar);

						//v.setValeur(valVar);
						//v.setValeurChaine(v.toString());
						v.setValeur(valVar);
					}

					posVar += (v.getDescriptor().getTailleOctets());

				}
			}
			var.setValeur(value);
		} else {


			value = new byte[vo_size];
			value = Arrays.copyOfRange(valueOrig,0,valueOrig.length);
			//			for (int ii = 0; ii < vo_size; ii++) {
			//			value[ii] = valueOrig[ii];
			//			}

			// reverse the order of octets or bytes if the poids are set on LSB
			if (var.getDescriptor().getPoidsPremierBit() == Poids.LSB) {
				value = reverseBits(value);
			}
			if (var.getDescriptor().getPoidsPremierOctet() == Poids.LSB) {
				value = reverseOctets(value);
			}

			// set the value
			try{
				var.setValeur(value);
			}
			catch(NullPointerException ex){
				System.out.println("Null pointer");
			}
		}
		return value;
	}
	@Override
	protected long setXbitsValue2(AVariableComposant v, byte[] value,
			int posVar, int startBitIdx) {

		// recuperation de la longueur de la variable
		int tailleVar = v.getDescriptor().getTailleBits();
		Poids poidsPremierBitVar = v.getDescriptor().getPoidsPremierBit();
		Poids poidsPremierOctetVar = v.getDescriptor().getPoidsPremierOctet();
		Type typeVariable = v.getDescriptor().getType();
		byte b = 0;
		int mask;
		int extractedBytesCnt = 0;
		int positionsToShift = 0;
		// anciennes spécifications
		// // chaque fois que l'on recommence un nouvel octet,
		// // on considère que la varaible précédente(dans l'octet n'est ni en
		// // poids-1er bit = MSB ni LSB)
		// if (startBitIdx == 0) {
		// firstPartLSB = false;
		// firstPartMSB = false;
		// }
		// cas où la variable est entièrement stockée dans l'octet courant
		// longueur de la variable inférieure au nombre de bits restant dans
		// l'octet
		int remainedBits;
		if (tailleVar <= 8 - startBitIdx) {

			// récupération du nombre de bits restant dans l'octet
			remainedBits = 8 - startBitIdx;

			// // ajout olivier
			// // cas d'une variable en poids-1er-bit=LSB
			// if (poidsPremierBitVar == Poids.LSB) {
			// // on inverse les bits de tout le tableau d'octet
			// byte[] bitsReverse = reverseBits(value);
			//
			// // cas d'un BCD4
			// {
			//
			// // nouvelles spécifications: on décale d'autant de bits que
			// // la
			// // le bit de début de lecture(étant donné que l'on inverse
			//
			// b = (byte) (bitsReverse[posVar] >>> startBitIdx);
			//
			// }
			//
			// /*
			// * anciennes spécifications // si la varaible précédente dans le
			// * meme octet n'est pas en // MSB // if (firstPartLSB) { // //
			// * si on est pas au début de l'octet // if (startBitIdx > 0) { //
			// //
			// * on récupère les bits de droite // // l'octet et on décale //
			// * b = (byte) (bitsReverse[posVar] >>> remainedBits -length ); //
			// } // } // } //
			// * la première varable dans l'octet est en poids-1er-bit=LSB //
			// * firstPartLSB = true;
			// */
			// }
			// // if (poidsPremierBitVar == Poids.MSB)
			// else {
			//
			// // nouvelles spécifications
			// b = (byte) (value[posVar] >>> remainedBits - tailleVar);
			//
			// /*
			// * anciennes spécifications // if (firstPartMSB) { // if
			// * (startBitIdx > 0) { // b = (byte) (value[posVar] >>>
			// * remainedBits - length); // } // } // } // la première
			// * variable dans l'octet est en poids-1er-bit=MSB //
			// * firstPartMSB = true;
			// */
			//
			// }
			//
			//
			// // on calcule le masque pour récupérer seulement les bits qui
			// nous
			// // interressent
			// mask = (int) Math.pow(2, tailleVar) - 1;
			// // on applique le masque pour récupérer seulement les bits qui
			// nous
			// // interressent
			// cacheMemory[0] = (byte) (b & mask);

			
			byte[] tabVarOneByte = new byte[1];
			//byte byteTemp = value[posVar];

			//byteTemp = (byte) (byteTemp & mask);

			tabVarOneByte[0] = value[posVar];
			
			BigInteger bigInt = null;
			positionsToShift = remainedBits - tailleVar;
			if (poidsPremierBitVar == Poids.LSB) {
				positionsToShift = startBitIdx;
				tabVarOneByte = reverseBits(tabVarOneByte);
			}
			byte tabVar[] = new byte[1];
			mask = (int) Math.pow(2, tailleVar) - 1;
			if (positionsToShift > 0) {
				
				
				if (typeVariable == Type.intXbits || typeVariable == Type.int16
						|| typeVariable == Type.int24
						|| typeVariable == Type.int32
						|| typeVariable == Type.int64
						|| typeVariable == Type.int8) {
					// on créé un BigInteger signé
					bigInt = new BigInteger(tabVarOneByte);
					bigInt = bigInt.shiftRight(positionsToShift);
				}

				else {
					bigInt = new BigInteger(1, tabVarOneByte);
					bigInt = bigInt.shiftRight(positionsToShift);
				}
				tabVar = bigInt.toByteArray();
				tabVar[0] = (byte) (tabVar[0] & mask);
			} else {
				
				tabVar[0] = (byte) (tabVarOneByte[0] & mask);
				
			}
			

			cacheMemory[0] = (byte) tabVar[0];
			// un seul octet a été extrait
			extractedBytesCnt = 1;

			// on met à jour l'index du bit à prendre dans l'octet
			startBitIdx = startBitIdx + tailleVar;

		}

		// cas où la variable a une longueur supérieure
		// au nombre de bits restant dans l'octet
		else {

			int octetCourant = posVar;

			// récupération de ce qui reste dans le premier octet de la variable
			remainedBits = 8 - startBitIdx;

//			// cas d'une variable en MSB
//			if (poidsPremierBitVar == Poids.MSB) {
//
//				// nouvelles spécifications
//				b = (byte) (value[posVar]);
//
//				// anciennes spécifications
//				// if (startBitIdx > 0) {
//				// // on prend les bits tout à gauche
//				// // calcul du masque
//				// mask = (int) (255 - (Math.pow(2, startBitIdx) - 1));
//				// // décalage
//				// b = (byte) ((value[octetCourant] & mask) >>> startBitIdx);
//				// cacheMemory[extractedBytesCnt] = b;
//				// } else {
//				// // sinon on prend les bits de droite
//				// mask = (int) (Math.pow(2, remainedBits) - 1);
//				// b = (byte) ((value[octetCourant] & mask));
//				// cacheMemory[extractedBytesCnt] = b;
//				// }
//
//			}
//
//			// cas d'une variable en LSB
//			// if (v.getDescriptor().getPoidsPremierBit() == Poids.LSB) {
//			else {
//
//				// nouvelles spécifications
//				// on récupère l'octet entier
//				b = (byte) (value[posVar]);
//
//				// anciennes spécifications
//				// b = bitsReverse[octetCourant];
//				// if (startBitIdx > 0) {
//				// b = (byte) (b >>> startBitIdx);
//				// }
//			}
			b = (byte) (value[posVar]);
			// on ne prend que les bits qui nous interressent
			mask = (int) Math.pow(2, remainedBits) - 1;
			// on met l'octet dans le tableau d'octets temporaire
			cacheMemory[extractedBytesCnt] = (byte) ((b & mask));

			// on incrémente pour obtenir le prochain octet
			extractedBytesCnt++;
			// on met a jour le nombre de bits restant pour la variable
			tailleVar -= remainedBits;

			boolean varTerminee = false;
			while (!varTerminee) {

				// l'octet courant change d'indice
				octetCourant = posVar + extractedBytesCnt;
				// si le nombre de bits restants de la variable est inférieure à
				// 8
				if (tailleVar - 8 <= 0) {

					startBitIdx = tailleVar;

					positionsToShift = 8 - tailleVar;

					varTerminee = true;

				} else {
					// on récupère l'octet entier

					tailleVar -= 8;

					if (tailleVar <= 0)
						varTerminee = true;

				}

				cacheMemory[extractedBytesCnt] = (byte) (value[octetCourant]);
				extractedBytesCnt++;
			}
		}

		// on créé un BigInteger qui permet de contenir toute la variable et de
		// faire des décalages à gauche ou à droite
		// cacheMemory : tableau d'octets de taille fixe
		byte[] valuesByteArray = Arrays.copyOfRange(cacheMemory, 0,
				extractedBytesCnt);
		tailleVar = v.getDescriptor().getTailleBits();
		if (tailleVar <= 8) {
			if (valuesByteArray.length == 2) {

				BigInteger bigInt = null;

				if (typeVariable == Type.intXbits || typeVariable == Type.int16
						|| typeVariable == Type.int24
						|| typeVariable == Type.int32
						|| typeVariable == Type.int64
						|| typeVariable == Type.int8) {
					// on créé un BigInteger signé
					bigInt = new BigInteger(valuesByteArray);
					bigInt = bigInt.shiftRight(positionsToShift);
				}

				else {
					bigInt = new BigInteger(1, valuesByteArray);
					bigInt = bigInt.shiftRight(positionsToShift);
				}

				byte tabVar[] = bigInt.toByteArray();
				// correction issue 754 : interpretation des variables à cheval
				// sur plusieurs octets
				// si variable de 8 bits et moins : une fois le décalage à
				// droite fait, on obtient toujours un tableau de 1 octet
				// et il n'est pas nécessaire d'appliquer un masque car il a
				// déjà été appliqué sur l'octet de poids fort

				// byte tabVar2[] = new byte[1];
				// if (poidsPremierBitVar == Poids.MSB) {
				// mask = (int) Math.pow(2, tailleVar) - 1;
				// }
				// //if (v.getDescriptor().getPoidsPremierBit() == Poids.LSB) {
				// else {
				// tabVar = reverseBits(tabVar);
				// mask = (int) Math.pow(2, tailleVar) - 1;
				// if (tabVar.length > 1)
				// tabVar[0] = (byte) (tabVar[1] >> (8 -
				// v.getDescriptor().getTaille()));
				// else
				// tabVar[0] = (byte) (tabVar[0] >> (8 -
				// v.getDescriptor().getTaille()));
				//
				// }
				// tabVar2[0] = (byte) (tabVar[0] & mask);

				if (poidsPremierBitVar == Poids.LSB) {
					tabVar = reverseBits(tabVar);
					int taille = tailleVar;
					if (taille % 8 != 0
							&& ((tabVar.length < (taille / 8)) || (tabVar.length > (taille / 8)))) {
						int decalage = 8 - (tailleVar % 8);
						tabVar[0] = (byte) (tabVar[0] >>> decalage);
					}
				}

				v.setValeur(tabVar);
			} else {
				v.setValeur(valuesByteArray);
			}
		}

		else {
			if (positionsToShift > 0) {
				Type t = typeVariable;
				byte[] valueArr = null;

				BigInteger bigInt = null;
				// cas de tout les entiers signés
				// il sont enregistrés en complément à 2 dans le fichier de
				// parcours
				if (t == Type.intXbits || t == Type.int16 || t == Type.int24
						|| t == Type.int32 || t == Type.int64 || t == Type.int8) {
					// on créé un BigInteger signé
					bigInt = new BigInteger(valuesByteArray);
					bigInt = bigInt.shiftRight(positionsToShift);
				}
				// on créé un BigInteger non signé
				// if (t == Type.uintXbits || t == Type.uint8 || t ==
				// Type.uint16
				// || t == Type.uint24 || t == Type.uint32
				// || t == Type.uint64 || t == Type.real32
				// || t == Type.real64 || t == Type.dateHeureBCD)
				else {
					bigInt = new BigInteger(1, valuesByteArray);
					bigInt = bigInt.shiftRight(positionsToShift);

				}

				valueArr = bigInt.toByteArray();

				if (poidsPremierBitVar == Poids.LSB) {
					valueArr = reverseBits(valueArr);
					int taille = tailleVar;
					if (taille % 8 != 0
							&& ((valueArr.length < (taille / 8)) || (valueArr.length > (taille / 8)))) {
						int decalage = 8 - (tailleVar % 8);
						valueArr[0] = (byte) (valueArr[0] >>> decalage);
					}

				}

				if (poidsPremierOctetVar == Poids.LSB)
					valueArr = reverseOctets(valueArr);

				v.setValeur(valueArr);
			} else {

				if (poidsPremierBitVar == Poids.LSB)
					valuesByteArray = reverseBits(valuesByteArray);

				if (poidsPremierOctetVar == Poids.LSB)
					valuesByteArray = reverseOctets(valuesByteArray);

				v.setValeur(valuesByteArray);
			}
		}
		// cas des intxbits: on doit reproduire le complément à 2 sur l'octet de
		// poids fort
		if (typeVariable == Type.intXbits) {
			byte[] valeurVariable = (byte[]) v.getValeur();// tagValCor

			byte octetSigne = valeurVariable[0];
			int tailleVariable = tailleVar;

			if (tailleVariable < 8) {

				int maskOctetSigne = (int) Math.pow(2, tailleVariable - 1);
				octetSigne = (byte) (octetSigne & maskOctetSigne);

				if (octetSigne > 0) {
					int maskOctet = (int) Math.pow(2, tailleVariable) - 1;
					valeurVariable[0] = (byte) (valeurVariable[0] + (255 - maskOctet));

					v.setValeur(valeurVariable);
				}

			} else if (tailleVariable > 8) {
				if (tailleVariable % 8 > 0) {
					int indiceBitsigne = tailleVariable % 8;
					int maskOctetSigne = (int) Math.pow(2, indiceBitsigne - 1);
					octetSigne = (byte) (octetSigne & maskOctetSigne);

					if (octetSigne > 0) {
						int maskOctet = (int) Math.pow(2, indiceBitsigne) - 1;
						valeurVariable[0] = (byte) (valeurVariable[0] + (255 - maskOctet));

						v.setValeur(valeurVariable);
					}
				}
			}

		}

		// on met à jour l'indice de début de lecture pour la variable suivante
		if (startBitIdx >= 8)
			startBitIdx = 0;

		if (startBitIdx > 0) {
			// if there were some bits used from a byte, but not all the byte,
			// this byte has left
			// some bits that are used by another variable = > the position must
			// be decremented
			posVar = posVar + extractedBytesCnt - 1; // if we still have
			// bits, we should
			// remain on the last
			// byte
		} else {
			posVar = posVar + extractedBytesCnt; // if no bits in the last
			// byte, go to next after
			// the last
		}

		// we return from here both the remained bits and the incremented pos
		// var
		// in order to avoid making the same computations outside
		long retVal = (((long) posVar) << 32) | startBitIdx;

		return retVal;

	}

	@Override
	protected boolean verifierCRCBloc(int crc, byte[] donneesBloc) {
		IStrategieControle controlTable = new Checksum();
		boolean valid = true;

		if (!controlTable.controlerCRC(crc, donneesBloc)) {
			valid = false;
		}
		return valid;
	}
	@Override
	protected boolean verifierCRCConfiguration() {
		return true;
	}
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
	@Override
	protected void enregistrerSegmentDistance(Message msg) throws InconsistentFileException {
		// TODO Auto-generated method stub
		super.enregistrerSegmentDistance(msg);
	}
	@Override
	protected void enregistrerSegmentTemps(Message msg) {
		// TODO Auto-generated method stub
		super.enregistrerSegmentTemps(msg);
	}

	private void fixerOctetsVides(String extension) {
		if (extension.toUpperCase().equals("LPB")) {
			FIN_CODE=-1;
		}else{
			FIN_CODE=-18;
		}
	}

	public double getTimeCount() {
		return timeCount;
	}

	public void setTimeCount(double cptTempsCourant) {
		this.timeCount = cptTempsCourant;
		setTimeCountChanged(true);
	}

	public double getDiametreRoue() {
		return diametreRoue;
	}

	public void setDiametreRoue(double diametreRoue) {
		this.diametreRoue = diametreRoue;
	}

	public boolean isTimeCountChanged() {
		return timeCountChanged;
	}

	public void setTimeCountChanged(boolean newCptTpsEncountered) {
		this.timeCountChanged = newCptTpsEncountered;
	}

	public boolean isDateTimeChanged() {
		return dateTimeChanged;
	}

	public void setDateTimeChanged(boolean newDateHeureEncountered) {
		this.dateTimeChanged = newDateHeureEncountered;
	}

	public double getDate() {
		return date;
	}

	public void setDate(double newDateValue) {
		this.date = newDateValue;
		setDateTimeChanged(true);
	}

	public boolean isDistanceChanged() {
		return distanceChanged;
	}

	public void setDistanceChanged(boolean newDistanceEncoutered) {
		this.distanceChanged = newDistanceEncoutered;
	}

	public double getNewDistance() {
		return newDistance;
	}

	public void setNewDistance(double newDistanceValue) {
		this.newDistance = newDistanceValue;
		setDistanceChanged(true);
	}

	public long getTime() {
		return time;
	}

	public void setTime(long newHeureValue) {
		this.time = newHeureValue;
	}

	protected void incrementerCumulTempsMax(double increment){
		setCumulTempsMax(getCumulTempsMax()+increment);
	}

	protected void incrementerCumulTemps(double increment){
		setCumulTemps(getCumulTemps()+increment);
	}

	protected void incrementerCumulDistance(double increment){
		setCumulDistance(getCumulDistance()+increment);
	}

	public double getDateBeforeChange() {
		return dateBeforeChange;
	}

	public void setDateBeforeChange(double dateBeforeChange) {
		this.dateBeforeChange = dateBeforeChange;
		setDateTimeChanged(true);
	}

	public long getTimeCountBeforeChange() {
		return timeCountBeforeChange;
	}

	public void setTimeCountBeforeChange(long timeBeforeChange) {
		this.timeCountBeforeChange = timeBeforeChange;
	}

	public long getTimeBeforeChange() {
		return timeBeforeChange;
	}

	public void setTimeBeforeChange(long timeBeforeChange) {
		this.timeBeforeChange = timeBeforeChange;
	}

	public int getFirstMsgSansTemps() {
		return firstMsgSansTemps;
	}

	public void setFirstMsgSansTemps(int firstMsgSansTemps) {
		this.firstMsgSansTemps = firstMsgSansTemps;
	}

	public boolean isMessageSansTemps() {
		return messageSansTemps;
	}

	public void setMessageSansTemps(boolean messageSansTemps) {
		this.messageSansTemps = messageSansTemps;
	}

	public double getLastRaz() {
		return lastRaz;
	}

	public void setLastRaz(double lastRaz) {
		this.lastRaz = lastRaz;
	}

	public double getIncRazDist() {
		return incRazDist;
	}

	public void setIncRazDist(double incRazDist) {
		this.incRazDist = incRazDist;
	}
	
	public boolean isKVBLoaded() {
		return this.kvbLoaded;
	}
}
