package com.faiveley.samng.principal.ihm.actions.fichier;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.principal.ihm.actions.captures.ICapturable;

public class SauverRapportDefautsAction extends Action{
	protected final IWorkbenchWindow window;

	public SauverRapportDefautsAction(final IWorkbenchWindow window, final String text) {
		super(text);
		this.window = window;
		//The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_SAVE_RAPPORT_DEFAUT);
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_SAVE_RAPPORT_DEFAUT);
		setImageDescriptor(com.faiveley.samng.principal.ihm.Activator
				.getImageDescriptor("/icons/sauver.png"));
	}

	/**
	 * Méthode d'ouverture de la fenetre de dialogue de capture de la vue
	 * sur laquelle on a le focus
	 */
	public final void run() {
		//récupération du widget  de contenu de la vue
		Composite contenu = getActiveViewContent();
		Control c[] = contenu.getChildren();
//		 creates a file dialog to open the binary files
		FileDialog dialog = new FileDialog(this.window.getShell(), SWT.SAVE);

		// définition des extensions visibles
		dialog.setFilterExtensions(new String[] { "*.txt" });  //$NON-NLS-1$

		dialog.setFilterNames(new String[] { "*.txt" });  //$NON-NLS-1$

		// récupération du nom du fichier et du chemin
		String cheminFichier = dialog.open();

		String nomFichier = dialog.getFileName();
		
		while(new File(cheminFichier).exists() || nomFichier.trim().equals("")){ //$NON-NLS-1$
			cheminFichier = dialog.open();
			nomFichier = dialog.getFileName();
		}
			
		
	
		try {
			FileWriter fileWriter = new  FileWriter(cheminFichier);
			fileWriter.write(((Text)c[0]).getText());
			fileWriter.close();
			MessageBox msgBox = new MessageBox(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), 
					SWT.ICON_INFORMATION | SWT.OK);
			msgBox.setText(Messages.getString("SauverRapportDefautsAction.1")); //$NON-NLS-1$
			msgBox.setMessage(Messages.getString("SauverRapportDefautsAction.0")); //$NON-NLS-1$
			msgBox.open();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
		
	}
	
	private Composite getActiveViewContent() {
		//récupération de la vue sur laquelle on a le focus
		try {
			IWorkbenchPart part = this.window.getActivePage().getActivePart();
			if(part == null) 
				return null;
			
			return part instanceof ICapturable ? ((ICapturable)part).getContenu() : null;
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public boolean isEnabled() {
		//return getActiveViewContent() != null;
		return true;
	}
	
}
