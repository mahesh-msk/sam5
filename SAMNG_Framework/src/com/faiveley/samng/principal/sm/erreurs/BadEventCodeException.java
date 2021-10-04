package com.faiveley.samng.principal.sm.erreurs;

import com.faiveley.samng.principal.logging.SamngLogger;

public class BadEventCodeException extends ParseurBinaireException  implements INonBlockingExecption{
	private static final long serialVersionUID = 1320514097747190298L;

	public BadEventCodeException() {
		super();
	}

	public BadEventCodeException(String message) {
		super(message);
		SamngLogger.getLogger().warn(message);
	}

	
}
