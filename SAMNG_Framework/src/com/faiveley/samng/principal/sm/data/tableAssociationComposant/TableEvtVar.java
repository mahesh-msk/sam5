package com.faiveley.samng.principal.sm.data.tableAssociationComposant;


/**
 * - du 2�me au n+1 bloc
 * - fait correspondre des �v�nempent � des variables(1 �v�nement pour n
 * variables)
 * - utilis�e pour l�analyse en compl�ment du fichier XML (ou autre) qui sera
 * g�n�rique et qui
 * contiendra la description des variables (type, la caract�ristique bool�enne
 * discr�te ou
 * continue, le type d�affichage, le nom utilisateur par d�faut, le poids du 1er
 * octet, le poids
 * du 1er bit, �ventuellement sa d�composition en sous variable (champ de bits)).
 * 
 * tableau associatif: codeEvt et tableau de codeVar
 * @author Oiry Herv�
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