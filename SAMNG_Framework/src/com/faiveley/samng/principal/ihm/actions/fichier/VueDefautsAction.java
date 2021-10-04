package com.faiveley.samng.principal.ihm.actions.fichier;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.principal.ihm.vues.vuedefauts.VueDefauts;

/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class VueDefautsAction extends Action {
	private final IWorkbenchWindow window;
	
	public VueDefautsAction(final IWorkbenchWindow window, final String text) {
		super(text);
		this.window = window;
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_VIEW_ERRORS_MESSAGE);
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_VIEW_ERRORS_MESSAGE);
		setImageDescriptor(com.faiveley.samng.principal.ihm.Activator
				.getImageDescriptor("/icons/menu/appli_vue_anomalies.png"));
	}
	
    @Override
    public void run() {
		if (this.window != null) {
			try {
				int i = 0;
				IViewReference[] ivr = this.window.getActivePage()
											.getViewReferences();
				for (int t = 0; t < ivr.length; t++) {
					if (ivr[t].getId().equals(VueDefauts.ID)) {
						i = t;
					}
				}

				if (i != 0) {
					this.window.getActivePage().hideView(ivr[i]);
				} else {
					this.window.getActivePage().showView(VueDefauts.ID);
					
				}
				
			} catch (PartInitException e) {
				MessageDialog.openError(this.window.getShell(), "Error",
						"Error opening view:" + e.getMessage());
			}
		}
    }
    
    @Override
    public boolean isEnabled() {
		if(ActivatorData.getInstance().getVueData().isEmpty())
			return false;
		else{
			/*String curLine = null;
			FileInputStream fileInputStream = null;
			try {
				fileInputStream = new FileInputStream(RepertoiresAdresses.logs_parser_log_TXT);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BufferedReader inFile = new BufferedReader(new InputStreamReader(fileInputStream));
			Set<String> lineMessages = new LinkedHashSet<String>();
			try {
				while ((curLine = inFile.readLine()) != null) {
					if(!lineMessages.contains(curLine))
						lineMessages.add(curLine);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int nbErrors=lineMessages.size();
			if (nbErrors>0) {
				return true;
			}else{
				return false;
			}*/
			// La vue défaut doit être accecible même si aucun défaut n'est présent.
			return true ;
		}
    }
}
