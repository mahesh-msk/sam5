package com.faiveley.kvbdecoder.services.xml.sam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import com.faiveley.kvbdecoder.exception.xml.XMLException;
import com.faiveley.kvbdecoder.services.xml.sam.CRC16Xml.CrcValue;

/**
 * Service contenant des m�thodes directement copi�es depuis SAM, pouvant �tre adapt�es. Les autres classes du package sont aussi copi�es depuis SAM.
 * 
 * @author jthoumelin
 *
 */
public class XMLSAMService {
	/**
	 * Le singleton
	 */
	private static XMLSAMService SERVICE_INSTANCE = new XMLSAMService();
	
	/**
	 * Le constructeur vide
	 */
	private XMLSAMService() {}
	
	/**
	 * Obtention du singleton
	 */
	public static XMLSAMService getServiceInstance() {
		return SERVICE_INSTANCE;
	}	

	/**
	 * V�rification du CRC du fichier (0 si OK, le CRC indiqu� dans le fichier XML sinon)
	 * 
	 * @param xmlFile
	 * @param CRC
	 * @return
	 * @throws XMLException
	 */
	public CrcValue verifCRC(File xmlFile, int CRC) throws XMLException {
		byte data[][] = initDatasDepuisFichier(xmlFile);
		byte debData[] = data[0];
		byte finData[] = data[1];
		byte totalAux[] = new byte[debData.length + finData.length];
		System.arraycopy(debData, 0, totalAux, 0, debData.length);
		System.arraycopy(finData, 0, totalAux, debData.length, finData.length);
		CRC16Xml control = new CRC16Xml();
		
		return control.controlerCRC(xmlFile, CRC, totalAux);
	}
	
	private byte[][] initDatasDepuisFichier(File xmlFile) {
		Integer intDebFin = null;
		Integer intFinDeb = null;
		byte result[][] = new byte[2][];
		byte debData[] = new byte[0];
		byte finData[] = new byte[0];

		try {
			FileInputStream fis = new FileInputStream(xmlFile);
			byte totalData[] = new byte[fis.available()];
			fis.read(totalData, 0, totalData.length);
			fis.close();
			int tailleaux = "\"".getBytes().length;
			byte aux[];
			
			for (int i = totalData.length - 1 - ("\"".getBytes().length - 1); i >= 0; i--) {
				aux = new byte[tailleaux];
				System.arraycopy(totalData, i, aux, 0, tailleaux);
				
				if (!Arrays.equals(aux, "\"".getBytes())) {
					continue;
				}
				
				finData = new byte[totalData.length - i];
				System.arraycopy(totalData, i, finData, 0, finData.length);
				intDebFin = Integer.valueOf(i);
				
				break;
			}

			aux = null;
			tailleaux = "<signature CRC=\"".getBytes().length;
			
			for (int i = intDebFin.intValue(); i >= 0; i--) {
				aux = new byte[tailleaux];
				
				System.arraycopy(totalData, i, aux, 0, tailleaux);
				
				if (!Arrays.equals(aux, "<signature CRC=\"".getBytes())) {
					continue;
				}
				
				debData = new byte[i + tailleaux];
				System.arraycopy(totalData, 0, debData, 0, debData.length);
				intFinDeb = Integer.valueOf(i + tailleaux);
				
				break;
			}

			if (intFinDeb != null) {
				byte crcData[] = new byte[intDebFin.intValue() - intFinDeb.intValue()];
				System.arraycopy(totalData, intFinDeb.intValue(), crcData, 0, crcData.length);
				result[0] = debData;
				result[1] = finData;
				
				return result;
			}
		} catch (FileNotFoundException ef) {
			System.out.println("fichier introuvable");
		} catch (IOException e) {
			System.out.println(e + "erreur lors de la lecture du fichier");
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}

		result[0] = debData;
		result[1] = finData;
		return result;
	}
}
