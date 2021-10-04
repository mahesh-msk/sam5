package com.faiveley.samng.vuetabulaire.ihm.vues.vuefiltre;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.vues.search.dialogs.RechercheDialog;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.IMoveOperationsListener;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.MoveOperationsFlags;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.controls.ComboPopupDissapearListener;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.controls.ComboPopupShowListener;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.controls.ExtendedCombo;
import com.faiveley.samng.principal.ihm.vues.vuesvbv.VbvsProvider;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.descripteur.Type;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.Util;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.LabelValeur;
import com.faiveley.samng.principal.sm.data.variableComposant.variableVirtuelle.VariableVirtuelle;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.variables.LigneVariableFiltreComposite;
import com.faiveley.samng.principal.sm.filtres.variables.OperateurLigneVariable;
import com.faiveley.samng.principal.sm.filtres.variables.ValeurLigneVariable;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.VitesseLimiteKVBService;
import com.faiveley.samng.principal.sm.segments.TableSegments;
import com.faiveley.samng.vuetabulaire.ihm.ActivatorVueTabulaire;
import com.faiveley.samng.vuetabulaire.sm.filtres.NommeeFiltreComposant;

/**
 * 
 * @author Cosmin Udroiu
 * 
 */
public class TabularFiltresEditorTable extends Composite {
	public static final String removeRowUid = Messages
			.getString("TabularFiltresEditorTable.0"); //$NON-NLS-1$

	public static final String searchStringUid = Messages
			.getString("TabularFiltresEditorTable.1"); //$NON-NLS-1$

	public static String searchDlgInputLabelText = Messages
			.getString("TabularFiltresEditorTable.2"); //$NON-NLS-1$

	private static final String[] operatorsList1 = new String[] {
			" ", "=", "\u2260", ">", "\u2265", "<", "\u2264", "<<" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$

	private static final String[] operatorsList2 = new String[] {
			" ", "=", "\u2260" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	private static final String[] valuesList = new String[] {
			" ", AVariableComposant.BOOL_STR_TRUE_VALUE, //$NON-NLS-1$
			AVariableComposant.BOOL_STR_FALSE_VALUE };

	private Color ERR_COLOR = getDisplay().getSystemColor(SWT.COLOR_RED);

	protected NommeeFiltreComposant removeRow = new NommeeFiltreComposant(
			removeRowUid, null);

	private NommeeFiltreComposant searchString = new NommeeFiltreComposant(
			searchStringUid, null);

	protected Table internalTable;

	protected TableColumn indexesColumn;

	protected TableColumn variablesColumn;

	protected TableColumn operatorColumn;

	protected TableColumn valueColumn;

	protected InputSelectionAdapter notificationsAdapter = new InputSelectionAdapter();

	protected VariableNamesComboSelectionAdapter varNamesComboAdapter = new VariableNamesComboSelectionAdapter();

	protected OperatorComboSelectionAdapter operatorComboAdapter = new OperatorComboSelectionAdapter();

	protected ComboPopupListener comboPopupListener = new ComboPopupListener();

	protected ValueTextModifyListener valueTextModifyListener = new ValueTextModifyListener();

	protected Map<String, DescripteurVariable> initialOptionValues = new LinkedHashMap<String, DescripteurVariable>();

	protected List<TableItemInfo> itemsInfo = new ArrayList<TableItemInfo>(0);

	protected List<NommeeFiltreComposant> initialValuesList = new ArrayList<NommeeFiltreComposant>(0);

	/**
	 * Set containing values that are not present in XML file but they appear as
	 * selected in the loaded filter
	 */
	protected Set<String> setInvalidValues = new HashSet<String>();

	/**
	 * Set containing the values that need to be colores. It contains both
	 * setInvalidValues and also the names that exist in XML file but are not
	 * used in the binary file
	 */
	protected Set<String> setColoredValues = new HashSet<String>();

	protected boolean isLastComboSearch;

	protected int indicesearch;

	protected boolean isChangedStateFromInitial;

	private boolean linesInterchanged;

	protected transient PropertyChangeSupport listeners = new PropertyChangeSupport(
			this);

	protected List<IMoveOperationsListener> moveOperationsListener = new ArrayList<IMoveOperationsListener>(0);

	private LineSelectionListener popupDissapearListener = new LineSelectionListener();

	public String searchFilter;

	public TabularFiltresEditorTable(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout());

		this.initialOptionValues.put(removeRowUid, null);
		// this.initialOptionValues.put(searchStringUid, null);
		indicesearch = 1;
		this.internalTable = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		this.internalTable.setHeaderVisible(true);
		initTableColumns();
		addRowItem(this.removeRow);

		addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle area = getClientArea();
				Point preferredSize = internalTable.computeSize(SWT.DEFAULT,
						SWT.DEFAULT);
				int width = area.width - (2 * internalTable.getBorderWidth());

				if (preferredSize.y > (area.height + internalTable
						.getHeaderHeight())) {
					// Subtract the scrollbar width from the total column width
					// if a vertical scrollbar will be required
					Point vBarSize = internalTable.getVerticalBar().getSize();
					width -= vBarSize.x;
				}

				Point oldSize = internalTable.getSize();

				int fixedColumnsSize = indexesColumn.getWidth()
						+ operatorColumn.getWidth();
				if (oldSize.x > area.width) {
					// table is getting smaller so make the columns
					// smaller first and then resize the table to
					// match the client area width
					// indexesColumn.setWidth(indexesColumn.getWidth());
					int valueColumnSize = (int) ((width - fixedColumnsSize) * 0.3);
					valueColumn.setWidth(valueColumnSize);
					variablesColumn.setWidth(width - fixedColumnsSize
							- valueColumnSize);
					internalTable.setSize(area.width, area.height);
				} else {
					// table is getting bigger so make the table
					// bigger first and then make the columns wider
					// to match the client area width
					internalTable.setSize(area.width, area.height);
					int valueColumnSize = (int) ((width - fixedColumnsSize) * 0.3);
					valueColumn.setWidth(valueColumnSize);
					variablesColumn.setWidth(width - fixedColumnsSize
							- valueColumnSize);
				}
			}
		});
		internalTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int selIdx = internalTable.getSelectionIndex();
				if (selIdx != -1) {
					setSelectionIndex(selIdx); // force computing the move
					// flags
					// internalTable.setSelection(internalTable.getItems().length
					// - 2);
				}
			}
		});
	}

	/**
	 * Returns the filters selected by the user
	 * 
	 * @return
	 */
	public List<NommeeFiltreComposant> getSelectedValues() {
		ArrayList<NommeeFiltreComposant> selValues = new ArrayList<NommeeFiltreComposant>();
		String curVarName;
		NommeeFiltreComposant curVar;
		AFiltreComposant varFilter;
		AFiltreComposant valVar;
		AFiltreComposant oper;
		DescripteurVariable descrVar;

		for (TableItemInfo itemInfo : this.itemsInfo) {
			curVarName = itemInfo.comboVarName.getToolTipText();
			if (!"".equals(curVarName) && curVarName != null) { //$NON-NLS-1$
				varFilter = new LigneVariableFiltreComposite();
				descrVar = this.initialOptionValues.get(curVarName);
				if (descrVar != null) {
					varFilter.setNom(descrVar.getM_AIdentificateurComposant()
							.getNom());
				} else
					varFilter.setNom(curVarName);
				oper = new OperateurLigneVariable();
				oper.setNom(itemInfo.comboOperator.getText());
				varFilter.ajouter(oper);

				valVar = new ValeurLigneVariable();
				valVar.setNom(getValueText(itemInfo));
				varFilter.ajouter(valVar);

				curVar = new NommeeFiltreComposant(curVarName, varFilter);

				selValues.add(curVar);
			}
		}
		selValues.trimToSize();
		
		return selValues;
	}

	public void setRemoveRowText(String str) {
		this.removeRow.setNomUtilisateur(str);
	}

	public void setSearchRowText(String str) {
		this.searchString.setNomUtilisateur(str);
	}

	public void setColumnText(int colIdx, String text) {
		this.internalTable.getColumn(colIdx).setText(text);
	}

	public Table getTable() {
		return this.internalTable;
	}

	private void resetValues() {
		this.initialValuesList.clear();
		this.internalTable.removeAll();
		for (TableItemInfo info : this.itemsInfo) {
			if (info != null) {
				if (info.item != null)
					info.item.dispose();
				if (info.editorVarName != null)
					info.editorVarName.dispose();
				if (info.comboVarName != null)
					info.comboVarName.dispose();
				if (info.editorOperator != null)
					info.editorOperator.dispose();
				if (info.comboOperator != null)
					info.comboOperator.dispose();
				if (info.editorValue != null)
					info.editorValue.dispose();
				if (info.valueEditorComponent != null)
					info.valueEditorComponent.dispose();
			}
		}
		this.itemsInfo.clear();
	}

	/**
	 * Initializes the values to be displayed in the combo boxes
	 * 
	 * @param values
	 */
	public void initValues(NommeeFiltreComposant[] values) {
		resetValues();
		if (values == null) {
			addRowItem(this.removeRow);
			return;
		}
		for (NommeeFiltreComposant value : values) {
			addRowItem(value);
			this.initialValuesList.add(value);
		}
		addRowItem(this.removeRow);
		this.isChangedStateFromInitial = false;
		this.linesInterchanged = false;
		updateMissingValues();
	}

	/**
	 * Initializes the values to be available as options in the combo boxes
	 * 
	 * @param values
	 */
	public void setInitialOptionValues(Map<String, DescripteurVariable> values,
			Set<String> nonPresentValues) {
		if (this.initialOptionValues.size() != 2) {
			this.initialOptionValues.clear();
			this.initialOptionValues.put(removeRowUid, null);
			// this.initialOptionValues.put(searchStringUid, null);
			indicesearch = 1;
		}
		if ((values == null) || (values.size() == 0)) {
			return;
		}
		this.initialOptionValues.putAll(values);

		this.setColoredValues.clear();
		if (nonPresentValues != null) {
			this.setColoredValues.addAll(nonPresentValues);
		}

		updateMissingValues();
	}

	/**
	 * Adds a new string value as a row in the table
	 * 
	 * @param value
	 */
	protected void addRowItem(NommeeFiltreComposant value) {
		// Add a new table item to the table
		final TableItemInfo itemInfo = new TableItemInfo();

		itemInfo.comboVarName.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					itemInfo.comboVarName.select(0);
					int selection = itemInfo.comboVarName.getSelectionIndex();
					if (selection == 0
							|| "".equals(itemInfo.comboVarName.getText().trim())) {
						if (itemInfo.valueEditorComponent instanceof Text) {
							((Text) itemInfo.valueEditorComponent).setText("");
						} else {
							((ExtendedCombo) itemInfo.valueEditorComponent)
									.select(0);
						}
					}
					removeItemAt(itemInfo);
					// itemInfo.item.setText(0, Integer.toString(idx));
					checkVarNameChanged(itemInfo);
				}
			}

			public void keyReleased(KeyEvent e) {
			}

		});

		if (this.removeRow == value) {
			itemInfo.comboVarName.setText(""); //$NON-NLS-1$
			// internalTable.setSelection(internalTable.getItems().length - 3);
		} else {

			setComboText(itemInfo.comboVarName, value.getNomUtilisateur());
			int idx = this.itemsInfo.size();
			itemInfo.item.setText(0, Integer.toString(idx));

			checkVarNameChanged(itemInfo);

			updateTableLine(value, itemInfo);

			itemInfo.comboVarName.getArrow().setText("-");
		}
	}

	/**
	 * Updates the possible values for a combo box by removing from the possible
	 * selection values the items that already exist in the table
	 * 
	 * @param combo
	 * @param currentValue
	 */
	private void updateComboPossibleValues(ExtendedCombo combo,
			String currentValue) {
		List<String> valuesPresent = getCurrentVariablesNames();
		List<String> initialOptionsList = new ArrayList<String>(
				initialOptionValues.keySet());
		List<String> possibleValues = listDifference(initialOptionsList,
				valuesPresent, currentValue);
		combo.removeAll();
		for (String val : possibleValues) {
			combo.add(val, this.setColoredValues.contains(val) ? ERR_COLOR
					: null);
		}
		// combo.setItems(possibleValues.toArray(new
		// String[possibleValues.size()]));
		setComboText(combo, currentValue != null ? currentValue : "");
		if ("".equals(currentValue) && (this.internalTable.getItemCount() > 1)) { //$NON-NLS-1$
			if (isLastComboSearch) {
				combo.setTopIndex(indicesearch - 1);
			} else {
				// If is the last item in the table then we must scroll to the
				// previous value
				TableItemInfo info = (TableItemInfo) this.internalTable
						.getItem(this.internalTable.getItemCount() - 2)
						.getData();
				String prevValText = info.comboVarName.getText();
				int prevValInitialPos = initialOptionsList.indexOf(prevValText);
				if (prevValInitialPos == -1)
					return;
				int idx;
				// search in the initial options values array starting from this
				// index
				// to get a value that was not yet added
				for (int i = prevValInitialPos; i < initialOptionsList.size(); i++) {
					idx = possibleValues.indexOf(initialOptionsList.get(i));
					if (idx > 0) {
						combo.setTopIndex(idx);
						break;
					}
				}
			}
		}
	}

	/**
	 * Returns the current variable names existing in the table
	 * 
	 * @return
	 */
	public List<String> getCurrentVariablesNames() {
		TableItemInfo info;
		List<String> valuesPresent = new ArrayList<String>();
		int itemsCount = this.internalTable.getItemCount();
		for (int i = 0; i < itemsCount; i++) {
			info = (TableItemInfo) this.internalTable.getItem(i).getData();

			if (info != null && info.comboVarName != null) {
				valuesPresent.add(info.comboVarName.getNomLong());
			}
		}
		return valuesPresent;
	}

	/**
	 * Returns a difference between the two lists given. There will be returned
	 * a list of items from list1 that are not found in list2
	 * 
	 * @param list1
	 *            the list that is checked
	 * @param list2
	 *            the list used for matching
	 * @param ignoredValue
	 *            the value from list1 that is not added in the returned list
	 * @return a list of items from list1 that are not found in list2
	 */
	public List<String> listDifference(Collection<String> list1,
			List<String> list2, String ignoredValue) {
		List<String> retList = new ArrayList<String>();
		for (String curElement : list1) {
			if (curElement != null) {
				if (curElement.equals(ignoredValue)
						|| !(list2.contains(curElement))) {
					retList.add(curElement);
				}
			}
		}
		return (retList);
	}

	/**
	 * Removes the item from the given info from the table
	 * 
	 * @param info
	 *            the info containing the item to be removed
	 */
	private void removeItemAt(TableItemInfo info) {
		int index = this.itemsInfo.indexOf(info);
		this.internalTable.remove(index);
		info.item.dispose();
		if (info != null) {
			if (info.editorVarName != null)
				info.editorVarName.dispose();
			if (info.comboVarName != null)
				info.comboVarName.dispose();
			if (info.editorOperator != null)
				info.editorOperator.dispose();
			if (info.comboOperator != null)
				info.comboOperator.dispose();
			if (info.editorValue != null)
				info.editorValue.dispose();
			if (info.valueEditorComponent != null)
				info.valueEditorComponent.dispose();

			this.itemsInfo.remove(info);
		}
		// The items editors must be notified that something changed
		int size = this.itemsInfo.size();
		TableItemInfo itemInfo;
		for (int i = 0; i < size; i++) {
			itemInfo = this.itemsInfo.get(i);
			itemInfo.editorVarName.setItem(itemInfo.item);
			if (itemInfo.editorOperator != null)
				itemInfo.editorOperator.setItem(itemInfo.item);
			if (itemInfo.editorValue != null)
				itemInfo.editorValue.setItem(itemInfo.item);
		}
	}

	/**
	 * Adds a property-change listener.
	 * 
	 * @param l
	 *            the listener
	 */
	public final void addPropertyChangeListener(final PropertyChangeListener l) {
		if (l == null) {
			throw new IllegalArgumentException();
		}
		this.listeners.addPropertyChangeListener(l);
	}

	/**
	 * Adds a move operations listener. This listener will be informed about the
	 * move operations that are available for the current table selection
	 * 
	 * @param listener
	 */
	public void addMoveOperationListener(IMoveOperationsListener listener) {
		if (listener != null)
			moveOperationsListener.add(listener);
	}

	public void removePropertyChangeListener(final PropertyChangeListener l) {
		this.listeners.removePropertyChangeListener(l);
	}

	public void removeMoveOperationListener(IMoveOperationsListener listener) {
		if (listener != null)
			moveOperationsListener.remove(listener);
	}

	/**
	 * Notificates all listeners to a model-change
	 * 
	 * @param prop
	 *            the property-id
	 * @param old
	 *            the old-value
	 * @param newValue
	 *            the new value
	 */
	protected final void firePropertyChange(final String prop,
			final Object old, final Object newValue) {
		try {
			if (this.listeners.hasListeners(prop)) {
				this.listeners.firePropertyChange(prop, old, newValue);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Computes the change state from the orginial one
	 * 
	 * @return true if the filter changed
	 */
	private boolean isDifferentFromInitialValues() {
		List<NommeeFiltreComposant> selValues = getSelectedValues();
		if (this.initialValuesList.size() != selValues.size()) {
			this.isChangedStateFromInitial = true;
		} else {
			selValues.removeAll(this.initialValuesList);
			this.isChangedStateFromInitial = (selValues.size() > 0);
		}
		return this.isChangedStateFromInitial || this.linesInterchanged;
	}

	/**
	 * If the filter changed from the original state it will return true
	 * otherwise will return false
	 * 
	 * @return true if the filter changed
	 */
	public boolean isChangedStateFromInitial() {
		return this.isChangedStateFromInitial || linesInterchanged;
	}

	/**
	 * The dirty status is reset according to the current values in the table.
	 * This is used after a save operation of the current values is performed
	 * 
	 */
	public void resetChangedStateFromInitial() {
		this.initialValuesList.clear();
		this.initialValuesList.addAll(getSelectedValues());
		this.isChangedStateFromInitial = false;
		this.linesInterchanged = false;
	}

	/**
	 * Updates the possible selection values with the missing values that are
	 * going to be displayed as we might have not them in the possible values
	 * list. These are going to be displayed with red color
	 * 
	 */
	private void updateMissingValues() {

		this.setColoredValues.removeAll(this.setInvalidValues);
		this.setInvalidValues.clear();
		String curVal;

		for (int i = 0; i < this.itemsInfo.size(); i++) {
			if (!itemsInfo.get(i).comboVarName.isDisposed()) {
				curVal = itemsInfo.get(i).comboVarName.getText();
				if (!"".equals(curVal)) { //$NON-NLS-1$
					if (!this.initialOptionValues.containsKey(curVal)) {

						DescripteurVariable desc = this.initialOptionValues
								.get(curVal);
						if (desc == null) {
							if (ActivatorData.getInstance().getPoolDonneesVues()
									.get("vbvNewName") != null) {
								// internalTable.remove(i);
								if (!((String) ActivatorData.getInstance().getPoolDonneesVues().get("vbvNewName"))
										.equals("")) {
									itemsInfo.get(i).comboVarName
											.setText("(V) "
													+ (String) ActivatorData.getInstance().getPoolDonneesVues()
															.get("vbvNewName"));
									setComboText(itemsInfo.get(i).comboVarName,
											itemsInfo.get(i).comboVarName
													.getText());
								} else {
									this.initialOptionValues.remove(curVal);
									removeItemAt(this.itemsInfo.get(i));
									setComboText(itemsInfo.get(i).comboVarName,
											"");
								}
							}
							this.setInvalidValues.add(curVal);
						} else {
							this.initialOptionValues.put(curVal, null);
							this.setInvalidValues.add(curVal);
							itemsInfo.get(i).comboVarName.setText(curVal,
									ERR_COLOR);
						}
					}
				}
			}
		}

		this.setColoredValues.addAll(this.setInvalidValues);

//		for (TableItemInfo itemInfo : this.itemsInfo) {
//			if (!itemInfo.comboVarName.isDisposed()) {
//				setComboText(itemInfo.comboVarName,
//						itemInfo.comboVarName.getText());
//			}
//		}
	}

	/**
	 * Méthode permettatn de raffraichir le tableau de variables
	 * 
	 */
	public void raffraichirListeVariables() {
		updateMissingValues();
	}

	/**
	 * Sets the text for a variable name. If an invalid value, the text will be
	 * colored with red
	 * 
	 * @param combo
	 * @param text
	 */
	private void setComboText(ExtendedCombo combo, String text) {
		boolean usesShortNames = ActivatorVueTabulaire.getDefault().isUsesShortNames();
		boolean variableValide = false;
		Set<AVariableComposant> setVariablesNonRenseignees = GestionnairePool.getInstance().getVariablesNonRenseignees();
		String vitesseLimiteKVBNomUtilisateur = VitesseLimiteKVBService.getInstance().getTableLangueNomUtilisateur()
				.getNomUtilisateur(Activator.getDefault().getCurrentLanguage());
		
		if (!"".equals(text)) {
			// vérification : il ne s'agit pas d'une variable de correction
			String varDistanceCorrige = null;
			String varVitesseCorrigee = null;

			if (GestionnaireDescripteurs
					.getDescripteurVariable(TypeRepere.vitesseCorrigee
							.getCode()) != null) {
				varVitesseCorrigee = GestionnaireDescripteurs
						.getDescripteurVariable(
								TypeRepere.vitesseCorrigee.getCode())
						.getNomUtilisateur()
						.getNomUtilisateur(
								Activator.getDefault().getCurrentLanguage());
			} else if (GestionnaireDescripteurs
					.getDescripteurVariable(TypeRepere.distanceCorrigee
							.getCode()) != null) {
				varVitesseCorrigee = GestionnaireDescripteurs
						.getDescripteurVariable(
								TypeRepere.distanceCorrigee.getCode())
						.getNomUtilisateur()
						.getNomUtilisateur(
								Activator.getDefault().getCurrentLanguage());
			}
			if (text.equals(varVitesseCorrigee)) {
				if (TableSegments.getInstance().isAppliedDistanceCorrections())
					variableValide = true;

			} else if (text.equals(varDistanceCorrige)) {
				if (TableSegments.getInstance().isAppliedDistanceCorrections())
					variableValide = true;

			} else if (text.contains("[C]")) {
				if (GestionnaireDescripteurs
						.getDescripteurVariableComposee(text.replace("[C]", "")) != null) {
					variableValide = true;
				}
			}else if(setVariablesNonRenseignees.contains(GestionnairePool.getInstance().getVariable(text))){
				variableValide = false;
			} else if (text.equals(vitesseLimiteKVBNomUtilisateur)) {
				variableValide = VitesseLimiteKVBService.isTableKVBXMLexist();
			}

			else {
				if (text.startsWith("(V) ")) {
					VbvsProvider providerVbv = ActivatorData.getInstance().getProviderVBVs();
					String nomVbvSansPrefixe = text.substring(4, text.length());
					VariableVirtuelle vbv = providerVbv.getGestionnaireVbvs()
							.getVBV(nomVbvSansPrefixe);
					if (providerVbv.verifierValiditeVBV(vbv) == null) {
						variableValide = true;
					}
				} else {
					// 1er test : si on trouve la variable, on regarde si elle
					// est dans le fichier de parcours
					variableValide = Util.getInstance().isVariableInXml(text, true);

					// 2ème vérification : la variable est elle dans le parcours
					if (variableValide) {
						variableValide = Util
								.getInstance().isVariableDansParcours(text, true);
					}
				}
			}
		}
		String displayText = usesShortNames ? Util.getInstance().getNomCourtFromNomUtilisateur(text) : text;
		if (!"".equals(text) && (!variableValide)) { //$NON-NLS-1$
			combo.setText(displayText, ERR_COLOR);
		} else {
			combo.setText(displayText);
		}
		combo.setToolTipText(text);
		combo.setNomLong(text);
	}

	/**
	 * Initializes the columns of the table
	 * 
	 */
	private void initTableColumns() {
		// Create the indexes column
		this.indexesColumn = new TableColumn(this.internalTable, SWT.NONE);
		this.indexesColumn.setWidth(25);
		this.indexesColumn.setResizable(false);
		this.indexesColumn.setText(" "); //$NON-NLS-1$
		// Create the variables column
		this.variablesColumn = new TableColumn(this.internalTable, SWT.NONE);
		this.variablesColumn.setText(" "); //$NON-NLS-1$
		this.variablesColumn.setResizable(true);
		this.variablesColumn.setAlignment(SWT.CENTER);
		this.variablesColumn.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event evt) {
				// : add computation of the last column size
				Point oldSize = internalTable.getSize();
				int prevColsWidth = indexesColumn.getWidth()
						+ variablesColumn.getWidth()
						+ operatorColumn.getWidth();
				if (prevColsWidth + 10 < oldSize.x)
					valueColumn.setWidth(oldSize.x - prevColsWidth);
				else
					evt.doit = false;
			}
		});
		// Create the operator column
		this.operatorColumn = new TableColumn(this.internalTable, SWT.NONE);
		this.operatorColumn.setText(" "); //$NON-NLS-1$
		this.operatorColumn.setWidth(80);
		this.operatorColumn.setResizable(false);
		this.operatorColumn.setAlignment(SWT.CENTER);
		// Create the value column
		this.valueColumn = new TableColumn(this.internalTable, SWT.NONE);
		this.valueColumn.setText(" "); //$NON-NLS-1$
		this.valueColumn.setResizable(false);
		this.valueColumn.setAlignment(SWT.CENTER);
	}

	/**
	 * Checks what components should be displayed on the operator and value
	 * columns According to the type of the variable, the combos are populated
	 * accordingly
	 * 
	 * @param itemInfo
	 */
	protected void checkVarNameChanged(TableItemInfo itemInfo) {

		// Populate the operator combo
		ExtendedCombo srcVarNameCombo = itemInfo.comboVarName;
		DescripteurVariable descrVar = initialOptionValues.get(srcVarNameCombo
				.getNomLong());

		// reset the editors for operator and second operand if they exist
		if (itemInfo.comboOperator != null) {
			itemInfo.editorOperator.dispose();
			itemInfo.comboOperator.dispose();
			itemInfo.comboOperator = null;
		}
		if (itemInfo.valueEditorComponent != null) {
			itemInfo.editorValue.dispose();
			itemInfo.valueEditorComponent.dispose();
			itemInfo.valueEditorComponent = null;
		}
		if (itemInfo.comboOperator == null) { // this condition is not needed
			// anymore - component is
			// recreated
			// : see if this can be added into TableItemInfo
			itemInfo.editorOperator = new TableEditor(this.internalTable);
			itemInfo.editorOperator.grabHorizontal = true;

			itemInfo.comboOperator = new ExtendedCombo(this.internalTable,
					SWT.NONE, false);
			itemInfo.initEditorComponent(itemInfo.comboOperator);
			itemInfo.editorOperator.setEditor(itemInfo.comboOperator,
					itemInfo.item, 2);
			initOperatorCombo(itemInfo, descrVar);
			itemInfo.comboOperator.addSelectionListener(notificationsAdapter);
			itemInfo.comboOperator.addSelectionListener(operatorComboAdapter);
			itemInfo.comboOperator
					.addPopupDissapearListener(popupDissapearListener);
		}
		if (itemInfo.valueEditorComponent == null) { // this condition is not
			// needed anymore -
			// component is
			// recreated
			// : see if this can be added into TableItemInfo
			itemInfo.editorValue = new TableEditor(this.internalTable);
			itemInfo.editorValue.grabHorizontal = true;
			boolean isBooleanDescr = false;
			if (descrVar != null) {
				isBooleanDescr = (descrVar.getTypeVariable() == TypeVariable.VAR_DISCRETE || descrVar
						.getTypeVariable() == TypeVariable.VAR_VIRTUAL)
						&& ((descrVar.getType() == Type.boolean1) || (descrVar
								.getType() == Type.boolean8));
			}

			if (isBooleanDescr
					|| (descrVar != null && descrVar.getTypeVariable() == TypeVariable.VAR_COMPOSEE)) {
				itemInfo.valueEditorComponent = new ExtendedCombo(
						this.internalTable, SWT.NONE, false);
				itemInfo.initEditorComponent(itemInfo.valueEditorComponent);
				((ExtendedCombo) itemInfo.valueEditorComponent)
						.addSelectionListener(notificationsAdapter);
				// : the same thing should be done for the text editor handled
				// below
				((ExtendedCombo) itemInfo.valueEditorComponent)
						.addPopupDissapearListener(popupDissapearListener);
				String[] possibleValues = getPossibleValuesList(descrVar);
				((ExtendedCombo) itemInfo.valueEditorComponent)
						.setVisibleItemCount(possibleValues.length - 1);
				for (String str : possibleValues)
					if (((ExtendedCombo) itemInfo.valueEditorComponent)
							.indexOf(str) == -1) {
						((ExtendedCombo) itemInfo.valueEditorComponent)
								.add(str);
					}
			} else {
				itemInfo.valueEditorComponent = new Text(this.internalTable,
						SWT.NONE);
				itemInfo.initEditorComponent(itemInfo.valueEditorComponent);
				((Text) itemInfo.valueEditorComponent)
						.addSelectionListener(notificationsAdapter);
				((Text) itemInfo.valueEditorComponent)
						.addModifyListener(valueTextModifyListener);
			}
			itemInfo.editorValue.setEditor(itemInfo.valueEditorComponent,
					itemInfo.item, 3);
		}
	}

	/**
	 * Returns the list of labels for a variable. The list is returned only if
	 * the variable is of type discrete and boolean1 or boolean8 or if is a
	 * composee variable
	 * 
	 * @param descrVar
	 *            the variable descriptor
	 * @return the list of labels or null if the variable has no labels
	 */
	private List<LabelValeur> getLabelsForVariable(DescripteurVariable descrVar) {
		if (descrVar != null) {
			boolean isBooleanVar = (descrVar.getTypeVariable() == TypeVariable.VAR_DISCRETE || descrVar
					.getTypeVariable() == TypeVariable.VAR_VIRTUAL)
					&& ((descrVar.getType() == Type.boolean1) || (descrVar
							.getType() == Type.boolean8));
			// get the labels
			if (isBooleanVar
					|| descrVar.getTypeVariable() == TypeVariable.VAR_COMPOSEE) {
				List<LabelValeur> labels = Util.getInstance().getLabelsForVariable(descrVar);
				// We should have at least two values
				if (labels != null && labels.size() > 1) {
					return labels;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the possible values strings for a variable descriptor. If the
	 * variable contains a list of labels, then this list is returned
	 * 
	 * @param descrVar
	 *            the variable descriptor
	 * @return an array of labels (true/FALSE array if no labels exist)
	 */
	private String[] getPossibleValuesList(DescripteurVariable descrVar) {
		String[] possibleValues = null;
		List<LabelValeur> labels = getLabelsForVariable(descrVar);
		if (labels != null) {
			possibleValues = new String[labels.size() + 1];
			possibleValues[0] = ""; //$NON-NLS-1$
			int i = 1;
			for (LabelValeur label : labels) {
				possibleValues[i] = label.getLabel();
				i++;
			}
		}
		if (possibleValues == null)
			possibleValues = valuesList;
		return possibleValues;
	}

	/**
	 * Updates the operator and value column
	 * 
	 * @param value
	 * @param itemInfo
	 */
	private void updateTableLine(NommeeFiltreComposant value,
			TableItemInfo itemInfo) {
		AFiltreComposant filtre = value.getFiltre();
		if (filtre.getEnfantCount() != 2)
			return;
		// update the columns texts
		AFiltreComposant filtreOperator = filtre.getEnfant(0);
		AFiltreComposant filtreValue = filtre.getEnfant(1);
		itemInfo.comboOperator.setText(filtreOperator.getNom());
		setValueText(itemInfo, filtreValue.getNom());
	}

	/**
	 * Gets the text from the value column for the given item. We have a
	 * separate method for this as we migth have to types of controles: a Text
	 * or a Combo
	 * 
	 * @param itemInfo
	 * @param value
	 */
	private String getValueText(TableItemInfo itemInfo) {
		String value = ""; //$NON-NLS-1$
		if (itemInfo != null && itemInfo.valueEditorComponent != null) {
			if (itemInfo.valueEditorComponent instanceof Text) {
				value = ((Text) itemInfo.valueEditorComponent).getText();
			} else {
				value = ((ExtendedCombo) itemInfo.valueEditorComponent)
						.getText().trim();
				int selIdx = ((ExtendedCombo) itemInfo.valueEditorComponent)
						.getSelectionIndex();
				if (!"".equals(value) && selIdx != -1) { //$NON-NLS-1$
					ExtendedCombo srcVarNameCombo = itemInfo.comboVarName;
					DescripteurVariable descrVar = initialOptionValues
							.get(srcVarNameCombo.getText());
					List<LabelValeur> labels = getLabelsForVariable(descrVar);
					if (labels != null) {
						// the first position is empty string (no selection) so
						// we have to decrement with 1
						value = (String) labels.get(selIdx - 1).getValeurs();
					}
				}
			}
		}
		return value;
	}

	/**
	 * Sets the text to the value column for the given item. We have a separate
	 * method for this as we migth have to types of controles: a Text or a Combo
	 * 
	 * @param itemInfo
	 * @param value
	 */
	private void setValueText(TableItemInfo itemInfo, String value) {
		if (itemInfo == null || itemInfo.valueEditorComponent == null)
			return;
		if (value == null)
			value = ""; //$NON-NLS-1$
		if (itemInfo.valueEditorComponent instanceof Text) {
			((Text) itemInfo.valueEditorComponent)
					.removeModifyListener(valueTextModifyListener);
			((Text) itemInfo.valueEditorComponent).setText(value);
			((Text) itemInfo.valueEditorComponent)
					.addModifyListener(valueTextModifyListener);
		} else {
			// Check if we have a label associated for the given value
			ExtendedCombo srcVarNameCombo = itemInfo.comboVarName;
			DescripteurVariable descrVar = initialOptionValues
					.get(srcVarNameCombo.getText());
			List<LabelValeur> labels = getLabelsForVariable(descrVar);
			if (labels != null) {
				int i = 0;
				for (LabelValeur labelVal : labels) {
					if (value.equals(labelVal.getValeurs())) {
						value = labelVal.getLabel();
						((ExtendedCombo) itemInfo.valueEditorComponent)
								.setText(value);
						((ExtendedCombo) itemInfo.valueEditorComponent)
								.select(i + 1); // the first position is empty
						// string (no selection)
						return; // we return here because setting text will
						// change also the selection
						// in combo (we might have the case when we have the
						// same label for
						// multiple values and we want to select the correct
						// label for the current value)
					}
					i++;
				}
			} else {
				// if we have a 0 or a 1 in file and we have a combo (so, a
				// boolean var)
				if ("0".equals(value)) { //$NON-NLS-1$
					value = AVariableComposant.BOOL_STR_FALSE_VALUE;
				} else if ("1".equals(AVariableComposant.BOOL_STR_TRUE_VALUE)) { //$NON-NLS-1$
					value = AVariableComposant.BOOL_STR_TRUE_VALUE;
				}
			}
			((ExtendedCombo) itemInfo.valueEditorComponent).setText(value);
		}
	}

	/**
	 * Initializes the option values for the operator
	 * 
	 * @param itemInfo
	 * @param descrVar
	 */
	private void initOperatorCombo(TableItemInfo itemInfo,
			DescripteurVariable descrVar) {

		String[] operatorsList = null;
		if (descrVar != null) {
			Type typeVariable = descrVar.getType();
			if (descrVar.getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
				if (descrVar.getType() == Type.string
						|| descrVar.getType() == Type.array)
					operatorsList = operatorsList2;
				else
					operatorsList = operatorsList1;
			} else if (descrVar.getTypeVariable() == TypeVariable.VAR_DISCRETE) {

				switch (typeVariable) {

				case unixTimestamp:
					operatorsList = operatorsList1;
					break;
				case string:
					operatorsList = operatorsList2;
					break;
				case array:
					operatorsList = operatorsList2;
					break;
				case BCD4:
					operatorsList = operatorsList1;
					break;
				case BCD8:
					operatorsList = operatorsList1;
					break;
				case uint8:
					operatorsList = operatorsList1;
					break;
				case dateHeureBCD:
					operatorsList = operatorsList1;
					break;

				case uintXbits:
					operatorsList = operatorsList1;
					break;
				case intXbits:
					operatorsList = operatorsList1;
					break;
				case int8:
					operatorsList = operatorsList1;
					break;
				case uint16:
					operatorsList = operatorsList1;
					break;
				case int16:
					operatorsList = operatorsList1;
					break;
				case uint24:
					operatorsList = operatorsList1;
					break;
				case int24:
					operatorsList = operatorsList1;
					break;
				case uint32:
					operatorsList = operatorsList1;
					break;
				case int32:
					operatorsList = operatorsList1;
					break;
				case uint64:
					operatorsList = operatorsList1;
					break;
				case int64:
					operatorsList = operatorsList1;
					break;
				case real32:
					operatorsList = operatorsList1;
					break;
				case boolean8:
					operatorsList = operatorsList2;
					break;
				case boolean1:
					operatorsList = operatorsList2;
					break;

				}

			} else if (descrVar.getTypeVariable() == TypeVariable.VAR_VIRTUAL
					|| descrVar.getTypeVariable() == TypeVariable.VAR_COMPOSEE) {
				operatorsList = operatorsList2;
			}
		}
		if (operatorsList == null) {
			itemInfo.comboOperator.setVisibleItemCount(1);
			itemInfo.comboOperator.add(" "); //$NON-NLS-1$
		} else {
			itemInfo.comboOperator
					.setVisibleItemCount(operatorsList.length - 1);

			for (String str : operatorsList) {
				itemInfo.comboOperator.add(str);
			}
		}
	}

	/**
	 * When a change notification occured, the current editing items are checked
	 * to see if they changed from the original ones and according to the
	 * current state a change or restored notification is sent
	 * 
	 */
	protected void onFilterChanged() {
		boolean listChanged = isDifferentFromInitialValues();
		if (listChanged)
			firePropertyChange(
					"CMB_SEL_CHANGED", null, TabularFiltresEditorTable.this); //$NON-NLS-1$
		else
			firePropertyChange(
					"CMB_SEL_RESTORED", null, TabularFiltresEditorTable.this); //$NON-NLS-1$
	}

	/**
	 * Class for encapsulating an row in the table. It contains the editors and
	 * the item that is added to the row
	 * 
	 * @author Cosmin Udroiu
	 * 
	 */
	private class TableItemInfo {
		public TableItem item;

		public TableEditor editorVarName;

		public ExtendedCombo comboVarName;

		public TableEditor editorOperator;

		public ExtendedCombo comboOperator;

		public TableEditor editorValue;

		public Scrollable valueEditorComponent;

		public TableItemInfo() {
			this.item = new TableItem(internalTable, SWT.NONE);
			this.item.setData(this);

			this.editorVarName = new TableEditor(internalTable);
			this.editorVarName.grabHorizontal = true;
			this.comboVarName = new ExtendedCombo(internalTable, SWT.NONE, true);
			initEditorComponent(this.comboVarName);
			this.editorVarName.setEditor(this.comboVarName, this.item, 1);
			this.comboVarName.addSelectionListener(varNamesComboAdapter);
			this.comboVarName.addSelectionListener(notificationsAdapter);
			// this.comboVarName.addKeyListener(new KeyListener(){
			// public void keyPressed(KeyEvent e) {
			// if (e.keyCode == SWT.DEL) {
			// comboVarName.select(0);
			// // int index = itemsInfo.indexOf(itemInfo);
			// // ExtendedCombo srcCombo = (ExtendedCombo) e.getSource();
			// // srcCombo.select(0);
			// // String oldValue = (String) e.data;
			// // if(oldValue.equals(srcCombo.getText()))
			// // return;
			//
			// int selection = comboVarName.getSelectionIndex();
			// // if(selection == 0 || "".equals(comboVarName.getText().trim()))
			// { //$NON-NLS-1$
			// // if(itemInfo.valueEditorComponent instanceof Text) {
			// // ((Text)itemInfo.valueEditorComponent).setText("");
			// //$NON-NLS-1$
			// // } else {
			// // ((ExtendedCombo)itemInfo.valueEditorComponent).select(0);
			// // }
			// // }
			// int idx = itemsInfo.size();
			// //itemInfo.item.setText(0, Integer.toString(idx));
			// // checkVarNameChanged(item);
			// boolean listChanged = isDifferentFromInitialValues();
			// if(listChanged)
			// firePropertyChange("CMB_SEL_CHANGED", null,
			// TabularFiltresEditorTable.this); //$NON-NLS-1$
			// else
			// firePropertyChange("CMB_SEL_RESTORED", null,
			// TabularFiltresEditorTable.this); //$NON-NLS-1$
			// }
			//
			// }
			//
			// public void keyReleased(KeyEvent e) {
			// }
			// });
			this.comboVarName.addPopupListener(comboPopupListener);
			this.comboVarName.addPopupDissapearListener(popupDissapearListener);
			TabularFiltresEditorTable.this.itemsInfo.add(this);
		}

		public void initEditorComponent(Scrollable widget) {
			widget.setData(this);
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.grabExcessHorizontalSpace = true;
			widget.setLayoutData(gridData);
			widget.setBackground(internalTable.getBackground());
			widget.setFont(internalTable.getFont());
			widget.setForeground(internalTable.getForeground());
			if (widget instanceof ExtendedCombo)
				((ExtendedCombo) widget).setEditable(false);
		}
	}

	/**
	 * Listener to changes that notifies in turn the change listeners that the
	 * filter changed.
	 * 
	 * @author Cosmin Udroiu
	 * 
	 */
	private class InputSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			super.widgetSelected(e);
			onFilterChanged();
		}
	}

	/**
	 * Listener for ExtendedCombo of the operator column that handles the change
	 * of the variable operator for an item
	 * 
	 * @author Cosmin Udroiu
	 * 
	 */
	private class OperatorComboSelectionAdapter extends SelectionAdapter {
		public void widgetSelected(SelectionEvent event) {
			ExtendedCombo srcCombo = (ExtendedCombo) event.getSource();
			String oldValue = (String) event.data;
			if (oldValue.equals(srcCombo.getText()))
				return;
			int selection = srcCombo.getSelectionIndex();
			TableItemInfo itemInfo = (TableItemInfo) srcCombo.getData();
			if (selection == 0 || "".equals(srcCombo.getText().trim())) { //$NON-NLS-1$
				if (itemInfo.valueEditorComponent instanceof Text) {
					((Text) itemInfo.valueEditorComponent).setText(""); //$NON-NLS-1$
				} else {
					((ExtendedCombo) itemInfo.valueEditorComponent).select(0);
				}
			}
		}
	}

	/**
	 * Listener for ExtendedCombo of the variables column that handles the
	 * change of the name of a variable for an item
	 * 
	 * @author Cosmin Udroiu
	 * 
	 */
	private class VariableNamesComboSelectionAdapter extends SelectionAdapter {
		public void widgetSelected(SelectionEvent event) {
			ExtendedCombo srcCombo = (ExtendedCombo) event.getSource();
			String oldValue = (String) event.data;
			if (oldValue.equals(srcCombo.getText()))
				return;
			int selection = srcCombo.getSelectionIndex();
			TableItemInfo itemInfo = (TableItemInfo) srcCombo.getData();

			if (selection == 0) {
				// if the user selected the remove variable value
				if ("".equals(oldValue)) { //$NON-NLS-1$
					srcCombo.setText(""); //$NON-NLS-1$
					return;
				} else {
					removeItemAt(itemInfo);
				}
			} else if (selection == 1) {
				RechercheDialog searchDlg = new RechercheDialog(getDisplay()
						.getActiveShell(), true);
				searchDlg.setHideVolatilVariables(true);
				searchDlg.setInputLabelText(searchDlgInputLabelText);
				searchDlg.setFilterText(searchFilter);
				searchDlg.setAppelant(this.getClass().getName());
				searchDlg.setTypeRecherche("Variable");

				List<String> valuesPresent = getCurrentVariablesNames();
				// The oldValue should be in this case searchString
				List<String> possibleValues = listDifference(
						initialOptionValues.keySet(), valuesPresent, oldValue);
				possibleValues.remove(removeRowUid); // Remove the
				// removeRowString value
				searchDlg.setSelectableValues(possibleValues
						.toArray(new String[initialOptionValues.size()]));
				String selValue = searchDlg.open();
				searchFilter = searchDlg.getFilterText(); // save the filter
				// for further
				// searches
				if (selValue == null) {
					setComboText(srcCombo, oldValue);
					return;
				}
				// if(selValue!=null)
				// ActivatorData.getInstance().getPoolDonneesVues().put(this.getClass().getName()+"Variable",
				// selValue);
				setComboText(srcCombo, selValue);
				isLastComboSearch = true;
				if (!selValue.equals(oldValue)) {
					if ("".equals(oldValue)) { // we had the last empty row
						// //$NON-NLS-1$
						itemInfo.item.setText(0,
								Integer.toString(itemsInfo.size()));
						addRowItem(removeRow);
					}
				}
				checkVarNameChanged(itemInfo);
			} else {
				// If the user selected a valid variable name
				isLastComboSearch = false;
				String selValue = srcCombo.getText();
				setComboText(srcCombo, selValue);
				if (!selValue.equals(oldValue)) {
					if ("".equals(oldValue)) { // we had the last empty row
						// //$NON-NLS-1$
						itemInfo.item.setText(0,
								Integer.toString(itemsInfo.size()));
						addRowItem(removeRow);
					}
				}
				checkVarNameChanged(itemInfo);
			}
		}
	}

	/**
	 * Listener for ExtendedCombo components of the variables column that the
	 * combo is about to be displayed. This is used to dynamically populate the
	 * possible values (as the already added variables should not appear in the
	 * combo)
	 * 
	 * @author Cosmin Udroiu
	 * 
	 */
	private class ComboPopupListener implements ComboPopupShowListener {
		public void onComboPopupShowing(Widget widget) {
			if (!(widget instanceof ExtendedCombo)) {
				return;
			}

			ExtendedCombo srcCombo = (ExtendedCombo) widget;
			updateComboPossibleValues(srcCombo, srcCombo.getNomLong());
			TableItemInfo itemInfo = (TableItemInfo) srcCombo.getData();
			int currentLinePos = itemsInfo.indexOf(itemInfo);
			setSelectionIndex(currentLinePos);
			if (srcCombo.getArrow().getText().equals("-")) {
				if (srcCombo.getArrow().isFocusControl()) {
					removeItemAt(itemInfo);
					onFilterChanged();
				}
			} else {
				// Ouverture de la boite de dialogue pour rechercher des variables à ajouter dans le filtre
				// Récupération de la valeur de l'option "Noms courts"
				boolean usesShortNames = ActivatorVueTabulaire.getDefault().isUsesShortNames();
				// Passage à un constructeur surchargé spécialement pour les noms courts
				RechercheDialog searchDlg = new RechercheDialog(getDisplay()
						.getActiveShell(), true, usesShortNames);
				searchDlg.setHideVolatilVariables(false);
				searchDlg.setInputLabelText(searchDlgInputLabelText);
				searchDlg.setFilterText(searchFilter);
				searchDlg.setAppelant(this.getClass().getName());
				searchDlg.setTypeRecherche("Variable");

				List<String> valuesPresent = getCurrentVariablesNames();
				// The oldValue should be in this case searchString
				List<String> possibleValues = listDifference(
						initialOptionValues.keySet(), valuesPresent,
						srcCombo.getText());
				possibleValues.remove(removeRowUid); // Remove the
				// removeRowString value
				searchDlg.setSelectableValues(possibleValues
						.toArray(new String[initialOptionValues.size()]));
				String selValue = searchDlg.open();
				searchFilter = searchDlg.getFilterText(); // save the filter
				// for further
				// searches
				if (selValue == null) {
					setComboText(srcCombo, srcCombo.getText());
					return;
				}
				// if(selValue!=null)
				// ActivatorData.getInstance().getPoolDonneesVues().put(this.getClass().getName()+"Variable",
				// selValue);

				int nbSelected = 0;
				String[] selects = searchDlg.getSelectedValue();
				if (selects == null) {
					return;
				}
				nbSelected = selects.length;
				if (nbSelected > 1) {
					String selected[] = searchDlg.getSelectedValue();
					for (int i = 0; i < nbSelected; i++) {

						srcCombo = itemsInfo.get(currentLinePos + i).comboVarName;
						itemInfo = (TableItemInfo) srcCombo.getData();

						if (!selected[i].equals(srcCombo.getText())) {
							if ("".equals(srcCombo.getText())) { // we had the
								// last
								// empty row
								itemInfo.item.setText(0,
										Integer.toString(itemsInfo.size()));
							}
						}
						setComboText(srcCombo, selected[i]);
						checkVarNameChanged(itemInfo);
						srcCombo.getArrow().setText("-");
						addRowItem(removeRow);
						onFilterChanged();
					}
				} else {
					isLastComboSearch = true;
					if (!selValue.equals(srcCombo.getText())) {
						if ("".equals(srcCombo.getText())) { // we had the last
							// empty row
							itemInfo.item.setText(0,
									Integer.toString(itemsInfo.size()));
						}
					}
					setComboText(srcCombo, selValue);
					checkVarNameChanged(itemInfo);
					srcCombo.getArrow().setText("-");
					addRowItem(removeRow);
					onFilterChanged();
				}
			}
		}
	}

	/**
	 * Listener for the text editors in the table that the text changed
	 * 
	 * @author Cosmin Udroiu
	 * 
	 */
	private class ValueTextModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			onFilterChanged();
		}
	}

	/**
	 * Listener for a line selection
	 * 
	 * @author Cosmin Udroiu
	 * 
	 */
	private class LineSelectionListener implements ComboPopupDissapearListener {
		public void onComboPopupDissapear(Widget widget) {
			TableItemInfo itemInfo = (TableItemInfo) ((ExtendedCombo) widget)
					.getData();
			int currentLinePos = itemsInfo.indexOf(itemInfo);
			setSelectionIndex(currentLinePos);
		}
	}

	/**
	 * Sets the selection in the table to the given index
	 * 
	 * @param index
	 */
	private void setSelectionIndex(int index) {
		internalTable.setSelection(index);
		int flags = computeMoveFlags(index);
		fireMoveOperations(flags);
	}

	/**
	 * Notifies move operations listeners that the flags changed
	 * 
	 * @param flags
	 */
	private void fireMoveOperations(int flags) {
		for (IMoveOperationsListener listener : moveOperationsListener) {
			listener.moveFlagsChanged(flags);
		}
	}

	/**
	 * Computes for the given index in the table the move operations that can be
	 * performed
	 * 
	 * @param index
	 *            index of the item in the table
	 * @return the move operations flags (a mask of all possible operations)
	 */
	private int computeMoveFlags(int index) {
		int moveFlags = MoveOperationsFlags.NO_MOVE;
		// we have a line row
		if (index != -1) {
			// we have the first variable in graphic
			if (index != itemsInfo.size() - 1) { // if we are on the last row
				// (which is empty) we have
				// no operations
				if (index == 0) {
					if (itemsInfo.size() > 2) { // we have at least one row and
						// an empty row
						moveFlags |= MoveOperationsFlags.MOVE_BOTTOM
								| MoveOperationsFlags.MOVE_DOWN;
					}
				} else if (index == itemsInfo.size() - 2) { // we are not on the
					// empty row
					// we have the last variable in graphic
					moveFlags |= MoveOperationsFlags.MOVE_UP
							| MoveOperationsFlags.MOVE_TOP;
				} else {
					moveFlags |= MoveOperationsFlags.MOVE_UP
							| MoveOperationsFlags.MOVE_TOP
							| MoveOperationsFlags.MOVE_DOWN
							| MoveOperationsFlags.MOVE_BOTTOM;
				}
			}
		}
		return moveFlags;
	}

	/**
	 * Handler for the notification that a move operation was performed (due to
	 * pressing a button, for example). The flag will contain only one of the
	 * possible flags and not a mask
	 * 
	 * @param flag
	 *            the move operation flag (ex. MOVE_TOP, MOVE_UP etc.)
	 */
	public void moveSelection(int flag) {
		int index = internalTable.getSelectionIndex();
		if (index == -1)
			return;
		int num = 0;
		switch (flag) {
		case MoveOperationsFlags.MOVE_TOP:
			num = index - itemsInfo.get(0).item.getImageIndent();
			for (int i = 0; i < num; i++) {
				interchangeVariableLines(index - 1 - i, index - i);
			}
			// interchangeVariableLines(0, index);
			break;
		case MoveOperationsFlags.MOVE_UP:
			interchangeVariableLines(index - 1, index);
			break;
		case MoveOperationsFlags.MOVE_DOWN:
			interchangeVariableLines(index + 1, index);
			break;
		case MoveOperationsFlags.MOVE_BOTTOM:
			num = itemsInfo.size() - 2 - index;
			for (int i = 0; i < num; i++) {
				interchangeVariableLines(index + 1 + i, index + i);
			}
			// interchangeVariableLines(itemsInfo.size()-2, index);
			break;
		}
	}

	/**
	 * Interchange two lines from the table
	 * 
	 * @param firstLine
	 *            the first line index
	 * @param secondLine
	 *            the second line index
	 */
	private void interchangeVariableLines(int firstLine, int secondLine) {
		TableItemInfo firstItemInfo = itemsInfo.get(firstLine);
		TableItemInfo secondItemInfo = itemsInfo.get(secondLine);

		String firstItemText = firstItemInfo.comboVarName.getNomLong();
		String firstItemOperator = firstItemInfo.comboOperator.getText();
		String firstItemValue = null;
		if (firstItemInfo.valueEditorComponent != null) {
			if (firstItemInfo.valueEditorComponent instanceof ExtendedCombo)
				firstItemValue = ((ExtendedCombo) firstItemInfo.valueEditorComponent)
						.getText();
			else
				firstItemValue = ((Text) firstItemInfo.valueEditorComponent)
						.getText();
		}

		String secondItemText = secondItemInfo.comboVarName.getNomLong();
		String secondItemOperator = secondItemInfo.comboOperator.getText();
		String secondItemValue = null;
		if (secondItemInfo.valueEditorComponent != null) {
			if (secondItemInfo.valueEditorComponent instanceof ExtendedCombo)
				secondItemValue = ((ExtendedCombo) secondItemInfo.valueEditorComponent)
						.getText();
			else
				secondItemValue = ((Text) secondItemInfo.valueEditorComponent)
						.getText();
		}
		setComboText(firstItemInfo.comboVarName, secondItemText);
		setComboText(secondItemInfo.comboVarName, firstItemText);

		checkVarNameChanged(firstItemInfo);
		checkVarNameChanged(secondItemInfo);

		firstItemInfo.comboOperator.setText(secondItemOperator);
		secondItemInfo.comboOperator.setText(firstItemOperator);
		if (firstItemValue != null) {
			if (secondItemInfo.valueEditorComponent instanceof ExtendedCombo)
				((ExtendedCombo) secondItemInfo.valueEditorComponent)
						.setText(firstItemValue);
			else
				((Text) secondItemInfo.valueEditorComponent)
						.setText(firstItemValue);
		}
		if (secondItemValue != null) {
			if (firstItemInfo.valueEditorComponent instanceof ExtendedCombo)
				((ExtendedCombo) firstItemInfo.valueEditorComponent)
						.setText(secondItemValue);
			else
				((Text) firstItemInfo.valueEditorComponent)
						.setText(secondItemValue);
		}

		setSelectionIndex(firstLine); // preserve selection

		this.linesInterchanged = true;
		onFilterChanged();
	}
}
