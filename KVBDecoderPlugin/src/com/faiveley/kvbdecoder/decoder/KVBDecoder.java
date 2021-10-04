package com.faiveley.kvbdecoder.decoder;

import org.json.JSONArray;
import org.json.JSONObject;

import com.faiveley.kvbdecoder.exception.EventException;
import com.faiveley.kvbdecoder.exception.InformationPointException;
import com.faiveley.kvbdecoder.exception.decoder.EventDecoderException;
import com.faiveley.kvbdecoder.exception.decoder.TrainCategoryDecoderException;
import com.faiveley.kvbdecoder.exception.model.train.TrainCategoryEnumException;
import com.faiveley.kvbdecoder.exception.model.train.TrainDirectionEnumException;
import com.faiveley.kvbdecoder.exception.xml.XMLException;
import com.faiveley.kvbdecoder.model.atess.event.DescriptorEvent;
import com.faiveley.kvbdecoder.model.kvb.event.Event;
import com.faiveley.kvbdecoder.model.kvb.train.TrainData;
import com.faiveley.kvbdecoder.services.decoder.DecoderService;
import com.faiveley.kvbdecoder.services.json.JSONService;
import com.faiveley.kvbdecoder.services.loader.KVBLoaderService;
import com.faiveley.kvbdecoder.services.loader.KVBLoaderService.TrainInformation;
import com.faiveley.kvbdecoder.services.message.MessageService;
import com.faiveley.kvbdecoder.services.xml.XMLService;
import com.faiveley.kvbdecoder.services.xml.sam.CRC16Xml.CrcValue;

/**
 * Décodeur KVB.
 * Service qui réunit les méthodes appelables de l'extérieur.
 * 
 * @author jthoumelin
 *
 */
public class KVBDecoder {	
	/**
	 * Le singleton
	 */
	private static KVBDecoder DECODER_INSTANCE = new KVBDecoder();
	
	/**
	 * Le constructeur vide
	 */
	private KVBDecoder() {}
	
	/**
	 * Obtention du singleton
	 */
	public static KVBDecoder getDecoderInstance() {
		return DECODER_INSTANCE;
	}
	
	/*************************************************************************************************/	
	
	/**
	 * Méthode appelée par SAM, et indirectement par ADS : décode l'événement
	 * 
	 * @param encodedEvent : l'événement codé en binaire
	 * @param trainData : les informations sur le train, au format JSON
	 * @return KVBResult : l'événement décodé + une éventuelle erreur bloquante
	 */
	public KVBDecoderResult decodeEvent(byte[] encodedEvent, TrainData trainData) {
		KVBDecoderResult result = new KVBDecoderResult();
				
		try {
			if (XMLService.getServiceInstance().isDataSuccessfullyLoaded()) {
				int eventCode = DecoderService.getServiceInstance().getEventCode(encodedEvent);
				DescriptorEvent eventDescriptor = DecoderService.getServiceInstance().getEventDescriptor(eventCode);
								
				if (eventDescriptor != null) {
					if(Event.isKVBEvent(eventCode)) {
						result.setEvent(DecoderService.getServiceInstance().buildEvent(encodedEvent, trainData, eventDescriptor));
					} else {
						throw new EventDecoderException(MessageService.ERROR_EVENT_DECODING__NOT_HANDLED_CODE, String.valueOf(eventCode));
					}
				} else {
					throw new EventDecoderException(MessageService.ERROR_EVENT_DECODING__NOT_EXISTING_CODE, String.valueOf(eventCode));
				}
			} else {
				throw new EventDecoderException(MessageService.ERROR_DECODING__XML_DATA_NOT_LOADED);
			}
		} catch (EventException e) {
			result.setEvent(null);
			result.setEventError(e);
			return result;
		}
		
		return result;
	}
	
	/**
	 * Méthode appelée par ADS
	 * 
	 * @param encodedEvent : l'événement codé en binaire
	 * @param jsonTrainInfo : les informations sur le train, au format JSON
	 * @return l'événement décodé + la liste des erreurs (bloquantes et non bloquantes), au format JSON
	 */
	public String decodeEventToJSON(byte[] encodedEvent, String jsonTrainInfo) {
		JSONObject resultJSON = new JSONObject();

		JSONObject exceptionsArray = new JSONObject();
		JSONArray eventArray = new JSONArray();

		// Les données de train
		TrainData trainData = null;
		
		try {
			trainData = new TrainData(jsonTrainInfo);
		} catch (TrainCategoryEnumException e) {
			exceptionsArray.put(e.getKey(), e.getMsg());
		} catch (TrainDirectionEnumException e) {
			exceptionsArray.put(e.getKey(), e.getMsg());
		}
		
		// L'événement		
		if (trainData != null) {
			KVBDecoderResult result = decodeEvent(encodedEvent, trainData);
			Event event = result.getEvent();
			EventException error = result.getEventError();
							
			if (event != null && event.getKVBVariable() != null && error == null) {
				eventArray = event.getKVBVariable().toJSON();
			}
			
			// Les erreurs
			
			if (error != null) {
				exceptionsArray.put(error.getKey(), error.getMsg());
			}
			
			if (event != null) {
				for (InformationPointException e : event.getInformationPointExceptions()) {
					exceptionsArray.put(e.getKey(), e.getMsg());
				}
			}
		}
		
		resultJSON.put(JSONService.JSON_DECODER_EVENT_LABEL, eventArray);
		resultJSON.put(JSONService.JSON_DECODER_ERRORS_LABEL, exceptionsArray);
		
		return resultJSON.toString();
	}
	
	/**
	 * Définit l'emplacement des fichiers XML et charge toutes les données
	 * 
	 * @param jsonXmlPath : l'emplacement des fichiers XML, au format JSON
	 * @param checkAtessFilesCrc : booléen qui indique si la vérification du CRC doit être faite pour les fichiers autres que TablesKVB.xml (ex: depuis SAM, non ; depuis autre: oui)
	 * @return la valeur de la vérification du CRC de chaque fichier (0 si OK, le CRC indiqué dans le fichier XML sinon)
	 * @throws XMLException 
	 * @throws TrainCategoryEnumException 
	 */	
	public CrcValue[] loadXml(String jsonXmlPath, boolean checkAtessFilesCrc) throws XMLException {
		return XMLService.getServiceInstance().loadAllXMLData(jsonXmlPath, checkAtessFilesCrc);
	}
	
	/**
	 * Définit l'emplacement des fichiers XML et charge toutes les données
	 * 
	 * @param xmlPath : l'emplacement des fichiers XML, au format JSON
	 * @return succès ou non du chargement + une éventuelle erreur bloquante, au format JSON
	 */
	public String loadXmlToJSON(String jsonXmlPath) {
		JSONObject resultJSON = new JSONObject();
		JSONObject exceptionsArray = new JSONObject();
		
		try {
			CrcValue[] crcValues = loadXml(jsonXmlPath, true);
			resultJSON.put(JSONService.JSON_XMLLOADER_XMLLOADED_LABEL, true);
			
			if (crcValues != null) {				
				for (int i = 0; i < crcValues.length; i++) {
					CrcValue crcValue = crcValues[i];
					
					if (crcValue != null && !crcValue.isCheckedCRC()) {
						exceptionsArray.put(JSONService.JSON_XMLLOADER_WARNING_LABEL, String.format(MessageService.getServiceInstance().getString(MessageService.WARNING_XMLLOADING_NOTMATCHING_CRC, XMLException.class), crcValue.getFileName()));
					}
				}
				
				resultJSON.put(JSONService.JSON_XMLLOADER_ERROR_LABEL, exceptionsArray);
			}
		} catch (XMLException e) {
			resultJSON.put(JSONService.JSON_XMLLOADER_XMLLOADED_LABEL, false);
			
			exceptionsArray.put(JSONService.JSON_XMLLOADER_ERROR_LABEL, String.format("%s : %s", e.getKey(), e.getMsg()));
			resultJSON.put(JSONService.JSON_XMLLOADER_ERROR_LABEL, exceptionsArray);
			
			XMLService.getServiceInstance().setDataSuccessfullyLoaded(false);
		}
		
		return resultJSON.toString();
	}
	
	public TrainInformation getTrainInformation(String trainClass) throws TrainCategoryDecoderException {
		if (XMLService.getServiceInstance().isDataSuccessfullyLoaded()) {
			return KVBLoaderService.getServiceInstance().getTrainTable().get(trainClass);
		} else {
			throw new TrainCategoryDecoderException(MessageService.ERROR_DECODING__XML_DATA_NOT_LOADED);
		}
	}
}
