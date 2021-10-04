package com.faiveley.samng.principal.ihm.actions.fichier;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchWindow;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.ICommandIds;

/**
 * @author Cosmin Udroiu
 */
public class FichierFermerAction extends Action {
	public FichierFermerAction(final IWorkbenchWindow window, final String text) {
		super(text);

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_CLOSE_MESSAGE);
		
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_CLOSE_MESSAGE);
		setAccelerator(SWT.CTRL + 'F');
	}

	@Override
	public void run() {		
		if (ActivatorData.getInstance().isMultimediaFileAlone()) {
			FichierFermerService.getInstance().closeMultimediaFile();
		} else {
			FichierFermerService.getInstance().closeFile();
		}
	}
	
	public void setEnabled() {
		setEnabled(isEnabled());
	}
	
	@Override
	public boolean isEnabled() {
		return !ActivatorData.getInstance().isFileEmpty();
	}
}
