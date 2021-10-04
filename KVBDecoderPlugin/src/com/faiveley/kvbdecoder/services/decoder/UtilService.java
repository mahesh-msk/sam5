package com.faiveley.kvbdecoder.services.decoder;

import java.util.Collection;
import java.util.Iterator;

import com.faiveley.kvbdecoder.exception.decoder.InformationPointDecoderException;
import com.faiveley.kvbdecoder.model.kvb.marker.Marker;
import com.faiveley.kvbdecoder.services.message.MessageService;


/**
 * Service de méthodes utilitaires
 * 
 * @author jthoumelin
 *
 */
public class UtilService {
	private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
		
	/**
	 * Conversion de bytes en une chaîne.
	 * 
	 * @param bytes : les bytes
	 * @return la chaîne
	 */
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	   
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    
	    return new String(hexChars);
	}
	
	/**
	 * Indique si un X n'est pas un X valide pour une balise. Envoi d'une exception si invalide.
	 * 
	 * @param x : le X
	 * @throws InformationPointDecoderException
	 */
	public static void isValidX(int x) throws InformationPointDecoderException {
		if(!(x == Marker.MARKER_M_VALUE || x == 1 || x == 3 || x == 4 || x == 5 || x == 6 || x == 7 || x == 8 || x == 9 || x == 13 || x == 14)) {
			throw new InformationPointDecoderException(MessageService.ERROR_IP_DECODING__INVALID_X_VALUE, String.valueOf(x), Marker.MARKER_M);
		}
	}
	
	/**
	 * Indique si un X est un X de balise X=1 ou 4
	 * 
	 * @param x : le X
	 * @return le résultat
	 */
	public static boolean isX1X4(int x) {
		return x == 1 || x == 4;
	}
	
	/**
	 * Indique si un X est un X de balise X=3, 6 ou 9
	 * 
	 * @param x : le X
	 * @return le résultat
	 */
	public static boolean isX3X6X9(int x) {
		return x == 3 || x == 6||  x == 9;
	}
	
	/**
	 * Indique si un X est un X de balise X=14
	 * 
	 * @param x : le X
	 * @return le résultat
	 */
	public static boolean isX14(int x) {
		return x == 14;
	}
	
	/**
	 * Indique si une chaîne est un nombre. Le nombre peut être précédé d'un symbole (+ ou -) et ne pas être entier.
	 * 
	 * @param value : la valeur
	 * @return : le résultat
	 */
	public static boolean isNumber(String value) {
		return value.matches("(\\+|-)?\\d+(\\.\\d+)?");
	}
	
	/**
	 * Équivalent de String.join en Java 8
	 * 
	 * @param col
	 * @param delim
	 * @return
	 */
	public static String join(String delim, Collection<?> col) {
	    StringBuilder sb = new StringBuilder();
	    Iterator<?> iter = col.iterator();
	    
	    if (iter.hasNext()) {
	        sb.append(iter.next().toString());
	    }
	    
	    while (iter.hasNext()) {
	        sb.append(delim);
	        sb.append(iter.next().toString());
	    }
	    
	    return sb.toString();
	}
}
