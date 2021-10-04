package com.faiveley.samng.principal.sm.data.descripteur;

public enum TypeRepere {
	date/*("Date")*/,
    temps/*("Temps")*/,
    distance/*("Distance")*/,
    vitesse/*("vitesse_TOM")*/,
    diametreRoue/*("DiametreRoue")*/,
    tempsCorrigee,
    distanceCorrigee,
    distanceCumulee,
    tempsRelatif,
    distanceRelatif, 
    vitesseCorrigee,
    dateAvantChangement,
    tempsAvantChangement,
    tempsAbsolu;
	
	private String str;
	
	private int code = -1;
	
	private boolean optionnel; 
	
	private TypeRepere() {
	}
	
	public void setName(String value) {
		this.str = value;
	}
	
	public String getName() {
		return this.str;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return this.code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}

	public boolean isOptionnel() {
		return optionnel;
	}

	public void setOptionnel(boolean optionnel) {
		this.optionnel = optionnel;
	}
	
	
	
}
