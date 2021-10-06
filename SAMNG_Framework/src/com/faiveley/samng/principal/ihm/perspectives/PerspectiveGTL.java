package com.faiveley.samng.principal.ihm.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.faiveley.samng.principal.data.ActivatorData;


/**
 * @author Olivier Graton 
 * */
public class PerspectiveGTL implements IPerspectiveFactory {
	/** */
	private static String iD = "SAMNG.perspectiveGTL";

	/**
	 * @param layout
	 *            1
	 */
	public final void createInitialLayout(final IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.addPerspectiveShortcut(PerspectiveAccueil.getID());
		layout.addPerspectiveShortcut(PerspectiveGT.getID());
		layout.addPerspectiveShortcut(PerspectiveLM.getID());
		layout.addPerspectiveShortcut(PerspectiveGL.getID());
		layout.addPerspectiveShortcut(PerspectiveGALT.getID());
		layout.addPerspectiveShortcut(PerspectiveGM.getID());
		layout.addPerspectiveShortcut(PerspectiveGestionDesMissions.getID());

		//folder.addPlaceholder(Accueil.ID + ":*");
		//folder.addView(Accueil.ID);

		//layout.getViewLayout(Accueil.ID).setCloseable(false);
		
		
		//Meggy: these Ids should not be added here. Adding them here implies that 
		//the framework application knows which plugins will be loadded 
		//and it is broken the ideea of a plugin.
//		List<String> listeVues = new ArrayList<String>();
//		listeVues.add("SAMNG.Vue.Tabulaire");
//		listeVues.add("SAMNG.Vue.Liste");
//		listeVues.add("SAMNG.Vue.Graphique");
//		IFolderLayout folder ;
//		for(int i=0; i<listeVues.size();i++){
//			folder =layout.createFolder("", IPageLayout.TOP, 1f,
//					editorArea);
//			
//			folder.addView(listeVues.get(i));
//			layout.getViewLayout(listeVues.get(i)).setCloseable(true);
//		}
		if(!ActivatorData.getInstance().getVueData().isEmpty()){
		/** Liste Constante */
		final float tailleGraphique = 0.5f;
		final float tailleTabulaire = 0.5f;
		final float tailleListe = 0.5f;

		layout.addStandaloneView("SAMNG.Vue.Tabulaire", true, IPageLayout.TOP,
				tailleTabulaire, editorArea);
		layout.addStandaloneView("SAMNG.Vue.Liste", true, IPageLayout.RIGHT,
				tailleListe, "SAMNG.Vue.Tabulaire");
		layout.addStandaloneView("SAMNG.Vue.Graphique", true, IPageLayout.BOTTOM,
				tailleGraphique, editorArea);
		
		layout.getViewLayout("SAMNG.Vue.Graphique").setCloseable(false);
		layout.getViewLayout("SAMNG.Vue.Graphique").setMoveable(false);
		layout.getViewLayout("SAMNG.Vue.Tabulaire").setCloseable(false);
		layout.getViewLayout("SAMNG.Vue.Tabulaire").setMoveable(false);
		layout.getViewLayout("SAMNG.Vue.Liste").setCloseable(false);
		layout.getViewLayout("SAMNG.Vue.Liste").setMoveable(false);
		
	/*	layout.addFastView("SAMNG.Vue.Filtre.TabularVueFiltre");
		layout.addFastView("SAMNG.Vue.Filtre.VueListeFiltre");
		layout.addFastView("SAMNG.Vue.Filtre.GraphiqueVueFiltre");
		
		
		layout.getViewLayout("SAMNG.Vue.Filtre.TabularVueFiltre").setCloseable(false);
		layout.getViewLayout("SAMNG.Vue.Filtre.VueListeFiltre").setCloseable(false);
		layout.getViewLayout("SAMNG.Vue.Filtre.GraphiqueVueFiltre").setCloseable(false);*/
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
