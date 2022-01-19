package com.faiveley.samng.principal.sm.parseurs.parseursJRU;

import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.codeFF;
import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.tailleBlocData;
import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.tailleEntete;
import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.tailleTableEvenement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.logging.SamngLogger;
import com.faiveley.samng.principal.sm.calculs.conversionTemps.ConversionTempsJRU;
import com.faiveley.samng.principal.sm.controles.CRC16CCITT;
import com.faiveley.samng.principal.sm.controles.IStrategieControle;
import com.faiveley.samng.principal.sm.controles.ReturnCRC;
import com.faiveley.samng.principal.sm.controles.util.CRC16CCITTHash;
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
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.descripteur.donneeBlocComposant.ADonneeBlocComposant;
import com.faiveley.samng.principal.sm.data.enregistrement.ErrorType;
import com.faiveley.samng.principal.sm.data.enregistrement.Evenement;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.enregistrement.tom4.GestionnaireSynchronisationGroupes;
import com.faiveley.samng.principal.sm.data.flag.Flag;
import com.faiveley.samng.principal.sm.data.flag.GestionnaireFlags;
import com.faiveley.samng.principal.sm.data.identificateurComposant.IdentificateurEvenement;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.AParcoursComposant;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosFichierSamNg;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TableLangueNomUtilisateur;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.Langage;
import com.faiveley.samng.principal.sm.data.variableComposant.Paquets;
import com.faiveley.samng.principal.sm.data.variableComposant.TableSousVariable;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.ChaineDynamique;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.StructureDynamique;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.TableauDynamique;
import com.faiveley.samng.principal.sm.erreurs.AExceptionSamNG;
import com.faiveley.samng.principal.sm.erreurs.BadArgumentInFileException;
import com.faiveley.samng.principal.sm.erreurs.BadEventCodeException;
import com.faiveley.samng.principal.sm.erreurs.BadFileLengthException;
import com.faiveley.samng.principal.sm.erreurs.BadHeaderInfoException;
import com.faiveley.samng.principal.sm.erreurs.BadStructureFileException;
import com.faiveley.samng.principal.sm.erreurs.ParseurBinaireException;
import com.faiveley.samng.principal.sm.erreurs.ParseurXMLException;
import com.faiveley.samng.principal.sm.fabriques.AFabriqueParcoursAbstraite;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.formats.DistinctionExtension;
import com.faiveley.samng.principal.sm.formats.FormatJRU;
import com.faiveley.samng.principal.sm.formats.FormatSAM;
import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurParcoursJRUExplorer;
import com.faiveley.samng.principal.sm.missions.jaxb.TypeDocument;
import com.faiveley.samng.principal.sm.missions.jaxb.TypeListeMission;
import com.faiveley.samng.principal.sm.missions.jaxb.TypeListeRegroupementTemps;
import com.faiveley.samng.principal.sm.missions.jaxb.TypeMission;
import com.faiveley.samng.principal.sm.missions.jaxb.TypeRefMission;
import com.faiveley.samng.principal.sm.missions.jaxb.TypeRegroupementTemps;
import com.faiveley.samng.principal.sm.parseurs.AParseurParcours;
import com.faiveley.samng.principal.sm.parseurs.BridageFormats;
import com.faiveley.samng.principal.sm.parseurs.Messages;
import com.faiveley.samng.principal.sm.parseurs.ParseurFlags;
import com.faiveley.samng.principal.sm.parseurs.adapteur.adapteur.ParseurAdapteur;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.principal.sm.segments.SegmentTemps;
import com.faiveley.samng.principal.sm.segments.TableSegments;
import com.faiveley.samng.principal.sm.segments.ruptures.TableRuptures;
import com.faiveley.samng.principal.sm.segments.ruptures.TypeRupture;

public class ParseurParcoursJRU extends AParseurParcours {
	private int indiceBlocCourant;
	private double incRazDist;
	private double lastRaz = 0;
	private double date;
	private double dateBeforeChange;
	private long time;
	private long timeBeforeChange;
	private long timeCountBeforeChange;
	private double timeCount;
	private double newDistance;
	private double diametreRoue;
	private boolean dateTimeChanged;
	private boolean timeCountChanged;
	private boolean distanceChanged;
	private int firstMsgSansTemps = 0;
	private int pos = 0;
	private int posDate = 0; // Offset, en octet, de la position de la Date
								// (celle
	// contenue dans le header) dans un message JRU
	private int posTime = 0; // Offset, en octet, de la position du Time (celui
								// contenu
	// dans le header) dans un message JRU
	private int posMode = 0; // Offset, en octet, de la position du Mode (celui
								// contenu
	// dans le header) dans un message JRU

	// bool�en permettant d'activer/d�sactiver le caract�re bloquant de certain
	// test
	// test sur la diff�rence longueur message enregistr�e (L_MESSAGE_JRU) /
	// longueur message calcul�e
	private boolean bad_l_message_jru_length_msg = false;
	// test sur la diff�rence longueur message enregistr�e (L_MESSAGE) /
	// longueur message calcul�e
	private boolean bad_l_message_length_msg = false;

	// liste utilis�e pour stocker temporairement les tableaux d'octets de tous
	// les messages
	private List<Byte[]> listeTableauOctetBrutMsg = new ArrayList<Byte[]>(0);

	private long positionPaquetSuivantUsing_L_Packet = 0;
	private boolean lecturePaquetEnCours = false;
	private long positionVarEntetePaquet = 0;

	public static ParseurParcoursJRU getInstance() {
		try {
			if (instance == null)
				instance = new ParseurParcoursJRU();

			if (ActivationExplorer.getInstance().isActif()) {
				return ParseurParcoursJRUExplorer.getInstance();
			}

			return (ParseurParcoursJRU) instance;
		} catch (ClassCastException ex) {
			if (ActivationExplorer.getInstance().isActif()) {
				return new ParseurParcoursJRUExplorer();
			}
			return new ParseurParcoursJRU();
		}
	}

	@Override
	public void parseRessource(String fileName, boolean explorer, int deb,
			int fin) throws AExceptionSamNG {
		// reset des variables
		resetCumulsTemps();

		// if (explorer) {
		// parsTableAssocEvVars=ParseurJRUTableAssociationEvVarsExplorer.getInstance();
		// }else{
		// parsTableAssocEvVars=ParseurJRUTableAssociationEvVars.getInstance();
		// }

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

		// if (explorer) {
		// TableSegmentsExplorer.getInstance().empty();
		// TablesRupturesExplorer.getInstance().clear();
		// factory = FabriqueParcoursExplorer.getInstance();
		// }else{
		// TableSegments.getInstance().empty();
		// TableRuptures.getInstance().clear();
		// factory = FabriqueParcours.getInstance();
		// }

		TableSegments.getInstance().empty();
		TableRuptures.getInstance().clear();
		AFabriqueParcoursAbstraite factory = FabriqueParcours.getInstance();

		FIN_CODE = -1;
		this.message = loadBinaryFile(new File(fileName), 0, -1);

		this.nomFichierBinaire = fileName;
		currentFileName = new String(fileName);

		if (BridageFormats.getInstance().getFormatFichierOuvert(fileName)
				.getFjru() == FormatJRU.bru) {
			chargerlentete(this.message);
		}

		if (BridageFormats.getInstance().getFormatFichierOuvert(fileName)
				.getFjru() == FormatJRU.bru) {
			FileInputStream inStream;
			String cheminFichierEntetes = RepertoiresAdresses.JRU_liste_tailleEntete_JRU;
			try {
				inStream = new FileInputStream(new File(cheminFichierEntetes)); //$NON-NLS-1$
				Properties props = new Properties();
				props.load(inStream);
				ConstantesParcoursJRU.tailleEntete = Short
						.parseShort((String) props
								.get("" + ConstantesParcoursJRU.normeJRU)); //$NON-NLS-1$
			} catch (FileNotFoundException e) {
				SamngLogger
						.getLogger()
						.error(Messages.getString("ParseurParcoursJRU.2") + "(" + cheminFichierEntetes + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				throw new ParseurBinaireException();

			} catch (IOException e) {
				SamngLogger
						.getLogger()
						.error(Messages.getString("ParseurParcoursJRU.3") + "(" + cheminFichierEntetes + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				throw new ParseurBinaireException();
			} catch (NullPointerException ex) {
				throw new ParseurBinaireException();
			} catch (NumberFormatException ex) {
				SamngLogger
						.getLogger()
						.error(Messages.getString("ParseurParcoursJRU.4") + "(" + cheminFichierEntetes + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				throw new ParseurBinaireException();
			}
		} else {
			ConstantesParcoursJRU.tailleEntete = 0;
		}

		if (deb != 0 || fin != -1) {
			this.messagebrut = Arrays.copyOfRange(message, deb, fin);
		} else {
			this.messagebrut = Arrays.copyOfRange(message, tailleEntete,
					this.message.length);
		}

		// this.messagebrut = Arrays.copyOfRange(message, tailleEntete,
		// this.message.length);

		traitementFichier(factory, fileName, explorer);

		if (BridageFormats.getInstance().getFormatFichierOuvert(fileName)
				.getFjru() == FormatJRU.bru) {
			pretraiterFichierBRU(this.message);
		}

		// TODO: v�rifier si cela fonctionne
		// if(!verifierCRCConfiguration()){
		// String errStr =
		// Messages.getString("errors.blocking.badConfigurationCrc");
		// throw new BadHeaderCrcException(errStr);
		// }

		// Si on est appel� par l'action explorer fichier, pas celle d'ouvrir
		// fichier...
		if (explorer) {
			try {
				// Il faut faire une analyse minimum du fichier juste pour cr�er
				// les missions
				// Algo : DR28 ed A - SAM 5.5.0 Ouvertures partielles.doc
				chargerDataExplore(fileName);
			} catch (DatatypeConfigurationException e) {
				e.printStackTrace();
			}
		} else {
			Message[][] messages = chargerData(deb, fin);
			// create DATA
			factory.creerData(messages);
			// if (explorer) {
			factory.creerReperes(GestionnairePool.getInstance().getReperes());
			// }
		}
	}

	private void chargerlentete(byte[] file) {
		// Entete
		ConstantesParcoursJRU.tailleBlocData = (new BigInteger(
				Arrays.copyOfRange(file, 0, 1)).shortValue());
		ConstantesParcoursJRU.indiceMajeurVersionJRU = new BigInteger(
				Arrays.copyOfRange(file, 1, 2)).intValue();
		ConstantesParcoursJRU.indiceMineurVersionJRU = new BigInteger(
				Arrays.copyOfRange(file, 2, 3)).intValue();

		// BridageFormats.getInstance().getFormatFichierOuvert(
		// this.currentFileName).getFjru().setVersion(
		// ConstantesParcoursJRU.indiceMajeurVersionJRU + "."
		// + ConstantesParcoursJRU.indiceMineurVersionJRU);

		ConstantesParcoursJRU.normeJRU = new BigInteger(Arrays.copyOfRange(
				file, 3, 5)).intValue();

		BridageFormats.getInstance()
				.getFormatFichierOuvert(this.currentFileName).getFjru()
				.setVersion(ConstantesParcoursJRU.normeJRU + ""); //$NON-NLS-1$

		ConstantesParcoursJRU.nbBitsLongueurMessage = new BigInteger(
				Arrays.copyOfRange(file, 5, 6)).intValue();
		ConstantesParcoursJRU.structureTimeSpecSecondes = new byte[8];
		ConstantesParcoursJRU.structureTimeSpecSecondes = Arrays.copyOfRange(
				file, 16, 20);
		ConstantesParcoursJRU.structureTimeSpecNanoSecondes = new byte[8];
		ConstantesParcoursJRU.structureTimeSpecNanoSecondes = Arrays
				.copyOfRange(file, 20, 24);
	}

	private void pretraiterFichierBRU(byte[] file)
			throws ParseurBinaireException {
		this.crc = new CRC16CCITTHash();
		byte codebaliseS1 = 86;
		byte codebaliseS11 = -59;
		byte codebaliseS2 = -87;
		byte codebaliseS22 = 58;

		byte[] crcEnreg = new byte[2];

		int indiceDebutMsg = -1;
		int indicemessage = -1;

		boolean bal1open = false;
		boolean bal2open = false;

		int length = file.length;
		byte[] fichierTraite1 = new byte[length];
		int indiceOctetTraite = 0;
		int indiceOctetDansMessage = 0;

		int longueurM = 0;

		List<Byte> listeOctetMessage = new ArrayList<Byte>();

		boolean crcMessageCourantVerifie = false;
		// /////////////////////////////////////
		// /////////////////////////////////////
		int i = tailleEntete;
		for (i = tailleEntete; i < length; i++) {

			if (((i - tailleEntete) % tailleBlocData == 0)) {
				if (file[i] == codebaliseS1) {
					if (bal2open || bal1open) {

						SamngLogger.getLogger().error(
								Messages.getString("ParseurParcoursJRU.7")); //$NON-NLS-1$
						throw new ParseurBinaireException();
					}
					crcMessageCourantVerifie = false;
					if (indicemessage > -1) {
						// ajout du tableau d'octets � la hashmap id message -
						// tableau octet brut
						Byte[] tableauOctetMsg = new Byte[listeOctetMessage
								.size()];
						for (int cpt = 0; cpt < listeOctetMessage.size(); cpt++) {
							tableauOctetMsg[cpt] = listeOctetMessage.get(cpt);
						}
						listeTableauOctetBrutMsg.add(tableauOctetMsg);
						listeOctetMessage = new ArrayList<Byte>();
					}

					bal1open = true;
					indiceDebutMsg = indiceOctetTraite;
					indicemessage++;

					// calcul L_Message
					byte[] L_Message = new byte[2];
					L_Message[0] = file[i + 2];
					L_Message[1] = file[i + 3];
					longueurM = (new BigInteger(L_Message).intValue() & 0xFFFF) >> (16 - ConstantesParcoursJRU.nbBitsLongueurL_Message);
					indiceOctetDansMessage = 0;

				} else if (file[i] == codebaliseS2) {
					if (bal2open || bal1open) {

						SamngLogger.getLogger().error(
								Messages.getString("ParseurParcoursJRU.5")); //$NON-NLS-1$
						throw new ParseurBinaireException();
					}
					bal2open = true;
				} else {
					SamngLogger.getLogger().error(
							Messages.getString("ParseurParcoursJRU.6")); //$NON-NLS-1$
					throw new ParseurBinaireException();
				}
			} else if ((i - tailleEntete) % tailleBlocData == tailleBlocData - 1) {
				if (file[i] == codebaliseS11) {
					if (bal2open || !bal1open) {

						SamngLogger.getLogger().error(
								Messages.getString("ParseurParcoursJRU.8")); //$NON-NLS-1$
						throw new ParseurBinaireException();
					}
					bal1open = false;
				} else if (file[i] == codebaliseS22) {
					if (!bal2open || bal1open) {

						SamngLogger.getLogger().error(
								Messages.getString("ParseurParcoursJRU.9")); //$NON-NLS-1$
						throw new ParseurBinaireException();
					}
					bal2open = false;
				} else {

					SamngLogger.getLogger().error(
							Messages.getString("ParseurParcoursJRU.10")); //$NON-NLS-1$

					throw new ParseurBinaireException();
				}
			} else {
				if (indiceOctetDansMessage < longueurM) {
					// octet � r�cup�rer
					fichierTraite1[indiceOctetTraite] = file[i];
					indiceOctetTraite++;
					indiceOctetDansMessage++;
				} else {
					if (!crcMessageCourantVerifie) {
						// verif CRC
						int crc = 0;
						boolean crcEnregValide = false;
						// test si CRC � cheval sur 2 blocs
						if (longueurM % (tailleBlocData - 2) == tailleBlocData - 2 - 1) {
							// CRC � cheval sur 2 bloc
							// on r�cup�re la valeur du crc
							crcEnreg[0] = file[i];
							crcEnreg[1] = file[i + 3];
							crc = new BigInteger(crcEnreg).intValue();
						} else {
							// on r�cup�re la valeur du crc
							crcEnreg = Arrays.copyOfRange(file, i, i + 2);
							crc = new BigInteger(crcEnreg).intValue();
						}

						byte[] tab = new byte[0];

						tab = Arrays.copyOfRange(fichierTraite1,
								indiceDebutMsg, indiceOctetTraite);

						try {
							crcEnregValide = verifierCRCBloc(crc, tab);
						} catch (RuntimeException e) {
							e.printStackTrace();
						}

						if (!crcEnregValide) {
							ReturnCRC ret;
							ret = getMessageCRCs(crc, tab, this.crc);
							listCRC_Error
									.add(new CRC_Erreur(indicemessage, ret));
						}

						crcMessageCourantVerifie = true;
					}
				}
			}
			// ajout de l'octet au tableau d'octets du message courant
			listeOctetMessage.add(file[i]);
		}

		// ajout du tableau d'octets � la liste des tableaux d'octets
		Byte[] tableauOctetMsg = new Byte[listeOctetMessage.size()];
		for (int cpt = 0; cpt < listeOctetMessage.size(); cpt++) {
			tableauOctetMsg[cpt] = listeOctetMessage.get(cpt);
		}
		listeTableauOctetBrutMsg.add(tableauOctetMsg);

		this.message = new byte[indiceOctetTraite];
		this.message = Arrays.copyOfRange(fichierTraite1, 0, indiceOctetTraite);
	}

	private String getNomTableEvenementVariable() {
		return (RepertoiresAdresses.ConfigurationJRU_XML
				+ BridageFormats.getFormat(this.currentFileName).getFjru()
						.getVersion() + ".xml"); //$NON-NLS-1$
	}

	private void chargerConstantes() {
		ConstantesParcoursJRU.resolutionTemps = 1;
		ConstantesParcoursJRU.pasCptTps = 1;
	}

	@Override
	protected void traitementFichier(AFabriqueParcoursAbstraite factory,
			String fileName, boolean explorer) throws AExceptionSamNG {
		resetParser();

		this.tc = new ConversionTempsJRU("01/01/2000 00:00:00.000"); //$NON-NLS-1$
		//
		// check if the file is ok
		if (fileName == null || fileName.length() == 0
				|| !new File(fileName).exists()) {
			throw new IllegalArgumentException("No resource to parse"); //$NON-NLS-1$
		}
		// check the extension file
		List<String> listeExtensionsJRU = new ArrayList<String>(
				FormatSAM.JRU.getExtensions());
		String extensionFichier = fileName.substring(fileName.length() - 4,
				fileName.length());
		if (!DistinctionExtension.isExtensionOF(listeExtensionsJRU,
				extensionFichier)) { //$NON-NLS-1$ //$NON-NLS-2$
			throw new IllegalArgumentException(
					Messages.getString("errors.blocking.invalidFileExtension")); //$NON-NLS-1$
		}
		File binaryFile = new File(fileName);

		this.message = loadBinaryFile(binaryFile, 0, -1);
		if (this.message == null) {
			throw new IllegalArgumentException("No resource to parse"); //$NON-NLS-1$
		}
		if (this.message.length < 4)
			throw new BadFileLengthException(
					Messages.getString("errors.blocking.invalidHeaderLength1")); //$NON-NLS-1$

		// create the Parcours
		factory.creerParcours();
		factory.creerEntete(null, null);

		// LOAD CONSTANTS
		chargerConstantes();

		// ************* PARSE FLAGS file **********/

		ParseurFlags.getInstance().parseRessource(
				RepertoiresAdresses.FLAGS_FILE_DIR + "flags.xml", false, 0, -1);
		this.loadedFlags = ParseurFlags.getInstance().chargerFlags();

		// ************* PARSE XML file ********/
		String nomTableAssocciationEvtVars = getNomTableEvenementVariable();

		try {
			ParseurJRUTableAssociationEvVars.getInstance()
					.chargerNomFichierXML(nomTableAssocciationEvtVars);
		} catch (Exception e) {
			throw new ParseurXMLException(
					Messages.getString("ChargeurParcours.9") //$NON-NLS-1$
							+ nomTableAssocciationEvtVars, true);
		}

		String nomFichierXML = "JRU" //$NON-NLS-1$
				+ BridageFormats.getFormat(this.currentFileName).getFjru()
						.getVersion() + ".xml";
		
		GestionnairePool.getInstance().chargerFichierXml(
				RepertoiresAdresses.xml + File.separator + nomFichierXML, fileName);

		ConstantesParcoursJRU.nbBitsLongueurL_Message = GestionnairePool
				.getInstance()
				.getVariable("L_MESSAGE_JRU").getDescriptor().getTailleBits(); //$NON-NLS-1$

		// chargement de la table evt/var
		try {
			ParseurJRUTableAssociationEvVars.getInstance().parseRessource(
					nomTableAssocciationEvtVars, false, 0, -1);
		} catch (Exception e) {
			System.out.println("ParseurJRUTableAssociationEvVars a �chou�"); //$NON-NLS-1$
		}
		ADescripteurComposant descripteursEvt = ParseurJRUTableAssociationEvVars
				.getInstance().getTableEvVars();

		// ADescripteurComposant descripteursEvt = chargerTableEvtVariable();

		// create the table
		factory.creerTableEvtVar(descripteursEvt);

		// affichage ordonn� des variables
		DescripteurComposite descrComposite = (DescripteurComposite) descripteursEvt;
		HashMap<Integer, DescripteurComposite> hashMapCodeDescComp = new HashMap<Integer, DescripteurComposite>();
		for (int o = 0; o < descrComposite.getLength(); o++) {
			hashMapCodeDescComp.put((Integer) descrComposite.getEnfant(o)
					.getEnfant(0).getM_AIdentificateurComposant().getCode(),
					(DescripteurComposite) descrComposite.getEnfant(o));
		}
		GestionnaireDescripteurs.setMapEvenementVariables(hashMapCodeDescComp);
		// ********* LOAD Info ******************
		InfosFichierSamNg info = (InfosFichierSamNg) GestionnairePool
				.getInstance().getXMLParser().getInfosFichier();
		if (info == null)
			throw new ParseurXMLException(
					Messages.getString("errors.blocking.errorLoadingXMLFile"), true); //$NON-NLS-1$
		info.setNomFichierParcoursBinaire(binaryFile.getAbsolutePath());
		info.setNomFichierXml(nomFichierXML);
		factory.creerInfoFichier(info);

		ConstantesParcoursJRU.maxCptTps = (short) ParseurJRUTableAssociationEvVars
				.getInstance().getMaxCompteurTemps();
		ConstantesParcoursJRU.maxCptDistance = (short) ParseurJRUTableAssociationEvVars
				.getInstance().getMaxCompteurDistance();
	}

	@Override
	protected ADonneeBlocComposant chargerCRCConfiguration() {
		return null;
	}

	@Override
	protected Message[][] chargerData(int deb, int fin)
			throws ParseurBinaireException {

		FileInputStream inStream;
		String cheminFichierEntetes = RepertoiresAdresses.JRU_liste_erreurs_bloquantes_JRU;
		
		try {
			inStream = new FileInputStream(new File(cheminFichierEntetes));
			Properties props = new Properties();
			props.load(inStream);
			if (props.get("bad_l_message_jru_length_msg").equals("1")) //$NON-NLS-1$ //$NON-NLS-2$
				bad_l_message_jru_length_msg = true;
			if (props.get("bad_l_message_length_msg").equals("1")) //$NON-NLS-1$ //$NON-NLS-2$
				bad_l_message_length_msg = true;

		} catch (Exception e) {
			System.out
					.println("erreur de chargement du fichier des erreurs bloquantes"); //$NON-NLS-1$
		}

		List<Message> goodMsgs = new ArrayList<Message>();
		List<Message> badMsgs = new ArrayList<Message>();
		lastMessage = null;

		// USE_HASHMAP
		// TableEvtVar table = (TableEvtVar) FabriqueParcours.getInstance()
		// .getParcours().getComposant(INDEXES.INDEX_TABLE_ASSOC);
		descrTable = (DescripteurComposite) ParseurJRUTableAssociationEvVars
				.getInstance().getTableEvVars();

		// int nbEnfant = descrTable.getLength();
		// for (int y =0; y<nbEnfant;y++) {
		// mapCodeEvtDescComposite.put(((DescripteurComposite)descrTable.getEnfant(y)).getEnfant(0).getM_AIdentificateurComposant().getCode(),
		// (DescripteurComposite)
		// ((DescripteurComposite)descrTable.getEnfant(y)));
		// }

		// on d�fini la position de lecture de l'octet � l'octet de d�but du
		// premier enregistrement

		int octetCourant = 0;
		if (deb != 0) {
			octetCourant = deb;
		}

		int tailleTableauMessage = this.message.length;

		if (fin != -1 && fin != 0) {
			tailleTableauMessage = fin;
		}

		// lorsque l'on atteint la fin du fichier, peu importe ce qui a �t� lu
		// on doit s'arreter
		if (octetCourant >= tailleTableauMessage) {
			SamngLogger
					.getLogger()
					.error(Messages
							.getString("errors.blocking.invalidFileStructure1"));
			ActivatorData.getInstance().getPoolDonneesVues()
					.put("fichierVide", new String("true"));
			throw new BadFileLengthException(
					Messages.getString("errors.blocking.invalidFileStructure1"));
		}

		int cpt = 1;
		int cptMsg = 1;
		indiceBlocCourant = 1;
		int compteurOctetsBrut = 0;

		// D�termine si le fichier est un BRU ou JRU
		boolean BRUFile = BridageFormats.getInstance()
				.getFormatFichierOuvert(this.currentFileName).getFjru() == FormatJRU.bru;

		while (octetCourant + 2 <= tailleTableauMessage - 1
				&& !Thread.interrupted()) {
			ActivatorData
					.getInstance()
					.getVp()
					.setValeurProgressBar(
							octetCourant * 100 / (tailleTableauMessage));
			ActivatorData.getInstance().getVpExportExplorer()
					.setValeurProgressBar(octetCourant, tailleTableauMessage);
			cpt = 0;

			while (BRUFile ? cpt <= tailleBlocData - 2 : cpt == 0) {
				// VueProgressBar.getInstance().setCourant(pos);
				ActivatorData
						.getInstance()
						.getVp()
						.setValeurProgressBar(
								octetCourant * 100 / (tailleTableauMessage));
				ActivatorData
						.getInstance()
						.getVpExportExplorer()
						.setValeurProgressBar(octetCourant,
								tailleTableauMessage);

				// if the blocks structure is ok load the message
				Message msg = new Message();
				if (octetCourant < tailleTableauMessage) {
					// chargement du message
					msg = chargerMessage(octetCourant, false);

					// on valorise l'id de message � partir de la taille de son
					// tableau d'octet brut
					// on valorise l'attribut data du message avec le tableau
					// d'octet brut
					boolean mauvaisCRC = false;
					if (listeTableauOctetBrutMsg != null
							&& listeTableauOctetBrutMsg.size() > 0) {

						msg.setMessageId(compteurOctetsBrut);
						msg.setOffsetDebut(octetCourant);
						msg.setOffsetFin(octetCourant + msg.getLongueur());

						int nbOctetMsg = listeTableauOctetBrutMsg
								.get(cptMsg - 1).length;

						byte[] tableauOctetMsgBrut = new byte[nbOctetMsg];
						Byte[] tabtmp = listeTableauOctetBrutMsg
								.get(cptMsg - 1);
						for (int cptTab = 0; cptTab < nbOctetMsg; cptTab++) {
							tableauOctetMsgBrut[cptTab] = tabtmp[cptTab];
						}
						msg.setMessageData(tableauOctetMsgBrut);

						int nbErreurCRC = listCRC_Error.size();
						if (nbErreurCRC > 0) {

							int cptErreurCRC = 0;
							ReturnCRC retourCRC = null;

							while (!mauvaisCRC && cptErreurCRC < nbErreurCRC) {
								if (listCRC_Error.get(cptErreurCRC).getIndice() == cptMsg - 1) {
									retourCRC = listCRC_Error.get(cptErreurCRC)
											.getRet();
									mauvaisCRC = true;
								}
								cptErreurCRC++;
							}

							// si le CRC est incorrect
							if (mauvaisCRC) {
								// r�cup�ration des chaines � afficher: crc
								// calcul� et crc enregistr�
								String calculCRC = retourCRC
										.getCRCString(retourCRC.getCalculCRC());
								String messageCRC = retourCRC
										.getCRCString(retourCRC.getMessageCRC());
								msg.setError(ErrorType.CRC);
								SamngLogger
										.getLogger()
										.error(Messages
												.getString("errors.nonblocking.badMsgCrc")
												+ " : 0x"
												+ calculCRC
												+ ". "
												+ Messages
														.getString("errors.nonblocking.blockStart1")
												+ " "
												+ (goodMsgs.size() + 1)
												+ "; "
												+ "CRC enregistr�:"
												+ " 0x" + messageCRC);
							}
						}

						compteurOctetsBrut += nbOctetMsg;
					}

					// // calcul de la diff�rence de longueur du message r�el
					// par rapport � la variable L_MESSAGE_JRU
					// la longueur enregistree est sup�rieure � la longueur
					// calculee car
					// elle est � l'octet pres
					int longueurCalculeeLMessageJru = GestionnaireLongueurMessage
							.getInstance().getLongueurCalculeeMessageCourant();
					int longueurEnregistreeLMessageJru = GestionnaireLongueurMessage
							.getInstance()
							.getLongueurEnregistreeMessageCourant();

					int diffLongueurLMessageJru = longueurEnregistreeLMessageJru
							- longueurCalculeeLMessageJru;

					// calcul de la diff�rence de longueur du message interne
					// (exemple: MESSAGE_FROM_RBC) par rapport � la variable
					// L_MESSAGE
					int diffLongueurLMessage = 0;
					int longueurCalculeeLMessage = longueurCalculeeLMessageJru;
					int longueurEnregistreeLMessage = longueurEnregistreeLMessageJru;
					if (GestionnaireLongueurMessage.getInstance()
							.getLongueurCalculeeMessageInterneCourant() != 0) {
						longueurCalculeeLMessage = GestionnaireLongueurMessage
								.getInstance()
								.getLongueurCalculeeMessageInterneCourant();
						longueurEnregistreeLMessage = GestionnaireLongueurMessage
								.getInstance()
								.getLongueurEnregistreeMessageInterneCourant();
						diffLongueurLMessage = longueurEnregistreeLMessage
								- longueurCalculeeLMessage;
						GestionnaireLongueurMessage.getInstance()
								.setLongueurCalculeeMessageInterneCourant(0);
						GestionnaireLongueurMessage.getInstance()
								.setLongueurEnregistreeMessageInterneCourant(0);
					}
					// System.out
					//					.println("longueur calculee" + GestionnaireLongueurMessage.getInstance().getLongueurCalculeeMessageCourant()); //$NON-NLS-1$
					// System.out
					//					.println("longueur enregistree" + GestionnaireLongueurMessage.getInstance().getLongueurEnregistreeMessageCourant()); //$NON-NLS-1$

					if (mauvaisCRC) {
						badMsgs.add(msg);
						if (msg.getEvenement() != null) {
							goodMsgs.add(msg);
						}
					}
					// cas o� la longueur du message interne (MESSAGE_FROM_RBC)
					// calcul�e est diff�rente de la variable L_MESSAGE(en bits)
					else if (diffLongueurLMessage > 7
							|| diffLongueurLMessage < 0) {
						if (bad_l_message_length_msg) {
							SamngLogger
									.getLogger()
									.warn(Messages
											.getString("errors.nonblocking.invalidMessageLength") + //$NON-NLS-1$
											Messages.getString("ParseurParcoursJRU.11") + (longueurEnregistreeLMessage * 8) + "(L_MESSAGE)" + //$NON-NLS-1$ //$NON-NLS-2$
											Messages.getString("ParseurParcoursJRU.12") + longueurCalculeeLMessage + //$NON-NLS-1$
											". " //$NON-NLS-1$
											+ Messages
													.getString("errors.nonblocking.blockStart1") + " " //$NON-NLS-1$ //$NON-NLS-2$
											+ indiceBlocCourant);

							msg.setError(ErrorType.BadLength);
							badMsgs.add(msg);

						}
						// else
						goodMsgs.add(msg);
					}// cas o� la longueur totale du message calcul�e est
						// diff�rente de la variable L_MESSAGE_JRU (en octets)
					else if ((diffLongueurLMessageJru - diffLongueurLMessage) > 7
							|| diffLongueurLMessageJru < 0) {
						if (bad_l_message_jru_length_msg) {
							SamngLogger
									.getLogger()
									.warn(Messages
											.getString("errors.nonblocking.invalidMessageLength") + //$NON-NLS-1$
											Messages.getString("ParseurParcoursJRU.11") + longueurEnregistreeLMessageJru + "(L_MESSAGE_JRU)" + //$NON-NLS-1$ //$NON-NLS-2$
											Messages.getString("ParseurParcoursJRU.12") + longueurCalculeeLMessageJru + //$NON-NLS-1$
											". " //$NON-NLS-1$
											+ Messages
													.getString("errors.nonblocking.blockStart1") + " " //$NON-NLS-1$ //$NON-NLS-2$
											+ indiceBlocCourant);

							msg.setError(ErrorType.BadLength);
							badMsgs.add(msg);
						}
						// else
						goodMsgs.add(msg);
					} else {
						if (msg != null && msg.getEvenement() != null) {
							goodMsgs.add(msg);
						}
					}
					cptMsg++;

					if (msg != null && msg.getMessageData() != null)
						indiceBlocCourant += msg.getMessageData().length
								/ tailleBlocData;
					else
						indiceBlocCourant = cptMsg;

					this.tc.getFormatedTime();
					// changement de la position de lecture
					if (msg == null) {
						octetCourant++;
						cpt++;
					} else {
						octetCourant += msg.getLongueur();
						// incr�mentation du compteur
						cpt += msg.getLongueur();
					}
				}
			}
		}
		lastMessage = null;
		lastSegTemp = null;
		lastSegDistance = null;

		for (int j = 0; j < goodMsgs.size(); j++) {
			try {
				Evenement ev = goodMsgs.get(j).getEvenement();

				// gestion de la synchronisation temps distance des �v�nements
				// su l'�v�nement n'est pas a synchroniser
				if (!ev.isASychroniser() && !ev.isReferenceSynchro()) {
					// on synchronise les �v�nements si une synchronisation
					// �tait en cours
					if (GestionnaireSynchronisationGroupes.getInstance()
							.isSynchroEnCours()) {
						GestionnaireSynchronisationGroupes.getInstance()
								.synchronisationMessages();
					}
				} else {
					GestionnaireSynchronisationGroupes.getInstance()
							.setSynchroEnCours(true);
					if (ev.isASychroniser()) {
						GestionnaireSynchronisationGroupes.getInstance()
								.ajouterMessageASynchroniser(goodMsgs.get(j));
					} else {
						GestionnaireSynchronisationGroupes.getInstance()
								.setMsgReferenceSynchro(goodMsgs.get(j));
					}
				}
				setFlags(goodMsgs.get(j));
			} catch (Exception e) {
				SamngLogger.getLogger().error(this, e);
			}
			lastMessage = goodMsgs.get(j);
		}
		if (lastSegDistance != null) {
			enregistrerSegmentDistance(lastMessage);
		}
		if (lastSegTemp != null) {
			enregistrerSegmentTemps(lastMessage);
		}
		// if (VueProgressBar.getInstance().isEscaped) {
		// return null;
		// }
		// creates the array with bad and good messages
		Message[][] msgs = new Message[2][];
		msgs[0] = goodMsgs.toArray(new Message[goodMsgs.size()]);
		msgs[1] = badMsgs.toArray(new Message[badMsgs.size()]);

		return msgs;
	}

	// M�thode permettant d'acqu�rir du fichier
	// AppData\Roaming\Faiveley Transport\SAM
	// X.X.X.bXX\ressources\bridage\missions.properties
	// le param�tre max_messages_mission_jru. Ce param�tre permet de donner une
	// limite, en nombre de
	// message, � la taille max d'une mission.
	// Cette m�thode �tait dans le module ParseurParcoursJRU. Elle a �t�
	// d�velopp�e par IMinfo.
	public static int getMaxMessagesJRU() {
		String max_messages_mission_jru = "50000"; //$NON-NLS-1$
		int max_messages_mission_jru_ = 50000;
		try {
			FileInputStream inStream;
			String cheminFichiermissions_PROPERTIES = RepertoiresAdresses.missions_PROPERTIES;
			inStream = new FileInputStream(new File(
					cheminFichiermissions_PROPERTIES)); //$NON-NLS-1$
			Properties props = new Properties();
			props.load(inStream);
			max_messages_mission_jru = (String) props
					.get("max_messages_mission_jru"); //$NON-NLS-1$
			if (max_messages_mission_jru != null) {
				max_messages_mission_jru_ = Integer
						.valueOf(max_messages_mission_jru);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return max_messages_mission_jru_;
	}

	// M�thode qui calcul les offsets (en octets) des variable Date, Time et
	// Mode, dans le Header.
	// Ce sont les seules variables n�cessaires � l'algo d'exploration.
	// Gr�ce � ces offsets, l'algo acc�de directement au ressources dont il a
	// besoin.
	private void computeOffsetExploreVariables(DescripteurComposite descrComp) {
		// Nombre de variables dont le message est constitu�
		int size = descrComp.getLength();

		pos = 8; // Offset de 8 : taille du NID_MESSAGE
		DescripteurVariable descVar = null; // Descripteur de la variable
		int codeVar = 0; // Code de la variable : celui du XML : code="XXX"

		// Pour chaque variable qui constitue le Header...
		for (int i = 1; i < size; i++) {
			// Obtention du descripteur de la variable
			descVar = (DescripteurVariable) descrComp.getEnfant(i);
			// Obtention du code de la variable
			codeVar = descVar.getM_AIdentificateurComposant().getCode();
			// Obtention de la variable
			AVariableComposant var = GestionnairePool.getInstance()
					.getVariable(codeVar);

			if (codeVar == TypeRepere.date.getCode()) {
				posDate = pos;
			} else if ((codeVar == TypeRepere.temps.getCode())) {
				posTime = pos;
			} else if ((codeVar == 60720)) {
				posMode = pos;
			}

			// Passage � la variable suivante...
			pos += var.getDescriptor().getTailleBits();
		}
	}

	// M�thode qui explore les fichiers JRU (et BRU)
	// Elle permet de diviser le fichier explor� en mission.
	// Elle cr�e le fichier XML qui contien t la division en mission du fichier
	// explor�.
	// Algo : DR28 ed A - SAM 5.5.0 Ouvertures partielles.doc
	private void chargerDataExplore(String nomFichierParcours)
			throws ParseurBinaireException, DatatypeConfigurationException {
		Message startMsgMission = null; // Contient le msg de d�but de mission
		boolean fermerMission = false; // Flag indiquant que la mission peut
										// �tre ferm�e
		int compteurMess = 0; // Nombre courant de messages contenus dans la
								// mission courante
		// Nombre max de message qu'une mission peut contenir
		int max_messages_mission_jru_ = getMaxMessagesJRU();
		TypeMission missionCurrent = null; // Contient la mission � ajouter
		int numMission = 0; // Nombre courant de missions contenues dans le
							// fichier explor�
		TypeListeMission listeMissions = new TypeListeMission(); // Liste
																	// missions
																	// �crit
																	// dans le
																	// XML
		String dateRegTpsPrev = "dateRegTpsPrev"; // Contient la date du
													// regroupement courant
		TypeRegroupementTemps trt = null; // Regroupement de temps �crit dans le
											// XML
		int numRegTps = 0; // Nombre courant de regroupement de temps
		// Liste des regroupements de temps �crit dans le XML
		TypeListeRegroupementTemps listeRegroupementTemps = new TypeListeRegroupementTemps();
		// Fichier XML qui contient les informations d'exploration
		TypeDocument doc = new TypeDocument();

		// Bind entre les objets et le document
		doc.setListeMission(listeMissions);
		doc.setListeRegoupementTemps(listeRegroupementTemps);

		// R�cup�ration du descripteur du fichier JRU � explorer
		descrTable = (DescripteurComposite) ParseurJRUTableAssociationEvVars
				.getInstance().getTableEvVars();

		// Calcul des offsets pour acc�der directement aux variables n�cessaire
		// � l'algo
		// d'exploration : utilise un g�n�ral message
		computeOffsetExploreVariables((DescripteurComposite) descrTable
				.getEnfant(0));

		// on d�fini la position de lecture de l'octet � l'octet de d�but du
		// premier enregistrement
		int octetCourant = 0;
		// R�cup�re la taille, en octet, du fichier � explorer
		int tailleTableauMessage = this.message.length;

		int cpt = 1; // Nombre d'octets trait�s dans le bloc en cours
		int cptMsg = 1; // Nombre de messages d�pil�s du fichier
		indiceBlocCourant = 1;
		int compteurOctetsBrut = 0;

		// Contient la Date du message courant sous forme d'epoch time �
		// 0h0min0s
		// Evite les converstion de type
		long currentDateL = 0;
		// Contient la date du premier message de la mission
		long startMsgMissionL = 0;
		// D�termine si le fichier est un BRU ou JRU
		boolean BRUFile = BridageFormats.getInstance()
				.getFormatFichierOuvert(this.currentFileName).getFjru() == FormatJRU.bru;

		// Tant qu'il y a des octets � traiter dans le fichier � explorer...
		// while (octetCourant + 2 <= tailleTableauMessage - 1 &&
		// !Thread.interrupted())
		while (octetCourant + 2 <= tailleTableauMessage - 1
				&& !monitor.isCanceled()) {
			// Gestion de la progress bar
			ActivatorData
					.getInstance()
					.getVp()
					.setValeurProgressBar(
							octetCourant * 100 / (tailleTableauMessage));
			ActivatorData.getInstance().getVpExportExplorer()
					.setValeurProgressBar(octetCourant, tailleTableauMessage);

			// Traitement de tous les messages du bloc courant pour les fichiers
			// BRU
			// Traitement du message courant pour les fichiers JRU
			cpt = 0;
			while (BRUFile ? cpt <= tailleBlocData - 2 : cpt == 0) {
				// if the blocks structure is ok load the message
				Message msg = new Message();

				// S'il reste des octets � traiter dans le fichier...
				if (octetCourant < tailleTableauMessage) {
					// chargement du message
					// Parse le message pour ne r�cup�rer que les ressources
					// utiles � l'exploration
					msg = chargerMessageExplore(octetCourant);

					// on valorise l'id de message � partir de la taille de son
					// tableau d'octet brut
					// on valorise l'attribut data du message avec le tableau
					// d'octet brut
					if (listeTableauOctetBrutMsg != null
							&& listeTableauOctetBrutMsg.size() > 0) {
						msg.setMessageId(compteurOctetsBrut);
						msg.setOffsetDebut(octetCourant);
						msg.setOffsetFin(octetCourant + msg.getLongueur());
						int nbOctetMsg = listeTableauOctetBrutMsg
								.get(cptMsg - 1).length;

						compteurOctetsBrut += nbOctetMsg;
					}

					if (msg != null) {
						fermerMission = false;

						// Pour optimiser le temps de traitement, l'objet
						// GregorianCalendar n'est
						// plus utilis� et du coup les calculs se font en epoch
						// time au jour : avec
						// comme heure : 0h0min0s

						// Suppression des heures, minutes, secondes et
						// millisecondes, pour obtenir
						// une date sous forme d'epoch time.
						// 86400 : 24 x 3600 : 1 jour en secondes
						// / 1000 : suppression des millisecondes
						currentDateL = (msg.getAbsoluteTime() / 1000 / 86400) * 86400;

						// Si c'est le 1er msg du parcours ou le 1er msg de la
						// nouvelle mission...
						if (startMsgMission == null) {
							startMsgMission = msg; // Enregistrement du premier
													// msg de la mission
							// Enregistrement de la date de d�but de mission
							startMsgMissionL = currentDateL;

							// La mission comporte un message
							compteurMess = 1;
						} else {
							compteurMess++; // 1 message en plus dans la mission

							// Si la date a chang�e et qu'elle n'est pas en
							// 2072...
							// 3218745600L : GMT : 31/12/2071 0h0min0s et non
							// 01/01/2072, car pour
							// des raisons de time zone, il se peut que le
							// 01/01/2072 se traduise
							// par 31/12/2071 � 23H00, avec la suppression des
							// heures, minutes,
							// secondes => 31/12/2071
							if ((currentDateL != startMsgMissionL)
									&& (currentDateL < 3218745600L)) {
								// Si la date de la mission �tait en 2072 ==
								// d�but de fichier en 2072...
								if (startMsgMissionL >= 3218745600L) {
									// La date de la mission devient celle du
									// message courant
									startMsgMissionL = currentDateL;
									startMsgMission.setAbsoluteTime(msg
											.getAbsoluteTime());
								}
								// Si M_Mode vaut 6 (Libell� � Stand By � dans
								// la table de labels)
								else if (msg.getVariable(60720).toString()
										.equals("Stand By")) {
									// Le message courant est le dernier de la
									// mission
									fermerMission = true;
								}
							}
						}

						// Si la mission doit �tre ferm�e : changement de jour +
						// M_mode = 6 ou si le
						// nombre max de message dans une mission est atteint,
						// ou, le dernier
						// message du fichier vient d'�tre trait�
						if (fermerMission
								|| (compteurMess >= max_messages_mission_jru_)
								|| ((octetCourant + msg.getLongueur() + 2) > (tailleTableauMessage - 1))) {
							missionCurrent = new TypeMission();
							listeMissions.getMission().add(missionCurrent);

							// Conversion de la date & time en heure locale et
							// au format :
							// AAAA-MM-JJTHH:MM:SS+01:00
							// sur les messages qui constituent les bornes de la
							// mission
							GregorianCalendar gc = new GregorianCalendar();
							gc.setTimeInMillis(startMsgMission
									.getAbsoluteTime());
							XMLGregorianCalendar xcDeb = DatatypeFactory
									.newInstance().newXMLGregorianCalendar(gc);
							gc.setTimeInMillis(msg.getAbsoluteTime());
							XMLGregorianCalendar xcFin = DatatypeFactory
									.newInstance().newXMLGregorianCalendar(gc);

							// Renseignement des champs de la mission
							// <Mission id="mX" numero="X">
							missionCurrent.setId("m" + numMission); //$NON-NLS-1$
							missionCurrent.setNumero(new BigInteger(
									"" + numMission)); //$NON-NLS-1$
							// <DateDebut>XXXX-MM-JJTHH:MM:SS+01:00</DateDebut>
							missionCurrent.setDateDebut(xcDeb);
							// <DateFin>XXXX-MM-JJTHH:MM:SS+01:00</DateFin>
							missionCurrent.setDateFin(xcFin);
							// <OffsetDebut>XXXXX</OffsetDebut>
							missionCurrent.setOffsetDebut(startMsgMission
									.getOffsetDebut());
							// <OffsetFin>XXXXX</OffsetFin>
							missionCurrent.setOffsetFin(msg.getOffsetFin());
							// <IdMessageDebut>XXXXX</IdMessageDebut>
							missionCurrent.setIdMessageDebut(new BigInteger(
									"" + startMsgMission.getMessageId())); //$NON-NLS-1$

							numMission++;

							// Traitement des regroupements de temps

							// Conversion du temps de secondes en millisecondes
							gc.setTimeInMillis(startMsgMissionL * 1000);

							// R�cup�ration du mois et de l'ann�e de la mission
							// pour savoir dans
							// quel regroupement de temps il faudra la mettre :
							// AAAA-MM
							String dateRegrTpsCurrent = DatatypeFactory
									.newInstance().newXMLGregorianCalendar(gc)
									.toString().substring(0, 7);

							// Si la mission qui vient d'�tre ajout�e ne rentre
							// pas dans le
							// regroupement de temps courant...
							if (!dateRegrTpsCurrent.equals(dateRegTpsPrev)) {
								dateRegTpsPrev = new String(dateRegrTpsCurrent);

								// Cr�ation d'un nouveau regroupement de temps
								trt = new TypeRegroupementTemps();
								listeRegroupementTemps.getRegroupementTemps()
										.add(trt);

								// <RegroupementTemps id="rXXX" numero="XXX">
								trt.setId("r" + numRegTps); //$NON-NLS-1$
								trt.setNumero(new BigInteger("" + numRegTps)); //$NON-NLS-1$
								numRegTps++;
								// <Mois>AAAA-MM-JJTHH:MM:SS+01:00</Mois>
								trt.setMois(xcDeb);
							}

							TypeRefMission refMission = new TypeRefMission();
							refMission.setReference(missionCurrent);
							// Ajout de la mission au regroupement de temps
							trt.getListeMission().getMission().add(refMission);

							// Le prochain message sera le premier message d'une
							// nouvelle mission
							startMsgMission = null;
						}
					}

					cptMsg++;

					this.tc.getFormatedTime();

					// changement de la position de lecture
					// Passage au message suivant
					if (msg == null) {
						octetCourant++;
						cpt++;
					} else {
						octetCourant += msg.getLongueur();
						cpt += msg.getLongueur();
					}
				}
			}
		}

		// if (!Thread.interrupted())
		if (!monitor.isCanceled()) {
			// Enregistrement du fichier XML

			String packageName = "com.faiveley.samng.principal.sm.missions.jaxb"; //$NON-NLS-1$

			JAXBContext jaxbContext;
			try {
				jaxbContext = JAXBContext.newInstance(packageName, getClass().getClassLoader());
				Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
						Boolean.valueOf(true));
				jaxbMarshaller.marshal(doc, new File(nomFichierParcours
						+ ".xml")); //$NON-NLS-1$
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected ADescripteurComposant chargerDescripteursCodeBloc()
			throws BadHeaderInfoException {
		DescripteurCodeBloc descr = new DescripteurCodeBloc();

		return descr;
	}

	@Override
	protected AParcoursComposant chargerEntete() throws AExceptionSamNG {
		// TODO Auto-generated method stub
		return null;
	}

	private int gestionID(int start) {

		byte[] tabIdEvt = new byte[2];
		tabIdEvt[0] = 0;
		tabIdEvt[1] = this.message[start];
		BigInteger bInt = new BigInteger(tabIdEvt);
		int id = bInt.intValue();
		return id;
	}

	private void gererIDNonTrouve(Message msg, int id)
			throws ParseurBinaireException {
		msg.setError(ErrorType.EventId);
		throw new BadEventCodeException(
				Messages.getString("errors.nonblocking.invalidEventId") //$NON-NLS-1$
						+ "; " //$NON-NLS-1$
						+ Messages.getString("errors.nonblocking.eventId1") //$NON-NLS-1$
						+ id + "; "); //$NON-NLS-1$
	}

	private void gererNomEvtNonTrouve(int id, Evenement ev, Message msg) {
		DescripteurEvenement descrEvt = new DescripteurEvenement();

		IdentificateurEvenement identifEvt = new IdentificateurEvenement();

		identifEvt.setCode(id);
		identifEvt.setNom("Event code: " + id); //$NON-NLS-1$

		descrEvt.setCode(id);
		descrEvt.setNom("Event code: " + id); //$NON-NLS-1$
		descrEvt.setCaractTemporelle(Temporelle.COPY_DOWN);

		descrEvt.setM_AIdentificateurComposant(identifEvt);

		ev.setM_ADescripteurComposant(descrEvt);
		ev.setChangementHeure(false);

		TableLangueNomUtilisateur tblLangues = new TableLangueNomUtilisateur();
		tblLangues.setNomUtilisateur(Langage.FR, "Code de l'�v�nement: " + id); //$NON-NLS-1$
		tblLangues.setNomUtilisateur(Langage.EN, "Event code: " + id); //$NON-NLS-1$

		ev.setNomUtilisateur(tblLangues);
		GestionnairePool.getInstance().ajouterEvenement(ev);

		msg.setError(ErrorType.EventId);
		SamngLogger
				.getLogger()
				.warn(Messages.getString("errors.nonblocking.notXmlFoundEvent") //$NON-NLS-1$
						+ "; " + Messages.getString("errors.nonblocking.eventId1") + id + ". " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						Messages.getString("errors.nonblocking.blockStart1") + " " + calculBloc(msg)); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private String calculBloc(Message msg) {
		if (tailleBlocData != 0) {
			return ""
					+ (((msg.getMessageId() - (tailleEntete + tailleTableEvenement)) / tailleBlocData))
					+ 1;
		} else {
			return "";
		}

	}

	private void gererNomEvtTrouve(int id, DescripteurEvenement descEvt,
			Evenement ev, Message msg) throws ParseurBinaireException {
		// check if is set the nom utilisateur
		if (ev.getNomUtilisateur() == null
				|| ev.getNomUtilisateur().size() == 0) {
			msg.setError(ErrorType.XMLRelated);
			throw new BadArgumentInFileException(
					Messages.getString("errors.nonblocking.invalidXmlUsersList") //$NON-NLS-1$
							+ "; " + Messages //$NON-NLS-1$
									.getString("errors.nonblocking.eventId1") + id + ". " + //$NON-NLS-1$ //$NON-NLS-2$
							Messages.getString("errors.nonblocking.blockStart1") + " " + calculBloc(msg)); //$NON-NLS-1$ //$NON-NLS-2$
		}

		// check if is set the caractereTemporelle
		if (descEvt.getCaractTemporelle() == null) {
			msg.setError(ErrorType.XMLRelated);
			throw new BadArgumentInFileException(
					Messages.getString("errors.nonblocking.invalidXmlEvCaractTemp") //$NON-NLS-1$
							+ "; " + Messages //$NON-NLS-1$
									.getString("errors.nonblocking.eventId1") + id + ". " + //$NON-NLS-1$ //$NON-NLS-2$
							Messages.getString("errors.nonblocking.blockStart1") + " " + calculBloc(msg)); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	// protected void remplirValue(AVariableComposant var, int start, Message
	// msg) {
	// remplirValue(var, start, msg, false);
	// }

	private void remplirValue(AVariableComposant var, int start, Message msg) {
		byte[] value = null;
		byte[] value2 = null;
		int longueurVariable = 0;
		try {
			int nbOctets = msg.getLongueur();
			byte[] message = new byte[nbOctets];
			longueurVariable = var.getDescriptor().getTailleBits();

			// r�cup�ration du message
			message = Arrays.copyOfRange(this.message, start, start + nbOctets);
			BigInteger bigInt = new BigInteger(message);

			// suppression des bits inutiles � droite
			BigInteger bigInt2 = bigInt.shiftRight(((nbOctets * 8))
					- longueurVariable - pos);
			byte[] tabbyte = bigInt2.toByteArray();

			// suppresion des bits inutiles � gauches
			for (int i = tabbyte.length - 1; i >= 0; i--) {
				if ((tabbyte.length - i) * 8 <= longueurVariable) {
					tabbyte[i] &= 0xff;
				} else if ((tabbyte.length - i - 1) * 8 < longueurVariable) {
					int mask = 0;
					int longueurArajouter = longueurVariable % 8;
					for (int j = 0; j < longueurArajouter; j++) {
						mask = mask
								+ Integer.valueOf(Math.round(Math.pow(2, j)) + ""); //$NON-NLS-1$

					}
					tabbyte[i] &= mask;
				} else {
					tabbyte[i] &= 0;
				}
			}

			int tailleValue = ((longueurVariable % 8) == 0 ? 0 : 1)
					+ longueurVariable / 8;
			value = Arrays.copyOfRange(tabbyte, tabbyte.length - tailleValue,
					tabbyte.length);

			// d�calage � gauche pour les variables complexes
			if (var instanceof VariableComplexe) {
				value2 = new byte[value.length];

				value2 = new BigInteger(value).shiftLeft(
						8 * tailleValue - longueurVariable).toByteArray();

				for (int i = value.length - 1; i >= 0; i--) {
					int ind = value2.length - value.length + i;
					if (ind >= 0) {
						value[i] = value2[ind];
					} else {
						value[i] = 0;
					}
				}
			}
		} catch (Exception e) {
			System.out.println("pb lecture value"); //$NON-NLS-1$
		}

		try {
			setVariableValue(var, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * M�thode de remplissage d'une structure dynamique
	 * 
	 * @param structureDynamique
	 *            la structure dynamique � renseigner
	 * @param definitionStructureDynamique
	 *            la definition de la structure dynamique
	 * @param start
	 *            la position le d�part du message
	 * @param pos
	 *            la position courante dans le message
	 * @param msg
	 *            le message
	 * @return la structure dynamique renseign�e
	 */
	private StructureDynamique gererStructureDynamique(
			StructureDynamique definitionStructureDynamique, int start,
			Message msg) {
		// cr�ation d'une nouvelle instance de structure dynamique
		StructureDynamique structureDynamique = new StructureDynamique();

		// d�finition du descripteur de la nouvelle structure dynamique
		structureDynamique.setDescripteur(definitionStructureDynamique
				.getDescriptor());

		// r�cup�ration de la d�finition de la variable d'entete
		AVariableComposant defVarEntete = GestionnairePool.getInstance()
				.getVariable(
						definitionStructureDynamique.getVariableEntete()
								.getDescriptor()
								.getM_AIdentificateurComposant().getCode());

		// copie de la varaible d'entete
		AVariableComposant varEntete = defVarEntete.copy();

		// valorisation de la varaible d'entete
		remplirValue(varEntete, start, msg);

		pos += varEntete.getDescriptor().getTailleBits();

		if (lecturePaquetEnCours)
			GestionnaireLongueurPacket.getInstance()
					.incrementerLongueurCalculeePacketCourant(
							varEntete.getDescriptor()
									.getM_AIdentificateurComposant().getNom(),
							varEntete.getDescriptor().getTailleBits());

		// gestion des longueurs de paquet, message complet et message interne
		GestionnaireLongueurMessage.getInstance()
				.incrementerLongueurCalculeeMessageCourant(
						varEntete.getDescriptor()
								.getM_AIdentificateurComposant().getNom(),
						varEntete.getDescriptor().getTailleBits());
		// lorssque que la varaible d'entete de la structure dynamique est
		// NID_MESSAGE, il y a un message interne
		if (varEntete.getDescriptor().getM_AIdentificateurComposant().getNom()
				.equals("NID_MESSAGE")) { //$NON-NLS-1$
			GestionnaireLongueurMessage.getInstance()
					.setLongueurCalculeeMessageInterneCourant(
							varEntete.getDescriptor().getTailleBits());
		} else if (GestionnaireLongueurMessage.getInstance()
				.getLongueurCalculeeMessageInterneCourant() != 0) {
			GestionnaireLongueurMessage.getInstance()
					.incrementerLongueurCalculeeMessageInterneCourant(
							varEntete.getDescriptor()
									.getM_AIdentificateurComposant().getNom(),
							varEntete.getDescriptor().getTailleBits());
		}

		// affectation de la variable d'entete � la nouvelle structure dynamique
		structureDynamique.setVariableEntete(varEntete);

		String valeurVarEntete = varEntete
				.getValeurBruteChaineVariableDiscrete();

		TableSousVariable tableSousVar = null;
		if (definitionStructureDynamique

		.getTableSousVariableReferenceByValeur(valeurVarEntete) != null) {
			tableSousVar = (definitionStructureDynamique
					.getTableSousVariableReferenceByValeur(valeurVarEntete))
					.copy();

			List<AVariableComposant> listeSousVariable = tableSousVar
					.getM_AVariableComposant();

			if (listeSousVariable != null) {

				List<AVariableComposant> listeSousVariable2 = new ArrayList<AVariableComposant>();

				listeSousVariable2 = renseignerListeSousVariable(
						listeSousVariable, start, msg);

				for (AVariableComposant composant : listeSousVariable2) {
					composant.setParent(structureDynamique);
				}
				tableSousVar.setM_AVariableComposant(listeSousVariable2);
				structureDynamique.ajouterTableSousVariable(tableSousVar);
			}
			// Cas o� le NID_XUSER n'est pas connu (Packet 44)
			// Message d'erreur + affichage de la valeur du NID_XUSER + saut de
			// position de L_PACKET
		} else if (varEntete.getDescriptor().getM_AIdentificateurComposant()
				.getNom().equals("NID_XUSER")) {

			GestionnaireLongueurMessage.getInstance()
					.incrementerLongueurCalculeeMessageCourant(
							varEntete.getDescriptor()
									.getM_AIdentificateurComposant().getNom(),
							(int) positionPaquetSuivantUsing_L_Packet - pos);

			pos = (int) positionPaquetSuivantUsing_L_Packet;

			if (lecturePaquetEnCours)
				GestionnaireLongueurPacket.getInstance()
						.setLongueurCalculeePacketCourant(
								GestionnaireLongueurPacket.getInstance()
										.getLongueurEnregistreePacketCourant());

			SamngLogger
					.getLogger()
					.error(varEntete.getDescriptor()
							.getM_AIdentificateurComposant().getNom()
							+ "="
							+ valeurVarEntete
							+ Messages.getString("ParseurParcoursJRU.17")
							+ " "
							+ Messages
									.getString("errors.nonblocking.blockStart1")
							+ " " + indiceBlocCourant);
		}
		// Cas o� le NID_MESSAGE n'est pas connu
		// Message d'erreur + affichage de la valeur du NID_MESSAGE + pas besoin
		// de saut,
		// Il ne peut pas y avoir d'autre information � d�coder apr�s un
		// NID_MESSAGE
		// = pas de NID_MESSAGE apr�s un NID_MESSAGE
		else if (varEntete.getDescriptor().getM_AIdentificateurComposant()
				.getNom().equals("NID_MESSAGE")) {
			SamngLogger
					.getLogger()
					.error(varEntete.getDescriptor()
							.getM_AIdentificateurComposant().getNom()
							+ "="
							+ valeurVarEntete
							+ Messages.getString("ParseurParcoursJRU.17")
							+ " "
							+ Messages
									.getString("errors.nonblocking.blockStart1")
							+ " " + indiceBlocCourant);
		}

		return structureDynamique;
	}

	/**
	 * M�thode de remplissage d'un tableau dynamique
	 * 
	 * @param tableauDynamique
	 *            le tableau dynamique � renseigner
	 * @param definitionTableauDynamique
	 *            la definition du tableau dynamique
	 * @param start
	 *            la position le d�part du message
	 * @param pos
	 *            la position courante dans le message en nombre de bits
	 * @param msg
	 *            le message
	 * @return le tableau dynamique renseign�
	 */
	private TableauDynamique gererTableauDynamique(
			TableauDynamique definitionTableauDynamique, int start, Message msg) {

		// cr�ation d'une nouvelle instance de tableau dynamique
		TableauDynamique tableauDynamique = new TableauDynamique();

		// d�finition du descripteur du tableau dynamique
		tableauDynamique.setDescripteur(definitionTableauDynamique
				.getDescriptor());

		// r�cup�ration de la d�finition de la variable d'entete
		AVariableComposant defVarEntete = GestionnairePool.getInstance()
				.getVariable(
						definitionTableauDynamique.getVariableEntete()
								.getDescriptor()
								.getM_AIdentificateurComposant().getCode());

		// copie de la varaible d'entete

		AVariableComposant varEntete = defVarEntete.copy();
		// valorisation de la varaible d'entete
		remplirValue(varEntete, start, msg);
		pos += varEntete.getDescriptor().getTailleBits();

		// gestion des longueurs de paquet, message complet et message interne
		if (lecturePaquetEnCours)
			GestionnaireLongueurPacket.getInstance()
					.incrementerLongueurCalculeePacketCourant(
							varEntete.getDescriptor()
									.getM_AIdentificateurComposant().getNom(),
							varEntete.getDescriptor().getTailleBits());

		GestionnaireLongueurMessage.getInstance()
				.incrementerLongueurCalculeeMessageCourant(
						varEntete.getDescriptor()
								.getM_AIdentificateurComposant().getNom(),
						varEntete.getDescriptor().getTailleBits());
		if (GestionnaireLongueurMessage.getInstance()
				.getLongueurCalculeeMessageInterneCourant() != 0) {
			GestionnaireLongueurMessage.getInstance()
					.incrementerLongueurCalculeeMessageInterneCourant(
							varEntete.getDescriptor()
									.getM_AIdentificateurComposant().getNom(),
							varEntete.getDescriptor().getTailleBits());
		}

		// affectation de la variable d'entete au nouveau tableau dynamique
		tableauDynamique.setVariableEntete(varEntete);

		// r�cup�ration de la valeur de la variable d'entete
		String valeurVarEntete = varEntete
				.getValeurBruteChaineVariableDiscrete();

		// la variable d'entete donne le nombre d'occurence de la table de
		// sous-variable
		int nbOccurenceTablesousVariable = 0;
		try {
			nbOccurenceTablesousVariable = Integer.parseInt(valeurVarEntete);
		} catch (Exception ex) {

		}
		if (nbOccurenceTablesousVariable > 0) {
			TableSousVariable defTableSousVar = definitionTableauDynamique
					.getTableSousVariableReferencee();
			if (defTableSousVar != null) {
				TableSousVariable tableSousVar = null;
				for (int i = 0; i < nbOccurenceTablesousVariable; i++) {
					tableSousVar = defTableSousVar.copy();
					List<AVariableComposant> listeSousVariable = defTableSousVar
							.getM_AVariableComposant();

					if (listeSousVariable != null) {
						List<AVariableComposant> listeSousVariable2 = renseignerListeSousVariable(
								listeSousVariable, start, msg);

						for (AVariableComposant composant : listeSousVariable2) {
							composant.setParent(tableauDynamique);
						}
						tableSousVar
								.setM_AVariableComposant(listeSousVariable2);
						tableauDynamique.ajouterTableSousVariable(tableSousVar);

					}

				}

			}
		}
		return tableauDynamique;
	}

	/**
	 * M�thode de remplissage d'une chaine dynamique
	 * 
	 * @param chaineDynamique
	 *            la chaine dynamique � renseigner
	 * @param definitionChaineDynamique
	 *            la definition de la chaine dynamique � renseigner
	 * @param start
	 *            la position le d�part du message
	 * @param pos
	 *            la position courante dans le message en nombre de bits
	 * @param msg
	 *            le message
	 * @return la chaine dynamique renseign�e
	 */
	private ChaineDynamique gererChaineDynamique(
			ChaineDynamique definitionChaineDynamique, int start, Message msg) {

		// cr�ation d'une nouvelle instance de chaine
		// dynamique
		ChaineDynamique chaineDynamique = new ChaineDynamique();

		// d�finition du descripteur de la nouvelle chaine dynamique
		chaineDynamique.setDescripteur(definitionChaineDynamique
				.getDescriptor());

		// r�cup�ration de la d�finition de la variable d'entete
		// donnant le nombre d'occurrence de la variable XText
		AVariableComposant defVarEntete = GestionnairePool.getInstance()
				.getVariable(
						definitionChaineDynamique.getVariableEntete()
								.getDescriptor()
								.getM_AIdentificateurComposant().getCode());

		// copie de la varaible d'entete
		AVariableComposant varEnteteLText = defVarEntete.copy();
		// valorisation de la varaible d'entete
		remplirValue(varEnteteLText, start, msg);
		pos += varEnteteLText.getDescriptor().getTailleBits();

		// gestion des longueurs de paquet, message complet et message interne
		if (lecturePaquetEnCours)
			GestionnaireLongueurPacket.getInstance()
					.incrementerLongueurCalculeePacketCourant(
							varEnteteLText.getDescriptor()
									.getM_AIdentificateurComposant().getNom(),
							varEnteteLText.getDescriptor().getTailleBits());
		GestionnaireLongueurMessage.getInstance()
				.incrementerLongueurCalculeeMessageCourant(
						varEnteteLText.getDescriptor()
								.getM_AIdentificateurComposant().getNom(),
						varEnteteLText.getDescriptor().getTailleBits());
		if (GestionnaireLongueurMessage.getInstance()
				.getLongueurCalculeeMessageInterneCourant() != 0) {
			GestionnaireLongueurMessage.getInstance()
					.incrementerLongueurCalculeeMessageInterneCourant(
							varEnteteLText.getDescriptor()
									.getM_AIdentificateurComposant().getNom(),
							varEnteteLText.getDescriptor().getTailleBits());
		}

		// affectation de la variable d'entete au nouveau tableau dynamique
		chaineDynamique.setVariableEntete(varEnteteLText);

		// r�cup�ration de la valeur de la variable d'entete
		String valeurVarEntete = varEnteteLText
				.getValeurBruteChaineVariableDiscrete();

		// la variable d'entete donne le nombre d'occurrence de la variable
		// XText
		int nbOccurenceCaractere = 0;
		try {
			nbOccurenceCaractere = Integer.parseInt(valeurVarEntete);
		} catch (Exception ex) {

		}
		TableSousVariable tableSousVar = definitionChaineDynamique
				.getTableSousVariableReferencee();

		List<AVariableComposant> listeSousVariable = tableSousVar
				.getM_AVariableComposant();
		TypeVariable typeVariable = null;
		DescripteurVariable descVar = null;
		AVariableComposant defVarXText = listeSousVariable.get(0);
		AVariableComposant sousVariableXText = null;

		int tailleVarXText = defVarXText.getDescriptor().getTailleBits();
		// cr�ation d'une nouvelle table sous-variable qui stocke les occurences
		// de la variable XText
		TableSousVariable tableSousVarChaineDynamique = new TableSousVariable();

		for (int i = 0; i < nbOccurenceCaractere; i++) {
			descVar = (DescripteurVariable) defVarXText.getDescriptor();
			typeVariable = descVar.getTypeVariable();

			sousVariableXText = defVarXText.copy();
			if (typeVariable == TypeVariable.VAR_ANALOGIC
					|| typeVariable == TypeVariable.VAR_DISCRETE) {
				remplirValue(sousVariableXText, start, msg);

				pos += tailleVarXText;

				// gestion des longueurs de paquet, message complet et message
				// interne
				if (lecturePaquetEnCours)
					GestionnaireLongueurPacket.getInstance()
							.incrementerLongueurCalculeePacketCourant(
									sousVariableXText.getDescriptor()
											.getM_AIdentificateurComposant()
											.getNom(), tailleVarXText);

				GestionnaireLongueurMessage.getInstance()
						.incrementerLongueurCalculeeMessageCourant(
								descVar.getM_AIdentificateurComposant()
										.getNom(), tailleVarXText);

				if (GestionnaireLongueurMessage.getInstance()
						.getLongueurCalculeeMessageInterneCourant() != 0) {
					GestionnaireLongueurMessage.getInstance()
							.incrementerLongueurCalculeeMessageInterneCourant(
									descVar.getM_AIdentificateurComposant()
											.getNom(), descVar.getTailleBits());
				}
			}
			tableSousVarChaineDynamique.ajouter(sousVariableXText);
		}

		chaineDynamique.ajouterTableSousVariable(tableSousVarChaineDynamique);
		return chaineDynamique;
	}

	private List<AVariableComposant> renseignerListeSousVariable(
			List<AVariableComposant> listeSousVariable, int start, Message msg) {

		AVariableComposant varEchelleTableSousVar = null;
		TypeVariable typeVariable = null;
		AVariableComposant varComp = null;
		int codeSousVar;
		DescripteurVariable descVar = null;
		// cr�ation d'une liste de sous variables temporaire pour modification
		// car on ne peut modifier directement listeSousVariable
		List<AVariableComposant> listeSousVariable2 = new ArrayList<AVariableComposant>();
		long valeur_L_Packet = 0;
		if (listeSousVariable != null) {

			for (AVariableComposant sousVariable : listeSousVariable) {

				descVar = (DescripteurVariable) sousVariable.getDescriptor();
				codeSousVar = descVar.getM_AIdentificateurComposant().getCode();

				if (!(sousVariable instanceof Paquets)) {
					// recherche dans les variables simples
					varComp = GestionnairePool.getInstance().getVariable(
							codeSousVar);

					if (varComp == null)
						// recherche dans les variables dynamiques
						varComp = GestionnairePool.getInstance()
								.getVariableDynamique(
										descVar.getM_AIdentificateurComposant()
												.getCode());

					typeVariable = sousVariable.getDescriptor()
							.getTypeVariable();

					if (typeVariable == TypeVariable.VAR_ANALOGIC
							|| typeVariable == TypeVariable.VAR_DISCRETE
							|| typeVariable == TypeVariable.VAR_COMPLEXE
							|| typeVariable == TypeVariable.VAR_COMPOSEE) {

						AVariableComposant variableRenseignee = sousVariable
								.copy();
						remplirValue(variableRenseignee, start, msg);

						if (varEchelleTableSousVar == null) {
							if (descVar.getVariableEchelle() != null) {
								int code = descVar.getVariableEchelle()
										.getDescriptor()
										.getM_AIdentificateurComposant()
										.getCode();
								boolean trouve = false;
								int i = 0;
								while (i < listeSousVariable2.size() && !trouve) {

									if (code == listeSousVariable2.get(i)
											.getDescriptor()
											.getM_AIdentificateurComposant()
											.getCode()) {
										varEchelleTableSousVar = listeSousVariable2
												.get(i);
										trouve = true;
									}
									i++;
								}
							}
						}

						// ajout de la sous variable � la liste des sous
						// variables
						listeSousVariable2.add(variableRenseignee);

						// gestion de l'arret de lecture des paquets
						if (lecturePaquetEnCours) {
							// gestion des longueurs de paquet
							GestionnaireLongueurPacket
									.getInstance()
									.incrementerLongueurCalculeePacketCourant(
											variableRenseignee
													.getDescriptor()
													.getM_AIdentificateurComposant()
													.getNom(),
											variableRenseignee.getDescriptor()
													.getTailleBits());
							if (variableRenseignee.getDescriptor()
									.getM_AIdentificateurComposant().getNom()
									.equals("L_PACKET")) { //$NON-NLS-1$
								valeur_L_Packet = ((Long) variableRenseignee
										.getValeurObjet());
								GestionnaireLongueurPacket.getInstance()
										.setLongueurEnregistreePacketCourant(
												valeur_L_Packet);

								positionPaquetSuivantUsing_L_Packet = (positionVarEntetePaquet + valeur_L_Packet);
								//								System.out.println("valeur L_PACKET: " + valeur_L_Packet); //$NON-NLS-1$

							}
						}

						pos += varComp.getDescriptor().getTailleBits();

						// gestion des longueurs de message complet et message
						// interne
						GestionnaireLongueurMessage.getInstance()
								.incrementerLongueurCalculeeMessageCourant(
										descVar.getM_AIdentificateurComposant()
												.getNom(),
										varComp.getDescriptor().getTailleBits());
						if (descVar.getM_AIdentificateurComposant().getNom()
								.equals("L_MESSAGE")) { //$NON-NLS-1$
							GestionnaireLongueurMessage
									.getInstance()
									.setLongueurEnregistreeMessageInterneCourant(
											((Long) variableRenseignee
													.getValeurObjet())
													.intValue() * 8);
						}
						if (GestionnaireLongueurMessage.getInstance()
								.getLongueurCalculeeMessageInterneCourant() != 0) {
							GestionnaireLongueurMessage
									.getInstance()
									.incrementerLongueurCalculeeMessageInterneCourant(
											descVar.getM_AIdentificateurComposant()
													.getNom(),
											varComp.getDescriptor().getTailleBits());
						}

					} else if (typeVariable == TypeVariable.STRUCTURE_DYNAMIQUE) {

						// r�cup�ration de la d�finition de la structure
						// dynamique
						StructureDynamique defStructureDyn = null;

						if ((varComp != null))
							defStructureDyn = (StructureDynamique) varComp;
						else
							defStructureDyn = (StructureDynamique) sousVariable;

						StructureDynamique structureDynamique2 = gererStructureDynamique(
								defStructureDyn, start, msg);

						// ajout de la sous variable � la liste des sous
						// variables
						listeSousVariable2.add(structureDynamique2);

					} else if (typeVariable == TypeVariable.TABLEAU_DYNAMIQUE) {

						// r�cup�ration de la d�finition du tableau
						// dynamique
						TableauDynamique defTableauDyn = null;

						if (varComp != null)
							defTableauDyn = (TableauDynamique) varComp;
						else
							defTableauDyn = (TableauDynamique) sousVariable;

						TableauDynamique tableauDynamique2 = gererTableauDynamique(
								defTableauDyn, start, msg);

						// ajout de la sous variable � la liste des sous
						// variables
						listeSousVariable2.add(tableauDynamique2);

					} else if (typeVariable == TypeVariable.CHAINE_DYNAMIQUE) {

						ChaineDynamique defChaineDyn = null;

						if (varComp != null)
							defChaineDyn = (ChaineDynamique) varComp;
						else
							defChaineDyn = (ChaineDynamique) sousVariable;

						// remplissage de la chaine dynamique
						ChaineDynamique chaineDynamique = gererChaineDynamique(
								defChaineDyn, start, msg);

						// ajout de la sous variable � la liste des sous
						// variables
						listeSousVariable2.add(chaineDynamique);

					}
				}

				else {

					// StructureDynamique structDyn = new StructureDynamique();
					varComp = GestionnairePool.getInstance()
							.getVariableDynamique(
									descVar.getM_AIdentificateurComposant()
											.getCode());
					// r�cup�ration de la d�finition de la tableau dynamique
					StructureDynamique defStructureDyn = (StructureDynamique) varComp;

					// r�cup�ration de la liste des paquets
					List<Paquets> listePaquets = gererListePaquets(
							defStructureDyn, start, msg);

					// ajout des paquets � la table des sous variables
					// et ajout de la varaible m�re(varaible d'entete) � chaque
					// paquet
					AVariableComposant variableEntete = null;
					if (listePaquets != null && listePaquets.size() > 0) {
						for (Paquets paquets : listePaquets) {
							// ajout de la sous variable � la liste des sous
							// variables
							variableEntete = paquets.getVariableEntete();
							paquets.setParent(variableEntete);
							listeSousVariable2.add(paquets);
						}
					}
				}
			}
		}
		return listeSousVariable2;
	}

	/**
	 * M�thode de remplissage d'une liste de paquets
	 * 
	 * @param definitionStructureDynamique
	 *            la definition de la structure dynamique servant � analyser le
	 *            ou les paquets
	 * @param start
	 *            la position le d�part du message
	 * @param pos
	 *            la position courante dans le message en nombre de bits
	 * @param msg
	 *            le message
	 * @return la liste des paquets analys�s
	 */
	private List<Paquets> gererListePaquets(
			StructureDynamique definitionStructureDynamique, int start,
			Message msg) {

		List<Paquets> listePaquets = new ArrayList<Paquets>();

		// r�cup�ration de la d�finition de la variable d'entete
		AVariableComposant defVarEntete = GestionnairePool.getInstance()
				.getVariable(
						definitionStructureDynamique.getVariableEntete()
								.getDescriptor()
								.getM_AIdentificateurComposant().getCode());

		// variables utilis�es pour le paquet courant
		Paquets paquetCourant = null;

		AVariableComposant varEntetePaquetCourant;
		TableSousVariable tableSousVarPaquetCourant = null;
		List<AVariableComposant> listeSousVariablePaquetCourant;
		TableSousVariable tableSousVarDefinition = null;
		String valeurVarEntetePaquetCourant = ""; //$NON-NLS-1$

		// r�cup�ration de la valeur d'arret de lecture des paquets
		// la valeur d'arret de lecture correspond � la valeur de la derni�re
		// table de sous variables
		// String valeurArretLecturePaquets = ((TableSousVariable)
		// definitionStructureDynamique
		// .getListeTablesSousVariable().get(
		// definitionStructureDynamique
		// .getListeTablesSousVariable().size() - 1))
		// .getValeur();

		boolean finMessage = false;
		String valEntetePrecedente = "";

		//		System.out.println("MESSAGE: " + msg.getEvenement().getM_ADescripteurComposant().getM_AIdentificateurComposant().getNom()); //$NON-NLS-1$
		//		System.out.println("longueur message enregistr�e: " + GestionnaireLongueurMessage.getInstance().getLongueurEnregistreeMessageCourant()); //$NON-NLS-1$
		while (!finMessage) {
			paquetCourant = new Paquets();
			lecturePaquetEnCours = true;

			// copie de la variable d'entete
			varEntetePaquetCourant = defVarEntete.copy();

			// si la position apr�s lecture de la variable d'entete du paquet ne
			// d�passe pas L_MESSAGE_JRU
			// on lit ce paquet
			// Dans un message interne (NID_MESSAGE) s'il ne reste pas assez de
			// bits pour un ent�te
			// de packet (8 bits) dans la longueur indiqu� par le paquet
			// (L_MESSAGE), c'est que c'est du padding !
			GestionnaireLongueurMessage gestLongueurMessage = GestionnaireLongueurMessage
					.getInstance();
			GestionnaireLongueurPacket gestLongueurPaquet = GestionnaireLongueurPacket
					.getInstance();
			if ((pos + varEntetePaquetCourant.getDescriptor().getTailleBits() < gestLongueurMessage
					.getLongueurEnregistreeMessageCourant())
					&& ((gestLongueurMessage
							.getLongueurCalculeeMessageInterneCourant() == 0) || ((gestLongueurMessage
							.getLongueurCalculeeMessageInterneCourant() + varEntetePaquetCourant
							.getDescriptor().getTailleBits()) < (gestLongueurMessage
							.getLongueurEnregistreeMessageInterneCourant())))
			// positionPaquetSuivantUsing_L_Packet +
			// varEntetePaquetCourant.getDescriptor().getTaille()<GestionnaireLongueurMessage.getInstance().getLongueurEnregistreeMessageCourant()
			) {
				//				System.out.println("PAQUET: " + varEntetePaquetCourant.getDescriptor().getM_AIdentificateurComposant().getNom()); //$NON-NLS-1$
				//				System.out.println("position paquet: " + positionPaquetCourant); //$NON-NLS-1$

				// valorisation de la variable d'entete
				remplirValue(varEntetePaquetCourant, start, msg);

				// r�cup�ration de la valeur de la variable d'entete
				valeurVarEntetePaquetCourant = varEntetePaquetCourant
						.getValeurBruteChaineVariableDiscrete();

				String valEntetePourMessage = valeurVarEntetePaquetCourant;

				// apr�s lecture du pr�c�dent paquet, la position r�elle peut
				// diff�r�e de celle donn�e par la variable L_PACKET du paquet
				// pr�c�dent
				if (gestLongueurPaquet.getLongueurEnregistreePacketCourant() != 0) {
					if (gestLongueurPaquet.getLongueurCalculeePacketCourant() != gestLongueurPaquet
							.getLongueurEnregistreePacketCourant()) {
						SamngLogger
								.getLogger()
								.error(Messages
										.getString("ParseurParcoursJRU.13")
										+ Messages
												.getString("ParseurParcoursJRU.14")
										+ " "
										+ defVarEntete
												.getDescriptor()
												.getM_AIdentificateurComposant()
												.getNom()
										+ ": "
										+ valEntetePrecedente
										+ Messages
												.getString("ParseurParcoursJRU.11")
										+ gestLongueurPaquet
												.getLongueurEnregistreePacketCourant()
										+ Messages
												.getString("ParseurParcoursJRU.12")
										+ gestLongueurPaquet
												.getLongueurCalculeePacketCourant()
										+ ". "
										+ Messages
												.getString("errors.nonblocking.blockStart1")
										+ " " + indiceBlocCourant);
					}
				} else {
					// r�cup�ration de la valeur de la variable d'entete
					valEntetePourMessage = varEntetePaquetCourant
							.getValeurBruteChaineVariableDiscrete();
				}
				valEntetePrecedente = valEntetePourMessage;
				// r�initialisation des longueurs calcul�e et enregistr�e du
				// paquet
				gestLongueurPaquet.setLongueurEnregistreePacketCourant(0);
				gestLongueurPaquet.setLongueurCalculeePacketCourant(0);

				positionVarEntetePaquet = pos;
				pos += varEntetePaquetCourant.getDescriptor().getTailleBits();

				// gestion des longueurs de paquet, message complet et message
				// interne
				gestLongueurPaquet.incrementerLongueurCalculeePacketCourant(
						varEntetePaquetCourant.getDescriptor()
								.getM_AIdentificateurComposant().getNom(),
						varEntetePaquetCourant.getDescriptor().getTailleBits());
				gestLongueurMessage.incrementerLongueurCalculeeMessageCourant(
						varEntetePaquetCourant.getDescriptor()
								.getM_AIdentificateurComposant().getNom(),
						varEntetePaquetCourant.getDescriptor().getTailleBits());
				if (gestLongueurMessage
						.getLongueurCalculeeMessageInterneCourant() != 0) {
					gestLongueurMessage
							.incrementerLongueurCalculeeMessageInterneCourant(
									varEntetePaquetCourant.getDescriptor()
											.getM_AIdentificateurComposant()
											.getNom(), varEntetePaquetCourant
											.getDescriptor().getTailleBits());
				}

				paquetCourant.setVariableEntete(varEntetePaquetCourant);
				paquetCourant.setDescripteur(definitionStructureDynamique
						.getDescriptor());

				// // r�cup�ration de la valeur de la variable d'entete
				// valeurVarEntetePaquetCourant =
				// varEntetePaquetCourant.getValeurBruteChaineVariableDiscrete();

				boolean depassementCapaciteMessage = false;
				if ((pos + 1) > gestLongueurMessage
						.getLongueurEnregistreeMessageCourant()) {
					depassementCapaciteMessage = true;
				}

				// r�cup�ration de la table de sous-variable correspondante
				tableSousVarDefinition = definitionStructureDynamique
						.getTableSousVariableReferenceByValeur(valeurVarEntetePaquetCourant);

				// tests :
				// - pour savoir si la valeur de la variable d'entete est une
				// valeur
				// d'arret: tableSousVarDefinition.getM_AVariableComposant()
				// doit
				// etre diff�rent de null
				// - pour savoir si la valeur de la variable d'entete correspond
				// �
				// une
				// table sous variable tableSousVarDefinition doit etre
				// diff�rent de
				// null

				if (!depassementCapaciteMessage
						&& tableSousVarDefinition != null
				// && !valeurVarEntetePaquetCourant
				// .equals(valeurArretLecturePaquets)
				) {
					tableSousVarPaquetCourant = tableSousVarDefinition.copy();

					// r�cup�ration de la liste des variable
					listeSousVariablePaquetCourant = tableSousVarPaquetCourant
							.getM_AVariableComposant();

					// if (listeSousVariablePaquetCourant != null) {
					// cr�ation d'une liste de sous variables temporaire
					// pour modification
					// car on ne peut modifier directement
					// listeSousVariablePaquetCourant

					List<AVariableComposant> listesousVariable2 = new ArrayList<AVariableComposant>();

					listesousVariable2 = renseignerListeSousVariable(
							listeSousVariablePaquetCourant, start, msg);

					for (AVariableComposant composant : listesousVariable2) {
						composant.setParent(paquetCourant);
					}
					tableSousVarPaquetCourant
							.setM_AVariableComposant(listesousVariable2);
					paquetCourant
							.ajouterTableSousVariable(tableSousVarPaquetCourant);
					listePaquets.add(paquetCourant);
					// }
				} else {
					// si le premier paquet est incorrect, on arrete la lecture
					// des paquets
					SamngLogger
							.getLogger()
							.error(varEntetePaquetCourant.getDescriptor()
									.getM_AIdentificateurComposant().getNom()
									+ "="
									+ valEntetePourMessage
									+ Messages
											.getString("ParseurParcoursJRU.16")
									+ " "
									+ Messages
											.getString("errors.nonblocking.blockStart1")
									+ " " + " " + indiceBlocCourant);
					finMessage = true;
				}
			} else {
				//				System.out.print("   fin de lecture des paquets---"); //$NON-NLS-1$
				//				System.out.print("   longueur totale calcul�e message courant: " + GestionnaireLongueurMessage.getInstance().getLongueurCalculeeMessageCourant()+"---"); //$NON-NLS-1$
				finMessage = true;
			}

			// Quand un paquet contient le code 255, il s'agit de la fin
			// du message. Les bits suivants doivent �tre ignor�s. CF DR22G
			if (valEntetePrecedente.equals("255")) {
				finMessage = true;
			}

		}
		lecturePaquetEnCours = false;
		positionPaquetSuivantUsing_L_Packet = 0;
		// si la variable d'entete du dernier paquet n'est pas �gale � la valeur
		// d'arret lev�e d'une erreur
		// if (!valeurVarEntetePaquetCourant.equals(valeurArretLecturePaquets))
		// SamngLogger.getLogger().error(
		//		Messages.getString("ParseurParcoursJRU.40") + definitionStructureDynamique.getDescriptor().getM_AIdentificateurComposant().getNom() + Messages.getString("ParseurParcoursJRU.41") + valeurArretLecturePaquets + Messages.getString("ParseurParcoursJRU.42") + valeurVarEntetePaquetCourant); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return listePaquets;
	}

	private void gereVariablesTypeRepere(DescripteurComposite descrComp,
			int id, Message msg, int start, DescripteurEvenement descrEvent,
			Evenement ev) {
		int size = descrComp.getLength();
		pos = 8;
		int codeVar = 0;

		for (int i = 1; i < size; i++) {
			DescripteurVariable descVar = (DescripteurVariable) descrComp
					.getEnfant(i);
			codeVar = descVar.getM_AIdentificateurComposant().getCode();
			// recherche dans les variables simples
			AVariableComposant var = GestionnairePool.getInstance()
					.getVariable(codeVar);

			if (var == null)
				// recherche dans les variables dynamiques
				var = GestionnairePool.getInstance().getVariableDynamique(
						codeVar);

			if (var == null)
				System.out.println("code variable not found : " + codeVar); //$NON-NLS-1$
			else {

				if (descrComp.isHasEntreeLogique() && i == size - 1) {
					int val = descrComp.isValeurEntreeLogique() ? 1 : 0;
					byte[] tab = { (byte) val };
					var.setValeur(tab);
					continue;
				}
				TypeVariable typeVar = var.getDescriptor().getTypeVariable();
				if (typeVar == TypeVariable.STRUCTURE_DYNAMIQUE) {

					// r�cup�ration de la d�finition de la structure dynamique
					StructureDynamique defStructureDyn = (StructureDynamique) var;

					StructureDynamique structureDynamique2 = gererStructureDynamique(
							defStructureDyn, start, msg);

					// affectation de la structure remplie � la sous-variable
					var = structureDynamique2;

				} else if (typeVar == TypeVariable.TABLEAU_DYNAMIQUE) {

					// r�cup�ration de la d�finition du tableau
					// dynamique
					TableauDynamique defTableauDyn = null;

					if (var != null)
						defTableauDyn = (TableauDynamique) var;

					TableauDynamique tableauDynamique = gererTableauDynamique(
							defTableauDyn, start, msg);
					// affectation de la structure remplie � la sous-variable
					var = tableauDynamique;
				} else if (typeVar == TypeVariable.CHAINE_DYNAMIQUE) {

					ChaineDynamique defChaineDyn = (ChaineDynamique) var;

					// remplissage de la chaine dynamique
					ChaineDynamique chaineDynamique = gererChaineDynamique(
							defChaineDyn, start, msg);

					// affectation de la structure remplie � la sous-variable
					var = chaineDynamique;
				}

				else {

					remplirValue(var, start, msg);
					pos += var.getDescriptor().getTailleBits();
					GestionnaireLongueurMessage.getInstance()
							.incrementerLongueurCalculeeMessageCourant(
									var.getDescriptor()
											.getM_AIdentificateurComposant()
											.getNom(),
									var.getDescriptor().getTailleBits());
					if (GestionnaireLongueurMessage.getInstance()
							.getLongueurCalculeeMessageInterneCourant() != 0) {
						GestionnaireLongueurMessage
								.getInstance()
								.incrementerLongueurCalculeeMessageInterneCourant(
										var.getDescriptor()
												.getM_AIdentificateurComposant()
												.getNom(),
										var.getDescriptor().getTailleBits());
					}
				}
				msg.ajouterVariable(var);

				if (codeVar == TypeRepere.date.getCode()) {
					try {
						// setDate(ConversionTempsJRU.getDateFromVariableDATE(value));
						double d = ConversionTempsJRU
								.getDateFromVariableDATE(var);
						setDate(d);

						// setTime(ConversionBase.HexaBCDToDecimal(value[3])*3600*1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (codeVar == TypeRepere.temps.getCode()) {
					setTimeCount(ConversionTempsJRU
							.getTimeFromVariableTIME(var));

				} else if (codeVar == TypeRepere.distance.getCode()) {
					setNewDistance(Double.parseDouble(var.toString())
							* ConstantesParcoursJRU.pasCptDistance);

				} else if (codeVar == TypeRepere.diametreRoue.getCode()) {
					setDiametreRoue(Double.parseDouble(var.toString()));
					msg.setDiametreRoue(diametreRoue);
				}
			}
		}

	}

	private void evIsRuptureAcquisition(Message msg) {
		// on remet tous les compteurs de temps et les variable globale utilis�e
		// � 0
		resetCumulsTemps();
		setLastCptTempsTimeChange(0);
		nomFichierBinaire = ""; //$NON-NLS-1$
		setNbBloc(0);
		setCumulDistance(0);
		setDerniereValeurCptDistance(0);
		setCumulDistanceMax(0);

		setPremiereDateRencontree(true);
		// on d�fini le temps absolu � l'aide :
		// du nombre de jour depuis la date pivot + nombre de millisecondes

		if (isDateTimeChanged()) {
			this.tc.addDate(date);
			this.tc.addTime(getTime() + getTimeCount());
			// on stocke la date et l'heure dans 2 variables
			setDerniereValeurDate(date);
			setDerniereValeurHeure(getTime());
		} else {
			this.tc.addDate(getDerniereValeurDate());
			this.tc.addTime(getTime() + getTimeCount());
			setDerniereValeurHeure(getTime());
		}
		setDerniereValeurCptTemps(getTimeCount());

		int nbVarAna = 0;
		int nbVarDis = 0;

		if (msg.getVariablesAnalogique() != null) {
			List<VariableAnalogique> varsAna = msg.getVariablesAnalogique();
			nbVarAna = varsAna.size();
			for (int i = 0; i < nbVarAna; i++) {
				// varsAna.get(i).setValeurChaine("0");
				if (varsAna.get(i).getDescriptor().getType() == Type.string)
					varsAna.get(i).setValeur("".getBytes()); //$NON-NLS-1$
				else {
					byte[] valeurTmp = (byte[]) varsAna.get(i).getValeur();
					for (int a = 0; a < valeurTmp.length; a++) {
						valeurTmp[a] = 0;
					}
					varsAna.get(i).setValeur(valeurTmp);
				}
			}
		}

		if (msg.getVariablesDiscrete() != null) {
			List<VariableDiscrete> varsDis = msg.getVariablesDiscrete();
			nbVarDis = varsDis.size();
			for (int i = 0; i < nbVarDis; i++) {
				// varsDis.get(i).setValeurChaine("false");
				if (varsDis.get(i).getDescriptor().getType() == Type.string)
					varsDis.get(i).setValeur("".getBytes()); //$NON-NLS-1$
				else {
					byte[] valeurTmp = (byte[]) varsDis.get(i).getValeur();
					for (int a = 0; a < valeurTmp.length; a++) {
						valeurTmp[a] = 0;
					}
					varsDis.get(i).setValeur(valeurTmp);
				}
			}
		}

		if (msg.getVariablesComplexe() != null) {
			// nbVar+=this.getVariablesComplexe().size();
			Collection<VariableComplexe> listeVarComp = msg
					.getVariablesComplexe();
			int nbSousVarMax = 0;
			for (VariableComplexe complexe : listeVarComp) {
				nbSousVarMax = complexe.getVariableCount();
				for (int i = 0; i < nbSousVarMax; i++) {
					TypeVariable typeVar = complexe.getEnfant(i)
							.getDescriptor().getTypeVariable();
					if (typeVar == TypeVariable.VAR_DISCRETE) {
						// complexe.getEnfant(i).setValeurChaine("false");
						if (complexe.getEnfant(i).getDescriptor().getType() == Type.string)
							complexe.getEnfant(i).setValeur("".getBytes()); //$NON-NLS-1$
						else {
							byte[] valeurTmp = (byte[]) complexe.getEnfant(i)
									.getValeur();
							for (int a = 0; a < valeurTmp.length; a++) {
								valeurTmp[a] = 0;
							}
							complexe.getEnfant(i).setValeur(valeurTmp);
						}

					} else if (typeVar == TypeVariable.VAR_ANALOGIC) {
						// complexe.getEnfant(i).setValeurChaine("0");
						if (complexe.getEnfant(i).getDescriptor().getType() == Type.string)
							complexe.getEnfant(i).setValeur("".getBytes()); //$NON-NLS-1$
						else {
							byte[] valeurTmp = (byte[]) complexe.getEnfant(i)
									.getValeur();
							for (int a = 0; a < valeurTmp.length; a++) {
								valeurTmp[a] = 0;
							}
							complexe.getEnfant(i).setValeur(valeurTmp);
						}

					}
				}
			}
		}
	}

	private void initialiserVariablesTempsDistance() {
		// setDate(0);
		// setTime(0);
		// setTimeCount(0);
		// setNewDistance(0);
		// setDiametreRoue(0);
		// setTimeBeforeChange(0);
		setDateTimeChanged(false);
		setTimeCountChanged(false);
		setDistanceChanged(false);
	}

	private void evIsRazCompteurTemps() {

		// le temps voulu est en milliseconde donc on multiplie par 1000
		incrementerCumulTempsMax(ConstantesParcoursJRU.pasCptTps
				* (long) (ConstantesParcoursJRU.resolutionTemps * 1000)
				* ConstantesParcoursJRU.maxCptTps);
		setTimeCount(0);
		// la valeur du cumul de temps est �gale � :
		// cumul temps + valeur courante du compteur temps - derni�re valeur du
		// compteur temps

		if (getDerniereValeurCptTemps() != 0) {
			incrementerCumulTemps(3600 - getDerniereValeurCptTemps());
		}

		this.tc.addDate(date);
		this.tc.addTime(getTime());

		// ///////////////////////////////////////////////////////
		setPremiereDateRencontree(true);

		// on remet � z�ro la derni�re valeur du compteur temps
		setDerniereValeurCptTemps(0);

		if (distanceChanged) {
			incrementerCumulDistance(getNewDistance()
					- getDerniereValeurCptDistance());
			setDerniereValeurCptDistance(getNewDistance());
		}
	}

	private void evIsRazCompteurDistance() {

		double rapport = getCumulDistance() / getIncRazDist();
		double ecartEntier = Math.abs(Math.round(rapport) - rapport);
		if (ecartEntier < 0.00001 && rapport != Math.round(rapport)) {
			rapport = Math.round(rapport);
		}

		setLastRaz(((int) (rapport)) * getIncRazDist());

		double newRaz = getLastRaz() + getIncRazDist();
		setCumulDistance(newRaz);

		if (isTimeCountChanged()) {
			// la valeur du cumul detemps est �gale � :
			// cumul temps + valeur courante du compteur temps - derni�re valeur
			// du compteur temps
			incrementerCumulTemps(getTimeCount() - getDerniereValeurCptTemps());
			this.tc.addTime(getDerniereValeurHeure() + getCumulTemps()
					+ getCumulTempsMax());
			setDerniereValeurCptTemps(getTimeCount());
		}
		setDerniereValeurCptDistance(0);
	}

	private void evIsChangementHeure() {
		// cas des �v�nements de changement d'heure
		// on ne doit ici pas prendre en compte le cumul du compteur temps
		// mais on ajoute la valeur de la variable LatchTemps(tempsAvantChgt)
		// pour les �v�nements suivants

		this.tc.addDate(date);

		setDateBeforeChange(getDerniereValeurDate());
		setTimeCountBeforeChange((long) getDerniereValeurCptTemps());
		setTimeBeforeChange((long) getDerniereValeurHeure());

		// on remet les cumul de temps � 0
		resetCumulsTemps();
		// on stocke les valeurs des date, heure et compteur temps
		setDerniereValeurCptTemps(getTimeCount());
		setDerniereValeurDate(date);
		setDerniereValeurHeure(getTime());

		this.tc.addTime(getTime() + getTimeCount());

		if (distanceChanged) {
			incrementerCumulDistance(getNewDistance()
					- getDerniereValeurCptDistance());
			setDerniereValeurCptDistance(getNewDistance());
		}
	}

	private void newDateHeure() {
		if (!isPremiereDateRencontree()) {
			setPremiereDateRencontree(true);
			setCumulTemps(0);
			this.tc.addDate(date);
			// TAG DATE.DAY this.tc.addDate(getDate()+1);
			this.tc.addTime(getTime() + getTimeCount());

			if (isTimeCountChanged()) {
				setDerniereValeurCptTemps(getTimeCount());
			}
			setDerniereValeurDate(date);
			setDerniereValeurHeure(getTime());
		} else if (isTimeCountChanged()) {
			this.tc.addDate(date);
			// TAG DATE.DAY this.tc.addDate(getDate()+1);
			if (getTimeCount() > getDerniereValeurCptTemps()) {
				incrementerCumulTemps(getTimeCount()
						- getDerniereValeurCptTemps());
			}
			this.tc.addTime(getTime() + getTimeCount());
			setDerniereValeurCptTemps(getTimeCount());
			setDerniereValeurDate(date);
			setDerniereValeurHeure(getTime());
		}
	}

	private void noNewDateHeure() {
		if (isPremiereDateRencontree() && isTimeCountChanged()) {
			if ((getTimeCount() > getDerniereValeurCptTemps())) {
				incrementerCumulTemps(getTimeCount()
						- getDerniereValeurCptTemps());
			}
			this.tc.addTime(getTimeCount() + getTime());
			setDerniereValeurCptTemps(getTimeCount());
		}
	}

	private Message gererTempsMsg(int start, Message msg, Evenement ev,
			DescripteurComposite descrComp, int id,
			DescripteurEvenement descrEvent) {
		msg.setEvenement(ev);

		gereVariablesTypeRepere(descrComp, id, msg, start, descrEvent, ev);

		// Algorithme de gestion du temps
		if (ev.isRuptureAcquisition() && isLastMsgNotRuptAcq()) {
			evIsRuptureAcquisition(msg);
		} else if (ev.isRazCompteurTemps()) {
			evIsRazCompteurTemps();
		}
		// cas des �v�nements marqu�s de razCompteurDistance dans le fichier xml
		else if (ev.isRazCompteurDistance()) {
			evIsRazCompteurDistance();
		} else if (ev.isChangementHeure()) {
			evIsChangementHeure();
		}
		// tous les autres cas
		else {
			// si la variable dateHeure est valoris�e
			if (isDateTimeChanged()) {
				newDateHeure();
			} else {
				noNewDateHeure();
			}
			// si la distance est valoris�e on incr�mente le compteur distance
			// la valeur du cumul detemps est �gale � :
			// cumul distance + valeur courante du compteur distance - derni�re
			// valeur du compteur distance
			if (distanceChanged) {
				incrementerCumulDistance(getNewDistance()
						- getDerniereValeurCptDistance());
				setDerniereValeurCptDistance(getNewDistance());
			}
		}

		ParseurAdapteur padapt = new ParseurAdapteur();
		padapt.gererEvNonDate(msg);

		if (isPremiereDateRencontree())
			msg.setAbsoluteTime(this.tc.getCurrentDateAsMillis());
		msg.setAbsoluteDistance(getCumulDistance());

		return msg;
	}

	private boolean isLastMsgNotRuptAcq() {
		try {
			return (!lastMessage.getEvenement().isRuptureAcquisition());
		} catch (Exception e) {
			return true;
		}

	}

	@Override
	protected Message chargerMessage(int start, boolean isExploration) throws ParseurBinaireException {
		initialiserVariablesTempsDistance();

		Message msg = new Message();
		msg.setMessageId(start);

		int id = gestionID(start);

		// calcul L_Message
		byte[] L_Message = new byte[2];
		L_Message[0] = this.message[start + 1];
		L_Message[1] = this.message[start + 2];
		int longueurM = (new BigInteger(L_Message).intValue() & 0xFFFF) >> (16 - ConstantesParcoursJRU.nbBitsLongueurL_Message);
		msg.setLongueur(longueurM);
		msg.setOffsetDebut(start);
		msg.setOffsetFin(start + msg.getLongueur());
		GestionnaireLongueurMessage.getInstance()
				.setLongueurEnregistreeMessageCourant(longueurM * 8);
		GestionnaireLongueurMessage.getInstance()
				.setLongueurCalculeeMessageCourant(8);

		DescripteurComposite descrComp = null;
		DescripteurEvenement descrEvent = null;

		// get the event id in the table
		int length = descrTable.getLength();

		for (int i = 0; i < length; i++) {
			descrComp = (DescripteurComposite) descrTable.getEnfant(i);
			descrEvent = (DescripteurEvenement) descrComp.getEnfant(0);
			if (descrEvent != null
					&& descrEvent.getM_AIdentificateurComposant().getCode() == id) {
				break;
			}
			descrComp = null;
		}

		// the code read in the message is not found in the table
		if (descrComp == null) {
			gererIDNonTrouve(msg, id);
		}

		Evenement ev = GestionnairePool.getInstance().getEvent(id);
		DescripteurEvenement descEvt = (DescripteurEvenement) ev
				.getM_ADescripteurComposant();

		// cas o� l'�v�vement est dans la table evt/var et dans le fichier xml
		if (descEvt.getNom() == null) {
			gererNomEvtNonTrouve(id, ev, msg);
		} else {
			gererNomEvtTrouve(id, descEvt, ev, msg);
		}

		msg = gererTempsMsg(start, msg, ev, descrComp, id, descrEvent);
		msg.deepTrimToSize();
		return msg;
	}

	/**
	 * Loads a message for exploration (with minimum ressources)
	 * 
	 * @param msg
	 *            an instance of the message
	 * @param start
	 *            the start position for the message
	 * @return the message filled
	 * @throws ParseurBinaireException
	 *             throws an exception if some error occurs
	 */
	private Message chargerMessageExplore(int start)
			throws ParseurBinaireException {
		initialiserVariablesTempsDistance();

		Message msg = new Message();
		msg.setMessageId(start);

		// calcul L_Message
		byte[] L_Message = new byte[2];
		L_Message[0] = this.message[start + 1];
		L_Message[1] = this.message[start + 2];
		int longueurM = (new BigInteger(L_Message).intValue() & 0xFFFF) >> (16 - ConstantesParcoursJRU.nbBitsLongueurL_Message);
		msg.setLongueur(longueurM);
		msg.setOffsetDebut(start);
		msg.setOffsetFin(start + msg.getLongueur());

		// Date
		// Positionnement au d�but du champs DATE
		pos = posDate;

		// Lecture de la variable DATE
		AVariableComposant var = GestionnairePool.getInstance().getVariable(
				74030);

		// Ajout de la variable DATE au message courant
		remplirValue(var, start, msg);
		msg.ajouterVariable(var);

		try {
			// Mise � jour de la DATE courante
			double d = ConversionTempsJRU.getDateFromVariableDATE(var);
			setDate(d);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Time
		// Positionnement au d�but du champs TIME
		pos = posTime;

		// Lecture de la variable TIME
		var = GestionnairePool.getInstance().getVariable(74040);

		// Ajout de la variable TIME au message courant
		remplirValue(var, start, msg);
		msg.ajouterVariable(var);

		// Mise � jour du temps courant
		setTimeCount(ConversionTempsJRU.getTimeFromVariableTIME(var));

		// Mode
		// Positionnement au d�but du champs MODE
		pos = posMode;

		// Lecture de la variable MODE
		var = GestionnairePool.getInstance().getVariable(60720);

		// Ajout de la variable MODE au message courant
		remplirValue(var, start, msg);
		msg.ajouterVariable(var);

		// si la variable dateHeure est valoris�e (setDate())
		if (isDateTimeChanged()) {
			newDateHeure();
		} else {
			noNewDateHeure();
		}

		ParseurAdapteur padapt = new ParseurAdapteur();
		padapt.gererEvNonDate(msg);

		if (isPremiereDateRencontree())
			msg.setAbsoluteTime(this.tc.getCurrentDateAsMillis());

		msg.deepTrimToSize();
		return msg;
	}

	@Override
	protected AVariableComposant chargerReperes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ADescripteurComposant chargerTableEvtVariable()
			throws ParseurBinaireException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean checkOctetsFF(int start, int noFF) {
		for (int i = 0; i < noFF; ++i) {
			if ((message[start + i] & 0xff) != codeFF) {
				return false;
			}
		}
		return true;
	}

	private void resetCumulsTemps() {
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
		nomFichierBinaire = ""; //$NON-NLS-1$
		setPremiereDateRencontree(false);
		setNbBloc(0);
		listeTableauOctetBrutMsg.clear();
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

		String nomTableAssocciationEvtVars = getNomTableEvenementVariable();
		try {
			ParseurJRUTableAssociationEvVars.getInstance()
					.chargerNomFichierXML(nomTableAssocciationEvtVars);
		} catch (Exception e) {
			throw new ParseurXMLException(
					Messages.getString("ChargeurParcours.9") //$NON-NLS-1$
							+ nomTableAssocciationEvtVars, true);
			// System.out.println("ParseurJRUTableAssociationEvVars a �chou�");
		}

		String nomFichierXML = ParseurJRUTableAssociationEvVars.getInstance()
				.getFichierDescr();
		return nomFichierXML;
	}

	/**
	 * M�thode permettant de r�cup�rer la version JRU (229,230 etc...) dans
	 * l'entete du fichier binaire utilis�e uniquement pour les .bru
	 * 
	 * @param nomFichier
	 * @return la version
	 * @throws Exception
	 */
	public String getVersionDansEnteteBinaire(String nomFichier)
			throws Exception {

		// check if the file is ok
		if (nomFichier == null || nomFichier.length() == 0
				|| !new File(nomFichier).exists()) {
			return null;
		}
		File binaryFile = new File(nomFichier);

		byte[] octetsFichier = loadBinaryFile(binaryFile, 0, -1);
		if (octetsFichier == null) {
			return null;
		}
		if (octetsFichier.length < 5)
			return null;

		String version = new String(new BigInteger(Arrays.copyOfRange(
				octetsFichier, 3, 5)).toString());
		return version;
	}

	@Override
	public void setCrcEnregistrements(
			HashMap<Integer, Boolean> crcEnregistrements) {
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
			if (lastMessage != null
					&& crtMsg.getFlag().getLabel().contains("{")) { //$NON-NLS-1$

				Flag flagLastMsg = lastMessage.getFlag();

				if (flagLastMsg == null
						|| !flagLastMsg.getLabel().contains("{")) { //$NON-NLS-1$
					Flag fl = new Flag(0, "}", lastMessage.getEvenement() //$NON-NLS-1$
							.getM_ADescripteurComposant()
							.getM_AIdentificateurComposant().getNom());
					if (flagLastMsg != null) {
						flagLastMsg.appendFlag(fl);
					} else {
						lastMessage.setFlag(fl);
					}
				} else {
					if (lastMessage.getEvenement().getM_ADescripteurComposant()
							.getM_AIdentificateurComposant().getCode() != crtMsg
							.getEvenement().getM_ADescripteurComposant()
							.getM_AIdentificateurComposant().getCode()) {
						Flag fl = new Flag(0, crtMsg.getFlag().getLabel()
								.replace("{", ""), lastMessage.getEvenement() //$NON-NLS-1$ //$NON-NLS-2$
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

			// zzzFlagRupt

			Flag newFlag = GestionnaireFlags.getInstance().getFlag(
					GestionnaireFlags.getInstance().ID_TC);

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
			// zzzFlagRupt
			flag = crtMsg.getFlag();
			Flag newFlag = GestionnaireFlags.getInstance().getFlag(
					GestionnaireFlags.getInstance().ID_DC);

			if (flag != null) {
				// if exists flag, then append to it
				if (!crtMsg.getEvenement().isChangementHeure())
					flag.appendFlag(newFlag);
			} else {
				// if not exist flag create one
				crtMsg.setFlag(newFlag);
			}

			// byte[] val=new byte[1];
			// val[0]=0;

			// List<VariableAnalogique> listva=crtMsg.getVariablesAnalogique();
			// if (listva!=null) {
			// int nbVarAna=listva.size();
			// for (int i = 0; i < nbVarAna; i++) {
			// AVariableComposant v=listva.get(i);
			// v.setValeurChaine("0");
			// v.setValeurObjet("0");
			// v.setValeur(val);
			// }
			// }

			// List<VariableComplexe> listvc=crtMsg.getVariablesComplexe();
			// if (listvc!=null) {
			// int nbVarComp=listvc.size();
			// for (int i = 0; i < nbVarComp; i++) {
			// AVariableComposant v=listvc.get(i);
			// v.setValeurChaine("0");
			// v.setValeurObjet("0");
			// v.setValeur(val);
			// }
			// }

			// List<VariableComposite> listvcompo=crtMsg.getVariablesComposee();
			// if (listvcompo!=null) {
			// int nbVarCompo=listvcompo.size();
			// for (int i = 0; i < nbVarCompo; i++) {
			// AVariableComposant v=listvcompo.get(i);
			// v.setValeurChaine("0");
			// v.setValeurObjet("0");
			// v.setValeur(val);
			// }
			// }

			// List<VariableDiscrete> listvd=crtMsg.getVariablesDiscrete();
			// if (listvd!=null) {
			// int nbVarDis=listvd.size();
			// for (int i = 0; i < nbVarDis; i++) {
			// AVariableComposant v=listvd.get(i);
			// v.setValeurChaine("0");
			// v.setValeurObjet("0");
			// v.setValeur(val);
			// }
			// }
		}
	}

	@Override
	protected void setSegmentsTemp(Message crtMsg, Message prevMsg) {

		if (crtMsg != null) {

			// si l'�v�nement est un �v�nement de rupture d'acquisition ou bien
			// le premier message du parcours
			if (prevMsg == null
					|| crtMsg.getEvenement().isRuptureAcquisition()
					&& !prevMsg.getEvenement().isRuptureAcquisition()
					&& prevMsg.getEvenement().getM_ADescripteurComposant()
							.getM_AIdentificateurComposant().getCode() != crtMsg
							.getEvenement().getM_ADescripteurComposant()
							.getM_AIdentificateurComposant().getCode()) {
				// si on est sur les messages de 2 � n
				if (prevMsg != null) {

					// si un segment a d�j� �t� cr�� on teste si l'�v�nement
					// pr�c�dent n'est pas le d�but du segment
					// si c'est le cas on enregistre le segment et on en cr�� un
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
						segment.setTempCorrige(segment.getTempInitial());
						this.lastSegTemp = segment;

					}

				}
				// on est sur le message 1: on cr�� un segment
				else {

					SegmentTemps segment = new SegmentTemps();
					segment.setStartMsgId(crtMsg.getMessageId());
					segment.setTempInitial(crtMsg.getAbsoluteTime());
					segment.setTempCorrige(segment.getTempInitial());
					this.lastSegTemp = segment;
				}
			}

			else if (prevMsg != null) {

				// on v�rifie si il y a un changement d'heure
				boolean isTimeEvtChgt = crtMsg.getEvenement()
						.isChangementHeure();

				if (isTimeEvtChgt) {
					// si il y a un segment pr�c�dent
					if (this.lastSegTemp != null) {
						enregistrerSegmentTemps(prevMsg);
					}

					TableRuptures.getInstance().ajouterRuptureTemps(
							crtMsg.getMessageId(),
							TypeRupture.CHANGEMENT_HEURE_RUPTURE);

					SegmentTemps segment = new SegmentTemps();
					segment.setStartMsgId(crtMsg.getMessageId());
					segment.setTempInitial(crtMsg.getAbsoluteTime());
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
					segment.setTempCorrige(segment.getTempInitial());
					this.lastSegTemp = segment;
				} else if (prevMsg.isMessageIncertitude()
						&& !crtMsg.isMessageIncertitude()) {
					enregistrerSegmentTemps(prevMsg);
					// a new segment starts
					SegmentTemps segment = new SegmentTemps();
					segment.setStartMsgId(crtMsg.getMessageId());
					segment.setTempInitial(crtMsg.getAbsoluteTime());
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
	public byte[] setVariableValue(AVariableComposant var, byte[] valueOrig) throws BadStructureFileException {

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
				value = Arrays.copyOfRange(valueOrig, 0, valueOrig.length);
				// for (int ii = 0; ii < vo_size; ii++) {
				// value[ii] = valueOrig[ii];
				// }

				// inversion des octets et des bits de la variable complexe
				if (varComp.getDescriptor().getPoidsPremierBit() == Poids.LSB) {
					value = reverseBits(value);
				}

				if (varComp.getDescriptor().getPoidsPremierOctet() == Poids.LSB) {
					value = reverseOctets(value);
				}

				typeVar = v.getDescriptor().getType();
				// if it's a value given in bits not octets, then gets the bits
				if (typeVar == Type.uintXbits || typeVar == Type.BCD4
						|| typeVar == Type.BCD8 || typeVar == Type.intXbits
						|| typeVar == Type.boolean1) {

					retVal = setXbitsValue2(v, value, posVar, remainedBits);
					remainedBits = (int) (retVal & 0xFFFFFFFF);
					posVar = (int) ((retVal >> 32) & 0xFFFFFFFF);
					// v.setValeur(null);

				}
				// cas d'une variable de type reserved
				else if (typeVar == Type.reserved) {

					// on ne r�cup�re aucune valeur pour ce type de variable

					// on teste si le nombre de bits actuels � lire est
					// inf�rieur ou sup�rieur � la taille de la variable
					// reserved
					int diffBits = remainedBits + v.getDescriptor().getTailleBits();
					// s'il est necessaire d'avancer d'un ou plusieurs octets(si
					// diffBits>8)
					int nbOctets = Math.abs(diffBits) / 8;
					if (nbOctets > 0) {
						// on calcul le nombre de bits restants
						remainedBits = Math.abs(diffBits) % 8;
						// on avance d'autant d'octets que n�cessaire
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

					// cas o`u la variable commence au d�but d'un octet
					if (remainedBits == 0) {

						byte valTemp[] = Arrays.copyOfRange(value, posVar,
								posVar + v.getDescriptor().getTailleOctets());

						if (poidsPremierOctetVar == Poids.LSB)
							valTemp = reverseOctets(valTemp);

						if (poidsPremierBitVar == Poids.LSB)
							valTemp = reverseBits(valTemp);

						v.setValeur(valTemp);

					}
					// cas o� la varaible commence � un nb bit donn� dans
					// l'octet
					else {

						byte valTemp[] = Arrays.copyOfRange(value, posVar,
								posVar + v.getDescriptor().getTailleOctets()
										+ 1);
						int mask = (int) Math.pow(2, 8 - remainedBits) - 1;
						// on met � 0 tous les bits inutiles sur le premier
						// octet
						valTemp[0] = (byte) (valTemp[0] & mask);
						// on place le tableau de byte dans un BigInteger
						BigInteger bigInt = new BigInteger(valTemp);
						// on fait un d�calage d'autant de bits que necessaire
						bigInt = bigInt.shiftRight(8 - remainedBits);

						byte valVar[] = bigInt.toByteArray();
						if (poidsPremierOctetVar == Poids.LSB)
							valVar = reverseOctets(valVar);

						if (poidsPremierBitVar == Poids.LSB)
							valVar = reverseBits(valVar);

						v.setValeur(valVar);
					}

					posVar += (v.getDescriptor().getTailleOctets());

				}
			}
			var.setValeur(value);
		} else {

			value = new byte[vo_size];
			value = Arrays.copyOfRange(valueOrig, 0, valueOrig.length);
			// for (int ii = 0; ii < vo_size; ii++) {
			// value[ii] = valueOrig[ii];
			// }

			// reverse the order of octets or bytes if the poids are set on LSB
			if (var.getDescriptor().getPoidsPremierBit() == Poids.LSB) {
				value = reverseBits(value);
			}
			if (var.getDescriptor().getPoidsPremierOctet() == Poids.LSB) {
				value = reverseOctets(value);
			}

			// set the value
			try {
				var.setValeur(value);
			} catch (NullPointerException ex) {
				System.out.println("Null pointer"); //$NON-NLS-1$
			}
		}
		return value;
	}

	@Override
	protected long setXbitsValue2(AVariableComposant v, byte[] value,
			int posVar, int startBitIdx) {

		// r�cup�ration de la longueur de la variable
		int tailleVar = v.getDescriptor().getTailleBits();
		Poids poidsPremierBitVar = v.getDescriptor().getPoidsPremierBit();
		Poids poidsPremierOctetVar = v.getDescriptor().getPoidsPremierOctet();
		Type typeVariable = v.getDescriptor().getType();
		byte b = 0;
		int mask;
		int extractedBytesCnt = 0;
		int positionsToShift = 0;
		// anciennes sp�cifications
		// // chaque fois que l'on recommence un nouvel octet,
		// // on consid�re que la varaible pr�c�dente(dans l'octet n'est ni en
		// // poids-1er bit = MSB ni LSB)
		// if (startBitIdx == 0) {
		// firstPartLSB = false;
		// firstPartMSB = false;
		// }
		// cas o� la variable est enti�rement stock�e dans l'octet courant
		// longueur de la variable inf�rieure au nombre de bits restant dans
		// l'octet
		int remainedBits;
		if (tailleVar <= 8 - startBitIdx) {

			// r�cup�ration du nombre de bits restant dans l'octet
			remainedBits = 8 - startBitIdx;

			// ajout olivier
			// cas d'une variable en poids-1er-bit=LSB
			if (poidsPremierBitVar == Poids.LSB) {
				// on inverse les bits de tout le tableau d'octet
				byte[] bitsReverse = reverseBits(value);

				// cas d'un BCD4
				{

					// nouvelles sp�cifications: on d�cale d'autant de bits que
					// la
					// le bit de d�but de lecture(�tant donn� que l'on inverse

					b = (byte) (bitsReverse[posVar] >>> startBitIdx);

				}

				/*
				 * anciennes sp�cifications // si la varaible pr�c�dente dans le
				 * meme octet n'est pas en // MSB // if (firstPartLSB) { // //
				 * si on est pas au d�but de l'octet // if (startBitIdx > 0) {
				 * // // on r�cup�re les bits de droite // // l'octet et on
				 * d�cale // b = (byte) (bitsReverse[posVar] >>> remainedBits
				 * -length ); // } // } // } // la premi�re varable dans l'octet
				 * est en poids-1er-bit=LSB // firstPartLSB = true;
				 */
			}
			// if (poidsPremierBitVar == Poids.MSB)
			else {

				// nouvelles sp�cifications
				b = (byte) (value[posVar] >>> remainedBits - tailleVar);

				/*
				 * anciennes sp�cifications // if (firstPartMSB) { // if
				 * (startBitIdx > 0) { // b = (byte) (value[posVar] >>>
				 * remainedBits - length); // } // } // } // la premi�re
				 * variable dans l'octet est en poids-1er-bit=MSB //
				 * firstPartMSB = true;
				 */

			}

			// on calcule le masque pour r�cup�rer seulement les bits qui nous
			// interressent
			mask = (int) Math.pow(2, tailleVar) - 1;
			// on applique le masque pour r�cup�rer seulement les bits qui nous
			// interressent
			cacheMemory[0] = (byte) (b & mask);

			// un seul octet a �t� extrait
			extractedBytesCnt = 1;

			// on met � jour l'index du bit � prendre dans l'octet
			startBitIdx = startBitIdx + tailleVar;

		}

		// cas o� la variable a une longueur sup�rieure
		// au nombre de bits restant dans l'octet
		else {

			int octetCourant = posVar;

			// r�cup�ration de ce qui reste dans le premier octet de la variable
			remainedBits = 8 - startBitIdx;

			// cas d'une variable en MSB
			if (poidsPremierBitVar == Poids.MSB) {

				// nouvelles sp�cifications
				b = (byte) (value[posVar]);

				// anciennes sp�cifications
				// if (startBitIdx > 0) {
				// // on prend les bits tout � gauche
				// // calcul du masque
				// mask = (int) (255 - (Math.pow(2, startBitIdx) - 1));
				// // d�calage
				// b = (byte) ((value[octetCourant] & mask) >>> startBitIdx);
				// cacheMemory[extractedBytesCnt] = b;
				// } else {
				// // sinon on prend les bits de droite
				// mask = (int) (Math.pow(2, remainedBits) - 1);
				// b = (byte) ((value[octetCourant] & mask));
				// cacheMemory[extractedBytesCnt] = b;
				// }

			}

			// cas d'une variable en LSB
			// if (v.getDescriptor().getPoidsPremierBit() == Poids.LSB) {
			else {

				// nouvelles sp�cifications
				// on r�cup�re l'octet entier
				b = (byte) (value[posVar]);

				// anciennes sp�cifications
				// b = bitsReverse[octetCourant];
				// if (startBitIdx > 0) {
				// b = (byte) (b >>> startBitIdx);
				// }
			}

			// on ne prend que les bits qui nous interressent
			mask = (int) Math.pow(2, remainedBits) - 1;
			// on met l'octet dans le tableau d'octets temporaire
			cacheMemory[extractedBytesCnt] = (byte) ((b & mask));

			// on incr�mente pour obtenir le prochain octet
			extractedBytesCnt++;
			// on met a jour le nombre de bits restant pour la variable
			tailleVar -= remainedBits;

			boolean varTerminee = false;
			while (!varTerminee) {

				// l'octet courant change d'indice
				octetCourant = posVar + extractedBytesCnt;
				// si le nombre de bits restants de la variable est inf�rieure �
				// 8
				if (tailleVar - 8 <= 0) {

					startBitIdx = tailleVar;

					positionsToShift = 8 - tailleVar;

					varTerminee = true;

				} else {
					// on r�cup�re l'octet entier

					tailleVar -= 8;

					if (tailleVar <= 0)
						varTerminee = true;

				}

				cacheMemory[extractedBytesCnt] = (byte) (value[octetCourant]);
				extractedBytesCnt++;
			}
		}

		// on cr�� un BigInteger qui permet de contenir toute la variable et de
		// faire des d�calages � gauche ou � droite
		// cacheMemory : tableau d'octets de taille fixe
		byte[] valuesByteArray = Arrays.copyOfRange(cacheMemory, 0,
				extractedBytesCnt);

		if (v.getDescriptor().getTailleBits() <= 8) {
			if (valuesByteArray.length == 2) {
				Type t = typeVariable;
				BigInteger bigInt = null;
				// cas de tout les entiers sign�s
				// il sont enregistr�s en compl�ment � 2 dans le fichier de
				// parcours
				if (t == Type.intXbits || t == Type.int16 || t == Type.int24
						|| t == Type.int32 || t == Type.int64 || t == Type.int8) {
					// on cr�� un BigInteger sign�
					bigInt = new BigInteger(valuesByteArray);

				}
				// on cr�� un BigInteger non sign�
				// if (t == Type.uintXbits || t == Type.uint8 || t ==
				// Type.uint16
				// || t == Type.uint24 || t == Type.uint32
				// || t == Type.uint64 || t == Type.real32
				// || t == Type.real64 || t == Type.dateHeureBCD)
				else {
					bigInt = new BigInteger(1, valuesByteArray);

				}
				bigInt = bigInt.shiftRight(positionsToShift);
				// cas o� la variabe est autre qu'un intxbits

				byte tabVar[] = bigInt.toByteArray();
				byte tabVar2[] = new byte[1];
				if (tabVar.length != 1) {
					if (poidsPremierBitVar == Poids.MSB) {
						mask = (int) Math.pow(2, tailleVar) - 1;
					}
					// if (v.getDescriptor().getPoidsPremierBit() == Poids.LSB)
					// {
					else {
						tabVar = reverseBits(tabVar);
						mask = (int) Math.pow(2, tailleVar) - 1;
						if (tabVar.length > 1)
							tabVar[0] = (byte) (tabVar[1] >> (8 - v
									.getDescriptor().getTailleBits()));
						else
							tabVar[0] = (byte) (tabVar[0] >> (8 - v
									.getDescriptor().getTailleBits()));

					}
					tabVar2[0] = (byte) (tabVar[0] & mask);
				} else {
					if (poidsPremierBitVar == Poids.LSB) {
						tabVar = reverseBits(tabVar);
					}
					tabVar2[0] = tabVar[0];
				}
				v.setValeur(tabVar2);
			} else {
				v.setValeur(valuesByteArray);
			}
		}

		else {
			if (positionsToShift > 0) {
				Type t = typeVariable;
				byte[] valueArr = null;

				BigInteger bigInt = null;
				// cas de tout les entiers sign�s
				// il sont enregistr�s en compl�ment � 2 dans le fichier de
				// parcours
				if (t == Type.intXbits || t == Type.int16 || t == Type.int24
						|| t == Type.int32 || t == Type.int64 || t == Type.int8) {
					// on cr�� un BigInteger sign�
					bigInt = new BigInteger(valuesByteArray);

				}
				// on cr�� un BigInteger non sign�
				// if (t == Type.uintXbits || t == Type.uint8 || t ==
				// Type.uint16
				// || t == Type.uint24 || t == Type.uint32
				// || t == Type.uint64 || t == Type.real32
				// || t == Type.real64 || t == Type.dateHeureBCD)
				else {
					bigInt = new BigInteger(1, valuesByteArray);

				}
				bigInt = bigInt.shiftRight(positionsToShift);
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
		// cas des intxbits: on doit reproduire le compl�ment � 2 sur l'octet de
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

		// on met � jour l'indice de d�but de lecture pour la variable suivante
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
		IStrategieControle controlTable = new CRC16CCITT();
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

	private void setDate(double newDateValue) {
		this.date = newDateValue;
		if (this.date != lastDateValue) {
			setDateTimeChanged(true);
			lastDateValue = this.date;
		} else
			setDateTimeChanged(false);
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

	private void incrementerCumulTempsMax(double increment) {
		setCumulTempsMax(getCumulTempsMax() + increment);
	}

	private void incrementerCumulTemps(double increment) {
		setCumulTemps(getCumulTemps() + increment);
	}

	private void incrementerCumulDistance(double increment) {
		setCumulDistance(getCumulDistance() + increment);
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
}
