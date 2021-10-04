package com.faiveley.kvbdecoder.services.json;

import com.faiveley.kvbdecoder.model.kvb.marker.Marker;

/**
 * Classe utilitaire pour g�rer les objets JSON.
 * 
 * @author jthoumelin
 *
 */
public class JSONService {
	// Input - xml
	public static final String JSON_XMLLOADER_PATH_LABEL = "xmlPath";
		
	// Output - xml
	public static final String JSON_XMLLOADER_XMLLOADED_LABEL = "xmlLoaded";
	public static final String JSON_XMLLOADER_WARNING_LABEL = "warning";
	public static final String JSON_XMLLOADER_ERROR_LABEL = "errors";
		
	// Input - d�codeur
	public static final String JSON_DECODER_TRAINCATEGORY_LABEL = "category";
	public static final String JSON_DECODER_TRAINDIRECTION_LABEL = "way";
	
	// Output - d�codeur
	public static final String JSON_DECODER_EVENT_LABEL = "event";
	public static final String JSON_DECODER_ERRORS_LABEL = "errors";
	public static final String JSON_DECODER_IP_LABEL = "IP";
	public static final String JSON_DECODER_IPTYPE_LABEL = "IPType";
		
	public static final String JSON_MARKER_VALUES_SEPARATOR = "; ";

	/**
	 * Indique si une balise doit �tre ignor�e ou non lors de l'�criture du JSON
	 * 
	 * @param m : la balise
	 * @return : le r�sultat
	 */
	public static boolean isMarkerToIgnore(Marker m) {
		int x = m.getX();
		return x == -1 || x == 8 || x == 13;
	}
}