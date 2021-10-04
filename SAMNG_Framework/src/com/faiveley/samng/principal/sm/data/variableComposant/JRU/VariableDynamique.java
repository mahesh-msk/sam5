package com.faiveley.samng.principal.sm.data.variableComposant.JRU;

import java.util.ArrayList;
import java.util.List;

import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.TableSousVariable;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete;

/**
 * @author olive
 * @version 1.0
 * @created 02-nov.-2010 16:43:42
 */
public class VariableDynamique extends VariableComposite {
	private AVariableComposant variableEntete;

	public VariableDynamique() {

	}

	public AVariableComposant getVariableEntete() {
		return this.variableEntete;
	}

	/**
	 * 
	 * @param variableEntete
	 */
	public void setVariableEntete(AVariableComposant variableEntete) {
		this.variableEntete = variableEntete;
	}

	public List<AVariableComposant> getListeTablesSousVariable() {
		return this.m_AVariableComposant;
	}

	public String getValeurHexa() throws Throwable {
		String valHexa = "0x";

		if (this != null && this.getListeTablesSousVariable() == null) {
			VariableDiscrete varEntete = (VariableDiscrete) this.getVariableEntete();
			System.err.println("Table de sous variables vide pour l'élément "
					+ this.getDescriptor().getM_AIdentificateurComposant()
							.getNom()
					+ " de variable d'entête : "
					+ varEntete
							.getDescriptor().getM_AIdentificateurComposant()
							.getNom()
					+ " de valeur "
					+ varEntete
							.getValeurBruteChaineVariableDiscrete());
			return "";
		}

		List<AVariableComposant> listVars = ((TableSousVariable) this
				.getListeTablesSousVariable().get(0)).getM_AVariableComposant();
		int len = listVars.size();
		for (int i = 0; i < len; i++) {
			byte[] tabByte = (byte[]) listVars.get(i).getValeur();
			if (tabByte != null) {
				for (int j = 0; j < tabByte.length; j++) {
					valHexa = valHexa + getHexString(tabByte);
				}
			}
		}

		return valHexa;
	}

	/**
	 * 
	 * @param tableSousVariable
	 */
	public void ajouterTableSousVariable(TableSousVariable tableSousVariable) {
		if (this.m_AVariableComposant == null) {
			this.m_AVariableComposant = new ArrayList<AVariableComposant>(1);
		}
		this.m_AVariableComposant.add(tableSousVariable);
	}

}