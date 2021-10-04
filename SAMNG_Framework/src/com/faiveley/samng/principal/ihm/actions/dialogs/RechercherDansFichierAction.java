package com.faiveley.samng.principal.ihm.actions.dialogs;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbenchWindow;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.principal.ihm.actions.fichier.FichierFermerAction;
import com.faiveley.samng.principal.ihm.vues.search.ASearchDialog;
import com.faiveley.samng.principal.ihm.vues.search.SearchInFile;


public class RechercherDansFichierAction extends Action{
	private IWorkbenchWindow window;

	/**
	 * Constructor
	 * 
	 * @param actionId	the id of the action
	 * @param label		the label of the dialog
	 * @param descriptor	the image 
	 */
	public RechercherDansFichierAction( 
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
	 * Runs the action set for Search In File 
	 * */
	public void run() {
//		Activator.getDefault().release();
		boolean search=false;
		if (this.window == null)
			this.window = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();

		if(ActivatorData.getInstance().getVueData().getDataTable()!=null){
			MessageBox msgBox;
			try{
				msgBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL );
				msgBox.setText(""); //$NON-NLS-1$
				msgBox.setMessage(Messages.getString("SearchInFileAction.1")); //$NON-NLS-1$

				if (msgBox.open() == SWT.OK) {
					search=true;
					
					FichierFermerAction ffa = null ;
					ffa = new FichierFermerAction(
							Activator.getDefault().getWorkbench().getActiveWorkbenchWindow(), 
							"");
					ffa.run();
				}
			}catch (Exception e) {
				search=true;
			}
		}else{
			search = true;
		}

		if (search){
			//Activator.getDefault().release();
			ASearchDialog dlg = new SearchInFile(Activator.getDefault().
					getWorkbench().getActiveWorkbenchWindow().getShell());

			if (dlg.open() == SearchInFile.RET_RELEASE) { 
				Activator.getDefault().release();
			}
		}
	}
}
