package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.vues.MessageSelection;
import com.faiveley.samng.principal.sm.data.enregistrement.ListMessages;
import com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.VueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe.GestionnaireGraphesNotifications;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.curseur.CursorPositionEvent;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class VueGraphicAction extends Action {

	public VueGraphicAction() {
		super();
	}

	public VueGraphicAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	public VueGraphicAction(String text, int style) {
		super(text, style);
	}

	public VueGraphicAction(String text) {
		super(text);
	}

	protected VueGraphique getVueGraphique() {
		IWorkbenchWindow window = ActivatorVueGraphique.getDefault().getWorkbench().getActiveWorkbenchWindow();
		IViewReference[] ivr = window.getActivePage().getViewReferences();
		VueGraphique vueGraphique;
		for (int t = 0; t < ivr.length; t++) {
			if (ivr[t].getId().equals(VueGraphique.ID)) {
				vueGraphique = (VueGraphique)ivr[t].getPart(false);
				return vueGraphique;
			}
		}
		return null;
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if ((selection instanceof MessageSelection) && (part != this)
				&& !selection.isEmpty()) {
			if (!((MessageSelection) selection).getUserSendSelection())
				return;
			int selId = ((MessageSelection) selection).getMessageId();

			ListMessages listeMessages = ActivatorData.getInstance()
					.getVueData().getDataTable().getEnregistrement()
					.getMessages();
			CursorPositionEvent cursorEvent = new CursorPositionEvent(
					listeMessages.get(0), listeMessages.getMessageById(selId),
					-1, -1, false);

			GestionnaireGraphesNotifications
					.notifyCursorPositionChanged(cursorEvent);
		}
	}
}
