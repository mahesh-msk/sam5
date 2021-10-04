package com.faiveley.samng.principal.sm.data.tableAssociationComposant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.faiveley.samng.principal.sm.data.variableComposant.LabelValeur;
import com.faiveley.samng.principal.sm.data.variableComposant.Langage;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:11:35
 */
public class TableValeurLabel extends ATableAssociationComposant {

	private HashMap<Langage, ArrayList<LabelValeur>> table = new HashMap<Langage, ArrayList<LabelValeur>>();

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	public ArrayList<LabelValeur> put(Langage arg0, ArrayList<LabelValeur> arg1) {
		return this.table.put(arg0, arg1);
	}
	
	public List<LabelValeur> get(Langage lang) {
		ArrayList<LabelValeur> labels = this.table.get(lang);
		if (labels == null || labels.size() == 0) {
			labels = this.table.get(Langage.DEF);
		}
		return labels;
	}

	/**
	 * @return
	 * @see java.util.HashMap#size()
	 */
	public int size() {
		return this.table.size();
	}
	
	
	/*private Vector<LabelValeur> m_LabelValeur;
	
	private Langage language;
	*/

	/*public TableValeurLabel() {
		this.m_LabelValeur = new Vector<LabelValeur>();
	}
	
	*//**
	 * @param val
	 * @return
	 *//*
	public boolean ajouter(LabelValeur val) {
		return m_LabelValeur.add(val);
	}

	*//**
	 * @param indice
	 * @return
	 *//*
	public LabelValeur getLabel(int indice) {
		return m_LabelValeur.get(indice);
	}

	*//**
	 * @param val
	 * @return
	 *//*
	public boolean supprimer(LabelValeur val) {
		return m_LabelValeur.remove(val);
	}

	*//**
	 * @return the language
	 *//*
	public Langage getLanguage() {
		return language;
	}

	*//**
	 * @param language the language to set
	 *//*
	public void setLanguage(Langage language) {
		this.language = language;
	}*/

	

}