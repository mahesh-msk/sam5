package com.faiveley.samng.principal.ihm.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class PerspectiveAccueil implements IPerspectiveFactory {
	public static String ID = "SAMNG.perspectiveAccueil";

	public PerspectiveAccueil() {
	}
	
	@Override
	public final void createInitialLayout(final IPageLayout layout) {		
		layout.setEditorAreaVisible(false);

		layout.setFixed(true);
		layout.addPerspectiveShortcut(PerspectiveGTL.getID());
		layout.addPerspectiveShortcut(PerspectiveGT.getID());
		layout.addPerspectiveShortcut(PerspectiveGL.getID());
		layout.addPerspectiveShortcut(PerspectiveLM.getID());
		layout.addPerspectiveShortcut(PerspectiveGALT.getID());
		layout.addPerspectiveShortcut(PerspectiveGM.getID());
		layout.addPerspectiveShortcut(PerspectiveGestionDesMissions.getID());

/*		layout.addFastView("SAMNG.Vue.Filtre.TabularVueFiltre");
		layout.addFastView("SAMNG.Vue.Filtre.VueListeFiltre");
		layout.addFastView("SAMNG.Vue.Filtre.GraphiqueVueFiltre");
		
		layout.getViewLayout("SAMNG.Vue.Filtre.TabularVueFiltre").setCloseable(false);
		layout.getViewLayout("SAMNG.Vue.Filtre.VueListeFiltre").setCloseable(false);
		layout.getViewLayout("SAMNG.Vue.Filtre.GraphiqueVueFiltre").setCloseable(false);*/
	}

	public static String getID() {
		return ID;
	}
}
