package com.faiveley.samng.principal.sm.controles;


import com.faiveley.samng.principal.sm.controles.util.Crc32Hash;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:10:36
 */
public class CRC32 implements IStrategieControle {

	public CRC32(){

	}

	/**
	 * 
	 * @param crc2
	 * @param donnees
	 */
	public boolean controlerCRC(int crc2, byte[] donnees){
		boolean ret = true;
		Crc32Hash crc = new Crc32Hash();
        crc.add(donnees, 0, donnees.length);
        byte[] result = crc.get();
        byte crcByte0 = (byte)(crc2 & 0xFF);
        byte crcByte1 = (byte)((crc2>>8) & 0xFF);
        byte crcByte2 = (byte)((crc2>>16) & 0xFF);
        byte crcByte3 = (byte)((crc2>>24) & 0xFF);
        if (result[3]!=crcByte0
                          || result[2]!=crcByte1
                          || result[1]!=crcByte2
                          || result[0]!=crcByte3) {
            ret = false;
        }
		return ret;
	}

}