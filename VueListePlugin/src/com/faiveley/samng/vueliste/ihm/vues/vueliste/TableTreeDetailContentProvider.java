package com.faiveley.samng.vueliste.ihm.vues.vueliste;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.faiveley.samng.principal.sm.data.descripteur.DescripteurChaineDynamique;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurComposite;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.descripteur.Type;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.Paquets;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.ChaineDynamique;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.StructureDynamique;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.TableauDynamique;

public class TableTreeDetailContentProvider implements ITreeContentProvider {
	/**
	 * Ajoute à la liste de variables la variable L_TEXT si une chaine dynamique est dans la liste
	 */
	public Object[] getVariablesEnfantsPlusVariableL_TEXT(Object[] vars) {
		List<AVariableComposant> listeVariableOrdonnees = new ArrayList<AVariableComposant>();
		
		if (vars != null && vars.length != 0) {
			for (int i = 0; i < vars.length; i++) {
				AVariableComposant var = (AVariableComposant) vars[i];
				if (var.getDescriptor() instanceof DescripteurChaineDynamique) {
					listeVariableOrdonnees.add(((ChaineDynamique) var).getVariableEntete());
					listeVariableOrdonnees.add(var);
				} else {
					listeVariableOrdonnees.add(var);
				}
			}
		}
		return listeVariableOrdonnees.toArray();
	}
	
	public Object[] getChildren(Object arg0) {
		try {
			if (arg0 instanceof VariableComplexe) {
				return getVariablesEnfantsPlusVariableL_TEXT(((VariableComplexe) arg0).getEnfantsWithoutReserved());
			} else if (arg0 instanceof StructureDynamique) {
				return getVariablesEnfantsPlusVariableL_TEXT(((StructureDynamique) arg0).getEnfants());
			} else if (arg0 instanceof TableauDynamique) {
				return getVariablesEnfantsPlusVariableL_TEXT(((TableauDynamique) arg0).getEnfants());
			} else if (arg0 instanceof Paquets) {
				return getVariablesEnfantsPlusVariableL_TEXT(((Paquets) arg0).getEnfants());
			}
		} catch (Exception ex) {
			return null;
		}
		return null;
	}

	public Object getParent(Object arg0) {
		return null;
	}

	public boolean hasChildren(Object arg0) {
		try {
			if (arg0 instanceof VariableComplexe) {
				VariableComplexe vc = (VariableComplexe) arg0;
				AVariableComposant[] sousvars = vc.getEnfants();
				
				for (AVariableComposant composant : sousvars) {
					if (composant.getTypeValeur() != Type.reserved) {
						return true;
					}
				}
			} else if (arg0 instanceof StructureDynamique) {
				StructureDynamique structureDyn = (StructureDynamique) arg0;
				return structureDyn.getEnfants() != null;
			} else if (arg0 instanceof TableauDynamique) {
				TableauDynamique tableauDyn = (TableauDynamique) arg0;
				return tableauDyn.getEnfants() != null;
			} else if (arg0 instanceof Paquets) {
				Paquets paquet = (Paquets) arg0;
				return paquet.getEnfants() != null;
			}
		} catch (Exception ex) {
			return false;
		}
		
		return false;
	}

	/**
	 * Gets the elements for the table
	 * 
	 * @param arg0 the model
	 * @return Object[]
	 */
	public Object[] getElements(Object arg0) {
		// Affichage ordonne des variables
		Message msg = ((Message) arg0);
		if (msg.getEvenement() == null) {
			return new ArrayList<AVariableComposant>().toArray();
		}
		
		int codeEvenement = msg.getEvenement().getM_ADescripteurComposant().getM_AIdentificateurComposant().getCode();
		DescripteurComposite descComposite = GestionnaireDescripteurs.getMapEvenementVariables().get(codeEvenement);
		DescripteurVariable descripteurVariable = null;
		List<AVariableComposant> listeVariableOrdonnees = new ArrayList<AVariableComposant>();

		if (descComposite != null) {
			for (int a = 1; a < descComposite.getLength(); a++) {
				descripteurVariable = ((DescripteurVariable) descComposite.getEnfant(a));
				AVariableComposant var = msg.getVariable(descripteurVariable.getM_AIdentificateurComposant().getCode());
				
				if (var != null) {
					if (var.getDescriptor() instanceof DescripteurChaineDynamique) {
						listeVariableOrdonnees.add(((ChaineDynamique) var).getVariableEntete());
						listeVariableOrdonnees.add(var);
					} else {
						listeVariableOrdonnees.add(var);
					}
				}
			}
		}
		
		return listeVariableOrdonnees.toArray();
	}

	/**
	 * Disposes any resources
	 */
	public void dispose() {}

	/**
	 * Called when the input changes
	 * 
	 * @param arg0 the parent viewer
	 * @param arg1 the old input
	 * @param arg2 the new input
	 */
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {}
}