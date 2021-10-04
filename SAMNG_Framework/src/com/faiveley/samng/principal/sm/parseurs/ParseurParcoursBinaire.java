package com.faiveley.samng.principal.sm.parseurs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;

import com.faiveley.samng.principal.logging.SamngLogger;
import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.controles.ReturnCRC;
import com.faiveley.samng.principal.sm.controles.util.CRC16CCITTHash;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurComposite;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.flag.Flag;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.AParcoursComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.erreurs.AExceptionSamNG;
import com.faiveley.samng.principal.sm.erreurs.InconsistentFileException;
import com.faiveley.samng.principal.sm.erreurs.ParseurBinaireException;
import com.faiveley.samng.principal.sm.fabriques.AFabriqueParcoursAbstraite;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.principal.sm.segments.SegmentDistance;
import com.faiveley.samng.principal.sm.segments.SegmentTemps;
import com.faiveley.samng.principal.sm.segments.TableSegments;
import com.faiveley.samng.principal.sm.segments.ruptures.TableRuptures;
import com.faiveley.samng.principal.sm.segments.ruptures.TypeRupture;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 28-août-2008 10:34:10
 */
public abstract class ParseurParcoursBinaire implements IParseurInterface {

	private int cptMsgSegment = 0;
	private int _max_messages_mission_ = -1;
	
	protected IProgressMonitor monitor;

	protected static final byte[] cacheMemory = new byte[20]; // cache memory

	protected static int FIN_CODE = 0xFF;

	// the only instance created. The parser is a singleton
	protected static ParseurParcoursBinaire instance;

	// flags
	protected HashMap<String, Flag> loadedFlags;

	protected HashMap<String, Flag> exLoadedFlags;

	// a conversion temp util
	protected ConversionTemps tc;

	// last message, distance segment, time segment
	protected SegmentDistance lastSegDistance;

	protected SegmentTemps lastSegTemp;

	protected Message lastMessage;

	protected boolean dateValide = true;

	protected double lastDateValue = 0;

	// the binary file loaded in a byte[]
	protected byte[] message;
	protected byte[] messagebrut;

	protected String currentFileName = null;

	protected DescripteurComposite descrTable = null;

	protected CRC16CCITTHash crc;


	public ParseurParcoursBinaire() {
	}

	public void setMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	public void clear() {
		// flags
		if (loadedFlags != null) {
			loadedFlags.clear();
		}

		if (exLoadedFlags != null) {
			exLoadedFlags.clear();
		}

		// a conversion temp util
		tc = null;

		// last message, distance segment, time segment
		lastSegDistance = null;
		lastSegTemp = null;
		lastMessage = null;
		dateValide = true;
		lastDateValue = 0;

		// the binary file loaded in a byte[]
		message = null;
		currentFileName = null;
		instance = null;

	}

	/**
	 * Returns the instance of the parser
	 * 
	 */
	public static ParseurParcoursBinaire getInstance() {
		return instance;
	}

	// //////////////////////////////////////////////////
	/**
	 * charge l'ensemble des messages et les place dans un objet Data
	 * 
	 * Returns 2 arrays with messages: first with good messages, second with bad
	 * massages
	 */
	protected abstract Message[][] chargerData(int deb, int fin)
			throws ParseurBinaireException;

	// /////////////////////////////////////////////

	// //////////////////////////////////////////////////
	/**
	 * Crée uniquement les segments de temps nécessaires à la création des
	 * missions
	 */
	// /////////////////////////////////////////////
	protected abstract void chargerDataExplore() throws ParseurBinaireException;

	/**
	 * cahrger les données de l'entete dans un objet Entete
	 * 
	 * @throws AExceptionSamNG
	 */
	protected abstract AParcoursComposant chargerEntete()
			throws AExceptionSamNG;

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
	protected abstract Message chargerMessage(int start, boolean isExploration)
			throws ParseurBinaireException;
	
	/**
	 * retourne les repères du parcours: temps, distance, vitesse, date
	 */
	protected abstract AVariableComposant chargerReperes();

	/**
	 * Loads the binary file is an array of bytes
	 * 
	 * @param fileName
	 *            the name of the file to load
	 * @param explorer
	 *            si true : c'est pour l'action explorer de la vue des missions
	 *            , sinon c'est pour un chargement de fichier normal
	 * @return the array of bytes loaded
	 */
	public abstract void parseRessource(String fileName, boolean explorer,
			int deb, int fin) throws AExceptionSamNG;

	/**
	 * Loads the binary file is an array of bytes
	 * 
	 * @param fileName
	 *            the name of the file to load
	 * @return the array of bytes loaded
	 */
	protected byte[] loadBinaryFile(File f, int deb, int fin) {
		byte[] bytes = null;
		InputStream is = null;
		try {
			is = new FileInputStream(f);

			// Get the size of the file
			int length = (int) f.length();
			if (fin != -1) {
				length = fin - deb;
			}

			// Create the byte array to hold the data
			bytes = new byte[length];

			// Read in the bytes
			int offset = deb;

			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}

			// Ensure all the bytes have been read in
			if (offset < bytes.length) {
				throw new IOException("Could not completely read file "
						+ f.getName());
			}

		} catch (FileNotFoundException e) {
			SamngLogger.getLogger().warn(e.getMessage()); //$NON-NLS-1$
		} catch (IOException e) {
			SamngLogger.getLogger().warn(e.getMessage()); //$NON-NLS-1$
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return bytes;
	}

	protected abstract void traitementFichier(
			AFabriqueParcoursAbstraite factory, String fileName,
			boolean explorer) throws AExceptionSamNG;

	/**
	 * Calculates the time segments
	 * 
	 * @param crtMsg
	 *            current message
	 * @param prevMsg
	 *            previous message
	 * @param fin
	 * @param deb
	 */
	protected void setSegmentsTemp(Message crtMsg, Message prevMsg) {
		
		if (_max_messages_mission_ == -1)
			_max_messages_mission_ = getMaxMessages();
	
		if (crtMsg != null) {			
		
			if (++cptMsgSegment > _max_messages_mission_) {
							
				if (this.lastSegTemp != null) {
					enregistrerSegmentTemps(prevMsg);
				}
				
				SegmentTemps segment = new SegmentTemps();
				cptMsgSegment = 1;
				segment.setOffsetDebut(crtMsg.getOffsetDebut());
				segment.setStartMsgId(crtMsg.getMessageId());
				segment.setTempInitial(this.tc.getCurrentDateAsMillis());
				segment.setTempCorrige(segment.getTempInitial());
				this.lastSegTemp = segment;
				segment = null;
			}
			// si l'événement est un événement de rupture d'acquisition
			else if (crtMsg.getEvenement().isRuptureAcquisition() || prevMsg == null) {
				// si on est sur les messages de 2 à n
				if (prevMsg != null) {
					// si un segment a déjà été créé on teste si l'événement
					// précédent n'est pas le début du segment
					// si c'est le cas on enregistre le segment et on en crée un autre
					if (this.lastSegTemp != null) {
						if (prevMsg.getMessageId() != lastSegTemp.getStartMsgId()) {
								
							TableRuptures.getInstance().ajouterRuptureTemps(crtMsg.getMessageId(), TypeRupture.RUPTURE_TOTALE_TEMPS_ABSOLU);
							TableRuptures.getInstance().ajouterRuptureDistance(crtMsg.getMessageId(), TypeRupture.RUPTURE_TOTALE_DISTANCE_ABSOLU);
								
							enregistrerSegmentTemps(prevMsg);
							
							// a new segment starts
							SegmentTemps segment = new SegmentTemps();
							cptMsgSegment = 1;
							segment.setOffsetDebut(crtMsg.getOffsetDebut());
							segment.setStartMsgId(crtMsg.getMessageId());
							segment.setTempInitial(this.tc.getCurrentDateAsMillis());
							segment.setTempCorrige(segment.getTempInitial());
							this.lastSegTemp = segment;
							segment = null;
						}
					}
				}
				// on est sur le message 1: on crée un segment
				else {
					// a new segment starts
					SegmentTemps segment = new SegmentTemps();
					segment.setOffsetDebut(crtMsg.getOffsetDebut());
					segment.setStartMsgId(crtMsg.getMessageId());
					segment.setTempInitial(this.tc.getCurrentDateAsMillis());
					segment.setTempCorrige(segment.getTempInitial());
					this.lastSegTemp = segment;
					segment = null;
				}
			}
			else if (prevMsg != null) {

				// check if is a time rupture

				boolean isTimeEvtChgt = prevMsg.getEvenement().isChangementHeure();

				if (isTimeEvtChgt) {
					// if a segment not closed, close it now
					if (this.lastSegTemp != null) {
						enregistrerSegmentTemps(prevMsg);
					}

					TableRuptures.getInstance().ajouterRuptureTemps(crtMsg.getMessageId(), TypeRupture.CHANGEMENT_HEURE_RUPTURE);

					// a new segment starts
					SegmentTemps segment = new SegmentTemps();
					cptMsgSegment = 1;
					segment.setOffsetDebut(crtMsg.getOffsetDebut());
					segment.setStartMsgId(crtMsg.getMessageId());
					segment.setTempInitial(this.tc.getCurrentDateAsMillis());
					segment.setTempCorrige(segment.getTempInitial());
					this.lastSegTemp = segment;
					segment = null;
				}
				else if (crtMsg.getAbsoluteTime() < prevMsg.getAbsoluteTime() && !prevMsg.getEvenement().isChangementHeure()) {
						
					TableRuptures.getInstance().ajouterRuptureTemps(crtMsg.getMessageId(),	TypeRupture.RUPTURE_TEMP_CALCULEE);
						
					enregistrerSegmentTemps(prevMsg);

					// a new segment starts
					SegmentTemps segment = new SegmentTemps();
					cptMsgSegment = 1;
					segment.setOffsetDebut(crtMsg.getOffsetDebut());
					segment.setStartMsgId(crtMsg.getMessageId());
					segment.setTempInitial(this.tc.getCurrentDateAsMillis());
					segment.setTempCorrige(segment.getTempInitial());
					this.lastSegTemp = segment;
					segment = null;
					
				} 
				else if (prevMsg.isMessageIncertitude() && !crtMsg.isMessageIncertitude()) {
					
					enregistrerSegmentTemps(prevMsg);
					
					// a new segment starts
					SegmentTemps segment = new SegmentTemps();
					cptMsgSegment = 1;
					segment.setOffsetDebut(crtMsg.getOffsetDebut());
					segment.setStartMsgId(crtMsg.getMessageId());
					segment.setTempInitial(this.tc.getCurrentDateAsMillis());
					segment.setTempCorrige(segment.getTempInitial());
					this.lastSegTemp = segment;
					segment = null;
				}
			}			
		} 
		// close the segment
		else if (this.lastSegTemp != null) {
			enregistrerSegmentTemps(prevMsg);
		}
	}
	
	// Méthode permettant d'acquérir du fichier
	// AppData\Roaming\Faiveley Transport\SAM X.X.X.bXX\ressources\bridage\missions.properties
	// le paramètre max_messages_mission_tomng. Ce paramètre permet de donner une
	// limite, en nombre de message, à la taille max d'une mission.
	public static int getMaxMessages() {
		String max_messages_mission = "10000"; //$NON-NLS-1$
		int max_messages_mission_ = 10000;
		
		try {
			FileInputStream inStream;
			String cheminFichiermissions_PROPERTIES = RepertoiresAdresses.missions_PROPERTIES;
			inStream = new FileInputStream(new File(cheminFichiermissions_PROPERTIES)); //$NON-NLS-1$
			Properties props = new Properties();
			props.load(inStream);
			max_messages_mission = (String) props.get("max_messages_mission_tomng"); //$NON-NLS-1$
			
			if (max_messages_mission != null) {
				max_messages_mission_ = Integer.valueOf(max_messages_mission);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return max_messages_mission_;
	}

	/**
	 * 
	 * Calculates the time segments
	 * 
	 * @param crtMsg
	 *            current message
	 * @param prevMsg
	 *            previous message
	 * @throws InconsistentFileException
	 *             throws exception if any occured
	 */
	protected void setSegmentsDistance(Message crtMsg, Message prevMsg)
			throws InconsistentFileException {

		if (crtMsg != null) {

			// on défini la validité ou non d'un segment distance
			// un segment distance est valide :
			// - si la varaible diametre ne change pas de valeur dans ce segment
			// - si la varaible diametre > 0
			double crtDiametre = crtMsg.getDiametreRoue();
			if (this.lastSegDistance != null) {
				// if the current diametre is not null check if the segment
				// has a diametre set or is different that the current one
				double diametreRoue = this.lastSegDistance.getInitialDiameter();
				if (diametreRoue != 0) {
					this.lastSegDistance
							.setValide(crtDiametre > 0 ? diametreRoue == crtDiametre
									: true);

				} else if (crtDiametre > 0) {
					// diamtre was not set on the segment then set it now
					diametreRoue = crtDiametre;
					this.lastSegDistance.setInitialDiameter(diametreRoue);
					this.lastSegDistance.setDiameterCorrige(diametreRoue);
					this.lastSegDistance.setValide(true);
				}

			}

			// si c'est le premier événement on créé un segment distance
			if (prevMsg == null) {

				// create a new segment
				SegmentDistance segment = new SegmentDistance();
				segment.setStartMsgId(crtMsg.getMessageId());
				segment.setInitialDiameter(crtDiametre);
				segment.setDiameterCorrige(crtDiametre);
				segment.setInitialTime(this.tc.getFormatedTime());
				// segment.setInitialTime(ConversionTemps.getFormattedDate(crtMsg
				// .getAbsoluteTime()));
				if (crtMsg.getVariable(TypeRepere.diametreRoue.getCode()) != null)
					segment.setValide(true);
				else
					segment.setValide(false);
				this.lastSegDistance = segment;

			} else {
				// on vérifie si il y a une rupture de distance calculée
				if (prevMsg != null) {
					// cas ou le message courant n'est pas une rupture
					// d'acquisition
					if (!crtMsg.getEvenement().isRuptureAcquisition()) {
						if (crtMsg.getAbsoluteDistance() < prevMsg
								.getAbsoluteDistance()
								&& !prevMsg.getEvenement().isChangementHeure()) {

							double diam = 0;
							try {
								if (this.lastSegDistance != null) {
									if (this.lastSegDistance
											.getInitialDiameter() > 0) {
										diam = this.lastSegDistance
												.getInitialDiameter();
									}
								}
							} catch (Exception e) {
								// TODO: handle exception
							}

							TableRuptures.getInstance().ajouterRuptureDistance(
									crtMsg.getMessageId(),
									TypeRupture.RUPTURE_DIST_CALCULEE);
							// enregistrerSegmentDistance(prevMsg);
							//
							// ///////////////////////////////////////////////////////////////
							// // create a new segment
							// SegmentDistance segment = new SegmentDistance();
							// segment.setStartMsgId(crtMsg.getMessageId());
							//
							// if (diam>0) {
							// segment.setInitialDiameter(diam);
							// segment.setDiameterCorrige(diam);
							// }else{
							// segment.setInitialDiameter(crtDiametre);
							// segment.setDiameterCorrige(crtDiametre);
							// }
							//
							//
							//
							// segment
							// .setInitialTime(ConversionTemps
							// .getFormattedDate(crtMsg
							// .getAbsoluteTime()));
							// if (crtMsg.getVariable(TypeRepere.diametreRoue
							// .getCode()) != null)
							// segment.setValide(true);
							// else
							// segment.setValide(false);
							// this.lastSegDistance = segment;
							// ///////////////////////////////////////////////////////////////

						}
					}
					// cas ou l'on a une rupture d'acquisition
					else {
						if (!prevMsg.getEvenement().isRuptureAcquisition()) {
							// on ferme le segment distance courant
							enregistrerSegmentDistance(prevMsg);

							// on créé un nouveau segment distance
							// create a new segment
							SegmentDistance segment = new SegmentDistance();
							segment.setStartMsgId(crtMsg.getMessageId());
							segment.setInitialDiameter(crtDiametre);
							segment.setDiameterCorrige(crtDiametre);
							segment.setInitialTime(this.tc.getFormatedTime());
							// segment
							// .setInitialTime(ConversionTemps
							// .getFormattedDate(crtMsg
							// .getAbsoluteTime()));
							if (crtMsg.getVariable(TypeRepere.diametreRoue
									.getCode()) != null)
								segment.setValide(true);
							else
								segment.setValide(false);
							this.lastSegDistance = segment;
						} else {
							// gestion des cas suivants :
							// - on a un power on(prevMsg) et ensuite un
							// recordingstart(crtMsg): on ne doit alors pas
							// fermer le segment distance et en ouvrir un autre
							// - on a deux recordingstart : on doit fermer le
							// segment distance et en ouvrir un autre
							if (prevMsg.getEvenement()
									.getM_ADescripteurComposant()
									.getM_AIdentificateurComposant().getCode() == crtMsg
									.getEvenement()
									.getM_ADescripteurComposant()
									.getM_AIdentificateurComposant().getCode()) {
								// on ferme le segment distance courant
								enregistrerSegmentDistance(prevMsg);

								// on créé un nouveau segment distance
								// create a new segment
								SegmentDistance segment = new SegmentDistance();
								segment.setStartMsgId(crtMsg.getMessageId());
								segment.setInitialDiameter(crtDiametre);
								segment.setDiameterCorrige(crtDiametre);
								segment.setInitialTime(ConversionTemps
										.getFormattedDate(
												crtMsg.getAbsoluteTime(), true));
								if (crtMsg.getVariable(TypeRepere.diametreRoue
										.getCode()) != null)
									segment.setValide(true);
								else
									segment.setValide(false);
								this.lastSegDistance = segment;
							}

						}
					}

				}
			}

		} else {
			// lorsque l'on est à la fin du fichier crtMsg == null
			// on teste si le dernier segment créé est valide et on le ferme
			double prevDiametre = prevMsg.getDiametreRoue();
			if (this.lastSegDistance != null) {
				double diametreRoue = this.lastSegDistance.getInitialDiameter();
				if (diametreRoue != 0) {
					this.lastSegDistance
							.setValide(prevDiametre != 0 ? diametreRoue == prevDiametre
									: true);

				} else if (prevDiametre > 0) {
					diametreRoue = prevDiametre;
					this.lastSegDistance.setInitialDiameter(diametreRoue);
					this.lastSegDistance.setDiameterCorrige(diametreRoue);
				}
				enregistrerSegmentDistance(prevMsg);
			}

		}
	}

	/**
	 * Save a distance segment
	 * 
	 * @param msg
	 *            the current message
	 * @throws InconsistentFileException
	 *             throws if diametre not valid
	 */
	protected void enregistrerSegmentDistance(Message msg)
			throws InconsistentFileException {
		// check if valid segment
		boolean isValid = this.lastSegDistance.isValide() ? this.lastSegDistance
				.getInitialDiameter() > 0 : false;
		this.lastSegDistance.setValide(isValid);
		this.lastSegDistance.setEndTime(this.tc.getFormatedTime());
		this.lastSegDistance.setEndMsgId(msg.getMessageId());

		// save the segment
		TableSegments.getInstance()
				.ajouterSegmentDistance(this.lastSegDistance);

		this.lastSegDistance = null;
	}

	/**
	 * Saves the time segment
	 * 
	 * @param msg
	 *            current message
	 * @param fin
	 * @param deb
	 */
	protected void enregistrerSegmentTemps(Message msg) {
		// set the final time and message id
		this.lastSegTemp.setTempFinal(msg.getAbsoluteTime());
		this.lastSegTemp.setOffsetFin(msg.getOffsetFin());
		this.lastSegTemp.setEndMsgId(msg.getMessageId());

		// save segment
		TableSegments.getInstance().ajouterSegmentTemps(this.lastSegTemp);

		this.lastSegTemp = null;
	}

	/**
	 * Clears all the data stored on parser
	 * 
	 */
	protected void resetParser() {
		this.lastMessage = null;
		this.lastSegDistance = null;
		this.lastDateValue = -1;

		this.message = null;

		this.tc = null;
		this.lastSegTemp = null;
	}

	/**
	 * Reverses the bits
	 * 
	 * @param bArr
	 *            the bits to be reversed
	 * @return the bits reversed
	 */
	protected static byte[] reverseBits(byte[] bArr) {
		// 0x12 0x34 0x56 0x78 0x90 0x12 0x34 0x56 = > in binary
		// 0001 0010 0011 0100 0101 0110 0111 1000 1001 0000 0001 0010 0011 0100
		// 0101 0110 => reversed
		// 0100 1000 0010 1100 0110 1010 0001 1110 0000 1001 0100 1000 0010 1100
		// 0110 1010
		byte[] bArrRev = new byte[bArr.length];
		boolean negativ = false;
		for (int i = 0; i < bArr.length; i++) {
			negativ = false;
			byte[] bits = new byte[8];
			if (bArr[i] < 0) {
				bArr[i] = (byte) (~bArr[i]);
				negativ = true;
			}
			for (int k = 0; k < 8; k++) {
				double power = Math.pow(2, 8 - 1 - k);
				bits[k] = (byte) (bArr[i] / power);
				bArr[i] = (byte) (bArr[i] % power);

			}

			int val = 0;
			for (int k = 0; k < 8; k++) {
				val += (bits[k] * Math.pow(2, k));
			}
			bArrRev[i] = (byte) (val & 0x00ff);
			if (negativ) {
				bArrRev[i] = (byte) (~bArrRev[i]);
			}

		}

		return bArrRev;
	}

	/**
	 * Reverses the octets
	 * 
	 * @param bArr
	 *            the byte[] to be reversed
	 * @return the reversed byte[]
	 */
	protected static byte[] reverseOctets(byte[] bArr) {
		// 0x12 0x34 0x56 0x78 0x90 0x12 0x34 0x56 => 0x56 0x34 0x12 0x90 0x78
		// 0x56 0x34 0x12
		byte[] bArrRev = new byte[bArr.length];
		for (int i = 0; i < bArr.length; i++) {
			bArrRev[bArr.length - 1 - i] = bArr[i];
		}
		return bArrRev;
	}

	public abstract String getNomFichierXml(String fileName) throws Exception;

	public byte[] getMessage() {
		return message;
	}

	protected static ReturnCRC getMessageCRCs(int crc2, byte[] crtMessage,
			CRC16CCITTHash crc) {

		crc.add(crtMessage, 0, crtMessage.length);
		byte[] result = crc.get();

		byte crcByte0 = (byte) (crc2 & 0xFF);
		byte crcByte1 = (byte) ((crc2 >> 8) & 0xFF);

		byte[] monCrc = new byte[2];
		monCrc[0] = crcByte1;
		monCrc[1] = crcByte0;

		ReturnCRC crcs = new ReturnCRC();
		crcs.setMessageCRC(monCrc);
		crcs.setCalculCRC(result);

		return crcs;
	}

	public byte[] getMessagebrut() {
		return messagebrut;
	}

}