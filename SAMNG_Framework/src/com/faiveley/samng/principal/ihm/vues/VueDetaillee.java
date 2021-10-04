package com.faiveley.samng.principal.ihm.vues;

import java.util.ArrayList;

public class VueDetaillee {
	
	private int codeEv=-1;
	
	private int posVScroll;
	
	private ArrayList<ArrayList<Integer>> indicesExpandedElements;
	
	public VueDetaillee(int codeEv, int posVScroll, ArrayList<ArrayList<Integer>> expandedElements) {
		super();
		this.codeEv = codeEv;
		this.posVScroll = posVScroll;
		this.indicesExpandedElements = expandedElements;
	}
	
	public int getCodeEv() {
		return codeEv;
	}
	
	public void setCodeEv(int codeEv) {
		this.codeEv = codeEv;
	}
	
	public ArrayList<ArrayList<Integer>> getExpandedElements() {
		return indicesExpandedElements;
	}
	
	public void setExpandedElements(ArrayList<ArrayList<Integer>> expandedElements) {
		this.indicesExpandedElements = expandedElements;
	}
	
	public int getPosVScroll() {
		return posVScroll;
	}
	
	public void setPosVScroll(int posVScroll) {
		this.posVScroll = posVScroll;
	}
}
