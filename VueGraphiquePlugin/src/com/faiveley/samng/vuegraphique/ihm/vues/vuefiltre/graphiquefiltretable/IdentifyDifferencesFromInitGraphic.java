package com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre.graphiquefiltretable;

import java.util.List;

import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.GraphiqueFiltreComposite;

public class IdentifyDifferencesFromInitGraphic {

	/**
	 * Computes the change state from the orginial one
	 * 
	 * @return true if the filter changed
	 */
	public static boolean isDifferentFromInitialValues(List<AFiltreComposant> initialGraphicsList, 
			boolean isChangedStateFromInitial, boolean linesInterchanged,GraphiqueFiltreComposite [] permanentGraphics) {
		List<AFiltreComposant> selValues = GraphiqueFiltreComposite.getSelectedValues(permanentGraphics,true);
		if (initialGraphicsList.size() != selValues.size()) {
			isChangedStateFromInitial = true;
		} else {
			selValues.removeAll(initialGraphicsList);
			isChangedStateFromInitial = (selValues.size() > 0);
		}
		return isChangedStateFromInitial || linesInterchanged;
	}
}
