package com.faiveley.samng.principal.ihm.actions.profil;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.IWorkbenchWindow;

import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.principal.sm.util.file.GestionFichiers;

public class ImporterProfilAction extends Action {
	/** */
	public final IWorkbenchWindow window;

	public ImporterProfilAction(final IWorkbenchWindow window) {
		this.window = window;
	}
	
	/**
	 * 
	 * @param window
	 *            1
	 * @param text
	 *            1
	 */
	public ImporterProfilAction(final IWorkbenchWindow window, final String text) {
		super(text);
		this.window = window;

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_IMPORT_PROFIL);
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_IMPORT_PROFIL);
		setImageDescriptor(com.faiveley.samng.principal.ihm.Activator
				.getImageDescriptor("/icons/menu/appli_ouvrir.png")); //$NON-NLS-1$
		 setAccelerator(SWT.CTRL+'I');
	}

		
	/**
	 * Runs the action set for Open a File
	 */
	public final void run() {
		DirectoryDialog dlg = new DirectoryDialog(window.getShell());
		dlg.setFilterPath(RepertoiresAdresses.INSTALL_SAM_PARAM);
        dlg.setText(Messages.getString("ImporterProfil.1"));
        dlg.setMessage(com.faiveley.samng.principal.ihm.Messages.getString("ApplicationActionBarAdvisor.56"));
        String dir = dlg.open();
        if (dir != null) {
        	traiterRepertoireChoisi(dir);
        }		
	}
	
	public void traiterRepertoireChoisi(String dir){
		boolean profilValide=true;
		
		boolean configurationvuesExists=false;
		boolean filtresExists=false;
		boolean vbvExists=false;
		
		File dossierProfil=new File(dir);
		String nomdossierProfil=dossierProfil.getName();
		
		String profilActuel=RepertoiresAdresses.getNom_profil_actuel();
		
		if (!dossierProfil.isDirectory()) {
			profilValide=false;
		}
		File[] sousRep=dossierProfil.listFiles();
		if (sousRep.length<3) {
			profilValide=false;
		}
		for (int i = 0; i < sousRep.length; i++) {
			String dossierName=sousRep[i].getName();
			if (dossierName.toLowerCase().equals("configuration-vues")) {
				configurationvuesExists=true;
			}
			if (dossierName.equals("filtres")) {
				filtresExists=true;
			}
			if (dossierName.equals("VBV")) {
				vbvExists=true;
			}
		}
		if (!(vbvExists && filtresExists && configurationvuesExists)) {
			profilValide=false;
		}
		
		if (profilValide==false) {
			MessageDialog.openError(window.getShell(), "", Messages.getString("ImporterProfil.3"));
		}else if (profilActuel.equals(nomdossierProfil)) {
			MessageDialog.openWarning(window.getShell(), "", Messages.getString("ImporterProfil.5"));
		}else if(GestionnaireProfil.getInstance().profilPresent(nomdossierProfil)){
			boolean ecraser=MessageDialog.openQuestion(window.getShell(), "", Messages.getString("ImporterProfil.6"));
			if (ecraser) {
				GestionnaireProfil.getInstance().supprimerProfil(nomdossierProfil);
				importerProfil(nomdossierProfil, dossierProfil);
			}
		}else{
			importerProfil(nomdossierProfil, dossierProfil);
		}
	}
	
	public void importerProfil(String nomdossierProfil,File f1){
		File newProfil=new File(RepertoiresAdresses.profil+ File.separator +nomdossierProfil);
		GestionFichiers.copierRepertoire(f1, newProfil);
		
		MessageDialog.openInformation(window.getShell(), "", Messages.getString("ImporterProfil.4")+" "+nomdossierProfil+".xml");
	}
}
