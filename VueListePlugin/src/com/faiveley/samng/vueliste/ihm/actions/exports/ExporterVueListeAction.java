package com.faiveley.samng.vueliste.ihm.actions.exports;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableTreeItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;

import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.principal.ihm.vues.Row;
import com.faiveley.samng.principal.ihm.vues.VueListeContentProvider;
import com.faiveley.samng.principal.sm.erreurs.AExceptionSamNG;
import com.faiveley.samng.principal.sm.parseurs.ParseurExport;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.vueliste.ihm.ActivatorVueListe;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.FixedColumnTableViewerDetail;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.TableTreeDetailViewer;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.VueListe;

@SuppressWarnings("deprecation")
@Deprecated
public class ExporterVueListeAction extends Action {
	private final IWorkbenchWindow window;

	public ExporterVueListeAction(final IWorkbenchWindow window, final String text) {
		super(text);
		this.window = window;

		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_OPEN_MESSAGE);
		
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_OPEN_MESSAGE);
		setImageDescriptor(com.faiveley.samng.principal.ihm.Activator.getImageDescriptor("/icons/toolBar/vue_tableau_exporter_selection.png"));
	}

	/**
	 * M�thode d'export de la selection de la vue liste: fait appel � la classe
	 * ParseurExport du Framework
	 * 
	 * @param cheminFichier
	 * @return
	 * @throws AExceptionSamNG
	 */
	private boolean exporterSelection(String cheminFichier)	throws AExceptionSamNG {
		// EVOLUTION EXPORT VUE DETAILLEE
		ActivatorVueListe activatorVueListe = ActivatorVueListe.getDefault();
		
		if (activatorVueListe.getVueFocus() == ActivatorVueListe.FOCUS_VUE_LISTE) {
			// R�cup�ration de la selection
			TableItem[] items = getVueListe().getTable().getSelection();
			int nbLignes = items.length;
			
			// Cr�ation du tableau de Row: donn�es parcourues par le parseur
			if (nbLignes > 0) {
				MessageBox msgBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.YES | SWT.NO);
				msgBox.setText(Messages.getString("ExporterVueListeAction.1"));
				msgBox.setMessage(Messages.getString("ExporterVueListeAction.2"));
				int ret = msgBox.open();
				List<Row> selectionLignes = new ArrayList<Row>();
				
				if (ret == SWT.NO) {
					VueListeContentProvider contentProvider = new VueListeContentProvider(activatorVueListe.getConfigurationMng(), activatorVueListe);
					
					// Set the filter name to the content provider
					contentProvider.setFilter(activatorVueListe.getFiltresProvider().getAppliedFilter());
					contentProvider.initializeColumns();
					contentProvider.setExport(true);
					contentProvider.loadContent(null);
					contentProvider.setExport(false);
					
					Object[] rows = contentProvider.getElements(null);

					// Issue 726
					for (int j = 0; j < rows.length; j++) {
						Row r = (Row) rows[j];
						String[] tabStrings = r.getStrings();
						
						for (int i = 0; i < tabStrings.length; i++) {
							if (tabStrings[i] != null) {
								if (tabStrings[i].contains("\0")) {
									r.setValue(i, tabStrings[i].substring(0, tabStrings[i].indexOf("\0")));
								}
							}
						}
						
						selectionLignes.add((Row) rows[j]);
					}
				} else if (ret == SWT.YES) {
					for (int i = 0; i < items.length; i++) {
						selectionLignes.add((Row) (items[i].getData()));
					}
				}

				ParseurExport parseur = new ParseurExport();
				parseur.parseRessource(cheminFichier,false,0,-1);
				parseur.exporterLignes(cheminFichier, getVueListe().getColumnNames(), selectionLignes);
				MessageBox msgBox2 = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION);
				msgBox2.setText(Messages.getString("ExporterVueListeAction.3"));
				msgBox2.setMessage(Messages.getString("ExporterVueListeAction.4"));
				msgBox2.open();
			}

			return true;
		} else {
			FixedColumnTableViewerDetail fctv = getVueListe().getFctvd();
			boolean isKVB = fctv.getTableTreeKVBDetailViewer() != null && fctv.getSelectedTab() == 1;
			
			TableTreeDetailViewer tableTreeDetailViewer = isKVB ? fctv.getTableTreeKVBDetailViewer() : fctv.getTableTreeDetailViewer();
			
			// La vue d�taill�e doit etre d�pli�e pour faire l'export	
			
			tableTreeDetailViewer.expandAll();

			String chaineExportVueDetaillee = creerChaineExportVueDetaillee(tableTreeDetailViewer);
			
			ParseurExport parseur = new ParseurExport();
			parseur.parseRessource(cheminFichier, false, 0, -1);
			parseur.exporterChaine(chaineExportVueDetaillee);

			return true;
		}
	}

	private String creerChaineExportVueDetaillee(TableTreeDetailViewer tableTreeDetailViewer) {	
		List<LigneFichierExport> lines = new ArrayList<LigneFichierExport>();	
		int level = 0;
		int maxLevel = level;
		
		// Colonnes
		String[] columnNames = tableTreeDetailViewer.getColumnNames();
		int nbColumns = columnNames.length;
		
		LigneFichierExport line = new LigneFichierExport(level);
		
		for (int i = 0; i < nbColumns; i++) {
			line.addValue(columnNames[i]);
		}
		
		lines.add(line);
		
		// Items
		for (TableTreeItem item : tableTreeDetailViewer.getTableTree().getItems()) {
			maxLevel = Math.max(maxLevel, inspectItem(nbColumns, lines, level, item));
		}
		
		StringBuilder chaineExportBuilder = new StringBuilder();
				
		for (LigneFichierExport l : lines) {
			List<String> values = l.getValues();
			
			chaineExportBuilder.append(creerTabulations(l.getLevel()));
			chaineExportBuilder.append(values.get(0));
			chaineExportBuilder.append(creerTabulations(maxLevel + 1 - l.getLevel()));
			
			for (int i = 1; i < values.size(); i++) {
				if (i > 1) {
					chaineExportBuilder.append(creerTabulations(1));
				}
				
				chaineExportBuilder.append(values.get(i));
			}
			
			chaineExportBuilder.append("\n");
		}
		
		return chaineExportBuilder.toString();
	}
	
	private int inspectItem(int nbColumns, List<LigneFichierExport> lines, int level, TableTreeItem item) {
		LigneFichierExport line = new LigneFichierExport(level);
		
		for (int i = 0; i < nbColumns; i++) {
			line.addValue(item.getText(i));
		}
		
		lines.add(line);
		
		TableTreeItem[] childs = item.getItems();
		
		int maxLevel = level;
		
		if (childs.length > 0) {
			level += 1;
			
			for (TableTreeItem tti : childs) {
				maxLevel = Math.max(maxLevel, inspectItem(nbColumns, lines, level, tti));
			}
		}
		
		return maxLevel;
	}

	/**
	 * Cr�� des tabulations
	 * 
	 * @param nbTabulations
	 * @return
	 */
	private static String creerTabulations(int nbTabulations) {
		StringBuilder chaineTabulationsBuilder = new StringBuilder();
		
		for (int i = 0; i < nbTabulations; i++) {
			chaineTabulationsBuilder.append("\t");
		}

		return chaineTabulationsBuilder.toString();

	}

	/**
	 * M�thode de r�cup�ration de l'instance de la VueListe
	 * 
	 * @return instance de VueListe
	 */
	private VueListe getVueListe() {
		IWorkbenchWindow window = ActivatorVueListe.getDefault().getWorkbench().getActiveWorkbenchWindow();
		IViewReference[] ivr = window.getActivePage().getViewReferences();
		VueListe vueListe;
		
		for (int t = 0; t < ivr.length; t++) {
			if (ivr[t].getId().equals(VueListe.ID)) {
				vueListe = (VueListe) ivr[t].getPart(false);
				
				return vueListe;
			}
		}
		
		return null;
	}

	/**
	 * M�thode d'ouverture de la fenetre de dialogue d'export de selection
	 */
	public final void run() {
		// Creates a file dialog to open the binary files
		FileDialog dialog = new FileDialog(this.window.getShell(), SWT.SAVE);

		// D�finition des extensions visibles
		dialog.setFilterExtensions(new String[] { "*.tsv", "*.csv" });
		dialog.setFilterNames(new String[] { "*.tsv", "*.csv" });
		
		//DR28_CL36 
		dialog.setFilterPath(RepertoiresAdresses.RepertoireTravail);
		
		// R�cup�ration du nom du fichier et du chemin
		String cheminFichier = dialog.open();
		String nomFichier = dialog.getFileName();

		if (!nomFichier.trim().equals("")) {
			try {
				exporterSelection(cheminFichier);
			} catch (AExceptionSamNG e) {
				e.printStackTrace();
			}
		}
	}

	private class LigneFichierExport {
		private int level;
		private List<String> values;
		
		public LigneFichierExport(int level) {
			this.level = level;
			this.values = new ArrayList<String>();
		}
		
		public int getLevel() {
			return this.level;
		}
		
		public List<String> getValues() {
			return this.values;
		}
		
		public void addValue(String v) {
			this.values.add(v);
		}
	}
}
