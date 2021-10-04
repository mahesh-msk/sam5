package com.faiveley.samng.principal.sm.data.tableAssociationComposant;

import java.util.HashMap;
import java.util.Set;

import com.faiveley.samng.principal.sm.data.variableComposant.Langage;


/**
 * @author meggy
 *
 */
public class TableLangueNomUtilisateur extends ATableAssociationComposant {

	private HashMap<Langage, String> table = new HashMap<Langage, String>();

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	public String setNomUtilisateur(Langage langage, String nomUtilisateur) {
		return this.table.put(langage, nomUtilisateur);
	}

	/**
	 * @return
	 * @see java.util.HashMap#size()
	 */
	public int size() {
		return this.table.size();
	}
	
	/**
	 * @param lang
	 * @return
	 */
	public String getNomUtilisateur(Langage lang) {
		String nom = this.table.get(lang);
		if (nom == null || nom.trim().length() == 0) {
			nom = this.table.get(Langage.DEF);
		}
		return nom;
	}
	
	public Set<Langage> getLanguages() {
		return this.table.keySet();
	}
	
//	private Langage lang;
//	private String nomUtilisateur;
	
	/**
	 * @return the lang
	 */
	/*public Langage getLang() {
		return lang;
	}
	*//**
	 * @param lang the lang to set
	 *//*
	public void setLang(Langage lang) {
		this.lang = lang;
	}
	*//**
	 * @return the nomUtilisateur
	 *//*
	public String getNomUtilisateur() {
		return nomUtilisateur;
	}
	
	*//**
	 * @param nomUtilisateur the nomUtilisateur to set
	 *//*
	public void setNomUtilisateur(String nomUtilisateur) {
		this.nomUtilisateur = nomUtilisateur;
	}
	*/
	
}
