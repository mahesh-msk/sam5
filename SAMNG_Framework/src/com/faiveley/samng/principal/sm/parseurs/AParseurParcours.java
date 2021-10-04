package com.faiveley.samng.principal.sm.parseurs;

import java.util.ArrayList;
import java.util.HashMap;

import com.faiveley.samng.principal.sm.data.descripteur.ADescripteurComposant;
import com.faiveley.samng.principal.sm.data.descripteur.donneeBlocComposant.ADonneeBlocComposant;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.erreurs.BadHeaderInfoException;
import com.faiveley.samng.principal.sm.erreurs.BadStructureFileException;
import com.faiveley.samng.principal.sm.erreurs.ParseurBinaireException;
import com.faiveley.samng.principal.sm.parseurs.parseursJRU.CRC_Erreur;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

public abstract class AParseurParcours extends ParseurParcoursBinaire {
	protected HashMap<Integer, Boolean> crcEnregistrements;

	protected double derniereValeurHeure = 0;

	protected double derniereValeurDate = 0;

	protected double derniereValeurCptTemps = 0;

	protected double cumulTempsMax = 0;

	protected double cumulTemps = 0;

	protected double lastCptTempsTimeChange = 0;

	protected String nomFichierBinaire = "";

	protected boolean premiereDateRencontree = false;

	protected double cumulDistanceMax;

	protected double cumulDistance;

	protected double derniereValeurCptDistance;

	protected int nbBloc = 0;

	protected int idParcours = 0;

	protected static String CHEMIN_FICHIER_IDCONFIG = RepertoiresAdresses.IdConfig_PROPERTIES;

	protected ArrayList<CRC_Erreur> listCRC_Error = new ArrayList<CRC_Erreur>(0);

	public int getIdParcours() {
		return idParcours;
	}

	public void setIdParcours(int idParcours) {
		this.idParcours = idParcours;
	}

	public int getNbBloc() {
		return nbBloc;
	}

	public void setNbBloc(int nbBloc) {
		this.nbBloc = nbBloc;
	}

	public AParseurParcours() {

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
	protected abstract ADescripteurComposant chargerDescripteursCodeBloc()
			throws BadHeaderInfoException;

	public abstract void clear();

	/**
	 * charge l'ensemble des messages et les place dans un objet Data
	 * 
	 * Returns 2 arrays with messages: first with good messages, second with bad
	 * massages
	 */
	protected abstract Message[][] chargerData(int deb, int fin)
			throws ParseurBinaireException;

	/**
	 * charge l'ensemble uniquement les messages dont l'exploration à besoin
	 * pour créer les missions et les regroupements de temps
	 * 
	 */
	protected void chargerDataExplore() throws ParseurBinaireException {
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
	protected abstract boolean verifierCRCBloc(int crc, byte[] donneesBloc);

	/**
	 * Sets the flag loaded from the file and calculates the rupture of time and
	 * distance
	 * 
	 * @param crtMsg
	 *            crurrent message
	 * @throws ParseurBinaireException
	 *             if any exception occured
	 */
	protected abstract void setFlags(Message crtMsg)
			throws ParseurBinaireException;

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
	protected abstract byte[] setVariableValue(AVariableComposant var,
			byte[] valueOrig) throws BadStructureFileException;

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
	protected abstract long setXbitsValue2(AVariableComposant v, byte[] value,
			int posVar, int startBitIdx);

	/**
	 * Gets a number of bits from the given byte
	 * 
	 * @param crtByte
	 *            the byte to extract the bits
	 * @param offset
	 *            the offset to start from
	 * @param length
	 *            the number of bits to extract
	 * @return in a byte only the bits extracted
	 */
	protected abstract byte getBits(byte crtByte, int offset, int length);

	/**
	 * Loads the association table of events and variables
	 * 
	 * @return the assoctiation table as a parcours
	 * @throws ParseurBinaireException
	 *             throws an exception if any occurs
	 */
	protected abstract ADescripteurComposant chargerTableEvtVariable()
			throws ParseurBinaireException;

	/**
	 * Calculates the time segments
	 * 
	 * @param crtMsg
	 *            current message
	 * @param prevMsg
	 *            previous message
	 */
	protected abstract void setSegmentsTemp(Message crtMsg, Message prevMsg);

	/**
	 * Checks if there is the corect number of FF bytes
	 * 
	 * @param start
	 *            the start position
	 * @param noFF
	 *            number of bytes of FF
	 * @return true if the bytes contains only FF, false otherwise
	 */
	protected abstract boolean checkOctetsFF(int start, int noFF);

	protected abstract boolean verifierCRCConfiguration();

	@Override
	protected abstract AVariableComposant chargerReperes();

	/**
	 * charge le CRC enregistré de l'entete dans un objet ADonneeBlocComposant
	 */
	protected abstract ADonneeBlocComposant chargerCRCConfiguration();

	public abstract HashMap<Integer, Boolean> getCrcEnregistrements();

	public abstract void setCrcEnregistrements(
			HashMap<Integer, Boolean> crcEnregistrements);

	/**
	 * Méthode de récupération du nom du fichier xml à charger
	 * 
	 * @param nomFichier
	 * @return nom du fichier xml contenant le type de parseur à utiliser
	 * @throws Exception
	 */
	public abstract String getNomFichierXml(String nomFichier) throws Exception;

	public double getCumulDistance() {
		return cumulDistance;
	}

	public void setCumulDistance(double cumulDistance) {
		this.cumulDistance = cumulDistance;
	}

	public double getCumulDistanceMax() {
		return cumulDistanceMax;
	}

	public void setCumulDistanceMax(double cumulDistanceMax) {
		this.cumulDistanceMax = cumulDistanceMax;
	}

	public double getCumulTemps() {
		return cumulTemps;
	}

	public void setCumulTemps(double cumulTemps) {
		this.cumulTemps = cumulTemps;
	}

	public double getCumulTempsMax() {
		return cumulTempsMax;
	}

	public void setCumulTempsMax(double cumulTempsMax) {
		this.cumulTempsMax = cumulTempsMax;
	}

	public double getDerniereValeurCptDistance() {
		return derniereValeurCptDistance;
	}

	public void setDerniereValeurCptDistance(double derniereValeurCptDistance) {
		this.derniereValeurCptDistance = derniereValeurCptDistance;
	}

	public double getDerniereValeurCptTemps() {
		return derniereValeurCptTemps;
	}

	public void setDerniereValeurCptTemps(double derniereValeurCptTemps) {
		this.derniereValeurCptTemps = derniereValeurCptTemps;
	}

	public double getDerniereValeurDate() {
		return derniereValeurDate;
	}

	public void setDerniereValeurDate(double derniereValeurDate) {
		this.derniereValeurDate = derniereValeurDate;
	}

	public double getDerniereValeurHeure() {
		return derniereValeurHeure;
	}

	public void setDerniereValeurHeure(double derniereValeurHeure) {
		this.derniereValeurHeure = derniereValeurHeure;
	}

	public boolean isPremiereDateRencontree() {
		return premiereDateRencontree;
	}

	public void setPremiereDateRencontree(boolean premiereDateRencontree) {
		this.premiereDateRencontree = premiereDateRencontree;
	}

	public double getLastCptTempsTimeChange() {
		return lastCptTempsTimeChange;
	}

	public void setLastCptTempsTimeChange(double lastCptTempsTimeChange) {
		this.lastCptTempsTimeChange = lastCptTempsTimeChange;
	}
}
