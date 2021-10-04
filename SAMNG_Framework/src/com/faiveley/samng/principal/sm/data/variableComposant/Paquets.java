package com.faiveley.samng.principal.sm.data.variableComposant;

import java.util.ArrayList;
import java.util.List;

import com.faiveley.samng.principal.sm.data.variableComposant.JRU.VariableDynamique;



/**
 * @author olive
 * @version 1.0
 * @created 02-nov.-2010 16:43:04
 */
public class Paquets extends VariableDynamique {


	/**
	 * Retourne une instance de TableSousVariable par sa valeur(attribut valeur xml)
	 * cette instance ne contient que la définition des sous-variable, pas leur valeur
	 * 
	 * @param valeur
	 */
	public TableSousVariable getTableSousVariableReferenceByValeur(java.lang.String valeur){
		List<AVariableComposant> listeTableSousVar = getListeTablesSousVariable();
		int i =0;
		boolean trouve = false;
		TableSousVariable tableSousVarRetour = null;
		while(!trouve && i<listeTableSousVar.size()){
			TableSousVariable tableSousVar = (TableSousVariable) listeTableSousVar.get(i);
			if(tableSousVar.getValeur().equals(valeur)){
				trouve = true;
				tableSousVarRetour = tableSousVar;
			}
			i++;
		}
		return tableSousVarRetour;
	}

	@Override
	public Paquets copy(){
		Paquets paquet = new Paquets();
		paquet.setDescripteur(this.getDescriptor());
		paquet.setVariableEntete(this.getVariableEntete());
		if (this.m_AVariableComposant != null) {
			paquet.m_AVariableComposant = new ArrayList<AVariableComposant>(this.m_AVariableComposant.size());
			for (AVariableComposant varC : this.m_AVariableComposant) {
				paquet.m_AVariableComposant.add(varC.copy());
			}
		}
		return paquet;
	}

	public AVariableComposant[] getEnfants(){

		AVariableComposant[] tableauEnfants = null; 

		// liste temporaire qui contient tous les enfants du tableau
		List<AVariableComposant> listeEnfantsTableauDynamique = new ArrayList<AVariableComposant>();

		// liste des tables de sous variables du tableau
		List<AVariableComposant> listetableSousVariable = this.m_AVariableComposant;

		if(listetableSousVariable!=null){
			// liste temporaire qui contient tous les sous variable de cuaque table
			// sous variable
			List<AVariableComposant> listeSousVariable = null;
			TableSousVariable tableSousVar = null;

			//parcours de toutes les tables de sous variables qui constituent les occurrences
			for (AVariableComposant composant : listetableSousVariable) {
				tableSousVar = (TableSousVariable) composant;
				listeSousVariable = tableSousVar.getM_AVariableComposant();

				for (AVariableComposant variable : listeSousVariable) {
					if (variable instanceof Paquets) {
						if (((Paquets) variable).m_AVariableComposant != null) {
							listeEnfantsTableauDynamique.add(variable);
						}
					} else
						listeEnfantsTableauDynamique.add(variable);
				}
			}

			tableauEnfants = new AVariableComposant[listeEnfantsTableauDynamique.size()];
			int i = 0;
			for (AVariableComposant composant : listeSousVariable) {
				tableauEnfants[i] = composant;
				i++;
			}
		}

		return tableauEnfants;
	}
}