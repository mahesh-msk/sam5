package com.faiveley.samng.principal.ihm.vues.configuration;

import com.faiveley.samng.principal.sm.data.descripteur.Type;

/**
 * @author olivier
 * @version 1.0
 * @created 06-déc.-2007 11:23:55
 */
public class ConfigurationColonne {
	private Type typeValeur = Type.string;
	private boolean affiche;
	private boolean isKVB = false;
		
	/**
	 * Largeur de colonne
	 */
	private int largeur;
	
	/**
	 * Nom unique de la variable correspondant à la colonne
	 */
	private String nom;
	
	/**
	 * Computed width of the columns that have automatic width 
	 */
	private int largeurCalculee;
	
	private boolean isVolatile;

	public ConfigurationColonne() {}

	public ConfigurationColonne(ConfigurationColonne col) {
		if (col != null) {
			this.affiche = col.affiche;
			this.largeur = col.largeur;
			this.nom = col.nom;
			this.largeurCalculee = col.largeurCalculee;
			this.isKVB = col.isKVB;
			this.isVolatile = col.isVolatile;
		}
	}

	/**
	 * Largeur de colonne
	 */
	public int getLargeur() {
		return largeur;
	}

	public String getNom() {
		return nom;
	}

	public boolean isAffiche() {
		return affiche;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setAffiche(boolean newVal) {
		affiche = newVal;
	}

	/**
	 * Set largeur de colonne
	 * 
	 * @param newVal
	 */
	public void setLargeur(int newVal) {
		largeur = newVal;
		
		if(largeur > 0) {
			largeurCalculee = 0;
		}
	}

	/**
	 * @param newVal
	 */
	public void setNom(String newVal) {
		nom = newVal;
	}
	
	public ConfigurationColonne clone() {
		return new ConfigurationColonne(this);
	}

	public int getLargeurCalculee() {
		return largeurCalculee;
	}

	public void setLargeurCalculee(int largeurCalculee) {
		this.largeurCalculee = largeurCalculee;
	}

	public Type getTypeVar() {
		return typeValeur;
	}

	public void setTypeVar(Type typeValeur) {
		if (typeValeur == null) {
			typeValeur = Type.string;
		}
		
		this.typeValeur = typeValeur;
	}
	
	public boolean isKVB() {
		return this.isKVB;
	}
	
	public void setIsKVB(boolean isKVB) {
		this.isKVB = isKVB;
	}

	public boolean isVolatile() {
		return isVolatile;
	}

	public void setVolatile(boolean isVolatile) {
		this.isVolatile = isVolatile;
	}
}