package com.faiveley.kvbdecoder.services.xml.sam;

import java.io.File;

import com.faiveley.kvbdecoder.exception.xml.XMLException;

public class CRC16Xml {	
	public CRC16Xml() {}

	/**
	 * Vérification du CRC du fichier (0 si OK, le CRC indiqué dans le fichier XML sinon)
	 * 
	 * @param xmlFile
	 * @param crc2
	 * @param donnees
	 * @return
	 * @throws XMLException
	 */
	public CrcValue controlerCRC(File xmlFile, int crc2, byte[] donnees) {
		CRC16XmlHash crc = new CRC16XmlHash();
        crc.add(donnees, 0, donnees.length);
        byte[] result = crc.get();
        result[0] = (byte) ((result[0] + 256) % 128);
        result[1] = (byte) ((result[1] + 256) % 128);
        byte crcByte0 = (byte) ((crc2 >> 8) & 0xFF);
        byte crcByte1 = (byte) (crc2 & 0xFF);
        
       CrcValue crcValue = new CrcValue(xmlFile.getName());
        
        if (result[1] != crcByte1 || result[0] != crcByte0) {
        	// Obtenir valeur calculée:
        	// System.out.println(Integer.toHexString(result[0] * 256 + result[1]).toUpperCase());
        	crcValue.setCrc(crc2);
        }
        
        return crcValue;
	}
	
	public class CrcValue {
		public static final int CHECKED_CRC_CODE = 0;
		
		private String fileName;
		private int crc = CHECKED_CRC_CODE;
		
		public CrcValue(String fileName) {
			this.fileName = fileName;
		}
		
		public String getFileName() {
			return this.fileName;
		}
		
		public void setCrc(int crc) {
			this.crc = crc;
		}
		
		public int getCrc() {
			return this.crc;
		}
		
		public boolean isCheckedCRC() {
			return this.crc == CHECKED_CRC_CODE;
		}
	}
}