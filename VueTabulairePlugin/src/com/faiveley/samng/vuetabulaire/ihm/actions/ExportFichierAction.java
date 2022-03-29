package com.faiveley.samng.vuetabulaire.ihm.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.principal.ihm.vues.Row;
import com.faiveley.samng.principal.ihm.vues.VueTabulaireContentProvider;
import com.faiveley.samng.principal.sm.erreurs.AExceptionSamNG;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.parseurs.ParseurExport;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.vuetabulaire.ihm.ActivatorVueTabulaire;
import com.faiveley.samng.vuetabulaire.ihm.dialogs.ExporterVueTabulaireDialog;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class ExportFichierAction extends Action {
	public IWorkbenchWindow window;

	public ExportFichierAction(final IWorkbenchWindow window,
			final String text) {
		super(text);
		this.window = window;

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_EXPORT_FILE);
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_EXPORT_FILE);
	}

	/**
	 * M�thode d'export de la selection de la vue tabulaire: fait appel � la classe
	 * ParseurExport du framework
	 * 
	 * @param cheminFichier
	 * @return
	 * @throws AExceptionSamNG
	 */
	private void exporterFichier(final String cheminFichier, final String selFilterName)
	throws AExceptionSamNG {	

		ExecutionThread executionThread=new ExecutionThread(this.window.getShell(),cheminFichier,selFilterName);
		executionThread.start();
	}

	/**
	 * M�thode d'ouverture de la fenetre de dialogue d'export de selection
	 */
	public final void run() {

		if(this.window==null)
			this.window =PlatformUI.getWorkbench().getActiveWorkbenchWindow();


		// creates a file dialog to open the binary files
		FileDialog dialog = new FileDialog(this.window.getShell(), SWT.SAVE);

		// d�finition des extensions visibles
		dialog.setFilterExtensions(new String[] { "*.tsv", "*.csv" }); //$NON-NLS-1$ //$NON-NLS-2$

		dialog.setFilterNames(new String[] { "*.tsv", "*.csv" }); //$NON-NLS-1$ //$NON-NLS-2$

		//DR28_CL36 
		dialog.setFilterPath(RepertoiresAdresses.RepertoireTravail);
		
		// r�cup�ration du nom du fichier et du chemin
		String cheminFichier = dialog.open();

		String nomFichier = dialog.getFileName();

		if (!nomFichier.trim().equals("")) { //$NON-NLS-1$
			List<String> filtersNames = new ArrayList<String>();
			AFiltreComposant filtersList = ActivatorVueTabulaire.getDefault().
			getFiltresProvider().getGestionnaireFiltres().
			getListeFiltres();
			if(filtersList != null) {
				int filtersCount = filtersList.getEnfantCount();
				for(int i = 0; i<filtersCount; i++) {
					AFiltreComposant filter = filtersList.getEnfant(i);
					if(filter != null)
						if(ActivatorVueTabulaire.getDefault()
								.getFiltresProvider().filtrevalide(filter))
								filtersNames.add(filter.getNom());
				}
			}

			//create the filters selections dialog and set the filters names
			ExporterVueTabulaireDialog filterSelDlg = new ExporterVueTabulaireDialog(this.window.getShell());
			filterSelDlg.setFiltersNames(filtersNames);
			int ret = filterSelDlg.open();
			if(ret == SWT.OK) {
				String selFilterName = filterSelDlg.getSelectedFilterName();
				try {
					exporterFichier(cheminFichier, selFilterName);
				} catch (AExceptionSamNG e) {

				}
			} else {
				MessageBox msgBox = new MessageBox(this.window.getShell(), SWT.ICON_INFORMATION | SWT.OK);
				msgBox.setText(""); //$NON-NLS-1$
				msgBox.setMessage(Messages.getString("ExportVueTabulaireAction.6")); //$NON-NLS-1$
				msgBox.open();
			}
		}
	}
	
	private class ExecutionThread extends Thread{
		
		private Shell shell;
		private String cheminFichier;
		private String selFilterName;
		public ExecutionThread(Shell shell,String cheminFichier,String selFilterName) {
			this.shell=shell;
			this.cheminFichier=cheminFichier;
			this.selFilterName=selFilterName;
		}
		
		@Override
		public void run() {
			try {
				VueTabulaireContentProvider contentProvider = new VueTabulaireContentProvider(ActivatorVueTabulaire.getDefault().getConfigurationMng(), ActivatorVueTabulaire.getDefault());
				//set the filter name to the content provider
				contentProvider.setFilter(selFilterName);
				//do not ignore columns that are not displayable
				contentProvider.setAddNonDisplayableColumns(true);
				contentProvider.initializeColumns();
				contentProvider.setExport(true);
				contentProvider.loadContent(null);
				contentProvider.setExport(false);
				//if (!VueProgressBar.getInstance().isEscaped){						
				List<String> columnNames = contentProvider.getColumnLabels();
				Object[] rows = contentProvider.getElements(null);

				if (rows != null && rows.length > 0) {
					List<Row> selectionLignes = new ArrayList<Row>();
					for (int i = 0; i < rows.length; i++) {
						selectionLignes.add((Row)rows[i]);
					}

					ParseurExport parseur = new ParseurExport();
					parseur.parseRessource(cheminFichier,false,0,-1);
					parseur.exporterLignes(cheminFichier, 
							columnNames, 
							selectionLignes);
				}
				Display.getDefault().syncExec(new Runnable(){
					public void run() {
						MessageBox msgBox2 = new MessageBox(ExecutionThread.this.shell,SWT.ICON_INFORMATION);
						msgBox2.setText(Messages.getString("MultipleExportsAction.11")); //$NON-NLS-1$
						msgBox2.setMessage(Messages.getString("MultipleExportsAction.12")); //$NON-NLS-1$
						msgBox2.open();
					}
				});


			} catch (Exception e) {

			} finally {

			}
		}
	}
}
