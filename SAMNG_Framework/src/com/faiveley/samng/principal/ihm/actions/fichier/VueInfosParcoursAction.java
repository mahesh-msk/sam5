package com.faiveley.samng.principal.ihm.actions.fichier;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.principal.ihm.vues.vueinfosparcours.VueInfosParcours;

/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class VueInfosParcoursAction extends Action {
	private final IWorkbenchWindow window;
	
	public VueInfosParcoursAction(final IWorkbenchWindow window, final String text) {
		super(text);
		this.window = window;
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_VIEW_INFOS_PARCOURS);
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_VIEW_INFOS_PARCOURS);
		setToolTipText(text);
		setImageDescriptor(com.faiveley.samng.principal.ihm.Activator
				.getImageDescriptor("/icons/menu/appli_vue_info_parcours.png"));
	}
	
    @Override
    public void run() {
		if (this.window != null) {
			try {
				int i = 0;
				IViewReference[] ivr = this.window.getActivePage()
											.getViewReferences();
				for (int t = 0; t < ivr.length; t++) {
					if (ivr[t].getId().equals(VueInfosParcours.ID)) {
						i = t;
					}
				}

				if (i != 0) {
					this.window.getActivePage().hideView(ivr[i]);
				} else {
					this.window.getActivePage().showView(VueInfosParcours.ID);
					
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
		return true;
    }
}
