package com.faiveley.samng.principal.ihm.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.faiveley.samng.principal.data.ActivatorData;

public class PerspectiveGM implements IPerspectiveFactory {
	private static String ID = "SAMNG.perspectiveGM";
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);

		layout.addPerspectiveShortcut(PerspectiveGTL.getID());
		layout.addPerspectiveShortcut(PerspectiveGL.getID());
		layout.addPerspectiveShortcut(PerspectiveLM.getID());
		layout.addPerspectiveShortcut(PerspectiveGT.getID());
		layout.addPerspectiveShortcut(PerspectiveAccueil.getID());
		layout.addPerspectiveShortcut(PerspectiveGALT.getID());
		layout.addPerspectiveShortcut(PerspectiveGestionDesMissions.getID());

		if (!ActivatorData.getInstance().getVueData().isEmpty()){
			/** Liste Constante */
			final float tailleGraphique = 0.5f;
			final float tailleMultimedia = 0.5f;

			layout.addStandaloneView("SAMNG.Vue.Graphique", true, IPageLayout.TOP, tailleGraphique, editorArea);
			layout.addStandaloneView("SAMNG.Vue.Multimedia", true, IPageLayout.BOTTOM, tailleMultimedia, editorArea);

			layout.getViewLayout("SAMNG.Vue.Graphique").setCloseable(false);
			layout.getViewLayout("SAMNG.Vue.Graphique").setMoveable(false);
			layout.getViewLayout("SAMNG.Vue.Multimedia").setCloseable(false);
			layout.getViewLayout("SAMNG.Vue.Multimedia").setMoveable(false);

			layout.addFastView("SAMNG.Vue.Filtre.TabularVueFiltre");
			layout.addFastView("SAMNG.Vue.Filtre.VueListeFiltre");
			layout.addFastView("SAMNG.Vue.Filtre.GraphiqueVueFiltre");

			layout.getViewLayout("SAMNG.Vue.Filtre.TabularVueFiltre").setCloseable(false);
			layout.getViewLayout("SAMNG.Vue.Filtre.VueListeFiltre").setCloseable(false);
			layout.getViewLayout("SAMNG.Vue.Filtre.GraphiqueVueFiltre").setCloseable(false);
		}
	}

	/**
	 * 
	 * @return ID
	 */
	public static String getID() {
		return ID;
	}
}
