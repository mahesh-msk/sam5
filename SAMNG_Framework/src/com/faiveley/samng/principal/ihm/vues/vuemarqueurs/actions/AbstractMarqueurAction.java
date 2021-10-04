package com.faiveley.samng.principal.ihm.vues.vuemarqueurs.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.vues.MessageSelection;
import com.faiveley.samng.principal.ihm.vues.vuemarqueurs.VueMarqueurs;
import com.faiveley.samng.principal.sm.marqueurs.AMarqueur;

/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class AbstractMarqueurAction extends Action {
	
	private String msgrErr = Messages.getString("AbstractMarqueurAction.0"); //$NON-NLS-1$
	
	public AbstractMarqueurAction() {
		
	}
	
//	@Override
//	public boolean isEnabled() {
//		return getVueMarqueurs() != null;
//	}
	
	protected VueMarqueurs getVueMarqueurs() {
		IWorkbenchPage page = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if(page == null)
			return null;
		final IWorkbenchPart activePart = page.getActivePart();
		if(activePart instanceof VueMarqueurs) {
			return (VueMarqueurs)activePart;
		}
		return null;
	}
	
	protected AMarqueur getSelectedMarker() {
		VueMarqueurs markersView = getVueMarqueurs();
		AMarqueur marker = null;
		if(markersView != null) {
			ISelection sel = markersView.getSelection();
			if(sel instanceof MessageSelection) {
				MessageSelection msgSel = (MessageSelection)sel;
				if(msgSel == null || msgSel.isEmpty()) {
					showWarnDialog(markersView.getSite().getShell(), this.msgrErr);
				} else {
					int msgId = msgSel.getMessageId();
					marker = ActivatorData.getInstance().getGestionnaireMarqueurs().getMarqueurParId(msgId);
				}
			}
		}
		return marker;
	}
	
	protected List<AMarqueur> getSelectedMarkers() {
		ArrayList<AMarqueur> listMarkers = new ArrayList<AMarqueur>();
		VueMarqueurs markersView = getVueMarqueurs();
		AMarqueur marker = null;
		if(markersView != null) {
			ISelection sel = markersView.getSelection();
			if(sel instanceof MessageSelection) {
				MessageSelection msgSel = (MessageSelection)sel;
				if(msgSel == null || msgSel.isEmpty()) {
					showWarnDialog(markersView.getSite().getShell(), this.msgrErr);
				} else {
					int[] msgIds = msgSel.getMessagesIds();
					for(int msgId: msgIds) {
						marker = ActivatorData.getInstance().getGestionnaireMarqueurs().getMarqueurParId(msgId);
						if(marker != null)
							listMarkers.add(marker);
					}
				}
			}
		}
		listMarkers.trimToSize();
		return listMarkers;
	}
	
	protected void showWarnDialog(Shell shell, String errMessage) {
		MessageBox msgBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
		msgBox.setText(""); //$NON-NLS-1$
		msgBox.setMessage(errMessage);
		msgBox.open();
	}
}
