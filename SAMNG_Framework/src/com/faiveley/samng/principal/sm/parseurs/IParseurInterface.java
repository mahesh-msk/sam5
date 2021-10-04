package com.faiveley.samng.principal.sm.parseurs;

import java.io.IOException;

import org.apache.xmlbeans.XmlException;

import com.faiveley.samng.principal.sm.erreurs.AExceptionSamNG;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:11:11
 */
public interface IParseurInterface {

	/**
	 * 
	 * @param chemin
	 * @throws IOException 
	 * @throws XmlException 
	 */
	public void parseRessource(String chemin, boolean explorer,int deb,int fin) throws AExceptionSamNG, XmlException, IOException;

}