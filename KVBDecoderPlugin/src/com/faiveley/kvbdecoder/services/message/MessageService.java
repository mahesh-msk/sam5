package com.faiveley.kvbdecoder.services.message;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Service chargé de récupérer les messages dans la bonne langue.
 * 
 * @author jthoumelin
 *
 */
public class MessageService {
	// Clefs des messages d'erreurs
	/// Décodage
	public static final String ERROR_DECODING__XML_DATA_NOT_LOADED = "KVB.erreur.decodage.donneesXmlNonChargees";
	//// Evénement
	public static final String ERROR_EVENT_DECODING__NOT_EXISTING_CODE = "KVB.erreur.decodage.evenement.codeNonExistant";
	public static final String ERROR_EVENT_DECODING__NOT_HANDLED_CODE = "KVB.erreur.decodage.evenement.codeNonGere";
	public static final String ERROR_EVENT_DECODING__INVALID_LENGTH = "KVB.erreur.decodage.evenement.longueurInvalide";
	public static final String ERROR_EVENT_DECODING__INVALID_ANALOG_VARIABLE_CODE = "KVB.erreur.decodage.evenement.codeVariableAnalogiqueInvalide";
	public static final String ERROR_EVENT_DECODING__INVALID_NUMERICAL_VARIABLE_CODE = "KVB.erreur.decodage.evenement.codeVariableNumeriqueInvalide";
	public static final String ERROR_EVENT_DECODING__UNKNOWN_VARIABLE_SIZE = "KVB.erreur.decodage.evenement.tailleVariableInconnue";
	//// Point d'information
	public static final String ERROR_IP_DECODING__INVALID_MARKER_NUMBER = "KVB.erreur.decodage.pointInformation.numeroDeBaliseInvalide";
	public static final String ERROR_IP_DECODING__INVALID_MARKER_NUMBER_VALUE = "KVB.erreur.decodage.pointInformation.valeurNumeroDeBaliseInvalide";
	public static final String ERROR_IP_DECODING__INVALID_SIGNE_SYMBOL = "KVB.erreur.decodage.pointInformation.symboleSigneInvalide";
	public static final String ERROR_IP_DECODING__UNKNOWN_ENCODING = "KVB.erreur.decodage.pointInformation.codageInconnu";
	public static final String ERROR_IP_DECODING__MISSING_UNIT = "KVB.erreur.decodage.pointInformation.uniteAbsente";
	public static final String ERROR_IP_DECODING__INVALID_X_SEQUENCE = "KVB.erreur.decodage.pointInformation.sequenceXInvalide";
	public static final String ERROR_IP_DECODING__INVALID_X_VALUE = "KVB.erreur.decodage.pointInformation.xInvalide";
	public static final String ERROR_IP_DECODING__INVALID_XSM_XCS_VALUE = "KVB.erreur.decodage.pointInformation.xsmxcsInvalide";
	public static final String ERROR_IP_DECODING__MISSING_GROUPING = "KVB.erreur.decodage.pointInformation.groupementAbsent";
	public static final String ERROR_IP_DECODING__MISSING_PRECEDING_MARKER = "KVB.erreur.decodage.pointInformation.balisePrecedenteAbsente";
	public static final String ERROR_IP_DECODING__INVALID_PRECEDING_MARKER = "KVB.erreur.decodage.pointInformation.balisePrecedenteInvalide";
	public static final String ERROR_IP_DECODING__INVALID_S2_MARKER = "KVB.erreur.decodage.pointInformation.baliseS2Invalide";
	public static final String ERROR_IP_DECODING__INVALID_S3_MARKER = "KVB.erreur.decodage.pointInformation.baliseS3Invalide";
	public static final String ERROR_IP_DECODING__INVALID_VARIABLE_TO_CONSIDER = "KVB.erreur.decodage.pointInformation.variableAConsidererInvalide";
	/// Catégorie de train
	public static final String ERROR_TC_DECODING__MISSING_LIMIT_SPEED = "KVB.erreur.decodage.categorieTrain.vitesseLimiteAbsente";
	public static final String ERROR_TC_DECODING__MISSING_LTE_CATEGORY = "KVB.erreur.decodage.categorieTrain.categorieVitesseInferieureOuEgaleAbsente";
	public static final String ERROR_TC_DECODING__MISSING_GT_CATEGORY = "KVB.erreur.decodage.categorieTrain.categorieVitesseSuperieureAbsente";
	
	/// Chargement xml
	public static final String ERROR_XMLLOADING_MISSING_SIGNATURE_TAG = "KVB.erreur.chargementXML.signatureMissing";
	public static final String ERROR_XMLLOADING_MISSING_CRC_ATTRIBUTE = "KVB.erreur.chargementXML.crcMissing";
	public static final String ERROR_XMLLOADING_INVALID_CRC_ATTRIBUTE = "KVB.erreur.chargementXML.crcInvalide";
	public static final String WARNING_XMLLOADING_NOTMATCHING_CRC = "KVB.warning.chargementXML.crcNotMatching";
	//// Atess
	public static final String ERROR_XMLLOADING_ATESS__INVALID_EVENT_ATTRIBUTE_CODE = "KVB.erreur.chargementXML.atess.codeEvenementInvalide";
	public static final String ERROR_XMLLOADING_ATESS__INVALID_VARIABLE_ATTRIBUTE_CODE = "KVB.erreur.chargementXML.atess.codeVariableInvalide";
	public static final String ERROR_XMLLOADING_ATESS__INVALID_VARIABLE_ATTRIBUTE_TAILLE = "KVB.erreur.chargementXML.atess.tailleVariableInvalide";
	public static final String ERROR_XMLLOADING_ATESS__INVALID_EVENT_ATTRIBUTE_LONGUEUR = "KVB.erreur.chargementXML.atess.longueurEvenementInvalide";
	public static final String ERROR_XMLLOADING_ATESS__INVALID_EXTENDEDID_ATTRIBUTE_NBOCTETS = "KVB.erreur.chargementXML.atess.nombreOctetsIdentifantEtenduInvalide";
	//// KVB
	public static final String ERROR_XMLLOADING_KVB__INVALID_XSM_XCS_ATTRIBUTE_VALEUR = "KVB.erreur.chargementXML.kvb.xsmxcsInvalide";
	public static final String ERROR_XMLLOADING_KVB__INVALID_CATEGORIE_ATTRIBUTE_VITESSELIMITE = "KVB.erreur.chargementXML.kvb.vitesseLimiteCategorieInvalide";
	
	/// Localisation
	public static final String WARNING_LOCALISATION_UNKNOWN_KEY = "KVB.warning.localisation.clefInconnue";
	
	private static final String CURRENT_FOLDER = ".";
	private static final String MESSAGES_FILE_BODY = "messages";
	private static final String MESSAGES_FILE_SEPARATOR = "_";
	private static final String DEFAULT_LANGUAGE = "DEF"; // La langue par défaut
	private String language = null; // La langue à utiliser au cours de l'exécution du programme
	
	/**
	 * Le singleton
	 */
	private static MessageService SERVICE_INSTANCE = new MessageService();
	
	/**
	 * Le constructeur vide
	 */
	private MessageService() {}
	
	/**
	 * Obtention du singleton
	 */
	public static MessageService getServiceInstance() {
		return SERVICE_INSTANCE;
	}
	
	public String getServiceLanguage() {
		if (language == null) {
			language = DEFAULT_LANGUAGE;
		}
		
		return language;
	}
	
	public void setServiceLanguage(String language) {
		this.language = language;
	}
	
	/**
	 * Pour une clef donnée, renvoie le message correspondant dans la bonne langue
	 * 
	 * @param key : la clef
	 * @param className : le nom de la classe qui a appelé le service
	 * @return
	 */
	public String getString(final String key, final Class<?> className) {		
		try {
			return getStringIfPossible(key, className);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	public String getStringIfPossible(final String key,  final Class<?> className) {
		ResourceBundle RESOURCE_BUNDLE = getBundle(className);
		return RESOURCE_BUNDLE.getString(key);
	}
	
	private ResourceBundle getBundle(final Class<?> className) {
		String language = getServiceLanguage();
		String bundleName = String.format("%s%s", className.getPackage().getName(), language.equals(DEFAULT_LANGUAGE) ? String.format("%s%s", CURRENT_FOLDER, MESSAGES_FILE_BODY) : String.format("%s%s%s%s", CURRENT_FOLDER, MESSAGES_FILE_BODY, MESSAGES_FILE_SEPARATOR, language));		
		return ResourceBundle.getBundle(bundleName);
	}
}