package com.faiveley.samng.principal.sm.controles;

import com.faiveley.samng.principal.sm.controles.util.CRC16Hash;

public class CRC16 implements IStrategieControle{

	public boolean controlerCRC(int crc2, byte[] donnees) {
		boolean ret = true;
		CRC16Hash crc = new CRC16Hash();
        crc.add(donnees, 0, donnees.length);
        byte[] result = crc.get();
    
        byte crcByte0 = (byte)(crc2 & 0xFF);
        byte crcByte1 = (byte)((crc2>>8) & 0xFF);
       
       
        if (result[0]!=crcByte0||result[1]!=crcByte1) {

            ret = false;
        }
		return ret;
	}

}
