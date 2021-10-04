package com.faiveley.kvbdecoder.model.kvb.train;

public enum TrainCategoryEnum {
	C1("1"),
	C2("2"),
	C3("3"),
	C4("4"),
	C5("5"),
	C6("6"),
	C7("7");
	
	private String value;
	  
	TrainCategoryEnum(String value) {
		this.value = value;
	}
	  
	public String toString() {
		return value;
	}
}
