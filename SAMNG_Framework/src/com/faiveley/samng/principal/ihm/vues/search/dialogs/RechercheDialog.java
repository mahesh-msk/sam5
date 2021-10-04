package com.faiveley.samng.principal.ihm.vues.search.dialogs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
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
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.Evenement;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.Util;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TableLangueNomUtilisateur;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.VitesseLimiteKVBService;

/**
 * 
 * @author Cosmin Udroiu
 * 
 */
public class RechercheDialog extends Dialog {

	private boolean multi = false;

	private Button cancelBtn;

	private Button okBtn;

	private Composite buttonsComposite;

	private Table itemsTable;

	private TableColumn tableColumn;

	private Label selectFromTableLabel;

	private Composite tableContainerComposite;

	private Text selectText;

	private Label selectTextLabel;

	private Composite textInputComposite;

	private Composite mainComposite;

	private String[] selectedValue;

	private String selectTextLabelText;

	private String tableInfoLabelText; //$NON-NLS-1$

	private List<String> selectableValuesList = new ArrayList<String>(0);

	private String filterText;

	private Set<String> messageVariablesNames = new HashSet<String>();

	private String appelant;

	private String typeRecherche;

	private boolean hideVolatilVariables = false;

	private boolean usesShortNames;

	public boolean isHideVolatilVariables() {
		return hideVolatilVariables;
	}

	public void setHideVolatilVariables(boolean hideVolatilVariables) {
		this.hideVolatilVariables = hideVolatilVariables;
	}

	public String getTypeRecherche() {
		return typeRecherche;
	}

	public void setTypeRecherche(String typeRecherche) {
		this.typeRecherche = typeRecherche;
	}

	public String getAppelant() {
		return appelant;
	}

	public void setAppelant(String appelant) {
		this.appelant = appelant;
	}

	/**
	 * SearchDialog constructor
	 * 
	 * @param parent
	 *            the parent
	 */
	public RechercheDialog(Shell parent) {
		// Pass the default styles here
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		selectTextLabelText = Messages.getString("RechercheDialog.0"); //$NON-NLS-1$
		tableInfoLabelText = Messages.getString("RechercheDialog.1"); //$NON-NLS-1$
	}

	/**
	 * SearchDialog constructor
	 * 
	 * @param parent
	 *            the parent
	 */
	public RechercheDialog(Shell parent, boolean multi) {
		this(parent);
		setMulti(multi);
		this.usesShortNames = false;
	}

	public RechercheDialog(Shell parent, boolean multi, boolean usesShortNames) {
		this(parent);
		setMulti(multi);
		this.usesShortNames = usesShortNames;
	}

	/**
	 * SearchDialog constructor
	 * 
	 * @param parent
	 *            the parent
	 * @param style
	 *            the style
	 */
	public RechercheDialog(Shell parent, int style) {
		// Let users override the default styles
		super(parent, style);
		setText(Messages.getString("RechercheDialog.2")); //$NON-NLS-1$
	}

	/**
	 * Opens the dialog and returns the input
	 * 
	 * @return String
	 */
	public String open() {
		// Create the dialog window
		final Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		shell.setToolTipText(getText());
		createContents(shell);
		// shell.pack();
		shell.setSize(420, 500);
		// position the dialog in the center of the parent shell
		Rectangle parentBounds = getParent().getBounds();
		Rectangle childBounds = shell.getBounds();
		int x = parentBounds.x + (parentBounds.width - childBounds.width) / 2;
		int y = parentBounds.y + (parentBounds.height - childBounds.height) / 2;
		shell.setLocation(x, y);
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		if (this.selectedValue == null) {
			return "";
		}

		// Return the entered value, or null
		try {
			return this.selectedValue[0];
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	public void setSelectableValues(String[] values) {
		this.selectableValuesList.clear();
		if (values == null)
			throw new IllegalArgumentException(Messages.getString("RechercheDialog.3")); //$NON-NLS-1$
		for (String val : values) {
			if (val != null) {
				//String displayVal = usesShortNames ? Util.getInstance().getNomCourtFromNomUtilisateur(val) : val;
				this.selectableValuesList.add(val);
			}
		}
		if (GestionnairePool.getInstance().getVariablesRenseignees() == null)
			initTableValues2(selectableValuesList);
		else
			initTableValues(selectableValuesList);
	}

	public void setSelectableValues(List<String> values) {
		this.selectableValuesList.clear();
		if (values == null)
			throw new IllegalArgumentException(Messages.getString("RechercheDialog.3")); //$NON-NLS-1$
		for (String val : values) {
			if (val != null) {
				String displayVal = usesShortNames ? Util.getInstance().getNomCourtFromNomUtilisateur(val) : val;
				this.selectableValuesList.add(displayVal);
			}
		}
		if (GestionnairePool.getInstance().getVariablesRenseignees() == null)
			initTableValues(this.selectableValuesList);
		else
			initTableValues2(this.selectableValuesList);
	}

	public void setInputLabelText(String text) {
		this.selectTextLabelText = text;
	}

	/**
	 * Sets the text for the filtering
	 * 
	 * @param filterText
	 */
	public void setFilterText(String filterText) {
		this.filterText = filterText;
	}

	/**
	 * Returns the filtering text (the text that is displayed in the select
	 * text)
	 * 
	 * @return
	 */
	public String getFilterText() {
		return this.filterText;
	}

	/**
	 * Creates the dialog's contents
	 * 
	 * @param shell
	 *            the dialog window
	 */
	private void createContents(final Shell shell) {
		shell.setLayout(new GridLayout());

		this.mainComposite = new Composite(shell, SWT.NONE);

		GridLayout composite1Layout = new GridLayout();
		composite1Layout.makeColumnsEqualWidth = true;

		GridData mainCompositeLData = new GridData();
		mainCompositeLData.grabExcessVerticalSpace = true;
		mainCompositeLData.verticalAlignment = GridData.FILL;
		mainCompositeLData.horizontalAlignment = GridData.FILL;
		mainCompositeLData.grabExcessHorizontalSpace = true;
		this.mainComposite.setLayoutData(mainCompositeLData);
		this.mainComposite.setLayout(composite1Layout);

		this.textInputComposite = new Composite(this.mainComposite, SWT.NONE);

		RowLayout textInputCompositeLayout = new RowLayout(org.eclipse.swt.SWT.HORIZONTAL);
		GridData textInputCompositeLData = new GridData();
		textInputCompositeLData.widthHint = 350;
		textInputCompositeLData.heightHint = 30;
		this.textInputComposite.setLayoutData(textInputCompositeLData);
		this.textInputComposite.setLayout(textInputCompositeLayout);

		this.selectTextLabel = new Label(this.textInputComposite, SWT.NONE);

		RowData selectTextLabelLData = new RowData();
		// selectTextLabelLData.width =
		// TailleBouton.CalculTailleBouton(this.selectTextLabelText.length());
		selectTextLabelLData.height = 18;
		this.selectTextLabel.setLayoutData(selectTextLabelLData);
		this.selectTextLabel.setText(this.selectTextLabelText);
		this.selectTextLabel.setToolTipText(this.selectTextLabelText);

		this.selectText = new Text(this.textInputComposite, SWT.BORDER);

		RowData selectTextLData = new RowData();
		selectTextLData.width = 80;// 150;
		selectTextLData.height = 14; // 18;
		this.selectText.setLayoutData(selectTextLData);
		this.selectText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				filterText = selectText.getText();
				List<String> filteredValues = filterItems(filterText);
				if (GestionnairePool.getInstance().getVariablesRenseignees() != null)
					initTableValues(filteredValues);
				else {
					initTableValues2(filteredValues);
				}
			}
		});

		// stockage de la derni�re recherche
		if (ActivatorData.getInstance().getPoolDonneesVues().get(this.getAppelant() + this.getTypeRecherche()) != null)
			this.selectText.setText((String) ActivatorData.getInstance().getPoolDonneesVues().get(this.getAppelant() + this.getTypeRecherche()));

		this.selectText.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				itemsTable.deselectAll();
			}

			public void focusLost(FocusEvent e) {
			}
		});

		this.tableContainerComposite = new Composite(mainComposite, SWT.NONE);

		GridLayout tableContainerCompositeLayout = new GridLayout();
		tableContainerCompositeLayout.makeColumnsEqualWidth = true;

		GridData tableContainerCompositeLData = new GridData();
		tableContainerCompositeLData.verticalAlignment = GridData.FILL;
		tableContainerCompositeLData.grabExcessHorizontalSpace = true;
		tableContainerCompositeLData.horizontalAlignment = GridData.FILL;
		tableContainerCompositeLData.grabExcessVerticalSpace = true;
		this.tableContainerComposite.setLayoutData(tableContainerCompositeLData);
		this.tableContainerComposite.setLayout(tableContainerCompositeLayout);

		this.selectFromTableLabel = new Label(this.tableContainerComposite, SWT.NONE);

		GridData selectFromTableLabelLData = new GridData();
		selectFromTableLabelLData.heightHint = 13;
		selectFromTableLabelLData.grabExcessHorizontalSpace = true;
		selectFromTableLabelLData.horizontalAlignment = GridData.FILL;
		selectFromTableLabelLData.verticalAlignment = GridData.BEGINNING;
		this.selectFromTableLabel.setLayoutData(selectFromTableLabelLData);
		this.selectFromTableLabel.setText(this.tableInfoLabelText);
		this.selectFromTableLabel.setToolTipText((this.tableInfoLabelText));

		GridData itemsTableLData = new GridData();
		itemsTableLData.verticalAlignment = GridData.FILL;
		itemsTableLData.horizontalAlignment = GridData.FILL;
		itemsTableLData.grabExcessVerticalSpace = true;
		itemsTableLData.grabExcessHorizontalSpace = true;

		if (!isMulti()) {
			this.itemsTable = new Table(tableContainerComposite, SWT.BORDER | SWT.SINGLE);
		} else {
			this.itemsTable = new Table(tableContainerComposite, SWT.BORDER | SWT.MULTI);
		}

		this.itemsTable.setLayoutData(itemsTableLData);
		this.itemsTable.setHeaderVisible(true);
		this.tableColumn = new TableColumn(itemsTable, SWT.NONE);
		this.tableColumn.setText(Messages.getString("RechercheDialog.5")); //$NON-NLS-1$
		this.tableColumn.setToolTipText((Messages.getString("RechercheDialog.5")));
		this.tableColumn.setResizable(true);
		this.tableColumn.setAlignment(SWT.CENTER);

		if (GestionnairePool.getInstance().getVariablesRenseignees() != null)
			initTableValues(this.selectableValuesList); // try an initialization
		else {
			this.selectableValuesList = getInitialValues();
			initTableValues2(this.selectableValuesList);
		}
		// : add a listener for selection to disable the OK button

		itemsTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selIdx = itemsTable.getSelectionIndex();
				if (selIdx < 0 || selIdx >= itemsTable.getItemCount()) {
					okBtn.setEnabled(false);
					return;
				}
				TableItem tblItem = itemsTable.getItem(selIdx);
				if (tblItem == null) {
					okBtn.setEnabled(false);
					return;
				}
				okBtn.setEnabled(true);
			}
		});

		this.itemsTable.addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {
				if (e.button == 1) { // left button double click
					// : this code is the same as the one in the
					// ok button handler
					int[] selIdx = itemsTable.getSelectionIndices();
					selectedValue = getSelectedVar(selIdx, itemsTable);
					if (RechercheDialog.this.filterText == null)
						setFilterText("");
					ActivatorData.getInstance().getPoolDonneesVues().put(RechercheDialog.this.getAppelant() + RechercheDialog.this.getTypeRecherche(), RechercheDialog.this.filterText);

					ActivatorData.getInstance().getPoolDonneesVues().put(RechercheDialog.this.getAppelant() + RechercheDialog.this.getTypeRecherche() + "select", selIdx);
					shell.close();
				}
			}

			public void mouseDown(MouseEvent e) {
			}

			public void mouseUp(MouseEvent e) {
			}
		});

		this.buttonsComposite = new Composite(mainComposite, SWT.NONE);

		FormLayout composite4Layout = new FormLayout();
		GridData buttonsCompositeLData = new GridData();
		buttonsCompositeLData.heightHint = 31;
		buttonsCompositeLData.grabExcessHorizontalSpace = true;
		buttonsCompositeLData.horizontalAlignment = GridData.FILL;
		buttonsCompositeLData.verticalAlignment = GridData.END;
		this.buttonsComposite.setLayoutData(buttonsCompositeLData);
		this.buttonsComposite.setLayout(composite4Layout);

		this.okBtn = new Button(this.buttonsComposite, SWT.PUSH | SWT.CENTER);

		FormData okBtnLData = new FormData();
		// okBtnLData.width = 60; //okBtnLData.width =
		// TailleBouton.CalculTailleBouton(Messages.getString("RechercheDialog.6").length());
		okBtnLData.height = 25;
		okBtnLData.right = new FormAttachment(48, -4);
		okBtnLData.bottom = new FormAttachment(854, 1000, 0);
		this.okBtn.setLayoutData(okBtnLData);
		this.okBtn.setText(Messages.getString("RechercheDialog.6")); //$NON-NLS-1$
		this.okBtn.setToolTipText((Messages.getString("RechercheDialog.6")));
		this.okBtn.setEnabled(false);
		this.okBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int[] selIdx = itemsTable.getSelectionIndices();
				selectedValue = getSelectedVar(selIdx, itemsTable);
				if (RechercheDialog.this.filterText == null)
					setFilterText("");
				ActivatorData.getInstance().getPoolDonneesVues().put(RechercheDialog.this.getAppelant() + RechercheDialog.this.getTypeRecherche(), RechercheDialog.this.filterText);

				ActivatorData.getInstance().getPoolDonneesVues().put(RechercheDialog.this.getAppelant() + RechercheDialog.this.getTypeRecherche() + "select", selIdx);
				shell.close();
			}
		});

		this.cancelBtn = new Button(buttonsComposite, SWT.PUSH | SWT.CENTER);

		FormData cancelBtnLData = new FormData();
		// cancelBtnLData.width =
		// TailleBouton.CalculTailleBouton(Messages.getString("RechercheDialog.7").length());
		cancelBtnLData.height = 25;
		cancelBtnLData.left = new FormAttachment(okBtn, 4);
		cancelBtnLData.bottom = new FormAttachment(854, 1000, 0);
		this.cancelBtn.setLayoutData(cancelBtnLData);
		this.cancelBtn.setText(Messages.getString("RechercheDialog.7")); //$NON-NLS-1$
		this.cancelBtn.setToolTipText((Messages.getString("RechercheDialog.7")));
		this.cancelBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedValue = null;
				shell.close();
			}
		});

		this.mainComposite.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle area = mainComposite.getClientArea();
				Point preferredSize = itemsTable.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				int width = area.width - (2 * itemsTable.getBorderWidth());

				if (preferredSize.y > (area.height + itemsTable.getHeaderHeight())) {
					// Subtract the scrollbar width from the total column width
					// if a vertical scrollbar will be required
					Point vBarSize = itemsTable.getVerticalBar().getSize();
					width -= vBarSize.x;
				}

				Point oldSize = itemsTable.getSize();

				if (oldSize.x > area.width) {
					// table is getting smaller so make the columns
					// smaller first and then resize the table to
					// match the client area width
					// column1.setWidth(width/3);
					tableColumn.setWidth(width);
					itemsTable.setSize(area.width, area.height);
				} else {
					// table is getting bigger so make the table
					// bigger first and then make the columns wider
					// to match the client area width
					itemsTable.setSize(area.width, area.height);
					// column1.setWidth(width/3);
					tableColumn.setWidth(width);
				}
			}
		});

		shell.setDefaultButton(okBtn);

		if (this.filterText != null) {
			// this setText will also fire a modification event
			selectText.setText(this.filterText);
			selectText.setToolTipText((this.filterText));
		}

		if (ActivatorData.getInstance().getPoolDonneesVues().get(RechercheDialog.this.getAppelant() + RechercheDialog.this.getTypeRecherche() + "select") != null) {

			int indexToSelect;
			try {
				indexToSelect = (Integer) ((int[]) ActivatorData.getInstance().getPoolDonneesVues().get(RechercheDialog.this.getAppelant() + RechercheDialog.this.getTypeRecherche() + "select"))[0];
			} catch (RuntimeException e1) {
				// TODO Auto-generated catch block
				indexToSelect = 0;
			}

			this.itemsTable.setSelection(indexToSelect);
			this.itemsTable.forceFocus();
		}

		this.selectText.setFocus();
	}

	public String[] getSelectedVar(int[] indices, Table vars) {
		String tab[] = new String[indices.length];
		for (int i = 0; i < indices.length; i++) {
			tab[i] = (String) vars.getItem(indices[i]).getData("nomLong");
		}
		return tab;
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

	protected void initTableValues2(List<String> values) {

		for (int i = 0; i < values.size(); i++) {
			if (values.get(i).equals(TypeRepere.distanceRelatif.getName()) || values.get(i).equals(TypeRepere.tempsRelatif.getName())) {
				values.remove(i);
			}
		}

		if (this.itemsTable == null)
			return;
		this.itemsTable.removeAll();
		TableItem itemTable;
		List<AVariableComposant> fpVariables = null;
		List<Evenement> fpEvenements = null;
		Set<String> messageEventsNames = new HashSet<String>();
		if (typeRecherche.equals("Variable")) {
			fpVariables = Util.getInstance().getAllVariables();
			if (fpVariables != null) {
				for (AVariableComposant var : fpVariables) {
					addVariableNameToSet(var);
					// values.add(var.getDescriptor().getNomUtilisateur()
					// .getNomUtilisateur(
					// Activator.getDefault().getCurrentLanguage()));
				}
			}
		}

		if (typeRecherche.equals("Event")) {
			fpEvenements = Util.getInstance().getAllEvents();
			if (fpEvenements != null) {
				for (Evenement event : fpEvenements) {
					messageEventsNames.add(event.getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()));
					// values.add(event.getNomUtilisateur()
					// .getNomUtilisateur(
					// Activator.getDefault().getCurrentLanguage()));

				}
			}
		}

		boolean invalide = false;
		java.util.Collections.sort(values);
		for (String filteredValue : values) {

			itemTable = new TableItem(this.itemsTable, SWT.NONE);
			String vitesseLimiteKVBNomUtilisateur = VitesseLimiteKVBService.getInstance().getTableLangueNomUtilisateur()
				.getNomUtilisateur(Activator.getDefault().getCurrentLanguage());
			if (VitesseLimiteKVBService.isTableKVBXMLexist() && vitesseLimiteKVBNomUtilisateur.equals(filteredValue)) {
				itemTable.setForeground(Display.getCurrent().getSystemColor(VitesseLimiteKVBService.isTableKVBXMLexist() ? SWT.COLOR_BLACK : SWT.COLOR_RED));
			} else if (filteredValue != null && filteredValue.contains("(V)")) {
				if (ActivatorData.getInstance().getProviderVBVs().verifierValiditeVBV(ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().getVBV(filteredValue.replace("(V) ", ""))) != null) {
					itemTable.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
					invalide = true;
				} else {
					itemTable.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
				}
			} else {
				if (fpVariables != null) {
					if (!this.messageVariablesNames.contains(filteredValue))
						if (GestionnaireDescripteurs.getDescripteurVariableComposee(filteredValue.replace("[C]", "")) == null) {
							itemTable.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
							invalide = true;
						} else
							itemTable.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
					else {
						itemTable.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
					}
				}
			}
			if (fpEvenements != null) {
				if (!messageEventsNames.contains(filteredValue)) {
					itemTable.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
					invalide = true;
				} else {
					itemTable.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));

				}
			}

			if (invalide) {
				itemTable.setText(0, filteredValue);
			} else {
				itemTable.setText(0, filteredValue);
			}
		}
	}

	private List<String> getInitialValues() {

		List<AVariableComposant> fpVariables = null;
		List<Evenement> fpEvenements = null;
		Set<String> messageEventsNames = new HashSet<String>();
		if (typeRecherche.equals("Variable")) {
			fpVariables = Util.getInstance().getAllVariables();
			if (fpVariables != null) {
				for (AVariableComposant var : fpVariables) {
					addVariableNameToSet(var);

				}
			}
		}

		if (typeRecherche.equals("Event")) {
			fpEvenements = Util.getInstance().getAllEvents();
			if (fpEvenements != null) {
				for (Evenement event : fpEvenements) {
					messageEventsNames.add(event.getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()));

				}
			}
		}

		List<String> listeNomsVar = new ArrayList<String>(messageVariablesNames);

		String tempsAbsolu = com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("GestionnaireVueListeBase.2");
		if (typeRecherche.equals("Variable")) {
			listeNomsVar.add(tempsAbsolu);
		}

		return listeNomsVar;
	}

	
	/**
	 * Initialise le code couleur des variable � afficher dans la popup de choix des variables � filtrer
	 * FILTRE VUE GRAPHIQUE
	 * @param values
	 */
	private void initTableValues(List<String> values) {

		for (int i = 0; i < values.size(); i++) {
			if (values.get(i) == TypeRepere.distanceRelatif.getName() || values.get(i) == TypeRepere.tempsRelatif.getName()) {
				values.remove(i);
			}
		}

		if (this.itemsTable == null)
			return;
		this.itemsTable.removeAll();
		TableItem itemTable = null;
		Set<AVariableComposant> fpVariables = null;

		Set<Evenement> fpEvenements = null;
		Set<String> messageEventsNames = new HashSet<String>();
		Set<String> nomVariablesNonRenseignees = new HashSet<String>();
		Set<String> goodlist = new HashSet<String>();// item dans le parcours
		Set<String> badlist = new HashSet<String>();// item pas dans le parcours
		Set<AVariableComposant> setVariablesNonRenseignees = GestionnairePool.getInstance().getVariablesNonRenseignees();

		if (typeRecherche.equals("Variable")) {
			fpVariables = GestionnairePool.getInstance().getVariablesRenseignees();
			for (AVariableComposant var : fpVariables) {
				addVariableNameToSet(var);
			}
			nomVariablesNonRenseignees = new HashSet<String>();
			for (AVariableComposant aVar : setVariablesNonRenseignees) {

				nomVariablesNonRenseignees.add(aVar.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()));

				if (aVar instanceof VariableComplexe) {
					VariableComplexe variableComplexe = (VariableComplexe) aVar;
					int nbEnfants = variableComplexe.getVariableCount();
					for (int i = 0; i < nbEnfants; i++) {
						AVariableComposant varFille = variableComplexe.getEnfant(i);
						nomVariablesNonRenseignees.add(varFille.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()));
					}
				}
			}
		}

		if (typeRecherche.equals("Event")) {
			fpEvenements = Util.getInstance().getMessagesEvents();
			for (Evenement event : fpEvenements) {
				if (event != null) {
					TableLangueNomUtilisateur tableLangueNomUtilisateur = event.getNomUtilisateur();
					String messageEventName = tableLangueNomUtilisateur.getNomUtilisateur(Activator.getDefault().getCurrentLanguage());
					messageEventsNames.add(messageEventName);
				}
			}
		}
		List<String> nomVariables = new ArrayList<String>(values);
		java.util.Collections.sort(nomVariables);

		for (String filteredValue : nomVariables) {
			boolean invalide = false;
			AVariableComposant variableDescripteur = GestionnairePool.getInstance().getVariable(filteredValue);
			if (hideVolatilVariables && variableDescripteur != null && variableDescripteur.getDescriptor().isVolatil()) {
				continue;
			}
			

			String vitesseLimiteKVBNomUtilisateur = VitesseLimiteKVBService.getInstance().getTableLangueNomUtilisateur()
					.getNomUtilisateur(Activator.getDefault().getCurrentLanguage());
			if (vitesseLimiteKVBNomUtilisateur.equals(filteredValue)) {
			    invalide = !VitesseLimiteKVBService.isTableKVBXMLexist();
			} else if (filteredValue != null && filteredValue.contains("(V)") ) {
				if (ActivatorData.getInstance().getProviderVBVs().verifierValiditeVBV(ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().getVBV(filteredValue.replace("(V) ", ""))) != null) {
					invalide = true;
				}
			} else {
				if (fpVariables != null) {
					if (!this.messageVariablesNames.contains(filteredValue)) {
						// invalide=true;
						if (GestionnaireDescripteurs.getDescripteurVariableComposee(filteredValue.replace("[C]", "")) == null) {
							invalide = true;
						}else if(setVariablesNonRenseignees.contains(variableDescripteur)){
							invalide = true;
						}
					}
				}
			}
			if (fpEvenements != null) {
				if (!messageEventsNames.contains(filteredValue)) {
					invalide = true;
				}
			}

			if (invalide) {
				badlist.add(filteredValue);
			} else {
				if (fpEvenements != null) {
					if (!Util.getInstance().isEventDansParcours(filteredValue, true)) {
						badlist.add(filteredValue);
					} else {
						goodlist.add(filteredValue);
					}
				} else if (fpVariables != null) {

					String varVitesseCorrigee = null;

					if (GestionnaireDescripteurs.getDescripteurVariable(TypeRepere.vitesseCorrigee.getCode()) != null) {
						varVitesseCorrigee = GestionnaireDescripteurs.getDescripteurVariable(TypeRepere.vitesseCorrigee.getCode()).getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());
					} else if (GestionnaireDescripteurs.getDescripteurVariable(TypeRepere.distanceCorrigee.getCode()) != null) {
						varVitesseCorrigee = GestionnaireDescripteurs.getDescripteurVariable(TypeRepere.distanceCorrigee.getCode()).getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());
					}

					if (filteredValue.equals(varVitesseCorrigee)) {
						goodlist.add(filteredValue);
					} else {

						if (!Util.getInstance().isVariableDansParcours(filteredValue, true)) {
							badlist.add(filteredValue);
						} else if (!Util.getInstance().isVariableComplexeDansParcoursByNomUnique(filteredValue)) {
							badlist.add(filteredValue);
						} else {
							goodlist.add(filteredValue);
						}
					}

				}

			}
		}

		// ajout � la fin de la liste des variables non renseign�es
		// Set<AVariableComposant> variablesNonRenseignees =
		// GestionnairePool.getVariablesNonRenseignees();
		// for (AVariableComposant aVariableComposant : variablesNonRenseignees)
		// {
		// messageVariablesNames.add(aVariableComposant.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()));
		// messageVariablesNamesNotInRunFile.add(aVariableComposant.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()));
		// invalids.add(aVariableComposant.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()));
		// }

		ArrayList<String> goods = new ArrayList<String>(goodlist);
		ArrayList<String> bads = new ArrayList<String>(badlist);
		java.util.Collections.sort(goods);
		java.util.Collections.sort(bads);

		// on ajoute les items
		for (String string : goods) {
			String text = this.usesShortNames ? Util.getInstance().getNomCourtFromNomUtilisateur(string) : string;
			if (string.equals("")) {
				continue;
			}
			itemTable = new TableItem(this.itemsTable, SWT.NONE);
			itemTable.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
			itemTable.setText(0, text);
			itemTable.setData("nomLong", string);
		}
		for (String string : bads) {
			String text = this.usesShortNames ? Util.getInstance().getNomCourtFromNomUtilisateur(string) : string;
			if (string.equals("")) {
				continue;
			}
			itemTable = new TableItem(this.itemsTable, SWT.NONE);
			itemTable.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
			itemTable.setText(0, text);
			itemTable.setData("nomLong", string);
		}

	}

	private void addVariableNameToSet(AVariableComposant var) {
		TypeVariable typeVar = var.getDescriptor().getTypeVariable();
		if (typeVar == TypeVariable.VAR_ANALOGIC || typeVar == TypeVariable.VAR_DISCRETE) {
			messageVariablesNames.add(var.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()));
		} else if (typeVar == TypeVariable.VAR_COMPLEXE || typeVar == TypeVariable.VAR_COMPOSEE) {
			addVariablesComposeeNamesToSet((VariableComposite) var);
		}
	}

	private void addVariablesComposeeNamesToSet(VariableComposite var) {
		int size = var.getVariableCount();
		ArrayList<String> dejapresent = new ArrayList<String>(0);

		for (int i = 0; i < size; i++) {
			AVariableComposant subVar = var.getEnfant(i);
			if (!dejapresent.contains(subVar.getDescriptor().getM_AIdentificateurComposant().getNom())) {
				dejapresent.add(subVar.getDescriptor().getM_AIdentificateurComposant().getNom());
				addVariableNameToSet(subVar);
			}
		}
	}

	public boolean isMulti() {
		return multi;
	}

	public void setMulti(boolean multi) {
		this.multi = multi;
	}

	public Table getItemsTable() {
		return itemsTable;
	}

	public void setItemsTable(Table itemsTable) {
		this.itemsTable = itemsTable;
	}

	public String[] getSelectedValue() {
		return selectedValue;
	}

	public void setSelectedValue(String[] selectedValue) {
		this.selectedValue = selectedValue;
	}
}
