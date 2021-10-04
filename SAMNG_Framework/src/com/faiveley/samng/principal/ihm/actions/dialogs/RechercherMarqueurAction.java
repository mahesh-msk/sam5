package com.faiveley.samng.principal.ihm.actions.dialogs;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.principal.ihm.listeners.ISearchMarquerListener;
import com.faiveley.samng.principal.sm.marqueurs.GestionnaireMarqueurs;

public class RechercherMarqueurAction extends Action{
	private boolean next;
	/**
	 * Constructor
	 * 
	 * @param actionId	the id of the action
	 * @param label		the label of the dialog
	 * @param descriptor	the image 
	 */
	public RechercherMarqueurAction( 
			final String actionId,
			final String label, 
			final ImageDescriptor descriptor,
			final boolean next) {
		this.next=next;


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
	 * Runs the action set for Search By Marquer 
	 * */
	public void run() {

		GestionnaireMarqueurs marquersGest = ActivatorData.getInstance().getGestionnaireMarqueurs();
		if (marquersGest != null) {
			int[] ids = marquersGest.getMarqueursIds();
			if (ids.length==0) {
				MessageBox messageBox = new MessageBox(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.getActivePart().getSite().getShell(), SWT.ICON_INFORMATION);
			    messageBox.setMessage(Messages.getString("Annotation.0"));
			    messageBox.open();
			}
		}

		ISearchMarquerListener listener = null;

		//looks for listeners in the current views
		IViewReference[] vr = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
		for (IViewReference v : vr) {
			IViewPart view = v.getView(false);
			if (view instanceof ISearchMarquerListener && view.getTitle()
					.equals(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart().getTitle())) {
				listener = ((ISearchMarquerListener) view);
			}
		}
		if (listener != null) {
			listener.onSelectedMarquerNomChange(this.next);
		}




		//ASearchDialog dlg = new SearchMarquer(Activator.getDefault().
				//	getWorkbench().getActiveWorkbenchWindow().getShell());

		//dlg.open();
	}
}
