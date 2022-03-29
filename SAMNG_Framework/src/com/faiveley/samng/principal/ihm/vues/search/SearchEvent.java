package com.faiveley.samng.principal.ihm.vues.search;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.calcul.TailleBouton;
import com.faiveley.samng.principal.ihm.listeners.ISearchEventListener;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurEvenement;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.Evenement;
import com.faiveley.samng.principal.sm.data.enregistrement.Util;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.ParcoursComposite;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;

/**
 * Dialog for search by event
 * @author meggy
 *
 */
public class SearchEvent extends ASearchVariableDialog {

	private Button btnPrecedent;

	private Button btnSuivant;

	private Button btnCancel;
	
	LinkedHashMap<String, DescripteurEvenement> values = new LinkedHashMap<String, DescripteurEvenement>();

	/**
	 * Constructor
	 * 
	 * @param parent
	 */
	public SearchEvent(Shell parent) {
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

	}

	/**
	 * Constructor
	 * 
	 * @param parent
	 * @param style
	 */
	public SearchEvent(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Create the controls for the current dialog
	 */
	@Override
	protected void createControls(final Shell parentShell) {
		
		this.parent = parentShell;
		
		// combo "Name"
		Label label = new Label(this.parent, SWT.NONE);
		label.setBounds(new Rectangle(20, 25, 94, 13));
		label.setText(NOM);
		label.setToolTipText(NOM);
		this.comboVar = new Combo(this.parent, SWT.NONE);
		this.comboVar.setVisible(false);
		this.comboVar.setBounds(new Rectangle(20, 45, 250, 21));
		fillCombo();

		this.comboVar.addSelectionListener(new ComboSelection());

		// permet de remettre la selection sur <Recherche> si la varaible avait
		// �t� trouv�e par recherche
		this.comboVar.addListener(SWT.MouseDown, new Listener() {

			public void handleEvent(Event arg0) {
				if (lastSearchVarCombo) {
					String[] items = SearchEvent.this.comboVar
							.getItems();
					for (int i = 0; i < items.length; i++) {
						if (items[i].equals(ADV_SEARCH)) {
							SearchEvent.this.comboVar.select(i);
						}
					}
				}
			}
		});
		
		////////////////////////////////////////////////////
		if(SearchEvent.this.values!=null)
		setSelectableValues(SearchEvent.this.values
				.keySet().toArray(
						new String[SearchEvent.this.values
								.size()]));

		setAppelant(this.getClass().getName());
		setTypeRecherche("Event"); //$NON-NLS-1$
		
		////////////////////////////////////////////////////////////////
		
		
		this.variableName = new Text(this.parent, SWT.BORDER);
		this.variableName.setBounds(20, 45, 250, 21);
		this.variableName.setText(""); //$NON-NLS-1$
		this.variableName.setEditable(false);
		
		this.selectTextLabel = new Label(this.parent, SWT.NONE);
		this.selectTextLabel.setBounds(20, 80, 250, 12);

		this.selectText = new Text(this.parent, SWT.BORDER);
		this.selectText.setBounds(20, 95, 250, 21);
		this.selectText.setText(""); //$NON-NLS-1$
		this.selectText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				filterText = selectText.getText();
				List<String> filteredValues = filterItems(filterText);
				initTableValues(filteredValues);
				ActivatorData.getInstance().getPoolDonneesVues().put(
						SearchEvent.this.getAppelant() + SearchEvent.this.getTypeRecherche(),
						filterText);
			}
		});
		
		// recup de la derni�re recherche
		try {
			if (ActivatorData.getInstance().getPoolDonneesVues().get(
					this.getAppelant() + this.getTypeRecherche()) != null)
				this.selectText.setText((String) ActivatorData.getInstance().getPoolDonneesVues().get(
								this.getAppelant() + this.getTypeRecherche()));
		} catch (RuntimeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		this.selectText.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				itemsTable.deselectAll();
			}

			public void focusLost(FocusEvent e) {
			}
		});

		GridData itemsTableLData = new GridData();
		itemsTableLData.verticalAlignment = GridData.FILL;
		itemsTableLData.horizontalAlignment = GridData.FILL;
		itemsTableLData.grabExcessVerticalSpace = true;
		itemsTableLData.grabExcessHorizontalSpace = true;

		this.itemsTable = new Table(this.parent, SWT.BORDER
				| SWT.SINGLE | SWT.FULL_SELECTION);
		this.itemsTable.setLayoutData(itemsTableLData);
		this.itemsTable.setHeaderVisible(true);
		this.itemsTable.setBounds(20, 120, 250, 300);
		
		this.tableColumn = new TableColumn(itemsTable, SWT.NONE);
//		this.tableColumn.setText(com.faiveley.samng.principal.ihm.vues.search.dialogs.Messages.getString("RechercheDialog.5")); //$NON-NLS-1$
//		this.tableColumn.setToolTipText((com.faiveley.samng.principal.ihm.vues.search.dialogs.Messages
//				.getString("RechercheDialog.5")));
		this.tableColumn.setResizable(true);
		this.tableColumn.setAlignment(SWT.CENTER);
		this.tableColumn.setWidth(250);
		
		initTableValues(this.selectableValuesList); // try an initialization
		// : add a listener for selection to disable the OK button

		this.itemsTable.addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {
				
			}

			public void mouseDown(MouseEvent e) {
				if (e.button == 1) { // left button click
					SearchEvent.this.selectValue();
				}
			}

			public void mouseUp(MouseEvent e) {
			}
		});

		if (this.filterText != null) {
			// this setText will also fire a modification event
			selectText.setText(this.filterText);
			selectText.setToolTipText((this.filterText));
		}
		
//		this.parent.setBounds(0, 0, 600, 400);
		// button "Previous"
		btnPrecedent = new Button(this.parent, SWT.NONE);
		int widthprec = TailleBouton.CalculTailleBouton(Messages.getString(
				"SearchVariable.0").length());
		btnPrecedent.setBounds(new Rectangle(300, 100, widthprec, 23)); // (50,
																		// 100,
																		// 80,
																		// 23)
		btnPrecedent.setText(Messages.getString("SearchVariable.0")); //$NON-NLS-1$
		btnPrecedent.setToolTipText((Messages.getString("SearchVariable.0")));
		btnPrecedent.setEnabled(false);
		btnPrecedent.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		String eventName = selectedValue;
        		IViewReference[] vr = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
				for (IViewReference v : vr) {
					IViewPart view = v.getView(false);
					if (view instanceof ISearchEventListener) {
						((ISearchEventListener) view).onSearchEvent(eventName, false);
					}
				}
				ActivatorData.getInstance().getPoolDonneesVues().put(
						SearchEvent.this.getAppelant() + SearchEvent.this.getTypeRecherche()+ "select",
						SearchEvent.this.itemsTable.getSelectionIndex());
        	}
        });

		// button "Next"
		btnSuivant = new Button(this.parent, SWT.NONE);
		int widthsuiv = TailleBouton.CalculTailleBouton(Messages.getString(
				"SearchVariable.1").length());
		btnSuivant.setBounds(new Rectangle(400, 100, widthsuiv, 23));
		btnSuivant.setText(Messages.getString("SearchVariable.1")); //$NON-NLS-1$
		btnSuivant.setToolTipText((Messages.getString("SearchVariable.1")));
		btnSuivant.setEnabled(false);
		btnSuivant.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		//comboEvents.getItem(comboEvents.getSelectionIndex()).
        		String eventName = selectedValue;
        		IViewReference[] vr = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
				for (IViewReference v : vr) {
					IViewPart view = v.getView(false);
					if (view instanceof ISearchEventListener) {
						((ISearchEventListener) view).onSearchEvent(eventName, true);
					}
				}
				ActivatorData.getInstance().getPoolDonneesVues().put(
						SearchEvent.this.getAppelant() + SearchEvent.this.getTypeRecherche()+ "select",
						SearchEvent.this.itemsTable.getSelectionIndex());
        	}
        });

		// button "Cancel"
		btnCancel = new Button(this.parent, SWT.NONE);
		int widthannul = TailleBouton.CalculTailleBouton(Messages.getString(
				"SearchVariable.2").length());
		btnCancel.setBounds(new Rectangle(500, 100, widthannul, 23));
		btnCancel.setText(Messages.getString("SearchVariable.2")); //$NON-NLS-1$
		btnCancel.setToolTipText((Messages.getString("SearchVariable.2")));
		btnCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				SearchEvent.this.parent.close();
			}
		});	
		
		try {
			if (ActivatorData.getInstance().getPoolDonneesVues().get(this.getAppelant()
					+ this.getTypeRecherche() + "select") != null){
				
				int indexToSelect=(Integer)ActivatorData.getInstance().getPoolDonneesVues().get(this.getAppelant()
						+ this.getTypeRecherche() + "select");
						
				this.itemsTable.setSelection(indexToSelect);	
				this.itemsTable.forceFocus();				
				
				int selIdx = itemsTable.getSelectionIndex();
				if (selIdx < 0 || selIdx >= itemsTable.getItemCount())
					return;
				TableItem tblItem = itemsTable.getItem(selIdx);
				selectedValue = tblItem.getText();
				variableName.setText(selectedValue);
				

				activerBoutonsRecherche();
								
				////////////////////
				
				if (SearchEvent.this.filterText == null)
					setFilterText("");
				ActivatorData.getInstance().getPoolDonneesVues().put(
						SearchEvent.this.getAppelant()
								+ SearchEvent.this.getTypeRecherche(),
								SearchEvent.this.filterText);
				
				ActivatorData.getInstance().getPoolDonneesVues().put(SearchEvent.this.getAppelant()
						+ SearchEvent.this.getTypeRecherche() + "select",selIdx);
			}
		} catch (RuntimeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		this.selectText.forceFocus();
		this.selectText.setFocus();
		
	}

	private List<String> filterItems(String subStr) {
		ArrayList<String> retList = new ArrayList<String>();
		if (subStr == null || subStr.trim().length() == 0)
			return this.selectableValuesList;
		for (String str : this.selectableValuesList) {
			// meggy : modified to be case insensitive
			if (str.toLowerCase().contains(subStr.toLowerCase()))
				retList.add(str);

		}
		retList.trimToSize();
		return retList;
	}
	
	/**
	 * Gets the size of the current dialog
	 */
	protected Point getSize() {
		return new Point(600, 200);
	}

	/**
	 * Fills the combo of the variables. Adds also the virtual variables if
	 * there is any view that implements ISearchVariableVirtuele
	 */
	protected void fillCombo() {
		
		ParcoursComposite p = FabriqueParcours.getInstance().getParcours();
		if (p == null) {
			return;
		}
		Enregistrement enrg = p.getData().getEnregistrement();
		if (enrg != null) {
			if (this.values == null) {
				this.values = new LinkedHashMap<String, DescripteurEvenement>();
			} else {
				this.values.clear();
			}
			
			Set<Evenement> fpEvenements = Util.getInstance().getMessagesEvents();
			if(fpEvenements!=null){
			for (Evenement event : fpEvenements) {
				if (event != null) {
					this.values.put(event.getNomUtilisateur()
							.getNomUtilisateur(
									Activator.getDefault().getCurrentLanguage()),(DescripteurEvenement)event.getM_ADescripteurComposant());
				}
			}
			}
		}
		if (this.values == null) {
			this.values = new LinkedHashMap<String, DescripteurEvenement>();
		}
	}

//	/**
//	 * Button selection listener used by "Next" and "Previous" buttons
//	 * 
//	 * @author meggy
//	 * 
//	 */
//	class ButtonSelection extends SelectionAdapter {
//		private boolean isNext = false;
//
//		/**
//		 * Sets the directions of search
//		 * 
//		 * @param next
//		 */
//		public void setNext(boolean next) {
//			this.isNext = next;
//		}
//
//		/**
//		 * If the button is selected
//		 */
//		public void widgetSelected(SelectionEvent e) {
//			DescripteurEvenement descrVar = null;
//
//			// get variable
//			String varName = SearchEvent.this.selectedValue;
//			if ((!varName.equals(NO_VARIABLE)) && (!varName.equals(ADV_SEARCH))) {
//				descrVar = SearchEvent.this.values.get(varName);
//			}
//
//			// get the opened views that implements ISearchVariableListener
//			// and notify them
//			// if (value != null) {
//			IViewReference[] vr = PlatformUI.getWorkbench()
//					.getActiveWorkbenchWindow().getActivePage()
//					.getViewReferences();
//			
//			for (IViewReference v : vr) {
//				IViewPart view = v.getView(false);
//				// search for the interface "SearchEvent"
//				if (view instanceof ISearchVariableListener) {
//					try {
//						((ISearchVariableListener) view).onSearch(descrVar, value, op, this.isNext);
//					} catch (Exception ex) {
//						ex.printStackTrace();
//					}
//				}
//			}
//		}
//	}

	@Override
	public void desactiverBoutonsRecherche() {
		this.btnPrecedent.setEnabled(false);
		this.btnSuivant.setEnabled(false);

	}

	@Override
	public void activerBoutonsRecherche() {
		this.btnPrecedent.setEnabled(true);
		this.btnSuivant.setEnabled(true);

	}

} // @jve:decl-index=0:visual-constraint="10,10,540,208"

