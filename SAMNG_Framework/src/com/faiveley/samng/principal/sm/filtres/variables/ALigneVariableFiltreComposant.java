package com.faiveley.samng.principal.sm.filtres.variables;

import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;

/**
 * @author Administrateur
 * @version 1.0
 * @created 12-nov.-2007 15:58:07
 */
public abstract class ALigneVariableFiltreComposant extends AFiltreComposant {

	public ALigneVariableFiltreComposant(){

	}

	public ALigneVariableFiltreComposant(ALigneVariableFiltreComposant source) {
		super(source);
	}

	/**
	 * 
	 * @param filtreComp
	 */
	public abstract void ajouterLigneVariable(ALigneVariableFiltreComposant filtreComp);


	/**
	 * 
	 * @param indice
	 */
	public abstract AFiltreComposant getEnfant(int indice);

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

	/**
	 * 
	 * @param filtreComp
	 */
	public abstract void supprimer(ALigneVariableFiltreComposant filtreComp);

	public boolean isFiltrable(){
		return filtrable;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setFiltrable(boolean newVal){
		filtrable = newVal;
	}

	public boolean isSelectionnable(){
		return selectionnable;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setSelectionnable(boolean newVal){
		selectionnable = newVal;
	}
}