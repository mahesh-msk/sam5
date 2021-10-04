package com.faiveley.samng.principal.ihm.actions.vue;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.principal.ihm.perspectives.PerspectiveAccueil;

public class VueAction extends Action{

	/** */
	private IWorkbenchWindow window;

	/** */
	protected String viewId;

	/** */
	private boolean showHide;

	/**
	 * Constructor
	 * 
	 * @param window	the window to open	
	 * @param actionId	the id of the action
	 * @param label		the label of the dialog
	 * @param viewId	the id of the view
	 * @param descriptor	the image 
	 */

	public VueAction(final IWorkbenchWindow window, 
			final String actionId,
			final String label, 
			final String viewId, 
			final ImageDescriptor descriptor,
			final boolean showHide) {
		this.window = window;
		this.viewId = viewId;

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
	 * Runs the action set for a Vue Action
	 * Gets the view with the current viewId and hide/open the view 
	 * */
	public void run() {
		if (getWindow() != null) {
			try {
				if (getWindow().getActivePage()==null){
					try {
						PlatformUI.getWorkbench().showPerspective(PerspectiveAccueil.getID(), getWindow());
						run();
					} catch (WorkbenchException e) {
						e.printStackTrace();
					}
				}

				int i = -1;
				IViewReference[] ivr = getWindow().getActivePage().getViewReferences();
				for (int t = 0; t < ivr.length; t++) {
					if (ivr[t].getId().equals(this.viewId)) {
						i = t;
					}
				}			

				if (i != 0 && showHide) {
					getWindow().getActivePage().hideView(ivr[i]);
				} else {
					getWindow().getActivePage().showView(getViewId());
				}
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getWindow().getShell(), "Error","Error opening view:" + e.getMessage());
			}
		}
	}

	/**
	 * @return the viewId
	 */
	public String getViewId() {
		return this.viewId;
	}

	/**
	 * @param viewId the viewId to set
	 */
	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	/**
	 * @return the window
	 */
	public IWorkbenchWindow getWindow() {
		return this.window;
	}

	/**
	 * @param window the window to set
	 */
	public void setWindow(IWorkbenchWindow window) {
		this.window = window;
	}

	//	@Override
	//	public boolean isEnabled() {
	////		if(ActivatorData.getInstance().getVueData().isEmpty())
	////			return false;
	//		return true;
	//	}
}