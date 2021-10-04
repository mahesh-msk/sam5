package com.faiveley.samng.principal.sm.filtres.variables;

import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;

/**
 * @author Administrateur
 * @version 1.0
 * @created 12-nov.-2007 15:58:41
 */
public class OperateurLigneVariable extends ALigneVariableFiltreComposant {

	public OperateurLigneVariable(){

	}

	public OperateurLigneVariable(OperateurLigneVariable source) {
		super(source);
	}

	public String getNom(){
		return nom;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setNom(String newVal){
		nom = newVal;
	}

	@Override
	public void ajouterLigneVariable(ALigneVariableFiltreComposant filtreComp) {
		//  Auto-generated method stub
		
	}

	@Override
	public AFiltreComposant getEnfant(int indice) {
		//  Auto-generated method stub
		return null;
	}

	@Override
	public void supprimer(int indice) {
		
	}
	
	@Override
	public void supprimer(ALigneVariableFiltreComposant filtreComp) {
		
	}

	@Override
	public void ajouter(AFiltreComposant filtreComp) {
		//  Auto-generated method stub
		
	}

	@Override
	public AFiltreComposant getEnfant(AFiltreComposant comp) {
		//  Auto-generated method stub
		return null;
	}

	@Override
	public void removeAll() {
		
		
	}

	@Override
	public void supprimer(AFiltreComposant filtreComp) {
		
		
	}

	@Override
	public OperateurLigneVariable clone() {
		return new OperateurLigneVariable(this);
	}

}