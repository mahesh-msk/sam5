package com.faiveley.samng.principal.sm.erreurs;

import com.faiveley.samng.principal.logging.SamngLogger;

public class BadVariableCodeException extends ParseurBinaireException {
	private static final long serialVersionUID = 1732699732702457660L;

	public BadVariableCodeException() {
		super();
	}

	public BadVariableCodeException(String message) {
		super(message);
		SamngLogger.getLogger().warn(message); 
	}

}
