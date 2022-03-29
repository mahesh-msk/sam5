package com.faiveley.samng.principal.ihm.actions.filtre;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.faiveley.samng.principal.ihm.ICommandIds;

/**
 * Generic action to show any filter view in a separate window (Issue 1104)
 * 
 * @author Olivier Prouvost
 *
 */
public class ShowFilterWindowAction extends Action {

	/** An helper to display the filter window (callable from differents places) */
	private ShowFilterWindowHelper helper;

	public ShowFilterWindowAction(String viewID) {
		helper = new ShowFilterWindowHelper(viewID);
	}

	// Define also a constructor compliant with the VueAction used to display also
	// filters...
	/** Use this constructor for toolbar */
	public ShowFilterWindowAction(final String label, final String viewId, final ImageDescriptor descriptor) {

		this(viewId);

		setText(label);
		setToolTipText(label);
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_OPEN_MARQUERS);
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_OPEN);

		setImageDescriptor(descriptor);
	}

	@Override
	public void run() {

		helper.showFilterWindow();

	}

}
