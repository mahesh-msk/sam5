package com.faiveley.kvbdecoder.model.kvb.marker;

import java.util.ArrayList;
import java.util.List;

import com.faiveley.kvbdecoder.exception.decoder.InformationPointDecoderException;
import com.faiveley.kvbdecoder.model.kvb.ip.InformationPoint;
import com.faiveley.kvbdecoder.model.kvb.marker.MarkerX14.MarkerX14Value;
import com.faiveley.kvbdecoder.model.kvb.marker.MarkerX1X4.MarkerX1X4Value;
import com.faiveley.kvbdecoder.model.kvb.marker.MarkerX1X4.MarkerX1X4ValueType;
import com.faiveley.kvbdecoder.model.kvb.marker.MarkerX3X6X9.MarkerX3X6X9Value;
import com.faiveley.kvbdecoder.model.kvb.xml.DecodingTable;
import com.faiveley.kvbdecoder.services.decoder.UtilService;
import com.faiveley.kvbdecoder.services.json.JSONService;
import com.faiveley.kvbdecoder.services.loader.KVBLoaderService;

public class Marker {	
	public static final int X_Y_Z_DEFAULT_VALUE = -2;
	
	public static final String MARKER_M = "M";
	public static final int MARKER_M_VALUE = -1;

	private InformationPoint parent;
	private int x = X_Y_Z_DEFAULT_VALUE;
	private int y = X_Y_Z_DEFAULT_VALUE;
	private int z = X_Y_Z_DEFAULT_VALUE;
	private String code; // Code de la principale table de décodage correspondante
	protected String unit; // Unite de la principale table de décodage correspondante
	protected List<MarkerValue> values; // Suite de valeurs (numériques ou textuelles) sous forme de chaînes

	// Cas analogique
	public Marker(InformationPoint parent, int x, int y, int z) throws InformationPointDecoderException {
		this.parent = parent;
		
		this.x = x;
		this.y = y;
		this.z = z;
		
		values = new ArrayList<MarkerValue>();
	}

	// Cas numérique
	public Marker(InformationPoint parent, int x) throws InformationPointDecoderException {
		this.parent = parent;
		
		this.x = x;
		values = new ArrayList<MarkerValue>();
	}

	public InformationPoint getParent() {
		return this.parent;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setY(String y) {
		this.y = Integer.parseInt(y, 16);
	}

	public int getZ() {
		return z;
	}

	public void setZ(String z) {
		this.z = Integer.parseInt(z, 16);
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(DecodingTable table) throws InformationPointDecoderException {
		table.checkUnit();
		this.unit = table.getUnit();
	}

	public List<MarkerValue> getValues() {
		return values;
	}
	
	private MarkerValue buildValue(String value, boolean hasUnit) {
		String finalValue = hasUnit ? buildValueWithUnit(value, unit) : value;
		boolean isNumeric = UtilService.isNumber(value);
		return isNumeric ? new MarkerValue(finalValue, isNumeric, Float.valueOf(value)) : new MarkerValue(finalValue, isNumeric, null);
	}
	
	public void addValue(String value, boolean hasUnit) {
		values.add(buildValue(value, hasUnit));
	}
	
	public void addValue(String value, boolean hasUnit, int position) {
		values.add(position, buildValue(value, hasUnit));
	}
		
	public String buildString(String lang) {
		KVBLoaderService loaderService = KVBLoaderService.getServiceInstance();
		
		StringBuilder stringBuilder = new StringBuilder();
		List<MarkerValue> markerValues = getValues();
				
		for (int i = 0; i < markerValues.size(); i++) {
			if (i != 0) {
				stringBuilder.append(JSONService.JSON_MARKER_VALUES_SEPARATOR);
			}
						
			MarkerValue mv = markerValues.get(i);
			
			String value = mv.getValue();
			String localizedValue = lang != null && !mv.isNumeric() ? loaderService.getLabel(value, lang) : value;
			
			if (mv instanceof MarkerX3X6X9Value) {
				String gap = ((MarkerX3X6X9Value) mv).getGap();
				String localizedGap = lang != null ? loaderService.getLabel(gap, lang) : gap;
				
				localizedValue = MarkerX3X6X9.buildDistanceOrSpeedWithGap(localizedValue, localizedGap);
			} else if (mv instanceof MarkerX14Value) {
				String inclination = ((MarkerX14Value) mv).getInclination();
				String localizedInclination = lang != null ? loaderService.getLabel(inclination, lang) : inclination;
				
				localizedValue = MarkerX14.buildInclinationValue(localizedInclination, localizedValue);
			} else if (mv instanceof MarkerX1X4Value) {		
				MarkerX1X4ValueType type = ((MarkerX1X4Value) mv).getType();
				
				if (type.equals(MarkerX1X4ValueType.VE)) {
					String VELabel = lang != null ? loaderService.getLabel(MarkerX1X4.LABEL_X1X4_VE, lang) : MarkerX1X4.LABEL_X1X4_VE;
					localizedValue = MarkerX1X4.buildSpeedValue(VELabel, localizedValue);
				} else if (type.equals(MarkerX1X4ValueType.VB)) {
					String VBLabel = lang != null ? loaderService.getLabel(MarkerX1X4.LABEL_X1X4_VB, lang) : MarkerX1X4.LABEL_X1X4_VB;
					localizedValue = MarkerX1X4.buildSpeedValue(VBLabel, localizedValue);
				}
			}
				
			stringBuilder.append(localizedValue);
		}
		
		return stringBuilder.toString();
	}
	
	protected String buildValueWithUnit(String value, String unit) {
		return String.format(UtilService.isNumber(value) ? "%s %s" : "%s", value, unit);
	}
	
	/**
	 * Classe interne a première vue sans intérêt (= String).
	 * D'autres classes héritent de cette classe, et y apportent un comportement spécialisé (plusieurs champs).
	 * L'intérêt est d'utiliser ces classes depuis des plugins extérieurs.
	 * 
	 * Le but premier est de gérer correctement la localisation.
	 */
	public class MarkerValue {
		private String value;
		private boolean isNumeric;
		private Float numericalValue = null;
		
		public MarkerValue(String value, boolean isNumeric, Float numericalValue) {
			this.value = value;
			this.isNumeric = isNumeric;
			this.numericalValue = numericalValue;
		}
		
		public boolean isNumeric() {
			return this.isNumeric;
		}
		
		public String getValue() {
			return this.value;
		}
		
		public Float getNumericalValue() {
			return this.numericalValue;
		}
		
		public String buildJSONValue() {
			return getValue();
		}
	}
}
