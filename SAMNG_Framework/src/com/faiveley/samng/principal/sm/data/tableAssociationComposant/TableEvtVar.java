package com.faiveley.samng.principal.sm.data.tableAssociationComposant;


/**
 * - du 2ème au n+1 bloc
 * - fait correspondre des événempent à des variables(1 événement pour n
 * variables)
 * - utilisée pour l’analyse en complément du fichier XML (ou autre) qui sera
 * générique et qui
 * contiendra la description des variables (type, la caractéristique booléenne
 * discrète ou
 * continue, le type d’affichage, le nom utilisateur par défaut, le poids du 1er
 * octet, le poids
 * du 1er bit, éventuellement sa décomposition en sous variable (champ de bits)).
 * 
 * tableau associatif: codeEvt et tableau de codeVar
 * @author Oiry Hervé
 * @version 1.0
 * @created 02-oct.-2007 13:11:34
 */
public class TableEvtVar extends AParcoursComposant {

	private ATableAssociationComposant m_ATableAssociationComposant;

	
	public ATableAssociationComposant getM_ATableAssociationComposant() {
		return this.m_ATableAssociationComposant;
	}

	public void setM_ATableAssociationComposant(
			ATableAssociationComposant tableAssociationComposant) {
		this.m_ATableAssociationComposant = tableAssociationComposant;
	}
	

}