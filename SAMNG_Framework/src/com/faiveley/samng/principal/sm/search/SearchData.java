package com.faiveley.samng.principal.sm.search;

import com.faiveley.samng.principal.ihm.vues.search.Operation;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;

public class SearchData {
	private DescripteurVariable descrVar = null;
	private Operation op = null;
	private String value = null;
	
	
	/**
	 * @return the descrVar
	 */
	public DescripteurVariable getDescriptorVariable() {
		return this.descrVar;
	}
	/**
	 * @param descrVar the descrVar to set
	 */
	public void setDescriptorVariable(DescripteurVariable descrVar) {
		this.descrVar = descrVar;
	}
	/**
	 * @return the op
	 */
	public Operation getOperation() {
		return this.op;
	}
	/**
	 * @param op the op to set
	 */
	public void setOperation(Operation op) {
		this.op = op;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	

}
