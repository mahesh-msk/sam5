package com.faiveley.kvbdecoder.model.kvb.xml;

import java.util.Map;

import com.faiveley.kvbdecoder.exception.decoder.InformationPointDecoderException;
import com.faiveley.kvbdecoder.model.atess.variable.DescriptorVariable;
import com.faiveley.kvbdecoder.services.message.MessageService;


/**
 * Entité qui contient les mêmes informations qu'une table de décodage stockée sous forme XML.
 * 
 * @author jthoumelin
 *
 */
public class DecodingTable {
	private String code = null; // L'équivalent de l'attribut "code" en XML
	private String unit = null; // L'équivalent de l'attribut "unite" en XML
	private Map<String, KVBXmlVariable> table; // L'équivalent des noeuds fils en XML
		
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Map<String, KVBXmlVariable> getTable() {
		return table;
	}
	
	public void setTable(Map<String, KVBXmlVariable> table) {
		this.table = table;
	}
	
	/**
	 * Vérifie que la table dispose d'une unité
	 * 
	 * @param unit : 
	 * @throws InformationPointDecoderException
	 */
	public void checkUnit() throws InformationPointDecoderException {
		if (unit == null) { // Cette exception n'est pas levée lors du chargement XML, car toutes les tables de décodage ne nécessitent pas une unité.
			throw new InformationPointDecoderException(MessageService.ERROR_IP_DECODING__MISSING_UNIT, DescriptorVariable.XML_ATESS_VARIABLES_ATTRIBUTE_UNITE_TAG, code);
		}	
	}
}
