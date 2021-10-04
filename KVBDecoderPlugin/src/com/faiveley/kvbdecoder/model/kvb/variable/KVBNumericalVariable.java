package com.faiveley.kvbdecoder.model.kvb.variable;

import java.util.ArrayList;
import java.util.List;

import com.faiveley.kvbdecoder.model.atess.variable.DescriptorVariable;
import com.faiveley.kvbdecoder.model.kvb.event.Event;
import com.faiveley.kvbdecoder.model.kvb.ip.InformationPoint;


public class KVBNumericalVariable extends KVBVariable {
	private List<InformationPoint> informationPoints;
	
	public KVBNumericalVariable(Event parent, DescriptorVariable descriptor, String value) {
		super(parent, descriptor, value);
		informationPoints = new ArrayList<InformationPoint>();
	}
	
	public List<InformationPoint> getInformationPoints() {
		return informationPoints;
	}

	public void addInformationPoint(InformationPoint informationPoint) {
		informationPoints.add(informationPoint);
	}
}
