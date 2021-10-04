package com.faiveley.kvbdecoder.model.kvb.xml;

import java.util.Map;

import com.faiveley.kvbdecoder.exception.decoder.InformationPointDecoderException;
import com.faiveley.kvbdecoder.model.atess.variable.DescriptorVariable;
import com.faiveley.kvbdecoder.services.message.MessageService;


/**
 * Entit� qui contient les m�mes informations qu'une table de d�codage stock�e sous forme XML.
 * 
 * @author jthoumelin
 *
 */
public class DecodingTable {
	private String code = null; // L'�quivalent de l'attribut "code" en XML
	private String unit = null; // L'�quivalent de l'attribut "unite" en XML
	private Map<String, KVBXmlVariable> table; // L'�quivalent des noeuds fils en XML
		
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
	 * V�rifie que la table dispose d'une unit�
	 * 
	 * @param unit : 
	 * @throws InformationPointDecoderException
	 */
	public void checkUnit() throws InformationPointDecoderException {
		if (unit == null) { // Cette exception n'est pas lev�e lors du chargement XML, car toutes les tables de d�codage ne n�cessitent pas une unit�.
			throw new InformationPointDecoderException(MessageService.ERROR_IP_DECODING__MISSING_UNIT, DescriptorVariable.XML_ATESS_VARIABLES_ATTRIBUTE_UNITE_TAG, code);
		}	
	}
}
