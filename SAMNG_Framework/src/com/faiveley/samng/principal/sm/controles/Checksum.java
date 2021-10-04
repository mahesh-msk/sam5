package com.faiveley.samng.principal.sm.controles;

import com.faiveley.samng.principal.sm.calculs.ConvertByteToHexa;


public class Checksum implements IStrategieControle {
	public boolean controlerCRC(int crc2, byte[] donnees) {
		boolean ret = false;

		int chk=(crc2>>8 & 0xFF);
		int complementChk=crc2 & 0xFF;

		byte octet0 = (byte)(donnees[0] & 0xFF);
		byte octet1 = (byte)(donnees[1] & 0xFF);
		byte octet2 = (byte)(donnees[2] & 0xFF);
		byte octet3 = (byte)(donnees[3] & 0xFF);
		byte octet4 = (byte)(donnees[4] & 0xFF);
		byte octet5 = (byte)(donnees[5] & 0xFF);
		byte octet6 = (byte)(donnees[6] & 0xFF);
		byte octet7 = (byte)(donnees[7] & 0xFF);
		byte octet8 = (byte)(donnees[8] & 0xFF);
		byte octet9 = (byte)(donnees[9] & 0xFF);
		byte octet10 = (byte)(donnees[10] & 0xFF);
		byte octet11 = (byte)(donnees[11] & 0xFF);
		byte octet12 = (byte)(donnees[12] & 0xFF);
		byte octet13 = (byte)(donnees[13] & 0xFF);
		byte octet14 = (byte)(donnees[14] & 0xFF);
		byte octet15 = (byte)(donnees[15] & 0xFF);
		byte octet16 = (byte)(donnees[16] & 0xFF);
		byte octet17 = (byte)(donnees[17] & 0xFF);
		byte octet18 = (byte)(donnees[18] & 0xFF);
		byte octet19 = (byte)(donnees[19] & 0xFF);
		byte octet20 = (byte)(donnees[20] & 0xFF);
		byte octet21 = (byte)(donnees[21] & 0xFF);
		byte octet22 = (byte)(donnees[22] & 0xFF);
		byte octet23 = (byte)(donnees[23] & 0xFF);
		byte octet24 = (byte)(donnees[24] & 0xFF);
		byte octet25 = (byte)(donnees[25] & 0xFF);
		byte octet26 = (byte)(donnees[26] & 0xFF);
		byte octet27 = (byte)(donnees[27] & 0xFF);
		byte octet28 = (byte)(donnees[28] & 0xFF);
		byte octet29 = (byte)(donnees[29] & 0xFF);

		Integer checksum30octets=octet0^octet1^octet2^octet3^octet4^octet5^octet6^octet7^octet8^octet9^
				octet10^octet11^octet12^octet13^octet14^octet15^octet16^octet17^octet18^octet19^
				octet20^octet21^octet22^octet23^octet24^octet25^octet26^octet27^octet28^octet29;
		int complementChecksum=0;
		int oppose=checksum30octets.byteValue() ^ 0xFF;
		if(oppose == 255)
			complementChecksum = 0;
		else
		    complementChecksum=oppose + 1;

		if ((chk==checksum30octets && complementChk==complementChecksum) || (chk==-complementChecksum && checksum30octets==-complementChk)) {
			ret=true;
		}else{
			ret = false;
		}

		return ret;
	}	

	public static int getCRC(byte[] donnees) {

		byte octet0 = (byte)(donnees[0] & 0xFF);
		byte octet1 = (byte)(donnees[1] & 0xFF);
		byte octet2 = (byte)(donnees[2] & 0xFF);
		byte octet3 = (byte)(donnees[3] & 0xFF);
		byte octet4 = (byte)(donnees[4] & 0xFF);
		byte octet5 = (byte)(donnees[5] & 0xFF);
		byte octet6 = (byte)(donnees[6] & 0xFF);
		byte octet7 = (byte)(donnees[7] & 0xFF);
		byte octet8 = (byte)(donnees[8] & 0xFF);
		byte octet9 = (byte)(donnees[9] & 0xFF);
		byte octet10 = (byte)(donnees[10] & 0xFF);
		byte octet11 = (byte)(donnees[11] & 0xFF);
		byte octet12 = (byte)(donnees[12] & 0xFF);
		byte octet13 = (byte)(donnees[13] & 0xFF);
		byte octet14 = (byte)(donnees[14] & 0xFF);
		byte octet15 = (byte)(donnees[15] & 0xFF);
		byte octet16 = (byte)(donnees[16] & 0xFF);
		byte octet17 = (byte)(donnees[17] & 0xFF);
		byte octet18 = (byte)(donnees[18] & 0xFF);
		byte octet19 = (byte)(donnees[19] & 0xFF);
		byte octet20 = (byte)(donnees[20] & 0xFF);
		byte octet21 = (byte)(donnees[21] & 0xFF);
		byte octet22 = (byte)(donnees[22] & 0xFF);
		byte octet23 = (byte)(donnees[23] & 0xFF);
		byte octet24 = (byte)(donnees[24] & 0xFF);
		byte octet25 = (byte)(donnees[25] & 0xFF);
		byte octet26 = (byte)(donnees[26] & 0xFF);
		byte octet27 = (byte)(donnees[27] & 0xFF);
		byte octet28 = (byte)(donnees[28] & 0xFF);
		byte octet29 = (byte)(donnees[29] & 0xFF);

		return octet0^octet1^octet2^octet3^octet4^octet5^octet6^octet7^octet8^octet9^
				octet10^octet11^octet12^octet13^octet14^octet15^octet16^octet17^octet18^octet19^
				octet20^octet21^octet22^octet23^octet24^octet25^octet26^octet27^octet28^octet29;
	}	
	
	public static String getCRCString(byte[] crc){
		String ret="";
		int crcLength=crc.length;
		for (int i = 0; i < crcLength; i++) {
			ret=ret+ConvertByteToHexa.Convert(crc[i]+"");  
		}
		return ret;
	}
}
