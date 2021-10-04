package com.faiveley.kvbdecoder.model.atess.variable;

import java.util.ArrayList;
import java.util.List;

import com.faiveley.kvbdecoder.exception.model.variable.VariableTypeEnumException;


/**
 * Spécification de DescriptorVariable, dans les cas d'une variable complexe.
 * 
 * @author jthoumelin
 *
 */
public class DescriptorComplexVariable extends DescriptorVariable {
	private int size = -1;
	private List<DescriptorVariable> subVariables;
		
	public List<DescriptorVariable> getSubVariables() {
		return subVariables;
	}
	
	public void addSubVariable(DescriptorVariable v) {
		if (!(v instanceof DescriptorComplexVariable)) {
			subVariables.add(v);
		}
	}
			
	public int getSize() {
		return size;
	}

	public DescriptorComplexVariable(int code, String name, String libelle, String type, int size) throws VariableTypeEnumException {
		super(code, name, libelle, type);
		this.size = size;
		subVariables = new ArrayList<DescriptorVariable>();
	}
	
	protected boolean isValidVariable(boolean subVariable) {
		return code != -1 && name != null && libelle != null && size != -1;
	}
}
