package com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre.graphiquefiltretable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

public class ColorsConstants extends Composite {
	
	public ColorsConstants(Composite parent, int style) {
		super(parent, style);
		
		graphicBackgroundColor = new Color(getDisplay(), new RGB(185,
				230, 210));
		
		ERR_COLOR = getDisplay().getSystemColor(SWT.COLOR_RED);
	}
	
	private static Color graphicBackgroundColor;
	
	private static Color ERR_COLOR;

	public static Color getERR_COLOR() {
		return ERR_COLOR;
	}

	public void setERR_COLOR(Color err_color) {
		ERR_COLOR = err_color;
	}

	public static Color getGraphicBackgroundColor() {
		return graphicBackgroundColor;
	}

	public void setGraphicBackgroundColor(Color graphicBackgroundColor) {
		ColorsConstants.graphicBackgroundColor = graphicBackgroundColor;
	}
}
