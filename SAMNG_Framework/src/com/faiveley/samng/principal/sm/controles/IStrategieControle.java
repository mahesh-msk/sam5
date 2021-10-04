package com.faiveley.samng.principal.sm.controles;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:11:13
 */
public interface IStrategieControle {

	/**
	 * 
	 * @param crc2
	 * @param donnees
	 */
	public boolean controlerCRC(int crc2, byte[] donnees);

}