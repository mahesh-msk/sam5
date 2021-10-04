package com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre.graphiquefiltretable;

import com.faiveley.samng.principal.sm.filtres.GraphicConstants;
import com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre.Messages;

public class LabelForGraphic {
	
	/**
	 * Returns the analogic label for the given graphic index
	 * 
	 * @param graphicIndex
	 * @return
	 */
	public static String getAnalogLabelForGraphic(int graphicIndex) {
		if (graphicIndex < 0 || graphicIndex >= GraphicConstants.MAX_GRAPHICS_COUNT)
			return ""; //$NON-NLS-1$

		return Messages.getString("GraphiqueFiltresEditorTable.28")
				+ graphicIndex + " - "+Messages.getString("GraphiqueFiltresEditorTable.33");

	}

	public static String getDigitalLabelForGraphic(int graphicIndex) {
		if (graphicIndex < 0 || graphicIndex >= GraphicConstants.MAX_GRAPHICS_COUNT)
			return ""; //$NON-NLS-1$

		return Messages.getString("GraphiqueFiltresEditorTable.31")
				+ graphicIndex + " - "+Messages.getString("GraphiqueFiltresEditorTable.34");
	}
	
	/**
	 * Returns the not used label for the given graphic index
	 * 
	 * @param graphicIndex
	 * @return
	 */
	public static String getNotUsedLabelForGraphic(int graphicIndex) {
		if (graphicIndex < 0 || graphicIndex >= GraphicConstants.MAX_GRAPHICS_COUNT)
			return ""; //$NON-NLS-1$

		return Messages.getString("GraphiqueFiltresEditorTable.25") + graphicIndex + " - "+Messages.getString("GraphiqueFiltresEditorTable.3"); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
