package com.faiveley.kvbdecoder.exception.model.marker;

import com.faiveley.kvbdecoder.exception.decoder.InformationPointDecoderException;

public class NextMarkerIndicatorEnumException extends InformationPointDecoderException {
	private static final long serialVersionUID = 6368862388766338241L;

	private static final String ERROR_IP_DECODING__INVALID_NEXT_MARKER_INDICATOR = "KVB.erreur.decodage.pointInformation.indicateurBaliseSuivanteInvalide";
	
	public NextMarkerIndicatorEnumException(String nextMarkerIndicator) {
		super(ERROR_IP_DECODING__INVALID_NEXT_MARKER_INDICATOR, nextMarkerIndicator);
	}
	
	public NextMarkerIndicatorEnumException(Throwable cause, String nextMarkerIndicator) {
		super(cause, ERROR_IP_DECODING__INVALID_NEXT_MARKER_INDICATOR, nextMarkerIndicator);
	}
}