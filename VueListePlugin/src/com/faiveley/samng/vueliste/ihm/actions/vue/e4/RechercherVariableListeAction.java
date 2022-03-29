package com.faiveley.samng.vueliste.ihm.actions.vue.e4;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.principal.ihm.vues.search.ASearchDialog;
import com.faiveley.samng.vueliste.ihm.dialogs.e4.SearchVariableListe;

public class RechercherVariableListeAction extends Action{
	
	
	/**
	 * Constructor
	 * 
	 * @param actionId	the id of the action
	 * @param label		the label of the dialog
	 * @param descriptor	the image 
	 */
	public RechercherVariableListeAction( 
			final String actionId,
			final String label, 
			final ImageDescriptor descriptor) {
		
		setText(label);
		setToolTipText(label);
		// The id is used to refer to the action in a menu or toolbar
		setId(actionId);
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_OPEN);
		
		setImageDescriptor(descriptor);
	}
	
	/**
	 * Runs the action set for Search By Variable 
	 * */
	public void run() {
		ASearchDialog dlg = new SearchVariableListe(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		dlg.open();
	}
}
