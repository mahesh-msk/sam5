package com.faiveley.kvbdecoder.model.kvb.marker;

public enum NextMarkerIndicatorEnum {
	ANALOG("0"),
	M("1"),
	NONE("2");
	
	private String value;
	  
	NextMarkerIndicatorEnum(String value) {
		this.value = value;
	}
	  
	public String toString() {
		return value;
	}
}
