package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom;


public class AnnulerZoom {
	
	boolean zoomAnnule;
	
    private static final AnnulerZoom INSTANCE = new AnnulerZoom();

    /**
     * La pr�sence d'un constructeur priv� supprime
     * le constructeur public par d�faut.
     */
    private AnnulerZoom() {
    
    }

	public boolean isZoomAnnule() {
		return zoomAnnule;
	}

	public void setZoomAnnule(boolean zoomAnnule) {
		this.zoomAnnule = zoomAnnule;
	}
      
	public static AnnulerZoom getInstance() {
        return INSTANCE;
    }
}