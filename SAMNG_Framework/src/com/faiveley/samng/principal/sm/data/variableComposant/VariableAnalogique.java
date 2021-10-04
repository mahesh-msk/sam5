package com.faiveley.samng.principal.sm.data.variableComposant;


/**
 * Les variables analogiques sont des variables contenant des informations �
 * caract�re continu souvent li� � une mesure (vitesse, temps, distance,
 * acc�l�ration, pression, temp�rature ?)
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:11:39
 */
public class VariableAnalogique extends AVariableComposant {

	public VariableAnalogique(){

	}
	
	@Override
	public VariableAnalogique copy() {
		VariableAnalogique var = new VariableAnalogique();
		copyTo(var);
		return var;
	}
}