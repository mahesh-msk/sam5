package com.faiveley.kvbdecoder.model.atess.variable;

import com.faiveley.kvbdecoder.exception.model.variable.VariableTypeEnumException;
import com.faiveley.kvbdecoder.services.loader.AtessLoaderService;


public enum VariableTypeEnum {
	uint8("uint8"),
	int8("int8"),
	uint16("uint16"),
	int16("int16"),
	uint24("uint24"),
	int24("int24"),
	uint32("uint32"), 
	int32("int32"),
    uint64("uint64"),
    int64("int64"),
    real32("real32"), 
    real64("real64"),
    boolean8("boolean8"),
    boolean1("boolean1"), 
    string("string"),
    unixTimestamp("unixTimestamp"),
    BCD4("BCD4"),
    BCD8("BCD8"),
    array("array"),
    uintXbits("uintXbits"),
    intXbits("intXbits"),
    dateHeureBCD("dateHeureBCD"),
    reserved("reserved");
  
	private String label;
  
	VariableTypeEnum(String label) {
		this.label = label;
	}
  
	public String toString() {
		return label;
	}
	
	/**
	 * Obtenir le nombre de bytes à lire pour ce type de variable
	 * 
	 * @return: le nombre de bytes, -1 si le type est inconnu
	 */
	public int getSize() {
		if (this.equals(uint8)) {
			return 1;
		} else if (this.equals(uint16)) {
			return 2;
		} else {
			return -1;
		}
	}
	
	public static VariableTypeEnum fromString(String variableType) throws VariableTypeEnumException {		  
		for (VariableTypeEnum value : VariableTypeEnum.values()) {
			if (value.toString().toLowerCase().equals(variableType.toLowerCase())) {
				return value;
			}
		}
		
		throw new VariableTypeEnumException(AtessLoaderService.XML_FILE_ATESS_NAME, variableType.toLowerCase());
	}
}
