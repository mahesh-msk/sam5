package com.faiveley.samng.vuetabulaire.ihm.perspectives;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.faiveley.samng.principal.data.ActivatorData;




/** */
public class PerspectiveVueTabulaire implements IPerspectiveFactory {
	/** */
	private static String iD = "VueTabulairePlugin.perspectiveVueTabulaire";

	/**
	 * @param layout
	 *            1
	 */
	public final void createInitialLayout(final IPageLayout layout) {
		
		
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		
	if(!ActivatorData.getInstance().getVueData().isEmpty()){
		List<String> listeVues = new ArrayList<String>(1);
		listeVues.add("SAMNG.Vue.Tabulaire");
		//listeVues.add("SAMNG.Vue.Filtre.TabularVueFiltre");
		IFolderLayout folder ;
		for(int i=0; i<listeVues.size();i++){
			folder =layout.createFolder("", IPageLayout.TOP, 1f,
					editorArea);
			
			folder.addView(listeVues.get(i));
			layout.getViewLayout(listeVues.get(i)).setCloseable(false);
		}
	/*	layout.addFastView("SAMNG.Vue.Filtre.TabularVueFiltre");
		layout.getViewLayout("SAMNG.Vue.Filtre.TabularVueFiltre").setCloseable(false);
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
