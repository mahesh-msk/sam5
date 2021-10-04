package com.faiveley.kvbdecoder.model.kvb.ip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.faiveley.kvbdecoder.exception.model.ip.GroupingEnumException;
import com.faiveley.kvbdecoder.model.kvb.marker.Marker;
import com.faiveley.kvbdecoder.model.kvb.marker.MarkerX14;
import com.faiveley.kvbdecoder.model.kvb.variable.KVBVariable;
import com.faiveley.kvbdecoder.services.json.JSONService;

public class InformationPoint {
	private static final String LABEL_ALERT_TEXT_INVALID_DIRECTION = "Alerte.PointInformationContreSens";
	
	public static final int XSM_XCS_DEFAULT_VALUE = -1;

	private KVBVariable parent;
	private Map<String, String> labels;
	private String xSequence;
	private int xsm;
	private int xcs;
	private boolean rightDirection = true;
	private int ipIndex = -1;

	private String alertText = "";
	private GroupingEnum grouping = null;
	private List<Marker> markers;
	private boolean corrupted = false; // Si une erreur s'est produite lors du traitement d'un point d'information déjà créé au préalable
	
	public InformationPoint(KVBVariable parent, int xsm, int xcs, boolean rightDirection, int ipIndex) {
		this.parent = parent;
		this.xsm = xsm;
		this.xcs = xcs;
		this.rightDirection = rightDirection;
		this.ipIndex = ipIndex;
		
		if (!this.rightDirection) {
			setAlertText(LABEL_ALERT_TEXT_INVALID_DIRECTION);
		}
		
		markers = new ArrayList<Marker>();
	}
	
	public KVBVariable getParent() {
		return this.parent;
	}
	
	public void setXSequence(String xSequence) {
		this.xSequence = xSequence;
	}
	
	public String getXSequence() {
		return this.xSequence;
	}
	
	public Map<String, String> getLabels() {
		return labels;
	}
	
	public void setLabels(Map<String, String> labels) {
		this.labels = labels;
	}
	
	public int getXsm() {
		return this.xsm;
	}
	
	public int getXcs() {
		return this.xcs;
	}
	
	public boolean isRightDirection() {
		return this.rightDirection;
	}
	
	public int getIndex() {
		return this.ipIndex;
	}
	
	public String getAlertText() {
		return this.alertText;
	}
	
	public void setAlertText(String alertTextKey) {
		this.alertText = alertTextKey;
	}
	
	public GroupingEnum getGrouping() {
		return this.grouping;
	}
		
	public void setGrouping(String grouping) throws GroupingEnumException {
		for (GroupingEnum value : GroupingEnum.values()) {
			if (grouping.endsWith(value.toString())) {
				this.grouping = value;
				return;
			}
		}
		
		throw new GroupingEnumException(grouping);
	}

	public List<Marker> getMarkers() {
		return markers;
	}
	
	public void addMarker(Marker marker) {
		markers.add(marker);
	}

	public boolean isCorrupted() {
		return corrupted;
	}

	public void setCorrupted(boolean corrupted) {
		this.corrupted = corrupted;
	}

	public JSONObject toJSON() {
		String lang = null;
				
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put(JSONService.JSON_DECODER_IP_LABEL, alertText);
		jsonObject.put(JSONService.JSON_DECODER_IPTYPE_LABEL, xSequence);
		
		if (rightDirection) {
			for (Marker m : markers) {
				if (!JSONService.isMarkerToIgnore(m)) {
					String markerCode = m.getCode();
					
					if (!jsonObject.has(markerCode)) { // Cas d'une double balise (5 5, 7 7...)						
						jsonObject.put(markerCode, m.buildString(lang));
						
						if (m instanceof MarkerX14 && ((MarkerX14) m).getDistanceCode() != null) {
							MarkerX14 mX14 = (MarkerX14) m;
																					
							jsonObject.put(mX14.getDistanceCode(), mX14.getDistanceValue());
						}
					}
				}
			}
		}
		
		return jsonObject;
	}
}
