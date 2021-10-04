package com.faiveley.samng.principal.sm.controles.util;

import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosFichierSamNg;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.parseurs.TypeParseur;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.ParseurParcoursAtess;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.ParseurTableAssociationEvVars;
import com.faiveley.samng.principal.sm.parseurs.parseursJRU.ParseurJRUTableAssociationEvVars;
import com.faiveley.samng.principal.sm.parseurs.parseursJRU.ParseurParcoursJRU;

public class XMLName {

	public static String updateCurrentXmlName() {
		String xmlName="";
		try {
			if (TypeParseur.getInstance().getParser() instanceof ParseurParcoursAtess) {
				return ParseurTableAssociationEvVars.getInstance().getFichierDescr();
			}else if (TypeParseur.getInstance().getParser() instanceof ParseurParcoursJRU) {
				return ParseurJRUTableAssociationEvVars.getInstance().getFichierDescr();
			}else{
				InfosFichierSamNg infos;
				try{
					infos = (InfosFichierSamNg)FabriqueParcours.getInstance().getParcours().getInfo();
				}catch(Exception ex){
					infos = ((InfosFichierSamNg) GestionnairePool.getInstance().getXMLParser().getInfosFichier());		
				}			

				xmlName = infos.getNomFichierXml().toLowerCase();
			}
			return xmlName;
		
		} catch (Exception e) {
			return "";
		}
	}
}
