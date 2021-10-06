package com.faiveley.samng.vuegraphique.ihm.perspectives;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.faiveley.samng.principal.data.ActivatorData;




/** */
public class PerspectiveVueGraphique implements IPerspectiveFactory {
	/** */
	private static String iD = "VueGraphiquePlugin.perspectiveVueGraphique";

	/**
	 * @param layout
	 *            1
	 */
	public final void createInitialLayout(final IPageLayout layout) {
		
		
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		
		if(!ActivatorData.getInstance().getVueData().isEmpty()){
		List<String> listeVues = new ArrayList<String>();
		listeVues.add("SAMNG.Vue.Graphique");
		//listeVues.add("SAMNG.Vue.Filtre.GraphiqueVueFiltre");
		IFolderLayout folder ;
		for(int i=0; i<listeVues.size();i++){
			folder =layout.createFolder("", IPageLayout.TOP, 1f,
					editorArea);
			
			folder.addView(listeVues.get(i));
			layout.getViewLayout(listeVues.get(i)).setCloseable(false);
		}
		/*layout.addFastView("SAMNG.Vue.Filtre.GraphiqueVueFiltre");
		

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
