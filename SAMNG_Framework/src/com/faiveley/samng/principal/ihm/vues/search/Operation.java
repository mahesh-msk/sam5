package com.faiveley.samng.principal.ihm.vues.search;


/**
 * Enumeration of the operations that should be possible for a variable 
 * @author meggy
 *
 */
public enum Operation {
	NoOperation(" "),
	Equal("="),
	NotEqual("\u2260"),
	Greater(">"),
	GreaterOrEqual("\u2265"),
	Less("<"),
	LessOrEqual("\u2264"),
	ShiftLeft("<<"),
	Change("\u2206");
//	Change("\u2192");
//	Change("→");

	private String value;

	/**
	 * Constructor for a operation
	 * @param character
	 */
	private Operation(String character) {
		this.value = character;
	}

	/**
	 * Returns the character for the operation
	 * @return
	 */
	public String value() {
		return this.value;
	}
}