package com.faiveley.samng.principal.sm.data.tableAssociationComposant;

import java.util.HashMap;
import java.util.Map;

import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;

/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class Reperes extends AParcoursComposant {
	Map<TypeRepere, AVariableComposant> reperes = new HashMap<TypeRepere, AVariableComposant>(); 
	Map<String, TypeRepere> nomType = new HashMap<String, TypeRepere>();
	
	public Reperes() {
		
	}
	
	public void ajouterReper(TypeRepere type, AVariableComposant reper) {
		if(reper != null) {
			this.reperes.put(type, reper);
			this.nomType.put(type.getName(), type);
		}
	}
	
	public AVariableComposant getRepere(TypeRepere type) {
		return this.reperes.get(type);
	}
	
	public TypeRepere getRepere(String reperName) {
		return this.nomType.get(reperName); 
		
	}
	
	public boolean isReper(String varName) {
		return this.nomType.containsKey(varName);
	}
	
	public void clear() {
		if(this.reperes != null) {
			this.reperes.clear();
		}
		if(this.nomType != null) {
			this.nomType.clear();
		}
	}
}
