package com.faiveley.kvbdecoder.model.atess.event;

import java.util.ArrayList;
import java.util.List;


import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.faiveley.kvbdecoder.exception.xml.XMLException;
import com.faiveley.kvbdecoder.services.loader.AtessLoaderService;
import com.faiveley.kvbdecoder.services.message.MessageService;



/**
 * Entité créée depuis les fichiers XML Atess et qui décrit la composition d'un événement donné.
 * 
 * @author jthoumelin
 *
 */
public class DescriptorEvent {
	public static final String XML_ATESS_EVENEMENT_ATTRIBUTE_CODE_TAG = "code";
	private static final String XML_ATESS_EVENEMENT_ATTRIBUTE_NOM_TAG = "nom";
	private static final String XML_ATESS_NOMUTILISATEUR_ATTRIBUTE_LIBELLE_TAG = "libelle";

	private int code;
	
	private String name;
	
	private String libelle;
	
	private int length;
	
	private List<String> variables;
 	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
		
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public List<String> getVariables() {
		return variables;
	}

	public DescriptorEvent(int code, String name, String libelle) {
		this.code = code;
		this.name = name;
		this.libelle = libelle;
		variables = new ArrayList<String>();
	}
	
	public void addVariable(String code) {
		variables.add(code);
	}
	
	/**
	 * Construit un descripteur d'événement à partir d'un noeud XML
	 * 
	 * @param n : le noeud XML
	 * @return le descripteur
	 */
	public static DescriptorEvent buildDescriptorEventFromXML(Node n) throws XMLException {
		int code = -1;
		String name = null;
		String libelle = null;
		
		NamedNodeMap attributes = n.getAttributes();
		NamedNodeMap nomUtilisateurAttributes = n.getFirstChild().getAttributes();
		
		for (int i = 0; i < attributes.getLength(); i++) {
			Node att = attributes.item(i);
			String attName = att.getNodeName();
			String attValue = att.getTextContent();
					
			if (attName.equals(XML_ATESS_EVENEMENT_ATTRIBUTE_CODE_TAG)) {
				try {
					code = Integer.parseInt(attValue);
				} catch (NumberFormatException e) {
					throw new XMLException(AtessLoaderService.XML_FILE_ATESS_NAME, MessageService.ERROR_XMLLOADING_ATESS__INVALID_EVENT_ATTRIBUTE_CODE, AtessLoaderService.XML_ATESS_EVENT_TAG, XML_ATESS_EVENEMENT_ATTRIBUTE_CODE_TAG, attValue);
				}
			} else if (attName.equals(XML_ATESS_EVENEMENT_ATTRIBUTE_NOM_TAG)) {
				name = attValue;
			}
		}
		
		for (int j = 0; j < nomUtilisateurAttributes.getLength(); j++) {
			Node att = nomUtilisateurAttributes.item(j);
					
			if (att.getNodeName().equals(XML_ATESS_NOMUTILISATEUR_ATTRIBUTE_LIBELLE_TAG)) {
				libelle = att.getTextContent();
			}
		}
		
		if (code != -1 && name != null && libelle != null) {
			return new DescriptorEvent(code, name, libelle);
		}
		
		return null;
	}
}
