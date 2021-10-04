package com.faiveley.samng.principal.sm.erreurs;

import com.faiveley.samng.principal.logging.SamngLogger;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:10:25
 */
public class BadCRCException extends ParseurBinaireException  implements INonBlockingExecption{
	private static final long serialVersionUID = -4029220701219412967L;

	public BadCRCException(){

	}

	public BadCRCException(String message) {
		super(message);
		SamngLogger.getLogger().warn(message);
	}
	
	
}