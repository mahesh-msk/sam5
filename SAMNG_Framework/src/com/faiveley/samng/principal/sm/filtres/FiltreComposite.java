package com.faiveley.samng.principal.sm.filtres;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrateur
 * @version 1.0
 * @created 12-nov.-2007 15:58:20
 */
public class FiltreComposite extends AFiltreComposant {

	private List<AFiltreComposant> enfants = new ArrayList<AFiltreComposant>(0);

	public FiltreComposite(){
	}

	public FiltreComposite(FiltreComposite source) {
		super(source);
		AFiltreComposant childClone;
		for(AFiltreComposant filtre: source.enfants) {
			childClone = filtre.clone();
			this.enfants.add(childClone);
		}
	}

	/**
	 * 
	 * @param filtreComp
	 */
	public void ajouter(AFiltreComposant filtreComp){
		if(filtreComp == null)
			throw new IllegalArgumentException("Invalid filter component");
		this.enfants.add(filtreComp);
	}

	public String getNom(){
		return nom;
	}

	/**
	 * 
	 * @param indice
	 */
	public AFiltreComposant getEnfant(int indice) {
		if(indice < 0 || indice >= this.enfants.size())
			throw new IllegalArgumentException("Invalid filter component");
		return this.enfants.get(indice);
	}
	
	public int getEnfantCount() {
		return this.enfants.size();
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
		this.enfants.remove(indice);
	}
	
	/**
	 * 
	 * @param filtreComp
	 */
	public void supprimer(AFiltreComposant filtreComp) {
		if(filtreComp == null)
			throw new IllegalArgumentException("Invalid filter component");
		this.enfants.remove(filtreComp);
	}
	
	public void removeAll() {
		this.enfants.clear();
	}

	@Override
	public AFiltreComposant getEnfant(AFiltreComposant comp) {
		
		return null;
	}

	@Override
	public FiltreComposite clone() {
		return new FiltreComposite(this);
	}
}