package com.faiveley.samng.vueexplorateur.ihm.vue.actions;

import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

import com.faiveley.samng.principal.sm.util.StringUtils;
import com.faiveley.samng.vueexplorateur.ihm.vue.VueExplorateurFichiersDeParcours;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeParent;
import com.faiveley.samng.vueexplorateur.util.DialogUtils;
import com.faiveley.samng.vueexplorateur.util.WorkspaceService;

public class ChangerEspaceTravailAction {

	public static void changerEspace(TreeParent parent) {
		DirectoryDialog dd=new DirectoryDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		
		dd.setMessage(Messages.getString("ChangerEspaceTravailAction_0"));
		dd.setText(Messages.getString("ChangerEspaceTravailAction_1"));
		
		//on r�cup�re l'espace de travail sur lequel on a cliqu�
		String currentWorkspaceDirectory = parent.getAbsoluteName();
		
		//si on r�cup�re un chemin on l'utilise
		if (currentWorkspaceDirectory!=null && (!currentWorkspaceDirectory.equals(""))) { //$NON-NLS-1$
			dd.setFilterPath(currentWorkspaceDirectory);
		}	
		
		// on r�cup�re les dossiers de l'espace de travail depuis le fichier missions.properties
		String[] workspaceDirectories = WorkspaceService.instance.getWorkspaceDirectories();	
		
		//on ouvre la boite de dialogue
		String path=dd.open();
		if (path!=null && path!="") { //$NON-NLS-1$
			
			// On doit v�rifier si le nouvel emplacement ne correspond pas � l'emplacement d'un dossier du workspace
			// d�j� pr�sent
			boolean alreadyPresent = false;
			for(int i = 0; i < workspaceDirectories.length; i++) {
				if (path.equals(workspaceDirectories[i]) && !path.equals(currentWorkspaceDirectory)) {
					alreadyPresent = true;
					break;
				}
			}
			
			// Erreur dossier d�j� pr�sent dans le workspace
			if (alreadyPresent) {
				MessageBox msgBox = DialogUtils.getErrorMessageBox(Display.getCurrent().getActiveShell(), 
						Messages.getString("ErreurChangerEspaceTravailAction"), 
						Messages.getString("ErreurChangerEspaceTravailMessage"));
				msgBox.open();
				return;
			}
			
			// Le workspace peut �tre modifi�
			for(int i = 0; i < workspaceDirectories.length; i++) {
				if (workspaceDirectories[i].equals(currentWorkspaceDirectory)) {
					workspaceDirectories[i] = path;
					break;
				}
			}
			String newWorkspacesStr = StringUtils.join(workspaceDirectories, WorkspaceService.WORKSPACE_SEP);
			
			// On met � jour le fichier missions.properties avec la nouvelle valeur
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
