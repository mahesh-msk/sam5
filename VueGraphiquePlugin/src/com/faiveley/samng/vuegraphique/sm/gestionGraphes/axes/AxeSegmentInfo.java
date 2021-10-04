package com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class AxeSegmentInfo {
	private int segmentNr;
	private int startMsgId;
	private int endMsgId;
	private int minX;
	private int maxX;
	private double minValue;
	private double maxValue;
	
	public AxeSegmentInfo() {
		
	}

	public AxeSegmentInfo(int segmentNr, int startMsgId, int endMsgId) {
		this.segmentNr = segmentNr;
		this.startMsgId = startMsgId;
		this.endMsgId = endMsgId;
	}

	public void setSegmentNr(int segmentNr) {
		this.segmentNr = segmentNr;
	}
	
	public int getSegmentNr() {
		return segmentNr;
	}

	public void setSegmentStartMsgId(int startMsgId) {
		this.startMsgId = startMsgId;
	}

	public int getSegmentStartMsgId() {
		return startMsgId;
	}

	public void setSegmentEndMsgId(int endMsgId) {
		this.endMsgId = endMsgId;
	}

	public int getSegmentEndMsgId() {
		return endMsgId;
	}
	
	public void setMinX(int minX) {
		this.minX = minX;
	}
	
	public int getMinX() {
		return minX;
	}

	public void setMaxX(int maxX) {
		this.maxX = maxX;
	}
	
	public int getMaxX() {
		return maxX;
	}
	
	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}
	
	public double getMinValue() {
		return minValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}
	
	public double getMaxValue() {
		return maxValue;
	}
	 

}
