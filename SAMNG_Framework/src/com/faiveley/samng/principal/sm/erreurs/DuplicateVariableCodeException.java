package com.faiveley.samng.principal.sm.erreurs;

import com.faiveley.samng.principal.logging.SamngLogger;

/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class DuplicateVariableCodeException extends RuntimeException {
	private static final long serialVersionUID = -4558446636559549492L;

	public DuplicateVariableCodeException() {
		super();
	}

	public DuplicateVariableCodeException(String message) {
		super(message);
		SamngLogger.getLogger().warn(message);
	}

}
