package com.faiveley.kvbdecoder.exception.decoder;

import com.faiveley.kvbdecoder.exception.EventException;
import com.faiveley.kvbdecoder.services.message.MessageService;


/**
 * Exception KVB relative à l'événement et lévée lors du décodage de l'événement.
 * 
 * @author jthoumelin
 *
 */
public class EventDecoderException extends EventException {
	private static final long serialVersionUID = 6368862388766338241L;
	
	private static final String ERROR_EVENT_DECODING = "KVB.erreur.decodage.evenement";

	public EventDecoderException(String key, String... values) {
		super(key, values);
	}

	public EventDecoderException(Throwable cause, String key, String... values) {
		super(cause, key, values);
	}
	
	protected void buildMessage() {
		String detailedMessage = String.format(MessageService.getServiceInstance().getString(key, EventDecoderException.class), (Object[]) values);
		msg = String.format(MessageService.getServiceInstance().getString(ERROR_EVENT_DECODING, EventDecoderException.class), detailedMessage);
	}
}