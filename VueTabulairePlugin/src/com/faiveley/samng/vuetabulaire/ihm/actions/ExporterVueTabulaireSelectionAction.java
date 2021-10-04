package com.faiveley.samng.vuetabulaire.ihm.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;

import com.faiveley.samng.principal.ihm.vues.Row;
import com.faiveley.samng.principal.ihm.vues.VueTabulaireContentProvider;
import com.faiveley.samng.principal.sm.erreurs.AExceptionSamNG;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.vuetabulaire.ihm.ActivatorVueTabulaire;
import com.faiveley.samng.vuetabulaire.ihm.vues.vuetabulaire.VueTabulaire;

/**
 * 
 * @author Olivier
 * 
 */
public class ExporterVueTabulaireSelectionAction extends ExporterFichierAction {

	public ExporterVueTabulaireSelectionAction(final IWorkbenchWindow window,
			final String text) {
		super(window, text);
		setImageDescriptor(com.faiveley.samng.principal.ihm.Activator
				.getImageDescriptor("/icons/toolBar/vue_tableau_exporter_selection.png")); //$NON-NLS-1$
	}

	/**
	 * Méthode d'export de la selection de la vue tabulaire: fait appel à la
	 * classe ParseurExport du framework
	 * 
	 * @param cheminFichier
	 * @return
	 * @throws AExceptionSamNG
	 */
	private void exporterSelection(String cheminFichier) throws AExceptionSamNG {

		TableItem[] items = getVueTabulaire().getTable().getSelection();
		VueTabulaireContentProvider contentProvider = (VueTabulaireContentProvider) getVueTabulaire().tblFix.getContentProvider();
		
		int nbLignes = items.length;
		List<Row> selectionLignes = new ArrayList<Row>();
		
		
		if (nbLignes > 0) {
			MessageBox msgBox = new MessageBox(Display.getCurrent().getActiveShell(),SWT.YES|SWT.NO);
			msgBox.setText(Messages.getString("ExporterVueTabulaireSelectionAction.1")); //$NON-NLS-1$
			msgBox.setMessage(Messages.getString("ExporterVueTabulaireSelectionAction.2")); //$NON-NLS-1$
			int ret = msgBox.open();
			if(ret==SWT.NO){
				selectionLignes = remplirLignesAExporter();
		
			}else if(ret==SWT.YES){
				for (int i = 0; i < items.length; i++) {
					selectionLignes.add((Row) (items[i].getData()));
				}
			}
			
			
			exporterDansFichier(cheminFichier, getVueTabulaire()
					.getColumnLabels(), selectionLignes, contentProvider);
			MessageBox msgBox2 = new MessageBox(Display.getCurrent().getActiveShell(),SWT.ICON_INFORMATION);
			msgBox2.setText(Messages.getString("ExporterVueTabulaireSelectionAction.3")); //$NON-NLS-1$
			msgBox2.setMessage(Messages.getString("ExporterVueTabulaireSelectionAction.4")); //$NON-NLS-1$
			msgBox2.open();
		}
	}

	/**
	 * @param selectionLignes
	 */
	public List<Row> remplirLignesAExporter() {
		List<Row> selectionLignes = new ArrayList<Row>();
		VueTabulaireContentProvider contentProvider = new VueTabulaireContentProvider(ActivatorVueTabulaire.getDefault().getConfigurationMng(), ActivatorVueTabulaire.getDefault());
		//set the filter name to the content provider
		contentProvider.setFilter(ActivatorVueTabulaire.getDefault().getFiltresProvider().getAppliedFilter());
		//do not ignore columns that are not displayable
		contentProvider.setAddNonDisplayableColumns(false);
		contentProvider.initializeColumns();
		//VueProgressBar.getInstance().start();
		contentProvider.setExport(true);
		contentProvider.loadContent(null);
		contentProvider.setExport(false);		
		Object[] rows = contentProvider.getElements(null);
		//issue 726
		for(int j=0;j<rows.length;j++){
			Row r=(Row)rows[j];
			String[] tabStrings=r.getStrings();
			for (int i = 0; i < tabStrings.length; i++) {
				if (tabStrings[i].contains("\0")) {
					r.setValue(i, tabStrings[i].substring(0, tabStrings[i].indexOf("\0")));
				}
			}
			selectionLignes.add((Row)rows[j]);
		}
		return selectionLignes;
	}

	/**
	 * Méthode de recherche de l'instance de la vue tabulaire
	 * 
	 * @return instance de VueTabulaire
	 */
	private VueTabulaire getVueTabulaire() {
		IWorkbenchWindow window = ActivatorVueTabulaire.getDefault()
				.getWorkbench().getActiveWorkbenchWindow();
		IViewReference[] ivr = window.getActivePage().getViewReferences();
		VueTabulaire vueListe;
		for (int t = 0; t < ivr.length; t++) {
			if (ivr[t].getId().equals(VueTabulaire.ID)) {
				vueListe = (VueTabulaire) ivr[t].getPart(false);
				return vueListe;
			}
		}
		return null;
	}

	/**
	 * Méthode d'ouverture de la fenetre de dialogue d'export de selection
	 */
	public final void run() {
		
		// creates a file dialog to open the binary files
		FileDialog dialog = new FileDialog(this.window.getShell(), SWT.SAVE);

		// définition des extensions visibles
		dialog.setFilterExtensions(new String[] { "*.tsv", "*.csv" }); //$NON-NLS-1$ //$NON-NLS-2$

		dialog.setFilterNames(new String[] { "*.tsv", "*.csv" }); //$NON-NLS-1$ //$NON-NLS-2$

		//DR28_CL36 
		dialog.setFilterPath(RepertoiresAdresses.RepertoireTravail);
		
		// récupération du nom du fichier et du chemin
		String cheminFichier = dialog.open();

		String nomFichier = dialog.getFileName();

		if (!nomFichier.trim().equals("")) //$NON-NLS-1$
			try {
				exporterSelection(cheminFichier);
			} catch (AExceptionSamNG e) {
				//  Auto-generated catch block
				e.printStackTrace();
			}
	}
}
