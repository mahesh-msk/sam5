package com.faiveley.samng.principal.sm.data.variableComposant;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:11:14
 */
public class LabelValeur {

	private String label;
	private Object valeurs;

	public LabelValeur(){

	}

	public String getLabel(){
		return this.label;
	}

	public Object getValeurs(){
		return this.valeurs;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setLabel(String newVal){
		this.label = newVal;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setValeurs(Object newVal){
		this.valeurs = newVal;
	}

}