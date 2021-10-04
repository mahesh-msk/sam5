package com.faiveley.samng.principal.sm.controles;

import com.faiveley.samng.principal.sm.calculs.ConvertByteToHexa;

public class ReturnCRC {
	private byte[] messageCRC;
	private byte[] calculCRC;
	
	public byte[] getCalculCRC() {
		return calculCRC;
	}
	public void setCalculCRC(byte[] calculCRC) {
		this.calculCRC = calculCRC;
	}
	public byte[] getMessageCRC() {
		return messageCRC;
	}
	public void setMessageCRC(byte[] messageCRC) {
		this.messageCRC = messageCRC;
	}
	
	public String getCRCString(byte[] crc){
		String ret="";
		int crcLength=crc.length;
		for (int i = 0; i < crcLength; i++) {
			ret=ret+ConvertByteToHexa.Convert(crc[i]+"");  
		}
		return ret;
	}
	
}
