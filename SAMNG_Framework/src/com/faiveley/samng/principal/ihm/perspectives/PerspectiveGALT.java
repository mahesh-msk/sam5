package com.faiveley.samng.principal.ihm.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.faiveley.samng.principal.data.ActivatorData;

public class PerspectiveGALT implements IPerspectiveFactory {
	/** */
	private static String iD = "SAMNG.perspectiveGALT";

	/**
	 * @param layout
	 *            1
	 */
	public final void createInitialLayout(final IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.addPerspectiveShortcut(PerspectiveAccueil.getID());
		layout.addPerspectiveShortcut(PerspectiveGT.getID());
		layout.addPerspectiveShortcut(PerspectiveGL.getID());
		layout.addPerspectiveShortcut(PerspectiveLM.getID());
		layout.addPerspectiveShortcut(PerspectiveGTL.getID());
		layout.addPerspectiveShortcut(PerspectiveGM.getID());
		layout.addPerspectiveShortcut(PerspectiveGestionDesMissions.getID());

		if(!ActivatorData.getInstance().getVueData().isEmpty()){
			/** Liste Constante */
			final float tailleGraphique = 1f;
			final float tailleVueAnnotations = 0.737f;
			final float tailleTabulaire = 0.5f;
			final float tailleListe = 0.5f;

			layout.addStandaloneView("SAMNG.Vue.Graphique",true,IPageLayout.TOP,tailleGraphique, editorArea);
			layout.addStandaloneView("SAMNG.Vue.Markers.VueMarqueurs",true,IPageLayout.RIGHT,tailleVueAnnotations, "SAMNG.Vue.Graphique");
			layout.addStandaloneView("SAMNG.Vue.Liste",true,IPageLayout.BOTTOM,tailleTabulaire, "SAMNG.Vue.Graphique");
			layout.addStandaloneView("SAMNG.Vue.Liste.e4",true,IPageLayout.BOTTOM,tailleTabulaire, "SAMNG.Vue.Graphique");
			layout.addStandaloneView("SAMNG.Vue.Tabulaire",true,IPageLayout.BOTTOM,tailleListe,"SAMNG.Vue.Markers.VueMarqueurs");

			layout.getViewLayout("SAMNG.Vue.Graphique").setCloseable(false);
			layout.getViewLayout("SAMNG.Vue.Graphique").setMoveable(false);
			layout.getViewLayout("SAMNG.Vue.Tabulaire").setCloseable(false);
			layout.getViewLayout("SAMNG.Vue.Tabulaire").setMoveable(false);
			layout.getViewLayout("SAMNG.Vue.Liste").setCloseable(false);
			layout.getViewLayout("SAMNG.Vue.Liste").setMoveable(false);
			layout.getViewLayout("SAMNG.Vue.Markers.VueMarqueurs").setCloseable(false);
			layout.getViewLayout("SAMNG.Vue.Markers.VueMarqueurs").setMoveable(false);

	/*		layout.addFastView("SAMNG.Vue.Filtre.TabularVueFiltre");
			layout.addFastView("SAMNG.Vue.Filtre.VueListeFiltre");
			layout.addFastView("SAMNG.Vue.Filtre.GraphiqueVueFiltre");

			layout.getViewLayout("SAMNG.Vue.Filtre.TabularVueFiltre").setCloseable(false);
			layout.getViewLayout("SAMNG.Vue.Filtre.VueListeFiltre").setCloseable(false);
			layout.getViewLayout("SAMNG.Vue.Filtre.GraphiqueVueFiltre").setCloseable(false);
			*/
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
