package com.faiveley.samng.principal.sm.data.tableAssociationComposant;

import java.util.ArrayList;
import java.util.List;

import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:10:22
 */
public abstract class ATableAssociationComposant {//extends HashMap<Object, Object>{

	private int valeurCRC;

	private List<AVariableComposant> varComp = null;
	
	public ATableAssociationComposant(){
		
	}

	/**
	 * 
	 * @param table
	 */
	public void ajouter(ATableAssociationComposant table){
		//does nothing
	}

	/**
	 * 
	 * @param indice
	 */
	public ATableAssociationComposant getEnfant(int indice){
		return null;
	}

	public int getValeurCRC(){
		return this.valeurCRC;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setValeurCRC(int newVal){
		this.valeurCRC = newVal;
	}

	/**
	 * 
	 * @param table
	 */
	public void supprimer(ATableAssociationComposant table){
		//does nothing
	}

	public boolean addComposant(AVariableComposant arg0) {
		if (this.varComp == null) {
			this.varComp = new ArrayList<AVariableComposant>(1);
		}
		return this.varComp.add(arg0);
	}

	public AVariableComposant getComposant(int arg0) {
		return this.varComp != null ? this.varComp.get(arg0) : null;
	}

	public boolean removeComposant(Object arg0) {
		return this.varComp != null ? this.varComp.remove(arg0) : false;
	}

	public AVariableComposant getComposantByName(String nomComposant){
		for (AVariableComposant comp : varComp) {
			if (comp.getDescriptor().getM_AIdentificateurComposant().getNom().equals(nomComposant)) {
				return comp;
			}
		}	
		return null;
	}
}