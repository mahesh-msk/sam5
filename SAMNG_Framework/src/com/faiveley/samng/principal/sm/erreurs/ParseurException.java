package com.faiveley.samng.principal.sm.erreurs;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:11:26
 */
public class ParseurException extends AExceptionSamNG {
	private static final long serialVersionUID = 7527612751616677670L;

	public ParseurException(){}

	public ParseurException(String message) {
		super(message);
	}
	
	public ParseurException(Throwable cause) {
		super(cause);
	}
}