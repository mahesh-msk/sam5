package com.faiveley.samng.principal.sm.data.variableComposant;

import java.util.ArrayList;
import java.util.List;

/**
 * @author olive
 * @version 1.0
 * @created 02-nov.-2010 16:43:07
 */
public class TableSousVariable extends VariableComposite {

	private String valeur;

	public TableSousVariable() {

	}

	/**
	 * 
	 * @param valeur
	 */
	public void setValeur(String valeur) {
		this.valeur = valeur;
	}

	public String getValeur() {
		return valeur;
	}

	public TableSousVariable copy() {

		TableSousVariable tableSousVar = new TableSousVariable();
		tableSousVar.valeur = this.valeur;
		if (this.m_AVariableComposant != null) {
			List<AVariableComposant> table = new ArrayList<AVariableComposant>(m_AVariableComposant.size());
			for (AVariableComposant variableComposant : this.m_AVariableComposant) {
				table.add(variableComposant.copy());
			}
			tableSousVar.setM_AVariableComposant(table);
		}

		return tableSousVar;
	}

	/**
	 * Retourne le nombre de variables de la table de sous variable
	 * 
	 * @return nombre de variables
	 */
	public int getNbSousVariables() {
		return this.m_AVariableComposant.size();
	}

}