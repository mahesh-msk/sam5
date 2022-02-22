package com.faiveley.samng.vueliste.ihm.vues.vueliste.kvb;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.custom.TableTreeItem;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.enregistrement.atess.AtessMessage;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.FixedColumnTableViewerDetail;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.TableTreeDetailViewer;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.configuration.GestionnaireVueDetaillee;

@SuppressWarnings("deprecation")
/** 
 * 
 * @deprecated use TreeKVBDetailViewer instead
 *
 */
@Deprecated
public class TableTreeKVBDetailViewer extends TableTreeDetailViewer {
	private static final Display display = PlatformUI.getWorkbench().getDisplay();
	private static final Color colorRed = display.getSystemColor(SWT.COLOR_RED);
		
	private FixedColumnTableViewerDetail parent;
	
	public TableTreeKVBDetailViewer(FixedColumnTableViewerDetail parent, Composite compositeParent, int style, GestionnaireVueDetaillee gestionnaireColonne) {
		super(compositeParent, style, gestionnaireColonne);
		this.parent = parent;
		columnNames = new String[] {GestionnaireVueDetaillee.KVB_TABLE_COL_NAME.getLabel(), GestionnaireVueDetaillee.KVB_DECODED_VALUE_COL_NAME.getLabel()};
	}
	
	@Override
	protected void setComponents() {
		setContentProvider(new TableTreeKVBDetailContentProvider());
		setLabelProvider(new TableTreeKVBDetailLabelProvider());
		setInput(null);
	}
	
	@Override
	protected void setHiddenColumn() {
		final Table table = getTableTree().getTable();
		TableColumn column = new TableColumn(table, SWT.LEFT);
		column.setText("");
		column.setWidth(0);
		column.setResizable(false);
	}
	
	@Override
	protected void handleMouseDownEvent(Table table, MouseEvent event) {
    	// Set information point
		Point pt = new Point(event.x, event.y);
        TableItem item = table.getItem(pt);
        
        if (item != null) {
        	String hiddenColumnText = item.getText(2);
        	
        	if (!hiddenColumnText.isEmpty()) {
        		parent.getTableTreeInformationPointDetailViewer().setInformationPoint(Integer.parseInt(hiddenColumnText));     		
        	}
        }
        
        // Re-color lines
        colorLines();
	}
		
	public void setInputMessage(AtessMessage message) {
		message.decodeKVBMessage();
		setInput(message);
	}
	
	public void refreshTableData(Message message) {
		TableTree treeTable = getTableTree();
		refresh(true);	
		modifierConfigurationColonnes();
		treeTable.redraw();
		chargerPresentationEv(message);
		
		// Si une exception a �t� remont�e
		// Ne pas d�pli� les points d'information car ils risqueraient de d�clancher
		// une exception suivant ou l'erreur de d�codage � eu lieu
		if (message instanceof AtessMessage) {
			if (((AtessMessage) message).getDecodingErrors().size() == 0) {
				expandAll();				
			} else { // Les points d'information se trouvent dans le dernier children
				try {
					expandToLevel(getChildren(internalGetWidgetToSelect(getRoot())).length - 1);
				} catch (Exception e) {
					// TODO Bloc catch g�n�r� automatiquement
					//e.printStackTrace();
				}
			}
		}
			
		// Color lines
		colorLines();
	}
	
	public void colorLines() {
		// Coloration des textes en rouge
		TableTree tableTree = getTableTree();
		Table table = tableTree.getTable();
		
		// On d�termine les lignes � colorer gr�ce au tableau sous forme TableTree (possibilit� d'inspecter les niveaux hi�rarchiquement)		
		int rowIndex = 0;
		int firstLevelIndex = 0;

		for (TableTreeItem tableTreeLevel1 : tableTree.getItems()) { // Niveau 1
			rowIndex++;
			firstLevelIndex++;
			
			for (TableTreeItem tableTreeLevel2 : tableTreeLevel1.getItems()) { // Niveau 2								
				if (tableTreeLevel2.getExpanded()) { // A un niveau inf�rieur: c'est uniquement les cas des Points d'Information
										
					if (!tableTreeLevel2.getText(1).isEmpty() && table.getItemCount() > rowIndex) { // A un texte d'alerte ; n'est pas repli� (si repli�: inutile et source d'erreur car 'table' ne consid�re que les lignes visibles, donc d�pli�es)
						// On colore la ligne, uniquement sur la deuxi�me colonne, gr�ce  � la table sous forme Table (possibilit� de diff�rencier les colonnes)
						table.getItem(rowIndex).setForeground(1, colorRed);
					}
					
					rowIndex += tableTreeLevel2.getItemCount();
				}
				
				rowIndex++;
			}
			
			if (firstLevelIndex >= 3) { // Niveau 1, troisi�me �l�ment ou plus: ce sont les erreurs
				tableTreeLevel1.setForeground(colorRed);
			}
		}
	}
}
