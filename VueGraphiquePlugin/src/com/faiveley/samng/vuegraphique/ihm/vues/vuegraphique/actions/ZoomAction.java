package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions;

import com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.TypeMenuOptions;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.GestionnaireZoom;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.dialogs.ManualZoomDialog;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class ZoomAction extends VueGraphicAction {
	private TypeMenuOptions zoomType;
	public ZoomAction(TypeMenuOptions zoomType) {
		this.zoomType = zoomType;
	}
	
	@Override
	public void run() {
		switch(zoomType) {
			case PREVIOUS_ZOOM:
				GestionnaireZoom.setPrevZoom();
				break;
			case NEXT_ZOOM:
				GestionnaireZoom.setNextZoom();
				break;
			case MAIN_ZOOM:
				GestionnaireZoom.ajouterZoom(null);
				break;
			case MANUAL_ZOOM:
				ManualZoomDialog zoomDlg = new ManualZoomDialog(ActivatorVueGraphique.getDefault().
						getWorkbench().getActiveWorkbenchWindow().getShell());
				zoomDlg.open();
				break;
		}
	}
}
