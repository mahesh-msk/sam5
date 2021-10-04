package com.faiveley.samng.principal.sm.data.descripteur;

import java.util.HashMap;


/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:10:43
 */
public class DescripteurEvenement extends ADescripteurComposant {

	private int longueur;
	private EntreeLogique entreeLogique;
	
	private Temporelle caractTemporelle;

	private int code;
	
	private String nom;
	
	private HashMap<Object, Object> nomUtilisateur;
	
	
	public DescripteurEvenement(){
		this.nomUtilisateur = new HashMap<Object, Object>();
	}

	public Temporelle getCaractTemporelle(){
		return this.caractTemporelle;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setCaractTemporelle(Temporelle newVal){
		this.caractTemporelle = newVal;
	}

	
	public int getCode() {
		return this.code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getNom() {
		return this.nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public HashMap<Object, Object> getNomUtilisateur() {
		return this.nomUtilisateur;
	}

	public void setNomUtilisateur(HashMap<Object, Object> nomUtilisateur) {
		this.nomUtilisateur = nomUtilisateur;
	}

	public EntreeLogique getEntreeLogique() {
		return entreeLogique;
	}

	public void setEntreeLogique(EntreeLogique entreeLogique) {
		this.entreeLogique = entreeLogique;
	}

	public int getLongueur() {
		return longueur;
	}

	public void setLongueur(int longueur) {
		this.longueur = longueur;
	}

}