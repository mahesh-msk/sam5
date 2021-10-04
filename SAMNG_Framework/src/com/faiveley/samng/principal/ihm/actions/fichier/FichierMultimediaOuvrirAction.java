package com.faiveley.samng.principal.ihm.actions.fichier;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.actions.vue.VueAction;

public class FichierMultimediaOuvrirAction {
	public static void ouvrirFichierMultimedia(final File file) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {				
				if (file != null) {
					ActivatorData.getInstance().setMultimediaFileAlone(file);
											
					for (Action action : ActivatorData.getInstance().getListMenuAction()) {
						if (action instanceof FichierFermerAction) {
							((FichierFermerAction) action).setEnabled();
						} else if (action instanceof VueAction && ((VueAction) action).getViewId().equals("SAMNG.Vue.Multimedia")) {
							action.setEnabled(true);
						}
					}
					
					IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					
					try {
						PlatformUI.getWorkbench().showPerspective("VueMultimediaPlugin.perspectiveVueMultimedia", window);
					} catch (WorkbenchException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
}
