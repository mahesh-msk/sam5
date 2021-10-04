package com.faiveley.samng.principal.sm.parseurs;

import java.io.IOException;

import org.apache.xmlbeans.XmlException;

import com.faiveley.samng.principal.ihm.vues.configuration.AGestionnaireConfigurationVue;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 04-févr.-2008 14:29:17
 */
public interface IParseurConfigurationVue extends IParseurInterface {

	


	public AGestionnaireConfigurationVue chargerConfigurationVue();

	/**
	 * 
	 * @param config
	 * @throws IOException 
	 * @throws XmlException 
	 */
	public void enregistrerConfigurationVue(AGestionnaireConfigurationVue config) throws XmlException, IOException;



}