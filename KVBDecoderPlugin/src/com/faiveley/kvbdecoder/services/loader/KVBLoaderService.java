package com.faiveley.kvbdecoder.services.loader;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.faiveley.kvbdecoder.exception.KVBException;
import com.faiveley.kvbdecoder.exception.decoder.TrainCategoryDecoderException;
import com.faiveley.kvbdecoder.exception.model.train.TrainCategoryEnumException;
import com.faiveley.kvbdecoder.exception.xml.XMLException;
import com.faiveley.kvbdecoder.model.kvb.marker.Marker;
import com.faiveley.kvbdecoder.model.kvb.train.TrainCategoryEnum;
import com.faiveley.kvbdecoder.model.kvb.train.TrainData;
import com.faiveley.kvbdecoder.model.kvb.xml.DecodingTable;
import com.faiveley.kvbdecoder.model.kvb.xml.DescriptorNumericalInformationPoint;
import com.faiveley.kvbdecoder.model.kvb.xml.KVBXmlVariable;
import com.faiveley.kvbdecoder.model.kvb.xml.XMLFile;
import com.faiveley.kvbdecoder.services.message.MessageService;
import com.faiveley.kvbdecoder.services.xml.XMLService;
import com.faiveley.kvbdecoder.services.xml.XMLService.XmlFileContent;
import com.faiveley.kvbdecoder.services.xml.sam.CRC16Xml.CrcValue;

public class KVBLoaderService {		
	public static final String XML_FILE_TABLESKVB_NAME = "TablesKVB.xml";
	public static final String XML_BODY_TAG = "body";
	
	private static final String XML_TABLE_LABELS_TAG = "tableLabels";
	private static final String XML_TEXT_TAG = "text";
	private static final String XML_TEXT_ATTRIBUTE_CODE_TAG = "code";
	private static final String XML_LABEL_TAG = "label";
	private static final String XML_LABEL_ATTRIBUTE_LANG_TAG = "lang";
	public static final String XML_LABEL_ATTRIBUTE_LANG_VALUE_DEF = "DEF";

	public static final String XML_TABLE_DECODAGE_TAG = "tableDecodage";
	public static final String XML_TABLE_DECODAGE_ATTRIBUTE_CODE_TAG = "code";
	private static final String XML_TABLE_DECODAGE_ATTRIBUTE_UNITE_TAG = "unite";
	
	private static final String XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_TRAIN = "Train";
	private static final String XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_IP_NUMERICAL = "PointInformationNumerique";
	public static final String XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X1X4 = "X1X4";
	public static final String XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X1X4VE = "X1X4VE";
	public static final String XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X1X4VB = "X1X4VB";
	public static final String XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X3 = "X3";
	public static final String XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X5 = "X5";
	public static final String XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X6 = "X6";
	public static final String XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X7 = "X7";
	public static final String XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X8X13BALISESIGNEVARIABLE = "X8X13BaliseSigneVariable";
	public static final String XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X8X13ECART = "X8X13Ecart";
	public static final String XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X9 = "X9";
	public static final String XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X14GENERAL = "X14General";
	public static final String XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X14TIVD = "X14TIVD";
	public static final String XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X14DECLIVITE = "X14Declivite";
	public static final String XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X14INCLINAISON = "X14Inclinaison";
		
	// Train category
	private static final String XML_TRAIN_CATEGORIE_TAG = "categorie";
	private static final String XML_TRAIN_CATEGORIE_ATTRIBUTE_VALEUR_TAG = "valeur";
	private static final String XML_TRAIN_CATEGORIE_ATTRIBUTE_VITESSELIMITE_TAG = "vitesseLimite";
	private static final String XML_TRAIN_LTE_TAG = "lte";
	private static final String XML_TRAIN_GT_TAG = "gt";
	
	// Numerical information point
	private static final String XML_NUMERICAL_IP_XSM_XCS_TAG = "xsm_xcs";
	private static final String XML_NUMERICAL_IP_XSM_XCS_ATTRIBUTE_XSM_TAG = "xsm";
	private static final String XML_NUMERICAL_IP_XSM_XCS_ATTRIBUTE_XCS_TAG = "xcs";
	private static final String XML_NUMERICAL_IP_X_TAG = "X";
	private static final String XML_NUMERICAL_IP_QUARTET_TAG = "quartet";
	
	// X Général
	public static final String XML_TABLE_DECODAGE_XVAR_Y_TAG = "Y";
	public static final String XML_TABLE_DECODAGE_XVAR_Z_TAG = "Z";
	private static final String XML_XVAR_ATTRIBUTE_VALEUR_TAG = "valeur";
	
	// X=1, 4, 8, 13
	public static final String XML_TABLE_DECODAGE_XVAR_X_TAG = "X";
	
	// X=3, 6, 7
	public static final String XML_TABLE_DECODAGE_XVAR_LIMITATION_TAG = "limitation";
	public static final String XML_TABLE_DECODAGE_XVAR_GROUPEMENT_TAG = "groupement";

	// X=5
	public static final String XML_TABLE_DECODAGE_XVAR_DIRECTION_TAG = "direction";
	
	// X=8, 13
	public static final String XML_TABLE_DECODAGE_XVAR_CATEGORIE_TAG = "categorie";
	public static final String XML_TABLE_DECODAGE_XVAR_BALISE_TAG = "S";
	public static final String XML_TABLE_DECODAGE_XVAR_ECART_TAG = "ecart";
	public static final String XML_TABLE_DECODAGE_XVAR_SIGNE_TAG = "signe";
	public static final String XML_TABLE_DECODAGE_XVAR_YZ_TAG = "Y_Z";
	public static final String XML_TABLE_DECODAGE_XVAR_Y_ATTRIBUTE_VALEUR_VALUE_0 = "0";
	public static final String XML_TABLE_DECODAGE_XVAR_Y_ATTRIBUTE_VALEUR_VALUE_NOT0 = "!0";

	// X=14
	public static final String XML_TABLE_DECODAGE_XVAR_YDIST_TAG = "Ydist";
	public static final String XML_TABLE_DECODAGE_XVAR_YDECL_TAG = "Ydecl";
	
	private XMLFile XMLFileTablesKVB = null;
	
	// Les tables de décodage KVB
	private Map<String, TrainInformation> trainTable;
	private Map<String, Map<String, String>> labelsTable;
	private Map<Integer, DescriptorNumericalInformationPoint> numericalIpDescriptorTableKeyXsm;
	private Map<Integer, DescriptorNumericalInformationPoint> numericalIpDescriptorTableKeyXcs;
	private Map<String, DecodingTable> decodingTables;
	
	/**
	 * Le singleton
	 */
	private static KVBLoaderService SERVICE_INSTANCE = new KVBLoaderService();
	
	/**
	 * Le constructeur vide
	 */
	private KVBLoaderService() {}
	
	/**
	 * Obtention du singleton
	 */
	public static KVBLoaderService getServiceInstance() {
		return SERVICE_INSTANCE;
	}
		
	public Map<String, TrainInformation> getTrainTable() {
		return trainTable;
	}
	
	public Map<String, String> getInformationPointLabels(String code) {		
		for (Entry<String, Map<String, String>> entry : labelsTable.entrySet()) {				    
			String key = entry.getKey();
			
			if (key.startsWith("^") && key.endsWith("$")) {
				if (Pattern.matches(entry.getKey(), code)) {
				    return entry.getValue();
				}				    
			}
		}
		
		return null;
	}
	
	public String getLabel(String code, String lang) {
	    if(labelsTable != null){
		Map<String, String> labels = labelsTable.get(code);
		
		if (labels != null) {
			String label = labelsTable.get(code).get(lang);
			
			if (label == null) {
				label = labelsTable.get(code).get(XML_LABEL_ATTRIBUTE_LANG_VALUE_DEF);
			}
			
			return label;
		} else {
			return String.format(MessageService.getServiceInstance().getString(MessageService.WARNING_LOCALISATION_UNKNOWN_KEY, KVBException.class), code, XML_FILE_TABLESKVB_NAME);
		}
	    }
	    else{
		return code;
	    }
		
	}
		
	public Map<Integer, DescriptorNumericalInformationPoint> getNumericalIpDescriptorTableKeyXsm() {
		return numericalIpDescriptorTableKeyXsm;
	}
	
	public Map<Integer, DescriptorNumericalInformationPoint> getNumericalIpDescriptorTableKeyXcs() {
		return numericalIpDescriptorTableKeyXcs;
	}
	
	public DecodingTable getDecodingTable(String tableName) {
		return decodingTables.get(tableName);
	}
	
	/**
	 * Charge toutes les tables de décodage KVB dans des structures
	 * @return la valeur de la vérification du CRC du fichier (0 si OK, le CRC indiqué dans le fichier XML sinon)
	 * @throws XMLException 
	 * @throws TrainCategoryEnumException 
	 */
	public CrcValue loadAllData() throws XMLException {
		XmlFileContent tablesKVBContent = XMLService.getServiceInstance().getXmlFileContent(XML_FILE_TABLESKVB_NAME, XML_BODY_TAG, true);		
		XMLFileTablesKVB = new XMLFile(tablesKVBContent.getDocument());
		
		labelsTable = buildLabelsTable();
		trainTable = buildTrainTable();
		
		Map<Integer, DescriptorNumericalInformationPoint>[] numericalIpDescriptorTables = buildNumericalIpDecodingTables();
		numericalIpDescriptorTableKeyXsm = numericalIpDescriptorTables[0];
		numericalIpDescriptorTableKeyXcs = numericalIpDescriptorTables[1];

		decodingTables = new HashMap<String, DecodingTable>();
		decodingTables.put(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X1X4, buildDecodingTable(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X1X4));
		decodingTables.put(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X1X4VE, buildDecodingTable(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X1X4VE));
		decodingTables.put(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X1X4VB, buildDecodingTable(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X1X4VB));
		decodingTables.put(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X3, buildDecodingTable(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X3));
		decodingTables.put(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X5, buildDecodingTable(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X5));
		decodingTables.put(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X6, buildDecodingTable(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X6));
		decodingTables.put(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X7, buildDecodingTable(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X7));
		decodingTables.put(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X8X13BALISESIGNEVARIABLE, buildDecodingTable(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X8X13BALISESIGNEVARIABLE));
		decodingTables.put(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X8X13ECART, buildDecodingTable(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X8X13ECART));
		decodingTables.put(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X9, buildDecodingTable(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X9));
		decodingTables.put(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X14GENERAL, buildDecodingTable(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X14GENERAL));
		decodingTables.put(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X14TIVD, buildDecodingTable(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X14TIVD));
		decodingTables.put(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X14DECLIVITE, buildDecodingTable(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X14DECLIVITE));
		decodingTables.put(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X14INCLINAISON, buildDecodingTable(XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X14INCLINAISON));
		
		return tablesKVBContent.getCRCValue();
	}
	
	/**
	 * Construit une table de labels (clef: langue, valeur: label) à partir de nodes XML.
	 * Seuls les nodes de type <label> sont considérés.
	 * 
	 * @param nList: les nodes
	 * @return la table de labels
	 */
	private Map<String, String> buildLabelsMap(NodeList nList) {
		Map<String, String> labels = new HashMap<String, String>();
		
		for (int i = 0; i < nList.getLength(); i++) {				
			Node label = nList.item(i);
			
			if (label.getNodeName().equals(XML_LABEL_TAG)) {
				NamedNodeMap labelAttributes = label.getAttributes();
				Node lang = labelAttributes.getNamedItem(XML_LABEL_ATTRIBUTE_LANG_TAG);
								
				if (lang != null) {
					labels.put(lang.getTextContent(), label.getTextContent());
				}
			}
		}
		
		return labels;
	}
	
	/**
	 * Construit la table des labels.
	 * 
	 * @return la table des labels
	 * @throws XMLException
	 */
	private Map<String, Map<String, String>> buildLabelsTable() throws XMLException {
		Map<String, Map<String, String>> table = new HashMap<String, Map<String, String>>();
		
		try {
			XPathExpression expr = XMLFileTablesKVB.getXpath().compile(String.format("//%s/%s", XML_TABLE_LABELS_TAG, XML_TEXT_TAG));
			NodeList nList = (NodeList) expr.evaluate(XMLFileTablesKVB.getDoc(), XPathConstants.NODESET);
		
			for (int i = 0; i < nList.getLength(); i++) {				
				Node node = nList.item(i);
				NamedNodeMap nodeAttributes = node.getAttributes();
				String code = nodeAttributes.getNamedItem(XML_TEXT_ATTRIBUTE_CODE_TAG).getTextContent();
				table.put(code, buildLabelsMap(node.getChildNodes()));		
			}
		} catch (XPathExpressionException e) {
			throw new XMLException(e, XML_FILE_TABLESKVB_NAME, null);
		}
				
		return table;
	}
		
	/**
	 * Construit la table pour la détermination de la catégorie de train
	 * 
	 * @return la table
	 * @throws XMLException 
	 * @throws TrainCategoryEnumException 
	 */
	private Map<String, TrainInformation> buildTrainTable() throws XMLException {
		Map<String, TrainInformation> table = new HashMap<String, TrainInformation>();
		
		try {
			XPathExpression expr = XMLFileTablesKVB.getXpath().compile(String.format("//%s[@%s='%s']/%s", XML_TABLE_DECODAGE_TAG, XML_TABLE_DECODAGE_ATTRIBUTE_CODE_TAG, XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_TRAIN, XML_TRAIN_CATEGORIE_TAG));
			NodeList nList = (NodeList) expr.evaluate(XMLFileTablesKVB.getDoc(), XPathConstants.NODESET);
			
			for (int i = 0; i < nList.getLength(); i++) {				
				Node node = nList.item(i);
				NamedNodeMap nodeAttributes = node.getAttributes();
								
				String trainClass = nodeAttributes.getNamedItem(XML_TRAIN_CATEGORIE_ATTRIBUTE_VALEUR_TAG).getTextContent();
				TrainInformation ti = new TrainInformation(trainClass);
				
				String limitSpeed = nodeAttributes.getNamedItem(XML_TRAIN_CATEGORIE_ATTRIBUTE_VITESSELIMITE_TAG).getTextContent();
				
				if (!limitSpeed.isEmpty()) {
					try {
						ti.setLimitSpeed(Long.parseLong(limitSpeed));
					} catch (NumberFormatException e) {
						throw new XMLException(e, XML_FILE_TABLESKVB_NAME, MessageService.ERROR_XMLLOADING_KVB__INVALID_CATEGORIE_ATTRIBUTE_VITESSELIMITE, XML_TRAIN_CATEGORIE_TAG, XML_TRAIN_CATEGORIE_ATTRIBUTE_VITESSELIMITE_TAG, limitSpeed);
					}
				}
				
				NodeList children = node.getChildNodes();
								
				for (int j = 0; j < children.getLength(); j++) {
					Node child = children.item(j);

 					String name = child.getNodeName();
					String value = child.getTextContent();

					if (!value.isEmpty()) {
						try {
							if (name.equals(XML_TRAIN_LTE_TAG)) {
								ti.setCategoryLte(TrainData.trainCategoryFromString(value));
							} else if (name.equals(XML_TRAIN_GT_TAG)) {
								ti.setCategoryGt(TrainData.trainCategoryFromString(value));
							}
						} catch (TrainCategoryEnumException e) {}
					}
				}
				
				table.put(trainClass, ti);
			}
		} catch (XPathExpressionException e) {
			throw new XMLException(e, XML_FILE_TABLESKVB_NAME, null);
		}
		
		return table;
	}
	
	/**
	 * Construit la table de décodage pour les points d'informations numériques
	 * 
	 * @return la table
	 * @throws XMLException 
	 */
	@SuppressWarnings("unchecked")
	private Map<Integer, DescriptorNumericalInformationPoint>[] buildNumericalIpDecodingTables() throws XMLException {
		Map<Integer, DescriptorNumericalInformationPoint> tableKeyXsm = new HashMap<Integer, DescriptorNumericalInformationPoint>();
		Map<Integer, DescriptorNumericalInformationPoint> tableKeyXcs = new HashMap<Integer, DescriptorNumericalInformationPoint>();

		try {
			XPathExpression expr = XMLFileTablesKVB.getXpath().compile(String.format("//%s[@%s='%s']/%s", XML_TABLE_DECODAGE_TAG, XML_TABLE_DECODAGE_ATTRIBUTE_CODE_TAG, XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_IP_NUMERICAL, XML_NUMERICAL_IP_XSM_XCS_TAG));
			NodeList nList = (NodeList) expr.evaluate(XMLFileTablesKVB.getDoc(), XPathConstants.NODESET);
			
			for (int i = 0; i < nList.getLength(); i++) {				
				Node node = nList.item(i);
				NamedNodeMap nodeAttributes = node.getAttributes();
				
				String xsmValue = nodeAttributes.getNamedItem(XML_NUMERICAL_IP_XSM_XCS_ATTRIBUTE_XSM_TAG).getTextContent();
				int xsm = -1;
				
				try {
					xsm = Integer.parseInt(xsmValue);
				} catch (NumberFormatException e) {
					throw new XMLException(e, XML_FILE_TABLESKVB_NAME, MessageService.ERROR_XMLLOADING_KVB__INVALID_XSM_XCS_ATTRIBUTE_VALEUR, XML_NUMERICAL_IP_XSM_XCS_TAG, XML_NUMERICAL_IP_XSM_XCS_ATTRIBUTE_XSM_TAG, xsmValue);
				}
				
				String xcsValue = nodeAttributes.getNamedItem(XML_NUMERICAL_IP_XSM_XCS_ATTRIBUTE_XCS_TAG).getTextContent();
				int xcs = -1;
				
				try {
					if (!xcsValue.isEmpty()) {
						xcs = Integer.parseInt(xcsValue);
					}
				} catch (NumberFormatException e) {
					throw new XMLException(e, XML_FILE_TABLESKVB_NAME, MessageService.ERROR_XMLLOADING_KVB__INVALID_XSM_XCS_ATTRIBUTE_VALEUR, XML_NUMERICAL_IP_XSM_XCS_TAG, XML_NUMERICAL_IP_XSM_XCS_ATTRIBUTE_XCS_TAG, xsmValue);
				}
								
				DescriptorNumericalInformationPoint piDescriptor = new DescriptorNumericalInformationPoint(xsm, xcs);
				
				NodeList childs = node.getChildNodes();
				
				// Construction de la séquence des X et de la liste des quartets
				for (int j = 0; j < childs.getLength(); j++) {
					Node child = childs.item(j);
					String childValue = child.getTextContent();
					String childName = child.getNodeName();
	
					if (childName.equals(XML_NUMERICAL_IP_X_TAG)) {
						int x = childValue.equals(Marker.MARKER_M) ? Marker.MARKER_M_VALUE : Integer.parseInt(childValue);
						piDescriptor.addX(x); // Ajout d'un X
					} else if (childName.equals(XML_NUMERICAL_IP_QUARTET_TAG)) {
						piDescriptor.addQuartet(childValue); // Ajout d'un quartet
					}
				}
								
				if (xsm != -1) {
					tableKeyXsm.put(xsm, piDescriptor);
				}
				
				if (xcs != -1) {
					tableKeyXcs.put(xcs, piDescriptor);
				}
			}
		} catch (XPathExpressionException e) {
			throw new XMLException(e, XML_FILE_TABLESKVB_NAME, null);
		}
				
		return new Map[] {tableKeyXsm, tableKeyXcs};
	}

	/**
	 * Construit une table de décodage pour X=?
	 * 
	 * @param tableCode : le code de la table
	 * @return la table
	 * @throws XMLException 
	 */
	private DecodingTable buildDecodingTable(String tableCode) throws XMLException {		
		DecodingTable decodingTable = new DecodingTable();
		Map<String, KVBXmlVariable> table = new HashMap<String, KVBXmlVariable>();
		
		try {
			XPathExpression expr = XMLFileTablesKVB.getXpath().compile(String.format("//%s[@%s='%s']", XML_TABLE_DECODAGE_TAG, XML_TABLE_DECODAGE_ATTRIBUTE_CODE_TAG, tableCode));			
			NodeList nList = (NodeList) expr.evaluate(XMLFileTablesKVB.getDoc(), XPathConstants.NODESET);
			Node nTable = nList.item(0);
			
			if (nTable != null) {
				// Informations sur la table
				decodingTable.setCode(tableCode);
				
				if (nTable.hasAttributes()) {					
					Node tableUnit = nTable.getAttributes().getNamedItem(XML_TABLE_DECODAGE_ATTRIBUTE_UNITE_TAG);

					if (tableUnit != null) {
						decodingTable.setUnit(tableUnit.getTextContent());
					}
				}
				
				// Ajout de ses fils autres que les labels, c'est à dire les objets de type KVBXmlVariable, à la table
				if (nTable.hasChildNodes()) {
					NodeList childs = nTable.getChildNodes();
					
					for (int i = 0; i < childs.getLength(); i++) {
						Node child = childs.item(i);						
						
						if (!child.getNodeName().equals(XML_LABEL_TAG)) {
							KVBXmlVariable v = buildKVBXmlVariable(child);
							table.put(v.getId(), v);
						}
					}
				}
			}
		} catch (XPathExpressionException e) {
			throw new XMLException(e, XML_FILE_TABLESKVB_NAME, null);
		}
		
		decodingTable.setTable(table);
		
		return decodingTable;
	}
	
	/**
	 * Construit un objet KVBXmlVariable à partir d'un node du fichier XML
	 * 
	 * @param node : le node
	 * @return l'objet KVBXmlVariable
	 */
	private KVBXmlVariable buildKVBXmlVariable(Node node) {
		String type = node.getNodeName();
		KVBXmlVariable v = new KVBXmlVariable(type);
		
		if (node.hasAttributes()) {
			Node attributeValeur = node.getAttributes().getNamedItem(XML_XVAR_ATTRIBUTE_VALEUR_TAG);		

			if (attributeValeur != null) {
				v.setId(type + attributeValeur.getTextContent());
			}
		}
		
		if (v.getId() == null) {
			v.setId(type);
		}
		
		// Ajout de ses fils à l'objet
		
		int nbChilds = 0;
		
		if (node.hasChildNodes()) {
			NodeList childsNextLevel = node.getChildNodes();
			
			for (int i = 0; i < childsNextLevel.getLength(); i++) {
				Node nodeChild = childsNextLevel.item(i);
				
				if (nodeChild.getNodeType() != 3) { // Différent de TEXT
					KVBXmlVariable child = buildKVBXmlVariable(childsNextLevel.item(i));
					v.addChild(child);
					nbChilds++;
				}
			}
		}
		
		// Si pas de fils, détermination de son contenu
		if (nbChilds == 0) {
			v.setContent(node.getTextContent());
		}
		
		return v;
	}
		
	public class TrainInformation {
		private String trainClass;
		private long limitSpeed = -1;
		private TrainCategoryEnum categoryGt = null;
		private TrainCategoryEnum categoryLte = null;
		
		public TrainInformation(String trainClass) {
			this.trainClass = trainClass;
		}
		
		public long getLimitSpeed() {
			return limitSpeed;
		}

		public void setLimitSpeed(long limitSpeed) {
			this.limitSpeed = limitSpeed;
		}

		public void setCategoryGt(TrainCategoryEnum categoryGt) {
			this.categoryGt = categoryGt;
		}

		public void setCategoryLte(TrainCategoryEnum categoryLte) {
			this.categoryLte = categoryLte;
		}

		public TrainCategoryEnum getCategory(long speed) throws TrainCategoryDecoderException {
			if (this.limitSpeed > -1) {
				if (speed <= this.limitSpeed) {
					if (this.categoryLte != null) {
						return this.categoryLte;
					} else { // Cette exception n'est pas levée lors du chargement XML, car toutes les classes de train n'ont pas nécessairement une catégorie dans le cas où la vitesse est inférieure ou égale à la vitesse limite.
						throw new TrainCategoryDecoderException(MessageService.ERROR_TC_DECODING__MISSING_LTE_CATEGORY, trainClass);
					}
				} else {
					if (this.categoryGt != null) {
						return this.categoryGt;
					} else { // Cette exception n'est pas levée lors du chargement XML, car toutes les classes de train n'ont pas nécessairement une catégorie dans le cas où la vitesse est supérieur.
						throw new TrainCategoryDecoderException(MessageService.ERROR_TC_DECODING__MISSING_GT_CATEGORY, trainClass);
					}
				}
			} else { // Cette exception n'est pas levée lors du chargement XML, car toutes les classes de train n'ont pas nécessairement une vitesse limite.
				throw new TrainCategoryDecoderException(MessageService.ERROR_TC_DECODING__MISSING_LIMIT_SPEED, trainClass);
			}
		}
	}
}
