package com.faiveley.samng.principal.ihm.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class PerspectiveGestionDesMissions implements IPerspectiveFactory {

	public static String ID = "SAMNG.perspectiveMission";
	public static final String VueExplorateur_ID = "SAMNG.Vue.VueExplorateur";

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
		layout.addPerspectiveShortcut(PerspectiveGL.getID());

		layout.setEditorAreaVisible(false);
		layout.addStandaloneView(VueExplorateur_ID, true,  IPageLayout.TOP, 1.0f, editorArea);
		layout.getViewLayout(VueExplorateur_ID).setMoveable(false);
		layout.getViewLayout(VueExplorateur_ID).setCloseable(false);
	}

	/**
	 * 
	 * @return iD
	 */
	public static String getID() {
		return ID;
	}
}