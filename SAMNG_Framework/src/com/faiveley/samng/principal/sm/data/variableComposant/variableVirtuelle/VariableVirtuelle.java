package com.faiveley.samng.principal.sm.data.variableComposant.variableVirtuelle;

import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 24-d�c.-2007 14:36:25
 */
public class VariableVirtuelle extends VariableComposite {

	private Operateur m_Operateur;
	private Object valeurObjet;
	
	/**
	 * Repr�sente le niveau d'une VBV: le niveau d'une VBV est le niveau de ses
	 * op�randes augment� de 1 un op�rande qui n'est pas une VBV a un niveau nul
	 * si une VBV contient une autre VBV avec deux op�randes non VBV, la
	 * premi�re VBV est de niveau 2
	 * 
	 */
	private int niveauVBV;

	public VariableVirtuelle() {
	}

	@Override
	public Object getValeurObjet() {
		return valeurObjet;
	};
	
	public void setValeurObjet(Object valeurObjet) {
		this.valeurObjet = valeurObjet;
	}
	
	public int getNiveauVBV() {
		return niveauVBV;
	}

	public void setNiveauVBV(int niveauVBV) {
		this.niveauVBV = niveauVBV;
	}

	public Operateur getM_Operateur() {
		return m_Operateur;
	}

	public void setM_Operateur(Operateur operateur) {
		m_Operateur = operateur;
	}

	public void clear() {
		m_Operateur = null;
		if (this.m_AVariableComposant != null)
			this.m_AVariableComposant.clear();
	}

	/**
	 * M�thode de recherche d'une variable dans une VBV
	 * 
	 * @param variable
	 *            : variable o� l'on doit chercher
	 * @param v
	 *            : variable � trouver
	 * @return true si variable trouve false sinon
	 */
	public static boolean contenirVariable(VariableVirtuelle v,
			AVariableComposant variable) {

		int nbEnfant = v.getVariableCount();
		String nomVarChercher = variable.getDescriptor()
				.getM_AIdentificateurComposant().getNom();

		for (int i = 0; i < nbEnfant; i++) {
			AVariableComposant enfant = v.getEnfant(i);
			if (enfant instanceof AVariableComposant) {
				if (nomVarChercher.equals(enfant.getDescriptor()
						.getM_AIdentificateurComposant().getNom()))
					return true;
			}
		}

		return false;

	}
}