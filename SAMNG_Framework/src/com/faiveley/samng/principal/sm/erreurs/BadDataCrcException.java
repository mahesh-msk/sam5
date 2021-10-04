package com.faiveley.samng.principal.sm.erreurs;

import com.faiveley.samng.principal.logging.SamngLogger;

public class BadDataCrcException extends ParseurBinaireException implements IBlockingException {
	private static final long serialVersionUID = -5951660981031067916L;

	public BadDataCrcException() {
		super();
	}

	public BadDataCrcException(String message) {
		super(message);
		SamngLogger.getLogger().fatal(message);
	}

}
