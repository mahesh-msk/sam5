package com.faiveley.samng.principal.sm.filtres.variables;

import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;

/**
 * @author Administrateur
 * @version 1.0
 * @created 12-nov.-2007 15:58:43
 */
public class ValeurLigneVariable extends ALigneVariableFiltreComposant {

	private int valeur;

	public ValeurLigneVariable(){

	}

	public ValeurLigneVariable(ValeurLigneVariable source){
		super(source);
		this.valeur = source.valeur;
	}

	public int getValeur(){
		return valeur;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setValeur(int newVal){
		valeur = newVal;
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
		//  Auto-generated method stub
		
	}

	@Override
	public void supprimer(AFiltreComposant filtreComp) {
		//  Auto-generated method stub
		
	}

	@Override
	public ValeurLigneVariable clone() {
		return new ValeurLigneVariable(this);
	}

}