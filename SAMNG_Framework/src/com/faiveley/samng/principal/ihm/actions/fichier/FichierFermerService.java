package com.faiveley.samng.principal.ihm.actions.fichier;

import java.io.File;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
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
import com.faiveley.samng.principal.ihm.perspectives.PerspectiveGestionDesMissions;
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
			MessageBox msgBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
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
			MessageBox msgBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.YES);
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
				PlatformUI.getWorkbench().showPerspective("SAMNG.perspectiveMission", PlatformUI.getWorkbench().getActiveWorkbenchWindow()) ;
			} catch (WorkbenchException e) {
				e.printStackTrace();
			}											
			
			closePerspectives(sauvegarderVueAccueil);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() == null) {
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().openPage(PerspectiveAccueil.getID(), null);
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
			String perspId = perspective.getId();
			if (!perspId.equals(PerspectiveGestionDesMissions.ID) && !perspId.equals("VueMultimediaPlugin.perspectiveVueMultimedia")) {
				if (perspId.equals(PerspectiveAccueil.ID) && sauvegarderVueAccueil) {
					page.setPerspective(perspective);
				}
				
				// DO not close accueil perspective to keep its model and personal configuration ...
				if (perspId.equals(PerspectiveAccueil.ID))
				{
					hidePerspectiveButKeepItsModel(PerspectiveAccueil.ID, PerspectiveGestionDesMissions.ID);
				}
				else
				{
					page.closePerspective(perspective, false, false) ;				

				}
			}
		}
	}
	
	/**
	 * Hide a perspective but do not close it !
	 * (because closing a perspective will erase the model and the user configuration)
	 * So this method is used to put a perspective not visible and not rendered.
	 * It is used to keep the user configuration of the Accueil perspective
	 * It does nothing if no perspective is found for perspId (just warn it)
	 * @param perspId the perspective to hide
	 * @param targetPerspId the perspective to display if perspId was the active one
	 */
	public void hidePerspectiveButKeepItsModel(String perspId, String targetPerspId)
	{
		MApplication appli = PlatformUI.getWorkbench().getService(MApplication.class);
		IEclipseContext ctx = appli.getContext();
		EModelService modelService = ctx.get(EModelService.class);
		EPartService partService = ctx.get(EPartService.class);
		MUIElement pstack = modelService.find("org.eclipse.ui.ide.perspectivestack", appli);
		MPerspective activePersp = modelService.getActivePerspective(appli.getChildren().get(0));
		
		if (pstack instanceof MPerspectiveStack)
		{
			for (MPerspective p : ((MPerspectiveStack)pstack).getChildren())
			{
				if (p.getElementId().equals(perspId)) {
					
					// Put back perspective into snippets but remove the previous one if present... 
					MUIElement toBeRemoved = null;
					for (MUIElement e : appli.getSnippets())
					{
						if (e instanceof MPerspective && e.getElementId().equals(perspId))
							toBeRemoved = e;
					}
					if (toBeRemoved != null)
						appli.getSnippets().remove(toBeRemoved);
						
				    // Then can add it to save it ... 
					appli.getSnippets().add(p);
										
					// If we closed the active perspective, must display a new one.... 
					if (p == activePersp)
						partService.switchPerspective(targetPerspId);
										
					return;
				}
			}
		}
		else
		{
			throw new IllegalArgumentException("La perspective stack 'org.eclipse.ui.ide.perspectivestack' n'a pas été trouvée... Impossible de fermer les perspectives");
		}
		
		System.out.println("Warning : in hidePerspectiveButKeepItsModel(), no perspective found for ID : " + perspId);

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
