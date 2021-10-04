package com.faiveley.kvbdecoder.services.decoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.faiveley.kvbdecoder.exception.InformationPointException;
import com.faiveley.kvbdecoder.exception.decoder.EventDecoderException;
import com.faiveley.kvbdecoder.exception.decoder.InformationPointDecoderException;
import com.faiveley.kvbdecoder.exception.model.marker.NextMarkerIndicatorEnumException;
import com.faiveley.kvbdecoder.model.kvb.ip.InformationPoint;
import com.faiveley.kvbdecoder.model.kvb.marker.Marker;
import com.faiveley.kvbdecoder.model.kvb.marker.MarkerX14;
import com.faiveley.kvbdecoder.model.kvb.marker.MarkerX1X4;
import com.faiveley.kvbdecoder.model.kvb.marker.MarkerX3X6X9;
import com.faiveley.kvbdecoder.model.kvb.marker.NextMarkerIndicatorEnum;
import com.faiveley.kvbdecoder.model.kvb.variable.KVBAnalogVariable;
import com.faiveley.kvbdecoder.model.kvb.variable.KVBVariable;
import com.faiveley.kvbdecoder.services.loader.KVBLoaderService;
import com.faiveley.kvbdecoder.services.message.MessageService;

/**
 * Service qui guide le décodage d'une balise analogique.
 * 
 * @author jthoumelin
 *
 */
public class AnalogService {
	private static final int BALISES123_COMPLEXVARIABLE_CODE = 51;
	private static final int BALISES45_COMPLEXVARIABLE_CODE = 52;
	
	/**
	 * Le singleton
	 */
	private static AnalogService DECODER_INSTANCE = new AnalogService();
	
	/**
	 * Le constructeur vide
	 */
	private AnalogService() {}
	
	/**
	 * Obtention du singleton
	 */
	protected static AnalogService getDecoderInstance() {
		return DECODER_INSTANCE;
	}	

	/**
	 * Décodage d'une balise analogique
	 * 
	 * @param: v : la variable KVB numérique
	 * @throws EventDecoderException 
	 */
	protected void decodeFromAnalog(KVBAnalogVariable v) throws EventDecoderException {		
		int codeVariable = v.getDescriptor().getCode();
		InformationPoint ip = null;
		
		if (codeVariable == BALISES123_COMPLEXVARIABLE_CODE || codeVariable == BALISES45_COMPLEXVARIABLE_CODE) {
			try {
				ip = buildIp(v, v.getValue(), codeVariable, 0);
				v.setInformationPoint(ip);
			} catch (InformationPointException e) {
				v.setInformationPoint(e.getIp());
				v.getParent().addInformationPointException(e);
			}
		} else {
			throw new EventDecoderException(MessageService.ERROR_EVENT_DECODING__INVALID_ANALOG_VARIABLE_CODE, String.valueOf(v.getParent().getEventDescriptor().getCode()), String.valueOf(codeVariable), String.valueOf(BALISES123_COMPLEXVARIABLE_CODE), String.valueOf(BALISES45_COMPLEXVARIABLE_CODE));
		}
	}
	
	/**
	 * Construction du point d'information unique d'une balise analogique
	 * 
	 * @param parent : l'événement parent
	 * @param KVBMessage : le message
	 * @param codeVariable : le code de la variable
	 * @param ipIndex : le numéro du point d'information au sein de l'événement
	 * @return le point d'information
	 */
	private InformationPoint buildIp(KVBVariable parent, String KVBMessage, int codeVariable, int ipIndex) throws InformationPointException {	
		InformationPoint ip = new InformationPoint(parent, InformationPoint.XSM_XCS_DEFAULT_VALUE, InformationPoint.XSM_XCS_DEFAULT_VALUE, true, ipIndex);
		
		int markerNumber;
		int offset = 2;
		NextMarkerIndicatorEnum lastIndicator = null;

		for (markerNumber = 1; markerNumber < 6; markerNumber++) {
			int x;
			int y;
			int z;
			
			// Détermination de X, Y et Z
			
			if (lastIndicator != null && lastIndicator.equals(NextMarkerIndicatorEnum.M)) {
				x = Marker.MARKER_M_VALUE;
				y = -1;
				z = -1;
			} else {
				x = Integer.parseInt(KVBMessage.substring(offset, offset + 1), 16);
				y = Integer.parseInt(KVBMessage.substring(offset + 1, offset + 2), 16);
				z = Integer.parseInt(KVBMessage.substring(offset + 2, offset + 3), 16);
			}
			
			Marker m = null;
			
			//UtilService.isValidX(x);
		
			if (UtilService.isX1X4(x)) {
				m =  new MarkerX1X4(ip, x, y, z);
			} else if (UtilService.isX3X6X9(x)) {
				m =  new MarkerX3X6X9(ip, x, y, z);
			} else if (UtilService.isX14(x)) {
				m = new MarkerX14(ip, x, y, z);
			} else {
				m = new Marker(ip, x, y, z);
			}
			
			ip.addMarker(m);
			
			// Détermination de l'indicateur sur la balise suivante
			String indicatorValue = KVBMessage.substring(offset + 3, offset + 4);
            
            for (NextMarkerIndicatorEnum value : NextMarkerIndicatorEnum.values()) {
                if (value.toString().equals(indicatorValue)) {
                    lastIndicator = value;
                    break;
                }
            }
            					
			if (lastIndicator == null) {
				throw new NextMarkerIndicatorEnumException(indicatorValue);
			} else if (lastIndicator.equals(NextMarkerIndicatorEnum.NONE)) {
				// Si l'indicateur indique qu'il n'y a pas de balise suivante: fin du traitement
				break;
			}
			
			offset += 4;
		}
		
		// Obtenir le descripteur
		/// Former la séquence des X (clef)
		List<String> xSequence = new ArrayList<String>();
		
		for (Marker m : ip.getMarkers()) {
			if (m != null) {				
				xSequence.add(String.valueOf(m.getX()));
			}
		}
		
		String xSequenceString = UtilService.join(",", xSequence);		
		Map<String, String> labels = KVBLoaderService.getServiceInstance().getInformationPointLabels(xSequenceString);
		ip.setXSequence(xSequenceString.replace(String.valueOf(Marker.MARKER_M_VALUE), Marker.MARKER_M));
		
		if (labels != null) {
			ip.setLabels(labels);			
		} else {
			throw new InformationPointDecoderException(MessageService.ERROR_IP_DECODING__INVALID_X_SEQUENCE, ip);
		}
		
		return ip;
	}
}
