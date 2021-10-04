package com.faiveley.samng.principal.sm.data.variableComposant;

import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;

/**
 * variable de repère temps, distance, vitesse, diametre-roue
 * @author Graton Olivier
 * @version 1.0
 * @created 12-oct.-2007 12:09:05
 */
public class VariableRepere extends VariableDiscrete {

	private String nomRepere;
	private TypeRepere type;

	public VariableRepere(){

	}

	public VariableRepere(TypeRepere type) {
		this.type = type;
	}

	public String getNomRepere(){
		return this.nomRepere;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setNomRepere(String newVal){
		this.nomRepere = newVal;
	}

	public TypeRepere getTypeRepere() {
		return this.type;
	}

	public void setTypeRepere(TypeRepere repType) {
		this.type = repType;
	}
}