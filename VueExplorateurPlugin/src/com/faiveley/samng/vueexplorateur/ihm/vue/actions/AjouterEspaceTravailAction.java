package com.faiveley.samng.vueexplorateur.ihm.vue.actions;

import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

import com.faiveley.samng.principal.sm.util.StringUtils;
import com.faiveley.samng.vueexplorateur.ihm.vue.VueExplorateurFichiersDeParcours;
import com.faiveley.samng.vueexplorateur.util.DialogUtils;
import com.faiveley.samng.vueexplorateur.util.WorkspaceService;

public class AjouterEspaceTravailAction {

	public static void ajouterEspace() {
		DirectoryDialog dd=new DirectoryDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		
		dd.setMessage(Messages.getString("AjouterEspcceTravailAction_0"));
		dd.setText(Messages.getString("AjouterEspcceTravailAction_1"));
		
		//on ouvre la boite de dialogue
		String path=dd.open();
		if (path!=null && path!="") { //$NON-NLS-1$
			
			boolean found = false;
			String[] workspaceDirectories = WorkspaceService.instance.getWorkspaceDirectories();
			int i = 0;
			while(!found && i < workspaceDirectories.length) {
				if (workspaceDirectories[i].equals(path)) {
					found = true;
				}
				i++;
			}
			
			if (!found) {
				String newWorkspaces = StringUtils.join(workspaceDirectories, WorkspaceService.WORKSPACE_SEP);
				newWorkspaces += WorkspaceService.WORKSPACE_SEP + path;
				WorkspaceService.instance.updateWorkspace(newWorkspaces);	
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						if (Display.getDefault().getActiveShell()!=null) {
							VueExplorateurFichiersDeParcours.refreshViewExplorateur(true);
						}					
					}
				});
			}
			else {
				// afficher message erreur : le dossier existe d�j� dans la liste des dossiers du workspace
				MessageBox msgBox = DialogUtils.getErrorMessageBox(Display.getCurrent().getActiveShell(),
						Messages.getString("ErreurAjouterEspaceTravailAction"),
						Messages.getString("ErreurAjouterEspaceTravailMessage"));
				msgBox.open();
			}
		}
	}
}
