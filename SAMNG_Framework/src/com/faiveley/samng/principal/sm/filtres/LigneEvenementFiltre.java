package com.faiveley.samng.principal.sm.filtres;

/**
 * @author Administrateur
 * @version 1.0
 * @created 12-nov.-2007 15:58:33
 */
public class LigneEvenementFiltre extends AFiltreComposant {

	public LigneEvenementFiltre() {

	}

	public LigneEvenementFiltre(LigneEvenementFiltre source) {
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
	
	public LigneEvenementFiltre clone() {
		return new LigneEvenementFiltre(this);
	}

}