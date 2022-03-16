package com.faiveley.samng.vueexplorateur.ihm.vue.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.actions.fichier.DecompressionService;
import com.faiveley.samng.principal.ihm.actions.fichier.UnsupportedJourneyFileFormat;
import com.faiveley.samng.principal.ihm.actions.fichier.VueDefautsAction;
import com.faiveley.samng.principal.sm.data.compression.DecompressedFile;
import com.faiveley.samng.principal.sm.erreurs.AExceptionSamNG;
import com.faiveley.samng.principal.sm.erreurs.ParseurXMLException;
import com.faiveley.samng.principal.sm.formats.DistinctionExtension;
import com.faiveley.samng.principal.sm.formats.FormatSAM;
import com.faiveley.samng.principal.sm.missions.ParseurMissions;
import com.faiveley.samng.principal.sm.missions.explorersingletons.activator.ActivatorDataExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurXML.ParseurXML_NG_UK_ATESS_Explorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurParcoursAtessExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurParcoursJRUExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurParcoursSamngExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurParcoursTomDISExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurParcoursTomHSBCExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurParcoursTomUkExplorer;
import com.faiveley.samng.principal.sm.parseurs.BridageFormats;
import com.faiveley.samng.principal.sm.parseurs.ParseurParcoursBinaire;
import com.faiveley.samng.principal.sm.parseurs.TypeParseur;
import com.faiveley.samng.principal.sm.parseurs.parseurXML.ParseurXML1;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.ParseurParcoursAtess;
import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ParseurParcoursTomUk;
import com.faiveley.samng.principal.sm.parseurs.parseursTomNg.ParseurParcoursSamng;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.vueexplorateur.ihm.vue.VueExplorateurFichiersDeParcours;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeFile;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeFolder;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeObject;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeParent;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeRepository;

public class ExplorerActionProgressionBar extends Job implements PropertyChangeListener {

	public ISelection iSelection;
	public IProgressMonitor monitor;
//	private int nbFilesToExplore=0;

	public ExplorerActionProgressionBar(ISelection iSelection) {
		super("Action Explorer"); //$NON-NLS-1$
		this.setUser(true);	
		this.iSelection=iSelection;
		ActivatorData.getInstance().getVpExportExplorer().addPropertyChangeListener(this);
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		this.monitor.worked((Integer) (arg0.getNewValue()));
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			//init
			this.monitor = monitor;
			ActivatorData.getInstance().getVpExportExplorer().setValeurProgressBar(0);
			ActivatorData.getInstance().getVpExportExplorer().setNbFiles(0);
			ActivatorData.getInstance().getVpExportExplorer().setIndiceFile(0);
			
			ActivatorDataExplorer.setInstance(ActivatorData.getInstance().clone());
//			ActivationExplorer.getInstance().setActif(true);		
			ActivatorData.getInstance().getVpExportExplorer().reset();			
//			monitor.setCanceled(false);

			//exloration
			ActivatorData.getInstance().getVpExportExplorer().setValeurProgressBar(0);
			explorer(iSelection,monitor);
			ActivatorData.getInstance().getVpExportExplorer().setValeurProgressBar(100);
			monitor.done();

			ActivatorData.getInstance().getVpExportExplorer().removePropertyChangeListener(this);

			if (!monitor.isCanceled()) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						if (Display.getDefault().getActiveShell()!=null) {
							MessageBox msgBox2 = new MessageBox(Display.getDefault().getActiveShell(),SWT.ICON_INFORMATION);
							msgBox2.setText(Messages.getString("ExplorerActionProgressionBar_1"));
							msgBox2.setMessage(Messages.getString("ExplorerActionProgressionBar_2"));
							msgBox2.open();
							VueExplorateurFichiersDeParcours.refreshViewExplorateur(true);
						}					
					}
				});
			}
			try {
				Activator.getDefault().release();
				//GestionnaireProfil.getInstance().resetProfil();
				// Reset du profil actuel pour pouvoir importer des profiles
				RepertoiresAdresses.setNom_profil_actuel("");
			} catch (Exception e) {
				e.printStackTrace();
			}
//			try {
//				ActivatorData.getInstance().notifyDataListeners();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
//			ActivationExplorer.getInstance().setActif(false);
		}			
		return Status.OK_STATUS;
	}
	
	@Override
	protected void canceling() {
		System.out.println("canceling");
		if (monitor.isCanceled()) {
			System.out.println("iscanceled");
//			ActivatorData.getInstance().getVp().removePropertyChangeListener(this);
			this.cancel();
			
//			try {
//				Activator.getDefault().release();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			try {
				VueExplorateurFichiersDeParcours.refreshViewExplorateur(true);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
//			try {
//				this.finalize();
//			} catch (Throwable e) {
//				e.printStackTrace();
//			}
			monitor.done();
		}
	}

	public void getNbFiles(Object ob){
		TreeParent tp=(TreeParent) ob;
		File f=new File(tp.getAbsoluteName());
		getNbFilesRecursif(f);
	}

	public void getNbFilesRecursif(File f){		
		File[] files=f.listFiles();
		if (files==null) {
			if (BridageFormats.getInstance().isextensionValide(f.getName())) {
				ActivatorData.getInstance().getVpExportExplorer().incrementNbFiles();
			}			
		}else{			
			for (File file : files) {
				getNbFilesRecursif(file);
			}
		}	
	}

	public void explorer(ISelection selection, IProgressMonitor monitor) throws AExceptionSamNG {
		TreeSelection treeSelection=((TreeSelection) selection);		
		Object ob=treeSelection.getFirstElement();
		getNbFiles(ob);//compter le nombre de fichiers � explorer
		monitor.beginTask(Messages.getString("ExplorerActionProgressionBar_1"), 100); //$NON-NLS-1$
		System.out.println("nbFilesToExplore = "+ActivatorData.getInstance().getVpExportExplorer().getNbFiles()); //$NON-NLS-1$
		explorerDossierRecursif(ob,monitor);		
	}

	public void explorerDossierRecursif(Object ob, IProgressMonitor monitor) throws AExceptionSamNG {
		if (ob instanceof TreeFile) {
			//si c'est un fichier on lance l'exploration
			explorerFile(((TreeFile) ob).getAbsoluteName(),monitor);
		}else{
			TreeObject[] to=((TreeParent) ob).getChildren();
			if (to!=null && to.length>0) {
				int size=to.length;
				for (int i = 0; i < size; i++) {
					if (to[i] instanceof TreeFolder || to[i] instanceof TreeRepository) {
						//si c'est un dossier on relance un algorithme r�cursif
						explorerDossierRecursif(to[i],monitor);
					}else if(to[i] instanceof TreeFile){
						explorerFile(((TreeFile) to[i]).getAbsoluteName(),monitor);
					}
				}
			}
		}
	}

	private String recupererNomFichierXML(FormatSAM format, File f){
		String nomFichierXml = null;
		try {
			nomFichierXml = null;
			if (format==FormatSAM.TOMNG) {
				nomFichierXml = ParseurParcoursSamng.getInstance().getNomFichierXml(f.getAbsolutePath());
			}else if (format==FormatSAM.ATESS) {
				File fichierXmlSource = f;
				nomFichierXml = ParseurParcoursAtess.getInstance().getNomFichierXml(fichierXmlSource.getAbsolutePath());
			}
			else if (format==FormatSAM.TOM4) {
				nomFichierXml = ParseurParcoursTomUk.getInstance().getNomFichierXml(f.getAbsolutePath());
			}
			else if(format==FormatSAM.JRU){
				String version = format.getFjru().getVersion();
				nomFichierXml = "JRU"+version+".xml"; //$NON-NLS-1$ //$NON-NLS-2$
			}else{
				System.out.println("Format non reconnu"); //$NON-NLS-1$
			}

			if (nomFichierXml!=null) {
				int cesure=nomFichierXml.lastIndexOf(".xml"); //$NON-NLS-1$
				if (cesure!=-1) {
					nomFichierXml=nomFichierXml.substring(0, cesure+4);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return nomFichierXml;
	}

	public void explorerFile(String nomFichier, IProgressMonitor monitor) throws AExceptionSamNG{
		if (!monitor.isCanceled()) {

			ParseurParcoursBinaire ppb=TypeParseur.getInstance().getParser();//on stocke le type de parseur pour le r�cup�rer apr�s
			//			ParseurXML_NG_UK_ATESS_Explorer parseurXML1=(ParseurXML_NG_UK_ATESS_Explorer) ParseurXML_NG_UK_ATESS_Explorer.getInstance();
			//			ParseurXML_NG_UK_ATESS_Explorer.getInstance(nomFichier);

			DistinctionExtension.setEnableFormats();
			BridageFormats.getInstance().initialiseValides();
			BridageFormats.setGestionConflitExtension(true);

			FormatSAM format=BridageFormats.getFormat(nomFichier);
			
			DecompressedFile decompressedFile = null;
			if (format.equals(FormatSAM.COMPRESSED)) {
				// On decompresse dans le dossier temporaire le fichier compresse
				try {
					decompressedFile = DecompressionService.getInstance().decompressFile(nomFichier, nomFichier.substring(nomFichier.lastIndexOf('\\') + 1), false);
					nomFichier = decompressedFile.getDecompressedFileName();
					format = BridageFormats.getFormat(nomFichier);
				} catch (IOException e) {
					if (e instanceof UnsupportedJourneyFileFormat) {
						Display.getDefault().syncExec(new Runnable(){
							public void run() {
								MessageDialog.openError(Display.getDefault().getActiveShell(), com.faiveley.samng.principal.ihm.actions.fichier.Messages.getString("FichierOuvrirAction.26"), 
										com.faiveley.samng.principal.ihm.actions.fichier.Messages.getString("FichierOuvrirAction.27"));
							}
						});
						
					} else {
						Display.getDefault().syncExec(new Runnable(){
							public void run() {
								MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.getString("ExplorerActionProgressionBar_1"), 
										Messages.getString("ExplorerActionProgressionBar_3"));
							}
						});
					}
					monitor.setCanceled(true);
					return;
				}
			} else if (format.equals(FormatSAM.MULTIMEDIA)) {
				return;
			}
			
			BridageFormats.getInstance().setFormatFichierOuvert(BridageFormats.getInstance().getFormatFichierOuvert(nomFichier));
			File f=new File(nomFichier);
			//on r�cup�re d'abord le nom du fichier xml du fichier de parcours
			String nomFichierXml = recupererNomFichierXML(format,f);
			if(nomFichierXml == null ){
			    // To avoid the message that the exploration is done and now the user can open part of file 
				monitor.setCanceled(true);
				Display.getDefault().syncExec(new Runnable(){
					public void run() {
						MessageDialog.openError(Activator.getDefault().getWorkbench().getDisplay().getActiveShell(), com.faiveley.samng.principal.ihm.actions.fichier.Messages.getString("ErreursTitre"), 
								com.faiveley.samng.principal.ihm.actions.fichier.Messages.getString("FichierOuvrirAction.23"));
					}
				});
				return;
			}
			nomFichierXml = nomFichierXml.trim();
			ParseurParcoursBinaire parser = null;
			try {
				if (format==FormatSAM.TOMNG) {
					parser = ParseurParcoursSamngExplorer.getInstance();
					parser.setMonitor(monitor);
					TypeParseur.getInstance().setParser(parser);
					parser.parseRessource(f.getAbsolutePath(),true,0,-1);
				}else if (format==FormatSAM.TOM4) {
					//on r�cup�re le mod�le d'enregistreur pour utiliser le bon parseur
					ParseurXML1 parseurXml = ParseurXML_NG_UK_ATESS_Explorer.getInstance();

					String nomxml=RepertoiresAdresses.xml + File.separator + nomFichierXml; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					parseurXml.parseRessource(nomxml,true,0,-1);
					String modele = parseurXml.chargerType();
					if(modele.equals("TOM_UK")){				 //$NON-NLS-1$
						parser = ParseurParcoursTomUkExplorer.getInstance();
						parser.setMonitor(monitor);
						TypeParseur.getInstance().setParser(parser);
						parser.parseRessource(f.getAbsolutePath(),true,0,-1);
					}
					else if(modele.equals("TOM_DIS")){ //$NON-NLS-1$
						parser = ParseurParcoursTomDISExplorer.getInstance();
						parser.setMonitor(monitor);
						TypeParseur.getInstance().setParser(parser);
						parser.parseRessource(f.getAbsolutePath(),true,0,-1);
					}
					else if(modele.equals("TOM_HSBC")){ //$NON-NLS-1$
						parser = ParseurParcoursTomHSBCExplorer.getInstance();
						parser.setMonitor(monitor);
						TypeParseur.getInstance().setParser(parser);
						parser.parseRessource(f.getAbsolutePath(),true,0,-1);
					}
				}else if (format==FormatSAM.ATESS) {
					parser = ParseurParcoursAtessExplorer.getInstance();
					parser.setMonitor(monitor);
					TypeParseur.getInstance().setParser(parser);
					parser.parseRessource(f.getAbsolutePath(),true,0,-1);
				}else if(format==FormatSAM.JRU){
					parser = ParseurParcoursJRUExplorer.getInstance();
					parser.setMonitor(monitor);
					TypeParseur.getInstance().setParser(parser);
					parser.parseRessource(f.getAbsolutePath(),true,0,-1);
				}
			} catch (final ParseurXMLException e1) {
				// e1.printStackTrace();
				// To avoid the message that the exploration is done and now the user can open part of file 
				monitor.setCanceled(true);
				Display.getDefault().syncExec(new Runnable(){
					public void run() {
						MessageDialog.openError(Activator.getDefault().getWorkbench().getDisplay().getActiveShell(), 
								com.faiveley.samng.principal.ihm.actions.fichier.Messages.getString("FichierOuvrirAction.15"), 
								com.faiveley.samng.principal.ihm.actions.fichier.Messages.getString("FichierOuvrirAction.16") +
								"\n" + e1.getMessage());
						new VueDefautsAction(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow(), "").run();
					}
				});
				return;
			} catch (AExceptionSamNG e1) {
				e1.printStackTrace();
			}catch (NullPointerException e1) {
				e1.printStackTrace();
			} catch (Exception e1){
				e1.printStackTrace();
			}

			if (!monitor.isCanceled()) 
				ParseurMissions.lancerParseurMissions(f,this);

			TypeParseur.getInstance().setParser(ppb);//on remet le type parseur pour le fichier ouvert						
		}

		ActivatorData.getInstance().getVpExportExplorer().incrementIndiceFile();
		//incr�menter ProgressBar
		ActivatorData.getInstance().getVpExportExplorer().setValeurProgressBar(
				100*ActivatorData.getInstance().getVpExportExplorer().getIndiceFile()/
				ActivatorData.getInstance().getVpExportExplorer().getNbFiles());
//		System.out.println("val "+ActivatorData.getInstance().getVpExportExplorer().getValeurProgressBar());		 //$NON-NLS-1$
	}
	
	
}