package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom;

import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.TypeAxe;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class ZoomX extends AZoomComposant {
	private int firstVisibleMsgId;
	private int lastVisibleMsgId;
	private double firstXValue;
	private double lastXValue;
	private TypeAxe typeAxe;

	public ZoomX() {
		
	}

	public ZoomX(AZoomComposant zoom) {
		super(zoom);
		if(zoom != null && (zoom instanceof ZoomX)) {
			firstVisibleMsgId = ((ZoomX)zoom).firstVisibleMsgId;
			lastVisibleMsgId = ((ZoomX)zoom).lastVisibleMsgId;
			firstXValue = ((ZoomX)zoom).firstXValue;
			lastXValue = ((ZoomX)zoom).lastXValue;
		}
	}
	
	public void setFirstVisibleMsgId(int firstVisibleMsgId) {
		this.firstVisibleMsgId = firstVisibleMsgId;
	}

	public int getFirstVisibleMsgId() {
		return this.firstVisibleMsgId;
	}

	public void setLastVisibleMsgId(int lastVisibleMsgId) {
		this.lastVisibleMsgId = lastVisibleMsgId;
	}

	public int getLastVisibleMsgId() {
		return this.lastVisibleMsgId;
	}

	public void setFirstXValue(double firstXValue) {
		this.firstXValue = firstXValue;
	}

	public double getFirstXValue() {
		return this.firstXValue;
	}

	public void setLastXValue(double lastXValue) {
		this.lastXValue = lastXValue;
	}

	public double getLastXValue() {
		return this.lastXValue;
	}

	public TypeAxe getTypeAxe() {
		return typeAxe;
	}

	public void setTypeAxe(TypeAxe typeAxe) {
		this.typeAxe = typeAxe;
	}

	@Override
	public AZoomComposant clone() {
		return new ZoomX(this);
	}

}
