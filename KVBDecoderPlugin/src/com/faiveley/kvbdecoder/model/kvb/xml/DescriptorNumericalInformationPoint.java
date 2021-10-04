package com.faiveley.kvbdecoder.model.kvb.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * Descripteur d'un point d'information pour un message numérique: le xsm permet de trouver la séquence des X ainsi que les quartets
 * 
 * @author jthoumelin
 *
 */
public class DescriptorNumericalInformationPoint {
	private int xsm;
	private int xcs;
	private List<String> quartets;
	private List<Integer> xSequence;
	
	public DescriptorNumericalInformationPoint(int xsm, int xcs) {
		this.xsm = xsm;
		this.xcs = xcs;
		xSequence = new ArrayList<Integer>();
		quartets = new ArrayList<String>();
	}
	
	public int getXsm() {
		return xsm;
	}
	
	public int getXcs() {
		return xcs;
	}
		
	public void addX(Integer x) {
		xSequence.add(x);
	}

	public List<String> getQuartets() {
		return quartets;
	}

	public void addQuartet(String quartet) {
		quartets.add(quartet);
	}
		
	public List<Integer> getXSequence() {
		return xSequence;
	}
}
