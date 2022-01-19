package com.faiveley.kvbdecoder.services.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.faiveley.kvbdecoder.exception.xml.XMLException;
import com.faiveley.kvbdecoder.exception.xml.XMLFileMissingException;
import com.faiveley.kvbdecoder.services.json.JSONService;
import com.faiveley.kvbdecoder.services.loader.AtessLoaderService;
import com.faiveley.kvbdecoder.services.loader.KVBLoaderService;
import com.faiveley.kvbdecoder.services.message.MessageService;
import com.faiveley.kvbdecoder.services.xml.sam.CRC16Xml.CrcValue;
import com.faiveley.kvbdecoder.services.xml.sam.XMLSAMService;

/**
 * Service pour la gestion des fichiers XML.
 * 
 * @author jthoumelin
 *
 */
public class XMLService {
	public static String XML_FOLDER = "./";
	private static String XML_SIGNATURE_TAG = "signature";
	private static String XML_SIGNATURE_ATTRIBUTE_CRC_TAG = "CRC";

	private boolean dataSuccessfullyLoaded = false; // Indique si les donn�es ont d�j� �t� charg�es
	//private CrcValue[] crcValues = null;
	
	/**
	 * Le singleton
	 */
	private static XMLService SERVICE_INSTANCE = new XMLService();
	
	/**
	 * Le constructeur vide
	 */
	private XMLService() {}
	
	/**
	 * Obtention du singleton
	 */
	public static XMLService getServiceInstance() {
		return SERVICE_INSTANCE;
	}
			
	public boolean isDataSuccessfullyLoaded() {
		return dataSuccessfullyLoaded;
	}

	public void setDataSuccessfullyLoaded(boolean dataSuccessfullyLoaded) {
		this.dataSuccessfullyLoaded = dataSuccessfullyLoaded;
	}

	/**
	 * D�finit l'emplacement des fichiers XML et charge toutes les donn�es
	 * 
	 * @param path : l'emplacement des fichiers XML
	 * @param checkAtessFilesCrc : bool�en qui indique si la v�rification du CRC doit �tre faite pour les fichiers autres que TablesKVB.xml (ex: depuis SAM, non ; depuis autre: oui)
	 * @return la valeur de la v�rification du CRC de chaque fichier (0 si OK, le CRC indiqu� dans le fichier XML sinon)
	 * @throws XMLException 
	 */	
	public CrcValue[] loadAllXMLData(String path, boolean checkAtessFilesCrc) throws XMLException {
		JSONObject jsonObject = new JSONObject(path);
		XML_FOLDER = jsonObject.get(JSONService.JSON_XMLLOADER_PATH_LABEL).toString();
		
		// OPC : json path files are already encoded with "/" -> Must not setup File.separator here ! 
		if (!XML_FOLDER.endsWith("/")) {
			XML_FOLDER += "/";
		}
		
		CrcValue[] atessCrcValues = AtessLoaderService.getServiceInstance().loadAllAtessData(checkAtessFilesCrc);
		CrcValue tablesKVBCrcValue = KVBLoaderService.getServiceInstance().loadAllData();
		
		dataSuccessfullyLoaded = true;
		return new CrcValue[] {atessCrcValues[0], atessCrcValues[1], tablesKVBCrcValue};
	}
	
	/**
	 * Obtention d'un fichier XML au format org.w3c.dom.Document
	 * 
	 * @param fileName : le nom du fichier
	 * @param bodyTag : la balise englobante � consid�rer
	 * @param checkAtessFilesCrc : bool�en qui indique si la v�rification du CRC doit �tre faite pour les fichiers autres que TablesKVB.xml (ex: depuis SAM, non ; depuis autre: oui)
	 * @return le contenu du fichier (objet au format org.w3c.dom.Document + la v�rification du CRC du fichier : 0 si OK, le CRC indiqu� dans le fichier XML sinon) 
	 * @throws XMLException 
	 */
	public XmlFileContent getXmlFileContent(String fileName, String bodyTag, boolean checkAtessFilesCrc) throws XMLException {
		String filePath = XMLService.XML_FOLDER + fileName;
		File xmlFile = new File(filePath);	
		File oneLineXmlFile = null;
		
		try {
		    oneLineXmlFile = File.createTempFile("buffer", ".tmp");
		    FileWriter fw = new FileWriter(oneLineXmlFile);
			
			Reader fr = new FileReader(xmlFile);
		    BufferedReader br = new BufferedReader(fr);
		    		    
		    while(br.ready()) {
		        fw.write(br.readLine().replaceAll("\n", "").replaceAll("\t", ""));
		    }
		    
		    fw.close();
		    br.close();
		    fr.close();
		} catch (FileNotFoundException e) {
			oneLineXmlFile.delete();
			throw new XMLFileMissingException(e, xmlFile.getName(), null);
		} catch (IOException e) {
			oneLineXmlFile.delete();
			throw new XMLException(e, xmlFile.getName(), null);
		}
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setIgnoringComments(true);
		DocumentBuilder dBuilder;
		
		try {
			Document doc = null;
			
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(oneLineXmlFile);
			doc.getDocumentElement().normalize();
			
			XmlFileContent xmlFileContent = new XmlFileContent(doc);
			
			if (checkAtessFilesCrc) {
				Node signature = doc.getElementsByTagName(XML_SIGNATURE_TAG).item(0);
				
				if (signature != null) {
					NamedNodeMap attributes = signature.getAttributes();
					Node crcNode = attributes.getNamedItem(XML_SIGNATURE_ATTRIBUTE_CRC_TAG);
					
					if (crcNode != null) {
						String crcText = crcNode.getTextContent();
						int CRC = -1;
						
						try {
							CRC = Integer.parseInt(crcText, 16);
						} catch (NumberFormatException e) {
							throw new XMLException(e, xmlFile.getName(), MessageService.ERROR_XMLLOADING_INVALID_CRC_ATTRIBUTE, XML_SIGNATURE_TAG, XML_SIGNATURE_ATTRIBUTE_CRC_TAG, crcText);
						}
						
						CrcValue crcvalue = XMLSAMService.getServiceInstance().verifCRC(xmlFile, CRC);
						xmlFileContent.setCRCValue(crcvalue);
					} else {
						throw new XMLException(xmlFile.getName(), MessageService.ERROR_XMLLOADING_MISSING_CRC_ATTRIBUTE, XML_SIGNATURE_ATTRIBUTE_CRC_TAG, XML_SIGNATURE_TAG);
					}
				} else {
					throw new XMLException(xmlFile.getName(), MessageService.ERROR_XMLLOADING_MISSING_SIGNATURE_TAG, XML_SIGNATURE_TAG);
				}
			}
			
			oneLineXmlFile.delete();
			
			return xmlFileContent;
		} catch (ParserConfigurationException e) {
			oneLineXmlFile.delete();
			throw new XMLException(e, xmlFile.getName(), null);
		} catch (SAXException e) {
			oneLineXmlFile.delete();
			throw new XMLException(e, xmlFile.getName(), null);
		} catch (IOException e) {
			oneLineXmlFile.delete();
			throw new XMLException(e, xmlFile.getName(), null);
		}
	}
	
	public class XmlFileContent {
		private Document doc;
		private CrcValue crcValue = null;
		
		public XmlFileContent(Document doc) {
			this.doc = doc;
		}
		
		public Document getDocument() {
			return this.doc;
		}
		
		public void setCRCValue(CrcValue crcValue) {
			this.crcValue = crcValue;
		}
		
		public CrcValue getCRCValue() {
			return this.crcValue;
		}
	}
}
