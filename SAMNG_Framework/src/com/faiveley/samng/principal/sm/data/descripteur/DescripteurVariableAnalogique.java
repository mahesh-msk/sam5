package com.faiveley.samng.principal.sm.data.descripteur;

public class DescripteurVariableAnalogique extends DescripteurVariable {
	
	private double coefDirecteur=1.0;
	/**
	 * Ordonnée à l'origine pour mise à l'échelle
	 */
	private double ordonneeOrigine =0;
	
	private String unite;
	
	private boolean escalier = false;
	
	private int nbDecimales=0;
	
	private String codageChaine;
	
	public DescripteurVariableAnalogique() {
	}
	
	public double getCoefDirecteur(){
		return this.coefDirecteur;
	}

	/**
	 * Ordonnée à l'origine pour mise à l'échelle
	 */
	public double getOrdonneeOrigine(){
		return this.ordonneeOrigine;
	}

	public String getUnite(){
		if (this.unite==null)
			return "";
		return this.unite;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setCoefDirecteur(double newVal){
		this.coefDirecteur = newVal;
	}

	/**
	 * Ordonnée à l'origine pour mise à l'échelle
	 * 
	 * @param newVal
	 */
	public void setOrdonneeOrigine(double newVal){
		this.ordonneeOrigine = newVal;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setUnite(String newVal){
		this.unite = newVal;
	}

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

	public boolean isEscalier() {
	    return escalier;
	}

	public void setEscalier(boolean escalier) {
	    this.escalier = escalier;
	}
	
}
