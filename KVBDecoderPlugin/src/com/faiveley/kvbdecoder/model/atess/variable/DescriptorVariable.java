package com.faiveley.kvbdecoder.model.atess.variable;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.faiveley.kvbdecoder.exception.model.variable.VariableTypeEnumException;
import com.faiveley.kvbdecoder.exception.xml.XMLException;
import com.faiveley.kvbdecoder.services.loader.AtessLoaderService;
import com.faiveley.kvbdecoder.services.message.MessageService;




/**
 * Entité créée depuis les fichiers XML Atess et qui décrit la composition d'une variable.
 * 
 * @author jthoumelin
 *
 */
public abstract class DescriptorVariable {
	private static final String XML_ATESS_VARIABLES_ATTRIBUTE_CODE_TAG = "code";
	private static final String XML_ATESS_VARIABLES_ATTRIBUTE_NOM_TAG = "nom";
	private static final String XML_ATESS_VARIABLES_ATTRIBUTE_LIBELLE_TAG = "libelle";
	private static final String XML_ATESS_VARIABLES_ATTRIBUTE_TYPE_TAG = "type";
	private static final String XML_ATESS_VARIABLES_ATTRIBUTE_TAILLE_TAG = "taille";
	public static final String XML_ATESS_VARIABLES_ATTRIBUTE_UNITE_TAG = "unite";
	public static final String XML_ATESS_VARIABLES_ATTRIBUTE_ESCALIER_TAG = "escalier";
	
	private static final String XML_ATESS_VARIABLEDISCRETE_TAG = "variable-discrete";
	private static final String XML_ATESS_VARIABLEANALOGIQUE_TAG = "variable-analogique";
	private static final String XML_ATESS_VARIABLECOMPLEXE_TAG = "variable-complexe";
	private static final String XML_ATESS_SOUSVARIABLE_TAG = "sous-variable";

	protected int code;
	
	protected String name;
	
	protected String libelle;
	
	protected VariableTypeEnum type;
		
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
	
	public VariableTypeEnum getType() {
		return type;
	}

	public void setType(VariableTypeEnum type) {
		this.type = type;
	}

	public DescriptorVariable(int code, String name, String libelle, String type) throws VariableTypeEnumException {
		this.code = code;
		this.name = name;
		this.libelle = libelle;
		
		if (type != null) {
			this.type = VariableTypeEnum.fromString(type);
		}
	}
	
	/**
	 * Construit un descripteur de variable à partir d'un noeud XML
	 * 
	 * @param n : le noeud XML
	 * @return le descripteur
	 */
	public static DescriptorVariable buildDescriptorVariableFromXML(Node n) throws XMLException {
		boolean subVariable = n.getParentNode().getNodeName().equals(XML_ATESS_SOUSVARIABLE_TAG);
		
		int code = -1;
		String name = null;
		String libelle = null;
		String type = null;
		int taille = -1;
		String unit = null;
		boolean escalier = false;
		
		NamedNodeMap attributes = n.getAttributes();
		NamedNodeMap nomUtilisateurAttributes = n.getFirstChild().getAttributes();
		
		for (int i = 0; i < attributes.getLength(); i++) {
			Node att = attributes.item(i);
			String attName = att.getNodeName();
			String attValue = att.getTextContent();
					
			if (attName.equals(XML_ATESS_VARIABLES_ATTRIBUTE_CODE_TAG)) {
				try {
					code = Integer.parseInt(attValue);
				} catch (NumberFormatException e) {
					throw new XMLException(e, AtessLoaderService.XML_FILE_ATESS_NAME, MessageService.ERROR_XMLLOADING_ATESS__INVALID_VARIABLE_ATTRIBUTE_CODE, AtessLoaderService.XML_ATESS_EVENT_TAG, XML_ATESS_VARIABLES_ATTRIBUTE_CODE_TAG, attValue);
				}
			} else if (attName.equals(XML_ATESS_VARIABLES_ATTRIBUTE_NOM_TAG)) {
				name = attValue;
			} else if (attName.equals(XML_ATESS_VARIABLES_ATTRIBUTE_TYPE_TAG)) {
				type = attValue;
			} else if (attName.equals(XML_ATESS_VARIABLES_ATTRIBUTE_TAILLE_TAG)) {
				try {
					taille = Integer.parseInt(attValue);
				} catch (NumberFormatException e) {
					throw new XMLException(e, AtessLoaderService.XML_FILE_ATESS_NAME, MessageService.ERROR_XMLLOADING_ATESS__INVALID_VARIABLE_ATTRIBUTE_TAILLE, AtessLoaderService.XML_ATESS_EVENT_TAG, XML_ATESS_VARIABLES_ATTRIBUTE_TAILLE_TAG, attValue);
				}
			} else if (attName.equals(XML_ATESS_VARIABLES_ATTRIBUTE_UNITE_TAG)) {
				unit = attValue;
			}else if (attName.equals(XML_ATESS_VARIABLES_ATTRIBUTE_ESCALIER_TAG)) {
			    escalier = Boolean.valueOf(attValue).booleanValue();
			}
		}
		
		for (int j = 0; j < nomUtilisateurAttributes.getLength(); j++) {
			Node att = nomUtilisateurAttributes.item(j);
					
			if (att.getNodeName().equals(XML_ATESS_VARIABLES_ATTRIBUTE_LIBELLE_TAG)) {
				libelle = att.getTextContent();
			}
		}
		
		if (n.getNodeName().equals(XML_ATESS_VARIABLEDISCRETE_TAG)) {
			DescriptorDiscreteVariable discreteVariable = new DescriptorDiscreteVariable(code, name, libelle, type);
			
			if (discreteVariable.isValidVariable(subVariable)) {
				return discreteVariable;
			}
		} else if (n.getNodeName().equals(XML_ATESS_VARIABLEANALOGIQUE_TAG)) {
			DescriptorAnalogVariable analogVariable = new DescriptorAnalogVariable(code, name, libelle, type, unit, escalier);
			
			if (analogVariable.isValidVariable(subVariable)) {
				return analogVariable;
			}
		} else if (n.getNodeName().equals(XML_ATESS_VARIABLECOMPLEXE_TAG)) {
			DescriptorComplexVariable complexVariable = new DescriptorComplexVariable(code, name, libelle, type, taille);
			
			if (complexVariable.isValidVariable(false)) {
				NodeList complexChilds = n.getChildNodes();
				
				for (int k = 0; k < complexChilds.getLength(); k++) {
					Node child = complexChilds.item(k);
					
					if (child.getNodeName().equals(XML_ATESS_SOUSVARIABLE_TAG)) {
						Node variable = child.getFirstChild();
						
						if (variable.getNodeName().equals(XML_ATESS_VARIABLEDISCRETE_TAG) || variable.getNodeName().equals(XML_ATESS_VARIABLEANALOGIQUE_TAG)) {
							complexVariable.addSubVariable(buildDescriptorVariableFromXML(variable));
						}
					}
				}
				
				return complexVariable;
			}
		}
		
		return null;
	}
	
	/**
	 * Indique si la variable est valide : si ses champs sont valides
	 * 
	 * @param subVariable : s'il s'agit d'une sous variable ou non
	 * @return la validité
	 */
	protected abstract boolean isValidVariable(boolean subVariable);
}
