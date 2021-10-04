package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe;

public class Graduation {

	private String label;
	private int labelX;
	private int labelY;
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	
	public Graduation() {
		// TODO Auto-generated constructor stub
	}
	
	public Graduation(String label, int labelX, int labelY, int x1, int y1, int x2, int y2) {
		super();
		this.label = label;
		this.labelX = labelX;
		this.labelY = labelY;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public int getLabelX() {
		return labelX;
	}
	public void setLabelX(int labelX) {
		this.labelX = labelX;
	}
	public int getLabelY() {
		return labelY;
	}
	public void setLabelY(int labelY) {
		this.labelY = labelY;
	}
	public int getX1() {
		return x1;
	}
	public void setX1(int x1) {
		this.x1 = x1;
	}
	public int getX2() {
		return x2;
	}
	public void setX2(int x2) {
		this.x2 = x2;
	}
	public int getY1() {
		return y1;
	}
	public void setY1(int y1) {
		this.y1 = y1;
	}
	public int getY2() {
		return y2;
	}
	public void setY2(int y2) {
		this.y2 = y2;
	}
	
}
