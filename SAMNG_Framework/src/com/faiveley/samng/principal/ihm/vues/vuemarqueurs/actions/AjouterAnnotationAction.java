package com.faiveley.samng.principal.ihm.vues.vuemarqueurs.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.vues.MessageSelection;
import com.faiveley.samng.principal.ihm.vues.vuemarqueurs.MarqueurCommentaireDialog;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.flag.Flag;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.ParcoursComposite;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.linecommands.GestionLineCommandParameters;
import com.faiveley.samng.principal.sm.marqueurs.Marqueur;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class AjouterAnnotationAction extends AbstractMarqueurAction {

	private String msgrErr = Messages.getString("AjouterMarqueurAction.0"); //$NON-NLS-1$

	public AjouterAnnotationAction() {
		setText(Messages.getString("AjouterMarqueurAction.1")); //$NON-NLS-1$
		setToolTipText(Messages.getString("AjouterMarqueurAction.1"));
		setImageDescriptor(com.faiveley.samng.principal.ihm.Activator
				.getImageDescriptor("/icons/toolBar/vues_commun_ajouter_annotation.png"));

	}

	@Override
	public void run() {
		final IWorkbenchPart activePart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		if(activePart instanceof ISelectionProvider) {
			ISelectionProvider selProvider = (ISelectionProvider)activePart;
			ISelection sel = selProvider.getSelection();
			if(!(sel instanceof MessageSelection))
				return;
			MessageSelection msgSel = (MessageSelection)sel;
			if(msgSel == null || msgSel.isEmpty()) {
				MessageBox msgBox = new MessageBox(activePart.getSite().getShell(), SWT.ICON_WARNING | SWT.OK);
				msgBox.setText(""); //$NON-NLS-1$
				msgBox.setMessage(msgrErr);
				msgBox.open();
				return;
			}
			int msgId = msgSel.getMessageId();
			MarqueurCommentaireDialog inputDlg = new MarqueurCommentaireDialog(activePart.getSite().getShell(), 
					Messages.getString("AjouterMarqueurAction.3"), Messages.getString("AjouterMarqueurAction.4"), "", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			int res = inputDlg.open();
			if (res == SWT.OK) {
				String markerName = inputDlg.getValue();
				Marqueur newMarker = new Marqueur();
				newMarker.setCommentaire(markerName);
				newMarker.setIdMessage(msgId);
				newMarker.setNom(ActivatorData.getInstance().getGestionnaireMarqueurs().generateUniqueMarkerName());
				ActivatorData.getInstance().getGestionnaireMarqueurs().ajouterMarqueur(newMarker);
				ActivatorData.getInstance().getVueData().addMarkerId(msgId);
				Message message=ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getGoodMessage(msgId);
				ActivatorData.getInstance().setSelectedMsg(message);

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				createAnnotation(msgId);
				 ActivatorData.getInstance().getPoolDonneesVues().put(new String("modifMarqueurListe"), true);
					ActivatorData.getInstance().getPoolDonneesVues().put(new String("modifMarqueurTab"), true);
					ActivatorData.getInstance().notifyRepereListeners(
						true,
						new TypeRepere[] { TypeRepere.distanceRelatif,
								TypeRepere.tempsRelatif });
			}
		}
	}

	public void createAnnotation(int msgId){				
		ParcoursComposite p = FabriqueParcours.getInstance().getParcours();
		if (p == null) {
			return;
		}
		// get enregisrtrement
		Enregistrement enrg = p.getData().getEnregistrement();
		if (enrg == null) {
			return;
		}
		
		//get the reference time and distance
		Message m = enrg.getGoodMessage(Integer.valueOf(msgId));
		Flag flagmsg = m.getFlag();
		Flag fl;
		if (flagmsg != null) {
			if (!(flagmsg.getLabel().contains("A"))) {
				fl = new Flag(msgId, flagmsg.getLabel() + "A", m.getEvenement()
						.getM_ADescripteurComposant()
						.getM_AIdentificateurComposant().getNom());
				m.setFlag(fl);
			}
		} else {
			fl = new Flag(msgId, "A", m.getEvenement()
					.getM_ADescripteurComposant()
					.getM_AIdentificateurComposant().getNom());
			m.setFlag(fl);
		}
		


		
	}

	@Override
	public boolean isChecked() {
		return getVueMarqueurs() != null;
	}

//	@Override
//	public boolean isEnabled() {
//		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//		if(page == null)
//			return false;
//		boolean enabled = false;
//
//		final IWorkbenchPart activePart = page.getActivePart();
//		if(activePart instanceof ISelectionProvider	&& !(activePart instanceof VueMarqueurs)
//		) {
//			ISelectionProvider selProvider = (ISelectionProvider)activePart;
//			ISelection sel = selProvider.getSelection();
//			if(sel instanceof MessageSelection) {
//				enabled = true;
//			}
//		}
//		
//		return enabled;
//	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return (!GestionLineCommandParameters.isAnnot_Lect_seule());
	}

}
