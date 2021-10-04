package com.faiveley.samng.vueliste.ihm.vues.vuefiltre;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

import com.faiveley.samng.principal.ihm.vues.search.dialogs.RechercheDialog;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.controls.ComboPopupShowListener;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.controls.ExtendedCombo;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.Util;

/**
 * 
 * @author Cosmin Udroiu
 * 
 */
public class ListeFiltresEditorTable extends Composite {
	private Color ERR_COLOR = getDisplay().getSystemColor(SWT.COLOR_RED);
	private String removeRowString = Messages
			.getString("ListeFiltresEditorTable.0"); //$NON-NLS-1$
	private String searchString = Messages
			.getString("ListeFiltresEditorTable.1"); //$NON-NLS-1$
	protected Table internalTable;
	protected TableColumn firstColumn;
	protected TableColumn secondColumn;
	protected String secondColumnText = Messages
			.getString("ListeFiltresEditorTable.2"); //$NON-NLS-1$
	protected List<String> initialOptionValues = new ArrayList<String>(0);
	protected List<TableItemInfo> itemsInfo = new ArrayList<TableItemInfo>(0);
	protected List<String> initialValuesList = new ArrayList<String>(0);
	protected Set<String> setInvalidValues = new HashSet<String>();
	protected Set<String> setColoredValues = new HashSet<String>();
	protected boolean isLastComboSearchVar, isLastComboSearchEv;
	protected int indicesearchVar, indicesearchEv;
	protected boolean isChangedStateFromInitial;
	protected ComboSelectionAdapter comboSelAdapter = new ComboSelectionAdapter();
	protected ComboPopupListener comboPopupListener = new ComboPopupListener();

	protected transient PropertyChangeSupport listeners = new PropertyChangeSupport(
			this);

	private String searchFilter;

	public ListeFiltresEditorTable(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout());

		this.initialOptionValues.add(this.removeRowString);
		this.initialOptionValues.add(this.searchString);
		indicesearchEv = 1;
		indicesearchVar = 1;
		this.internalTable = new Table(this, SWT.BORDER | SWT.MULTI);
		this.internalTable.setHeaderVisible(true);
		this.firstColumn = new TableColumn(this.internalTable, SWT.NONE);
		this.firstColumn.setWidth(0);
		this.firstColumn.setResizable(false);
		this.firstColumn.setText(" "); //$NON-NLS-1$
		this.secondColumn = new TableColumn(this.internalTable, SWT.NONE);
		this.secondColumn.setText(this.secondColumnText);
		this.secondColumn.setResizable(false);
		this.secondColumn.setAlignment(SWT.CENTER);
		addRowItem(this.removeRowString);

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

				if (oldSize.x > area.width) {
					// table is getting smaller so make the columns
					// smaller first and then resize the table to
					// match the client area width
					// column1.setWidth(width/3);
					secondColumn.setWidth(width - firstColumn.getWidth());
					internalTable.setSize(area.width, area.height);
				} else {
					// table is getting bigger so make the table
					// bigger first and then make the columns wider
					// to match the client area width
					internalTable.setSize(area.width, area.height);
					// column1.setWidth(width/3);
					secondColumn.setWidth(width - firstColumn.getWidth());
				}
			}
		});
	}

	public List<String> getSelectedValues() {
		List<String> selValues = new ArrayList<String>();
		String curVal;
		for (TableItemInfo itemInfo : this.itemsInfo) {
			curVal = itemInfo.combo.getText();
			if (!"".equals(curVal)) //$NON-NLS-1$
				selValues.add(curVal);
		}

		return selValues;
	}

	public void setRemoveRowText(String str) {
		this.removeRowString = str;
		this.initialOptionValues.set(0, str);
	}

	public void setSearchRowText(String str) {
		this.searchString = str;
		this.initialOptionValues.set(1, str);
	}

	/**
	 * Sets the text for the main column
	 * 
	 * @param text
	 */
	public void setMainColumnText(String text) {
		this.internalTable.getColumn(1).setText(text);
	}

	/**
	 * Returns the internal table
	 * 
	 * @return
	 */
	public Table getTable() {
		return this.internalTable;
	}

	/**
	 * Clears the table
	 * 
	 */
	private void resetValues() {
		this.initialValuesList.clear();
		this.internalTable.removeAll();
		for (TableItemInfo info : this.itemsInfo) {
			if (info != null) {
				if (info.item != null)
					info.item.dispose();
				if (info.editor != null)
					info.editor.dispose();
				if (info.combo != null)
					info.combo.dispose();
			}
		}
		this.itemsInfo.clear();
	}

	/**
	 * Initializes the values to be displayed in the combo boxes
	 * 
	 * @param values
	 */
	public void initValues(String[] values) {
		resetValues();

		if (values == null) {
			addRowItem(this.removeRowString);
			return;
		}

		for (String value : values) {
			addRowItem(value);
			this.initialValuesList.add(value);
		}

		addRowItem(this.removeRowString);
		this.isChangedStateFromInitial = false;

		updateMissingValues();
	}

	/**
	 * Initializes the values to be available as options in the combo boxes
	 * 
	 * @param values
	 */
	public void setInitialOptionValues(String[] values,
			Set<String> nonPresentValues) {
		if ((values == null) || (values.length == 0)) {
			return; // : maybe a cleanup of the current values should be made
		}

		if (this.initialOptionValues.size() != 2) {
			this.initialOptionValues.clear();
			this.initialOptionValues.add(this.removeRowString);
			this.initialOptionValues.add(this.searchString);
			indicesearchEv = 1;
			indicesearchVar = 1;
		}

		for (int i = 0; i < values.length; i++) {
			this.initialOptionValues.add(values[i]);
		}

		for (String string : nonPresentValues) {
			this.initialOptionValues.add(string);
		}

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
	private void addRowItem(String value) {
		// Add a new table item to the table
		final TableItemInfo itemInfo = new TableItemInfo();
		itemInfo.item = new TableItem(this.internalTable, SWT.NONE);
		itemInfo.editor = new TableEditor(this.internalTable);
		itemInfo.combo = new ExtendedCombo(this.internalTable, SWT.NONE, true);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		itemInfo.combo.setLayoutData(gridData);
		itemInfo.combo.setBackground(this.internalTable.getBackground());
		itemInfo.combo.setFont(this.internalTable.getFont());
		itemInfo.combo.setForeground(this.internalTable.getForeground());
		itemInfo.combo.setEditable(false);
		itemInfo.combo.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					int index = itemsInfo.indexOf(itemInfo);
					if (index + 1 < itemsInfo.size()) {

						TableItem tblItem = internalTable.getItem(index);

						TableItemInfo info = getInfo(tblItem);
						info.combo.select(0);

						int selection = info.combo.getSelectionIndex();
						String oldValue = (String) e.data;
						if (selection == 0) {
							if ("".equals(oldValue)) { //$NON-NLS-1$
								info.combo.setText(""); //$NON-NLS-1$
								return;
							} else {
								removeItemAt(getItemIndex(info.combo));
							}
						} else if (selection == 1) {
							addVariables(oldValue, info);
						} else {
							String selValue = info.combo.getText();
							setComboText(info.combo, selValue);
							if (!selValue.equals(oldValue)) {
								if ("".equals(oldValue)) { //we had the last empty row //$NON-NLS-1$
									addRowItem(removeRowString);
								}
							}
						}
						boolean listChanged = isDifferentFromInitialValues();
						if (listChanged)
							firePropertyChange(
									"CMB_SEL_CHANGED", null, ListeFiltresEditorTable.this); //$NON-NLS-1$
						else
							firePropertyChange(
									"CMB_SEL_RESTORED", null, ListeFiltresEditorTable.this); //$NON-NLS-1$
					}
				}

			}

			public void keyReleased(KeyEvent e) {
				// TODO Raccord de méthode auto-généré

			}

		});
		// itemInfo.combo.setItems(initialOptionValues.toArray(new
		// String[initialOptionValues.size()]));
		if (this.removeRowString.equals(value))
			itemInfo.combo.setText(""); //$NON-NLS-1$
		else {
			setComboText(itemInfo.combo, value);
			itemInfo.combo.getArrow().setText("-");
		}
		itemInfo.combo.addSelectionListener(this.comboSelAdapter);
		itemInfo.combo.addPopupListener(this.comboPopupListener);

		itemInfo.editor.grabHorizontal = true;
		itemInfo.editor.setEditor(itemInfo.combo, itemInfo.item, 1);
		this.itemsInfo.add(itemInfo);
		// internalTable.select(internalTable.getItemCount() - 1);
	}

	public void addVariables(String oldValue, TableItemInfo info) {
		RechercheDialog searchDlg = new RechercheDialog(getDisplay()
				.getActiveShell(), true);
		searchDlg.setFilterText(searchFilter);
		List<String> valuesPresent = getCurrentValues();
		// The oldValue should be in this case searchString
		List<String> possibleValues = listDifference(initialOptionValues,
				valuesPresent, oldValue);
		possibleValues.remove(removeRowString); // Remove the removeRowString
												// value
		searchDlg.setSelectableValues(possibleValues
				.toArray(new String[initialOptionValues.size()]));
		searchDlg.setAppelant(this.getClass().getName());
		String typeRecherche = null;
		if (GestionnairePool.getInstance().getVariable(possibleValues.get(2)) != null)
			typeRecherche = "Variable";
		else
			typeRecherche = "Event";

		searchDlg.setTypeRecherche(typeRecherche);
		String selValue = searchDlg.open();
		searchFilter = searchDlg.getFilterText(); // save the filter for further
													// searches
		if (selValue == null) {
			setComboText(info.combo, oldValue);
			return;
		}
		// if(selValue!=null)
		// ActivatorData.getInstance().getPoolDonneesVues().put(this.getClass().getName()+typeRecherche,
		// selValue);
		setComboText(info.combo, selValue);
		if ("".equals(oldValue)) { //we had the last empty row //$NON-NLS-1$
			addRowItem(removeRowString);
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
		List<String> valuesPresent = getCurrentValues();
		List<String> possibleValues = listDifference(this.initialOptionValues,
				valuesPresent, currentValue);
		combo.removeAll();
		for (String val : possibleValues) {
			combo.add(val, this.setColoredValues.contains(val) ? ERR_COLOR
					: null);
		}
		// combo.setItems(possibleValues.toArray(new
		// String[possibleValues.size()]));
		setComboText(combo, currentValue);
		if ("".equals(currentValue) && (this.internalTable.getItemCount() > 1)) { //$NON-NLS-1$
			// If is the last item in the table then we must scroll to the
			// previous value
			if (isLastComboSearchVar)
				combo.setTopIndex(indicesearchVar);
			else if (isLastComboSearchEv)
				combo.setTopIndex(indicesearchEv);
			else {
				TableItemInfo info = getInfo(this.internalTable
						.getItem(this.internalTable.getItemCount() - 2));
				String prevValText = info.combo.getText();
				int prevValInitialPos = this.initialOptionValues
						.indexOf(prevValText);
				if (prevValInitialPos == -1)
					return;
				int idx;
				// search in the initial options values array starting from this
				// index
				// to get a value that was not yet added
				for (int i = prevValInitialPos; i < this.initialOptionValues
						.size(); i++) {
					idx = possibleValues.indexOf(this.initialOptionValues
							.get(i));
					if (idx > 0) {
						combo.setTopIndex(idx);
						break;
					}
				}
			}
		}

		// combo.select(currentValPos);
	}

	/**
	 * Returns the list of the current values in the table. The list contains
	 * the user names for the variables
	 * 
	 * @return
	 */
	private List<String> getCurrentValues() {
		TableItemInfo info;
		List<String> valuesPresent = new ArrayList<String>();
		int itemsCount = this.internalTable.getItemCount();
		for (int i = 0; i < itemsCount; i++) {
			info = getInfo(this.internalTable.getItem(i));

			if (info != null && info.combo != null) {
				valuesPresent.add(info.combo.getText());
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
	private List<String> listDifference(List<String> list1, List<String> list2,
			String ignoredValue) {
		List<String> retList = new ArrayList<String>();

		for (String curElement : list1) {
			if (curElement != null) {
				if (curElement.equals(ignoredValue)) {
					retList.add(curElement);
				} else {
					boolean trouve = false;
					for (String string2 : list2) {
						if (string2.equals(curElement)) {
							trouve = true;
							break;
						}
					}
					if (!trouve)
						retList.add(curElement);
				}
			}

		}

		return (retList);
	}

	/**
	 * Returns the table item info from the given table item
	 * 
	 * @param item
	 * @return
	 */
	private TableItemInfo getInfo(TableItem item) {
		TableItemInfo info = null;
		int size = this.itemsInfo.size();
		for (int i = 0; i < size; i++) {
			if (item == this.itemsInfo.get(i).item) {
				info = this.itemsInfo.get(i);
				break;
			}
		}
		return info;
	}

	/**
	 * Returns the index in the items info list for the given combo
	 * 
	 * @param combo
	 * @return
	 */
	private int getItemIndex(ExtendedCombo combo) {
		int size = this.itemsInfo.size();
		for (int i = 0; i < size; i++) {
			if (combo == this.itemsInfo.get(i).combo) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Removes and item at the given index
	 * 
	 * @param index
	 */
	private void removeItemAt(int index) {
		if (index < 0 || index > this.internalTable.getItemCount())
			return;
		TableItem tblItem = this.internalTable.getItem(index);
		TableItemInfo info = getInfo(tblItem);
		this.internalTable.remove(index);
		tblItem.dispose();
		if (info != null) {
			if (info.editor != null)
				info.editor.dispose();
			if (info.combo != null)
				info.combo.dispose();
			this.itemsInfo.remove(info);
		}
		// The items editors must be notified that something changed
		int size = this.itemsInfo.size();
		for (int i = 0; i < size; i++) {
			this.itemsInfo.get(i).editor.setItem(this.itemsInfo.get(i).item);
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

	public void removePropertyChangeListener(final PropertyChangeListener l) {
		this.listeners.removePropertyChangeListener(l);
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
	 * Determines if the current state of the variables is changed from the
	 * original one
	 * 
	 * @return
	 */
	private boolean isDifferentFromInitialValues() {
		List<String> selValues = getSelectedValues();
		if (this.initialValuesList.size() != selValues.size()) {
			isChangedStateFromInitial = true;
		} else {
			selValues.removeAll(this.initialValuesList);
			this.isChangedStateFromInitial = (selValues.size() > 0);
		}
		return this.isChangedStateFromInitial;
	}

	/**
	 * Specifies if the values are changed from the original ones
	 * 
	 * @return
	 */
	public boolean isChangedStateFromInitial() {
		return this.isChangedStateFromInitial;
	}

	/**
	 * Reset the dirty status and the current state will become the initial
	 * state. This is useful especially for saving
	 * 
	 */
	public void resetChangedStateFromInitial() {
		this.initialValuesList.clear();
		this.initialValuesList.addAll(getSelectedValues());
		this.isChangedStateFromInitial = false;
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

		for (TableItemInfo itemInfo : this.itemsInfo) {
			curVal = itemInfo.combo.getText();
			if (!"".equals(curVal)) { //$NON-NLS-1$
				if (!this.initialOptionValues.contains(curVal)) {
					this.initialOptionValues.add(curVal);
					this.setInvalidValues.add(curVal);
					itemInfo.combo.setText(curVal, ERR_COLOR);
				}
			}
		}

		this.setColoredValues.addAll(this.setInvalidValues);

		// Refresh the colors
		for (TableItemInfo itemInfo : this.itemsInfo) {
			setComboText(itemInfo.combo, itemInfo.combo.getText());
		}

	}

	/**
	 * Sets the text for a combo, setting also the corresponding foreground
	 * color if the variable is an invalid one
	 * 
	 * @param combo
	 * @param text
	 */
	private void setComboText(ExtendedCombo combo, String text) {
		boolean elementTrouve = false;

		if (!"".equals(text)) {
			// première vérification : on regarde si on trouve cette variable
			// dans le xml
			elementTrouve = Util.getInstance().isVariableInXml(text, true);
			// deuxième test : si on trouve la variable, on regarde si elle esy
			// dans le fichier de parcours
			if (elementTrouve == true) {
				elementTrouve = Util.getInstance().isVariableDansParcours(text, true);
			}
			// pour les variables avec un nom uilisateur égal à "" est dans le
			// xml, le nom unique
			if (elementTrouve == false) {
				elementTrouve = Util.getInstance().isVariableComplexeInXmlByNomUnique(text);
				if (elementTrouve == true) {
					elementTrouve = Util
							.getInstance().isVariableComplexeDansParcoursByNomUnique(text);
				}
			}
			// troisième test : on regarde si on trouve un événément avec ce nom
			if (elementTrouve == false) {
				elementTrouve = Util.getInstance().isEventInXml(text, true);
				if (elementTrouve == true) {
					elementTrouve = Util.getInstance().isEventDansParcours(text, true);
				}
			}

		}

		if (!"".equals(text) && (!elementTrouve)) {
			combo.setText(text, ERR_COLOR);
		} else {
			combo.setText(text);
		}
	}

	private class TableItemInfo {
		TableItem item;
		TableEditor editor;
		ExtendedCombo combo;
	}

	private class ComboSelectionAdapter extends SelectionAdapter {
		public void widgetSelected(SelectionEvent event) {
			ExtendedCombo srcCombo = (ExtendedCombo) event.getSource();
			String oldValue = (String) event.data;
			int selection = srcCombo.getSelectionIndex();

			if (selection == 0) {
				if ("".equals(oldValue)) { //$NON-NLS-1$
					srcCombo.setText(""); //$NON-NLS-1$
					return;
				} else {
					removeItemAt(getItemIndex(srcCombo));
				}
			} else if (selection == 1) {
				RechercheDialog searchDlg = new RechercheDialog(getDisplay()
						.getActiveShell(), true);
				searchDlg.setFilterText(searchFilter);
				List<String> valuesPresent = getCurrentValues();
				// The oldValue should be in this case searchString
				List<String> possibleValues = listDifference(
						initialOptionValues, valuesPresent, oldValue);
				possibleValues.remove(removeRowString); // Remove the
														// removeRowString value
				searchDlg.setSelectableValues(possibleValues
						.toArray(new String[initialOptionValues.size()]));
				searchDlg.setAppelant(this.getClass().getName());
				String typeRecherche = null;
				if (GestionnairePool.getInstance()
						.getVariableByUseName(possibleValues.get(2)) != null)
					typeRecherche = "Variable";
				else
					typeRecherche = "Event";

				searchDlg.setTypeRecherche(typeRecherche);
				String selValue = searchDlg.open();
				searchFilter = searchDlg.getFilterText(); // save the filter for
															// further searches
				if (selValue == null) {
					setComboText(srcCombo, oldValue);
					return;
				}
				// if(selValue!=null)
				// ActivatorData.getInstance().getPoolDonneesVues().put(this.getClass().getName()+typeRecherche,
				// selValue);
				setComboText(srcCombo, selValue);
				if (typeRecherche.equals("Variable"))
					isLastComboSearchVar = true;
				else if (typeRecherche.equals("Event"))
					isLastComboSearchEv = true;

				if ("".equals(oldValue)) { //we had the last empty row //$NON-NLS-1$
					addRowItem(removeRowString);
				}
			} else {

				isLastComboSearchVar = false;
				isLastComboSearchEv = false;

				String selValue = srcCombo.getText();
				setComboText(srcCombo, selValue);
				if (!selValue.equals(oldValue)) {
					if ("".equals(oldValue)) { //we had the last empty row //$NON-NLS-1$
						addRowItem(removeRowString);
					}
				}
			}
			boolean listChanged = isDifferentFromInitialValues();
			if (listChanged)
				firePropertyChange(
						"CMB_SEL_CHANGED", null, ListeFiltresEditorTable.this); //$NON-NLS-1$
			else
				firePropertyChange(
						"CMB_SEL_RESTORED", null, ListeFiltresEditorTable.this); //$NON-NLS-1$
		}
	}

	private class ComboPopupListener implements ComboPopupShowListener {
		public void onComboPopupShowing(Widget widget) {
			if (!(widget instanceof ExtendedCombo)) {
				return;
			}

			ExtendedCombo srcCombo = (ExtendedCombo) widget;
			updateComboPossibleValues(srcCombo, srcCombo.getText());

			int currentLinePos = getItemIndex(srcCombo);

			if (srcCombo.getArrow().getText().equals("-")) {
				if (srcCombo.getArrow().isFocusControl()) {
					removeItemAt(getItemIndex(srcCombo));
					boolean listChanged = isDifferentFromInitialValues();
					if (listChanged)
						firePropertyChange(
								"CMB_SEL_CHANGED", null, ListeFiltresEditorTable.this); //$NON-NLS-1$
					else
						firePropertyChange(
								"CMB_SEL_RESTORED", null, ListeFiltresEditorTable.this); //$NON-NLS-1$
				}
			} else {

				RechercheDialog searchDlg = new RechercheDialog(getDisplay()
						.getActiveShell(), true);
				searchDlg.setFilterText(searchFilter);
				List<String> valuesPresent = getCurrentValues();
				// The oldValue should be in this case searchString
				List<String> possibleValues = listDifference(
						initialOptionValues, valuesPresent, srcCombo.getText());
				possibleValues.remove(removeRowString); // Remove the
														// removeRowString value
				possibleValues.remove(searchString);
				searchDlg.setSelectableValues(possibleValues
						.toArray(new String[possibleValues.size()]));
				searchDlg.setAppelant(this.getClass().getName());
				String typeRecherche = null;
				if (initialOptionValues.get(0).equals(
						com.faiveley.samng.vueliste.ihm.vues.vuefiltre.Messages
								.getString("VueListeFiltreEditeur.3")))
					typeRecherche = "Variable";
				else
					typeRecherche = "Event";

				searchDlg.setTypeRecherche(typeRecherche);
				String selValue = searchDlg.open();
				searchFilter = searchDlg.getFilterText(); // save the filter for
															// further searches
				if (selValue == null) {
					setComboText(srcCombo, srcCombo.getText());
					return;
				}
				//

				int nbSelected = 0;
				String[] selects = searchDlg.getSelectedValue();
				if (selects == null) {
					return;
				}
				nbSelected = selects.length;

				if (nbSelected > 1) {
					String selected[] = searchDlg.getSelectedValue();
					for (int i = 0; i < nbSelected; i++) {

						srcCombo = itemsInfo.get(currentLinePos + i).combo;

						setComboText(srcCombo, selected[i]);
						if (typeRecherche.equals("Variable"))
							isLastComboSearchVar = true;
						else if (typeRecherche.equals("Event"))
							isLastComboSearchEv = true;

						if ("".equals(srcCombo.getText())) { //we had the last empty row //$NON-NLS-1$
							addRowItem(removeRowString);
						}

						srcCombo.getArrow().setText("-");
						addRowItem(removeRowString);

						boolean listChanged = isDifferentFromInitialValues();
						if (listChanged)
							firePropertyChange(
									"CMB_SEL_CHANGED", null, ListeFiltresEditorTable.this); //$NON-NLS-1$
						else
							firePropertyChange(
									"CMB_SEL_RESTORED", null, ListeFiltresEditorTable.this); //$NON-NLS-1$
					}
				} else {
					setComboText(srcCombo, selValue);
					if (typeRecherche.equals("Variable"))
						isLastComboSearchVar = true;
					else if (typeRecherche.equals("Event"))
						isLastComboSearchEv = true;

					if ("".equals(srcCombo.getText())) { //we had the last empty row //$NON-NLS-1$
						addRowItem(removeRowString);
					}

					srcCombo.getArrow().setText("-");
					addRowItem(removeRowString);

					boolean listChanged = isDifferentFromInitialValues();
					if (listChanged)
						firePropertyChange(
								"CMB_SEL_CHANGED", null, ListeFiltresEditorTable.this); //$NON-NLS-1$
					else
						firePropertyChange(
								"CMB_SEL_RESTORED", null, ListeFiltresEditorTable.this); //$NON-NLS-1$
				}
			}
		}
	}
}
