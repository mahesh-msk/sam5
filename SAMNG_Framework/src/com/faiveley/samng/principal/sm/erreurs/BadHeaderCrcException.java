package com.faiveley.samng.principal.sm.erreurs;

import com.faiveley.samng.principal.logging.SamngLogger;

public class BadHeaderCrcException extends ParseurBinaireException implements IBlockingException {
	private static final long serialVersionUID = -8890891085841278306L;

	public BadHeaderCrcException() {
		super();
	}

	public BadHeaderCrcException(String message) {
		super(message);
		SamngLogger.getLogger().fatal(message);
	}

}
