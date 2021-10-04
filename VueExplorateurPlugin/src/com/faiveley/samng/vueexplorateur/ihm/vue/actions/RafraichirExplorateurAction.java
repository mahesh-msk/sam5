package com.faiveley.samng.vueexplorateur.ihm.vue.actions;

import org.eclipse.swt.widgets.Display;

import com.faiveley.samng.vueexplorateur.ihm.vue.VueExplorateurFichiersDeParcours;

public class RafraichirExplorateurAction {

	public static void rafraichirExplorateur() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				if (Display.getDefault().getActiveShell()!=null) {
					VueExplorateurFichiersDeParcours.refreshViewExplorateur(true);
				}					
			}
		});
	}
}
