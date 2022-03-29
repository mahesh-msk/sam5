package com.faiveley.samng.principal.ihm.progbar;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.ApplicationWorkbenchWindowAdvisor;
import com.faiveley.samng.principal.ihm.actions.fichier.FichierOuvrirAction;
import com.faiveley.samng.principal.ihm.actions.fichier.VueDefautsAction;
import com.faiveley.samng.principal.sm.corrections.GestionnaireCorrection;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.Messages;
import com.faiveley.samng.principal.sm.erreurs.ParseurXMLException;
import com.faiveley.samng.principal.sm.formats.FormatSAM;
import com.faiveley.samng.principal.sm.linecommands.GestionLineCommandParameters;
import com.faiveley.samng.principal.sm.parseurs.BridageFormats;
import com.faiveley.samng.principal.sm.parseurs.ChargeurParcours;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.principal.sm.segments.TableSegments;

public class BarreProgressionChargementFichier extends Job implements PropertyChangeListener{
	private boolean ret = false;
	FichierOuvrirAction foa;
	public IProgressMonitor monitor;
	private FutureTask<Integer> task;
	protected String activePerspectiveID = "SAMNG.perspectiveMission";
	
	public String getActivePerspectiveID() {
		return activePerspectiveID;
	}

	public void setActivePerspectiveID(String activePerspectiveID) {
		this.activePerspectiveID = activePerspectiveID;
	}

	protected ArrayList<String> openPerspectivesID = new ArrayList<String>(0);
	protected boolean closeGMperspective = false;
	protected IPerspectiveDescriptor gmPerspective;
	
	public ArrayList<String> getOpenPerspectivesID() {
		return openPerspectivesID;
	}

	public void setOpenPerspectivesID(ArrayList<String> openPerspectivesID) {
		this.openPerspectivesID = openPerspectivesID;
	}

	public boolean isCloseGMperspective() {
		return closeGMperspective;
	}

	public void setCloseGMperspective(boolean closeGMperspective) {
		this.closeGMperspective = closeGMperspective;
	}

	public IPerspectiveDescriptor getGmPerspective() {
		return gmPerspective;
	}

	public void setGmPerspective(IPerspectiveDescriptor gmPerspective) {
		this.gmPerspective = gmPerspective;
	}

	public BarreProgressionChargementFichier(FichierOuvrirAction foa){
		super(foa.fileName);
		this.setUser(true);
		this.foa = foa;
		ActivatorData.getInstance().getVp().addPropertyChangeListener(this);
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		this.monitor.worked((Integer) (arg0.getNewValue()));
	}

	@Override
	protected void canceling() {		
		if (monitor.isCanceled()) {
			this.task.cancel(true);
			this.cancel();
			monitor.done();
		}
	}

	@Override 
	protected IStatus run(IProgressMonitor monitor) { 
		ActivatorData.getInstance().getVp().reset();
		this.monitor=monitor;
		monitor.setCanceled(false);
		monitor.beginTask(com.faiveley.samng.principal.ihm.progbar.Messages.getString("ProgressBar.0"), 100);

		// Starts the task
		task = new FutureTask<Integer>(new MyCallable());
		ExecutorService es = Executors.newCachedThreadPool();
		es.submit(task);

		try {
			// Get and interpret the results
			int result = task.get();		
			ActivatorData.getInstance().getVp().setValeurProgressBar(95);

			if (result == 0) {
				if (monitor.isCanceled()==false) {
					Display.getDefault().syncExec(new Runnable(){
						public void run() {
							MessageDialog.openError(foa.window.getShell(), com.faiveley.samng.principal.ihm.actions.fichier.Messages.getString("FichierOuvrirAction.7"), com.faiveley.samng.principal.ihm.actions.fichier.Messages.getString("FichierOuvrirAction.23"));							//							"SAMNG.perspectiveAccueil"); //$NON-NLS-1$
							new VueDefautsAction(foa.window, "").run();
							ActivatorData.getInstance().notifyDataListeners();	
							Activator.getDefault().release();
							ActivatorData.getInstance().getBarAdvisor().setActionsEnabled(false);
						}
					});					
				} else {
					monitor.setCanceled(false);
				}
			} else if(result == 2) {
				if (monitor.isCanceled() == false) {
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(foa.window.getShell(), com.faiveley.samng.principal.ihm.actions.fichier.Messages.getString("FichierOuvrirAction.15"), com.faiveley.samng.principal.ihm.actions.fichier.Messages.getString("FichierOuvrirAction.16"));
							new VueDefautsAction(foa.window, "").run();
							ActivatorData.getInstance().notifyDataListeners();	
							Activator.getDefault().release();
							ActivatorData.getInstance().getBarAdvisor().setActionsEnabled(false);
						}
					});
				} else {
					monitor.setCanceled(false);
				}
			} else {
				if (monitor.isCanceled() == false) {
					affichageErreurVitesseNonTrouvee();
					displayErrors();
					
					String fileName;
					if (foa.isCompressedFile) {
						fileName = foa.compressedXmlFileBaseName;
					} else {
						fileName = foa.fileName;
					}
					
					if (GestionnaireCorrection.getInstance().correctionsExistantes(fileName)) {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								ret = MessageDialog.openQuestion(foa.window.getShell(), com.faiveley.samng.principal.ihm.actions.fichier.Messages.getString("ActionFichierOuvrir.11"), com.faiveley.samng.principal.ihm.actions.fichier.Messages.getString("ActionFichierOuvrir.12"));
							}
						});

						if (ret) {
							ChargeurParcours.initializePools(fileName,true);
						} else {
							ChargeurParcours.initializePools(fileName,false);
							GestionnaireCorrection.getInstance().chargerCorrections();
							GestionnaireCorrection.getInstance().chargerSegments();

							// Do not apply the time and distance changes
							GestionnaireCorrection.getInstance().doNotApplyDistanceChanges();
							GestionnaireCorrection.getInstance().doNotApplyTempChanges();

							if (TableSegments.getInstance().areDistanceCorrections()) {
								TableSegments.getInstance().setAppliedDistanceCorrections(false);
							}
							
							if (TableSegments.getInstance().areTempCorrections()) {
								TableSegments.getInstance().setAppliedTempCorrections(false);
							}
						}
					} else {
						ChargeurParcours.initializePools(fileName, false);
						
						// Do not apply the time and distance changes
						GestionnaireCorrection.getInstance().doNotApplyDistanceChanges();
						GestionnaireCorrection.getInstance().doNotApplyTempChanges();

						if (TableSegments.getInstance().areDistanceCorrections()) {
							TableSegments.getInstance().setAppliedDistanceCorrections(false);
						}
						
						if (TableSegments.getInstance().areTempCorrections()) {
							TableSegments.getInstance().setAppliedTempCorrections(false);
						}
					}
				} else {
					monitor.setCanceled(false);
				}
				
				Display.getDefault().syncExec(new Runnable(){
					public void run() {
						try {
							// Dans le cas d'une ouverture de fichier par ligne de commande, il faut fermer toutes les perspectives, pour qu'elles s'ouvrent correctement.
							if (GestionLineCommandParameters.auMoinsUnArgument) {
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllPerspectives(true, true);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				
				Display.getDefault().syncExec(new Runnable(){
					public void run() {
						try {
							IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() ;
							
							for (String perspectiveID : openPerspectivesID) {								
								PlatformUI.getWorkbench().showPerspective(perspectiveID, 
								PlatformUI.getWorkbench().getActiveWorkbenchWindow()) ;							
							}
						
							// Set perspective active : cmd line or switch between Gestion Mission and Accueil
							ApplicationWorkbenchWindowAdvisor.afficherPerspectiveApresOuvertureFichier();
						
							// If the Gestion Mission perspective has to be closed
							if (closeGMperspective) {
								page.closePerspective(gmPerspective, false, false);
							}

							// If the active perspective wasn't Gestion Mission, active the one before
							if (!activePerspectiveID.equals("SAMNG.perspectiveMission")) {
								PlatformUI.getWorkbench().showPerspective(activePerspectiveID, 
								PlatformUI.getWorkbench().getActiveWorkbenchWindow());	
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		} catch(ConcurrentModificationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		es.shutdown();
		ActivatorData.getInstance().getVp().setValeurProgressBar(100);
		monitor.done(); 
		ActivatorData.getInstance().getVp().removePropertyChangeListener(this);
		
		return Status.OK_STATUS; 
	}

	public void displayErrors() {
		String curLine;
		int nbErrors = 0;
		
		try {
			FileInputStream fileInputStream = new FileInputStream(RepertoiresAdresses.logs_parser_log_TXT);
			BufferedReader inFile = new BufferedReader(new InputStreamReader(fileInputStream));
			Set<String> lineMessages = new LinkedHashSet<String>();
			
			while ((curLine = inFile.readLine()) != null) {
				if (!lineMessages.contains(curLine)) {
					lineMessages.add(curLine);
					
					// Issue 727
					if (!curLine.equals("")) {
						nbErrors++;
					}
				}
			}
			
			if (nbErrors > 0) {
				Display.getDefault().syncExec(new Runnable(){
					public void run() {
						MessageDialog.openWarning(PlatformUI.getWorkbench().getDisplay().getActiveShell(), com.faiveley.samng.principal.ihm.actions.fichier.Messages.getString("ErreursTitre"), com.faiveley.samng.principal.ihm.actions.fichier.Messages.getString("ErreursMessage"));
					}
				});
			}
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void affichageErreurVitesseNonTrouvee(){
		if ((!GestionnairePool.getInstance().isRepereVitesseRenseignee())&&(!BridageFormats.getInstance().getFormatFichierOuvert(foa.fileName).equals(FormatSAM.JRU))) {
			Display.getDefault().syncExec(new Runnable(){
				public void run() {
					MessageDialog.openWarning(PlatformUI.getWorkbench().getDisplay().getActiveShell(),com.faiveley.samng.principal.sm.data.enregistrement.Messages.getString("GestionnairePool.2"), com.faiveley.samng.principal.sm.data.enregistrement.Messages.getString("GestionnairePool.0") + " " + Messages.getString("GestionnairePool.1"));
				}
			});
		}
	}

	public class MyCallable implements Callable<Integer> {
		public Integer call() throws java.io.IOException {
			try {
				ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().enregistrerVBV();
				Activator.getDefault().release();
				ActivatorData.getInstance().getBarAdvisor().setActionsEnabled(false);
				ChargeurParcours.loadBinaryFile(BarreProgressionChargementFichier.this.foa.fileName, BarreProgressionChargementFichier.this.foa.deb, BarreProgressionChargementFichier.this.foa.fin, BarreProgressionChargementFichier.this.foa.lancerExplorer);
			} catch (ParseurXMLException ex){
				// Erreurs relatives � l'ouverture du fichier xml associ�
				// L'exception lev� est g�n�rique mais le texte pr�cis de l'erreur est dans la vue des d�fauts
				
				return 2;
			} catch (OutOfMemoryError e) {
				System.err.println(com.faiveley.samng.principal.ihm.actions.fichier.Messages.getString("ActionFichierOuvrir.8"));
				
				return 0;
			} catch (Throwable t) {
				t.printStackTrace();
				Activator.getDefault().release();
				
				return 0;
			}

			return 1;
		}
	}
}