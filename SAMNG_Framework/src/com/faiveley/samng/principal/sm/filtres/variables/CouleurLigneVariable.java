package com.faiveley.samng.principal.sm.filtres.variables;

import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;

/**
 * @author Administrateur
 * @version 1.0
 * @created 12-nov.-2007 15:58:11
 */
public class CouleurLigneVariable extends ALigneVariableFiltreComposant {

	private String valeurHexa;

	public CouleurLigneVariable(){

	}
	
	public CouleurLigneVariable(CouleurLigneVariable source) {
		super(source);
		this.valeurHexa = source.valeurHexa;
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

	public String getValeurHexa(){
		return valeurHexa;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setValeurHexa(String newVal){
		valeurHexa = newVal;
	}

	@Override
	public void ajouter(AFiltreComposant filtreComp) {
		
		
	}

	@Override
	public AFiltreComposant getEnfant(AFiltreComposant comp) {
		
		return null;
	}

	@Override
	public void removeAll() {
		
		
	}

	@Override
	public void supprimer(int indice) {
		
	}
	
	@Override
	public void supprimer(AFiltreComposant filtreComp) {
		
		
	}

	@Override
	public void ajouterLigneVariable(ALigneVariableFiltreComposant filtreComp) {
		
		
	}

	@Override
	public AFiltreComposant getEnfant(int indice) {
		
		return null;
	}

	@Override
	public void supprimer(ALigneVariableFiltreComposant filtreComp) {
		
		
	}

	@Override
	public CouleurLigneVariable clone() {
		return new CouleurLigneVariable(this);
	}

}