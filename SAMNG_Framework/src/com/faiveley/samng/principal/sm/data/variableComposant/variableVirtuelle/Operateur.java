package com.faiveley.samng.principal.sm.data.variableComposant.variableVirtuelle;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 24-déc.-2007 14:36:40
 */
public enum Operateur {
	EQUALS('='),
	DIFFERENT('\u2260'),
	LESS_THAN('<'),
	GREATER_THAN('>'),
	LESS_THAN_OR_EQUALS('\u2264'),
	GREATER_THAN_OR_EQUALS('\u2265'),
	AND ('&'),
	OR ('|');

	private char code;
	private String strValue;
	
	private Operateur(char code) {
		this.code = code;
		if(this.code == '&')
			strValue = "AND";
		else if(this.code == '|')
			strValue = "OR";
		else {
			char[] codeArr = new char[] {this.code};
			strValue = new String(codeArr);
		}
	}
	
	public char getCode() {
		return code;
	}
	
	public String getStringValue() {
		return strValue;
	}
}