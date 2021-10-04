package com.faiveley.samng.principal.sm.data.enregistrement.atess;

import com.faiveley.kvbdecoder.exception.decoder.TrainCategoryDecoderException;
import com.faiveley.samng.principal.sm.data.enregistrement.Messages;

public class AtessMessageException extends Exception {
	private static final long serialVersionUID = 1149356715111548264L;
	
	private static final String ERROR_ATESS_MESSAGE_DECODING = "erreurDecodageMessageAtess";
	
	private String msg;
	
	public AtessMessageException(String key, String... values) {
		msg = buildMessage(key, values);
	}
		
	public AtessMessageException(Throwable cause, String key, String... values) {
		super(cause);
		msg = buildMessage(key, values);
	}
	
	public AtessMessageException(TrainCategoryDecoderException e) {
		super(e);
		msg = e.getMsg();
	}

	private String buildMessage(String key, String... values) {
		String detailedMessage = String.format(Messages.getString(key), (Object[]) values);
		return String.format(Messages.getString(ERROR_ATESS_MESSAGE_DECODING), detailedMessage);
	}
	
	public String getMsg() {
		return msg;
	}
}
