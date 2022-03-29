package com.faiveley.samng.principal.ihm.vues.vuemarqueurs.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.vues.MessageSelection;
import com.faiveley.samng.principal.ihm.vues.vuemarqueurs.VueMarqueurs;
import com.faiveley.samng.principal.ihm.vues.vuemarqueurs.VueMarqueursDeltaValues;
import com.faiveley.samng.principal.sm.marqueurs.AMarqueur;

/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class MesureDeltaValeurMarqueurAction extends AbstractMarqueurAction {
	private String msgrErr = Messages.getString("MesureDeltaValeurMarqueurAction.0"); //$NON-NLS-1$
	public static final int MARKERS_COUNT = 2;
	
	public MesureDeltaValeurMarqueurAction() {
		String texteAction = Messages.getString("MesureDeltaValeurMarqueurAction.1"); //$NON-NLS-1$
		setText(texteAction);
		setToolTipText(texteAction);
		setImageDescriptor(com.faiveley.samng.principal.ihm.Activator
				.getImageDescriptor("/icons/vueToolBars/vue_annotation_delta.png"));
				
		
	}
	
	@Override
	public void run() {
		List<AMarqueur> listMarkers = new ArrayList<AMarqueur>();
		VueMarqueurs markersView = getVueMarqueurs();
		AMarqueur marker = null;
		if(markersView != null) {
			ISelection sel = markersView.getSelection();
			if(sel instanceof MessageSelection) {
				MessageSelection msgSel = (MessageSelection)sel;
				int[] msgIds;
				
				if(msgSel == null || msgSel.isEmpty() || (msgIds = msgSel.getMessagesIds()).length != MARKERS_COUNT) {
					showWarnDialog(markersView.getSite().getShell(), this.msgrErr);
				} else {
					for(int msgId: msgIds) {
						
						marker = ActivatorData.getInstance().getGestionnaireMarqueurs().getMarqueurParId(msgId);
						
						if(marker == null) {
							showWarnDialog(markersView.getSite().getShell(), this.msgrErr);
							return;	//we do not have in fact two lines
						}
						listMarkers.add(marker);
					}
					showMeasuredDeltaValues(listMarkers);
				}
			}
		}
	}
	
	private void showMeasuredDeltaValues(List<AMarqueur> markers) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		try {
			VueMarqueursDeltaValues vue = (VueMarqueursDeltaValues)window.getActivePage().
											showView(VueMarqueursDeltaValues.ID);
			int[] msgsIds = new int[markers.size()];
			for(int i = 0; i<markers.size(); i++) {
				msgsIds[i] = markers.get(i).getIdMessage();
			}
			vue.setMessagesIds(msgsIds);
		} catch (Exception e) {
			e.printStackTrace();
			showWarnDialog(window.getShell(), Messages.getString("MesureDeltaValeurMarqueurAction.2")); //$NON-NLS-1$
		}
	}
	
	public boolean isEnabled() {
		//return 	getVueMarqueurs() != null;
		return true;
	}
}
