package com.faiveley.kvbdecoder.exception.model.variable;

import com.faiveley.kvbdecoder.exception.xml.XMLException;

public class VariableTypeEnumException extends XMLException {
	private static final long serialVersionUID = 6368862388766338241L;

	private static final String ERROR_XMLLOADING_ATESS__INVALID_VARIABLE_TYPE = "KVB.erreur.chargementXML.atess.typeVariableInvalide";
	
	public VariableTypeEnumException(String fileName, String variableType) {
		super(fileName, ERROR_XMLLOADING_ATESS__INVALID_VARIABLE_TYPE, variableType);
	}
	
	public VariableTypeEnumException(String fileName, String variableType, Throwable cause) {
		super(cause, fileName, ERROR_XMLLOADING_ATESS__INVALID_VARIABLE_TYPE, variableType);
	}
}