package com.faiveley.kvbdecoder.model.kvb.ip;

/**
 * Énumération sur les groupements possibles (voir tables X=3, X=8 et X=13)
 * 
 * @author jthoumelin
 *
 */
public enum GroupingEnum {
	G("G"),
	GS1("G,S1"),
	GS3("G,S3"),
	GS1S2S3("G,S1,S2,S3");
	
	private String value;
	  
	GroupingEnum(String value) {
		this.value = value;
	}
	  
	public String toString() {
		return value;
	}
}
