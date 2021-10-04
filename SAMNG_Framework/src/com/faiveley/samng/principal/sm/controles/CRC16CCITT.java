package com.faiveley.samng.principal.sm.controles;

import com.faiveley.samng.principal.sm.controles.util.CRC16CCITTHash;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:10:34
 */
public class CRC16CCITT implements IStrategieControle {

	public CRC16CCITT(){

	}

	/**
	 * 
	 * @param crc2
	 * @param donnees
	 */
	public boolean controlerCRC(int crc2, byte[] donnees){
		boolean ret = true;
		CRC16CCITTHash crc = new CRC16CCITTHash();
        crc.add(donnees, 0, donnees.length);
        byte[] result = crc.get();

        byte crcByte0 = (byte)(crc2 & 0xFF);
        byte crcByte1 = (byte)((crc2>>8) & 0xFF);
       
        if (result[0]!=crcByte1||result[1]!=crcByte0) {

            ret = false;
        }
		return ret;
	}

}