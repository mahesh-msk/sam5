package com.faiveley.samng.principal.sm.parseurs.parseursTomNg;

import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.codeFF;
import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.nomFichierParcours;
import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.nomFichierXML;
import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.signatureDonneesParcours;
import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.tailleBlocData;
import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.tailleDonneesParcours;
import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.tailleEntete;
import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.tailleFichierParcours;
import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.tailleTableEvenement;
import static com.faiveley.samng.principal.sm.parseurs.parseursTomNg.ConstantesParcoursSamNg.codeContinueDebutBloc;
import static com.faiveley.samng.principal.sm.parseurs.parseursTomNg.ConstantesParcoursSamNg.codeContinueDebutDefaut;
import static com.faiveley.samng.principal.sm.parseurs.parseursTomNg.ConstantesParcoursSamNg.codeContinueFinBloc;
import static com.faiveley.samng.principal.sm.parseurs.parseursTomNg.ConstantesParcoursSamNg.codeContinueFinDefaut;
import static com.faiveley.samng.principal.sm.parseurs.parseursTomNg.ConstantesParcoursSamNg.codeDebut;
import static com.faiveley.samng.principal.sm.parseurs.parseursTomNg.ConstantesParcoursSamNg.codeDebutDefaut;
import static com.faiveley.samng.principal.sm.parseurs.parseursTomNg.ConstantesParcoursSamNg.codeFin;
import static com.faiveley.samng.principal.sm.parseurs.parseursTomNg.ConstantesParcoursSamNg.codeFinDefaut;
import static com.faiveley.samng.principal.sm.parseurs.parseursTomNg.ConstantesParcoursSamNg.formatCodage;
import static com.faiveley.samng.principal.sm.parseurs.parseursTomNg.ConstantesParcoursSamNg.tailleNomFichierXML;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.logging.SamngLogger;
import com.faiveley.samng.principal.sm.calculs.conversionTemps.ConversionTempsTomNg;
import com.faiveley.samng.principal.sm.controles.CRC16CCITT;
import com.faiveley.samng.principal.sm.controles.CRC32;
import com.faiveley.samng.principal.sm.controles.IStrategieControle;
import com.faiveley.samng.principal.sm.controles.ReturnCRC;
import com.faiveley.samng.principal.sm.controles.util.CRC16CCITTHash;
import com.faiveley.samng.principal.sm.data.compression.DecompressedFile;
import com.faiveley.samng.principal.sm.data.descripteur.ADescripteurComposant;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurCodeBloc;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurComposite;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurEvenement;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurTableAssociation;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.descripteur.Poids;
import com.faiveley.samng.principal.sm.data.descripteur.Temporelle;
import com.faiveley.samng.principal.sm.data.descripteur.Type;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.descripteur.donneeBlocComposant.ADonneeBlocComposant;
import com.faiveley.samng.principal.sm.data.descripteur.donneeBlocComposant.DonneeBloc;
import com.faiveley.samng.principal.sm.data.enregistrement.ErrorType;
import com.faiveley.samng.principal.sm.data.enregistrement.Evenement;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.ListMessages;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.flag.Flag;
import com.faiveley.samng.principal.sm.data.flag.GestionnaireFlags;
import com.faiveley.samng.principal.sm.data.identificateurComposant.AIdentificateurComposant;
import com.faiveley.samng.principal.sm.data.identificateurComposant.IdentificateurEvenement;
import com.faiveley.samng.principal.sm.data.identificateurComposant.IdentificateurVariable;
import com.faiveley.samng.principal.sm.data.multimedia.MultimediaFile;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.AParcoursComposant;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.Entete;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosFichierSamNg;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TableEvtVar;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TableLangueNomUtilisateur;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.Langage;
import com.faiveley.samng.principal.sm.data.variableComposant.Paquets;
import com.faiveley.samng.principal.sm.data.variableComposant.TableSousVariable;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.ChaineDynamique;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.StructureDynamique;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.TableauDynamique;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.VariableDynamique;
import com.faiveley.samng.principal.sm.erreurs.AExceptionSamNG;
import com.faiveley.samng.principal.sm.erreurs.BADEventLenghtException;
import com.faiveley.samng.principal.sm.erreurs.BadArgumentInFileException;
import com.faiveley.samng.principal.sm.erreurs.BadBlocStructureException;
import com.faiveley.samng.principal.sm.erreurs.BadCRCException;
import com.faiveley.samng.principal.sm.erreurs.BadEvTableCrcException;
import com.faiveley.samng.principal.sm.erreurs.BadEventCodeException;
import com.faiveley.samng.principal.sm.erreurs.BadFileLengthException;
import com.faiveley.samng.principal.sm.erreurs.BadHeaderCrcException;
import com.faiveley.samng.principal.sm.erreurs.BadHeaderInfoException;
import com.faiveley.samng.principal.sm.erreurs.BadStructureFileException;
import com.faiveley.samng.principal.sm.erreurs.BadTableEventStructureException;
import com.faiveley.samng.principal.sm.erreurs.BadVariableCodeException;
import com.faiveley.samng.principal.sm.erreurs.NoninterpretableMessageException;
import com.faiveley.samng.principal.sm.erreurs.ParseurBinaireException;
import com.faiveley.samng.principal.sm.erreurs.ParseurXMLException;
import com.faiveley.samng.principal.sm.fabriques.AFabriqueParcoursAbstraite;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.formats.FormatSAM;
import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurParcoursSamngExplorer;
import com.faiveley.samng.principal.sm.parseurs.BridageFormats;
import com.faiveley.samng.principal.sm.parseurs.Messages;
import com.faiveley.samng.principal.sm.parseurs.ParseurFlags;
import com.faiveley.samng.principal.sm.parseurs.ParseurParcoursBinaire;
import com.faiveley.samng.principal.sm.parseurs.adapteur.adapteur.ParseurAdapteur;
import com.faiveley.samng.principal.sm.parseurs.parseursJRU.GestionnaireLongueurMessage;
import com.faiveley.samng.principal.sm.parseurs.parseursJRU.GestionnaireLongueurPacket;
import com.faiveley.samng.principal.sm.parseurs.parseursJRU.ParseurParcoursJRU;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.principal.sm.segments.SegmentDistance;
import com.faiveley.samng.principal.sm.segments.SegmentTemps;
import com.faiveley.samng.principal.sm.segments.TableSegments;
import com.faiveley.samng.principal.sm.segments.ruptures.TableRuptures;
import com.faiveley.samng.principal.sm.segments.ruptures.TypeRupture;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:11:27
 */
public class ParseurParcoursSamng extends ParseurParcoursBinaire {
	private static int EVENT_MASK = 0x80;
	private static int VARIABLE_MASK = 0x7F;
	private Message[][] currentMessages = null;
	private IStrategieControle controlTable;
	private HashMap<Integer, SegmentTemps> exSegmentsTemps;
	private HashMap<Integer, SegmentDistance> exSegmentsDistance;
	private HashMap<Integer, TypeRupture> exMapRupturesDistance;
	private HashMap<Integer, TypeRupture> exMapRupturesTemps;
	private Map<Message, Integer> messagesCRC;
	private Map<Message, byte[]> messagesCRCData;
	private static List<Message> goodMsgs;
	private List<Message> badMsgs;
	private static ParseurParcoursJRU parseurParcoursJRU = null;
	private static int blockDefaultMessagesCount;

	private static int EVENT_AUDIO_FILE_NAME_CODE = 2601;
	
	private class JRUData {
		byte[] value;
		boolean lecturePaquetEnCours;
		int pos;
		long positionPaquetSuivantUsing_L_Packet;
		long positionVarEntetePaquet;
		int codeRawLen = -1;
		int codeRawPtr = -1;
		int codeNID;
	}

	/**
	 * Protected constructor. Just in the class is created the only instance of
	 * the parser
	 * 
	 */
	protected ParseurParcoursSamng() {
		messagesCRC = new HashMap<Message, Integer>();
		messagesCRCData = new HashMap<Message, byte[]>();
	}

	/**
	 * Returns the instance of the parser
	 * 
	 * @return ParseurParcoursSamng instance
	 */
	public static ParseurParcoursSamng getInstance() {
		try {
			if (instance == null)
				instance = new ParseurParcoursSamng();

			if (ActivationExplorer.getInstance().isActif()) {
				return ParseurParcoursSamngExplorer.getInstance();
			}

			return (ParseurParcoursSamng) instance;
		} catch (ClassCastException ex) {
			if (ActivationExplorer.getInstance().isActif()) {
				return new ParseurParcoursSamngExplorer();
			}
			return new ParseurParcoursSamng();
		}
	}

	/** Suppression de l'instance */
	public void clear() {
		super.clear();
		currentMessages = null;
		lastDateValue = -1;
		if (exSegmentsTemps != null) {
			exSegmentsTemps.clear();
		}
		if (exSegmentsDistance != null) {
			exSegmentsDistance.clear();
		}

		if (exMapRupturesDistance != null) {
			exMapRupturesDistance.clear();
		}
		if (exMapRupturesTemps != null) {
			exMapRupturesTemps.clear();
		}
		instance = null;
	}

	/**
	 * Load the header from the binary file
	 * 
	 * @return the Entete as a ParcoursComposant
	 * @throws AExceptionSamNG
	 */
	protected AParcoursComposant chargerEntete() throws AExceptionSamNG,
			ParseurXMLException {
		Entete entete = new Entete();

		formatCodage = this.message[0];
		entete.setFormatCodage(formatCodage);

		// set the bytes for tailleEntate message[1, 2]
		tailleEntete = (short) (this.message[1] << 8 | this.message[2] & 0xff);

		// CIU - Protection on header length is greater than the read bytes
		if (this.message.length < tailleEntete)
			throw new BadFileLengthException(
					Messages.getString("errors.blocking.invalidHeaderLength2")); //$NON-NLS-1$
		entete.setTailleEntete(tailleEntete);

		// the length of the XML file name
		tailleNomFichierXML = this.message[3];
		// CIU - Check if the name of the XML file name is too long ... It might
		// exceed the configured
		// length of the header. 20 is the sum of all configured bytes from
		// header
		if (tailleEntete < (20 + (tailleNomFichierXML & 0xFF)))
			throw new BadFileLengthException(
					Messages.getString("errors.blocking.invalidHeaderLength3")); //$NON-NLS-1$

		entete.setTailleNomFichierXML(tailleNomFichierXML);

		// set the file name that is written on x bytes starting to byte 4
		nomFichierXML = new String(Arrays.copyOfRange(this.message, 4,
				tailleNomFichierXML + 4)).trim();
		// test de présente du fichier xml dans le répertoire ressources/xml
		File f = new File(RepertoiresAdresses.xml + File.separator + nomFichierXML.trim());
		if (!f.exists())
			throw new ParseurXMLException(
					com.faiveley.samng.principal.sm.parseurs.parseursTomNg.Messages
							.getString("ParseurParcoursSamng.1") + " " + nomFichierXML, true); //$NON-NLS-1$

		entete.setNomFichierXML(nomFichierXML);

		return entete;
	}

	/**
	 * 
	 * Load the codes
	 * 
	 * CL_5 : L’octet 5+x+3 = > le code Debut de la table Data ; CL_6 : L’octet
	 * 5+x+4 = > le code Fin de la table Data CL_7 : L’octet 5+x+5 = > le code
	 * Continue Fin Bloc de la table Data. CL_8 : L’octet 5+x+6 = > le code
	 * Continue Debut Bloc de la table Data. CL_D_2 : L’octet 5+x+7 = > le code
	 * Début Bloc Defaut de la table Data. CL_D_3 : L’octet 5+x+8 = > le code
	 * Fin Bloc Defaut de la table Data. CL_D_4 : L’octet 5+x+9 = > le code
	 * Continue Fin Bloc Defaut de la table Data. CL_D_5 : L’octet 5+x+10 = > le
	 * code Continue Debut Bloc Defaut de la table Data.
	 * 
	 * 
	 * @param codeName
	 *            the code name
	 * @param from
	 *            starting position in the byte[]
	 * @return the descriptor created
	 */
	private ADescripteurComposant chargerDescripteursCodeBloc(int from)
			throws BadHeaderInfoException {
		DescripteurCodeBloc descr = new DescripteurCodeBloc();

		// set code as 1 byte get from the byte[]
		codeDebut = this.message[from] & 0xff;
		descr.setCodeDebut(Integer.toString(codeDebut));

		codeFin = this.message[from + 1] & 0xff;
		descr.setCodeFin(Integer.toString(codeFin));

		codeContinueFinBloc = this.message[from + 2] & 0xff;
		descr.setCodeContinuFin(Integer.toString(codeContinueFinBloc));

		codeContinueDebutBloc = this.message[from + 3] & 0xff;
		descr.setCodeContinuDebut(Integer.toString(codeContinueDebutBloc));

		codeDebutDefaut = this.message[from + 4] & 0xff;
		if (codeDebut == codeDebutDefaut) {
			throw new BadHeaderInfoException(
					Messages.getString("errors.blocking.conflictCodeDebuts")); //$NON-NLS-1$
		}
		descr.setCodeDebutDefaut(Integer.toString(codeDebutDefaut));

		codeFinDefaut = this.message[from + 5] & 0xff;
		if (codeFin == codeFinDefaut) {
			throw new BadHeaderInfoException(
					Messages.getString("errors.blocking.conflictCodeFins")); //$NON-NLS-1$
		}
		descr.setCodeFinDefaut(Integer.toString(codeFinDefaut));

		codeContinueFinDefaut = this.message[from + 6] & 0xff;
		if (codeContinueFinBloc == codeContinueFinDefaut) {
			throw new BadHeaderInfoException(
					Messages.getString("errors.blocking.conflictCodeContinueFins")); //$NON-NLS-1$
		}
		descr.setCodeContinuFinDefaut(Integer.toString(codeContinueFinDefaut));

		codeContinueDebutDefaut = this.message[from + 7] & 0xff;
		if (codeContinueDebutBloc == codeContinueDebutDefaut) {
			throw new BadHeaderInfoException(
					Messages.getString("errors.blocking.conflictCodeContinueDebuts")); //$NON-NLS-1$
		}
		descr.setCodeContinuFinDefaut(Integer.toString(codeContinueDebutDefaut));

		return descr;
	}

	/**
	 * Load the descriptor for the header
	 * 
	 * @return the descriptor
	 */
	private ADescripteurComposant chargerDescripteursEntete()
			throws AExceptionSamNG {
		DescripteurCodeBloc descr = new DescripteurCodeBloc();

		// the first position of a descriptor
		int start = 4 + tailleNomFichierXML;

		// set tailleBlocData in Entete
		descr.ajouter(chargerDescripteursTableAssociation(start));

		// set the codes
		descr.ajouter(chargerDescripteursCodeBloc(start + 2));

		return descr;
	}

	/**
	 * Loads the descriptor pf the table association
	 * 
	 * @param start
	 *            the start position for the association table between events
	 *            and variables
	 * @return the descriptor
	 */
	private ADescripteurComposant chargerDescripteursTableAssociation(int start)
			throws AExceptionSamNG {
		DescripteurTableAssociation descr = new DescripteurTableAssociation();
		AIdentificateurComposant identifier = new IdentificateurVariable();

		if (this.message.length < start + 12) // including CRC
			throw new BadFileLengthException(
					Messages.getString("errors.blocking.invalidHeaderLength2")); //$NON-NLS-1$
		// the tailleBloc is written on 2 bytes starting with position given by
		// paramter <code>from</code>
		tailleBlocData = (short) (this.message[start] << 8 | (this.message[start + 1] & 0xff));
		descr.setTailleBloc(tailleBlocData);

		// set the table event

		// start from the 'start' position + 6 (the bytes used for
		// tailleBlocData, codeStart, ..., codeContinueDebutBloc)
		start += 10;

		int code = this.message[start] << 3 * 8
				| (this.message[start + 1] << 2 * 8 & 0xff0000)
				| (this.message[start + 2] << 1 * 8 & 0x00ff00)
				| (this.message[start + 3] & 0xff);

		// set the code for the identifier
		tailleTableEvenement = code;
		if (tailleTableEvenement <= 0)
			throw new BadFileLengthException(
					Messages.getString("errors.blocking.invalidEvTableLen1")); //$NON-NLS-1$

		if ((tailleTableEvenement % 2) != 0)
			throw new BadTableEventStructureException(
					Messages.getString("errors.blocking.invalidEvTableLen2")); //$NON-NLS-1$

		identifier.setCode(tailleTableEvenement);
		// set the name for the identifier
		identifier.setNom("tailleTableEvenement"); //$NON-NLS-1$

		// set the identifier
		descr.setM_AIdentificateurComposant(identifier);
		descr.setTailleBits(32);

		return descr;
	}

	/**
	 * charge le CRC enregistré de l'entete dans un objet ADonneeBlocComposant
	 */
	private ADonneeBlocComposant chargerCRCEntete() {
		ADonneeBlocComposant crc = new DonneeBloc();

		tailleEntete = (short) ((this.message[1] << 8) | (this.message[2] & 0xff));
		// get CRC written on the 2 last bytes starting from this.lengthEntete -
		// 2
		int value = this.message[tailleEntete - 2] << 8
				| (this.message[tailleEntete - 1] & 0xff);

		crc.setValeur(Integer.valueOf(value));

		return crc;
	}

	/**
	 * charge le CRC enregistré de la table d'assocation dans un objet
	 * ADonneeBlocComposant
	 */
	private ADonneeBlocComposant chargerCRCTableEvtVar() throws AExceptionSamNG {
		if (this.message.length < tailleEntete + tailleTableEvenement)
			throw new BadFileLengthException(
					Messages.getString("errors.blocking.invalidEvTableLength")); //$NON-NLS-1$
		ADonneeBlocComposant crc = new DonneeBloc();
		// get CRC written on 4 bytes starting from table length - 2
		// in the byte[] the bytes for CRC are at the position (lengthEntete +
		// lengthTable - lengthCRC)
		int value = this.message[tailleEntete + tailleTableEvenement - 4] << 3 * 8
				| (this.message[tailleEntete + tailleTableEvenement - 3] << 2 * 8 & 0x00ff0000)
				| (this.message[tailleEntete + tailleTableEvenement - 2] << 1 * 8 & 0xff00)
				| (this.message[tailleEntete + tailleTableEvenement - 1] & 0xff);

		crc.setValeur(Integer.valueOf(value));

		return crc;
	}

	/**
	 * charge l'ensemble des messages et les place dans un objet Data
	 * 
	 * Returns 2 arrays with messages: first with good messages, second with bad
	 * massages
	 */
	@Override
	protected Message[][] chargerData(int deb, int fin)
			throws ParseurBinaireException {
		ParseurParcoursSamng.goodMsgs = new ArrayList<Message>();
		this.badMsgs = new ArrayList<Message>();

		TableEvtVar table = (TableEvtVar) FabriqueParcours.getInstance()
				.getParcours().getTableAssos();
		this.descrTable = (DescripteurComposite) table.getDescripteur(0);
		this.controlTable = new CRC16CCITT();
		this.crc = new CRC16CCITTHash();
		int startPos = tailleEntete + tailleTableEvenement;
		if (deb != 0) {
			startPos = deb;
		}

		int tailleTableauMessage = this.message.length;

		if (fin != -1 && fin != 0) {
			tailleTableauMessage = fin;
		}

		// CIU - we have no blocks containing messages
		if (startPos >= tailleTableauMessage) {
			SamngLogger
					.getLogger()
					.error(Messages
							.getString("errors.blocking.invalidFileStructure1"));
			ActivatorData.getInstance().getPoolDonneesVues()
					.put("fichierVide", new String("true"));
			throw new BadFileLengthException(
					Messages.getString("errors.blocking.invalidFileStructure1")); //$NON-NLS-1$
		}

		Message msg = null;
		boolean isValidBloc = true;
		int blocksNo = 0;
		int codeDebutMsg = 0;
		int calcCodeFin = 0;
		int calcCodeDebut = 0;
		int length = 0;
		int noFF = 0;
		ErrorType error = null;
		boolean isValidMsg = false;
		int posPlusTailleBlocData = 0;
		int pos = startPos;
		String messageCRC = "";
		String calculCRC = "";

		blockDefaultMessagesCount = 0;

		while (startPos < tailleTableauMessage && !Thread.interrupted()) {
			
	
			ActivatorData
					.getInstance()
					.getVp()
					.setValeurProgressBar(
							startPos * 100 / (tailleTableauMessage));
			ActivatorData.getInstance().getVpExportExplorer()
					.setValeurProgressBar(startPos, tailleTableauMessage);

			try {
				codeDebutMsg = this.message[startPos] & 0xff;
				// checks if the block starts with a codeDebut
				if ((codeDebutMsg == codeDebut)
						|| (codeDebutMsg == codeDebutDefaut)) {
					blocksNo = 0;
					pos = startPos;
					// validate the block(s) that contain(s) the current message
					do {
						posPlusTailleBlocData = pos + tailleBlocData;
						// found one blocvous
						blocksNo++;
						isValidBloc = true;
						if (posPlusTailleBlocData > tailleTableauMessage) {
							// it's the end of the data table
							isValidBloc = false;
							break;
						}
						calcCodeFin = this.message[posPlusTailleBlocData - 1] & 0xff;

						// start and end for bloc are correct
						if ((calcCodeFin) == codeFin
								|| (calcCodeFin) == codeFinDefaut) {
							break;
						} else if ((calcCodeFin) != codeContinueFinBloc
								&& (calcCodeFin) != codeContinueFinDefaut) {
							isValidBloc = false;
							throw new BadBlocStructureException(
									Messages.getString("errors.nonblocking.invalidEndBlockCode"));
						}

						if (((calcCodeFin)) == codeContinueFinBloc
								|| (calcCodeFin) == codeContinueFinDefaut) {
							calcCodeDebut = this.message[posPlusTailleBlocData] & 0xff;
							if (((calcCodeDebut)) != codeContinueDebutBloc
									&& (calcCodeDebut) != codeContinueDebutDefaut) {
								// the block is finished with a "CF" but the
								// next block does not start with a "DF"
								isValidBloc = false;
								throw new BadBlocStructureException(
										Messages.getString("errors.nonblocking.invalidStartBlockCode1"));
							}
						} else if (posPlusTailleBlocData >= tailleTableauMessage) {
							// if the end of the bloc is not "F" or "CF" it is
							// an invalid bloc
							isValidBloc = false;
							throw new BadBlocStructureException(
									Messages.getString("errors.nonblocking.invalidEndBlockCode"));
						}
						pos += tailleBlocData;
					} while (true);
					// if the blocks structure is ok load the message
					msg = new Message();
					if (isValidBloc) {

						isValidMsg = true;
						try {
							msg = chargerMessage(startPos, false);
							if (msg.getError() != null) {
								isValidMsg = false;
								msg.setMessageId(startPos);
								msg.setMessageData(Arrays.copyOfRange(
										this.message, startPos, startPos
												+ tailleBlocData * blocksNo));
								badMsgs.add(msg);
								goodMsgs.add(msg);
							}
							if (isValidMsg) {
								// length contains the length of the variables +
								// length of code event
								length = msg.getLongueur();

								// if valid message then add it to the list of
								// received messages
								Integer crc = messagesCRC.get(msg);
								byte[] crcData = messagesCRCData.get(msg);
								if (!checkMessageCRC(crc, crcData,
										this.controlTable)) {
									ReturnCRC ret = getMessageCRCs(crc,
											crcData, this.crc);
									messageCRC = "";
									calculCRC = "";

									try {
										calculCRC = ret.getCRCString(ret
												.getCalculCRC());
										messageCRC = ret.getCRCString(ret
												.getMessageCRC());
									} catch (Exception e) {
										e.printStackTrace();
									}
									
									msg.setError(ErrorType.CRC);
									// SamngLogger.getLogger().warn(Messages
									// .getString("errors.nonblocking.badMsgCrc")
									// + "; " +
									// Messages.getString("errors.nonblocking.blockStart1")
									// + " " + (((msg.getMessageId() -
									// (tailleEntete + tailleTableEvenement)) /
									// tailleBlocData) + 1) + "; " +
									// "CRC enregistré:" + " " + msg.getCRC());
									throw new BadCRCException(
											Messages.getString("errors.nonblocking.badMsgCrc")
													+ "; "
													+ Messages
															.getString("errors.nonblocking.blockStart1")
													+ " "
													+ (goodMsgs.size() + 1 - blockDefaultMessagesCount));
								}

								// start + message length + nr of octets for CRC
								// + blocksNo * (nr of octets for CD or CCD + nr
								// of
								// octets for CF or CCF)
								noFF = (startPos + tailleBlocData * blocksNo)
										- (startPos + length + 2 + blocksNo * 2);
								if (!checkOctetsFF(startPos + length + blocksNo
										* 2 - 1 + 2, noFF)) {
									error = ErrorType.FFBlockDefault;
									error.setStartPos(startPos + length
											+ blocksNo * 2 - 1 + 2);
									error.setLength(noFF);
									msg.setError(error);
								}
							}
						} catch (Exception e) {
							// valid block, but not valid message
							isValidMsg = false;
							msg.setMessageId(startPos);
							msg.setMessageData(Arrays.copyOfRange(this.message,
									startPos, startPos + tailleBlocData
											* blocksNo));
							badMsgs.add(msg);
							if (msg.getEvenement() != null) {
								goodMsgs.add(msg);
							}
						}

						if (isValidMsg) {
							if (this.message[startPos] == codeDebutDefaut) {
								// default block = > save it in the list of bad
								// messages
								msg.setError(ErrorType.BlockDefaut);
								msg.setMessageData(Arrays.copyOfRange(
										this.message, startPos, startPos
												+ tailleBlocData * blocksNo));
								blockDefaultMessagesCount++;
								badMsgs.add(msg);
								goodMsgs.add(msg);
							} else {

								// If everything is ok and no exception
								msg.setMessageData(Arrays.copyOfRange(
										this.message, startPos, startPos
												+ tailleBlocData * blocksNo));
								goodMsgs.add(msg);
								try {
									msg.setOffsetDebut(startPos);
									msg.setOffsetFin(startPos + tailleBlocData);
									setFlags(msg);
								} catch (Exception e) {
									SamngLogger.getLogger().error(this, e);
								}
								this.lastMessage = msg;
								
								if (msg.getEvenement().getM_ADescripteurComposant().getM_AIdentificateurComposant().getCode() == EVENT_AUDIO_FILE_NAME_CODE) {
									addMultimediaFile(msg, goodMsgs.size() - 1);
								}
							}
						}
					} else {
						// is not a valid block
						// => just save the data in a bad message
						msg.setMessageId(startPos);
						msg.setMessageData(Arrays.copyOfRange(this.message,
								startPos, startPos + tailleBlocData * blocksNo));
						msg.setError(ErrorType.BadBlock);
						badMsgs.add(msg);
					}
					msg.setOffsetDebut(startPos);
					msg.setOffsetFin(startPos + tailleBlocData);
					// set the start position for the next message
					startPos += tailleBlocData * blocksNo;

				} else {
					// not valid block => save the data in a bad message
					msg = new Message();
					msg.setMessageId(startPos);
					msg.setMessageData(Arrays.copyOfRange(this.message,
							startPos, startPos + tailleBlocData));
					msg.setError(ErrorType.BadBlock);
					msg.setOffsetDebut(startPos);
					msg.setOffsetFin(startPos + tailleBlocData);
					badMsgs.add(msg);
					goodMsgs.add(msg);

					// throw an error. it doesn't start with "0D"
					throw new BadBlocStructureException(
							Messages.getString("errors.nonblocking.invalidStartBlockCode")
									+ "; "
									+ Messages
											.getString("errors.nonblocking.blockStart1")
									+ " "
									+ (goodMsgs.size() - blockDefaultMessagesCount));//(((msg.getMessageId() - (tailleEntete + tailleTableEvenement)) / tailleBlocData) + 1));
				}
			} catch (ParseurBinaireException e) {
				if (badMsgs.size() > 0
						&& startPos != badMsgs.get(badMsgs.size() - 1)
								.getMessageId()) {
					msg = new Message();
					msg.setMessageId(startPos);
					msg.setMessageData(Arrays.copyOfRange(this.message,
							startPos, startPos + tailleBlocData));
					msg.setError(ErrorType.BadBlock);
					badMsgs.add(msg);
				}

				// it was an exception on the current bloc. move to the next one
				// CIU - changed this as if we have an invalid block start code
				// blocksNo will be 0 and startPos will never change
				// leading to an infinite loop
				startPos += (blocksNo > 0) ? tailleBlocData * blocksNo
						: tailleBlocData;
				// e.printStackTrace();
			}

		}

		// if (VueProgressBar.getInstance().isEscaped) {
		// return null;
		// }
		// creates the array with bad and good messages
		Message[][] msgs = new Message[2][];
		msgs[0] = goodMsgs.toArray(new Message[goodMsgs.size()]);
		msgs[1] = badMsgs.toArray(new Message[badMsgs.size()]);

		if (this.lastSegTemp != null)
			enregistrerSegmentTemps(this.lastMessage);

		messagesCRC.clear();
		messagesCRCData.clear();
		
		return msgs;
	}

	@Override
	protected void chargerDataExplore() throws ParseurBinaireException {
		
		/*boolean onTest = false;
		
		if (onTest)
		{			
			// XML used : ZA577145C_FLIRT_France_Plus.xml
			
			tailleEntete = 0;
			tailleTableEvenement = 0;
			
			// Normal case on 1 line => setOffsetDebut(0); / msg.setOffsetFin(15); / chargerMessage(0);
			//byte testValues[] = new byte[] {(byte)codeDebut, 0, 9, 1, 11, 41, (byte)146, (byte)167, 19, (byte)201, 104, (byte)255, (byte)155, 79, (byte)236, (byte)codeFin};
			
			// With some bad bytes before good event => setOffsetDebut(0); / msg.setOffsetFin(18); / chargerMessage(3); 
			//byte testValues[] = new byte[] {11, 11, 11, (byte)codeDebut, 0, 9, 1, 11, 41, (byte)146, (byte)167, 19, (byte)201, 104, (byte)255, (byte)155, 79, (byte)236, (byte)codeFin};
			
			// With some bad bytes after good event => setOffsetDebut(0); / msg.setOffsetFin(18); / chargerMessage(0);
			//byte testValues[] = new byte[] {(byte)codeDebut, 0, 9, 1, 11, 41, (byte)146, (byte)167, 19, (byte)201, 104, (byte)255, (byte)155, 79, (byte)236, (byte)codeFin, 11, 11, 11};
			
			// With some bad bytes before and after good event => setOffsetDebut(0); / msg.setOffsetFin(20); / chargerMessage(2);
			//byte testValues[] = new byte[] {12, 12, (byte)codeDebut, 0, 9, 1, 11, 41, (byte)146, (byte)167, 19, (byte)201, 104, (byte)255, (byte)155, 79, (byte)236, (byte)codeFin, 11, 11, 11};
			
			// Good payload on 2 lines => setOffsetDebut(0); / msg.setOffsetFin(31); / chargerMessage(0);
			byte testValues[] = new byte[] {(byte) codeDebut, 0, (byte) 150, 0, (byte) 254, 41, (byte) 146, (byte) 177, 19, (byte) 201, (byte) 245, 0, 6, 14, 47, (byte) codeContinueFinBloc, (byte) codeContinueDebutBloc, 4, 60, 0, 25, 76, 121, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) codeFin};
			
			// Good payload on 3 lines => setOffsetDebut(0); / msg.setOffsetFin(47); / chargerMessage(0);
			//byte testValues[] = new byte[] {(byte) codeDebut, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, (byte) codeContinueFinBloc, (byte) codeContinueDebutBloc, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, (byte) codeContinueFinBloc, (byte) codeContinueDebutBloc, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, (byte) codeFin};
			
			// No payload => exception : print message to user: no data!
			//byte testValues[] = new byte[] {};

			// No valid payload => exception : print message to user: no data!
			//byte testValues[] = new byte[] {10, 10, 10};
			
			// Bad payload on 3 lines but one byte missing => any segment
			//byte testValues[] = new byte[] {(byte) codeDebut, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, (byte) codeContinueFinBloc, (byte) codeContinueDebutBloc, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, (byte) codeContinueFinBloc, (byte) codeContinueDebutBloc, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, (byte) codeFin};
			
			//1 good event, several wrong bytes, then 1 good event => 1 evt: setOffsetDebut(0); / msg.setOffsetFin(15); / chargerMessage(0); 2 evt: setOffsetDebut(16); / msg.setOffsetFin(49); / chargerMessage(34) 
			//byte testValues[] = new byte[] {(byte) codeDebut, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, (byte) codeFin, 10, 10, (byte) codeContinueDebutBloc, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, (byte) codeContinueFinBloc, (byte) codeDebut, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, (byte) codeFin};			
			
			this.message = testValues; 
		}*/
		
		TableEvtVar table = (TableEvtVar) FabriqueParcours.getInstance().getParcours().getTableAssos();		
		this.descrTable = (DescripteurComposite) table.getDescripteur(0);
		
		ParseurParcoursSamng.goodMsgs = new ArrayList<Message>(); // just to avoid pointer null exception, because goodMsgs in exploration context it'snot used 
		
		this.controlTable = new CRC16CCITT();
		this.crc = new CRC16CCITTHash();
		
		int currentPos = tailleEntete + tailleTableEvenement;
		int realSegmentStart = currentPos;
		int evtStart = currentPos;
		int payloadSize = this.message.length;
				
		int nbEvt = 0;

		Message msg = null;

		int codeDebutMsg = 0;
		int codeFinMsg = 0;
		
		// CIU - we have no blocks containing messages
		if (currentPos >= payloadSize) {			
			//SamngLogger.getLogger().error(Messages.getString("errors.blocking.invalidFileStructure1"));
			
			Display.getDefault().syncExec(new Runnable(){
				public void run() {
					MessageDialog.openError(Activator.getDefault().getWorkbench().getDisplay().getActiveShell(), com.faiveley.samng.principal.ihm.actions.fichier.Messages.getString("ErreursTitre"), 
							Messages.getString("errors.blocking.invalidFileStructure1"));
				}
			});
			
			throw new BadFileLengthException(Messages.getString("errors.blocking.invalidFileStructure1")); //$NON-NLS-1$
		}

		// As long as there's payload
		// and the user doesn't cancel
		while ((currentPos + tailleBlocData - 1) <= payloadSize && !monitor.isCanceled()) 
		{						
			// Update the progress bar
			ActivatorData.getInstance().getVpExportExplorer().setValeurProgressBar(currentPos, payloadSize);

			codeDebutMsg = this.message[currentPos] & 0xff;			 			
			
			// checks if the block starts with a codeDebut				
			if (codeDebutMsg == codeDebut) 				
			{
				evtStart = currentPos;
				codeFinMsg = this.message[currentPos + tailleBlocData - 1] & 0xff;
				
				if ((codeFinMsg == codeFin) || (codeFinMsg == codeContinueFinBloc))
				{
					// If the event is recorded on several lines and there's enough payload to deal it
					while ((codeFinMsg == codeContinueFinBloc) && (((currentPos + (tailleBlocData * 2)) - 1) <= payloadSize))
					{
						currentPos += tailleBlocData;
						codeDebutMsg = this.message[currentPos] & 0xff;			 
						codeFinMsg = this.message[currentPos + tailleBlocData - 1] & 0xff;
							
						if ((codeDebutMsg == codeContinueDebutBloc) && ((codeFinMsg == codeFin) || (codeFinMsg == codeContinueFinBloc)))
						{
							continue;	
						}
						else
						{
							currentPos++;
							break;
						}
					}
					
					if (((codeDebutMsg == codeContinueDebutBloc) || (codeDebutMsg == codeDebut)) && (codeFinMsg == codeFin))
					{
						currentPos += tailleBlocData;						
						msg = chargerMessage(evtStart, true);
						
						msg.setOffsetDebut(realSegmentStart);
						
						// if remains some data but not enough to be event, pack with current segment
						// The open process will warn the user for that
						if ((payloadSize - currentPos) < tailleBlocData)
							currentPos = payloadSize;
							
						msg.setOffsetFin(currentPos);
						
						if (msg.getError() != null)
							continue;
						
						nbEvt++;
						
						setSegmentsTemp(msg, this.lastMessage);						
						
						realSegmentStart = currentPos;
						this.lastMessage = msg;	
					}
				}
				else
				{
					currentPos++;
				}
			}
			else
			{
				currentPos++;
			}
		}
		
		if (this.lastSegTemp != null)
		{
			this.lastMessage.setOffsetFin(payloadSize);						
			
			enregistrerSegmentTemps(this.lastMessage);
		}
		
		if (nbEvt == 0)
		{
			Display.getDefault().syncExec(new Runnable(){
				public void run() {
					MessageDialog.openError(Activator.getDefault().getWorkbench().getDisplay().getActiveShell(), com.faiveley.samng.principal.ihm.actions.fichier.Messages.getString("ErreursTitre"), 
							Messages.getString("errors.blocking.invalidFileStructure1"));
				}
			});
			
			throw new BadFileLengthException(Messages.getString("errors.blocking.invalidFileStructure1")); //$NON-NLS-1$
		}
	}

	/**
	 * Checks the CRC of the message
	 * 
	 * @param crc
	 *            the value of CRC
	 * @param message
	 *            the bytes for which the CRC is calculated
	 * @return true if CRC is ok, false otherwise
	 */
	private boolean checkMessageCRC(int crc, byte[] crtMessage,
			IStrategieControle controlTable) {
		// IStrategieControle controlTable = new CRC16CCITT();
		boolean valid = true;

		if (!controlTable.controlerCRC(crc, crtMessage)) {
			valid = false;
		}
		return valid;
	}

	/**
	 * Loads a message
	 * 
	 * @param msg
	 *            an instance of the message
	 * @param start
	 *            the start position for the message
	 * @return the message filled
	 * @throws ParseurBinaireException
	 *             throws an exception if some error occurs
	 */
	@Override
	protected Message chargerMessage(int start, boolean isExploration) throws ParseurBinaireException {
		GestionnaireLongueurMessage.getInstance().setLongueurCalculeeMessageInterneCourant(0);
		GestionnaireLongueurMessage.getInstance().setLongueurEnregistreeMessageInterneCourant(0);
		GestionnaireLongueurMessage.getInstance().setLongueurEnregistreeMessageCourant(0);
		GestionnaireLongueurMessage.getInstance().setLongueurCalculeeMessageCourant(0);
		
		Message msg = new Message();
		// set the message ID as the start address of the message block
		msg.setMessageId(start);

		// get event id
		int eventId = (this.message[start + 1] << 8 | (this.message[start + 2] & 0xff)) & 0xffff;

		DescripteurComposite descrComp = getDescripteurComposite(eventId);
		// the code read in the message is not found in the table
		if (descrComp == null) {
			msg.setError(ErrorType.EventId);
			msg.setErrorInfo(String.valueOf(eventId));
			
			SamngLogger
					.getLogger()
					.warn(Messages
							.getString("errors.nonblocking.invalidEventId") //$NON-NLS-1$
							+ "; " + Messages //$NON-NLS-1$
									.getString("errors.nonblocking.eventId1") + eventId + "; " + //$NON-NLS-1$ //$NON-NLS-2$
							Messages.getString("errors.nonblocking.blockStart1") + " " + (goodMsgs.size() + 1 - blockDefaultMessagesCount));//(((msg.getMessageId() - (tailleEntete + tailleTableEvenement)) / tailleBlocData) + 1)); //$NON-NLS-1$ //$NON-NLS-2$
			return msg;
		}

		chargerMessageEvt(msg, eventId);

		int length = descrTable.getLength();
		int[] codes = getVariablesCodes(descrComp, msg);
		// Check if the message contains dynamic data
		
		JRUData jruData = new JRUData();
		boolean msgUnified = isMsgUnified(codes, jruData);
		
		// check the message if its length is the expected one and throws an
		// execption if not unless the message contains dynamic data
		if (!msgUnified) {
			try {
				length = checkMessageLength(codes, start, msg);
			} catch (BadStructureFileException e) {
				if (msg.getError() == null) {
					msg.setError(ErrorType.BadLength);
				}
				return msg;
			}
		}

		// for this event take all the descriptor variables
		// descrComp contains an event descriptor and the descriptor varaibles
		// associated
		if (descrComp.getLength() <= 1) {
			msg.setError(ErrorType.BadLength);
			throw new BADEventLenghtException(
					Messages.getString("errors.nonblocking.invalidMessageLength") + "; " //$NON-NLS-1$ //$NON-NLS-2$
							+ Messages
									.getString("errors.nonblocking.blockStart1") + " " //$NON-NLS-1$ //$NON-NLS-2$
							+ (((msg.getMessageId() - (tailleEntete + tailleTableEvenement)) / tailleBlocData) + 1 - blockDefaultMessagesCount)); //$NON-NLS-1$); //$NON-NLS-1$
		}

		List<Byte> crcValues = new ArrayList<Byte>();
		crcValues.add(this.message[start + 1]);
		crcValues.add(this.message[start + 2]);

		int pos = 1 + 2; // 1 -> Code de début de block ; 2 -> ID event (sur 2 octets)
		int variablesLength = 0;
		double newDateValue = 0;
		double newTempValue = 0;
		double dateAvantChgt = 0;
		double tempsAvantChgt = 0;
		boolean newDateEncountered = false;
		boolean newTempEncountered = false;
		boolean newChgtEncountered = false;
		for (int code : codes) {
			checkMessage(start, msg, pos);

			AVariableComposant var = getVariable(msg, eventId, code);

			// set the value of the variable
			// if the length of the variables + 2 bytes of codes + 2 bytes for
			// event code + 2 bytes CRC
			int numBlockTmp = pos / tailleBlocData;
			if (pos % tailleBlocData > 0) {
				numBlockTmp++;
			}

			byte[] value = null;
			int tailleOctetsVariable = var.getDescriptor().getTailleOctets();
			if (code == jruData.codeRawPtr) {
				AVariableComposant v = msg.getVariable(jruData.codeRawLen);
				if (v == null) {
					throw new NoninterpretableMessageException(
							Messages.getString("errors.nonblocking.invalidjruMsg") + "; " //$NON-NLS-1$ //$NON-NLS-2$
									+ Messages
											.getString("errors.nonblocking.blockStart1") + " " //$NON-NLS-1$ //$NON-NLS-2$
									+ (((msg.getMessageId() - (tailleEntete + tailleTableEvenement)) / tailleBlocData) + 1 - blockDefaultMessagesCount));
				}
				tailleOctetsVariable = ((Long) v.getCastedValeur()).intValue();
				GestionnaireLongueurMessage.getInstance().setLongueurEnregistreeMessageCourant(tailleOctetsVariable * 8);
			}
			if (pos + tailleOctetsVariable >= (numBlockTmp * tailleBlocData)) {
				if (tailleOctetsVariable == 1) {
					pos += tailleOctetsVariable + 2;
					value = Arrays.copyOfRange(this.message, pos + start,
							tailleOctetsVariable + pos + start);
				} else {
					// load a variable written on multiple blocks
					pos = chargeVariableSurBlocksMultiple(var, pos, start,
							tailleOctetsVariable);
					value = (byte[]) var.getValeur();
				}
			} else {
				// extract the value for the variable
				if (tailleOctetsVariable != DescripteurVariable.NO_TAILLE_OCTETS) {
					value = Arrays.copyOfRange(this.message, pos + start,
							tailleOctetsVariable + pos + start);
					pos += tailleOctetsVariable;
				} else {
					value = Arrays.copyOfRange(this.message, pos + start, var
							.getDescriptor().getTailleBits() + pos + start);
					pos += var.getDescriptor().getTailleBits();
				}
			}
			// set the bytes that are acctually the data (event + variables).
			// This are nedded for checking the CRC
			for (byte val : value) {
				crcValues.add(val);
			}

			jruData.pos = 0;
			jruData.value = value;
			TypeVariable typeVar = var.getDescriptor().getTypeVariable();
			if (typeVar == TypeVariable.STRUCTURE_DYNAMIQUE) {
				// récupération de la définition de la structure dynamique
				StructureDynamique defStructureDyn = (StructureDynamique) var;
				StructureDynamique structureDynamique2 = gererStructureDynamique(
						defStructureDyn, 0, msg, jruData);

				// affectation de la structure remplie à la sous-variable
				var = structureDynamique2;
			} else if (typeVar == TypeVariable.TABLEAU_DYNAMIQUE) {
				// récupération de la définition du tableau
				// dynamique
				TableauDynamique defTableauDyn = null;
				if (var != null)
					defTableauDyn = (TableauDynamique) var;
				TableauDynamique tableauDynamique = gererTableauDynamique(
						defTableauDyn, 0, msg, jruData);
				// affectation de la structure remplie à la sous-variable
				var = tableauDynamique;
			} else if (typeVar == TypeVariable.CHAINE_DYNAMIQUE) {
				ChaineDynamique defChaineDyn = (ChaineDynamique) var;
				// remplissage de la chaine dynamique
				ChaineDynamique chaineDynamique = gererChaineDynamique(
						defChaineDyn, 0, msg, jruData);
				// affectation de la structure remplie à la sous-variable
				var = chaineDynamique;
			} else {
				// set the value for the variable. The variable can be a
				// composed variable
				if (var instanceof VariableComplexe) {
					checkVariableComplexe(msg, var);
				}
				value = setVariableValue(var, value, msg);
			}

			// add variable to the current message
			msg.ajouterVariable(var);

			// Check for defined reperes
			if (code == TypeRepere.temps.getCode()) {
				newTempValue = Double.parseDouble(var.toString());
				newTempEncountered = true;
			} else if (code == TypeRepere.date.getCode()) {
				newDateValue = Double.parseDouble(var.toString());
				newDateEncountered = true;
			} else if (code == TypeRepere.distance.getCode()) {
				double distance = Double.parseDouble(var.toString());
				msg.setAbsoluteDistance(distance);
			} else if (code == TypeRepere.diametreRoue.getCode()) {
				double diametreRoue = Double.parseDouble(var.toString());
				msg.setDiametreRoue(diametreRoue);
			} else if (code == TypeRepere.dateAvantChangement.getCode()) {
				dateAvantChgt = Double.parseDouble(var.toString());
				newChgtEncountered = true;
			} else if (code == TypeRepere.tempsAvantChangement.getCode()) {
				tempsAvantChgt = Double.parseDouble(var.toString());
			}

			// increment the current length of the variables data with the
			// current variable length
			variablesLength += tailleOctetsVariable;

			// if the current position is at the end of bloc - 2 (nr of octets
			// used by "CF")
			// then for the next variable we have to skeep "CF" and "DF" codes
			// if ((this.message[pos + start-tailleEntete-tailleTableEvenement]
			// << 8 | (this.message[pos +
			// start-tailleEntete-tailleTableEvenement + 1] & 0xff) ) ==
			// codeContinueFinBloc) {
			int blocks = pos / tailleBlocData;
			if (pos % tailleBlocData > 0) {
				blocks++;
			}
			if ((this.message[start + tailleBlocData - 1] & 0xFF) == codeFin
					|| (this.message[start + tailleBlocData - 1] & 0xFF) == codeFinDefaut) {

				// last position in this message (crt pos + CRC + CF + 1 -> must
				// be new start block)
				if (pos + 2 + 1 > tailleBlocData) {
					msg.setError(ErrorType.BadBlock);
					throw new BadBlocStructureException(
							Messages.getString("errors.nonblocking.invalidMessageLength") //$NON-NLS-1$
									+ "; " + Messages //$NON-NLS-1$
											.getString("errors.nonblocking.eventId1") //$NON-NLS-1$
									+ eventId
									+ "; " //$NON-NLS-1$
									+ Messages
											.getString("errors.nonblocking.blockStart1") + " " //$NON-NLS-1$ //$NON-NLS-2$
									+ (((msg.getMessageId() - (tailleEntete + tailleTableEvenement)) / tailleBlocData) + 1 - blockDefaultMessagesCount));
				}
				if ((this.message.length < start + tailleBlocData)
						&& (this.message[start + tailleBlocData] & 0xFF) != codeDebut
						&& (this.message[start + tailleBlocData] & 0xFF) != codeDebutDefaut) {
					msg.setError(ErrorType.BadBlock);
					throw new BadBlocStructureException(
							Messages.getString("errors.nonblocking.invalidStartBlockCode1")); //$NON-NLS-1$
				}
			} else if ((this.message[start + blocks * tailleBlocData - 1] & 0xFF) == codeContinueFinBloc
					|| (this.message[start + blocks * tailleBlocData - 1] & 0xFF) == codeContinueFinDefaut) {
				// last position in this message (crt pos +"CCF" +"CCD")
				if ((pos + 1) % tailleBlocData == 0) {
					// it is the position before de "CCF" then add "CCF" and
					// "CCD"
					pos += 2;
				}
				// next block does not start with CCD or CCDD
				if ((this.message[start + blocks * tailleBlocData] & 0xFF) != codeContinueDebutBloc
						&& (this.message[start + blocks * tailleBlocData] & 0xFF) != codeContinueDebutDefaut) {
					msg.setError(ErrorType.BadBlock);
					throw new BadBlocStructureException(
							Messages.getString("errors.nonblocking.invalidStartBlockCode1")); //$NON-NLS-1$
				}

			} else if ((this.message[start + blocks * tailleBlocData - 1] & 0xFF) == codeFin
					|| (this.message[start + blocks * tailleBlocData - 1] & 0xFF) == codeFinDefaut) {
				// block doesn't end with CF or CFD
				if (pos + 2 + 1 > tailleBlocData * blocks) {
					msg.setError(ErrorType.BadBlock);
					throw new BadBlocStructureException(
							Messages.getString("errors.nonblocking.invalidMessageLength") //$NON-NLS-1$
									+ "; " + Messages //$NON-NLS-1$
											.getString("errors.nonblocking.eventId1") //$NON-NLS-1$
									+ eventId
									+ "; " //$NON-NLS-1$
									+ Messages
											.getString("errors.nonblocking.blockStart1") + " " //$NON-NLS-1$ //$NON-NLS-2$
									+ (((msg.getMessageId() - (tailleEntete + tailleTableEvenement)) / tailleBlocData) + 1 - blockDefaultMessagesCount));
				}

				// block doesn't start with CD or CDD
				if ((this.message.length < start + blocks * tailleBlocData)
						&& ((this.message[start + blocks * tailleBlocData] & 0xFF) != codeDebut && (this.message[start
								+ blocks * tailleBlocData] & 0xFF) != codeDebutDefaut)) {
					msg.setError(ErrorType.BadBlock);
					throw new BadBlocStructureException(
							Messages.getString("errors.nonblocking.invalidStartBlockCode1")); //$NON-NLS-1$
				}
			} else {
				msg.setError(ErrorType.BadBlock);
				throw new BadBlocStructureException(
						Messages.getString("errors.nonblocking.invalidMessageLength") //$NON-NLS-1$
								+ "; " + Messages //$NON-NLS-1$
										.getString("errors.nonblocking.eventId1") //$NON-NLS-1$
								+ eventId
								+ "; " //$NON-NLS-1$
								+ Messages
										.getString("errors.nonblocking.blockStart1") + " " //$NON-NLS-1$ //$NON-NLS-2$
								+ (((msg.getMessageId() - (tailleEntete + tailleTableEvenement)) / tailleBlocData) + 1 - blockDefaultMessagesCount));
			}
		}

		// the calculated length of the variables doesn's match the length set
		// for those variables
		// if message has dynamic variables => size is unknown
		if (length != variablesLength && !msgUnified) {
			throw new BadStructureFileException(
					Messages.getString("errors.nonblocking.invalidMessageLength1")); //$NON-NLS-1$
		}

		// nr octets event id + length of variables (whitout the nr octets of
		// crc)
		msg.setLongueur(2 + variablesLength);

		int crcPos = pos + start;
		if ((crcPos - tailleTableEvenement - tailleEntete + 1) % tailleBlocData == 0) {
			crcPos += 2;
		}
		int crc = this.message[crcPos] << 8;

		if ((crcPos + 1 - tailleTableEvenement - tailleEntete + 1)
				% tailleBlocData == 0) {
			// next octet of the CRC is in the next block
			crc |= (this.message[crcPos + 1 + 2] & 0xff);
		} else {
			crc |= (this.message[crcPos + 1] & 0xff);
		}

		crc = crc & 0x0000ffff;
		
		byte[] crcData = toPrimitiveByteArray(crcValues);
		
		if (isExploration == false) {
			messagesCRC.put(msg, crc);
			messagesCRCData.put(msg, crcData);
		}
		
		boolean controleCrcOk = checkMessageCRC(crc, crcData, this.controlTable);
		if (lastMessage != null) {
			if (controleCrcOk) {
				if (newDateEncountered) {
					lastDateValue = newDateValue;
					dateValide = true;
				} else {
					if (!dateValide && lastDateValue != -1) {
						dateValide = true;
					}
				}
			} else {
				dateValide = false;
			}
		} else {
			if (!controleCrcOk) {
				dateValide = false;
			} else {
				if (!newDateEncountered) {											
					if (ConstantesParcoursSamNg.initDateS != null) {
						if (!ConstantesParcoursSamNg.initDateS.isEmpty()) {						
							this.tc = new ConversionTempsTomNg(ConstantesParcoursSamNg.initDateS);
						}
					} else {
						dateValide = false;
					}
				} else {
					lastDateValue = newDateValue;
				}
			}
		}

		if (!dateValide) {
			msg.setMessageIncertitude(true);
		} else {
			// Only if is everything ok, then set the corrected time to message
			if (newDateEncountered) {
				this.tc.addDate(newDateValue);
			} else {
				if (lastDateValue != -1) {
					this.tc.addDate(lastDateValue);
				}
			}
			if (newChgtEncountered) {
				this.tc.addDate(dateAvantChgt);
			}
		}

		if (newChgtEncountered) {
			this.tc.addTime(tempsAvantChgt);
			newChgtEncountered = false;
		} else if (newTempEncountered) {
			this.tc.addTime(newTempValue);
		}

		ParseurAdapteur padapt = new ParseurAdapteur();
		padapt.gererEvNonDate(msg);

		// set time, distance and diametre. this will be used to set the flag
		msg.setAbsoluteTime(this.tc.getCurrentDateAsMillis());
		msg.deepTrimToSize();
		return msg;
	}
	
	/**
	* This function informed if an event is unified 
	* 
	* @param codes
	*  	List of codes content into the current event
	* @param jruData
	* 		This function filled this structure with Structure Dynamic information
	* 			- variable which content the length of event packed (VNSBA_XXX_RAW_LEN)
    *			- variable of dynamic structure (VNSBA_XXX_RAW_PTR)
    *  		- variable of dynamic structure header (NID_MESSAGE_XXX)
    *  
    * @return true if the event is unified 
	*/
	private static boolean isMsgUnified(int[] codes, JRUData jruData) {
		int codeRawLenTmp = -1; 		
		
		for (int code : codes) {			
			/* If a variable of the event is a "structure dynamique",
			the event comes from a unified journey file */
			if (GestionnairePool.getInstance().getVariable(code).
				getDescriptor().getTypeVariable() == TypeVariable.STRUCTURE_DYNAMIQUE) {
				
				/* If the the variable of the structure dynamique is not 
				preceded by a variable (the length), the event couldn't come 
				from a unified journey file */
				if (codeRawLenTmp == -1) {
					continue;
				}
				
				jruData.codeNID = ((VariableDynamique)GestionnairePool.
									getInstance().getVariable(code)).
									getVariableEntete().getDescriptor().
									getM_AIdentificateurComposant().getCode();
				
				// The length is the previeus variable
				jruData.codeRawLen = codeRawLenTmp;
				jruData.codeRawPtr = code;
				
				return true;
			}
			
			codeRawLenTmp = code;
		}
		return false;
	}

	private void checkVariableComplexe(Message msg, AVariableComposant var)
			throws BadStructureFileException {
		if (!((VariableComplexe) var).verifierTailleVariable()) {
			msg.setError(ErrorType.EventId);
			throw new BadStructureFileException(
					Messages.getString("errors.nonblocking.invalidXmlComplexTaille2")
							+ var.getDescriptor()
									.getM_AIdentificateurComposant().getCode()
							+ ", "
							+ Messages
									.getString("errors.nonblocking.blockStart1")
							+ " "
							+ (((msg.getMessageId() - (tailleEntete + tailleTableEvenement)) / tailleBlocData) + 1 - blockDefaultMessagesCount));
		}
	}

	private void checkMessage(final int start, Message msg, int pos)
			throws BADEventLenghtException {
		// start position calculated from the beginning of the data block
		int startPosData = start - tailleEntete - tailleTableEvenement;
		// if the position where should start the variables is at the end of
		// the bloc
		if (((pos + startPosData) % tailleBlocData) == 0) {
			msg.setError(ErrorType.EventId);
			throw new BADEventLenghtException(
					Messages.getString("errors.nonblocking.invalidMessageLength") + "; " //$NON-NLS-1$ //$NON-NLS-2$
							+ Messages
									.getString("errors.nonblocking.blockStart1") + " " //$NON-NLS-1$ //$NON-NLS-2$
							+ (((msg.getMessageId() - (tailleEntete + tailleTableEvenement)) / tailleBlocData) + 1 - blockDefaultMessagesCount)); //$NON-NLS-1$); //$NON-NLS-1$
		}
	}

	private static AVariableComposant getVariable(Message msg, int eventId,
			int code) throws BadVariableCodeException {
		// load the variable and add it to the message
		AVariableComposant var = GestionnairePool.getInstance().getVariable(
				code);
		if (var == null) {
			var = GestionnairePool.getInstance().getVariableDynamique(code);
		}
		if (var == null) {
			// if variable not found throw an error
			msg.setError(ErrorType.EventId);
			throw new BadVariableCodeException(
					Messages.getString("errors.nonblocking.invalidVariableId") //$NON-NLS-1$
							+ "; " //$NON-NLS-1$
							+ Messages
									.getString("errors.nonblocking.variableId1") //$NON-NLS-1$
							+ +code
							+ "; " //$NON-NLS-1$
							+ Messages.getString("errors.nonblocking.eventId1") //$NON-NLS-1$
							+ eventId
							+ Messages
									.getString("errors.nonblocking.blockStart1") + " " //$NON-NLS-1$ //$NON-NLS-2$
							+ (((msg.getMessageId() - (tailleEntete + tailleTableEvenement)) / tailleBlocData) + 1));
		}
		return var;
	}

	private static byte[] toPrimitiveByteArray(List<Byte> crcValues) {
		byte data2[] = new byte[crcValues.size()];
		int count = 0;
		for (Byte b : crcValues) {
			data2[count] = b.byteValue();
			count++;
		}
		return data2;
	}

	private DescripteurComposite getDescripteurComposite(int id) {
		for (int i = 0; i < descrTable.getLength(); i++) {
			DescripteurComposite descrComp = (DescripteurComposite) descrTable
					.getEnfant(i);
			DescripteurEvenement descrEvent = (DescripteurEvenement) descrComp
					.getEnfant(0);
			if (descrEvent.getM_AIdentificateurComposant().getCode() == id) {
				return descrComp;
			}
		}

		return null;
	}

	private void chargerMessageEvt(Message msg, int id)
			throws BadArgumentInFileException {
		Evenement ev = GestionnairePool.getInstance().getEvent(id);

		// cas où l'évévement est dans la table evt/var et dans le fichier xml
		if (((DescripteurEvenement) ev.getM_ADescripteurComposant()).getNom() == null) {

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
			tblLangues.setNomUtilisateur(Langage.FR, "Code de l'événement: " //$NON-NLS-1$
					+ id);
			tblLangues.setNomUtilisateur(Langage.EN, "Event code: " + id); //$NON-NLS-1$

			ev.setNomUtilisateur(tblLangues);
			GestionnairePool.getInstance().ajouterEvenement(ev);

			msg.setError(ErrorType.EventId);
			SamngLogger
					.getLogger()
					.warn(Messages
							.getString("errors.nonblocking.notXmlFoundEvent") //$NON-NLS-1$
							+ "; " + Messages //$NON-NLS-1$
									.getString("errors.nonblocking.eventId1") + id + " " + //$NON-NLS-1$ //$NON-NLS-2$
							Messages.getString("errors.nonblocking.blockStart1") + " " + ((((msg.getMessageId() - (tailleEntete + tailleTableEvenement)) / tailleBlocData)) + 1 - blockDefaultMessagesCount)); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			// check if is set the nom utilisateur
			if (ev.getNomUtilisateur() == null
					|| ev.getNomUtilisateur().size() == 0) {
				msg.setError(ErrorType.XMLRelated);
				throw new BadArgumentInFileException(
						Messages.getString("errors.nonblocking.invalidXmlUsersList")
								+ "; "
								+ Messages
										.getString("errors.nonblocking.eventId1")
								+ id
								+ "; "
								+ Messages
										.getString("errors.nonblocking.blockStart1")
								+ " "
								+ (((msg.getMessageId() - (tailleEntete + tailleTableEvenement)) / tailleBlocData) + 1 - blockDefaultMessagesCount));
			}

			// check if is set the caractereTemporelle
			if (((DescripteurEvenement) ev.getM_ADescripteurComposant())
					.getCaractTemporelle() == null) {
				msg.setError(ErrorType.XMLRelated);
				throw new BadArgumentInFileException(
						Messages.getString("errors.nonblocking.invalidXmlEvCaractTemp")
								+ "; "
								+ Messages
										.getString("errors.nonblocking.eventId1")
								+ id
								+ "; "
								+ Messages
										.getString("errors.nonblocking.blockStart1")
								+ " "
								+ (((msg.getMessageId() - (tailleEntete + tailleTableEvenement)) / tailleBlocData) + 1 - blockDefaultMessagesCount));
			}
		}

		// set the message event
		msg.setEvenement(ev);
	}

	/**
	 * Sets the flag loaded from the file and calculates the rupture of time and
	 * distance
	 * 
	 * @param crtMsg
	 *            crurrent message
	 * @param fin
	 * @param deb
	 * @throws ParseurBinaireException
	 *             if any exception occured
	 */
	private void setFlags(Message crtMsg) throws ParseurBinaireException {

		// set the flag for the current event
		Flag flag = this.loadedFlags.get(crtMsg.getEvenement()
				.getM_ADescripteurComposant().getM_AIdentificateurComposant()
				.getNom());
		crtMsg.setFlag(flag);
		if (flag != null) {
			if (flag.getLabel().equals("{")) { //$NON-NLS-1$

				if (lastMessage != null) {
					Flag flagmsg = lastMessage.getFlag();
					Flag fl = new Flag(0, "}", lastMessage.getEvenement() //$NON-NLS-1$
							.getM_ADescripteurComposant()
							.getM_AIdentificateurComposant().getNom());
					if (flagmsg != null) {
						flagmsg.appendFlag(fl);
					} else {
						lastMessage.setFlag(fl);
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
		}
	}

	/**
	 * Extract the byte[] value for a variable and convert it conform to the
	 * value of the 1er-poids-octet and 1er-poids-bit
	 * 
	 * @param var
	 *            the variable
	 * @param value
	 *            the byte[] that contains the value as is in the binary file
	 * @throws BadStructureFileException
	 */
	private byte[] setVariableValue(AVariableComposant var, byte[] valueOrig,
			Message msg) throws BadStructureFileException {
		long retVal = 0;
		Type typeVar;
		byte value[] = null;
		int posVar = 0;
		Poids poidsPremierOctetVar = null;
		Poids poidsPremierBitVar = null;
		AVariableComposant v = null;
		if (var instanceof VariableComposite) {
			// it is a composed variable
			VariableComposite varComp = ((VariableComposite) var);

			int remainedBits = 0;
			int nbChildren = varComp.getVariableCount();
			int vo_size = valueOrig.length;

			for (int k = 0; k < nbChildren; k++) {
				// extract the value for each variable
				v = varComp.getEnfant(k);
				v.setParent(var);
				int tailleOctets = v.getDescriptor().getTailleOctets();
				poidsPremierOctetVar = v.getDescriptor().getPoidsPremierOctet();
				poidsPremierBitVar = v.getDescriptor().getPoidsPremierBit();
				value = new byte[vo_size];
				value = Arrays.copyOfRange(valueOrig, 0, valueOrig.length);

				// inversion des octets et des bits de la variable complexe
				if (varComp.getDescriptor().getPoidsPremierBit() == Poids.LSB) {
					value = reverseBits(value);
				}

				if (varComp.getDescriptor().getPoidsPremierOctet() == Poids.LSB) {
					value = reverseOctets(value);
				}

				typeVar = v.getDescriptor().getType();

				if (v.getDescriptor().getOffsetComposant() != null) {
					remainedBits = 7 - v.getDescriptor().getOffsetComposant()
							.getBitOffset();
					posVar = v.getDescriptor().getOffsetComposant()
							.getByteOffset();
				}

				// if it's a value given in bits not octets, then gets the bits
				if (typeVar == Type.boolean1 || typeVar == Type.uintXbits
						|| typeVar == Type.BCD4 || typeVar == Type.BCD8
						|| typeVar == Type.intXbits) {
					retVal = setXbitsValue2(v, value, posVar, remainedBits);
					remainedBits = (int) (retVal & 0xFFFFFFFF);
					posVar = (int) ((retVal >> 32) & 0xFFFFFFFF);
					// v.setValeur(null);

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
						remainedBits = Math.abs(diffBits);
					}
				} else {
					// if the previous var was a bitwise variable, and the
					// remained bits are different
					// than 0 we should increment the posVar position as this
					// was not made before

					// cas o`u la variable commence au début d'un octet
					if (remainedBits == 0) {

						byte valTemp[] = Arrays.copyOfRange(value, posVar,
								posVar + tailleOctets);

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
								posVar + tailleOctets + 1);
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

						v.setValeur(valVar);
					}
					posVar += (tailleOctets);
				}
			}
			var.setValeur(value);
		} else {
			poidsPremierOctetVar = var.getDescriptor().getPoidsPremierOctet();
			poidsPremierBitVar = var.getDescriptor().getPoidsPremierBit();
			int vo_size = valueOrig.length;
			value = new byte[vo_size];
			value = Arrays.copyOfRange(valueOrig, 0, valueOrig.length);

			// reverse the order of octets or bytes if the poids are set on LSB
			if (poidsPremierBitVar == Poids.LSB) {
				value = reverseBits(value);
			}
			if (poidsPremierOctetVar == Poids.LSB) {
				value = reverseOctets(value);
			}

			// set the value
			var.setValeur(value);
		}
		return value;
	}

	/**
	 * Récupère la valeur d'une sous-variable(dans une varaible complexe)
	 * 
	 * @param v
	 *            la varaible dont on doit définir la valeur
	 * @param value
	 *            le tableau d'octets contenant la valeur
	 * @param posVar
	 *            la position dans le tableau d'octets de la variable complexe
	 * @param startBitIdx
	 *            le nombre de bits restant dans l'octet
	 * @return a long containing the new posVar on the first 32 bits and the
	 *         number of bits remained from the last octet on the last 32 bits
	 */
	private long setXbitsValue2(AVariableComposant v, byte[] value, int posVar,
			int startBitIdx) {
		// récupération de la longueur de la variable
		int length = v.getDescriptor().getTailleBits();
		byte b = 0;
		int mask;
		int extractedBytesCnt = 0;
		int positionsToShift = 0;
		Poids poidsPremierBitVar = v.getDescriptor().getPoidsPremierBit();
		Poids poidsPremierOctetVar = v.getDescriptor().getPoidsPremierOctet();
		Type typeVariable = v.getDescriptor().getType();

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
		if (length <= 8 - startBitIdx) {

			// récupération du nombre de bits restant dans l'octet
			remainedBits = 8 - startBitIdx;

			// ajout olivier
			// cas d'une variable en poids-1er-bit=LSB
			if (poidsPremierBitVar == Poids.LSB) {
				// on inverse les bits de tout le tableau d'octet
				byte[] bitsReverse = reverseBits(value);

				// cas d'un BCD4
				{

					// nouvelles spécifications: on décale d'autant de bits que
					// la
					// le bit de début de lecture(étant donné que l'on inverse

					b = (byte) (bitsReverse[posVar] >>> startBitIdx);

				}

				/*
				 * anciennes spécifications // si la varaible précédente dans le
				 * meme octet n'est pas en // MSB // if (firstPartLSB) { // //
				 * si on est pas au début de l'octet // if (startBitIdx > 0) {
				 * // // on récupère les bits de droite // // l'octet et on
				 * décale // b = (byte) (bitsReverse[posVar] >>> remainedBits
				 * -length ); // } // } // } // la première varable dans l'octet
				 * est en poids-1er-bit=LSB // firstPartLSB = true;
				 */
			}

			else {

				// nouvelles spécifications
				b = (byte) (value[posVar] >>> remainedBits - length);

				/*
				 * anciennes spécifications // if (firstPartMSB) { // if
				 * (startBitIdx > 0) { // b = (byte) (value[posVar] >>>
				 * remainedBits - length); // } // } // } // la première
				 * variable dans l'octet est en poids-1er-bit=MSB //
				 * firstPartMSB = true;
				 */

			}

			// on calcule le masque pour récupérer seulement les bits qui nous
			// interressent
			mask = (int) Math.pow(2, length) - 1;
			// on applique le masque pour récupérer seulement les bits qui nous
			// interressent
			cacheMemory[0] = (byte) (b & mask);

			// un seul octet a été extrait
			extractedBytesCnt = 1;

			// on met à jour l'index du bit à prendre dans l'octet
			startBitIdx = startBitIdx + length;

		}

		// cas où la variable a une longueur supérieure
		// au nombre de bits restant dans l'octet
		else {

			int octetCourant = posVar;

			// récupération de ce qui reste dans le premier octet de la variable
			remainedBits = 8 - startBitIdx;

			// cas d'une variable en MSB
			if (poidsPremierBitVar == Poids.MSB) {

				// nouvelles spécifications
				b = (byte) (value[posVar]);

				// anciennes spécifications
				// if (startBitIdx > 0) {
				// // on prend les bits tout à gauche
				// // calcul du masque
				// mask = (int) (255 - (Math.pow(2, startBitIdx) - 1));
				// // décalage
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
			else {

				// nouvelles spécifications
				// on récupère l'octet entier
				b = (byte) (value[posVar]);

				// anciennes spécifications
				// b = bitsReverse[octetCourant];
				// if (startBitIdx > 0) {
				// b = (byte) (b >>> startBitIdx);
				// }
			}

			// on ne prend que les bits qui nous interressent
			mask = (int) Math.pow(2, remainedBits) - 1;
			// on met l'octet dans le tableau d'octets temporaire
			cacheMemory[extractedBytesCnt] = (byte) ((b & mask));

			// on incrémente pour obtenir le prochain octet
			extractedBytesCnt++;
			// on met a jour le nombre de bits restant pour la variable
			length -= remainedBits;

			boolean varTerminee = false;
			while (!varTerminee) {

				// l'octet courant change d'indice
				octetCourant = posVar + extractedBytesCnt;
				// si le nombre de bits restants de la variable est inférieure à
				// 8
				if (length - 8 <= 0) {

					startBitIdx = length;

					positionsToShift = 8 - length;

					varTerminee = true;

				} else {
					// on récupère l'octet entier

					length -= 8;

					if (length <= 0)
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
		int tailleVar = v.getDescriptor().getTailleBits();
		if (tailleVar <= 8) {
			if (valuesByteArray.length == 2) {

				BigInteger bigInt = new BigInteger(valuesByteArray);
				bigInt = bigInt.shiftRight(positionsToShift);
				// cas où la variabe est autre qu'un intxbits

				byte tabVar[] = bigInt.toByteArray();
				byte tabVar2[] = new byte[1];
				if (poidsPremierBitVar == Poids.MSB) {
					mask = (int) Math.pow(2, tailleVar) - 1;
				}
				// poidsPremierBitVar == Poids.LSB
				else {
					tabVar = reverseBits(tabVar);
					mask = (int) Math.pow(2, tailleVar) - 1;
					if (tabVar.length > 1)
						tabVar[0] = (byte) (tabVar[1] >> (8 - v.getDescriptor()
								.getTailleBits()));
					else
						tabVar[0] = (byte) (tabVar[0] >> (8 - v.getDescriptor()
								.getTailleBits()));

				}
				tabVar2[0] = (byte) (tabVar[0] & mask);

				v.setValeur(tabVar2);
			} else {
				v.setValeur(valuesByteArray);
			}
		}

		else {
			if (positionsToShift > 0) {

				byte[] valueArr = null;

				BigInteger bigInt = null;
				// cas de tout les entiers signés
				// il sont enregistrés en complément à 2 dans le fichier de
				// parcours
				if (typeVariable == Type.int16 || typeVariable == Type.intXbits
						|| typeVariable == Type.int24
						|| typeVariable == Type.int32
						|| typeVariable == Type.int64
						|| typeVariable == Type.int8) {

					// on créé un BigInteger signé
					bigInt = new BigInteger(valuesByteArray);
					bigInt = bigInt.shiftRight(positionsToShift);

				}
				// on créé un BigInteger non signé
				// /t == Type.uintXbits || t == Type.uint8 || t == Type.uint16
				// || t == Type.uint24 || t == Type.uint32
				// || t == Type.uint64 || t == Type.real32
				// || t == Type.real64
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

			}
			if ((tailleVariable > 8) && (tailleVariable % 8 > 0)) {

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

	/**
	 * Loads a variable written on multiple blocks
	 * 
	 * @param var
	 * @param pos
	 *            returns the last position
	 * @return
	 */
	private int chargeVariableSurBlocksMultiple(AVariableComposant var,
			int pos, int start, int tailleOctets) {

		int crt = 0;
		byte[] value = new byte[tailleOctets];
		byte[] tmp = null;
		int offsetBlock = 0;
		int valTmp = 0;
		int valTmp2 = 0;
		// sur chaque bloc il y a systèmatiquement 2 octets attribué au code
		// début et code fin
		int decalageCodeDebutCodeFin = 2;
		// get the value
		while (crt < tailleOctets) {

			if ((tailleOctets - crt + decalageCodeDebutCodeFin < tailleBlocData)
					&& crt != 0) {
				// dernier block
				tmp = Arrays.copyOfRange(this.message, start + pos + crt, start
						+ tailleOctets + pos);

				for (int j = 0; j < tailleOctets - crt; j++) {
					value[crt + j] = tmp[j];

				}
				crt += (tailleOctets - crt);

			} else if (crt == 0) {
				// premier block
				offsetBlock = (start + pos - tailleEntete - tailleTableEvenement)
						% tailleBlocData;

				valTmp = tailleBlocData - 1 - offsetBlock;
				// get all data bytes in a temporary array
				tmp = Arrays.copyOfRange(this.message, start + pos, start + pos
						+ valTmp);

				for (int j = 0; j < valTmp; j++) {
					value[crt + j] = tmp[j];
				}
				// on first block and we have here the codes for debut and fin
				// and code for event
				pos += decalageCodeDebutCodeFin;
				crt += valTmp;
			} else {
				// du 2ème à n block - 1
				valTmp2 = tailleBlocData - decalageCodeDebutCodeFin;
				tmp = Arrays.copyOfRange(this.message, start + pos + crt, start
						+ pos + crt + tailleBlocData - 1);
				for (int j = 0; j < valTmp2; j++) {
					value[crt + j] = tmp[j];
				}
				// on multiple block and we have here the codes for debut and
				// fin
				pos += decalageCodeDebutCodeFin;
				crt += valTmp2;
			}
		}
		// set the value
		var.setValeur(value);

		// increment the position with the length
		pos += tailleOctets;

		return pos;
	}

	/**
	 * Loads the association table of events and variables
	 * 
	 * @return the assoctiation table as a parcour
	 * @throws ParseurBinaireException
	 *             throws an exception if any occurs
	 */
	private ADescripteurComposant chargerTableEvtVariable()
			throws ParseurBinaireException {

		// creation of a DescripteurComposite for contain all associations
		// codeEvt codeVar1 codeVar2 codeVar3 etc.
		ADescripteurComposant descripteurTable = new DescripteurComposite();

		int crcPos = tailleEntete + tailleTableEvenement - 4;
		int pos = tailleEntete;
		boolean notFinished = true;

		while (pos < crcPos && notFinished) {

			// creation of a DescripteurComposite for each assoctiation codeEvt
			// codeVar1 codeVar2 codeVar3 etc...
			ADescripteurComposant descComp = new DescripteurComposite();
			ADescripteurComposant desc = null;
			AIdentificateurComposant identif = null;

			ArrayList<IdentificateurVariable> vars = new ArrayList<IdentificateurVariable>();
			// add the descripor for the event and for all variable associated
			do {
				int id = (this.message[pos] & 0xff) << 8
						| (this.message[pos + 1] & 0xff);

				if (((this.message[pos] & 0xff) | VARIABLE_MASK) == VARIABLE_MASK) {
					// create a DescripteurVariable to contain all codeVar
					TypeVariable type = GestionnairePool.getInstance()
							.getXMLParser().getVariableType(id);

					if (type == null) {
						// if no type creates a dummy descriptor and
						// identificator
						desc = new DescripteurVariable();
						identif = new IdentificateurVariable();
						identif.setCode(id);
						desc.setM_AIdentificateurComposant(identif);

					} else {
						// creates the descriptor and th identificator conform
						// to the type
						switch (type) {
						case VAR_ANALOGIC:
							desc = GestionnaireDescripteurs
									.getDescripteurVariableAnalogique(id);
							identif = desc.getM_AIdentificateurComposant();
							break;
						case VAR_DISCRETE:
							desc = GestionnaireDescripteurs
									.getDescripteurVariableDiscrete(id);
							identif = desc.getM_AIdentificateurComposant();
							break;
						case VAR_COMPLEXE:
							desc = GestionnaireDescripteurs
									.getDescripteurVariableComplexe(id);
							identif = desc.getM_AIdentificateurComposant();
							break;
						case VAR_COMPOSEE:
							desc = new DescripteurVariable();
							identif = new IdentificateurVariable();
							desc.setM_AIdentificateurComposant(identif);
							break;
						case STRUCTURE_DYNAMIQUE:
							desc = GestionnaireDescripteurs
									.getDescripteurStructureDynamique(id);
							identif = desc.getM_AIdentificateurComposant();
							break;
						case VAR_VIRTUAL:
							break;
						case UNKNOWN:
							break;
						}
					}

					// adds the identificator
					vars.add((IdentificateurVariable) identif);

				} else if (((this.message[pos] & 0xff) & FIN_CODE) != FIN_CODE) {// :
					// CIU:
					// This
					// is
					// according
					// to
					// old
					// specs
					// not allowed cod event
					if (id == 65535) {
						throw new BadEventCodeException(
								Messages.getString("errors.nonblocking.nonAcceptableEventId")); //$NON-NLS-1$
					}
					id = id & 0x7FFF; // 0111 1111 1111 1111

					// create a DescripteurEvenement to contain codeEvt
					desc = GestionnaireDescripteurs.getDescripteurEvenement(id);
					identif = desc.getM_AIdentificateurComposant();

					// eliminate the first 1 of the id to get just the event id
					vars.clear();

				} else {
					// it is an FF code which means that the table has endded
					notFinished = false;
					for (int i = pos; i < tailleEntete + tailleTableEvenement; i++) {
						if ((this.message[pos] & 0xff) != codeFF) {
							throw new BadTableEventStructureException(
									Messages.getString("errors.nonblocking.invalidValuesAfterMsg")); //$NON-NLS-1$
						}
					}
					break;
				}

				// set the code which is on 2 bytes in the message

				descComp.ajouter(desc);

				// set the position to the next code(each code has 2 octets)
				pos += 2;

			} while (pos < crcPos
					&& ((this.message[pos] & 0xff) & EVENT_MASK) != EVENT_MASK);

			// when you meet another bit de poids fort =1
			descripteurTable.ajouter(descComp);
		}

		return descripteurTable;
	}

	/**
	 * Checks if there is the corect number of FF bytes
	 * 
	 * @param start
	 *            the start position
	 * @param noFF
	 *            number of bytes of FF
	 * @return true if the bytes contains only FF, false otherwise
	 */
	private boolean checkOctetsFF(int start, int noFF) {
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

	/**
	 * Check the message if its length is the expected one and throws an
	 * execption if not
	 * @param start
	 *            the start position of the bloc which contains the message
	 * @param descrComp
	 *            the descriptor which contains the variables and also the event
	 * 
	 * @return the variables length in bytes
	 * @throws BadStructureFileException
	 *             if the bloc has not the correct structure
	 * @throws BADEventLenghtException
	 */
	private int checkMessageLength(int[] codes, int start, Message msg)
			throws BadStructureFileException, BADEventLenghtException {

		int variablesLength = getVariablesLength(codes);

		int valTmp = this.message[start + tailleBlocData - 1] & 0xFF;
		int valTmp2 = variablesLength + 6;
		// codedebut + event code + variables length + CRC + codeContinueFin
		if (valTmp2 > tailleBlocData) {
			if ((valTmp) != codeContinueFinBloc
					&& ((valTmp) != codeContinueFinDefaut)) {
				msg.setError(ErrorType.BadLength);
				// the position it's at the end of the bloc, and it's without
				// CRC
				throw new BadStructureFileException(
						Messages.getString("errors.nonblocking.invalidMessageLength1") + "; " + //$NON-NLS-1$ //$NON-NLS-2$
								Messages.getString("errors.nonblocking.invalidMessageLength11") + "; " //$NON-NLS-1$ //$NON-NLS-2$
								+ Messages
										.getString("errors.nonblocking.blockStart1") //$NON-NLS-1$
								+ " " + (goodMsgs.size() + 1 - blockDefaultMessagesCount));//(((msg.getMessageId() - (tailleEntete + tailleTableEvenement)) / tailleBlocData) + 1)); //$NON-NLS-1$
			}
		} else if (valTmp2 <= tailleBlocData) {
			if (((valTmp) != codeFin) && (valTmp) != codeFinDefaut) {
				// codedebut + event code + variables length + CRC +
				// codeContinueFin
				throw new BadStructureFileException(
						Messages.getString("errors.nonblocking.invalidMessageLength1") + "; " + //$NON-NLS-1$ //$NON-NLS-2$
								Messages.getString("errors.nonblocking.invalidMessageLength12") + "; " //$NON-NLS-1$ //$NON-NLS-2$
								+ Messages
										.getString("errors.nonblocking.blockStart1") //$NON-NLS-1$
								+ " " + (goodMsgs.size() + 1 - blockDefaultMessagesCount));//(((msg.getMessageId() - (tailleEntete + tailleTableEvenement)) / tailleBlocData) + 1)); //$NON-NLS-1$
			}
		} else {
			// codedebut + event code + variables length + CRC + codeContinueFin
			throw new BadStructureFileException(
					Messages.getString("errors.nonblocking.invalidMessageLength1") + "; " + //$NON-NLS-1$ //$NON-NLS-2$
							Messages.getString("errors.nonblocking.invalidMessageLength12")); //$NON-NLS-1$
		}

		if (variablesLength == 0) {
			msg.setError(ErrorType.EventId);
			throw new BADEventLenghtException(
					Messages.getString("errors.nonblocking.invalidMessageLength")); //$NON-NLS-1$
		}

		return variablesLength;
	}

	private int[] getVariablesCodes(DescripteurComposite descrComp, Message msg)
			throws BadStructureFileException {
		int descNo = descrComp.getLength();
		int[] codes = new int[descNo - 1];

		// check if the length of the event related variables is the expected
		// one
		for (int i = 1; i < descNo; i++) {
			// get the code of the variable from the TableEvtVar
			DescripteurVariable descVar = (DescripteurVariable) descrComp
					.getEnfant(i);
			int codeVar = descVar.getM_AIdentificateurComposant().getCode();
			if (descVar.getM_AIdentificateurComposant().getNom() == null)
				throw new BadStructureFileException(
						Messages.getString("errors.nonblocking.nonInterpretableEvent")
								+ (((msg.getMessageId() - (tailleEntete + tailleTableEvenement)) / tailleBlocData) + 1)
								+ "; "
								+ Messages
										.getString("errors.nonblocking.nonInterpretableVariable") + codeVar); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			codes[i - 1] = codeVar;
		}

		return codes;
	}

	/**
	 * Méthode qui récupère le nom du fichier XML avant son chargement
	 * 
	 * @return null si problème dans l'entete, sinon: nom du fichier XML
	 */
	public String getNomFichierXml(String fileName) {

		// resets the parser
		resetParser();

		// check if the file is ok
		if (fileName == null || fileName.length() == 0
				|| !new File(fileName).exists()) {
			return null;
		}

		// check the extension file
		if (!BridageFormats.getInstance().isextensionValideFromFormat(
				FormatSAM.TOMNG, fileName)) { //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
		File binaryFile = new File(fileName);

		this.message = loadBinaryFile(binaryFile, 0, -1);
		if (this.message == null) {
			return null;
		}
		if (this.message.length < 4)
			return null;

		String nomFichierXml = new String(Arrays.copyOfRange(this.message, 4,
				this.message[3] + 4));
		resetParser();
		// int cesure=nomFichierXml.lastIndexOf(".xml");
		// nomFichierXml=nomFichierXml.substring(0, cesure+4);
		// set the file name that is written on x bytes starting to byte 4
		return nomFichierXml;
	}

	private static int getVariablesLength(int[] codes) {
		int length = 0;
		for (int code : codes) {
			AVariableComposant var = GestionnairePool.getInstance()
					.getVariable(code);
			if (var != null) {
				int tailleOctets = var.getDescriptor().getTailleOctets();
				if (tailleOctets == DescripteurVariable.NO_TAILLE_OCTETS) {
					length += var.getDescriptor().getTailleBits();
				} else {
					length += tailleOctets * 8;
				}
			}
		}

		return length / 8;
	}

	/**
	 * Parses the binary file
	 * 
	 * @param fileName
	 *            name of the file
	 */
	@SuppressWarnings("unchecked")
	public void parseRessource(String fileName, boolean explorer, int deb,
			int fin) throws AExceptionSamNG, ParseurXMLException {

		TableSegments tempTSeg;
		TableRuptures tempRupt;
		// if (!explorer) {
		// tempTSeg = TableSegments.getInstance();
		// tempRupt = TableRuptures.getInstance();
		// }else{
		// tempTSeg = TableSegmentsExplorer.getInstance();
		// tempRupt = TablesRupturesExplorer.getInstance();
		// }

		tempTSeg = TableSegments.getInstance();
		tempRupt = TableRuptures.getInstance();

		if (this.loadedFlags != null)
			exLoadedFlags = this.loadedFlags;

		try {
			exSegmentsTemps = (HashMap<Integer, SegmentTemps>) tempTSeg
					.getSegmentsTemps().clone();
		} catch (NullPointerException ex) {

		}
		try {
			exSegmentsDistance = (HashMap<Integer, SegmentDistance>) tempTSeg
					.getSegmentsDistance().clone();
		} catch (NullPointerException ex) {

		}
		try {
			exMapRupturesDistance = (HashMap<Integer, TypeRupture>) tempRupt
					.getListeRupturesDistance().clone();
		} catch (NullPointerException ex) {

		}
		try {
			exMapRupturesTemps = (HashMap<Integer, TypeRupture>) tempRupt
					.getListeRupturesTemps().clone();
		} catch (NullPointerException ex) {

		}
		if (this.loadedFlags != null)
			this.loadedFlags.clear();

		tempTSeg.empty();

		tempRupt.clear();

		// ******* CREATE PARCOUR *********

		AFabriqueParcoursAbstraite factory = FabriqueParcours.getInstance();

		traitementFichier(factory, fileName, explorer);

		// ********** LOAD the DATA messages ******

		if (explorer) {
			chargerDataExplore();
		} else {
			Message[][] messages = chargerData(deb, fin);

			if (messages == null) {
				if (currentMessages == null || currentFileName == null)
					return;
				traitementFichier(factory, currentFileName, explorer);
				messages = currentMessages;

				tempTSeg.setSegmentsTemps(exSegmentsTemps);
				tempTSeg.setSegmentsDistance(exSegmentsDistance);
				tempRupt.setMapRupturesDistance(exMapRupturesDistance);
				tempRupt.setMapRupturesTemps(exMapRupturesTemps);
				if (exLoadedFlags != null)
					this.loadedFlags = exLoadedFlags;
			} else {
				currentFileName = new String(fileName);
				int size1 = messages.length;
				currentMessages = new Message[size1][];
				for (int i = 0; i < size1; i++) {
					int size2 = messages[i].length;
					currentMessages[i] = new Message[size2];
					for (int j = 0; j < size2; j++) {
						currentMessages[i][j] = messages[i][j];
					}
				}

			}

			// set the segments for last message
			if (messages[0] != null && messages[0].length > 0) {
				Message lastMsg = messages[0][messages[0].length - 1];
				try {
					setSegmentsDistance(null, lastMsg);

				} catch (Exception e) {
					SamngLogger.getLogger().error(this, e);
				}
				try {
					setSegmentsTemp(null, lastMsg);
				} catch (Exception e) {
					SamngLogger.getLogger().error(this, e);
				}
			}

			// create DATA
			factory.creerData(messages);
			factory.creerReperes(GestionnairePool.getInstance().getReperes());
		}

		System.out
				.println("blocks no = " //$NON-NLS-1$
						+ ((this.message.length - tailleEntete - tailleTableEvenement) / tailleBlocData));
	}

	protected void traitementFichier(AFabriqueParcoursAbstraite factory,
			String fileName, boolean explorer) throws AExceptionSamNG,
			ParseurXMLException {
		// resets the parser
		resetParser();

		this.tc = new ConversionTempsTomNg("01/01/2000 00:00:00.000"); //$NON-NLS-1$

		// check if the file is ok
		if (fileName == null || fileName.length() == 0
				|| !new File(fileName).exists()) {
			throw new IllegalArgumentException("No resource to parse"); //$NON-NLS-1$
		}

		File binaryFile = new File(fileName);

		this.message = loadBinaryFile(binaryFile, 0, -1);
		if (this.message == null) {
			throw new IllegalArgumentException("No resource to parse"); //$NON-NLS-1$
		}
		if (this.message.length < 4)
			throw new BadFileLengthException(
					Messages.getString("errors.blocking.invalidHeaderLength1")); //$NON-NLS-1$

		// create the Parcour
		factory.creerParcours();

		// ********* LOAD ENTETE ************

		// create base Entete

		AParcoursComposant baseEntete = chargerEntete();

		// validate the CRC of the Entete
		ADonneeBlocComposant crc = chargerCRCEntete();
		IStrategieControle control = new CRC16CCITT();

		if (!control.controlerCRC(((Integer) crc.getValeur()).intValue(),
				Arrays.copyOfRange(this.message, 0, tailleEntete - 2))) {
			String errStr = Messages.getString("errors.blocking.badHeaderCrc"); //$NON-NLS-1$
			throw new BadHeaderCrcException(errStr);
		}

		// create the Entete descriptor
		ADescripteurComposant descriptor = chargerDescripteursEntete();
		
		// if the coding format is 2
		if (formatCodage == 2) {
			// We get the size of the data and their signature
			chargerEnteteDonnees();
			if (!validateTailleDonneesParcours()) {
				String errStr = Messages.getString("errors.nonblocking.nonMatchingDataLength");
				SamngLogger.getLogger().error(errStr);
			}
			if (!validateIntegriteDonneesParcours()) {
				String errStr = Messages.getString("errors.nonblocking.badHeaderCrcData");
				SamngLogger.getLogger().error(errStr);
			}
		}

		// create the Entete
		factory.creerEntete(descriptor, baseEntete);

		// ************* PARSE FLAGS file **********/
		ParseurFlags.getInstance().parseRessource(
				RepertoiresAdresses.FLAGS_FILE_DIR + "flags.xml", false, 0, -1);
		this.loadedFlags = ParseurFlags.getInstance().chargerFlags();

		// ************ PARSE XML file ********/
		GestionnairePool.getInstance().chargerFichierXml(
				RepertoiresAdresses.xml + File.separator + nomFichierXML.trim(), fileName);

		// ******** LOAD association TABLE of events and variables ******

		// check the CRC
		ADonneeBlocComposant tableCRC = chargerCRCTableEvtVar();
		IStrategieControle controlTable = new CRC32();

		if (!controlTable.controlerCRC(
				((Integer) tableCRC.getValeur()).intValue(),
				Arrays.copyOfRange(this.message, tailleEntete, tailleEntete
						+ tailleTableEvenement - 4))) {
			String errStr = Messages.getString("errors.blocking.badEvTableCrc"); //$NON-NLS-1$
			throw new BadEvTableCrcException(errStr);
		}

		ADescripteurComposant descripteursEvt = chargerTableEvtVariable();

		// create the table
		factory.creerTableEvtVar(descripteursEvt);

		// affichage ordonné des variables
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
		info.setNomFichierXml(((Entete) baseEntete).getNomFichierXML());
		factory.creerInfoFichier(info);

	}

	private void chargerEnteteDonnees() {
		int start = 18 + tailleNomFichierXML;
		tailleFichierParcours = (short) ((this.message[start] << 8) | (this.message[start + 1] & 0xff));
		start += 2;
		nomFichierParcours = new String(Arrays.copyOfRange(this.message, start,
				start + tailleFichierParcours)).trim();
		start += tailleFichierParcours;
		tailleDonneesParcours = this.message[start] << 3 * 8
				| (this.message[start + 1] << 2 * 8 & 0xff0000)
				| (this.message[start + 2] << 1 * 8 & 0x00ff00)
				| (this.message[start + 3] & 0xff);
		start += 4; 
		signatureDonneesParcours = this.message[start] << 3 * 8
				| (this.message[start + 1] << 2 * 8 & 0xff0000)
				| (this.message[start + 2] << 1 * 8 & 0x00ff00)
				| (this.message[start + 3] & 0xff);
	}
	
	private boolean validateTailleDonneesParcours() {
		return tailleDonneesParcours == this.message.length - tailleEntete - tailleTableEvenement;
	}
	
	private boolean validateIntegriteDonneesParcours() {
		IStrategieControle controleDonnees = new CRC32();
		return controleDonnees.controlerCRC(signatureDonneesParcours, 
				Arrays.copyOfRange(this.message, tailleEntete + tailleTableEvenement, this.message.length));
	}

	@Override
	protected AVariableComposant chargerReperes() {
		return null;
	}

	private static StructureDynamique gererStructureDynamique(
			StructureDynamique definitionStructureDynamique, int start,
			Message msg, JRUData jruData) {
		// création d'une nouvelle instance de structure dynamique
		StructureDynamique structureDynamique = new StructureDynamique();

		// définition du descripteur de la nouvelle structure dynamique
		structureDynamique.setDescripteur(definitionStructureDynamique
				.getDescriptor());

		// récupération de la définition de la variable d'entete
		AVariableComposant defVarEntete = GestionnairePool.getInstance()
				.getVariable(
						definitionStructureDynamique.getVariableEntete()
								.getDescriptor()
								.getM_AIdentificateurComposant().getCode());

		// copie de la varaible d'entete
		AVariableComposant varEntete = defVarEntete.copy();

		// valorisation de la varaible d'entete
		remplirValueJRU(varEntete, start, msg, jruData);

		jruData.pos += varEntete.getDescriptor().getTailleBits();

		if (jruData.lecturePaquetEnCours)
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

		// affectation de la variable d'entete à la nouvelle structure dynamique
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
						listeSousVariable, start, msg, jruData);

				for (AVariableComposant composant : listeSousVariable2) {
					composant.setParent(structureDynamique);
				}
				tableSousVar.setM_AVariableComposant(listeSousVariable2);
				structureDynamique.ajouterTableSousVariable(tableSousVar);
			}
			// Cas où le NID_XUSER n'est pas connu (Packet 44)
			// Message d'erreur + affichage de la valeur du NID_XUSER + saut de
			// position de L_PACKET
		} else if (varEntete.getDescriptor().getM_AIdentificateurComposant()
				.getNom().equals("NID_XUSER")) {

			GestionnaireLongueurMessage.getInstance()
					.incrementerLongueurCalculeeMessageCourant(
							varEntete.getDescriptor()
									.getM_AIdentificateurComposant().getNom(),
							(int) jruData.positionPaquetSuivantUsing_L_Packet
									- jruData.pos);

			jruData.pos = (int) jruData.positionPaquetSuivantUsing_L_Packet;

			if (jruData.lecturePaquetEnCours)
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
							+ " " + (goodMsgs.size() + 1 - blockDefaultMessagesCount));
		}
		// Cas où le NID_MESSAGE n'est pas connu
		// Message d'erreur + affichage de la valeur du NID_MESSAGE + pas besoin
		// de saut,
		// Il ne peut pas y avoir d'autre information à décoder après un
		// NID_MESSAGE
		// = pas de NID_MESSAGE après un NID_MESSAGE
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
							+ " " + (goodMsgs.size() + 1 - blockDefaultMessagesCount));
		}

		return structureDynamique;
	}

	private static ChaineDynamique gererChaineDynamique(
			ChaineDynamique definitionChaineDynamique, int start, Message msg,
			JRUData jruData) {

		// création d'une nouvelle instance de chaine
		// dynamique
		ChaineDynamique chaineDynamique = new ChaineDynamique();

		// définition du descripteur de la nouvelle chaine dynamique
		chaineDynamique.setDescripteur(definitionChaineDynamique
				.getDescriptor());

		// récupération de la définition de la variable d'entete
		// donnant le nombre d'occurrence de la variable XText
		AVariableComposant defVarEntete = GestionnairePool.getInstance()
				.getVariable(
						definitionChaineDynamique.getVariableEntete()
								.getDescriptor()
								.getM_AIdentificateurComposant().getCode());

		// copie de la varaible d'entete
		AVariableComposant varEnteteLText = defVarEntete.copy();
		// valorisation de la varaible d'entete
		remplirValueJRU(varEnteteLText, start, msg, jruData);
		jruData.pos += varEnteteLText.getDescriptor().getTailleBits();

		// gestion des longueurs de paquet, message complet et message interne
		if (jruData.lecturePaquetEnCours)
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

		// récupération de la valeur de la variable d'entete
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
		// création d'une nouvelle table sous-variable qui stocke les occurences
		// de la variable XText
		TableSousVariable tableSousVarChaineDynamique = new TableSousVariable();

		for (int i = 0; i < nbOccurenceCaractere; i++) {
			descVar = (DescripteurVariable) defVarXText.getDescriptor();
			typeVariable = descVar.getTypeVariable();

			sousVariableXText = defVarXText.copy();
			if (typeVariable == TypeVariable.VAR_ANALOGIC
					|| typeVariable == TypeVariable.VAR_DISCRETE) {
				remplirValueJRU(sousVariableXText, start, msg, jruData);

				jruData.pos += tailleVarXText;

				// gestion des longueurs de paquet, message complet et message
				// interne
				if (jruData.lecturePaquetEnCours)
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

	private static void remplirValueJRU(AVariableComposant var, int start,
			Message msg, JRUData jruData) {
		byte[] value = null;
		byte[] value2 = null;
		int longueurVariable = 0;
		try {
			int nbOctets = ((Long) msg.getVariable(jruData.codeRawLen)
					.getCastedValeur()).intValue();
			longueurVariable = var.getDescriptor().getTailleBits();
			
			// récupération du message
			byte[] mess = Arrays.copyOfRange(jruData.value, start, start
					+ nbOctets);
			
			// position du signum à 1 :
			// sinon, si en MSB on a 0xFF suivi d'un octet avec bit 7 = 1, le 0xFF sera vu par défaut 
			// comme bit de signe négatif et retiré lors du .toByteArray() car non utile pour
			// qualifier la magnitude comme étant de signe négatif (bit 7 = 1 suffisant) !
			BigInteger bigInt = new BigInteger(1, mess);
			
			// suppression des bits inutiles à droite
			BigInteger bigInt2 = bigInt.shiftRight((nbOctets * 8) - longueurVariable - jruData.pos);

			// Prise en compte du cas où le premier octet est à 0
            // car dans ce cas là la conversion en int le supprimé
			byte[] tabbyte = new byte[(((longueurVariable + jruData.pos) % 8) == 0 ? 0 : 1) + 
			                          ((longueurVariable + jruData.pos) / 8)]; 
			
			byte[] magnitudeByteArray = bigInt2.toByteArray();
			magnitudeByteArray = removeZeros(magnitudeByteArray, magnitudeByteArray.length);
			
			System.arraycopy(magnitudeByteArray, 0, tabbyte, 
					tabbyte.length - magnitudeByteArray.length, magnitudeByteArray.length);			

			// suppresion des bits inutiles à gauches
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
			
			/* décalage à gauche pour les variables complexes
			 * cf. traitement fonction 
			 * com.faiveley.samng.principal.sm.parseurs.parseursJRU.setXbitsValue2 */
			if (var instanceof VariableComplexe) {
				value2 = new byte[value.length];

				value2 = new BigInteger(1, value).shiftLeft(
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
			getParseurParcoursJRU().setVariableValue(var, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static ParseurParcoursJRU getParseurParcoursJRU() {
		if (parseurParcoursJRU == null) {
			parseurParcoursJRU = ParseurParcoursJRU.getInstance();
		}
		return parseurParcoursJRU;
	}

	private static List<AVariableComposant> renseignerListeSousVariable(
			List<AVariableComposant> listeSousVariable, int start, Message msg,
			JRUData jruData) {

		AVariableComposant varEchelleTableSousVar = null;
		TypeVariable typeVariable = null;
		AVariableComposant varComp = null;
		int codeSousVar;
		DescripteurVariable descVar = null;
		// création d'une liste de sous variables temporaire pour modification
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
						remplirValueJRU(variableRenseignee, start, msg, jruData);

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

						// ajout de la sous variable à la liste des sous
						// variables
						listeSousVariable2.add(variableRenseignee);

						// gestion de l'arret de lecture des paquets
						if (jruData.lecturePaquetEnCours) {
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

								jruData.positionPaquetSuivantUsing_L_Packet = (jruData.positionVarEntetePaquet + valeur_L_Packet);
								//								System.out.println("valeur L_PACKET: " + valeur_L_Packet); //$NON-NLS-1$

							}
						}

						jruData.pos += varComp.getDescriptor().getTailleBits();

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

						// récupération de la définition de la structure
						// dynamique
						StructureDynamique defStructureDyn = null;

						if ((varComp != null))
							defStructureDyn = (StructureDynamique) varComp;
						else
							defStructureDyn = (StructureDynamique) sousVariable;

						StructureDynamique structureDynamique2 = gererStructureDynamique(
								defStructureDyn, start, msg, jruData);

						// ajout de la sous variable à la liste des sous
						// variables
						listeSousVariable2.add(structureDynamique2);

					} else if (typeVariable == TypeVariable.TABLEAU_DYNAMIQUE) {

						// récupération de la définition du tableau
						// dynamique
						TableauDynamique defTableauDyn = null;

						if (varComp != null)
							defTableauDyn = (TableauDynamique) varComp;
						else
							defTableauDyn = (TableauDynamique) sousVariable;

						TableauDynamique tableauDynamique2 = gererTableauDynamique(
								defTableauDyn, start, msg, jruData);

						// ajout de la sous variable à la liste des sous
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
								defChaineDyn, start, msg, jruData);

						// ajout de la sous variable à la liste des sous
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
					// récupération de la définition de la tableau dynamique
					StructureDynamique defStructureDyn = (StructureDynamique) varComp;

					// récupération de la liste des paquets
					List<Paquets> listePaquets = gererListePaquets(
							defStructureDyn, start, msg, jruData);

					// ajout des paquets à la table des sous variables
					// et ajout de la varaible mère(varaible d'entete) à chaque
					// paquet
					AVariableComposant variableEntete = null;
					if (listePaquets != null && listePaquets.size() > 0) {
						for (Paquets paquets : listePaquets) {
							// ajout de la sous variable à la liste des sous
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

	private static TableauDynamique gererTableauDynamique(
			TableauDynamique definitionTableauDynamique, int start,
			Message msg, JRUData jruData) {

		// création d'une nouvelle instance de tableau dynamique
		TableauDynamique tableauDynamique = new TableauDynamique();

		// définition du descripteur du tableau dynamique
		tableauDynamique.setDescripteur(definitionTableauDynamique
				.getDescriptor());

		// récupération de la définition de la variable d'entete
		AVariableComposant defVarEntete = GestionnairePool.getInstance()
				.getVariable(
						definitionTableauDynamique.getVariableEntete()
								.getDescriptor()
								.getM_AIdentificateurComposant().getCode());

		// copie de la varaible d'entete

		AVariableComposant varEntete = defVarEntete.copy();
		// valorisation de la varaible d'entete
		remplirValueJRU(varEntete, start, msg, jruData);
		jruData.pos += varEntete.getDescriptor().getTailleBits();

		// gestion des longueurs de paquet, message complet et message interne
		if (jruData.lecturePaquetEnCours)
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

		// récupération de la valeur de la variable d'entete
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
								listeSousVariable, start, msg, jruData);

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

	private static List<Paquets> gererListePaquets(
			StructureDynamique definitionStructureDynamique, int start,
			Message msg, JRUData jruData) {

		List<Paquets> listePaquets = new ArrayList<Paquets>();

		// récupération de la définition de la variable d'entete
		AVariableComposant defVarEntete = GestionnairePool.getInstance()
				.getVariable(
						definitionStructureDynamique.getVariableEntete()
								.getDescriptor()
								.getM_AIdentificateurComposant().getCode());

		// variables utilisées pour le paquet courant
		Paquets paquetCourant = null;

		AVariableComposant varEntetePaquetCourant;
		TableSousVariable tableSousVarPaquetCourant = null;
		List<AVariableComposant> listeSousVariablePaquetCourant;
		TableSousVariable tableSousVarDefinition = null;
		String valeurVarEntetePaquetCourant = ""; //$NON-NLS-1$

		// récupération de la valeur d'arret de lecture des paquets
		// la valeur d'arret de lecture correspond à la valeur de la dernière
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
		//		System.out.println("longueur message enregistrée: " + GestionnaireLongueurMessage.getInstance().getLongueurEnregistreeMessageCourant()); //$NON-NLS-1$
		while (!finMessage) {
			paquetCourant = new Paquets();
			jruData.lecturePaquetEnCours = true;

			// copie de la variable d'entete
			varEntetePaquetCourant = defVarEntete.copy();

			// si la position après lecture de la variable d'entete du paquet ne
			// dépasse pas L_MESSAGE_JRU
			// on lit ce paquet
			// Dans un message interne (NID_MESSAGE) s'il ne reste pas assez de
			// bits pour un entête
			// de packet (8 bits) dans la longueur indiqué par le paquet
			// (L_MESSAGE), c'est que c'est du padding !
			GestionnaireLongueurMessage gestLongueurMessage = GestionnaireLongueurMessage
					.getInstance();
			GestionnaireLongueurPacket gestLongueurPaquet = GestionnaireLongueurPacket
					.getInstance();
			if ((jruData.pos
					+ varEntetePaquetCourant.getDescriptor().getTailleBits() < gestLongueurMessage
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
				remplirValueJRU(varEntetePaquetCourant, start, msg, jruData);

				// récupération de la valeur de la variable d'entete
				valeurVarEntetePaquetCourant = varEntetePaquetCourant
						.getValeurBruteChaineVariableDiscrete();

				String valEntetePourMessage = valeurVarEntetePaquetCourant;

				// après lecture du précédent paquet, la position réelle peut
				// différée de celle donnée par la variable L_PACKET du paquet
				// précédent
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
										+ " " + (goodMsgs.size() + 1 - blockDefaultMessagesCount));										
					}
				} else {
					// récupération de la valeur de la variable d'entete
					valEntetePourMessage = varEntetePaquetCourant
							.getValeurBruteChaineVariableDiscrete();
				}
				valEntetePrecedente = valEntetePourMessage;
				// réinitialisation des longueurs calculée et enregistrée du
				// paquet
				gestLongueurPaquet.setLongueurEnregistreePacketCourant(0);
				gestLongueurPaquet.setLongueurCalculeePacketCourant(0);

				jruData.positionVarEntetePaquet = jruData.pos;
				jruData.pos += varEntetePaquetCourant.getDescriptor()
						.getTailleBits();

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

				// // récupération de la valeur de la variable d'entete
				// valeurVarEntetePaquetCourant =
				// varEntetePaquetCourant.getValeurBruteChaineVariableDiscrete();

				boolean depassementCapaciteMessage = false;
				if ((jruData.pos + 1) > gestLongueurMessage
						.getLongueurEnregistreeMessageCourant()) {
					depassementCapaciteMessage = true;
				}

				// récupération de la table de sous-variable correspondante
				tableSousVarDefinition = definitionStructureDynamique
						.getTableSousVariableReferenceByValeur(valeurVarEntetePaquetCourant);

				// tests :
				// - pour savoir si la valeur de la variable d'entete est une
				// valeur
				// d'arret: tableSousVarDefinition.getM_AVariableComposant()
				// doit
				// etre différent de null
				// - pour savoir si la valeur de la variable d'entete correspond
				// à
				// une
				// table sous variable tableSousVarDefinition doit etre
				// différent de
				// null

				if (!depassementCapaciteMessage
						&& tableSousVarDefinition != null
				// && !valeurVarEntetePaquetCourant
				// .equals(valeurArretLecturePaquets)
				) {
					tableSousVarPaquetCourant = tableSousVarDefinition.copy();

					// récupération de la liste des variable
					listeSousVariablePaquetCourant = tableSousVarPaquetCourant
							.getM_AVariableComposant();

					// if (listeSousVariablePaquetCourant != null) {
					// création d'une liste de sous variables temporaire
					// pour modification
					// car on ne peut modifier directement
					// listeSousVariablePaquetCourant

					List<AVariableComposant> listesousVariable2 = new ArrayList<AVariableComposant>();

					listesousVariable2 = renseignerListeSousVariable(
							listeSousVariablePaquetCourant, start, msg, jruData);

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
									+ " " + (goodMsgs.size() + 1 - blockDefaultMessagesCount));
					finMessage = true;
				}
			} else {
				//				System.out.print("   fin de lecture des paquets---"); //$NON-NLS-1$
				//				System.out.print("   longueur totale calculée message courant: " + GestionnaireLongueurMessage.getInstance().getLongueurCalculeeMessageCourant()+"---"); //$NON-NLS-1$
				finMessage = true;
			}

			// Quand un paquet contient le code 255, il s'agit de la fin
			// du message. Les bits suivants doivent être ignorés. CF DR22G
			if (valEntetePrecedente.equals("255")) {
				finMessage = true;
			}

		}
		jruData.lecturePaquetEnCours = false;
		jruData.positionPaquetSuivantUsing_L_Packet = 0;
		// si la variable d'entete du dernier paquet n'est pas égale à la valeur
		// d'arret levée d'une erreur
		// if (!valeurVarEntetePaquetCourant.equals(valeurArretLecturePaquets))
		// SamngLogger.getLogger().error(
		//		Messages.getString("ParseurParcoursJRU.40") + definitionStructureDynamique.getDescriptor().getM_AIdentificateurComposant().getNom() + Messages.getString("ParseurParcoursJRU.41") + valeurArretLecturePaquets + Messages.getString("ParseurParcoursJRU.42") + valeurVarEntetePaquetCourant); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return listePaquets;
	}
	
	public void addMultimediaFile(Message msg, int indexInParcoursList) {
		if (msg.getVariablesDiscrete().size() > 0) {
			VariableDiscrete fileName = msg.getVariablesDiscrete().get(0);
			String strFilename = fileName.toString().trim(); // OPCoach : Warning toString generates spaces at the end. Must trim !!
			File file = new File(RepertoiresAdresses.RepertoireTravail + File.separator + strFilename);
			
			if (file.exists()) {				
				ActivatorData.getInstance().addMultimediaFile(file, msg, indexInParcoursList);
			}
		}
	}
	
	static byte[] removeZeros(byte[] a, int n) throws Exception 
	{ 
	  
	    // index to store the first 
	    // non-zero number 
	    int ind = -1; 
	  
	    // traverse in the array and find the first 
	    // non-zero number 
	    for (int i = 0; i < n; i++) { 
	        if (a[i] != 0) { 
	            ind = i; 
	            break; 
	        } 
	    } 
	  
	    // if no non-zero number is there 
	    if (ind == -1) { 
	    	throw new Exception("Array has leading zeros only");
	    } 
	  
	    // Create an array to store 
	    // numbers apart from leading zeros 
	    byte[] b = new byte[n - ind]; 
	  
	    // store the numbers removing leading zeros 
	    for (int i = 0; i < n - ind; i++) 
	        b[i] = a[ind + i]; 
	  
	    return b; 
	}
}