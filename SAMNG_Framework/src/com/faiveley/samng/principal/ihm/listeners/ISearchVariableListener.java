package com.faiveley.samng.principal.ihm.listeners;


import com.faiveley.samng.principal.ihm.vues.search.Operation;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;

/**
 * Interface for search by variable listener 
 * @author meggy
 *
 */
public interface ISearchVariableListener {
	
	/**
	 * Method to inform the listeners that is searched by a variable 
	 * 	 
	 * @param descrVar	the descriptor of the variable
	 * @param value		the value to compare with
	 * @param op		the operation to be used in comparation
	 * @param next		flag to say that must be selected for next or previous marquer
	 */
	public void onSearchVariable(DescripteurVariable descrVar, String value, Operation op, boolean next);
	
	public void onSearchVariable(DescripteurVariable descrVar, String stringValue, String value, Operation op, boolean next);
	
}
