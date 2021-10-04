package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public abstract class AZoomComposant {
	private TypeZoom zoomType;
	
	public AZoomComposant() {
	}
	
	public AZoomComposant(AZoomComposant zoom) {
		this.zoomType = zoom.zoomType;
	}
	
	public void setTypeZoom(TypeZoom zoomType) {
		this.zoomType = zoomType;
	}
	
	public TypeZoom getTypeZoom() {
		return this.zoomType;
	}
	
	public abstract AZoomComposant clone();
	
}
