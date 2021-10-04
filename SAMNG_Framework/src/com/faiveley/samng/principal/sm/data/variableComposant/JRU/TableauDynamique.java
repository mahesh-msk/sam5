package com.faiveley.samng.principal.sm.data.variableComposant.JRU;

import java.util.ArrayList;
import java.util.List;

import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.Paquets;
import com.faiveley.samng.principal.sm.data.variableComposant.TableSousVariable;

/**
 * @author olive
 * @version 1.0
 * @created 02-nov.-2010 16:43:06
 */
public class TableauDynamique extends VariableDynamique {
	
	public TableauDynamique() {
	}

	/**
	 * Récupère le nombre de répétitions de la table de sous-variable qui
	 * correpond normalement à la valeur de la variable d'entete.
	 */
	public int getNombreRepetitionsTableSousVariable() {
		return 0;
	}

	/**
	 * Récupère une instance de TableSousVariable. Il s'agit de la table de
	 * sous-variable de reference c'est à dire sans les valeurs des
	 * sous-variable.
	 */
	public TableSousVariable getTableSousVariableReferencee() {
		if (m_AVariableComposant != null && m_AVariableComposant.size() > 0)
			return (TableSousVariable) m_AVariableComposant.get(0);
		else
			return null;
	}

	@Override
	public TableauDynamique copy() {
		TableauDynamique tableauDyn = new TableauDynamique();
		tableauDyn.setDescripteur(this.getDescriptor());
		tableauDyn.setVariableEntete(this.getVariableEntete());
		if (this.m_AVariableComposant != null) {
			tableauDyn.m_AVariableComposant = new ArrayList<AVariableComposant>(this.m_AVariableComposant.size());
			for (AVariableComposant varC : this.m_AVariableComposant) {
				tableauDyn.m_AVariableComposant.add(varC.copy());
			}
		}
		return tableauDyn;
	}

	@Override
	public AVariableComposant[] getEnfants() {

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
		for (AVariableComposant composant : listeEnfantsTableauDynamique) {
			tableauEnfants[i] = composant;
			i++;
		}
		}
		
		return tableauEnfants;
	}

}