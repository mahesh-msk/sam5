package com.faiveley.samng.principal.ihm.actions.fichier;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.actions.profil.GestionnaireProfil;
import com.faiveley.samng.principal.ihm.perspectives.PerspectiveAccueil;
import com.faiveley.samng.principal.sm.corrections.GestionnaireCorrection;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosFichierSamNg;
import com.faiveley.samng.principal.sm.erreurs.ParseurXMLException;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;

public class FichierFermerService {
	private static FichierFermerService INSTANCE;	
	
	private FichierFermerService() {	
	}
	
	public static FichierFermerService getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new FichierFermerService();
		}
		
		return INSTANCE;
	}
	
	public void closeMultimediaFile() {
		ActivatorData.getInstance().clearMultimediaFiles();
		
		closeMultimediaPerspectives();
		ActivatorData.getInstance().getBarAdvisor().setActionsEnabled(false);
		return;
	}
	
	private void closeMultimediaPerspectives() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() ;
		
		for (IPerspectiveDescriptor p : page.getOpenPerspectives()) {
			if (p.getId().equals("VueMultimediaPlugin.perspectiveVueMultimedia")) {
				page.closePerspective(p, false, false);
			}
		}
	}

	public void closeFile() {
		if (ActivatorData.getInstance().hasMultimediaFiles()) {
			ActivatorData.getInstance().clearMultimediaFiles();
		}
		
		if (ActivatorData.getInstance().isCompressedFile()) {
			deleteTempFile(ActivatorData.getInstance().getAbsoluteFileName());
			ActivatorData.getInstance().clearCompressedFile();
		}

		GestionnaireCorrection.getInstance().saveCorrections(false);
		
		if (ActivatorData.getInstance().getGestionnaireMarqueurs().isModifications()) {
			MessageBox msgBox = new MessageBox(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			msgBox.setText(Messages.getString("GestionnaireVueListeBase.11")); //$NON-NLS-1$
			msgBox.setMessage(Messages.getString("GestionnaireVueListeBase.12")); //$NON-NLS-1$ //$NON-NLS-2$
			int ret = msgBox.open();
			
			if (ret == SWT.YES) {
				SauverRemarquesUtilisateurAction sura = new SauverRemarquesUtilisateurAction(Messages.getString("GestionnaireVueListeBase.11")); //$NON-NLS-1$
				sura.run();
			}
		}

		try {
			ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().enregistrerVBV();
		} catch (ParseurXMLException e1) {
			MessageBox msgBox = new MessageBox(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.YES);
			msgBox.setText(com.faiveley.samng.principal.ihm.actions.fichier.Messages.getString("ErreurParseurVBV.1"));
			msgBox.setMessage(com.faiveley.samng.principal.ihm.actions.fichier.Messages.getString("ErreurParseurVBV.2"));
			msgBox.open();
		}

		boolean sauvegarderVueAccueil = true ;
		
		try {			
			try {				
				((InfosFichierSamNg) FabriqueParcours.getInstance().getParcours().getInfo()).setNomFichierParcoursBinaire(null);
			}
			// Pas de fichier ouvert
			catch(Exception ex) {
				sauvegarderVueAccueil = false ;								
			}
			
								
			// Launch Gestion Mission perspective to save explorer view
			try {
				PlatformUI.getWorkbench().showPerspective("SAMNG.perspectiveMission", Activator.getDefault().getWorkbench().getActiveWorkbenchWindow()) ;
			} catch (WorkbenchException e) {
				e.printStackTrace();
			}											
			
			closePerspectives(sauvegarderVueAccueil);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		if (Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage() == null) {
			try {
				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().openPage(PerspectiveAccueil.getID(), null);
			} catch (WorkbenchException e) {
				e.printStackTrace();
			}
		}

		ActivatorData.getInstance().notifyDataListeners();
		Activator.getDefault().release();
		ActivatorData.getInstance().getBarAdvisor().setActionsEnabled(false);

		GestionnaireProfil.getInstance().resetProfil();
	}
		
	private void closePerspectives(boolean sauvegarderVueAccueil) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		// Get the open perspectives with Gestion Mission perspective (sure)
		IPerspectiveDescriptor[] perspectives = page.getOpenPerspectives();
		
		// Close all perspectives but Gestion Mission
		for (IPerspectiveDescriptor perspective : perspectives) {
			if (!perspective.getId().equals("SAMNG.perspectiveMission") && !perspective.getId().equals("VueMultimediaPlugin.perspectiveVueMultimedia")) {
				if (perspective.getId().equals("SAMNG.perspectiveAccueil") && sauvegarderVueAccueil) {
					page.setPerspective(perspective);
				}
				
				page.closePerspective(perspective, false, false) ;					
			}
		}
	}
	
	private void deleteTempFile(String fileName) {
		File tempFile = new File(fileName);
		if (tempFile.exists()) {
			boolean deletionResult = tempFile.delete();
			if (!deletionResult) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.getString("FichierFermerAction.5"), 
						Messages.getString("FichierFermerAction.6"));
			}
		}
	}
}
