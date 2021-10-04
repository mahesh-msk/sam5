package com.faiveley.samng.vueliste.ihm.vues.vueliste;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.vues.InstancePresentationVueDetaillee;
import com.faiveley.samng.principal.ihm.vues.MessageSelection;
import com.faiveley.samng.principal.ihm.vues.VariablesSelect;
import com.faiveley.samng.principal.ihm.vues.VueDetaillee;
import com.faiveley.samng.principal.ihm.vues.configuration.ConfigurationColonne;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.Paquets;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.ChaineDynamique;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.StructureDynamique;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.TableauDynamique;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.VariableDynamique;
import com.faiveley.samng.principal.sm.formats.FormatSAM;
import com.faiveley.samng.principal.sm.parseurs.BridageFormats;
import com.faiveley.samng.vueliste.ihm.ActivatorVueListe;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.FixedColumnTableViewerDetail.TypeMenuOptions;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.configuration.GestionnaireVueDetaillee;

@SuppressWarnings("deprecation")
public class TableTreeDetailViewer extends TableTreeViewer {	
	protected String[] columnNames = new String[] {GestionnaireVueDetaillee.VARIABLE_NAME_COL_NAME.getLabel(), GestionnaireVueDetaillee.CRUDE_VALUE_COL_NAME.getLabel(), GestionnaireVueDetaillee.DECODED_VALUE_COL_NAME.getLabel()};
	private GestionnaireVueDetaillee gestionnaireColonne;
	
	public TableTreeDetailViewer(Composite parent, int style, GestionnaireVueDetaillee gestionnaireColonne) {
		super(parent, style);
		this.gestionnaireColonne = gestionnaireColonne;
	}
	
	public void createContents(final VueListe vueListe) {
		setComponents();
		setUpTable(gestionnaireColonne, vueListe);
	}
	
	protected void setComponents() {
		setContentProvider(new TableTreeDetailContentProvider());
		setLabelProvider(new TableTreeDetailLabelProvider());
		setInput(null);
	}
	
	protected void setHiddenColumn() {
		return;
	}
	
	protected void handleMouseDownEvent(Table table, MouseEvent event) {
		return;
	}
	
	public void setUpTable(final GestionnaireVueDetaillee gestionnaireColonne, final VueListe vueListe) {
		final Table table = getTableTree().getTable();
		
		for (String cn : columnNames) {
			TableColumn column = new TableColumn(table, SWT.LEFT);
			column.setText(cn);
			
			column.addControlListener(new ControlAdapter() {
				public void controlResized(ControlEvent event) {
					if (event.widget instanceof TableColumn) {
						TableColumn col = (TableColumn) event.widget;
						int width = col.getWidth();
						String colName = (String) col.getText();
						ConfigurationColonne colCfg = gestionnaireColonne.getConfigurationColonneByNom(colName);
						
						if (colCfg != null) {
							colCfg.setLargeur(width);
							gestionnaireColonne.setChanged(true);
						}
					}
				}
			});
		}
		
		setHiddenColumn();
		
		// Turn on the header and the lines
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		table.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
				if (e.button == 1) {
					Object element = getSelectionFromWidget().get(0);
					
					if (element instanceof AVariableComposant) {
						AVariableComposant var = (AVariableComposant) getSelectionFromWidget().get(0);
						
						if (var instanceof VariableAnalogique || var instanceof VariableDiscrete) {
							Message msg = (Message) getTableTree().getData();
							((MessageSelection) vueListe.currentSelection).setMessageId(msg.getMessageId());
							ActivatorData.getInstance().setSelectedMsg(msg);
							vueListe.upSelection(true);
						}
					}
				}
			}

			public void mouseDown(MouseEvent e) {
				ActivatorVueListe.getDefault().setVueFocus(ActivatorVueListe.FOCUS_VUE_DETAILLEE);
				handleMouseDownEvent(table, e);
			}

			public void mouseUp(MouseEvent e) {
				if (e.button == 3) {
					getTableTree().getTable().getMenu().setEnabled(true);
					getTableTree().getTable().getMenu().setVisible(true);
				}
			}
		});
	}
					
	private boolean expandVariable(DescripteurVariable descr) {
		boolean structureExpanded = false;
		AVariableComposant varToExpand = null;
		
		try {
			VariablesSelect objectToExpand = VueListe.varSelectionnee;
			if (objectToExpand != null && objectToExpand.getVars() != null && objectToExpand.getVars().size() != 0) {
				int taille = objectToExpand.getVars().size();
				Object vv = objectToExpand.getVars().get(taille - 1);
				
				if (vv instanceof AVariableComposant) {
					varToExpand = (AVariableComposant) vv;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}

		Message msg = (Message) getTableTree().getData();
		List<VariableAnalogique> varsAna = msg.getVariablesAnalogique();
		
		for (VariableAnalogique var : varsAna) {
			if (var.equals(varToExpand)) {
				setSelection(new StructuredSelection(var));
				getTableTree().getTable().showSelection();
				return false;
			}
		}

		List<VariableDiscrete> varsDis = msg.getVariablesDiscrete();
		
		for (VariableDiscrete var : varsDis) {
			if (var.equals(varToExpand)) {
				setSelection(new StructuredSelection(var));
				getTableTree().getTable().showSelection();
				return false;
			}
		}
		
		ArrayList<VariableComplexe> list = new ArrayList<VariableComplexe>();
		if (msg.getVariablesComplexe() != null) {
			list.addAll(msg.getVariablesComplexe());
			
			for (VariableComplexe var : list) {
				if (var.equals(varToExpand)) {
					expandVariableParent(var, 1, true, false);
					return true;
				}
				
				int nbvar = var.getVariableCount();
				
				for (int i = 0; i < nbvar; i++) {
					if (var.getEnfant(i).equals(varToExpand)) {
						expandVariableParent(var.getEnfant(i), 2, true, false);
						return true;
					}
				}
			}
		}

		List<StructureDynamique> structs = msg.getStructuresDynamique();
		List<TableauDynamique> tabs = msg.getTableauxDynamique();
		List<ChaineDynamique> chaines = msg.getChainesDynamique();
		List<VariableDynamique> varsDyn = new ArrayList<VariableDynamique>();
		varsDyn.addAll(structs);
		varsDyn.addAll(tabs);
		varsDyn.addAll(chaines);
		
		for (VariableDynamique v : varsDyn) {
			if (expandStructDynamique(descr, v, 1, varToExpand)) {
				return true;
			}
		}
		
		return structureExpanded;
	}

	/**
	 * Retourne true si on a expandé une variable
	 * 
	 * @param arg0 descripteur à expander
	 * @param arg1 structure où on doit retrouver le descripteur
	 * @return boolean
	 */
	private boolean expandStructDynamique(DescripteurVariable descr, VariableDynamique struct, int niveau, AVariableComposant varToExpand) {
		if (struct.equals(varToExpand)) { // Si c'est la structure elle-même (cas chaine dynamique)
			expandVariableParent(struct, 0, false, true);
			return true;
		} else if (struct.getVariableEntete().equals(varToExpand)) { // Si c'est la variable entete
			expandVariableParent(struct, niveau + 1, false, false);
			return true;
		} else if (struct.getEnfants() != null && struct.getEnfants().length != 0) { // Si c'est dans la table de sous-variables
			for (AVariableComposant var : struct.getEnfants()) {
				if (var.equals(varToExpand)) { // Si la variable correspond
					expandVariableParent(var, niveau, false, false);
					return true;					
				} else if (var instanceof StructureDynamique || var instanceof Paquets || var instanceof TableauDynamique) { // Si c'est une structure dynamique, algo récursif
					boolean ret = expandStructDynamique(descr, ((VariableDynamique) var), niveau + 1, varToExpand);
					
					if (ret) {
						return true;
					}
				}
				else if (var instanceof VariableComplexe){
					int nbChildren = ((VariableComplexe)var).getVariableCount();
					
					for (int i = 0 ; i < nbChildren ; i++) {
						if (((VariableComplexe)var).getEnfant(i).equals(varToExpand)) { // Si la variable correspond
							expandVariableParent(varToExpand, niveau + 1, false, false);
							return true;	
						}
					}
				}
			}
		}
		
		return false;
	}

	private void expandVariableParent(AVariableComposant var, int niveau, boolean allLevels, boolean chaineDynX_TEXT) {
		AVariableComposant varet = var;
		int puissance = 0;
		
		for (int i = 0; i < niveau; i++) {
			if (varet.getParent() != null) {
				varet = varet.getParent();
				puissance++;
			}
		}

		if (var instanceof ChaineDynamique && (!chaineDynX_TEXT)) {
			var = ((ChaineDynamique) var).getVariableEntete();
		}

		if (allLevels) {
			puissance = AbstractTreeViewer.ALL_LEVELS;
		}

		expandToLevel(varet, puissance);
		setSelection(new StructuredSelection(var));
		getTableTree().getTable().showSelection();
	}
	
	public void setInputMessage(Message message) {
		setInput(message);
	}
		
	public void refreshTableData(DescripteurVariable descr, Message message) {
		boolean structExpanded = false;
		TableTree treeTable = getTableTree();
		
		refresh(true);
					
		if (descr != null) {
			structExpanded = expandVariable(descr);
		}
		
		modifierConfigurationColonnes();
		treeTable.redraw();
		
		if (!structExpanded) {
			chargerPresentationEv(message);
		}
				
		if (message != null) {
			if (BridageFormats.getInstance().getFormatFichierOuvert("") == FormatSAM.JRU) {
				expandToLevel(message, 0);
			} else {
				expandToLevel(message, 2);
			}
		}
	}
	
	public void chargerPresentationEv(Message msg) {
		if (msg.getEvenement() != null && InstancePresentationVueDetaillee.getInstance().getPresentation() != null && InstancePresentationVueDetaillee.getInstance().getPresentation().getCodeEv() == msg.getEvenement().getM_ADescripteurComposant().getM_AIdentificateurComposant().getCode()) {
			VueDetaillee vd = InstancePresentationVueDetaillee.getInstance().getPresentation();
			
			if (vd.getExpandedElements() != null) {
				int nbToExpand = vd.getExpandedElements().size();
				
				for (int i = 0; i < nbToExpand; i++) {
					ArrayList<Integer> listInt = vd.getExpandedElements().get(i);
					AVariableComposant comp = msg.getVariable(listInt.get(listInt.size() - 1));
					
					if (listInt.size() > 1 && comp instanceof VariableComposite) {
						VariableComposite varComp = (VariableComposite) comp;
						for (int j = listInt.size() - 2; j >= 0; j--) {
							AVariableComposant[] tabAVar = varComp.getEnfants();
							for (int k = 0; k < tabAVar.length; k++) {
								if (tabAVar[k].getDescriptor().getM_AIdentificateurComposant().getCode() == listInt.get(j)) {
									comp = tabAVar[k];
									break;
								}
							}
						}
					}
					
					expandToLevel(comp, 1);
				}
			}
			
			if (getTableTree().getTable().getVerticalBar() != null) {
				getTableTree().getTable().setTopIndex(vd.getPosVScroll());
				getTableTree().getTable().getVerticalBar().setSelection(vd.getPosVScroll());
			}
		} else {
			InstancePresentationVueDetaillee.getInstance().setPresentation(null);
		}
	}
	
	public void modifierConfigurationColonnes() {
		TableColumn[] columns = getTableTree().getTable().getColumns();
		ConfigurationColonne configColonne = null;
		
		for (TableColumn column : columns) {
			configColonne = gestionnaireColonne.getConfigurationColonneByNom(column.getText());
			
			if (configColonne != null) {
				if (!configColonne.isAffiche()) {
					column.setWidth(0);
				} else {
					if (configColonne.getLargeur() > 0) {
						column.setWidth(configColonne.getLargeur());
					} else {
						column.pack();
					}
				}
			} else if (!column.getText().isEmpty()) { // For the hidden column
				column.pack();
			}
		}
	}
	
	public void setMenu(Listener menuSelListener) {
		Menu popupMenu = new Menu(getTableTree());

		MenuItem item = new MenuItem(popupMenu, SWT.NONE);
		item.setText(com.faiveley.samng.vueliste.ihm.actions.table.Messages.getString("ConfigListVueAction.2"));
		item.setData(TypeMenuOptions.EXPAND);
		item.addListener(SWT.Selection, menuSelListener);

		item = new MenuItem(popupMenu, SWT.NONE);
		item.setText(com.faiveley.samng.vueliste.ihm.actions.table.Messages.getString("ConfigListVueAction.3"));
		item.setData(TypeMenuOptions.COLLAPSE);
		item.addListener(SWT.Selection, menuSelListener);

		item = new MenuItem(popupMenu, SWT.NONE);
		item.setText(com.faiveley.samng.vueliste.ihm.actions.table.Messages.getString("ConfigListVueAction.4"));
		item.setData(TypeMenuOptions.GESTIONNAIRE_COLONNES);
		item.addListener(SWT.Selection, menuSelListener);
		
		getTableTree().getTable().setMenu(popupMenu);
	}

	public String[] getColumnNames() {
		return this.columnNames;
	}
}
