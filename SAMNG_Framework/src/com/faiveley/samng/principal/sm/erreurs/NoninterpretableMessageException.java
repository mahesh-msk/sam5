package com.faiveley.samng.principal.sm.erreurs;

import com.faiveley.samng.principal.logging.SamngLogger;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:11:18
 */
public class NoninterpretableMessageException extends ParseurBinaireException {
	
	private static final long serialVersionUID = -7144982655024444561L;

	public NoninterpretableMessageException(){

	}
	
	public NoninterpretableMessageException(String msg) {
		super(msg);
		SamngLogger.getLogger().warn(msg);
	}

}