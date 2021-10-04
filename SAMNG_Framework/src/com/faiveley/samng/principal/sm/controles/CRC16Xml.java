package com.faiveley.samng.principal.sm.controles;

import com.faiveley.samng.principal.sm.controles.util.CRC16XmlHash;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:10:34
 */
public class CRC16Xml implements IStrategieControle {

	public CRC16Xml(){

	}

	/**
	 * 
	 * @param crc2
	 * @param donnees
	 */
	public boolean controlerCRC(int crc2, byte[] donnees){
		boolean ret = true;
		CRC16XmlHash crc = new CRC16XmlHash();
        crc.add(donnees, 0, donnees.length);
        byte[] result = crc.get();
        result[0] = (byte)((result[0] + 256) % 128);
        result[1] = (byte)((result[1] + 256) % 128);
        byte crcByte0 = (byte)((crc2>>8) & 0xFF);
        byte crcByte1 = (byte)(crc2 & 0xFF);
        if (result[1]!=crcByte1||result[0]!=crcByte0) {

            ret = false;
        }
		return ret;
	}

}