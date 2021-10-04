package com.faiveley.samng.principal.sm.data.variableComposant;


/**
 * Les variables discr�tes regroupent les types suivants :
 * Variable bool�enne (� 2 �tats)
 * Variable num�rique non continue
 * Cha�ne de caract�res
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