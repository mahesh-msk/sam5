package com.faiveley.kvbdecoder.model.atess.variable;

import com.faiveley.kvbdecoder.exception.model.variable.VariableTypeEnumException;

/**
 * Spécification de DescriptorVariable, dans les cas d'une variable analogique.
 * 
 * @author jthoumelin
 *
 */
public class DescriptorAnalogVariable extends DescriptorVariable {	
	private String unit;
	private boolean escalier;
	
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	public boolean isEscalier() {
	    return escalier;
	}

	public void setEscalier(boolean escalier) {
	    this.escalier = escalier;
	}

	public DescriptorAnalogVariable(int code, String name, String libelle, String type, String unit) throws VariableTypeEnumException {
		super(code, name, libelle, type);
		this.unit = unit;
	}
	
	public DescriptorAnalogVariable(int code, String name, String libelle, String type, String unit, boolean escalier) throws VariableTypeEnumException {
		this(code, name, libelle, type, unit);
		this.escalier = escalier;
	}
	
	protected boolean isValidVariable(boolean subVariable) {
		return  (code != -1 || subVariable) && name != null && type != null && (unit != null || subVariable) && libelle != null;
	}
}
