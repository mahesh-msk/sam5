package com.faiveley.samng.principal.ihm.actions.fichier;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.principal.sm.linecommands.GestionLineCommandParameters;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class SauverRemarquesUtilisateurAction extends Action {
	
	public SauverRemarquesUtilisateurAction(final String text) {
		setText(text);
		setToolTipText(text);
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_SAVES_ANNOTATIONS);
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_SAVES_ANNOTATIONS);
		setImageDescriptor(com.faiveley.samng.principal.ihm.Activator.getImageDescriptor("/icons/menu/appli_enr_annotation.png"));
		setAccelerator(SWT.CTRL+'E');
	}
	
	@Override
	public void run() {
			
		//the filename is kept in the GestionnaireMarquers as the last file name loaded
		ActivatorData.getInstance().getGestionnaireMarqueurs().enregistrerRemarques(""); //$NON-NLS-1$
		
//		if(success) {
//			MessageBox msgBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
//					SWT.ICON_INFORMATION | SWT.OK);
//			msgBox.setText(""); //$NON-NLS-1$
//			msgBox.setMessage(Messages.getString("SaveUserRemarksAction.1")); //$NON-NLS-1$
//			msgBox.open();
//		} else {
//			MessageBox msgBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
//					SWT.ICON_ERROR | SWT.OK);
//			msgBox.setText(""); //$NON-NLS-1$
//			msgBox.setMessage(Messages.getString("SaveUserRemarksAction.2")); //$NON-NLS-1$
//			msgBox.open();
//		}
		
	}
	
	@Override
	public boolean isEnabled() {
		if(ActivatorData.getInstance().getVueData().isEmpty() || (GestionLineCommandParameters.isAnnot_Lect_seule()))
			return false;
		return true;
	}
}
