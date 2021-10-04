package com.faiveley.kvbdecoder.model.kvb.variable;

import java.util.ArrayList;
import java.util.List;

import com.faiveley.kvbdecoder.model.atess.variable.DescriptorVariable;
import com.faiveley.kvbdecoder.model.kvb.event.Event;
import com.faiveley.kvbdecoder.model.kvb.ip.InformationPoint;


public class KVBAnalogVariable extends KVBVariable {
	private InformationPoint informationPoint = null;
	
	public KVBAnalogVariable(Event parent, DescriptorVariable descriptor, String value) {
		super(parent, descriptor, value);
	}
	
	public void setInformationPoint(InformationPoint informationPoint) {
		this.informationPoint = informationPoint;
	}
	
	public List<InformationPoint> getInformationPoints() {
		List<InformationPoint> ips = new ArrayList<InformationPoint>();
		
		if (informationPoint != null) {
			ips.add(informationPoint);
		}
		
		return ips;
	}
}
