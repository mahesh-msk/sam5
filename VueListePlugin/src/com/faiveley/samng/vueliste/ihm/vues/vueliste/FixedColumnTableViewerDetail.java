package com.faiveley.samng.vueliste.ihm.vues.vueliste;

/*******************************************************************************
 * Copyright (c) 2006 BestSolution Systemhaus GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of:
 * 1. The Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 2. LGPL v2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 3. MPL v1.1 which accompanies this distribution, and is available at
 * http://www.mozilla.org/MPL/MPL-1.1.html
 *
 * Contributors:
 *     Tom Schind <tom.schindl@bestsolution.at> - Initial API and implementation
 *******************************************************************************/

import java.util.ArrayList;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.faiveley.kvbdecoder.decoder.KVBDecoderResult;
import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.principal.ihm.vues.InstancePresentationVueDetaillee;
import com.faiveley.samng.principal.ihm.vues.Row;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.Evenement;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.enregistrement.atess.AtessMessage;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.ParseurParcoursAtess;
import com.faiveley.samng.vueliste.ihm.ActivatorVueListe;
import com.faiveley.samng.vueliste.ihm.actions.table.CollapseAction;
import com.faiveley.samng.vueliste.ihm.actions.table.ExpandAction;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.configuration.GestionnaireVueDetaillee;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.configuration.action.ConfigListVueDetailleeAction;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.kvb.TableTreeInformationPointDetailViewer;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.kvb.TableTreeKVBDetailViewer;

@SuppressWarnings("deprecation")
@Deprecated
public class FixedColumnTableViewerDetail extends FixedColumnTableVueListe {
	private static final String ONGLET_BRUT_NON_KVB_LABEL = Messages.getString("FixedColumnTableViewerDetailTab.0");
	private static final String ONGLET_BRUT_KVB_LABEL = Messages.getString("FixedColumnTableViewerDetailTab.1");
	private static final String ONGLET_DECODE_KVB_LABEL = Messages.getString("FixedColumnTableViewerDetailTab.2");
	
	private int style;
	
	private TableTreeDetailViewer tableTreeDetailViewer;
	
	private Composite kvbContainer;
	private TableTreeKVBDetailViewer tableTreeKVBDetailViewer;
	private TableTreeInformationPointDetailViewer tableTreeInformationPointDetailViewer;	
	
	private TabFolder tabFolder;
	private TabItem ongletKVB;
	private TabItem ongletBrut;
	
	private VueListe vueListe;
	
	private GridData layoutData;
	
	private GestionnaireVueDetaillee gestionnaireColonne;
	
	public FixedColumnTableViewerDetail(Composite parent, int style, VueListe vueListe) {
		super(parent, SWT.NONE);
		this.vueListe = vueListe;
		this.style = style;
		
		FillLayout fillLayout = new FillLayout();
		setLayout(fillLayout);
		setLayoutData(new GridData(GridData.FILL_BOTH));

		final SashForm sashForm = new SashForm(this, SWT.HORIZONTAL | SWT.BORDER);

		Composite vuePrincipale = new Composite(sashForm, this.style);
		vuePrincipale.setLayout(new FormLayout());

		gestionnaireColonne = ActivatorVueListe.getDefault().getConfigurationVueDetaillee();

		this.fixedTableViewer = new TableViewer(vuePrincipale, this.style | SWT.VIRTUAL);
		this.fixedTableViewer.setUseHashlookup(true);
		this.fixedTable = this.fixedTableViewer.getTable();
		this.fixedTable.getHorizontalBar().setEnabled(true);
		this.fixedTable.getHorizontalBar().setVisible(true);
		
		this.tabFolder = new TabFolder(sashForm, SWT.NONE);	
				
		this.tableTreeDetailViewer = new TableTreeDetailViewer(tabFolder, this.style | SWT.VIRTUAL, gestionnaireColonne);
		this.tableTreeDetailViewer.setUseHashlookup(true);
		this.tableTreeDetailViewer.setAutoExpandLevel(1);
		
		TableTree treeTable = this.tableTreeDetailViewer.getTableTree();
		treeTable.getTable().getHorizontalBar().setEnabled(true);
		treeTable.getTable().getHorizontalBar().setVisible(true);

		this.tableTreeDetailViewer.createContents(vueListe);
		this.tableTreeDetailViewer.modifierConfigurationColonnes();
		
		this.ongletBrut = new TabItem(tabFolder, SWT.NONE);
		this.ongletBrut.setText(ONGLET_BRUT_NON_KVB_LABEL);
		this.ongletBrut.setControl(treeTable);
				
		FormData data = new FormData();
		data.top = new FormAttachment(0);
		data.left = new FormAttachment(0);
		data.right = new FormAttachment(100);
		data.bottom = new FormAttachment(100);
		fixedTable.setLayoutData(data);

		setBackground(fixedTable.getBackground());
		hookTables();
		
		this.tableTreeDetailViewer.setMenu(buildMenuListener(false));
				
		sashForm.addListener(SWT.Resize, new Listener() {
			int init = 0;
			
	        @Override
	        public void handleEvent(Event e) {	        	
	        	if (init < 2) {
	        		init++;
	        		
	        		sashForm.setWeights(new int[] {50, 50});
	        		
	        		int largeurColonnesVueDetaille = gestionnaireColonne.getLargeurTotaleColonnes();
	        		int pourcentVueDetaillee = ((100 * largeurColonnesVueDetaille) / (sashForm.getChildren()[0].getSize().x + sashForm.getChildren()[1].getSize().x)) + 2;
	        		
	        		if (pourcentVueDetaillee >= 0 && pourcentVueDetaillee <= 100) {
	        			sashForm.setWeights(new int[] {100 - pourcentVueDetaillee, pourcentVueDetaillee});
	        		}
	        	}
	        }
	    });
	}
	
	public void enregistrerPresentationEvenement() {
		ArrayList<ArrayList<Integer>> codeVars = new ArrayList<ArrayList<Integer>>();
		Object[] vars = this.tableTreeDetailViewer.getExpandedElements();
		
		for (Object composant : vars) {
			ArrayList<Integer> ar = new ArrayList<Integer>();
			ar.add(((AVariableComposant) composant).getDescriptor().getM_AIdentificateurComposant().getCode());
			AVariableComposant comp = (AVariableComposant) composant;
			
			while (comp.getParent() != null) {
				ar.add(comp.getParent().getDescriptor().getM_AIdentificateurComposant().getCode());
				comp = comp.getParent();
			}
			
			codeVars.add(ar);
		}
		
		Message m = ((Message) this.tableTreeDetailViewer.getTableTree().getData());
		int code = m.getEvenement().getM_ADescripteurComposant().getM_AIdentificateurComposant().getCode();
		int posVScroll = this.tableTreeDetailViewer.getTableTree().getTable().getVerticalBar() == null ? 0: this.tableTreeDetailViewer.getTableTree().getTable().getVerticalBar().getSelection();
		
		InstancePresentationVueDetaillee.getInstance().enregistrerPresentation(code, posVScroll, codeVars);
	}
	
	@Override
	public void refreshTable(DescripteurVariable descr) {
		Evenement fixedTableEvenement = ((Message) ((Row) this.fixedTable.getSelection()[0].getData()).getData()).getEvenement();
		refreshOnglets(fixedTableEvenement);
		
		Message treeTableMessage = (Message) this.tableTreeDetailViewer.getTableTree().getData();
		if (this.tableTreeDetailViewer.getTableTree().getData() != null && fixedTableEvenement != null && treeTableMessage.getEvenement() != null) {
			int fixedTableMessageCode = fixedTableEvenement.getM_ADescripteurComposant().getM_AIdentificateurComposant().getCode();
			int treeTableMessageCode = treeTableMessage.getEvenement().getM_ADescripteurComposant().getM_AIdentificateurComposant().getCode();
			
			if (fixedTableMessageCode == treeTableMessageCode) {
				enregistrerPresentationEvenement();
			}
		}
		
		if (this.fixedTable.getSelection().length == 1) {
			Message msg = getSelectionCourante();
			
			this.tableTreeDetailViewer.setInputMessage(msg);
			this.tableTreeDetailViewer.refreshTableData(descr, msg);
			
			if (ParseurParcoursAtess.getInstance().isKVBLoaded() && isKVBEnabled()) {
				AtessMessage atessMessage = (AtessMessage) msg;
				
				this.tableTreeKVBDetailViewer.setInputMessage(atessMessage);
				this.tableTreeKVBDetailViewer.refreshTableData(msg);

				KVBDecoderResult decodedEvent = ((AtessMessage) msg).getDecodedEvent();
				
				if (decodedEvent != null) {
					this.tableTreeInformationPointDetailViewer.setEvent(decodedEvent.getEvent());
					this.tableTreeInformationPointDetailViewer.setInformationPoint(0);	
				}
			}
		}
	}
	
	public void refreshOnglets(Evenement fixedTableEvenement) {
		if (ParseurParcoursAtess.getInstance().isKVBLoaded() && fixedTableEvenement.isKVBEvent() && !isKVBEnabled()) {			
			this.kvbContainer = new Composite(this.tabFolder, SWT.NONE);
			this.kvbContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			this.kvbContainer.setLayout(new GridLayout(1, false));
			
			this.ongletKVB = new TabItem(this.tabFolder, SWT.NONE);
			this.ongletKVB.setText(ONGLET_DECODE_KVB_LABEL);
			this.ongletKVB.setControl(this.kvbContainer);
						
			this.tableTreeKVBDetailViewer = new TableTreeKVBDetailViewer(this, this.kvbContainer, SWT.NONE, gestionnaireColonne);
			this.tableTreeKVBDetailViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			this.tableTreeKVBDetailViewer.setUseHashlookup(true);
			this.tableTreeKVBDetailViewer.setMenu(buildMenuListener(true));
			
			TableTree treeTableKVB = this.tableTreeKVBDetailViewer.getTableTree();
			treeTableKVB.getTable().getHorizontalBar().setEnabled(true);
			treeTableKVB.getTable().getHorizontalBar().setVisible(true);
			
			this.tableTreeKVBDetailViewer.createContents(vueListe);
			this.tableTreeKVBDetailViewer.modifierConfigurationColonnes();
		
			this.ongletBrut.setText(ONGLET_BRUT_KVB_LABEL);	
			
			this.tableTreeInformationPointDetailViewer = new TableTreeInformationPointDetailViewer(this.kvbContainer, SWT.NONE, this, fixedTableEvenement);
			this.layoutData = new GridData(SWT.FILL, SWT.NONE, true, false);
			
			this.tabFolder.setSelection(1);
		} else if (fixedTableEvenement != null && !fixedTableEvenement.isKVBEvent() && isKVBEnabled()) {
			this.tableTreeKVBDetailViewer.getTableTree().dispose();
			this.tableTreeKVBDetailViewer = null;
			
			this.tableTreeInformationPointDetailViewer.getTableTree().dispose();
			this.tableTreeInformationPointDetailViewer = null;
			
			this.kvbContainer.dispose();
			this.kvbContainer = null;
			
			this.ongletKVB.dispose();
			this.ongletBrut.setText(ONGLET_BRUT_NON_KVB_LABEL);
			
			this.tabFolder.setSelection(0);
		}
			
		if (this.tableTreeInformationPointDetailViewer != null) {
			if (!this.tableTreeInformationPointDetailViewer.isExpanded()) {
				this.layoutData.heightHint = 0;
			} else {
				this.layoutData.heightHint = 6 * this.tableTreeInformationPointDetailViewer.getTableTree().getItemHeight() 
						+ this.tableTreeInformationPointDetailViewer.getTableTree().getTable().getHeaderHeight() 
						+ 6 * this.tableTreeInformationPointDetailViewer.getTableTree().getTable().getGridLineWidth();
			}
			
			this.tableTreeInformationPointDetailViewer.getControl().setLayoutData(this.layoutData);
			this.tableTreeInformationPointDetailViewer.getControl().getParent().layout();
		}
	}
	
	private Listener buildMenuListener(final boolean isKVBTab) {
		return new Listener() {
			public void handleEvent(Event e) {
				MenuItem menuItem = (MenuItem) e.widget;
				TypeMenuOptions menuId = (TypeMenuOptions) menuItem.getData();
				
				switch (menuId) {
					case EXPAND:
						new ExpandAction(ICommandIds.CMD_EXP, Messages.getString("ConfigListVueAction.2"), ActivatorVueListe.getImageDescriptor("/icons/toolBar/expand2.png"), FixedColumnTableViewerDetail.this).run();
						break;
					case COLLAPSE:
						new CollapseAction(ICommandIds.CMD_COLL, Messages.getString("ConfigListVueAction.3"), ActivatorVueListe.getImageDescriptor("/icons/toolBar/collapse.png"), FixedColumnTableViewerDetail.this).run();
						break;
					case GESTIONNAIRE_COLONNES:
						new ConfigListVueDetailleeAction(FixedColumnTableViewerDetail.this, isKVBTab).run();
						break;
				}
			}
		};
	}
	
	public TableTreeDetailViewer getTableTreeDetailViewer() {
		return tableTreeDetailViewer;
	}
	
	public TableTreeKVBDetailViewer getTableTreeKVBDetailViewer() {
		return tableTreeKVBDetailViewer;
	}
	
	public TableTreeInformationPointDetailViewer getTableTreeInformationPointDetailViewer() {
		return tableTreeInformationPointDetailViewer;
	}

	@Deprecated
	protected enum TypeMenuOptions {
		COLLAPSE, EXPAND, GESTIONNAIRE_COLONNES
	}

	public Message getSelectionCourante() {
		return (Message) ((Row) this.fixedTable.getSelection()[0].getData()).getData();
	}
		
	public boolean isKVBEnabled() {		
		return this.tabFolder.getTabList().length == 2;
	}
	
	public int getSelectedTab() {
		return tabFolder.getSelectionIndex();
	}
}
