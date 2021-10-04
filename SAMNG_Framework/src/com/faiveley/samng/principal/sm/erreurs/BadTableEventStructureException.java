package com.faiveley.samng.principal.sm.erreurs;

import com.faiveley.samng.principal.logging.SamngLogger;

/**
 * @author meggy
 *
 */
public class BadTableEventStructureException extends BadStructureFileException {
	private static final long serialVersionUID = 2893758441501302082L;

	public BadTableEventStructureException() {
	}

	public BadTableEventStructureException(String message) {
		super(message);
		SamngLogger.getLogger().fatal(message);
	}

}
