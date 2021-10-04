package com.faiveley.samng.principal.sm.erreurs;

import com.faiveley.samng.principal.logging.SamngLogger;

public class BadFileLengthException extends ParseurBinaireException implements IBlockingException{

	private static final long serialVersionUID = 7305237034579411906L;

	public BadFileLengthException() {
		super();
	}

	public BadFileLengthException(String message) {
		super(message);
		SamngLogger.getLogger().fatal(message);
	}

}
