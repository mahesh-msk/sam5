package com.faiveley.samng.principal.ihm.actions.fichier;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.principal.ihm.progbar.BarreProgressionChargementFichier;
import com.faiveley.samng.principal.sm.data.compression.DecompressedFile;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosFichierSamNg;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.formats.DistinctionExtension;
import com.faiveley.samng.principal.sm.formats.FormatSAM;
import com.faiveley.samng.principal.sm.linecommands.GestionLineCommandParameters;
import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.parseurs.BridageFormats;
import com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

public class FichierOuvrirAction extends Action {
	public final IWorkbenchWindow window;

	public BarreProgressionChargementFichier bp;
	public String fileName;
	public String fileNameLigneCommande = "";
	public String compressedXmlFileBaseName;
	public boolean isCompressedFile=false;
	public int deb;
	public int fin;
	public boolean lancerExplorer=false;
	private Properties props = new Properties();
	public DecompressedFile decompressedFile;
	
	public FichierOuvrirAction(final IWorkbenchWindow window) {
		this.window = window;
		this.deb = 0;
		this.fin = -1;
		
		loadProperties();
	}
	
	public FichierOuvrirAction(final IWorkbenchWindow window, String filename2, int deb, int fin, boolean lancerExplorer, String initDateS) {
		this.window = window;
		this.deb = deb;
		this.fin = fin;
		
		ConstantesParcoursBinaire.offsetDebut = deb;
		ConstantesParcoursBinaire.offsetFin = fin;
		ConstantesParcoursBinaire.initDateS = initDateS;
		
		this.lancerExplorer = lancerExplorer;
		setId(ICommandIds.CMD_OPEN_MESSAGE);
		setActionDefinitionId(ICommandIds.CMD_OPEN_MESSAGE);
		setImageDescriptor(com.faiveley.samng.principal.ihm.Activator.getImageDescriptor("/icons/menu/appli_ouvrir.png"));
		this.fileNameLigneCommande = filename2;
		
		loadProperties();
	}
	
	public FichierOuvrirAction(final IWorkbenchWindow window, final String text) {
		super(text);
		this.window = window;
		setId(ICommandIds.CMD_OPEN_MESSAGE);
		setActionDefinitionId(ICommandIds.CMD_OPEN_MESSAGE);
		setImageDescriptor(com.faiveley.samng.principal.ihm.Activator.getImageDescriptor("/icons/menu/appli_ouvrir.png"));
		deb = 0;
		fin = -1;
	}
	
	private void loadProperties() {
		try {
			FileInputStream inStream;
			String cheminFichiermissions_PROPERTIES = RepertoiresAdresses.missions_PROPERTIES;
			inStream = new FileInputStream(new File(cheminFichiermissions_PROPERTIES));
			props.load(inStream);		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Runs the action set for Open a File
	 */
	public final void run() {
		// Sauvegarde de l'espace de travail si un fichier est deja ouvert
		ActivationExplorer.getInstance().setOuvertureFichierPartielle(false);
		GC gc = new GC(Display.getDefault());
		ActivatorData.getInstance().setTailleMoyenneCaractere( gc.getFontMetrics().getAverageCharWidth());
		ActivatorData.getInstance().setEspaceMoyenCaractere(gc.getFontMetrics().getLeading());
		gc.dispose();
		
		BridageFormats.setGestionConflitExtension(BridageFormats.isGestionConflitExtensionInitial());
		FormatSAM.TOM4.setEnable(DistinctionExtension.isTOM4init());
		FormatSAM.ATESS.setEnable(DistinctionExtension.isATESSinit());
		
		// Creates a file dialog to open the binary files
		FileDialog dialog = new FileDialog(this.window.getShell(), SWT.CANCEL);
		
		// Set the posible extensions of the binary files : ".tbf" and ".cbf"
		FormatSAM lesFormatsSAM[] = FormatSAM.values();
		List <String> extensionsDispo=new ArrayList<String>(lesFormatsSAM.length); 
		
		for (int i = 0; i < lesFormatsSAM.length; i++) {
			if (lesFormatsSAM[i].isEnable()) {
				extensionsDispo = DistinctionExtension.supprimerExtensionsDoublons(extensionsDispo, lesFormatsSAM[i].getExtensions());
			}
		}
		
		String formats=DistinctionExtension.toExtensionFormalisees(extensionsDispo);

		if (formats.equalsIgnoreCase("")) {
			MessageBox msgBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_WARNING | SWT.OK );
			msgBox.setMessage(Messages.getString("BridageFormat.0"));
			msgBox.open();
			
			return;
		}
		
		String filters = " (" + formats.replaceAll(";", ",") + ")";
		
		//DR28_CL36 
		dialog.setFilterPath(RepertoiresAdresses.RepertoireTravail);
		dialog.setFilterExtensions(new String[] {formats, "*.*"});

		dialog.setFilterNames(new String[] {
		Messages.getString("ActionFichierOuvrir.3") + filters, Messages.getString("ActionFichierOuvrir.5") + " (*.*)" });
		
		if (!ActivatorData.getInstance().isFileEmpty()) {
			if (ActivatorData.getInstance().isMultimediaFileAlone()) {
				FichierFermerService.getInstance().closeMultimediaFile();
			} else {
				FichierFermerService.getInstance().closeFile();
			}
		}
		
		if (fileNameLigneCommande == null || fileNameLigneCommande.equals("")) {
			this.fileName = dialog.open();
		} else {
			this.fileName = fileNameLigneCommande;
			fileNameLigneCommande = "";
		}
		
		if (!lancerExplorer) {
			ActivationExplorer.getInstance().setOuvertureFichierPartielle(true);
		}
		
		DecompressionService decompressionService = DecompressionService.getInstance();
		if (decompressionService.isCompressedFile(fileName)) {
			File fileParent = new File(fileName).getParentFile();
			isCompressedFile = true;
			try {
				this.decompressedFile = decompressionService.decompressFile(fileName, fileName.substring(fileName.lastIndexOf('\\') + 1), true);
				fileName = decompressedFile.getDecompressedFileName();
				compressedXmlFileBaseName = new File(fileParent.getAbsolutePath(), decompressedFile.getInnerFileName()).getAbsolutePath();
				ActivatorData.getInstance().setAbsoluteFileName(fileName);
				ActivatorData.getInstance().setCompressedFile(isCompressedFile);
				ActivatorData.getInstance().setDecompressedFile(decompressedFile);
			} catch(IOException ioex) {
				if (ioex instanceof UnsupportedJourneyFileFormat) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.getString("FichierOuvrirAction.26"), 
							Messages.getString("FichierOuvrirAction.27"));
				} else {
					MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.getString("FichierOuvrirAction.24"), 
						Messages.getString("FichierOuvrirAction.25"));
				}
				return;
			}
		}
		
		traiterOuvertureFichier();
	}
	
	// M�thode permettant d'acqu�rir du fichier 
	// "AppData\Roaming\Faiveley Transport\SAM X.X.X.bXX\ressources\bridage\missions.properties"
	// le param�tre max_open_file_size. Ce param�tre permet de donner une limite, en octet,
	// pour laquelle un message d'avertissement sur l'ouverture d'un fichier trop gros. 
	public int getMaxOpenFileSize() {
		String max_open_file_size = "2500000";
		int max_open_file_size_int = 2500000;
		
		try {
			max_open_file_size = (String) props.get("max_open_file_size");
			
			if (max_open_file_size != null) {
				max_open_file_size_int = Integer.valueOf(max_open_file_size);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return max_open_file_size_int;
	}
	
	public void traiterOuvertureFichier() {
		FormatSAM formatFichierOuvert = null;
		
		if (this.fileName != null) {
			formatFichierOuvert = BridageFormats.getFormat(this.fileName);
			BridageFormats.getInstance().setFormatFichierOuvert(formatFichierOuvert);
		}
				
		// Si le nom choisi est null
		if (this.fileName == null) {
			return;
		}
		
		String nomMajuscule = this.fileName.toUpperCase();
		
		// Si le fichier choisi n'est pas dans la liste des extensions possibles
		if (!BridageFormats.getInstance().isextensionValide(nomMajuscule)) {
			MessageBox msgBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_ERROR | SWT.OK);
			msgBox.setText("");
			msgBox.setMessage(Messages.getString("ActionFichierOuvrir.6"));
			msgBox.open();
			
			return;
		}

		File f = new File(this.fileName);
		long tailleFichier ;
		
		// Test de la m�moire
		if ((deb == 0) && (fin == -1)) {
			tailleFichier = f.length();
		} else {
			tailleFichier = ConstantesParcoursBinaire.offsetFin - ConstantesParcoursBinaire.offsetDebut;
		}
		
		int limitMem = 0;
		int maxOpenFileSize = getMaxOpenFileSize();
		
		if (tailleFichier > maxOpenFileSize && formatFichierOuvert != FormatSAM.MULTIMEDIA) {
			MessageBox msgBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			msgBox.setText(Messages.getString("FichierOuvrirAction.8"));
			
			if ((deb == 0) && (fin == -1)) {
				msgBox.setMessage(Messages.getString("FichierOuvrirAction.9"));
			} else {
				msgBox.setMessage(Messages.getString("FichierOuvrirAction.10"));
			}
			
			limitMem = msgBox.open();
		}
					
		// Si l'utilisateur d�cide de ne pas ouvrir le fichier suite � l'avertissement m�moire
		if (limitMem == SWT.NO) {
			return;
		}
		
		if (ActivatorData.getInstance().getGestionnaireMarqueurs().isModifications()) {
			MessageBox msgBox = new MessageBox(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			msgBox.setText(Messages.getString("GestionnaireVueListeBase.11"));
			msgBox.setMessage(Messages.getString("GestionnaireVueListeBase.12"));
			int ret = msgBox.open();
			
			if (ret == SWT.YES) {
				SauverRemarquesUtilisateurAction srua = new SauverRemarquesUtilisateurAction(Messages.getString("GestionnaireVueListeBase.11"));
				srua.run();
			}
		}
		
		// Updates the current window
		this.window.getShell().update();
				
		RepertoiresAdresses.RepertoireTravail = f.getParent();
		
		if (formatFichierOuvert != null) {
			if (formatFichierOuvert.equals(FormatSAM.MULTIMEDIA)) {
				FichierMultimediaOuvrirAction.ouvrirFichierMultimedia(f);
				ActivatorData.getInstance().getBarAdvisor().manageMultiMediaActions();
			} else {				
				bp = new BarreProgressionChargementFichier(this);
				fermerVues();
				bp.schedule();
			}
			
			ActivatorData.getInstance().notifyDataListeners();
		}
	}

	public void fermerVues() {
		ArrayList<String> openPerspectivesID = new ArrayList<String>(0);
		boolean closeGMperspective = false;
		IPerspectiveDescriptor gmPerspective = null;
		String activePerspectiveID = null;
		boolean nePasSauvegarderVueAccueil = false ;
				
		// Les param�tres de la ligne de commande doivent �tre pris en compte qu'une fois
		if (!GestionLineCommandParameters.isOneTime()) {
			GestionLineCommandParameters.reset() ;
		}
		
		GestionLineCommandParameters.setOneTime(false) ;
		
		try {									
			try{				
				((InfosFichierSamNg) FabriqueParcours.getInstance().getParcours().getInfo()).setNomFichierParcoursBinaire(null);
			} catch (Exception ex) { // Pas de fichier ouvert
				nePasSauvegarderVueAccueil = true ;								
			}
										
			// Le but est de fermer toutes les perspectives pour que toutes les vues
			// soient aussi ferm�es, sauf la vue exploreur. Du coup, la perspective
			// Gestion Mission est ouverte (contient la vue explorateur) et toutes 
			// les autres sont ferm�es.
			
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() ;
			IPerspectiveDescriptor[] openPerspectives = page.getOpenPerspectives() ;
				
			// Get the open perspectives
			for (IPerspectiveDescriptor perspectives : openPerspectives){
				openPerspectivesID.add(perspectives.getId()) ;
			}
			
			// Get the active perspective
			activePerspectiveID = page.getPerspective().getId() ;					
								
			// Launch Gestion Mission perspective to save explorer view
			try {
				PlatformUI.getWorkbench().showPerspective("SAMNG.perspectiveMission", Activator.getDefault().getWorkbench().getActiveWorkbenchWindow()) ;
			} catch (WorkbenchException e) {
				e.printStackTrace();
			}
			
			// Get the open perspectives with Gestion Mission perspective (sure)
			IPerspectiveDescriptor[] perspectives = page.getOpenPerspectives() ;
			
			// Gestion Mission perspective added or it was already
			// If we had it, we have to close it
			if (perspectives.length != openPerspectives.length) {
				closeGMperspective = true;
			} else {
				closeGMperspective = false;
			}
				
			// La perspective "Gestion des missions" peut contenir d'autres vues que la vue exploreur
			// -> Il faut les fermer sinon elles ne pouront �tre mises � jour
			Activator.getDefault().hideAllViews();
				
			// Close all perspectives but Gestion Mission
			for (IPerspectiveDescriptor perspective : perspectives) {								 
				if (!perspective.getId().equals("SAMNG.perspectiveMission")) {
					if (perspective.getId().equals("SAMNG.perspectiveAccueil") && !nePasSauvegarderVueAccueil) {
						page.setPerspective(perspective) ;										
						page.savePerspective() ;
					}
					
					page.closePerspective(perspective, false, false);
				}
				else{
					gmPerspective = perspective;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ActivatorData.getInstance().getBarAdvisor().setActionsEnabled(true);
		
		bp.setActivePerspectiveID(activePerspectiveID);
		bp.setCloseGMperspective(closeGMperspective);
		bp.setGmPerspective(gmPerspective);
		bp.setOpenPerspectivesID(openPerspectivesID);
	}

}