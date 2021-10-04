package com.faiveley.samng.vueexplorateur.ihm.vue.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import com.faiveley.samng.vueexplorateur.ihm.vue.VueExplorateurFichiersDeParcours;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeParent;
import com.faiveley.samng.vueexplorateur.util.DialogUtils;
import com.faiveley.samng.vueexplorateur.util.WorkspaceService;

public class SupprimerEspaceTravailAction {

	public static void supprimerEspaceTravail(TreeParent parent) {
		// On r�cup�re le dossier du workspace s�lectionn�
		String currentWorkspaceDirectory = parent.getAbsoluteName();
		
		// On r�cup�re la liste des dossiers du workspace
		String[] workspaceDirectories = WorkspaceService.instance.getWorkspaceDirectories();
		
		// S'il n'en reste qu'un, on ne peut pas le supprimer
		if (workspaceDirectories.length == 1) {
			// afficher msgbox erreur
			MessageBox msgBox = DialogUtils.getErrorMessageBox(Display.getCurrent().getActiveShell(), 
					Messages.getString("ErreurSupprimerEspaceTravailAction"),
					Messages.getString("ErreurSupprimerEspaceTravailMessage")); //$NON-NLS-1$ //$NON-NLS-2$
			msgBox.open();
			return;
		}
		
		// Il y a plus qu'un seul dossier dans le workspace, OK pour supprimer celui qui est s�lectionn� : 
		// on demande confirmation � l'utilisateur
		MessageBox msgBox = DialogUtils.getConfirmMessageBox(Display.getCurrent().getActiveShell(), 
				Messages.getString("ConfirmerSupprimerEspaceTravailAction"),
				Messages.getString("ConfirmerSupprimerEspaceTravailMessage"));
		int ret = msgBox.open();
		if(ret == SWT.NO) {
			return;
		}
		
		// L'utilisateur a valid� la suppression
		// On recr�� la cha�ne moins le dossier du workspace � enlever
		StringBuilder newWorkspace = new StringBuilder();
		for(int i = 0; i < workspaceDirectories.length; i++) {
			if (!workspaceDirectories[i].equals(currentWorkspaceDirectory)) {
				newWorkspace.append(workspaceDirectories[i]);
				if (i != workspaceDirectories.length - 1) {
					newWorkspace.append(WorkspaceService.WORKSPACE_SEP);
				}
			}
		}
		
		// On met � jour le fichier missions.properties avec la nouvelle valeur
		WorkspaceService.instance.updateWorkspace(newWorkspace.toString());
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				if (Display.getDefault().getActiveShell()!=null) {
					VueExplorateurFichiersDeParcours.refreshViewExplorateur(true);
				}					
			}
		});
	}
}
