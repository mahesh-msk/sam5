package com.faiveley.samng.principal.ihm.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.faiveley.samng.principal.data.ActivatorData;


/**
 * @author Olivier Graton 
 * */
public class PerspectiveGL implements IPerspectiveFactory {
	/** */
	private static String iD = "SAMNG.perspectiveGL";

	/**
	 * @param layout
	 *            1
	 */
	public final void createInitialLayout(final IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);

		layout.addPerspectiveShortcut(PerspectiveGTL.getID());
		layout.addPerspectiveShortcut(PerspectiveGT.getID());
		layout.addPerspectiveShortcut(PerspectiveLM.getID());
		layout.addPerspectiveShortcut(PerspectiveAccueil.getID());
		layout.addPerspectiveShortcut(PerspectiveGALT.getID());
		layout.addPerspectiveShortcut(PerspectiveGM.getID());
		layout.addPerspectiveShortcut(PerspectiveGestionDesMissions.getID());

		if(!ActivatorData.getInstance().getVueData().isEmpty()){
			/** Liste Constante */
			final float tailleGraphique = 0.5f;
			final float tailleListe = 0.5f;

			layout.addStandaloneView("SAMNG.Vue.Liste", true, IPageLayout.TOP,tailleListe, editorArea);
			layout.addStandaloneView("SAMNG.Vue.Graphique", true, IPageLayout.BOTTOM,tailleGraphique, editorArea);

			layout.getViewLayout("SAMNG.Vue.Graphique").setCloseable(false);
			layout.getViewLayout("SAMNG.Vue.Graphique").setMoveable(false);
			layout.getViewLayout("SAMNG.Vue.Liste").setCloseable(false);
			layout.getViewLayout("SAMNG.Vue.Liste").setMoveable(false);

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
	 * @return iD
	 */
	public static String getID() {
		return iD;
	}
}
