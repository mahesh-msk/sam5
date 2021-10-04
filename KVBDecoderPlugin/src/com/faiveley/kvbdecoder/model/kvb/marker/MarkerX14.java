package com.faiveley.kvbdecoder.model.kvb.marker;

import com.faiveley.kvbdecoder.exception.decoder.InformationPointDecoderException;
import com.faiveley.kvbdecoder.model.kvb.ip.InformationPoint;
import com.faiveley.kvbdecoder.model.kvb.xml.DecodingTable;
import com.faiveley.kvbdecoder.services.decoder.UtilService;

public class MarkerX14 extends Marker {
	// Informations relatives à l'information distance : le cas X=14 est particulier, si non précédé de X=9 il peut permettre de déterminer deux informations
	//	- la déclivité (toujours le cas)
	//	- la distance (seulement si non précédé de X=9)
	private String distanceCode = null;
	private String distanceUnit = null;
	private String distanceValue = null;
	
	public MarkerX14(InformationPoint parent, int x, int y, int z) throws InformationPointDecoderException {
		super(parent, x, y, z);
	}
	
	public MarkerX14(InformationPoint parent, int x) throws InformationPointDecoderException {
		super(parent, x);
	}

	public String getDistanceCode() {		
		return distanceCode;
	}

	public void setDistanceCode(String distanceCode) {
		this.distanceCode = distanceCode;
	}

	public void setDistanceUnit(DecodingTable table) throws InformationPointDecoderException {
		table.checkUnit();
		this.distanceUnit = table.getUnit();
	}
	
	public void setDistanceValue(String distanceValue) {
		this.distanceValue = buildValueWithUnit(distanceValue, distanceUnit);
	}

	public String getDistanceValue() {
		return distanceValue;
	}

	public void addInclination(String inclination, String value, boolean hasUnit) { // Cas particulier de la déclivité, où l'information sur l'inclinaison (rampe/pente) est demandée		
		String finalValue = hasUnit ? buildValueWithUnit(value, unit) : value;
		boolean isNumeric = UtilService.isNumber(value);
		MarkerValue markerValue = isNumeric ? new MarkerX14Value(finalValue, inclination, isNumeric, Float.valueOf(value)) : new MarkerX14Value(finalValue, inclination, isNumeric, null);
		values.add(markerValue);
	}
	
	/**
	 * Spécialisation de la classe MarkerValue.
	 * 
	 * Le but est de gérer le cas particulier de la déclivité, où l'information sur l'inclinaison (rampe/pente) est demandée
	 */
	public class MarkerX14Value extends MarkerValue {
		private String inclination;
		
		public MarkerX14Value(String value, String inclination, boolean isNumeric, Float numericalValue) {
			super(value, isNumeric, numericalValue);
			this.inclination = inclination;
		}
		
		public String getInclination() {
			return this.inclination;
		}
	}
	
	public static String buildInclinationValue(String inclination, String value) {
		return String.format("%s: %s", inclination, value);
	}
}
