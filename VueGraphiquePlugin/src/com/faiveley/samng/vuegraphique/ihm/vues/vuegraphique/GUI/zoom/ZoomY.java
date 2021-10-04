package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom;

import com.faiveley.samng.vuegraphique.sm.gestionGraphes.Courbe;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class ZoomY extends AZoomComposant {
	private double maxValue;
	private double minValue;
	private Courbe courbe;

	public ZoomY() {
	}
	
	public ZoomY(Courbe courbe, double minValue, double maxValue) {
		this.courbe = courbe;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public ZoomY(AZoomComposant zoom) {
		super(zoom);
		if(zoom != null && zoom instanceof ZoomY) {
			this.courbe = ((ZoomY)zoom).courbe;
			this.minValue = ((ZoomY)zoom).minValue;
			this.maxValue = ((ZoomY)zoom).maxValue;
		}
	}
	
	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}
	
	public double getMinValue() {
		return this.minValue;
	}
	
	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}
	
	public double getMaxValue() {
		return this.maxValue;
	}

	public void setCourbe(Courbe courbe) {
		this.courbe = courbe;
	}

	public Courbe getCourbe() {
		return this.courbe;
	}
	
	@Override
	public AZoomComposant clone() {
		return new ZoomY(this);
	}
}
