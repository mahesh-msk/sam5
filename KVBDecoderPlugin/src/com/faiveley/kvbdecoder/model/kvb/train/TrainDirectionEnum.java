package com.faiveley.kvbdecoder.model.kvb.train;

public enum TrainDirectionEnum {
	INCONNU("0"),
	SENS_DE_MARCHE("1"),
	CONTRE_SENS("2");
	  
	private String label;
	  
	TrainDirectionEnum(String label) {
		this.label = label;
	}
	  
	public String toString() {
		return label;
	}
}
