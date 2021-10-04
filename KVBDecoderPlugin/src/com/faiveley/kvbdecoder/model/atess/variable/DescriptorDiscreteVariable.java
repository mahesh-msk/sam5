package com.faiveley.kvbdecoder.model.atess.variable;

import com.faiveley.kvbdecoder.exception.model.variable.VariableTypeEnumException;

/**
 * Spécification de DescriptorVariable, dans les cas d'une variable discrete.
 * 
 * @author jthoumelin
 *
 */
public class DescriptorDiscreteVariable extends DescriptorVariable {	
	public DescriptorDiscreteVariable(int code, String name, String libelle, String type) throws VariableTypeEnumException {
		super(code, name, libelle, type);
	}
	
	protected boolean isValidVariable(boolean subVariable) {
		return (code != -1 || subVariable) && name != null && type != null && libelle != null;
	}
}
