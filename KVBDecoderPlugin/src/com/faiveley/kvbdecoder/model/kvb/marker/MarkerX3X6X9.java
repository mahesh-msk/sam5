package com.faiveley.kvbdecoder.model.kvb.marker;

import java.util.List;

import com.faiveley.kvbdecoder.exception.decoder.InformationPointDecoderException;
import com.faiveley.kvbdecoder.model.kvb.ip.InformationPoint;
import com.faiveley.kvbdecoder.services.decoder.UtilService;

public class MarkerX3X6X9 extends Marker {
	public static final String SYMBOL_MINUS = "-";
	public static final String SYMBOL_PLUS = "+";
	
	private String distanceOrSpeed;
	
	public String getDistanceOrSpeed() {
		return distanceOrSpeed;
	}

	private String gap;
	private String symbol = null; // + ou -
	
	private boolean gapApplied = false;
	
	public MarkerX3X6X9(InformationPoint parent, int x, int y, int z) throws InformationPointDecoderException {
		super(parent, x, y, z);
	}
	
	public MarkerX3X6X9(InformationPoint parent, int x) throws InformationPointDecoderException {
		super(parent, x);
	}
	
	@Override
	public List<MarkerValue> getValues() {
		if (distanceOrSpeed != null && !gapApplied) {
			if (UtilService.isNumber(distanceOrSpeed)) {
				if (gap != null) { // Application de l'écart: sur la vitesse (X=3,6 ; écart : X=8x13) ou la distance (X=9 ; écart : X=14)
					if (UtilService.isNumber(gap)) {
						float distanceFloat = Float.parseFloat(distanceOrSpeed);
						float gapFloat = Float.parseFloat(gap);
						addValue(String.valueOf(symbol.equals(SYMBOL_MINUS) ? distanceFloat - gapFloat : distanceFloat + gapFloat), true, 0);
					} else { // Cas où la vitesse ou la distance sont des valeurs numériques, mais pas l'écart (ex: AE pour X=8/13 et Y/Z=14)
						addValue(distanceOrSpeed, true, 0);
						values.set(0, new MarkerX3X6X9Value(values.get(0).getValue(), gap));
					}
				} else {
					addValue(distanceOrSpeed, true, 0);
				}
			} else {
				addValue(distanceOrSpeed, false, 0);
			}
			
			gapApplied = true;
		}
				
		return values;
	}
	
	public void setDistanceOrSpeed(String distanceOrSpeed) {
		this.distanceOrSpeed = distanceOrSpeed;
	}
	
	public void setGap(String gap) throws InformationPointDecoderException {		
		this.gap = gap;
	}
	
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	/**
	 * Spécialisation de la classe MarkerValue.
	 * 
	 * Le but est de gérer le cas particulier où la vitesse ou la distance sont des valeurs numériques, mais pas l'écart (ex: AE pour X=8/13 et Y/Z=14)
	 */
	public class MarkerX3X6X9Value extends MarkerValue {
		private String gap;
		
		public MarkerX3X6X9Value(String value, String gap) {
			super(value, true, Float.valueOf(value));
			this.gap = gap;
		}
		
		public String getGap() {
			return this.gap;
		}
	}
	
	public static String buildDistanceOrSpeedWithGap(String value, String gap) {
		return String.format("%s + %s", value, gap);
	}
}
