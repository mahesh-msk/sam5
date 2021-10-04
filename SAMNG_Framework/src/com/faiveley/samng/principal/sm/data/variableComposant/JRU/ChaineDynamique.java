package com.faiveley.samng.principal.sm.data.variableComposant.JRU;

import java.util.ArrayList;
import java.util.List;

import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.Paquets;
import com.faiveley.samng.principal.sm.data.variableComposant.TableSousVariable;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;

/**
 * @author olive
 * @version 1.0
 * @created 02-nov.-2010 16:43:06
 */
public class ChaineDynamique extends VariableDynamique {

	public ChaineDynamique() {
	}

	/**
	 * R�cup�re une instance de TableSousVariable. Il s'agit de la table de
	 * sous-variable de reference c'est � dire sans les valeurs des
	 * sous-variable.
	 */
	public TableSousVariable getTableSousVariableReferencee() {
		return (TableSousVariable) m_AVariableComposant.get(0);
	}

	@Override
	public String toString() {
		String chaine = "";
		// if a ChaineDynamic has a length equal 0, no character has to be displayed 
		if (m_AVariableComposant != null && m_AVariableComposant.get(0) != null && ((VariableComposite)m_AVariableComposant.get(0)).getEnfant(0) != null) {
			List<AVariableComposant> listeVariables = getTableSousVariableReferencee()
					.getM_AVariableComposant();
		
			for (AVariableComposant composant : listeVariables) {
				if (composant != null && composant.getValeur() != null) {
					chaine += new String((byte[]) composant.getValeur());
				}
			}
		}

		return chaine;
	}

	@Override
	public ChaineDynamique copy() {
		ChaineDynamique chaineDyn = new ChaineDynamique();
		chaineDyn.setDescripteur(this.getDescriptor());
		chaineDyn.setVariableEntete(this.getVariableEntete());
		if (this.m_AVariableComposant != null) {
			chaineDyn.m_AVariableComposant = new ArrayList<AVariableComposant>(
					this.m_AVariableComposant.size());
			for (AVariableComposant varC : this.m_AVariableComposant) {
				chaineDyn.m_AVariableComposant.add(varC.copy());
			}
		}

		return chaineDyn;
	}

	@Override
	public AVariableComposant[] getEnfants() {

		AVariableComposant[] tableauEnfants = null;

		// liste temporaire qui contient tous les enfants du tableau
		List<AVariableComposant> listeEnfantsChaineDynamique = new ArrayList<AVariableComposant>();

		// liste des tables de sous variables du tableau
		List<AVariableComposant> listetableSousVariable = this.m_AVariableComposant;

		if (listetableSousVariable != null) {
			// liste temporaire qui contient tous les sous variable de cuaque
			// table
			// sous variable
			List<AVariableComposant> listeSousVariable = null;
			TableSousVariable tableSousVar = null;

			// parcours de toutes les tables de sous variables qui constituent
			// les occurrences
			for (AVariableComposant composant : listetableSousVariable) {
				tableSousVar = (TableSousVariable) composant;
				listeSousVariable = tableSousVar.getM_AVariableComposant();
				
				if (listeSousVariable != null) {
					for (AVariableComposant variable : listeSousVariable) {
						if (variable instanceof Paquets) {
							if (((Paquets) variable).m_AVariableComposant != null) {
								listeEnfantsChaineDynamique.add(variable);
							}
						} else {
							listeEnfantsChaineDynamique.add(variable);
						}
					}
				}
			}

			tableauEnfants = new AVariableComposant[listeEnfantsChaineDynamique
					.size()];
			int i = 0;
			for (AVariableComposant composant : listeEnfantsChaineDynamique) {
				tableauEnfants[i] = composant;
				i++;
			}
		}

		return tableauEnfants;
	}

}