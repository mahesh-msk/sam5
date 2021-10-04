package com.faiveley.samng.principal.sm.data.descripteur;

public class OffsetComposant {

	private int bitOffset;
	private int byteOffset;
	public int getBitOffset() {
		return bitOffset;
	}
	public void setBitOffset(int bitOffset) {
		this.bitOffset = bitOffset;
	}
	public int getByteOffset() {
		return byteOffset;
	}
	
	public void setByteOffset(int byteOffset) {
		this.byteOffset = byteOffset;
	}
	
	public OffsetComposant() {
	}
	public OffsetComposant(int bitOffset, int byteOffset) {
		this.bitOffset = bitOffset;
		this.byteOffset = byteOffset;
	}
}
