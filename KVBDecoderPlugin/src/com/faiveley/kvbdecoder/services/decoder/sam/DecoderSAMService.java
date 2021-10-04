package com.faiveley.kvbdecoder.services.decoder.sam;

import java.math.BigInteger;
import java.util.Map;

import com.faiveley.kvbdecoder.services.loader.AtessLoaderService;

/**
 * Service contenant des méthodes directement copiées depuis SAM, pouvant être adaptées.  Les autres classes du package sont aussi copiées depuis SAM.
 * 
 * @author jthoumelin
 *
 */
public class DecoderSAMService {
	/**
	 * Le singleton
	 */
	private static DecoderSAMService SERVICE_INSTANCE = new DecoderSAMService();
	
	/**
	 * Le constructeur vide
	 */
	private DecoderSAMService() {}
	
	/**
	 * Obtention du singleton
	 */
	public static DecoderSAMService getServiceInstance() {
		return SERVICE_INSTANCE;
	}	
	
	/**
	 * Obtenir l'identifiant de l'événement à partir d'une donnée brute.
	 * 
	 * @param encodedEvent
	 * @return l'identifiant
	 */
	public int gestionID(byte[] encodedEvent) {
		byte[] tabIdEvt = new byte[2];
		tabIdEvt[0] = 0;
		tabIdEvt[1] = encodedEvent[0];
		BigInteger bInt = new BigInteger(tabIdEvt);
		int id = bInt.intValue();
		
		switch (tailleIdentifiant(id)) {
			case 2:
				byte[] tabIdExtEvt = new byte[2];
				tabIdExtEvt[0] = 0;
				tabIdExtEvt[1] = encodedEvent[1];
				BigInteger bIntExt = new BigInteger(tabIdExtEvt);
				int idExt = bIntExt.intValue();
				int tampon = id * 256 + idExt;	
				id = tampon;
				break;
			case 4:
				byte[] tabIdExtEvt2 = new byte[2];
				tabIdExtEvt2[0] = 0;
				tabIdExtEvt2[1] = encodedEvent[1];
				
				byte[] tabIdExtEvt3 = new byte[2];
				tabIdExtEvt3[0] = 0;
				tabIdExtEvt3[1] = encodedEvent[2];
				
				byte[] tabIdExtEvt4 = new byte[2];
				tabIdExtEvt4[0] = 0;
				tabIdExtEvt4[1] = encodedEvent[3];
	
				BigInteger bIntExt2 = new BigInteger(tabIdExtEvt2);
				BigInteger bIntExt3 = new BigInteger(tabIdExtEvt3);
				BigInteger bIntExt4 = new BigInteger(tabIdExtEvt4);
				
				int idExt2 = bIntExt2.intValue();
				int idExt3 = bIntExt3.intValue();
				int idExt4 = bIntExt4.intValue();
				
				int tampon2 = id * 16777216 + idExt2 * 65536 + idExt3 * 256 + idExt4;
				id = tampon2;
				
				break;
			default:
				break;
		}
		
		return id;
	}
	
	/**
	 * 
	 * @param ID
	 * @return
	 */
	private int tailleIdentifiant(int ID) {
		Map<String, Integer> atessExtendedIds = AtessLoaderService.getServiceInstance().getAtessExtendedIds();
		
		for (Map.Entry<String, Integer> entry : atessExtendedIds.entrySet()) {
			String valeurHexa = entry.getKey();
			int intVal = Integer.parseInt(valeurHexa, 16);
			
			if (intVal == ID) {
				return entry.getValue();
			}
		}
				
		return 1;	
	}
}
