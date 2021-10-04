package com.faiveley.samng.principal.sm.filtres.variables;

import java.util.ArrayList;
import java.util.List;

import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;


/**
 * @author Administrateur
 * @version 1.0
 * @created 12-nov.-2007 15:58:36
 */
public class LigneVariableFiltreComposite extends ALigneVariableFiltreComposant {


	private List<AFiltreComposant> enfants;

	public LigneVariableFiltreComposite() {

	}

	public LigneVariableFiltreComposite(LigneVariableFiltreComposite source){
		super(source);
		AFiltreComposant childClone;
		if(source.enfants == null)
			return;
		this.enfants = new ArrayList<AFiltreComposant>(source.enfants.size());
		for(AFiltreComposant filtre: source.enfants) {
			childClone = (AFiltreComposant)filtre.clone();
			this.enfants.add(childClone);
		}
	}

	/**
	 * 
	 * @param filtreComp
	 */
	public void ajouterLigneVariable(ALigneVariableFiltreComposant filtreComp) {
		if(filtreComp == null)
			return;
		if(this.enfants == null)
			this.enfants = new ArrayList<AFiltreComposant>(1);
		this.enfants.add(filtreComp);
	}

	public String getNom() {
		return nom;
	}

	/**
	 * 
	 * @param indice
	 */
	public AFiltreComposant getEnfant(int indice) {
		if(this.enfants == null)
			throw new IllegalArgumentException("Invalid child index");
		if(indice < 0 || indice >= this.enfants.size())
			throw new IllegalArgumentException("Invalid child index");
		return this.enfants.get(indice);
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setNom(String newVal){
		nom = newVal;
	}

	@Override
	public void supprimer(int indice) {
		
	}
	
	
	/**
	 * 
	 * @param filtreComp
	 */
	public void supprimer(ALigneVariableFiltreComposant filtreComp) {
		if(this.enfants == null || filtreComp == null)
			return;
		this.enfants.remove(filtreComp);
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

	@Override
	public void ajouter(AFiltreComposant filtreComp) {
		if(filtreComp == null)
			return;
		if(this.enfants == null)
			this.enfants = new ArrayList<AFiltreComposant>(1);
		this.enfants.add(filtreComp);
	}

	@Override
	public AFiltreComposant getEnfant(AFiltreComposant comp) {
		//  Auto-generated method stub
		return null;
	}
	
	public int getEnfantCount() {
		if(this.enfants == null)
			return 0;
		return this.enfants.size();
	}

	@Override
	public void removeAll() {
		if(this.enfants != null)
			this.enfants.clear();
	}

	@Override
	public void supprimer(AFiltreComposant filtreComp) {
		if(this.enfants == null || filtreComp == null)
			return;
		this.enfants.remove(filtreComp);
	}

	public LigneVariableFiltreComposite clone() {
		return new LigneVariableFiltreComposite(this);
	}
}