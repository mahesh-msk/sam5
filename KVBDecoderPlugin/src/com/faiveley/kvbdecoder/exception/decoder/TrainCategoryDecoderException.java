package com.faiveley.kvbdecoder.exception.decoder;

import com.faiveley.kvbdecoder.exception.KVBException;
import com.faiveley.kvbdecoder.services.message.MessageService;

public class TrainCategoryDecoderException extends KVBException {
	private static final long serialVersionUID = -4816174297673546979L;

	private static final String ERROR_TC_DECODING = "KVB.erreur.decodage.categorieTrain";
	
	public TrainCategoryDecoderException(String key, String... values) {
		super(key, values);
	}

	protected void buildMessage() {
		String detailedMessage = String.format(MessageService.getServiceInstance().getString(key, EventDecoderException.class), (Object[]) values);
		msg = String.format(MessageService.getServiceInstance().getString(ERROR_TC_DECODING, EventDecoderException.class), detailedMessage);
	}
}
