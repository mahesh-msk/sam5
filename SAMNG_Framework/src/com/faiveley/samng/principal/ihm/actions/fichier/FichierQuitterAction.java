package com.faiveley.samng.principal.ihm.actions.fichier;



import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.faiveley.samng.principal.ihm.ICommandIds;



/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class FichierQuitterAction extends Action {

	public FichierQuitterAction(final IWorkbenchWindow window, final String text) {
		super(text);

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_QUIT_MESSAGE);
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_QUIT_MESSAGE);
	}

	@Override
	public void run() {
		try{
			PlatformUI.getWorkbench().close();
		}
		catch(Exception ex){

		}
		finally{
			System.exit(0);
		}
	}
}
