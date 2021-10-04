package com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olivier
 * @version 1.0
 * @created 14-janv.-2008 12:36:58
 */
public class AxeX {
	private double allCumul;	//this is the cumul for all segments ignoring zoom
	private double cumul;		//this is the cumul for all segments according to the current X zoom
	private int idMsgDebut;
	private int idMsgFin;
	private double resolution;	//we put it a double as it can be less than 1 also
	public TypeAxe m_TypeAxe;
	private int valeurFormate; 
	private List<AxeSegmentInfo> segmentsInfos = new ArrayList<AxeSegmentInfo>();

	public AxeX() {

	}

	public double getCumul(){
		return cumul;
	}

	public int getIdMsgDebut() {
		return this.idMsgDebut;
	}

	public void setIdMsgDebut(int idMsg) {
		this.idMsgDebut = idMsg;
	}

	public int getIdMsgFin() {
		return this.idMsgFin;
	}

	public void setIdMsgFin(int idMsg) {
		this.idMsgFin = idMsg;
	}

	public double getResolution(){
		return resolution;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setCumul(double newVal){
		cumul = newVal;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setResolution(double newVal){
		resolution = newVal;
	}
	
	public void setFormateValeur(int format) {
		this.valeurFormate = format;
	}
	
	public int getFormateValeur() {
		return this.valeurFormate;
	}
	
	public void addSegmentInfo(AxeSegmentInfo infoSegment) {
		this.segmentsInfos.add(infoSegment);
	}
	
	public List<AxeSegmentInfo> getInfoSegments() {
		return this.segmentsInfos;
	}
	
	public void reset() {
		this.segmentsInfos.clear();
	}
	
	public AxeSegmentInfo getSegmentInfoByXPosition(int xPos) {
		//computes in what segment is this x value
		if(xPos < this.segmentsInfos.get(0).getMinX())	//to the left of start X
			return this.segmentsInfos.get(0);
		if(xPos > this.segmentsInfos.get(this.segmentsInfos.size() - 1).getMaxX())	//to the right of X axe
			return this.segmentsInfos.get(this.segmentsInfos.size() - 1);
		for(AxeSegmentInfo segInf: segmentsInfos) {
			if(xPos >= segInf.getMinX() && xPos <= segInf.getMaxX())
				return segInf;
		}
		return null;	//It should never get here unless xMin and xMax are not computed correctly
	}
	
	public AxeSegmentInfo getSegmentInfoByMessageId(int msgId) {
		//computes in what segment is this x value
		if(msgId < this.segmentsInfos.get(0).getSegmentStartMsgId())	//to the left of start X
			return this.segmentsInfos.get(0);
		if(msgId > this.segmentsInfos.get(this.segmentsInfos.size() - 1).getSegmentEndMsgId())	//to the right of X axe
			return this.segmentsInfos.get(this.segmentsInfos.size() - 1);
		for(AxeSegmentInfo segInf: segmentsInfos) {
			if(msgId >= segInf.getSegmentStartMsgId() && msgId <= segInf.getSegmentEndMsgId())
				return segInf;
		}
		return null;	//It should never get here unless xMin and xMax are not computed correctly
	}
	
	public boolean isSegmentDifferent(int msgIdPrev, int msgIdNext) {
		AxeSegmentInfo info1 = getSegmentInfoByMessageId(msgIdPrev);
		AxeSegmentInfo info2 = getSegmentInfoByMessageId(msgIdNext);
		if(info1.getSegmentNr() != info2.getSegmentNr())
			return true;
		return false;
	}

	public double getAllCumul() {
		return allCumul;
	}

	public void setAllCumul(double allCumul) {
		this.allCumul = allCumul;
	}
}