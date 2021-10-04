package com.faiveley.samng.principal.ihm.vues;

import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;

public class RowVariable {

	private AVariableComposant var;
	
	private boolean valorisee;
	
	public RowVariable(AVariableComposant var, boolean valorisee) {
		this.var = var;
		this.valorisee = valorisee;
	}

	public AVariableComposant getVar() {
		return var;
	}
	
	public void setVar(AVariableComposant var) {
		this.var = var;
	}

	public boolean isValorisee() {
		return valorisee;
	}
	
	public void setValorisee(boolean valorisee) {
		this.valorisee = valorisee;
	}
}
