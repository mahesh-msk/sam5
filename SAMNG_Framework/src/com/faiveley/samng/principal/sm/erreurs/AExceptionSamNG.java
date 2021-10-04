package com.faiveley.samng.principal.sm.erreurs;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:10:07
 */
public abstract class AExceptionSamNG extends Exception {

	private static final long serialVersionUID = 346943775517046082L;

	public AExceptionSamNG(){
		super();
	}
	
	public AExceptionSamNG(Throwable cause){
		super(cause);
	}
	
	public AExceptionSamNG(String message){
		super(message);
	}
		
}