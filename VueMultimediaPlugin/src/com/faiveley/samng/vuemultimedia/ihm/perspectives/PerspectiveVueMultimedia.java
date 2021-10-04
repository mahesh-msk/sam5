package com.faiveley.samng.vuemultimedia.ihm.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class PerspectiveVueMultimedia implements IPerspectiveFactory {
	private static String ID = "VueMultimediaPlugin.perspectiveVueMultimedia";

	public final void createInitialLayout(final IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);

		IFolderLayout folder = layout.createFolder("", IPageLayout.TOP, 1f, layout.getEditorArea());
		folder.addView("SAMNG.Vue.Multimedia");
		layout.getViewLayout("SAMNG.Vue.Multimedia").setCloseable(false);		
	}

	public static String getID() {
		return ID;
	}
}
