package com.faiveley.samng.vueliste.ihm.vues.vueliste.kvb.e4;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;

import com.faiveley.kvbdecoder.model.kvb.ip.InformationPoint;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.enregistrement.atess.AtessMessage;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.configuration.GestionnaireVueDetaillee;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.e4.FixedColumnTableViewerDetail;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.e4.TreeDetailViewer;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.kvb.TableTreeKVBDetailContentProvider;

public class TreeKVBDetailViewer extends TreeDetailViewer {
	private static final Display display = PlatformUI.getWorkbench().getDisplay();
	private static final Color colorRed = display.getSystemColor(SWT.COLOR_RED);
		
	private FixedColumnTableViewerDetail parent;
	
	public TreeKVBDetailViewer(FixedColumnTableViewerDetail parent, Composite compositeParent, int style, GestionnaireVueDetaillee gestionnaireColonne) {
		super(compositeParent, style, gestionnaireColonne);
		this.parent = parent;
		columnNames = new String[] {GestionnaireVueDetaillee.KVB_TABLE_COL_NAME.getLabel(), GestionnaireVueDetaillee.KVB_DECODED_VALUE_COL_NAME.getLabel()};
	}
	
	
	/** return the column provider to use for the tree columns. THis method can be overridden */
	protected ColumnLabelProvider getColumnLabelProvider(int index)
	{
		return new TreeKVBDetailColumnProvider(index);
	}

	@Override
	protected void setComponents() {
		setContentProvider(new TableTreeKVBDetailContentProvider());
		setLabelProvider(new LabelProvider());  // Label provider on tree can be the default, as only columns are displayed
		setInput(null);
	}
	
	@Override
	protected void setHiddenColumn() {
		
		TreeViewerColumn column = new TreeViewerColumn(this, SWT.LEFT);
		column.getColumn().setText("");
		column.getColumn().setWidth(0);
		column.setLabelProvider(new ColumnLabelProvider());
		column.getColumn().setResizable(false);
		
	}
	
	@Override
	protected void handleMouseDownEvent(Tree tree, MouseEvent event) {
		
    	// Set information point
		Point pt = new Point(event.x, event.y);
        TreeItem item = tree.getItem(pt);
        
        if (item != null) {
        	Object data = item.getData();
        	if (data instanceof InformationPoint)
        	{
        		InformationPoint ip = (InformationPoint) data;
        		parent.getTableTreeInformationPointDetailViewer().setInformationPoint(ip.getIndex());     		
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
				
		Tree treeTable = getTree();
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
					//e.printStackTrace();
				}
			}
		}
			
		// Color lines
		colorLines();
		
		
	}
	
	public void colorLines() {

		// Coloration des textes en rouge
		Tree tree = getTree();
		
		// On d�termine les lignes � colorer gr�ce au tableau sous forme TableTree (possibilit� d'inspecter les niveaux hi�rarchiquement)		
		int rowIndex = 0;
		int firstLevelIndex = 0;

		for (TreeItem tableTreeLevel1 : tree.getItems()) { // Niveau 1
			rowIndex++;
			firstLevelIndex++;
			
			for (TreeItem tableTreeLevel2 : tableTreeLevel1.getItems()) { // Niveau 2								
				if (tableTreeLevel2.getExpanded()) { // A un niveau inf�rieur: c'est uniquement les cas des Points d'Information
										
					if (!tableTreeLevel2.getText(1).isEmpty() && tree.getItemCount() > rowIndex) { // A un texte d'alerte ; n'est pas repli� (si repli�: inutile et source d'erreur car 'table' ne consid�re que les lignes visibles, donc d�pli�es)
						// On colore la ligne, uniquement sur la deuxi�me colonne, gr�ce  � la table sous forme Table (possibilit� de diff�rencier les colonnes)
						tree.getItem(rowIndex).setForeground(1, colorRed);
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
