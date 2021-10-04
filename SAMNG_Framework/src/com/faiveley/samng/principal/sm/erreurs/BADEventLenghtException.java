package com.faiveley.samng.principal.sm.erreurs;

import com.faiveley.samng.principal.logging.SamngLogger;

public class BADEventLenghtException extends ParseurBinaireException implements INonBlockingExecption {
	private static final long serialVersionUID = 2565836201197369192L;

	public BADEventLenghtException() {
		super();
	}

	public BADEventLenghtException(String message) {
		super(message);
		SamngLogger.getLogger().warn(message);
	}

	
}
