package com.faiveley.samng.principal.sm.erreurs;

import com.faiveley.samng.principal.logging.SamngLogger;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:11:29
 */
public class ParseurXMLException extends ParseurException {

	private static final long serialVersionUID = 6132098324714574290L;

	public ParseurXMLException(){

	}
	
	public ParseurXMLException(String message, boolean blockingEx) {
		super(message);
		if(blockingEx)
			SamngLogger.getLogger().fatal(message);
		else
			SamngLogger.getLogger().warn(message); 
	}

	
}