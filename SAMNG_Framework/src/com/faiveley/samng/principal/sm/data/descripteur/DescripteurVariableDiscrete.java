package com.faiveley.samng.principal.sm.data.descripteur;

import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TableValeurLabel;

public class DescripteurVariableDiscrete extends DescripteurVariable {
	
	private TableValeurLabel labels;

	private int nbDecimales;
	
	private String codageChaine;

	public String getCodageChaine() {
		return codageChaine;
	}
	public void setCodageChaine(String codageChaine) {
		this.codageChaine = codageChaine;
	}
	public int getNbDecimales() {
		return nbDecimales;
	}
	public void setNbDecimales(int nbDecimales) {
		this.nbDecimales = nbDecimales;
	}
	public DescripteurVariableDiscrete() {
		//  Auto-generated constructor stub
	}
	/**
	 * @return the label
	 */
	public TableValeurLabel getLabels() {
		return this.labels;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabels(TableValeurLabel label) {
		this.labels = label;
	}
}
