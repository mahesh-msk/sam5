package com.faiveley.kvbdecoder.model.kvb.marker;

import com.faiveley.kvbdecoder.exception.decoder.InformationPointDecoderException;
import com.faiveley.kvbdecoder.model.kvb.ip.InformationPoint;
import com.faiveley.kvbdecoder.services.decoder.UtilService;

public class MarkerX1X4 extends Marker {
	// Clefs pour les labels au sein des valeurs
	public static final String LABEL_X1X4_VE = "X1X4.VEVB.VE";
	public static final String LABEL_X1X4_VB= "X1X4.VEVB.VB";
	
	public enum MarkerX1X4ValueType {
		VE,
		VB;
	}
		
	public MarkerX1X4(InformationPoint parent, int x, int y, int z) throws InformationPointDecoderException {
		super(parent, x, y, z);
	}
	
	public MarkerX1X4(InformationPoint parent, int x) throws InformationPointDecoderException {
		super(parent, x);
	}

	public void setVe(String ve) {
		values.add(new MarkerX1X4Value(ve, MarkerX1X4ValueType.VE));
	}

	public void setVb(String vb) {
		values.add(new MarkerX1X4Value(vb, MarkerX1X4ValueType.VB));
	}
	
	/**
	 * Spécialisation de la classe MarkerValue.
	 * 
	 * Le but est de gérer les cas VE et VB, où une traduction peut être demandée
	 */
	public class MarkerX1X4Value extends MarkerValue {		
		private MarkerX1X4ValueType type;
		
		public MarkerX1X4Value(String value, MarkerX1X4ValueType type) {
			super(value, UtilService.isNumber(value), UtilService.isNumber(value) ? Float.valueOf(value) : null);
			this.type = type;
		}
		
		public MarkerX1X4ValueType getType() {
			return this.type;
		}
	}
	
	public static String buildSpeedValue(String label, String value) {
		return String.format("%s=%s", label, value);
	}
}
