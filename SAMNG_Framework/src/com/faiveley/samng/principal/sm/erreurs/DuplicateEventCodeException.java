package com.faiveley.samng.principal.sm.erreurs;

import com.faiveley.samng.principal.logging.SamngLogger;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class DuplicateEventCodeException extends RuntimeException {
	private static final long serialVersionUID = -2143535991985943042L;

	public DuplicateEventCodeException() {
		super();
	}

	public DuplicateEventCodeException(String message) {
		super(message);
		SamngLogger.getLogger().warn(message);
	}
}
