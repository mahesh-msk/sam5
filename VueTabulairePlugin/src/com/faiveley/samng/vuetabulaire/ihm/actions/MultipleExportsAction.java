package com.faiveley.samng.vuetabulaire.ihm.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.principal.ihm.actions.fichier.FichierFermerAction;
import com.faiveley.samng.principal.ihm.vues.Row;
import com.faiveley.samng.principal.ihm.vues.VueTabulaireContentProvider;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.ListMessages;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosFichierSamNg;
import com.faiveley.samng.principal.sm.erreurs.AExceptionSamNG;
import com.faiveley.samng.principal.sm.erreurs.BadFileLengthException;
import com.faiveley.samng.principal.sm.erreurs.ParseurXMLException;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.GestionnaireFiltresTabulaire;
import com.faiveley.samng.principal.sm.filtres.gestionnaires.AGestionnaireFiltres;
import com.faiveley.samng.principal.sm.parseurs.BridageFormats;
import com.faiveley.samng.principal.sm.parseurs.ChargeurParcours;
import com.faiveley.samng.principal.sm.parseurs.ParseurExport;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.vuetabulaire.ihm.ActivatorVueTabulaire;
import com.faiveley.samng.vuetabulaire.ihm.actions.progbar.BarreProgressionExportMultiple;
import com.faiveley.samng.vuetabulaire.ihm.dialogs.ExporterVueTabulaireDialog;

/**
 * 
 * @author Cosmin Udroiu
 * 
 */
public class MultipleExportsAction extends Action {
	public IWorkbenchWindow window;

	private List<String> cheminsFichier = new ArrayList<String>(0);
	private String selFilterName;
	private String xmlFile;

	public MultipleExportsAction(final IWorkbenchWindow window,
			final String text) {
		super(text);
		this.window = window;

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_MULTIPLE_EXPORT_FILE);
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_MULTIPLE_EXPORT_FILE);

	}

	/**
	 * M�thode d'export de la selection de la vue tabulaire: fait appel � la
	 * classe ParseurExport du framework
	 * 
	 * @param cheminFichier
	 * @return
	 * @throws Exception 
	 */
	public void exporterFichier(final String cheminFichier,
			final String selFilterName, IProgressMonitor monitor) throws Exception {

		// Thread executionThread = new Thread(new Runnable() {
		// public void run() {

		if(FabriqueParcours.getInstance().getParcours()!=null && FabriqueParcours.getInstance().getParcours().getData().getEnregistrement()!=null){
			FabriqueParcours.getInstance().getParcours().getData().getEnregistrement().setMessages(new ListMessages());
			FabriqueParcours.getInstance().getParcours().getData().getEnregistrement().setBadMessages(new ListMessages());
			FabriqueParcours.getInstance().getParcours().getData().getEnregistrement().setLongueurEnregistrement(0);
		}
		try{
			// load the file
			ChargeurParcours.loadBinaryFile(cheminFichier,0,-1,false);
		}
		catch(BadFileLengthException ex){
			throw ex;
		}
		xmlFile = ((InfosFichierSamNg) GestionnairePool.getInstance().getXMLParser().getInfosFichier()).getNomFichierXml();

		if (((InfosFichierSamNg) GestionnairePool.getInstance().getXMLParser()
				.getInfosFichier()).getNomFichierXml().equals(xmlFile)) {
			ChargeurParcours.initializePools(cheminFichier, false);

			VueTabulaireContentProvider contentProvider = new VueTabulaireContentProvider(ActivatorVueTabulaire.getDefault().getConfigurationMng(), ActivatorVueTabulaire.getDefault());
			// set the filter name to the content provider
			contentProvider.setFilter(selFilterName);
			// do not ignore columns that are not displayable
			contentProvider.setAddNonDisplayableColumns(true);
			contentProvider.initializeColumns();
			contentProvider.loadContent(monitor);
			List<String> columnNames = contentProvider.getColumnLabels();
			Object[] rows = contentProvider.getElements(null);

			if (rows != null && rows.length > 0) {
				List<Row> selectionLignes = new ArrayList<Row>();
				for (int i = 0; i < rows.length; i++) {
					selectionLignes.add((Row) rows[i]);
				}

				String xptFileName = cheminFichier + ".tsv"; //$NON-NLS-1$
				new File(xptFileName).delete();

				ParseurExport parseur = new ParseurExport();
				parseur.parseRessource(xptFileName,false,0,-1);
				parseur.exporterLignes(xptFileName, columnNames,
						selectionLignes);

			}

		}

		// }
		// });
		// executionThread.start();
	}

	/**
	 * M�thode d'ouverture de la fenetre de dialogue d'export de selection
	 */
	public final void run() {

		if (this.window == null)
			this.window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

//		String fileOpen="";
//		try {
//			InfosFichierSamNg infos = (InfosFichierSamNg) FabriqueParcours.getInstance().getParcours().getInfo();
//			fileOpen = infos.getNomFichierParcoursBinaire();
//		} catch (Exception e2) {
//			e2.printStackTrace();
//		}
		
		int ret = 0;
		MessageBox msgBox;
		
		// Si un fichier est ouvert
		if(ActivatorData.getInstance().getVueData().getDataTable()!=null){		
			msgBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL );
			msgBox.setText(""); //$NON-NLS-1$
			msgBox.setMessage(com.faiveley.samng.principal.ihm.actions.dialogs.Messages.getString("SearchInFileAction.1")); //$NON-NLS-1$
	
			if (msgBox.open() == SWT.OK) {
			
				FichierFermerAction ffa = null ;
				ffa = new FichierFermerAction(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
						"");
				ffa.run();
			} else {
				return ;
			}
		}

		DirectoryDialog repertoire = new DirectoryDialog(this.window.getShell(), SWT.NONE|SWT.MULTI);
		repertoire.setText(com.faiveley.samng.vuetabulaire.ihm.actions.Messages.getString("MultipleExportsAction.5"));
//		int lastIndex=fileOpen.lastIndexOf("\\");
//		repertoire.setFilterPath(fileOpen.substring(0,lastIndex));
		//DR28_CL36 
		repertoire.setFilterPath(RepertoiresAdresses.RepertoireTravail);

		String repertoireFichiers = repertoire.open();
		if (repertoireFichiers == null)
			return;

		cheminsFichier.clear();
		
		fillCheminsFichier(repertoireFichiers) ;

		if (cheminsFichier.size() != 0) {			
			
			// #880 :
			// L'�volution consiste � impl�menter le traitement suivant :
			// 1) prendre le 1er fichier par ordre alphab�tique du r�pertoire s�lectionn�
			// 2) proposer � l'utilisateur les filtres li�s � ce fichier (� son profil XML)
			// 3a) Si l'utilisateur s�lectionne un filtre, seuls les fichiers du m�me XML que le 1er fichier du r�pertoire, sont export�s (avec le filtre s�lectionn�).
			// 3b) Si l'utilisateur ne s�lectionne pas de filtre, tous les fichiers du r�pertoire, sont export�s

			// Just to print to the user the filter of the first file
			BridageFormats.getInstance().setFormatFichierOuvert(BridageFormats.getInstance().getFormatFichierOuvert(cheminsFichier.get(0)));		
			
			try {
				ChargeurParcours.loadBinaryFile(cheminsFichier.get(0),0,1,false);
			} catch (ParseurXMLException e) {
				// TODO Bloc catch g�n�r� automatiquement
				e.printStackTrace();
			} catch (AExceptionSamNG e) {
				// TODO Bloc catch g�n�r� automatiquement
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Bloc catch g�n�r� automatiquement
				e.printStackTrace();
			}
			
			ActivatorData.getInstance().getListenersDuplication().clear();
			
			List<String> filtersNames = new ArrayList<String>();
			// creating a GestionnaireFiltresTabulaire will also load the filters
			AGestionnaireFiltres filtersMng = new GestionnaireFiltresTabulaire();
			AFiltreComposant filtersList = filtersMng.getListeFiltres();
			
			if (filtersList != null) {
				int filtersCount = filtersList.getEnfantCount();
				for (int i = 0; i < filtersCount; i++) {
					AFiltreComposant filter = filtersList.getEnfant(i);
					if (filter != null)
						filtersNames.add(filter.getNom());
				}
			}

			// create the filters selections dialog and set the filters names
			ExporterVueTabulaireDialog filterSelDlg = new ExporterVueTabulaireDialog(
					this.window.getShell());
			filterSelDlg.setFiltersNames(filtersNames);
			ret = filterSelDlg.open();
			if (ret == SWT.OK) {
				selFilterName = filterSelDlg.getSelectedFilterName();
				
				if (selFilterName != null) {
					List<String> cheminsFichier_old = new ArrayList<String>(cheminsFichier);
					
					for (String nomFichier : cheminsFichier_old) {
						if (!BridageFormats.getInstance().isextensionValideFromFormat(BridageFormats.getInstance().getFormatFichierOuvert(""), nomFichier)) {
							cheminsFichier.remove(nomFichier) ;
						}
					}
				}
				
				
				BarreProgressionExportMultiple bp=new BarreProgressionExportMultiple(this);
				bp.schedule();
				//////////////////////////////////////////////////				
				//				try {					
				//					int i =0;
				//					while(i<cheminsFichier.size()){
				//						Activator.getDefault().getListRunFileToMultipleExport().add(cheminsFichier.get(i));
				//						BridageFormats.getInstance().setGestionConflitExtension(true);
				//						BridageFormats.getInstance().setFormatFichierOuvert(null);
				//						exporterFichier(cheminsFichier.get(i), selFilterName);
				//						i++;
				//					}
				//					
				//					MessageBox msgBox2 = new MessageBox(Display.getCurrent().getActiveShell(),SWT.ICON_INFORMATION);
				//					msgBox2.setText(Messages.getString("MultipleExportsAction.11")); //$NON-NLS-1$
				//					msgBox2.setMessage(Messages.getString("MultipleExportsAction.12")); //$NON-NLS-1$
				//					msgBox2.open();
				//				} catch (AExceptionSamNG e) {
				//					e.printStackTrace();
				//				}finally{
				////					VueProgressBar.getInstance().courant=VueProgressBar.getInstance().fin;
				////					VueProgressBar.getInstance().stop();
				//				}
				/////////////////////////////////////////////////

				xmlFile = null;
			} else {
				//				VueProgressBar.getInstance().courant=VueProgressBar.getInstance().fin;
				//				VueProgressBar.getInstance().stop();
				msgBox = new MessageBox(this.window.getShell(),
						SWT.ICON_INFORMATION | SWT.OK);
				msgBox.setText(""); //$NON-NLS-1$
				msgBox
				.setMessage(com.faiveley.samng.vuetabulaire.ihm.actions.Messages
						.getString("MultipleExportsAction.8")); //$NON-NLS-1$
				msgBox.open();
			}
		}

		// release the current data in the activator
		Activator.getDefault().release();
		ActivatorData.getInstance().getBarAdvisor().setActionsEnabled(false);

		ActivatorData.getInstance().notifyDataListeners();
	}

	private void fillCheminsFichier(String repertoireFichiers) {
		File f = new File(repertoireFichiers);
		File[] listeFichiers = f.listFiles();
		int nbFichiers = listeFichiers.length;
		
		for (int i = 0; i < nbFichiers; i++) {
			if (listeFichiers[i].isDirectory()) {
				fillCheminsFichier(listeFichiers[i].getAbsolutePath()) ;
			} else {
				String nomFichier = listeFichiers[i].getName().toUpperCase();
				if (BridageFormats.getInstance().isextensionValide(nomFichier))
					cheminsFichier.add(listeFichiers[i].getAbsolutePath());
			}
		}
		
	}

	public List<String> getCheminsFichier() {
		return cheminsFichier;
	}

	public void setCheminsFichier(List<String> cheminsFichier) {
		this.cheminsFichier = cheminsFichier;
	}

	public String getSelFilterName() {
		return selFilterName;
	}

	public void setSelFilterName(String selFilterName) {
		this.selFilterName = selFilterName;
	}
}
