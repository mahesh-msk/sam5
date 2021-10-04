package com.faiveley.samng.principal.sm.data.enregistrement;


public enum ErrorType {

	CRC,
	EventId,
	BlockDefaut,
	FFBlockDefault,
	BadBlock,
	XMLRelated,
	BadLength;
	
	int start = 0;
	int length = 0;
	
	public void setStartPos(int start) {
		this.start = start;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public int getStartPosition() {
		return this.start;
	}
	
	public int getLength() {
		return this.length;
	}

}
