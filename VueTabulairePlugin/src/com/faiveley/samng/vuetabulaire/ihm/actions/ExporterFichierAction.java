package com.faiveley.samng.vuetabulaire.ihm.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;

import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.principal.ihm.vues.Row;
import com.faiveley.samng.principal.ihm.vues.VueTabulaireContentProvider;
import com.faiveley.samng.principal.sm.parseurs.ParseurExportVueTabulaire;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class ExporterFichierAction extends Action {
	protected final IWorkbenchWindow window;
	private String cheminFichier;
	private List<String> columnNames;
	private List<Row> selectionLignes;
	private boolean shouldReleaseData;

	/**
	 * Méthode de création du tableau de Row nécessaire à l'export
	 * @param cheminFichier
	 * @param filtre
	 * @return
	 */
	public void exporterDansFichier(String cheminFichier, List<String> columnNames, 
			List<Row> selectionLignes, final VueTabulaireContentProvider contentProvider) {
		
		this.cheminFichier = cheminFichier;
		this.columnNames = columnNames;
		this.selectionLignes = selectionLignes;

//		VueProgressBar progrBar = Activator.getDefault().getProgressBar();
        
//		VueProgressBar.getInstance().setRect(window.getWorkbench().getActiveWorkbenchWindow().getShell().getBounds());
//		VueProgressBar.getInstance().start(false);		

		
		Thread executionThread = new Thread(new Runnable() {
			public void run() {
				try {
					ParseurExportVueTabulaire parseur = new ParseurExportVueTabulaire();
					parseur.parseRessource(ExporterFichierAction.this.cheminFichier,false,0,-1);
					parseur.exporterLignes(ExporterFichierAction.this.cheminFichier, 
							ExporterFichierAction.this.columnNames, 
							ExporterFichierAction.this.selectionLignes, contentProvider);
					if(shouldReleaseData)
						Activator.getDefault().release();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
//					if(VueProgressBar.getInstance()!=null)
//						VueProgressBar.getInstance().stop();
				}
			}
		});
		executionThread.start();
	}

	
	/**
	 * Méthode d'ouverture de la fenêtre de dialogue
	 * @param window
	 * @param text
	 */
	public ExporterFichierAction(final IWorkbenchWindow window,
			final String text) {
		super(text);
		this.window = window;

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_EXPORT_FILE);
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_EXPORT_FILE);
		setImageDescriptor(com.faiveley.samng.principal.ihm.Activator
				.getImageDescriptor("/icons/sample3.gif"));
	}


	public void setShouldReleaseData(boolean shouldReleaseData) {
		this.shouldReleaseData = shouldReleaseData;
	}
}
