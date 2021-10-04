package com.faiveley.samng.vueexplorateur.ihm.vue.actions;

import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.sm.util.StringUtils;
import com.faiveley.samng.vueexplorateur.ihm.vue.VueExplorateurFichiersDeParcours;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeParent;
import com.faiveley.samng.vueexplorateur.util.DialogUtils;
import com.faiveley.samng.vueexplorateur.util.WorkspaceService;

public class ChangerEspaceTravailAction {

	public static void changerEspace(TreeParent parent) {
		DirectoryDialog dd=new DirectoryDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
		
		dd.setMessage(Messages.getString("ChangerEspaceTravailAction_0"));
		dd.setText(Messages.getString("ChangerEspaceTravailAction_1"));
		
		//on récupère l'espace de travail sur lequel on a cliqué
		String currentWorkspaceDirectory = parent.getAbsoluteName();
		
		//si on récupère un chemin on l'utilise
		if (currentWorkspaceDirectory!=null && (!currentWorkspaceDirectory.equals(""))) { //$NON-NLS-1$
			dd.setFilterPath(currentWorkspaceDirectory);
		}	
		
		// on récupère les dossiers de l'espace de travail depuis le fichier missions.properties
		String[] workspaceDirectories = WorkspaceService.instance.getWorkspaceDirectories();	
		
		//on ouvre la boite de dialogue
		String path=dd.open();
		if (path!=null && path!="") { //$NON-NLS-1$
			
			// On doit vérifier si le nouvel emplacement ne correspond pas à l'emplacement d'un dossier du workspace
			// déjà présent
			boolean alreadyPresent = false;
			for(int i = 0; i < workspaceDirectories.length; i++) {
				if (path.equals(workspaceDirectories[i]) && !path.equals(currentWorkspaceDirectory)) {
					alreadyPresent = true;
					break;
				}
			}
			
			// Erreur dossier déjà présent dans le workspace
			if (alreadyPresent) {
				MessageBox msgBox = DialogUtils.getErrorMessageBox(Display.getCurrent().getActiveShell(), 
						Messages.getString("ErreurChangerEspaceTravailAction"), 
						Messages.getString("ErreurChangerEspaceTravailMessage"));
				msgBox.open();
				return;
			}
			
			// Le workspace peut être modifié
			for(int i = 0; i < workspaceDirectories.length; i++) {
				if (workspaceDirectories[i].equals(currentWorkspaceDirectory)) {
					workspaceDirectories[i] = path;
					break;
				}
			}
			String newWorkspacesStr = StringUtils.join(workspaceDirectories, WorkspaceService.WORKSPACE_SEP);
			
			// On met à jour le fichier missions.properties avec la nouvelle valeur
			WorkspaceService.instance.updateWorkspace(newWorkspacesStr);	
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					if (Display.getDefault().getActiveShell()!=null) {
						VueExplorateurFichiersDeParcours.refreshViewExplorateur(true);
					}					
				}
			});
		}
	}	
}
