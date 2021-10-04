package com.faiveley.samng.principal.sm.erreurs;

import com.faiveley.samng.principal.logging.SamngLogger;

public class BadEvTableCrcException extends ParseurBinaireException implements IBlockingException {
	private static final long serialVersionUID = -5078455517523070891L;

	public BadEvTableCrcException() {
		super();
	}

	public BadEvTableCrcException(String message) {
		super(message);
		SamngLogger.getLogger().fatal(message);
	}
}
