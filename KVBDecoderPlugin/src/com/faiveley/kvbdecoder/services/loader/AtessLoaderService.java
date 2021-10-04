package com.faiveley.kvbdecoder.services.loader;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;


import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.faiveley.kvbdecoder.exception.xml.XMLException;
import com.faiveley.kvbdecoder.model.atess.event.DescriptorEvent;
import com.faiveley.kvbdecoder.model.atess.variable.DescriptorVariable;
import com.faiveley.kvbdecoder.model.kvb.xml.XMLFile;
import com.faiveley.kvbdecoder.services.message.MessageService;
import com.faiveley.kvbdecoder.services.xml.XMLService;
import com.faiveley.kvbdecoder.services.xml.XMLService.XmlFileContent;
import com.faiveley.kvbdecoder.services.xml.sam.CRC16Xml.CrcValue;


/**
 * Service qui charge les données ATESS depuis les fichiers ATESS.xml et ConfigurationATESS.xml.
 * 
 * @author jthoumelin
 *
 */
public class AtessLoaderService {
	// ATESS.xml
	public static final String XML_FILE_ATESS_NAME = "ATESS.xml";
	private static final String XML_ATESS_BODY_TAG = "enregistreur";
	public static final String XML_ATESS_EVENT_TAG = "evenement";
	private static final String XML_ATESS_EVENT_PATH = "//liste-evenements/" + XML_ATESS_EVENT_TAG;
	private static final String XML_ATESS_VARIABLE_PATH = "//liste-variables/*[starts-with(name(), 'variable')]";
	
	// ConfigurationATESS.xml
	private static final String XML_FILE_CONFIGURATIONATESS_NAME = "ConfigurationATESS.xml";
	private static final String XML_CONFIGURATIONATESS_BODY_TAG = "table-evenements-variables";
	
	private static final String XML_CONFIGURATIONATESS_EVENEMENTVARIABLES_TAG = "evenement-variables";
	private static final String XML_CONFIGURATIONATESS_EVENEMENTVARIABLES_PATH = "//table-evenements-variables/" + XML_CONFIGURATIONATESS_EVENEMENTVARIABLES_TAG;
	private static final String XML_CONFIGURATIONATESS_EVENEMENTVARIABLES_ATTRIBUTE_CODEEVT_TAG = "code-evt";
	private static final String XML_CONFIGURATIONATESS_EVENEMENTVARIABLES_ATTRIBUTE_LONGUEUR_TAG = "longueur";
	
	private static final String XML_CONFIGURATIONATESS_VARIABLEATESS_ATTRIBUTE_CODEVAR_TAG = "code-var";
	
	private static final String XML_CONFIGURATIONATESS_IDENTIFIANT_TAG = "identifiant";
	private static final String XML_CONFIGURATIONATESS_IDENTIFIANT_PATH = "//liste-identifiants-etendus/" + XML_CONFIGURATIONATESS_IDENTIFIANT_TAG;
	private static final String XML_CONFIGURATIONATESS_IDENTIFIANT_ATTRIBUTE_CODE_TAG = "code";
	private static final String XML_CONFIGURATIONATESS_IDENTIFIANT_ATTRIBUTE_NBOCTETS_TAG = "nb_octets";
	
	private XMLFile XMLFileConfigurationATESS;
	private XMLFile XMLFileATESS;
	
	// Les objets ATESS, stockés dans des maps de hashage et identifiables par leurs codes
	private Map<String, DescriptorEvent> atessEvents = null;
	private Map<String, DescriptorVariable> atessVariables = null;
	private Map<String, Integer> atessExtendedIds = null; // Utilisés pour déterminer le code de l'événement
	
	/**
	 * Le singleton
	 */
	private static AtessLoaderService SERVICE_INSTANCE = new AtessLoaderService();
	
	/**
	 * Le constructeur vide
	 */
	private AtessLoaderService() {}
	
	/**
	 * Obtention du singleton
	 */
	public static AtessLoaderService getServiceInstance() {
		return SERVICE_INSTANCE;
	}	
	
	public Map<String, DescriptorEvent> getAtessEvents() {
		return atessEvents;
	}

	public Map<String, DescriptorVariable> getAtessVariables() {
		return atessVariables;
	}

	public Map<String, Integer> getAtessExtendedIds() {
		return atessExtendedIds;
	}
	
	/**
	 * Charge toutes les données ATESS des fichiers ATESS.xml et ConfigurationATESS.xml
	 * @param checkAtessFilesCrc : booléen qui indique si la vérification du CRC doit être faite pour les fichiers autres que TablesKVB.xml (ex: depuis SAM, non ; depuis autre: oui)
	 * @return la valeur de la vérification du CRC de chaque fichier (0 si OK, le CRC indiqué dans le fichier XML sinon)
	 * @throws XMLException 
	 */
	public CrcValue[] loadAllAtessData(boolean checkAtessFilesCrc) throws XMLException {
		XmlFileContent atessContent = XMLService.getServiceInstance().getXmlFileContent(XML_FILE_ATESS_NAME, XML_ATESS_BODY_TAG, checkAtessFilesCrc);
		XMLFileATESS = new XMLFile(atessContent.getDocument());
		
		XmlFileContent configurationAtessContent = XMLService.getServiceInstance().getXmlFileContent(XML_FILE_CONFIGURATIONATESS_NAME, XML_CONFIGURATIONATESS_BODY_TAG, checkAtessFilesCrc);
		XMLFileConfigurationATESS = new XMLFile(configurationAtessContent.getDocument());

		loadAtessData();
		loadConfigurationAtessData();
		loadExtendedIds();
		
		return new CrcValue[] {atessContent.getCRCValue(), configurationAtessContent.getCRCValue()};
	}
	
	/**
	 * Charge toutes les données ATESS du fichier ATESS.xml, dans les maps atessEvents et atessVariables.
	 * @throws XMLException 
	 */
	private void loadAtessData() throws XMLException {
		atessEvents = new HashMap<String, DescriptorEvent>();
		atessVariables = new HashMap<String, DescriptorVariable>();
		
		// Construction des descripteurs d'événéments
		XPathExpression expr;
		try {
			expr = XMLFileATESS.getXpath().compile(XML_ATESS_EVENT_PATH);
			NodeList events = (NodeList) expr.evaluate(XMLFileATESS.getDoc(), XPathConstants.NODESET);
						
			for (int i = 0; i < events.getLength(); i++) {
				DescriptorEvent event = DescriptorEvent.buildDescriptorEventFromXML(events.item(i));
				
				if (event != null) {
					atessEvents.put(String.valueOf(event.getCode()), event);
				}
			}
		} catch (XPathExpressionException e) {
			throw new XMLException(e, XML_FILE_ATESS_NAME, null);
		}
		
		// Construction des descripteurs de variables
		try {
			expr = XMLFileATESS.getXpath().compile(XML_ATESS_VARIABLE_PATH);
			NodeList variables = (NodeList) expr.evaluate(XMLFileATESS.getDoc(), XPathConstants.NODESET);
		
			for (int i = 0; i < variables.getLength(); i++) {
				DescriptorVariable d = DescriptorVariable.buildDescriptorVariableFromXML(variables.item(i));
				
				if (d != null) {
					atessVariables.put(String.valueOf(d.getCode()), d);
				}
			}
		} catch (XPathExpressionException e) {
			throw new XMLException(e, XML_FILE_ATESS_NAME, null);
		}
	}
	
	/**
	 * Effectue le lien entre les événements et les variables, à partir du fichier ConfigurationATESS.xml.
	 * @throws XMLException 
	 */
	private void loadConfigurationAtessData() throws XMLException {		
		XPathExpression expr;
		try {
			expr = XMLFileConfigurationATESS.getXpath().compile(XML_CONFIGURATIONATESS_EVENEMENTVARIABLES_PATH);
			NodeList eventVariableAssociations = (NodeList) expr.evaluate(XMLFileConfigurationATESS.getDoc(), XPathConstants.NODESET);
			
			for (int i = 0; i < eventVariableAssociations.getLength(); i++) {
				Node association = eventVariableAssociations.item(i);
				NamedNodeMap attributes = association.getAttributes();
				
				String codeEvent = null;
				int longueur = -1;

				// Détermination de l'événement à lier
				for (int j = 0; j < attributes.getLength(); j++) {
					String attName = attributes.item(j).getNodeName();
					String attValue = attributes.item(j).getTextContent();

					if (attName.equals(XML_CONFIGURATIONATESS_EVENEMENTVARIABLES_ATTRIBUTE_CODEEVT_TAG)) {
						codeEvent = attValue;
					} else if (attName.equals(XML_CONFIGURATIONATESS_EVENEMENTVARIABLES_ATTRIBUTE_LONGUEUR_TAG)) {
						try {
							longueur = Integer.parseInt(attValue);
						} catch (NumberFormatException e) {
							throw new XMLException(e, AtessLoaderService.XML_FILE_ATESS_NAME, MessageService.ERROR_XMLLOADING_ATESS__INVALID_EVENT_ATTRIBUTE_LONGUEUR, XML_CONFIGURATIONATESS_EVENEMENTVARIABLES_TAG, XML_CONFIGURATIONATESS_EVENEMENTVARIABLES_ATTRIBUTE_LONGUEUR_TAG, attValue);
						}
					}
				}
				
				if (codeEvent != null && atessEvents.get(codeEvent) != null) {
					DescriptorEvent e = atessEvents.get(codeEvent);
					e.setLength(longueur * 2);
					NodeList variables = association.getFirstChild().getChildNodes();
					
					// Réalisation du lien avec chacune de "ses" variables
					for (int k = 0; k < variables.getLength(); k++) {
						Node var = variables.item(k);
						
						NamedNodeMap vAttributes = var.getAttributes();
						
						for (int l = 0; l < vAttributes.getLength(); l++) {
							if (vAttributes.item(l).getNodeName().equals(XML_CONFIGURATIONATESS_VARIABLEATESS_ATTRIBUTE_CODEVAR_TAG)) {
								e.addVariable(vAttributes.item(l).getTextContent());
								break;
							}
						}
					}
				}
			}
		} catch (XPathExpressionException e) {
			throw new XMLException(e, XML_FILE_CONFIGURATIONATESS_NAME, null);
		}
	}
	
	/**
	 * Charge les identifiants étendus depuis le fichier ConfigurationATESS.xml.
	 * @throws XMLException 
	 */
	private void loadExtendedIds() throws XMLException {
		atessExtendedIds = new HashMap<String, Integer>();
		
		XPathExpression expr;
		try {
			expr = XMLFileConfigurationATESS.getXpath().compile(XML_CONFIGURATIONATESS_IDENTIFIANT_PATH);
			NodeList extendedIds = (NodeList) expr.evaluate(XMLFileConfigurationATESS.getDoc(), XPathConstants.NODESET);

			for (int i = 0; i < extendedIds.getLength(); i++) {
				Node id = extendedIds.item(i);
				NamedNodeMap attributes = id.getAttributes();
				
				String code = null;
				String nb_octets = null;
				
				for (int j = 0; j < attributes.getLength(); j++) {
					String name = attributes.item(j).getNodeName();
					String value = attributes.item(j).getTextContent();

					if (name.equals(XML_CONFIGURATIONATESS_IDENTIFIANT_ATTRIBUTE_CODE_TAG)) {
						code = value;
					} else if (name.equals(XML_CONFIGURATIONATESS_IDENTIFIANT_ATTRIBUTE_NBOCTETS_TAG)) {
						nb_octets = value;
					}
				}
				
				if (code != null && nb_octets != null) {
					try {
						atessExtendedIds.put(code, Integer.parseInt(nb_octets));
					} catch (NumberFormatException e) {
						throw new XMLException(e, AtessLoaderService.XML_FILE_ATESS_NAME, MessageService.ERROR_XMLLOADING_ATESS__INVALID_EXTENDEDID_ATTRIBUTE_NBOCTETS, XML_CONFIGURATIONATESS_IDENTIFIANT_TAG, XML_CONFIGURATIONATESS_IDENTIFIANT_ATTRIBUTE_NBOCTETS_TAG, nb_octets);
					}
				}
			}
		} catch (XPathExpressionException e) {
			throw new XMLException(e, XML_FILE_CONFIGURATIONATESS_NAME, null);
		}
	}
}
