package com.faiveley.samng.principal.sm.data.variableComposant;


/**
 * Les variables discrètes regroupent les types suivants :
 * Variable booléenne (à 2 états)
 * Variable numérique non continue
 * Chaîne de caractères
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:11:49
 */
public class VariableDiscrete extends AVariableComposant {

	public VariableDiscrete(){

	}
	
	@Override
	public VariableDiscrete copy() {
		VariableDiscrete var = new VariableDiscrete();
		copyTo(var);
		return var;
	}
}