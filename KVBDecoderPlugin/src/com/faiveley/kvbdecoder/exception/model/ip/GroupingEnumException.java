package com.faiveley.kvbdecoder.exception.model.ip;

import com.faiveley.kvbdecoder.exception.decoder.InformationPointDecoderException;

public class GroupingEnumException extends InformationPointDecoderException {
	private static final long serialVersionUID = 6368862388766338241L;

	private static final String ERROR_IP_DECODING__INVALID_GROUPING = "KVB.erreur.decodage.pointInformation.groupementInvalide";
	
	public GroupingEnumException(String grouping) {
		super(ERROR_IP_DECODING__INVALID_GROUPING, grouping);
	}
	
	public GroupingEnumException(Throwable cause, String grouping) {
		super(cause, ERROR_IP_DECODING__INVALID_GROUPING, grouping);
	}
}