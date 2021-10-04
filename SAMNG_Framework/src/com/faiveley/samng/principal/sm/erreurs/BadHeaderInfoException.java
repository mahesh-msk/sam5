package com.faiveley.samng.principal.sm.erreurs;

import com.faiveley.samng.principal.logging.SamngLogger;

public class BadHeaderInfoException extends ParseurBinaireException {
	private static final long serialVersionUID = -8890891085841278306L;

	public BadHeaderInfoException() {
		super();
	}

	public BadHeaderInfoException(String message) {
		super(message);
		SamngLogger.getLogger().fatal(message);
	}

}
