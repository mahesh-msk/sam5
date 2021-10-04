package com.faiveley.samng.principal.sm.erreurs;

import com.faiveley.samng.principal.logging.SamngLogger;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:10:27
 */
public class BadStructureFileException extends ParseurBinaireException {
	private static final long serialVersionUID = -6791801016572015002L;

	public BadStructureFileException(){

	}

	public BadStructureFileException(String message) {
		super(message);
		SamngLogger.getLogger().warn(message);
	}

}