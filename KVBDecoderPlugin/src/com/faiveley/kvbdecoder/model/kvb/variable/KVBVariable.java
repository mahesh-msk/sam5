package com.faiveley.kvbdecoder.model.kvb.variable;

import java.util.List;

import org.json.JSONArray;

import com.faiveley.kvbdecoder.model.atess.variable.DescriptorVariable;
import com.faiveley.kvbdecoder.model.kvb.event.Event;
import com.faiveley.kvbdecoder.model.kvb.ip.InformationPoint;


public abstract class KVBVariable {
	private Event parent;
	private DescriptorVariable descriptor;
	private String value; // La valeur avant décodage
		
	public DescriptorVariable getDescriptor() {
		return descriptor;
	}
	
	public String getValue() {
		return value;
	}
			
	public KVBVariable(Event parent, DescriptorVariable descriptor, String value) {
		this.parent = parent;
		this.descriptor = descriptor;
		this.value = value;
	}
	
	public Event getParent() {
		return this.parent;
	}
		
	public JSONArray toJSON() {		
		JSONArray jsonObject = new JSONArray();
		
		for (InformationPoint ip : getInformationPoints()) {
			if (!ip.isCorrupted()) {
				jsonObject.put(ip.toJSON());
			}
		}
		
		return jsonObject;
	}
	
	public abstract List<InformationPoint> getInformationPoints();
}
