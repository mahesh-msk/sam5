package com.faiveley.samng.principal.sm.erreurs;

/**
 * Execption thrown when a bloc structure is not good
 * @author meggy
 *
 */
public class BadBlocStructureException extends BadStructureFileException {

	private static final long serialVersionUID = -4827623980581299398L;

	/**
	 * 
	 */
	public BadBlocStructureException() {
		super();
	}

	public BadBlocStructureException(String message) {
		super(message);
	}

	
}
