package com.faiveley.kvbdecoder.exception.decoder;

import com.faiveley.kvbdecoder.exception.InformationPointException;
import com.faiveley.kvbdecoder.model.kvb.ip.InformationPoint;
import com.faiveley.kvbdecoder.services.message.MessageService;


/**
 * Exception KVB relative au point d'information et lévée lors du décodage du point d'information.
 * 
 * @author jthoumelin
 *
 */
public class InformationPointDecoderException extends InformationPointException {
	private static final long serialVersionUID = 6368862388766338241L;

	private static final String ERROR_IP_DECODING = "KVB.erreur.decodage.pointInformation";
	
	
	public InformationPointDecoderException(String key, String... values) {
		super(key, values);
	}
	
	public InformationPointDecoderException(String key, InformationPoint ip) {
		super(key, ip);
	}

	public InformationPointDecoderException(Throwable cause, String key, String... values) {
		super(cause, key, values);
	}
	
	protected void buildMessage() {
		String detailedMessage = String.format(MessageService.getServiceInstance().getString(key, InformationPointDecoderException.class), (Object[]) values);
		msg = String.format(MessageService.getServiceInstance().getString(ERROR_IP_DECODING, InformationPointDecoderException.class), detailedMessage);
	}
}
